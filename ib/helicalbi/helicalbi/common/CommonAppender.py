from helicalbi.model.AgentState import AgentState


def append_to_workflow(message_content:str,state:AgentState):
    work_flow = state.get("flow", [])
    work_flow.append(message_content)
    state["flow"] = work_flow