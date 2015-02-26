#!/bin/sh

if [ ! $1 ]
then
	echo "First argument should be the Cassandra root directory"
	exit 1
fi

cassandra_home=$1
if [ ! -d $cassandra_home ]
then
	echo "Error: directory does not exist - $cassandra_home"
	exit 1
fi

echo "Compiling jar with Gradle..."
if gradle clean assemble -q
then
	triggers_dir="$cassandra_home/conf/triggers"
	mkdir -p $triggers_dir	

	echo "Uninstalling old jar versions..."
	rm -f "$triggers_dir/cassandra-logger*.jar"
	
	echo "Copying jar into Cassandra triggers directory..."
	cp build/libs/cassandra-logger*.jar $triggers_dir

	echo "Asking Cassandra to reload triggers..."
	$cassandra_home/bin/nodetool -h localhost reloadtriggers
else
    echo "Error: Gradle build failed."
    exit 1
fi