package it.mineblock.basementx.config;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BasementConfig implements SettingsHolder {

    public static final Property<String> REDIS_HOST = newProperty("redis.host", "localhost");
    public static final Property<Integer> REDIS_PORT = newProperty("redis.port", 6379);
    public static final Property<String> REDIS_USERNAME = newProperty("redis.username", "default");
    public static final Property<String> REDIS_PASSWORD = newProperty("redis.password", "root");

    public static final Property<String> MARIA_HOST = newProperty("mysql.host", "mariadb");
    public static final Property<String> MARIA_USERNAME = newProperty("mysql.username", "maria");
    public static final Property<String> MARIA_PASSWORD = newProperty("mysql.password", "password");

}
