import logging
from typing import Any, List

from helicalbi.common.CubeInfoModel import topic_mappings_from_domain
from helicalbi.common.JsonToPara import (
    generate_bare_minimum_context,
    get_column_names,
    is_bare_minimum_config,
    iter_cube_entries,
)

logger = logging.getLogger(__name__)


def _topic_name(topic: Any) -> str:
    """Support legacy string topics and ``{topic, description, components}`` objects."""
    if isinstance(topic, dict):
        name = topic.get("topic") or topic.get("topic_name") or ""
        return str(name) if name else ""
    if topic:
        return str(topic)
    return ""


def _topic_description(topic: Any) -> str:
    if isinstance(topic, dict):
        return str(topic.get("description") or "").strip()
    return ""


def _normalize_topic_names(topics: list) -> List[str]:
    names: List[str] = []
    for topic in topics or []:
        name = _topic_name(topic)
        if name:
            names.append(name)
    return list(dict.fromkeys(names))


class InformationProvider:

    def __init__(self, model_data):
        self.model_data = model_data or {}
        self._ensure_topic_mappings()

    def _ensure_topic_mappings(self) -> None:
        """Derive topic_mappings from domain topic objects when not already present."""
        if self.model_data.get("topic_mappings"):
            return
        derived = topic_mappings_from_domain(self.model_data)
        if derived:
            self.model_data["topic_mappings"] = derived

    def get_primary_domain(self) -> str:
        for d in self.model_data.get("domain") or []:
            if isinstance(d, dict) and d.get("domain_name"):
                return d["domain_name"]
        return ""

    def format_domain_info(self, input_domains):
        domain_data = self.model_data.get("domain") or []
        for d in domain_data:
            if not isinstance(d, dict):
                continue
            if d.get("domain_name") in input_domains:
                domain_str = f'Here is your domain: ["{d["domain_name"]}"]'

                topic_parts: List[str] = []
                for topic in d.get("topics") or []:
                    name = _topic_name(topic)
                    if not name:
                        continue
                    description = _topic_description(topic)
                    if description:
                        topic_parts.append(f'"{name}" (description: {description})')
                    else:
                        topic_parts.append(f'"{name}"')
                topics_str = "Here is your topics: [" + ", ".join(topic_parts) + "]"

                return domain_str + "\n" + topics_str

        return "No matching domain found"

    def get_topics(self, input_domains):
        domain_data = self.model_data.get("domain") or []
        topics_to_send: List[str] = []
        for d in domain_data:
            if not isinstance(d, dict):
                continue
            if d.get("domain_name") == input_domains:
                topics_to_send.extend(_normalize_topic_names(d.get("topics") or []))
        return topics_to_send

    def format_semantic_layer(self, topics):
        semantic = self.model_data.get("topic_mappings") or []
        topic_names = set(_normalize_topic_names(topics))
        to_send = []

        for item in semantic:
            if not isinstance(item, dict):
                continue
            topic_name = item.get("topic_name")
            if topic_name in topic_names:
                components = ", ".join(item.get("component") or [])
                result = f"    {topic_name} -> {components}\n"
                to_send.append(result)

        if not to_send and is_bare_minimum_config(self.model_data):
            cube_metadata = self.model_data.get("cube_metadata") or []
            for cube in iter_cube_entries(cube_metadata):
                table_name = cube["database_table"]
                columns = ", ".join(get_column_names(cube))
                to_send.append(f"    {table_name} -> {columns}\n")

        return "Semantic Layer:\n" + "\n".join(to_send)

    def get_matching_descriptions(self, input_tables):
        descriptions = []
        metrics = self.model_data.get("business_metrics") or []

        for metric in metrics:
            if not isinstance(metric, dict):
                continue
            metric_tables = metric.get("tables") or []
            if any(table in input_tables for table in metric_tables):
                description = metric.get("description")
                if description:
                    descriptions.append(description)

        if not descriptions and is_bare_minimum_config(self.model_data):
            for cube in iter_cube_entries(self.model_data.get("cube_metadata")):
                description = cube.get("description")
                if description:
                    descriptions.append(description)

        return descriptions

    def _match_targets(self, input_topics):
        """Components from topic_mappings; fall back to topics when mappings are absent."""
        components = self.get_components_from_topics(input_topics)
        if components:
            return components
        return _normalize_topic_names(input_topics)

    def get_input_tables(self, input_topics):
        match_targets = self._match_targets(input_topics)
        matched_tables = []
        bare_minimum_tables = []
        all_tables = []

        for cube in iter_cube_entries(self.model_data.get("cube_metadata")):
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
        bare_minimum = is_bare_minimum_config(self.model_data)

        for cube in iter_cube_entries(self.model_data.get("cube_metadata")):
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
            self.model_data.get("cube_metadata"),
        )

    def get_components_from_topics(self, input_topics):
        components = []
        topic_mappings = self.model_data.get("topic_mappings") or []
        topic_names = set(_normalize_topic_names(input_topics))

        for topic in topic_mappings:
            if not isinstance(topic, dict):
                continue
            if topic.get("topic_name") in topic_names:
                components.extend(topic.get("component") or [])

        return components
