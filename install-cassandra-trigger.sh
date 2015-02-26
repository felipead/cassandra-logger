#!/bin/sh

set -e

jar_name="cassandra-logger*.jar"
gradle_build_dir="build/libs"

if [ ! $1 ]; then
	echo "First argument should be a Cassandra 2.1+ root directory. You need to have write access to it."
	exit 1
fi

cassandra_home=$1
if [ ! -d ${cassandra_home} ]; then
	echo "Directory does not exist - $cassandra_home"
	exit 1
fi

triggers_dir="${cassandra_home}/conf/triggers"
if [ ! -d ${triggers_dir} ]; then
	echo "Triggers directory does not exist ($triggers_dir)."
	echo "Are you sure this is a valid Cassandra 2.1+ installation?"
	exit 1
fi

if ! type "gradle" > /dev/null; then
	echo "Gradle command was not found. Make sure it was installed and added to the PATH."
	exit 1
fi

echo "Building jar with Gradle..."
gradle clean assemble -q

echo "Uninstalling old jar versions..."
rm -f ${triggers_dir}/${jar_name}

echo "Copying new jar into Cassandra triggers directory..."
cp ${gradle_build_dir}/${jar_name}  ${triggers_dir}

echo "The trigger was successfully installed."

user=`whoami`
cassandra_pid=`pgrep -u ${user} -f cassandra`

if [ ${cassandra_pid} ]; then
    echo "Cassandra is running for current user with PID ${cassandra_pid}. Atempting to reload triggers..."
    if ${cassandra_home}/bin/nodetool -h localhost reloadtriggers; then
        echo "Trigger loaded successfuly. You can already use it on the CQL sheel."
    else
        echo "Something went wrong. Could not reload triggers. Try restarting Cassandra manually."
        exit 1
    fi
else
    echo "Cassandra is not running. The trigger will be available next time it starts."
fi

exit 0