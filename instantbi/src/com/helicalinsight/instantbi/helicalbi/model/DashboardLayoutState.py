from typing import Any, TypedDict


class DashboardLayoutState(TypedDict, total=False):
    items: list[dict[str, Any]]
    user_input: dict[str, Any]
    username: str
    user_id: Any
    session_cookie: str
    thread_id: str
    chatid: str
    user_query: str
    domain: Any
    topics: Any
    viz_types: list[str]
    chat_context: list[dict[str, Any]]
    layout_plan: str
    filter_components: list[dict[str, Any]]
    theme: dict[str, Any]
    summary: dict[str, Any]
    sections: list[dict[str, Any]]
    filters: list[dict[str, Any]]
    layout: list[dict[str, Any]]
    decorations: list[dict[str, Any]]
    widgets: list[dict[str, Any]]
    templateId: str
    token_usage: dict
    time_consumed: dict
    error: str
