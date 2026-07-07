import logging

from helicalbi.model.SQLAgent import SQLAgent

logger = logging.getLogger(__name__)


class GetRequiredSynonyms:
    def process_flow(self, state: SQLAgent):
        logger.info("GetRequiredSynonyms flow started")
        table_names = state["required_tables"]
        synonyms = state["synonyms"]
        required_synonyms = self.get_synonms_by_tables(table_names, synonyms)
        state["required_synonyms"] = required_synonyms
        return state

    def get_synonms_by_tables(self, table_list, synonyms):
        examples_data = []

        for metric in synonyms:
            metric_tables = metric.get("database_table", [])

            # Check if any table matches
            if any(table in metric_tables for table in table_list):
                examples_data.append(metric)

        return examples_data
