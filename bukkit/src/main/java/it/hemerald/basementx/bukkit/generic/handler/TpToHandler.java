package it.hemerald.basementx.bukkit.generic.handler;

import it.hemerald.basementx.api.redis.messages.handler.BasementMessageHandler;
import it.hemerald.basementx.api.redis.messages.implementation.TpToMessage;
import it.hemerald.basementx.bukkit.listeners.PlayerListener;

public class TpToHandler implements BasementMessageHandler<TpToMessage> {

    private final PlayerListener playerListener;

    public TpToHandler(PlayerListener playerListener) {
        this.playerListener = playerListener;
    }

    @Override
    public void execute(TpToMessage message) {
        playerListener.tpTo(message.getPlayer(), message.getTarget());
    }

    @Override
    public Class<TpToMessage> getCommandClass() {
        return TpToMessage.class;
    }
}
