package it.hemerald.basementx.velocity.friends.commands.arguments;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.velocitypowered.api.proxy.Player;
import it.hemerald.basementx.api.friends.Friend;
import it.hemerald.basementx.velocity.friends.commands.CommandArgument;
import it.hemerald.basementx.velocity.friends.manager.FriendsManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class AddArgument extends CommandArgument {

    private static final Cache<String, String> invitations = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build();

    public AddArgument(FriendsManager friendManager) {
        super(friendManager, "add", 2);
    }

    public static boolean isInvited(String invited, String inviter) {
        return invitations.getIfPresent(invited) != null && Objects.equals(invitations.getIfPresent(invited), inviter);
    }

    @Override
    public void execute(Player player, String[] args) {
        Friend friend = friendService.getFriend(player).orElse(null);
        if (friend == null) return;
        Optional<Player> invitedPlayer = friendService.getTogether().getServer().getPlayer(args[1]);
        if (invitedPlayer.isEmpty()) {
            friendService.sendMessage(player, "Giocatore non trovato.");
            return;
        }

        if (invitedPlayer.get() == player) {
            friendService.sendMessage(player, "Non puoi invitare una richiesta di amicizia a te stesso.");
            return;
        }

        if (friend.containsFriend(invitedPlayer.get().getUsername())) {
            friendService.sendMessage(player, "Questo giocatore è già tuo amico.");
            return;
        }

        if (isInvited(invitedPlayer.get().getUsername(), player.getUsername())) {
            friendService.sendMessage(player, "Hai già inviato una richiesta di amicizia a questo giocatore.");
            return;
        }

        if (friend.limit()) {
            friendService.sendMessage(player, "§cHai raggiunto il limite massimo di amici.");
            return;
        }

        invitations.put(invitedPlayer.get().getUsername(), player.getUsername());
        friendService.sendMessage(player, "Hai inviato a §b§l" + invitedPlayer.get().getUsername() + "§7 una richiesta di amicizia.");
        friendService.sendMessage(invitedPlayer.get(), Component.text("§7Hai ricevuto una richiesta di amicizia da §b§l" + player.getUsername() + "§7. Clicca Qui per accettare.")
                .clickEvent(ClickEvent.runCommand("/friends accept " + player.getUsername()))
                .hoverEvent(Component.text("§aClicca per accettare la richiesta.")));
    }

}
