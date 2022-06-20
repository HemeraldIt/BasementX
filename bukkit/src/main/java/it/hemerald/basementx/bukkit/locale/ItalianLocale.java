package it.hemerald.basementx.bukkit.locale;

import it.hemerlad.basementx.api.bukkit.locale.ColoredLocale;
import it.hemerlad.basementx.api.yaml.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class ItalianLocale implements ColoredLocale {

    private final Configuration config;

    public ItalianLocale(JavaPlugin plugin) {
        config = new Configuration(new File(plugin.getDataFolder(), "it.yml"), getResource("it.yml"));
        try {
            config.autoload();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getLanguage() {
        return "it";
    }

    @Override
    public Configuration getConfig() {
        return config;
    }

    @Override
    public String getContext() {
        return "basement";
    }
}
