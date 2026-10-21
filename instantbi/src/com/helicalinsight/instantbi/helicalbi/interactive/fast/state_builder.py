"""Build the LangGraph turn state bag for fast interactive mode."""
from __future__ import annotations

from typing import Any

from langchain_core.messages import HumanMessage

from helicalbi.controller.helpers import turn_state_defaults
from helicalbi.interactive.fast.constants import (
    CUBE_INFO_DICT_KEYS,
    CUBE_INFO_LIST_KEYS,
    CUBE_INFO_STATE_KEYS,
)
from helicalbi.interactive.fast.session import FastModelSession


class FastTurnStateBuilder:
    """Assemble the initial graph state from session + chat context."""

    def build(
        self,
        *,
        session: FastModelSession,
        user_query: str,
        username: str,
        session_cookie: str,
        thread_id: Any,
        last_chats: list,
        rm_cols_in_filter: bool = True,
    ) -> dict[str, Any]:
        cube = session.cube_info_prepared if session.use_cube_info_flow else {}
        state: dict[str, Any] = {
            **turn_state_defaults(),
            "query": user_query,
            "table_columns": [],
            "messages": [HumanMessage(content=user_query, username=username)],
            "session_cookie": session_cookie,
            "last_chats": last_chats,
            "user_name": username,
            "thread_id": thread_id,
            "reduced_para": "",
            "cube_metadata": session.cube_metadata,
            "use_cube_info_sql_flow": session.use_cube_info_flow,
            "relationship_of_table": session.joins,
            "dbname": session.actual_md["databaseName"],
            "md_location": session.md_location,
            "md_file_name": session.med_file_name,
            "model_file_name": session.model_file_name,
            "model_location": session.location,
            "dialect": session.metadata_fun_ref["reference"],
            "rm_cols_in_filter": rm_cols_in_filter,
        }
        for key in CUBE_INFO_STATE_KEYS:
            state[key] = self._default_for(key, cube.get(key))
        return state

    @staticmethod
    def _default_for(key: str, value: Any) -> Any:
        if value is not None and value != "":
            return value
        if key in CUBE_INFO_LIST_KEYS:
            return []
        if key in CUBE_INFO_DICT_KEYS:
            return {}
        return ""
