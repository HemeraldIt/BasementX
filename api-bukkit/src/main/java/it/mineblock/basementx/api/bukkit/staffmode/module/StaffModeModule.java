package it.mineblock.basementx.api.bukkit.staffmode.module;

import ch.jalu.configme.properties.Property;
import it.mineblock.basementx.api.bukkit.BasementBukkit;
import it.mineblock.basementx.api.bukkit.module.Module;
import it.mineblock.basementx.api.bukkit.staffmode.adapter.StaffModeAdapter;
import org.bukkit.entity.Player;

public abstract class StaffModeModule extends Module<StaffModeAdapter> {

    public StaffModeModule(BasementBukkit basement, Property<Boolean> property) {
        super(basement, property);
    }

    public abstract void enableMode(Player player);

    public abstract void disableMode(Player player);

    public abstract void toggleMode(Player player);

    public abstract boolean isMode(Player player);

    public abstract void vanish(Player player);

    public abstract boolean localVanish(Player player);

    public abstract void unvanish(Player player);

    public abstract boolean localUnvanish(Player player);

    public abstract void toggleVanish(Player player);

    public abstract boolean isVanished(Player player);
}
