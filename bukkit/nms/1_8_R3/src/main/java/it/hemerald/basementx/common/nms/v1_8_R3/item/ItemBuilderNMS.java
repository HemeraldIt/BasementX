package it.hemerald.basementx.common.nms.v1_8_R3.item;

import it.hemerald.basementx.api.bukkit.item.ItemBuilder;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemBuilderNMS implements ItemBuilder.NMS {

    @Override
    public void setUnbreakable(ItemMeta meta, boolean state) {
        meta.spigot().setUnbreakable(state);
    }
}
