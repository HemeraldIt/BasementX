package it.hemerald.basementx.api.bukkit.staffmode.adapter;

import it.hemerald.basementx.api.bukkit.adapter.Adapter;
import it.hemerald.basementx.api.bukkit.item.InteractiveItem;
import it.hemerald.basementx.api.bukkit.item.ItemClickListener;
import it.hemerald.basementx.api.bukkit.staffmode.module.StaffModeModule;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@Getter
public abstract class StaffModeAdapter extends Adapter<StaffModeModule> {

    @Getter(AccessLevel.NONE)
    private final Map<String, InteractiveItem> listeners = new HashMap<>();
    private final Set<Player> vanished = Collections.newSetFromMap(new WeakHashMap<>());
    private final Map<Player, ItemStack[]> inventories = new HashMap<>();
    private final Set<Player> wasFlying = Collections.newSetFromMap(new WeakHashMap<>());

    public StaffModeAdapter(StaffModeModule module) {
        super(module);

        setupListeners(listeners);
    }

    public abstract Map<String, Integer> getInventory(Player player);

    public abstract void setupListeners(Map<String, InteractiveItem> listeners);

    public ItemClickListener getListener(ItemStack itemStack) {
        InteractiveItem item = listeners.get(InteractiveItem.getKey(itemStack, module.getBasement().getItemDataManager()));
        return item == null ? ItemClickListener.EMPTY : item.getListener();
    }

    public void setupInventory(Player player) {
        if (!module.isMode(player)) return;

        for (Map.Entry<String, Integer> entry : getInventory(player).entrySet()) {
            player.getInventory().setItem(
                    entry.getValue(),
                    listeners.get(entry.getKey()).getItemStack(module.getBasement().getItemDataManager(), player));
        }

        player.updateInventory();
    }

    public ItemStack getItem(String key, Player player) {
        return listeners.get(key).getItemStack(module.getBasement().getItemDataManager(), player);
    }

    public boolean onEnterMode(Player player) {
        return true;
    }

    public boolean onExitMode(Player player) {
        return true;
    }

    public boolean onVanish(Player player, List<Player> targets) {
        return true;
    }

    public boolean onUnvanish(Player player, List<Player> targets) {
        return true;
    }
}
