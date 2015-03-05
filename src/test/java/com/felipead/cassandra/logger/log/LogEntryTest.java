package com.felipead.cassandra.logger.log;

import org.junit.Test;

import java.util.HashSet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class LogEntryTest {

    @Test
    public void testIsMeaningfulIfOperationIsDelete() {
        LogEntry logEntry = new LogEntry();
        logEntry.setOperation(Operation.delete);
        assertThat(logEntry.isMeaningful(), is(true));
    }

    @Test
    public void testIsMeaningfulIfUpdateColumnsIsNotEmpty() {
        LogEntry logEntry = new LogEntry();
        logEntry.addUpdatedColumn("foo");
        assertThat(logEntry.isMeaningful(), is(true));
    }

    @Test
    public void testIsNotMeaningfulIfOperationIsNotDeleteAndUpdateColumnsIsNull() {
        LogEntry logEntry = new LogEntry();
        assertThat(logEntry.isMeaningful(), is(false));
    }

    @Test
    public void testIsNotMeaningfulIfOperationIsNotDeleteAndUpdateColumnsIsEmpty() {
        LogEntry logEntry = new LogEntry();
        logEntry.setUpdatedColumns(new HashSet<String>());
        assertThat(logEntry.isMeaningful(), is(false));
    }
}