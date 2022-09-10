package it.hemerald.basementx.api.bukkit.custom;

import org.bukkit.entity.Player;

public class AddonExecuter {

    /**
     * This method replace Player#softHidePlayer of custom paper build
     * @param hider The name of the player that another player will be hidden from
     * @param hided The name of the player that will be hidden
     */
    public static void softHidePlayer(Player hider, Player hided) {
        hider.softHidePlayer(hided);
    }
}
