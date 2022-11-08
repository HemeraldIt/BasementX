package it.hemerald.basementx.api.bukkit.nametag.filters;

import it.hemerald.basementx.api.bukkit.BasementBukkit;
import it.hemerald.basementx.api.player.BasementPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Skin;
import org.bukkit.entity.Player;

public class StreamFilter extends NameTagFilter {

    public StreamFilter(BasementBukkit basement) {
        super(basement, "");
    }

    @Override
    public boolean test(Player player) {
        return basement.getStreamMode().isEnabled() && basement.getPlayerManager().getBasementPlayers().parallelStream().anyMatch(BasementPlayer::isInStreamMode);
    }

    @Override
    public boolean ignoreMe(Player player) {
        return basement.getPlayerManager().getBasementPlayer(player.getName()).isInStreamMode();
    }

    @Override
    public void apply(Player player, boolean ignoreMe) {
        if(ignoreMe) {
            for (Player who : Bukkit.getOnlinePlayers()) {
                if(!ignoreMe(who))
                    who.customizePlayer(basement.getDisguiseModule().getRandomUsername(), Skin.EMPTY, player);
            }
        } else {
            basement.getStreamMode().sendPackets(
                    player,
                    basement.getPlayerManager().getBasementPlayers().parallelStream().filter(BasementPlayer::isInStreamMode)
                            .map(bp -> Bukkit.getPlayer(bp.getName())).toArray(Player[]::new));
        }
    }
}
