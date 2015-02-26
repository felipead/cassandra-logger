package org.apache.cassandra.logger;

import org.apache.cassandra.cql3.ColumnIdentifier;
import org.apache.cassandra.db.Mutation;
import org.apache.cassandra.db.composites.CellName;
import org.apache.cassandra.db.composites.CellNames;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

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
        Mutation mutation = new Mutation(keyspace, bytes(logEntry.getId()));
        mutation.add(columnFamily, toCellName("keyspace"), bytes(logEntry.getKeyspace()), timestamp);
        mutation.add(columnFamily, toCellName("column_family"), bytes(logEntry.getColumnFamily()), timestamp);
        mutation.add(columnFamily, toCellName("key"), bytes(logEntry.getRowKey()), timestamp);
        mutation.add(columnFamily, toCellName("column_names"), bytes(toCommaSeparatedString(logEntry.getColumnNames())), timestamp);
        mutation.add(columnFamily, toCellName("operation_type"), bytes(logEntry.getOperationType().getValue()), timestamp);
        mutation.add(columnFamily, toCellName("timestamp"), bytes(logEntry.getTimestamp()), timestamp);
        return mutation;
    }

    private CellName toCellName(String name) {
        return CellNames.simpleSparse(new ColumnIdentifier(name, false));
    }
    
    private String toCommaSeparatedString(Collection<String> list) {
        return StringUtils.join(list, ',');
    }
}