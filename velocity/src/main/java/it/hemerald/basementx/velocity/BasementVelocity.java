package it.hemerald.basementx.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import it.hemerald.basementx.api.Basement;
import it.hemerald.basementx.api.persistence.generic.connection.Connector;
import it.hemerald.basementx.api.persistence.generic.connection.TypeConnector;
import it.hemerald.basementx.api.persistence.maria.queries.builders.table.QueryBuilderCreateTable;
import it.hemerald.basementx.api.persistence.maria.structure.AbstractMariaDatabase;
import it.hemerald.basementx.api.persistence.maria.structure.AbstractMariaHolder;
import it.hemerald.basementx.api.persistence.maria.structure.column.MariaType;
import it.hemerald.basementx.api.redis.messages.implementation.BukkitNotifyShutdownMessage;
import it.hemerald.basementx.api.redis.messages.implementation.ServerShutdownMessage;
import it.hemerald.basementx.api.redis.messages.implementation.VelocityNotifyMessage;
import it.hemerald.basementx.api.remote.RemoteVelocityService;
import it.hemerald.basementx.api.remote.UserDataService;
import it.hemerald.basementx.common.config.BasementConfig;
import it.hemerald.basementx.common.plugin.AbstractBasementPlugin;
import it.hemerald.basementx.velocity.alert.AlertType;
import it.hemerald.basementx.velocity.commands.*;
import it.hemerald.basementx.velocity.listeners.DisguiseListener;
import it.hemerald.basementx.velocity.listeners.PlayerListener;
import it.hemerald.basementx.velocity.locale.EnglishLocale;
import it.hemerald.basementx.velocity.locale.ItalianLocale;
import it.hemerald.basementx.velocity.player.UserDataManager;
import it.hemerald.basementx.velocity.redis.message.handler.BukkitNotifyShutdownHandler;
import it.hemerald.basementx.velocity.redis.message.handler.ServerShutdownHandler;
import it.hemerald.basementx.velocity.remote.RemoteVelocityServiceImpl;
import it.hemerald.basementx.velocity.remote.UserDataServiceImpl;
import it.hemerald.basementx.velocity.together.Together;
import lombok.Getter;
import org.redisson.api.RRemoteService;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

@Plugin(id = "basement", name = "Basement", version = "1.0", authors = {"TheDarkSword"}, dependencies = {@Dependency(id = "luckperms"), @Dependency(id = "viaversion")})
@Getter
public class BasementVelocity extends AbstractBasementPlugin {

    private final ProxyServer server;
    private final File dataFolder;
    private final File configFile;
    private final Logger logger;

    private Together together;

    private AbstractMariaHolder holder;
    private AbstractMariaDatabase database;

    private final Map<String, AlertType> toggled = new HashMap<>();

    private UserDataManager userDataManager;

    @Inject
    public BasementVelocity(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;

        dataFolder = dataDirectory.toFile();
        configFile = new File(dataDirectory.toFile(), "config.yml");
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onInitialize(ProxyInitializeEvent event) {
        enable();

        Connector connector = basement.getConnector(TypeConnector.MARIADB);
        connector.connect(basement.getSettingsManager().getProperty(BasementConfig.MARIA_HOST),
                basement.getSettingsManager().getProperty(BasementConfig.MARIA_USERNAME),
                basement.getSettingsManager().getProperty(BasementConfig.MARIA_PASSWORD));
        holder = basement.getHolder(BasementVelocity.class, AbstractMariaHolder.class);
        holder.setConnector(connector);
        database = holder.createDatabase("minecraft").ifNotExists(true).build().execReturn();

        database.createTable("player_boosters").ifNotExists(true)
                .addColumn("user_id", MariaType.INT, QueryBuilderCreateTable.ColumnData.NOT_NULL)
                .addColumn("mode", MariaType.INT, 4, QueryBuilderCreateTable.ColumnData.NOT_NULL)
                .addColumn("type", MariaType.INT, QueryBuilderCreateTable.ColumnData.NOT_NULL)
                .addColumn("value", MariaType.INT, QueryBuilderCreateTable.ColumnData.NOT_NULL)
                .addColumn("time", MariaType.INT, QueryBuilderCreateTable.ColumnData.NOT_NULL)
                .withPrimaryKeys("user_id", "mode", "type")
                .addForeignKeyConstraint("user_id", "players", "id", "ON DELETE CASCADE ON UPDATE CASCADE")
                .build().exec();

       //database.createTable(DatabaseConstants.PLAYER_TABLE).ifNotExists(true)
       //        .addColumn("id", MariaType.INT, QueryBuilderCreateTable.ColumnData.AUTO_INCREMENT)
       //        .addColumn("uuid", MariaType.VARCHAR, 36, QueryBuilderCreateTable.ColumnData.UNIQUE)
       //        .addColumn("username", MariaType.VARCHAR, 32)
       //        .addColumn("last_join", MariaType.DATETIME, "CURRENT_TIMESTAMP")
       //        .withPrimaryKeys("id").build().exec();

       //database.createTable(DatabaseConstants.PREMIUM_TABLE).ifNotExists(true)
       //        .addColumn("id", MariaType.INT, QueryBuilderCreateTable.ColumnData.AUTO_INCREMENT)
       //        .addColumn("uuid", MariaType.VARCHAR, 36, QueryBuilderCreateTable.ColumnData.UNIQUE)
       //        .addColumn("username", MariaType.VARCHAR, 32, QueryBuilderCreateTable.ColumnData.UNIQUE)
       //        .withPrimaryKeys("id").build().exec();

        userDataManager = new UserDataManager(this, database);

        basement.getRedisManager().registerTopicListener(ServerShutdownMessage.TOPIC, new ServerShutdownHandler(server));
        basement.getRedisManager().registerTopicListener(BukkitNotifyShutdownMessage.TOPIC, new BukkitNotifyShutdownHandler(server));
        RRemoteService remoteService = basement.getRedisManager().getRedissonClient().getRemoteService();
        remoteService.register(RemoteVelocityService.class, new RemoteVelocityServiceImpl(this), 3, Executors.newSingleThreadExecutor());
        remoteService.register(UserDataService.class, new UserDataServiceImpl(userDataManager), 3, Executors.newSingleThreadExecutor());

        together = new Together(this);
        together.enable();

        server.getCommandManager().register(server.getCommandManager().metaBuilder("disguise").aliases("disg").build(), new ToggleDisguiseCommand(this));
        server.getCommandManager().register(server.getCommandManager().metaBuilder("createserver").aliases("cs").build(), new CreateServerCommand(this));
        server.getCommandManager().register(server.getCommandManager().metaBuilder("alerts").aliases("anticheat").build(), new AlertsCommand(this));
        server.getCommandManager().register(server.getCommandManager().metaBuilder("tpto").build(), new TpToCommand(this));
        server.getCommandManager().register(server.getCommandManager().metaBuilder("goto").build(), new GoToCommand(this));
        server.getCommandManager().register(server.getCommandManager().metaBuilder("spectate").aliases("spect").build(), new SpectateCommand(this));
        server.getCommandManager().register(server.getCommandManager().metaBuilder("hub").aliases("lobby").build(), new HubCommand(this));
        server.getCommandManager().register(server.getCommandManager().metaBuilder("find").build(), new FindCommand(this));
        server.getCommandManager().register(server.getCommandManager().metaBuilder("send").build(), new SendCommand(this));
        server.getCommandManager().register(server.getCommandManager().metaBuilder("globalchat").aliases("gc").build(), new GlobalChatCommand(this));

        server.getEventManager().register(this, new PlayerListener(this));
        server.getEventManager().register(this, new DisguiseListener(getBasement()));

        basement.getRedisManager().publishMessage(new VelocityNotifyMessage(false));
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        basement.getRedisManager().publishMessage(new VelocityNotifyMessage(true));
        together.disable();
        userDataManager.shutdown();
        disable();
        holder.close();
    }

    @Override
    protected void registerApiOnPlatform(Basement basement) {
        //Velocity doesn't provide a service manager
    }

    @Override
    protected void registerCommands() {

    }

    @Override
    protected void registerListeners() {

    }

    @Override
    protected void registerLocales() {
        basement.getLocaleManager().addLocale("basement", new EnglishLocale(dataFolder));
        basement.getLocaleManager().addLocale("basement", new ItalianLocale(dataFolder));
    }

    @Override
    public File getConfig() {
        return configFile;
    }
}
