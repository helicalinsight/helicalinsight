"""LLM polish for domain-specific VizModel properties only (1 call).

Shelves and chart type stay frozen from VizModelFiller. This step fills
color / background and may refine title / axis labels.

Catalog Ant Charts render from ``viz_model`` only; ``vf_string`` is reserved
for the DrawOther / custom VF path handled by Fallback.
"""
from __future__ import annotations

import json
import logging

from langchain_core.output_parsers import PydanticOutputParser
from langchain_core.prompts import PromptTemplate

from helicalbi.common.ChatManager import add_viz_response
from helicalbi.common.LlmInvokeHelper import invoke_structured
from helicalbi.common.configuration import llm
from helicalbi.model.ModelState import ModelState
from helicalbi.model.output.viz.VizModel import VizModel, VizPropertiesPolish
from helicalbi.prompt.FormatInstruction import format_instruction_string
from helicalbi.prompt.VizFillPrompt import viz_properties_polish_prompt_string
from helicalbi.viz.viz_model_fill import merge_properties_polish, viz_model_to_chart_settings

logger = logging.getLogger(__name__)


class VizPropertiesPolishNode:
    """One focused LLM call for thematic VizModel properties (color, labels, etc.)."""

    def process_flow(self, state: ModelState):
        logger.info("VizPropertiesPolish flow started")
        if state.get("skip") or state.get("use_other_fallback"):
            return state

        raw_model = state.get("viz_model")
        if not raw_model:
            return state

        try:
            viz_model = (
                raw_model
                if isinstance(raw_model, VizModel)
                else VizModel.model_validate(raw_model)
            )
            data_json = state.get("sql_result") or {}
            data_md = data_json.get("metadata") if isinstance(data_json, dict) else {}
            data_rows = (
                data_json.get("data")
                if isinstance(data_json, dict)
                else None
            ) or state.get("data") or []
            sample_row = data_rows[0] if data_rows else {}
            viz_context = state.get("viz_column_context") or {}

            parser = PydanticOutputParser(pydantic_object=VizPropertiesPolish)
            prompt = PromptTemplate(
                template=viz_properties_polish_prompt_string + format_instruction_string,
                input_variables=[
                    "domain",
                    "topics",
                    "domain_context",
                    "user_question",
                    "sql",
                    "data_types",
                    "sample_row",
                    "frozen_viz_model",
                    "column_format_strings",
                    "column_ai_instructions",
                    "column_viz_context",
                ],
                partial_variables={
                    "format_instructions": parser.get_format_instructions()
                },
            )
            polish, _ = invoke_structured(
                prompt,
                llm,
                parser,
                {
                    "domain": state.get("domain") or [],
                    "topics": state.get("topics") or [],
                    "domain_context": (
                        state.get("sql_domain_context")
                        or state.get("domain_context")
                        or ""
                    ),
                    "user_question": state.get("query") or "",
                    "sql": state.get("sql") or "",
                    "data_types": data_md,
                    "sample_row": json.dumps(sample_row, default=str),
                    "frozen_viz_model": json.dumps(
                        viz_model.model_dump(), default=str
                    ),
                    "column_format_strings": viz_context.get("column_format_strings")
                    or state.get("column_format_strings")
                    or "",
                    "column_ai_instructions": viz_context.get("column_ai_instructions")
                    or state.get("column_ai_instructions")
                    or "",
                    "column_viz_context": viz_context.get("column_context") or "",
                },
                state=state,
            )

            viz_model = merge_properties_polish(viz_model, polish)
            state["viz_model"] = viz_model.model_dump()
            if viz_model.properties.title:
                state["vf_title"] = viz_model.properties.title

            state["chart_settings"] = viz_model_to_chart_settings(
                viz_model, data_types=data_md
            )
            state["viz_reason"] = (
                "Deterministic shelves/chart + LLM domain property polish"
            )
            add_viz_response(state["thread_id"], state["viz_model"])
            logger.info(
                "VizPropertiesPolish done color=%s background=%s labelX=%s labelY=%s",
                viz_model.properties.color,
                viz_model.properties.background,
                viz_model.properties.labelX,
                viz_model.properties.labelY,
            )
        except Exception:
            logger.exception(
                "VizPropertiesPolish failed; applying deterministic VizModel as-is"
            )
            # Keep shelves/chart from VizModelFiller; only property polish was skipped.
            # Keep the existing SQL insight in state["output"]; do not leak tracebacks.
            try:
                viz_model = (
                    raw_model
                    if isinstance(raw_model, VizModel)
                    else VizModel.model_validate(raw_model)
                )
                state["viz_model"] = viz_model.model_dump()
                data_json = state.get("sql_result") or {}
                data_md = data_json.get("metadata") if isinstance(data_json, dict) else {}
                state["chart_settings"] = viz_model_to_chart_settings(
                    viz_model, data_types=data_md
                )
            except Exception:
                logger.exception("VizPropertiesPolish deterministic fallback also failed")

        return state
