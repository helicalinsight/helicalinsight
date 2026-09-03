from __future__ import annotations

import logging
from typing import Any, Optional, Type

from langchain_core.output_parsers import PydanticOutputParser
from langchain_core.prompts import PromptTemplate
from pydantic import BaseModel

from helicalbi.common.LlmInvokeHelper import invoke_structured
from helicalbi.common.configuration import llm as default_llm

logger = logging.getLogger(__name__)


def invoke_agent_model(
    template: str,
    inputs: dict,
    pydantic_model: Type[BaseModel],
    *,
    llm: Any = None,
    state: Optional[dict] = None,
) -> BaseModel:
    """Run a prompt through the configured LLM and parse a Pydantic model."""
    parser = PydanticOutputParser(pydantic_object=pydantic_model)
    prompt = PromptTemplate(
        template=template,
        input_variables=list(inputs.keys()),
        partial_variables={"format_instructions": parser.get_format_instructions()},
    )
    parsed, _ = invoke_structured(
        prompt,
        llm if llm is not None else default_llm,
        parser,
        inputs,
        state=state,
    )
    return parsed
