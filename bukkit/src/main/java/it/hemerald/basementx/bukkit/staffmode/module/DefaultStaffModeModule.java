package it.hemerald.basementx.bukkit.staffmode.module;

import it.hemerald.basementx.api.bukkit.BasementBukkit;
import it.hemerald.basementx.api.bukkit.staffmode.adapter.StaffModeAdapter;
import it.hemerald.basementx.api.bukkit.staffmode.module.StaffModeModule;
import it.hemerald.basementx.bukkit.generic.chat.BasementMessages;
import it.hemerald.basementx.bukkit.plugin.config.BasementBukkitConfig;
import it.hemerald.basementx.bukkit.staffmode.adapter.DefaultStaffModeAdapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.redisson.api.RSetCache;

import java.util.ArrayList;
import java.util.List;

public class DefaultStaffModeModule extends StaffModeModule implements Listener {

    private RSetCache<String> vanishSet;

    public DefaultStaffModeModule(BasementBukkit basement) {
        super(basement, BasementBukkitConfig.STAFF_MODE);
    }

    @Override
    protected StaffModeAdapter getDefaultAdapter() {
        return new DefaultStaffModeAdapter(this);
    }

    @Override
    public void onStart() {
        basement.getPlugin().getServer().getPluginManager().registerEvents(this, basement.getPlugin());
        vanishSet = basement.getRedisManager().getRedissonClient().getSetCache("vanish");
    }

    @Override
    public void onStop() {
        for (Player player : adapter.getInventories().keySet()) {
            disableMode(player);
        }

        HandlerList.unregisterAll(this);
        vanishSet = null;
    }

    @Override
    public void enableMode(Player player) {
        if(!localEnable(player)) return;

        vanish(player);

        player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "STAFF! " + ChatColor.RESET + "" + ChatColor.WHITE + "StaffMode abilitata!");
    }

    private boolean localEnable(Player player) {
        if(!isEnabled() || isMode(player) || !adapter.onEnterMode(player)) return false;

        player.setAllowFlight(true);
        player.setFlying(true);

        adapter.getInventories().put(player, player.getInventory().getContents());
        player.getInventory().clear();
        adapter.setupInventory(player);

        return true;
    }

    @Override
    public void disableMode(Player player) {
        if (!localDisable(player)) return;

        unvanish(player);

        player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "STAFF! " + ChatColor.RESET + "" + ChatColor.WHITE + "StaffMode disabilitata!");
    }

    private boolean localDisable(Player player) {
        if(!isEnabled() || !isMode(player) || !adapter.onExitMode(player)) return false;

        player.setFlying(false);
        player.setAllowFlight(false);

        player.getInventory().setContents(adapter.getInventories().get(player));

        adapter.getInventories().remove(player);

        player.updateInventory();
        return true;
    }

    @Override
    public void toggleMode(Player player) {
        if(isMode(player)) disableMode(player);
        else enableMode(player);
    }

    @Override
    public boolean isMode(Player player) {
        if(!isEnabled()) return false;

        return adapter.getInventories().containsKey(player);
    }

    @Override
    public void vanish(Player player) {
        if(!localVanish(player)) return;

        adapter.setupInventory(player);

        vanishSet.addAsync(player.getName());

        player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "STAFF! " + ChatColor.RESET + "" + ChatColor.WHITE + "Vanish abilitata!");
    }

    public boolean localVanish(Player player) {
        if(!isEnabled() || isVanished(player)) return false;

        List<Player> targets = new ArrayList<>(Bukkit.getOnlinePlayers());
        targets.removeIf(target -> target.hasPermission(BasementMessages.STAFF_PERMISSION));
        if(!adapter.onVanish(player, targets)) return false;

        adapter.getVanished().add(player);

        for (Player target : targets) {
            target.hidePlayer(player);
        }

        return true;
    }

    @Override
    public void unvanish(Player player) {
        if(!localUnvanish(player)) return;

        adapter.setupInventory(player);

        vanishSet.removeAsync(player.getName());

        player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "STAFF! " + ChatColor.RESET + "" + ChatColor.WHITE + "Vanish disabilitata!");
    }

    public boolean localUnvanish(Player player) {
        if(!isEnabled() || !isVanished(player)) return false;

        List<Player> targets = new ArrayList<>(Bukkit.getOnlinePlayers());
        if (!adapter.onUnvanish(player, targets)) return false;

        adapter.getVanished().remove(player);
        for (Player target : targets) {
            target.showPlayer(player);
        }

        return true;
    }

    @Override
    public void toggleVanish(Player player) {
        if(isVanished(player)) unvanish(player);
        else vanish(player);
    }

    @Override
    public boolean isVanished(Player player) {
        if(!isEnabled()) return false;

        return adapter.getVanished().contains(player);
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        if(isMode(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(isMode((Player) event.getWhoClicked())) event.setCancelled(true);
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        if(isMode(event.getPlayer())) {
            event.setCancelled(true);
            adapter.getListener(event.getPlayer().getInventory().getItemInHand()).onInteractEntity(event);
        }
    }

    @EventHandler
    public void onAttackPlayer(EntityDamageByEntityEvent event) {
        Entity attacker = event.getDamager();
        if (attacker.getType() != EntityType.PLAYER) return;

        Player player = (Player) attacker;
        if (isVanished(player)) event.setCancelled(true);

        if (event.getEntity().getType() != EntityType.PLAYER) return;

        if (isMode(player)) {
            event.setCancelled(true);
            adapter.getListener(player.getInventory().getItemInHand()).onAttackPlayer(event, player, (Player) event.getEntity());
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType() != EntityType.PLAYER) return;

        if (isMode((Player) entity)) event.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (isMode(event.getPlayer())) {
            event.setCancelled(true);
            adapter.getListener(event.getPlayer().getInventory().getItemInHand()).onInteract(event);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (isMode(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (isMode(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler
    public void onPickupItem(PlayerPickupItemEvent event) {
        if (isMode(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if(!player.hasPermission(BasementMessages.STAFF_PERMISSION)) {
            for (Player staff : adapter.getVanished()) {
                player.hidePlayer(staff);
            }
            return;
        }

        if(vanishSet.contains(player.getName())) {
            localVanish(player);
            player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "STAFF! " + ChatColor.RESET + "" + ChatColor.WHITE + "Sei invisibile!");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        localDisable(player);
        localUnvanish(player);
    }
}
