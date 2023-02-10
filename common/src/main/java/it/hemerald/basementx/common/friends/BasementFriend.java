package it.hemerald.basementx.common.friends;

import it.hemerald.basementx.api.friends.Friend;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BasementFriend implements Friend {

    private final UUID uuid;
    private final Set<String> friends = new HashSet<>();

    private BasementFriend() {
        this.uuid = null;
    }

    public BasementFriend(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public void addFriend(String username) {
        friends.add(username);
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
    public Set<String> getFriends() {
        return friends;
    }

    @Override
    public boolean containsFriend(String name) {
        return friends.contains(name);
    }

}
