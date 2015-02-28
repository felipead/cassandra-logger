package com.felipead.cassandra.logger.store;

import com.felipead.cassandra.logger.log.LogEntry;
import com.felipead.cassandra.logger.log.Operation;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
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
        LogEntry created = new LogEntry();
        created.setTime(new Date());
        created.setLoggedKeyspace("test_keyspace");
        created.setLoggedTable("test_table");
        created.setLoggedKey(UUID.randomUUID().toString());
        created.addUpdatedColumn("one");
        created.addUpdatedColumn("two");
        created.addUpdatedColumn("three");
        created.setOperation(Operation.save);
        
        store.create(created);
        
        LogEntry read = store.read(created.getLoggedKeyspace(), created.getLoggedTable(), created.getLoggedKey(), created.getTime());
        assertThat(read, notNullValue());
        assertThat(read, equalTo(created));
    }
    
    @Test
    public void createAndFind() {
        String loggedKeyspace = "test_keyspace";
        String loggedTable = "test_table";
        String loggedKey = UUID.randomUUID().toString();

        long timestamp = System.currentTimeMillis();
        
        LogEntry created1 = new LogEntry();
        created1.setTime(new Date(timestamp));
        created1.setLoggedKeyspace(loggedKeyspace);
        created1.setLoggedTable(loggedTable);
        created1.setLoggedKey(loggedKey);
        created1.addUpdatedColumn("one");
        created1.addUpdatedColumn("two");
        created1.addUpdatedColumn("three");
        created1.setOperation(Operation.save);
        
        store.create(created1);

        LogEntry created2 = new LogEntry();
        created2.setTime(new Date(timestamp + 1000));
        created2.setLoggedKeyspace(loggedKeyspace);
        created2.setLoggedTable(loggedTable);
        created2.setLoggedKey(loggedKey);
        created2.addUpdatedColumn("hello");
        created2.setOperation(Operation.delete);

        store.create(created2);

        List<LogEntry> found = store.find(loggedKeyspace, loggedTable, loggedKey);
        assertThat(found, hasSize(greaterThanOrEqualTo(2)));
        assertThat(found.contains(created1), is(true));
        assertThat(found.contains(created2), is(true));
    }
}