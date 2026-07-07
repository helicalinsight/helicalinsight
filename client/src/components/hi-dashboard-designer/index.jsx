import "../../../node_modules/react-resizable/css/styles.css";
import "../../../node_modules/react-grid-layout/css/styles.css";
import React, { useEffect, useState, useMemo, useRef } from "react";
import { Responsive, WidthProvider } from "react-grid-layout";
import { SizeAwareHIGridItem, HIGridItemDrawer } from "./components";
import { useDispatch, useSelector } from "react-redux";
import { Menu, Dropdown, Tooltip } from "antd";
import {
  toggleDashboardDrawer,
  updateGroupId,
  removeGridItem,
  updateGridLayout,
  updateGridItemId,
  updateDesignerLayout,
  updateGridItemStyles,
  removeItemsOpenThroughApi,
  addToTab
} from "../../redux/actions/dashboard-designer.actions";
import { HIDashboardDrawer, HIReportParametersDrawer } from "./components";
import "react-quill/dist/quill.snow.css";
import { getDashboardConfig } from "./utils/get-dashboard-config";
import { addItem, getDashboardVariableScope, wrapSpecialVariables } from "./utils/common-functions";
import "./index.scss";
import TutorialInfo from "../common/hi-tutorial";
import { getDashboardVariableConfig, getGlobalVariables } from "../../utils/utilities";
import { Liquid } from "liquidjs";
import notify from "../hi-notifications/notify";
import { useHistory } from "react-router-dom";
import { routesUrl } from '../../app/constants'
import { executeScript } from "../hi-reports/utils/utilities";
import { isEmpty, isEqual } from "lodash";
import { openInDashboardCallback } from "./utils/dashboard-requests";
import { getGridItem } from "./utils/recursive-functions";
import { getDropDownApis } from "./components/dropdown-component";
import { updateDashboardVariables } from "../../redux/actions";
import { freeFloatFilterDefaultSettings, freeFloatFilterKeyName } from "./utils/constants";

const ResponsiveGridLayout = WidthProvider(Responsive);

const getTabProperties = (gridItemsData) => {
  const tabGridItems = gridItemsData.filter(item => item.compType === "tab");

  const tabProperties = tabGridItems.reduce((props, item) => {
    props[item.id] = Object.seal(item.gridItemConfig.find(config => config.key === "edit")?.values);
    return props;
  }, {})

  return Object.seal(tabProperties);

}

const getTextProperties = (gridItemsData) => {
  const textGridItems = gridItemsData.filter(item => item.compType === "text");

  const textProperties = textGridItems.reduce((props, item) => {
    props[item.id] = Object.seal(item.gridItemConfig.find(config => config.key === "edit")?.values);
    return props;
  }, {})

  return Object.seal(textProperties);

}

const getImageProperties = (gridItemsData) => {
  const imageGridItems = gridItemsData.filter(item => item.compType === "image");

  const imageProperties = imageGridItems.reduce((props, item) => {
    props[item.id] = Object.seal(item.gridItemConfig.find(config => config.key === "edit")?.values);
    return props;
  }, {})

  return Object.seal(imageProperties);

}
const HIDashboardDesigner = (designerProps) => {
  const { refresh, setRefresh, parameters } = designerProps || {};
  const history = useHistory();
  const { location } = history || {};
  const { pathname } = location || {};
  const gridItemsData = useSelector((state) =>
    state.designer.present.gridItemsData.filter((item) => item.compType !== "filter") || []
  );
  const gridSettingsData = useSelector((state) => state.designer.present.gridSettings || []);
  const gridWidthOption = useSelector(
    (state) => state.designer.present.gridWidthOption
  );
  const maximizedGridItem = useSelector(
    (state) => state.designer.present.maximizedGridItem
  );
  const groupId = useSelector((state) => state.designer.present.groupId);
  const designerMode = useSelector((state) => state.designer.present.designerMode);
  const previewMode = useSelector((state) => state.designer.present.previewMode);
  const refreshDashboard = useSelector((state) => state.dashboard.present.refreshDashboard); // addded for 7720
  const isOpenMode = ["open"].includes(designerMode);
  const selectedItems = [
    ...gridItemsData.filter((item) => item.isGrouped === true),
  ];
  const [colsValues, breakpointsValues] = getDashboardConfig(
    gridSettingsData,
    null,
    ["columns", "breakpoints"],
    gridWidthOption
  );
  const headerValues = gridSettingsData?.find(
    (item) => item.key === "header"
  )?.values || {};
  const htmlValues = gridSettingsData?.find(
    (item) => item.key === "html"
  )?.values || {};
  const cssValues = gridSettingsData?.find(
    (item) => item.key === "css"
  )?.values || {};
  const jsValues = gridSettingsData?.find(
    (item) => item.key === "javascript"
  )?.values || {};
  const gridValues =
    gridSettingsData?.find((item) => item.key === "grid")?.values || {};
  const layout = useSelector((state) => state.designer.present.layout);
  const dispatch = useDispatch();
  const maximizingStatus = useSelector(
    (state) => state.designer.present.maximizingStatus
  );
  const globalVariables = getGlobalVariables({ dispatch });
  const Notify = notify(dispatch);
  const [currentBreakpoint, setCurrentBreakpoint] = useState("lg");
  const offsetHeight = document.querySelector("#hi-dashboard")?.offsetHeight
    || document.querySelector(".report-viwer-content")?.offsetHeight
  const maxLayout = [
    {
      i: maximizedGridItem.id,
      h: (offsetHeight + 10 - gridValues.verticalMargin) / (gridValues.rowHeight + 10 + gridValues.verticalMargin),
      w: colsValues[currentBreakpoint],
      x: 0,
      y: 0,
    },
  ];
  const designerSettings = useSelector(
    (state) => state.designer.present.designerSettings
  );
  const parameterSettings = designerSettings?.find(
    (item) => item.key === "parameters"
  )?.values || {};
  let dashboardVariables = useSelector(
    (state) => state.dashboard.dashboardVariables
  ) || {};
  const applyDashboardFilters = useSelector(
    (state) => state.designer.present.applyDashboardFilters
  );
  dashboardVariables = parameterSettings?.enableApplyButton
    ? null
    : dashboardVariables;

  const [borderStyles, backgroundStyles, shadowStyles, headerStyles] =
    getDashboardConfig(gridSettingsData, null, [
      "border",
      "background",
      "shadow",
      "header",
    ]);

  const gridStyles = { ...borderStyles, ...shadowStyles };

  let group = [
    {
      label: <Tooltip title={"Set advanced properties"}>Advance</Tooltip>,
      key: "advanced",
      children: [
        {
          label: <Tooltip title={"Set CSS"}>CSS</Tooltip>,
          key: "css",
          // tooltip: "Set CSS",
        },
        {
          label: <Tooltip title={"Set HTML"}>HTML</Tooltip>,
          key: "html",
          // tooltip: "Set HTML",
        },

        {
          label: <Tooltip title={"Set javascript"}>JS</Tooltip>,
          key: "javascript",
          // tooltip: "Set javascript",
        },
      ],
    },
    {
      label: <Tooltip title={"Set background"}>Background</Tooltip>,
      key: "background",
      // tooltip: "Set background",
    },
    {
      label: <Tooltip title={"Set border"}>Border</Tooltip>,
      key: "border",
      // tooltip: "Set border",
    },

    {
      label: <Tooltip title={"Set header"}>Header</Tooltip>,
      key: "header",
      // tooltip: "Set header",
    },
    {
      label: <Tooltip title={"Set shadow"}>Shadow</Tooltip>,
      key: "shadow",
      // tooltip: "Set shadow",
    },

    { type: "divider", key: "divider2" },

    {
      label: <Tooltip title={"Add filters to dashboard"}>Filters</Tooltip>,
      key: "parameters",
    },
    ...(selectedItems.length > 1
      ? [
        {
          label: (
            <Tooltip title={"Add tagged group items to new grid item"}>
              Group
            </Tooltip>
          ),
          key: "grouping",
          // tooltip: "Add tagged group items to new grid item",
        },
      ]
      : []),
    { type: "divider", key: "divider5" },
    {
      label: "Add",
      key: "add",
      children: [
        {
          label: <Tooltip title={"Add image to dashboard"}>Image</Tooltip>,
          key: "image",
        },
        {
          label: <Tooltip title={"Add text to dashboard"}>Text</Tooltip>,
          key: "text",
        },
        {
          label: <Tooltip title={"Add tabs to dashboard"}>Tab</Tooltip>,
          key: "tab",
        },
        {
          label: <Tooltip title={"Add HTML component to dashboard"}>HTML</Tooltip>,
          key: "html-component",
        },
        {
          label: <Tooltip title={"Add dropdown component to dashboard"}>Dropdown</Tooltip>,
          key: "select-dropdown",
        },
      ],
    },
    { type: "divider", key: "divider3" },
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
  ];

  const errorHandlingLiquidJs = ({ value = "<p></p>" }) => {
    const engine = new Liquid();
    let result;
    try {
      value = wrapSpecialVariables(value) // 7202 fix
      let scope = getDashboardVariableConfig(globalVariables, dispatch); // [7720] fix
      result = engine.parseAndRenderSync(value, scope);
    } catch (e) {
      Notify.error({ message: e.message, type: "Frontend" });
    }
    return result;
  };

  const headerTitle = useMemo(
    () => errorHandlingLiquidJs({ value: headerValues?.title }),
    [headerValues, dashboardVariables, applyDashboardFilters, refreshDashboard]
  );

  const headerLink = useMemo(
    () => errorHandlingLiquidJs({ value: headerValues?.link }),
    [headerValues, dashboardVariables, applyDashboardFilters]
  );
  const html = useMemo(
    () =>
      errorHandlingLiquidJs({
        value: htmlValues?.value,
      }),
    [htmlValues, dashboardVariables, applyDashboardFilters]
  );
  const js = useMemo(
    () =>
      errorHandlingLiquidJs({
        value: jsValues?.value,
      }),
    [jsValues, dashboardVariables, applyDashboardFilters]
  );
  const cssData = useMemo(
    () =>
      errorHandlingLiquidJs({
        value: cssValues?.value,
      }),
    [cssValues, dashboardVariables, applyDashboardFilters]
  );


  const settingsObj = {
    key: "settings",
    label: "Settings",
  };

  group.push(settingsObj);

  const handleMenuClick = (key) => {
    switch (key) {
      case "image":
        addItem({ dispatch, compType: "image" });
        break;
      case "text":
        addItem({ dispatch, compType: "text" });
        break;
      case "tab":
        addItem({ dispatch, compType: "tab" });
        break;
      case "settings":
        dispatch(updateGroupId("header"));
        dispatch(toggleDashboardDrawer(true));
        break;
      case "grouping":
        groupTheGridItems();
        break;
      case "html-component":
        addItem({ dispatch, compType: "html-component" });
        break;
      case "select-dropdown":
        addItem({ dispatch, compType: "select-dropdown" });
        break;
      default:
        dispatch(updateGroupId(key));
        dispatch(toggleDashboardDrawer(true));
        return null;
    }
  };

  const menu = (
    <Menu
      onClick={({ key }) => {
        handleMenuClick(key);
      }}
      selectable={false}
      items={group}
    />
  );
  const props = {
    resizeHandles: ["se"],
    containerPadding: [gridValues.containerMarginHorizontal, gridValues.containerMarginVertical],
    breakpoints: breakpointsValues,
    cols: colsValues,
    compactType: gridValues.compactType,
    rowHeight: gridValues.rowHeight,
    isDroppable: !previewMode && !isOpenMode && gridValues.isDroppable,
    preventCollision: gridValues.preventCollision,
    measureBeforeMount: gridValues.measureBeforeMount,
    isDraggable: !previewMode && !isOpenMode && gridValues.isDraggable,
    isResizable: !isOpenMode && gridValues.isResizable,
    margin: [gridValues.horizontalMargin, gridValues.verticalMargin],
    useCSSTransforms: false,
    allowOverlap: gridValues.allowOverlap,
  };
  // const cssObj = css.parse(cssData, {
  //   silent: true,
  // });
  // const sheet = cssObj.stylesheet;
  // sheet.rules.map((rule) => {
  //   rule.selectors = rule.selectors.map((item) => `#hi-dashboard ${item}`);
  //   return rule;
  // });
  // const stringifiedCSS = cssData;

  const prevJsValues = useRef();
  const prevHtmlValues = useRef();
  const prevCssValues = useRef();

  const addRawData = () => {
    document.getElementById("hi-dashboard-html").innerHTML = htmlValues?.enable
      ? html
      : "";

    var style = document.createElement("style");
    style.setAttribute("id", "hi-dashboard-css");
    style.innerHTML = cssValues?.enable ? cssData : "";
    var parent = document.getElementById("hi-grid");


    if (cssValues?.enable) {
      style.innerHTML = cssData;
      parent.appendChild(style);
    }

    if (!cssValues?.enable) {
      document.getElementById(`hi-dashboard-css`)?.remove();
    }

    if (jsValues?.enable) {
      let scope = getDashboardVariableScope(globalVariables, dispatch)
      const tabProperties = getTabProperties(gridItemsData);
      const textProperties = getTextProperties(gridItemsData);
      const imageProperties = getImageProperties(gridItemsData);

      executeScript({ ...scope, properties: { tab: tabProperties, text: textProperties, image: imageProperties } }, js, dispatch);
      for (let properties of [tabProperties, textProperties, imageProperties]) {
        for (let [itemId, props] of Object.entries(properties)) {
          const updations = [];
          for (let [key, value] of Object.entries(props)) {
            if (value === null) continue;
            updations.push({ key, value, groupId: "edit" });
          }
          dispatch(updateGridItemStyles({ itemsData: updations, gridItemId: itemId }));
        }
      }
      //     var script = document.createElement("script");
      //     script.setAttribute("id", "hi-dashboard-js");
      //     script.innerHTML = jsValues?.enable
      //       ? `try{
      //   (function(){
      //   ${js}
      //   })();
      // }
      // catch(e){
      // }
      //   `
      //       : "";
      //     parent.appendChild(script);
    }

    if (!jsValues?.enable) {
      document.getElementById(`hi-dashboard-js`)?.remove();
    }


  };

  const handleApplyDropdownScript = (gridItemsData, parameters) => {
    let dropdownValues = parameters?.selected_dropdown_values || null;
    if (dropdownValues && !isEmpty(dropdownValues)) {
      for (const [key, value] of Object.entries(dropdownValues)) {
        const gridItemStylesData = getGridItem(gridItemsData, key)?.gridItemConfig;
        if (gridItemStylesData?.length) {
          const editValues = gridItemStylesData?.find(
            (item) => item.key === "edit"
          )?.values;
          const { jsValue = '' } = editValues || {};
          if (jsValue?.length) {
            let scope = getDashboardVariableScope(globalVariables, dispatch);
            const dropdownApis = getDropDownApis(gridItemsData, key, dispatch, false);
            scope = { value, ...scope, ...scope.getAllVariables(), ...dropdownApis };
            dispatch(updateDashboardVariables({ key: [key], value }));
            executeScript({ ...scope }, jsValue, dispatch);
          }
        }
      }
    }
  }

  const [highlight, setHighlight] = useState("");
  const itemAddedStatus = useSelector(
    (state) => state.designer.present.itemAddedStatus
  );
  useEffect(() => {
    if (!isOpenMode) {
      setHighlight("highlight");
    }

    const timer = setTimeout(() => {
      setHighlight("");
    }, 3000);

    return () => {
      clearTimeout(timer);
    };
  }, [itemAddedStatus]);

  useEffect(() => {
    const isJsValuesChanged = !isEqual(js, prevJsValues.current);
    const isHtmlValuesChanged = !isEqual(html, prevHtmlValues.current);
    const isCssValuesChanged = !isEqual(cssData, prevCssValues.current);

    if (isHtmlValuesChanged || isCssValuesChanged || isJsValuesChanged) { // [7230, 7231]
      addRawData();
    }

    prevJsValues.current = js;
    prevHtmlValues.current = html;
    prevCssValues.current = cssData;
  }, [html, js, cssData, dashboardVariables, applyDashboardFilters]);

  useEffect(() => {
    if (gridItemsData?.length && !isEmpty(parameters)) {
      handleApplyDropdownScript(gridItemsData, parameters);
    }
  }, [dashboardVariables])

  const groupTheGridItems = () => {
    if (selectedItems.length > 1) {
      addItem({
        selectedItems,
        dispatch,
        compType: "grouped-component",
      });
      selectedItems.forEach((item) => dispatch(removeGridItem(item.id)));
    }
  };

  const renderGridItems = React.useCallback(
    (array) => {
      return array?.map((ele, index) => {
        const [shadowStyles, borderStyles] = getDashboardConfig(
          gridItemsData,
          ele.id,
          ["shadow", "border"]
        );
        let filterCompStyles = { overflow: 'visible' };
        let gridItemStyles = { ...borderStyles, ...shadowStyles };
        if (['filter-component'].includes(ele.compType)) {
          gridItemStyles = { ...gridItemStyles, ...filterCompStyles };
        }

        const gridItemClassName =
          highlight && index + 1 === gridItemsData.length
            ? `grid-item ${ele.isSaved ? "" : "hi-outline-hover"} ${highlight}`
            : `grid-item ${ele.isSaved ? "" : "hi-outline-hover"}`;
        const initialPosition = layout.find(item => item.i === ele.id) ? layout.find(item => item.i === ele.id) : ele.initialPosition;
        if (ele.compType === "filter-component") {
          const { gridItemConfig = [] } = ele || {};
          const freeFloatFilterSettings = gridItemConfig?.find((item) => item.key === freeFloatFilterKeyName) ?? freeFloatFilterDefaultSettings;
          if (freeFloatFilterSettings?.values?.['bringToFront']) {
            gridItemStyles = { ...gridItemStyles, zIndex: 99 + (index + 1) }
          } else {
            gridItemStyles = { ...gridItemStyles, zIndex: 0 }
          }
        }
        return (
          <div
            className={gridItemClassName}
            data-grid={initialPosition}
            data-testid={ele.id}
            key={ele.id}
            style={
              ele.compType === "text" || ele.compType === "image" || ele.compType === "html-component"
                ? { overflow: "hidden", ...gridItemStyles }
                : gridItemStyles
            }
            id={`${ele.id}`}
          >
            <div
              onContextMenu={(e) => {
                dispatch(updateGridItemId(ele.id));
                e.stopPropagation();
              }}
              className="hi-grid-item"
            >
              <SizeAwareHIGridItem
                groupId={groupId}
                key={ele.id}
                element={ele}
                parameters={parameters}
              ></SizeAwareHIGridItem>
            </div>
          </div>
        );
      });
    },
    [
      gridItemsData,
      groupId,
      highlight,
      dashboardVariables,
      applyDashboardFilters,
    ]
  );

  const children = React.useMemo(() => {
    return maximizingStatus
      ? renderGridItems([maximizedGridItem])
      : renderGridItems(gridItemsData);
  }, [
    renderGridItems,
    maximizingStatus,
    gridItemsData,
    maximizedGridItem,
    dashboardVariables,
    applyDashboardFilters,
  ]);

  return (
    <div id="hi-dashboard" className="hi-dashboard-designer">
      <Dropdown
        disabled={isOpenMode || previewMode}
        overlay={menu}
        trigger={["contextMenu"]}
      >
        <div style={gridStyles} id="hi-grid" className="hi-grid">
          <TutorialInfo elementKey="hi-designer-canvas">
            <div></div>
          </TutorialInfo>
          <div id="hi-dashboard-html"></div>
          <header className="hi-dashboard-header">
            {headerValues.enable ? (
              <a
                className={
                  headerValues.link !== "" ? "" : "hi-header-anchor-disabled"
                }
                target="_blank"
                rel="noreferrer"
                href={headerLink}
              >
                <div
                  dangerouslySetInnerHTML={{
                    __html: headerTitle,
                  }}
                  className="ql-editor"
                  style={{ width: "100%", ...headerStyles }}
                ></div>
              </a>
            ) : null}
          </header>
          {/* <div className={`dashboard-designer-content ${(pathname || '').includes(routesUrl.reportViewUrl) ? 'report-view-height' : 'dashboard-designer-height'}`}>  bug 5885 */}
          <section data-testid="hi-dd-content" style={backgroundStyles} className="hi-parameter-container">
            <ResponsiveGridLayout
              onLayoutChange={(layout) => {
                dispatch(updateDesignerLayout(layout))
              }}
              onDragStop={(layout) => {
                if (!maximizingStatus && !isOpenMode && !previewMode) {
                  dispatch(updateGridLayout(layout));
                }
              }}
              onResizeStop={(layout) => {
                if (!maximizingStatus && !isOpenMode && !previewMode) {
                  dispatch(updateGridLayout(layout));
                }
              }}
              onDrop={(layout) => {
                if (!maximizingStatus && !isOpenMode && !previewMode) {
                  dispatch(updateGridLayout(layout));
                }
              }}
              layouts={{
                lg: maximizingStatus ? maxLayout : layout,
                md: maximizingStatus ? maxLayout : layout,
                sm: maximizingStatus ? maxLayout : layout,
                xs: maximizingStatus ? maxLayout : layout,
                xxs: maximizingStatus ? maxLayout : layout,
              }}
              onBreakpointChange={(breakpoint) => {
                setCurrentBreakpoint(breakpoint);
              }}
              // className="hi-react-grid"
              className="layout"
              {...props}
            >
              {children}
            </ResponsiveGridLayout>
            <HIReportParametersDrawer />
          </section>
        </div>
        {/* </div> */}
      </Dropdown >
      {!isOpenMode && <HIDashboardDrawer setRefresh={setRefresh} />}
      {!isOpenMode && <HIGridItemDrawer setRefresh={setRefresh} />}
    </div >
  );
};
export { HIDashboardDesigner };
