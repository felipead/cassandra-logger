package org.apache.cassandra.logger.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;

public class ConfigurationSingleton {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationSingleton.class);
    private static final String CONFIGURATION_FILE_NAME = "logger.properties";
    
    private static Configuration instance;
    
    public static Configuration getInstance() {
        if (instance == null) {
            instance = loadConfigurationOrGetDefault();
        }
        return instance;
    }

    private static Configuration loadConfigurationOrGetDefault() {
        Path path = Paths.get(CONFIGURATION_FILE_NAME);
        if (Files.exists(path)) {
            LOGGER.info("loading configuration from " + path);
            try {
                return ConfigurationLoader.load(path.toAbsolutePath().toString());
            } catch (IOException e) {
                LOGGER.error("could not load configuration from " + path);
                throw new RuntimeException(e);
            }
        } else {
            LOGGER.info("configuration file not found, using defaults.");
            return buildDefaultConfiguration();
        }
    }

    private static Configuration buildDefaultConfiguration() {
        Configuration configuration = new Configuration();
        configuration.setLoggingMode(LoggingMode.ALL_KEYSPACES);
        configuration.setLogKeyspace("logger");
        configuration.setLogColumnFamily("log");
        configuration.setKeyspacesToLog(new HashSet<String>());
        return configuration;
    }
}