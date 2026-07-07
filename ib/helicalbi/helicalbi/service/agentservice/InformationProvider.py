import logging

from helicalbi.common.JsonToPara import (
    generate_bare_minimum_context,
    get_all_cube_tables,
    get_column_names,
    is_bare_minimum_config,
    iter_cube_entries,
)

logger = logging.getLogger(__name__)


class InformationProvider:

    def __init__(self, agent_data):
        self.agent_data = agent_data or {}

    def get_primary_domain(self) -> str:
        for d in self.agent_data.get("domain") or []:
            if isinstance(d, dict) and d.get("domain_name"):
                return d["domain_name"]
        return ""

    def format_domain_info(self, input_domains):
        domain_data = self.agent_data.get("domain") or []
        for d in domain_data:
            if not isinstance(d, dict):
                continue
            if d.get("domain_name") in input_domains:
                domain_str = f'Here is your domain: ["{d["domain_name"]}"]'

                topics = d.get("topics") or []
                topics_str = "Here is your topics: [" + ", ".join(topics) + "]"

                return domain_str + "\n" + topics_str

        return "No matching domain found"

    def get_topics(self, input_domains):
        domain_data = self.agent_data.get("domain") or []
        topics_to_send = []
        for d in domain_data:
            if not isinstance(d, dict):
                continue
            if d.get("domain_name") == input_domains:
                topics_to_send.extend(d.get("topics") or [])
        return topics_to_send

    def format_semantic_layer(self, topics):
        semantic = self.agent_data.get("topic_mappings") or []
        to_send = []

        for item in semantic:
            if not isinstance(item, dict):
                continue
            topic_name = item.get("topic_name")
            if topic_name in topics:
                components = ", ".join(item.get("component") or [])
                result = f"    {topic_name} -> {components}\n"
                to_send.append(result)

        if not to_send and is_bare_minimum_config(self.agent_data):
            cube_metadata = self.agent_data.get("cube_metadata") or []
            for cube in iter_cube_entries(cube_metadata):
                table_name = cube["database_table"]
                columns = ", ".join(get_column_names(cube))
                to_send.append(f"    {table_name} -> {columns}\n")

        return "Semantic Layer:\n" + "\n".join(to_send)

    def get_matching_descriptions(self, input_tables):
        descriptions = []
        metrics = self.agent_data.get("business_metrics") or []

        for metric in metrics:
            if not isinstance(metric, dict):
                continue
            metric_tables = metric.get("tables") or []
            if any(table in input_tables for table in metric_tables):
                description = metric.get("description")
                if description:
                    descriptions.append(description)

        if not descriptions and is_bare_minimum_config(self.agent_data):
            for cube in iter_cube_entries(self.agent_data.get("cube_metadata")):
                description = cube.get("description")
                if description:
                    descriptions.append(description)

        return descriptions

    def _match_targets(self, input_topics):
        """Components from topic_mappings; fall back to topics when mappings are absent."""
        components = self.get_components_from_topics(input_topics)
        if components:
            return components
        return list(input_topics or [])

    def get_input_tables(self, input_topics):
        match_targets = self._match_targets(input_topics)
        matched_tables = []
        bare_minimum_tables = []
        all_tables = []

        for cube in iter_cube_entries(self.agent_data.get("cube_metadata")):
            database_table = cube["database_table"]
            all_tables.append(database_table)
            dimension_names = cube.get("dimension_name") or []

            if not dimension_names:
                bare_minimum_tables.append(database_table)
                continue

            for dimension in dimension_names:
                if dimension in match_targets:
                    matched_tables.append(database_table)
                    break

        tables = matched_tables or bare_minimum_tables or all_tables
        return list(dict.fromkeys(tables))

    def get_attribute_string(self, input_topics):
        match_targets = self._match_targets(input_topics)
        tables_to_send = []
        bare_minimum = is_bare_minimum_config(self.agent_data)

        for cube in iter_cube_entries(self.agent_data.get("cube_metadata")):
            database_table = cube["database_table"]
            col_string = get_column_names(cube)
            if not col_string:
                continue

            columns_part = ", ".join(col_string)
            name_array = cube.get("dimension_name") or []
            entry_added = False

            if name_array:
                for itm in name_array:
                    if itm in match_targets:
                        tables_to_send.append({itm: f"{database_table}({columns_part})"})
                        entry_added = True
            elif bare_minimum:
                tables_to_send.append({database_table: f"{database_table}({columns_part})"})
                entry_added = True

            if bare_minimum and not entry_added:
                tables_to_send.append({database_table: f"{database_table}({columns_part})"})

        return tables_to_send

    def get_bare_minimum_context(self, user_query: str = "") -> str:
        return generate_bare_minimum_context(
            user_query,
            self.agent_data.get("cube_metadata"),
        )

    def get_components_from_topics(self, input_topics):
        components = []
        topic_mappings = self.agent_data.get("topic_mappings") or []
        input_topics = input_topics or []

        for topic in topic_mappings:
            if not isinstance(topic, dict):
                continue
            if topic.get("topic_name") in input_topics:
                components.extend(topic.get("component") or [])

        return components
