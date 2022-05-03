package it.mineblock.basementx.api;

import ch.jalu.configme.SettingsManager;
import it.mineblock.basementx.api.locale.LocaleManager;
import it.mineblock.basementx.api.party.PartyManager;
import it.mineblock.basementx.api.persistence.generic.Holder;
import it.mineblock.basementx.api.persistence.generic.connection.Connector;
import it.mineblock.basementx.api.persistence.generic.connection.TypeConnector;
import it.mineblock.basementx.api.persistence.maria.structure.AbstractMariaDatabase;
import it.mineblock.basementx.api.player.BasementPlayer;
import it.mineblock.basementx.api.player.PlayerManager;
import it.mineblock.basementx.api.redis.RedisManager;
import it.mineblock.basementx.api.remote.RemoteCerebrumService;
import it.mineblock.basementx.api.remote.RemoteVelocityService;
import it.mineblock.basementx.api.server.ServerManager;

import java.util.UUID;

public interface Basement {

    /**
     * Gets the instance UUID
     * @return the instance UUID
     */
    UUID getUuid();

    /**
     * Reload the configuration
     */
    void reloadConfig();

    /**
     * Gets the Settings Manager
     * @return Settings Manager
     */
    SettingsManager getSettingsManager();

    /**
     * Gets the Redis Manager
     * @return Redis Manager
     */
    RedisManager getRedisManager();

    /**
     * Gets the Server Manager
     * @return Server Manager
     */
    ServerManager getServerManager();

    /**
     * Gets the Player Manager
     * @param <E> the BasementPlayer implementation type
     * @return Player Manager
     */
    <E extends BasementPlayer> PlayerManager<E> getPlayerManager();

    /**
     * Gets the Party Manager
     * @return Party Manager
     */
    PartyManager getPartyManager();

    /**
     * Gets the Locale Manager
     * @return Locale Manager
     */
    LocaleManager getLocaleManager();

    /**
     * Gets the remote instance of velocity service
     * @return remote instance of velocity service
     */
    RemoteVelocityService getRemoteVelocityService();

    /**
     * Gets the remote instance of cerebrum service
     * @return remote instance of cerebrum service
     */
    RemoteCerebrumService getRemoteCerebrumService();

    /**
     * Register a new PlayerManager instance with custom BasementPlayer implementation
     * @param type the new BasementPlayer implementation class
     * @param <T> the BasementPlayer implementation type
     */
    <T extends BasementPlayer> void registerBasementPlayerType(Class<T> type);

    /**
     * Gets if a custom type of BasementPlayer is registered. if false Basement will not register players on join, the registration must be handled by yourself
     * @return false if a custom type of BasementPlayer is registered
     */
    boolean savePlayer();

    /**
     * Gets the default server database
     * @return the default maria database
     */
    AbstractMariaDatabase getDatabase();

    /**
     * Gets a new Connector object
     * @return connector object
     */
    Connector getConnector(TypeConnector type);

    <T extends Holder> T getHolder(Class<?> key, Class<T> type);
}
