"""
ConfigWatcher
-------------
Reusable watchdog wrapper that monitors a single YAML config file and calls
an *on_change* callback the moment the OS reports the file was written.

Usage::

    watcher = ConfigWatcher("application_config.yaml", on_change=my_reload_fn)
    watcher.start()          # starts a daemon observer thread
    # later, if you need to stop explicitly:
    watcher.stop()
"""

from __future__ import annotations

import logging
import os
import threading

from watchdog.events import FileSystemEventHandler
from watchdog.observers import Observer

from helicalbi.core.ConfigLoader import ConfigLoader

logger = logging.getLogger(__name__)


def _normalize_path(path: str) -> str:
    if not path:
        return ""
    abs_path = os.path.abspath(path)
    if abs_path.startswith("\\\\?\\"):
        abs_path = abs_path[4:]
    return os.path.normcase(abs_path)


class _Handler(FileSystemEventHandler):
    def __init__(self, watched_path: str, on_change) -> None:
        super().__init__()
        self._watched_path = _normalize_path(watched_path)
        self._on_change = on_change

    def _matches(self, path: str | None) -> bool:
        return bool(path) and _normalize_path(path) == self._watched_path

    def _maybe_notify(self, event) -> None:
        if event.is_directory:
            return
        src = getattr(event, "src_path", None)
        dest = getattr(event, "dest_path", None)
        if self._matches(src) or self._matches(dest):
            self._on_change()

    def on_modified(self, event):
        self._maybe_notify(event)

    def on_created(self, event):
        self._maybe_notify(event)

    # Editors (VS Code / JetBrains / vim) often write a temp file then rename.
    def on_moved(self, event):
        self._maybe_notify(event)

    def on_deleted(self, event):
        # A replace cycle can delete then recreate the watched file.
        self._maybe_notify(event)


class ConfigWatcher:
    """
    Monitors *config_filename* (relative name, resolved via ConfigLoader) and
    calls *on_change()* on every detected file-write.

    The internal Observer thread is a daemon and will be cleaned up automatically
    when the process exits.
    """

    def __init__(
        self,
        config_filename: str,
        on_change,
        *,
        debounce_ms: int = 300,
    ) -> None:
        self._config_path = ConfigLoader.resolve_path(config_filename)
        self._on_change = on_change
        self._debounce_ms = max(0, int(debounce_ms))
        self._observer: Observer | None = None
        self._debounce_timer: threading.Timer | None = None
        self._lock = threading.Lock()

    def start(self) -> None:
        with self._lock:
            if self._observer is not None:
                return  # already running
            watch_dir = os.path.dirname(self._config_path)
            handler = _Handler(self._config_path, self._schedule_change)
            observer = Observer()
            observer.schedule(handler, path=watch_dir, recursive=False)
            observer.daemon = True
            observer.start()
            self._observer = observer
            logger.debug("ConfigWatcher started for %s", self._config_path)

    def stop(self) -> None:
        with self._lock:
            if self._debounce_timer is not None:
                self._debounce_timer.cancel()
                self._debounce_timer = None
            if self._observer is None:
                return
            self._observer.stop()
            self._observer.join()
            self._observer = None
            logger.debug("ConfigWatcher stopped for %s", self._config_path)

    def _schedule_change(self) -> None:
        if self._debounce_ms <= 0:
            self._fire()
            return
        with self._lock:
            if self._debounce_timer is not None:
                self._debounce_timer.cancel()
            timer = threading.Timer(self._debounce_ms / 1000.0, self._fire)
            timer.daemon = True
            timer.start()
            self._debounce_timer = timer

    def _fire(self) -> None:
        with self._lock:
            self._debounce_timer = None
        try:
            self._on_change()
        except Exception:
            logger.exception("ConfigWatcher on_change failed for %s", self._config_path)
