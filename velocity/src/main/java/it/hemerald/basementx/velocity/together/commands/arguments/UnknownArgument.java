package it.hemerald.basementx.velocity.together.commands.arguments;

import com.velocitypowered.api.proxy.Player;
import it.hemerald.basementx.velocity.together.commands.CommandArgument;
import it.hemerald.basementx.velocity.together.manager.PartyManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

public class UnknownArgument extends CommandArgument {

    public UnknownArgument(PartyManager partyManager) {
        super(partyManager, "", 0);
    }

    @Override
    public void execute(Player player, String[] args) {
        player.sendMessage(Component.text("§8§m------------------- §a§lPARTY §8§m-------------------"));
        sendMessage(player, "§8§l› §b/party invite <player> §eInvita un player.", "/party invite <player>");
        sendMessage(player, "§8§l› §b/party join <player> §eEntra nel party di un utente.", "/party join <player>");
        sendMessage(player, "§8§l› §b/party leave §eEsci dal party.", "/party leave");
        sendMessage(player, "§8§l› §b/party kick <player> §eKicka un player dal party.", "/party kick <player>");
        sendMessage(player, "§8§l› §b/party chat §eAttiva o disattiva la chat del party.", "/party chat");
        sendMessage(player, "§8§l› §b/party list §eVisualizza i player nel party.", "/party list");
        sendMessage(player, "§8§l› §b/party leader <player> §eCambia il leader.", "/party leader <player>");
        if (player.hasPermission("party.open")) {
            sendMessage(player, "§8§l› §b/party open §eRendi il party pubblico.", "/party open");
        }
        player.sendMessage(Component.text("§8§m---------------------------------------------"));
    }

    private void sendMessage(Player player, String message, String action) {
        player.sendMessage(Component.text(message)
                .clickEvent(ClickEvent.suggestCommand(action))
                .hoverEvent(HoverEvent.showText(Component.text("§b" + action))));
    }

}
