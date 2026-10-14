"""Per-item dashboard_model emitted on a convert-dashboard item."""
from __future__ import annotations

from typing import Any

from helicalbi.model.output.dashboard.Kind import Kind
from helicalbi.model.output.dashboard.Layout import Layout


class DashboardModel:
    """Kind, title, grid layout, and the extras that belong to that kind."""

    def __init__(
        self,
        kind: Kind | str,
        *,
        title: str = "",
        layout: Layout | None = None,
        css: str = "",
        js: str = "",
        html: str = "",
        export: bool | None = None,
        column: str | None = None,
        table: str | None = None,
        source_item_id: str | None = None,
        default_values: list[Any] | None = None,
    ) -> None:
        wire = Kind.wire(str(kind))
        try:
            self.kind: Kind | str = Kind(wire)
        except ValueError:
            self.kind = wire
        self.title = title
        self.layout = layout or Layout()
        self.css = css
        self.js = js
        self.html = html
        self.export = export
        self.column = column
        self.table = table
        self.source_item_id = source_item_id
        self.default_values = default_values

    def to_dict(self) -> dict[str, Any]:
        model: dict[str, Any] = {
            "kind": str(self.kind),
            "title": self.title,
            "layout": self.layout.to_dict(),
        }
        css = str(self.css or "").strip()
        js = str(self.js or "").strip()
        html = str(self.html or "").strip()
        if css:
            model["css"] = css
        if js:
            model["js"] = js
        if html:
            model["html"] = html
        # Omit default export:true; only emit when export is explicitly disabled.
        if self.kind == Kind.INSTANT and self.export is False:
            model["export"] = False
        if self.kind == Kind.INSTANT_FILTER and self.column is not None:
            model["column"] = self.column
            model["table"] = self.table or ""
            model["sourceItemId"] = self.source_item_id or ""
            model["defaultValues"] = list(self.default_values or [])
        return model
