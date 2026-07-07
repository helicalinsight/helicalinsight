from abc import ABC, abstractmethod


class LLMFactory(ABC):
    """Base Class for the LLM interface"""

    @abstractmethod
    def create_llm(self):
        pass
