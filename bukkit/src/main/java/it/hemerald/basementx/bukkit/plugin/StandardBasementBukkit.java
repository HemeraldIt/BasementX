package it.hemerald.basementx.bukkit.plugin;

import it.hemerald.basementx.api.bukkit.BasementBukkit;
import it.hemerald.basementx.api.bukkit.chat.Colorizer;
import it.hemerald.basementx.api.bukkit.disguise.module.DisguiseModule;
import it.hemerald.basementx.api.bukkit.item.ItemBuilder;
import it.hemerald.basementx.api.bukkit.item.ItemDataManager;
import it.hemerald.basementx.api.bukkit.nametag.module.NameTagModule;
import it.hemerald.basementx.api.bukkit.permission.PermissionManager;
import it.hemerald.basementx.api.bukkit.player.stream.StreamMode;
import it.hemerald.basementx.api.bukkit.scoreboard.ScoreboardProvider;
import it.hemerald.basementx.api.bukkit.scoreboard.ScoreboardUtils;
import it.hemerald.basementx.api.bukkit.scoreboard.adapter.ScoreboardAdapter;
import it.hemerald.basementx.api.bukkit.staffmode.module.StaffModeModule;
import it.hemerald.basementx.api.redis.messages.implementation.DisguiseMessage;
import it.hemerald.basementx.api.redis.messages.implementation.PartyWarpMessage;
import it.hemerald.basementx.api.redis.messages.implementation.ServerShutdownMessage;
import it.hemerald.basementx.api.redis.messages.implementation.VelocityNotifyMessage;
import it.hemerald.basementx.api.server.BukkitServer;
import it.hemerald.basementx.bukkit.cooldown.BukkitCooldownFactory;
import it.hemerald.basementx.bukkit.disguise.handler.DisguiseHandler;
import it.hemerald.basementx.bukkit.disguise.module.DefaultDisguiseModule;
import it.hemerald.basementx.bukkit.nametag.module.DefaultNameTagModule;
import it.hemerald.basementx.bukkit.permission.DefaultPermissionManager;
import it.hemerald.basementx.bukkit.player.economy.CoinsVaultProvider;
import it.hemerald.basementx.bukkit.player.stream.BukkitStreamMode;
import it.hemerald.basementx.bukkit.plugin.config.BasementBukkitConfig;
import it.hemerald.basementx.bukkit.redis.message.handler.PartyWarpHandler;
import it.hemerald.basementx.bukkit.redis.message.handler.ServerShutdownHandler;
import it.hemerald.basementx.bukkit.redis.message.handler.VelocityNotifyHandler;
import it.hemerald.basementx.bukkit.scoreboard.ScoreboardManager;
import it.hemerald.basementx.bukkit.staffmode.module.DefaultStaffModeModule;
import it.hemerald.basementx.common.nms.v1_19_R1.inventory.InventoryFixer;
import it.hemerald.basementx.common.plugin.StandardBasement;
import lombok.Setter;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class StandardBasementBukkit extends StandardBasement implements BasementBukkit {

    private final JavaPlugin plugin;
    private final LuckPerms luckPerms;
    private final PermissionManager permissionManager;

    private final StaffModeModule staffModeModule;
    private final NameTagModule nameTagModule;
    private final DisguiseModule disguiseModule;

    private final ItemDataManager itemDataManager;
    private final ScoreboardAdapter scoreboardAdapter;
    private final StreamMode streamMode;

    private ScoreboardManager scoreboardManager;

    @Setter
    private String serverID = "unknown";
    private String hoster;
    private boolean hostable;
    private boolean hosted;

    public StandardBasementBukkit(AbstractBukkitBasementPlugin basementPlugin, JavaPlugin plugin) {
        super(basementPlugin, BasementBukkitConfig.class);

        hostable = getSettingsManager().getProperty(BasementBukkitConfig.HOSTABLE);

        this.plugin = plugin;
        this.luckPerms = basementPlugin.getLuckPerms();
        this.permissionManager = new DefaultPermissionManager(luckPerms);

        setServerID(plugin.getServer().getServerName());
        getRemoteVelocityService().registerServer(plugin.getServer().getServerName(), plugin.getServer().getPort());

        String version = plugin.getServer().getClass().getPackage().getName().split("\\.")[3];
        ItemDataManager itemDataManager = null;
        ScoreboardUtils scoreboardUtils = null;

        switch (version) {
            case "v1_8_R3" -> {
                itemDataManager = new it.hemerald.basementx.common.nms.v1_8_R3.item.ItemDataManager();
                scoreboardUtils = new it.hemerald.basementx.common.nms.v1_8_R3.scoreboard.ScoreboardUtils();
                ItemBuilder.setNms(new it.hemerald.basementx.common.nms.v1_8_R3.item.ItemBuilderNMS());
                Colorizer.setNms(new it.hemerald.basementx.common.nms.v1_8_R3.chat.ColorizerNMS());
            }
            case "v1_19_R1" -> {
                itemDataManager = new it.hemerald.basementx.common.nms.v1_19_R1.item.ItemDataManager(plugin);
                scoreboardUtils = new it.hemerald.basementx.common.nms.v1_19_R1.scoreboard.ScoreboardUtils();
                ItemBuilder.setNms(new it.hemerald.basementx.common.nms.v1_19_R1.item.ItemBuilderNMS());
                Colorizer.setNms(new it.hemerald.basementx.common.nms.v1_19_R1.chat.ColorizerNMS());
                new InventoryFixer(plugin);
            }
        }

        this.staffModeModule = new DefaultStaffModeModule(this);
        this.nameTagModule = new DefaultNameTagModule(this);
        this.disguiseModule = new DefaultDisguiseModule(this);

        StreamMode streamMode = new BukkitStreamMode(getPlayerManager(), getDisguiseModule());

        getRedisManager().registerTopicListener(DisguiseMessage.TOPIC, new DisguiseHandler(this));
        getRedisManager().registerTopicListener(VelocityNotifyMessage.TOPIC, new VelocityNotifyHandler(this));
        getRedisManager().registerTopicListener(PartyWarpMessage.TOPIC, new PartyWarpHandler(this));
        getRedisManager().registerTopicListener(ServerShutdownMessage.TOPIC, new ServerShutdownHandler(this));

        if(Bukkit.getPluginManager().getPlugin("Vault") != null) {
            Bukkit.getServicesManager().register(
                    net.milkbowl.vault.economy.Economy.class,
                    new CoinsVaultProvider(getPlayerManager()),
                    getPlugin(),
                    ServicePriority.Normal
            );
        }

        this.scoreboardAdapter = ScoreboardAdapter.builder(plugin, scoreboardUtils).build();
        this.itemDataManager = itemDataManager;
        this.streamMode = streamMode;
        this.streamMode.toggle(getSettingsManager().getProperty(BasementBukkitConfig.STREAM_MODE));

        cooldownFactory = new BukkitCooldownFactory(plugin);
    }

    @Override
    public void start() {
        super.start();

        disguiseModule.enable();
        staffModeModule.enable();
        nameTagModule.enable();
    }

    @Override
    public void stop() {
        super.stop();

        staffModeModule.disable();
        nameTagModule.disable();
        disguiseModule.disable();
        if(scoreboardManager != null) scoreboardManager.stop();
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        hostable = getSettingsManager().getProperty(BasementBukkitConfig.HOSTABLE);
    }

    @Override
    public JavaPlugin getPlugin() {
        return plugin;
    }

    @Override
    public PermissionManager getPermissionManager() {
        return permissionManager;
    }

    @Override
    public LuckPerms getLuckPerms() {
        return luckPerms;
    }

    @Override
    public ScoreboardAdapter getScoreboardAdapter() {
        return scoreboardAdapter;
    }

    @Override
    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    @Override
    public StaffModeModule getStaffModeModule() {
        return staffModeModule;
    }

    @Override
    public NameTagModule getNameTagModule() {
        return nameTagModule;
    }

    @Override
    public DisguiseModule getDisguiseModule() {
        return disguiseModule;
    }

    @Override
    public ItemDataManager getItemDataManager() {
        return itemDataManager;
    }

    @Override
    public StreamMode getStreamMode() {
        return streamMode;
    }

    @Override
    public void registerScoreboard(ScoreboardProvider scoreboardProvider, int delay) {
        scoreboardManager = new ScoreboardManager(plugin, scoreboardProvider, delay);
    }

    @Override
    public BukkitServer getServer() {
        return getServerManager().getServer(serverID).get();
    }

    @Override
    public String getServerID() {
        return serverID;
    }

    @Override
    public boolean isHostable() {
        return hostable;
    }

    @Override
    public boolean isHosted() {
        return hosted;
    }

    @Override
    public String getHoster() {
        return hoster;
    }

    @Override
    public void setHosted(boolean hosted) {
        this.hosted = hosted;
    }

    @Override
    public void setHoster(String hoster) {
        this.hoster = hoster;
    }
}
