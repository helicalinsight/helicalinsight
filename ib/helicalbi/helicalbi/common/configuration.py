import logging

from helicalbi.common.logging_config import configure_logging
from helicalbi.common.LLMProxy import LLMProxy
from helicalbi.core.manager.LLMManager import LLMManager

configure_logging()
logger = logging.getLogger(__name__)

llm_manager = LLMManager()

# LLMProxy keeps the module-level `llm` name stable while the underlying
# LLM object is resolved (and hot-reloaded) lazily on every call.
llm = LLMProxy(llm_manager)

baseUrl = llm_manager.get_baseUrl()
rule_strategy = "basic"
