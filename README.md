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

If the compilation is successful, the resulting JAR will be available at `build/libs/cassandra-logger-<version>.jar`.

Copy the JAR to `{CASSANDRA_HOME}/conf/triggers`:

    cp build/libs/cassandra-logger-0.1.jar {CASSANDRA_HOME}/conf/triggers

### Installing the Trigger


Running Automated Tests
-----------------------

    gradle test
