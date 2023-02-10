package it.hemerald.basementx.velocity.friends.commands.arguments;

import com.velocitypowered.api.proxy.Player;
import it.hemerald.basementx.api.friends.Friend;
import net.kyori.adventure.text.Component;
import it.hemerald.basementx.velocity.friends.commands.CommandArgument;
import it.hemerald.basementx.velocity.friends.manager.FriendsManager;

public class ListArgument extends CommandArgument {

    public ListArgument(FriendsManager friendManager) {
        super(friendManager, "list", 0);
    }

    @Override
    public void execute(Player player, String[] args) {
        Friend friend = friendService.getFriend(player).orElse(null);
        if (friend == null) {
            friendService.sendMessage(player, "§cNon hai amici");
            return;
        }
        if (friend.getFriends().size() > 1) {
            player.sendMessage(Component.text("§8§m------------------- §3§lFRIEND §8§m-------------------"));
            for (String username : friend.getFriends().stream().sorted().toList()) {
                player.sendMessage(Component.text("§8§l› §b" + username));
            }
            player.sendMessage(Component.text("§8§m---------------------------------------------"));
        } else {
            friendService.sendMessage(player, "§cNon hai amici");
        }
    }

}
