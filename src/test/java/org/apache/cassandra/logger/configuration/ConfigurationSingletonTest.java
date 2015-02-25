package org.apache.cassandra.logger.configuration;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

public class ConfigurationSingletonTest {
    
    @Test
    public void loadDefaultConfigurationIfConfigurationFileNotFound() {
        Configuration configuration = ConfigurationSingleton.getInstance();
        assertThat(configuration.getLoggingMode(), is(LoggingMode.ALL_KEYSPACES));
        assertThat(configuration.getLogKeyspace(), is("logger"));
        assertThat(configuration.getLogColumnFamily(), is("log"));
        assertThat(configuration.getKeyspacesToLog(), is(empty()));        
    }
}