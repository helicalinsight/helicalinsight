import logging

from helicalbi.core.sqlflow.util.BusinessMetricMatcher import filter_required_business_metrics
from helicalbi.model.SQLAgent import SQLAgent

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
        return state
