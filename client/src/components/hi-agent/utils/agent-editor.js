import { cubeReducer } from "../../../redux/reducers/cube.reducer";
import initialStates from "../../../redux/reducers/initialStates";

/** Local UI state for the shared Cube field table — not store.cube. */
export const createAgentEditorState = ({
  metadataTablesData = {},
  metadataDetails = {},
} = {}) => ({
  ...initialStates.cubeInitialState,
  cubeFieldsData: {
    ...initialStates.cubeInitialState.cubeFieldsData,
    children: [],
    hierarchyData: { isHierarchyPresent: false, hierarchyList: [] },
  },
  metadataTablesData,
  metadataDetails,
  cubeForEdit: {},
  cubeDataAfterSave: {},
});

export const mergeAgentEditorState = (editorState, agentSlice = {}) => ({
  ...editorState,
  metadataTablesData: agentSlice.metadataTablesData || {},
  metadataDetails: agentSlice.metadataDetails || {},
});

export const agentEditorReducer = cubeReducer;
