"""Designer apply-reset tile emitted on a freefloat dashboard that has filters."""
from __future__ import annotations

import secrets
import string
from typing import Any

from helicalbi.model.output.dashboard.Kind import Kind
from helicalbi.model.output.dashboard.Layout import Layout

_SUFFIX = string.ascii_letters


class ApplyResetComponent:
    """Skeleton for one apply-reset control on the dashboard grid."""

    kind = Kind.APPLY_RESET

    def __init__(self, *, x: int = 0, y: int = 0, w: int = 1, h: int = 1, item_id: str = "") -> None:
        self.id = item_id or f"item-{''.join(secrets.choice(_SUFFIX) for _ in range(5))}"
        self.x = int(x)
        self.y = int(y)
        self.w = max(1, int(w))
        self.h = max(1, int(h))

    def layout_box(self) -> dict[str, Any]:
        return Layout(self.x, self.y, self.w, self.h, i=self.id, static=False).to_dict()

    def to_dict(self) -> dict[str, Any]:
        return {
            "id": self.id,
            "component_id": self.id,
            "itemId": self.id,
            "kind": str(self.kind),
            "isGrouped": False,
            "isSaved": False,
            "layout": self.layout_box(),
        }
