package it.hemerald.basementx.api.bukkit.permission;

import net.luckperms.api.model.user.User;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PermissionManager {

    /**
     * Gets the luckperms {@link User} object
     *
     * @param player the player
     * @return the luckperms user object
     */
    User getUser(Player player);

    /**
     * Gets the luckperms {@link User} object async
     *
     * @param name the player name
     * @return the luckperms user object
     */
    CompletableFuture<User> getUser(String name);

    /**
     * Gets the luckperms {@link User} object async
     *
     * @param uuid the player uuid
     * @return the luckperms user object
     */
    CompletableFuture<User> getUser(UUID uuid);

    /**
     * Gets the player {@link net.luckperms.api.model.group.Group} priority
     *
     * @param player the player
     * @return the player group priority. 1000 as default value
     */
    int getPriority(Player player);

    /**
     * Gets player {@link net.luckperms.api.model.group.Group} prefix
     *
     * @param name the player name
     * @return the group prefix
     */
    CompletableFuture<String> getPrefix(String name);

    /**
     * Gets player {@link net.luckperms.api.model.group.Group} prefix
     *
     * @param uuid the player uuid
     * @return the group prefix
     */
    CompletableFuture<String> getPrefix(UUID uuid);

    /**
     * Gets {@link net.luckperms.api.model.group.Group} prefix
     *
     * @param groupName the group name
     * @return the group prefix
     */
    String getPrefixGroup(String groupName);
}
