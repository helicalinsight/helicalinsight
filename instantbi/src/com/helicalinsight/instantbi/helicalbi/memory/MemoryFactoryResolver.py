from helicalbi.memory.MemoryFactory import MemoryFactory
from helicalbi.memory.storagetype.InMemoryFactory import InMemoryFactory
from helicalbi.memory.storagetype.PostgresFactory import PostgresFactory
from helicalbi.memory.storagetype.SqliteFactory import SqliteFactory


class MemoryFactoryResolver:

    @staticmethod
    def get_factory(config: dict) -> MemoryFactory:
        provider = config.get("provider", "memory")

        if provider == "memory":
            return InMemoryFactory()

        elif provider == "sqlite":
            return SqliteFactory(config["sqlite"])

        elif provider == "postgres":
            return PostgresFactory(config["postgres"])

        else:
            raise ValueError(f"Unsupported memory provider: {provider}")