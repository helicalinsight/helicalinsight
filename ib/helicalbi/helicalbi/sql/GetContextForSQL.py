from helicalbi.common.JsonToPara import (
    generate_bare_minimum_context,
    generate_semantic_hint,
    is_bare_minimum_config,
    iter_cube_entries,
)


def get_tables_and_columns_by_topics(topics: list, topic_table: dict):
    required_tables = []
    for topic in topics:
        required_tables.append(topic_table.get(topic, ""))
    return required_tables


def get_table_col_description(
    cube_metadata,
    table_names=None,
    user_query=None,
    agent_data=None,
):
    cube_metadata = cube_metadata or []
    table_names = table_names or []
    all_cubes = list(iter_cube_entries(cube_metadata))
    bare_minimum = is_bare_minimum_config(agent_data) if agent_data else False

    if table_names:
        reduced_cubes = [
            cube for cube in all_cubes
            if cube.get("database_table") in table_names
        ]
    else:
        reduced_cubes = all_cubes

    if not reduced_cubes:
        reduced_cubes = all_cubes

    if bare_minimum or not table_names:
        return generate_bare_minimum_context(user_query, reduced_cubes)

    return generate_semantic_hint(reduced_cubes)
