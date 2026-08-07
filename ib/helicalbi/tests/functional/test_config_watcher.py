"""Unit tests for ConfigWatcher path matching and editor replace events."""
from types import SimpleNamespace

import pytest

from helicalbi.core.ConfigWatcher import _Handler


pytestmark = pytest.mark.functional


def test_handler_notifies_on_atomic_rename(tmp_path):
    watched = tmp_path / "application_config.yaml"
    watched.write_text("kpi: {}\n", encoding="utf-8")
    calls: list[int] = []
    handler = _Handler(str(watched), lambda: calls.append(1))

    event = SimpleNamespace(
        is_directory=False,
        src_path=str(tmp_path / "application_config.yaml.tmp"),
        dest_path=str(watched),
    )
    handler.on_moved(event)
    assert calls == [1]


def test_handler_notifies_on_in_place_modify(tmp_path):
    watched = tmp_path / "application_config.yaml"
    watched.write_text("kpi: {}\n", encoding="utf-8")
    calls: list[int] = []
    handler = _Handler(str(watched), lambda: calls.append(1))

    event = SimpleNamespace(
        is_directory=False,
        src_path=str(watched),
        dest_path=None,
    )
    handler.on_modified(event)
    assert calls == [1]


def test_handler_ignores_unrelated_file(tmp_path):
    watched = tmp_path / "application_config.yaml"
    watched.write_text("kpi: {}\n", encoding="utf-8")
    calls: list[int] = []
    handler = _Handler(str(watched), lambda: calls.append(1))

    event = SimpleNamespace(
        is_directory=False,
        src_path=str(tmp_path / "other.yaml"),
        dest_path=None,
    )
    handler.on_modified(event)
    assert calls == []
