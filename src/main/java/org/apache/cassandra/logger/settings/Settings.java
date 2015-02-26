package org.apache.cassandra.logger.settings;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Settings {

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
    
    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("logKeyspace", getLogKeyspace());
        builder.append("logColumnFamily", getLogColumnFamily());
        return builder.build();
    }
}