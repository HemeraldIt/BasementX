package it.hemerald.basementx.api.bukkit.nametag.filters;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class SubFilter extends PermissionFilter {

    private List<String> streamerPermissions;

    public SubFilter() {
        super(ChatColor.DARK_PURPLE + ChatColor.BOLD.toString() + "SUB " + ChatColor.RESET);
    }

    @Override
    public boolean test(Player player) {
        return player.getFakeName() == null && super.test(player);
    }

    @Override
    protected void fill() {
        this.permissions.add("sub.hemerald");

        this.streamerPermissions = new ArrayList<>();
        for (String permission : this.permissions) {
            streamerPermissions.add("streamer." + permission.split("\\.")[1]);
        }
    }

    @Override
    public boolean ignoreMe(Player player) {
        for (String permission : streamerPermissions) {
            if(player.hasPermission(permission)) return true;
        }

        return false;
    }
}
