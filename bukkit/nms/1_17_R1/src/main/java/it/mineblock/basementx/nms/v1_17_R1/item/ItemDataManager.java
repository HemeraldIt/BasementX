package it.mineblock.basementx.nms.v1_17_R1.item;

import lombok.RequiredArgsConstructor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

@RequiredArgsConstructor
public class ItemDataManager implements it.mineblock.basementx.api.bukkit.item.ItemDataManager {

    private final Plugin plugin;

    @Override
    public String getData(ItemStack itemStack, String key) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return "";

        NamespacedKey namespacedKey = new NamespacedKey(plugin, key);
        if (!meta.getPersistentDataContainer().has(namespacedKey, PersistentDataType.STRING)) return "";

        return meta.getPersistentDataContainer().get(namespacedKey, PersistentDataType.STRING);
    }

    @Override
    public void setData(ItemStack itemStack, String key, String value) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return;

        NamespacedKey namespacedKey = new NamespacedKey(plugin, key);
        meta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, value);
        itemStack.setItemMeta(meta);
    }

    @Override
    public boolean hasData(ItemStack itemStack, String key) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return false;

        NamespacedKey namespacedKey = new NamespacedKey(plugin, key);
        return meta.getPersistentDataContainer().has(namespacedKey, PersistentDataType.STRING);
    }
}
