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
  const metadata = agentState?.metadata_info?.metadata || {};
  return {
    domainName: metadata.domain || [],
    cubeDescription: metadata.description || "",
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
