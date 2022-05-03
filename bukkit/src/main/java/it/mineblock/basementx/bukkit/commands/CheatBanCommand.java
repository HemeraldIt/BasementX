package it.mineblock.basementx.bukkit.commands;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.spigotmc.SpigotConfig;

import java.util.function.Consumer;

@RequiredArgsConstructor
public class CheatBanCommand implements CommandExecutor {

    private final Consumer<String> ban;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp() && !sender.hasPermission("cheat.ban") || args.length == 0) {
            sender.sendMessage(SpigotConfig.unknownCommandMessage);
            return true;
        }
        ban.accept(args[0]);
        return true;
    }
}
