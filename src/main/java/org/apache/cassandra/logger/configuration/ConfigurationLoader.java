package org.apache.cassandra.logger.configuration;

import org.apache.commons.lang3.StringUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigurationLoader {

    private static final String DEFAULT_LOG_KEYSPACE = "logger";
    private static final String DEFAULT_LOG_COLUMN_FAMILY = "log";
    
    public static Configuration load(String fileName) throws IOException {
        Properties properties = loadPropertiesFromClassPath(fileName);
        
        Configuration config = new Configuration();
        config.setLogKeyspace(StringUtils.strip(properties.getProperty("logKeyspace", DEFAULT_LOG_KEYSPACE)));
        config.setLogColumnFamily(StringUtils.strip(properties.getProperty("logColumnFamily", DEFAULT_LOG_COLUMN_FAMILY)));
        return config;
    }

    private static Properties loadPropertiesFromClassPath(String fileName) throws IOException {
        Properties properties = new Properties();
        
        try (InputStream stream = ConfigurationLoader.class.getClassLoader().getResourceAsStream(fileName)) {
            if (stream != null) {
                properties.load(stream);
            } else {
                throw new FileNotFoundException(fileName);
            }
        }

        return properties;
    }
}