package it.mineblock.basementx.nms.v1_18_R2.item;

import it.mineblock.basementx.api.bukkit.item.ItemBuilder;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemBuilderNMS implements ItemBuilder.NMS {

    @Override
    public void setUnbreakable(ItemMeta meta, boolean state) {
        meta.setUnbreakable(state);
    }
}
