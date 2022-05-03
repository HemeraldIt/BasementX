package it.mineblock.basementx.bukkit.commands;

import it.mineblock.basementx.api.bukkit.BasementBukkit;
import it.mineblock.basementx.api.locale.Locale;
import it.mineblock.basementx.bukkit.generic.chat.BasementMessages;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.spigotmc.SpigotConfig;

import java.util.Optional;

@RequiredArgsConstructor
public class StaffModeCommand implements CommandExecutor {

    private final BasementBukkit basement;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(BasementMessages.CONSOLE_DENIED);
            return true;
        }

        Optional<Locale> optionalLocale = basement.getPlayerManager().getBasementPlayer(player.getName()).getLocale("basement");
        if (!sender.hasPermission(BasementMessages.STAFF_PERMISSION)) {
            optionalLocale.ifPresentOrElse(locale -> player.sendMessage(locale.getText("unknown-command-message")),
                    () -> player.sendMessage(SpigotConfig.unknownCommandMessage));
            return true;
        }

        if (!basement.getStaffModeModule().isEnabled()) {
            optionalLocale.ifPresentOrElse(locale -> player.sendMessage(locale.getText("command-not-available")),
                    () -> player.sendMessage(BasementMessages.COMMAND_NOT_AVAILABLE));
            return true;
        }

        basement.getStaffModeModule().toggleMode(player);
        return true;
    }
}
