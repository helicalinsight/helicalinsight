import actionTypes from './actionTypes';

const {
    HCR__OLD_CONFIGURATIONS,
    PREVIEW_DETAILS,
    PROPERTY_PANE_DATA,
    HCR_GROUPS,
    NEW_CONFIGURATION,
    DIAGRAM_PAGE_SIZE,
    HCR_ORIENTATION,
    HCR_LAYOUT,
    HCR_DIAGRAM_MARGIN,
    HCR_ACTIVE_ELE_ID,
} = actionTypes;

const storeHCROldConfigurations = (payload) => {
    return { type: HCR__OLD_CONFIGURATIONS, payload };
};

const storeHCRPreviewDetails = (payload) => {
    return { type: PREVIEW_DETAILS, payload };
};

const storeHCRPropertyPaneData = (payload) => {
    return { type: PROPERTY_PANE_DATA, payload };
};

const storeHCRNewConfiguration = (payload) => {
    return { type: NEW_CONFIGURATION, payload };
};

const storeHCRGroups = (payload) => {
    return { type: HCR_GROUPS, payload };
};

const handleDiagramPageSize = (payload) => {
    return { type: DIAGRAM_PAGE_SIZE, payload };
};

const handleDiagramMargin = (payload) => {
    return { type: HCR_DIAGRAM_MARGIN, payload };
};

const hcrOrientation = (payload) => {
    return { type: HCR_ORIENTATION, payload };
};

const hcrLayout = (payload) => {
    return { type: HCR_LAYOUT, payload };
};

const storeHcrActiveEleId = (payload) => {
    return { type: HCR_ACTIVE_ELE_ID, payload };
};

const storeTextFieldContent = (payload) => { };

const onAddingHcrTabData = (payload, isUserAction = false) => {
    return { type: actionTypes.ADD_HCR_TAB_DATA, payload, isUserAction };
};

const setHcrTabActiveKey = (payload) => {
    return { type: actionTypes.HCR_TAB_ACTIVE_KEY, payload };
};

const RemoveHcrTab = (payload) => {
    return { type: actionTypes.REMOVE_HCR_TAB, payload };
};

const storeHcrDiagramNodesData = (payload) => {
    return { type: actionTypes.HCR_DIAGRAM_NODES_DATA, payload };
};

const handleHcrLoaded = (payload) => {
    return { type: actionTypes.HCR_LOADED, payload };
};

const handleAddingDsPaneItem = (payload, undoRedoAction) => {
    return { type: actionTypes.HCR_DS_PANE_ITEM_ADD, payload, meta: { undoRedoAction } };
};

const handleEditingDsPaneItem = (payload, isUserAction = false) => {
    return {
        type: actionTypes.HCR_DS_PANE_ITEM_EDIT,
        payload,
        meta: { userAction: isUserAction },
    };
};

const createActiveConfig = (payload) => {
    return { type: actionTypes.HCR_DS_PANE_ACTIVE_CONFIG, payload };
};

const editActiveConfig = (payload) => {
    return { type: actionTypes.HCR_DS_PANE_ACTIVE_CONFIG_EDIT, payload };
};

const handleDeletingDsPaneItem = (payload) => {
    return { type: actionTypes.HCR_DS_PANE_ITEM_DELETE, payload };
};

const editNode = (payload) => {
    return { type: actionTypes.HCR_EDIT_NODE, payload };
};

const editNodes = (payload) => {
    return { type: actionTypes.HCR_EDIT_NODES, payload };
};

const hcrAddNode = (payload) => {
    return { type: actionTypes.HCR_ADD_NODE, payload };
};

const hcrAddNodes = (payload) => {
    return { type: actionTypes.HCR_ADD_NODES, payload };
};

export const hcrDeleteNode = (payload) => {
    return { type: actionTypes.HCR_DELETE_NODE, payload };
};

export const hcrDeleteNodes = (payload) => {
    return { type: actionTypes.HCR_DELETE_NODES, payload };
};

const hcrSetSelectedDS = (payload) => {
    return { type: actionTypes.HCR_SELECTED_DS, payload };
};

const hcrSetSelectConnection = (payload) => {
    return { type: actionTypes.HCR_SELECT_CONNECTION, payload };
};
const hcrSetDSPanes = (payload) => {
    return { type: actionTypes.HCR_SET_DS_PANES, payload };
};

const setHcrMode = (payload) => {
    return { type: actionTypes.HCR_MODE, payload };
};

const setHcrCanvasMargin = (payload) => {
    return { type: actionTypes.HCR_CANVAS_MARGIN, payload };
};

const setHcrCanvasLayoutSize = (payload) => {
    return { type: actionTypes.HCR_CANVAS_LAYOUT_SIZE, payload };
};

const setHcrCanvasPageProps = (payload) => {
    return { type: actionTypes.HCR_CANVAS_PAGE_PROPS, payload };
};

const setHcrCanvasCalculations = (payload) => {
    return { type: actionTypes.HCR_CANVAS_CALCULATIONS, payload };
};

const setHcrCanvasPreviewParms = (payload) => {
    return { type: actionTypes.HCR_CANVAS_PREVIEW_PARAMS, payload };
};

const setHcrCanvasGroupProperties = (payload) => {
    return { type: actionTypes.HCR_CANVAS_GROUP_PROPERTIES, payload };
};

const setHcrCanvasPageStyles = (payload) => {
    return { type: actionTypes.HCR_CANVAS_PAGE_STYLES, payload };
};

const saveHcrDetails = (payload) => {
    return { type: actionTypes.SAVE_HCR_DETAILS, payload };
};

const storeHcrState = (payload) => {
    return { type: actionTypes.STORE_HCR_STATE, payload };
};

const storeSelectedQueryId = (payload) => {
    return { type: actionTypes.STORE_SELECTED_QUERY_ID, payload };
};

const updateHcrFiltersDrawerStatus = (payload) => {
    return { type: actionTypes.HCR_FILTERS_DRAWER_STATUS, payload };
};

const addToGroup = (payload) => {
    return { type: actionTypes.HCR_ADD_GROUP, payload };
};

const updateGroupCount = (payload) => {
    return { type: actionTypes.HCR_UPDATE_GROUP_COUNT, payload };
};

const deleteGroup = (payload) => {
    return { type: actionTypes.HCR_DELETE_GROUP, payload };
};

const handleHcrSidebarPaneActiveKey = (payload) => {
    return { type: actionTypes.HCR_SIDEBAR_PANE_ACTIVE_KEY, payload };
};

const addCalculation = (payload) => {
    return { type: actionTypes.HCR_ADD_CALCULATION, payload };
};

const resetHcrState = () => {
    return {
        type: actionTypes.RESET_HCR_STATE,
        // payload: data
    };
};

const updateParametersList = (data) => {
    return {
        type: actionTypes.UPDATE_PARAMETER_LIST,
        payload: data,
    };
};

const handleTogglePreview = (data) => {
    return {
        type: actionTypes.HCR_TOGGLE_PREVIEW,
        payload: data
    };
};

const handlePreviewTag = (data) => {
    return {
        type: actionTypes.HCR_PREVIEW_TAG,
        payload: data,
    };
};

const handlePageDetails = (data) => {
    return {
        type: actionTypes.HCR_PAGEDETAILS,
        payload: data,
    };
};

const handleHcrDatasourceList = (data) => {
    return {
        type: actionTypes.HCR_DS_LIST,
        payload: data,
    };
};

const handleHcrImagesList = (data) => {
    return {
        type: actionTypes.HCR_IMAGES_LIST,
        payload: data,
    };
};

const setHcrFilebrowserFor = (data) => {
    return {
        type: actionTypes.HCR_FB_FOR,
        payload: data,
    };
};

const handleHcrImageDel = (data) => {
    return {
        type: actionTypes.HCR_IMAGE_DEL,
        payload: data,
    };
};

export const hcrUndo = () => {
    return { type: actionTypes.HCR_UNDO };
};

export const hcrRedo = () => {
    return { type: actionTypes.HCR_REDO };
};

export const clearHcrUndoRedoHistory = () => {
    return {
        type: actionTypes.HCR_CLEAR_HISTORY,
    };
};

export const hcrHandleEditingDsPaneItem = (payload) => {
    return {
        type: actionTypes.HCR_HANDLE_EDITING_DS_PANE_ITEM,
        payload,
    }
}

const setUpdateHcrCanvasPageStyles = (payload) => {
    return { type: actionTypes.HCR_UPDATE_CANVAS_PAGE_STYLES, payload };
};

const hcrUpdateTableNodeConfig = (payload) => {
    return { type: actionTypes.HCR_UPDATE_TABLE_NODE_CONFIG, payload };
}

const hcrAddNewExportProperty = (payload) => {
    return { type: actionTypes.HCR_ADD_NEW_EXPORT_PRPOPERTY, payload };
}

const hcrEditExportProperty = (payload) => {
    return { type: actionTypes.HCR_EDIT_EXPORT_PROPERTY, payload };
}

const hcrDeleteExportProperty = (payload) => {
    return { type: actionTypes.HCR_DELETE_EXPORT_PROPERTY, payload };
}

const setHcrExportProperties = (payload) => {
    return { type: actionTypes.SET_HCR_EXPORT_PROPERTIES, payload };
}

const hcrAddDefaultExportProperties = (payload) => {
    return { type: actionTypes.HCR_ADD_DEFAULT_EXPORT_PRPOPERTIES, payload };
}

const hcrResizeNode = (payload) => {
    return { type: actionTypes.HCR_RESIZE_NODE, payload };
}

const setHcrQueryRunning = (payload) => {
    return { type: actionTypes.HCR_QUERY_RUNNING, payload };
}

const setHcrNodesSelection = (payload) => {
    return { type: actionTypes.HCR_NODES_SELECTION, payload };
}

const hcrUpdateCanvasView = (payload) => {
    return { type: actionTypes.HCR_UPDATE_CANVAS_VIEW, payload };
}

const hcrUpdateCanvasTabComponent = (payload) => {
    return { type: actionTypes.HCR_UPDATE_CANVAS_TAB_VIEW_COMPONENT, payload };
}

const hcrUpdatePreview = (payload) => {
    return { type: actionTypes.HCR_UPDATE_PREVIEW, payload };
}

const hcrUpdateTableClipboard = (payload) => {
    return { type: actionTypes.HCR_TABLE_CLIPBOARD_DATA, payload };
}

const hcrUpdateSubdataSets = (payload) => {
    return { type: actionTypes.HCR_UPDATE_SUB_DATASETS, payload };
}

const hcrUpdateTableStyles = (payload) => {
    return { type: actionTypes.HCR_UPDATE_TABLE_STYLES, payload };
}


export const hcrActions = {
    handleHcrImageDel,
    setHcrFilebrowserFor,
    handleHcrImagesList,
    handleHcrDatasourceList,
    handlePageDetails,
    handlePreviewTag,
    handleTogglePreview,
    updateParametersList,
    resetHcrState,
    addCalculation,
    handleHcrSidebarPaneActiveKey,
    updateGroupCount,
    deleteGroup,
    addToGroup,
    updateHcrFiltersDrawerStatus,
    storeSelectedQueryId,
    storeHcrState,
    saveHcrDetails,
    setHcrCanvasPageStyles,
    setHcrCanvasGroupProperties,
    setHcrCanvasPreviewParms,
    setHcrCanvasCalculations,
    setHcrCanvasPageProps,
    setHcrCanvasLayoutSize,
    setHcrCanvasMargin,
    setHcrMode,
    hcrSetDSPanes,
    hcrSetSelectConnection,
    hcrSetSelectedDS,
    editNodes,
    hcrDeleteNode,
    hcrDeleteNodes,
    hcrAddNode,
    hcrAddNodes,
    editNode,
    // storeExecuteQueryData,
    handleDeletingDsPaneItem,
    editActiveConfig,
    createActiveConfig,
    handleEditingDsPaneItem,
    handleAddingDsPaneItem,
    handleHcrLoaded,
    storeHcrDiagramNodesData,
    RemoveHcrTab,
    setHcrTabActiveKey,
    onAddingHcrTabData,
    storeTextFieldContent,
    storeHcrActiveEleId,
    storeHCROldConfigurations,
    storeHCRPreviewDetails,
    storeHCRPropertyPaneData,
    storeHCRGroups,
    storeHCRNewConfiguration,
    handleDiagramPageSize,
    handleDiagramMargin,
    hcrOrientation,
    hcrLayout,
    setUpdateHcrCanvasPageStyles,
    hcrUpdateTableNodeConfig,
    hcrAddNewExportProperty,
    hcrEditExportProperty,
    hcrDeleteExportProperty,
    setHcrExportProperties,
    hcrAddDefaultExportProperties,
    hcrResizeNode,
    setHcrQueryRunning,
    setHcrNodesSelection,
    hcrUpdateCanvasView,
    hcrUpdateCanvasTabComponent,
    hcrUpdatePreview,
    hcrUpdateTableClipboard,
    hcrUpdateSubdataSets,
    hcrUpdateTableStyles
};
