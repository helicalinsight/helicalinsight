from typing import Any, TypedDict


class DataInsightState(TypedDict, total=False):
    sql: str
    user_question: str
    username: str
    profile: dict[str, Any]
    memory: dict[str, Any]
    thread_id: str
    session_cookie: str
    md_location: str
    md_file_name: str
    data: list[Any]
    metadata: list[Any]
    sql_result: dict
    sql_error: str
    skip: bool
    insight: str
    token_usage: dict
    time_consumed: dict
