package it.hemerald.basementx.velocity.friends.commands.arguments;

import com.velocitypowered.api.proxy.Player;
import it.hemerald.basementx.api.friends.Friend;
import it.hemerald.basementx.api.friends.Pair;
import it.hemerald.basementx.velocity.friends.commands.CommandArgument;
import it.hemerald.basementx.velocity.friends.manager.FriendsManager;
import net.kyori.adventure.text.Component;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class ListArgument extends CommandArgument {

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("hh:mm:ss dd/M/yyyy");

    public ListArgument(FriendsManager friendManager) {
        super(friendManager, "list", 0);
    }

    private static String format(long epochSeconds) {
        return FORMAT.format(new Date(Instant.ofEpochSecond(epochSeconds).toEpochMilli()));
    }

    @Override
    public void execute(Player player, String[] args) {
        Friend friend = friendService.getFriend(player).orElse(null);
        if (friend == null) return;
        if (!friend.getFriends().isEmpty()) {
            player.sendMessage(Component.text("§8§m-------------------§3§lFRIEND §8§m-------------------"));
            for (Pair<String, Long> pair : friend.getFriends()) {
                player.sendMessage(Component.text(status(pair.getKey()) + " §b" + pair.getKey() + " §7- §e" + format(pair.getValue())));
            }
            player.sendMessage(Component.text("§8§m---------------------------------------------"));
        } else {
            friendService.sendMessage(player, "§cNon hai amici");
        }

    }

    private String status(String username) {
        return friendService.getTogether().getServer().getPlayer(username).isPresent() ? "§a§l●" : "§c§l●";
    }

}
