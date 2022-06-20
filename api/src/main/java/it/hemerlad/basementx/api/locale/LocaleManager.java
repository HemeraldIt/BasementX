package it.hemerlad.basementx.api.locale;

import it.hemerlad.basementx.api.player.BasementPlayer;

import java.util.Optional;

public interface LocaleManager {

    /**
     * Gets the Locale of the player from its options
     * @param context the plugin name
     * @param player the player name
     * @return the locale {@link Locale}
     */
    Optional<Locale> getLocale(String context, BasementPlayer player);

    /**
     * Add a new Locale {@link Locale}
     * @param context the plugin name
     * @param locale the Locale object
     */
    void addLocale(String context, Locale locale);

    /**
     * Remove a Locale {@link Locale}
     * @param context the plugin name
     * @param language the language of Locale
     */
    void removeLocale(String context, String language);
}
