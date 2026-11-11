"""Join lines and neighbor tables for table and column selection."""
from __future__ import annotations

from typing import Any, Iterable, Optional


def column_schema_tables(
    selected: Any,
    relationships: Any,
    hops: int = 2,
) -> list[str]:
    """Selected tables plus tables within ``hops`` join steps.

    A year filter often lives two hops away (meeting → employee → travel).
    The column prompt has to see that table or it reuses a join key as a date.
    """
    start = _table_list(selected)
    if not start or hops <= 0:
        return start
    graph = _undirected_graph(relationships)
    if not graph:
        return start
    ordered = list(start)
    seen = set(start)
    frontier = list(start)
    for _ in range(hops):
        next_frontier: list[str] = []
        for table_name in frontier:
            for neighbor in graph.get(table_name, ()):
                if neighbor in seen:
                    continue
                seen.add(neighbor)
                ordered.append(neighbor)
                next_frontier.append(neighbor)
        frontier = next_frontier
        if not frontier:
            break
    return ordered


def format_joins(
    relationships: Any,
    only_tables: Optional[Iterable[str]] = None,
) -> str:
    """One ``table.column = table.column`` line per join. Empty when there are none."""
    allowed = {str(table) for table in (only_tables or []) if table}
    lines: list[str] = []
    seen: set[tuple] = set()
    for left_table, left_col, right_table, right_col in _directed_edges(relationships):
        if allowed and (left_table not in allowed or right_table not in allowed):
            continue
        key = tuple(sorted(((left_table, left_col), (right_table, right_col))))
        if key in seen:
            continue
        seen.add(key)
        lines.append(f"{left_table}.{left_col} = {right_table}.{right_col}")
    return "\n".join(lines)


def joins_section(
    relationships: Any,
    only_tables: Optional[Iterable[str]] = None,
) -> str:
    """A Joins block, or an empty string when this catalog has no joins."""
    text = format_joins(relationships, only_tables)
    if not text:
        return ""
    return f"Joins:\n{text}\n"


def _table_list(selected: Any) -> list[str]:
    if isinstance(selected, str):
        text = selected.strip()
        return [text] if text else []
    if not isinstance(selected, list):
        return []
    tables: list[str] = []
    for item in selected:
        text = str(item or "").strip()
        if text and text not in tables:
            tables.append(text)
    return tables


def _relationship_rows(relationships: Any) -> list:
    if isinstance(relationships, dict):
        rows = relationships.get("joins")
        if isinstance(rows, list):
            return rows
        if isinstance(rows, dict):
            return [rows]
        if relationships.get("left") or relationships.get("table"):
            return [relationships]
        return []
    if isinstance(relationships, list):
        return [item for item in relationships if isinstance(item, dict)]
    return []


def _endpoint(side: Any) -> tuple[str, str]:
    if not isinstance(side, dict):
        return "", ""
    table = side.get("table") or side.get("tableName") or side.get("database_table")
    column = side.get("column") or side.get("columnName") or side.get("column_name")
    return (str(table).strip() if table else "", str(column).strip() if column else "")


def _directed_edges(relationships: Any) -> list[tuple[str, str, str, str]]:
    edges: list[tuple[str, str, str, str]] = []
    for item in _relationship_rows(relationships):
        left_table, left_col = _endpoint(item.get("left"))
        right_table, right_col = _endpoint(item.get("right"))
        if left_table and left_col and right_table and right_col:
            edges.append((left_table, left_col, right_table, right_col))
            continue
        src_table = str(item.get("table") or item.get("from_table") or "").strip()
        column = str(item.get("column") or item.get("from_column") or item.get("fk_column") or "").strip()
        ref_table = str(
            item.get("ref_table") or item.get("to_table") or item.get("referenced_table") or ""
        ).strip()
        ref_column = str(
            item.get("ref_column") or item.get("to_column") or item.get("referenced_column") or ""
        ).strip()
        if src_table and column and ref_table and ref_column:
            edges.append((src_table, column, ref_table, ref_column))
    return edges


def _undirected_graph(relationships: Any) -> dict[str, list[str]]:
    graph: dict[str, list[str]] = {}
    for left_table, _left_col, right_table, _right_col in _directed_edges(relationships):
        if right_table not in graph.setdefault(left_table, []):
            graph[left_table].append(right_table)
        if left_table not in graph.setdefault(right_table, []):
            graph[right_table].append(left_table)
    return graph
