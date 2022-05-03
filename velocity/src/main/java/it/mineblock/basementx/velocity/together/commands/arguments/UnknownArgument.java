package it.mineblock.basementx.velocity.together.commands.arguments;

import com.velocitypowered.api.proxy.Player;
import it.mineblock.basementx.velocity.together.commands.CommandArgument;
import it.mineblock.basementx.velocity.together.manager.PartyManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class UnknownArgument extends CommandArgument {

    public UnknownArgument(PartyManager partyManager) {
        super(partyManager, "", 0);
    }

    @Override
    public void execute(Player player, String[] args) {
        player.sendMessage(Component.text("/party info").color(NamedTextColor.DARK_AQUA).append(Component.text(" Visualizza lista dei membri").color(NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/party invite <player>").color(NamedTextColor.DARK_AQUA).append(Component.text(" Invita un utente").color(NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/party join <player> ").color(NamedTextColor.DARK_AQUA).append(Component.text(" Entra nel party di un utente").color(NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/party kick <player> ").color(NamedTextColor.DARK_AQUA).append(Component.text(" Kicka un utente").color(NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/party leader <player>").color(NamedTextColor.DARK_AQUA).append(Component.text(" Cambia leader").color(NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/party chat").color(NamedTextColor.DARK_AQUA).append(Component.text(" Cambia chat").color(NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/party leave").color(NamedTextColor.DARK_AQUA).append(Component.text(" Esci dal party").color(NamedTextColor.GRAY)));
        if (player.hasPermission("party.open")) {
            player.sendMessage(Component.text("/party open").color(NamedTextColor.DARK_AQUA).append(Component.text(" Rendi il party pubblico").color(NamedTextColor.GRAY)));
        }
    }
}
