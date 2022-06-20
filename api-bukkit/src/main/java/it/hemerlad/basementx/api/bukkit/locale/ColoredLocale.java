package it.hemerlad.basementx.api.bukkit.locale;

import it.hemerlad.basementx.api.bukkit.chat.Colorizer;
import it.hemerlad.basementx.api.locale.Locale;

public interface ColoredLocale extends Locale {

    @Override
    default String getText(String path) {
        return Colorizer.colorize(Locale.super.getText(path));
    }
}
