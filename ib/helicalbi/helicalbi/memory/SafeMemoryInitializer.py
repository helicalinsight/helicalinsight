import logging

from helicalbi.memory.MemoryFactoryResolver import MemoryFactoryResolver
from helicalbi.memory.storagetype.InMemoryFactory import InMemoryFactory

logger = logging.getLogger(__name__)


class SafeMemoryInitializer:

    @staticmethod
    def initialize(config):
        try:
            factory = MemoryFactoryResolver.get_factory(config)
            return factory.create_saver()

        except Exception:
            logger.exception("Memory initialization failed; falling back to MemorySaver")
            return InMemoryFactory().create_saver()