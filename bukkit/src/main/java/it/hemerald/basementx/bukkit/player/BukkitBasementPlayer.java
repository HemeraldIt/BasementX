package it.hemerald.basementx.bukkit.player;

import com.viaversion.viaversion.api.Via;
import it.hemerald.basementx.api.bukkit.BasementBukkit;
import it.hemerald.basementx.api.locale.Locale;
import it.hemerald.basementx.api.locale.LocaleManager;
import it.hemerald.basementx.api.player.BasementPlayer;
import it.hemerald.basementx.api.player.UserData;
import it.hemerald.basementx.api.player.version.MinecraftVersion;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class BukkitBasementPlayer implements BasementPlayer {

    private static final List<Integer> numbers = new ArrayList<>(IntStream.range(1, 1000).boxed().toList());

    private final Player player;
    private final LocaleManager localeManager;
    private final UserData userData;
    private final String streamName;
    private final Integer streamId;

    public BukkitBasementPlayer(Player player, BasementBukkit basement) {
        this.player = player;
        this.localeManager = basement.getLocaleManager();
        this.userData = basement.getUserData(player.getUniqueId());
        this.streamName = "Player" + (streamId = numbers.remove(0));
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public String getStreamName() {
        return streamName;
    }

    @Override
    public void streamMode(boolean enabled) {
        userData.setStreamMode(enabled);
    }

    @Override
    public boolean isInStreamMode() {
        return userData.getStreamMode();
    }

    @Override
    public String getLanguage() {
        return userData.getLanguage();
    }

    @Override
    public Optional<Locale> getLocale(String context) {
        return localeManager.getLocale(context, this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public MinecraftVersion getVersion() {
        return MinecraftVersion.byProtocolVersion(Via.getAPI().getPlayerVersion(player));
    }

    @Override
    public void remove() {
        numbers.add(streamId);
        numbers.sort(null);
    }
}
