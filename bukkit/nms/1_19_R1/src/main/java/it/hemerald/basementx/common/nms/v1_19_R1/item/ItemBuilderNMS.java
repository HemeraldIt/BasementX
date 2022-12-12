package it.hemerald.basementx.common.nms.v1_19_R1.item;

import it.hemerald.basementx.api.bukkit.item.ItemBuilder;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

public class ItemBuilderNMS implements ItemBuilder.NMS {

    @Override
    public void setUnbreakable(ItemMeta meta, boolean state) {
        meta.setUnbreakable(state);
    }

    @Override
    public void setBasePotionData(PotionMeta meta, PotionType type, boolean extended, boolean upgraded) {
        meta.setBasePotionData(new PotionData(type, extended, upgraded));
    }
}
