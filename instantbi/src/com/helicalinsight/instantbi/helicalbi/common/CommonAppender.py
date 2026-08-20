from helicalbi.model.ModelState import ModelState


def append_to_workflow(message_content:str,state:ModelState):
    work_flow = state.get("flow", [])
    work_flow.append(message_content)
    state["flow"] = work_flow