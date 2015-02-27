package org.apache.cassandra.logger.build;

import org.apache.cassandra.config.CFMetaData;
import org.apache.cassandra.db.Cell;
import org.apache.cassandra.db.ColumnFamily;
import org.apache.cassandra.logger.log.LogEntry;
import org.apache.cassandra.logger.log.Operation;
import org.apache.cassandra.utils.UUIDGen;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class LogEntryBuilder {

    public LogEntry build(ColumnFamily update, CFMetaData metadata, String keyspace, String columnFamily, String keyText) {
        LogEntry logEntry = new LogEntry();
        logEntry.setId(generateUUID());

        logEntry.setLoggedKeyspace(keyspace);
        logEntry.setLoggedTable(columnFamily);
        logEntry.setLoggedKey(keyText);

        if (update.isMarkedForDelete()) {
            logEntry.setOperation(Operation.DELETE);
        } else {
            logEntry.setOperation(Operation.SAVE);
        }

        List<String> cellNames = new LinkedList<>();
        for (Cell cell : update) {
            if (cell.value().remaining() > 0) {
                cellNames.add(metadata.comparator.getString(cell.name()));
            }
        }
        logEntry.setUpdatedColumns(cellNames);

        logEntry.setTime(new Date());
        return logEntry;
    }

    private static UUID generateUUID() {
        return UUIDGen.getTimeUUID();
    }
}
