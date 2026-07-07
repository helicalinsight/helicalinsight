import { useState, useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { Collapse, Space, Button, Skeleton } from "antd";
import { PlusOutlined, CaretRightOutlined } from "@ant-design/icons";
import { v4 as uuidv4 } from "uuid";
import {
  storeReportCeDatasource,
  storeReportCeParameters,
  storeReportCeReport,
  storeReportCeEditorData,
  storeReportCeDashboardData,
  storeActiveTab,
} from "../../redux/actions/reprotce.actions.js";
import DataSources from "../hi-report-ce/components/dataSources";
import Parameters from "../hi-report-ce/components/parameters";
import Report from "../hi-report-ce/components/Report";
import requests from "../../base/requests";

import "./index.scss";

const { Panel } = Collapse;

let dataSourceNumber = 1;
let parameterNumber = 1;
let reportNumber = 1;

const getReportConfig = (name, id) => {
  return `var ${name} = {
          name: "${name}",
          type:"chart",
          listeners:["column_name"],
          requestParameters :{
          column_name: "column_name"
          },
          vf : {
              id: "${id}",
              file: '__efwvf_name__'
          },
          efwd : {
              file: '__efwd_file_name__'
          },
          htmlElementId : "#htmlelement_id", // provide report id here
          executeAtStart: true  // it can be true or false
      };`;
};

const getParamConfig = (name, sqlId) => {
  return `var ${name} = {
          name: "${name}" ,
          type:  "select/select2/datepicker/daterangepicker/input/button",             
          parameters:["column_name"],
          options:{
              multiple: false,             // it can be true or false
              display : 'column_name',
              value : 'column_name',
              placeholder: 'select value'
          },
          htmlElementId : "#htmlelement_id",              
          executeAtStart: true,            // it can be true or false
          map: ${sqlId},
          efwd : {
              file: '__efwd_file_name__'
          },
      };`;
};

export const handleAddDataSource = (e, dispatch, dataSourceData) => {
  if (e) {
    e.stopPropagation();
  }
  if (dataSourceData.length) {
    dataSourceNumber = dataSourceData.slice(-1)[0].number + 1;
  } else {
    dataSourceNumber = 1;
  }

  const id = uuidv4();
  const name = "connection" + dataSourceNumber;

  const data = {
    id,
    label: name,
    value: id,
    name: name,
    number: dataSourceNumber,
    type: {
      name: "Managed DataSource",
      type: "global.jdbc",
    },
    isEditClicked: false,
    configure: "<globalId></globalId>",
  };

  dispatch(storeReportCeDatasource([...dataSourceData, data]));
};

export const handleAddParameters = (e, dispatch, parametersData) => {
  if (e) {
    e.stopPropagation();
  }
  if (parametersData.length) {
    parameterNumber = parametersData.slice(-1)[0].number + 1;
  } else {
    parameterNumber = 1;
  }

  const id = uuidv4();
  const name = "paramter" + parameterNumber;

  const data = {
    id,
    name,
    configure: getParamConfig(name, id),
    number: parameterNumber,
    type: "String",
    isEditClicked: false,
    label: name,
    value: id,
    sql: { id, type: "sql", text: ``, dataSource: {}, parameters: [] },
  };

  dispatch(storeReportCeParameters([...parametersData, data]));
};

const HIReportCeSidebar = () => {
  const dispatch = useDispatch();
  const dataSourceData = useSelector((store) => store.reportCe.dataSourceData);
  const parametersData = useSelector((store) => store.reportCe.parametersData);
  const reportData = useSelector((store) => store.reportCe.reportData);
  const activeTabs = useSelector((store) => store.reportCe.activeTabs);
  const typesDetails = useSelector((store) => store.reportCe.typesDetails);
  const [activeKeys, setActiveKeys] = useState([]);

  useEffect(() => {
    let keys = [];
    if (dataSourceData.length > 0) {
      keys.push("1");
    }
    if (parametersData.length > 0) {
      keys.push("3");
    }
    if (reportData.length > 0) {
      keys.push("4");
    }
    Object.keys(activeTabs).map((key, i) => {
      if (activeTabs[key]) keys.push(i + 1 + "");
    });
    setActiveKeys(keys);
  }, [activeTabs, dataSourceData, parametersData, reportData]);

  const handleAddReports = (e) => {
    e.stopPropagation();
    if (reportData.length) {
      reportNumber = reportData.slice(-1)[0].number + 1;
    } else {
      reportNumber = 1;
    }

    const name = "report" + reportNumber;
    const id = uuidv4();

    const data = {
      id: id,
      number: reportNumber,
      configure: getReportConfig(name, id),
      visualisation: `<Dimensions>column_name</Dimensions>\n<Measures>column_name</Measures>`,
      name,
      isEditClicked: false,
      vizName: "Area",
      type: "EFW-c3",
      subtype: "Area",
      icon: "AreaChart",
      sql: {
        id,
        type: "sql",
        text: ``,
        dataSource: {},
        parameters: [],
      },
    };
    dispatch(storeReportCeReport([...reportData, data]));
  };

  const handleDashboardLayoutClick = (id) => {
    dispatch(storeReportCeEditorData({ id, type: "dashboardLayout" }));
  };

  function callback(keys) {
    setActiveKeys(keys);
  }

  const handleEdit = () => {
    typesDetails(dispatch);

    const uri = "dashboard/efwce/fetch";
    const formData = {
      dir: "1463377807724/1570173811451",
      file: "edae6d65-f21d-4287-be53-37af5b9faea6.efwce",
    };

    requests.reportce(dispatch).editReportCeFile(
      uri,
      formData,
      (res) => {
        document.title = `${res.reportName} HI:Report edit`;
        console.log(res);
        dispatch(storeReportCeDatasource(res.state.datasource));
        dispatch(storeReportCeParameters(res.state.parameters));
        dispatch(storeReportCeReport(res.state.reports));
        dispatch(storeReportCeDashboardData(res.state.dashboard));
        dispatch(
          storeActiveTab({
            showDashboard: res.state.showDashboard,
            showDatasource: res.state.showDatasource,
            showParameter: res.state.showParameter,
            showReport: res.state.showReport,
          })
        );
        dispatch(
          storeReportCeEditorData({
            id: "html",
            type: "dashboardLayout",
          })
        );
      },
      (e) => {
        console.log(e);
      }
    );
  };

  return (
    <div data-testid = "hi-report-ce-sidebar" className="hi-sidebar-content">
      {typesDetails ? (
        <>
          <Collapse
            bordered={false}
            activeKey={activeKeys}
            onChange={callback}
            ghost
            expandIcon={({ isActive }) => (
              <CaretRightOutlined rotate={isActive ? 90 : 0} />
            )}
            className="report-ce-collapse"
          >
            <Panel
              header={[
                <span className="report-title">Data Source</span>,
                <span className="add-icon">
                  <PlusOutlined
                    onClick={(e) =>
                      handleAddDataSource(e, dispatch, dataSourceData)
                    }
                  />
                </span>,
              ]}
              key="1"
            >
              <DataSources />
            </Panel>
            <Panel
              header={[<span className="report-title">Dashboard Layout</span>]}
              key="2"
            >
              <Space>
                <a onClick={() => handleDashboardLayoutClick("html")}>HTML</a>
                <a onClick={() => handleDashboardLayoutClick("css")}>CSS</a>
              </Space>
            </Panel>
            <Panel
              header={[
                <span className="report-title">Parameter</span>,
                <span>
                  <PlusOutlined
                    className="add-icon"
                    onClick={(e) =>
                      handleAddParameters(e, dispatch, parametersData)
                    }
                  />
                </span>,
              ]}
              key="3"
            >
              <Parameters />
            </Panel>
            <Panel
              header={[
                <span className="report-title">Report</span>,
                <span>
                  <PlusOutlined
                    className="add-icon"
                    onClick={handleAddReports}
                  />
                </span>,
              ]}
              key="4"
            >
              <Report />
            </Panel>
          </Collapse>
          <Button onClick={handleEdit}>Edit</Button>
        </>
      ) : (
        <Skeleton />
      )}
    </div>
  );
};

export default HIReportCeSidebar;
