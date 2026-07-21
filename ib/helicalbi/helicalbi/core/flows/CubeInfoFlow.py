"""Model flow for cube_info semantic layer files."""

import logging

from helicalbi.common.CubeInfoModel import (
    domain_context_from_model,
    extract_domain_topics,
    topic_mappings_from_domain,
)
from helicalbi.model.ModelState import ModelState
from helicalbi.service.modelservice.ModelLayerHelper import ModelLayerHelper

logger = logging.getLogger(__name__)


class CubeInfoFlow:
    """Prepare model state for SQL generation from cube_info model files.

    Skips the LLM-based domain/topic discovery used by the standard main graph
    and instead reads domain, topics, and topic mappings directly from the
    model file. Topics may be strings or ``{topic, description, components}``
    objects; mappings are identified from each topic's components here.
    """

    def process_flow(self, state: ModelState) -> ModelState:
        logger.info("CubeInfoFlow started")
        helper = ModelLayerHelper(
            state["session_cookie"],
            state["model_file_name"],
            state["model_location"],
        )
        model_data = helper.get_model_semantic_layer() or {}
        domains, topics = extract_domain_topics(model_data)

        state["domain"] = domains or state.get("domain") or []
        state["topics"] = topics or state.get("topics") or []

        # Identify topic mappings in the domain/topic step (from topic objects).
        topic_mappings = (
            state.get("topic_mappings")
            or model_data.get("topic_mappings")
            or topic_mappings_from_domain(model_data)
        )
        if topic_mappings:
            state["topic_mappings"] = topic_mappings
            logger.debug(
                "CubeInfoFlow identified topic_mappings count=%s topics=%s",
                len(topic_mappings),
                [entry.get("topic_name") for entry in topic_mappings if isinstance(entry, dict)],
            )

        if not state.get("domain_context"):
            state["domain_context"] = domain_context_from_model(model_data)
        state["action"] = "none"
        state["sql_query"] = state.get("query", "") or ""
        state["viz_query"] = state.get("query", "") or ""
        return state
