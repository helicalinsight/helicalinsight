import logging

from helicalbi.model.SQLModel import SQLModel

logger = logging.getLogger(__name__)
class GetExamples:
    def process_flow(self, state: SQLModel):
        logger.info("GetExamples flow started")
        table_names = state["required_tables"]
        examples = state["examples"]
        required_examples = self.get_examples_by_tables(table_names,examples)
        state["required_examples"] = required_examples
        return state

    def get_examples_by_tables(self, table_list,examples:list):
        examples_data = []
        for metric in examples:
            metric_tables = metric.get("database_table", [])

            # Check if any table matches
            if any(table in metric_tables for table in table_list):
                examples_data.append(metric)

        return examples_data


