package com.felipead.cassandra.logger.log;

import com.felipead.cassandra.logger.internal.ColumnFamilyUtil;
import org.apache.cassandra.db.ColumnFamily;
import org.apache.cassandra.utils.UUIDGen;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public class LogEntryBuilder {

    private Collection<String> ignoreColumns;
    
    public void setIgnoreColumns(Collection<String> ignoreColumns) {
        this.ignoreColumns = ignoreColumns;
    }

    public LogEntry build(ColumnFamily update, ByteBuffer key) {
        LogEntry logEntry = new LogEntry();

        logEntry.setTimeUuid(generateTimeUuid());
        logEntry.setLoggedKeyspace(ColumnFamilyUtil.getKeyspaceName(update));
        logEntry.setLoggedTable(ColumnFamilyUtil.getTableName(update));
        logEntry.setLoggedKey(ColumnFamilyUtil.getKeyText(update, key));

        if (ColumnFamilyUtil.isDeleted(update)) {
            logEntry.setOperation(Operation.delete);
        } else {
            logEntry.setOperation(Operation.save);
        }

        Set<String> cellNames = ColumnFamilyUtil.getCellNames(update);
        if (this.ignoreColumns != null) {
            cellNames.removeAll(this.ignoreColumns);
        }
        
        logEntry.setUpdatedColumns(cellNames);
        return logEntry;
    }

    private static UUID generateTimeUuid() {
        return UUIDGen.getTimeUUID();
    }
}