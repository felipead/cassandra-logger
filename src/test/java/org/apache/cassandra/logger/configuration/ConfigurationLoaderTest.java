package org.apache.cassandra.logger.configuration;

import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ConfigurationLoaderTest {
    
    @Test
    public void loadDisabledLoggingConfiguration() throws IOException {
        Configuration configuration = ConfigurationLoader.load("org/apache/cassandra/logger/configuration/Disabled.properties");
        assertThat(configuration.getLoggingMode(), is(LoggingMode.DISABLED));
        assertThat(configuration.getKeyspacesToLog(), is(empty()));
        assertThat(configuration.getLogKeyspace(), is("test_logger"));
        assertThat(configuration.getLogColumnFamily(), is("test_log"));
    }

    @Test
    public void loadLogAllKeyspacesConfiguration() throws IOException {
        Configuration configuration = ConfigurationLoader.load("org/apache/cassandra/logger/configuration/LogAllKeyspaces.properties");
        assertThat(configuration.getLoggingMode(), is(LoggingMode.ALL_KEYSPACES));
        assertThat(configuration.getKeyspacesToLog(), is(empty()));
        assertThat(configuration.getLogKeyspace(), is("test_logger"));
        assertThat(configuration.getLogColumnFamily(), is("test_log"));
    }

    @Test
    public void loadLogOnlySpecifiedKeyspacesConfiguration() throws IOException {
        Configuration configuration = ConfigurationLoader.load("org/apache/cassandra/logger/configuration/LogOnlySpecifiedKeyspaces.properties");
        assertThat(configuration.getLoggingMode(), is(LoggingMode.ONLY_SPECIFIED_KEYSPACES));
        assertThat(configuration.getKeyspacesToLog(), containsInAnyOrder("products", "users", "items", "orders"));
        assertThat(configuration.getLogKeyspace(), is("test_logger"));
        assertThat(configuration.getLogColumnFamily(), is("test_log"));
    }
    
    @Test(expected = IOException.class)
    public void failIfNoPropertiesFileFound() throws IOException {
        ConfigurationLoader.load("Nonexistent.properties");
    }
}