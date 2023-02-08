package it.hemerald.basementx.velocity.friends.manager;

import com.velocitypowered.api.proxy.Player;
import it.hemerald.basementx.api.friends.Friend;
import it.hemerald.basementx.velocity.together.Together;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RLocalCachedMap;

import java.util.Optional;

@RequiredArgsConstructor
public class FriendsManager {

    public static final Component PREFIX = Component.text("§e§lFriends §8§l› ");

    @Getter private final Together together;
    private final RLocalCachedMap<String, Friend> friends;

    public FriendsManager(Together together) {
        this.together = together;
        this.friends = together.getBasement().getRedisManager().getRedissonClient().getLocalCachedMap("friends", LocalCachedMapOptions.defaults());
    }

    public void disable() {
        friends.clear();
    }

    public Optional<Friend> getFriend(String player) {
        return Optional.ofNullable(friends.get(player));
    }

    public Optional<Friend> getFriend(Player player) {
        return getFriend(player.getUsername());
    }

    public void sendMessage(Player player, String component) {
        player.sendMessage(PREFIX.append(Component.text("§7" + component)));
    }

    public void sendMessage(Player player, Component component) {
        player.sendMessage(PREFIX.append(component));
    }

}
