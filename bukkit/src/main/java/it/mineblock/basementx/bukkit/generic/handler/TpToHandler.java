package it.mineblock.basementx.bukkit.generic.handler;

import it.mineblock.basementx.api.redis.messages.handler.BasementMessageHandler;
import it.mineblock.basementx.api.redis.messages.implementation.TpToMessage;
import it.mineblock.basementx.bukkit.listeners.PlayerListener;

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
