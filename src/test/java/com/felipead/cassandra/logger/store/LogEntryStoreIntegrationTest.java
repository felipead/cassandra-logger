package com.felipead.cassandra.logger.store;

import com.felipead.cassandra.logger.log.LogEntry;
import com.felipead.cassandra.logger.log.Operation;
import org.apache.cassandra.utils.UUIDGen;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class LogEntryStoreIntegrationTest {

    LogEntryStore store;
    
    @Before
    public void setup() {
        store = new LogEntryStore("127.0.0.1", "logger", "log");
    }
    
    @Test
    public void createAndRead() {
        Date now = new Date();
        String loggedKey = UUID.randomUUID().toString();
        
        LogEntry created = buildLogEntry("test_keyspace", "test_table", loggedKey, now, Operation.save, "one", "two", "three");
        
        store.create(created);
        
        LogEntry read = store.read(created.getLoggedKeyspace(), created.getLoggedTable(), created.getLoggedKey(), created.getTimeUuid());
        assertThat(read, notNullValue());
        assertThat(read, equalTo(created));
    }
    
    @Test
    public void createAndFind() {
        String loggedKeyspace = "test_keyspace";
        String loggedTable = "test_table";
        String loggedKey = UUID.randomUUID().toString();
        
        LogEntry created1 = buildLogEntry(loggedKeyspace, loggedTable, loggedKey, new Date(), Operation.save, "one", "two", "three");
        store.create(created1);

        LogEntry created2 = buildLogEntry(loggedKeyspace, loggedTable, loggedKey, new Date(), Operation.delete);
        store.create(created2);

        List<LogEntry> found = store.findByLoggedKey(loggedKeyspace, loggedTable, loggedKey);
        assertThat(found, hasSize(greaterThanOrEqualTo(2)));
        assertThat(found, hasItems(created1, created2));
    }
    
    private LogEntry buildLogEntry(String loggedKeyspace, String loggedTable, String loggedKey,
                                   Date time, Operation operation, String... updatedColumns) {
        LogEntry logEntry = new LogEntry();
        logEntry.setTimeUuid(UUIDGen.getTimeUUID(time.getTime()));
        logEntry.setLoggedKeyspace(loggedKeyspace);
        logEntry.setLoggedTable(loggedTable);
        logEntry.setLoggedKey(loggedKey);
        logEntry.setOperation(operation);
        
        logEntry.setUpdatedColumns(new HashSet<String>());
        for (String updatedColumn : updatedColumns) {
            logEntry.addUpdatedColumn(updatedColumn);
        }
        return logEntry;
    }
}