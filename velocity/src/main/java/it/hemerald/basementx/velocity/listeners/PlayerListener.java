package it.hemerald.basementx.velocity.listeners;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import it.hemerald.basementx.velocity.BasementVelocity;
import org.redisson.api.RAtomicLong;

public class PlayerListener {

    private final BasementVelocity velocity;
    private final RAtomicLong playersCount;

    public PlayerListener(BasementVelocity velocity) {
        this.velocity = velocity;
        playersCount = velocity.getBasement().getRedisManager().getRedissonClient().getAtomicLong("playersCount");
        playersCount.set(velocity.getServer().getPlayerCount());
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onLogged(PostLoginEvent event) {
        playersCount.set(velocity.getServer().getPlayerCount());
        velocity.getUserDataManager().prepareUser(event.getPlayer());
    }

    @Subscribe(order = PostOrder.LAST)
    private void onQuit(DisconnectEvent event) {
        playersCount.set(velocity.getServer().getPlayerCount());
        velocity.getUserDataManager().cacheUser(event.getPlayer());
    }

}