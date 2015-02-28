package com.felipead.cassandra.logger.settings;

import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SettingsLoaderTest {
    
    @Test
    public void loadValidSettings() throws IOException {
        Settings settings = SettingsLoader.load("com/felipead/cassandra/logger/settings/ValidSettings.properties");
        assertThat(settings.getLogKeyspace(), is("test_logger"));
        assertThat(settings.getLogTable(), is("test_log"));
    }

    @Test
    public void useDefaultLogTableIfNotProvidedInSettingsFile() throws IOException {
        Settings settings = SettingsLoader.load("com/felipead/cassandra/logger/settings/SettingsWithoutLogTable.properties");
        assertThat(settings.getLogKeyspace(), is("test_logger"));
        assertThat(settings.getLogTable(), is("log"));
    }

    @Test
    public void useDefaultLogKeyspaceIfNotProvidedInSettingsFile() throws IOException {
        Settings settings = SettingsLoader.load("com/felipead/cassandra/logger/settings/SettingsWithoutLogKeyspace.properties");
        assertThat(settings.getLogKeyspace(), is("logger"));
        assertThat(settings.getLogTable(), is("test_log"));
    }
    
    @Test(expected = IOException.class)
    public void failIfNoSettingsFileFound() throws IOException {
        SettingsLoader.load("Nonexistent.properties");
    }
}