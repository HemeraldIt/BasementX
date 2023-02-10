package it.hemerald.basementx.api;

import ch.jalu.configme.SettingsManager;
import it.hemerald.basementx.api.concurrent.process.ProcessScheduler;
import it.hemerald.basementx.api.friends.FriendsManager;
import it.hemerald.basementx.api.locale.LocaleManager;
import it.hemerald.basementx.api.party.PartyManager;
import it.hemerald.basementx.api.persistence.generic.Holder;
import it.hemerald.basementx.api.persistence.generic.connection.Connector;
import it.hemerald.basementx.api.persistence.generic.connection.TypeConnector;
import it.hemerald.basementx.api.persistence.maria.structure.AbstractMariaDatabase;
import it.hemerald.basementx.api.player.BasementPlayer;
import it.hemerald.basementx.api.player.PlayerManager;
import it.hemerald.basementx.api.player.UserData;
import it.hemerald.basementx.api.redis.RedisManager;
import it.hemerald.basementx.api.remote.RemoteCerebrumService;
import it.hemerald.basementx.api.remote.RemoteVelocityService;
import it.hemerald.basementx.api.remote.UserDataService;
import it.hemerald.basementx.api.server.ServerManager;

import java.util.UUID;

public interface Basement {

    /**
     * Gets the instance UUID
     *
     * @return the instance UUID
     */
    UUID getUuid();

    /**
     * Reload the configuration
     */
    void reloadConfig();

    /**
     * Gets the Settings Manager
     *
     * @return Settings Manager
     */
    SettingsManager getSettingsManager();

    /**
     * Gets the Redis Manager
     *
     * @return Redis Manager
     */
    RedisManager getRedisManager();

    /**
     * Gets the Server Manager
     *
     * @return Server Manager
     */
    ServerManager getServerManager();

    /**
     * Gets the Player Manager
     *
     * @param <E> the BasementPlayer implementation type
     * @return Player Manager
     */
    <E extends BasementPlayer> PlayerManager<E> getPlayerManager();

    /**
     * Gets the Party Manager
     *
     * @return Party Manager
     */
    PartyManager getPartyManager();

    /**
     * Gets the Friend Manager
     *
     * @return Friend Manager
     */
    FriendsManager getFriendsManager();

    /**
     * Gets the Locale Manager
     *
     * @return Locale Manager
     */
    LocaleManager getLocaleManager();

    /**
     * Gets the remote instance of velocity service
     *
     * @return remote instance of velocity service
     */
    RemoteVelocityService getRemoteVelocityService();

    /**
     * Gets the remote instance of cerebrum service
     *
     * @return remote instance of cerebrum service
     */
    RemoteCerebrumService getRemoteCerebrumService();

    /**
     * Gets the remote instance of userData service
     *
     * @return remote instance of userData service
     */
    UserDataService getUserDataService();

    /**
     * Gets a UserData instance from player uuid
     *
     * @param uuid the player uuid
     * @return the UserData instance associated with player
     */
    UserData getUserData(UUID uuid);

    /**
     * Gets a UserData instance from player name
     *
     * @param username the player name
     * @return the UserData instance associated with player
     */
    UserData getUserData(String username);

    /**
     * Gets the default server database
     *
     * @return the default maria database
     */
    AbstractMariaDatabase getDatabase();

    /**
     * Gets a new Connector object
     *
     * @return connector object
     */
    Connector getConnector(TypeConnector type);

    <T extends Holder> T getHolder(Class<?> key, Class<T> type);

    /**
     * Gets the process scheduler
     *
     * @return the process scheduler instance
     */
    ProcessScheduler getScheduler();
}
