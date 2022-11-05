package it.hemerald.basementx.bukkit.commands;

import it.hemerald.basementx.api.bukkit.BasementBukkit;
import it.hemerald.basementx.api.bukkit.player.stream.StreamMode;
import it.hemerald.basementx.api.player.BasementPlayer;
import it.hemerald.basementx.bukkit.generic.chat.BasementMessages;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.spigotmc.SpigotConfig;

import java.util.ArrayList;

@RequiredArgsConstructor
public class StreamModeToggle implements CommandExecutor {

    private final BasementBukkit basement;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player player)) {
            sender.sendMessage(BasementMessages.CONSOLE_DENIED);
            return true;
        }

        if (!player.hasPermission("hemerald.streammode")) {
            player.sendMessage(SpigotConfig.unknownCommandMessage);
            return true;
        }

        StreamMode streamMode = basement.getStreamMode();
        BasementPlayer basementPlayer = basement.getPlayerManager().getBasementPlayer(player.getName());

        boolean inStreamMode = !basementPlayer.isInStreamMode();
        basementPlayer.streamMode(inStreamMode);

        if(streamMode.isEnabled()) {
            if(inStreamMode) basement.getPlayerManager().disguise(player.getName());
            else basement.getPlayerManager().undisguise(player.getName());
            streamMode.sendPackets(new ArrayList<>(Bukkit.getOnlinePlayers()), player, inStreamMode);
            player.sendMessage("Hai " + (inStreamMode ? "abilitato " : "disabilitato ") + "la streammode");
        } else {
            player.sendMessage("Hai " + (inStreamMode ? "abilitato " : "disabilitato ") +
                    "la streammode, ma in questo server è disabilitata, quindi nessun cambiamento visivo verrà applicato su questo server");
        }

        return false;
    }
}
