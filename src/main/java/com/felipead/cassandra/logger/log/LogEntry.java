package com.felipead.cassandra.logger.log;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.*;

public class LogEntry {
    
    private String loggedKeyspace;
    private String loggedTable;
    private String loggedKey;
    private Set<String> updatedColumns;
    private Operation operation;
    private Date time;

    public String getLoggedKeyspace() {
        return loggedKeyspace;
    }
    
    public void setLoggedKeyspace(String loggedKeyspace) {
        this.loggedKeyspace = loggedKeyspace;
    }

    public String getLoggedTable() {
        return loggedTable;
    }
    
    public void setLoggedTable(String loggedTable) {
        this.loggedTable = loggedTable;
    }

    public String getLoggedKey() {
        return loggedKey;
    }
    
    public void setLoggedKey(String loggedKey) {
        this.loggedKey = loggedKey;
    }

    public Operation getOperation() {
        return operation;
    }
    
    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Set<String> getUpdatedColumns() {
        return updatedColumns;
    }

    public void setUpdatedColumns(Set<String> updatedColumns) {
        this.updatedColumns = updatedColumns;
    }
    
    public void addUpdatedColumn(String updatedColumn) {
        if (this.updatedColumns == null) {
            this.updatedColumns = new HashSet<>();
        }
        this.updatedColumns.add(updatedColumn);
    }
    
    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("time", getTime());
        builder.append("loggedKeyspace", getLoggedKeyspace());
        builder.append("loggedTable", getLoggedTable());
        builder.append("loggedKey", getLoggedKey());
        builder.append("operation", getOperation());
        builder.append("updatedColumns", getUpdatedColumns());
        return builder.build();
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof LogEntry)) {
            return false;
        }
        
        LogEntry other = (LogEntry)o;
        return Objects.equals(this.time, other.time) &&
                Objects.equals(this.loggedKeyspace, other.loggedKeyspace) &&
                Objects.equals(this.loggedTable, other.loggedTable) &&
                Objects.equals(this.loggedKey, other.loggedKey) &&
                Objects.equals(this.operation, other.operation) &&
                Objects.equals(this.updatedColumns, other.updatedColumns);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.time, this.loggedKeyspace,
                this.loggedTable, this.loggedKey, this.operation, this.updatedColumns);
    }
}