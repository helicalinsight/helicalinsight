import { v4 as uuidv4 } from "uuid";
import parser from "xml-js";

const getXmlConnObj = (xml) => {
  try {
    if (!xml) return "";
    xml = "<a>" + xml + "</a>";
    let jsonObj = JSON.parse(parser.xml2json(xml), {
      compact: true,
      spaces: 4,
    });
    let obj = {},
      modified = false,
      isValid = true;
    jsonObj.elements[0].elements.map((elem) => {
      if (elem.elements) {
        if (!modified) modified = true;
        obj[elem.name] = elem.elements[0][elem.elements[0].type];
      } else {
        isValid = false;
      }
    });
    if (!modified) {
      // $.notify({message: "One of your configuration is invalid", icon: "fa fa-exclamation-circle"}, {type: "warning"})
      return "";
    }
    if (!isValid) {
      // $.notify({message: "One of your configuration is invalid", icon: "fa fa-exclamation-circle"}, {type: "warning"})
      return isValid;
    }
    if (modified && isValid) return obj;
  } catch (e) {
    // $.notify(
    //   {
    //     message: "One of your configuration is invalid",
    //     icon: "fa fa-exclamation-circle",
    //   },
    //   { type: "warning" }
    // );
    return "";
  }
};

const getXmlVizObj = (xml, type) => {
  try {
    if (!xml) return {};
    let isValid = true;
    if (type === "Custom") return xml;
    xml = "<a>" + xml + "</a>";
    let jsonObj = JSON.parse(parser.xml2json(xml), {
      compact: true,
      spaces: 4,
    });
    let obj = {};
    if (jsonObj.elements[0].elements && jsonObj.elements[0].elements.length) {
      jsonObj.elements[0].elements.map((elem) => {
        if (!elem.elements) {
          isValid = false;
        } else {
          obj[elem.name] = elem.elements[0].text;
        }
      });
      if (!isValid) {
        return isValid;
      } else {
        return obj;
      }
    } else {
      return "";
    }
  } catch (e) {
    return;
  }
};

export const getFormData = (
  typesDetails,
  dataSourceData,
  dashboardLayoutData,
  parametersData,
  reportData,
  dir,
  fileName
) => {
  let formData = {
    configurations: {},
    cssString: "",
    dir: dir.path,
    file: fileName,
    htmlString: "",
    state: {
      Editor: {
        aceText: "",
        component: "",
        name: "",
        type: "",
      },
      aceText: "",
      allTypes: typesDetails,
      currentEditor: {
        component: "",
        name: "",
        type: "",
      },
      dashboard: {
        html: "",
        css: "",
      },
      datasource: dataSourceData,
      parameters: parametersData,
      reports: reportData,
      editing: false,
      isEditor: "",
      showDashboard: false,
      showDatasource: false,
      showParameter: false,
      showReport: false,
      sqlList: [],
      uuid: uuidv4(),
    },
    uuid: uuidv4(),
    visualisation: [],
    isValid: true,
  };

  let dataMaps = [];
  // let isConnSelected = true;

  parametersData.map((param) => {
    let { id, dataSource, text, type } = param.sql;
    if (text && !dataSource.id) {
      // isConnSelected = false;
      // $.notify(
      //   {
      //     message:
      //       "Please select connection for " + param.name + " sql in parameters",
      //     icon: "fa fa-exclamation-circle",
      //   },
      //   { type: "warning" }
      // );
    }
    let dataMap = {
      name: param.name,
      id,
      type,
      connection: dataSource.id,
      query: text,
    };
    if (param.sql.parameters.length) {
      dataMap.parameters = param.sql.parameters.map((sqlParam) => {
        let { openQuote, closeQuote } = sqlParam;
        let paramSql = {};
        paramSql.default = "";
        if (openQuote) paramSql.openQuote = openQuote;
        if (closeQuote) paramSql.closeQuote = closeQuote;
        parametersData.map((parameter) => {
          if (sqlParam.id === parameter.id) {
            paramSql.name = parameter.name;
            paramSql.type = parameter.type;
          }
        });
        return { parameter: paramSql };
      });
    }
    if (text) {
      dataMaps.push({ dataMap });
    }
  });

  reportData.map((report) => {
    let { id, dataSource, text, type } = report.sql;
    if (text && !dataSource.id) {
      // isConnSelected = false;
      // $.notify(
      //   {
      //     message:
      //       "please select connection for " + report.name + " sql in reports",
      //     icon: "fa fa-exclamation-circle",
      //   },
      //   { type: "warning" }
      // );
    }
    let dataMap = {
      name: report.name,
      id: String(id),
      type,
      connection: String(dataSource.id),
      query: text,
    };
    if (report.sql.parameters.length) {
      dataMap.parameters = report.sql.parameters.map((param) => {
        let { openQuote, closeQuote } = param;
        let paramSql = {};
        paramSql.default = "";
        if (openQuote) paramSql.openQuote = openQuote;
        if (closeQuote) paramSql.closeQuote = closeQuote;
        parametersData.map((parameter) => {
          if (param.id === parameter.id) {
            paramSql.name = parameter.name;
            paramSql.type = parameter.type;
          }
        });
        return { parameter: paramSql };
      });
    }
    if (text) {
      dataMaps.push({ dataMap });
    }
  });

  dataSourceData.map((conn) => {
    if (getXmlConnObj(conn.configure)) {
      if (!formData.efwd) formData.efwd = {};
      if (!formData.efwd.dataSources) {
        formData.efwd.dataSources = {};
        formData.efwd.dataSources.connections = [];
      }
      formData.efwd.dataSources.connections.push({
        connection: {
          id: conn.id,
          type: conn.type.type,
          connDetails: getXmlConnObj(conn.configure),
        },
      });
    }
    // if(!getXmlConnObj(conn.configure)){
    //     formData.isValid = false
    //     $.notify({message : `Please provide valid connection details for <b> ${conn.name} </b>`, icon: "fa fa-exclamation-circle"}, {type: "warning"})
    //     return
    // }
  });

  if (parametersData.length) {
    formData.configurations.parameters = [];
    parametersData.map((param) => {
      formData.state.sqlList = [...formData.state.sqlList, param.sql];
      if (param.configure) {
        formData.configurations.parameters.push({
          parameter: {
            id: param.id,
            name: param.name,
            value: param.configure,
          },
        });
      }
    });
  }

  if (reportData.length) {
    formData.configurations.reports = [];
    reportData.map((report) => {
      formData.state.sqlList = [...formData.state.sqlList, report.sql];
      if (report.configure) {
        formData.configurations.reports.push({
          report: {
            id: report.id,
            name: report.name,
            value: report.configure,
          },
        });
      }
    });

    // This is for visualization
    reportData.map((report) => {
      if (report.visualisation) {
        let obj = {};
        obj.id = report.id;
        obj.name = report.name;
        obj.type = report.type;
        obj.subtype = report.subtype;
        if (report.type === "Custom") {
          obj.dataSource = report.sql.id;
          obj.settings = { script: report.visualisation };
        } else {
          obj.DataSource = report.sql.id;
          Object.assign(obj, getXmlVizObj(report.visualisation, report.type));
        }
        formData.visualisation.push(obj);
      }
    });
  }

  if (dashboardLayoutData.css) {
    formData.cssString = dashboardLayoutData.css;
    formData.state.dashboard.css = dashboardLayoutData.css;
  }

  if (dashboardLayoutData.html) {
    formData.htmlString = dashboardLayoutData.html;
    formData.state.dashboard.html = dashboardLayoutData.html;
  }

  console.log("newData", formData);
  return formData;
};
