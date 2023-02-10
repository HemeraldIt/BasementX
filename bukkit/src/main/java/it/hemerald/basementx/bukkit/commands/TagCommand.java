package it.hemerald.basementx.bukkit.commands;

import it.hemerald.basementx.api.bukkit.BasementBukkit;
import it.hemerald.basementx.bukkit.generic.chat.BasementMessages;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class TagCommand implements CommandExecutor {

    private final BasementBukkit basement;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(BasementMessages.CONSOLE_DENIED);
            return true;
        }

        if (!basement.getNameTagModule().isEnabled() || !basement.getNameTagModule().tagsEnabled()) {
            basement.getPlayerManager().getBasementPlayer(player.getName()).getLocale("basement")
                    .ifPresentOrElse(locale -> player.sendMessage(locale.getText("command-not-available")),
                            () -> player.sendMessage(BasementMessages.COMMAND_NOT_AVAILABLE));
            return true;
        }

        basement.getNameTagModule().openInventory(player);
        return true;
    }
}
