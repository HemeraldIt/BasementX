package it.hemerald.basementx.velocity.friends.commands.arguments;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import it.hemerald.basementx.api.friends.Friend;
import it.hemerald.basementx.velocity.friends.commands.CommandArgument;
import it.hemerald.basementx.velocity.friends.manager.FriendsManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RemoveArgument extends CommandArgument {

    public RemoveArgument(FriendsManager friendManager) {
        super(friendManager, "remove", 2);
    }

    @Override
    public void execute(Player player, String[] args) {
        Optional<Friend> optionalFriend = friendService.getFriend(player);
        if (optionalFriend.isEmpty()) {
            return;
        }
        String username = args[1].toLowerCase();
        if (optionalFriend.get().getFriends().stream().map(String::toLowerCase).noneMatch(s -> s.equals(username))) {
            friendService.sendMessage(player, "Questo giocatore non è nei tuoi amici.");
            return;
        }

        Optional<Player> optionalPlayer = friendService.getTogether().getServer().getPlayer(username);
        optionalPlayer.ifPresent(value -> friendService.sendMessage(value, player.getUsername() + " ha rimosso la tua amicizia."));

        friendService.removeFriend(username, player.getUsername());
        friendService.removeFriend(player, username);
        friendService.sendMessage(player, "Hai rimosso " + username + " dai tuoi amici.");
    }

    @Override
    public List<String> suggest(CommandSource source, String[] currentArgs) {
        if (!(source instanceof Player player)) return super.suggest(source, currentArgs);
        Friend friend = friendService.getFriend(player).orElse(null);
        if (friend == null) return super.suggest(source, currentArgs);
        return new ArrayList<>(friend.getFriends());
    }
}
