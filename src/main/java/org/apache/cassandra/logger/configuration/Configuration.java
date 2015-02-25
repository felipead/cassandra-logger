package org.apache.cassandra.logger.configuration;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class Configuration {

    private static final String PROPERTIES_FILE_NAME = "LogTrigger.properties";
    private static final Properties PROPERTIES = PropertiesLoader.loadFromFile(PROPERTIES_FILE_NAME);

    private String logKeyspace;
    private String logColumnFamily;    
    private List<String> keyspacesToLog;
    private LoggingMode loggingMode;
        
    public Configuration() {
        logKeyspace = PROPERTIES.getProperty("logKeyspace", "LOGGER");
        logColumnFamily = PROPERTIES.getProperty("logColumnFamily", "LOG");

        loggingMode = getLoggingModeOrDefault(PROPERTIES.getProperty("loggingMode"), LoggingMode.LOG_ALL_KEYSPACES);
        
        keyspacesToLog = Collections.unmodifiableList(
                splitByCommaStrippingWhitespace(PROPERTIES.getProperty("keyspacesToLog")));
    }

    public String getLogKeyspace() {
        return logKeyspace;
    }

    public String getLogColumnFamily() {
        return logColumnFamily;
    }

    public List<String> getKeyspacesToLog() {
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
    
    private static List<String> splitByCommaStrippingWhitespace(String string) {
        String[] tokens = StringUtils.split(string, ",");
        List<String> stripedTokens = new ArrayList<>(tokens.length);
        for (String token : tokens) {
            token = StringUtils.strip(token);
            if (!token.isEmpty()) {
                stripedTokens.add(token);
            }
        }
        return stripedTokens;
    }
}