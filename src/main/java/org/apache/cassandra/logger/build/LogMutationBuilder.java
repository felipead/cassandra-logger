package org.apache.cassandra.logger.build;

import org.apache.cassandra.cql3.ColumnIdentifier;
import org.apache.cassandra.db.Mutation;
import org.apache.cassandra.db.composites.CellName;
import org.apache.cassandra.db.composites.CellNames;
import org.apache.cassandra.logger.log.LogEntry;
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
        mutation.add(columnFamily, toCellName("logged_keyspace"), bytes(logEntry.getLoggedKeyspace()), timestamp);
        mutation.add(columnFamily, toCellName("logged_table"), bytes(logEntry.getLoggedTable()), timestamp);
        mutation.add(columnFamily, toCellName("logged_key"), bytes(logEntry.getLoggedKey()), timestamp);
        mutation.add(columnFamily, toCellName("updated_columns"), bytes(toCommaSeparatedString(logEntry.getUpdatedColumns())), timestamp);
        mutation.add(columnFamily, toCellName("operation"), bytes(logEntry.getOperation().getValue()), timestamp);
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