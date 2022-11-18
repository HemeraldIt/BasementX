package it.hemerald.basementx.bukkit.commands;

import it.hemerald.basementx.api.Basement;
import it.hemerald.basementx.api.BasementProvider;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BedwarsCommand implements CommandExecutor {

    private final Basement basement;

    public BedwarsCommand(Basement basement) {
        this.basement = basement;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) return false;
        Basement basement = BasementProvider.get();
        if (basement.getRemoteVelocityService().isOnRanch(player.getUniqueId(), "bedwars_lobby")) {
            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERRORE! Sei già connesso nelle bedwars");
        }
        basement.getPlayerManager().sendToGameLobby(player.getUniqueId(), "bedwars_lobby");
        return false;
    }
}
