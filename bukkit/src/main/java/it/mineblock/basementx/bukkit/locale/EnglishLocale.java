package it.mineblock.basementx.bukkit.locale;

import it.mineblock.basementx.api.bukkit.locale.ColoredLocale;
import it.mineblock.basementx.api.yaml.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class EnglishLocale implements ColoredLocale {

    private final Configuration config;

    public EnglishLocale(JavaPlugin plugin) {
        config = new Configuration(new File(plugin.getDataFolder(), "en_us.yml"), getResource("en_us.yml"));
        try {
            config.autoload();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getLanguage() {
        return "en-us";
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
