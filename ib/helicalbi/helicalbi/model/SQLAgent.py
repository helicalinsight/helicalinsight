from langgraph.graph import MessagesState


class SQLAgent(MessagesState):
    query: str
    metadata: dict
    location: str
    metadata_file_name: str
    sql_parts: dict
    dummy_sql: str
    hi_sql: str
    cube_metadata: dict
    relationship_of_table: dict
    session_cookie: str
    topics: list
    table_names: dict
    domain: str
    topic_graph: dict
    topic_table: dict
    sql_result: dict
    required_tables: list
    required_columns:list
    examples:list
    required_examples: list
    synonyms:list
    required_synonyms: list
    required_joins: str
    business_metrics: list
    required_business_metrics: list
    required_column_description: str
    last_chats: list
    query_plan: dict
    final_sql: str
    sql_reason: str
    filters: dict
    groupby: dict
    agent_file_name:str
    agent_location:str
    user_name: str
    context: str
    preferences: str
    thread_id: str
    action:str
    dialect:str
    dbname:str
    token_usage: dict
    time_consumed: dict
    required_functions: str
    required_cube_info: dict
    domain_context: str
    topic_mappings: list

