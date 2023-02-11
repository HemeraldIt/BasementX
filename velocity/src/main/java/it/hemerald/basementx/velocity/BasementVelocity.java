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
import it.hemerald.basementx.api.redis.messages.implementation.BukkitNotifyShutdownMessage;
import it.hemerald.basementx.api.redis.messages.implementation.ServerShutdownMessage;
import it.hemerald.basementx.api.redis.messages.implementation.VelocityNotifyMessage;
import it.hemerald.basementx.api.remote.RemoteVelocityService;
import it.hemerald.basementx.api.remote.UserDataService;
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
    private final Map<String, AlertType> toggled = new HashMap<>();
    private Together together;
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

        userDataManager = new UserDataManager(this);

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
        server.getCommandManager().register(server.getCommandManager().metaBuilder("basementsub").build(), new SubCommand());
        server.getCommandManager().register(server.getCommandManager().metaBuilder("basementunsub").build(), new UnSubCommand());
        server.getCommandManager().register(server.getCommandManager().metaBuilder("staffnotes").aliases("staffnote").aliases("sn").aliases("sns").build(),
                new StaffNoteCommand(this));

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
