package com.felipead.cassandra.logger.settings;

import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

public class SettingsLoaderTest {
    
    @Test
    public void loadValidSettings() throws IOException {
        Settings settings = SettingsLoader.load("com/felipead/cassandra/logger/settings/ValidSettings.properties");
        assertThat(settings.getLogKeyspace(), is("test_logger"));
        assertThat(settings.getLogTable(), is("test_log"));
        assertThat(settings.getIgnoreColumns(), containsInAnyOrder("created_at", "updated_at"));
    }

    @Test
    public void useDefaultLogTableIfNotProvidedInSettingsFile() throws IOException {
        Settings settings = SettingsLoader.load("com/felipead/cassandra/logger/settings/SettingsWithoutLogTable.properties");
        assertThat(settings.getLogTable(), is("log"));
    }

    @Test
    public void useDefaultLogKeyspaceIfNotProvidedInSettingsFile() throws IOException {
        Settings settings = SettingsLoader.load("com/felipead/cassandra/logger/settings/SettingsWithoutLogKeyspace.properties");
        assertThat(settings.getLogKeyspace(), is("logger"));
    }

    @Test
    public void useEmptyCollectionOfIgnoreColumnsIfIgnoreColumnsNotProvidedInSettingsFile() throws IOException {
        Settings settings = SettingsLoader.load("com/felipead/cassandra/logger/settings/SettingsWithoutIgnoreColumns.properties");
        assertThat(settings.getIgnoreColumns(), is(empty()));
    }
    
    @Test(expected = IOException.class)
    public void failIfNoSettingsFileFound() throws IOException {
        SettingsLoader.load("Nonexistent.properties");
    }
}