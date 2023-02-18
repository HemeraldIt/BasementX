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
                event.getCheck().getDescription(),
                event.getCheck().getName().substring(0, 1).toUpperCase() + event.getCheck().getName().substring(1),
                String.valueOf(Character.toUpperCase(event.getCheck().getType())),
                event.getCheck().getVl(), event.getCheck().getMaxVl());
    }

    private void alert(Player player, String desc, String check, String type, int vl, int maxVL) {
        if (bannedPlayers.contains(player.getName())) return;

        int ping = VulcanAPI.Factory.getApi().getPing(player);
        if (ping == 0) return;
        double cps = VulcanAPI.Factory.getApi().getCps(player);

        basement.getRemoteVelocityService().cheatAlert(basement.getServerID(), player.getName(), check, type, desc, vl, maxVL, (long) cps, ping);
    }

    public void ban(String player) {
        if (bannedPlayers.contains(player)) return;

        bannedPlayers.add(player);
        Bukkit.getScheduler().runTaskLaterAsynchronously(basement.getPlugin(), () -> bannedPlayers.remove(player), 40L);

        Player onlinePlayer = Bukkit.getPlayerExact(player);
        if (onlinePlayer != null) {
            alert(onlinePlayer,
                    "Banned by AntiCheat",
                    "Cheating",
                    "A",
                    1, 1);
        }

        int ping = VulcanAPI.Factory.getApi().getPing(onlinePlayer);
        if (ping == 0) {
            return;
        }

        basement.getRemoteVelocityService().cheatBan(basement.getServerID(), player);

    }
}
