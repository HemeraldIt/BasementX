package it.mineblock.basementx.bukkit.listeners;

import it.mineblock.basementx.api.bukkit.BasementBukkit;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class HostListener implements Listener {

    private final BasementBukkit basement;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if(Bukkit.getServer().getOnlinePlayers().size() != 1) return;

        basement.setHosted(true);
        basement.setHoster(event.getPlayer().getName());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if(Bukkit.getServer().getOnlinePlayers().size() != 1) return;

        basement.setHosted(false);
        basement.setHoster(null);
    }
}
