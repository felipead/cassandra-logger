package org.apache.cassandra.logger.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesLoader.class);
    
    public static Properties loadFromFile(String fileName) throws IOException {
        Properties properties = new Properties();
        
        try (InputStream stream = PropertiesLoader.class.getClassLoader().getResourceAsStream(fileName)) {
            if (stream != null) {
                properties.load(stream);
            } else {
                throw new FileNotFoundException(fileName);
            }
        } catch (Exception e) {
            LOGGER.error("could not load properties file: " + fileName, e);
            throw e;
        }
        
        LOGGER.info("loaded properties file: " + fileName);
        return properties;
    }
}