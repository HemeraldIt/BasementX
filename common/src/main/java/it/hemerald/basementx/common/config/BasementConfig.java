package it.hemerald.basementx.common.config;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

import static ch.jalu.configme.properties.PropertyInitializer.newListProperty;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BasementConfig implements SettingsHolder {

    public static final Property<List<String>> REDIS_HOSTS = newListProperty("redis.hosts", "redis0", "redis1", "redis2");

    public static final Property<String> MARIA_HOST = newProperty("mysql.host", "mariadb");
    public static final Property<String> MARIA_USERNAME = newProperty("mysql.username", "maria");
    public static final Property<String> MARIA_PASSWORD = newProperty("mysql.password", "password");

}
