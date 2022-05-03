package it.mineblock.basementx.velocity.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import it.mineblock.basementx.api.server.BukkitServer;
import it.mineblock.basementx.velocity.BasementVelocity;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.Optional;

@RequiredArgsConstructor
public class HubCommand implements SimpleCommand {

    private final BasementVelocity velocity;

    @Override
    public void execute(Invocation invocation) {
        if(!(invocation.source() instanceof Player player)) return;
        Optional<BukkitServer> optionalBukkitServer = velocity.getBasement().getPlayerManager().bestServer("lobby");
        if(optionalBukkitServer.isEmpty()) {
            player.sendMessage(Component.text()
                    .append(Component.text("ERRORE! ").color(NamedTextColor.RED).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE))
                    .append(Component.text("La lobby non è online!").color(NamedTextColor.RED)));
            return;
        }
        Optional<RegisteredServer> optionalRegisteredServer = velocity.getServer().getServer(optionalBukkitServer.get().getName());
        if(optionalRegisteredServer.isEmpty()) {
            player.sendMessage(Component.text()
                    .append(Component.text("ERRORE! ").color(NamedTextColor.RED).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE))
                    .append(Component.text("La lobby non è online!").color(NamedTextColor.RED)));
            return;
        }
        player.createConnectionRequest(optionalRegisteredServer.get()).fireAndForget();
    }
}
