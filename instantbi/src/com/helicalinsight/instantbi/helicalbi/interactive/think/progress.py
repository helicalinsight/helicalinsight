"""Thin progress emitter for think orchestration stages."""
from __future__ import annotations

from typing import Optional

from helicalbi.interactive.think.constants import ProgressCallback


class ProgressEmitter:
    """Optional callback wrapper — no-op when callback is absent."""

    def __init__(self, on_progress: Optional[ProgressCallback] = None) -> None:
        self._on_progress = on_progress

    def emit(self, stage: str, status: str, message: str) -> None:
        if self._on_progress:
            self._on_progress(stage, status, message)
