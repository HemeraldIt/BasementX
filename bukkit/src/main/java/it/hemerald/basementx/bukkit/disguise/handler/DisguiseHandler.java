package it.hemerald.basementx.bukkit.disguise.handler;

import it.hemerlad.basementx.api.bukkit.BasementBukkit;
import it.hemerlad.basementx.api.redis.messages.handler.BasementMessageHandler;
import it.hemerlad.basementx.api.redis.messages.implementation.DisguiseMessage;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class DisguiseHandler implements BasementMessageHandler<DisguiseMessage> {

    private final BasementBukkit basement;

    @Override
    public void execute(DisguiseMessage command) {
        String name = command.getPlayer();
        Player player = Bukkit.getPlayerExact(name);
        if (player == null) return;

        Bukkit.getScheduler().runTask(basement.getPlugin(), () -> {
            if (!player.isOnline()) return;

            switch (command.getAction()) {
                case DISGUISE -> basement.getDisguiseModule().disguise(player);
                case UNDISGUISE -> basement.getDisguiseModule().undisguise(player);
            }
        });
    }

    @Override
    public Class<DisguiseMessage> getCommandClass() {
        return DisguiseMessage.class;
    }
}
