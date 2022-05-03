package it.mineblock.basementx.bukkit.redis.message.handler;

import it.mineblock.basementx.api.bukkit.BasementBukkit;
import it.mineblock.basementx.api.redis.messages.handler.BasementMessageHandler;
import it.mineblock.basementx.api.redis.messages.implementation.ServerShutdownMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.ChatColor;

@Slf4j(topic = "basement")
@RequiredArgsConstructor
public class ServerShutdownHandler implements BasementMessageHandler<ServerShutdownMessage> {

    private final BasementBukkit basement;

    @Override
    public void execute(ServerShutdownMessage message) {
        if(!message.getReceiver().equals(basement.getServerID())) return;
        log.info("Il server si sta spegnendo per il ServerShutdownMessage inviato da " + message.getSender());
        basement.getPlugin().getServer().broadcastMessage(ChatColor.RED + ChatColor.BOLD.toString() + "Avviso " + ChatColor.RED + "Il server si sta spegnendo");
        basement.getPlugin().getServer().shutdown();
    }

    @Override
    public Class<ServerShutdownMessage> getCommandClass() {
        return ServerShutdownMessage.class;
    }
}
