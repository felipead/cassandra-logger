Cassandra Logger
================

[Trigger](http://www.datastax.com/dev/blog/whats-new-in-cassandra-2-0-prototype-triggers-support) for [Apache Cassandra](http://cassandra.apache.org) that keeps a log of all updates in a set of tables. Useful to sync Cassandra with other databases, like Solr or Elastic Search.

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

Gradle is a build tool for Java, a simplified successor to the well established Maven.

Please follow the instructions from the Gradle project [website](http://gradle.org/installation).

### Installing the Trigger *Automagically*

The script [`install-cassandra-trigger.sh`](install-cassandra-trigger.sh) will build and install the trigger on Cassandra *automagically*:

    ./install-cassandra-trigger {CASSANDRA_HOME}

where `{CASSANDRA_HOME}` is the root of your Cassandra installation. This directory needs to be writable by the user.

*Please notice that the trigger needs to be installed on every node of your cluster.*

### Installing the Trigger *Manually*

In case you are deploying to a multi-node clustered environment or need to troubleshoot the installation, you can install the trigger manually.

1. Build the jar:

        gradle jar

2. If compilation is successful, copy the jar from `build/libs` and put it inside Cassandra's triggers folder:

        cp build/libs/cassandra-logger-snapshot.jar {CASSANDRA_HOME}/conf/triggers

3. Start Cassandra. If it is already running, you can force reloading of the triggers by using:

        {CASSANDRA_HOME}/bin/nodetool -h localhost reloadtriggers

4. You should see a line like this at `{CASSANDRA_HOME}/logs/system.log`:

        INFO  [...] 2015-02-26 12:51:09,933 CustomClassLoader.java:87 - Loading new jar /.../apache-cassandra-2.1.3/conf/triggers/cassandra-logger-snapshot.jar

### Create the Schema

By default, the Logger will use table `log` on keyspace `logger`. You need to create this table manually.

Load script [`create-log-schema.cql`](create-log-schema.cql) into CQL shell to create the schema:
 
    {CASSANDRA_HOME}/bin/cqlsh --file create-log-schema.sql

To make sure it was created correctly, enter CQL shell and run:

    DESCRIBE TABLE logger.log

Usage
-----

For each table you want to log, you need to create a trigger using the following CQL statement:

    CREATE TRIGGER <trigger_name> ON <table> USING 'com.felipead.cassandra.logger.LogTrigger';

For instance:

    CREATE TRIGGER logger ON product USING 'com.felipead.cassandra.logger.LogTrigger';

If you want to disable this trigger, you can use:

    DROP TRIGGER logger ON product;

Examples
--------

Suppose we have the following example table:
        
    CREATE TABLE product (id uuid PRIMARY KEY, name text, price decimal)
    CREATE TRIGGER logger ON product USING 'LogTrigger';
 
We then insert some values into it:
 
    INSERT INTO product (id, name, price) values (uuid(), 't-shirt', 49.99);
    INSERT INTO product (id, name, price) values (uuid(), 'jeans', 99.99);
    INSERT INTO product (id, name, price) values (uuid(), 'socks', 9.99);
        
Which gives:
 
     id                                   | name    | price
    --------------------------------------+---------+-------
     0ee3aeb1-f3bd-445f-b503-5c57b21c1f43 |   jeans | 99.99
     6260b1a1-6f68-4c35-831a-7c38096fcc94 |   socks |  9.99
     33b09808-636a-44b1-9b2f-eb76615f7f34 | t-shirt | 49.99

Now, querying the log table:

     id                                   | logged_key                           | logged_keyspace | logged_table | operation | time                     | updated_columns
    --------------------------------------+--------------------------------------+-----------------+--------------+-----------+--------------------------+-----------------
     8553e510-be4b-11e4-804d-ef9f87394ca6 | 6260b1a1-6f68-4c35-831a-7c38096fcc94 |            test |      product |      save | 2015-02-27 03:40:29-0300 |      name,price
     63e0d0a0-be4b-11e4-804d-ef9f87394ca6 | 0ee3aeb1-f3bd-445f-b503-5c57b21c1f43 |            test |      product |      save | 2015-02-27 03:39:33-0300 |      name,price
     52fc2b40-be4b-11e4-804d-ef9f87394ca6 | 33b09808-636a-44b1-9b2f-eb76615f7f34 |            test |      product |      save | 2015-02-27 03:39:05-0300 |      name,price
     
Let's update the price of a product:

    UPDATE product SET price=14.99 WHERE id=6260b1a1-6f68-4c35-831a-7c38096fcc94;
    
The log table now contains:

     id                                   | logged_key                           | logged_keyspace | logged_table | operation | time                     | updated_columns
    --------------------------------------+--------------------------------------+-----------------+--------------+-----------+--------------------------+-----------------
     a444ed10-be4c-11e4-804d-ef9f87394ca6 | 6260b1a1-6f68-4c35-831a-7c38096fcc94 |            test |      product |      save | 2015-02-27 03:48:31-0300 |           price
     8553e510-be4b-11e4-804d-ef9f87394ca6 | 6260b1a1-6f68-4c35-831a-7c38096fcc94 |            test |      product |      save | 2015-02-27 03:40:29-0300 |      name,price
     63e0d0a0-be4b-11e4-804d-ef9f87394ca6 | 0ee3aeb1-f3bd-445f-b503-5c57b21c1f43 |            test |      product |      save | 2015-02-27 03:39:33-0300 |      name,price
     52fc2b40-be4b-11e4-804d-ef9f87394ca6 | 33b09808-636a-44b1-9b2f-eb76615f7f34 |            test |      product |      save | 2015-02-27 03:39:05-0300 |      name,price

Let's delete one product:

    DELETE FROM product WHERE id=33b09808-636a-44b1-9b2f-eb76615f7f34;

The log table now contains a delete entry:

     id                                   | logged_key                           | logged_keyspace | logged_table | operation | time                     | updated_columns
    --------------------------------------+--------------------------------------+-----------------+--------------+-----------+--------------------------+-----------------
     fb80c630-be4c-11e4-804d-ef9f87394ca6 | 33b09808-636a-44b1-9b2f-eb76615f7f34 |            test |      product |    delete | 2015-02-27 03:50:57-0300 |
     a444ed10-be4c-11e4-804d-ef9f87394ca6 | 6260b1a1-6f68-4c35-831a-7c38096fcc94 |            test |      product |      save | 2015-02-27 03:48:31-0300 |           price
     8553e510-be4b-11e4-804d-ef9f87394ca6 | 6260b1a1-6f68-4c35-831a-7c38096fcc94 |            test |      product |      save | 2015-02-27 03:40:29-0300 |      name,price
     63e0d0a0-be4b-11e4-804d-ef9f87394ca6 | 0ee3aeb1-f3bd-445f-b503-5c57b21c1f43 |            test |      product |      save | 2015-02-27 03:39:33-0300 |      name,price
     52fc2b40-be4b-11e4-804d-ef9f87394ca6 | 33b09808-636a-44b1-9b2f-eb76615f7f34 |            test |      product |      save | 2015-02-27 03:39:05-0300 |      name,price
