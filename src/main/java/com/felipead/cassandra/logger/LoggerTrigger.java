package com.felipead.cassandra.logger;

import com.felipead.cassandra.logger.log.LogEntry;
import com.felipead.cassandra.logger.settings.Settings;
import org.apache.cassandra.config.CFMetaData;
import org.apache.cassandra.db.ColumnFamily;
import org.apache.cassandra.db.Mutation;
import com.felipead.cassandra.logger.build.LogEntryBuilder;
import com.felipead.cassandra.logger.settings.SettingsProvider;
import com.felipead.cassandra.logger.store.LogEntryStore;
import org.apache.cassandra.triggers.ITrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Collections;

@SuppressWarnings("UnusedDeclaration")
public class LoggerTrigger implements ITrigger {
    public static final String LOCALHOST = "127.0.0.1";
    
    private static final Logger logger = LoggerFactory.getLogger(LoggerTrigger.class);

    private LogEntryBuilder logEntryBuilder;
    private LogEntryStore logEntryStore;
    
    public LoggerTrigger() {
        Settings settings = SettingsProvider.getSettings();
        logEntryBuilder = new LogEntryBuilder();
        logEntryStore = new LogEntryStore(LOCALHOST, settings.getLogKeyspace(), settings.getLogTable());
    }
    
    public Collection<Mutation> augment(ByteBuffer key, ColumnFamily update) {
        CFMetaData metadata = update.metadata();
        String keyspace = metadata.ksName;
        String table = metadata.cfName;
        String keyText = metadata.getKeyValidator().getString(key);
        
        try {
            LogEntry logEntry = logEntryBuilder.build(update, metadata, keyspace, table, keyText);
            logger.info("Processing log entry: {}", logEntry);
            logEntryStore.create(logEntry);
        } catch (Exception e) {
            logger.error("Exception while processing update from keyspace {}, table {} and key {}:",
                    keyspace, table, keyText, e);
        }

        return Collections.emptyList();
    }
}