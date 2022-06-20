package it.hemerald.basementx.velocity.locale;

import it.hemerlad.basementx.api.velocity.locale.ComponentLocale;
import it.hemerlad.basementx.api.yaml.Configuration;

import java.io.File;
import java.io.IOException;

public class ItalianLocale implements ComponentLocale {

    private final Configuration config;

    public ItalianLocale(File folder) {
        config = new Configuration(new File(folder, "it.yml"), getResource("it.yml"));
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
