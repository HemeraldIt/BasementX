package it.hemerald.basementx.api.bukkit;

import it.hemerald.basementx.api.bukkit.disguise.module.DisguiseModule;
import it.hemerald.basementx.api.bukkit.item.ItemDataManager;
import it.hemerald.basementx.api.bukkit.nametag.module.NameTagModule;
import it.hemerald.basementx.api.bukkit.permission.PermissionManager;
import it.hemerald.basementx.api.bukkit.scoreboard.IScoreboardManager;
import it.hemerald.basementx.api.bukkit.scoreboard.ScoreboardProvider;
import it.hemerald.basementx.api.bukkit.scoreboard.adapter.ScoreboardAdapter;
import it.hemerald.basementx.api.bukkit.staffmode.module.StaffModeModule;
import it.hemerald.basementx.api.server.BukkitServer;
import it.hemerald.basementx.api.Basement;
import net.luckperms.api.LuckPerms;
import org.bukkit.plugin.java.JavaPlugin;

public interface BasementBukkit extends Basement {

    /**
     * Gets the {@link JavaPlugin} instance
     * @return java plugin instance
     */
    JavaPlugin getPlugin();

    /**
     * Gets the {@link PermissionManager}
     * @return permission manager instance
     */
    PermissionManager getPermissionManager();

    /**
     * Gets the {@link LuckPerms}
     * @return luckperms instance
     */
    LuckPerms getLuckPerms();

    /**
     * Gets the StaffMode module for managing vanish and staffmode
     * @return the StaffMode module
     */
    StaffModeModule getStaffModeModule();

    /**
     * Gets the NameTag module for managing tablist nametags
     * @return the NameTag module
     */
    NameTagModule getNameTagModule();

    /**
     * Gets the DisguiseModule for managing player disguise
     * @return the Disguise module
     */
    DisguiseModule getDisguiseModule();

    /**
     * Gets the ScoreboardAdapter for managing scoreboard actions
     * @return the Scoreboard adapter
     */
    ScoreboardAdapter getScoreboardAdapter();

    /**
     * Register a custom Scoreboard
     * @param scoreboardProvider the class that create scoreboard
     * @param delay time in ticks that scoreboard refresh
     */
    void registerScoreboard(ScoreboardProvider scoreboardProvider, int delay);

    /**
     * Gets the ScoreboardManager
     * @return the Scoreboard manager
     */
    IScoreboardManager getScoreboardManager();

    /**
     * Gets the ItemDataManager for managing item custom data nbt
     * @return the ItemData manager
     */
    ItemDataManager getItemDataManager();

    /**
     * Gets the server object
     * @return the server object
     */
    BukkitServer getServer();

    /**
     * Gets if the server ID
     * @return the server ID
     */
    String getServerID();

    /**
     * Gets if the server can be hosted
     * @return true if the server can be hosted, false otherwise
     */
    boolean isHostable();

    /**
     * Gets if the server is hosted
     * @return true if is hosted, false otherwise
     */
    boolean isHosted();

    /**
     * Gets the name of who hosted the game
     * @return name of who hosted the game
     */
    String getHoster();

    /**
     * Set a new server id
     * @param serverID the server id
     */
    void setServerID(String serverID);

    /**
     * Set it to true if the server is hosted, false otherwise
     * @param hosted true if is hosted, false otherwise
     */
    void setHosted(boolean hosted);

    /**
     * Set the player who hosted the game
     * @param hoster the player who hosted the game
     */
    void setHoster(String hoster);
}
