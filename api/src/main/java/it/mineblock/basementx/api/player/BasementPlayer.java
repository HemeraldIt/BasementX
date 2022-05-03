package it.mineblock.basementx.api.player;

import it.mineblock.basementx.api.locale.Locale;
import it.mineblock.basementx.api.player.version.MinecraftVersion;

import java.util.Optional;

public interface BasementPlayer {

    /**
     * Gets the player name
     * @return player name
     */
    String getName();

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
}
