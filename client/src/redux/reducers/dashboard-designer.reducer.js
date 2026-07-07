import initialStates from "./initialStates";
import actionTypes from "../actions/actionTypes";
import produce from "immer";
import {
  unGroup,
  getFilteredGridItemsData,
  getGridItem,
  injectLayoutToGroupedGridItem,
  changeIsDraggableInLayout,
  injectFiltersListAndListenersToGridItem,
  injectLastModifiedToGridItem,
  getUpdatedLayout,
  getGridItemLayoutObject,
  getTabsInfo,
  replaceReportRecursive,
  updateGridItem
} from "../../components/hi-dashboard-designer/utils/recursive-functions";
import { removeLayoutConstants } from "../../components/hi-dashboard-designer/utils/config-dashboard-gridSettings";
import { makeid } from "../../components/hi-dashboard-designer/utils/common-functions";
import { cloneDeep } from "lodash";
import { freeFloatFilterDefaultSettings } from "../../components/hi-dashboard-designer/utils/constants";

const designerReducer = (
  state = initialStates.dashboardDesignerIntialState,
  action
) => {
  switch (action.type) {
    case actionTypes.STORE_GRID_ITEMS_DATA:
      return produce(state, (draft) => {
        draft.gridItemsData = action.payload;
      });
    case actionTypes.STORE_BREAKPOINTS_DATA:
      return { ...state, breakpoints: action.payload };
    case actionTypes.STORE_COLUMNS_DATA:
      return { ...state, cols: action.payload };
    case actionTypes.TOGGLE_DASHBOARD_DRAWER:
      return {
        ...state,
        dashboardDrawerStatus: action.payload,
      };
    case actionTypes.CHANGE_DRAWER_POSITION:
      let updatedPosition = state.currentDrawerPosition + 1;
      if (updatedPosition > 3) {
        updatedPosition = 0;
      }
      return { ...state, currentDrawerPosition: updatedPosition };
    case actionTypes.UPDATE_GRID_SETTINGS:
      let gridSettingsData = [...state.gridSettings];
      let designerSettingsData = [...state.designerSettings];
      const itemsData = action.payload;
      itemsData.forEach((propertyPaneItem) => {
        if (propertyPaneItem.groupId === "parameters") {
          let updatedParameters = {
            ...designerSettingsData.find(
              (item) => item.key === propertyPaneItem.groupId
            )?.values,
          };
          updatedParameters[propertyPaneItem.key] = propertyPaneItem.value;

          designerSettingsData = designerSettingsData.map((item) => {
            if (item.key === propertyPaneItem.groupId) {
              return { ...item, values: updatedParameters };
            }
            return item;
          });
        }
        if (
          propertyPaneItem.groupId === "breakpoints" ||
          propertyPaneItem.groupId === "columns"
        ) {
          const groupIdSettings = [
            ...gridSettingsData.find(
              (item) => item.key === propertyPaneItem.groupId
            ).values,
          ];
          const updatedGroupIdSettings = groupIdSettings.map((item) => {
            if (item.key === propertyPaneItem.key) {
              return { ...item, value: parseInt(propertyPaneItem.value) };
            }
            return item;
          });

          gridSettingsData = gridSettingsData.map((item) => {
            if (item.key === propertyPaneItem.groupId) {
              return { ...item, values: updatedGroupIdSettings };
            }
            return item;
          });
        } else {
          let updatedGroup = {
            ...gridSettingsData.find(
              (item) => item.key === propertyPaneItem.groupId
            )?.values,
          };
          updatedGroup[propertyPaneItem.key] = propertyPaneItem.value;

          gridSettingsData = gridSettingsData.map((item) => {
            if (item.key === propertyPaneItem.groupId) {
              return { ...item, values: updatedGroup };
            }
            return item;
          });
        }
      });

      return {
        ...state,
        gridSettings: gridSettingsData,
        designerSettings: designerSettingsData,
      };

    // const updateGridSettings=({groupId,key,value})=>{
    // switch (groupId) {
    //   case "breakpoints":
    //     const breakpoints = [
    //       ...state.gridSettings.find((item) => item.key === "breakpoints")
    //         .values,
    //     ];
    //     const updatedBreakpoints = breakpoints.map((item) => {
    //       if (item.key === key) {
    //         return { ...item, value: parseInt(value) };
    //       }
    //       return item;
    //     });

    //     const updatedGrid = state.gridSettings.map((item) => {
    //       if (item.key === "breakpoints") {
    //         return { ...item, values: updatedBreakpoints };
    //       }
    //       return item;
    //     });
    //     return updatedGrid ;
    //   case "columns":
    //     const cols = [
    //       ...state.gridSettings.find((item) => item.key === "columns").values,
    //     ];
    //     const updatedCols = cols.map((item) => {
    //       if (item.key === key) {
    //         return { ...item, value: parseInt(value) };
    //       }
    //       return item;
    //     });

    //     const updatedGrid1 = state.gridSettings.map((item) => {
    //       if (item.key === "columns") {
    //         return { ...item, values: updatedCols };
    //       }
    //       return item;
    //     });
    //     return updatedGrid1;
    //   case "header":
    //     let updatedHeader = {
    //       ...state.gridSettings.find((item) => item.key === "header").values,
    //     };
    //     updatedHeader[key] = value;

    //     const updatedGrid2 = state.gridSettings.map((item) => {
    //       if (item.key === "header") {
    //         return { ...item, values: updatedHeader };
    //       }
    //       return item;
    //     });
    //     return updatedGrid2;
    //   case "shadow":
    //     let updatedShadow = {
    //       ...state.gridSettings.find((item) => item.key === "shadow").values,
    //     };
    //     updatedShadow[key] = value;

    //     const updatedGrid3 = state.gridSettings.map((item) => {
    //       if (item.key === "shadow") {
    //         return { ...item, values: updatedShadow };
    //       }
    //       return item;
    //     });
    //     return { ...state, gridSettings: updatedGrid3 };
    //   case "background":
    //     let updatedBackground = {
    //       ...state.gridSettings.find((item) => item.key === "background")
    //         .values,
    //     };
    //     updatedBackground[key] = value;

    //     const updatedGrid4 = state.gridSettings.map((item) => {
    //       if (item.key === "background") {
    //         return { ...item, values: updatedBackground };
    //       }
    //       return item;
    //     });
    //     return updatedGrid4;
    //   case "border":
    //     let updatedBorder = {
    //       ...state.gridSettings.find((item) => item.key === "border").values,
    //     };
    //     updatedBorder[key] = value;

    //     const updatedGrid5 = state.gridSettings.map((item) => {
    //       if (item.key === "border") {
    //         return { ...item, values: updatedBorder };
    //       }
    //       return item;
    //     });
    //     return updatedGrid5 };
    //   case "html":
    //     let updatedHTML = {
    //       ...state.gridSettings.find((item) => item.key === "html").values,
    //     };
    //     updatedHTML[key] = value;

    //     const updatedGrid6 = state.gridSettings.map((item) => {
    //       if (item.key === "html") {
    //         return { ...item, values: updatedHTML };
    //       }
    //       return item;
    //     });
    //     return { ...state, gridSettings: updatedGrid6 };
    //   case "css":
    //     let updatedCSS = {
    //       ...state.gridSettings.find((item) => item.key === "css").values,
    //     };
    //     updatedCSS[key] = value;

    //     const updatedGrid7 = state.gridSettings.map((item) => {
    //       if (item.key === "css") {
    //         return { ...item, values: updatedCSS };
    //       }
    //       return item;
    //     });
    //     return { ...state, gridSettings: updatedGrid7 };
    //   case "javascript":
    //     let updatedJS = {
    //       ...state.gridSettings.find((item) => item.key === "javascript")
    //         .values,
    //     };
    //     updatedJS[key] = value;

    //     const updatedGrid8 = state.gridSettings.map((item) => {
    //       if (item.key === "javascript") {
    //         return { ...item, values: updatedJS };
    //       }
    //       return item;
    //     });
    //     return { ...state, gridSettings: updatedGrid8 };
    //   case "grid":
    //     let updatedGridValues = {
    //       ...state.gridSettings.find((item) => item.key === "grid").values,
    //     };
    //     updatedGridValues[key] = value;
    //     const updatedGrid10 = state.gridSettings.map((item) => {
    //       if (item.key === "grid") {
    //         return { ...item, values: updatedGridValues };
    //       }
    //       return item;
    //     });
    //     return { ...state, gridSettings: updatedGrid10 };
    //   default:
    //     return { ...state };
    // }
    case actionTypes.UPDATE_CURRENT_GROUP_ID:
      return { ...state, currentGroupId: action.payload };
    case actionTypes.UPDATE_GROUP_ID:
      return { ...state, groupId: action.payload };
    case actionTypes.UPDATE_GRID_ITEM_STYLES:
      const { gridItemId } = action.payload;
      const gridItem = getGridItem(state.gridItemsData, gridItemId);
      const gridItemlayoutObj = produce(state, (draft) => {
        draft = getGridItemLayoutObject({
          data: state.gridItemsData,
          id: gridItemId,
          layout: state.layout,
        });
      });
      let gridItemConfig = [...gridItem.gridItemConfig];
      let updatedAlignment = {
        ...gridItemlayoutObj,
      };
      let updatedEdit = {};

      if (gridItem) {
        if (gridItem.compType === "text" || gridItem.compType === "image") {
          updatedEdit = {
            ...gridItemConfig.find((item) => item.key === "edit").values,
          };
        }
        action.payload.itemsData.forEach((propertyPaneItem) => {
          if (propertyPaneItem.groupId === "alignment") {
            updatedAlignment[propertyPaneItem.key] = propertyPaneItem.value;
          }
          if (propertyPaneItem.groupId === "edit") {
            updatedEdit[propertyPaneItem.key] = propertyPaneItem.value;

            gridItemConfig = [
              ...gridItemConfig.map((item) => {
                if (item.key === "edit") {
                  return { ...item, values: updatedEdit };
                }
                return item;
              }),
            ];
          } else {
            let updatedGroup;
            if ((gridItem.compType === "grouped-component" || gridItem.compType === "tab")
              && (propertyPaneItem.groupId === "breakpoints" || propertyPaneItem.groupId === "columns")) {
              updatedGroup = [
                ...(gridItemConfig.find(
                  (item) => item.key === propertyPaneItem.groupId
                )?.values || [])
              ];

              const toUpdate = updatedGroup.find(item => item.key === propertyPaneItem.key);
              if (toUpdate) {
                toUpdate.value = propertyPaneItem?.value;
              }
            } else {
              updatedGroup = {
                ...gridItemConfig.find(
                  (item) => item.key === propertyPaneItem.groupId
                )?.values,
              };
              updatedGroup[propertyPaneItem.key] = propertyPaneItem.value;
            }

            gridItemConfig = gridItemConfig.map((item) => {
              if (item.key === propertyPaneItem.groupId) {
                return { ...item, values: updatedGroup };
              }
              return item;
            });
          }
        });
        return produce(state, (draft) => {
          getGridItem(draft.gridItemsData, gridItemId, gridItemConfig);
          let { resultData, resultLayout } = getUpdatedLayout({
            data: draft.gridItemsData,
            id: gridItemId,
            layout: draft.layout,
            updatedItem: updatedAlignment,
          });
          if (gridItem.compType === "tab") {
            resultData = getTabsInfo(resultData, gridItemId);
          }
          draft.layout = removeLayoutConstants(resultLayout);
          draft.gridItemsData = resultData;
        });
      } else {
        return { ...state };
      }
    // if (gridItem) {
    //   switch (action.payload.groupId) {
    //     case "header":
    //       let updatedHeader = {
    //         ...gridItem.gridItemConfig.find((item) => item.key === "header")
    //           .values,
    //       };
    //       updatedHeader[action.payload.key] = action.payload.value;

    //       const updatedGridItemConfig = [
    //         ...gridItem.gridItemConfig.map((item) => {
    //           if (item.key === "header") {
    //             return { ...item, values: updatedHeader };
    //           }
    //           return item;
    //         }),
    //       ];

    //       return produce(state, (draft) => {
    //         getGridItem(
    //           draft.gridItemsData,
    //           gridItemId,
    //           updatedGridItemConfig
    //         );
    //       });
    //     case "shadow":
    //       let updatedShadow = {
    //         ...gridItem.gridItemConfig.find((item) => item.key === "shadow")
    //           .values,
    //       };
    //       updatedShadow[action.payload.key] = action.payload.value;

    //       const updatedGridItemConfig1 = [
    //         ...gridItem.gridItemConfig.map((item) => {
    //           if (item.key === "shadow") {
    //             return { ...item, values: updatedShadow };
    //           }
    //           return item;
    //         }),
    //       ];

    //       return produce(state, (draft) => {
    //         getGridItem(
    //           draft.gridItemsData,
    //           gridItemId,
    //           updatedGridItemConfig1
    //         );
    //       });

    //     case "background":
    //       let updatedBackground = {
    //         ...gridItem.gridItemConfig.find(
    //           (item) => item.key === "background"
    //         ).values,
    //       };
    //       updatedBackground[action.payload.key] = action.payload.value;

    //       const updatedGridItemConfig2 = [
    //         ...gridItem.gridItemConfig.map((item) => {
    //           if (item.key === "background") {
    //             return { ...item, values: updatedBackground };
    //           }
    //           return item;
    //         }),
    //       ];

    //       return produce(state, (draft) => {
    //         getGridItem(
    //           draft.gridItemsData,
    //           gridItemId,
    //           updatedGridItemConfig2
    //         );
    //       });
    //     case "border":
    //       let updatedBorder = {
    //         ...gridItem.gridItemConfig.find((item) => item.key === "border")
    //           .values,
    //       };
    //       updatedBorder[action.payload.key] = action.payload.value;

    //       const updatedGridItemConfig3 = [
    //         ...gridItem.gridItemConfig.map((item) => {
    //           if (item.key === "border") {
    //             return { ...item, values: updatedBorder };
    //           }
    //           return item;
    //         }),
    //       ];

    //       return produce(state, (draft) => {
    //         getGridItem(
    //           draft.gridItemsData,
    //           gridItemId,
    //           updatedGridItemConfig3
    //         );
    //       });
    //     case "html":
    //       let updatedHTML = {
    //         ...gridItem.gridItemConfig.find((item) => item.key === "html")
    //           .values,
    //       };
    //       updatedHTML[action.payload.key] = action.payload.value;

    //       const updatedGridItemConfig4 = [
    //         ...gridItem.gridItemConfig.map((item) => {
    //           if (item.key === "html") {
    //             return { ...item, values: updatedHTML };
    //           }
    //           return item;
    //         }),
    //       ];

    //       return produce(state, (draft) => {
    //         getGridItem(
    //           draft.gridItemsData,
    //           gridItemId,
    //           updatedGridItemConfig4
    //         );
    //       });
    //     case "css":
    //       let updatedCSS = {
    //         ...gridItem.gridItemConfig.find((item) => item.key === "css")
    //           .values,
    //       };
    //       updatedCSS[action.payload.key] = action.payload.value;

    //       const updatedGridItemConfig5 = [
    //         ...gridItem.gridItemConfig.map((item) => {
    //           if (item.key === "css") {
    //             return { ...item, values: updatedCSS };
    //           }
    //           return item;
    //         }),
    //       ];
    //       return produce(state, (draft) => {
    //         getGridItem(
    //           draft.gridItemsData,
    //           gridItemId,
    //           updatedGridItemConfig5
    //         );
    //       });
    //     case "javascript":
    //       let updatedJS = {
    //         ...gridItem.gridItemConfig.find(
    //           (item) => item.key === "javascript"
    //         ).values,
    //       };
    //       updatedJS[action.payload.key] = action.payload.value;

    //       const updatedGridItemConfig6 = [
    //         ...gridItem.gridItemConfig.map((item) => {
    //           if (item.key === "javascript") {
    //             return { ...item, values: updatedJS };
    //           }
    //           return item;
    //         }),
    //       ];
    //       return produce(state, (draft) => {
    //         getGridItem(
    //           draft.gridItemsData,
    //           gridItemId,
    //           updatedGridItemConfig6
    //         );
    //       });
    //     case "griditemsettings":
    //       let updatedSettings = {
    //         ...gridItem.gridItemConfig.find(
    //           (item) => item.key === "griditemsettings"
    //         ).values,
    //       };
    //       updatedSettings[action.payload.key] = action.payload.value;

    //       const updatedGridItemConfig9 = [
    //         ...gridItem.gridItemConfig.map((item) => {
    //           if (item.key === "griditemsettings") {
    //             return { ...item, values: updatedSettings };
    //           }
    //           return item;
    //         }),
    //       ];
    //       return produce(state, (draft) => {
    //         getGridItem(
    //           draft.gridItemsData,
    //           gridItemId,
    //           updatedGridItemConfig9
    //         );
    //       });

    //   case "alignment":
    //     let updatedAlignment = {};
    //     updatedAlignment[action.payload.key] = action.payload.value;

    //     // const updatedGridItemConfig10 = [
    //     //   ...gridItem.gridItemConfig.map((item) => {
    //     //     if (item.key === "alignment") {
    //     //       return { ...item, values: updatedAlignment };
    //     //     }
    //     //     return item;
    //     //   }),
    //     // ];
    //     return produce(state, (draft) => {
    //       // getGridItem(
    //       //   draft.gridItemsData,
    //       //   gridItemId,
    //       //   updatedGridItemConfig10
    //       // );
    //       const { resultData, resultLayout } = getUpdatedLayout({
    //         data: draft.gridItemsData,
    //         id: gridItemId,
    //         layout: draft.layout,
    //         updatedItem: updatedAlignment,
    //       });
    //       draft.layout = resultLayout;
    //       draft.gridItemsData = resultData;
    //     });
    //   case "edit":
    //     let updatedEdit = {
    //       ...gridItem.gridItemConfig.find((item) => item.key === "edit")
    //         .values,
    //     };
    //     updatedEdit[action.payload.key] = action.payload.value;

    //     const updatedGridItemConfig11 = [
    //       ...gridItem.gridItemConfig.map((item) => {
    //         if (item.key === "edit") {
    //           return { ...item, values: updatedEdit };
    //         }
    //         return item;
    //       }),
    //     ];
    //     return produce(state, (draft) => {
    //       getGridItem(
    //         draft.gridItemsData,
    //         gridItemId,
    //         updatedGridItemConfig11
    //       );
    //       const { resultData, resultLayout } = getUpdatedLayout({
    //         data: draft.gridItemsData,
    //         id: gridItemId,
    //         layout: draft.layout,
    //         updatedItem: updatedEdit,
    //       });
    //       draft.layout = resultLayout;
    //       draft.gridItemsData = resultData;
    //     });
    //   default:
    //     return { ...state };
    // }
    // }
    case actionTypes.ADD_GRID_ITEM:
      const itemToAdd = action.payload;

      if (itemToAdd.compType === "tab") {
        let idx = 1;
        for (let item of state.gridItemsData.filter(i => i.compType === "tab")) {
          if (idx <= item.index) {
            idx = item.index + 1;
          }
        }
        itemToAdd.index = idx;
        const headerValues = itemToAdd.gridItemConfig.find(conf => conf.key === "header")?.values;
        headerValues.title = `Grid Item ${idx}`;
      }
      const updatedGridItemsData = [...state.gridItemsData, itemToAdd];
      const updatedLayout = [...state.layout, action.payload.initialPosition];
      return produce(state, (draft) => {
        draft.gridItemsData = updatedGridItemsData;

        // bug 5434 - fix
        if (action.payload.compType !== "filter") {
          draft.itemAddedStatus = new Date().getTime();
          draft.layout = updatedLayout; // 7488 fix
        }

        if (
          action.payload.compType === "text" ||
          action.payload.compType === "tab" ||
          action.payload.compType === "image" ||
          action.payload.compType === "html-component" ||
          action.payload.compType === "select-dropdown"
        ) {
          draft.groupId = action.payload.compType === "html-component" ? "html" : "edit";
          draft.gridItemId = action.payload.id;
          draft.dashboardDrawerStatus = false;
          draft.gridItemDrawerStatus = true;
        }
      });
    case actionTypes.UPDATE_GRID_ITEM_ID:
      return { ...state, gridItemId: action.payload };
    case actionTypes.TOGGLE_GRID_ITEM_DRAWER:
      return { ...state, gridItemDrawerStatus: action.payload };
    case actionTypes.UPDATE_GRID_LAYOUT:
      return produce(state, (draft) => {
        draft.layout = action.payload.map((item) => ({ ...item }));
      });

    case actionTypes.CHANGE_IS_DRAGGABLE_GRID_ITEM:
      return produce(state, (draft) => {
        const { resultData, resultLayout } = changeIsDraggableInLayout(
          draft.gridItemsData,
          action.payload,
          draft.layout
        );
        draft.gridItemsData = resultData;
        draft.layout = resultLayout;
      });
    case actionTypes.CHANGE_IS_SELECTED_GRID_ITEM:
      const updatedGridItemsData7 = state.gridItemsData.map((item) => {
        if (item.id === action.payload) {
          return { ...item, isGrouped: !item.isGrouped };
        }
        return item;
      });
      return { ...state, gridItemsData: updatedGridItemsData7 };
    case actionTypes.REMOVE_GRID_ITEM:
      const updatedGridItemsData8 = state.gridItemsData.filter(
        (item) => item.id !== action.payload
      );
      return {
        ...state,
        gridItemsData: updatedGridItemsData8,
        gridIndex: state.gridIndex - 1,
      };
    case actionTypes.UNGROUP_GRID_ITEM:
      return produce(state, (draft) => {
        draft.gridItemsData = unGroup(draft.gridItemsData, action.payload);
      });
    case actionTypes.DELETE_GRID_ITEM:
      return produce(state, (draft) => {
        const id = action.payload;
        const item = state.gridItemsData.find(item => item.id === id);

        draft.gridItemsData = getFilteredGridItemsData(
          draft.gridItemsData,
          action.payload
        );

        if (item?.compType === "filter-component") {
          const filterId = draft.gridItemsData.find(item => item.compType === "filter" && item.filterCompId === id)?.id;
          draft.gridItemsData = draft.gridItemsData.filter(item => item.id !== filterId);

        }
        draft.gridIndex = draft.gridIndex - 1;
      });
    case "tempValue":
      return { ...state, tempValue: action.payload };
    case actionTypes.UPDATE_DASHBOARD_UUID:
      return {
        ...state,
        dashboardUUID: action.payload.uuid,
        isSaving: false,
        savedReportName: action.payload.savedReportName,
      };
    case actionTypes.UPDATE_NESTED_GRID_LAYOUT:
      return produce(state, (draft) => {
        draft.gridItemsData = injectLayoutToGroupedGridItem(
          draft.gridItemsData,
          action.payload.layout,
          action.payload.id
        );
      });
    case actionTypes.RESET_STORE:
      return initialStates.dashboardDesignerIntialState;
    case actionTypes.STORE_GRID_SETTINGS:
      return { ...state, gridSettings: action.payload };
    case actionTypes.STORE_DASHBOARD_CONFIG:
      const {
        gridSettings,
        gridItemsData,
        dashboardUUID,
        dashboardVariables,
        designerMode,
        components,
        parameterDrawerStatus,
        gridIndex,
        designerSettings,
        savedReportName,
      } = action.payload;
      return produce(state, (draft) => {
        draft.gridSettings = gridSettings;
        draft.gridItemsData = gridItemsData;
        draft.dashboardUUID = dashboardUUID;
        draft.layout = action.payload.layout;
        draft.dashboardVariables = dashboardVariables;
        draft.designerMode = designerMode;
        draft.components = components;
        draft.parameterDrawerStatus = parameterDrawerStatus;
        draft.gridIndex = gridIndex;
        draft.isLoading = false;
        draft.designerSettings = designerSettings;
        draft.savedReportName = savedReportName;
      });
    case actionTypes.STORE_FILTERS_LIST:
      return produce(state, (draft) => {
        let { filtersList, designerItemId, reportId } = action.payload;
        draft.gridItemsData = injectFiltersListAndListenersToGridItem({
          data: draft.gridItemsData,
          filtersList,
          id: designerItemId,
          reportId,
        });
      });
    case actionTypes.UPDATE_DESIGNER_SETTINGS:
      // const { key, value, groupId } = action.payload;
      switch (action.payload.groupId) {
        case "parameters":
          let updatedParameters = {
            ...state.designerSettings.find((item) => item.key === "parameters")
              .values,
          };
          updatedParameters[action.payload.key] = action.payload.value;

          const updatedDesignerSettings = state.designerSettings.map((item) => {
            if (item.key === "parameters") {
              return { ...item, values: updatedParameters };
            }
            return item;
          });
          return { ...state, designerSettings: updatedDesignerSettings };
        default:
          return { ...state };
      }
    case actionTypes.ADD_FILTER_ITEM:
      return {
        ...state,
        filterItemsData: [
          ...state.filterItemsData,
          {
            ...action.payload,
            index: state.filterItemsData.length,
            key: (state.filterItemsData.length + 1).toString(),
          },
        ],
      };
    case actionTypes.STORE_FILTER_ITEMS_TO_GRID_ITEMS_DATA:
      const nonFilterItems = state.gridItemsData.filter(
        (item) => item.reportInfo?.mode !== "filter"
      );
      return produce(state, (draft) => {
        draft.gridItemsData = [...nonFilterItems, ...action.payload];
      });

    case actionTypes.STORE_FILTER_ITEMS_DATA:
      return { ...state, filterItemsData: action.payload };
    case actionTypes.UPDATE_PARAMETER_DRAWER_STATUS:
      return { ...state, parameterDrawerStatus: action.payload };
    case actionTypes.SET_PREVIEW_MODE:
      return { ...state, previewMode: action.payload };
    case actionTypes.MAXIMISE_GRID_ITEM:
      return {
        ...state,
        maximizedGridItem: action.payload.data,
        maximizingStatus: action.payload.status,
      };
    case actionTypes.STORE_LAST_MODIFIED:
      return produce(state, (draft) => {
        let { lastModified, designerItemId } = action.payload;
        draft.gridItemsData = injectLastModifiedToGridItem({
          data: draft.gridItemsData,
          lastModified,
          id: designerItemId,
        });
      });
    case actionTypes.RESET_DESIGNER_STATE:
      return initialStates.dashboardDesignerIntialState;
    case actionTypes.SET_DESIGNER_MODE:
      return {
        ...initialStates.dashboardDesignerIntialState,
        designerMode: action.payload,
        gridIndex: state.gridIndex,
      };

    case actionTypes.SET_IS_LOADING:
      return {
        ...state,
        isLoading: action.payload,
      };
    case actionTypes.SET_FILTER_COUNTER:
      return produce(state, (draft) => {
        if (action.payload) {
          draft.filterCounter = draft.filterCounter + 1;
        } else {
          draft.filterCounter = draft.filterCounter - 1;
        }
      });
    case actionTypes.APPLY_DASHBOARD_FILTERS:
      return { ...state, applyDashboardFilters: action.payload };
    case actionTypes.DESIGNER_IS_SAVING:
      return { ...state, isSaving: action.payload };
    case actionTypes.DESIGNER_DRAWER_EXPANSION:
      return { ...state, expandDesignerDrawers: !state.expandDesignerDrawers };
    case actionTypes.UPDATE_DESIGNER_LAYOUT:
      return { ...state, designerLayout: action.payload }
    case actionTypes.TOGGLE_TOOLS_AREA_SHELF:
      return { ...state, toggleToolsAreaShelf: !state.toggleToolsAreaShelf }
    case actionTypes.ADD_TO_TAB:
      let { tabItemId, tabId, item, openThroughApi = false, openThrough, userDefinedLayout } = action.payload;
      return produce(state, (draft) => {
        if (openThroughApi) {
          // in case of openThroughApi, item is the id of the report
          item = state.gridItemsData.find(i => i.id === item);
          item.openThrough = openThrough;
        }
        draft.gridItemsData = draft.gridItemsData.map((gridItem) => {
          if (gridItem.id !== tabItemId) return gridItem;
          gridItem.children = [...gridItem.children, item];
          gridItem.tabsInfo = gridItem.tabsInfo.map((tab) => {
            if (tab.tabId === tabId) {
              const newLayout = {
                "x": 0, // Set default or adjust based on userDefinedLayout
                "y": 0,
                "w": 2,
                "h": 2,
                "i": item?.id,
                "static": false
              };
              // If userDefinedLayout exists, override defaults
              if (userDefinedLayout) {
                newLayout.x = userDefinedLayout.x || newLayout.x;
                newLayout.y = userDefinedLayout.y || newLayout.y;
                newLayout.w = userDefinedLayout.w || newLayout.w;
                newLayout.h = userDefinedLayout.h || newLayout.h;
              }

              return {
                ...tab,
                item: [...tab.item, item.id],
                layout: tab.layout ? [...tab.layout, newLayout] : [newLayout]
              };
            }
            return tab;
          });
          return gridItem;
        });
        // Remove the added item from gridItemsData
        draft.gridItemsData = draft.gridItemsData.filter(
          (gridItem) => gridItem.id !== item.id
        );
      });
    case actionTypes.UPDATE_TAB_GRID_ITEMS_LAYOUT: {
      const { tabId = null, layout = {}, isPinAction = false, layoutID = null, deleteLayout = false } = action.payload
      return produce(state, (draft) => {
        draft.gridItemsData = draft.gridItemsData.map((gridItem) => {
          if (gridItem.compType !== "tab") return gridItem;
          gridItem.tabsInfo = gridItem.tabsInfo.map((tab) => {
            if (layoutID) {
              if (isPinAction) {
                tab.layout = tab.layout.map((item) => {
                  if (item.i === layoutID) {
                    return { ...item, static: !item.static }
                  }
                  return item
                })
                return tab
              }
              if (deleteLayout) {
                tab.layout = tab.layout.filter(item => item.i !== layoutID)
                return tab
              }
            }
            if (tab.tabId === tabId) {
              return { ...tab, layout };
            }
            return tab;
          });
          return gridItem;
        });
      })
    }

    case actionTypes.UPDATE_TAB_GRID_ITEMS_LAYOUT_FROM_PROPERTIES: {
      const { gridItemId, itemsData = [] } = action.payload
      return produce(state, (draft) => {
        draft.gridItemsData = draft.gridItemsData.map((gridItem) => {
          if (gridItem.compType !== "tab") return gridItem;
          let alignmentProperties = itemsData.filter((item) => item.groupId === "alignment")
          gridItem.tabsInfo = gridItem.tabsInfo.map((tab) => {
            if (tab.item.includes(gridItemId)) {
              return {
                ...tab, layout: tab.layout.map((l) => {
                  if (l.i === gridItemId) {
                    return {
                      ...l,
                      ...alignmentProperties.reduce((acc, { key, value }) => {
                        return { ...acc, [key]: value }
                      }, {})
                    }
                  }
                  return l;
                })
              }
            }
            return tab;
          });
          return gridItem;
        });
      })
    }

    case actionTypes.UPDATE_DROPDOWN: {
      const { id, value } = action.payload;
      return produce(state, (draft) => {
        draft.gridItemsData = draft.gridItemsData.map(item => {
          if (item.id !== id) return item;

          return { ...item, selectedValue: value }
        })
      })
    }

    case actionTypes.REMOVE_ITEMS_OPEN_THROUGH_API: {
      const { id } = action.payload;
      return produce(state, (draft) => {
        // remove from dashboard
        draft.gridItemsData = draft.gridItemsData.filter(item => {
          return item.openThrough !== id
        });

        // remove from 'tab' components
        let updatedItems = [];

        for (let gridItem of draft.gridItemsData) {
          if (gridItem.compType !== "tab") {
            updatedItems.push(gridItem);
            continue;
          }

          gridItem.children = gridItem.children.filter(child => child.openThrough !== id);

          for (let tab of gridItem.tabsInfo) {
            tab.item = tab.item.filter(i => gridItem.children.map(c => c.id).includes(i))
          }

          updatedItems.push(gridItem);
        }
        draft.gridItemsData = updatedItems;


      })
    }

    case actionTypes.CHANGE_THE_REPORT: {
      const { reportInfo } = action.payload;
      return produce(state, (draft) => {
        const newId = `item-${makeid({})}`
        draft.layout = draft.layout.map(l => {
          if (l.i !== draft.replaceReportId) return l;

          return { ...l, i: newId };
        });

        draft.designerLayout = draft.designerLayout.map(l => {
          if (l.i !== draft.replaceReportId) return l;

          return { ...l, i: newId };
        });
        draft.gridItemsData = replaceReportRecursive({ reportInfo, gridItemsData: draft.gridItemsData, replaceReportId: state.replaceReportId, newId });
        draft.replaceReportId = newId;
      })
    }

    case actionTypes.OPEN_COMPACT_FB_BROWSER: {
      return produce(state, (draft) => {
        draft.openCompactFbBrower = action.payload;
      })
    }

    case actionTypes.REPLACE_REPORT_ID: {
      return produce(state, (draft) => {
        draft.replaceReportId = action.payload;
      })
    }

    case actionTypes.UPDATE_FREE_FLOAT_FILTER_SETTINGS: {
      return produce(state, (draft) => {
        const { gridItemId, key, value, groupKey } = action.payload;
        const gridItem = getGridItem(draft.gridItemsData, gridItemId);
        let gridItemConfig = cloneDeep(gridItem.gridItemConfig);
        if (!gridItemConfig.some(item => item.key === freeFloatFilterDefaultSettings.key)) {
          gridItemConfig.push(freeFloatFilterDefaultSettings);
        }
        gridItemConfig = gridItemConfig.map(item => {
          if (item.key === groupKey) {
            return { ...item, values: { ...item.values, [key]: value } }
          }
          return item;
        })
        const updatedGridItem = getGridItem(draft.gridItemsData, gridItemId, gridItemConfig)
        let udpatedGridItems = updateGridItem(draft.gridItemsData, gridItemId, updatedGridItem);
        draft.gridItemsData = udpatedGridItems
      })
    }

    default:
      return state;
  }
};
export default designerReducer;
