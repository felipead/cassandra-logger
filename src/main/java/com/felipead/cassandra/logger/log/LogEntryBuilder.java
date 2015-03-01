package com.felipead.cassandra.logger.log;

import org.apache.cassandra.config.CFMetaData;
import org.apache.cassandra.db.Cell;
import org.apache.cassandra.db.ColumnFamily;
import org.apache.cassandra.utils.UUIDGen;

public class LogEntryBuilder {

    public LogEntry build(ColumnFamily update, CFMetaData metadata, String keyspace, String columnFamily, String keyText) {
        LogEntry logEntry = new LogEntry();

        logEntry.setTimeUuid(UUIDGen.getTimeUUID());

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

        return logEntry;
    }
}