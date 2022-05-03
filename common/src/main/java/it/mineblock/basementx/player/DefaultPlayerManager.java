package it.mineblock.basementx.player;

import it.mineblock.basementx.api.Basement;
import it.mineblock.basementx.api.player.BasementPlayer;
import it.mineblock.basementx.api.player.PlayerManager;
import it.mineblock.basementx.api.remote.RemoteVelocityService;
import it.mineblock.basementx.api.server.BukkitServer;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSetCache;

import java.util.*;

@Slf4j(topic = "basement")
public class DefaultPlayerManager<E extends BasementPlayer> implements PlayerManager<E> {

    private final Basement basement;
    private final RSetCache<String> disguiseSet;
    private final RemoteVelocityService velocityService;

    private final Map<String, E> playerMap = new HashMap<>();

    public DefaultPlayerManager(Basement basement) {
        this.basement = basement;
        this.disguiseSet = basement.getRedisManager().getRedissonClient().getSetCache("disguise");
        this.velocityService = basement.getRedisManager().getRedissonClient().getRemoteService().get(RemoteVelocityService.class);
    }

    @Override
    public void addBasementPlayer(String name, E basementPlayer) {
        playerMap.put(name, basementPlayer);
    }

    @Override
    public void removeBasementPlayer(String name) {
        playerMap.remove(name);
    }

    @Override
    public E getBasementPlayer(String name) {
        return playerMap.get(name);
    }

    @Override
    public Collection<E> getBasementPlayers() {
        return Collections.unmodifiableCollection(playerMap.values());
    }

    @Override
    public void disguise(String name) {
        disguiseSet.add(name);
    }

    @Override
    public void undisguise(String name) {
        disguiseSet.remove(name);
    }

    @Override
    public void sendMessage(String player, String... messages) {
        velocityService.sendMessage(player, messages);
    }

    @Override
    public void sendMessageWithPermission(String player, String permissionNode, String... messages) {
        velocityService.sendMessageWithPermission(player, permissionNode, messages);
    }

    @Override
    public void sendToServer(String player, String server) {
        velocityService.sendToServer(player, server);
    }

    @Override
    public void sendToServer(UUID uuid, String server) {
        velocityService.sendToServer(uuid, server);
    }

    @Override
    public void sendToLobby(String player) {
        sendToGameLobby(player, "lobby");
    }

    @Override
    public void sendToLobby(UUID player) {
        sendToGameLobby(player, "lobby");
    }

    @Override
    public void sendToGameLobby(String player, String game) {
        Optional<BukkitServer> bestServer = bestServer(game);
        if(bestServer.isEmpty()) {
            log.warn("Tried to send " + player + "to a lobby server but no one is online");
            return;
        }

        sendToServer(player, bestServer.get().getName());
    }

    @Override
    public void sendToGameLobby(UUID player, String game) {
        Optional<BukkitServer> bestServer = bestServer(game);
        if(bestServer.isEmpty()) {
            log.warn("Tried to send " + player + "to a lobby server but no one is online");
            return;
        }

        sendToServer(player, bestServer.get().getName());
    }

    @Override
    public Optional<BukkitServer> bestServer(String ranch) {
        List<BukkitServer> bestServers = basement.getServerManager().getOnlineServers(ranch)
                .parallelStream().sorted(Comparator.comparingInt(BukkitServer::getOnline)).limit(2).toList();
        if(bestServers.isEmpty()) {
            return Optional.empty();
        }
        if(bestServers.size() == 1) {
            return Optional.of(bestServers.get(0));
        }
        BukkitServer first = bestServers.get(0);
        BukkitServer second = bestServers.get(1);
        if(second.getOnline() - first.getOnline() > 10) {
            return Optional.of(first);
        } else {
            return Optional.of(bestServers.get(Math.random() < 0.5 ? 0 : 1));
        }
    }

    @Override
    public boolean isDisguised(String name) {
        return disguiseSet.contains(name);
    }
}
