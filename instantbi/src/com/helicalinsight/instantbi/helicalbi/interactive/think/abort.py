"""Request cancellation guard for think turns."""
from __future__ import annotations

from typing import Optional

from helicalbi.common.RequestCancellation import request_cancellation
from helicalbi.controller.helpers import RequestAborted


class AbortGuard:
    """Single responsibility: raise when the request was cancelled."""

    def __init__(self, request_id: Optional[str]) -> None:
        self.request_id = request_id

    def ensure(self) -> None:
        if self.request_id and request_cancellation.is_cancelled(self.request_id):
            raise RequestAborted(f"Request {self.request_id} was cancelled")


def ensure_not_aborted(request_id: Optional[str]) -> None:
    AbortGuard(request_id).ensure()
