package it.hemerald.basementx.api.friends;

import java.util.Optional;

public interface FriendsManager {

    /**
     * Gets the friends of a player
     *
     * @param player the player name
     * @return the friends of the player
     */
    Optional<Friend> getFriends(String player);
}
