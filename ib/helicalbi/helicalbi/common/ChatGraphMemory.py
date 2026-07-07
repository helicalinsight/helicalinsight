"""Graph-backed in-memory store for chat responses.

Each ``chatid`` (a.k.a. ``thread_id``) owns its own directed graph. Nodes are
keyed by ``chat_seq_id`` and hold the rendered ``ChatResponse`` payload plus
everything required to re-execute the SQL later (raw SQL, metadata file +
location, dialect, original user query, etc.). Edges connect consecutive
interactions within the same chat (``prev`` -> ``next``), which gives the
``/load-chat`` endpoint a way to walk the conversation history if needed.

The store is intentionally process-local and protected by a re-entrant lock
so it is safe to use from Flask's threaded request handlers. Swap it out for
a persistent backend (Redis, Postgres, etc.) by re-implementing the same
public surface.
"""

import copy
import logging
import threading
from typing import Any, Optional

logger = logging.getLogger(__name__)


class ChatGraphMemory:
    """In-memory directed graph of chat responses keyed by chat id + seq id."""

    def __init__(self) -> None:
        self._nodes: dict[str, dict[str, dict[str, Any]]] = {}
        self._edges_out: dict[str, dict[str, list[str]]] = {}
        self._edges_in: dict[str, dict[str, list[str]]] = {}
        self._last_seq: dict[str, Optional[str]] = {}
        self._lock = threading.RLock()

    @staticmethod
    def _key(value: Any) -> str:
        return str(value)

    def add_node(self, chatid: Any, chat_seq_id: Any, payload: dict) -> None:
        """Insert (or replace) a node and link it to the previous node in the chat."""
        chat_key = self._key(chatid)
        seq_key = self._key(chat_seq_id)
        snapshot = copy.deepcopy(payload) if payload is not None else {}

        with self._lock:
            self._nodes.setdefault(chat_key, {})[seq_key] = snapshot
            self._edges_out.setdefault(chat_key, {}).setdefault(seq_key, [])
            self._edges_in.setdefault(chat_key, {}).setdefault(seq_key, [])

            prev_seq = self._last_seq.get(chat_key)
            if prev_seq and prev_seq != seq_key:
                self._add_edge(chat_key, prev_seq, seq_key)
            self._last_seq[chat_key] = seq_key

        logger.debug(
            "ChatGraphMemory: stored node chatid=%s chat_seq_id=%s", chat_key, seq_key
        )

    def _add_edge(self, chat_key: str, src: str, dst: str) -> None:
        out_edges = self._edges_out[chat_key].setdefault(src, [])
        if dst not in out_edges:
            out_edges.append(dst)
        in_edges = self._edges_in[chat_key].setdefault(dst, [])
        if src not in in_edges:
            in_edges.append(src)

    def get_node(self, chatid: Any, chat_seq_id: Any) -> Optional[dict]:
        chat_key = self._key(chatid)
        seq_key = self._key(chat_seq_id)
        with self._lock:
            node = self._nodes.get(chat_key, {}).get(seq_key)
            return copy.deepcopy(node) if node is not None else None

    def has_node(self, chatid: Any, chat_seq_id: Any) -> bool:
        chat_key = self._key(chatid)
        seq_key = self._key(chat_seq_id)
        with self._lock:
            return seq_key in self._nodes.get(chat_key, {})

    def get_predecessors(self, chatid: Any, chat_seq_id: Any) -> list[str]:
        chat_key = self._key(chatid)
        seq_key = self._key(chat_seq_id)
        with self._lock:
            return list(self._edges_in.get(chat_key, {}).get(seq_key, []))

    def get_successors(self, chatid: Any, chat_seq_id: Any) -> list[str]:
        chat_key = self._key(chatid)
        seq_key = self._key(chat_seq_id)
        with self._lock:
            return list(self._edges_out.get(chat_key, {}).get(seq_key, []))

    def list_seq_ids(self, chatid: Any) -> list[str]:
        chat_key = self._key(chatid)
        with self._lock:
            return list(self._nodes.get(chat_key, {}).keys())


chat_graph_memory = ChatGraphMemory()
