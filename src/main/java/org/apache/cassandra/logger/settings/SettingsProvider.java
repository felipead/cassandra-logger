package org.apache.cassandra.logger.settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SettingsProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsProvider.class);
    private static final String USER_SETTINGS_FILE = "logger.properties";
    private static final String DEFAULT_SETTINGS_FILE = "default.properties";
    
    private static Settings instance;
    
    public static Settings getSettings() {
        if (instance == null) {
            instance = loadConfiguration();
        }
        return instance;
    }

    private static Settings loadConfiguration() {
        try {
            Settings settings = SettingsLoader.load(USER_SETTINGS_FILE);
            LOGGER.info("Loaded settings from file {}.", USER_SETTINGS_FILE);
            return settings;
        } catch (IOException e) {
            LOGGER.warn("Settings file {} not found at the classpath.", USER_SETTINGS_FILE);
        }
        
        LOGGER.info("Loading default settings.");
        try {
            return SettingsLoader.load(DEFAULT_SETTINGS_FILE);
        } catch (IOException e) {
            LOGGER.error("Could not load default settings from {}. Abort.", DEFAULT_SETTINGS_FILE);
            throw new RuntimeException(e);
        }
    }
}