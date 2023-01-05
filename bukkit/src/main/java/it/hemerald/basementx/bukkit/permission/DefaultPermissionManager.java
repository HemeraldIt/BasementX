package it.hemerald.basementx.bukkit.permission;

import it.hemerald.basementx.api.bukkit.permission.PermissionManager;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.platform.PlayerAdapter;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DefaultPermissionManager implements PermissionManager {

    private final LuckPerms luckPerms;
    private final PlayerAdapter<Player> playerAdapter;

    public DefaultPermissionManager(LuckPerms luckPerms) {
        this.luckPerms = luckPerms;
        this.playerAdapter = luckPerms.getPlayerAdapter(Player.class);
    }

    @Override
    public User getUser(Player player) {
        return playerAdapter.getUser(player);
    }

    @Override
    public CompletableFuture<User> getUser(String player) {
        User user = luckPerms.getUserManager().getUser(player);
        if(user != null) return CompletableFuture.completedFuture(user);

        return luckPerms.getUserManager().lookupUniqueId(player).thenCompose(uuid -> {
            if(uuid == null) return null;

            return luckPerms.getUserManager().loadUser(uuid, player);
        });
    }

    @Override
    public CompletableFuture<User> getUser(UUID uuid) {
        User user = luckPerms.getUserManager().getUser(uuid);
        if(user != null) return CompletableFuture.completedFuture(user);

        return luckPerms.getUserManager().lookupUsername(uuid).thenCompose(username -> {
            if(username == null) return null;

            return luckPerms.getUserManager().loadUser(uuid, username);
        });
    }

    @Override
    public int getPriority(Player player) {
        User user = getUser(player);
        if(user == null) return 1000;

        Collection<Group> groups = user.getInheritedGroups(user.getQueryOptions());
        for (Group group : groups) {
            if (group.getWeight().isPresent()) return 1000 - group.getWeight().getAsInt();
        }

        return 1000;
    }

    @Override
    public CompletableFuture<String> getPrefix(String name) {
        return getUser(name).thenApply(this::getPrefix);
    }

    @Override
    public CompletableFuture<String> getPrefix(UUID uuid) {
        return getUser(uuid).thenApply(this::getPrefix);
    }

    @Override
    public String getPrefixGroup(String groupName) {
        Group group = luckPerms.getGroupManager().getGroup(groupName);
        if(group == null) return ChatColor.GRAY.toString();

        String prefix = group.getCachedData().getMetaData().getPrefix();
        if(prefix == null) return ChatColor.GRAY.toString();

        return prefix;
    }

    private String getPrefix(User user) {
        if(user == null) return ChatColor.GRAY.toString();

        String prefix = user.getCachedData().getMetaData().getPrefix();
        if (prefix == null) return ChatColor.GRAY.toString();

        return prefix;
    }
}
