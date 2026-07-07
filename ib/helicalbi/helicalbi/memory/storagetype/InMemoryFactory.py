from langgraph.checkpoint.memory import MemorySaver

from helicalbi.memory.MemoryFactory import MemoryFactory


class InMemoryFactory(MemoryFactory):

    def create_saver(self):
        return MemorySaver()
