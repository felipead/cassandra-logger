package org.apache.cassandra.logger.configuration;

@SuppressWarnings("UnusedDeclaration")
public enum LoggingMode {
    LOG_ALL_KEYSPACES,
    LOG_ONLY_SPECIFIED_KEYSPACES,
    DISABLED
}
