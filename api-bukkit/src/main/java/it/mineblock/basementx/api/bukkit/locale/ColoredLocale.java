package it.mineblock.basementx.api.bukkit.locale;

import it.mineblock.basementx.api.bukkit.chat.Colorizer;
import it.mineblock.basementx.api.locale.Locale;

public interface ColoredLocale extends Locale {

    @Override
    default String getText(String path) {
        return Colorizer.colorize(Locale.super.getText(path));
    }
}
