package it.hemerald.basementx.velocity.remote;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import it.hemerald.basementx.api.remote.RemoteVelocityService;
import it.hemerald.basementx.velocity.BasementVelocity;
import it.hemerald.basementx.velocity.alert.AlertType;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

@RequiredArgsConstructor
public class RemoteVelocityServiceImpl implements RemoteVelocityService {

    private final BasementVelocity velocity;

    @Override
    public boolean isOnRanch(String player, String server) {
        Optional<Player> proxyPlayer = velocity.getServer().getPlayer(player);
        if (proxyPlayer.isEmpty()) return false;
        Optional<ServerConnection> connection = proxyPlayer.get().getCurrentServer();
        if (connection.isEmpty()) return false;
        return connection.get().getServerInfo().getName().startsWith(server);
    }

    @Override
    public boolean isOnRanch(UUID player, String server) {
        Optional<Player> proxyPlayer = velocity.getServer().getPlayer(player);
        if (proxyPlayer.isEmpty()) return false;
        Optional<ServerConnection> connection = proxyPlayer.get().getCurrentServer();
        if (connection.isEmpty()) return false;
        return connection.get().getServerInfo().getName().startsWith(server);
    }

    @Override
    public String getServer(String player) {
        Optional<Player> proxyPlayer = velocity.getServer().getPlayer(player);
        if (proxyPlayer.isEmpty()) return null;
        Optional<ServerConnection> connection = proxyPlayer.get().getCurrentServer();
        if (connection.isEmpty()) return null;
        return connection.get().getServerInfo().getName();
    }

    @Override
    public void sendToServer(String player, String server) {
        Optional<Player> optionalPlayer = this.velocity.getServer().getPlayer(player);
        if(optionalPlayer.isEmpty()) return;
        Optional<RegisteredServer> optionalServer = this.velocity.getServer().getServer(server);
        if (optionalServer.isEmpty()) {
            velocity.getLogger().log(Level.WARNING, () -> "Tried to send " + player + "to an invalid server (" + server + ")");
            return;
        }
        optionalPlayer.get().createConnectionRequest(optionalServer.get()).fireAndForget();
    }

    @Override
    public void sendToServer(UUID uuid, String server) {
        Optional<Player> optionalPlayer = this.velocity.getServer().getPlayer(uuid);
        if(optionalPlayer.isEmpty()) return;
        Optional<RegisteredServer> optionalServer = this.velocity.getServer().getServer(server);
        if (optionalServer.isEmpty()) {
            velocity.getLogger().log(Level.WARNING, () -> "Tried to send " + uuid + "to an invalid server (" + server + ")");
            return;
        }
        optionalPlayer.get().createConnectionRequest(optionalServer.get()).fireAndForget();
    }

    @Override
    public void sendMessage(String player, String... messages) {
        Optional<Player> optionalPlayer = this.velocity.getServer().getPlayer(player);
        if(optionalPlayer.isEmpty()) return;
        Player velocityPlayer = optionalPlayer.get();
        for (String message : messages) {
            velocityPlayer.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(message));
        }
    }

    @Override
    public void sendMessageWithPermission(String player, String permissionNode, String... messages) {
        Optional<Player> optionalPlayer = this.velocity.getServer().getPlayer(player);
        if(optionalPlayer.isEmpty()) return;
        Player velocityPlayer = optionalPlayer.get();
        if(!velocityPlayer.hasPermission(permissionNode)) return;
        for (String message : messages) {
            velocityPlayer.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(message));
        }
    }

    @Override
    public void registerServer(String serverName, int port) {
        Optional<RegisteredServer> serverOptional = velocity.getServer().getServer(serverName);
        ServerInfo newServer = new ServerInfo(serverName, new InetSocketAddress(serverName, port));
        if(serverOptional.isEmpty()) {
            velocity.getServer().registerServer(newServer);
        } else {
            velocity.getServer().unregisterServer(serverOptional.get().getServerInfo());
            velocity.getServer().registerServer(newServer);
        }
    }

    @Override
    public void cheatAlert(String server, String alert, long ping) {
        for (Player player : velocity.getServer().getAllPlayers()) {
            if (!player.hasPermission("hemerald.alerts")) continue;


            Optional<ServerConnection> currentServer = player.getCurrentServer();
            if (currentServer.isPresent() && currentServer.get().getServerInfo().getName().equals("server_screenshare")) {
                continue;
            }

            AlertType alertType = velocity.getToggled().get(player.getUsername());
            if (alertType != null) {
                if (alertType == AlertType.NONE) continue;
                if (alertType == AlertType.SERVER) {
                    Optional<ServerConnection> optional = player.getCurrentServer();
                    if (optional.isPresent()) {
                        if (!optional.get().getServerInfo().getName().equalsIgnoreCase(server))
                            continue;
                    }
                }
            }

            TextComponent.Builder builder = Component.text();

            builder.append(Component.text("[" + server + "] ")).color(NamedTextColor.DARK_AQUA);
            builder.append(Component.text(alert));

            builder.hoverEvent(HoverEvent.showText(
                    Component.text()
                            .content("Ping: " + ping)
                            .color(NamedTextColor.AQUA)))
            ;

            builder.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/goto " + server));

            player.sendMessage(builder);
        }
    }

    @Override
    public void cheatBan(String server, String player) {
        velocity.getServer().getCommandManager().executeAsync(velocity.getServer().getConsoleCommandSource(), "ban " +
                player + " 14d Cheating (AntiCheat)");

        TextComponent.Builder component = Component.text();
        component.append(Component.newline());
        component.append(Component.text("✘ L'anticheat ha bannato ").color(NamedTextColor.RED));
        component.append(Component.text(player).color(NamedTextColor.WHITE));
        component.append(Component.text(" ✘").color(NamedTextColor.RED));
        component.append(Component.newline());

        for (Player velocityPlayer : velocity.getServer().getAllPlayers()) {
            Optional<ServerConnection> playerServer = velocityPlayer.getCurrentServer();
            if (velocityPlayer.hasPermission("hemerald.alerts") || playerServer.isPresent() && playerServer.get().getServerInfo().getName().equals(server)) {
                velocityPlayer.sendMessage(component);
            }
        }
    }
}
