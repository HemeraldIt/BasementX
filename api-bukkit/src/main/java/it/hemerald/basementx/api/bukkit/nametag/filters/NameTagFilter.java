package it.hemerald.basementx.api.bukkit.nametag.filters;

import it.hemerald.basementx.api.bukkit.BasementBukkit;
import org.bukkit.entity.Player;


public abstract class NameTagFilter {

    protected final BasementBukkit basement;
    protected final String prefix;

    protected NameTagFilter(BasementBukkit basement, String prefix) {
        this.basement = basement;
        this.prefix = prefix;
    }

    public abstract boolean isEnabled();

    public abstract boolean test(Player player);

    public abstract boolean ignoreMe(Player player);

    public abstract void apply(Player player, boolean ignoreMe);

    public void apply(Player player) {
        apply(player, ignoreMe(player));
    }

    public void testThenApply(Player player, boolean ignoreMe) {
        if(test(player))
            apply(player, ignoreMe);
    }

    public void testThenApply(Player player) {
        if(!isEnabled()) return;
        testThenApply(player, ignoreMe(player));
    }
}
