package it.hemerald.basementx.common.plugin;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import it.hemerald.basementx.common.config.BasementConfig;
import it.hemerald.basementx.common.locale.DefaultLocaleManager;
import it.hemerald.basementx.common.party.DefaultPartyManager;
import it.hemerald.basementx.common.persistence.hikari.TypeHolder;
import it.hemerald.basementx.common.player.DefaultPlayerManager;
import it.hemerald.basementx.common.redis.DefaultRedisManager;
import it.hemerald.basementx.common.server.DefaultServerManager;
import it.hemerlad.basementx.api.Basement;
import it.hemerlad.basementx.api.cooldown.CooldownFactory;
import it.hemerlad.basementx.api.locale.LocaleManager;
import it.hemerlad.basementx.api.party.PartyManager;
import it.hemerlad.basementx.api.persistence.generic.Holder;
import it.hemerlad.basementx.api.persistence.generic.connection.Connector;
import it.hemerlad.basementx.api.persistence.generic.connection.TypeConnector;
import it.hemerlad.basementx.api.persistence.maria.structure.AbstractMariaDatabase;
import it.hemerlad.basementx.api.persistence.maria.structure.AbstractMariaHolder;
import it.hemerlad.basementx.api.player.BasementPlayer;
import it.hemerlad.basementx.api.player.PlayerManager;
import it.hemerlad.basementx.api.plugin.BasementPlugin;
import it.hemerlad.basementx.api.redis.RedisManager;
import it.hemerlad.basementx.api.remote.RemoteCerebrumService;
import it.hemerlad.basementx.api.remote.RemoteVelocityService;
import it.hemerlad.basementx.api.server.ServerManager;
import it.hemerald.basementx.common.cooldown.DefaultCooldownFactory;

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

    protected CooldownFactory cooldownFactory;

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

        cooldownFactory = new DefaultCooldownFactory();
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
    public CooldownFactory getCooldownFactory() {
        return cooldownFactory;
    }
}
