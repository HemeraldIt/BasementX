package it.hemerald.basementx.velocity.listeners;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import it.hemerald.basementx.api.party.Party;
import it.hemerald.basementx.velocity.BasementVelocity;
import org.redisson.api.RAtomicLong;

import java.util.Optional;

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
        velocity.getUserDataManager().saveUser(event.getPlayer());
    }

    @Subscribe
    public void onServerSwitch(ServerConnectedEvent event) {
        if (!event.getServer().getServerInfo().getName().contains("_lobby")) return;
        Optional<Party> optional = velocity.getTogether().getPartyManager().getParty(event.getPlayer());
        if (optional.isEmpty()) return;
        Party party = optional.get();
        if (!party.getLeader().equals(event.getPlayer().getUsername())) return;

        for (String memberName : party.getMembers()) {
            Optional<Player> optionalMember = velocity.getServer().getPlayer(memberName);
            if (optionalMember.isEmpty()) continue;
            optionalMember.get().createConnectionRequest(event.getServer()).fireAndForget();
        }
    }
}