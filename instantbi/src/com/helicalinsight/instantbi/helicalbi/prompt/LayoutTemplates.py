"""ASCII dashboard layout sketches and the template decision table."""
from __future__ import annotations

DECISION_TABLE = """
Use this table to pick exactly one templateId before placing widgets.
If the mix is imperfect, choose the closest fit. Do not invent charts to fill empty slots.

templateId                 | Pick when
---------------------------|--------------------------------------------------------------
executive-kpi-first        | Management / exec view. Several KPIs plus a trend and 1-2 breakdowns.
analytical-grid            | Analysts investigating. Shared filters, side-by-side charts, a detail table.
storytelling-narrative     | Guide the reader: overview → what happened → why/where → details.
dashboard-sidebar          | Many filters or navigation; keep slicers in a left rail.
mosaic-freeform            | Mixed sizes; one dominant viz plus smaller tiles (designer-like).
comparison                 | Side-by-side periods, regions, products, departments, or similar pairs.
drilldown-hierarchical     | Company → region → product → transaction style drill path.
operational-realtime       | Monitoring: status KPIs, live chart, alerts/feed, event table.
""".strip()

LAYOUTS = {
    "analytical-grid": """
id: analytical-grid
name: Analytical / Grid
best_for: Users who investigate data.

┌────────────────────────────────────────────┐
│ Filters: Date | Region | Product | Channel │
├──────────────────────┬─────────────────────┤
│ Trend Analysis       │ Comparison          │
│                      │                     │
├──────────────────────┴─────────────────────┤
│ Detailed Data Table                        │
│                                            │
└────────────────────────────────────────────┘
""".strip(),
    "comparison": """
id: comparison
name: Comparison layout
best_for: Comparing regions, products, years, departments, etc.

┌──────────────────────┬──────────────────────┐
│       2025           │        2026          │
│                      │                      │
│      Chart           │       Chart          │
├──────────────────────┼──────────────────────┤
│      Region A        │       Region B       │
│      Chart           │       Chart          │
└──────────────────────┴──────────────────────┘
""".strip(),
    "dashboard-sidebar": """
id: dashboard-sidebar
name: Dashboard + Sidebar
best_for: Many filters or navigation controls.

┌──────────┬─────────────────────────────────┐
│ Filters  │ KPI  │ KPI  │ KPI  │ KPI        │
│          ├─────────────────────────────────┤
│ Date     │                                 │
│ Region   │           Main Chart            │
│ Product  │                                 │
│ Channel  ├────────────────┬────────────────┤
│          │ Chart          │ Chart          │
│          │                │                │
└──────────┴────────────────┴────────────────┘
""".strip(),
    "drilldown-hierarchical": """
id: drilldown-hierarchical
name: Drill-down / Hierarchical
best_for: Progressive path Company → Region → Department → Product → Transaction.

┌─────────────────────────────────────────────┐
│ Company Overview                            │
├─────────────────────────────────────────────┤
│ Revenue by Region                           │
│                                             │
├───────────────────────┬─────────────────────┤
│ Region Details        │ Product Details     │
├───────────────────────┴─────────────────────┤
│ Transaction Details                         │
└─────────────────────────────────────────────┘
""".strip(),
    "executive-kpi-first": """
id: executive-kpi-first
name: Executive / KPI-first
best_for: Management dashboards.

┌──────────┬──────────┬──────────┬──────────┐
│ Revenue  │ Profit   │ Orders   │ Customers│
├──────────┴──────────┴──────────┴──────────┤
│              Revenue Trend                │
│                  Line Chart               │
├──────────────────────┬────────────────────┤
│ Sales by Region      │ Sales by Product   │
│       Map            │    Bar Chart       │
└──────────────────────┴────────────────────┘
""".strip(),
    "mosaic-freeform": """
id: mosaic-freeform
name: Mosaic / Free-form
best_for: Modern BI where every visualization can have a different size.
Widgets may have arbitrary x, y, width, height on the 12-column grid.

┌──────────────┬──────────────┬───────────────┐
│              │ KPI          │ KPI           │
│  Large Chart ├──────────────┴───────────────┤
│              │       Medium Chart           │
├───────┬──────┴──────────────────────────────┤
│ Chart │              Table                  │
└───────┴─────────────────────────────────────┘
""".strip(),
    "operational-realtime": """
id: operational-realtime
name: Operational / Real-time
best_for: Monitoring systems rather than historical analysis.

┌─────────┬─────────┬─────────┬─────────┐
│ Status  │ Alerts  │ Errors  │ Uptime  │
├─────────┴─────────┴─────────┴─────────┤
│              Live Monitor             │
├──────────────────────┬────────────────┤
│ Activity Feed        │ Current Issues │
├──────────────────────┴────────────────┤
│              Event Table              │
└───────────────────────────────────────┘
""".strip(),
    "storytelling-narrative": """
id: storytelling-narrative
name: Storytelling / Narrative
best_for: Guiding the user from overview → explanation → details.

┌────────────────────────────────────────────┐
│             Business Overview              │
├──────────┬──────────┬──────────┬───────────┤
│ KPI 1    │ KPI 2    │ KPI 3    │ KPI 4     │
├────────────────────────────────────────────┤
│ What happened?                             │
│        Main Trend / Time Series            │
├──────────────────────┬─────────────────────┤
│ Why did it happen?   │ Where did it happen?│
│ Breakdown            │ Regional Analysis   │
├──────────────────────┴─────────────────────┤
│ Detailed explanation / table               │
└────────────────────────────────────────────┘
""".strip(),
}

KNOWN_TEMPLATE_IDS = tuple(LAYOUTS)


def load_decision_table() -> str:
    return DECISION_TABLE


def load_layout_catalog() -> str:
    return "\n\n".join(LAYOUTS[template_id] for template_id in sorted(LAYOUTS))


def list_template_ids() -> list[str]:
    return sorted(LAYOUTS)
