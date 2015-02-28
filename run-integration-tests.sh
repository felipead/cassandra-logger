#!/bin/sh
set -e

cassandra_dir=${1%/}

echo "Installing trigger..."
sh install-cassandra-trigger.sh ${cassandra_dir}

user=`whoami`
cassandra_pid=`pgrep -u ${user} -f cassandra`

if [ -z "${cassandra_pid}" ]; then
    echo "Cassandra is not running... Please start it manually then run this script again."
    exit 1
fi

echo "Creating log schema..."
${cassandra_dir}/bin/cqlsh --file create-log-schema.cql

echo "Running tests..."
cd integration-tests
py.test
