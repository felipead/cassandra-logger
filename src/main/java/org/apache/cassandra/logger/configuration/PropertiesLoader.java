package org.apache.cassandra.logger.configuration;

import org.apache.cassandra.io.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesLoader.class);
    
    public static Properties loadFromFile(String fileName) {
        Properties properties = new Properties();
        InputStream stream = Configuration.class.getClassLoader().getResourceAsStream(fileName);
        try {
            properties.load(stream);
        } catch (Exception e) {
            LOGGER.error("could not load properties file: " + fileName, e);
            throw new RuntimeException(e);
        } finally {
            FileUtils.closeQuietly(stream);
        }
        LOGGER.info("loaded properties file: " + fileName);
        return properties;
    }
}
