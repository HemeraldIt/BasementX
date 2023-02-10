package it.hemerald.basementx.velocity.friends.commands.arguments;

import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

public class HelpArgument extends CommandArgument {

    public HelpArgument(FriendsManager friendManager) {
        super(friendManager, "", 0);
    }

    @Override
    public void execute(Player player, String[] args) {
        player.sendMessage(Component.text("§8§m------------------- §3§lFRIEND §8§m-------------------"));
        sendMessage(player, "§8§l› §b/friend add <player> §eInvita un amico.", "/friend add <player>");
        sendMessage(player, "§8§l› §b/friend accept <player> §eAccetta l'amicizia.", "/friend accept <player>");
        sendMessage(player, "§8§l› §b/friend remove <player> §eRimuovi un amico.", "/friend remove <player>");
        sendMessage(player, "§8§l› §b/friend list §eVisualizza gli amici.", "/friend list");
        sendMessage(player, "§8§l› §b/friend help §eVisualizza i comandi.", "/friend help");
        player.sendMessage(Component.text("§8§m---------------------------------------------"));
    }

    private void sendMessage(Player player, String message, String action) {
        player.sendMessage(Component.text(message)
                .clickEvent(ClickEvent.suggestCommand(action))
                .hoverEvent(HoverEvent.showText(Component.text("§b" + action))));
    }

}
