"""Retry SQL generate+execute without rewriting the stored user question."""
from __future__ import annotations

import logging
from typing import Any, Callable

logger = logging.getLogger(__name__)

SQL_RETRY_MAX = 3

ExecuteFn = Callable[[dict[str, Any]], dict[str, Any]]
RegenerateFn = Callable[[dict[str, Any], str], dict[str, Any]]


def rewrite_failed_sql_prompt(
    question: str,
    sql: str,
    error: str,
    *,
    flat_sql: bool = False,
) -> str:
    """Feed prior SQL + engine error into the next generation attempt."""
    bits = [str(question or "").strip()]
    failed_sql = str(sql or "").strip()
    failed_err = str(error or "").strip()
    if failed_sql:
        bits.append(f"Previous SQL that failed:\n{failed_sql}")
    if failed_err:
        bits.append(f"Execution error to fix:\n{failed_err}")
    bits.append(
        "Rewrite the SQL using valid physical column names or metadata aliases "
        "from the schema. Prefer fixing the named columns rather than dropping "
        "the measure entirely."
    )
    if flat_sql:
        bits.append(
            "Think mode forbids subqueries. Rewrite as one flat SELECT that "
            "joins base tables only. Do not use CTEs, derived tables, or any "
            "nested SELECT. If the comparison needs another aggregate, drop "
            "that part — it belongs in a separate question."
        )
    else:
        bits.append(
            "Prefer JOIN of pre-aggregated derived tables over nested scalar "
            "subqueries in WHERE/HAVING (especially above/below-average comparisons)."
        )
    return "\n\n".join(bit for bit in bits if bit)


def sql_execution_failed(state: dict[str, Any] | None) -> bool:
    if not isinstance(state, dict):
        return False
    error = str(state.get("sql_error") or "").strip()
    if not error or error == "Not Generated":
        return False
    return True


def execute_sql_with_retries(
    state: dict[str, Any],
    *,
    execute: ExecuteFn,
    regenerate: RegenerateFn,
    original_question: str,
    max_attempts: int = SQL_RETRY_MAX,
) -> dict[str, Any]:
    """Run execute; on failure regenerate SQL from the error and retry.

    ``original_question`` stays on ``state["query"]``. The failed SQL is only
    passed into ``regenerate`` via the rewrite prompt.
    """
    question = str(original_question or (state or {}).get("query") or "").strip()
    work = dict(state or {})
    attempts = max(1, int(max_attempts or SQL_RETRY_MAX))
    for attempt in range(attempts):
        work["_defer_sql_insight"] = True
        work["query"] = question
        work = execute(work)
        work["query"] = question
        if not sql_execution_failed(work):
            work.pop("_defer_sql_insight", None)
            return work
        logger.info(
            "SQL retry %s/%s: %s",
            attempt + 1,
            attempts,
            work.get("sql_error"),
        )
        if attempt >= attempts - 1:
            break
        prompt = rewrite_failed_sql_prompt(
            question,
            work.get("sql") or "",
            work.get("sql_error") or "",
        )
        work["skip"] = False
        work["query"] = question
        work = regenerate(work, prompt)
        work["query"] = question
    work["query"] = question
    work.pop("_defer_sql_insight", None)
    return work
