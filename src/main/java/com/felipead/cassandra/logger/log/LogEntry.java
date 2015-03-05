package com.felipead.cassandra.logger.log;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.*;

public class LogEntry {

    private String loggedKeyspace;
    private String loggedTable;
    private String loggedKey;
    private UUID timeUuid;
    private Set<String> updatedColumns;
    private Operation operation;

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

    public UUID getTimeUuid() {
        return timeUuid;
    }

    public void setTimeUuid(UUID timeUuid) {
        this.timeUuid = timeUuid;
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
    
    public boolean isMeaningful() {
        return operation == Operation.delete || 
                (updatedColumns != null && !updatedColumns.isEmpty());
    }
    
    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("timeUuid", getTimeUuid());
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
        return Objects.equals(this.timeUuid, other.timeUuid) &&
                Objects.equals(this.loggedKeyspace, other.loggedKeyspace) &&
                Objects.equals(this.loggedTable, other.loggedTable) &&
                Objects.equals(this.loggedKey, other.loggedKey) &&
                Objects.equals(this.operation, other.operation) &&
                Objects.equals(this.updatedColumns, other.updatedColumns);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.timeUuid, this.loggedKeyspace,
                this.loggedTable, this.loggedKey, this.operation, this.updatedColumns);
    }
}