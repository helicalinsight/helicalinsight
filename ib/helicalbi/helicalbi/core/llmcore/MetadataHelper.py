from collections import defaultdict
from typing import Any


class MetadataHelper:
    metadata: dict[Any]

    # --For cube
    domain_topic_map = defaultdict(set)
    table_column_map = {}
    topic_table_map = defaultdict(list)

    def __init__(self, metadata: dict[Any]):
        self.metadata = metadata
        # self.generate_domain_topics()




    def get_domain_topic_map(self):
        self.domain_topic_map.clear()
        for item in self.metadata:
            for domain in item.get("domain", []):
                self.domain_topic_map[domain].update(item.get("topics", []))

        # Convert to comma-separated values
        domain_topic_map = {
            domain: ", ".join(sorted(topics))
            for domain, topics in self.domain_topic_map.items()
        }
        return domain_topic_map

    def get_table_column_map(self):
        self.table_column_map.clear()

        for table in self.metadata or []:
            if not isinstance(table, dict):
                continue

            table_name = table.get("database_table")
            if not table_name:
                continue

            columns = []

            # dimensions → column_name
            for dim in table.get("columns") or []:
                if not isinstance(dim, dict):
                    continue
                col_n = dim.get("column_name")
                if not col_n:
                    continue
                dim_type = (dim.get("column_metrics") or {}).get("data_type", "unknown")
                columns.append(f"{col_n}(type {dim_type})")

            # measures → column_name ONLY (not alias_name)
            for measure in table.get("measures") or []:
                if not isinstance(measure, dict):
                    continue
                col = measure.get("column_name")
                if col:
                    columns.append(col)

            self.table_column_map[table_name] = columns

        return self.table_column_map

    def prepare_topic_table_map(self):
        self.topic_table_map.clear()
        for item in self.metadata:
            table_name = item.get("database_table")
            for topic in item.get("topics", []):
                self.topic_table_map[topic].append(table_name)

        # convert to normal dict
        topic_table_map = dict(self.topic_table_map)
        return topic_table_map

    def get_domain_only(self):
        domain_csv = "\n    ".join(set(
            domain
            for item in self.metadata
            for domain in item.get("domain", [])
        ))
        return domain_csv

    def get_topics_only(self):
        topics_csv = "\n    ".join(set(
            topic
            for item in self.metadata
            for topic in item.get("topics", [])
        ))
        return topics_csv