package it.hemerald.basementx.api.party;

import java.util.Set;
import java.util.UUID;

public interface Party {

    /**
     * Gets if party is open
     *
     * @return true if open, false otherwise
     */
    boolean isOpen();

    /**
     * Set if party is open
     *
     * @param open true if open, false otherwise
     */
    void setOpen(boolean open);

    /**
     * Gets the party UUID
     *
     * @return party UUID
     */
    UUID getUuid();

    /**
     * Gets the leader name
     *
     * @return leader name
     */
    String getLeader();

    /**
     * Set a new leader
     *
     * @param leader the new leader name
     */
    void setLeader(String leader);

    /**
     * Gets the members of the party
     *
     * @return party members
     */
    Set<String> getMembers();

    /**
     * Gets if a player is member of this party
     *
     * @param name the player name
     * @return true if player is member of this party, false otherwise
     */
    boolean containsMember(String name);

    /**
     * Gets if party is full
     *
     * @return true if is full, false otherwise
     */
    boolean isFull();

}
