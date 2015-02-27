package org.apache.cassandra.logger.settings;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Settings {

    private String logKeyspace;
    private String logTable;

    public String getLogKeyspace() {
        return logKeyspace;
    }

    public void setLogKeyspace(String logKeyspace) {
        this.logKeyspace = logKeyspace;
    }

    public String getLogTable() {
        return logTable;
    }

    public void setLogTable(String logTable) {
        this.logTable = logTable;
    }
    
    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("logKeyspace", getLogKeyspace());
        builder.append("logTable", getLogTable());
        return builder.build();
    }
}