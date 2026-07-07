import { appActions } from "../../../redux/actions";
import {
  designerRedo,
  designerUndo,
  setToggleToolsAreaShelf,
  toggleDashboardDrawer,
  toggleGridItemDrawer,
  updateGroupId,
  updateParameterDrawerStatus,
} from "../../../redux/actions/dashboard-designer.actions";
import { handleFullScreenClick } from "../../hi-reports/hr-taskbar-items";
import { addItem } from "./common-functions";

export const designerShortcuts = ({ dispatch, onSave, onSaveAs ,refs,propertyPaneRefs,setRefresh}) => {
  return {
    settings: {
      shortcut: ["i"],
      scLocation:"DD",
      onClick: () => {
        dispatch(updateGroupId("header"));
        dispatch(toggleDashboardDrawer(true));
      },
    },
    saveDropdown: {
      shortcut: ["s"],
      scLocation:"DD",
      onClick: () => {
        refs?.saveDropdown?.current?.click()
        dispatch(appActions.setShotCutCurrentLocation("DD SAVE"));
      },
    },
    save: {
      shortcut: ["s","1"],
      scLocation:"DD SAVE",
      onClick: () => {
        refs?.saveDropdown?.current?.click()
        refs?.save?.current()
      },
    },
    saveAs: {
      shortcut: ["s","2"],
      scLocation:"DD SAVE",
      onClick: () => {
        refs?.saveDropdown?.current?.click()
        refs?.saveAs?.current()
      },
    },
    header: {
      shortcut: ["h"],
      scLocation:"DD",
      onClick: () => {
        dispatch(updateGroupId("header"));
      },
    },
    shadow: {
      shortcut: ["w"],
      scLocation:"DD",

      onClick: () => {
        dispatch(updateGroupId("shadow"));
      },
    },
    background: {
      shortcut: ["b"],
      scLocation:"DD",

      onClick: () => {
        dispatch(updateGroupId("background"));
      },
    },
    border: {
      shortcut: ["o"],
      scLocation:"DD",

      onClick: () => {
        dispatch(updateGroupId("border"));
      },
    },
    filters: {
      shortcut: ["p"],
      scLocation:"DD",

      onClick: () => {
        dispatch(updateGroupId("parameters"));
      },
    },
    breakpoints: {
      shortcut: ["k"],
      scLocation:"DD",

      onClick: () => {
        dispatch(updateGroupId("breakpoints"));
      },
    },
    columns: {
      shortcut: ["u"],
      scLocation:"DD",

      onClick: () => {
        dispatch(updateGroupId("columns"));
      },
    },
    grid: {
      shortcut: ["g"],
      scLocation:"DD",

      onClick: () => {
        dispatch(updateGroupId("grid"));
      },
    },
    html: {
      shortcut: ["m"],
      scLocation:"DD",
      onClick: () => {
        dispatch(updateGroupId("html"));
      },
    },
    css: {
      shortcut: ["c"],
      scLocation:"DD",
      onClick: () => {
        dispatch(updateGroupId("css"));
      },
    },
    javascript: {
      shortcut: ["j"],
      scLocation:"DD",
      onClick: () => {
        dispatch(updateGroupId("javascript"));
      },
    },
    addImage: {
      shortcut: ["1"],
      scLocation:"DD",
      onClick: () => {
        addItem({ dispatch, compType: "image" });
      },
    },
    addText: {
      shortcut: ["2"],
      scLocation:"DD",
      onClick: () => {
        addItem({ dispatch, compType: "text" });
      },
    },
    addTab: {
      shortcut: ["3"],
      scLocation:"DD",
      onClick: () => {
        addItem({ dispatch, compType: "tab" });
      },
    },
    addHtmlComponent: {
      shortcut: ["4"],
      scLocation:"DD",
      onClick: () => {
        addItem({ dispatch, compType: "html-component" });
      },
    },
    addDropdown: {
      shortcut: ["5"],
      scLocation:"DD",
      onClick: () => {
        addItem({ dispatch, compType: "select-dropdown" });
      },
    },
    undo: {
      shortcut: ["z"],
      scLocation:"DD",
      onClick: () => {
        dispatch(designerUndo());
        dispatch(toggleGridItemDrawer(false));
        dispatch(toggleDashboardDrawer(false));
      },
    },
    redo: {
      shortcut: ["y"],
      scLocation:"DD",
      onClick: () => {
        dispatch(designerRedo());
        dispatch(toggleGridItemDrawer(false));
        dispatch(toggleDashboardDrawer(false));
      },
    },
    filterDrawer: {
      shortcut: ["f"],
      scLocation:"DD",
      onClick: () => {
        dispatch(updateParameterDrawerStatus(true));
      },
    },
    gridSettings: {
      shortcut: ["9"],
      scLocation:"DD",
      onClick: () => {
        dispatch(updateGroupId("gridsettings"));
      },
    },
    gridItemSettings: {
      shortcut: ["3"],
      scLocation:"DD",
      
      onClick: () => {
        dispatch(updateGroupId("griditemsettings"));
      },
    },
    gridItemEdit: {
      shortcut: ["4"],
      scLocation:"DD",

      onClick: () => {
        dispatch(updateGroupId("edit"));
      },
    },
    alignment: {
      shortcut: ["5"],
      scLocation:"DD",

      onClick: () => {
        dispatch(updateGroupId("alignment"));
      },
    },
    toggleToolsShelf: {
      shortcut: ["l"],
      scLocation:"DD",
      onClick: () => {
        dispatch(setToggleToolsAreaShelf());

      },
    },
    applyProperties: {
      shortcut: ["a"],
      scLocation:"DD",
      onClick: () => {
        propertyPaneRefs?.applyRef?.current?.click();

      },
    },
    resetProperties: {
      shortcut: ["r"],
      scLocation:"DD",
      onClick: () => {
        propertyPaneRefs?.resetRef?.current?.click();
        propertyPaneRefs?.applyRef?.current?.click();

      },
    },
    searchProperties: {
      shortcut: ["q"],
      scLocation:"DD",
      onClick: () => {
        propertyPaneRefs?.searchRef?.current?.click();

      },
    },
    refresh: {
      shortcut: ["r"],
      scLocation:"DD",
      onClick: () => {
        setRefresh(new Date())
      },
    },
    fullscreen: {
      shortcut: ["p"],
      scLocation:"DD",
      onClick: () => {
        handleFullScreenClick()
      },
    },


  };
};
