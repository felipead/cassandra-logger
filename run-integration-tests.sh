#!/bin/sh
set -e

if ! type "py.test"; then
	echo "py.test not found. Make sure both Python and requirements.txt are installed or the virtual environment is activated."
	exit 1
fi

cassandra_dir=${1%/}

sh install-cassandra-trigger.sh ${cassandra_dir}

user=`whoami`
cassandra_pid=`pgrep -u ${user} -f cassandra || true`

if [ -z "${cassandra_pid}" ]; then
    echo "Cassandra is not running... Please start it manually then run this script again."
    exit 1
fi

echo "Creating log schema..."
${cassandra_dir}/bin/cqlsh --file create-log-schema.cql

echo "Running tests..."
cd integration-tests
py.test
