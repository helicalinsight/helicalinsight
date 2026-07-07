from helicalbi.core.ConfigLoader import ConfigLoader
from helicalbi.core.LLMFactoryResolver import LLMFactoryResolver


class MemoryManager:

    def __init__(self, config_path="memory_provider.yaml"):
        self.config = ConfigLoader.load_config(config_path)

    def get_config(self):
        memory_config = self.config["memory"]
        return memory_config
