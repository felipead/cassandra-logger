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
        )
        """)

    session.execute("CREATE TRIGGER IF NOT EXISTS logger ON product USING '%s'" % LOG_TRIGGER_NAME)


def create_product(session, _id, name, quantity):
    statement = session.prepare(
        """
        INSERT INTO product (id, name, quantity)
        VALUES (?, ?, ?)
        """)
    session.execute(statement, [_id, name, quantity])


def select_log_entries(session, log_table_identifier):
    return session.execute(
        """
        SELECT logged_keyspace, logged_table, logged_key, time, operation, updated_columns FROM %s
        """
        % log_table_identifier
    )


def select_log_entries_from_logged_key(session, log_table_identifier, logged_keyspace, logged_table, logged_key):
    statement = session.prepare(
        """
        SELECT logged_keyspace, logged_table, logged_key, time, operation, updated_columns FROM %s
        WHERE logged_keyspace = ? AND logged_table = ? AND logged_key = ?
        """
        % log_table_identifier)
    return session.execute(statement, [logged_keyspace, logged_table, logged_key])


# noinspection PyClassHasNoInit,PyMethodMayBeStatic,PyShadowingNames
@pytest.mark.usefixtures("create_fixture_keyspace", "create_fixture_schema")
class TestLogTrigger:

    def test_create_log_entry_on_insert(self, session, fixture_keyspace, log_table_identifier):
        create_time = datetime.now()

        shirt_id = uuid.uuid4()
        create_product(session, shirt_id, "shirt", 20)
        socks_id = uuid.uuid4()
        create_product(session, socks_id, "socks", 5)
        jeans_id = uuid.uuid4()
        create_product(session, jeans_id, "jeans", 10)
        product_ids = [shirt_id, socks_id, jeans_id]

        log_entries = select_log_entries(session, log_table_identifier)
        assert log_entries >= len(product_ids)

        matched_log_entries = 0
        for (logged_keyspace, logged_table, logged_key, log_time, operation, updated_columns) in log_entries:
            if uuid.UUID(logged_key) in product_ids:
                assert logged_keyspace == fixture_keyspace
                assert logged_table == "product"
                assert log_time >= create_time
                assert operation == "save"
                assert_that(updated_columns, has_items("name", "quantity"))
                matched_log_entries += 1

        assert matched_log_entries == len(product_ids)

    def test_create_log_entry_on_update(self, session, fixture_keyspace, log_table_identifier):
        product_id = uuid.uuid4()
        create_product(session, product_id, "shirt", 20)

        update_time = datetime.now()
        update_quantity_statement = session.prepare("UPDATE product SET quantity=? WHERE id=?")
        session.execute(update_quantity_statement, [10, product_id])

        log_entries = select_log_entries_from_logged_key(session, log_table_identifier,
                                                         fixture_keyspace, "product", str(product_id))
        assert len(log_entries) == 2

        matched_log_entries = 0
        matched_quantity_columns = 0
        matched_name_columns = 0
        for (logged_keyspace, logged_table, logged_key, log_time, operation, updated_columns) in log_entries:
            assert logged_keyspace == fixture_keyspace
            assert logged_table == "product"
            assert logged_key == str(product_id)
            assert log_time >= update_time
            assert operation == "save"
            if "quantity" in updated_columns:
                matched_quantity_columns += 1
            if "name" in updated_columns:
                matched_name_columns += 1
            matched_log_entries += 1

        assert matched_log_entries == len(log_entries)
        assert matched_quantity_columns == 2
        assert matched_name_columns == 1

    def test_create_log_entry_on_delete(self, session, fixture_keyspace, log_table_identifier):
        product_id = uuid.uuid4()
        create_product(session, product_id, "shirt", 20)

        delete_time = datetime.now()
        delete_product_statement = session.prepare("DELETE FROM product WHERE id=?")
        session.execute(delete_product_statement, [product_id])

        log_entries = select_log_entries_from_logged_key(session, log_table_identifier,
                                                         fixture_keyspace, "product", str(product_id))

        assert len(log_entries) == 2

        matched_log_entries = 0
        matched_delete_operations = 0
        for (logged_keyspace, logged_table, logged_key, log_time, operation, updated_columns) in log_entries:
            assert logged_keyspace == fixture_keyspace
            assert logged_table == "product"
            assert logged_key == str(product_id)
            assert log_time >= delete_time
            if operation == "delete":
                matched_delete_operations += 1
            matched_log_entries += 1

        assert matched_log_entries == len(log_entries)
        assert matched_delete_operations == 1
