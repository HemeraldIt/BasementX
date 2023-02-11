package it.hemerald.basementx.common.friends;

import it.hemerald.basementx.api.friends.Friend;
import it.hemerald.basementx.api.friends.Pair;

import java.util.*;

public class BasementFriend implements Friend {

    private final UUID uuid;
    private final List<Pair<String, Long>> friends = new ArrayList<>();

    private BasementFriend() {
        this.uuid = null;
    }

    public BasementFriend(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public void addFriend(String username, long seconds) {
        friends.add(new Pair<>(username, seconds));
    }

    @Override
    public void removeFriend(String friendName) {
        friends.removeIf(s -> s.getKey().equalsIgnoreCase(friendName));
    }

    @Override
    public boolean limit() {
        return friends.size() >= 10;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BasementFriend that)) return false;
        return that.uuid.equals(uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public List<Pair<String, Long>> getFriends() {
        return friends;
    }

    @Override
    public boolean containsFriend(String name) {
        return friends.stream().map(Pair::getKey).anyMatch(name::equalsIgnoreCase);
    }

}
