package it.hemerald.basementx.api.bukkit.nametag.module;

import ch.jalu.configme.properties.Property;
import it.hemerald.basementx.api.bukkit.BasementBukkit;
import it.hemerald.basementx.api.bukkit.module.Module;
import it.hemerald.basementx.api.bukkit.nametag.adapter.NameTagAdapter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public abstract class NameTagModule extends Module<NameTagAdapter> {

    protected Property<Boolean> tags;

    public NameTagModule(BasementBukkit basement, Property<Boolean> property, Property<Boolean> tags) {
        super(basement, property);

        this.tags = tags;
    }

    public boolean tagsEnabled() {
        return basement.getSettingsManager().getProperty(getProperty()) && basement.getSettingsManager().getProperty(tags);
    }

    public abstract void openInventory(Player player);

    public abstract void setTag(Player player, String tag);

    public abstract CompletableFuture<String> getTag(Player player);

    public abstract CompletableFuture<String> loadTag(Player player);

    public abstract Scoreboard getScoreboard(Player player);

    public abstract void removePlayer(Player player);

    public abstract void update(Player player);

    public abstract void updateDisplayName(Player player);

    public abstract void updateTab(Player player);

    public abstract Collection<Team> getTeams();

    public abstract Team getTeam(Player player);

    public abstract String getTeamName(Player player);

    public abstract String resize(String string);

    public abstract void updateHealth(Player player);

    public abstract void updateHealth(Player player, boolean tab);

    public abstract void updateHealth(Player player, double health);

    public abstract void updateHealth(Player player, double health, boolean tab);

    public abstract void updateHealthTab(Player player, double health);

    public interface TeamUtils {
        void setColor(Team team, ChatColor color);
    }
}
