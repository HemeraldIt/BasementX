package it.mineblock.basementx.api.velocity.locale;

import it.mineblock.basementx.api.locale.Locale;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public interface ComponentLocale extends Locale {

    @Override
    default Component getComponent(String path) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(Locale.super.getText(path));
    }
}
