package com.felipead.cassandra.logger;

import com.felipead.cassandra.logger.internal.ColumnFamilyUtil;
import com.felipead.cassandra.logger.log.LogEntry;
import com.felipead.cassandra.logger.log.LogEntryBuilder;
import com.felipead.cassandra.logger.settings.Settings;
import com.felipead.cassandra.logger.settings.SettingsProvider;
import com.felipead.cassandra.logger.store.LogEntryStore;
import org.apache.cassandra.db.ColumnFamily;
import org.apache.cassandra.db.Mutation;
import org.apache.cassandra.triggers.ITrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Collections;

@SuppressWarnings("UnusedDeclaration")
public class LogTrigger implements ITrigger {
    public static final String LOCALHOST = "127.0.0.1";
    
    private static final Logger logger = LoggerFactory.getLogger(LogTrigger.class);

    private LogEntryBuilder logEntryBuilder;
    private LogEntryStore logEntryStore;

    public LogTrigger() {
        Settings settings = SettingsProvider.getSettings();
        this.logEntryStore = new LogEntryStore(LOCALHOST, settings.getLogKeyspace(), settings.getLogTable());
        this.logEntryBuilder = new LogEntryBuilder();
        this.logEntryBuilder.setIgnoreColumns(settings.getIgnoreColumns());
    }

    public Collection<Mutation> augment(ByteBuffer partitionKey, ColumnFamily update) {
        try {
            LogEntry logEntry = logEntryBuilder.build(update, partitionKey);
            if (logEntry.isMeaningful()) {
                logger.info("Processing log entry: {}", logEntry);
                logEntryStore.create(logEntry);
            } else {
                logger.info("Ignoring meaningless update: {}", logEntry);
            }
        } catch (Exception exception) {
            try {
                logger.error("Exception while processing update from keyspace {}, table {} and partition key {}:",
                        ColumnFamilyUtil.getKeyspaceName(update),
                        ColumnFamilyUtil.getTableName(update),
                        ColumnFamilyUtil.getKeyText(update, partitionKey),
                        exception);
            } catch (Exception nestedException) {
                logger.error("Can't get keyspace, table or key text from byte partition key {} and column family {}:",
                        partitionKey, update, nestedException);
            }
        }

        return Collections.emptyList();
    }
}