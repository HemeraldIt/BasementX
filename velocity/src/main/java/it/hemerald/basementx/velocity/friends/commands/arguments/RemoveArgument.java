package it.hemerald.basementx.velocity.friends.commands.arguments;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import it.hemerald.basementx.api.friends.Friend;
import it.hemerald.basementx.api.friends.Pair;
import it.hemerald.basementx.velocity.friends.commands.CommandArgument;
import it.hemerald.basementx.velocity.friends.manager.FriendsManager;

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
        if (!optionalFriend.get().containsFriend(args[1])) {
            friendService.sendMessage(player, "§cQuesto giocatore non è nei tuoi amici.");
            return;
        }

        Optional<Player> optionalPlayer = friendService.getTogether().getServer().getPlayer(args[1]);
        optionalPlayer.ifPresent(value -> friendService.sendMessage(value, "§b§l" + player.getUsername() + " §cti ha rimosso dagli amici."));

        friendService.removeFriend(args[1], player.getUsername());
        friendService.removeFriend(player, args[1]);
        friendService.sendMessage(player, "Hai rimosso §b§l" + args[1] + "§7 dai tuoi amici.");
    }

    @Override
    public List<String> suggest(CommandSource source, String[] currentArgs) {
        if (!(source instanceof Player player)) return super.suggest(source, currentArgs);
        Friend friend = friendService.getFriend(player).orElse(null);
        if (friend == null) return super.suggest(source, currentArgs);
        return friend.getFriends().stream().map(Pair::getKey).toList();
    }
}
