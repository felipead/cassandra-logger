package com.felipead.cassandra.logger.settings;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Set;

public class Settings {

    private String logKeyspace;
    private String logTable;
    private Set<String> ignoreColumns;

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

    public Set<String> getIgnoreColumns() {
        return ignoreColumns;
    }

    public void setIgnoreColumns(Set<String> ignoreColumns) {
        this.ignoreColumns = ignoreColumns;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("logKeyspace", getLogKeyspace());
        builder.append("logTable", getLogTable());
        builder.append("ignoreColumns", getIgnoreColumns());
        return builder.build();
    }
}