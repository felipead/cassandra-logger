package org.apache.cassandra.logger;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;
import java.util.UUID;

public class LogEntry {
    
    private UUID id;
    private String keyspace;
    private String columnFamily;
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

    public String getKeyspace() {
        return keyspace;
    }
    
    public void setKeyspace(String keyspace) {
        this.keyspace = keyspace;
    }

    public String getColumnFamily() {
        return columnFamily;
    }
    
    public void setColumnFamily(String columnFamily) {
        this.columnFamily = columnFamily;
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
        builder.append("id", id);
        builder.append("keyspace", keyspace);
        builder.append("columnFamily", columnFamily);
        builder.append("rowKey", rowKey);
        builder.append("columnNames", columnNames);
        builder.append("operationType", operationType);
        builder.append("timestamp", timestamp);
        return builder.build();
    }
}