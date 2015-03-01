from time_uuid import TimeUUID


class LogEntry(object):

    logged_keyspace = None
    logged_table = None
    logged_key = None
    time_uuid = None
    time = None
    operation = None
    updated_columns = None

    @property
    def time(self):
        if self.time_uuid is None:
            return None
        else:
            return TimeUUID.upgrade(self.time_uuid).get_datetime()

class LogEntryStore(object):

    def __init__(self, session, log_table_identifier):
        self._session = session
        self._log_table_identifier = log_table_identifier

    def find_all(self):
        rows = self._session.execute(
            """
            SELECT logged_keyspace, logged_table, logged_key, time_uuid, operation, updated_columns FROM %s
            """
            % self._log_table_identifier
        )
        return self._to_log_entries(rows)

    def find_by_logged_key(self, logged_keyspace, logged_table, logged_key):
        statement = self._session.prepare(
            """
            SELECT logged_keyspace, logged_table, logged_key, time_uuid, operation, updated_columns FROM %s
            WHERE logged_keyspace = ? AND logged_table = ? AND logged_key = ?
            """
            % self._log_table_identifier)

        rows = self._session.execute(statement, [logged_keyspace, logged_table, logged_key])
        return self._to_log_entries(rows)

    @staticmethod
    def _to_log_entries(rows):
        log_entries = []
        for (logged_keyspace, logged_table, logged_key, time_uuid, operation, updated_columns) in rows:
            log_entry = LogEntry()
            log_entry.time_uuid = time_uuid
            log_entry.logged_keyspace = logged_keyspace
            log_entry.logged_table = logged_table
            log_entry.logged_key = logged_key
            log_entry.operation = operation
            log_entry.updated_columns = updated_columns
            log_entries.append(log_entry)
        return log_entries
