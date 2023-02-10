package it.hemerald.basementx.velocity.friends.commands.arguments;

import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

public class RemoveArgument extends CommandArgument {

    public RemoveArgument(FriendsManager friendManager) {
        super(friendManager, "remove", 2);
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

        if (!optionalFriend.get().getFriends().contains(invitedPlayer.get().getUsername())) {
            friendService.sendMessage(player, "Questo giocatore non è nei tuoi amici.");
            return;
        }

        //todo remove
        
    }

}
