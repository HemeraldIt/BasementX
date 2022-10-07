package it.hemerald.basementx.velocity.player;

import com.google.common.cache.*;
import com.velocitypowered.api.proxy.Player;
import it.hemerald.basementx.api.persistence.maria.queries.builders.WhereBuilder;
import it.hemerald.basementx.api.persistence.maria.queries.builders.data.QueryBuilderSelect;
import it.hemerald.basementx.api.persistence.maria.queries.builders.data.QueryBuilderUpdate;
import it.hemerald.basementx.api.persistence.maria.structure.AbstractMariaDatabase;
import it.hemerald.basementx.api.persistence.maria.structure.data.QueryData;
import it.hemerald.basementx.api.player.UserData;
import org.redisson.api.RedissonClient;
import org.redisson.api.condition.Conditions;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class UserDataManager {

    private final Cache<UUID, UserData> userDataCache;

    private final Map<UUID, UserData> userDataMap = new HashMap<>();
    private final RedissonClient redissonClient;
    private final QueryBuilderSelect builderSelect;
    private final QueryBuilderUpdate builderUpdate;

    public UserDataManager(RedissonClient redissonClient, AbstractMariaDatabase database) {
        this.redissonClient = redissonClient;
        builderSelect = database.select().from("players").columns("username", "xp", "level", "coins", "gems", "premium", "language");
        builderUpdate = database.update().table("players");

        userDataCache = CacheBuilder.newBuilder().removalListener((RemovalListener<UUID, UserData>) notification -> {
            if(notification.getCause() == RemovalCause.EXPIRED) {
                UserData userData = notification.getValue();
                builderUpdate.patternClone()
                        .set("xp", userData.getXp())
                        .set("level", userData.getNetworkLevel())
                        .set("coins", userData.getNetworkCoin())
                        .set("gems", userData.getGems())
                        .set("language", userData.getLanguage()).build().exec();
            }
        }).expireAfterAccess(10, TimeUnit.MINUTES).build();
    }

    public void prepareUser(Player player) {
        UUID uuid = player.getUniqueId();

        UserData userData = userDataCache.getIfPresent(uuid);
        if (userData != null) {
            userDataCache.invalidate(uuid);
            userDataMap.put(uuid, userData);
            return;
        }

        QueryData data = builderSelect.where(WhereBuilder.builder().equals("uuid", uuid.toString()).close()).build().execReturn();
        data.first();
        userData = new UserData(uuid.toString(), data.getString("username"));
        userData.setXp(data.getInt("xp"));
        userData.setNetworkLevel(data.getInt("level"));
        userData.setNetworkCoin(data.getInt("coins"));
        userData.setGems(data.getInt("gems"));
        userData.setPremium(data.getInt("premiun") == 1);
        userData.setLanguage(data.getString("language"));
        userData.setProtocolVersion(player.getProtocolVersion().getProtocol());

        userDataMap.put(uuid, redissonClient.getLiveObjectService().merge(userData));
    }

    public void cacheUser(Player player) {
        UUID uuid = player.getUniqueId();
        UserData userData = userDataMap.get(uuid);
        if (userData == null) return;
        userDataCache.put(uuid, userData);
    }

    public UserData getUserData(UUID uuid) {
        return userDataCache.asMap().getOrDefault(uuid, userDataMap.get(uuid));
    }

    public UserData getUserData(String username) {
        Collection<UserData> data = redissonClient.getLiveObjectService().find(UserData.class, Conditions.eq("username", username));
        if (data.isEmpty()) return null;
        return (UserData) data.toArray()[0];
    }

}
