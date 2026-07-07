"""Functional tests for the memory factory layer."""
from unittest.mock import patch

import pytest

from helicalbi.memory.MemoryFactoryResolver import MemoryFactoryResolver
from helicalbi.memory.SafeMemoryInitializer import SafeMemoryInitializer
from helicalbi.memory.storagetype.InMemoryFactory import InMemoryFactory
from helicalbi.memory.storagetype.PostgresFactory import PostgresFactory
from helicalbi.memory.storagetype.SqliteFactory import SqliteFactory


pytestmark = pytest.mark.functional


class TestMemoryFactoryResolver:
    def test_resolves_memory_provider(self):
        assert isinstance(
            MemoryFactoryResolver.get_factory({"provider": "memory"}),
            InMemoryFactory,
        )

    def test_defaults_to_memory_when_provider_absent(self):
        assert isinstance(MemoryFactoryResolver.get_factory({}), InMemoryFactory)

    def test_resolves_sqlite_provider(self):
        cfg = {"provider": "sqlite", "sqlite": {"db_path": "/tmp/test.db"}}
        factory = MemoryFactoryResolver.get_factory(cfg)
        assert isinstance(factory, SqliteFactory)

    def test_resolves_postgres_provider(self):
        cfg = {
            "provider": "postgres",
            "postgres": {
                "host": "localhost",
                "port": 5432,
                "database": "x",
                "user": "u",
                "password": "p",
                "schema": "public",
            },
        }
        factory = MemoryFactoryResolver.get_factory(cfg)
        assert isinstance(factory, PostgresFactory)

    def test_unknown_provider_raises(self):
        with pytest.raises(ValueError, match="Unsupported memory provider"):
            MemoryFactoryResolver.get_factory({"provider": "mongo"})


class TestInMemoryFactory:
    def test_create_saver_returns_memory_saver(self):
        saver = InMemoryFactory().create_saver()
        from langgraph.checkpoint.memory import MemorySaver

        assert isinstance(saver, MemorySaver)


class TestSafeMemoryInitializer:
    def test_returns_memory_saver_on_success(self):
        from langgraph.checkpoint.memory import MemorySaver

        saver = SafeMemoryInitializer.initialize({"provider": "memory"})
        assert isinstance(saver, MemorySaver)

    def test_falls_back_to_in_memory_on_failure(self):
        from langgraph.checkpoint.memory import MemorySaver

        with patch.object(
            MemoryFactoryResolver,
            "get_factory",
            side_effect=RuntimeError("boom"),
        ):
            saver = SafeMemoryInitializer.initialize({"provider": "memory"})
        assert isinstance(saver, MemorySaver)
