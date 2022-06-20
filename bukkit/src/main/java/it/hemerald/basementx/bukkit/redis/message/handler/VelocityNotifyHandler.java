package it.hemerald.basementx.bukkit.redis.message.handler;

import it.hemerlad.basementx.api.bukkit.BasementBukkit;
import it.hemerlad.basementx.api.redis.messages.handler.BasementMessageHandler;
import it.hemerlad.basementx.api.redis.messages.implementation.VelocityNotifyMessage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VelocityNotifyHandler implements BasementMessageHandler<VelocityNotifyMessage> {

    private final BasementBukkit basement;

    @Override
    public void execute(VelocityNotifyMessage message) {
        basement.getRemoteVelocityService().registerServer(basement.getPlugin().getServer().getServerName(), basement.getPlugin().getServer().getPort());
    }

    @Override
    public Class<VelocityNotifyMessage> getCommandClass() {
        return VelocityNotifyMessage.class;
    }
}
