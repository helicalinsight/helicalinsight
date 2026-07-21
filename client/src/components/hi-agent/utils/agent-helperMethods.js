import {
  getAgentStateFromCubeFields,
  getSerializedAgentFromCubeFields,
} from "./agent-cube-bridge";

export const getSerializedAgentStateFromFields = ({
  cubeFieldsData,
  existingAgentData,
}) =>
  getSerializedAgentFromCubeFields(cubeFieldsData, existingAgentData);

export const getAgentStateForSave = ({
  cubeFieldsData,
  existingAgentData,
}) =>
  getAgentStateFromCubeFields(cubeFieldsData, existingAgentData);

export const validateAgentSaveInput = ({ agentState, isRawJsonView }) => {
  if (isRawJsonView) {
    return Boolean(agentState);
  }
  return true;
};
