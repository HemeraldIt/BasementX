package it.hemerald.basementx.bukkit.commands;

import it.hemerald.basementx.api.bukkit.BasementBukkit;
import it.hemerald.basementx.bukkit.generic.chat.BasementMessages;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.spigotmc.SpigotConfig;

@RequiredArgsConstructor
public class ReloadTabCommand implements CommandExecutor {

    private final BasementBukkit basement;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.hasPermission(BasementMessages.RELOAD_TAB)) {
            sender.sendMessage(SpigotConfig.unknownCommandMessage);
            return true;
        }

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            basement.getNameTagModule().update(onlinePlayer);
        }

        sender.sendMessage(ChatColor.GREEN + "Tab ricaricata per tutti gli online players!");
        return true;
    }
}
