package org.apache.cassandra.logger.settings;

import org.apache.commons.lang3.StringUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SettingsLoader {

    private static final String DEFAULT_LOG_KEYSPACE = "logger";
    private static final String DEFAULT_LOG_TABLE = "log";
    
    public static Settings load(String fileName) throws IOException {
        Properties properties = loadPropertiesFromClassPath(fileName);
        
        Settings settings = new Settings();
        settings.setLogKeyspace(StringUtils.strip(properties.getProperty("logKeyspace", DEFAULT_LOG_KEYSPACE)));
        settings.setLogTable(StringUtils.strip(properties.getProperty("logTable", DEFAULT_LOG_TABLE)));
        return settings;
    }

    private static Properties loadPropertiesFromClassPath(String fileName) throws IOException {
        Properties properties = new Properties();
        
        try (InputStream stream = SettingsLoader.class.getClassLoader().getResourceAsStream(fileName)) {
            if (stream != null) {
                properties.load(stream);
            } else {
                throw new FileNotFoundException(fileName);
            }
        }

        return properties;
    }
}