package it.mineblock.basementx.plugin;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import it.mineblock.basementx.api.Basement;
import it.mineblock.basementx.api.locale.LocaleManager;
import it.mineblock.basementx.api.party.PartyManager;
import it.mineblock.basementx.api.persistence.generic.Holder;
import it.mineblock.basementx.api.persistence.generic.connection.Connector;
import it.mineblock.basementx.api.persistence.generic.connection.TypeConnector;
import it.mineblock.basementx.api.persistence.maria.structure.AbstractMariaDatabase;
import it.mineblock.basementx.api.persistence.maria.structure.AbstractMariaHolder;
import it.mineblock.basementx.api.player.BasementPlayer;
import it.mineblock.basementx.api.player.PlayerManager;
import it.mineblock.basementx.api.plugin.BasementPlugin;
import it.mineblock.basementx.api.redis.RedisManager;
import it.mineblock.basementx.api.remote.RemoteCerebrumService;
import it.mineblock.basementx.api.remote.RemoteVelocityService;
import it.mineblock.basementx.api.server.ServerManager;
import it.mineblock.basementx.config.BasementConfig;
import it.mineblock.basementx.locale.DefaultLocaleManager;
import it.mineblock.basementx.party.DefaultPartyManager;
import it.mineblock.basementx.persistence.hikari.TypeHolder;
import it.mineblock.basementx.player.DefaultPlayerManager;
import it.mineblock.basementx.redis.DefaultRedisManager;
import it.mineblock.basementx.server.DefaultServerManager;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.UUID;

public class StandardBasement implements Basement {

    private final UUID uuid = UUID.randomUUID();

    private final SettingsManager settingsManager;
    private final RedisManager redisManager;
    private final ServerManager serverManager;
    private PlayerManager<? extends BasementPlayer> playerManager;
    private final PartyManager partyManager;
    private final LocaleManager localeManager;
    private final RemoteVelocityService velocityService;
    private final RemoteCerebrumService cerebrumService;

    private final AbstractMariaDatabase database;

    private final HashMap<Class<?>, Holder> holderBucket = new HashMap<>();

    private boolean savePlayer = false;

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

        velocityService = redisManager.getRedissonClient().getRemoteService().get(RemoteVelocityService.class);
        cerebrumService = redisManager.getRedissonClient().getRemoteService().get(RemoteCerebrumService.class);
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
    public <T extends BasementPlayer> void registerBasementPlayerType(Class<T> type) {
        playerManager = new DefaultPlayerManager<T>(this);
        this.savePlayer = false;
    }

    @Override
    public boolean savePlayer() {
        return savePlayer;
    }

    @Override
    public AbstractMariaDatabase getDatabase() {
        return database;
    }

    @Override
    public Connector getConnector(TypeConnector type) {
        return it.mineblock.basementx.persistence.hikari.TypeConnector.valueOf(type.toString()).provide();
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
}
