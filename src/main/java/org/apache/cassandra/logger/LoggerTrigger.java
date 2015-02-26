package org.apache.cassandra.logger;

import org.apache.cassandra.db.ColumnFamily;
import org.apache.cassandra.db.Mutation;
import org.apache.cassandra.logger.configuration.Configuration;
import org.apache.cassandra.logger.configuration.ConfigurationSingleton;
import org.apache.cassandra.logger.configuration.LoggingMode;
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
    private Configuration configuration;
    
    public LoggerTrigger() {
        configuration = ConfigurationSingleton.getInstance();
        mutationBuilder = new LogMutationBuilder(
                configuration.getLogKeyspace(), configuration.getLogColumnFamily());
    }
    
    public Collection<Mutation> augment(ByteBuffer key, ColumnFamily update) {
        if (configuration.getLoggingMode() == LoggingMode.DISABLED) {
            return null;
        }
        
        String keyspaceBeingUpdated = update.metadata().ksName;
        String columnFamilyBeingUpdated = update.metadata().cfName;

        if (configuration.getLoggingMode() == LoggingMode.ONLY_SPECIFIED_KEYSPACES &&
                !configuration.getKeyspacesToLog().contains(keyspaceBeingUpdated)) {
            return null;
        }
        
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