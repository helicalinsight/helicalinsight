# agent_base.py
from langgraph.graph import MessagesState


class AgentState(MessagesState):
    query: str
    thread_id:str
    metadata: dict
    cube_metadata: dict
    domain_prompt: str
    relationship_of_table: dict
    session_cookie: str
    data: dict
    sql: str
    sql_reason: str
    insight: str
    intent: str
    topics: list
    topic_table: dict
    domain: str
    visualization: str
    topic_graph: dict
    sql_result: dict
    output: str
    output2: str
    classifyintent: str
    required_tables: list
    required_relationships: list
    skip: bool
    vf_string: str
    vf_title: str
    viz_hint: str
    sqlAgent: dict
    flow: list
    citation: list
    reduced_para: str
    last_chats: list
    md_file_name: str
    md_location: str
    sql_error: str
    required_details: dict
    agent_file_name: str
    agent_location: str
    user_name: str
    context: str
    preferences: str
    viz_reason:str
    viz_config:dict
    agg_cols:str
    non_agg_cols:str
    action:str
    viz_query:str
    sql_query:str
    dialect:str
    dbname:str
    token_usage: dict
    time_consumed: dict
    got_domain: bool
    domain_context: str
    business_metrics: list
    topic_mappings: list
    synonyms: list
    use_cube_info_sql_flow: bool
