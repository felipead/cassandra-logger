package org.apache.cassandra.logger.build;

import org.apache.cassandra.config.CFMetaData;
import org.apache.cassandra.db.Cell;
import org.apache.cassandra.db.ColumnFamily;
import org.apache.cassandra.logger.log.LogEntry;
import org.apache.cassandra.logger.log.Operation;

import java.util.Date;

public class LogEntryBuilder {

    public LogEntry build(ColumnFamily update, CFMetaData metadata, String keyspace, String columnFamily, String keyText) {
        LogEntry logEntry = new LogEntry();

        logEntry.setLoggedKeyspace(keyspace);
        logEntry.setLoggedTable(columnFamily);
        logEntry.setLoggedKey(keyText);

        if (update.isMarkedForDelete()) {
            logEntry.setOperation(Operation.delete);
        } else {
            logEntry.setOperation(Operation.save);
        }

        for (Cell cell : update) {
            if (cell.value().remaining() > 0) {
                logEntry.addUpdatedColumn(metadata.comparator.getString(cell.name()));
            }
        }

        logEntry.setTime(new Date());
        return logEntry;
    }
}