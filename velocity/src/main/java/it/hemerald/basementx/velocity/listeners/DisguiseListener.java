package it.hemerald.basementx.velocity.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import it.hemerlad.basementx.api.Basement;
import it.hemerlad.basementx.api.player.disguise.DisguiseAction;
import it.hemerlad.basementx.api.redis.messages.implementation.DisguiseMessage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DisguiseListener {

    private final Basement basement;

    @Subscribe
    public void onPlayerConnect(ServerConnectedEvent event) {
        String playerUsername = event.getPlayer().getUsername();

        if(basement.getPlayerManager().isDisguised(playerUsername)) {
            basement.getRedisManager().publishMessage(new DisguiseMessage(playerUsername, DisguiseAction.DISGUISE));
        }
    }

    @Subscribe
    public void onPlayerQuit(DisconnectEvent event) {
        basement.getPlayerManager().undisguise(event.getPlayer().getUsername());
    }
}
