package it.hemerald.basementx.bukkit.player.stream;

import it.hemerald.basementx.api.bukkit.nametag.module.NameTagModule;
import it.hemerald.basementx.api.bukkit.player.stream.StreamMode;
import it.hemerald.basementx.api.player.BasementPlayer;
import it.hemerald.basementx.api.player.PlayerManager;
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
                    new org.bukkit.Skin(
                            "ewogICJ0aW1lc3RhbXAiIDogMTYwNjcwMzI3MDYxNSwKICAicHJvZmlsZUlkIiA6ICI5ZDEzZjcyMTcxM2E0N2U0OTAwZTMyZGVkNjBjNDY3MyIsCiAgInByb2ZpbGVOYW1lIiA6ICJUYWxvZGFvIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzgwY2JkZDRmNDU0ZjNlODM1OWJjYzJiNGNiODY5NTVmMGQ2ZDZjNThmOWNhNGQzYzBiNmQ5MzY0ZDEwMGE5YzYiCiAgICB9CiAgfQp9",
                            "SI+ogCPVvRuiufmaBYUPVTO+09lYRQsVLZvRC5M2IpUlRgpLT3wJLEkoX2NjqzUBG6/ScYMjKq5xNuCPfEERF5fkQ7vnV8grKhnJOV1ynljzemDZDRkO6cRR0ooON8DE98wsHwc3RNaZotr3Jz8Wogfpx1RlKEWoY9d8HqlfuG3iLmClEdr35k4Xj1RzXuQ2yVziuIip6Y4Fw61GBre6P70KfYO9S1/IZlW5fhGS+Pvc5I1LFrUVImR1zw1S/CT2+1f9aX/BKshnmrw/uLQyMkfPJSF3Ymf0tmxHb2JxjsvtsT8+lsysJt+07J9erBSlrhYL50Qt+vwiCyNP28w5Lr1Ya8EqdFqUT5OLKsUFSeBAPOiuUgUoGHD9C2T8lM8ntCaaW/Yl8BUFriWVUxMwUCgK1PLb3Fg1oMLebpIuKph5yfu7905XNa+fFwzjtAExcQkycB8tPTxs8UfobI2NnUhHLiBVTsThe2Lr5h+zeBFvypEHKiLewIu7CnY/wVkyRrmcD0Wg3QjCuKPysbQLQq5dEzglLO8zuX4TYIIfkHn/Ye/uo+vpmdbltNAQ3kIk4tOq5pVTq3ghceLC7y0c5gm/9j4k4iUPctpxRercDrkOlP3sTonQCFZvQyWTj643rYax7+KaXWb+t3k2+ZzTqkG6edcqkrfxTn6UB5ty2K8="
                    ),
                    who
            );
            who.customizePlayer(
                    playerManager.getBasementPlayer(streamer.getName()).getStreamName(),
                    org.bukkit.Skin.EMPTY,
                    streamer
            );
            nameTagModule.update(streamer);
        }
        nameTagModule.update(who);
    }

    @Override
    public void sendPackets(List<Player> players, Player streamer, boolean enable) {
        BasementPlayer streamerPlayer = playerManager.getBasementPlayer(streamer.getName());
        for (Player who : players) {
            if (who.equals(streamer)) continue;
            BasementPlayer basementPlayer = playerManager.getBasementPlayer(who.getName());
            if (basementPlayer.isInStreamMode()) {
                streamer.hidePlayer(who);
                streamer.showPlayer(who);
                who.hidePlayer(streamer);
                who.showPlayer(streamer);
            } else {
                streamer.customizePlayer(
                        enable ? basementPlayer.getStreamName() : who.getSafeFakeName(),
                        enable ? new org.bukkit.Skin(
                                "ewogICJ0aW1lc3RhbXAiIDogMTYwNjcwMzI3MDYxNSwKICAicHJvZmlsZUlkIiA6ICI5ZDEzZjcyMTcxM2E0N2U0OTAwZTMyZGVkNjBjNDY3MyIsCiAgInByb2ZpbGVOYW1lIiA6ICJUYWxvZGFvIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzgwY2JkZDRmNDU0ZjNlODM1OWJjYzJiNGNiODY5NTVmMGQ2ZDZjNThmOWNhNGQzYzBiNmQ5MzY0ZDEwMGE5YzYiCiAgICB9CiAgfQp9",
                                "SI+ogCPVvRuiufmaBYUPVTO+09lYRQsVLZvRC5M2IpUlRgpLT3wJLEkoX2NjqzUBG6/ScYMjKq5xNuCPfEERF5fkQ7vnV8grKhnJOV1ynljzemDZDRkO6cRR0ooON8DE98wsHwc3RNaZotr3Jz8Wogfpx1RlKEWoY9d8HqlfuG3iLmClEdr35k4Xj1RzXuQ2yVziuIip6Y4Fw61GBre6P70KfYO9S1/IZlW5fhGS+Pvc5I1LFrUVImR1zw1S/CT2+1f9aX/BKshnmrw/uLQyMkfPJSF3Ymf0tmxHb2JxjsvtsT8+lsysJt+07J9erBSlrhYL50Qt+vwiCyNP28w5Lr1Ya8EqdFqUT5OLKsUFSeBAPOiuUgUoGHD9C2T8lM8ntCaaW/Yl8BUFriWVUxMwUCgK1PLb3Fg1oMLebpIuKph5yfu7905XNa+fFwzjtAExcQkycB8tPTxs8UfobI2NnUhHLiBVTsThe2Lr5h+zeBFvypEHKiLewIu7CnY/wVkyRrmcD0Wg3QjCuKPysbQLQq5dEzglLO8zuX4TYIIfkHn/Ye/uo+vpmdbltNAQ3kIk4tOq5pVTq3ghceLC7y0c5gm/9j4k4iUPctpxRercDrkOlP3sTonQCFZvQyWTj643rYax7+KaXWb+t3k2+ZzTqkG6edcqkrfxTn6UB5ty2K8="
                        ) : who.getSafeFakeSkin(),
                        who
                );
                who.customizePlayer(
                        enable ? streamerPlayer.getStreamName() : streamer.getSafeFakeName(),
                        enable ? org.bukkit.Skin.EMPTY : streamer.getSafeFakeSkin(),
                        streamer
                );
            }
            nameTagModule.update(who);
        }
        nameTagModule.update(streamer);
    }

}
