package it.hemerald.basementx.velocity.friends.commands.arguments;

import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

public class AcceptArgument extends CommandArgument {

    public AcceptArgument(FriendsManager friendManager) {
        super(friendManager, "accept", 2);
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

        //todo add logic
    }

}
