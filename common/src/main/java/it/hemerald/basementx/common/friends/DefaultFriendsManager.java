package it.hemerald.basementx.common.friends;

import it.hemerald.basementx.api.friends.Friend;
import it.hemerald.basementx.api.friends.FriendsManager;
import it.hemerald.basementx.api.redis.RedisManager;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RLocalCachedMap;

import java.util.Optional;

public class DefaultFriendsManager implements FriendsManager {

    private final RLocalCachedMap<String, Friend> friendsMap;

    public DefaultFriendsManager(RedisManager redisManager) {
        this.friendsMap = redisManager.getRedissonClient().getLocalCachedMap("friends", LocalCachedMapOptions.defaults());
    }

    @Override
    public Optional<Friend> getFriends(String player) {
        return Optional.ofNullable(friendsMap.get(player));
    }
}
