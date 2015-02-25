package org.apache.cassandra.logger.configuration;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;

public class Configuration {

    private String logKeyspace;
    private String logColumnFamily;    
    private Set<String> keyspacesToLog;
    private LoggingMode loggingMode;
        
    @SuppressWarnings("UnusedDeclaration")
    public Configuration() throws IOException {
        this("logger.properties");
    }
    
    public Configuration(String propertiesFileName) throws IOException {
        Properties properties = PropertiesLoader.loadFromFile(propertiesFileName);
        
        logKeyspace = properties.getProperty("logKeyspace", "LOGGER");
        logColumnFamily = properties.getProperty("logColumnFamily", "LOG");

        loggingMode = getLoggingModeOrDefault(properties.getProperty("loggingMode"), LoggingMode.ALL_KEYSPACES);

        keyspacesToLog = Collections.unmodifiableSet(
                splitByCommaStrippingWhitespaceAndRemovingDuplicates(properties.getProperty("keyspacesToLog")));
    }

    public String getLogKeyspace() {
        return logKeyspace;
    }

    public String getLogColumnFamily() {
        return logColumnFamily;
    }

    public Set<String> getKeyspacesToLog() {
        return keyspacesToLog;
    }
    
    public LoggingMode getLoggingMode() {
        return loggingMode;
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
}