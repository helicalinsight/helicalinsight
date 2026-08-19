import importlib
import os
import sys
from typing import Callable

from langgraph.constants import END
from langgraph.graph import StateGraph

from helicalbi.core.manager.MemoryManager import MemoryManager
from helicalbi.memory.SafeMemoryInitializer import SafeMemoryInitializer


class LLMEngine:
    package: str
    flow_sequence: list[str]
    graph: StateGraph
    sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
    memory_manager = MemoryManager()
    memory_config = memory_manager.get_config()
    checkpointer = SafeMemoryInitializer.initialize(memory_config)

    def __init__(self, flow_sequence: list[str] = None, package: str = "flows"):
        self.flow_sequence = flow_sequence
        self.package = package

    def build_graph(self, state_cls):
        workflow = StateGraph(state_cls)
        for class_name in self.flow_sequence:
            # Dynamically import module
            module = importlib.import_module(f"{self.package}.{class_name}")
            clazz = getattr(module, class_name)
            cls = clazz()
            method: Callable = getattr(cls, "process_flow")
            workflow.add_node(class_name, method)
        for i in range(len(self.flow_sequence) - 1):
            workflow.add_edge(self.flow_sequence[i], self.flow_sequence[i + 1])
            # Last goes to END
            workflow.add_edge(self.flow_sequence[-1], END)

            # Entry point
            workflow.set_entry_point(self.flow_sequence[0])

        self.graph = workflow.compile()
        return self.graph
