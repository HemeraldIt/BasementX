package it.hemerlad.basementx.api.bukkit.item;

import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public interface ItemClickListener {

    ItemClickListener EMPTY = new ItemClickListener() {
    };

    default void onBlockPlace(BlockPlaceEvent event) {
    }

    default void onBlockBreak(BlockBreakEvent event) {
    }

    default void onInteractEntity(PlayerInteractEntityEvent event) {
    }

    default void onInteract(PlayerInteractEvent event) {
    }

    default void onAttackPlayer(EntityDamageByEntityEvent event, Player damager, Player entity) {
    }
}
