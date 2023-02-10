package it.hemerald.basementx.velocity.friends.commands.arguments;

import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

public class ListArgument extends CommandArgument {

    public ListArgument(FriendsManager friendManager) {
        super(friendManager, "", 0);
    }

    @Override
    public void execute(Player player, String[] args) {
        Friend friend = friendManager.getFriend(player).orElse(null);
        if (friend.getFriends().size() > 1) {
            player.sendMessage(Component.text("§8§m------------------- §3§lFRIEND §8§m-------------------"));
            for (String username : friend.getFriends().stream().sorted().toList()) {
                player.sendMessage(Component.text("§8§l› §b" + username));
            }
            player.sendMessage(Component.text("§8§m---------------------------------------------"));
        } else {
            friendManager.sendMessage(player, "§cNon hai amici");
        }
    }

}
