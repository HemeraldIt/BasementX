package it.hemerald.basementx.common.plugin;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import it.hemerald.basementx.api.Basement;
import it.hemerald.basementx.api.concurrent.process.ProcessScheduler;
import it.hemerald.basementx.api.locale.LocaleManager;
import it.hemerald.basementx.api.party.PartyManager;
import it.hemerald.basementx.api.persistence.generic.Holder;
import it.hemerald.basementx.api.persistence.generic.connection.Connector;
import it.hemerald.basementx.api.persistence.generic.connection.TypeConnector;
import it.hemerald.basementx.api.persistence.maria.structure.AbstractMariaDatabase;
import it.hemerald.basementx.api.persistence.maria.structure.AbstractMariaHolder;
import it.hemerald.basementx.api.player.BasementPlayer;
import it.hemerald.basementx.api.player.PlayerManager;
import it.hemerald.basementx.api.player.UserData;
import it.hemerald.basementx.api.plugin.BasementPlugin;
import it.hemerald.basementx.api.redis.RedisManager;
import it.hemerald.basementx.api.remote.RemoteCerebrumService;
import it.hemerald.basementx.api.remote.RemoteVelocityService;
import it.hemerald.basementx.api.remote.UserDataService;
import it.hemerald.basementx.api.server.ServerManager;
import it.hemerald.basementx.common.config.BasementConfig;
import it.hemerald.basementx.common.locale.DefaultLocaleManager;
import it.hemerald.basementx.common.party.DefaultPartyManager;
import it.hemerald.basementx.common.persistence.hikari.TypeHolder;
import it.hemerald.basementx.common.player.DefaultPlayerManager;
import it.hemerald.basementx.common.redis.DefaultRedisManager;
import it.hemerald.basementx.common.server.DefaultServerManager;
import org.redisson.api.RRemoteService;
import org.redisson.api.condition.Conditions;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class StandardBasement implements Basement {

    private final UUID uuid = UUID.randomUUID();

    private final SettingsManager settingsManager;
    private final RedisManager redisManager;
    private final ServerManager serverManager;
    private final PlayerManager<? extends BasementPlayer> playerManager;
    private final PartyManager partyManager;
    private final LocaleManager localeManager;
    private final RemoteVelocityService velocityService;
    private final RemoteCerebrumService cerebrumService;
    private final UserDataService userDataService;

    private final AbstractMariaDatabase database;

    private final HashMap<Class<?>, Holder> holderBucket = new HashMap<>();

    private final ProcessScheduler processScheduler;

    public StandardBasement(BasementPlugin plugin) {
        this(plugin, null);
    }

    public StandardBasement(BasementPlugin plugin, Class<? extends SettingsHolder> settingsHolder) {
        settingsManager = SettingsManagerBuilder.withYamlFile(plugin.getConfig()).configurationData(
                settingsHolder == null ? BasementConfig.class : settingsHolder).useDefaultMigrationService().create();

        redisManager = new DefaultRedisManager(settingsManager);

        Connector connector = getConnector(TypeConnector.MARIADB);
        connector.connect(getSettingsManager().getProperty(BasementConfig.MARIA_HOST),
                getSettingsManager().getProperty(BasementConfig.MARIA_USERNAME),
                getSettingsManager().getProperty(BasementConfig.MARIA_PASSWORD));
        AbstractMariaHolder holder = getHolder(Basement.class, AbstractMariaHolder.class);
        holder.setConnector(connector);
        database = holder.createDatabase("minecraft").ifNotExists(true).build().execReturn();

        serverManager = new DefaultServerManager(this);
        playerManager = new DefaultPlayerManager<>(this);
        partyManager = new DefaultPartyManager(redisManager);
        localeManager = new DefaultLocaleManager();

        RRemoteService remoteService = redisManager.getRedissonClient().getRemoteService();
        velocityService = remoteService.get(RemoteVelocityService.class);
        cerebrumService = remoteService.get(RemoteCerebrumService.class);
        userDataService = remoteService.get(UserDataService.class);

        processScheduler = new ProcessScheduler();
    }

    public void start() {}

    public void stop() {
        redisManager.getRedissonClient().shutdown();
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public void reloadConfig() {
        settingsManager.reload();
    }

    @Override
    public SettingsManager getSettingsManager() {
        return settingsManager;
    }

    @Override
    public RedisManager getRedisManager() {
        return redisManager;
    }

    @Override
    public ServerManager getServerManager() {
        return serverManager;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends BasementPlayer> PlayerManager<E> getPlayerManager() {
        return (PlayerManager<E>) playerManager;
    }

    @Override
    public PartyManager getPartyManager() {
        return partyManager;
    }

    @Override
    public LocaleManager getLocaleManager() {
        return localeManager;
    }

    @Override
    public RemoteVelocityService getRemoteVelocityService() {
        return velocityService;
    }

    @Override
    public RemoteCerebrumService getRemoteCerebrumService() {
        return cerebrumService;
    }

    @Override
    public UserDataService getUserDataService() {
        return userDataService;
    }

    @Override
    public UserData getUserData(UUID uuid) {
        return redisManager.getRedissonClient().getLiveObjectService().get(UserData.class, uuid.toString());
    }

    @Override
    public UserData getUserData(String username) {
        Collection<UserData> data = redisManager.getRedissonClient().getLiveObjectService().find(UserData.class, Conditions.eq("username", username));
        if (data.isEmpty()) return null;
        return (UserData) data.toArray()[0];
    }

    @Override
    public AbstractMariaDatabase getDatabase() {
        return database;
    }

    @Override
    public Connector getConnector(TypeConnector type) {
        return it.hemerald.basementx.common.persistence.hikari.TypeConnector.valueOf(type.toString()).provide();
    }

    @Override
    public <T extends Holder> T getHolder(Class<?> key, Class<T> type) {
        Holder holder = holderBucket.get(key);
        if (holder == null) {
            try {
                T instance = type.cast(TypeHolder.TYPES.get(type).getDeclaredConstructor().newInstance());
                holderBucket.put(key, instance);
                return instance;
            } catch (InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                return null;
            }
        }
        return type.cast(holder);
    }

    @Override
    public ProcessScheduler getScheduler() {
        return processScheduler;
    }
}
