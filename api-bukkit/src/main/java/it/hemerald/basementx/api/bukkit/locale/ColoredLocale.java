package it.hemerald.basementx.api.bukkit.locale;

import it.hemerald.basementx.api.locale.Locale;
import it.hemerald.basementx.api.bukkit.chat.Colorizer;

public interface ColoredLocale extends Locale {

    @Override
    default String getText(String path) {
        return Colorizer.colorize(Locale.super.getText(path));
    }
}
