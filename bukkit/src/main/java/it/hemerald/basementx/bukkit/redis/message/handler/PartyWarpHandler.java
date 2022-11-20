package it.hemerald.basementx.bukkit.redis.message.handler;

import it.hemerald.basementx.api.bukkit.BasementBukkit;
import it.hemerald.basementx.api.bukkit.events.PartyWarpEvent;
import it.hemerald.basementx.api.redis.messages.handler.BasementMessageHandler;
import it.hemerald.basementx.api.redis.messages.implementation.PartyWarpMessage;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class PartyWarpHandler implements BasementMessageHandler<PartyWarpMessage> {

    private final BasementBukkit basement;

    @Override
    public void execute(PartyWarpMessage message) {
        Player player = Bukkit.getPlayer(message.getPlayer());
        if(player == null) return;
        PartyWarpEvent partyWarpEvent = new PartyWarpEvent(player, message.getServer());
        basement.getPlugin().getServer().getScheduler().runTask(basement.getPlugin(), () -> {
            Bukkit.getServer().getPluginManager().callEvent(partyWarpEvent);
            if(!partyWarpEvent.isCancelled()) {
                basement.getPlayerManager().sendToServer(message.getPlayer(), message.getServer());
            }
        });
    }

    @Override
    public Class<PartyWarpMessage> getCommandClass() {
        return PartyWarpMessage.class;
    }
}
