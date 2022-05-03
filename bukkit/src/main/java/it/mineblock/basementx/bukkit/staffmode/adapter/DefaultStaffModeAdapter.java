package it.mineblock.basementx.bukkit.staffmode.adapter;

import com.cryptomorin.xseries.XMaterial;
import it.mineblock.basementx.api.bukkit.item.InteractiveItem;
import it.mineblock.basementx.api.bukkit.item.ItemBuilder;
import it.mineblock.basementx.api.bukkit.item.ItemClickListener;
import it.mineblock.basementx.api.bukkit.staffmode.adapter.StaffModeAdapter;
import it.mineblock.basementx.api.bukkit.staffmode.module.StaffModeModule;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class DefaultStaffModeAdapter extends StaffModeAdapter {

    private final String randomTp = "random-tp";
    private final String inventoryViewer = "inventory-viewer";
    private final String testKb = "test-kb";
    private final String vanishOn = "vanish-on";
    private final String vanishOff = "vanish-off";

    public DefaultStaffModeAdapter(StaffModeModule module) {
        super(module);
    }

    @Override
    public Map<String, Integer> getInventory(Player player) {
        Map<String, Integer> inventory = new HashMap<>();

        inventory.put(randomTp, 0);
        inventory.put(inventoryViewer, 1);
        inventory.put(testKb, 7);

        if (module.isVanished(player)) inventory.put(vanishOn, 8);
        else inventory.put(vanishOff, 8);

        return inventory;
    }

    @Override
    public void setupListeners(Map<String, InteractiveItem> listeners) {
        listeners.put(randomTp, new InteractiveItem(
                new ItemBuilder(Material.COMPASS).setName(ChatColor.YELLOW + ChatColor.BOLD.toString() + "Random TP"),
                randomTp,
                new ItemClickListener() {
                    @Override
                    public void onInteract(PlayerInteractEvent event) {
                        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
                        Player target = players.get(ThreadLocalRandom.current().nextInt(players.size())).getPlayer();
                        event.getPlayer().teleport(target);
                    }
                }
        ));

        listeners.put(inventoryViewer, new InteractiveItem(
                new ItemBuilder(Material.BOOK).setName(ChatColor.AQUA + ChatColor.BOLD.toString() + "Inventory Viewer"),
                inventoryViewer,
                new ItemClickListener() {
                    @Override
                    public void onInteractEntity(PlayerInteractEntityEvent event) {
                        Entity entity = event.getRightClicked();
                        if (entity.getType() == EntityType.PLAYER)
                            event.getPlayer().openInventory(((Player) entity).getInventory());
                    }
                }
        ));

        listeners.put(testKb, new InteractiveItem(
                new ItemBuilder(Material.BLAZE_ROD).setName(ChatColor.RED + ChatColor.BOLD.toString() + "Test KB"),
                testKb,
                new ItemClickListener() {
                    @Override
                    public void onAttackPlayer(EntityDamageByEntityEvent event, Player damager, Player entity) {
                        event.setDamage(0);
                        event.setCancelled(false);
                    }
                }
        ));

        listeners.put(vanishOn, new InteractiveItem(
                new ItemBuilder(XMaterial.GRAY_DYE.parseItem()).setDurability((short) 8).setName(ChatColor.GREEN + ChatColor.BOLD.toString() + "Vanish ON"),
                vanishOn,
                new ItemClickListener() {
                    @Override
                    public void onInteract(PlayerInteractEvent event) {
                        module.toggleVanish(event.getPlayer());
                        event.getPlayer().setItemInHand(getItem("vanish-off", event.getPlayer()));
                    }
                }
        ));

        listeners.put(vanishOff, new InteractiveItem(
                new ItemBuilder(XMaterial.RED_DYE.parseItem()).setDurability((short) 1).setName(ChatColor.RED + ChatColor.BOLD.toString() + "Vanish OFF"),
                vanishOff,
                new ItemClickListener() {
                    @Override
                    public void onInteract(PlayerInteractEvent event) {
                        module.toggleVanish(event.getPlayer());
                        event.getPlayer().setItemInHand(getItem("vanish-on", event.getPlayer()));
                    }
                }
        ));
    }
}
