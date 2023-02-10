package it.hemerald.basementx.api.plugin;

import it.hemerald.basementx.api.Basement;

import java.io.File;

public interface BasementPlugin {

    /**
     * Gets the basement instance
     *
     * @return basement instance
     */
    Basement getBasement();

    /**
     * Gets the config file
     *
     * @return config file
     */
    File getConfig();
}
