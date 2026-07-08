import {
  getAgentStateFromCubeFields,
  getSerializedAgentFromCubeFields,
} from "./agent-cube-bridge";
import { validateCubeDomainAndDescription } from "../../hi-cube/helperMethods";

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

const toCubeFieldsDataForValidation = (cubeFieldsData, agentState) => {
  if (cubeFieldsData) {
    return cubeFieldsData;
  }
  const domainEntry = agentState?.domain?.[0] || {};
  return {
    domainName: (agentState?.domain || [])
      .map((entry) => entry.domain_name)
      .filter(Boolean),
    cubeDescription: domainEntry.description || "",
  };
};

export const validateAgentDomainAndDescription = ({
  cubeFieldsData,
  agentState,
  dispatch,
}) =>
  validateCubeDomainAndDescription({
    cubeFieldsData: toCubeFieldsDataForValidation(cubeFieldsData, agentState),
    dispatch,
    descriptionLabel: "Description",
  });

export const validateAgentSaveInput = ({
  cubeFieldsData,
  agentState,
  isRawJsonView,
  dispatch,
}) => {
  if (isRawJsonView) {
    if (!agentState) {
      return false;
    }
    return validateAgentDomainAndDescription({
      agentState,
      dispatch,
    });
  }
  return validateAgentDomainAndDescription({
    cubeFieldsData,
    dispatch,
  });
};
