import actionTypes from '../actions/actionTypes';
import produce from 'immer';
import initialStates from './initialStates';
import {
    HCR_TABLE_DATA_CELL_WIDTH,
    hcrCanvasViews,
    hcrDSParameter,
    hcrDSQuery,
    hcrParaDate,
    hcrParaDateAndTime,
    hcrParaInput,
} from '../../components/hi-canned-reports/hcr-constants';
import { v4 as uuidv4 } from 'uuid';
import {
    handleDeletingGroup,
    removeCalculation,
    updateCanvasTabViewComponent,
    updateElementsWithStyles,
    updateSubDataSets,
    updateTableStyles,
} from '../../components/hi-canned-reports/hcrHelperMethods';
import { deleteItemById } from '../../components/hi-fileBrowser/helperMethods';
import { cloneDeep, isEmpty } from 'lodash';

// const {
// 	HCR__OLD_CONFIGURATIONS,
// 	PREVIEW_DETAILS,
// 	NEW_CONFIGURATION,
// 	HCR_GROUPS,
// 	PROPERTY_PANE_DATA,
// 	DIAGRAM_PAGE_SIZE,
// 	HCR_ACTIVE_ELE_ID,
// } = actionTypes;

function getReqPane(draft) {
    return draft.hcrTabData.panes.find((pane) => {
        if (pane.key === draft.hcrTabData.activeKey) {
            return true;
        }
        return false;
    });
}

function getReqPaneByKey(draft, key) {
    return draft.hcrTabData.panes.find((pane) => {
        if (pane.key === key) {
            return true;
        }
        return false;
    });
}

const initialCanvasTabView = {
    active: hcrCanvasViews.CANVAS,
    views: [
        { key: hcrCanvasViews.CANVAS, label: "Canvas", id: hcrCanvasViews.CANVAS }
    ]
}

const initialPreviewData = {
    isPreviewLoading: false,
    isAborted: false,
    pageVal: 1,
    isStreamComplete: false,
    previewTotalPageCount: 1,
    streamFirstPageData: null,
    previewError: null,
    updatedPages: []
}

export const hcrTabInitialState = {
    mode: "create",
    hcrFiltersDrawerStatus: false,
    hcrDiagramNodesData: [],
    selectedConnectionDetails: null,
    dsPaneTypes: [
        {
            key: 'query',
            dataSourcePane: hcrDSQuery,
            menu: [],
        },
        {
            key: 'parameter',
            dataSourcePane: hcrDSParameter,
            menu: [],
        },
    ],
    selectedDS: null,
    selectedQueryId: null,
    canvasProperties: {
        margin: {},
        layout: {
            name: 'A4',
            orientation: 'Portrait',
            size: {
                width: 595,
                height: 842,
            },
        },
        pageProperties: {
            columnCount: 1,
        },
        calculations: {
            selectCalculation: '',
            options: [],
            keyValuePairs: {
                id: uuidv4(),
                // name: '',
                // className: '',
                // calculation: '',
                // resetType: '',
                // resetGroup: '',
                // increment: '',
                // incrementGroup: '',
                // expression: '',
                // initialValueExp: '',
                // incrementFactoryClassName: ''
            },
        },
        previewParameters: {
            showParameters: true,
        },
        groupProperties: {
            selectGroup: '',
            options: [],
            keyValuePairs: {}
        },
        pageStyles: {
            selectStyles: '',
            options: [],
            keyValuePairs: {
                // id : uuidv4(),
                // styleName: '',
                // fontFamily:'Aharoni',
                // fontSize: 12,
                // mode: 'Transparent',
                // fontFill: '#000000',
                // fill: '#ffffff',
                // bold: false,
                // italic: false,
                // underLine: false,
                // strikeThrough: false,
                // horizontalAlign: 'center',
                // verticalAlign: 'middle',
                // rotation: 'None',
                // markUp: 'none',
                // blankWhenNull: false,
                // defaultStyle: false,
                // pattern: '',
                borders: {
                    // stroke: 1, style: 'SOLID', color: '#000000'
                    // Top: {  },
                    // Bottom: {  },
                    // Right: {  },
                    // Left: { },
                },
                padding: {
                    // Top: 0,
                    // Bottom: 0,
                    // Right: 0,
                    // Left: 0,
                },
                lineStyles: {}, // stroke: 1, style: 'SOLID', color: '#000000'
            },
        },
    },
    groupsOrder: [],
    groupCount: 0,
    sidebarPaneActiveKey: 'datasource',
    isPreviewing: false,
    previewTag: '',
    pageDetails: { totalPageCount: 10, currentPageNo: 1 },
    isUpdatingCanvasPageStyles: false,
    hcrExportProperties: [],
    hcrQueryRunning: false,
    canvasView: hcrCanvasViews.CANVAS,
    canvasTabViews: initialCanvasTabView,
    hcrPreviewData: initialPreviewData,
    hcrTableClipboardData: {},
    subDataSets: [],
    tableStyles: []
};

export const hcrReducer = (state = initialStates.hcrInitialState, action) => {
    switch (action.type) {
        case actionTypes.HCR_FB_FOR: {
            return produce(state, (draft) => {
                const reqPane = getReqPane(draft);
                reqPane.hcrFBFor = action.payload;
            });
        }
        // case actionTypes.HCR_HANDLE_EDITING_DS_PANE_ITEM: {
        //   const { dataSourcePane, itemId, key, value } = action.payload;
        //   return {
        //     ...state,
        //     hcrTabData: {
        //       ...state.hcrTabData,
        //       panes: state.hcrTabData.panes.map((pane) => {
        //         if (pane.key !== state.hcrTabData.activeKey) return pane;
        //         return {
        //           ...pane,
        //           dsPaneTypes: pane.dsPaneTypes.map((dsPane) => {
        //             if (dsPane.dataSourcePane !== dataSourcePane) return dsPane;

        //             return {
        //               ...dsPane,
        //               menu: dsPane.menu.map((query) => {
        //                 if (query.id !== itemId) return query;

        //                 return {
        //                   ...query,
        //                   [key]: value, // This line is important for tracking config/connection changes
        //                   isSaved:
        //                     key === "config" || key === "connectionDetails"
        //                       ? false
        //                       : query.isSaved,
        //                 };
        //               }),
        //             };
        //           }),
        //         };
        //       }),
        //     },
        //   };
        // }

        case actionTypes.HCR_HANDLE_EDITING_DS_PANE_ITEM: {
            const { dataSourcePane, itemId, key, value } = action.payload;

            return produce(state, (draft) => {
                const reqPane = getReqPane(draft);

                reqPane.dsPaneTypes.forEach((dsPane) => {
                    if (dsPane.dataSourcePane !== dataSourcePane) return;

                    dsPane.menu = dsPane.menu.map((query) => {
                        if (query.id !== itemId) return query;

                        return {
                            ...query,
                            [key]: value,
                            isSaved:
                                key === 'config' || key === 'connectionDetails'
                                    ? false
                                    : query.isSaved,
                        };
                    });
                });
            });
        }
        case actionTypes.HCR_IMAGE_DEL: {
            return produce(state, (draft) => {
                const { delKey, delPath } = action.payload;
                draft.imagesList = {
                    ...draft.imagesList,
                    data: deleteItemById(
                        draft.imagesList.data,
                        delKey,
                        delPath
                    ),
                };
            });
        }
        case actionTypes.HCR_IMAGES_LIST: {
            return produce(state, (draft) => {
                const reqPane = getReqPane(draft);
                draft.imagesList = action.payload;
                reqPane.hcrFBFor = 'images';
            });
        }
        case actionTypes.HCR_DS_LIST: {
            return produce(state, (draft) => {
                draft.allDataSourceTypes = action.payload.allDataSourceTypes;
                draft.datasourceListToRender =
                    action.payload.datasourceListToRender;
                draft.driversList = action.payload.driversList;
                draft.dataSourceTypes = action.payload.dataSourceTypes;
            });
        }
        case actionTypes.HCR_PAGEDETAILS: {
            const { pageDetails, reportKey } = action.payload || {}
            return produce(state, (draft) => {
                let reqPane = getReqPaneByKey(draft, reportKey);
                if (!reqPane) {
                    reqPane = getReqPane(draft)
                }
                reqPane.pageDetails = pageDetails;
            });
        }
        case actionTypes.HCR_PREVIEW_TAG: {
            const { previewTag, reportKey } = action.payload || {}
            return produce(state, (draft) => {
                let reqPane = getReqPaneByKey(draft, reportKey);
                if (!reqPane) {
                    reqPane = getReqPane(draft)
                }
                reqPane.previewTag = previewTag;
            });
        }
        case actionTypes.HCR_TOGGLE_PREVIEW: {
            return produce(state, (draft) => {
                const reqPane = getReqPane(draft);
                // reqPane.isPreviewing = !reqPane.isPreviewing;
                reqPane.isPreviewing = action.payload;
            });
        }
        case actionTypes.UPDATE_PARAMETER_LIST: {
            return produce(state, (draft) => {
                const reqPane = getReqPane(draft);
                const reqParameters = reqPane.dsPaneTypes.find(
                    (ele) => ele.dataSourcePane === hcrDSParameter
                );
                reqParameters.menu = action.payload;
            });
        }
        case actionTypes.RESET_HCR_STATE: {
            // let { mode, others } = action.payload
            return {
                ...initialStates.hcrInitialState,
                hCROldConfigurations: state.hCROldConfigurations,
            };
        }
        case actionTypes.HCR_ADD_CALCULATION: {
            return produce(state, (draft) => {
                const reqPane = getReqPane(draft);
                let { node, calculation } = action.payload;
                let i = 1;
                let reqCalcName = `${calculation}_${node.name}${i}`;
                let reqCalcId = i;
                while (
                    reqPane.canvasProperties.calculations.options.find(
                        (ele) => ele.name === reqCalcName
                    )
                ) {
                    i++;
                    reqCalcName = `${calculation}_${node.name}${i}`;
                    reqCalcId = i;
                }
                const reqCalc = {
                    calculation: calculation || 'Count',
                    className: 'java.lang.Integer',
                    expression: node.label,
                    id: uuidv4(),
                    name: reqCalcName,
                    resetType: 'Report',
                    calcId: reqCalcId,
                };
                reqPane.canvasProperties.calculations.options.push(reqCalc);
                node = {
                    ...node,
                    name: reqCalcName,
                    label: `$V{${reqCalcName}}`,
                    type: 'calculatedParams',
                    isNewCalculation: true,
                };
                delete node.backendDataType;
                reqPane.hcrDiagramNodesData = reqPane.hcrDiagramNodesData.map(
                    (existedNode) => {
                        if (existedNode.id === node.id) {
                            return node;
                        }
                        return existedNode;
                    }
                );
            });
        }
        // case actionTypes.HCR_SIDEBAR_PANE_ACTIVE_KEY: {
        // 	return produce(state, (draft) => {
        // 		const reqPane = getReqPane(draft);
        // 		reqPane.sidebarPaneActiveKey = action.payload;
        // 	});
        // }
        case actionTypes.HCR_SIDEBAR_PANE_ACTIVE_KEY: {
            return produce(state, (draft) => {
                const reqPane = getReqPane(draft);
                if (reqPane) {
                    reqPane.sidebarPaneActiveKey = action.payload;
                }
            });
        }
        case actionTypes.HCR_DELETE_GROUP: {
            return produce(state, (draft) => {
                const reqPane = getReqPane(draft);
                handleDeletingGroup(reqPane);
            });
        }
        case actionTypes.HCR_UPDATE_GROUP_COUNT: {
            return produce(state, (draft) => {
                const reqPane = getReqPane(draft);
                reqPane.groupCount = reqPane.groupCount + 1;
            });
        }
        case actionTypes.HCR_ADD_GROUP: {
            return produce(state, (draft) => {
                const reqPane = getReqPane(draft);
                reqPane.groupsOrder.push(action.payload);
            });
        }
        case actionTypes.HCR_FILTERS_DRAWER_STATUS: {
            return produce(state, (draft) => {
                const reqPane = getReqPane(draft);
                if (reqPane) {
                    reqPane.hcrFiltersDrawerStatus =
                        !reqPane.hcrFiltersDrawerStatus;
                }
            });
        }
        case actionTypes.STORE_SELECTED_QUERY_ID: {
            return produce(state, (draft) => {
                const reqPane = getReqPane(draft);
                reqPane.selectedQueryId = action.payload;
            });
        }
        // case actionTypes.HCR_DELETE_NODE: {
        // 	return produce(state, (draft) => {
        // 		const reqPane = getReqPane(draft);
        // 		reqPane.hcrDiagramNodesData = reqPane.hcrDiagramNodesData.filter(existedNode => {
        //             return existedNode.id !== action.payload?.id
        // 		})
        // 	});
        // }

        // case actionTypes.HCR_DELETE_NODE: {
        //     return produce(state, (draft) => {
        //         const reqPane = getReqPane(draft);
        //         const nodeId = action.payload?.nodeId || action.payload?.id;
        //         if (!nodeId) return;
        //         if (
        //             draft.propertyPaneData &&
        //             nodeId in draft.propertyPaneData
        //         ) {
        //             const imageNode = draft.propertyPaneData[nodeId];
        //             if (imageNode?.type === 'image' && imageNode?.src) {
        //                 reqPane.hcrUndoData.push({
        //                     actionType: 'IMAGE_NODE_DELETED',
        //                     payload: {
        //                         nodeId,
        //                         config: imageNode,
        //                     },
        //                 });
        //             }
        //             delete draft.propertyPaneData[nodeId];
        //         }
        //         reqPane.hcrDiagramNodesData =
        //             reqPane.hcrDiagramNodesData.filter(
        //                 (node) => node.id !== nodeId
        //             );
        //     });
        // }
        case actionTypes.STORE_HCR_STATE: {
            return produce(state, (draft) => {
                draft.hcrTabData.panes.find((pane) => {
                    if (pane.key === draft.hcrTabData.activeKey) {
                        action.payload.forEach((ele) => {
                            pane[ele.key] = ele.value;
                        });
                        return true;
                    }
                    return false;
                });
                draft.isHcrLoaded = true;
            });
        }
        case actionTypes.SAVE_HCR_DETAILS: {
            return produce(state, (draft) => {
                // const {key, value} = action.payload;
                const reqPane = getReqPane(draft);
                reqPane.saveDetails = action.payload;
                reqPane.title = action.payload?.hcrName;
                reqPane.uuid = action.payload?.uuid;
            });
        }
        case actionTypes.HCR_CANVAS_PAGE_STYLES: {
            return produce(state, (draft) => {
                const {
                    key,
                    value,
                    isKeyValuePair,
                    saveOption,
                    clearKeyValuePairs,
                    type,
                } = action.payload;
                const reqPane = draft.hcrTabData.panes.find((pane) => {
                    if (pane.key === draft.hcrTabData.activeKey) {
                        return true;
                    }
                    return false;
                });
                const initalStyles = {
                    borders: {},
                    padding: {},
                    fontFill: "#000000",
                    fill: "#fefefe",
                    fontSize: 10,
                    fontFamily: "Serif",
                    bold: false,
                    italic: false,
                    underLine: false,
                    strikeThrough: false,
                    verticalAlign: 'middle',
                    horizontalAlign: "center",
                    mode: "Transparent",
                    rotation: "None",
                    stroke: 1,
                    style: 'Solid',
                    color: "#000000",
                    styleName: 'None'
                }

                const selectedStyleName = reqPane?.canvasProperties?.pageStyles?.keyValuePairs?.styleName || ''
                if (isKeyValuePair) {
                    reqPane.canvasProperties.pageStyles.keyValuePairs[key] =
                        value;
                } else if (type === 'add') {
                    reqPane.canvasProperties.pageStyles.options.push({
                        id: uuidv4(),
                        ...reqPane.canvasProperties.pageStyles.keyValuePairs,
                    });
                    reqPane.canvasProperties.pageStyles.keyValuePairs = {
                        borders: {},
                        padding: {},
                        lineStyles: {},
                    };
                    reqPane.canvasProperties.pageStyles.selectStyles = '';
                } else if (type === 'delete') {
                    reqPane.canvasProperties.pageStyles.options =
                        reqPane.canvasProperties.pageStyles.options.filter(
                            (ele) => {
                                return (
                                    ele.id !==
                                    reqPane.canvasProperties.pageStyles
                                        .keyValuePairs.id
                                );
                            }
                        );

                    reqPane.hcrDiagramNodesData = reqPane.hcrDiagramNodesData.map((node) => { // 8263
                        if (node.name === 'line') {
                            if (node.lineStyles.styleName === selectedStyleName) {
                                node = {
                                    ...node,
                                    lineStyles: {
                                        ...node.lineStyles,
                                        stroke: 1,
                                        style: 'Solid',
                                        color: "#000000",
                                        styleName: 'None'
                                    }
                                }
                            }
                        }
                        if (node?.styleName === selectedStyleName) {
                            node = {
                                ...node,
                                ...initalStyles,
                            }
                        }
                        return node;
                    })


                    reqPane.canvasProperties.pageStyles.keyValuePairs = {
                        borders: {},
                        padding: {},
                        lineStyles: {},
                    };
                    reqPane.canvasProperties.pageStyles.selectStyles = '';
                } else if (type === 'update') {
                    reqPane.canvasProperties.pageStyles.options =
                        reqPane.canvasProperties.pageStyles.options.map(
                            (ele) => {
                                if (
                                    ele.id ===
                                    reqPane.canvasProperties.pageStyles
                                        .keyValuePairs.id
                                ) {
                                    return reqPane.canvasProperties.pageStyles
                                        .keyValuePairs;
                                }
                                return ele;
                            }
                        );
                    reqPane.hcrDiagramNodesData = reqPane.hcrDiagramNodesData.map((node) => { // 8264 fix
                        if (node.name === 'line') {
                            if (node.lineStyles.styleName === selectedStyleName) {
                                const { id = '', ...restStyles } = reqPane.canvasProperties.pageStyles?.keyValuePairs?.lineStyles || {}
                                node = {
                                    ...node,
                                    lineStyles: {
                                        ...node.lineStyles,
                                        ...restStyles
                                    }
                                }
                            }
                        }
                        if (node?.styleName === selectedStyleName) {
                            const { id = '', ...restStyles } = reqPane.canvasProperties.pageStyles?.keyValuePairs || {}
                            node = {
                                ...node,
                                ...restStyles,
                            }
                        }
                        return node;
                    })
                    reqPane.canvasProperties.pageStyles.keyValuePairs = {
                        borders: {},
                        padding: {},
                        lineStyles: {},
                    };
                    reqPane.canvasProperties.pageStyles.selectStyles = '';
                } else if (clearKeyValuePairs) {
                    reqPane.canvasProperties.pageStyles.selectStyles = '';
                    reqPane.canvasProperties.pageStyles.keyValuePairs = {
                        borders: {},
                        padding: {},
                        lineStyles: {},
                    };
                } else {
                    if (key === 'selectStyles') {
                        reqPane.canvasProperties.pageStyles[key] = value;
                        const reqObj =
                            reqPane.canvasProperties.pageStyles.options.find(
                                (ele) => ele.id === value
                            );
                        reqPane.canvasProperties.pageStyles.keyValuePairs = {
                            ...reqObj,
                        };
                    }
                }
            });
        }
        case actionTypes.HCR_CANVAS_GROUP_PROPERTIES: {
            return produce(state, (draft) => {
                const { key, value, type } = action.payload;
                const reqPane = getReqPane(draft);
                function clear() {
                    reqPane.canvasProperties.groupProperties.keyValuePairs = {};
                    reqPane.canvasProperties.groupProperties.selectGroup = '';
                }

                switch (type) {
                    case "update": {
                        switch (key) {
                            case "selectGroup": {
                                reqPane.canvasProperties.groupProperties.selectGroup = value;
                                const currentGrp = reqPane.canvasProperties.groupProperties.options.find((ele) => ele.id === value);
                                reqPane.canvasProperties.groupProperties.keyValuePairs = currentGrp;
                                break;
                            }
                            default: {
                                reqPane.canvasProperties.groupProperties.keyValuePairs = {
                                    ...(reqPane.canvasProperties.groupProperties.keyValuePairs || {}),
                                    [key]: value
                                }
                                break;
                            }
                        }
                        break;
                    }
                    case "save": {
                        reqPane.canvasProperties.groupProperties.options = reqPane.canvasProperties.groupProperties.options.map((item) => {
                            if (item.id === reqPane.canvasProperties.groupProperties.selectGroup) {
                                const { id = '', name, ...rest } = reqPane.canvasProperties.groupProperties.keyValuePairs;
                                reqPane.hcrDiagramNodesData = reqPane.hcrDiagramNodesData.map((node) => {
                                    if (node.id === item.nodeId) {
                                        node.groupName = name;
                                    }
                                    return node;
                                })
                                return {
                                    ...item,
                                    name,
                                    ...rest
                                }
                            }
                            return item;
                        })
                        clear();
                        break;
                    }
                    case "clear": {
                        clear();
                        break;
                    }
                    default:
                        break
                }

                // if(isKeyValuePair) {
                // 	reqPane.canvasProperties.groupProperties.keyValuePairs[key] = value;
                // } else {
                // reqPane.canvasProperties.groupProperties[key] = value;
                // }
            });
        }
        case actionTypes.HCR_CANVAS_PREVIEW_PARAMS: {
            return produce(state, (draft) => {
                const { key, value } = action.payload;
                const reqPane = getReqPane(draft);
                reqPane.canvasProperties.previewParameters[key] = value;
            });
        }
        case actionTypes.HCR_CANVAS_CALCULATIONS: {
            return produce(state, (draft) => {
                const {
                    key,
                    value,
                    isKeyValuePair,
                    saveOption,
                    clearKeyValuePairs,
                    isDeleted = false,
                    fromAdvanceComp = false,
                    editValues = {},
                } = action.payload;
                const reqPane = getReqPane(draft);
                if (isKeyValuePair) {
                    reqPane.canvasProperties.calculations.keyValuePairs[key] =
                        value;
                } else if (saveOption) {
                    let isItemAlreadyExist = false;
                    reqPane.canvasProperties.calculations.options =
                        reqPane.canvasProperties.calculations.options.map(
                            (ele) => {
                                if (
                                    ele.id ===
                                    reqPane.canvasProperties.calculations
                                        .keyValuePairs.id
                                ) {
                                    isItemAlreadyExist = true;
                                    return reqPane.canvasProperties.calculations
                                        .keyValuePairs;
                                }
                                return ele;
                            }
                        );
                    if (!isItemAlreadyExist) {
                        reqPane.canvasProperties.calculations.options.push(
                            reqPane.canvasProperties.calculations.keyValuePairs
                        );
                    }
                    reqPane.canvasProperties.calculations.selectCalculation =
                        '';
                    reqPane.canvasProperties.calculations.keyValuePairs = {
                        id: uuidv4(),
                    };
                } else if (isDeleted) {
                    removeCalculation({ reqPane, newId: uuidv4() });
                } else if (clearKeyValuePairs) {
                    reqPane.canvasProperties.calculations.selectCalculation =
                        '';
                    reqPane.canvasProperties.calculations.keyValuePairs = {
                        id: uuidv4(),
                    };
                } else {
                    if (key === 'selectCalculation') {
                        reqPane.canvasProperties.calculations[key] = value;
                        const reqObj =
                            reqPane.canvasProperties.calculations.options.find(
                                (ele) => ele.id === value
                            );
                        reqPane.canvasProperties.calculations.keyValuePairs = {
                            ...reqObj,
                        };
                    } else if (fromAdvanceComp && key === 'selectCalculationFromAdvComp') {
                        reqPane.canvasProperties.calculations.keyValuePairs = {
                            ...editValues,
                        };
                    }
                }
            });
        }
        case actionTypes.HCR_CANVAS_PAGE_PROPS: {
            return produce(state, (draft) => {
                const { key, value } = action.payload;
                const reqPane = getReqPane(draft);
                reqPane.canvasProperties.pageProperties[key] = value;
            });
        }
        case actionTypes.HCR_ORIENTATION: {
            return produce(state, (draft) => {
                const reqPane = getReqPane(draft);
                reqPane.canvasProperties.layout.orientation = action.payload;
            });
        }
        case actionTypes.HCR_CANVAS_LAYOUT_SIZE: {
            return produce(state, (draft) => {
                const { name, size } = action.payload;
                const reqPane = getReqPane(draft);
                reqPane.canvasProperties.layout.name = name;
                reqPane.canvasProperties.layout.size = size;
            });
        }
        case actionTypes.HCR_CANVAS_MARGIN: {
            return produce(state, (draft) => {
                const { key, value } = action.payload;
                const reqPane = getReqPane(draft);
                reqPane.canvasProperties.margin[key] = value;
            });
        }
        case actionTypes.HCR_MODE: {
            return produce(state, (draft) => {
                draft.hcrMode = action.payload;
            });
        }
        case actionTypes.HCR_SET_DS_PANES: {
            return produce(state, (draft) => {
                const reqPane = getReqPane(draft);
                reqPane.dsPaneTypes = action.payload;
            });
        }
        case actionTypes.HCR_SELECT_CONNECTION: {
            return produce(state, (draft) => {
                const reqPane = getReqPane(draft);
                reqPane.selectedConnectionDetails = action.payload;
            });
        }
        case actionTypes.HCR_SELECTED_DS: {
            return produce(state, (draft) => {
                const reqPane = getReqPane(draft);
                reqPane.selectedDS = action.payload;
            });
        }
        case actionTypes.HCR_EDIT_NODES: {
            const {
                nodesToBeHighlighted = [],
                type,
                positionedNodes = [],
                nodesPositions = {}
            } = action.payload;
            return produce(state, (draft) => {
                const reqPane = getReqPane(draft);
                reqPane.hcrDiagramNodesData.forEach((node, _i, _arr) => {
                    if (type === 'removeStrokes') {
                        // not req
                        node.stroke = node.actualStroke || node.stroke;
                        delete node.actualStroke;
                    } else if (type === 'move:nodes') {
                        positionedNodes.forEach((positionNode) => {
                            if (positionNode.id === node.id) {
                                if ('x' in positionNode) {
                                    node.x = positionNode.x;
                                }
                                if ('y' in positionNode) {
                                    node.y = positionNode.y;
                                }
                            }
                        });
                        if (nodesPositions[node.id]) {
                            node.x = nodesPositions[node.id].x;
                            node.y = nodesPositions[node.id].y;
                        }
                    } else {
                        if (
                            nodesToBeHighlighted.find(
                                (ele) => ele.id === node.id
                            )
                        ) {
                            if (!node.actualStroke) {
                                // not req
                                node.actualStroke = node.stroke;
                            }
                            node.stroke = '#FF0000';
                        } else {
                            // not req
                            node.stroke = node.actualStroke || node.stroke;
                            delete node.actualStroke;
                        }
                    }
                    if (node?.parent && !node?.offset) { //  it is a child of some element
                        const parentNode = _arr.find((ele) => ele.id === node.parent);
                        node.offset = {
                            x: node.x - parentNode.x,
                            y: node.y - parentNode.y,
                        }
                    }
                });
            });
        }
        case actionTypes.HCR_ADD_NODE: {
            return produce(state, (draft) => {
                const reqPane = getReqPane(draft);
                reqPane.hcrDiagramNodesData = [
                    ...reqPane.hcrDiagramNodesData,
                    action.payload,
                ];
                // return { ...state, hcrDiagramNodesData: [...state.hcrDiagramNodesData, action.payload]};
            });
        }
        case actionTypes.HCR_ADD_NODES: {
            const { nodes, nodeId, propertiesToBeChange, maxColumns } = action.payload;
            return produce(state, (draft) => {
                const reqPane = getReqPane(draft);
                reqPane.hcrDiagramNodesData = [
                    ...reqPane.hcrDiagramNodesData.filter((node) => (node.parent !== nodeId || (node.parent === nodeId && !node.sibling && node.columnIndex <= maxColumns))),
                    ...nodes,
                ];
                reqPane.hcrDiagramNodesData = reqPane.hcrDiagramNodesData.map((node) => {
                    if (node.id === nodeId) {
                        return {
                            ...node,
                            ...propertiesToBeChange
                        }
                    }
                    return node;
                })
            });
        }
        case actionTypes.HCR_EDIT_NODE: {
            let {
                nodeData = {},
                menuKeyPath,
                pageHeight,
                pageWidth,
                type,
                selectedNodes,
                isCreateGroup,
                nodeId,
                nodeKey,
                nodeVal,
                nodesPositions = {},
                multiple = false,
                propertiesToBeChange = {},
                isCrosstab = false,
                isTable = false
            } = action.payload;
            return produce(state, (draft) => {
                const reqPane = getReqPane(draft);
                const currentNode = reqPane.hcrDiagramNodesData.find((ele) => ele.id === nodeId);
                reqPane.hcrDiagramNodesData = reqPane.hcrDiagramNodesData.map(
                    (existedNode) => {
                        if (existedNode.id === nodeId) {
                            if (!multiple) {
                                return { ...existedNode, [nodeKey]: nodeVal };
                            } else {
                                return { ...existedNode, ...propertiesToBeChange }
                            }
                        } else if (existedNode.id === nodeData.id) {
                            if (isCreateGroup) {
                                if (
                                    !reqPane.canvasProperties.groupProperties.options.find(
                                        (ele) => ele.nodeId === nodeData.id
                                    )
                                ) {
                                    reqPane.canvasProperties.groupProperties.options =
                                        [
                                            ...reqPane.canvasProperties
                                                .groupProperties.options,
                                            {
                                                nodeId: nodeData.id,
                                                id: reqPane.groupCount + 1,
                                                expression: nodeData.label,
                                                name: `group_${nodeData.name}`,
                                            },
                                        ];
                                    ++reqPane.groupCount;
                                    nodeData.isGrp = true;
                                    nodeData.grpId = reqPane.groupCount;
                                }
                            }
                            if (nodesPositions[nodeData.id]) {
                                nodeData.x = nodesPositions[nodeData.id].x;
                                nodeData.y = nodesPositions[nodeData.id].y;
                            }
                            return nodeData;
                        }
                        return existedNode;
                    }
                );
                if (isTable && multiple) {
                    reqPane.hcrDiagramNodesData = reqPane.hcrDiagramNodesData.map(node => {
                        if (node?.parent === nodeId) {
                            const parentPrevX = currentNode?.x || 0,
                                parentPrevY = currentNode?.y || 0,
                                parentX = propertiesToBeChange?.x || 0,
                                parentY = propertiesToBeChange?.y || 0;
                            const nodeOffsetX = node.x - parentPrevX, nodeOffsetY = node.y - parentPrevY;
                            node.x = parentX + nodeOffsetX;
                            node.y = parentY + nodeOffsetY;
                        }
                        return node;
                    })
                }
                if (isCrosstab && multiple) {
                    reqPane.hcrDiagramNodesData = reqPane.hcrDiagramNodesData.map(node => {
                        if (node?.parent === nodeId) {
                            if (node?.aggregateFn) {  // change the aggreagte fn of child [8660]
                                node.aggregateFn = propertiesToBeChange?.measuresAggregateMap?.[node.value] || "Count";
                            }
                            const { padding = {} } = propertiesToBeChange || {};
                            if (!isEmpty(padding) && ("Top" in padding || "Left" in padding)) { // [8628]
                                let nodeX = node.x,
                                    nodeY = node.y,
                                    parentX = propertiesToBeChange?.x || 0,
                                    parentY = propertiesToBeChange?.y || 0,
                                    parentPositionRequired = true;
                                if (node?.offset) {
                                    nodeX = node.offset?.x;
                                    nodeY = node.offset?.y;
                                } else {
                                    node.offset = {
                                        x: nodeX - parentX,
                                        y: nodeY - parentY
                                    }
                                    parentPositionRequired = false
                                }
                                let pt = padding.Top || 0,
                                    pl = padding.Left || 0;
                                node.x = nodeX + (parentPositionRequired ? parentX : 0) + (pl > 1 ? pl : 0);
                                node.y = nodeY + (parentPositionRequired ? parentY : 0) + (pt > 1 ? pt : 0);
                            }
                        }
                        return node;
                    })
                }
                if (
                    type === 'contextMenuAlign' ||
                    type === 'contextMenuRepeat'
                ) {
                    if (
                        type === 'contextMenuRepeat' &&
                        menuKeyPath[1] !== 'groupBy'
                    ) {
                        const allBands = ['rt', 'pg', 'cl', 'rd', 'lpf', 'nd'];
                        if (
                            reqPane.canvasProperties.groupProperties.options
                                ?.length
                        ) {
                            reqPane.canvasProperties.groupProperties.options.forEach(
                                (ele) => {
                                    allBands.push(`gp${ele.id}`);
                                }
                            );
                        }
                        if (nodeData.repeat !== menuKeyPath[0]) {
                            allBands.forEach((bnd) => {
                                nodeData[bnd] = null;
                            });
                        }
                    }
                    if (menuKeyPath[1] !== 'groupBy') {
                        // nodeData[menuKeyPath[1]] = menuKeyPath[0];
                        const allNodes = reqPane.hcrDiagramNodesData;
                        if (selectedNodes?.length) {
                            selectedNodes.forEach((selectedNode) => {
                                const reqNode = allNodes.find(
                                    (node) => node.id === selectedNode.id
                                );
                                reqNode[menuKeyPath[1]] = menuKeyPath[0];
                                if (["rt", "pg", "cl"].includes(menuKeyPath[0])) {
                                    reqNode[menuKeyPath[0]] = "header";
                                }
                            });
                        } else {
                            nodeData[menuKeyPath[1]] = menuKeyPath[0];
                            if (["rt", "pg", "cl"].includes(menuKeyPath[0])) {
                                nodeData[menuKeyPath[0]] = "header";
                            }
                        }
                    }
                }
                if (type === 'contextMenuAlign') {
                    if (menuKeyPath[0] === 'stickToTop') {
                        const allNodes = reqPane.hcrDiagramNodesData;
                        function handleStickToTop(reqNode) {
                            const reqNodeHorStart = reqNode.x;
                            const reqNodeHorEnd = reqNode.x + reqNode.width;
                            const nodesArr = allNodes.filter((item) => {
                                const itemStart = item.x;
                                const itemEnd = item.x + item.width;
                                if (
                                    item.id !== reqNode.id &&
                                    item.y + item.height <= reqNode.y &&
                                    ((itemStart <= reqNodeHorStart &&
                                        reqNodeHorStart <= itemEnd) ||
                                        (itemStart <= reqNodeHorEnd &&
                                            reqNodeHorEnd <= itemEnd) ||
                                        (reqNodeHorStart <= itemStart &&
                                            itemStart <= reqNodeHorEnd) ||
                                        (reqNodeHorStart <= itemEnd &&
                                            itemEnd <= reqNodeHorEnd))
                                ) {
                                    return true;
                                }
                                return false;
                            });
                            let reqY = 0;
                            nodesArr.forEach((ele) => {
                                if (ele.y + ele.height > reqY) {
                                    reqY = ele.y + ele.height;
                                }
                            });
                            const parentY = reqNode.y;
                            reqNode.y = reqY;
                            if (reqNode.isGroup && reqNode?.groupChildren?.length) {
                                let children = allNodes.filter((node) => reqNode.groupChildren.includes(node.id));
                                children.forEach((child) => {
                                    // child.y = child.y - reqY;
                                    let childOffsetY = child.y - parentY
                                    child.y = reqNode.y + childOffsetY
                                })
                            }
                        }
                        if (selectedNodes.length) {
                            selectedNodes = selectedNodes.sort(
                                (a, b) => a.y - b.y
                            );
                            selectedNodes.forEach((selectedNode) => {
                                const reqNode = allNodes.find(
                                    (node) => node.id === selectedNode.id
                                );
                                handleStickToTop(reqNode);
                            });
                        } else {
                            handleStickToTop(nodeData);
                        }
                        reqPane.hcrDiagramNodesData = [...(allNodes || [])];
                    } else if (menuKeyPath[0] === 'stickToLeft') {
                        const allNodes = reqPane.hcrDiagramNodesData;
                        function handleStickToLeft(reqNode) {
                            const reqNodeVerStart = reqNode.y;
                            const reqNodeVerEnd = reqNode.y + reqNode.height;
                            const nodesArr = allNodes.filter((item) => {
                                const itemStart = item.y;
                                const itemEnd = item.y + item.height;
                                if (
                                    item.id !== reqNode.id &&
                                    item.x + item.width <= reqNode.x &&
                                    ((itemStart <= reqNodeVerStart &&
                                        reqNodeVerStart <= itemEnd) ||
                                        (itemStart <= reqNodeVerEnd &&
                                            reqNodeVerEnd <= itemEnd) ||
                                        (reqNodeVerStart <= itemStart &&
                                            itemStart <= reqNodeVerEnd) ||
                                        (reqNodeVerStart <= itemEnd &&
                                            itemEnd <= reqNodeVerEnd))
                                ) {
                                    return true;
                                }
                                return false;
                            });
                            let reqX = 0;
                            nodesArr.forEach((ele) => {
                                if (ele.x + ele.width > reqX) {
                                    reqX = ele.x + ele.width;
                                }
                            });
                            if (reqX < 0) {
                                reqX = 0;
                            }
                            const parentX = reqNode.x;
                            reqNode.x = reqX;
                            if (reqNode.isGroup && reqNode?.groupChildren?.length) {
                                let children = allNodes.filter((node) => reqNode.groupChildren.includes(node.id));
                                children.forEach((child) => {
                                    // child.x = child.x - reqNode.x
                                    let childOffsetX = child.x - parentX
                                    child.x = reqNode.x + childOffsetX
                                })
                            }
                        }
                        if (selectedNodes.length) {
                            selectedNodes = selectedNodes.sort(
                                (a, b) => a.x - b.x
                            );
                            selectedNodes.forEach((selectedNode) => {
                                const reqNode = allNodes.find(
                                    (node) => node.id === selectedNode.id
                                );
                                handleStickToLeft(reqNode);
                            });
                        } else {
                            handleStickToLeft(nodeData);
                        }
                        reqPane.hcrDiagramNodesData = [...(allNodes || [])];
                    } else if (menuKeyPath[0] === 'stickToRight') {
                        const allNodes = reqPane.hcrDiagramNodesData;
                        function handleStickToRight(reqNode) {
                            const reqNodeVerStart = reqNode.y;
                            const reqNodeVerEnd = reqNode.y + reqNode.height;
                            const nodesArr = allNodes.filter((item) => {
                                const itemStart = item.y;
                                const itemEnd = item.y + item.height;
                                if (
                                    item.id !== reqNode.id &&
                                    item.x >= reqNode.x + reqNode.width &&
                                    ((itemStart <= reqNodeVerStart &&
                                        reqNodeVerStart <= itemEnd) ||
                                        (itemStart <= reqNodeVerEnd &&
                                            reqNodeVerEnd <= itemEnd) ||
                                        (reqNodeVerStart <= itemStart &&
                                            itemStart <= reqNodeVerEnd) ||
                                        (reqNodeVerStart <= itemEnd &&
                                            itemEnd <= reqNodeVerEnd))
                                ) {
                                    return true;
                                }
                                return false;
                            });
                            let reqX = pageWidth - reqNode.width;
                            nodesArr.forEach((ele) => {
                                if (ele.x - reqNode.width < reqX) {
                                    reqX = ele.x - reqNode.width;
                                }
                            });
                            if (reqX > pageWidth - reqNode.width) {
                                reqX = pageWidth - reqNode.width;
                            }
                            const parentX = reqNode.x;
                            reqNode.x = reqX;
                            if (reqNode.isGroup && reqNode?.groupChildren?.length) {
                                let children = allNodes.filter((node) => reqNode.groupChildren.includes(node.id));
                                children.forEach((child) => {
                                    let childOffsetX = child.x - parentX
                                    child.x = reqNode.x + childOffsetX
                                })
                            }
                        }
                        if (selectedNodes.length) {
                            selectedNodes = selectedNodes.sort(
                                (a, b) => b.x + b.width - (a.x + a.width)
                            );
                            selectedNodes.forEach((selectedNode) => {
                                const reqNode = allNodes.find(
                                    (node) => node.id === selectedNode.id
                                );
                                handleStickToRight(reqNode);
                            });
                        } else {
                            handleStickToRight(nodeData);
                        }
                        reqPane.hcrDiagramNodesData = [...(allNodes || [])];
                    } else if (menuKeyPath[0] === 'leftAlign') {
                        if (selectedNodes.length) {
                            selectedNodes.forEach((selectedNode) => {
                                const reqNode =
                                    reqPane.hcrDiagramNodesData.find(
                                        (node) => node.id === selectedNode.id
                                    );
                                if (reqNode.isGroup && reqNode?.groupChildren?.length) {
                                    const allNodes = reqPane.hcrDiagramNodesData;
                                    let children = allNodes.filter((node) => reqNode.groupChildren.includes(node.id));
                                    children.forEach((child) => {
                                        child.x = child.x - reqNode.x
                                    })
                                }
                                reqNode.x = 0;
                            });
                        } else {
                            if (nodeData.isGroup && nodeData?.groupChildren?.length) {
                                const allNodes = reqPane.hcrDiagramNodesData;
                                let children = allNodes.filter((node) => nodeData.groupChildren.includes(node.id));
                                children.forEach((child) => {
                                    child.x = child.x - nodeData.x
                                })
                            }
                            nodeData.x = 0;
                        }
                    } else if (menuKeyPath[0] === 'rightAlign') {
                        if (selectedNodes.length) {
                            selectedNodes.forEach((selectedNode) => {
                                const reqNode =
                                    reqPane.hcrDiagramNodesData.find(
                                        (node) => node.id === selectedNode.id
                                    );
                                const parentX = reqNode.x;
                                reqNode.x = pageWidth - nodeData.width;
                                if (reqNode.isGroup && reqNode?.groupChildren?.length) {
                                    const allNodes = reqPane.hcrDiagramNodesData;
                                    let children = allNodes.filter((node) => reqNode.groupChildren.includes(node.id));
                                    children.forEach((child) => {
                                        let childOffsetX = child.x - parentX
                                        child.x = reqNode.x + childOffsetX
                                    })
                                }
                            });
                        } else {
                            const parentX = nodeData.x;
                            nodeData.x = pageWidth - nodeData.width;
                            if (nodeData.isGroup && nodeData?.groupChildren?.length) {
                                const allNodes = reqPane.hcrDiagramNodesData;
                                let children = allNodes.filter((node) => nodeData.groupChildren.includes(node.id));
                                children.forEach((child) => {
                                    let childOffsetX = child.x - parentX
                                    child.x = nodeData.x + childOffsetX
                                })
                            }
                        }
                    } else if (menuKeyPath[0] === 'centerAlign') {
                        if (selectedNodes.length) {
                            selectedNodes.forEach((selectedNode) => {
                                const reqNode =
                                    reqPane.hcrDiagramNodesData.find(
                                        (node) => node.id === selectedNode.id
                                    );
                                if (reqNode.isGroup && reqNode?.groupChildren?.length) {
                                    const allNodes = reqPane.hcrDiagramNodesData;
                                    let children = allNodes.filter((node) => reqNode.groupChildren.includes(node.id));
                                    children.forEach((child) => {
                                        child.x = child.x - reqNode.x + (pageWidth - nodeData.width) / 2
                                    })
                                }
                                reqNode.x = (pageWidth - nodeData.width) / 2;
                            });
                        } else {
                            if (nodeData.isGroup && nodeData?.groupChildren?.length) {
                                const allNodes = reqPane.hcrDiagramNodesData;
                                let children = allNodes.filter((node) => nodeData.groupChildren.includes(node.id));
                                children.forEach((child) => {
                                    child.x = child.x - nodeData.x + (pageWidth - nodeData.width) / 2;
                                })
                            }
                            nodeData.x = (pageWidth - nodeData.width) / 2;
                        }
                    } else if (menuKeyPath[0] === 'fitToWidth') {
                        if (selectedNodes.length) {
                            selectedNodes.forEach((selectedNode) => {
                                const reqNode =
                                    reqPane.hcrDiagramNodesData.find(
                                        (node) => node.id === selectedNode.id
                                    );
                                reqNode.width = pageWidth;
                                if (reqNode.isGroup && reqNode?.groupChildren?.length) {
                                    const allNodes = reqPane.hcrDiagramNodesData;
                                    let children = allNodes.filter((node) => reqNode.groupChildren.includes(node.id));
                                    children.forEach((child) => {
                                        child.x = child.x - reqNode.x
                                    })
                                }
                                reqNode.x = 0;
                            });
                        } else {
                            nodeData.width = pageWidth;
                            if (nodeData.isGroup && nodeData?.groupChildren?.length) {
                                const allNodes = reqPane.hcrDiagramNodesData;
                                let children = allNodes.filter((node) => nodeData.groupChildren.includes(node.id));
                                children.forEach((child) => {
                                    child.x = child.x - nodeData.x
                                })
                            }
                            nodeData.x = 0;
                        }
                    } else if (menuKeyPath[0] === 'commonHeight') {
                        selectedNodes.forEach((item) => {
                            reqPane.hcrDiagramNodesData.find((existedNode) => {
                                if (
                                    item.id === existedNode.id &&
                                    existedNode.id !== nodeData.id
                                ) {
                                    existedNode.height = nodeData.height;
                                    return true;
                                }
                                return false;
                            });
                        });
                    } else if (menuKeyPath[0] === 'commonWidth') {
                        selectedNodes.forEach((item) => {
                            reqPane.hcrDiagramNodesData.find((existedNode) => {
                                if (
                                    item.id === existedNode.id &&
                                    existedNode.id !== nodeData.id
                                ) {
                                    existedNode.width = nodeData.width;
                                    return true;
                                }
                                return false;
                            });
                        });
                    } else if (menuKeyPath[0] === 'commonHeightWidth') {
                        selectedNodes.forEach((item) => {
                            reqPane.hcrDiagramNodesData.find((existedNode) => {
                                if (
                                    item.id === existedNode.id &&
                                    existedNode.id !== nodeData.id
                                ) {
                                    existedNode.height = nodeData.height;
                                    existedNode.width = nodeData.width;
                                    return true;
                                }
                                return false;
                            });
                        });
                    } else if (menuKeyPath[0] === 'alignWithFirstNode') {
                        let fstNode;
                        selectedNodes.forEach((item, i) => {
                            if (i === 0) {
                                fstNode = item;
                            } else {
                                reqPane.hcrDiagramNodesData.find(
                                    (existedNode) => {
                                        if (item.id === existedNode.id) {
                                            existedNode.height = fstNode.height;
                                            existedNode.width = fstNode.width;
                                            existedNode.x = fstNode.x;
                                            return true;
                                        }
                                        return false;
                                    }
                                );
                            }
                        });
                    }

                    if (['stickToTop', 'stickToLeft', 'stickToRight', 'leftAlign', 'rightAlign', 'centerAlign', 'fitToWidth'].includes(menuKeyPath[0])) {
                        reqPane.hcrDiagramNodesData = reqPane.hcrDiagramNodesData.map((node, _i, _arr) => {
                            if (node?.parent && !node?.offset) { //  it is a child of some element
                                const parentNode = _arr.find((ele) => ele.id === node.parent);
                                node.offset = {
                                    x: node.x - parentNode.x,
                                    y: node.y - parentNode.y,
                                }
                            }
                            return node;
                        })
                    }

                } else if (type === 'contextMenuRepeat') {
                    if (menuKeyPath[1] === 'groupBy') {
                        // const reqGrpId = reqPane.canvasProperties.groupProperties.options?.find(ele => {
                        // 	return `gp${ele.id}` === menuKeyPath[0]
                        // })?.id;
                        const allBands = ['rt', 'pg', 'cl', 'rd', 'lpf', 'nd'];
                        if (
                            reqPane.canvasProperties.groupProperties.options
                                ?.length
                        ) {
                            reqPane.canvasProperties.groupProperties.options.forEach(
                                (ele) => {
                                    allBands.push(`gp${ele.id}`);
                                }
                            );
                        }
                        if (nodeData.repeat !== menuKeyPath[0]) {
                            allBands.forEach((bnd) => {
                                if (nodeData[bnd]) {
                                    nodeData[bnd] = null;
                                }
                            });
                        }
                        nodeData.repeat = menuKeyPath[0];
                        if (menuKeyPath.length < 4) {
                            nodeData[menuKeyPath[0]] = "header"
                        }
                    }
                }
                // return { ...state, hcrDiagramNodesData: [...draft.hcrDiagramNodesData]};
                // return {...state, }
            });
        }
        case actionTypes.HCR_DS_PANE_ITEM_ADD: {
            return produce(state, (draft) => {
                const reqPane = getReqPane(draft);
                const dsType = reqPane.dsPaneTypes.find(
                    (ele) => ele.dataSourcePane === action.payload
                );
                let newItem;
                if (dsType.menu.length) {
                    newItem = {
                        id: dsType.menu[dsType.menu.length - 1].id + 1,
                    };
                } else {
                    newItem = { id: 1 };
                }
                if (dsType.dataSourcePane === hcrDSQuery) {
                    newItem.name = `Query${newItem.id}`;
                    newItem.config = '';
                    newItem.connectionDetails =
                        reqPane.selectedConnectionDetails;
                    newItem.executeQueryData = { data: [], field: [] };
                    newItem.isNameEditable = false;
                    newItem.isSaved = true;
                    newItem.parameterList = [];
                } else {
                    newItem.name = `Parameter${newItem.id}`;
                    newItem.config = '';
                    // newItem.value = null;
                    newItem.connectionDetails =
                        reqPane.selectedConnectionDetails;
                    newItem.executeQueryData = { data: [], field: [] };
                    newItem.isNameEditable = false;
                    newItem.isSaved = true;
                    newItem.type = 'java.lang.String';
                    newItem.parameterList = [];
                    newItem.canvasValues = {
                        filterType: hcrParaInput,
                        multipleType: false,
                        disabled: false,
                        isChecked: false,
                        open: "'",
                        close: "'",
                    };
                }
                dsType.menu.push(newItem);
            });
        }
        case actionTypes.HCR_DS_PANE_ITEM_EDIT: {
            const {
                dataSourcePane,
                itemId,
                value,
                key,
                parameterObj,
                isDateRange,
                paraToChange,
                isFilterTypeChanged,
                isFormatChnged,
                paraDisabled,
                paraChecked
            } = action.payload;
            return produce(state, (draft) => {
                const reqPane = getReqPane(draft);
                const dsType = reqPane.dsPaneTypes.find(
                    (ele) => ele.dataSourcePane === dataSourcePane
                );

                if (dataSourcePane === hcrDSParameter && key === "name") { // [8634]
                    const currentP = dsType.menu.find((ele) => ele.id === itemId);
                    const prevName = currentP.name || "";
                    dsType.menu.forEach((ele) => {
                        if (ele.canvasValues?.start && ele.canvasValues?.end) {
                            if (ele.canvasValues.start === prevName) {
                                ele.canvasValues.start = value;
                            }
                            if (ele.canvasValues.end === prevName) {
                                ele.canvasValues.end = value;
                            }
                        }
                    })
                }

                let changingRecord;
                dsType.menu.forEach((ele) => {
                    if (ele.id === itemId) {
                        if (key === 'config') {
                            ele.isSaved = false;
                        } else if (key === 'type') {
                            if (
                                value === 'java.lang.String' ||
                                value === 'java.util.Collection'
                            ) {
                                if (!ele.canvasValues.open) {
                                    ele.canvasValues.open = "'";
                                }
                                if (!ele.canvasValues.close) {
                                    ele.canvasValues.close = "'";
                                }
                            } else if (value === 'java.lang.Integer') {
                                if (ele.canvasValues.open === "'") {
                                    ele.canvasValues.open = '';
                                }
                                if (ele.canvasValues.close === "'") {
                                    ele.canvasValues.close = '';
                                }
                            }
                        }
                        ele[key] = value;
                        changingRecord = ele;
                    } else {
                        if (isDateRange) {
                            if (ele.name === paraToChange) {
                                // ele.canvasValues.disabled = true;
                                // ele.canvasValues.isChecked = true;
                                ele.canvasValues.disabled = paraDisabled;
                                ele.canvasValues.isChecked = paraChecked;
                                if (
                                    value.filterType
                                        .toLowerCase()
                                        .includes('time')
                                ) {
                                    ele.canvasValues.filterType =
                                        hcrParaDateAndTime;
                                    ele.canvasValues.displayFormat =
                                        'YYYY-MM-DD HH:mm:ss.S';
                                    ele.canvasValues.valueFormat =
                                        'YYYY-MM-DD HH:mm:ss.S';
                                } else {
                                    ele.canvasValues.filterType = hcrParaDate;
                                    ele.canvasValues.displayFormat =
                                        'YYYY-MM-DD';
                                    ele.canvasValues.valueFormat = 'YYYY-MM-DD';
                                }
                            }
                        } else if (isFormatChnged) {
                            if (
                                key === 'canvasValues' &&
                                value.filterType.toLowerCase().includes('range')
                            ) {
                                if (
                                    value.start === ele.name ||
                                    value.end === ele.name
                                ) {
                                    ele.canvasValues.displayFormat =
                                        value.displayFormat;
                                    ele.canvasValues.valueFormat =
                                        value.valueFormat;
                                }
                            }
                        }
                    }
                });
                // if (paraToChange) {
                //     dsType.menu.forEach((i) => {
                //         if (
                //             !i.canvasValues.filterType
                //                 .toLowerCase()
                //                 .includes('range')
                //         ) {
                //             const isExisted = dsType.menu.find((j) => {
                //                 if (
                //                     j.canvasValues.filterType
                //                         .toLowerCase()
                //                         .includes('range') &&
                //                     j.id !== i.id
                //                 ) {
                //                     if (
                //                         j.canvasValues.start === i.name ||
                //                         j.canvasValues.end === i.name
                //                     ) {
                //                         return true;
                //                     }
                //                 }
                //                 return false;
                //             });
                //             // if (!isExisted) {
                //             //     i.canvasValues.disabled = false;
                //             //     i.canvasValues.isChecked = false;
                //             // }
                //         }
                //     });
                // }
                if (isFilterTypeChanged) {
                    if (value.filterType?.toLowerCase().includes('range')) {
                        ['start', 'end'].forEach((key) => {
                            if (value[key]) {
                                dsType.menu.find((ele) => {
                                    if (
                                        value[key] === ele.name &&
                                        ele.id !== itemId
                                    ) {
                                        ele.canvasValues.isChecked = true;
                                        if (
                                            value.filterType
                                                .toLowerCase()
                                                .includes('time')
                                        ) {
                                            ele.canvasValues.filterType =
                                                hcrParaDateAndTime;
                                            ele.canvasValues.displayFormat =
                                                'YYYY-MM-DD HH:mm:ss.S';
                                            ele.canvasValues.valueFormat =
                                                'YYYY-MM-DD HH:mm:ss.S';
                                            return true;
                                        } else {
                                            ele.canvasValues.filterType =
                                                hcrParaDate;
                                            ele.canvasValues.displayFormat =
                                                'YYYY-MM-DD';
                                            ele.canvasValues.valueFormat =
                                                'YYYY-MM-DD';
                                            return true;
                                        }
                                    }
                                    return false;
                                });
                            }
                        });
                    } else {
                        dsType.menu.forEach((paraItem) => {
                            if (paraItem.id !== itemId) {
                                if (
                                    value.filterType
                                        ?.toLowerCase()
                                        .includes('input') ||
                                    value.filterType
                                        ?.toLowerCase()
                                        .includes('query')
                                ) {
                                    if (
                                        paraItem.canvasValues?.filterType
                                            ?.toLowerCase()
                                            .includes('range')
                                    ) {
                                        if (
                                            paraItem.canvasValues.start ===
                                            changingRecord?.name
                                        ) {
                                            paraItem.canvasValues.start = '';
                                        }
                                        if (
                                            paraItem.canvasValues.end ===
                                            changingRecord?.name
                                        ) {
                                            paraItem.canvasValues.end = '';
                                        }
                                    }
                                }
                                if (
                                    paraItem.canvasValues?.start ===
                                    value.start ||
                                    paraItem.canvasValues?.end === value.end
                                ) {
                                    paraItem.canvasValues.isChecked = false;
                                    paraItem.canvasValues.disabled = false;
                                }
                            }
                        });
                    }
                }
                if (dsType) {
                    reqPane.dsPaneTypes.find((ele) => {
                        if (ele.dataSourcePane === dataSourcePane) {
                            ele = { ...dsType };
                            return true;
                        }
                        return false;
                    });
                }
            });
        }
        case actionTypes.HCR_DS_PANE_ACTIVE_CONFIG: {
            const { dataSourcePane, itemId, value } = action.payload;
            return produce(state, (draft) => {
                // draft.activeConfig = {
                // 	...(action.payload || {}),
                // 	isSaved: true
                // }
            });
        }
        case actionTypes.HCR_DS_PANE_ACTIVE_CONFIG_EDIT: {
            return produce(state, (draft) => {
                // const {value} =  action.payload;
                // draft.activeConfig.value =  value;
                // draft.activeConfig.isSaved = false;
            });
        }
        case actionTypes.HCR_DS_PANE_ITEM_DELETE: {
            const { dataSourcePane, itemId } = action.payload;
            return produce(state, (draft) => {
                const reqPane = getReqPane(draft);
                let dsType = reqPane.dsPaneTypes.find(
                    (ele) => ele.dataSourcePane === dataSourcePane
                );
                dsType.menu = dsType.menu.filter((ele) => ele.id !== itemId);

                if (dataSourcePane === hcrDSParameter) {
                    const dsQuery = reqPane.dsPaneTypes.find((ele) => ele.dataSourcePane === hcrDSQuery);
                    dsQuery.menu = dsQuery.menu.map(({ parameterList = [], ...rest }) => {
                        return {
                            ...rest,
                            parameterList: parameterList.filter((ele) => ele.id !== itemId)
                        }
                    })
                }

                if (itemId === state.selectedQueryId) {
                    reqPane.selectedQueryId = null;
                }

                let otherDSType = reqPane.dsPaneTypes.find(
                    (ele) => ele.dataSourcePane !== dataSourcePane
                );
                let dsToActiveName = "",
                    dsToActiveId = "";
                if (dsType.dataSourcePane === reqPane.selectedDS.dataSourcePane && dsType.menu.length) {
                    dsToActiveName = dsType.dataSourcePane;
                    if (!dsType?.menu?.find((ele) => ele.id === reqPane.selectedDS.id)) {
                        dsToActiveId = dsType.menu[0].id;
                    }
                } else {
                    if (otherDSType?.menu?.length) {
                        dsToActiveName = otherDSType.dataSourcePane;
                        dsToActiveId = otherDSType.menu[0].id;
                    }
                }
                if (dsToActiveName && dsToActiveId) {
                    reqPane.selectedDS = { dataSourcePane: dsToActiveName, id: dsToActiveId };
                }
            });
        }
        // case actionTypes.HCR_EXECUTE_QUERY: {
        // 	const { dataSourcePane, itemId} = action.payload;
        // 	return produce(state, draft => {
        // 		let dsType = draft.dsPaneTypes.find(ele => ele.dataSourcePane === dataSourcePane);
        // 		dsType.menu = dsType.menu.filter(ele => ele.id !== itemId);
        //     })
        // };
        case actionTypes.HCR_LOADED: {
            return { ...state, isHcrLoaded: action.payload };
        }
        case actionTypes.HCR_DIAGRAM_NODES_DATA: {
            return produce(state, (draft) => {
                const reqPane = getReqPane(draft);
                reqPane.hcrDiagramNodesData = [...action.payload];
            });
        }
        case actionTypes.HCR_DELETE_NODE:
            return produce(state, (draft) => {
                const reqPane = getReqPane(draft);
                reqPane.hcrDiagramNodesData = reqPane?.hcrDiagramNodesData?.filter(
                    (node) => (node.id !== action.payload.nodeId)
                );
                reqPane.hcrDiagramNodesData = reqPane?.hcrDiagramNodesData?.filter((node) => node.parent !== action.payload.nodeId);

                if (reqPane.canvasTabViews.views.find((view) => view.id === action.payload.nodeId)) {
                    reqPane.canvasTabViews.views = reqPane.canvasTabViews.views.filter((view) => view.id !== action.payload.nodeId)
                }
                if (reqPane.canvasTabViews.views.length < 2) {
                    reqPane.canvasView = hcrCanvasViews.CANVAS
                    reqPane.canvasTabViews = initialCanvasTabView
                }

                reqPane.tableStyles = reqPane.tableStyles.filter((style) => style.tableId !== action.payload.nodeId)

                // reqPane.hcrDiagramNodesData = reqPane.hcrDiagramNodesData.map((node) => {
                //     if (node.category === "advancedTable") {
                //         for (let cellId in node.cells) {
                //             let cell = node.cells[cellId], styleNameReference = reqPane.tableStyles.find((style) => style.id === cell.styleNameReference);
                //             if (!styleNameReference) {
                //                 cell.styleNameReference = null;
                //             }
                //         }
                //     }
                //     return node;
                // })

            });
        case actionTypes.HCR_DELETE_NODES:
            return produce(state, (draft) => {
                const reqPane = getReqPane(draft);
                reqPane.hcrDiagramNodesData = reqPane?.hcrDiagramNodesData?.filter(
                    (node) => !action.payload.includes(node.id)
                )
                reqPane.hcrDiagramNodesData = reqPane?.hcrDiagramNodesData?.filter(
                    (node) => !action.payload.includes(node.parent)
                )

                if (reqPane.canvasTabViews.views.find((view) => action.payload.includes(view.id))) {
                    reqPane.canvasTabViews.views = reqPane.canvasTabViews.views.filter((view) => !action.payload.includes(view.id))
                }
                if (reqPane.canvasTabViews.views.length < 2) {
                    reqPane.canvasView = hcrCanvasViews.CANVAS
                    reqPane.canvasTabViews = initialCanvasTabView
                }

                // for table delete 
                const prevTableStyles = cloneDeep(reqPane.tableStyles);
                reqPane.tableStyles = reqPane.tableStyles.filter((style) => !action.payload.includes(style.tableId))
                reqPane.hcrDiagramNodesData = updateElementsWithStyles(prevTableStyles, reqPane.tableStyles, reqPane.hcrDiagramNodesData);


                // reqPane.hcrDiagramNodesData = reqPane.hcrDiagramNodesData.map((node) => {
                //     if (node.category === "advancedTable") {
                //         for (let cellId in node.cells) {
                //             let cell = node.cells[cellId], styleNameReference = reqPane.tableStyles.find((style) => style.id === cell.styleNameReference);
                //             if (!styleNameReference) {
                //                 cell.styleNameReference = null;
                //             }
                //         }
                //     }
                //     return node;
                // })
            });
        case actionTypes.ADD_HCR_TAB_DATA: {
            return produce(state, (draft) => {
                const newPane = {
                    ...action.payload.pane,
                    ...hcrTabInitialState,
                };
                if (action.payload.hcrEditedReportArr) {
                    draft.isHcrLoaded = true;
                    newPane.mode = "edit";
                    action.payload.hcrEditedReportArr.forEach((ele) => {
                        newPane[ele.key] = ele.value;
                    });
                    const emptyPane = action.payload.emptyPane;
                    if (emptyPane) {
                        draft.hcrTabData = {
                            panes: [
                                ...state.hcrTabData.panes.map((ele) => {
                                    if (ele.key === emptyPane.key) {
                                        newPane.key = ele.key;
                                        return newPane;
                                    }
                                    return ele;
                                }),
                            ],
                            activeKey: newPane.key,
                        };
                    } else {
                        draft.hcrTabData = {
                            panes: [...state.hcrTabData.panes, newPane],
                            activeKey: action.payload.activeKey,
                        };
                    }
                } else {
                    draft.hcrTabData = {
                        panes: [...state.hcrTabData.panes, newPane],
                        activeKey: action.payload.activeKey,
                    };
                }
            });
        }
        case actionTypes.HCR_TAB_ACTIVE_KEY: {
            return {
                ...state,
                hcrTabData: {
                    panes: [...state.hcrTabData.panes],
                    activeKey: action.payload,
                },
            };
        }
        case actionTypes.REMOVE_HCR_TAB: {
            let filteredPanes;
            let reqActiveKey = null;

            if (state.hcrTabData.panes.length > 1) {
                filteredPanes = state.hcrTabData.panes.filter(
                    (pane) => pane.key !== action.payload
                );
                if (filteredPanes.length > 1) {
                    state.hcrTabData.panes.find((pane, i) => {
                        if (pane.key === action.payload) {
                            if (i === filteredPanes.length) {
                                reqActiveKey = filteredPanes[i - 1].key;
                            } else {
                                reqActiveKey = filteredPanes[i].key;
                            }
                            return true;
                        }
                        return false;
                    });
                } else {
                    reqActiveKey = filteredPanes[0].key;
                }
            } else {
                reqActiveKey = '1';
                filteredPanes = [
                    {
                        title: 'Untitled ' + 1,
                        key: '1',
                        uuid: uuidv4(),
                        ...hcrTabInitialState,
                        dsPaneTypes: [
                            {
                                key: 'query',
                                dataSourcePane: 'Query',
                                menu: [
                                    {
                                        id: 1,
                                        name: 'Query1',
                                        config: '',
                                        connectionDetails: null,
                                        executeQueryData: {
                                            data: [],
                                            field: [],
                                        },
                                        isNameEditable: false,
                                        isSaved: true,
                                        parameterList: [],
                                    },
                                ],
                            },
                            {
                                key: 'parameter',
                                dataSourcePane: 'Parameter',
                                menu: [],
                            },
                        ],
                        selectedDS: {
                            dataSourcePane: 'Query',
                            id: 1,
                        },
                    },
                ];
            }
            return {
                ...state,
                hcrTabData: { panes: filteredPanes, activeKey: reqActiveKey },
            };
        }
        case actionTypes.HCR__OLD_CONFIGURATIONS: {
            return { ...state, hCROldConfigurations: action.payload };
        }
        case actionTypes.HCR_UPDATE_CANVAS_PAGE_STYLES: {
            return produce(state, (draft) => {
                const reqPane = getReqPane(draft);
                reqPane.isUpdatingCanvasPageStyles = action.payload;
            });
        }
        case actionTypes.HCR_ADD_NEW_EXPORT_PRPOPERTY: {
            return produce(state, (draft) => {
                const reqPane = getReqPane(draft);
                reqPane.hcrExportProperties = [...reqPane.hcrExportProperties, action.payload];
            });
        }
        case actionTypes.HCR_EDIT_EXPORT_PROPERTY: {
            const { id, key, value, alias, deleteId = null } = action.payload;
            return produce(state, (draft) => {
                const reqPane = getReqPane(draft);
                reqPane.hcrExportProperties = reqPane.hcrExportProperties.map((ele) => {
                    if (ele.id === id) {
                        ele.key = key;
                        ele.value = value;
                        ele.alias = alias;
                    }
                    return ele;
                });
                if (deleteId) {
                    reqPane.hcrExportProperties = reqPane.hcrExportProperties.filter((ele) => ele.id !== deleteId);
                }
            });
        }
        case actionTypes.HCR_DELETE_EXPORT_PROPERTY: {
            return produce(state, (draft) => {
                const reqPane = getReqPane(draft);
                reqPane.hcrExportProperties = reqPane.hcrExportProperties.filter((ele) => ele.id !== action.payload);
            });
        }
        case actionTypes.SET_HCR_EXPORT_PROPERTIES: {
            return { ...state, hcrExportPropertiesData: action.payload };
        }

        case actionTypes.HCR_ADD_DEFAULT_EXPORT_PRPOPERTIES: {
            return produce(state, (draft) => {
                const defaultProperties = action.payload;
                const reqPane = getReqPane(draft);
                const seen = new Set();
                if (reqPane) {
                    reqPane.hcrExportProperties = [...(reqPane.hcrExportProperties || []), ...(defaultProperties || [])].filter((item) => {
                        const duplicate = seen.has(item.key);
                        seen.add(item.key);
                        return !duplicate;
                    })
                    reqPane.defaultPropertiesAdded = true
                }
            });
        }

        case actionTypes.HCR_RESIZE_NODE: {
            const { id, x: currentX, y: currentY, width, height, groupChildren = [] } = action.payload;
            return produce(state, (draft) => {
                const reqPane = getReqPane(draft);
                const allNodes = reqPane.hcrDiagramNodesData;
                const resizedNode = allNodes.find((node) => node.id === id);
                if (groupChildren?.length) {
                    let childNodes = allNodes.filter((node) => groupChildren.includes(node.id));
                    if (childNodes?.length) {
                        childNodes.forEach((child) => {
                            let childOffsetX = child.x - resizedNode.x;
                            let childOffsetY = child.y - resizedNode.y;
                            child.x = currentX + childOffsetX;
                            child.y = currentY + childOffsetY;
                        });
                    }
                }
                resizedNode.x = currentX;
                resizedNode.y = currentY;
                resizedNode.width = width;
                resizedNode.height = height;
            });
        }
        case actionTypes.HCR_QUERY_RUNNING: {
            return produce(state, (draft) => {
                const reqPane = getReqPane(draft);
                reqPane.hcrQueryRunning = action.payload;
            });
        }
        case actionTypes.HCR_NODES_SELECTION: {
            const { parentNodeId, type } = action.payload || {}
            return produce(state, (draft) => {
                const reqPane = getReqPane(draft);
                switch (type) {
                    case "select":
                        reqPane.hcrDiagramNodesData = reqPane.hcrDiagramNodesData.map(
                            (node) => {
                                if (node.parent) {
                                    if (node.parent === parentNodeId) {
                                        node.selectable = true;
                                    } else {
                                        node.selectable = false;
                                    }
                                }
                                return node;
                            }
                        );
                        break;
                    case "unselectAll":
                        reqPane.hcrDiagramNodesData = reqPane.hcrDiagramNodesData.map(
                            (node) => {
                                if (node.parent) {
                                    node.selectable = false;
                                }
                                return node;
                            }
                        )
                        break;
                    default:
                        break;
                }
            });
        }

        case actionTypes.HCR_UPDATE_CANVAS_VIEW: {
            const { view, viewData = {}, active = hcrCanvasViews.CANVAS, id, tabIds = [] } = action.payload || {}
            return produce(state, (draft) => {
                const reqPane = getReqPane(draft);
                switch (view) {
                    case hcrCanvasViews.CANVAS:
                        reqPane.canvasView = hcrCanvasViews.CANVAS
                        reqPane.canvasTabViews = initialCanvasTabView
                        break;
                    case hcrCanvasViews.TAB:
                        reqPane.canvasView = hcrCanvasViews.TAB
                        reqPane.canvasTabViews = {
                            active: active,
                            views: [...reqPane.canvasTabViews.views, viewData]
                        }
                        break;
                    case "updateActive":
                        if (reqPane?.canvasProperties) {
                            if (reqPane.canvasTabViews.active !== hcrCanvasViews.CANVAS) { // table edit was active, reset table edit
                                reqPane.hcrDiagramNodesData = reqPane.hcrDiagramNodesData.map((node) => {
                                    if (node.id === reqPane.canvasTabViews.active) {
                                        node.selectedColumnId = null;
                                        node.selectedBandType = null;
                                        node.selectedCells = [];
                                        node.selectedNodes = [];
                                    }
                                    return node
                                })
                            }
                            reqPane.canvasTabViews.active = active;
                        }
                        break;
                    case "removeTab":
                        const currentActive = reqPane.canvasTabViews.active
                        if (currentActive === id) {
                            reqPane.canvasTabViews.active = hcrCanvasViews.CANVAS
                        }
                        reqPane.canvasTabViews.views = reqPane.canvasTabViews.views.filter((view) => view.id !== id)
                        break;
                    case "removeMultipleTabs": {
                        reqPane.canvasTabViews.views = reqPane.canvasTabViews.views.filter((view) => !tabIds.includes(view.id))
                        if (reqPane.canvasTabViews.views.length < 2) {
                            reqPane.canvasTabViews = initialCanvasTabView
                            reqPane.canvasView = hcrCanvasViews.CANVAS
                        }
                        break;
                    }

                    case "reset":
                        reqPane.canvasView = hcrCanvasViews.CANVAS
                        reqPane.canvasTabViews = initialCanvasTabView
                    default:
                        break;
                }
            });
        }

        case actionTypes.HCR_UPDATE_CANVAS_TAB_VIEW_COMPONENT: {
            const { id, actionType, ...restPayload } = action.payload || {}
            return produce(state, (draft) => {
                const reqPane = getReqPane(draft);
                if (reqPane) {
                    reqPane.hcrDiagramNodesData = reqPane.hcrDiagramNodesData.map(
                        (node) => {
                            if (node.id === id) {
                                updateCanvasTabViewComponent(node, actionType, { ...restPayload, activeReport: reqPane })
                            }
                            return node
                        }
                    )
                }
            });
        }

        case actionTypes.HCR_UPDATE_PREVIEW: {
            const { type, values = {}, reportKey } = action.payload || {}
            const { pages = [] } = values || {}
            return produce(state, (draft) => {
                let reqPane = getReqPaneByKey(draft, reportKey);
                if (!reqPane) {
                    reqPane = getReqPane(draft);
                }

                if (reqPane) {
                    switch (type) {
                        case "update": {
                            reqPane.hcrPreviewData = { ...(reqPane?.hcrPreviewData || {}), ...values }
                            break;
                        }
                        case "add_updatedPages": {
                            reqPane.hcrPreviewData.updatedPages = [...(reqPane.hcrPreviewData.updatedPages || []), ...(pages || [])]
                            break;
                        }
                        case "reset_updatedPages": {
                            reqPane.hcrPreviewData.updatedPages = []
                            break;
                        }
                        case "reset": {
                            reqPane.hcrPreviewData = initialPreviewData
                            break;
                        }
                        default:
                            break
                    }
                }
            });
        }

        case actionTypes.HCR_TABLE_CLIPBOARD_DATA: {
            const { type, id, nodes = [] } = action.payload || {}
            return produce(state, (draft) => {
                let reqPane = getReqPane(draft);
                if (reqPane) {
                    switch (type) {
                        case "copy": {
                            reqPane.hcrTableClipboardData[id] = {
                                cut: [],
                                copy: nodes,
                            }
                            break;
                        }
                        case "cut": {
                            reqPane.hcrTableClipboardData[id] = {
                                copy: [],
                                cut: nodes
                            }
                            break;
                        }
                        case "delete": {
                            delete reqPane.hcrTableClipboardData[id]
                        }
                        case "reset": {
                            reqPane.hcrTableClipboardData = {}
                        }
                        default:
                            break
                    }
                }
            });
        }

        case actionTypes.HCR_UPDATE_SUB_DATASETS: {
            const { reportKey, actionType, ...restPayload } = action.payload || {}
            return produce(state, (draft) => {
                let reqPane = getReqPaneByKey(draft, reportKey);
                if (!reqPane) {
                    reqPane = getReqPane(draft);
                }
                updateSubDataSets(reqPane, actionType, restPayload)
            });
        }

        case actionTypes.HCR_UPDATE_TABLE_STYLES: {
            const { reportKey, actionType, ...restPayload } = action.payload || {}
            return produce(state, (draft) => {
                let reqPane = getReqPaneByKey(draft, reportKey);
                if (!reqPane) {
                    reqPane = getReqPane(draft);
                }
                updateTableStyles(reqPane, actionType, restPayload)
            });
        }

        // case NEW_CONFIGURATION:
        // 	return { ...state, propertyPaneData: { ...state.propertyPaneData, newConfiguration: action.payload } };
        // case HCR_GROUPS:
        // 	return { ...state, propertyPaneData: { ...state.propertyPaneData, hcrGroups: action.payload } };
        // case PREVIEW_DETAILS:
        // 	return { ...state, previewDetails: action.payload };
        // case PROPERTY_PANE_DATA:
        // 	const { path, value } = action.payload;
        // 	return produce(state, (draft) => {
        // 		let obj = draft.propertyPaneData;
        // 		path.forEach((ele, i) => {
        // 			if (i !== path.length - 1) {
        // 				if (ele === '$activeShape') {
        // 					obj = obj[draft.propertyPaneData[ele]];
        // 				} else {
        // 					obj = obj[ele];
        // 				}
        // 			} else {
        // 				obj[ele] = value;
        // 			}
        // 		});
        // 	});
        // case HCR_DIAGRAM_MARGIN:
        // return {
        // 	...state,
        // 	propertyPaneData: {
        // 		...state.propertyPaneData,
        // 		pageLayoutInfo: {
        // 			...state.propertyPaneData.pageLayoutInfo,
        // 			margin: { ...state.propertyPaneData.pageLayoutInfo.margin, ...action.payload }
        // 		}
        // 	}
        // };
        // case DIAGRAM_PAGE_SIZE:
        // 	return produce(state, (draft) => {
        // 		let { width, height } = action.payload;
        // 		width = typeof width === 'string' ? width : width + 'px';
        // 		height = typeof height === 'string' ? height : height + 'px';
        // 		draft.propertyPaneData.pageLayoutInfo.size.fullPage = { width, height };
        // 		// (action.payload.value) && (draft.propertyPaneData.pageLayoutInfo.layoutName = action.payload.value);
        // 	});
        // case HCR_LAYOUT:
        // 	return produce(state, (draft) => {
        // 		draft.propertyPaneData.pageLayoutInfo.layoutName = action.payload;
        // 	});
        // case HCR_ACTIVE_ELE_ID:
        // 	return produce(state, (draft) => {
        // 		draft.propertyPaneData.$activeShape = action.payload;
        // 		if (!draft.propertyPaneData.shapeIds[draft.propertyPaneData.$activeShape]) {
        // 			draft.propertyPaneData.shapeIds[draft.propertyPaneData.$activeShape] = {
        // 				properties: { ...draft.hCROldConfigurations.HcrShapeState.textField }
        // 			};
        // 		}
        // 		draft.propertyPaneData.activeEleId = action.payload;
        // 	});
        // case TEXTFIELD_CONTENT:
        // 	return produce(state, (draft) => {
        // 		draft.propertyPaneData.shapeIds[draft.propertyPaneData.$activeShape].properties.textFieldExpression =
        // 			action.payload;
        // 	});
        default:
            return { ...state };
    }
};

// if (i === 0) {
// 	if (path.length === 1) {
// 		if (ele === '$activeShape') {
// 			draft.propertyPaneData[draft.propertyPaneData.$activeShape] = value;
// 		} else {
// 			draft.propertyPaneData[ele] = value;
// 		}
// 	} else {
// 		if (ele === '$activeShape') {
// 			obj = draft.propertyPaneData[draft.propertyPaneData.$activeShape];
// 		} else {
// 			obj = draft.propertyPaneData[ele];
// 		}
// 	}
// } else {
// 	if (i === path.length - 1) {
// 		if (ele === '$activeShape') {
// 			obj[draft.propertyPaneData.$activeShape] = value;
// 		} else {
// 			obj[ele] = value;
// 		}
// 	} else {
// 		if (ele === '$activeShape') {
// 			obj = obj[draft.propertyPaneData.$activeShape];
// 		} else {
// 			obj = obj[ele];
// 		}
// 	}
// }
