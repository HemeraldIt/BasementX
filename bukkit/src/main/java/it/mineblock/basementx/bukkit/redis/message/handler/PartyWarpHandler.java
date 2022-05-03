package it.mineblock.basementx.bukkit.redis.message.handler;

import it.mineblock.basementx.api.Basement;
import it.mineblock.basementx.api.bukkit.events.PartyWarpEvent;
import it.mineblock.basementx.api.redis.messages.handler.BasementMessageHandler;
import it.mineblock.basementx.api.redis.messages.implementation.PartyWarpMessage;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class PartyWarpHandler implements BasementMessageHandler<PartyWarpMessage> {

    private final Basement basement;

    @Override
    public void execute(PartyWarpMessage message) {
        Player player = Bukkit.getPlayer(message.getPlayer());
        if(player == null) return;
        PartyWarpEvent partyWarpEvent = new PartyWarpEvent(player, message.getServer());
        Bukkit.getServer().getPluginManager().callEvent(partyWarpEvent);
        if(!partyWarpEvent.isCancelled()) {
            basement.getPlayerManager().sendToServer(message.getPlayer(), message.getServer());
        }
    }

    @Override
    public Class<PartyWarpMessage> getCommandClass() {
        return PartyWarpMessage.class;
    }
}
