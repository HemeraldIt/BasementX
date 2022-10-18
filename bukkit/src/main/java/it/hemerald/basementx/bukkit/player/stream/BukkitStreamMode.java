package it.hemerald.basementx.bukkit.player.stream;

import it.hemerald.basementx.api.bukkit.player.stream.StreamMode;
import it.hemerald.basementx.api.player.BasementPlayer;
import it.hemerald.basementx.api.player.PlayerManager;
import org.bukkit.entity.Player;

import java.util.List;

public class BukkitStreamMode extends StreamMode {

    public BukkitStreamMode(PlayerManager<BasementPlayer> playerManager) {
        super(playerManager);
    }

    @Override
    public void sendPackets(Player who, Player... streamers) {
        for (Player streamer : streamers) {
            streamer.customizePlayer(
                    playerManager.getBasementPlayer(who.getName()).getStreamName(),
                    STREAM,
                    streamer
            );
        }
    }

    @Override
    public void sendPackets(List<Player> players, Player streamer) {
        for (Player who : players) {
            if (who.equals(streamer)) continue;
            streamer.customizePlayer(
                    playerManager.getBasementPlayer(who.getName()).getStreamName(),
                    STREAM,
                    who
            );
        }
    }

}
