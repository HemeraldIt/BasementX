package it.hemerald.basementx.bukkit.generic.handler;

import it.hemerald.basementx.api.redis.messages.handler.BasementMessageHandler;
import it.hemerald.basementx.api.redis.messages.implementation.TpToMessage;
import it.hemerald.basementx.bukkit.listeners.PlayerListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.BiConsumer;

public class TpToHandler implements BasementMessageHandler<TpToMessage> {

    private final JavaPlugin plugin;
    private final BiConsumer<String, String> tpTo;

    public TpToHandler(JavaPlugin plugin, BiConsumer<String, String> tpTo) {
        this.plugin = plugin;
        this.tpTo = tpTo;
    }

    @Override
    public void execute(TpToMessage message) {
        plugin.getServer().getScheduler().runTask(plugin, () -> tpTo.accept(message.getPlayer(), message.getTarget()));
    }

    @Override
    public Class<TpToMessage> getCommandClass() {
        return TpToMessage.class;
    }
}
