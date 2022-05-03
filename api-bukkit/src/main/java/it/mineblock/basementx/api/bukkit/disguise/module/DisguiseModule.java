package it.mineblock.basementx.api.bukkit.disguise.module;

import ch.jalu.configme.properties.Property;
import it.mineblock.basementx.api.bukkit.BasementBukkit;
import it.mineblock.basementx.api.bukkit.disguise.adapter.DisguiseAdapter;
import it.mineblock.basementx.api.bukkit.module.Module;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class DisguiseModule extends Module<DisguiseAdapter> {

    protected final List<String> names = new ArrayList<>();
    private final Random random = new Random();

    public DisguiseModule(BasementBukkit basement, Property<Boolean> property) {
        super(basement, property);
    }

    public String getRandomUsername() {
        return this.names.get(this.random.nextInt(this.names.size()));
    }

    public abstract void disguise(Player player);

    public abstract void undisguise(Player player);

    public abstract boolean isDisguised(Player player);

    public abstract void softHide(Player player, Player target);

    public abstract String getDisguisedName(Player player);
}
