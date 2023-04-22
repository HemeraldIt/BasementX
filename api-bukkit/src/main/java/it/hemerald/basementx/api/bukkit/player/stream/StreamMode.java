package it.hemerald.basementx.api.bukkit.player.stream;

import it.hemerald.basementx.api.player.BasementPlayer;
import it.hemerald.basementx.api.player.PlayerManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.List;

@RequiredArgsConstructor
public abstract class StreamMode {

    protected final PlayerManager<BasementPlayer> playerManager;
    private boolean enabled;

    public boolean isEnabled() {
        return enabled;
    }

    public void toggle(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * switch the player parameter to a
     * disguised version changing for all the
     * 'streamers' the name and skin
     *
     * @param who
     * @param streamers
     */
    public abstract void sendPackets(Player who, Player... streamers);

    /**
     * switch all the players out of the list parameter
     * to a disguised version changing only for the 'streamer'
     * each name and skin
     *
     * @param players
     * @param streamer
     * @param enable
     */
    public abstract void sendPackets(List<Player> players, Player streamer, boolean enable);
}
