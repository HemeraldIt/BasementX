package it.hemerald.basementx.api.bukkit.nametag.filters;

import org.bukkit.entity.Player;


public abstract class NameTagFilter {

    protected final String prefix;

    protected NameTagFilter(String prefix) {
        this.prefix = prefix;
    }

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
        testThenApply(player, ignoreMe(player));
    }
}
