/* 
 * Run this script on the CQL shell:
 * 
 *    {CASSANDRA_HOME}/bin/cqlsh --file create-log-schema.cql
 *
 * It will create a table "log" on keyspace "logger".
 * If you want to customize these names, you also need to edit cassandra-logger.properties.
 */
 
CREATE KEYSPACE IF NOT EXISTS logger
WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };

USE logger;

DROP TABLE IF EXISTS log;

CREATE TABLE log (
    time timestamp,
    logged_keyspace text,
    logged_table text,
    logged_key text,
    operation text,
    updated_columns set<text>,
    PRIMARY KEY ((logged_keyspace, logged_table, logged_key), time)
) WITH CLUSTERING ORDER BY (time DESC);