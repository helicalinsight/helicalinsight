"""Run column and join correction after SQL has been generated."""
from __future__ import annotations

import logging
from typing import Any, Optional, Tuple

from langchain_core.prompts import PromptTemplate

from helicalbi.autocorrection.columns import (
    columns_for_join_recheck,
    format_column_corrections,
    join_api_columns,
    join_needs_rewrite,
    replace_missing_columns,
    restore_join_aliases,
    sql_column_mismatches,
)
from helicalbi.common import app_config
from helicalbi.common.LlmInvokeHelper import invoke_structured
from helicalbi.common.configuration import llm
from helicalbi.core.sqlflow.FindJoinFromApi import FindJoinFromApi
from helicalbi.prompt.FormatInstruction import (
    brief_format_instructions,
    format_instruction_string,
)
from helicalbi.sql.SelectAliasRewriter import rewrite_select_aliases
from helicalbi.sql.exclusion_predicate import prefer_not_in

logger = logging.getLogger(__name__)
# Root logging stays at ERROR. This logger still records whether correction ran.
logger.setLevel(logging.INFO)

_CORRECTION_PROMPT = """
Rewrite the SQL so each column comes from the table that stores it.
Dialect: {dialect}
A year, quarter, or month filter uses this dialect's date function.
Do not rewrite that filter as CAST of a date range.
When a join is listed, use that join and keep every table alias, including an alias used only in ON. Do not invent a join key.
A column that exists on no table must be replaced with a similar column of the same table. Do not keep that column.
Keep the same filters, groups, and measures.

{corrections}

SQL:
{sql}

Question:
{question}
"""


def autocorrect_enabled() -> bool:
    """True only when application config turns SQL autocorrection on."""
    return bool(getattr(app_config, "sql_autocorrect", False))


def apply_sql_correction(
    sql: str,
    response: Any,
    *,
    state,
    question: str,
    dialect: Optional[str],
    parser,
) -> Tuple[str, Any]:
    """Correct columns and joins. A disabled config returns the SQL unchanged."""
    if not autocorrect_enabled():
        logger.info("SQL autocorrect off")
        return sql, response

    original_sql = sql
    cube_metadata = state.get("cube_metadata")
    join_columns = columns_for_join_recheck(sql, cube_metadata, dialect)
    join_text = FindJoinFromApi().refresh_joins(state, join_columns)
    logger.info(
        "SQL autocorrect join recheck columns=%s join=%s",
        join_columns,
        join_text or "(none)",
    )
    known_columns = join_api_columns(join_text, dialect)
    mismatches = sql_column_mismatches(sql, cube_metadata, dialect, known_columns)
    sql, replaced = replace_missing_columns(sql, mismatches, dialect)
    if replaced:
        logger.info("SQL autocorrect replaced missing columns: %s", "; ".join(replaced))
    mismatches = sql_column_mismatches(sql, cube_metadata, dialect, known_columns)
    corrections = format_column_corrections(mismatches)
    if join_text and (mismatches or join_needs_rewrite(sql, join_text, dialect)):
        join_note = f"Join:\n{join_text}\nUse this join."
        corrections = f"{corrections}\n\n{join_note}" if corrections else join_note
    if corrections:
        logger.info("SQL autocorrect rewriting:\n%s", corrections)
        correction_prompt = PromptTemplate(
            template=_CORRECTION_PROMPT + format_instruction_string,
            input_variables=["corrections", "sql", "question", "dialect"],
            partial_variables={
                "format_instructions": brief_format_instructions(parser.pydantic_object),
            },
        )
        try:
            corrected, _usage = invoke_structured(
                correction_prompt,
                llm,
                parser,
                {
                    "corrections": corrections,
                    "sql": sql,
                    "question": question,
                    "dialect": dialect or "",
                },
                state=state,
            )
        except Exception:
            logger.exception("Column correction call failed")
        else:
            response = corrected
            sql = rewrite_select_aliases(
                corrected.sql,
                cube_metadata=cube_metadata,
                dialect=dialect,
            )
            sql = prefer_not_in(sql, question=question, dialect=dialect)
            sql, replaced = replace_missing_columns(
                sql,
                sql_column_mismatches(sql, cube_metadata, dialect, known_columns),
                dialect,
            )
            if replaced:
                logger.info(
                    "SQL autocorrect replaced missing columns: %s",
                    "; ".join(replaced),
                )
            remaining = sql_column_mismatches(sql, cube_metadata, dialect, known_columns)
            if remaining:
                logger.info(
                    "SQL autocorrect columns still mismatch:\n%s",
                    format_column_corrections(remaining),
                )
    if join_text:
        sql = restore_join_aliases(sql, join_text, dialect)
    if sql == original_sql:
        logger.info("SQL autocorrect checked; SQL unchanged")
    else:
        logger.info("SQL autocorrect applied\n%s", sql)
    return sql, response
