package org.apache.cassandra.logger;

import org.apache.cassandra.config.CFMetaData;
import org.apache.cassandra.db.Cell;
import org.apache.cassandra.db.ColumnFamily;
import org.apache.cassandra.db.Mutation;
import org.apache.cassandra.logger.settings.Settings;
import org.apache.cassandra.logger.settings.SettingsProvider;
import org.apache.cassandra.triggers.ITrigger;
import org.apache.cassandra.utils.UUIDGen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("UnusedDeclaration")
public class LoggerTrigger implements ITrigger {
    private static final Logger logger = LoggerFactory.getLogger(LoggerTrigger.class);

    private LogMutationBuilder mutationBuilder;
    
    public LoggerTrigger() {
        Settings settings = SettingsProvider.getSettings();
        logger.info("using settings: {}", settings);
        
        mutationBuilder = new LogMutationBuilder(
                settings.getLogKeyspace(), settings.getLogColumnFamily());
    }
    
    public Collection<Mutation> augment(ByteBuffer key, ColumnFamily update) {
        CFMetaData metadata = update.metadata();
        String keyspace = metadata.ksName;
        String columnFamily = metadata.cfName;
        String keyText = metadata.getKeyValidator().getString(key);
        
        try {
            LogEntry logEntry = buildLogEntry(update, metadata, keyspace, columnFamily, keyText);
            logger.info("Processing log entry: {}", logEntry);
            
            Mutation mutation = mutationBuilder.build(logEntry);
            logger.info("Built mutation: {}", mutation);

            // FIXME: return mutation that was just built
        } catch (Exception e) {
            logger.error("Exception while processing keyspace {}, column family {}, key {}:",
                    keyspace, columnFamily, keyText, e);
        }

        return Collections.emptyList();
    }

    private LogEntry buildLogEntry(ColumnFamily update, CFMetaData metadata, String keyspace, String columnFamily, String keyText) {
        LogEntry logEntry = new LogEntry();
        logEntry.setId(UUIDGen.getTimeUUID());

        logEntry.setKeyspaceName(keyspace);
        logEntry.setColumnFamilyName(columnFamily);
        logEntry.setRowKey(keyText);

        if (update.isMarkedForDelete()) {
            logEntry.setOperationType(OperationType.DELETE);
        } else {
            logEntry.setOperationType(OperationType.SAVE);
        }

        List<String> cellNames = new LinkedList<>();
        for (Cell cell : update) {
            if (cell.value().remaining() > 0) {
                cellNames.add(metadata.comparator.getString(cell.name()));
            }
        }
        logEntry.setColumnNames(cellNames);

        logEntry.setTimestamp(System.currentTimeMillis());
        return logEntry;
    }
}