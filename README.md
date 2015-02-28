Cassandra Logger
================

[Trigger](http://www.datastax.com/dev/blog/whats-new-in-cassandra-2-0-prototype-triggers-support) for [Apache Cassandra](http://cassandra.apache.org) that keeps a log of all updates in a set of tables. Useful to sync Cassandra with other databases, like Solr or Elastic Search.

Requirements
------------

- [Cassandra](http://wiki.apache.org/cassandra/GettingStarted) 2.1+
- [Oracle JDK](http://docs.oracle.com/javase/7/docs/webnotes/install) 7
- [Gradle](http://gradle.org/installation) 2.2

Setup
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

        cp build/libs/cassandra-logger-snapshot.jar {CASSANDRA_HOME}/conf/triggers

3. Start Cassandra. If it is already running, you can force reloading of the triggers by using:

        {CASSANDRA_HOME}/bin/nodetool -h localhost reloadtriggers

4. You should see a line like this at `{CASSANDRA_HOME}/logs/system.log`:

        INFO  [...] 2015-02-26 12:51:09,933 CustomClassLoader.java:87 - Loading new jar /.../apache-cassandra-2.1.3/conf/triggers/cassandra-logger-snapshot.jar

### Create the Log Schema

*Before using the trigger you MUST create the log table schema.*

To do this, load script [`create-log-schema.cql`](create-log-schema.cql) into CQL shell:
 
    {CASSANDRA_HOME}/bin/cqlsh --file create-log-schema.sql

To make sure it was created correctly, enter CQL shell and run:

    DESCRIBE TABLE logger.log

By default, the logger will use table `log` and keyspace `logger`. You can customize this by editting the settings file.

Usage
-----

For each table you want to log, you need to create a trigger using the following CQL statement:

    CREATE TRIGGER <trigger_name> ON <table> USING 'com.felipead.cassandra.logger.LogTrigger';

For instance:

    CREATE TRIGGER logger ON product USING 'com.felipead.cassandra.logger.LogTrigger';

If you want to disable this trigger, you can use:

    DROP TRIGGER logger ON product;

Customization
-------------

In order to customize the names of the keyspace and table used by the logger, copy the file [`cassandra-logger.properties`](config/cassandra-logger.properties) to `{CASSANDRA_HOME}/conf` and edit it to better suit your needs. The installation script will copy this file for you automatically.

*If you change the default keyspace or table names, you need to recreate the log schema with those names.*

Examples
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
     16afd270-7006-4024-94a6-f1b250381762 |   socks |  9.99
     6fe1dd30-1bbd-4193-b044-7e11e979445c | t-shirt | 49.99
     3d0ca255-b9ae-4eaa-baf7-ac8c4b4279a1 |   jeans | 99.99

Now, querying the log table we can see that there's an entry for each product we created:

     logged_keyspace | logged_table | logged_key                           | time                     | operation | updated_columns
    -----------------+--------------+--------------------------------------+--------------------------+-----------+-------------------
             example |      product | 3d0ca255-b9ae-4eaa-baf7-ac8c4b4279a1 | 2015-02-27 23:41:56-0300 |      save | {'name', 'price'}
             example |      product | 6fe1dd30-1bbd-4193-b044-7e11e979445c | 2015-02-27 23:41:50-0300 |      save | {'name', 'price'}
             example |      product | 16afd270-7006-4024-94a6-f1b250381762 | 2015-02-27 23:42:01-0300 |      save | {'name', 'price'}
     
Let's update the price of a product:

    UPDATE product SET price=14.99 WHERE id=6260b1a1-6f68-4c35-831a-7c38096fcc94;
    
The log table now contains an entry that accounts for the update of the price column:

     logged_keyspace | logged_table | logged_key                           | time                     | operation | updated_columns
    -----------------+--------------+--------------------------------------+--------------------------+-----------+-------------------
             example |      product | 6260b1a1-6f68-4c35-831a-7c38096fcc94 | 2015-02-27 23:43:59-0300 |      save |         {'price'}
             example |      product | 3d0ca255-b9ae-4eaa-baf7-ac8c4b4279a1 | 2015-02-27 23:41:56-0300 |      save | {'name', 'price'}
             example |      product | 6fe1dd30-1bbd-4193-b044-7e11e979445c | 2015-02-27 23:41:50-0300 |      save | {'name', 'price'}
             example |      product | 16afd270-7006-4024-94a6-f1b250381762 | 2015-02-27 23:42:01-0300 |      save | {'name', 'price'}

Let's delete one product:

    DELETE FROM product WHERE id=6260b1a1-6f68-4c35-831a-7c38096fcc94;

The log table now contains a delete entry:

     logged_keyspace | logged_table | logged_key                           | time                     | operation | updated_columns
    -----------------+--------------+--------------------------------------+--------------------------+-----------+-------------------
             example |      product | 6260b1a1-6f68-4c35-831a-7c38096fcc94 | 2015-02-27 23:45:36-0300 |    delete |              null
             example |      product | 6260b1a1-6f68-4c35-831a-7c38096fcc94 | 2015-02-27 23:43:59-0300 |      save |         {'price'}
             example |      product | 3d0ca255-b9ae-4eaa-baf7-ac8c4b4279a1 | 2015-02-27 23:41:56-0300 |      save | {'name', 'price'}
             example |      product | 6fe1dd30-1bbd-4193-b044-7e11e979445c | 2015-02-27 23:41:50-0300 |      save | {'name', 'price'}
             example |      product | 16afd270-7006-4024-94a6-f1b250381762 | 2015-02-27 23:42:01-0300 |      save | {'name', 'price'}
             
You can filter log entries by the time they were created:

    select * from logger.log where time >= '2015-02-27 23:43:00-0300' allow filtering;
    
The above query would give us:

     logged_keyspace | logged_table | logged_key                           | time                     | operation | updated_columns
    -----------------+--------------+--------------------------------------+--------------------------+-----------+-----------------
             example |      product | 6260b1a1-6f68-4c35-831a-7c38096fcc94 | 2015-02-27 23:45:36-0300 |    delete |            null
             example |      product | 6260b1a1-6f68-4c35-831a-7c38096fcc94 | 2015-02-27 23:43:59-0300 |      save |       {'price'}

Running Automated Tests
-----------------------

### Unit Tests (Java)

    gradle test

You need to have Cassandra running and the log schema created, otherwise tests will fail.

### Black Box Integration Tests (Python)

You need to have Python 2.7+ installed with pip.

1. Create a virtual environment (optional):
        
        cd integration-tests
        virtualenv env
        source env/bin/activate
        cd ..

2. Install Python dependencies through pip:

        pip install -r integration-tests/requirements.txt
        
3. Run [`run-integration-tests.sh`](run-integration-tests.sh) script passing the location of your Cassandra installation:

        ./run-integration-tests {CASSANDRA_HOME}
        
**WARNING:** the script will wipe the `logger` keyspace. You will lose all your logged data.
