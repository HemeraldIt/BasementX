package it.hemerald.basementx.velocity.listeners;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import it.hemerald.basementx.velocity.BasementVelocity;
import it.hemerald.basementx.velocity.database.DatabaseConstants;
import it.hemerlad.basementx.api.persistence.maria.queries.builders.WhereBuilder;
import it.hemerlad.basementx.api.persistence.maria.queries.builders.data.QueryBuilderSelect;
import it.hemerlad.basementx.api.server.BukkitServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RSetCache;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class PlayerListener {
    private static final String AUTH_URL = "https://api.minetools.eu/uuid/";
    private static final Gson gson = new Gson();

    private final BasementVelocity velocity;
    private final QueryBuilderSelect patternSelect;
    private final RAtomicLong playersCount;
    private final RSetCache<String> authPlayers;

    private final Cache<Player, Player> chatCache = CacheBuilder.newBuilder().expireAfterWrite(2, TimeUnit.SECONDS)
            .build();

    public PlayerListener(BasementVelocity velocity) {
        this.velocity = velocity;
        patternSelect = velocity.getDatabase().select().columns("id").from(DatabaseConstants.PLAYER_TABLE);
        playersCount = velocity.getBasement().getRedisManager().getRedissonClient().getAtomicLong("playersCount");
        playersCount.set(velocity.getServer().getPlayerCount());
        authPlayers = velocity.getBasement().getRedisManager().getRedissonClient().getSetCache("authPlayers");
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onConnect(PreLoginEvent event) {
        if(isPremium(event.getUsername())) {
            event.setResult(PreLoginEvent.PreLoginComponentResult.forceOnlineMode());
        }
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onLogged(PostLoginEvent event) {
        try {
            if (!patternSelect.patternClone().where(WhereBuilder.builder().equals("uuid", event.getPlayer().getUniqueId()).close())
                    .build().execReturn().isBeforeFirst()) {
                velocity.getDatabase().insert().ignore(true).into(DatabaseConstants.PLAYER_TABLE).columnSchema("uuid", "username")
                        .values(event.getPlayer().getUniqueId(), event.getPlayer().getUsername()).build().execAsync();
                if(event.getPlayer().isOnlineMode()) {
                    velocity.getDatabase().insert().ignore(true).into(DatabaseConstants.PREMIUM_TABLE).columnSchema("uuid", "username")
                            .values(event.getPlayer().getUniqueId().toString(), event.getPlayer().getUsername()).build().execAsync();
                }
            } else {
                velocity.getDatabase().update().table(DatabaseConstants.PLAYER_TABLE).set("username", event.getPlayer().getUsername())
                        .set("last_join", new Timestamp(System.currentTimeMillis()))
                        .where(WhereBuilder.builder().equals("uuid", event.getPlayer().getUniqueId()).close()).build().execAsync();
                if(event.getPlayer().isOnlineMode()) {
                    ResultSet resultSet = velocity.getDatabase().select().columns("username", "uuid").from(DatabaseConstants.PREMIUM_TABLE)
                            .where(WhereBuilder.builder().equals("uuid", event.getPlayer().getUniqueId().toString()).close()).build().execReturn();
                    if(resultSet.isBeforeFirst()) {
                        resultSet.next();
                        if(!resultSet.getString(1).equals(event.getPlayer().getUsername())) {
                            velocity.getDatabase().update().table(DatabaseConstants.PREMIUM_TABLE).set("username", event.getPlayer().getUsername())
                                    .where(WhereBuilder.builder().equals("uuid", event.getPlayer().getUniqueId()).close()).build().execAsync();
                        }
                    } else {
                        velocity.getDatabase().insert().ignore(true).into(DatabaseConstants.PREMIUM_TABLE).columnSchema("uuid", "username")
                                .values(event.getPlayer().getUniqueId().toString(), event.getPlayer().getUsername()).build().execAsync();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        playersCount.set(velocity.getServer().getPlayerCount());
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onServerChoice(PlayerChooseInitialServerEvent event) {
        if (event.getPlayer().isOnlineMode()) {
            getBestLobby().ifPresent(event::setInitialServer);
            event.getPlayer().sendMessage(Component.text()
                    .append(Component.text("PREMIUM! ").color(NamedTextColor.GREEN).decoration(TextDecoration.BOLD, true))
                    .append(Component.text("Ti sei autenticato automaticamente").color(NamedTextColor.GREEN)));
        } else {
            Optional<RegisteredServer> auth_lobby = velocity.getServer().getServer("auth_lobby");
            if (auth_lobby.isEmpty()) {
                event.getPlayer().disconnect(Component.text("La auth lobby è offline, riprova tra poco").color(NamedTextColor.RED));
                return;
            }
            event.setInitialServer(auth_lobby.get());
        }
    }

    @Subscribe
    private void onQuit(DisconnectEvent event) {
        playersCount.set(velocity.getServer().getPlayerCount());
    }

    @Subscribe
    public void onKickAuth(KickedFromServerEvent event) {
        if (event.getServer().getServerInfo().getName().startsWith("auth_")) {
            event.getPlayer().disconnect(Component.text("» Tempo scaduto per eseguire l''autenticazione, sei stato espulso dal server, per favore riprova!")
                    .color(NamedTextColor.RED));
        }
    }

    @Subscribe
    private void onCommand(CommandExecuteEvent event) {
        if(!(event.getCommandSource() instanceof Player player) || player.isOnlineMode()) return;
        if(event.getCommand().startsWith("login") || event.getCommand().startsWith("register") ||
                event.getCommand().startsWith("l") || event.getCommand().startsWith("reg")) return;
        Optional<ServerConnection> connection = player.getCurrentServer();
        if(connection.isPresent() && !connection.get().getServerInfo().getName().startsWith("auth_")) return;
        if(authPlayers.contains(player.getUsername())) event.setResult(CommandExecuteEvent.CommandResult.denied());
    }

    @Subscribe
    private void onMessage(PlayerChatEvent event) {
        if(event.getPlayer().hasPermission("mineblock.staff")) return;
        if(chatCache.getIfPresent(event.getPlayer()) != null) {
            event.getPlayer().sendMessage(Component.text()
                    .append(Component.text("ERRORE! ").color(NamedTextColor.RED).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE))
                    .append(Component.text("Puoi inviare un messaggio ogni due secondi").color(NamedTextColor.RED)));
            event.setResult(PlayerChatEvent.ChatResult.denied());
        } else {
            chatCache.put(event.getPlayer(), event.getPlayer());
        }
    }

    private Optional<RegisteredServer> getBestLobby() {
        Optional<BukkitServer> lobby = velocity.getBasement().getPlayerManager().bestServer("lobby");
        if(lobby.isEmpty()) {
            velocity.getLogger().log(Level.WARNING, () -> "Tried to get a lobby server but no one is online");
        } else {
            Optional<RegisteredServer> optionalServer = velocity.getServer().getServer(lobby.get().getName());
            if (optionalServer.isEmpty()) {
                velocity.getLogger().log(Level.WARNING, () -> "Tried to get an invalid server (" + lobby.get().getName() + ")");
            } else {
                return optionalServer;
            }
        }
        return Optional.empty();
    }

    private boolean isPremium(String username) {
        try {
            if (velocity.getDatabase().select().columns("username").from(DatabaseConstants.AUTHME_TABLE)
                    .where(WhereBuilder.builder().equals("username", username).close()).build().execReturn().isBeforeFirst()) return false;
            if (velocity.getDatabase().select().columns("username", "uuid").from(DatabaseConstants.PREMIUM_TABLE)
                    .where(WhereBuilder.builder().equals("username", username).close()).build().execReturn().isBeforeFirst()) {
                return true;
            }
        } catch (SQLException e) {
            velocity.getLogger().log(Level.SEVERE, "SQLException caught when user " + username + " was connecting! Forced Online Mode", e);
            return true;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(AUTH_URL + username).openStream()))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            JsonObject json = gson.fromJson(content.toString(), JsonElement.class).getAsJsonObject();
            if (!json.get("status").getAsString().equals("ERR")) {
                return true;
            }
        } catch (Exception e) {
            velocity.getLogger().log(Level.SEVERE, "Exception caught when user " + username + " was connecting! Forced Online Mode", e);
            return true;
        }
        return false;
    }
}