"""Functional tests for ``helicalbi.api.ApiCallCache``."""
import pytest

from helicalbi.api.ApiCallCache import clear as clear_api_cache
from helicalbi.api.ApiCallCache import get as cache_get
from helicalbi.api.ApiCallCache import set as cache_set
from helicalbi.common import app_config


pytestmark = pytest.mark.functional


@pytest.fixture(autouse=True)
def _reset_api_cache():
    clear_api_cache()
    original_enabled = app_config.api_cache_enabled
    original_max_entries = app_config.api_cache_max_entries
    app_config.api_cache_enabled = True
    app_config.api_cache_max_entries = 100
    yield
    clear_api_cache()
    app_config.api_cache_enabled = original_enabled
    app_config.api_cache_max_entries = original_max_entries


class TestApiCallCache:
    def test_stores_and_returns_cached_response(self):
        cache_set('{"a": 1}', "alice", "acme", {"status": 1, "response": {"ok": True}})
        cached = cache_get('{"a": 1}', "alice", "acme")
        assert cached == {"status": 1, "response": {"ok": True}}

    def test_disabled_cache_does_not_store_or_return(self, monkeypatch):
        monkeypatch.setattr(app_config, "api_cache_enabled", False)
        cache_set('{"a": 1}', "alice", "acme", {"status": 1})
        assert cache_get('{"a": 1}', "alice", "acme") is None

    def test_evicts_oldest_entry_when_max_entries_reached(self, monkeypatch):
        monkeypatch.setattr(app_config, "api_cache_max_entries", 2)

        cache_set('{"entry": 1}', "alice", "acme", {"status": 1, "response": {"id": 1}})
        cache_set('{"entry": 2}', "alice", "acme", {"status": 1, "response": {"id": 2}})
        cache_set('{"entry": 3}', "alice", "acme", {"status": 1, "response": {"id": 3}})

        assert cache_get('{"entry": 1}', "alice", "acme") is None
        assert cache_get('{"entry": 2}', "alice", "acme") == {"status": 1, "response": {"id": 2}}
        assert cache_get('{"entry": 3}', "alice", "acme") == {"status": 1, "response": {"id": 3}}

    def test_get_refreshes_entry_so_it_is_not_evicted_next(self, monkeypatch):
        monkeypatch.setattr(app_config, "api_cache_max_entries", 2)

        cache_set('{"entry": 1}', "alice", "acme", {"status": 1, "response": {"id": 1}})
        cache_set('{"entry": 2}', "alice", "acme", {"status": 1, "response": {"id": 2}})
        assert cache_get('{"entry": 1}', "alice", "acme") == {"status": 1, "response": {"id": 1}}
        cache_set('{"entry": 3}', "alice", "acme", {"status": 1, "response": {"id": 3}})

        assert cache_get('{"entry": 1}', "alice", "acme") == {"status": 1, "response": {"id": 1}}
        assert cache_get('{"entry": 2}', "alice", "acme") is None
        assert cache_get('{"entry": 3}', "alice", "acme") == {"status": 1, "response": {"id": 3}}

    def test_clear_returns_removed_entry_count(self):
        cache_set('{"a": 1}', "alice", "acme", {"status": 1})
        cache_set('{"b": 2}', "alice", "acme", {"status": 1})
        assert clear_api_cache() == 2
        assert clear_api_cache() == 0
