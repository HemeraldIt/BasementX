package it.mineblock.basementx.api.party;

import java.util.Optional;

public interface PartyManager {

    /**
     * Gets the party of a player
     * @param player the player name
     * @return the party of the player
     */
    Optional<Party> getParty(String player);
}
