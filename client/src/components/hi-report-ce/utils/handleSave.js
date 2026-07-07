import requests from "../../../base/requests";

export const handleSaveReportCeFile = (dispatch, formData, Notify) => {
  const uri = "dashboard/efwce/designer";
  const data = {
    state: {
      allTypes: {
        sqlTypes: [
          { name: "sql" },
          { name: "sql.groovy" },
          { name: "sql.adhoc" },
        ],
        vizTypes: [
          { name: "Area", type: "EFW-c3", subtype: "Area", icon: "AreaChart" },
          {
            name: "Area Spline",
            type: "EFW-c3",
            subtype: "AreaSpline",
            icon: "AreaSplineChart",
          },
          {
            name: "Area Step",
            type: "EFW-c3",
            subtype: "AreaStep",
            icon: "AreaStepChart",
          },
          { name: "Bar", type: "EFW-c3", subtype: "bar", icon: "BarChart" },
          {
            name: "Donut",
            type: "EFW-c3",
            subtype: "donut",
            icon: "DonutChart",
          },
          {
            name: "Gauge",
            type: "EFW-c3",
            subtype: "gauge",
            icon: "HICircularGauge",
          },
          { name: "Line", type: "EFW-c3", subtype: "Line", icon: "LineChart" },
          { name: "Pie", type: "EFW-c3", subtype: "Pie", icon: "PieChart" },
          {
            name: "Scatter",
            type: "EFW-c3",
            subtype: "Scatter",
            icon: "ScatterChart",
          },
          {
            name: "Spline",
            type: "EFW-c3",
            subtype: "Spline",
            icon: "SplineChart",
          },
          { name: "Step", type: "EFW-c3", subtype: "Step", icon: "StepChart" },
          { name: "Cross Tab", type: "EFW-CrossTab", icon: "HICrossTable" },
          { name: "Table", type: "EFW-Table", icon: "HITable" },
          { name: "Custom", type: "Custom", icon: "VF" },
        ],
        connTypes: [
          {
            clazz: "com.helicalinsight.datasource.GlobalJdbcDataSource",
            classifier: "global",
            name: "Managed DataSource",
            type: "global.jdbc",
            hidden: "false",
          },
          {
            clazz: "com.helicalinsight.datasource.JDBCDriver",
            classifier: "efwd",
            name: "Plain Jdbc DataSource",
            type: "sql.jdbc",
            hidden: "false",
          },
          {
            clazz: "com.helicalinsight.adhoc.SqlAdhocDriver",
            classifier: "efwd",
            name: "Adhoc DataSource",
            type: "sql.adhoc",
            hidden: "true",
          },
          {
            clazz: "com.helicalinsight.datasource.ExtJDBCDriver",
            classifier: "efwd",
            name: "Groovy Plain Jdbc DataSource",
            type: "sql.jdbc.groovy",
            hidden: "false",
          },
          {
            clazz: "com.helicalinsight.datasource.GroovyManagedDriver",
            classifier: "efwd",
            name: "Groovy Managed Jdbc DataSource",
            type: "sql.jdbc.groovy.managed",
            hidden: "false",
          },
        ],
        parameterTypes: [
          { name: "Collection" },
          { name: "Numeric" },
          { name: "String" },
        ],
      },
      datasource: [],
      dashboard: { html: "", css: "" },
      parameters: [],
      reports: [],
      currentEditor: { type: "", name: "", component: "" },
      sqlList: [],
      aceText: "",
      uuid: "",
      showDatasource: false,
      showDashboard: false,
      showParameter: false,
      showReport: false,
      Editor: { type: "", name: "", component: "", aceText: "" },
      editing: false,
      isEditor: "",
    },
    htmlString: "",
    cssString: "",
    configurations: {},
    dir: "1645750179859",
    file: "Name",
  };

  const myState = {
    configurations: {},
    cssString: "This is css",
    dir: "ReporCE",
    file: "Test",
    htmlString: "<h1>This is html</h1>",
    state: {
      Editor: { aceText: "", component: "", name: "", type: "" },
      aceText: "",
      allTypes: {
        sqlTypes: [
          { name: "sql" },
          { name: "sql.groovy" },
          { name: "sql.adhoc" },
        ],
        vizTypes: [
          { name: "Area", type: "EFW-c3", subtype: "Area", icon: "AreaChart" },
          {
            name: "Area Spline",
            type: "EFW-c3",
            subtype: "AreaSpline",
            icon: "AreaSplineChart",
          },
          {
            name: "Area Step",
            type: "EFW-c3",
            subtype: "AreaStep",
            icon: "AreaStepChart",
          },
          { name: "Bar", type: "EFW-c3", subtype: "bar", icon: "BarChart" },
          {
            name: "Donut",
            type: "EFW-c3",
            subtype: "donut",
            icon: "DonutChart",
          },
          {
            name: "Gauge",
            type: "EFW-c3",
            subtype: "gauge",
            icon: "HICircularGauge",
          },
          { name: "Line", type: "EFW-c3", subtype: "Line", icon: "LineChart" },
          { name: "Pie", type: "EFW-c3", subtype: "Pie", icon: "PieChart" },
          {
            name: "Scatter",
            type: "EFW-c3",
            subtype: "Scatter",
            icon: "ScatterChart",
          },
          {
            name: "Spline",
            type: "EFW-c3",
            subtype: "Spline",
            icon: "SplineChart",
          },
          { name: "Step", type: "EFW-c3", subtype: "Step", icon: "StepChart" },
          { name: "Cross Tab", type: "EFW-CrossTab", icon: "HICrossTable" },
          { name: "Table", type: "EFW-Table", icon: "HITable" },
          { name: "Custom", type: "Custom", icon: "VF" },
        ],
        connTypes: [
          {
            clazz: "com.helicalinsight.datasource.GlobalJdbcDataSource",
            classifier: "global",
            name: "Managed DataSource",
            type: "global.jdbc",
            hidden: "false",
          },
          {
            clazz: "com.helicalinsight.datasource.JDBCDriver",
            classifier: "efwd",
            name: "Plain Jdbc DataSource",
            type: "sql.jdbc",
            hidden: "false",
          },
          {
            clazz: "com.helicalinsight.adhoc.SqlAdhocDriver",
            classifier: "efwd",
            name: "Adhoc DataSource",
            type: "sql.adhoc",
            hidden: "true",
          },
          {
            clazz: "com.helicalinsight.datasource.ExtJDBCDriver",
            classifier: "efwd",
            name: "Groovy Plain Jdbc DataSource",
            type: "sql.jdbc.groovy",
            hidden: "false",
          },
          {
            clazz: "com.helicalinsight.datasource.GroovyManagedDriver",
            classifier: "efwd",
            name: "Groovy Managed Jdbc DataSource",
            type: "sql.jdbc.groovy.managed",
            hidden: "false",
          },
        ],
        parameterTypes: [
          { name: "Collection" },
          { name: "Numeric" },
          { name: "String" },
        ],
      },
      currentEditor: { component: "", name: "", type: "" },
      dashboard: { html: "<h1>This is html</h1>", css: "This is css" },
      datasource: [],
      parametersData: [],
      reports: [],
      editing: false,
      isEditor: "",
      showDashboard: false,
      showDatasource: false,
      showParameter: false,
      showReport: false,
      sqlList: [],
      uuid: "6a7b97e3-2f37-4fcd-a3ff-636fb82567a8",
    },
    uuid: "9d73341d-1afc-474d-b82a-dd7a60e39d18",
    visualisation: [],
    // isValid: true,
  };
  requests.reportce(dispatch).saveReportCeFile(
    uri,
    formData,
    (res) => {
      console.log(res);
    },
    (e) => {
      console.log(e);
    }
  );
};
