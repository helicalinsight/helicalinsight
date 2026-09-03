"""
LLMProxy
--------
A thin, LangChain-compatible proxy that delegates every call to the *live*
LLM returned by LLMManager.get_llm().

Why a proxy?
  - `configuration.py` exports `llm` as a module-level name that is imported
    by ~15 modules at startup.  We cannot replace that name later once the
    modules have cached their reference.
  - The proxy holds the reference stable while the underlying LLM object can
    be swapped transparently whenever llm_config.yaml is updated on disk
    (hot-reload handled by LLMManager).
  - When the LLM is not configured the proxy raises a clear RuntimeError
    instead of crashing with a Pydantic ValidationError at import time.
"""

from __future__ import annotations

import logging
from typing import Any, AsyncIterator, Iterator, List, Optional

from langchain_core.runnables import Runnable
from langchain_core.runnables.config import RunnableConfig

logger = logging.getLogger(__name__)

_NOT_CONFIGURED_MSG = (
    "LLM is not configured. "
    "Please set the provider credentials / base_url in llm_config.yaml "
    "(the hot-reload watcher will pick up the change immediately)."
)


class LLMProxy(Runnable):
    """
    LangChain-compatible proxy that delegates every Runnable call to the
    *live* LLM returned by LLMManager.get_llm().

    Inheriting from ``Runnable`` makes LangChain's ``coerce_to_runnable``
    accept this object directly, so ``prompt | llm`` works without any
    special operator tricks.
    """

    def __init__(self, manager: Any) -> None:
        self._manager = manager

    # ------------------------------------------------------------------
    # Internal: resolve current live LLM (raises clearly when not set)
    # ------------------------------------------------------------------

    def _llm(self) -> Any:
        llm = self._manager.get_llm()
        if llm is None:
            detail = getattr(self._manager, "_last_error", None)
            if detail:
                raise RuntimeError(f"{_NOT_CONFIGURED_MSG} {detail}")
            raise RuntimeError(_NOT_CONFIGURED_MSG)
        return llm

    # ------------------------------------------------------------------
    # Runnable interface — all methods delegate to the live LLM
    # ------------------------------------------------------------------

    def invoke(self, input: Any, config: Optional[RunnableConfig] = None, **kwargs: Any) -> Any:
        return self._llm().invoke(input, config, **kwargs)

    def stream(
        self, input: Any, config: Optional[RunnableConfig] = None, **kwargs: Any
    ) -> Iterator[Any]:
        return self._llm().stream(input, config, **kwargs)

    async def ainvoke(
        self, input: Any, config: Optional[RunnableConfig] = None, **kwargs: Any
    ) -> Any:
        return await self._llm().ainvoke(input, config, **kwargs)

    async def astream(
        self, input: Any, config: Optional[RunnableConfig] = None, **kwargs: Any
    ) -> AsyncIterator[Any]:
        async for chunk in self._llm().astream(input, config, **kwargs):
            yield chunk

    def batch(
        self, inputs: List[Any], config: Optional[RunnableConfig] = None, **kwargs: Any
    ) -> List[Any]:
        return self._llm().batch(inputs, config, **kwargs)

    # ------------------------------------------------------------------
    # Passthrough attribute access (e.g. llm.model_name)
    # ------------------------------------------------------------------

    def __getattr__(self, name: str) -> Any:
        if name.startswith("_"):
            raise AttributeError(name)
        return getattr(self._llm(), name)

    def __repr__(self) -> str:  # pragma: no cover
        try:
            return f"LLMProxy({self._llm()!r})"
        except RuntimeError:
            return "LLMProxy(<not configured>)"
