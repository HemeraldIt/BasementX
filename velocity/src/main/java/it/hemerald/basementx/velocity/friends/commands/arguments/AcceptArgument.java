package it.hemerald.basementx.velocity.friends.commands.arguments;

import com.velocitypowered.api.proxy.Player;
import it.hemerald.basementx.api.friends.Friend;
import it.hemerald.basementx.velocity.friends.commands.CommandArgument;
import it.hemerald.basementx.velocity.friends.manager.FriendsManager;

import java.util.Optional;

public class AcceptArgument extends CommandArgument {

    public AcceptArgument(FriendsManager friendManager) {
        super(friendManager, "accept", 2);
    }

    @Override
    public void execute(Player player, String[] args) {
        Optional<Friend> optionalFriend = friendService.getFriend(player);
        if (optionalFriend.isEmpty()) {
            return;
        }
        Optional<Player> invitedPlayer = friendService.getTogether().getServer().getPlayer(args[1]);
        if (invitedPlayer.isEmpty()) {
            friendService.sendMessage(player, "Giocatore non trovato.");
            return;
        }

        if (!AddArgument.isInvited(player.getUsername(), invitedPlayer.get().getUsername())) {
            friendService.sendMessage(player, "Non hai ricevuto nessuna richiesta di amicizia da questo giocatore.");
            return;
        }

        friendService.addFriend(player, invitedPlayer.get().getUsername());
        friendService.addFriend(invitedPlayer.get(), player.getUsername());
        friendService.sendMessage(player, "Hai accettato la richiesta di amicizia di " + invitedPlayer.get().getUsername() + ".");
        friendService.sendMessage(invitedPlayer.get(), player.getUsername() + " ha accettato la tua richiesta di amicizia.");
    }

}
