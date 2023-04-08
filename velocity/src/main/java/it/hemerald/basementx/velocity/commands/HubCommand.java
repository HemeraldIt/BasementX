package it.hemerald.basementx.velocity.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import it.hemerald.basementx.api.server.BukkitServer;
import it.hemerald.basementx.velocity.BasementVelocity;
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
        if (!(invocation.source() instanceof Player player)) return;
        Optional<ServerConnection> optionalServerConnection = player.getCurrentServer();
        if (optionalServerConnection.isEmpty()) {
            player.sendMessage(Component.text()
                    .append(Component.text("Hemerald").color(NamedTextColor.LIGHT_PURPLE))
                    .append(Component.text(" » ").color(NamedTextColor.BLACK))
                    .append(Component.text("C'è stato un problema con il proxy!").color(NamedTextColor.RED)));
            return;
        }
        String[] serverNameParts = optionalServerConnection.get().getServerInfo().getName().split("_");
        if (serverNameParts[0].equals("hub")) {
            player.sendMessage(Component.text()
                    .append(Component.text("Hemerald").color(NamedTextColor.LIGHT_PURPLE))
                    .append(Component.text(" » ").color(NamedTextColor.BLACK))
                    .append(Component.text("Sei già alla hub!").color(NamedTextColor.RED)));
            return;
        }
        Optional<BukkitServer> optionalBukkitServer;
        if (serverNameParts.length > 1) {
            if (serverNameParts[1].contains("lobby")) {
                optionalBukkitServer = velocity.getBasement().getPlayerManager().bestServer("hub");
            } else {
                optionalBukkitServer = velocity.getBasement().getPlayerManager().bestServer(serverNameParts[0] + "_lobby");
                if (optionalBukkitServer.isEmpty()) {
                    optionalBukkitServer = velocity.getBasement().getPlayerManager().bestServer("hub");
                }
            }
        } else {
            optionalBukkitServer = velocity.getBasement().getPlayerManager().bestServer("hub");
        }
        if (optionalBukkitServer.isEmpty()) {
            player.sendMessage(Component.text()
                    .append(Component.text("Hemerald").color(NamedTextColor.LIGHT_PURPLE))
                    .append(Component.text(" » ").color(NamedTextColor.BLACK))
                    .append(Component.text("La lobby non è online!").color(NamedTextColor.RED)));
            return;
        }
        Optional<RegisteredServer> optionalRegisteredServer = velocity.getServer().getServer(optionalBukkitServer.get().getName());
        if (optionalRegisteredServer.isEmpty()) {
            player.sendMessage(Component.text()
                    .append(Component.text("Hemerald").color(NamedTextColor.LIGHT_PURPLE))
                    .append(Component.text(" » ").color(NamedTextColor.BLACK))
                    .append(Component.text("La lobby non è online!").color(NamedTextColor.RED)));
            return;
        }
        player.createConnectionRequest(optionalRegisteredServer.get()).fireAndForget();
    }
}
