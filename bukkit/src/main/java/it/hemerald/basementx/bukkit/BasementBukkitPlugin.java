package it.hemerald.basementx.bukkit;

import ch.jalu.configme.SettingsHolder;
import it.hemerald.basementx.bukkit.commands.*;
import it.hemerald.basementx.bukkit.generic.handler.TpToHandler;
import it.hemerald.basementx.bukkit.listeners.HostListener;
import it.hemerald.basementx.bukkit.listeners.PlayerListener;
import it.hemerald.basementx.bukkit.listeners.VulcanListener;
import it.hemerald.basementx.bukkit.locale.EnglishLocale;
import it.hemerald.basementx.bukkit.locale.ItalianLocale;
import it.hemerald.basementx.bukkit.plugin.AbstractBukkitBasementPlugin;
import it.hemerald.basementx.bukkit.placeholders.BasementPlaceholder;
import it.hemerald.basementx.api.Basement;
import it.hemerald.basementx.api.bukkit.BasementBukkit;
import it.hemerald.basementx.api.redis.messages.implementation.BukkitNotifyShutdownMessage;
import it.hemerald.basementx.api.redis.messages.implementation.TpToMessage;
import it.hemerald.basementx.api.server.BukkitServer;
import it.hemerald.basementx.api.server.ServerStatus;
import lombok.RequiredArgsConstructor;
import me.frep.vulcan.api.VulcanAPI;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.event.user.UserDataRecalculateEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.spigotmc.SpigotConfig;

import java.io.File;

public class BasementBukkitPlugin extends AbstractBukkitBasementPlugin {

    private final File config;
    private BasementBukkit basement;
    private BukkitTask task;

    private BukkitServer server;

    public BasementBukkitPlugin(JavaPlugin plugin) {
        super(plugin);
        config = new File(plugin.getDataFolder(), "config.yml");
        SpigotConfig.unknownCommandMessage = ChatColor.RED + ChatColor.BOLD.toString() + "ERRORE! " + ChatColor.RED + "Comando sconosciuto!";
    }

    @Override
    public void init() {
        super.init();
        this.basement = (BasementBukkit) super.basement;
    }

    @Override
    public void init(Class<? extends SettingsHolder> settingsHolder) {
        super.init(settingsHolder);
        this.basement = (BasementBukkit) super.basement;
    }

    private void setupServer() {
        server = new BukkitServer(basement.getServerID());
    }

    @Override
    public void enable() {
        RegisteredServiceProvider<LuckPerms> provider = plugin.getServer().getServicesManager().getRegistration(LuckPerms.class);
        if(provider != null) {
            luckPerms = provider.getProvider();
        } else {
            plugin.getLogger().severe("LuckPerms not found!");
            Bukkit.shutdown();
            return;
        }
        super.enable();
        setupServer();

        task = new ServerInfoRunnable(ServerStatus.OPEN).runTaskTimerAsynchronously(plugin, 10L, 20L);
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new BasementPlaceholder(basement).register();
        }

        //Added a check to auto-update the nametag adapter of that player if his group change
        luckPerms.getEventBus().subscribe(UserDataRecalculateEvent.class, (event) -> {
            Player player = Bukkit.getPlayer(event.getUser().getUniqueId());
            if (player == null) return;
            basement.getNameTagModule().update(player);
        });
    }

    @Override
    public void disable() {
        if(task != null) task.cancel();
        getBasement().getRedisManager().publishMessage(new BukkitNotifyShutdownMessage(basement.getServerID()));
        getBasement().getServerManager().removeServer(basement.getServerID());
        super.disable();
    }

    @Override
    public File getConfig() {
        return config;
    }

    @Override
    protected void registerApiOnPlatform(Basement basement) {
        plugin.getServer().getServicesManager().register(Basement.class, basement, plugin, ServicePriority.Normal);
        plugin.getServer().getServicesManager().register(BasementBukkit.class, (BasementBukkit) basement, plugin, ServicePriority.Normal);
    }

    @Override
    protected void registerCommands() {
        plugin.getCommand("basement").setExecutor(new BasementBukkitCommand(basement));
        plugin.getCommand("staffmode").setExecutor(new StaffModeCommand(basement));
        plugin.getCommand("tabreload").setExecutor(new ReloadTabCommand(basement));
        plugin.getCommand("vanish").setExecutor(new VanishCommand(basement));
        plugin.getCommand("tag").setExecutor(new TagCommand(basement));
    }

    @Override
    protected void registerListeners() {
        PlayerListener playerListener = new PlayerListener(basement);
        plugin.getServer().getPluginManager().registerEvents(playerListener, plugin);
        getBasement().getRedisManager().registerTopicListener(TpToMessage.TOPIC, new TpToHandler(playerListener));
        if(basement.isHostable()) {
            plugin.getServer().getPluginManager().registerEvents(new HostListener(basement), plugin);
        }
        try {
            if(plugin.getServer().getPluginManager().isPluginEnabled("Vulcan") && VulcanAPI.Factory.getApi() != null) {
                VulcanListener vulcanListener = new VulcanListener(basement);
                plugin.getServer().getPluginManager().registerEvents(vulcanListener, plugin);
                plugin.getCommand("cheatban").setExecutor(new CheatBanCommand(vulcanListener::ban));
            }
        } catch (Exception ignored) {}
    }

    @Override
    protected void registerLocales() {
        basement.getLocaleManager().addLocale("basement", new EnglishLocale(plugin));
        basement.getLocaleManager().addLocale("basement", new ItalianLocale(plugin));
    }

    @RequiredArgsConstructor
    private class ServerInfoRunnable extends BukkitRunnable {
        private final ServerStatus serverStatus;
        @Override
        public void run() {
            server.setWhitelist(Bukkit.hasWhitelist());
            server.setOnline(Bukkit.getServer().getOnlinePlayers().size());
            server.setMax(Bukkit.getMaxPlayers());
            server.setHoster(basement.getHoster());
            server.setHostable(basement.isHostable());
            server.setHosted(basement.isHosted());
            server.setStatus(serverStatus);
            getBasement().getServerManager().addServer(basement.getServerID(), server);
        }
    }
}
