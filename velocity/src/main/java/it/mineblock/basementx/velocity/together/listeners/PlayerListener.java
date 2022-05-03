package it.mineblock.basementx.velocity.together.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import it.mineblock.basementx.api.party.Party;
import it.mineblock.basementx.velocity.together.Together;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Optional;

@RequiredArgsConstructor
public class PlayerListener {

    private final Together together;

    @Subscribe
    public void onPlayerChat(PlayerChatEvent event) {
        if(together.getPartyManager().isChat(event.getPlayer())) {
            event.setResult(PlayerChatEvent.ChatResult.denied());
            together.getPartyManager().getParty(event.getPlayer()).ifPresent(party -> together.getPartyManager().broadcastMessage(party, Component.text("[PARTY] ").color(NamedTextColor.AQUA)
                    .append(Component.text(event.getPlayer().getUsername() + ": ").color(NamedTextColor.GRAY))
                    .append(Component.text(event.getMessage()).color(NamedTextColor.WHITE))));
        }
    }

    @Subscribe
    public void serverSwitch(ServerPreConnectEvent event) {
        Optional<Party> optional = together.getPartyManager().getParty(event.getPlayer());

        if (optional.isEmpty()) {
            return;
        }

        Optional<RegisteredServer> optionalServer = event.getResult().getServer();

        if (optionalServer.isEmpty()) {
            return;
        }

        if (!optionalServer.get().getServerInfo().getName().equals("server_screenshare")) {
            return;
        }

        together.getPartyManager().leave(event.getPlayer());
    }

    @Subscribe
    public void onPlayerQuit(DisconnectEvent event) {
        together.getPartyManager().leave(event.getPlayer());
    }
}
