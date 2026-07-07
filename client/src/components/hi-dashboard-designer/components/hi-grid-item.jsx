import "../index.scss";
import { useEffect, useMemo, useRef, useState } from "react";
import { Dropdown, Menu, Layout, Tooltip, Button, Progress, Modal } from "antd";
import { useSelector } from "react-redux";
import { getDashboardConfig } from "../utils/get-dashboard-config";
import {
  PushpinOutlined,
  GroupOutlined,
  UngroupOutlined,
  DeleteOutlined,
  FilterOutlined,
  MoreOutlined,
  ArrowsAltOutlined,
  ShrinkOutlined,
  EditOutlined,
  ExportOutlined,
  FileExcelOutlined,
  MenuOutlined,
  RetweetOutlined,
  FilePdfOutlined
} from "@ant-design/icons";
import { useDispatch } from "react-redux";
import {
  changeIsDraggableInGridItem,
  changeIsSelectedInGridItem,
  updateNestedGridLayout,
  updateGridItemId,
  unGroupGridItem,
  deleteGridItem,
  maximiseGridItem,
  toggleGridItemDrawer,
  updateGroupId,
  updateTabGridItemsLayout,
  updateGridItemStyles,
  storeFilterItemsToGridItemsData,
  updateParameterDrawerStatus,
  applyDashboardFilters as applyDashboardFiltersAction,
  openCompactFbBrower,
  replaceReportId,
} from "../../../redux/actions/dashboard-designer.actions";
import { Responsive } from "react-grid-layout";
import { withSize } from "react-sizeme";
import {
  getGridItem,
  getGridItemLayoutObject,
} from "../utils/recursive-functions";
import { exportPrintedReport, getDashboardVariableConfig, getFieldDisplayName } from "../../../utils/utilities";
import { HIDashboard } from "../../hi-dashboard";
import {
  addItem,
  getDashboardVariableScope,
  gridItemsGroup,
  wrapSpecialVariables,
} from "../utils/common-functions";
import { setDashboardVariables, updateDashboardVariables } from "../../../redux/actions";
import { contextMenuEditOptions } from "../../hi-fileBrowser/constants";
import { appActions } from "../../../redux/actions";
import HIIcon from "../../../components/common/icons/hi-icons";
import { exportReport, getGlobalVariables } from "../../../utils/utilities";
import { Liquid } from "liquidjs";
import notify from "../../hi-notifications/notify";
import { removeAllReports } from "../../../redux/actions/hreport.actions";
import { getMinimizedGridItemOptions } from "../utils/hi-grid-item-functions";
import { executeScript } from "../../hi-reports/utils/utilities";
import { isEqual } from "lodash";
import TabComponent from "./tab-component";
import DropdownComponent from "./dropdown-component";
import SortableTable from "./hi-parameter-drawer/hi-sortable-table";
import { HIDashboardWrapper } from "../../hi-dashboard/components/hi-dashboard-wrapper";
import { SortableHandle } from "react-sortable-hoc";
import { freeFloatFilterDefaultSettings, freeFloatFilterKeyName } from "../utils/constants";
import { uuid } from "../../../utils/uuid";
import useExportOptions from "../../../hooks/useExportOptions";

const { Header, Content } = Layout;
const withSizeHOC = withSize({ monitorHeight: true });
const ResponsiveGridLayout = withSizeHOC(Responsive);

let HIGridItem = (props) => {
  const { element, groupId, size, childElement, parameters } = props;
  const {
    id,
    isGrouped,
    children,
    layout,
    filters,
    listeners,
    reportInfo,
    parameter,
    reportId,
    compType,
  } = element;

  const hiAxiosRef = useRef(null);
  const { getDropdownOptions } = useExportOptions();

  let [downloadingInfo, setDownloadingInfo] = useState(null);

  const gridItemsData = useSelector(
    (state) => state.designer.present.gridItemsData
  );
  const filterCounter = useSelector(
    (state) => state.designer.present.filterCounter
  );
  const designerMode = useSelector(
    (state) => state.designer.present.designerMode
  );
  const refreshDashboard = useSelector((state) => state.dashboard.present.refreshDashboard); // added for 7720 
  const isOpenMode = ["open"].includes(designerMode);
  const dispatch = useDispatch();
  const previewMode = useSelector(
    (state) => state.designer.present.previewMode
  );
  const filterItemsData = useSelector((state) =>
    state.designer.present.gridItemsData.filter(
      (item) =>
        item.compType === "filter" &&
        item.filterCompId === id
    )
  );
  const variables = useSelector(
    (state) => state.dashboard.present.dashboardVariables
  );
  const gridItemStylesData = getGridItem(gridItemsData, id)?.gridItemConfig;
  const [backgroundStyles] = getDashboardConfig(gridItemsData, id, [
    "background",
  ]);
  const gridItemStyles = { ...backgroundStyles };
  const headerValues = gridItemStylesData?.find(
    (item) => item.key === "header"
  ).values;
  const htmlValues = gridItemStylesData?.find(
    (item) => item.key === "html"
  ).values;
  const cssValues = gridItemStylesData?.find(
    (item) => item.key === "css"
  ).values;
  const jsValues = gridItemStylesData?.find(
    (item) => item.key === "javascript"
  ).values;
  const settingsValues = gridItemStylesData?.find(
    (item) => item.key === "griditemsettings"
  )?.values;
  const editValues = gridItemStylesData?.find(
    (item) => item.key === "edit"
  )?.values;
  const groupedComponentGridValues = gridItemStylesData?.find(
    (item) => item.key === "grid"
  )?.values;
  const groupedComponentColsValues = gridItemStylesData?.find(
    (item) => item.key === "columns"
  )?.values?.reduce((acc, item) => {
    acc[item.key] = item.value || 2;
    return acc;
  }, {});
  const groupedComponentBreakpointsValue = gridItemStylesData?.find(
    (item) => item.key === "breakpoints"
  )?.values?.reduce((acc, item) => {
    acc[item.key] = item.value || 2;
    return acc;
  }, {});

  const headerStyles = headerValues?.enable
    ? {
      backgroundColor: `rgba(${headerValues?.backgroundColor?.r},${headerValues?.backgroundColor?.g},${headerValues?.backgroundColor?.b},${headerValues?.backgroundColor?.a})`,
      textAlign: headerValues?.position,
    }
    : null;
  const maximizingStatus = useSelector(
    (state) => state.designer.present.maximizingStatus
  );
  const dashboardLayout = useSelector((state) => state.designer.present.layout);

  const gridItemlayoutObj = getGridItemLayoutObject({
    data: gridItemsData,
    id,
    layout: dashboardLayout,
  });
  const gridSettingsData = useSelector(
    (state) => state.designer.present.gridSettings
  );

  const columns = [
    {
      title: "Filter",
      dataIndex: "index",
      width: 80,
      className: "drag-visible",
      render: (_, record) => (
        <div style={{ marginLeft: 18 }}>
          <HIDashboardWrapper
            type={record.reportInfo?.extension}
            parameter={record.parameter}
            id={record.id}
            listeners={record.listeners}
            reportInfo={record.reportInfo}
            dashboardVariables={variables}
          />
        </div>
      ),
    },
  ];
  const [colsValues, breakpointsValues] = getDashboardConfig(
    gridSettingsData,
    null,
    ["columns", "breakpoints"]
  );
  const designerSettings = useSelector(
    (state) => state.designer.present.designerSettings
  );
  const parameterSettings = designerSettings?.find(
    (item) => item.key === "parameters"
  )?.values;
  let dashboardVariables = useSelector(
    (state) => state.dashboard.present.dashboardVariables
  );
  const applyDashboardFilters = useSelector(
    (state) => state.designer.present.applyDashboardFilters
  );
  const gridValues =
    gridSettingsData?.find((item) => item.key === "grid")?.values || {};
  dashboardVariables = parameterSettings?.enableApplyButton
    ? null
    : dashboardVariables;
  const globalVariables = getGlobalVariables({
    dispatch,
    level: "reportLevel",
    id,
  });

  const Notify = notify(dispatch);

  const [imageStyles] = getDashboardConfig(gridItemsData, id, ["image"]);

  const shouldShowGridItemsToolBar = () => {
    if (
      isOpenMode
      && (compType === "text" || compType === "image")) {
      return false;
    }

    return true;
  }

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
  const htmlData = useMemo(
    () => errorHandlingLiquidJs({ value: htmlValues?.value }),
    [htmlValues, dashboardVariables, applyDashboardFilters]
  );
  const cssData = useMemo(
    () => errorHandlingLiquidJs({ value: cssValues?.value }),
    [cssValues, dashboardVariables, applyDashboardFilters]
  );
  const jsData = useMemo(
    () => errorHandlingLiquidJs({ value: jsValues?.value }),
    [jsValues, dashboardVariables, applyDashboardFilters]
  );
  const textCompText = useMemo(
    () => errorHandlingLiquidJs({ value: editValues?.text }),
    [editValues, dashboardVariables, applyDashboardFilters]
  );
  const textCompLink = useMemo(
    () => errorHandlingLiquidJs({ value: editValues?.link }),
    [editValues, dashboardVariables, applyDashboardFilters]
  );
  const imgCompSource = useMemo(
    () => errorHandlingLiquidJs({ value: editValues?.url?.toString() }),
    [editValues, dashboardVariables, applyDashboardFilters]
  );

  const addRawData = () => {
    // const cssObj = css.parse(cssValues ? cssValues?.value : "", {
    //   silent: true,
    // });
    // const sheet = cssObj.stylesheet;
    // sheet.rules.map((rule) => {
    //   rule.selectors = rule.selectors.map((item) => `${item}`);
    //   return rule;
    // });
    // const stringifiedCSS = !cssObj.stylesheet.parsingErrors.length
    //   ? css.stringify(cssObj)
    //   : null;
    if (compType !== "html-component" || id === "hi-grid") {
      document.getElementById(`html-${id}`).innerHTML = htmlValues?.enable
        ? htmlData
        : "";
    }

    var style = document.createElement("style");
    style.setAttribute("id", `hi-grid-item-css-${id}`);
    var parent = document.getElementById(`${id}`);

    if (cssValues?.enable) {
      style.innerHTML = cssData;
      parent.appendChild(style);
    }
    if (!cssValues?.enable) {
      document.getElementById(`hi-grid-item-css-${id}`)?.remove();
    }
    if (jsValues?.enable && compType !== "html-component") {
      let scope = {}
      let properties = { [compType]: Object.seal({ ...editValues }) }
      executeScript({ ...scope, properties }, jsData, dispatch);
      if (["tab", "image", "text"].includes(compType)) {
        const updations = [];
        for (let [key, value] of Object.entries(properties[compType])) {
          if (value === null) continue;
          updations.push({ key, value, groupId: "edit" });
        }
        dispatch(updateGridItemStyles({ itemsData: updations, gridItemId: id }));
      }
    }
    // if (jsValues?.enable) {
    //   var script = document.createElement("script");
    //   script.setAttribute("id", `hi-grid-item-js-${id}`);
    //   script.innerHTML = jsValues?.enable
    //     ? `try{
    //     (function(){
    //     ${jsData}
    //     })();
    //   }
    //   catch(e){

    //   }
    //     `
    //     : "";
    //   parent.appendChild(script);

    // }

    // if (!jsValues?.enable) {
    //   document.getElementById(`hi-grid-item-js-${id}`)?.remove();
    // }
  };

  const filterMenuClick = (item) => {
    dispatch(
      setDashboardVariables({
        parameter: getFieldDisplayName(item),
        value: item.values,
      })
    );

    let filterCompId;
    if (parameterSettings?.floatingFilter) {
      filterCompId = addItem({
        dispatch,
        reportInfo: { ...reportInfo, mode: "filter" },
        parameter: {
          dashboardFilter: {
            columnName: getFieldDisplayName(item),
            uid: item.uid,
          },
        },
        compType: "filter-component",
        index: filterCounter,
        key: (filterCounter + 1).toString(),
      });
    }
    addItem({
      dispatch,
      reportInfo: { ...reportInfo, mode: "filter" },
      parameter: {
        dashboardFilter: {
          columnName: getFieldDisplayName(item),
          uid: item.uid,
        },
      },
      compType: "filter",
      index: filterCounter,
      key: (filterCounter + 1).toString(),
      filterCompId,
    });
  };

  const filtersGridItemsConfig =
    filters?.length > 0
      ? [
        ...filters?.map((item) => ({
          key: getFieldDisplayName(item),
          label: (
            <Tooltip placement="right" title={getFieldDisplayName(item)}>
              {getFieldDisplayName(item)}
            </Tooltip>
          ),
          onClick: () => {
            filterMenuClick(item);
          },
        })),
      ]
      : [{ key: "no-filters", label: "No Filters" }];

  const freeFloatFilterSettings = gridItemStylesData?.find((item) => item.key === freeFloatFilterKeyName) ?? freeFloatFilterDefaultSettings;

  const gridItems = gridItemsGroup({
    id,
    dispatch,
    filtersGridItemsConfig,
    compType,
    gridItemsData,
    isTabChild: Boolean(props.isTabChild),
    layout: gridItemlayoutObj,
    freeFloatFilterSettings
  });

  const handleMenuClick = ({ key }) => {
    if (compType === "html-component" && key === "edit") {
      dispatch(updateGroupId("html"));
      dispatch(toggleGridItemDrawer(true));
      return;
    }
    switch (key) {
      case "export":
      case "maximize":
      case "editReport":
        dispatch(updateGroupId("griditemsettings"));
        dispatch(toggleGridItemDrawer(true));
        break;
      case "background":
      case "border":
      case "header":
      case "shadow":
      case "css":
      case "html":
      case "javascript":
      case "alignment":
      case "grid":
      case "columns":
      case "breakpoints":
      case "edit":
        dispatch(updateGroupId(key));
        dispatch(toggleGridItemDrawer(true));
        break;
      case "bringToFront":
      case "sendToBack":
      case "transparent":
        dispatch(updateGroupId(key));
        break;
      default:
        break;
    }
  };

  const gridItemsMenu = (
    <Menu
      onClick={({ key }) => {
        handleMenuClick({ key });
      }}
      selectable={false}
      items={gridItems}
    />
  );

  const downloadingInfoModal = (
    <Modal
      visible={!!downloadingInfo}
      className="hi-hr-download-modal"
      title="Downloading"
      okText={() => null}
      cancelText="Cancel"
      maskClosable={false}
      // onOk={() => {
      //   remove(warningVisible.deleteKey); // test
      //   setWarningVisible({ visible: false, deleteKey: "" });
      // }}
      onCancel={() => {
        hiAxiosRef.current.abort();
        setDownloadingInfo(null);
      }}
    >
      <div>
        <Progress percent={downloadingInfo?.progress} />
        <div>Please wait while downloading</div>
      </div>
    </Modal>
  )

  const handleExport = (e, format) => {
    hiAxiosRef.current = exportReport({ id: reportId, format, callback: setDownloadingInfo }, dispatch);
  };

  const handlePrintExport = (e, format) => {
    let changedFilterValues = {};
    let file = reportInfo.file;
    if (reportId) {
      dispatch((dispatch, getState) => {
        let activeReport = getState().hreport.present.reports.find(
          (item) => item.id === reportId
        );
        if (activeReport.activeDrillthroughId) {
          activeReport = getState().hreport.present.reports.find(
            (item) => item.id === activeReport.activeDrillthroughId
          );
          let { reportInfo } = activeReport;
          let { location, uuid, reportName } = reportInfo;
          file = {
            path: location + "/" + uuid,
            name: uuid,
            title: reportName,
          };
        }
        let { filters } = activeReport;
        filters.map((filter) => {
          changedFilterValues[getFieldDisplayName(filter)] = filter.values;
        });
      });
    }
    hiAxiosRef.current = exportPrintedReport(
      {
        file,
        parameters: { ...props.parameters, ...changedFilterValues },
        format,
        callback: setDownloadingInfo,
      },
      dispatch
    );
  };


  const previewModeItems = [
    ...(settingsValues?.maximize
      ? [
        ...(maximizingStatus
          ? [
            {
              label: "Minimize",
              icon: <ShrinkOutlined />,
              key: "minimize",
              className: "dashboard-sub-menu-export-item",
              onClick: () => {
                dispatch(
                  maximiseGridItem({ data: element, status: false })
                );
              },
            },
          ]
          : [
            {
              label: "Maximize",
              icon: <ArrowsAltOutlined />,
              key: "maximize",
              className: "dashboard-sub-menu-export-item",
              onClick: () => {
                dispatch(maximiseGridItem({ data: element, status: true }));
              },
            },
          ]),
      ]
      : []),
    ...(settingsValues?.edit
      ? [
        {
          label: "Edit",
          icon: <EditOutlined />,
          key: "edit",
          className: "dashboard-sub-menu-export-item",
          onClick: () => {
            const extension = reportInfo?.file.name.split(".")[1];
            if (extension === "hr") dispatch(removeAllReports());
            dispatch(
              appActions.setEditModeInfo({
                dir: reportInfo?.file?.path,
                file: reportInfo?.file?.name,
                extension,
              })
            );

            const routeToUrl = contextMenuEditOptions.find(
              (e) => e.extension === extension
            );
            // console.log("directory", directory, extension, "extension");
            if (routeToUrl)
              dispatch(appActions.updateRoute(routeToUrl.routeTo));
          },
        },
      ]
      : []),
    ...(settingsValues?.export
      ? [
        {
          label: "Export",
          icon: <ExportOutlined />,
          key: "export",
          className: "dashboard-sub-menu-export-item",
          children: getDropdownOptions("designer", (format) => format === "pdf" ? handlePrintExport(null, format) : handleExport(null, format)),
        },
      ]
      : []),
  ];

  const openModeOptions =
    reportId &&
      settingsValues &&
      (settingsValues.export || settingsValues.maximize || settingsValues.edit)
      ? [
        {
          icon: (
            <Tooltip title="Settings">
              <MoreOutlined />
            </Tooltip>
          ),
          children: previewModeItems,
          key: "griditemsettings",
        },
      ]
      : [];

  const toolbarOptions = [
    ...(!isOpenMode
      ? [
        {
          key: "pintodashboard",
          tooltip: "Pin to dashboard",
          icon: (
            <Tooltip title="Pin to dashboard">
              <PushpinOutlined
                className={
                  ((props.isTabChild && props.tabChildLayout?.static) || (!props.isTabChild && gridItemlayoutObj?.static))
                    ? "hi-icon hi-selected"
                    : "hi-icon"
                }
              />
            </Tooltip>
          ),

          onClick: () => {
            dispatch(changeIsDraggableInGridItem(id));
            if (props?.isTabChild) {
              dispatch(updateTabGridItemsLayout({ isPinAction: true, layoutID: id }))
            }
          },
        },
        ...(childElement || compType === "tab" || Boolean(props.isTabChild)
          ? []
          : [
            {
              key: "itemgrouping",
              // iconTooltip: "Tag this item for grouping",
              icon: (
                <Tooltip title="Tag this item for grouping">
                  <GroupOutlined
                    className={
                      isGrouped ? "hi-icon hi-selected" : "hi-icon"
                    }
                  />
                </Tooltip>
              ),
              onClick: () => {
                dispatch(changeIsSelectedInGridItem(id));
              },
            },
          ]),
        ...(children?.length > 0
          ? [
            {
              key: "itemungrouping",
              iconTooltip:
                "Ungroup report items and place them in dashboard",
              icon: (
                <Tooltip title="Ungroup report items and place them in dashboard">
                  <UngroupOutlined />
                </Tooltip>
              ),
              onClick: () => {
                dispatch(unGroupGridItem(id));
              },
            },
          ]
          : []),
        ...(reportInfo &&
          reportInfo?.mode !== "filter" &&
          reportInfo?.extension === "hr"
          ? [
            {
              key: "filters",
              // tooltip: "Filters",

              icon: (
                <Tooltip title="Add filters">
                  <FilterOutlined />
                </Tooltip>
              ),
              children: filtersGridItemsConfig,
            },
          ]
          : []),
        {
          key: "remove",
          // iconTooltip: "Delete grid item",
          icon: (
            <Tooltip title="Delete grid item">
              <DeleteOutlined />
            </Tooltip>
          ),

          onClick: () => {
            dispatch(deleteGridItem(id));
            if (props?.isTabChild) {
              dispatch(updateTabGridItemsLayout({ deleteLayout: true, layoutID: id }))
            }
          },
        },
        ...(compType === "dashboard-designer-component" ? [
          {
            key: "changethereport",
            icon: (
              <Tooltip title="Change the report">
                <RetweetOutlined />
              </Tooltip>
            ),

            onClick: () => {
              dispatch(openCompactFbBrower(true));
              dispatch(replaceReportId(id))
            },
          },
        ] : [])
      ]
      : []),
    ...openModeOptions,
  ];


  const minimizedGridItemOptions = getMinimizedGridItemOptions({ isOpenMode, id, dispatch, gridItemlayoutObj, childElement, isGrouped, children, openModeOptions, filtersGridItemsConfig, reportInfo, isTabChild: props.isTabChild, compType })

  const gridItemToolbar = (
    <Menu
      mode="vertical"
      multiple={size.height < 115}
      items={size.height > 115 ? toolbarOptions : minimizedGridItemOptions}
      selectable={size.height < 115}
      className={
        children?.length
          ? "hi-grid-item-options hi-nested-grid-item-options"
          : "hi-grid-item-options"
      }
    />
  );

  const openModeItems = [...openModeOptions];

  const openModeItemsToolbar = (
    <Menu
      mode="vertical"
      items={openModeItems}
      selectable={false}
      className="hi-grid-item-open-mode-options"
    />
  );
  const nestedGridProps = {
    resizeHandles: ["se"],
    containerPadding: [groupedComponentGridValues?.containerMarginHorizontal, groupedComponentGridValues?.containerMarginVertical],
    breakpoints: groupedComponentBreakpointsValue,
    cols: groupedComponentColsValues,
    compactType: groupedComponentGridValues?.compactType,
    rowHeight: groupedComponentGridValues?.rowHeight,
    isDroppable: !previewMode && !isOpenMode && groupedComponentGridValues?.isDroppable,
    preventCollision: groupedComponentGridValues?.preventCollision,
    measureBeforeMount: groupedComponentGridValues?.measureBeforeMount,
    isDraggable: !previewMode && !isOpenMode && groupedComponentGridValues?.isDraggable,
    isResizable: !isOpenMode && groupedComponentGridValues?.isResizable,
    margin: [groupedComponentGridValues?.horizontalMargin, groupedComponentGridValues?.verticalMargin],
    useCSSTransforms: false,
    allowOverlap: groupedComponentGridValues?.allowOverlap,
  };
  useEffect(() => {
    addRawData();
  }, [
    jsValues,
    htmlValues,
    cssValues,
    id,
    dashboardVariables,
    applyDashboardFilters,
  ]);

  useEffect(() => {
    const runner = () => {
      if (compType === "html-component" && jsValues?.enable) {
        let scope = getDashboardVariableScope(globalVariables, dispatch)
        executeScript({ ...scope }, jsData, dispatch)
      }
    };

    runner();
  }, [jsValues])


  const renderDashboardDesignerComponent = (
    <HIDashboard
      id={id}
      type={reportInfo?.extension}
      parameter={parameter}
      reportInfo={reportInfo}
      listeners={listeners}
    />
  );

  const groupedComponent = (
    <ResponsiveGridLayout
      // onLayoutChange={(layout) => {
      //   if (!isOpenMode && !previewMode) {
      //     dispatch(updateNestedGridLayout({ layout, id }));
      //   }
      // }}
      onDragStop={(layout) => {
        if (!isOpenMode && !previewMode) {
          dispatch(updateNestedGridLayout({ layout, id }));
        }
      }}
      onResizeStop={(layout) => {
        if (!isOpenMode && !previewMode) {
          dispatch(updateNestedGridLayout({ layout, id }));
        }
      }}
      layouts={{
        lg: layout,
        md: layout,
        sm: layout,
        xs: layout,
        xxs: layout
      }}
      width={size?.width ? size?.width : null}
      className="hi-nested-grid"
      {...nestedGridProps}
    >
      {children?.map((ele) => {
        const [shadowStyles, borderStyles] = getDashboardConfig(
          gridItemsData,
          ele.id,
          ["shadow", "border"]
        );
        const gridItemStyles = {
          ...borderStyles,
          ...shadowStyles,
        };
        return (
          <div
            className="grid-item"
            data-grid={ele.initialPosition}
            key={ele.id}
            id={`${ele.id}`}
            style={gridItemStyles}
          >
            <div
              onContextMenu={(e) => {
                dispatch(updateGridItemId(ele.id));
                e.stopPropagation();
              }}
              className="hi-grid-item"
            >
              <Dropdown
                disabled={isOpenMode || previewMode}
                overlay={gridItemsMenu}
                trigger={["contextMenu"]}
              >
                <HIGridItem
                  groupId={groupId}
                  gridItemsGroup={gridItemsGroup}
                  key={ele.id}
                  id={ele.id}
                  size={size}
                  element={ele}
                  childElement={true}
                />
              </Dropdown>
            </div>
          </div>
        );
      })}
    </ResponsiveGridLayout>
  );
  const textComponent = editValues?.enable ? (
    <div style={{ height: "100%" }} className="hi-text-grid-item">
      <a
        target="_blank"
        rel="noreferrer"
        href={textCompLink}
        className={editValues.link !== "" ? "" : "hi-header-anchor-disabled"}
        onClick={(e) => {
          if (!editValues.link) {
            e.preventDefault();
          }
        }}
      >
        <div
          // style={{ width: "100%", ...headerStyles }}
          dangerouslySetInnerHTML={{
            __html: textCompText,
          }}
          className="ql-editor"
        />
      </a>
    </div>
  ) : null;

  const imageComponent = editValues?.enable ? (
    <div style={{ height: "100%", width: "100%", ...imageStyles }}>
      {/* <img
        style={{ height: "100%", width: "100%", ...imageStyles }}
        src={imgCompSource}
        alt=""
      /> */}
    </div>
  ) : null;

  const tabComponent = editValues?.enable ? (
    <div style={{ height: "100%" }} className="hi-text-grid-item">
      <TabComponent
        gridItem={gridItemsData.find(item => item.id === id)}
        gridItemsData={gridItemsData}
        nestedGridProps={nestedGridProps}
        key={editValues?.activeTab}
        {...props}
      />
    </div>
  ) : null;

  const dropdownComponent = editValues?.enable ? (
    <div style={{ height: "100%" }} className="hi-text-grid-item">
      <DropdownComponent parameters={parameters} editValues={editValues} gridItem={gridItemsData.find(item => item.id === id)} />
    </div>
  ) : null;
  const updateData = (data) => {
    dispatch(storeFilterItemsToGridItemsData(data));
  };

  const uniqueClass = useMemo(() => `free-filter-comp-${uuid(true)}`, []);
  const shouldFilterTransparent = freeFloatFilterSettings.values['transparent'];
  const filterComponent = (
    <div style={{ height: "100%" }} className={`hi-text-grid-item ${uniqueClass}`}>
      {
        shouldFilterTransparent &&
        <style>
          {`
              .${uniqueClass} .ant-table {
                background: transparent !important;
                .ant-table-cell-row-hover {
                  background: transparent !important;
                }
              }
            `}
        </style>
      }

      <SortableTable
        dataSource={filterItemsData}
        columns={columns}
        onSortEnd={(data) => {
          updateData(data);
        }}
      />
    </div>
  );

  const applyReset = (
    <div style={{ height: "100%" }} className="hi-text-grid-item">
      <div>
        {parameterSettings?.enableApplyButton && (
          <Button
            size="small"
            style={{ marginLeft: 25 }}
            type="primary"
            onClick={() => {
              dispatch(
                applyDashboardFiltersAction(new Date().getTime().toString())
              );
              if (parameterSettings?.closeOnApply) {
                dispatch(updateParameterDrawerStatus(false));
              }
            }}
          >
            Apply
          </Button>
        )}
        <Button
          size="small"
          style={{ marginLeft: 25, marginTop: 10 }}
          onClick={() => {
            // Reset logic
            dispatch((dispatch, getState) => {
              const baseStateReports =
                getState().hreport.present.baseStateReports;

              for (let baseReport of baseStateReports) {
                if (baseReport.mode !== "filter") continue;
                dispatch(
                  updateDashboardVariables({
                    key: baseReport.dashboardFilter.columnName,
                    value: baseReport.filters.find(
                      (filter) => filter.uid === baseReport.dashboardFilter.uid
                    )?.values,
                  })
                );
              }
            });
            dispatch(
              applyDashboardFiltersAction(new Date().getTime().toString())
            );
          }}
        >
          Reset
        </Button>
      </div>
    </div>
  );

  const renderGridItem = () => {
    switch (compType) {
      case "dashboard-designer-component":
        return renderDashboardDesignerComponent;
      case "grouped-component":
        return groupedComponent;
      case "text":
        return textComponent;
      case "image":
        return imageComponent;
      case "tab":
        return tabComponent;
      case "html-component":
        return (
          <div dangerouslySetInnerHTML={{ __html: htmlValues.enable ? htmlData : "" }}>
          </div>
        );
      case "select-dropdown":
        return dropdownComponent;
      case "filter-component":
        return filterComponent;
      case "apply-reset":
        return applyReset;
      default:
        return null;
    }
  };

  const renderGridItemHeader = () => {
    return <Header
      style={{ width: "100%", ...headerStyles }}
      className="hi-grid-item-header"
    >
      <a
        target="_blank"
        rel="noreferrer"
        className={
          headerValues.link !== "" ? "" : "hi-header-anchor-disabled"
        }
        href={headerLink}
      >
        <div
          id="grid-item-header"
          // style={{ width: "100%", ...headerStyles }}
          dangerouslySetInnerHTML={{
            __html: headerTitle,
          }}
          className="ql-editor"
        />
      </a>
    </Header>
  }

  return (
    <>
      <Dropdown
        disabled={isOpenMode || previewMode}
        overlay={gridItemsMenu}
        trigger={["contextMenu"]}
      >
        <div className="hi-grid-item-inner" id={`id${id}`} key={id}>
          <Layout className={`hi-grid-item-container ${['filter-component'].includes(compType) ? 'hi-grid-item-container-overflow-visible' : ''}`}>
            {headerValues?.enable && openModeItemsToolbar}
            {headerValues?.enable && (
              headerValues?.enableTooltip ? <Tooltip title={headerValues?.tooltipText} >
                {renderGridItemHeader()}
              </Tooltip> : renderGridItemHeader()
            )}

            {
              shouldShowGridItemsToolBar()
              && <div style={{ marginTop: "2px" }}>{gridItemToolbar}</div>}
            {downloadingInfoModal}
            <Content
              className="hi-grid-item-content"
              style={
                gridItemStyles
                  ? { height: "100%", ...gridItemStyles }
                  : { height: "100%" }
              }
            >

              {renderGridItem()}
              <div id={`html-${id}`} />
            </Content>
          </Layout>
        </div>
      </Dropdown>
    </>
  );
};
const SizeAwareHIGridItem = withSizeHOC(HIGridItem);
export { SizeAwareHIGridItem, HIGridItem };
