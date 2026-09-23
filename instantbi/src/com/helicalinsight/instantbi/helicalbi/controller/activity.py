"""User-facing activity lines emitted while InstantBI work is in flight."""

from __future__ import annotations

from typing import Any

from helicalbi.controller.sse import SseEventWriter

QUESTION_LIMIT = 80
SQL_LIMIT = 140


def quote_question(question: str, limit: int = QUESTION_LIMIT) -> str:
    cleaned = " ".join(str(question or "").split())
    if not cleaned:
        return "your question"
    if len(cleaned) <= limit:
        return cleaned
    return cleaned[: limit - 1] + "…"


def sql_snippet(sql: Any, limit: int = SQL_LIMIT) -> str:
    text = sql
    if isinstance(text, dict):
        text = text.get("raw_sql") or text.get("sql") or ""
    text = str(text or "").strip()
    if text.startswith("```"):
        lines = text.splitlines()
        if lines and lines[0].startswith("```"):
            lines = lines[1:]
        if lines and lines[-1].strip() == "```":
            lines = lines[:-1]
        text = "\n".join(lines)
    text = " ".join(text.split())
    if not text:
        return ""
    if len(text) <= limit:
        return text
    return text[: limit - 1] + "…"


class ActivityReporter:
    """Collects named progress events. Silent until streaming is enabled."""

    def __init__(self, question: str = "") -> None:
        self.writer = SseEventWriter()
        self.enabled = False
        self.question = question or ""
        self._pending: list[str] = []

    def enable_streaming(self) -> None:
        self.enabled = True

    def drain(self) -> list[str]:
        events = self._pending
        self._pending = []
        return events

    def understood_intent(self) -> None:
        quoted = quote_question(self.question)
        self._progress(
            "intent",
            "started",
            f"Hi, you want to understand “{quoted}”. Let me find out what I can do.",
        )

    def generating_sql(self) -> None:
        self._progress("sql", "started", "I can query the database for this.")

    def found_sql(self, sql: Any = "") -> None:
        snippet = sql_snippet(sql)
        if snippet:
            message = f"Found this query is suitable: {snippet}"
        else:
            message = "Found this query is suitable."
        self._progress("sql", "done", message)

    def executing_sql(self) -> None:
        self._progress("execute", "started", "Executing your query…")

    def received_data(self) -> None:
        self._progress("execute", "done", "Got your data.")

    def generating_visualization(self) -> None:
        self._progress("viz", "started", "Finding which visualization suits this best…")

    def visualization_generated(self) -> None:
        self._progress("viz", "done", "Found it. Chart is ready.")

    def generating_insight(self) -> None:
        self._progress("insight", "started", "Writing an insight from your data…")

    def insight_generated(self) -> None:
        self._progress("insight", "done", "Insight is ready.")

    def collecting_visualizations(self) -> None:
        self._progress("collect", "started", "Collecting your visualizations…")

    def planning_dashboard(self) -> None:
        self._progress("plan", "started", "Planning the dashboard…")

    def selecting_filters(self) -> None:
        self._progress("filters", "started", "Selecting filters…")

    def generating_layout(self) -> None:
        self._progress("layout", "started", "Generating the layout…")

    def dashboard_generated(self) -> None:
        self._progress("assemble", "done", "Dashboard is ready.")

    def generated(self, stage: str = "done") -> None:
        self._progress(stage, "done", "Found it. Done.")

    def graph_node(self, node_name: str) -> None:
        spec = NODE_ACTIVITY.get(node_name)
        if not spec:
            return
        stage, status, message = spec
        self._progress(stage, status, message)

    def custom(self, stage: str, status: str, message: str) -> None:
        """Emit an arbitrary progress line (used by think plan streaming)."""
        self._progress(stage, status, message)

    def _progress(self, stage: str, status: str, message: str) -> None:
        if not self.enabled:
            return
        self._pending.append(self.writer.progress(stage, status, message))


NODE_ACTIVITY = {
    "UpdateIntentRephrase": (
        "intent",
        "started",
        "Restating your question so I can work with it…",
    ),
    "FindDomainAndTopics": (
        "intent",
        "done",
        "I know which area of the data this is about.",
    ),
    "CubeInfoFlow": (
        "intent",
        "done",
        "I have the model context for this question.",
    ),
    "FindTablesFromTopics": (
        "sql",
        "started",
        "Looking up the tables that match this question…",
    ),
    "GetRequiredSynonyms": (
        "sql",
        "started",
        "Matching your wording to the column names…",
    ),
    "GetColumnNames": (
        "sql",
        "started",
        "Picking the columns I need…",
    ),
    "GetRequiredMetrics": (
        "sql",
        "started",
        "Choosing the measures and calculations…",
    ),
    "GetExamples": (
        "sql",
        "started",
        "Checking similar questions for a good pattern…",
    ),
    "FindJoinFromApi": (
        "sql",
        "started",
        "Figuring out how those tables join…",
    ),
    "FinalSqlGen": (
        "sql",
        "started",
        "Writing the SQL…",
    ),
    "VizModelFiller": (
        "viz",
        "started",
        "Matching your data to a chart type…",
    ),
    "VizPropertiesPolish": (
        "viz",
        "started",
        "Tuning the chart so it reads clearly…",
    ),
    "AntdVisualization": (
        "viz",
        "started",
        "Choosing a chart for this data…",
    ),
    "ChartFiller": (
        "viz",
        "started",
        "Filling in the chart settings…",
    ),
    "Fallback": (
        "viz",
        "started",
        "Trying another chart that fits this data…",
    ),
    "CollectContext": (
        "collect",
        "started",
        "Collecting your visualizations…",
    ),
    "PlanSummary": (
        "plan",
        "started",
        "Planning the dashboard…",
    ),
    "SelectFilters": (
        "filters",
        "started",
        "Selecting filters…",
    ),
    "MakeLayout": (
        "layout",
        "started",
        "Generating the layout…",
    ),
    "Assemble": (
        "layout",
        "started",
        "Fitting the charts on the canvas…",
    ),
    "Audit": (
        "assemble",
        "started",
        "Checking the dashboard…",
    ),
}
