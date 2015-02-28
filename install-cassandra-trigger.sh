#!/bin/sh

set -e

jar_file="cassandra-logger*.jar"
settings_file="cassandra-logger.properties"
gradle_build_dir="build/libs"

if [ ! $1 ]; then
	echo "First argument should be a Cassandra 2.1+ root directory. You need to have write access to it."
	exit 1
fi

cassandra_dir=${1%/}
if [ ! -d ${cassandra_dir} ]; then
	echo "Directory does not exist - ${cassandra_dir}"
	exit 1
fi

cassandra_conf_dir=${cassandra_dir}/conf
cassandra_triggers_dir=${cassandra_conf_dir}/triggers
if [ ! -d ${cassandra_triggers_dir} ]; then
	echo "Triggers directory does not exist ($cassandra_triggers_dir)."
	echo "Are you sure this is a valid Cassandra 2.1+ installation?"
	exit 1
fi

if ! type "gradle" > /dev/null; then
	echo "Gradle command was not found. Make sure it was installed and added to the PATH."
	exit 1
fi

echo "Building jar with Gradle..."
gradle clean jar -q

echo "Uninstalling old jar versions..."
rm -f ${cassandra_triggers_dir}/${jar_file}

echo "Copying new jar into ${cassandra_triggers_dir}..."
cp ${gradle_build_dir}/${jar_file}  ${cassandra_triggers_dir}

if [ ! -f ${cassandra_conf_dir}/${settings_file} ]; then
    echo "Copying settings file ${settings_file} to ${cassandra_conf_dir}..."
    cp config/${settings_file} ${cassandra_conf_dir}
fi

echo "The trigger was successfully installed."

user=`whoami`
cassandra_pid=`pgrep -u ${user} -f cassandra || true`

if [ ! -z "${cassandra_pid}" ]; then
    echo "Cassandra is running for current user with PID ${cassandra_pid}. Atempting to reload triggers..."
    if ${cassandra_dir}/bin/nodetool -h localhost reloadtriggers; then
        echo "Trigger loaded successfuly. You can already use it on the CQL sheel."
    else
        echo "Something went wrong. Could not reload triggers. Try restarting Cassandra manually."
        exit 1
    fi
fi

exit 0