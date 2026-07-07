import {
  AreaChartOutlined,
  BgColorsOutlined,
  ConsoleSqlOutlined,
  EditOutlined,
  FunnelPlotOutlined,
  SettingOutlined,
  ToolOutlined
} from "@ant-design/icons";
import { Menu, Switch, Tooltip } from "antd";
import { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import ErrorFallback from "../../../errorBoundry/ErrorFallback";
import { changeEditingPane, setShowAllVizualizations } from "../../../redux/actions/hreport.actions";
import ShortCutText from "../../common/hi-shortcuts/hi-shortcuts";
import TutorialInfo from "../../common/hi-tutorial";
import { checkIfDrillThrough } from "../hi-viz-area/utils/utillities";
import CodeEditor from "./components/editor/editor";
import Filters from "./components/filters/filters";
import HrPropertiesWrapper from "./components/properties/properties-wrapper";
import Settings from "./components/settings/settings";
import SqlEditor from "./components/sql/sql";
import ValuesComponent from "./components/values/values";
import VizList from "./components/viz-items/viz-list";
import "./editing.scss";
import { previousStateInstance } from "./utils/filter-utils";
import VizListNew from "./components/viz-items/viz-list-new";

let editingOptions = [
  {
    id: "1",
    elementKey: "hr-viz",
    display: "Visualization",
    icon: <AreaChartOutlined />,
    scText: "V",
    scLocation: "HR"
  },
  {
    id: "2",
    elementKey: "hr-filters",
    display: "Filters",
    icon: <FunnelPlotOutlined />,
    scText: "F",
    scLocation: "HR"
  },
  {
    id: "3",
    elementKey: "hr-editor",
    display: "Operations",
    icon: <EditOutlined />,
    scText: "O",
    scLocation: "HR"
  },
  {
    id: "4",
    elementKey: "hr-sql",
    display: "Sql",
    icon: <ConsoleSqlOutlined />,
    scText: "Q",
    scLocation: "HR"
  },
  {
    id: "5",
    elementKey: "hr-settings",
    display: "Settings",
    icon: <ToolOutlined />,
    scText: "T",
    scLocation: "HR"
  },
  {
    id: "6",
    elementKey: "hr-marks",
    display: "Marks",
    icon: <BgColorsOutlined />,
    scText: "M",
    scLocation: "HR"
  },
  {
    id: "7",
    elementKey: "hr-props",
    display: "Properties",
    icon: <SettingOutlined />,
    scText: "P",
    scLocation: "HR"
  },
];

export const handleChangePane = (dispatch, id, e) => {
  dispatch(changeEditingPane({ id }));
};

const EditingArea = (props) => {
  const dispatch = useDispatch();
  let { reportId, dashboardFilter, openFileBrowser, isFilterComponent = false } = props;
  const tutorialData = useSelector((state) => state.app.tutorialData);
  let { activeTool, filters, mode, reportData, activeDrillthroughId = '', showAllVisualizations } = useSelector((state) => {
    let activeReport = state.hreport.present.reports.find(
      (report) => report.id === reportId
    );
    return activeReport;
  });
  const isDrillThroughReport = checkIfDrillThrough(dispatch, reportId, activeDrillthroughId)

  const filterMode = mode === "open" || mode === "filter";
  if (filterMode) {
    activeTool = "2";
  }

  useEffect(() => {
    const instance = previousStateInstance()
    return () => {
      instance.clear()
    }
  }, [])


  return (
    <ErrorFallback {...props}>
      <div
        className={mode === "filter" ? "height100percent" : "hr-editing-area"}
      >
        {!["open", "filter", "dashboard"].includes(mode) && (
          <Menu
            selectedKeys={[activeTool]}
            onClick={(e) => handleChangePane(dispatch, e.key, e)}
            className="hr-editing-area-items"
            mode="horizontal"
          >
            {editingOptions.map((item) => {
              let filterCount = null;
              if (item.id === "2" && filters.length) {
                filterCount = (
                  <span className="filter-count"> ({filters.length}) </span>
                );
              }
              if (tutorialData) {
                return (
                  <Menu.Item key={item.id} data-testid={`tool-${item.id}`} disabled={isDrillThroughReport}>
                    <TutorialInfo elementKey={item.elementKey}>
                      <Tooltip title={isDrillThroughReport ? "This action is disabled" : ""}>
                        <span className="menu-item">
                          <span className="menu-icon"> {item.icon} </span>
                          {item.display}
                          {filterCount}
                        </span>
                      </Tooltip>
                    </TutorialInfo>
                  </Menu.Item>
                );
              } else {
                return (
                  <Menu.Item
                    key={item.id}
                    icon={item.icon}
                    data-testid={`tool-${item.id}`}
                    disabled={isDrillThroughReport}
                  >
                    <ShortCutText text={item.scText} scLocation={item.scLocation} menuItem={true}>
                      <Tooltip title={isDrillThroughReport ? "This action is disabled" : ""}>
                        <span>
                          {item.display}
                          {filterCount}
                        </span>
                      </Tooltip>
                    </ShortCutText>
                  </Menu.Item>
                );
              }
            })}
          </Menu>
        )}
        <ErrorFallback {...props}>
          <div className={isDrillThroughReport ? "hr-editing-area-content hi-editing-area-disabled" : "hr-editing-area-content"}>
            {activeTool === "1" && (showAllVisualizations ? <VizListNew getApi={props.getApi} vizRef={props.vizRef} /> : <VizList getApi={props.getApi} vizRef={props.vizRef} />)}
            {activeTool === "2" && (
              <Filters
                reportId={reportId}
                getApi={props.getApi}
                dashboardFilter={dashboardFilter}
                changeDashboardFilter={props.changeDashboardFilter}
                allFilters={props.allFilters}
                isFilterComponent={isFilterComponent}
              />
            )}
            {activeTool === "3" && <CodeEditor getApi={props.getApi} />}
            {activeTool === "4" && (
              <SqlEditor
                reportId={reportId}
                getApi={props.getApi}
                abortFetchData={props.abortFetchData}
              />
            )}
            {activeTool === "5" && <Settings openFileBrowser={openFileBrowser} />}
            {activeTool === "6" && (
              <ValuesComponent
                openFbForDrillThrough={props.openFbForDrillThrough}
                reportId={reportId}
                abortFetchData={props.abortFetchData}
                getApi={props.getApi}
              />
            )}
            {activeTool === "7" && <HrPropertiesWrapper dataId={reportData?.dataId} loading={reportData?.loading} />}
          </div>
        </ErrorFallback>
      </div>
    </ErrorFallback>
  );
};

export default EditingArea;
