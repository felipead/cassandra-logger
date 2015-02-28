from datetime import datetime
import uuid

import pytest
from hamcrest import *


LOG_TRIGGER_NAME = "com.felipead.cassandra.logger.LogTrigger"


# noinspection PyShadowingNames
@pytest.fixture(scope="module")
def create_fixture_schema(session, fixture_keyspace):
    session.set_keyspace(fixture_keyspace)
    session.execute(
        """
        CREATE TABLE product (
          id uuid PRIMARY KEY,
          name text,
          quantity int
        );
        """)

    session.execute("CREATE TRIGGER IF NOT EXISTS logger ON product USING '%s'" % LOG_TRIGGER_NAME)


@pytest.fixture
def insert_product_statement(session):
    return session.prepare(
        """
            INSERT INTO product (id, name, quantity)
            VALUES (?, ?, ?)
            """)


# noinspection PyClassHasNoInit,PyMethodMayBeStatic,PyShadowingNames
@pytest.mark.usefixtures("create_fixture_keyspace", "create_fixture_schema")
class TestLogTrigger:

    def select_log_entries(self, session, log_table_identifier):
        return session.execute(
            """
            SELECT logged_keyspace, logged_table, logged_key, time, operation, updated_columns FROM %s
            """
            % log_table_identifier
        )

    def test_create_log_entry_on_insert(self, session, fixture_keyspace,
                                        log_table_identifier, insert_product_statement):

        now = datetime.now()

        shirt_id = uuid.uuid4()
        session.execute(insert_product_statement, [shirt_id, "shirt", 20])
        socks_id = uuid.uuid4()
        session.execute(insert_product_statement, [socks_id, "socks", 5])
        jeans_id = uuid.uuid4()
        session.execute(insert_product_statement, [jeans_id, "jeans", 10])
        product_ids = [shirt_id, socks_id, jeans_id]

        log_entries = self.select_log_entries(session, log_table_identifier)
        assert log_entries >= len(product_ids)

        matched_rows = 0
        for (logged_keyspace, logged_table, logged_key, time, operation, updated_columns) in log_entries:
            if uuid.UUID(logged_key) in product_ids:
                assert logged_keyspace == fixture_keyspace
                assert logged_table == "product"
                assert operation == "save"
                assert time > now
                assert_that(updated_columns, has_items("name", "quantity"))
                matched_rows += 1

        assert matched_rows == len(product_ids)

    def test_create_log_entry_on_update(self):
        pass

    def test_create_log_entry_on_delete(self):
        pass
