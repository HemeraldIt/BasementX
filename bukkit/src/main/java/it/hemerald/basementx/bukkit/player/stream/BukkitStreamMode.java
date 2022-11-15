package it.hemerald.basementx.bukkit.player.stream;

import it.hemerald.basementx.api.bukkit.nametag.module.NameTagModule;
import it.hemerald.basementx.api.bukkit.player.stream.StreamMode;
import it.hemerald.basementx.api.player.BasementPlayer;
import it.hemerald.basementx.api.player.PlayerManager;
import org.bukkit.Skin;
import org.bukkit.entity.Player;

import java.util.List;

public class BukkitStreamMode extends StreamMode {

    private final NameTagModule nameTagModule;

    public BukkitStreamMode(PlayerManager<BasementPlayer> playerManager, NameTagModule nameTagModule) {
        super(playerManager);
        this.nameTagModule = nameTagModule;
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
            who.customizePlayer(
                    playerManager.getBasementPlayer(who.getName()).getStreamName(),
                    Skin.EMPTY,
                    streamer
            );
            nameTagModule.getTeamUtils().updateFakeTeam(streamer);
        }
        nameTagModule.getTeamUtils().updateFakeTeam(who);
    }

    @Override
    public void sendPackets(List<Player> players, Player streamer, boolean enable) {
        BasementPlayer streamerPlayer = playerManager.getBasementPlayer(streamer.getName());
        for (Player who : players) {
            if (who.equals(streamer)) continue;
            BasementPlayer basementPlayer = playerManager.getBasementPlayer(who.getName());
            if(basementPlayer.isInStreamMode()) continue;
            streamer.customizePlayer(
                    enable ? basementPlayer.getStreamName() : who.getSafeFakeName(),
                    enable ? STREAM : who.getSafeFakeSkin(),
                    who
            );
            who.customizePlayer(
                    enable ? streamerPlayer.getStreamName() : streamer.getSafeFakeName(),
                    enable ? Skin.EMPTY : streamer.getSafeFakeSkin(),
                    streamer
            );
            nameTagModule.getTeamUtils().updateFakeTeam(who);
        }
        nameTagModule.getTeamUtils().updateFakeTeam(streamer);
    }

}
