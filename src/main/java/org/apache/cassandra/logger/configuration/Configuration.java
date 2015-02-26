package org.apache.cassandra.logger.configuration;

public class Configuration {

    private String logKeyspace;
    private String logColumnFamily;    

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
}