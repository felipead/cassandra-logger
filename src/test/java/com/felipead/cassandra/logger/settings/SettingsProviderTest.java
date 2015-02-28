package com.felipead.cassandra.logger.settings;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SettingsProviderTest {
    
    @Test
    public void loadDefaultSettingsIfUserSettingsFileNotFound() {
        Settings settings = SettingsProvider.getSettings();
        assertThat(settings.getLogKeyspace(), is("logger"));
        assertThat(settings.getLogTable(), is("log"));
    }
}