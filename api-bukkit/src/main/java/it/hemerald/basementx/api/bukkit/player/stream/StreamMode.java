package it.hemerald.basementx.api.bukkit.player.stream;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public interface StreamMode {

    void sendPackets(JavaPlugin plugin, Player who, String streamName, Player... players);
}
