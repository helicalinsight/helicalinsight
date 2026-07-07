from langgraph.checkpoint.sqlite import SqliteSaver

from helicalbi.memory.MemoryFactory import MemoryFactory


class SqliteFactory(MemoryFactory):

    def __init__(self, config):
        self.config = config

    def create_saver(self):
        return SqliteSaver.from_conn_string(
            f"sqlite:///{self.config['db_path']}"
        )