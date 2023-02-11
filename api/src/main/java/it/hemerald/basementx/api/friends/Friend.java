package it.hemerald.basementx.api.friends;

import java.util.List;
import java.util.Set;

public interface Friend {

    /**
     * Gets the friends
     *
     * @return friends members
     */
    List<Pair<String, Long>> getFriends();

    /**
     * Gets if a player is friend to this player
     *
     * @param name the player name
     * @return true if player is friend, false otherwise
     */
    boolean containsFriend(String name);

    /**
     * Gets the limit of the friends
     *
     * @return true if is full, false otherwise
     */
    boolean limit();

    void addFriend(String username, long seconds);

    void removeFriend(String friendName);
}
