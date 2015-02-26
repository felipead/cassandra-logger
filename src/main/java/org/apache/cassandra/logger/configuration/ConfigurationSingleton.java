package org.apache.cassandra.logger.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ConfigurationSingleton {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationSingleton.class);
    private static final String USER_SETTINGS_FILE = "logger.properties";
    private static final String DEFAULT_SETTINGS_FILE = "default.properties";
    
    private static Configuration instance;
    
    public static Configuration getInstance() {
        if (instance == null) {
            instance = loadConfiguration();
        }
        return instance;
    }

    private static Configuration loadConfiguration() {
        try {
            Configuration configuration = ConfigurationLoader.load(USER_SETTINGS_FILE);
            LOGGER.info(String.format("Loaded settings from file %s.", USER_SETTINGS_FILE));
            return configuration;
        } catch (IOException e) {
            LOGGER.warn(String.format("Settings file %s not found at the classpath.", USER_SETTINGS_FILE));
        }
        
        LOGGER.info("Loading default settings.");
        try {
            return ConfigurationLoader.load(DEFAULT_SETTINGS_FILE);
        } catch (IOException e) {
            LOGGER.error(String.format("Could not load default settings from %s. Abort.", DEFAULT_SETTINGS_FILE));
            throw new RuntimeException(e);
        }
    }
}