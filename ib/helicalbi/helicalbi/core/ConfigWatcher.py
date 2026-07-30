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


class _Handler(FileSystemEventHandler):
    def __init__(self, watched_path: str, on_change) -> None:
        super().__init__()
        self._watched_path = os.path.abspath(watched_path)
        self._on_change = on_change

    def on_modified(self, event):
        if not event.is_directory and os.path.abspath(event.src_path) == self._watched_path:
            self._on_change()

    # vim / JetBrains do delete + create instead of in-place modify
    on_created = on_modified


class ConfigWatcher:
    """
    Monitors *config_filename* (relative name, resolved via ConfigLoader) and
    calls *on_change()* on every detected file-write.

    The internal Observer thread is a daemon and will be cleaned up automatically
    when the process exits.
    """

    def __init__(self, config_filename: str, on_change) -> None:
        self._config_path = ConfigLoader.resolve_path(config_filename)
        self._on_change = on_change
        self._observer: Observer | None = None
        self._lock = threading.Lock()

    def start(self) -> None:
        with self._lock:
            if self._observer is not None:
                return  # already running
            watch_dir = os.path.dirname(self._config_path)
            handler = _Handler(self._config_path, self._on_change)
            observer = Observer()
            observer.schedule(handler, path=watch_dir, recursive=False)
            observer.daemon = True
            observer.start()
            self._observer = observer
            logger.debug("ConfigWatcher started for %s", self._config_path)

    def stop(self) -> None:
        with self._lock:
            if self._observer is None:
                return
            self._observer.stop()
            self._observer.join()
            self._observer = None
            logger.debug("ConfigWatcher stopped for %s", self._config_path)
