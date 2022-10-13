package it.hemerald.basementx.api.bukkit.player.stream;

import it.hemerald.basementx.api.bukkit.BasementBukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public abstract class StreamMode {

    private boolean enabled;

    public boolean isEnabled() {
        return enabled;
    }

    public void toggle(boolean enabled) {
        this.enabled = enabled;
    }

    public abstract void sendPackets(JavaPlugin plugin, Player who, String streamName, Player... streamers);

    public abstract void sendPackets(BasementBukkit basement, List<Player> players, Player streamer);
}
