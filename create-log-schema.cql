/* 
 * Run this script on the CQL shell:
 * 
 *    {CASSANDRA_HOME}/bin/cqlsh --file create-log-schema.cql
 *
 * It will create a table "log" on keyspace "logger".
 * If you want to customize these names, you also need to edit cassandra-logger.properties.
 * Feel free to customize the replication and clustering settings to better suit your needs.
 */

CREATE KEYSPACE IF NOT EXISTS logger
WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };

USE logger;

DROP TABLE IF EXISTS log;

CREATE TABLE log (
    id uuid,
    logged_keyspace text,
    logged_table text,
    logged_key text,
    updated_columns text,
    operation text,
    host text,
    timestamp timestamp,
    PRIMARY KEY (id, timestamp)
) WITH CLUSTERING ORDER BY (timestamp DESC);