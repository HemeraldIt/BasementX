package it.mineblock.basementx.api.bukkit.item;

import org.bukkit.inventory.ItemStack;

public interface ItemDataManager {

    /**
     * Gets data value of an {@link ItemStack}
     * @param itemStack the item
     * @param key the key identifier
     * @return the data value of an item. Empty string if no one is set
     */
    String getData(ItemStack itemStack, String key);

    /**
     * Sets data value of an {@link ItemStack}
     * @param itemStack the item
     * @param key the key identifier
     * @param value the new value
     */
    void setData(ItemStack itemStack, String key, String value);

    /**
     * Check if an {@link ItemStack} has a data value
     * @param itemStack the item
     * @param key the key identifier
     * @return true if it has a data value, else otherwise
     */
    boolean hasData(ItemStack itemStack, String key);
}
