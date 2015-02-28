package com.felipead.cassandra.logger.settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;

public class SettingsProvider {

    private static final String USER_SETTINGS_FILE = "cassandra-logger.properties";
    private static final String DEFAULT_SETTINGS_FILE = "default.properties";
    private static final Logger logger = LoggerFactory.getLogger(SettingsProvider.class);

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
            logger.info("Loaded settings from file {}.", USER_SETTINGS_FILE);
            return settings;
        } catch (FileNotFoundException e) {
            logger.info("Settings file {} not found at the classpath. Loading default settings.", USER_SETTINGS_FILE);
        } catch (IOException e) {
            logger.error("Could not load settings from {}. IOException: {}", USER_SETTINGS_FILE, e.getMessage());
            throw new RuntimeException(e);
        }
        
        try {
            return SettingsLoader.load(DEFAULT_SETTINGS_FILE);
        } catch (IOException e) {
            logger.error("Could not load default settings from {}. Abort.", DEFAULT_SETTINGS_FILE);
            throw new RuntimeException(e);
        }
    }
}