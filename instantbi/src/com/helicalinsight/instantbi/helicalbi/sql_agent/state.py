from typing import Annotated, Any, Dict, List, Optional, TypedDict

from langgraph.graph.message import add_messages


class SubQuestionStep(TypedDict, total=False):
    sub_question: str
    pruned_schema: str
    generated_sql: str
    execution_result: str
    analysis: str
    chat_response: Dict[str, Any]
    chat_seq_id: str
    include_in_dashboard: bool


class AgentState(TypedDict, total=False):
    original_question: str
    collected_data: List[SubQuestionStep]
    current_sub_question: Optional[str]
    current_schema_subset: Optional[str]
    generated_sql: Optional[str]
    sql_error: Optional[str]
    query_result: Optional[str]
    is_complete: bool
    final_answer: Optional[str]
    dialect: str
    catalog_id: str
    schema_top_k: int
    sql_retry_count: int
    max_sql_retries: int
    max_sub_questions: int
    max_tool_loops: int
    agent_mode: str
    use_llm_synthesizer: bool
    session_cookie: Optional[str]
    md_location: Optional[str]
    md_file_name: Optional[str]
    token_usage: Dict[str, Any]
    schema_overview: str
    semantic_overview: str
    current_semantic_context: Optional[str]
    selected_domains: List[str]
    selected_topics: List[str]
    session_context: Dict[str, Any]
    thread_id: str
    chat_seq_id: str
    request_id: Optional[str]
    username: str
    build_dashboard: bool
    current_chat_response: Optional[Dict[str, Any]]
    current_chat_seq_id: Optional[str]
    dashboard: Optional[Dict[str, Any]]
    messages: Annotated[list, add_messages]
    tool_loop_count: int
    asked_questions: List[str]
    attempt_count: int
    investigation_steps: List[Dict[str, Any]]
    mode: Dict[str, Any]
    investigation_plan: Dict[str, Any]
    persona: Dict[str, Any]
    user_role: List[Any]
    user_profile: List[Any]


def initial_agent_state(
    question: str,
    *,
    dialect: str = "postgres",
    catalog_id: str = "default",
    schema_top_k: int = 5,
    max_sql_retries: int = 3,
    max_sub_questions: int = 8,
    max_tool_loops: int = 32,
    agent_mode: str = "balanced",
    use_llm_synthesizer: bool = True,
    session_cookie: Optional[str] = None,
    md_location: Optional[str] = None,
    md_file_name: Optional[str] = None,
    schema_overview: str = "",
    semantic_overview: str = "",
    session_context: Optional[Dict[str, Any]] = None,
    thread_id: str = "",
    chat_seq_id: str = "",
    request_id: Optional[str] = None,
    username: str = "",
    build_dashboard: bool = False,
    investigation_plan: Optional[Dict[str, Any]] = None,
    persona: Optional[Dict[str, Any]] = None,
    user_role: Optional[List[Any]] = None,
    user_profile: Optional[List[Any]] = None,
) -> AgentState:
    """Build a complete starting state for one agent run."""
    return {
        "original_question": question,
        "collected_data": [],
        "current_sub_question": None,
        "current_schema_subset": None,
        "generated_sql": None,
        "sql_error": None,
        "query_result": None,
        "is_complete": False,
        "final_answer": None,
        "dialect": dialect,
        "catalog_id": catalog_id,
        "schema_top_k": schema_top_k,
        "sql_retry_count": 0,
        "max_sql_retries": max_sql_retries,
        "max_sub_questions": max_sub_questions,
        "max_tool_loops": max_tool_loops,
        "agent_mode": agent_mode,
        "use_llm_synthesizer": use_llm_synthesizer,
        "session_cookie": session_cookie,
        "md_location": md_location,
        "md_file_name": md_file_name,
        "token_usage": {},
        "schema_overview": schema_overview,
        "semantic_overview": semantic_overview,
        "current_semantic_context": None,
        "selected_domains": [],
        "selected_topics": [],
        "session_context": session_context or {},
        "thread_id": thread_id,
        "chat_seq_id": str(chat_seq_id or ""),
        "request_id": request_id,
        "username": username,
        "build_dashboard": build_dashboard,
        "current_chat_response": None,
        "current_chat_seq_id": None,
        "dashboard": None,
        "messages": [],
        "tool_loop_count": 0,
        "asked_questions": [],
        "attempt_count": 0,
        "investigation_steps": [],
        "mode": {
            "name": agent_mode,
            "max_charts": max_sub_questions,
            "max_tool_loops": max_tool_loops,
            "use_llm_synthesizer": use_llm_synthesizer,
        },
        "investigation_plan": investigation_plan or {},
        "persona": persona or {},
        "user_role": list(user_role or []),
        "user_profile": list(user_profile or []),
    }
