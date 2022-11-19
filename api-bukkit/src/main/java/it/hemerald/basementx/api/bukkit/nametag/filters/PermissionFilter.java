package it.hemerald.basementx.api.bukkit.nametag.filters;

import it.hemerald.basementx.api.bukkit.BasementBukkit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class PermissionFilter extends NameTagFilter {

    protected final List<String> permissions = new ArrayList<>();

    protected PermissionFilter(BasementBukkit basement, String prefix) {
        super(basement, prefix);

        fill();
    }

    protected abstract void fill();

    @Override
    public boolean test(Player player) {
        for (String permission : permissions) {
            if(player.hasPermission(permission)) return true;
        }

        return false;
    }

    public List<String> getPermissionOfPlayer(Player player) {
        List<String> list = new ArrayList<>();
        for (String permission : permissions) {
            if(player.hasPermission(permission)) {
                list.add(permission);
            }
        }

        return list;
    }

    public Set<Player> getPlayers(String permission) {
        return Bukkit.getOnlinePlayers().parallelStream().filter(player -> player.hasPermission(permission)).collect(Collectors.toSet());
    }
}
