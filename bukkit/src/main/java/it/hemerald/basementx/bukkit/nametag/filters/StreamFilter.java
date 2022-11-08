package it.hemerald.basementx.bukkit.nametag.filters;

import it.hemerald.basementx.api.bukkit.BasementBukkit;
import it.hemerald.basementx.api.bukkit.nametag.filters.NameTagFilter;
import it.hemerald.basementx.api.player.PlayerManager;
import it.hemerald.basementx.bukkit.player.BukkitBasementPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Skin;
import org.bukkit.entity.Player;

public class StreamFilter extends NameTagFilter {

    private final PlayerManager<BukkitBasementPlayer> playerManager;

    public StreamFilter(BasementBukkit basement) {
        super(basement, "");
        this.playerManager = basement.getPlayerManager();
    }

    @Override
    public boolean test(Player player) {
        return basement.getStreamMode().isEnabled() && basement.getPlayerManager().getStreamers().size() > 0;
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
                    playerManager.getStreamers().parallelStream().map(BukkitBasementPlayer::getPlayer).toArray(Player[]::new));
        }
    }
}
