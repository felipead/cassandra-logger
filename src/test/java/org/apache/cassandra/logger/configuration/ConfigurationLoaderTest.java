package org.apache.cassandra.logger.configuration;

import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ConfigurationLoaderTest {
    
    @Test
    public void loadValidConfiguration() throws IOException {
        Configuration configuration = ConfigurationLoader.load("org/apache/cassandra/logger/configuration/ValidConfiguration.properties");
        assertThat(configuration.getLogKeyspace(), is("test_logger"));
        assertThat(configuration.getLogColumnFamily(), is("test_log"));
    }

    @Test
    public void useDefaultLogColumnFamilyIfNotProvidedInConfigurationFile() throws IOException {
        Configuration configuration = ConfigurationLoader.load("org/apache/cassandra/logger/configuration/ConfigurationWithoutLogColumnFamily.properties");
        assertThat(configuration.getLogKeyspace(), is("test_logger"));
        assertThat(configuration.getLogColumnFamily(), is("log"));
    }

    @Test
    public void useDefaultLogKeyspaceIfNotProvidedInConfigurationFile() throws IOException {
        Configuration configuration = ConfigurationLoader.load("org/apache/cassandra/logger/configuration/ConfigurationWithoutLogKeyspace.properties");
        assertThat(configuration.getLogKeyspace(), is("logger"));
        assertThat(configuration.getLogColumnFamily(), is("test_log"));
    }
    
    @Test(expected = IOException.class)
    public void failIfNoPropertiesFileFound() throws IOException {
        ConfigurationLoader.load("Nonexistent.properties");
    }
}