import actionTypes from "../actions/actionTypes";
import { addFileToFolder, deleteItemById } from "../../components/hi-fileBrowser/helperMethods";
import produce from 'immer'

const initialState = {
  globalFilters: {
    filterByType: null,
    groupBy: null,
  },
  files: { loading: false, data: [], error: null },
  filteredFiles: null,
  searchResults: null,
  clickedContextItemDetails: {
    contextItem: null,
    clickedRecord: null,
  },
  expandedRow: null,
  saveFileDetails: {
    name: "",
    path: "",
  },
  isShareModalVisible: false,
  tableColumns: null,
  showFileBrowser: null,
  shareTableData: null,
  globalSearch: null,
  compactModeCoordinates: null,
  globalFbEnabled: false,
  exportModalData: {
    visible: false,
    recordData: {}
  },
  copyOrCutItemDetails: null,
};

const fileBrowserReducer = (state = initialState, action) => {
  switch (action.type) {
    case actionTypes.CUT_OR_COPY_ITEM_DETAILS: {
      return {
        ...state,
        copyOrCutItemDetails: action.payload,
      };
    }
    case actionTypes.RESET_STORE: {
      return { ...initialState };
    }
    case actionTypes.SET_FILTER_BY_TYPE: {
      return {
        ...state,
        globalFilters: {
          ...state.globalFilters,
          filterByType: action.payload,
        },
      };
    }
    case actionTypes.SET_GROUP_BY: {
      return {
        ...state,
        globalFilters: {
          ...state.globalFilters,
          groupBy: action.payload,
        },
      };
    }
    case actionTypes.SET_LOADING: {
      return {
        ...state,
        files: {
          ...state.files,
          loading: action.payload,
        },
      };
    }
    case actionTypes.SET_CONTENT: {
      return {
        ...state,
        files: action.payload,
      };
    }
    case actionTypes.SET_ERROR: {
      return {
        ...state,
        files: {
          ...state.files,
          error: action.payload,
        },
      };
    }
    case actionTypes.FB_SEARCH: {
      return {
        ...state,
        searchResults: action.payload,
      };
    }
    case actionTypes.SET_FILTERED_FILES: {
      return {
        ...state,
        filteredFiles: action.payload,
      };
    }
    case actionTypes.SET_CONTEXT_ITEM: {
      return {
        ...state,
        clickedContextItemDetails: {
          contextItem: !action.payload
            ? null
            : action.payload.contextItem !== undefined
              ? action.payload.contextItem
              : state.clickedContextItemDetails.contextItem,
          clickedRecord: !action.payload
            ? null
            : action.payload.clickedRecord !== undefined
              ? action.payload.clickedRecord
              : state.clickedContextItemDetails.clickedRecord,
        },
      };
    }
    case actionTypes.SET_EXPANDED_ROW: {
      return {
        ...state,
        expandedRow: action.payload,
      };
    }
    case actionTypes.SHARE_MODAL_VISIBILITY: {
      return {
        ...state,
        isShareModalVisible: !state.isShareModalVisible,
      };
    }
    case actionTypes.TABLE_COLUMNS: {
      return {
        ...state,
        tableColumns: action.payload,
      };
    }
    case actionTypes.TOGGLE_FILE_BROWSER: {
      return {
        ...state,
        showFileBrowser: action.payload,
      };
    }
    case actionTypes.SET_DELETED_UPDATED: {
      const { delKey, delPath } = action.payload;
      return produce(state, (draft) => {
        draft.files = {
          ...draft.files,
          data: deleteItemById(draft.files.data, delKey, delPath),
        };
      });
    }
    case actionTypes.SET_SHARE_TABLE_DATA: {
      if (!action.payload) return { ...state, shareTableData: null }
      const { user, role, organization } = action.payload
      return {
        ...state,
        shareTableData: { user, role, organization },
      };
    }
    case actionTypes.SET_SHARE_TABLE_DATA_KEY: {
      const { tabKey, item } = action.payload
      let tabData = [...state.shareTableData[tabKey]].map(e => {
        if (e.id === item.id) {
          return item
        }
        return e
      })
      return {
        ...state,
        shareTableData: { ...state.shareTableData, [tabKey]: tabData },
      };
    }
    case actionTypes.SET_GLOBAL_SEARCH: {
      return {
        ...state,
        globalSearch: action.payload,
      };
    }
    case actionTypes.SET_COMPACT_MODE_COORDS: {
      return {
        ...state,
        compactModeCoordinates: action.payload,
      };
    }
    case actionTypes.TOGGLE_GLOBAL_FB: {
      return {
        ...state,
        globalFbEnabled: action.payload,
      };
    }
    case actionTypes.EXPORT_MODAL_DATA: {
      return {
        ...state,
        exportModalData: { ...state.exportModalData, ...action.payload },
      };
    }
    case actionTypes.SAVE_FILE_FB: {
      const file = action.payload
      let currentLocation = file[0]?.path?.split('/') || []
      // currentLocation = currentLocation.split('/')
      currentLocation.pop()
      currentLocation = currentLocation.join('/')
      const clickedPath = state.clickedContextItemDetails.clickedRecord?.path || currentLocation
      let updatedResult = state.files.data
      if (file && clickedPath) {
        updatedResult = addFileToFolder(JSON.parse(JSON.stringify(state.files.data)), file, clickedPath)
      }
      return {
        ...state,
        files: {
          loading: false,
          data: updatedResult,
          error: null,
        }
      };
    }
    default:
      return { ...state };
  }
};
export default fileBrowserReducer;
