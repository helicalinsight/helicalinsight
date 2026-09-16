import logging

from helicalbi.common.CubeInfoModel import (
    domains_from_picked_mappings,
    filter_domain_context_for_sql,
    filter_sort_orders_for_picked,
    filter_topic_mappings_for_picked,
    format_sort_orders_for_prompt,
    topics_from_picked_mappings,
)
from helicalbi.core.sqlflow.util.BusinessMetricMatcher import filter_required_business_metrics
from helicalbi.core.sqlflow.util.CubeInfoPicker import build_required_cube_info
from helicalbi.model.SQLModel import SQLModel
from helicalbi.sql.GetContextForSQL import get_required_column_description, get_required_functions

logger = logging.getLogger(__name__)


class GetRequiredMetrics:
    def process_flow(self, state: SQLModel):
        logger.info("GetRequiredMetrics flow started")
        table_names = state["required_tables"]
        business_metrics = state["business_metrics"]
        required_metrics = filter_required_business_metrics(
            business_metrics,
            table_names,
            state.get("query_plan"),
        )
        state["required_business_metrics"] = required_metrics
        state["required_column_description"] = get_required_column_description(
            state.get("cube_metadata"),
            state.get("query_plan"),
        )
        state["required_functions"] = get_required_functions(
            state.get("cube_metadata"),
            state.get("query_plan"),
        )
        required_cube_info = build_required_cube_info(
            state.get("cube_metadata"),
            state.get("query_plan"),
            required_metrics,
        )
        state["required_cube_info"] = required_cube_info

        picked_names = list(
            dict.fromkeys(
                list(required_cube_info.get("picked_dimensions") or [])
                + list(required_cube_info.get("picked_metrics") or [])
            )
        )
        # Final SQL / Semantic section: only domains/topics whose dims/metrics were used.
        filtered_mappings = filter_topic_mappings_for_picked(
            state.get("topic_mappings") or [],
            picked_names,
        )
        semantic_topics = topics_from_picked_mappings(
            filtered_mappings,
            state.get("topics") or [],
        )
        semantic_domains = domains_from_picked_mappings(
            filtered_mappings,
            state.get("domain") or [],
        )
        # Persist the same filter used for metrics/dimensions/measures so the
        # client required_domain / required_topic lists match what SQL used.
        state["topics"] = semantic_topics
        state["domain"] = semantic_domains
        if filtered_mappings:
            state["topic_mappings"] = filtered_mappings
        state["sql_domain_context"] = filter_domain_context_for_sql(
            domain=semantic_domains,
            topics=semantic_topics,
            topic_mappings=filtered_mappings,
            domain_context=state.get("domain_context") or "",
        )

        filtered_sorts = filter_sort_orders_for_picked(
            state.get("sort_orders") or [],
            picked_names,
        )
        state["column_sort_orders"] = format_sort_orders_for_prompt(filtered_sorts)
        return state
