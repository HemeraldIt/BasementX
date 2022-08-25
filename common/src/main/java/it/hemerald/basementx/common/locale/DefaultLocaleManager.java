package it.hemerald.basementx.common.locale;

import it.hemerald.basementx.api.locale.Locale;
import it.hemerald.basementx.api.locale.LocaleManager;
import it.hemerald.basementx.api.player.BasementPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DefaultLocaleManager implements LocaleManager {

    private final Map<String, Map<String, Locale>> localesByContext = new HashMap<>();

    public DefaultLocaleManager() {

    }

    @Override
    public Optional<Locale> getLocale(String context, BasementPlayer player) {
        Map<String, Locale> locales = localesByContext.get(context);
        if(locales == null) return Optional.empty();
        return Optional.ofNullable(locales.get(player.getLanguage()));
    }

    @Override
    public void addLocale(String context, Locale locale) {
        Map<String, Locale> locales = localesByContext.computeIfAbsent(context, k -> new HashMap<>());
        locales.put(locale.getLanguage(), locale);
    }

    @Override
    public void removeLocale(String context, String language) {
        Map<String, Locale> locales = localesByContext.computeIfPresent(context, (k, map) -> {
            map.remove(language);
            return map;
        });
        if(locales != null && locales.isEmpty())
            localesByContext.remove(context);
    }
}
