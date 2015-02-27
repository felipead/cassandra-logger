package org.apache.cassandra.logger;

import org.apache.cassandra.config.CFMetaData;
import org.apache.cassandra.db.ColumnFamily;
import org.apache.cassandra.db.Mutation;
import org.apache.cassandra.logger.build.LogEntryBuilder;
import org.apache.cassandra.logger.build.LogMutationBuilder;
import org.apache.cassandra.logger.log.LogEntry;
import org.apache.cassandra.logger.settings.Settings;
import org.apache.cassandra.logger.settings.SettingsProvider;
import org.apache.cassandra.triggers.ITrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

@SuppressWarnings("UnusedDeclaration")
public class LoggerTrigger implements ITrigger {
    private static final Logger logger = LoggerFactory.getLogger(LoggerTrigger.class);

    private LogEntryBuilder logEntryBuilder;
    private LogMutationBuilder logMutationBuilder;
    
    public LoggerTrigger() {
        Settings settings = SettingsProvider.getSettings();
        logEntryBuilder = new LogEntryBuilder();
        logMutationBuilder = new LogMutationBuilder(
                settings.getLogKeyspace(), settings.getLogTable());
    }
    
    public Collection<Mutation> augment(ByteBuffer key, ColumnFamily update) {
        CFMetaData metadata = update.metadata();
        String keyspace = metadata.ksName;
        String table = metadata.cfName;
        String keyText = metadata.getKeyValidator().getString(key);
        
        try {
            LogEntry logEntry = logEntryBuilder.build(update, metadata, keyspace, table, keyText);
            logger.info("Processing log entry: {}", logEntry);
            
            Mutation mutation = logMutationBuilder.build(logEntry);
            return Arrays.asList(mutation);
        } catch (Exception e) {
            logger.error("Exception while processing update from keyspace {}, table {} and key {}:",
                    keyspace, table, keyText, e);
            return Collections.emptyList();
        }
    }
}