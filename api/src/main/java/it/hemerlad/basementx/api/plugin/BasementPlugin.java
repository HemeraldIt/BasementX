package it.hemerlad.basementx.api.plugin;

import it.hemerlad.basementx.api.Basement;

import java.io.File;

public interface BasementPlugin {

    /**
     * Gets the basement instance
     * @return basement instance
     */
    Basement getBasement();

    /**
     * Gets the config file
     * @return config file
     */
    File getConfig();
}
