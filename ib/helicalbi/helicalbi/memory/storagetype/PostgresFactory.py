from langgraph.checkpoint.postgres import PostgresSaver

from helicalbi.memory.MemoryFactory import MemoryFactory


class PostgresFactory(MemoryFactory):

    def __init__(self, config):
        self.config = config

    def create_saver(self):
        cfg = self.config
        conn_str = (
            f"postgresql://{cfg['user']}:{cfg['password']}"
            f"@{cfg['host']}:{cfg['port']}/{cfg['database']}"
        )
        return PostgresSaver.from_conn_string(conn_str)