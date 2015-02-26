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

triggers_dir="$cassandra_home/conf/triggers"
if [ ! -d $triggers_dir ]
then 
	echo "Triggers directory does not exist ($triggers_dir)."
	echo "Are you sure this is a valid Cassandra 2.1+ installation?"
	exit 1
fi

echo "Compiling jar with Gradle..."
if gradle clean assemble -q
then
	echo "Uninstalling old jar versions..."
	rm -f "$triggers_dir/cassandra-logger*.jar"
	
	echo "Copying jar into Cassandra triggers directory..."
	cp build/libs/cassandra-logger*.jar $triggers_dir

	user=`whoami`
	cassandra_pid=`pgrep -u $user -f cassandra`
	if [ $cassandra_pid ]
	then
		echo "Cassandra is running with PID $cassandra_pid."
		echo "Reloading triggers..."
		if $cassandra_home/bin/nodetool -h localhost reloadtriggers
		then
			echo "Trigger installed and loaded successfuly. You can already use it on the CQL sheel."
		else
			echo "Something went wrong. Could not reload triggers."
		fi
	else
		echo "Cassandra is not running. You can start it with $cassandra_home/bin/cassandra."
		echo "The trigger was successfully installed nevertheless."
	fi
else
    echo "Error: Gradle build failed."
    exit 1
fi