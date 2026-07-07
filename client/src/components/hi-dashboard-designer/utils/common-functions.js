import { Tooltip } from "antd";
import { initialConfig } from "./constants";
import {
  addGridItem,
  setFilterCounter,
  changeIsDraggableInGridItem,
  changeIsSelectedInGridItem,
  deleteGridItem,
  designerUndo,
  toggleGridItemDrawer,
  toggleDashboardDrawer,
  designerRedo,
  applyDashboardFilters,
  addToTab,
  updateTabGridItemsLayout,
  replaceReportId,
  updateFreeFloatFilterSettings
} from "../../../redux/actions/dashboard-designer.actions";
import notify from "../../hi-notifications/notify";
import { useCallback } from "react";
import { appActions, setKeysPressed, setShotCutCurrentLocation, updateDashboardVariables } from "../../../redux/actions";
import {
  designerShortcuts,
} from "./keyboard-shortcuts-designer";

const getNotfiyMsg = ({ compType, reportName }) => {
  switch (compType) {
    case "filter":
      return "Filter added successfully";
    case "dashboard-designer-component":
      return `${reportName} added successfully`;
    case "text":
      return "Text component added successfully";
    case "image":
      return "Image component added successfully";
    case "grouped-component":
      return "Grouped component created successfully";
    case "tab":
      return "Tab component created successfully";
    case "html-component":
      return "HTML component created successfully";
    case "select-dropdown":
      return "Dropdown component created successfully";
    default:
      return null;
  }
};

const getNewPosition = ({ height, width, totalColumns, layouts }) => {
  let newX = 0,
    newY = 0;
  return validatePosition({
    newX,
    newY,
    height,
    width,
    layouts,
    totalColumns,
  });
};

const getCordinates = ({ x, y, h, w }) => {
  let result = [];
  new Array(w).fill("w").map((wVal, wIndex) => {
    new Array(h).fill("h").map((hVal, hIndex) => {
      result.push(`${x + wIndex}_${y + hIndex}`);
    });
  });
  return result;
};

const validatePosition = ({
  newX,
  newY,
  height,
  width,
  layouts,
  totalColumns,
}) => {
  let proposedCordinates = getCordinates({
    x: newX,
    y: newY,
    h: height,
    w: width,
  });

  let isValidPosition = true;
  layouts.map((eachLayout) => {
    let layoutCordinates = getCordinates(eachLayout);
    let intersectedCordinates = proposedCordinates.filter((value) =>
      layoutCordinates.includes(value)
    );
    if (intersectedCordinates.length) {
      isValidPosition = false;
    }
  });
  return isValidPosition
    ? [newX, newY]
    : updatePostionAndValidate({
      newX: newX,
      newY: newY,
      height,
      width,
      layouts,
      totalColumns,
    });
};
const updatePostionAndValidate = ({
  newX,
  newY,
  height,
  width,
  layouts,
  totalColumns,
}) => {
  if (newX + 1 + width <= totalColumns) {
    newX = newX + 1;
  } else {
    newX = 0;
    newY = newY + 1;
  }
  return validatePosition({
    newX,
    newY,
    height,
    width,
    layouts,
    totalColumns,
  });
};

export const makeid = ({ hreport = false }) => {
  var text = "";
  var possible =
    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

  if (hreport) {
    possible = "abcdefghijklmnopqrstuvwxyz0123456789";
    for (var i = 0; i < 8; i++)
      text += possible.charAt(Math.floor(Math.random() * possible.length));

    return text;

  }

  for (var i = 0; i < 5; i++)
    text += possible.charAt(Math.floor(Math.random() * possible.length));

  return text;
}


export const addItem = ({
  selectedItems = [],
  dispatch,
  reportInfo,
  parameter,
  returnValue,
  index,
  compType,
  key,
  openThroughApi,
  gridItemId,
  filterCompId,
  userDefinedLayout,
  replaceReportIdValue,
}) => {
  const Notify = notify(dispatch);

  function generateLayout({ id, layout }) {
    const [x, y] = getNewPosition({
      height: 2,
      width: 2,
      totalColumns: 12,
      layouts: [...layout],
    });
    return {
      x,
      y,
      w: 2,
      h: 2,
      i: id,
      static: false,
    };
  }
  const gridItem = {
    id: `item-${makeid({})}`,
    compType,
  };

  let layout = [];
  dispatch((dispatch, getState) => {
    layout = getState()?.designer?.present?.designerLayout || [];
    replaceReportIdValue = getState()?.designer?.present?.replaceReportId || null
  });
  let gridDimension = generateLayout({
    id: gridItem.id,
    layout,
  });

  if (
    compType === "dashboard-designer-component" ||
    compType === "text" ||
    compType === "image" ||
    compType === "tab" ||
    compType === "grouped-component" ||
    compType === "html-component" ||
    compType === "filter-component" ||
    compType === "apply-reset" ||
    compType === "select-dropdown"
  ) {
    gridItem.isGrouped = false;
    gridItem.isSaved = false;

    gridItem.initialPosition = gridDimension;
    if (userDefinedLayout) {
      if (userDefinedLayout.x !== undefined) {
        (gridItem.initialPosition.x = userDefinedLayout.x);
      }
      if (userDefinedLayout.y !== undefined) {
        (gridItem.initialPosition.y = userDefinedLayout.y);
      }
      if (userDefinedLayout.h) {
        (gridItem.initialPosition.h = userDefinedLayout.h);
      }
      if (userDefinedLayout.w) {
        (gridItem.initialPosition.w = userDefinedLayout.w);
      }
    }
    gridItem.gridItemConfig = initialConfig({
      reportName: reportInfo?.file?.title,
      id: gridItem.id,
      compType,
    });

    if (replaceReportIdValue) {
      return;
    }
  }

  if (compType === "tab") {
    gridItem.tabsInfo = [{
      tabId: `${gridItem.id}-0`,
      item: [],
      name: `Tab 1`,
      layout: userDefinedLayout ? [ // Use userDefinedLayout for layout
        {
          x: userDefinedLayout.x || 0,
          y: userDefinedLayout.y || 0,
          h: userDefinedLayout.h || 2,
          w: userDefinedLayout.w || 2
        }
      ] : [],
    }]
    gridItem.children = []
    gridItem.layout = userDefinedLayout ? [
      {
        x: userDefinedLayout.x || 0,
        y: userDefinedLayout.y || 0,
        h: userDefinedLayout.h || 2,
        w: userDefinedLayout.w || 2
      }
    ] : [];
  }
  if (compType === "filter" || compType === "filter-component") {
    gridItem.index = index;
    gridItem.key = key;
    dispatch(setFilterCounter(true));
  }
  if (compType === "filter") {
    gridItem.filterCompId = filterCompId;
  }
  if (selectedItems.length >= 2 && compType === "grouped-component") {
    gridItem.initialPosition = {
      x: 0,
      y: 0,
      h: 2,
      w: 2,
      static: false,
      i: gridItem.id,
    };
    gridItem.children = selectedItems;
    // gridItem.layout = []; 
    gridItem.layout = selectedItems.map(item => item.initialPosition);
  }

  if (compType === "filter-component") {
    gridItem.initialPosition.h = 1;
    gridItem.initialPosition.w = 3;
  }

  if (compType === "apply-reset") {
    gridItem.initialPosition.h = 1;
    gridItem.initialPosition.w = 1;
  }

  if (reportInfo) {
    gridItem.reportInfo = reportInfo;
  }
  if (parameter) {
    gridItem.parameter = parameter;
  }
  if (returnValue) {
    gridItem.initialPosition = gridDimension;
    gridItem.initialPosition.i = "idtestid";
    gridItem.id = "idtestid";
    return gridItem;
  }
  if (openThroughApi) {
    gridItem.openThrough = gridItemId;
  }
  dispatch(addGridItem(gridItem));
  const notificationMsg = getNotfiyMsg({
    compType,
    reportName: reportInfo?.file?.title,
  });
  notificationMsg &&
    Notify.success({
      message: notificationMsg,
      type: "Frontend",
    });
  return gridItem.id;
};

export const sortingFunctionForContextMenu = (group) => {
  return group?.sort((a, b) => {
    if (a.children) {
      a.children = sortingFunctionForContextMenu(a.children);
    }

    return (
      !a.type && a.title?.toLowerCase().localeCompare(b.title?.toLowerCase())
    );
  });
};

export const gridItemsGroup = ({
  dispatch,
  id,
  filtersGridItemsConfig,
  compType,
  gridItemsData,
  isTabChild,
  layout,
  freeFloatFilterSettings
}) => {
  const tabItems = gridItemsData.filter(item => item.compType === "tab");
  const applyResetCreated = gridItemsData.find(item => item.compType === "apply-reset");
  const filterBringToFront = freeFloatFilterSettings?.values?.['bringToFront']
  const transparent = freeFloatFilterSettings?.values?.['transparent']
  let gridItemsGroup = [
    {
      label: <Tooltip title="Set advanced properties">Advance</Tooltip>,
      key: "advanced",
      children: [
        {
          label: <Tooltip title="Set CSS">CSS</Tooltip>,
          key: "css",
        },
        {
          label: <Tooltip title="Set HTML">HTML</Tooltip>,
          key: "html",
        },

        {
          label: <Tooltip title="Set javascript">JS</Tooltip>,
          key: "javascript",
        },
      ],
    },
    {
      label: <Tooltip title="Set background">Background</Tooltip>,
      key: "background",
    },
    {
      label: <Tooltip title="Set border">Border</Tooltip>,
      key: "border",
    },

    {
      label: <Tooltip title="Set header">Header</Tooltip>,
      key: "header",
    },
    {
      label: <Tooltip title="Set shadow">Shadow</Tooltip>,
      key: "shadow",
    },
    { type: "divider", key: "divider1" },
    ...(compType === "dashboard-designer-component"
      ? [
        {
          label: (
            <Tooltip title="Add filter(s) to dashboard">Filters</Tooltip>
          ),
          key: "parameters",
          children: filtersGridItemsConfig,
        }
      ]
      : []),
    ...(compType === "grouped-component" || compType === "tab"
      ? [
        {
          label: "Grid Settings",
          key: "gridsettings",
          children: [
            {
              label: <Tooltip title={"Set breakpoints"}>Breakpoints</Tooltip>,
              key: "breakpoints",
            },
            {
              label: <Tooltip title={"Set columns"}>Columns</Tooltip>,
              key: "columns",
            },
            {
              label: <Tooltip title={"Configure grid"}>Grid</Tooltip>,
              key: "grid",
            },
          ],
        },
      ]
      : []),
    {
      key: "settings",
      label: <Tooltip title="Settings">Settings</Tooltip>,
      children: [
        {
          label: <Tooltip title="Pin to dashboard"><span style={layout.static ? { color: "#3da1fd" } : {}}>Pin</span></Tooltip>,
          key: "pin",
          onClick: () => {
            dispatch(changeIsDraggableInGridItem(id));
            if (isTabChild) {
              dispatch(updateTabGridItemsLayout({ isPinAction: true, layoutID: id }))
            }
          },
        },
        ...(!isTabChild && compType !== "tab"
          ? [
            {
              label: <Tooltip title="Tag this item for grouping">Group</Tooltip>,
              key: "group",
              onClick: () => {
                dispatch(changeIsSelectedInGridItem(id));
              },
            }
          ] : []
        ),
        {
          label: <Tooltip title="Delete grid item">Delete</Tooltip>,
          key: "delete",
          onClick: () => {
            dispatch(deleteGridItem(id));
          },
        },
        ...(compType === "filter-component" && !applyResetCreated ? [
          {
            label: <Tooltip title="Add Apply and Reset button">Add Apply Reset button</Tooltip>,
            key: "ApplyReset",
            onClick: () => {
              addItem({ dispatch, compType: "apply-reset" });
            },
          },
        ] : []),

        ...(tabItems.length > 0 && compType !== "tab" && !isTabChild
          ? [
            {
              label: <Tooltip title="Add grid item to tab">Add To Tab</Tooltip>,
              key: "addToTab",
              children: getTabContextChildren(dispatch, tabItems, gridItemsData.find(item => item.id === id)),
            },
          ] : []
        ),
        ...(compType === "dashboard-designer-component"
          ? [
            { type: "divider", key: "divider4" },
            {
              groupKey: "griditemsettings",
              key: "export",
              label: (
                <Tooltip title="Enable exporting option">Export</Tooltip>
              ),
            },
            {
              groupKey: "griditemsettings",
              key: "editReport",
              label: <Tooltip title="Enable editing">Edit Report</Tooltip>,
            },
            {
              groupKey: "griditemsettings",
              label: (
                <Tooltip title="Enable maximize and minimize">
                  Maximize
                </Tooltip>
              ),
              key: "maximize",
            },
          ]
          : []),
      ],
    },
    ...(compType === "text" || compType === "image" || compType === "tab" || compType === "html-component" || compType === "select-dropdown"
      ? [
        { type: "divider", key: "divider6" },
        { label: "Edit", key: "edit", tooltip: "Edit" },
        { type: "divider", key: "divider7" },
      ]
      : []),
    {
      label: <Tooltip title="Set alignment">Alignment</Tooltip>,
      key: "alignment",
    },
    ...(compType === "filter-component" ? [
      {
        label: <Tooltip title="Bring To Front">
          <span style={filterBringToFront ? { color: "#3da1fd" } : {}}>Bring To Front</span>
        </Tooltip>,
        groupKey: "filterComponentSettings",
        key: "bringToFront",
        onClick: () => {
          dispatch(updateFreeFloatFilterSettings({
            gridItemId: id,
            groupKey: "filterComponentSettings",
            key: "bringToFront",
            value: true
          }));
        },
      },
      {
        label: <Tooltip title="Send To Back">
          <span style={!filterBringToFront ? { color: "#3da1fd" } : {}}>Send To Back</span>
        </Tooltip>,
        groupKey: "bringToFront",
        key: "sendToBack",
        onClick: () => {
          dispatch(updateFreeFloatFilterSettings({
            gridItemId: id,
            groupKey: "filterComponentSettings",
            key: "bringToFront",
            value: false
          }));
        },
      },
      {
        label: <Tooltip title="This will make the filter component transparent. Note: Filter should also be transparent from hreoprt using filter properties.">
          <span style={transparent ? { color: "#3da1fd" } : {}}>Transparent</span>
        </Tooltip>,
        groupKey: "transparent",
        key: "transparent",
        onClick: () => {
          dispatch(updateFreeFloatFilterSettings({
            gridItemId: id,
            groupKey: "filterComponentSettings",
            key: "transparent",
            value: !transparent
          }));
        },
      },
    ] : [])
  ];
  return gridItemsGroup;
};

export const handleDashboardDesignerKeyPress = ({
  keysPressed,
  dispatch,
  onSave,
  onSaveAs,
  altTriggered,
  refs,
  currentSCLocation,
  setRefresh,
  propertyPaneRefs = { applyRef: null, searchRef: null, resetRef: null }
}) => {

  const allDesignerShortcuts = designerShortcuts({
    dispatch,
    onSave,
    onSaveAs,
    altTriggered,
    refs,
    currentSCLocation,
    propertyPaneRefs,
    setRefresh
  });
  Object.keys(allDesignerShortcuts).forEach((item) => {
    if (
      keysPressed
        .join("")
        .endsWith(allDesignerShortcuts[item].shortcut.join(""))
    ) {
      if (allDesignerShortcuts[item].scLocation === currentSCLocation && altTriggered) {
        allDesignerShortcuts[item].onClick();
      }
    }
  });
};


export const wrapSpecialVariables = (template) => {
  const regex = /{{\s*(['"]?)([^\s{}'".&]+|[^}]+)\s*}}/g;
  if (!template) return template;
  return template.replace(regex, (match, q, variable) => {
    match = match.replace(/&amp;/g, '&')
    const wrappedRegex = /^\{\{\s*\[['"]([^'"]+)['"]\]\s*\}\}$/;
    if (wrappedRegex.test(match) || (variable.includes("|"))) {
      return match;
    }
    if (/[^\w\d.&]/.test(variable)) {
      variable = variable.replace(/&amp;/g, '&')
      return `{{['${variable}']}}`;
    } else {
      return match;
    }
  });
};

export const getDashboardVariableScope = (variables, dispatch) => {
  const changeVariableValue = (name, value) => {
    dispatch(updateDashboardVariables({
      value,
      key: name,
    }));
  }

  return {
    setVariable: (name, value) => {
      if (typeof name === "object" && !value) {
        for (let _k in name) {
          changeVariableValue(_k, name[_k])
        }
      } else {
        changeVariableValue(name, value)
      }
      let timeout = setTimeout(() => {
        clearTimeout(timeout)
        dispatch(applyDashboardFilters(new Date().getTime().toString()));
      }, 100)
    },
    getAllVariables: () => {
      return variables;
    }
  }
}

export const getTabContextChildren = (dispatch, tabItems, item) => {
  const Notify = notify(dispatch);
  return tabItems.map(tabItem => {
    const headerValues = tabItem.gridItemConfig.find(config => config.key === "header")?.values;
    // let title = `Grid Tab ${tabItem.index}`;
    // if(headerValues && headerValues.enable){
    let title = headerValues.title.replace(/<\/?.*?>/g, '');
    // }
    return {
      label: <Tooltip title="Add grid item to tab">
        {title}
      </Tooltip>,
      key: "addToSubTab-" + tabItem.id,
      children: tabItem.tabsInfo.map(tab => {
        return {
          label: <Tooltip title="Add grid item to tab">{tab.name}</Tooltip>,
          key: "addToSubTab-" + tab.tabId,
          onClick: () => {
            dispatch(addToTab({ tabItemId: tabItem.id, tabId: tab.tabId, item }));
            Notify.success({
              message: `Item added to ${tab.name} successfully`,
              type: "Frontend",
            })
          }
        }
      })
    }
  })
}

export const getComponentWidth = (id, initial) => {
  return document.getElementById(id)?.offsetWidth || initial;
}

export const getComponentHeight = (id, initial) => {
  return document.getElementById(id)?.offsetHeight || initial;
}


export const fallsInBreakpoint = (breakpoints, width) => {
  if (!breakpoints) return "xss"
  for (let breakpoint of breakpoints) {
    if (width >= breakpoint?.value) return breakpoint.key
  }

  return "xxs";
}