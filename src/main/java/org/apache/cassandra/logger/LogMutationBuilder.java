package org.apache.cassandra.logger;

import org.apache.cassandra.db.Mutation;
import org.apache.cassandra.db.composites.CellName;
import org.apache.cassandra.db.composites.CellNames;
import org.apache.cassandra.utils.ByteBufferUtil;

public class LogMutationBuilder {
    
    private String keyspace;
    private String columnFamily;
    
    public LogMutationBuilder(String keyspace, String columnFamily) {
        this.keyspace = keyspace;
        this.columnFamily = columnFamily;
    }
    
    public Mutation build(LogEntry logEntry) {
        long timestamp = System.currentTimeMillis();
        Mutation mutation = new Mutation(keyspace, ByteBufferUtil.bytes(logEntry.getKey()));
        mutation.add(columnFamily, toCellName("target_keyspace"), ByteBufferUtil.bytes(logEntry.getTargetKeyspace()), timestamp);
        mutation.add(columnFamily, toCellName("target_column_family"), ByteBufferUtil.bytes(logEntry.getTargetColumnFamily()), timestamp);
        mutation.add(columnFamily, toCellName("target_partition_key"), logEntry.getTargetPartitionKey(), timestamp);
        mutation.add(columnFamily, toCellName("operation_type"), ByteBufferUtil.bytes(logEntry.getOperationType().name()), timestamp);
        mutation.add(columnFamily, toCellName("operation_timestamp"), ByteBufferUtil.bytes(logEntry.getOperationTimestamp()), timestamp);
        return mutation;
    }

    private CellName toCellName(String name) {
        return CellNames.simpleDense(ByteBufferUtil.bytes(name));
    }
}