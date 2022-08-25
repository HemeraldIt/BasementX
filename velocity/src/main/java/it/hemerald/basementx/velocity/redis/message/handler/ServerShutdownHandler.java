package it.hemerald.basementx.velocity.redis.message.handler;

import com.velocitypowered.api.proxy.ProxyServer;
import it.hemerald.basementx.api.redis.messages.handler.BasementMessageHandler;
import it.hemerald.basementx.api.redis.messages.implementation.ServerShutdownMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

@Slf4j(topic = "basement")
@RequiredArgsConstructor
public class ServerShutdownHandler implements BasementMessageHandler<ServerShutdownMessage> {

    private final ProxyServer server;

    @Override
    public void execute(ServerShutdownMessage message) {
        if(!message.getReceiver().endsWith("velocity")) return;
        log.info("Il server si sta spegnendo per il ServerShutdownMessage inviato da " + message.getSender());
        server.shutdown(Component.text()
                .append(Component.text("AVVISO ").color(NamedTextColor.RED).decorate(TextDecoration.BOLD))
                .append(Component.text("Il server si sta spegnendo").color(NamedTextColor.RED)).build());
    }

    @Override
    public Class<ServerShutdownMessage> getCommandClass() {
        return ServerShutdownMessage.class;
    }
}
