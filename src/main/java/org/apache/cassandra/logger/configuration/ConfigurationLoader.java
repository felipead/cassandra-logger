package org.apache.cassandra.logger.configuration;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class ConfigurationLoader {

    public static Configuration load(String propertiesFileName) throws IOException {
        Properties properties = loadPropertiesFromFile(propertiesFileName);

        Configuration config = new Configuration();
        config.setLogKeyspace(StringUtils.strip(properties.getProperty("logKeyspace", "LOGGER")));
        config.setLogColumnFamily(StringUtils.strip(properties.getProperty("logColumnFamily", "LOG")));
        config.setLoggingMode(getLoggingModeOrDefault(properties.getProperty("loggingMode"), LoggingMode.ALL_KEYSPACES));
        config.setKeyspacesToLog(Collections.unmodifiableSet(
                splitByCommaStrippingWhitespaceAndRemovingDuplicates(properties.getProperty("keyspacesToLog"))));
        
        return config;
    }

    private static LoggingMode getLoggingModeOrDefault(String string, LoggingMode defaultLoggingMode) {
        LoggingMode loggingMode = EnumUtils.getEnum(LoggingMode.class, string);
        if (loggingMode == null) {
            return defaultLoggingMode;
        } else {
            return loggingMode;
        }
    }

    private static Set<String> splitByCommaStrippingWhitespaceAndRemovingDuplicates(String string) {
        String[] tokens = StringUtils.split(string, ",");
        Set<String> stripedTokens = new HashSet<>();
        if (tokens != null) {
            for (String token : tokens) {
                token = StringUtils.strip(token);
                if (!token.isEmpty()) {
                    stripedTokens.add(token);
                }
            }
        }
        return stripedTokens;
    }

    private static Properties loadPropertiesFromFile(String fileName) throws IOException {
        Properties properties = new Properties();

        try (InputStream stream = new FileInputStream(fileName)) {
            properties.load(stream);
        }

        return properties;
    }
}