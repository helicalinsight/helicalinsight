"""Grid rectangle for a dashboard tile."""
from __future__ import annotations

from typing import Any


class Layout:
    """Position and size on the 12-column dashboard grid.

    Designer apply-reset tiles also carry ``i`` and ``static``. Chart and
    filter models omit those keys.
    """

    def __init__(
        self,
        x: Any = 0,
        y: Any = 0,
        w: Any = 0,
        h: Any = 0,
        *,
        i: str | None = None,
        static: bool | None = None,
    ) -> None:
        self.x = int(x or 0)
        self.y = int(y or 0)
        self.w = int(w or 0)
        self.h = int(h or 0)
        self.i = i
        self.static = static

    def to_dict(self) -> dict[str, Any]:
        box: dict[str, Any] = {
            "x": self.x,
            "y": self.y,
            "w": self.w,
            "h": self.h,
        }
        if self.i is not None:
            box["i"] = self.i
        if self.static is not None:
            box["static"] = self.static
        return box
