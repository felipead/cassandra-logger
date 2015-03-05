package com.felipead.cassandra.logger.settings;

import org.apache.commons.lang3.StringUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class SettingsLoader {

    private static final String DEFAULT_LOG_KEYSPACE = "logger";
    private static final String DEFAULT_LOG_TABLE = "log";
    
    public static Settings load(String fileName) throws IOException {
        Properties properties = loadPropertiesFromClassPath(fileName);
        
        Settings settings = new Settings();
        settings.setLogKeyspace(normalize(properties.getProperty("logKeyspace", DEFAULT_LOG_KEYSPACE)));
        settings.setLogTable(normalize(properties.getProperty("logTable", DEFAULT_LOG_TABLE)));
        settings.setIgnoreColumns(Collections.unmodifiableSet(
                splitByComma(properties.getProperty("ignoreColumns"))));
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

    private static Set<String> splitByComma(String string) {
        String[] tokens = StringUtils.split(string, ",");
        Set<String> normalizedTokens = new HashSet<>();
        if (tokens != null) {
            for (String token : tokens) {
                token = normalize(token);
                if (!token.isEmpty()) {
                    normalizedTokens.add(token);
                }
            }
        }
        return normalizedTokens;
    }
    
    private static String normalize(String value) {
        return StringUtils.strip(value);

    }
}