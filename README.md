CASSANDRA LOGGER
================
[![Build Status](https://travis-ci.org/felipead/cassandra-logger.svg)](https://travis-ci.org/felipead/cassandra-logger)

[Trigger](http://www.datastax.com/dev/blog/whats-new-in-cassandra-2-0-prototype-triggers-support) for [Apache Cassandra](http://cassandra.apache.org) that keeps a log of all updates in a set of tables. Useful to sync Cassandra with other databases, such as Solr, Elasticsearch or even traditional RDBMS.

You can see an example project on how to use this trigger to efficiently synchronize Cassandra and Elasticsearch  [here](http://githu.com/felipead/cassandra-elasticsearch-sync).

REQUIREMENTS
------------

- [Cassandra](http://wiki.apache.org/cassandra/GettingStarted) 2.1+
- [Oracle JDK](http://docs.oracle.com/javase/7/docs/webnotes/install) 7
- [Gradle](http://gradle.org/installation) 2.2

USAGE
-----

For each table you want to log, you need to create a trigger using the following CQL statement:

    CREATE TRIGGER <trigger_name> ON <table> USING 'com.felipead.cassandra.logger.LogTrigger';

For instance:

    CREATE TRIGGER logger ON product USING 'com.felipead.cassandra.logger.LogTrigger';

If you want to disable this trigger, you can use:

    DROP TRIGGER logger ON product;

Every `INSERT`, `UPDATE` or `DELETE` made on a table that has a log trigger enabled will be logged on table `logger.log`.

You can customize the name and keyspace of the log table by editing file [`cassandra-logger.properties`](#customization).

ASSUMPTIONS ABOUT YOUR SCHEMA
-----------------------------

The logger currently does not support tables with composite primary keys. Please make sure all primary keys for the tables you want to log have one single column.

By default, any column named `timestamp` will not be logged. The logger assumes this is an audit timestamp, which does not make sense to be logged. You can easily customize which columns to ignore editing the `cassandra-logger.properties`.

All column names will be logged in lower case.

SETUP
-----

### Installing Cassandra

The trigger API was released as part of Cassandra 2.0. However, it was changed after Cassandra 2.1. This trigger *will not work* with versions of Cassandra previous to 2.1.

Please follow the instructions from the Cassandra project [website](http://wiki.apache.org/cassandra/GettingStarted).

### Installing the Log Trigger *Automagically*

The script [`install-cassandra-trigger.sh`](install-cassandra-trigger.sh) will build and install the trigger on Cassandra *automagically*:

    ./install-cassandra-trigger {CASSANDRA_HOME}

where `{CASSANDRA_HOME}` is the root of your Cassandra installation. This directory needs to be writable by the user.

*Please notice that the trigger needs to be installed on every node of your cluster.*

### Installing the Log Trigger *Manually*

In case you are deploying to a multi-node clustered environment or need to troubleshoot the installation, you can install the trigger manually.

1. Build the jar:

        gradle jar

2. If compilation is successful, copy the jar from `build/libs` and put it inside Cassandra's triggers folder:

        cp build/libs/cassandra-logger.jar {CASSANDRA_HOME}/conf/triggers

3. Start Cassandra. If it is already running, you can force reloading of the triggers by using:

        {CASSANDRA_HOME}/bin/nodetool -h localhost reloadtriggers

4. You should see a line like this at `{CASSANDRA_HOME}/logs/system.log`:

        INFO  [...] 2015-02-26 12:51:09,933 CustomClassLoader.java:87 - Loading new jar /.../apache-cassandra-2.1.3/conf/triggers/cassandra-logger.jar

### Create the Log Schema

*Before using the trigger you MUST create the log table schema.*

To do this, load script [`create-log-schema.cql`](create-log-schema.cql) into CQL shell:
 
    {CASSANDRA_HOME}/bin/cqlsh --file create-log-schema.sql

To make sure it was created correctly, enter CQL shell and run:

    DESCRIBE TABLE logger.log

By default, the logger will use table `log` and keyspace `logger`. You can customize this by editing `cassandra-logger.properties`.

<a name="customization">CUSTOMIZATION</a>
------------------------------------------

In order to customize the names of the keyspace and table used by the logger, copy the file [`cassandra-logger.properties`](config/cassandra-logger.properties) to `{CASSANDRA_HOME}/conf` and edit it to better suit your needs. The installation script will copy this file for you automatically.

*If you change the default keyspace or table names, you need to recreate the log schema with those names.*

EXAMPLES
--------

For illustration purposes, let's create an example schema:

    CREATE KEYSPACE example WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };
    USE example;

Suppose we have the following example table:
        
    CREATE TABLE product (id uuid PRIMARY KEY, name text, price decimal);

Let's add a log trigger to it:

    CREATE TRIGGER logger ON product USING 'com.felipead.cassandra.logger.LogTrigger';
 
We then create some products:
 
    INSERT INTO product (id, name, price) values (uuid(), 't-shirt', 49.99);
    INSERT INTO product (id, name, price) values (uuid(), 'jeans', 99.99);
    INSERT INTO product (id, name, price) values (uuid(), 'socks', 9.99);
        
Which gives us:
 
     id                                   | name    | price
    --------------------------------------+---------+-------
     47e50ec8-4448-4c35-a975-e0d2effccc5d |   socks |  9.99
     00672db2-6df8-48ad-b18c-75d9ca74006e | t-shirt | 49.99
     e4780d51-f240-410f-b1c6-099f9414198c |   jeans | 99.99


Now, querying the log table we can see that there's an entry for each product we created:

     logged_keyspace | logged_table | logged_key                           | time_uuid                            | operation | dateOf(time_uuid)        | updated_columns
    -----------------+--------------+--------------------------------------+--------------------------------------+-----------+--------------------------+-------------------
             example |      product | 00672db2-6df8-48ad-b18c-75d9ca74006e | b63e4660-bfc3-11e4-bbfe-ef9f87394ca6 |      save | 2015-03-01 00:33:22-0300 | {'name', 'price'}
             example |      product | 47e50ec8-4448-4c35-a975-e0d2effccc5d | bc648630-bfc3-11e4-bbfe-ef9f87394ca6 |      save | 2015-03-01 00:33:32-0300 | {'name', 'price'}
             example |      product | e4780d51-f240-410f-b1c6-099f9414198c | b96501d0-bfc3-11e4-bbfe-ef9f87394ca6 |      save | 2015-03-01 00:33:27-0300 | {'name', 'price'}
     
Let's update the price of a product:

    UPDATE product SET price=14.99 WHERE id=47e50ec8-4448-4c35-a975-e0d2effccc5d;
    
The log table now contains an entry that accounts for the update of the price column:

     logged_keyspace | logged_table | logged_key                           | time_uuid                            | operation | dateOf(time_uuid)        | updated_columns
    -----------------+--------------+--------------------------------------+--------------------------------------+-----------+--------------------------+-------------------
             example |      product | 00672db2-6df8-48ad-b18c-75d9ca74006e | b63e4660-bfc3-11e4-bbfe-ef9f87394ca6 |      save | 2015-03-01 00:33:22-0300 | {'name', 'price'}
             example |      product | 47e50ec8-4448-4c35-a975-e0d2effccc5d | f6969870-bfc3-11e4-bbfe-ef9f87394ca6 |      save | 2015-03-01 00:35:10-0300 |         {'price'}
             example |      product | 47e50ec8-4448-4c35-a975-e0d2effccc5d | bc648630-bfc3-11e4-bbfe-ef9f87394ca6 |      save | 2015-03-01 00:33:32-0300 | {'name', 'price'}
             example |      product | e4780d51-f240-410f-b1c6-099f9414198c | b96501d0-bfc3-11e4-bbfe-ef9f87394ca6 |      save | 2015-03-01 00:33:27-0300 | {'name', 'price'}

Let's delete one product:

    DELETE FROM product WHERE id=00672db2-6df8-48ad-b18c-75d9ca74006e;
    
The log table now contains a delete entry:

     logged_keyspace | logged_table | logged_key                           | time_uuid                            | operation | dateOf(time_uuid)        | updated_columns
    -----------------+--------------+--------------------------------------+--------------------------------------+-----------+--------------------------+-------------------
             example |      product | 00672db2-6df8-48ad-b18c-75d9ca74006e | 6110e570-bfc4-11e4-bbfe-ef9f87394ca6 |    delete | 2015-03-01 00:38:09-0300 |              null
             example |      product | 00672db2-6df8-48ad-b18c-75d9ca74006e | b63e4660-bfc3-11e4-bbfe-ef9f87394ca6 |      save | 2015-03-01 00:33:22-0300 | {'name', 'price'}
             example |      product | 47e50ec8-4448-4c35-a975-e0d2effccc5d | f6969870-bfc3-11e4-bbfe-ef9f87394ca6 |      save | 2015-03-01 00:35:10-0300 |         {'price'}
             example |      product | 47e50ec8-4448-4c35-a975-e0d2effccc5d | bc648630-bfc3-11e4-bbfe-ef9f87394ca6 |      save | 2015-03-01 00:33:32-0300 | {'name', 'price'}
             example |      product | e4780d51-f240-410f-b1c6-099f9414198c | b96501d0-bfc3-11e4-bbfe-ef9f87394ca6 |      save | 2015-03-01 00:33:27-0300 | {'name', 'price'}
             
You can filter log entries by the time they were created using `minTimeuuid` and `maxTimeuuid`:

    SELECT * FROM logger.log WHERE time_uuid >= minTimeuuid('2015-03-01 00:35:00-0300') ALLOW FILTERING;


     logged_keyspace | logged_table | logged_key                           | time_uuid                            | operation | dateOf(time_uuid)        | updated_columns
    -----------------+--------------+--------------------------------------+--------------------------------------+-----------+--------------------------+-----------------
             example |      product | 00672db2-6df8-48ad-b18c-75d9ca74006e | 6110e570-bfc4-11e4-bbfe-ef9f87394ca6 |    delete | 2015-03-01 00:38:09-0300 |            null
             example |      product | 47e50ec8-4448-4c35-a975-e0d2effccc5d | f6969870-bfc3-11e4-bbfe-ef9f87394ca6 |      save | 2015-03-01 00:35:10-0300 |       {'price'}

Another example:    
    
    SELECT * FROM logger.log WHERE time_uuid < maxTimeuuid('2015-03-01 00:34:00-0300') ALLOW FILTERING;


     logged_keyspace | logged_table | logged_key                           | time_uuid                            | operation | dateOf(time_uuid)        | updated_columns
    -----------------+--------------+--------------------------------------+--------------------------------------+-----------+--------------------------+-------------------
             example |      product | 00672db2-6df8-48ad-b18c-75d9ca74006e | b63e4660-bfc3-11e4-bbfe-ef9f87394ca6 |      save | 2015-03-01 00:33:22-0300 | {'name', 'price'}
             example |      product | 47e50ec8-4448-4c35-a975-e0d2effccc5d | bc648630-bfc3-11e4-bbfe-ef9f87394ca6 |      save | 2015-03-01 00:33:32-0300 | {'name', 'price'}
             example |      product | e4780d51-f240-410f-b1c6-099f9414198c | b96501d0-bfc3-11e4-bbfe-ef9f87394ca6 |      save | 2015-03-01 00:33:27-0300 | {'name', 'price'}

You can also search log entries from a particular row:

    SELECT * FROM logger.log WHERE logged_keyspace='example' AND logged_table='product' AND logged_key='47e50ec8-4448-4c35-a975-e0d2effccc5d';


     logged_keyspace | logged_table | logged_key                           | time_uuid                            | operation | dateOf(time_uuid)        | updated_columns
    -----------------+--------------+--------------------------------------+--------------------------------------+-----------+--------------------------+-------------------
             example |      product | 47e50ec8-4448-4c35-a975-e0d2effccc5d | f6969870-bfc3-11e4-bbfe-ef9f87394ca6 |      save | 2015-03-01 00:35:10-0300 |         {'price'}
             example |      product | 47e50ec8-4448-4c35-a975-e0d2effccc5d | bc648630-bfc3-11e4-bbfe-ef9f87394ca6 |      save | 2015-03-01 00:33:32-0300 | {'name', 'price'}


AUTOMATED TESTS
---------------

### Running Unit Tests (Java)

    gradle test

You need to have Cassandra running and the log schema created, otherwise tests will fail.

### Running Black Box Integration Tests (Python)

You need to have Python 2.7+ installed with pip.

1. Create a virtual environment (optional):

        virtualenv env
        source env/bin/activate

2. Install Python dependencies through `pip`:

        pip install -r integration-tests/requirements.txt
        
3. Execute the [`run-integration-tests.sh`](run-integration-tests.sh) script passing the location of your Cassandra installation:

        ./run-integration-tests {CASSANDRA_HOME}
        
**WARNING:** the script will wipe the `logger` keyspace. You will lose all your logged data.
