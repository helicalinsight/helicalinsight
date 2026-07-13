import logging

from helicalbi.core.sqlflow.util.BusinessMetricMatcher import filter_required_business_metrics
from helicalbi.core.sqlflow.util.CubeInfoPicker import build_required_cube_info
from helicalbi.model.SQLAgent import SQLAgent
from helicalbi.sql.GetContextForSQL import get_required_column_description, get_required_functions

logger = logging.getLogger(__name__)


class GetRequiredMetrics:
    def process_flow(self, state: SQLAgent):
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
        state["required_cube_info"] = build_required_cube_info(
            state.get("cube_metadata"),
            state.get("query_plan"),
            required_metrics,
        )
        return state
