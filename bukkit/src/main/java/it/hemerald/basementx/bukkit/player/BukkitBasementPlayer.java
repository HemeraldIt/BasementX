package it.hemerald.basementx.bukkit.player;

import it.hemerald.basementx.api.bukkit.BasementBukkit;
import it.hemerald.basementx.api.bukkit.disguise.module.DisguiseModule;
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
    private final DisguiseModule disguiseModule;
    private final UserData userData;
    private final Integer streamId;
    private final MinecraftVersion version;
    private String streamName;

    public BukkitBasementPlayer(Player player, BasementBukkit basement) {
        this.player = player;
        this.localeManager = basement.getLocaleManager();
        this.disguiseModule = basement.getDisguiseModule();
        this.userData = basement.getUserData(player.getUniqueId());
        this.streamName = "Player" + (streamId = numbers.remove(0));
        this.version = MinecraftVersion.byProtocolVersion(basement.getRemoteVelocityService().playerVersion(player.getUniqueId()));
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
    public void setStreamName(String streamName) {
        this.streamName = streamName == null ? "Player" + streamId : streamName;
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
    public void disguise(boolean enabled) {
        if (enabled) disguiseModule.disguise(player);
        else disguiseModule.undisguise(player);
    }

    @Override
    public String getLanguage() {
        return userData.getLanguage();
    }

    @Override
    public Optional<Locale> getLocale(String context) {
        return localeManager.getLocale(context, this);
    }

    @Override
    public MinecraftVersion getVersion() {
        return version;
    }

    @Override
    public void remove() {
        numbers.add(streamId);
        numbers.sort(null);
    }

    @Override
    public UserData getUserData() {
        return userData;
    }

    public Player getPlayer() {
        return player;
    }
}
