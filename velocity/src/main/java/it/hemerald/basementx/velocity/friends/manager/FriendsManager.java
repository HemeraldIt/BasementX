package it.hemerald.basementx.velocity.friends.manager;

import com.velocitypowered.api.proxy.Player;
import it.hemerald.basementx.api.friends.Friend;
import it.hemerald.basementx.api.persistence.maria.queries.builders.WhereBuilder;
import it.hemerald.basementx.api.persistence.maria.queries.builders.data.QueryBuilderDelete;
import it.hemerald.basementx.api.persistence.maria.queries.builders.data.QueryBuilderInsert;
import it.hemerald.basementx.api.persistence.maria.queries.builders.data.QueryBuilderSelect;
import it.hemerald.basementx.api.persistence.maria.structure.data.QueryData;
import it.hemerald.basementx.common.friends.BasementFriend;
import it.hemerald.basementx.velocity.together.Together;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RLocalCachedMap;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class FriendsManager {

    public static final Component PREFIX = Component.text("§3§lFriends §8§l› ");
    private static QueryBuilderInsert INSERT_FRIENDS;
    private static QueryBuilderDelete REMOVE_FRIENDS;
    private static QueryBuilderSelect SELECT_FRIENDS;
    private static QueryBuilderSelect PLAYER_ID;
    private static QueryBuilderSelect PLAYER_USERNAME;
    @Getter
    private final Together together;
    private final RLocalCachedMap<String, Friend> friends;

    public FriendsManager(Together together) {
        this.together = together;
        this.friends = together.getBasement().getRedisManager().getRedissonClient().getLocalCachedMap("friends", LocalCachedMapOptions.defaults());

        PLAYER_ID = together.getBasement().getDatabase().select().columns("id").from("players");
        PLAYER_USERNAME = together.getBasement().getDatabase().select().columns("username").from("players");

        INSERT_FRIENDS = together.getBasement().getDatabase().insert().into("friends").columnSchema("player_id", "friend_id", "date");
        REMOVE_FRIENDS = together.getBasement().getDatabase().delete().from("friends");
        SELECT_FRIENDS = together.getBasement().getDatabase().select().columns("friend_id", "date").from("friends");
    }

    public void disable() {
        friends.clear();
    }

    public Optional<Friend> getFriend(String player) {
        return Optional.ofNullable(friends.get(player));
    }

    public Optional<Friend> getFriend(Player player) {
        return getFriend(player.getUsername());
    }

    public void saveFriend(String username, Friend friend) {
        friends.fastPut(username, friend);
    }

    public void sendMessage(Player player, String component) {
        player.sendMessage(PREFIX.append(Component.text("§7" + component)));
    }

    public void sendMessage(Player player, Component component) {
        player.sendMessage(PREFIX.append(component));
    }

    private CompletableFuture<Friend> loadFromDatabase(Player player) {
        return SELECT_FRIENDS.patternClone().where(WhereBuilder.builder().equals("player_id", playerId(player.getUsername())).close()).build().execReturnAsync().thenApplyAsync(queryData -> {
            Friend friend = new BasementFriend(player.getUniqueId());
            if (!queryData.isBeforeFirst()) {
                return friend;
            }
            while (queryData.next()) {
                friend.addFriend(playerUsername(queryData.getInt("friend_id")), queryData.getLong("date"));
            }
            return friend;
        });
    }

    public void join(Player player) {
        loadFromDatabase(player).whenComplete((friend, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                return;
            }
            friends.fastPut(player.getUsername(), friend);
            friend.getFriends().forEach(friendName -> {
                Optional<Player> optionalPlayer = together.getServer().getPlayer(friendName.getKey());
                optionalPlayer.ifPresent(value -> sendMessage(value, "§a" + player.getUsername() + " §7è entrato nel server."));
            });
        });
    }

    public void leave(Player player) {
        Friend friend = friends.remove(player.getUsername());
        if (friend == null) return;
        friend.getFriends().forEach(friendName -> {
            Optional<Player> optionalPlayer = together.getServer().getPlayer(friendName.getKey());
            optionalPlayer.ifPresent(value -> sendMessage(value, "§c" + player.getUsername() + " §7è uscito dal server."));
        });
    }


    public void addFriend(Player player, String friendName) {
        long epochSecond = Instant.now().getEpochSecond();
        getFriend(player).ifPresent(friend -> {
            friend.addFriend(friendName, epochSecond);
            saveFriend(player.getUsername(), friend);
        });
        INSERT_FRIENDS.patternClone()
                .values(playerId(player.getUsername()), playerId(friendName), epochSecond)
                .build().execAsync();
    }

    public void removeFriend(String player, String friendName) {
        getFriend(player).ifPresent(friend -> {
            friend.removeFriend(friendName);
            saveFriend(player, friend);
        });
        REMOVE_FRIENDS.patternClone()
                .where(WhereBuilder.builder().equals("player_id", playerId(player)).and()
                        .equals("friend_id", playerId(friendName)).close())
                .build().execAsync();
    }

    public void removeFriend(Player player, String friendName) {
        removeFriend(player.getUsername(), friendName);
    }

    private QueryBuilderSelect playerId(String username) {
        return PLAYER_ID.patternClone().where(WhereBuilder.builder().equals("username", username).close()).build();
    }

    private String playerUsername(int id) {
        QueryData queryData = PLAYER_USERNAME.patternClone().where(WhereBuilder.builder().equals("id", id).close()).build().execReturn();
        queryData.first();
        return queryData.getString("username");
    }

}
