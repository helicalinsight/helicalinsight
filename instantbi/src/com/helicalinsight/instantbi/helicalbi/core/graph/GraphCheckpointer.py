from helicalbi.core.manager.MemoryManager import MemoryManager
from helicalbi.memory.SafeMemoryInitializer import SafeMemoryInitializer

_memory_manager = MemoryManager()
_memory_config = _memory_manager.get_config()
checkpointer = SafeMemoryInitializer.initialize(_memory_config)
