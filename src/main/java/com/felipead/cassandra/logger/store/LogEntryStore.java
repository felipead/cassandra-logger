package com.felipead.cassandra.logger.store;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.felipead.cassandra.logger.log.LogEntry;
import com.felipead.cassandra.logger.log.Operation;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.EnumUtils;

import java.util.List;
import java.util.UUID;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;

public class LogEntryStore extends AbstractCassandraStore {
    
    public LogEntryStore(String node, String keyspace, String table) {
        super(node, keyspace, table);
    }
    
    public void create(LogEntry entry) {
        Statement statement = QueryBuilder.insertInto(getKeyspace(), getTable())
                .value("time_uuid", entry.getTimeUuid())
                .value("logged_keyspace", entry.getLoggedKeyspace())
                .value("logged_table", entry.getLoggedTable())
                .value("logged_key", entry.getLoggedKey())
                .value("updated_columns", entry.getUpdatedColumns())
                .value("operation", entry.getOperation().toString());
        
        execute(statement);
    }

    public List<LogEntry> findByLoggedKey(String loggedKeyspace, String loggedTable, String loggedKey) {
        Statement statement = QueryBuilder.select().all()
                .from(getKeyspace(), getTable())
                .where(eq("logged_keyspace", loggedKeyspace))
                    .and(eq("logged_table", loggedTable))
                    .and(eq("logged_key", loggedKey));

        ResultSet result = execute(statement);
        return toEntity(result.all());
    }

    public LogEntry read(String loggedKeyspace, String loggedTable, String loggedKey, UUID timeUuid) {
        Statement statement = QueryBuilder.select().all()
                .from(getKeyspace(), getTable())
                .where(eq("logged_keyspace", loggedKeyspace))
                    .and(eq("logged_table", loggedTable))
                    .and(eq("logged_key", loggedKey))
                    .and(eq("time_uuid", timeUuid));

        ResultSet result = execute(statement);
        return toEntity(result.one());
    }

    private List<LogEntry> toEntity(List<Row> rows) {
        List<LogEntry> entities = Lists.newArrayListWithCapacity(rows.size());
        for (Row row : rows) {
            entities.add(toEntity(row));
        }
        return entities;
    }
    
    private LogEntry toEntity(Row row) {
        LogEntry entity = new LogEntry();
        entity.setTimeUuid(row.getUUID("time_uuid"));
        entity.setLoggedKeyspace(row.getString("logged_keyspace"));
        entity.setLoggedTable(row.getString("logged_table"));
        entity.setLoggedKey(row.getString("logged_key"));
        entity.setUpdatedColumns(row.getSet("updated_columns", String.class));
        entity.setOperation(EnumUtils.getEnum(Operation.class, row.getString("operation")));
        return entity;
    }
}