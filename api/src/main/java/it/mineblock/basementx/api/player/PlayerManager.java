package it.mineblock.basementx.api.player;

import it.mineblock.basementx.api.server.BukkitServer;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface PlayerManager<E extends BasementPlayer> {

    /**
     * Add a {@link BasementPlayer} object associated with the player's name
     * @param name the name of the player
     * @param basementPlayer the BasementPlayer object
     */
    void addBasementPlayer(String name, E basementPlayer);

    /**
     * Remove the {@link BasementPlayer} object associated with the player's name
     * @param name the name of the player
     */
    void removeBasementPlayer(String name);

    /**
     * Gets the {@link BasementPlayer} object associated with the player's name
     * @param name the name of the player
     * @return the BasementPlayer object
     */
    E getBasementPlayer(String name);

    /**
     * Gets all {@link BasementPlayer}
     * @return all basement players
     */
    Collection<E> getBasementPlayers();

    /**
     * Send a player to a server
     * @param player player name
     * @param server server name
     */
    void sendToServer(String player, String server);

    /**
     * Send a player to a server
     * @param uuid player uuid
     * @param server server name
     */
    void sendToServer(UUID uuid, String server);

    /**
     * Send a player to the best lobby server
     * It's depend on how many players are in every lobby
     * @param player player name
     */
    void sendToLobby(String player);

    /**
     * Send a player to the best lobby server
     * It's depend on how many players are in every lobby
     * @param player player uuid
     */
    void sendToLobby(UUID player);

    /**
     * Send a player to the best game lobby server
     * It's depend on how many players are in every lobby
     * @param player player name
     */
    void sendToGameLobby(String player, String game);

    /**
     * Send a player to the best game lobby server
     * It's depend on how many players are in every lobby
     * @param player player name
     */
    void sendToGameLobby(UUID player, String game);

    /**
     * Gets the best server
     * It's depend on how many players are in every server
     * @param ranch the start name of the server
     * @return the best server
     */
    Optional<BukkitServer> bestServer(String ranch);

    /**
     * Gets if the player is disguised
     * @param name player name
     */
    boolean isDisguised(String name);

    /**
     * Disguise the player
     * @param name player name
     */
    void disguise(String name);

    /**
     * Undisguise the name
     * @param name player name
     */
    void undisguise(String name);

    /**
     * Send same messages to a player
     * @param player player name
     * @param messages the messages to send to the player
     */
    void sendMessage(String player, String... messages);

    /**
     * Send same messages to a player only if it has a determinate permission
     * @param player player name
     * @param permissionNode the permission the player need to receive the messages
     * @param messages the messages to send to the player
     */
    void sendMessageWithPermission(String player, String permissionNode, String... messages);
}
