"""Process-local registry for cancelling in-flight Flask requests.

The Java Instant BI layer issues a ``requestId`` per client call and can ask
this service to stop work for that id via ``/abort``. Long-running routes such
as ``/interactive`` register the id for the duration of the request and poll
``is_cancelled`` between expensive graph steps.
"""

import logging
import threading
from typing import Any, Optional

logger = logging.getLogger(__name__)


class RequestCancellationRegistry:
    """Thread-safe set of request ids that should stop processing."""

    def __init__(self) -> None:
        self._cancelled: set[str] = set()
        self._active: set[str] = set()
        self._lock = threading.RLock()

    @staticmethod
    def _key(value: Any) -> str:
        return str(value)

    def register(self, request_id: Any) -> None:
        key = self._key(request_id)
        with self._lock:
            self._active.add(key)
            self._cancelled.discard(key)

    def cancel(self, request_id: Any) -> bool:
        key = self._key(request_id)
        with self._lock:
            self._cancelled.add(key)
            was_active = key in self._active
        logger.info("Cancellation registered for requestId=%s active=%s", key, was_active)
        return was_active

    def is_cancelled(self, request_id: Optional[Any]) -> bool:
        if request_id is None:
            return False
        key = self._key(request_id)
        with self._lock:
            return key in self._cancelled

    def clear(self, request_id: Any) -> None:
        key = self._key(request_id)
        with self._lock:
            self._active.discard(key)
            self._cancelled.discard(key)


request_cancellation = RequestCancellationRegistry()
