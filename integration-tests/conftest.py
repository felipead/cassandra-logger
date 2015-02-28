# Standard py.test configuration file. Fixtures defined here will be available in all modules.

from cassandra.cluster import Cluster
import pytest

from log_entry import LogEntryStore

# This variable tells py.test which files and folders to ignore
collect_ignore = ["env"]


@pytest.fixture(scope="module")
def fixture_keyspace():
    return "logger_test"


@pytest.fixture(scope="module")
def log_keyspace():
    return "logger"


@pytest.fixture(scope="module")
def log_table():
    return "log"


@pytest.fixture(scope="module")
def log_trigger_name():
    return "com.felipead.cassandra.logger.LogTrigger"


# noinspection PyShadowingNames
@pytest.fixture(scope="module")
def log_table_identifier(log_keyspace, log_table):
    return "%s.%s" % (log_keyspace, log_table)


@pytest.fixture(scope="module")
def cluster():
    return Cluster(["127.0.0.1"])


# noinspection PyShadowingNames
@pytest.fixture(scope="module")
def session(cluster):
    return cluster.connect()


# noinspection PyShadowingNames
@pytest.fixture(scope="module")
def log_entry_store(session, log_table_identifier):
    return LogEntryStore(session, log_table_identifier)


# noinspection PyShadowingNames
@pytest.fixture(scope="module")
def create_fixture_keyspace(session, fixture_keyspace):
    session.execute("DROP KEYSPACE IF EXISTS %s" % fixture_keyspace)
    session.execute(
        """
        CREATE KEYSPACE %s
        WITH REPLICATION = {'class':'SimpleStrategy', 'replication_factor':1};
        """
        % fixture_keyspace
    )
