package org.apache.cassandra.logger.configuration;

import java.util.*;

public class Configuration {

    private String logKeyspace;
    private String logColumnFamily;    
    private Set<String> keyspacesToLog;
    private LoggingMode loggingMode;

    public String getLogKeyspace() {
        return logKeyspace;
    }

    public void setLogKeyspace(String logKeyspace) {
        this.logKeyspace = logKeyspace;
    }

    public String getLogColumnFamily() {
        return logColumnFamily;
    }

    public void setLogColumnFamily(String logColumnFamily) {
        this.logColumnFamily = logColumnFamily;
    }

    public Set<String> getKeyspacesToLog() {
        return keyspacesToLog;
    }

    public void setKeyspacesToLog(Set<String> keyspacesToLog) {
        this.keyspacesToLog = keyspacesToLog;
    }

    public LoggingMode getLoggingMode() {
        return loggingMode;
    }

    public void setLoggingMode(LoggingMode loggingMode) {
        this.loggingMode = loggingMode;
    }
}