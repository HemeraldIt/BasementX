package it.mineblock.basementx.bukkit.plugin;

import ch.jalu.configme.SettingsHolder;
import it.mineblock.basementx.api.bukkit.events.BasementNewServerFound;
import it.mineblock.basementx.api.bukkit.events.BasementServerRemoved;
import it.mineblock.basementx.bukkit.player.BukkitBasementPlayer;
import it.mineblock.basementx.plugin.AbstractBasementPlugin;
import lombok.RequiredArgsConstructor;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@RequiredArgsConstructor
public abstract class AbstractBukkitBasementPlugin extends AbstractBasementPlugin {

    protected final JavaPlugin plugin;
    protected LuckPerms luckPerms;

    @Override
    public void init() {
        basement = new StandardBasementBukkit(this, plugin);

        basement.registerBasementPlayerType(BukkitBasementPlayer.class, true);
        basement.getServerManager().setServerAddConsumer(server -> Bukkit.getPluginManager().callEvent(new BasementNewServerFound(server)));
        basement.getServerManager().setServerRemoveConsumer(server -> Bukkit.getPluginManager().callEvent(new BasementServerRemoved(server)));
    }

    @Override
    public void init(Class<? extends SettingsHolder> settingsHolder) {
        basement = new StandardBasementBukkit(this, plugin);

        basement.registerBasementPlayerType(BukkitBasementPlayer.class, true);
        basement.getServerManager().setServerAddConsumer(server -> Bukkit.getPluginManager().callEvent(new BasementNewServerFound(server)));
        basement.getServerManager().setServerRemoveConsumer(server -> Bukkit.getPluginManager().callEvent(new BasementServerRemoved(server)));
    }

    public LuckPerms getLuckPerms() {
        return luckPerms;
    }
}
