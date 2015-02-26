package org.apache.cassandra.logger;

import java.nio.ByteBuffer;
import java.util.UUID;

public class LogEntry {
    
    private UUID key;
    private String targetKeyspace;
    private String targetColumnFamily;
    private ByteBuffer targetPartitionKey;
    private OperationType operationType;
    private long operationTimestamp;

    public UUID getKey() {
        return key;
    }

    public void setKey(UUID key) {
        this.key = key;
    }

    public String getTargetKeyspace() {
        return targetKeyspace;
    }

    public void setTargetKeyspace(String targetKeyspace) {
        this.targetKeyspace = targetKeyspace;
    }

    public String getTargetColumnFamily() {
        return targetColumnFamily;
    }

    public void setTargetColumnFamily(String targetColumnFamily) {
        this.targetColumnFamily = targetColumnFamily;
    }

    public ByteBuffer getTargetPartitionKey() {
        return targetPartitionKey;
    }

    public void setTargetPartitionKey(ByteBuffer targetPartitionKey) {
        this.targetPartitionKey = targetPartitionKey;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public long getOperationTimestamp() {
        return operationTimestamp;
    }

    public void setOperationTimestamp(long operationTimestamp) {
        this.operationTimestamp = operationTimestamp;
    }
}
