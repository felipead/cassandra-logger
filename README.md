Cassandra Logger
================

[Apache Cassandra](http://cassandra.apache.org) Trigger that creates a log of all data mutations. Useful to sync Cassandra with other databases, like Solr or Elastic Search.

Version: 0.1 (not ready for production).

Requirements
------------

- Java SDK 1.7+
- Gradle 2.x
- Cassandra 2.1+

Setup
-----

### Installing the JDK

You need a Java JDK 7 or 8. Please follow the instructions from the Oracle [website](http://docs.oracle.com/javase/7/docs/webnotes/install/)

### Installing Cassandra

The Trigger API was released as part of Cassandra 2.0. However, it was changed after Cassandra 2.1. This trigger *will not work* with versions of Cassandra previous to 2.1.

Please follow the instructions from the Cassandra project [website](http://wiki.apache.org/cassandra/GettingStarted).

### Installing Gradle

Gradle is a build tool for Java, a simplified successor to the well stablished Maven.

Please follow the instructions from the Gradle project [website](http://gradle.org/installation).

### Building the JAR

Enter the project root folder and type:

        gradle assemble

If the compilation is successful, the resulting JAR will be available at `build/libs`.

Copy the JAR to `{CASSANDRA_HOME}/conf/triggers`:

        cp build/libs/cassandra-logger-0.1.jar {CASSANDRA_HOME}/conf/triggers

Start Cassandra (`{CASSANDRA_HOME}/bin/cassandra`) or tell it to reload the triggers:

        nodetool -h localhost reloadtriggers

You should see a line like this at `{CASSANDRA_HOME}/logs/system.log`:

        INFO  [...] 2015-02-26 12:51:09,933 CustomClassLoader.java:87 - Loading new jar /.../apache-cassandra-2.1.3/conf/triggers/cassandra-logger-0.1.jar

### Create the Log Table

By default, the Log table will have the keyspace `logger` and column family `log`. The logger trigger needs both to be created beforehand, otherwise it will fail.

Open the CLI (`{CASSANDRA_HOME}/bin/cassandra-cli`) and run:

        CREATE KEYSPACE logger;
        USE logger;
        
        CREATE TABLE log (
            id timeuuid PRIMARY KEY,
            keyspace text,
            column_family text,
            key text,
            column_name text,
            operation text,
            time timestamp
        ) WITH CLUSTERING ORDER BY (time desc);

### Create Triggers

For each column family you want to log, you need to create a trigger using the following CQL statement:

        CREATE TRIGGER <trigger_name> ON <table> USING 'org.apache.cassandra.logger.LoggerTrigger';

For instance:

        CREATE TRIGGER product_logger ON product USING 'org.apache.cassandra.logger.LoggerTrigger';

If you want to disable this trigger, you can use:

        DROP TRIGGER product_logger ON product;

Running Automated Tests
-----------------------

        gradle test
