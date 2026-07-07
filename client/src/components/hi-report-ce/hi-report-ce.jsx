import { useState, useEffect } from "react";
import { useSelector, useDispatch } from "react-redux";
import { Row, Col, Image, Select } from "antd";
import {
  storeReportCeTypesDetails,
  storeReportCeEditorData,
} from "../../redux/actions/reprotce.actions.js";
import classNames from "classnames";
import DataSourcesEditor from "./components/DataSourcesEditor";
import DashboardEditor from "./components/DashboardEditor";
import ParametersEditor from "./components/ParametersEditor";
import ReportEditor from "./components/ReportEditor";
import requests from "../../base/requests";
import notify from "../hi-notifications/notify";
import { Responsive, WidthProvider } from "react-grid-layout";
import HIReportCeSidebar from "../hi-sidebar/hi-reportCeSidebar.jsx";
import { useWindowSize } from "../../customHooks/useWindowSize";
import "./index.scss";

const { Option } = Select;

const ResponsiveGridLayout = WidthProvider(Responsive);

export const typesDetails = (dispatch) => {
  const uri = "util/io/getTypesDetails";

  requests.reportce(dispatch).postTypesDetails(
    uri,
    {},
    (res) => {
      dispatch(storeReportCeTypesDetails(res));
    },
    (e) => {}
  );
};

const HIReportCE = () => {
  const dispatch = useDispatch();
  const editing = useSelector((store) => store.reportCe.editing);
  const dataSourceData = useSelector((store) => store.reportCe.dataSourceData);
  const parametersData = useSelector((store) => store.reportCe.parametersData);
  const reportData = useSelector((store) => store.reportCe.reportData);
  const dashboardLayoutData = useSelector((store) => store.reportCe.dashboardLayoutData);
  const activeEditorData = useSelector((store) => store.reportCe.activeEditorData);
  const [html, setHtml] = useState("");
  const [css, setCss] = useState("");
  const [js, setJs] = useState("");
  const [sql, setSql] = useState("");
  const [visualization, setVisualization] = useState("");
  const [width, offsetHeight] = useWindowSize();
  const Notify = notify(dispatch);

  let calculatedH = 0;
  try {
    calculatedH = offsetHeight / 12 || 52;
  } catch (e) {
    calculatedH = 52;
  }

  let storedLayouts = {
    xxs: [
      { i: "report-ce-sidebar", w: 100, h: calculatedH, x: 0, y: 0 },
      { i: "report-ce-body", w: 100, h: calculatedH, x: 0, y: calculatedH },
    ],
    xs: [
      { i: "report-ce-sidebar", w: 50, h: calculatedH, x: 0, y: 0 },
      { i: "report-ce-body", w: 50, h: calculatedH, x: 50, y: 0 },
    ],
    sm: [
      { i: "report-ce-sidebar", w: 50, h: calculatedH, x: 0, y: 0 },
      { i: "report-ce-body", w: 50, h: calculatedH, x: 50, y: 0 },
    ],
    md: [
      { i: "report-ce-sidebar", w: 17, h: calculatedH, x: 0, y: 0 },
      { i: "report-ce-body", w: 83, h: calculatedH, x: 17, y: 0 },
    ],
    lg: [
      { i: "report-ce-sidebar", w: 17, h: 52, x: 0, y: 0 },
      { i: "report-ce-body", w: 83, h: 52, x: 17, y: 0 },
    ],
  };

  useEffect(() => {
    if (process.env.NODE_ENV === "test") {
      return null;
    } else {
      typesDetails(dispatch);
    }
  }, []);

  useEffect(() => {
    if (activeEditorData.type === "datasource" && dataSourceData.length > 0) {
      let activeData = dataSourceData.filter(
        (eachData) => eachData.id === activeEditorData.datasourceId
      );
      if (activeData.length) {
        setHtml(activeData[0].configure);
      } else {
        dispatch(
          storeReportCeEditorData({
            id: dataSourceData[0].id,
            name: dataSourceData[0].name,
            type: "datasource",
          })
        );
      }
    } else if (activeEditorData.type === "dashboardLayout") {
      setHtml(dashboardLayoutData.html);
      setCss(dashboardLayoutData.css);
    } else if (activeEditorData.type === "parameter" && parametersData.length > 0) {
      let activeData = parametersData.filter(
        (eachData) => eachData.id === activeEditorData.paramterId
      );
      if (activeData.length) {
        setJs(activeData[0].configure);
        setSql(activeData[0].sql.text);
      } else {
        dispatch(
          storeReportCeEditorData({
            id: parametersData[0].id,
            name: parametersData[0].name,
            type: "parameter",
          })
        );
      }
    } else if (activeEditorData.type === "report" && reportData.length > 0) {
      let activeData = reportData.filter((eachData) => eachData.id === activeEditorData.reportId);
      if (activeData.length) {
        setJs(activeData[0].configure);
        setSql(activeData[0].sql.text);
        setVisualization(activeData[0].visualisation);
      } else {
        dispatch(
          storeReportCeEditorData({
            id: reportData[0].id,
            name: reportData[0].name,
            type: "report",
          })
        );
      }
    }
  }, [activeEditorData, dataSourceData, parametersData, reportData]);

  const onSelectDatasource = (value, e) => {
    if (editing) {
      Notify.error({
        message: "Please click on Apply button to save changes.",
        type: "warning",
      });
    } else {
      dispatch(
        storeReportCeEditorData({
          id: e.key,
          name: e.value,
          type: "datasource",
        })
      );
    }
  };
  const onSelectParameter = (value, e) => {
    if (editing) {
      Notify.error({
        message: "Please click on Apply button to save changes.",
        type: "warning",
      });
    } else {
      dispatch(storeReportCeEditorData({ id: e.key, name: e.value, type: "parameter" }));
    }
  };
  const onSelectReport = (value, e) => {
    if (editing) {
      Notify.error({
        message: "Please click on Apply button to save changes.",
        type: "warning",
      });
    } else {
      dispatch(storeReportCeEditorData({ id: e.key, name: e.value, type: "report" }));
    }
  };
  const operations = () => {
    if (activeEditorData.type === "datasource" && dataSourceData.length > 0) {
      return (
        <Select
          className="report-select"
          value={activeEditorData.name}
          onChange={onSelectDatasource}
        >
          {dataSourceData.map((eachData) => (
            <Option key={eachData.id} value={eachData.name}>
              {eachData.name}
            </Option>
          ))}
        </Select>
      );
    } else if (activeEditorData.type === "parameter" && parametersData.length > 0) {
      return (
        <Select
          value={activeEditorData.name}
          className="report-select"
          onChange={onSelectParameter}
        >
          {parametersData.map((eachData) => (
            <Option key={eachData.id} value={eachData.name}>
              {eachData.name}
            </Option>
          ))}
        </Select>
      );
    } else if (activeEditorData.type === "report" && reportData.length > 0) {
      return (
        <Select value={activeEditorData.name} className="report-select" onChange={onSelectReport}>
          {reportData.map((eachData) => (
            <Option key={eachData.id} value={eachData.name}>
              {eachData.name}
            </Option>
          ))}
        </Select>
      );
    }
  };
  const handleDataSource = () => {
    if (editing) {
      Notify.error({
        message: "Please click on Apply button to save changes.",
        type: "warning",
      });
    } else {
      dispatch(
        storeReportCeEditorData({
          id: dataSourceData[0] ? dataSourceData[0].id : "",
          name: dataSourceData[0] ? dataSourceData[0].name : "",
          type: "datasource",
        })
      );
    }
  };
  const handleDashboard = () => {
    if (editing) {
      Notify.error({
        message: "Please click on Apply button to save changes.",
        type: "warning",
      });
    } else {
      dispatch(
        storeReportCeEditorData({
          id: "html",
          type: "dashboardLayout",
        })
      );
    }
  };
  const handleParameters = () => {
    if (editing) {
      Notify.error({
        message: "Please click on Apply button to save changes.",
        type: "warning",
      });
    } else {
      dispatch(
        storeReportCeEditorData({
          id: parametersData[0] ? parametersData[0].id : "",
          name: parametersData[0] ? parametersData[0].name : "",
          type: "parameter",
        })
      );
    }
  };
  const handleReport = () => {
    if (editing) {
      Notify.error({
        message: "Please click on Apply button to save changes.",
        type: "warning",
      });
    } else {
      dispatch(
        storeReportCeEditorData({
          id: reportData[0] ? reportData[0].id : "",
          name: reportData[0] ? reportData[0].name : "",
          type: "report",
        })
      );
    }
  };

  const renderBody = () => {
    return (
      <>
        <Col span={24} className="editor-header">
          <Row>
            <Col className="report-menu-container">
              <ul className="report-ce-menu">
                <li
                  className={classNames({
                    "list-item": true,
                    "first-item": true,
                    "active-tab": activeEditorData.type === "datasource",
                  })}
                  key="1"
                  onClick={handleDataSource}
                >
                  Data Source
                </li>
                <li
                  className={classNames({
                    "list-item": true,
                    "active-tab": activeEditorData.type === "dashboardLayout",
                  })}
                  key="2"
                  onClick={handleDashboard}
                >
                  Dashboard Layout
                </li>
                <li
                  className={classNames({
                    "list-item": true,
                    "active-tab": activeEditorData.type === "parameter",
                  })}
                  key="3"
                  onClick={handleParameters}
                >
                  Parameter
                </li>
                <li
                  className={classNames({
                    "list-item": true,
                    "last-item": true,
                    "active-tab": activeEditorData.type === "report",
                  })}
                  key="4"
                  onClick={handleReport}
                >
                  Reports
                </li>
              </ul>
            </Col>
            <Col>{operations()}</Col>
          </Row>
        </Col>
        <Col span={24}>
          {activeEditorData.type === "datasource" ? (
            <DataSourcesEditor dataSourceData={dataSourceData} html={html} setHtml={setHtml} />
          ) : activeEditorData.type === "dashboardLayout" ? (
            <DashboardEditor html={html} setHtml={setHtml} css={css} setCss={setCss} />
          ) : activeEditorData.type === "parameter" ? (
            <ParametersEditor
              sql={sql}
              setSql={setSql}
              js={js}
              setJs={setJs}
              parametersData={parametersData}
            />
          ) : (
            <ReportEditor
              reportData={reportData}
              sql={sql}
              setSql={setSql}
              js={js}
              setJs={setJs}
              visualization={visualization}
              setVisualization={setVisualization}
            />
          )}
        </Col>
      </>
    );
  };

  let layoutProps = {
    cols: { lg: 100, md: 100, sm: 100, xs: 100, xxs: 100 },
    // width: 500,
    className: "layout",
    // rowHeight: 100,
    rowHeight: 1,
    // autoSize:true,
    colWidth: 1,
    isDraggable: false,
    isResizable: true,
    // compactType: null,
    preventCollision: true,
    measureBeforeMount: true,
    breakpoints: { lg: 1200, md: 996, sm: 768, xs: 480, xxs: 0 },
    compactType: "horizontal",
    margin: [0, 10],
    useCSSTransforms:false
  };

  return (
    <ResponsiveGridLayout
      {...layoutProps}
      layouts={storedLayouts}
      className="hi-report-ce-container"
    >
      <div key="report-ce-sidebar" className="report-ce-sidebar">
        <HIReportCeSidebar />
      </div>
      <div key="report-ce-body" className="report-ce-body">
        <Row>
          {activeEditorData.type === "" ? (
            <Image preview={false} src="images/hi-report-ce/report-ce.png" />
          ) : (
            renderBody()
          )}
        </Row>
      </div>
    </ResponsiveGridLayout>
  );
};

export default HIReportCE;
