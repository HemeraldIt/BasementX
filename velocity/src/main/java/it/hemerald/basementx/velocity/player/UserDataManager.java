package it.hemerald.basementx.velocity.player;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.scheduler.ScheduledTask;
import it.hemerald.basementx.api.persistence.maria.queries.builders.WhereBuilder;
import it.hemerald.basementx.api.persistence.maria.queries.builders.data.QueryBuilderDelete;
import it.hemerald.basementx.api.persistence.maria.queries.builders.data.QueryBuilderReplace;
import it.hemerald.basementx.api.persistence.maria.queries.builders.data.QueryBuilderSelect;
import it.hemerald.basementx.api.persistence.maria.queries.builders.data.QueryBuilderUpdate;
import it.hemerald.basementx.api.persistence.maria.structure.AbstractMariaDatabase;
import it.hemerald.basementx.api.persistence.maria.structure.data.QueryData;
import it.hemerald.basementx.api.player.UserData;
import it.hemerald.basementx.velocity.BasementVelocity;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.redisson.api.condition.Conditions;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public class UserDataManager {

    private final Map<UUID, UserData> userDataMap = new HashMap<>();
    private final RedissonClient redissonClient;

    private final QueryBuilderSelect querySelectUserData;
    private final QueryBuilderUpdate queryUpdateUserData;

    private final QueryBuilderDelete querySelectUserBoosters;
    private final QueryBuilderReplace queryInsertUserBoosters;

    private final ScheduledTask task;
    private final BasementVelocity velocity;

    public UserDataManager(BasementVelocity velocity, AbstractMariaDatabase database) {
        this.velocity = velocity;
        this.redissonClient = velocity.getBasement().getRedisManager().getRedissonClient();

        querySelectUserData = database.select().from("players").columns("id", "username", "xp", "level", "coins", "gems", "premium", "language");
        querySelectUserBoosters = database.delete().from("player_boosters").returning("type, value, time");

        queryUpdateUserData = database.update().table("players")
                .setNQ("xp", "?").setNQ("level", "?").setNQ("coins", "?")
                .setNQ("gems", "?").setNQ("language", "?")
                .where(WhereBuilder.builder().equalsNQ("uuid", "?").close());

        queryInsertUserBoosters = database.replace().into("player_boosters")
                .columnSchema("user_id", "mode", "type", "value", "time")
                .valuesNQ("?", "?", "?", "?", "?");

        task = velocity.getServer().getScheduler().buildTask(velocity, this::saveAllToDisk).repeat(5, TimeUnit.MINUTES).schedule();
    }

    public void prepareUser(Player player) {
        UUID uuid = player.getUniqueId();
        UserData userData = velocity.getBasement().getUserData(uuid);

        if (userData == null) {
            // User Data Select
            QueryData data = querySelectUserData.patternClone().where(WhereBuilder.builder().equals("uuid", uuid.toString()).close()).build().execReturn();
            userData = new UserData(uuid.toString(), player.getUsername());

            if (data.first()) {
                userData.setTableIndex(data.getInt("id"));
                userData.setXp(data.getInt("xp"));
                userData.setNetworkLevel(data.getInt("level"));
                userData.setNetworkCoins(data.getInt("coins"));
                userData.setGems(data.getInt("gems"));
                userData.setLanguage(data.getString("language"));

                // User Boosters Eviction
                data = querySelectUserBoosters.patternClone()
                        .where(WhereBuilder.builder().equalsNQ("user_id", userData.getTableIndex()).and().equalsNQ("mode", 0).close())
                        .build().execReturn();
                if (data.isBeforeFirst()) {
                    while (data.next()) {
                        NetworkBoosters type = NetworkBoosters.values()[data.getInt("type")];
                        type.setBuff.accept(userData, data.getInt("value"));
                        type.setTime.accept(userData, data.getLong("time"));
                    }
                }
            }
        }

        userData.setProtocolVersion(player.getProtocolVersion().getProtocol());
        userData.setPremium(player.isOnlineMode());

        userDataMap.put(uuid, redissonClient.getLiveObjectService().merge(userData));
    }

    @RequiredArgsConstructor
    private enum NetworkBoosters {
        XP (UserData::setXpBoost, (user, time) -> {
            if (System.currentTimeMillis() < time)
                user.setXpBoostTime(time);
        }),

        COINS (UserData::setCoinsBoost, (user, time) -> {
            if (System.currentTimeMillis() < time)
                user.setCoinsBoostTime(time);
        });

        private final BiConsumer<UserData, Integer> setBuff;
        private final BiConsumer<UserData, Long> setTime;
    }

    public void saveUser(Player player) {
        UserData userData = userDataMap.remove(player.getUniqueId());
        if (userData == null) {
            System.out.println("[SAVE-USER : QUIT] User data not found for " + player.getUsername() + " (" + player.getUniqueId() + ")");
            return;
        }
        saveUserToDatabase(userData);
    //    redissonClient.getLiveObjectService().delete(userData);
    }

    private Set<UserData> getUsers() {
        return new HashSet<>(userDataMap.values());
    }

    public Optional<UserData> getUserData(UUID uuid) {
        return Optional.ofNullable(userDataMap.get(uuid));
    }

    public UserData getUserData(String username) {
        Collection<UserData> data = redissonClient.getLiveObjectService().find(UserData.class, Conditions.eq("username", username));
        if (data.isEmpty()) return null;
        return (UserData) data.toArray()[0];
    }

    public void shutdown() {
        task.cancel();
        saveAllToDisk();
    }

    private void addBatchedUserData(PreparedStatement statement, UserData data) throws SQLException {
        statement.setInt(1, data.getXp());
        statement.setInt(2, data.getNetworkLevel());
        statement.setInt(3, data.getNetworkCoins());
        statement.setInt(4, data.getGems());
        statement.setString(5, data.getLanguage());
        statement.setString(6, data.getUuid());
        statement.addBatch();
    }

    private void addBatchedUserBoosters(PreparedStatement statement, UserData data) throws SQLException {

        if (data.getTableIndex() == -1) {
            querySelectUserData.patternClone()
                    .columns("id")
                    .where(WhereBuilder.builder().equals("uuid", data.getUuid()).close())
                    .build().execConsume( (queryData) -> {
                        if (queryData.first())
                            data.setTableIndex(queryData.getInt("id"));
                    });
        }

        statement.setInt(1, data.getTableIndex());
        statement.setInt(2, 0);

        if (data.hasXpBoost()) {
            statement.setInt(3, NetworkBoosters.XP.ordinal());
            statement.setInt(4, data.getXpBoost());
            statement.setLong(5, data.getXpBoostTime());
            statement.addBatch();
        }

        if (data.hasCoinsBoost()) {
            statement.setInt(3, NetworkBoosters.COINS.ordinal());
            statement.setInt(4, data.getCoinsBoost());
            statement.setLong(5, data.getCoinsBoostTime());
            statement.addBatch();
        }

    }

    public void saveAllToDisk() {

        Set<UserData> users = getUsers();
        System.out.println("Saving Online UserData(s) & Boosters to disk. (batch size: " + users.size() + ")");

        try (PreparedStatement preparedStatement = queryUpdateUserData.patternClone().build().asPrepared()) {
            for (UserData userData : users) {
                try {
                    addBatchedUserData(preparedStatement, userData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            preparedStatement.executeBatch();
            preparedStatement.getConnection().close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        try (PreparedStatement preparedStatement = queryInsertUserBoosters.patternClone().build().asPrepared()) {
            for (UserData userData : users) {
                try {
                    addBatchedUserBoosters(preparedStatement, userData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            preparedStatement.executeBatch();
            preparedStatement.getConnection().close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

    }

    private void saveUserToDatabase(UserData data) {
        System.out.println("Saving on disk for offline player " + data.getUsername());
        queryUpdateUserData.patternClone().clearSet()
                .setNQ("xp", data.getXp()).setNQ("level", data.getNetworkLevel()).setNQ("coins", data.getNetworkCoins())
                .setNQ("gems", data.getGems()).set("language", data.getLanguage())
                .where(WhereBuilder.builder().equals("uuid", data.getUuid()).close())
                .build().exec();

        int tableIndex = data.getTableIndex();
        if (tableIndex == -1) {
            QueryData selectIdQueryData = querySelectUserData.patternClone()
                    .columns("id")
                    .where(WhereBuilder.builder().equals("uuid", data.getUuid()).close())
                    .build().execReturn();
            if (selectIdQueryData.first())
                tableIndex = selectIdQueryData.getInt("id");
            else return;
        }

        if (data.hasXpBoost()) {
            queryInsertUserBoosters.patternClone().clearValues()
                    .values(tableIndex, 0, NetworkBoosters.XP.ordinal(),
                            data.getXpBoost(), data.getXpBoostTime())
                    .build().exec();
        }
        if (data.hasCoinsBoost()) {
            queryInsertUserBoosters.patternClone().clearValues()
                    .values(tableIndex, 0, NetworkBoosters.COINS.ordinal(),
                            data.getCoinsBoost(), data.getCoinsBoostTime())
                    .build().exec();
        }
    }

}
