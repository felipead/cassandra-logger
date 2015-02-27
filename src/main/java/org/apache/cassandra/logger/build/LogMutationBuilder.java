package org.apache.cassandra.logger.build;

import org.apache.cassandra.cql3.ColumnIdentifier;
import org.apache.cassandra.db.Mutation;
import org.apache.cassandra.db.composites.CellName;
import org.apache.cassandra.db.composites.CellNames;
import org.apache.cassandra.db.marshal.TimestampType;
import org.apache.cassandra.db.marshal.UTF8Type;
import org.apache.cassandra.db.marshal.UUIDType;
import org.apache.cassandra.logger.log.LogEntry;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Date;

public class LogMutationBuilder {

    private String keyspace;
    private String columnFamily;

    public LogMutationBuilder(String keyspace, String columnFamily) {
        this.keyspace = keyspace;
        this.columnFamily = columnFamily;
    }

    public Mutation build(LogEntry logEntry) {
        long timestamp = new Date().getTime();
        
        Mutation mutation = new Mutation(keyspace, UUIDType.instance.decompose(logEntry.getId()));
        
        mutation.add(columnFamily, toCellName("time"), TimestampType.instance.decompose(logEntry.getTime()), timestamp);        
        mutation.add(columnFamily, toCellName("logged_keyspace"), UTF8Type.instance.decompose(logEntry.getLoggedKeyspace()), timestamp);
        mutation.add(columnFamily, toCellName("logged_table"), UTF8Type.instance.decompose(logEntry.getLoggedTable()), timestamp);
        mutation.add(columnFamily, toCellName("logged_key"), UTF8Type.instance.decompose(logEntry.getLoggedKey()), timestamp);
        mutation.add(columnFamily, toCellName("updated_columns"), UTF8Type.instance.decompose(toCommaSeparatedString(logEntry.getUpdatedColumns())), timestamp);
        mutation.add(columnFamily, toCellName("operation"), UTF8Type.instance.decompose(logEntry.getOperation().getValue()), timestamp);
        
        return mutation;
    }

    private CellName toCellName(String name) {
        return CellNames.simpleSparse(new ColumnIdentifier(name, false));
    }
    
    private String toCommaSeparatedString(Collection<String> list) {
        return StringUtils.join(list, ',');
    }
}