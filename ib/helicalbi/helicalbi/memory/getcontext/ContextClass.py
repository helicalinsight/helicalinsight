from typing import List, Optional, Literal
from pydantic import BaseModel


class ChatItem(BaseModel):
    question: Optional[str] = None
    sql: Optional[str] = None
    visualization: Optional[dict] = None


class GraphState(BaseModel):
    user_query: Optional[str] = None
    chat_history: Optional[List[ChatItem]] = None

    # Step 1 output
    query_type: Optional[Literal["NEW_QUERY", "CONTINUATION"]] = None
    continuation_type: Optional[
        Literal["VISUALIZATION_UPDATE", "SQL_UPDATE", "GENERIC_FOLLOWUP"]
    ] = None

    # Step 2 output
    resolved_context: Optional[ChatItem] = None

    # Step 3 output
    final_query: Optional[str] = None
