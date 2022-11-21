package it.hemerald.basementx.bukkit.listeners;

import it.hemerald.basementx.api.bukkit.BasementBukkit;
import lombok.RequiredArgsConstructor;
import me.frep.vulcan.api.VulcanAPI;
import me.frep.vulcan.api.event.VulcanFlagEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class VulcanListener implements Listener {

    private final BasementBukkit basement;
    private final Set<String> bannedPlayers = new HashSet<>();

    @EventHandler
    public void onPlayerViolation(VulcanFlagEvent event) {
        if (event.getCheck().getVl() < event.getCheck().getMinimumVlToNotify()) return;
        if (event.getCheck().getVl() > event.getCheck().getMaxVl()) return;
        if (event.getCheck().getVl() % event.getCheck().getAlertInterval() != 0) return;

        alert(
                event.getPlayer(),
                event.getCheck().getName().substring(0, 1).toUpperCase() + event.getCheck().getName().substring(1),
                event.getCheck().getDescription(),
                String.valueOf(Character.toUpperCase(event.getCheck().getType())),
                event.getCheck().getVl(), event.getCheck().getMaxVl());
    }

    private void alert(Player player, String desc, String check, String type, int vl, int maxVL) {
        if (bannedPlayers.contains(player.getName())) return;

        int ping = VulcanAPI.Factory.getApi().getPing(player);
        double cps = VulcanAPI.Factory.getApi().getCps(player);

        basement.getRemoteVelocityService().cheatAlert(basement.getServerID(), player.getName(), check, type, desc, vl, maxVL, (long) cps, ping);
    }

    public void ban(String player) {
        if (bannedPlayers.contains(player)) return;

        bannedPlayers.add(player);
        Bukkit.getScheduler().runTaskLaterAsynchronously(basement.getPlugin(), () -> bannedPlayers.remove(player), 40L);
        basement.getRemoteVelocityService().cheatBan(basement.getServerID(), player);

        Player onlinePlayer = Bukkit.getPlayer(player);
        if (onlinePlayer != null) onlinePlayer.kickPlayer("AntiCheat Detection (Cheating)");
    }
}
