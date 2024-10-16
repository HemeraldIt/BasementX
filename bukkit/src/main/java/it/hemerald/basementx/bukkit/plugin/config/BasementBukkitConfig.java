package it.hemerald.basementx.bukkit.plugin.config;

import ch.jalu.configme.properties.Property;
import it.hemerald.basementx.common.config.BasementConfig;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BasementBukkitConfig extends BasementConfig {

    public static final Property<Boolean> HOSTABLE = newProperty("hostable", false);

    public static final Property<Boolean> COINS_PROVIDER = newProperty("coins-provider.enabled", false);

    public static final Property<Boolean> STREAM_MODE = newProperty("stream-mode.enabled", false);

    public static final Property<Boolean> STAFF_MODE = newProperty("modules.staff-mode.enabled", true);
    public static final Property<Boolean> NAME_TAG = newProperty("modules.name-tag.enabled", true);
    public static final Property<Boolean> TAGS = newProperty("modules.name-tag.tags", true);
    public static final Property<Boolean> SUB_FILTER = newProperty("modules.name-tag.filters.sub", true);
    public static final Property<Boolean> DISGUISE = newProperty("modules.disguise.enabled", true);
}
