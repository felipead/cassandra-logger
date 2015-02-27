package org.apache.cassandra.logger.log;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class LogEntry {
    
    private UUID id;
    private String loggedKeyspace;
    private String loggedTable;
    private String loggedKey;
    private List<String> updatedColumns;
    private Operation operation;
    private Date time;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    public List<String> getUpdatedColumns() {
        return updatedColumns;
    }

    public void setUpdatedColumns(List<String> updatedColumns) {
        this.updatedColumns = updatedColumns;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
    
    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("id", getId());
        builder.append("loggedKeyspace", getLoggedKeyspace());
        builder.append("loggedTable", getLoggedTable());
        builder.append("loggedKey", getLoggedKey());
        builder.append("updatedColumns", getUpdatedColumns());
        builder.append("operation", getOperation());
        builder.append("time", getTime());
        return builder.build();
    }
}