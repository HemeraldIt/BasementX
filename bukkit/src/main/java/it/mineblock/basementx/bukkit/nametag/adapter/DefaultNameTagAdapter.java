package it.mineblock.basementx.bukkit.nametag.adapter;

import it.mineblock.basementx.api.bukkit.nametag.adapter.NameTagAdapter;
import it.mineblock.basementx.api.bukkit.nametag.module.NameTagModule;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Team;

import java.util.concurrent.CompletableFuture;

public class DefaultNameTagAdapter extends NameTagAdapter {

    public DefaultNameTagAdapter(NameTagModule module) {
        super(module);
    }

    @Override
    public CompletableFuture<String> getSuffix(Player player) {
        return CompletableFuture.completedFuture("");
    }

    @Override
    public CompletableFuture<String> getPrefix(Player player) {
        if (module.getBasement().getDisguiseModule().isDisguised(player)) {
            return CompletableFuture.completedFuture(ChatColor.GRAY.toString());
        } else {
            return super.getPrefix(player);
        }
    }

    @Override
    public CompletableFuture<String> getDisplayName(Player player) {
        return this.getPrefix(player).thenApply((prefix) -> {
            if (module.getBasement().getDisguiseModule().isDisguised(player)) {
                return ChatColor.GRAY + player.getSafeFakeName();
            } else {
                return prefix + player.getName();
            }
        });
    }

    public void hideNameTag(Player player) {
        Team team = player.getScoreboard().getTeam(module.getTeamName(player));
        if(team != null) {
            team.setNameTagVisibility(NameTagVisibility.NEVER);
        }
    }

    @Override
    public void onCreateTeam(Player player, Team team) {
        if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
            hideNameTag(player);
        }
    }

    @Override
    public int getPriority(Player player) {
        return module.getBasement().getDisguiseModule().isDisguised(player) ? 1000 : super.getPriority(player);
    }

    @Override
    public String getPlayerName(Player player) {
        return player.getSafeFakeName();
    }
}
