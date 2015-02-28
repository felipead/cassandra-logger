from cassandra.cluster import Cluster
import pytest

# py.test will ignore these files and folders
collect_ignore = ["env"]

LOCALHOST = "127.0.0.1"


@pytest.fixture(scope="module")
def fixture_keyspace():
    return "logger_test"


@pytest.fixture(scope="module")
def log_keyspace():
    return "logger"


@pytest.fixture(scope="module")
def log_table():
    return "log"


# noinspection PyShadowingNames
@pytest.fixture(scope="module")
def log_table_identifier(log_keyspace, log_table):
    return "%s.%s" % (log_keyspace, log_table)


@pytest.fixture(scope="module")
def cluster():
    return Cluster([LOCALHOST])


# noinspection PyShadowingNames
@pytest.fixture(scope="module")
def session(cluster):
    return cluster.connect()


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
