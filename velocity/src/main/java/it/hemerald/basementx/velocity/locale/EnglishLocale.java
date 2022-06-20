package it.hemerald.basementx.velocity.locale;

import it.hemerlad.basementx.api.velocity.locale.ComponentLocale;
import it.hemerlad.basementx.api.yaml.Configuration;

import java.io.File;
import java.io.IOException;

public class EnglishLocale implements ComponentLocale {

    private final Configuration config;

    public EnglishLocale(File folder) {
        config = new Configuration(new File(folder, "en_us.yml"), getResource("en_us.yml"));
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
