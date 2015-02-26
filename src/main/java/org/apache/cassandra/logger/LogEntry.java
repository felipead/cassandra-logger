package org.apache.cassandra.logger;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;
import java.util.UUID;

public class LogEntry {
    
    private UUID id;
    private String keyspaceName;
    private String columnFamilyName;
    private String rowKey;
    private List<String> columnNames;
    private OperationType operationType;
    private long timestamp;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getKeyspaceName() {
        return keyspaceName;
    }
    
    public void setKeyspaceName(String keyspaceName) {
        this.keyspaceName = keyspaceName;
    }

    public String getColumnFamilyName() {
        return columnFamilyName;
    }
    
    public void setColumnFamilyName(String columnFamilyName) {
        this.columnFamilyName = columnFamilyName;
    }

    public String getRowKey() {
        return rowKey;
    }
    
    public void setRowKey(String rowKey) {
        this.rowKey = rowKey;
    }

    public OperationType getOperationType() {
        return operationType;
    }
    
    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(List<String> columnNames) {
        this.columnNames = columnNames;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("id", getId());
        builder.append("keyspace", getKeyspaceName());
        builder.append("columnFamilyName", getColumnFamilyName());
        builder.append("rowKey", getRowKey());
        builder.append("columnNames", getColumnNames());
        builder.append("operationType", getOperationType());
        builder.append("timestamp", getTimestamp());
        return builder.build();
    }
}