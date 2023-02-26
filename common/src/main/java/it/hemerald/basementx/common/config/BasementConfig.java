package it.hemerald.basementx.common.config;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BasementConfig implements SettingsHolder {

    public static final Property<String> REDIS_HOST = newProperty("redis.hosts", "redis0");

    public static final Property<String> MARIA_HOST = newProperty("mysql.host", "mariadb");
    public static final Property<String> MARIA_USERNAME = newProperty("mysql.username", "maria");
    public static final Property<String> MARIA_PASSWORD = newProperty("mysql.password", "password");

}
