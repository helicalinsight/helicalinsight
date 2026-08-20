from abc import ABC, abstractmethod


class MemoryFactory(ABC):

    @abstractmethod
    def create_saver(self):
        pass
