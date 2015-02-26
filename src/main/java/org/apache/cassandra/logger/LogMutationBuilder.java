package org.apache.cassandra.logger;

import org.apache.cassandra.cql3.ColumnIdentifier;
import org.apache.cassandra.db.Mutation;
import org.apache.cassandra.db.composites.CellName;
import org.apache.cassandra.db.composites.CellNames;

import static org.apache.cassandra.utils.ByteBufferUtil.bytes;

public class LogMutationBuilder {
    
    private String keyspace;
    private String columnFamily;
    
    public LogMutationBuilder(String keyspace, String columnFamily) {
        this.keyspace = keyspace;
        this.columnFamily = columnFamily;
    }
    
    public Mutation build(LogEntry logEntry) {
        long timestamp = System.currentTimeMillis();
        Mutation mutation = new Mutation(keyspace, bytes(logEntry.getKey()));
        mutation.add(columnFamily, toCellName("target_keyspace"), bytes(logEntry.getTargetKeyspace()), timestamp);
        mutation.add(columnFamily, toCellName("target_column_family"), bytes(logEntry.getTargetColumnFamily()), timestamp);
        mutation.add(columnFamily, toCellName("target_partition_key"), logEntry.getTargetPartitionKey(), timestamp);
        mutation.add(columnFamily, toCellName("operation_type"), bytes(logEntry.getOperationType().name()), timestamp);
        mutation.add(columnFamily, toCellName("operation_timestamp"), bytes(logEntry.getOperationTimestamp()), timestamp);
        return mutation;
    }

    private CellName toCellName(String name) {
        return CellNames.simpleSparse(new ColumnIdentifier(name, false));
    }
}