package org.apache.cassandra.logger.configuration;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ConfigurationSingletonTest {
    
    @Test
    public void loadDefaultConfigurationIfUserFileNotFound() {
        Configuration configuration = ConfigurationSingleton.getInstance();
        assertThat(configuration.getLogKeyspace(), is("logger"));
        assertThat(configuration.getLogColumnFamily(), is("log"));
    }
}