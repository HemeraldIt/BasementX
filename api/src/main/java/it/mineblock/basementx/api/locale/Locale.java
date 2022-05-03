package it.mineblock.basementx.api.locale;

import it.mineblock.basementx.api.yaml.Configuration;
import net.kyori.adventure.text.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public interface Locale {

    /**
     * Gets the language
     * @return the language
     */
    String getLanguage();

    /**
     * Gets the configuration object (YAML)
     * @return the configuration object {@link Configuration}
     */
    Configuration getConfig();

    /**
     * Gets text string from config
     * @param path the element path in the config
     * @return the text string
     */
    default String getText(String path) {
        return getConfig().getString(path);
    }

    /**
     * Gets text component from config
     * @param path the element path in the config
     * @return the text component
     */
    default Component getComponent(String path) {
        return Component.text(getConfig().getString(path));
    }
    /**
     * Gets the context of the Locale (plugin name)
     * @return the context of the Locale
     */
    String getContext();

    default InputStream getResource(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("Filename cannot be null");
        } else {
            try {
                URL url = this.getClass().getClassLoader().getResource(filename);
                if (url == null) {
                    return null;
                } else {
                    URLConnection connection = url.openConnection();
                    connection.setUseCaches(false);
                    return connection.getInputStream();
                }
            } catch (IOException var4) {
                return null;
            }
        }
    }
}
