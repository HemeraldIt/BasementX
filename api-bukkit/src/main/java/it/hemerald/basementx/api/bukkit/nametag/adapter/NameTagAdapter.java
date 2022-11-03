package it.hemerald.basementx.api.bukkit.nametag.adapter;

import it.hemerald.basementx.api.bukkit.adapter.Adapter;
import it.hemerald.basementx.api.bukkit.chat.Colorizer;
import it.hemerald.basementx.api.bukkit.nametag.module.NameTagModule;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Team;

import java.util.concurrent.CompletableFuture;

public abstract class NameTagAdapter extends Adapter<NameTagModule> {

    public NameTagAdapter(NameTagModule module) {
        super(module);
    }

    public CompletableFuture<String> getPrefix(Player player) {
        return module.getBasement().getPermissionManager().getPrefix(player.getUniqueId()).thenApply(Colorizer::colorize);
    }

    public CompletableFuture<String> getPrefixUncolored(Player player) {
        return module.getBasement().getPermissionManager().getPrefix(player.getUniqueId());
    }

    public CompletableFuture<String> getSuffix(Player player) {
        return CompletableFuture.completedFuture("");
    }

    public CompletableFuture<String> getDisplayName(Player player) {
        return getPrefix(player).thenApply(prefix -> prefix + getPlayerName(player));
    }

    public CompletableFuture<String> getTab(Player player) {
        return module.getTag(player).thenCompose(tag -> getPrefix(player).thenCompose(prefix -> getSuffix(player).thenApply(suffix -> tag + prefix + getPlayerName(player) + suffix)));
    }

    public String getTeamName(Player player) {
        return module.getTeamName(player);
    }

    public int getPriority(Player player) {
        return module.getBasement().getPermissionManager().getPriority(player);
    }

    public String getPlayerName(Player player) {
        return player.getSafeFakeName();
    }

    public void onCreateTeam(Player player, Team team) {
    }

    public void onUpdateTeam(Player player, Team team) {
    }

    public void onPreJoin(PlayerJoinEvent event) {
    }

    public void onPostJoin(PlayerJoinEvent event) {
    }
}
