"""Collect convert-dashboard items from the request or ChatGraphMemory."""
from __future__ import annotations

import secrets
import string
from typing import Any

from helicalbi.common.ChatGraphMemory import chat_graph_memory
from helicalbi.sql.SqlSanitizer import extract_sql, strip_sql_markdown

_ID_CHARS = string.ascii_letters + string.digits


def new_component_id(used: set[str] | None = None) -> str:
    used_ids = used if used is not None else set()
    while True:
        value = "".join(secrets.choice(_ID_CHARS) for _ in range(8))
        if value not in used_ids:
            used_ids.add(value)
            return value


def assign_component_ids(items: list[dict[str, Any]], chatid: str = "") -> list[dict[str, Any]]:
    used: set[str] = set()
    assigned: list[dict[str, Any]] = []
    for item in items:
        next_item = dict(item)
        next_item["chatid"] = str(next_item.get("chatid") or chatid or "")
        aid = str(next_item.get("component_id") or "").strip()
        if not aid or aid in used:
            aid = new_component_id(used)
        else:
            used.add(aid)
        next_item["component_id"] = aid
        assigned.append(next_item)
    return assigned


def _clean_sql(raw: Any, dialect: str = "") -> str:
    text = strip_sql_markdown(str(raw or ""))
    if not text:
        return ""
    return strip_sql_markdown(extract_sql(text, dialect) or text) or ""


def _sql_from_viz_item(item: dict[str, Any]) -> str:
    sql = item.get("sql")
    if isinstance(sql, dict):
        return _clean_sql(sql.get("raw_sql") or sql.get("sql") or "", str(sql.get("dialect") or ""))
    data_model = item.get("data_model") if isinstance(item.get("data_model"), dict) else {}
    if data_model.get("sql"):
        return _clean_sql(data_model.get("sql"))
    return _clean_sql(sql)


def _viz_without_template(viz: Any) -> dict[str, Any]:
    if not isinstance(viz, dict):
        return {}
    cleaned = dict(viz)
    cleaned.pop("vf_template", None)
    return cleaned


def list_names(value: Any) -> list[str]:
    if isinstance(value, str) and value.strip():
        return [value.strip()]
    names: list[str] = []
    if isinstance(value, list):
        for entry in value:
            if isinstance(entry, str) and entry.strip():
                names.append(entry.strip())
            elif isinstance(entry, dict):
                name = str(entry.get("domain_name") or entry.get("name") or entry.get("topic") or "").strip()
                if name:
                    names.append(name)
    return names


def _has_error(payload: Any) -> bool:
    if not isinstance(payload, dict):
        return False
    status = str(payload.get("status") or payload.get("request_status") or "").strip().lower()
    if status in {"fail", "failed", "error", "aborted"}:
        return True
    error = payload.get("error")
    if error is True:
        return True
    if isinstance(error, str) and error.strip() and error != "Not Generated":
        return True
    if isinstance(error, list) and error:
        return True
    sql_error = payload.get("sql_error")
    if isinstance(sql_error, str) and sql_error.strip() and sql_error != "Not Generated":
        return True
    return False


def _item_has_error(raw: dict[str, Any], node: dict[str, Any] | None = None) -> bool:
    chat_response = raw.get("chat_response") if isinstance(raw.get("chat_response"), dict) else {}
    nested = node.get("chat_response") if isinstance((node or {}).get("chat_response"), dict) else {}
    return (
        _has_error(raw)
        or _has_error(chat_response)
        or _has_error(node or {})
        or _has_error(nested)
    )


def normalize_item(raw: Any, fallback_id: str = "") -> dict[str, Any] | None:
    if not isinstance(raw, dict):
        return None
    if _item_has_error(raw):
        return None
    chat_sequence_id = str(
        raw.get("chat_sequence_id") or raw.get("id") or fallback_id or ""
    ).strip()
    viz = _viz_without_template(raw.get("viz"))
    report = raw.get("report_model") if isinstance(raw.get("report_model"), dict) else {}
    viz_model = report.get("viz_model") if isinstance(report.get("viz_model"), dict) else None
    if not viz_model:
        viz_model = raw.get("viz_model") if isinstance(raw.get("viz_model"), dict) else None
    if not viz_model and isinstance(viz.get("viz_model"), dict):
        viz_model = viz.get("viz_model")
    if not viz and viz_model:
        viz = {"viz_model": viz_model, "chart_name": str(raw.get("chart_name") or "")}
    data_model = report.get("data_model") if isinstance(report.get("data_model"), dict) else None
    if not data_model:
        data_model = raw.get("data_model") if isinstance(raw.get("data_model"), dict) else None
    sql = _sql_from_viz_item(raw)
    summary = raw.get("summary")
    if isinstance(summary, dict):
        summary_text = str(summary.get("insight") or summary.get("text") or "")
    else:
        summary_text = str(summary or "")
    if not chat_sequence_id:
        return None
    if not sql and not viz and not viz_model and not data_model:
        return None
    sql_section = raw.get("sql") if isinstance(raw.get("sql"), dict) else {}
    return {
        "id": chat_sequence_id,
        "chat_sequence_id": chat_sequence_id,
        "chatid": str(raw.get("chatid") or raw.get("chat_id") or ""),
        "component_id": str(raw.get("component_id") or "").strip(),
        "user_query": str(raw.get("user_query") or raw.get("userQuery") or ""),
        "data_model": data_model,
        "viz_model": viz_model,
        "sql": sql,
        "viz": viz,
        "summary": summary_text,
        "domain": list_names(raw.get("domain") or sql_section.get("required_domain")),
        "topics": list_names(raw.get("topics") or sql_section.get("required_topic")),
    }


def items_from_request(user_input: dict[str, Any]) -> list[dict[str, Any]]:
    raw_items = user_input.get("items")
    if isinstance(raw_items, str):
        return []
    if not isinstance(raw_items, list):
        return []
    chatid = str(user_input.get("chatid") or user_input.get("thread_id") or user_input.get("chat_id") or "")
    items: list[dict[str, Any]] = []
    for index, raw in enumerate(raw_items):
        payload = dict(raw) if isinstance(raw, dict) else raw
        if isinstance(payload, dict) and chatid and not payload.get("chatid"):
            payload["chatid"] = chatid
        item = normalize_item(payload, fallback_id=str(index + 1))
        if item:
            items.append(item)
    return items


def items_from_memory(chatid: Any) -> list[dict[str, Any]]:
    if not chatid:
        return []
    items: list[dict[str, Any]] = []
    for seq in chat_graph_memory.list_seq_ids(chatid):
        node = chat_graph_memory.get_node(chatid, seq) or {}
        chat_response = node.get("chat_response") if isinstance(node.get("chat_response"), dict) else {}
        if _item_has_error({"chat_response": chat_response}, node):
            continue
        viz = chat_response.get("viz") or {}
        report = (
            chat_response.get("report_model")
            if isinstance(chat_response.get("report_model"), dict)
            else {}
        )
        sql = node.get("sql") or (chat_response.get("sql") or {}).get("raw_sql")
        summary = chat_response.get("summary") or {}
        item = normalize_item(
            {
                "id": seq,
                "chat_sequence_id": seq,
                "chatid": chatid,
                "sql": sql,
                "viz": viz,
                "report_model": report or None,
                "viz_model": report.get("viz_model")
                or chat_response.get("viz_model")
                or (viz.get("viz_model") if isinstance(viz, dict) else None),
                "data_model": report.get("data_model") or chat_response.get("data_model"),
                "user_query": node.get("user_query") or chat_response.get("user_query") or "",
                "summary": summary,
                "domain": (chat_response.get("sql") or {}).get("required_domain") if isinstance(chat_response.get("sql"), dict) else [],
                "topics": (chat_response.get("sql") or {}).get("required_topic") if isinstance(chat_response.get("sql"), dict) else [],
            },
            fallback_id=str(seq),
        )
        if item:
            items.append(item)
    return items


def collect_items(user_input: dict[str, Any]) -> list[dict[str, Any]]:
    items = items_from_request(user_input)
    if not items:
        thread_id = user_input.get("chatid") or user_input.get("thread_id") or user_input.get("chat_id")
        items = items_from_memory(thread_id)
    chatid = str(user_input.get("chatid") or user_input.get("thread_id") or user_input.get("chat_id") or "")
    return assign_component_ids(items, chatid=chatid)


def item_cards(items: list[dict[str, Any]], *, domain: Any = None, topics: Any = None) -> list[dict[str, Any]]:
    """Compact cards for the layout LLM: id, domain, topics, user query, generated viz."""
    shared_domain = list_names(domain)
    shared_topics = list_names(topics)
    cards: list[dict[str, Any]] = []
    for item in items:
        viz = item.get("viz") or {}
        model = item.get("viz_model") if isinstance(item.get("viz_model"), dict) else {}
        if not model:
            model = viz.get("viz_model") if isinstance(viz.get("viz_model"), dict) else {}
        data = model.get("data") if isinstance(model.get("data"), dict) else {}
        chart = model.get("chart") if isinstance(model.get("chart"), dict) else {}
        props = model.get("properties") if isinstance(model.get("properties"), dict) else {}
        rows = list(data.get("rows") or [])
        measures = list(data.get("columns") or [])
        filters = []
        for raw in list(data.get("filters") or []) + rows:
            if isinstance(raw, str) and raw.strip():
                filters.append(raw.strip())
            elif isinstance(raw, dict):
                name = str(raw.get("name") or raw.get("column") or raw.get("alias") or "").strip()
                if name:
                    filters.append(name)
        cards.append(
            {
                "component_id": item.get("component_id"),
                "domain": list_names(item.get("domain")) or shared_domain,
                "topics": list_names(item.get("topics")) or shared_topics,
                "user_query": str(item.get("user_query") or ""),
                "summary": str(item.get("summary") or "")[:240],
                "viz": {
                    "chart": viz.get("chart_name") or chart.get("viz") or chart.get("mark") or "",
                    "title": props.get("title") or viz.get("vf_title") or "",
                    "rows": rows,
                    "columns": measures,
                    "filters": filters,
                },
            }
        )
    return cards
