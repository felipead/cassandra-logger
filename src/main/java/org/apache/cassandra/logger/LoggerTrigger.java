package org.apache.cassandra.logger;

import org.apache.cassandra.db.ColumnFamily;
import org.apache.cassandra.db.Mutation;
import org.apache.cassandra.logger.settings.Settings;
import org.apache.cassandra.logger.settings.SettingsProvider;
import org.apache.cassandra.triggers.ITrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

@SuppressWarnings("UnusedDeclaration")
public class LoggerTrigger implements ITrigger {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerTrigger.class);

    private LogMutationBuilder mutationBuilder;
    
    public LoggerTrigger() {
        Settings config = SettingsProvider.getSettings();
        mutationBuilder = new LogMutationBuilder(
                config.getLogKeyspace(), config.getLogColumnFamily());
    }
    
    public Collection<Mutation> augment(ByteBuffer key, ColumnFamily update) {
        String keyspaceBeingUpdated = update.metadata().ksName;
        String columnFamilyBeingUpdated = update.metadata().cfName;

        LogEntry logEntry = new LogEntry();
        logEntry.setKey(UUID.randomUUID());
        logEntry.setTargetKeyspace(keyspaceBeingUpdated);
        logEntry.setTargetColumnFamily(columnFamilyBeingUpdated);
        logEntry.setTargetPartitionKey(key);
        if (update.isMarkedForDelete()) {
            logEntry.setOperationType(OperationType.DELETE);
        } else {
            logEntry.setOperationType(OperationType.SAVE);
        }
        logEntry.setOperationTimestamp(System.currentTimeMillis());
        
        Mutation mutation = mutationBuilder.build(logEntry);
        return Arrays.asList(mutation);
    }
}