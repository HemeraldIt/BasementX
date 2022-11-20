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
        String whoFakeName = nameTagModule.getAdapter().getFakePrefix(who) + playerManager.getBasementPlayer(who.getName()).getStreamName();
        for (Player streamer : streamers) {
            if (streamer == who) continue;
            streamer.customizePlayer(
                    whoFakeName,
                    STREAM,
                    who
            );
            who.customizePlayer(
                    nameTagModule.getAdapter().getFakePrefix(streamer) + playerManager.getBasementPlayer(streamer.getName()).getStreamName(),
                    Skin.EMPTY,
                    streamer
            );
            nameTagModule.update(streamer);
        }
        nameTagModule.update(who);
    }

    @Override
    public void sendPackets(List<Player> players, Player streamer, boolean enable) {
        BasementPlayer streamerPlayer = playerManager.getBasementPlayer(streamer.getName());
        String streamName = nameTagModule.getAdapter().getFakePrefix(streamer) + streamerPlayer.getStreamName();
        for (Player who : players) {
            if (who.equals(streamer)) continue;
            BasementPlayer basementPlayer = playerManager.getBasementPlayer(who.getName());
            if(basementPlayer.isInStreamMode()) {
                streamer.hidePlayer(who);
                streamer.showPlayer(who);
                who.hidePlayer(streamer);
                who.showPlayer(streamer);
            } else {
                streamer.customizePlayer(
                        enable ? nameTagModule.getAdapter().getFakePrefix(who) + basementPlayer.getStreamName() : who.getSafeFakeName(),
                        enable ? STREAM : who.getSafeFakeSkin(),
                        who
                );
                who.customizePlayer(
                        enable ? streamName : streamer.getSafeFakeName(),
                        enable ? Skin.EMPTY : streamer.getSafeFakeSkin(),
                        streamer
                );
            }
            nameTagModule.update(who);
        }
        nameTagModule.update(streamer);
    }

}
