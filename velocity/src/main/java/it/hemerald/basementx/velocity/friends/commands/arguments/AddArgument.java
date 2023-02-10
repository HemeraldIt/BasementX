package it.hemerald.basementx.velocity.friends.commands.arguments;

import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

public class AddArgument extends CommandArgument {

    public AddArgument(FriendsManager friendManager) {
        super(friendManager, "add", 2);
    }

    @Override
    public void execute(Player player, String[] args) {
        Optional<Friend> optionalFriend = friendManager.getFriend(player);
        if (optionalFriend.isEmpty()) {
            return;
        }
        Optional<Player> invitedPlayer = friendService.getTogether().getServer().getPlayer(args[1]);
        if (invitedPlayer.isEmpty()) {
            friendService.sendMessage(player, "Giocatore non trovato.");
            return;
        }

        if (invitedPlayer.get() == player) {
            friendService.sendMessage(player, "Non puoi invitare una richiesta di amicizia a te stesso.");
            return;
        }

        if (optionalFriend.get().getFriends().contains(invitedPlayer.get().getUsername())) {
            friendService.sendMessage(player, "Questo giocatore è già nei tuoi amici.");
            return;
        }

        //todo cache
    }

}
