package it.hemerald.basementx.velocity.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import it.hemerald.basementx.api.Basement;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DisguiseListener {

    private final Basement basement;

    @Subscribe
    public void onPlayerQuit(DisconnectEvent event) {
        basement.getPlayerManager().undisguise(event.getPlayer().getUsername());
    }
}
