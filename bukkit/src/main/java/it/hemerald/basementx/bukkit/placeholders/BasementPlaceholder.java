package it.hemerald.basementx.bukkit.placeholders;

import it.hemerald.basementx.api.Basement;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class BasementPlaceholder extends PlaceholderExpansion {

    private final Basement basement;
    private final int counterLength;

    public BasementPlaceholder(Basement basement) {
        this.basement = basement;
        counterLength = "counter_".length();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "basement";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Hemerald";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        //Usage: %basement_counter_SERVERSTART
        if (params.startsWith("counter_")) {
            return Integer.toString(basement.getServerManager().getOnlinePlayers(params.substring(counterLength)));
        }
        return null;
    }
}
