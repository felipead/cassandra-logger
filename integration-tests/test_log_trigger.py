from datetime import datetime
import uuid
import pytest
from hamcrest import *


@pytest.fixture(scope="module")
def create_product_schema(session, fixture_keyspace, log_trigger_name):
    session.set_keyspace(fixture_keyspace)
    session.execute(
        """
        CREATE TABLE product (
          id uuid PRIMARY KEY,
          name text,
          quantity int
        )
        """)

    session.execute("CREATE TRIGGER IF NOT EXISTS logger ON product USING '%s'" % log_trigger_name)


def create_product(session, _id, name, quantity):
    statement = session.prepare(
        """
        INSERT INTO product (id, name, quantity)
        VALUES (?, ?, ?)
        """)
    session.execute(statement, [_id, name, quantity])


# noinspection PyClassHasNoInit,PyMethodMayBeStatic
@pytest.mark.usefixtures("create_fixture_keyspace", "create_product_schema")
class TestLogTrigger:

    def test_create_log_entry_on_insert(self, session, fixture_keyspace, log_entry_store):
        creation_time = datetime.now()

        shirt_id = uuid.uuid4()
        create_product(session, shirt_id, "shirt", 20)
        socks_id = uuid.uuid4()
        create_product(session, socks_id, "socks", 5)
        jeans_id = uuid.uuid4()
        create_product(session, jeans_id, "jeans", 10)
        product_ids = [shirt_id, socks_id, jeans_id]

        matched_log_entries = 0
        for log_entry in log_entry_store.find_all():
            if uuid.UUID(log_entry.logged_key) in product_ids:
                assert log_entry.logged_keyspace == fixture_keyspace
                assert log_entry.logged_table == "product"
                assert log_entry.operation == "save"
                assert log_entry.time >= creation_time
                assert_that(log_entry.updated_columns, has_items("name", "quantity"))
                matched_log_entries += 1

        assert matched_log_entries == len(product_ids)

    def test_create_log_entry_on_update(self, session, fixture_keyspace, log_entry_store):
        product_id = uuid.uuid4()
        create_product(session, product_id, "shirt", 20)

        update_time = datetime.now()
        update_quantity_statement = session.prepare("UPDATE product SET quantity=? WHERE id=?")
        session.execute(update_quantity_statement, [10, product_id])

        log_entries = log_entry_store.find_by_logged_key(fixture_keyspace, "product", str(product_id))
        assert len(log_entries) == 2

        matched_log_entries = 0
        matched_quantity_columns = 0
        matched_name_columns = 0
        for log_entry in log_entries:
            assert log_entry.logged_keyspace == fixture_keyspace
            assert log_entry.logged_table == "product"
            assert log_entry.logged_key == str(product_id)
            assert log_entry.time >= update_time
            assert log_entry.operation == "save"
            if "quantity" in log_entry.updated_columns:
                matched_quantity_columns += 1
            if "name" in log_entry.updated_columns:
                matched_name_columns += 1
            matched_log_entries += 1

        assert matched_log_entries == len(log_entries)
        assert matched_quantity_columns == 2
        assert matched_name_columns == 1

    def test_create_log_entry_on_delete(self, session, fixture_keyspace, log_entry_store):
        product_id = uuid.uuid4()
        create_product(session, product_id, "shirt", 20)

        deletion_time = datetime.now()
        delete_product_statement = session.prepare("DELETE FROM product WHERE id=?")
        session.execute(delete_product_statement, [product_id])

        log_entries = log_entry_store.find_by_logged_key(fixture_keyspace, "product", str(product_id))
        assert len(log_entries) == 2

        matched_log_entries = 0
        matched_delete_operations = 0
        for log_entry in log_entries:
            assert log_entry.logged_keyspace == fixture_keyspace
            assert log_entry.logged_table == "product"
            assert log_entry.logged_key == str(product_id)
            assert log_entry.time >= deletion_time
            if log_entry.operation == "delete":
                matched_delete_operations += 1
            matched_log_entries += 1

        assert matched_log_entries == len(log_entries)
        assert matched_delete_operations == 1
