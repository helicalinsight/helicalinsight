import logging


from helicalbi.common.logging_config import configure_logging
from helicalbi.core.manager.LLMManager import LLMManager

configure_logging()
logger = logging.getLogger(__name__)

llm_manager = LLMManager()
llm = llm_manager.get_llm()

baseUrl = llm_manager.get_baseUrl()
rule_strategy = "basic"


