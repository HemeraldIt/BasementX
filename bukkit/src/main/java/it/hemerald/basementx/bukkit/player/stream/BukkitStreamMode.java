package it.hemerald.basementx.bukkit.player.stream;

import it.hemerald.basementx.api.bukkit.disguise.module.DisguiseModule;
import it.hemerald.basementx.api.bukkit.player.stream.StreamMode;
import it.hemerald.basementx.api.player.BasementPlayer;
import it.hemerald.basementx.api.player.PlayerManager;
import org.bukkit.Skin;
import org.bukkit.entity.Player;

import java.util.List;

public class BukkitStreamMode extends StreamMode {

    private final DisguiseModule disguiseModule;

    public BukkitStreamMode(PlayerManager<BasementPlayer> playerManager, DisguiseModule disguiseModule) {
        super(playerManager);
        this.disguiseModule = disguiseModule;
    }

    @Override
    public void sendPackets(Player who, Player... streamers) {
        for (Player streamer : streamers) {
            if (streamer == who) continue;
            streamer.customizePlayer(
                    playerManager.getBasementPlayer(who.getName()).getStreamName(),
                    STREAM,
                    who
            );
        }
    }

    @Override
    public void sendPackets(List<Player> players, Player streamer, boolean enable) {
        for (Player who : players) {
            if (who.equals(streamer)) continue;
            BasementPlayer basementPlayer = playerManager.getBasementPlayer(who.getName());
            streamer.customizePlayer(
                    enable ? basementPlayer.getStreamName() : who.getSafeFakeName(),
                    enable ? STREAM : who.getSafeFakeSkin(),
                    who
            );
            who.customizePlayer(
                    enable ? disguiseModule.getRandomUsername() : streamer.getSafeFakeName(),
                    enable ? Skin.EMPTY : streamer.getSafeFakeSkin(),
                    streamer
            );
        }
    }

}
