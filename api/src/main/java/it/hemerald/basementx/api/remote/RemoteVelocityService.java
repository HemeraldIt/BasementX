package it.hemerald.basementx.api.remote;

import java.util.UUID;

public interface RemoteVelocityService {

    /**
     * Gets if a player is in a server that names start with {@param server}
     *
     * @param player the player name
     * @param server the server ranch name
     * @return true if player is in a server ranch, false otherwise
     */
    boolean isOnRanch(String player, String server);

    /**
     * Gets if a player is in a server that names start with {@param server}
     *
     * @param player the player uuid
     * @param server the server ranch name
     * @return true if player is in a server ranch, false otherwise
     */
    boolean isOnRanch(UUID player, String server);

    /**
     * Gets the server name of a player
     *
     * @param player the player name
     * @return the server name
     */
    String getServer(String player);

    /**
     * Send a player to a server
     *
     * @param player the player name
     * @param server the server name
     */
    void sendToServer(String player, String server);

    /**
     * Send a player to a server
     *
     * @param uuid   the player uuid
     * @param server the server name
     */
    void sendToServer(UUID uuid, String server);

    /**
     * Send same messages to a player
     *
     * @param player   player name
     * @param messages the messages to send to the player
     */
    void sendMessage(String player, String... messages);

    /**
     * Send same messages to a player only if it has a determinate permission
     *
     * @param player         player name
     * @param permissionNode the permission the player need to receive the messages
     * @param messages       the messages to send to the player
     */
    void sendMessageWithPermission(String player, String permissionNode, String... messages);

    /**
     * Send same messages to a player, serialized in json string {@link net.kyori.adventure.text.Component}
     *
     * @param player   player name
     * @param messages the messages to send to the player serialized in Json String
     */
    void sendMessageComponent(String player, String... messages);

    /**
     * Register a new server to velocity
     *
     * @param serverName the server name
     * @param port       the server port
     */
    void registerServer(String serverName, int port);

    /**
     * Send a cheat alert message to all staff
     *
     * @param server   the server name
     * @param player   the player name
     * @param category the alert category
     * @param type     the alert type
     * @param desc     the alert desc
     * @param level    the alert level
     * @param maxLevel the alert max level
     * @param cps      the player cps
     * @param ping     the player ping
     */
    void cheatAlert(String server, String player, String category, String type, String desc, int level, int maxLevel, long cps, long ping);

    /**
     * Ban a player for cheat
     *
     * @param server the server name
     * @param player the player name
     */
    void cheatBan(String server, String player);

    /**
     * Gets the protocol version of player, with this you can resolve the minecraft version player is using
     *
     * @param uuid the uuid of player
     * @return the protocol version of player
     */
    int playerVersion(UUID uuid);
}
