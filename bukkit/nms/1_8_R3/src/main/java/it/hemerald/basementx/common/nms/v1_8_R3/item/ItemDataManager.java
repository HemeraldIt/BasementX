package it.hemerald.basementx.common.nms.v1_8_R3.item;

import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class ItemDataManager implements it.hemerald.basementx.api.bukkit.item.ItemDataManager {

    @Override
    public String getData(ItemStack itemStack, String key) {
        Optional<NBTTagCompound> nbt = getNBT(itemStack);
        return nbt.map(nbtTagCompound -> nbtTagCompound.getString(key)).orElse("");
    }

    @Override
    public void setData(ItemStack itemStack, String key, String value) {
        net.minecraft.server.v1_8_R3.ItemStack itemNMS = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound compound = itemNMS.hasTag() ? itemNMS.getTag() : new NBTTagCompound();

        compound.setString(key, value);
        itemNMS.setTag(compound);
        itemStack.setItemMeta(CraftItemStack.asBukkitCopy(itemNMS).getItemMeta());
    }

    @Override
    public boolean hasData(ItemStack itemStack, String key) {
        Optional<NBTTagCompound> nbt = getNBT(itemStack);
        return nbt.map(nbtTagCompound -> nbtTagCompound.hasKey(key)).orElse(false);
    }

    private Optional<NBTTagCompound> getNBT(ItemStack itemStack) {
        if (itemStack == null) return Optional.empty();
        net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        if (nmsStack == null || nmsStack.getTag() == null) return Optional.empty();

        return Optional.of(nmsStack.getTag());
    }
}
