package it.hemerald.basementx.bukkit.listeners;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import it.hemerald.basementx.api.bukkit.BasementBukkit;
import it.hemerald.basementx.api.bukkit.player.stream.StreamMode;
import it.hemerald.basementx.api.player.BasementPlayer;
import it.hemerald.basementx.bukkit.player.BukkitBasementPlayer;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
public class PlayerListener implements Listener {

    private final BasementBukkit basement;

    private Cache<String, String> tpToCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        boolean isDisguised = basement.getDisguiseModule().isDisguised(player);
        AtomicReference<String> format = new AtomicReference<>(event.getMessage());

        if (!isDisguised) {
            basement.getNameTagModule().getTag(player).whenComplete(((tag, throwable) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                } else {
                    format.set(tag + player.getDisplayName() +
                            ChatColor.GRAY + ": " +
                            (player.hasPermission("chat.white") ? ChatColor.WHITE : ChatColor.GRAY) + event.getMessage());
                    event.setFormat(format.get());
                }
            }));
        } else {
            format.set(ChatColor.GRAY + player.getSafeFakeName() + ChatColor.GRAY + ": " + event.getMessage());
        }

        event.setFormat(format.get());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        BukkitBasementPlayer basementPlayer = new BukkitBasementPlayer(event.getPlayer(), basement);
        basement.getPlayerManager().addBasementPlayer(event.getPlayer().getName(), basementPlayer);

        Bukkit.getScheduler().runTaskLater(basement.getPlugin(), () -> {
            if(basement.getPlayerManager().isDisguised(event.getPlayer().getName())) {
                basement.getDisguiseModule().disguise(event.getPlayer());
            }
            StreamMode streamMode = basement.getStreamMode();
            if (streamMode.isEnabled()) {
                if (basementPlayer.isInStreamMode()) {
                    List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
                    streamMode.sendPackets(players, event.getPlayer(), true);
                } else {
                    streamMode.sendPackets(
                            event.getPlayer(),
                            basement.getPlayerManager().getBasementPlayers().parallelStream().filter(BasementPlayer::isInStreamMode)
                                    .map(bp -> Bukkit.getPlayer(bp.getName())).toArray(Player[]::new)
                    );
                }
            }
        }, 2L);

        String targetName = tpToCache.asMap().get(event.getPlayer().getName());
        if(targetName == null) return;
        Player target = Bukkit.getPlayer(targetName);
        if(target != null && target.isOnline()) {
            Bukkit.getServer().getScheduler().runTaskLater(basement.getPlugin(), () -> event.getPlayer().teleport(target), 20);
        }
        tpToCache.invalidate(event.getPlayer().getName());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        basement.getPlayerManager().removeBasementPlayer(event.getPlayer().getName());
    }

    public void tpTo(String playerName, String targetName) {
        Player player = Bukkit.getPlayer(playerName);
        if(player != null && player.isOnline()) {
            Player target = Bukkit.getPlayer(targetName);
            if(target != null && target.isOnline()) player.teleport(target);
        } else {
            tpToCache.put(playerName, targetName);
        }
    }
}
