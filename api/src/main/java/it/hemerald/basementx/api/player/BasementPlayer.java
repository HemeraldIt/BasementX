package it.hemerald.basementx.api.player;

import it.hemerald.basementx.api.player.version.MinecraftVersion;
import it.hemerald.basementx.api.locale.Locale;

import java.util.Optional;

public interface BasementPlayer {

    /**
     * Gets the player name
     * @return player name
     */
    String getName();

    /**
     * Gets the player stream name
     * @return player stream name
     */
    String getStreamName();

    /**
     * Turn on or off the stream mode
     * @param enabled true if enable stream mode, false otherwise
     */
    void streamMode(boolean enabled);

    /**
     * Gets if the player is in stream mode
     * @return true if the player is in stream mode
     */
    boolean isInStreamMode();

    /**
     * Gets the player language
     * @return player language
     */
    String getLanguage();

    /**
     * Gets the player {@link Locale}
     * @param context the name of the plugin that use the Locale
     * @return the locale of the player
     */
    Optional<Locale> getLocale(String context);

    /**
     * Gets the minecraft version the player is using
     * @return the minecraft version of the player
     */
    MinecraftVersion getVersion();

    /**
     * Called when player is removed
     */
    public void remove();
}
