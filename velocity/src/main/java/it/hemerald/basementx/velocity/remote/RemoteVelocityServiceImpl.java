package it.hemerald.basementx.velocity.remote;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import com.viaversion.viaversion.api.Via;
import it.hemerald.basementx.api.remote.RemoteVelocityService;
import it.hemerald.basementx.velocity.BasementVelocity;
import it.hemerald.basementx.velocity.alert.AlertEvent;
import it.hemerald.basementx.velocity.alert.AlertType;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

@RequiredArgsConstructor
public class RemoteVelocityServiceImpl implements RemoteVelocityService {

    private final GsonComponentSerializer componentSerializer = GsonComponentSerializer.colorDownsamplingGson();

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

        Player realPlayer = optionalPlayer.get();
        Optional<ServerConnection> currentServer = realPlayer.getCurrentServer();
        if (currentServer.isEmpty() || currentServer.get().getServerInfo().getName().equalsIgnoreCase(server)) return;
        realPlayer.createConnectionRequest(optionalServer.get()).fireAndForget();
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

        Player realPlayer = optionalPlayer.get();
        Optional<ServerConnection> currentServer = realPlayer.getCurrentServer();
        if (currentServer.isEmpty() || currentServer.get().getServerInfo().getName().equalsIgnoreCase(server)) return;
        realPlayer.createConnectionRequest(optionalServer.get()).fireAndForget();
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
    public void sendMessageComponent(String player, String... messages) {
        Optional<Player> optionalPlayer = this.velocity.getServer().getPlayer(player);
        if(optionalPlayer.isEmpty()) return;
        Player velocityPlayer = optionalPlayer.get();
        for (String message : messages) {
            velocityPlayer.sendMessage(componentSerializer.deserialize(message));
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
    public void cheatAlert(String server, String playerName, String category, String type, String desc, int level, int maxLevel, long cps, long ping) {
        this.velocity.getServer().getEventManager().fire(new AlertEvent(server, playerName, category, type, desc, level, maxLevel, cps, ping)).thenAccept(alertEvent -> {
            if(!alertEvent.getResult().isAllowed()) return;

            List<Player> toAlert = new ArrayList<>();
            for (Player player : velocity.getServer().getAllPlayers()) {
                if (!player.hasPermission("basement.alerts")) continue;

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

                toAlert.add(player);
            }

            TextComponent.Builder builder = Component.text();

            builder.append(Component.text("[" + server + "] ").clickEvent(ClickEvent.runCommand("/goto " + server))).color(NamedTextColor.RED);
            builder.append(Component.text("§8» §b" + playerName + " §7flagged §e" + category + " (" + type + ") §7(" + level + "/" + maxLevel + ")")
                    .clickEvent(ClickEvent.runCommand("/tpto " + playerName)));

            builder.hoverEvent(HoverEvent.showText(
                    Component.join(JoinConfiguration.newlines(),
                            Component.text(),
                            Component.text("Categoria: ", NamedTextColor.GRAY).append(Component.text(category, NamedTextColor.YELLOW)),
                            Component.text("Tipo: ", NamedTextColor.GRAY).append(Component.text(type, NamedTextColor.AQUA)),
                            Component.text("Info: ", NamedTextColor.GRAY).append(Component.text(desc, NamedTextColor.GREEN)),
                            Component.text("Livello: ", NamedTextColor.GRAY)
                                    .append(Component.text(level, NamedTextColor.RED))
                                    .append(Component.text("/", NamedTextColor.GRAY)
                                            .append(Component.text(maxLevel, NamedTextColor.RED))),
                            Component.text("CPS: ", NamedTextColor.GRAY).append(Component.text(cps, formatCps(cps))),
                            Component.text("Ping: ", NamedTextColor.GRAY).append(Component.text(ping, formatPing(ping))),
                            Component.text()
                    )
            ));

            TextComponent alert = builder.build();

            for (Player player : toAlert) {
                player.sendMessage(alert);
            }
        });
    }

    @Override
    public void cheatBan(String server, String player) {
        velocity.getServer().getCommandManager().executeAsync(velocity.getServer().getConsoleCommandSource(), "ban " +
                player + " 30d Cheating (AntiCheat)");
    }

    public int playerVersion(UUID uuid) {
        return Via.getAPI().getPlayerVersion(uuid);
    }

    public static NamedTextColor formatPing(long ping) {
        if (ping >= 0 && ping < 70) {
            return NamedTextColor.GREEN;
        } else if (ping >= 70 && ping < 100) {
            return NamedTextColor.YELLOW;
        } else if (ping >= 100 && ping < 150) {
            return NamedTextColor.GOLD;
        } else if (ping >= 150) {
            return NamedTextColor.RED;
        }
        return NamedTextColor.WHITE;
    }

    public static NamedTextColor formatCps(long cps) {
        if (cps >= 0 && cps < 10) {
            return NamedTextColor.GREEN;
        } else if (cps >= 10 && cps < 15) {
            return NamedTextColor.YELLOW;
        } else if (cps >= 15 && cps < 20) {
            return NamedTextColor.GOLD;
        } else if (cps >= 20) {
            return NamedTextColor.RED;
        }
        return NamedTextColor.WHITE;
    }
}
