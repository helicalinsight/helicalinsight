import { Image, Menu, Space, Tooltip, Typography } from "antd";
import constants from "../../constants";
import { fileBrowserActions, hcrActions } from "../../redux/actions";
import { AreaChartOutlined, PlusOutlined } from "@ant-design/icons";
import {
  COLUMN_DATA,
  COLUMN_FOOTER,
  COLUMN_HEADER,
  HCR_CROSSTAB_CELL_HEIGHT,
  HCR_CROSSTAB_CELL_WIDTH,
  HCR_CROSSTAB_COLUMN_GROUP,
  HCR_CROSSTAB_COLUMN_HEADER,
  HCR_CROSSTAB_COLUMN_TOTAL_HEADER,
  HCR_CROSSTAB_ROW_GROUP,
  HCR_CROSSTAB_ROW_HEADER,
  HCR_CROSSTAB_ROW_TOTAL_HEADER,
  HCR_TABLE_DATA_CELL_HEIGHT,
  HCR_TABLE_DATA_CELL_WIDTH,
  hcrDSParameter,
  hcrDSQuery,
  hcrParaDate,
  hcrParaDateAndTime,
  hcrParaDateAndTimeRange,
  hcrParaDateRange,
  hcrParaInput,
  hcrParaQueryBasedDropdownList,
  hcrTableBandOrder,
  hcrTableBandsTypes,
  TABLE_FOOTER,
  TABLE_HEADER,
} from "./hcr-constants";
import {
  checkRelativeDateFilter,
  defaultAnchor,
} from "../../utils/filter-utils";
import moment from "moment";
import { v4 as uuidv4 } from "uuid";
import TutorialInfo from "../common/hi-tutorial";
import HIIcon from "../common/icons/hi-icons";
import requests from "../../base/requests";
import { cloneDeep, isArray, isEmpty, isEqual, property, update } from "lodash";
import { hcrTabInitialState } from "../../redux/reducers/hCR.reducer";
import { uuid } from "../../utils/uuid";
import {
  createCell,
  getErrorMessage,
  handleStreamResponse,
  makeCellId,
  hcrCanvasPaneHelperMethods,
  getDroppedNodeData,
  getAdvancedTableConfig,
  isGroupBand,
  getAvailableBands,
  getInitialGroupData,
  getTableStyles
} from "./hcrCanvas/hcrCanvasPaneHelperMethods";
import { HCR_TABLE_CELL_PROPERTIES } from "./hcrCanvas/advanceComponents/contants";

const randomString = uuid;
const { Paragraph } = Typography;

export const getDataSourceType = (dataSourceTypes = [], type) => {
  return dataSourceTypes.find((source) => source.type === type)?.name;
};

export const createHTMLTable = (arr) => {
  let headers = ["Key", "Action"];

  return (
    <table id="tooltipTable">
      {headers.map((val, index) => {
        return (
          <th style={{ textAlign: "center", fontWeight: "bold" }}>
            {capitalizeFirstLetter(val)}
          </th>
        );
      })}
      {arr.map((ele) => {
        return (
          <tr>
            <td
              style={{
                border: "1px solid white",
                padding: "0 5px 0 5px",
              }}
            >
              {ele.key}
            </td>
            <td
              style={{
                border: "1px solid white",
                padding: "0 5px 0 5px",
              }}
            >
              {ele.action}
            </td>
          </tr>
        );
      })}
    </table>
  );
};

function capitalizeFirstLetter(string) {
  return string.charAt(0).toUpperCase() + string.slice(1);
}

export const handleHcrFilterChange = ({ value, filter, setFilters }) => {
  if (filter.condition === "IN_RANGE") {
    setFilters((prevFilters) => {
      return prevFilters.map((ele) => {
        if (ele.uid !== filter.uid) {
          if (ele.label === filter.range?.[0]) {
            ele.values = [value[0]];
          } else if (ele.label === filter.range?.[1]) {
            ele.values = [value[1]];
          }
        } else {
          // ele.values = value;
          if (value === "_all_") {
            ele.values = ele.values.includes("_all_") ? [] : ["_all_"];
          } else {
            ele.values = value;
          }
          ele.dateValuesType = filter.dateValuesType;
        }
        return { ...ele };
      });
    });
  } else {
    setFilters((prevFilters) => {
      return prevFilters.map((ele) => {
        if (ele.uid === filter.uid) {
          const flatValue = Array.isArray(value?.[0]) ? value[0] : value;
          const finalValue = Array.isArray(flatValue) ? flatValue : [flatValue];
          if (filter.condition === "IS_ONE_OF") {
            if (filter.isMultiple) {
              if (
                ele.values.includes(flatValue) ||
                ele.values.includes(flatValue + "")
              ) {
                ele.values = ele.values.filter((item) => item != flatValue);
              } else {
                ele.values.push(flatValue);
              }
            } else {
              if (value === "_all_") {
                ele.values = ele.values.includes("_all_") ? [] : ["_all_"];
              } else {
                ele.values = finalValue;
              }
            }
          } else if (filter.condition === "CONTAINS") {
            if (value === "_all_") {
              ele.values = ele.values.includes("_all_") ? [] : ["_all_"];
            } else {
              ele.values = finalValue;
            }
          } else if (filter.condition === "EQUALS") {
            if (value === "_all_") {
              ele.values = ele.values.includes("_all_") ? [] : ["_all_"];
            } else {
              ele.values = finalValue;
            }
            ele.dataId = uuidv4();
          }
          ele.dateValuesType = filter.dateValuesType;
        }
        return { ...ele };
      });
    });
  }
};

export const getFormat = (value) => {
  if (value.toLowerCase()?.includes("date")) {
    if (value.toLowerCase()?.includes("time")) {
      return "YYYY-MM-DD HH:mm:ss.S";
    } else {
      return "YYYY-MM-DD";
    }
  }
};

export const getHCRParaTypeFormat = (value) => {
  if (!value) return null;

  const paraTypeFormats = {
    [hcrParaDate]: "YYYY-MM-DD",
    [hcrParaDateAndTime]: "YYYY-MM-DD HH:mm:ss.S",
    [hcrParaDateRange]: "YYYY-MM-DD",
    [hcrParaDateAndTimeRange]: "YYYY-MM-DD HH:mm:ss.S",
  };

  if (value in paraTypeFormats) {
    return {
      displayFormat: paraTypeFormats[value],
      valueFormat: paraTypeFormats[value]
    }
  }

  return null
}

export const handleParaFilterType = ({ canvasValues, dispatch, id, value }) => {
  let displayFormat = canvasValues.displayFormat,
    valueFormat = canvasValues.valueFormat;

  const displayAndValueFormat = getHCRParaTypeFormat(value)
  if (displayAndValueFormat) {
    displayFormat = displayAndValueFormat.displayFormat;
    valueFormat = displayAndValueFormat.valueFormat;
  }

  dispatch(
    hcrActions.handleEditingDsPaneItem({
      dataSourcePane: hcrDSParameter,
      itemId: id,
      key: "canvasValues",
      value: {
        ...canvasValues,
        filterType: value,
        multipleType: value === hcrParaQueryBasedDropdownList ? true : false,
        displayFormat,
        valueFormat,
      },
      isFilterTypeChanged: true,
    }),
  );
};

export const hcrSidebarPanes = [
  {
    title: (
      <TutorialInfo elementKey="hcr-datasource" placement="topLeft">
        <div className="hcr-left-tab">
          <HIIcon name={"hi-hcr-datasource"} className="hcr-tab-icon" />
          <div className="hcr-tabname">DATASOURCE</div>
        </div>
      </TutorialInfo>
    ),
    key: "datasource",
  },
  {
    title: (
      <div className="hcr-left-tab" data-testid="hcr-canvas-tab-wrapper">
        <Image
          height="10%"
          width="20px"
          preview={false}
          src="images/hi-cannedReports/Elements.png"
          className="hcr-tab-icon"
        />
        {/* <HIIcon name={"hi-hcr-datasource"} className="hcr-tab-icon" /> */}
        <div className="hcr-tabname">CANVAS</div>
      </div>
    ),
    key: "canvas",
  },
  {
    title: (
      <TutorialInfo elementKey="hcr-reports">
        <div className="hcr-left-tab">
          <AreaChartOutlined className="hcr-tab-icon" />
          <div className="hcr-tabname">REPORTS</div>
        </div>
      </TutorialInfo>
    ),
    key: "reports",
  },
];

export const handleDeletingGroup = (reqPane) => {
  let reqOtn;
  reqPane.canvasProperties.groupProperties.options =
    reqPane.canvasProperties.groupProperties.options.filter((ele) => {
      if (ele.id === reqPane.canvasProperties.groupProperties.selectGroup) {
        reqOtn = ele;
        return false;
      }
      return true;
    });
  reqPane.canvasProperties.groupProperties.selectGroup = "";
  reqPane.canvasProperties.groupProperties.keyValuePairs = {};
  if (reqOtn) {
    reqPane.hcrDiagramNodesData = reqPane.hcrDiagramNodesData.map((node) => {
      if (node.repeat === `gp${reqOtn.id}`) {
        node.repeat = "na";
        node.groupBy = "";
      }
      if (node.id === reqOtn.nodeId) {
        node.isGrp = "";
        node.groupId = "";
        node.groupName = "";
      }
      return { ...node };
    });
  }
  if (["test"].includes(process.env.NODE_ENV)) {
    return reqPane;
  }
};

export const removeCalculation = ({ reqPane, newId = uuidv4() }) => {
  reqPane.canvasProperties.calculations.options =
    reqPane.canvasProperties.calculations.options.filter((ele) => {
      return ele.id !== reqPane.canvasProperties.calculations.keyValuePairs.id;
    });
  reqPane.canvasProperties.calculations.keyValuePairs = { id: newId };
  reqPane.canvasProperties.calculations.selectCalculation = "";
  if (["test"].includes(process.env.NODE_ENV)) {
    return reqPane;
  }
};

export const getFilterParams = ({ parametersList, filters }) => {
  const reqParamsList = parametersList.map((ele) => ({ ...ele }));
  filters.forEach((fil) => {
    const reqPara = reqParamsList.find((ele) => ele.id === fil.uid);
    if (fil.condition === "IS_ONE_OF") {
      reqPara.canvasValues.filterType = hcrParaQueryBasedDropdownList;
      reqPara.canvasValues.defaultValue = fil.values.join(",");
    } else if (fil.condition === "CONTAINS") {
      reqPara.canvasValues.filterType = hcrParaInput;
      reqPara.canvasValues.defaultValue = fil.values[0];
    } else if (fil.condition === "EQUALS") {
      if (fil.backendDataType === "java.sql.Date") {
        reqPara.canvasValues.filterType = hcrParaDate;
      } else {
        reqPara.canvasValues.filterType = hcrParaDateAndTime;
      }
      reqPara.canvasValues.defaultValue = fil.values[0];
      reqPara.canvasValues.valueFormat = fil.valueFormat;
      reqPara.canvasValues.displayFormat = fil.displayFormat;
    } else if (fil.condition === "IN_RANGE") {
      if (fil.backendDataType === "java.sql.Date") {
        reqPara.canvasValues.filterType = hcrParaDateRange;
      } else {
        reqPara.canvasValues.filterType = hcrParaDateAndTimeRange;
      }
      reqPara.canvasValues.defaultValue = fil.values.join(",");
      reqPara.canvasValues.valueFormat = fil.valueFormat;
      reqPara.canvasValues.displayFormat = fil.displayFormat;
    }
  });
  return reqParamsList;
};

// export const getHcrParameterFilters = ({parameters, hcrDiagramNodesData}) => {
//     const paraArr = [];
//         parameters?.menu?.forEach(para => {
//             // if(!para.canvasValues.isChecked) {
//                 let backendDataType = '', active = true, columnID, condition, dataType, dateTimeToggle = false, label, loading = false, mode = 'auto', values = [], valuesList = [], valuesMode = 'custom', cascade = {}, className = '';
//                 label = para.name;
//                 className = para.type;
//                 let dateProps = {};
//                 backendDataType = hcrDiagramNodesData?.find((ele, i) => {
//                     return ele.name === label
//                 })?.backendDataType;
//                 if (para.canvasValues.filterType === hcrParaQueryBasedDropdownList) {
//                     condition = "IS_ONE_OF";
//                     valuesList = para.executeQueryData?.data.map(item => {
//                         return { display: item[para.canvasValues.display], value: item[para.canvasValues.value] };
//                     })
//                     values = para.canvasValues.defaultValue?.split(',')?.map(ele => ele.trim()) || [];
//                     if(!backendDataType) {
//                         backendDataType = "java.lang.String";
//                     }
//                 } else if (para.canvasValues.filterType === hcrParaInput) {
//                     condition = "CONTAINS";
//                     values = para.canvasValues.defaultValue ? [para.canvasValues.defaultValue] : [];
//                     if(!backendDataType) {
//                         backendDataType = "java.lang.String";
//                     }
//                 } else if (para.canvasValues.filterType === hcrParaDate) {
//                     condition = "EQUALS";
//                     dateProps = {
//                         anchor: defaultAnchor(),
//                         currentDate: (new Date()).toString(),
//                         datePart: "individual",
//                         dateValuesType: "datePicker",
//                         values: para.canvasValues.defaultValue?.split(',')?.map(ele => ele.trim()) || [moment().format(para.canvasValues.displayFormat)],
//                         valueFormat: para.canvasValues.valueFormat,
//                         displayFormat: para.canvasValues.displayFormat,
//                     }
//                     if(!backendDataType) {
//                         backendDataType = "java.sql.Date";
//                     }
//                 } else if (para.canvasValues.filterType === hcrParaDateAndTime) {
//                     condition = "EQUALS";
//                     dateProps = {
//                         anchor: defaultAnchor(),
//                         currentDate: (new Date()).toString(),
//                         datePart: "individual",
//                         dateValuesType: "datePicker",
//                         values: para.canvasValues.defaultValue?.split(',')?.map(ele => ele.trim()) || [moment().format(para.canvasValues.displayFormat)],
//                         valueFormat: para.canvasValues.valueFormat,
//                         displayFormat: para.canvasValues.displayFormat,
//                     }
//                     if(!backendDataType) {
//                         backendDataType = "java.sql.Timestamp";
//                     }
//                 } else if (para.canvasValues.filterType === hcrParaDateRange) {
//                     condition = "IN_RANGE";
//                     dateProps = {
//                         anchor: defaultAnchor(),
//                         currentDate: (new Date()).toString(),
//                         datePart: "individual",
//                         dateValuesType: "datePicker",
//                         values: para.canvasValues.defaultValue?.split(',')?.map(ele => ele.trim()) || [moment().format(para.canvasValues.displayFormat), moment().format(para.canvasValues.displayFormat)],
//                         range: [para.canvasValues.start, para.canvasValues.end],
//                         valueFormat: para.canvasValues.valueFormat,
//                         displayFormat: para.canvasValues.displayFormat,
//                     }
//                     if(!backendDataType) {
//                         backendDataType = "java.sql.Date";
//                     }
//                 } else if (para.canvasValues.filterType === hcrParaDateAndTimeRange) {
//                     condition = "IN_RANGE";
//                     dateProps = {
//                         anchor: defaultAnchor(),
//                         currentDate: (new Date()).toString(),
//                         datePart: "individual",
//                         dateValuesType: "datePicker",
//                         values: para.canvasValues.defaultValue?.split(',')?.map(ele => ele.trim()) || [moment().format(para.canvasValues.displayFormat), moment().format(para.canvasValues.displayFormat)],
//                         range: [para.canvasValues.start, para.canvasValues.end],
//                         valueFormat: para.canvasValues.valueFormat,
//                         displayFormat: para.canvasValues.displayFormat,
//                     }
//                     if(!backendDataType) {
//                         backendDataType = "java.sql.Timestamp";
//                     }
//                 }
//                 if (backendDataType.toLowerCase().includes('string')) {
//                     dataType = 'text';
//                 } else if (backendDataType.toLowerCase().includes('integer')) {
//                     dataType = 'numeric';
//                 } else if (backendDataType.toLowerCase().includes('date')) {
//                     dataType = 'date';
//                 } else if (backendDataType.toLowerCase().includes('time')) {
//                     dataType = "dateTime";
//                 }

//                 const obj = {
//                     active,
//                     backendDataType,
//                     isMultiple: para.canvasValues.multipleType,
//                     condition,
//                     dataType,
//                     dateTimeToggle,
//                     label,
//                     loading,
//                     mode,
//                     uid: para.id,
//                     values,
//                     valuesList,
//                     valuesMode,
//                     cascade,
//                     className,
//                     orgPara: para,
//                     show: !para.canvasValues.isChecked,
//                     ...dateProps,
//                 }
//                 paraArr.push(obj);
//         })

//     return paraArr;
// }

///////////////////////////////////////////////////

export const getHcrParameterFilters = ({ parameters, hcrDiagramNodesData }) => {
  let paraArr = [];
  let filterParameters = {};
  parameters?.menu?.forEach((para) => {
    const { canvasValues } = para;
    let backendDataType = "",
      active = true,
      columnID,
      condition,
      dataType,
      dateTimeToggle = false,
      label = para.name,
      loading = false,
      mode = "auto",
      values = [],
      valuesList = [],
      valuesMode = "custom",
      cascade = {},
      className = para.type;

    let dateProps = {};
    backendDataType = hcrDiagramNodesData?.find(
      (ele) => ele.name === label,
    )?.backendDataType;

    if (
      canvasValues.filterType === hcrParaDateRange ||
      canvasValues.filterType === hcrParaDateAndTimeRange
    ) {
      const startParam = parameters.menu.find(
        (p) => p.name === canvasValues.start,
      );
      const endParam = parameters.menu.find((p) => p.name === canvasValues.end);
      condition = "IN_RANGE";
      values = [
        startParam?.canvasValues?.defaultValue ||
        moment().format(canvasValues.displayFormat),
        endParam?.canvasValues?.defaultValue ||
        moment().format(canvasValues.displayFormat),
      ];
      dateProps = {
        anchor: defaultAnchor(),
        currentDate: new Date().toString(),
        datePart: "individual",
        dateValuesType: "datePicker",
        values,
        range: [canvasValues.start, canvasValues.end],
        valueFormat: canvasValues.valueFormat,
        displayFormat: canvasValues.displayFormat,
        defaultValue: {
          start: startParam?.canvasValues?.defaultValue || "",
          end: endParam?.canvasValues?.defaultValue || "",
        },
      };
      backendDataType =
        backendDataType ||
        (canvasValues.filterType === hcrParaDateAndTimeRange
          ? "java.sql.Timestamp"
          : "java.sql.Date");
    } else if (canvasValues.filterType === hcrParaQueryBasedDropdownList) {
      condition = "IS_ONE_OF";
      // valuesList = para.executeQueryData?.data.map((item) => ({
      //     display: item[canvasValues.display],
      //     value: item[canvasValues.value],
      // }));
      if (!canvasValues?.multipleType) {
        condition = "EQUALS";
      }
      values =
        canvasValues.defaultValue?.split(",")?.map((ele) => ele.trim()) || [];
      backendDataType = backendDataType || "java.lang.String";
    } else if (canvasValues.filterType === hcrParaInput) {
      condition = "CONTAINS";
      values = canvasValues.defaultValue ? [canvasValues.defaultValue] : [];
      backendDataType = backendDataType || "java.lang.String";
    } else if (canvasValues.filterType === hcrParaDate) {
      condition = "EQUALS";
      const format = "YYYY-MM-DD";
      values =
        canvasValues.defaultValue?.split(",")?.map((ele) => ele.trim()) || [];
      dateProps = {
        anchor: defaultAnchor(),
        currentDate: new Date().toString(),
        datePart: "individual",
        dateValuesType: "datePicker",
        values,
        valueFormat: format,
        displayFormat: format,
      };
      backendDataType = backendDataType || "java.sql.Date";
    } else if (canvasValues.filterType === hcrParaDateAndTime) {
      condition = "EQUALS";
      const format = "YYYY-MM-DD HH:mm:ss";
      values = canvasValues.defaultValue
        ?.split(",")
        ?.map((ele) => ele.trim()) || [
          moment().format(canvasValues.displayFormat),
        ];
      dateProps = {
        anchor: defaultAnchor(),
        currentDate: new Date().toString(),
        datePart: "individual",
        dateValuesType: "datePicker",
        values,
        valueFormat: format,
        displayFormat: format,
      };
      backendDataType = backendDataType || "java.sql.Timestamp";
    }

    if (
      backendDataType.toLowerCase().includes("string") ||
      backendDataType.toLowerCase().includes("boolean")
    ) {
      dataType = "text";
    } else if (
      backendDataType.toLowerCase().includes("integer") ||
      backendDataType.toLowerCase().includes("float")
    ) {
      dataType = "numeric";
    } else if (backendDataType.toLowerCase().includes("date")) {
      dataType = "date";
    } else if (backendDataType.toLowerCase().includes("time")) {
      dataType = "dateTime";
    }

    const obj = {
      active,
      backendDataType,
      isMultiple: canvasValues.multipleType,
      condition,
      dataType,
      dateTimeToggle,
      label,
      loading,
      mode,
      uid: para.id,
      values,
      valuesList,
      valuesMode,
      cascade,
      className,
      orgPara: para,
      show: !canvasValues.isChecked,
      configId: uuidv4(),
      ...dateProps,
    };
    filterParameters[label] = values;
    paraArr.push(obj);
  });
  paraArr = checkRelativeDateFilter(paraArr, filterParameters);

  return paraArr;
};

export const getQueryFormdata = ({
  connectionDetails,
  reqQuery,
  paraList: dsPaneParas,
}) => {
  const connDetails = {};
  if (connectionDetails.type === "dynamicDataSource") {
    connDetails.globalId = connectionDetails.id;
  } else {
    connDetails.efwdId = connectionDetails.id;
  }

  return {
    name: "_temp_filename",
    version: 5,
    efwd: {
      dataSources: {
        connections: [
          {
            connection: {
              id: connectionDetails?.id,
              type: connectionDetails?.baseType,
              connDetails: {
                ...connDetails,
              },
            },
          },
        ],
      },
      dataMaps: [
        {
          dataMap: {
            name: reqQuery.name,
            id: reqQuery.id,
            connection: connectionDetails?.id,
            type: ["sql.jdbc.groovy", "sql.jdbc.groovy.managed"].includes(
              connectionDetails?.baseType,
            )
              ? "sql.groovy"
              : "sql",
            query: reqQuery.config.trim(),
            parameters: reqQuery.parameterList.map((parameter) => {
              const dsPara =
                dsPaneParas.find((dspar) => {
                  return dspar.id === parameter.id;
                }) || {};
              let paraType = "";
              let paraDefault = parameter.value;
              let paraValues = { default: parameter.value };
              if (parameter.type.toLowerCase().includes("collection")) {
                paraType = "Collection";
              } else if (parameter.type.toLowerCase().includes("string")) {
                paraType = "String";
                // paraValues.openQuote =  "'";
                // paraValues.closeQuote =  "%'";
              } else if (parameter.type.toLowerCase().includes("integer")) {
                paraType = "Numeric";
                paraValues.default = parseInt(paraDefault);
              }
              return {
                parameter: {
                  ...paraValues,
                  name: parameter.name,
                  type: paraType,
                  openQuote: dsPara.canvasValues?.open,
                  closeQuote: dsPara.canvasValues?.close,
                },
              };
            }),
          },
        },
      ],
    },
  };
};

export const handleRunQuery = ({
  reqQuery,
  isEditedFile = false,
  dispatch,
  selectedDS,
  saveQueryReportState,
  saveExecuteReportQuery,
  Notify,
  paraList = [],
  getApiInstance = () => { },
  resetQueryuuids = () => { }
}) => {
  const connectionDetails = reqQuery?.connectionDetails;

  const formData = getQueryFormdata({
    connectionDetails,
    reqQuery,
    paraList,
  });
  dispatch(hcrActions.setHcrQueryRunning(true));

  const saveInstance = saveQueryReportState(
    formData,
    (res) => {
      resetQueryuuids();
      dispatch(
        hcrActions.handleEditingDsPaneItem({
          dataSourcePane: selectedDS.dataSourcePane,
          itemId: reqQuery.id,
          value: res.temp_uuid,
          key: "temp_uuid",
        }),
      );
      const executeQueryFormData = {
        mapJson: {
          map_id: 1,
          type: connectionDetails?.baseType,
          temp_uuid: res.temp_uuid,
        },
      };
      formData?.efwd?.dataMaps[0]?.dataMap?.parameters?.forEach((obj) => {
        executeQueryFormData.mapJson[obj.parameter.name] = obj.parameter?.type
          .toLowerCase()
          ?.includes("collection")
          ? obj.parameter.default?.split(",").map((ele) => ele.trim())
          : obj.parameter.default;
      });
      const runInstance = saveExecuteReportQuery(
        executeQueryFormData,
        (exeQryRes) => {
          dispatch(
            hcrActions.handleEditingDsPaneItem({
              dataSourcePane: selectedDS.dataSourcePane,
              itemId: reqQuery.id,
              value: exeQryRes,
              key: "executeQueryData",
            }),
          );
          if (selectedDS.dataSourcePane === hcrDSQuery) {
            updateDataSets(dispatch, reqQuery.id, exeQryRes);
          }
          dispatch(hcrActions.setHcrQueryRunning(false));
          Notify.success({ message: "Query Execution Success" });
        },
        (exeQryErr) => {
          dispatch(hcrActions.setHcrQueryRunning(false));
          console.log(exeQryErr);
        },
      );
      getApiInstance(runInstance);
    },
    (err) => {
      dispatch(hcrActions.setHcrQueryRunning(false));
      console.log(err);
    },
  );
  getApiInstance(saveInstance);
};

// export const getReqNodeData = (data) => {
//     let {...rest} = data;
//     delete rest.popoverContent;
//     delete rest.isCustom;
//     delete rest.ports;
//     rest = {...rest, isLeaf: rest.isLeaf || rest.originData?.isLeaf, category: rest.category || rest.originData?.category, type: rest.type || rest.originData?.type};
//     delete rest.originData;
//     if(!rest.repeat) {
//         rest.repeat = 'na';
//     }
//     return rest;
// }

function updateDataSets(dispatch, queryId, queryResponse) {
  let activeTab = {}
  dispatch((_, getState) => {
    const state = getState()
    activeTab = state.cannedReports.present.hcrTabData.panes.find(
      (pane) => pane.key === state.cannedReports.present.hcrTabData.activeKey
    )
  })
  if (!isEmpty(activeTab)) {
    const { hcrDiagramNodesData = [], subDataSets = [] } = activeTab || {};

    if (subDataSets.length) {
      const subDataSet = subDataSets.find((subDataSet) => subDataSet.id === queryId);
      const advancedTables = hcrDiagramNodesData?.filter((node) => node.category === "advancedTable") || [];
      if (subDataSet) {
        const { id: subDSId } = subDataSet || {};
        const { field = [] } = queryResponse || {};
        const propertiesToUpdate = [
          { key: "fields", value: field.map((field) => ({ ...field, id: uuidv4() })) },
          // { key: "groups", value: [] }
        ]
        dispatch(hcrActions.hcrUpdateSubdataSets({
          actionType: "updateMany",
          id: subDSId,
          propertiesToUpdate,
          reportKey: activeTab.key
        }))

        // if (advancedTables.length) {
        //   const tablesUsingSubDataSet = advancedTables.filter((table) => table.selectedQueryID === subDSId) || [];
        //   if (tablesUsingSubDataSet.length) {
        //     const tableIds = tablesUsingSubDataSet.map((table) => table.id);
        //     tableIds.forEach((id) => {
        //       dispatch(hcrActions.hcrUpdateCanvasTabComponent({
        //         actionType: "deleteOldGroups",
        //         id,
        //       }))
        //     })
        //   }
        // }
      }
    }
  }
}

export const getReqNodeData = (data) => {
  let { __innerHTML, ...rest } = data;

  delete rest.popoverContent;
  delete rest.isCustom;
  delete rest.ports;

  rest = {
    ...rest,
    __innerHTML,
    isLeaf: rest.isLeaf || rest.originData?.isLeaf,
    category: rest.category || rest.originData?.category,
    type: rest.type || rest.originData?.type,
  };

  delete rest.originData;

  if (!rest.repeat) {
    rest.repeat = "na";
  }

  return rest;
};

export default function waitForForeignObject(nodeId, callback, attempt = 0) {
  const maxTries = 60;
  const el = document.querySelector(
    `g[data-cell-id="${nodeId}"] foreignObject`,
  );
  if (el) {
    callback(el);
  } else if (attempt < maxTries) {
    requestAnimationFrame(() =>
      waitForForeignObject(nodeId, callback, attempt + 1),
    );
  } else {
    console.warn(`foreignObject not found for ${nodeId}`);
  }
}

export const checkMiddleNodes = (repeat, limits, repeatTypesToCheck) => {
  return repeatTypesToCheck[repeat]?.find((ele) => {
    let isNodePresent = false;
    if (ele === "rd" || ele === "gp") {
      isNodePresent = limits[ele].node;
    } else {
      isNodePresent = limits[ele].header.node || limits[ele].footer.node;
    }
    return isNodePresent;
  });
};

const headerFooterLogic = ({
  repeatType,
  limits,
  node,
  repeatTypesToCheck,
  isIndividualGroup,
}) => {
  let isMiddleElePresent;
  if (isIndividualGroup) {
    isMiddleElePresent = repeatTypesToCheck[repeatType]?.find((ele) => {
      if (
        limits[ele].header.node &&
        limits[ele].header.upper > limits[repeatType].header.lower &&
        limits[ele].header.lower < node.y
      ) {
        return true;
      }
      return false;
    });
    if (!isMiddleElePresent) {
      repeatTypesToCheck[repeatType]?.find((ele) => {
        if (
          limits[ele].footer.node &&
          limits[ele].footer.upper > node.y + node.height &&
          limits[ele].footer.lower < limits[repeatType].footer.upper
        ) {
          return true;
        }
        return false;
      });
    }
    if (!isMiddleElePresent) {
      if (
        limits.rd.node &&
        limits.rd.lower > limits[repeatType].header.lower &&
        limits.rd.lower < node.y
      ) {
        isMiddleElePresent = true;
      }
    }
  } else {
    if (node[repeatType]) {
      isMiddleElePresent = node[repeatType] === "footer";
    } else {
      isMiddleElePresent = checkMiddleNodes(
        repeatType,
        limits,
        repeatTypesToCheck,
      );
    }
  }
  if (isMiddleElePresent) {
    // if middle nodes present
    if (!limits[repeatType].footer.node) {
      // if footer not there
      limits[repeatType].footer.node = node;
      limits[repeatType].footer.upper = node.y;
      limits[repeatType].footer.lower = node.y + node.height;
    } else {
      // if footer there
      if (node.y < limits[repeatType].footer.upper) {
        limits[repeatType].footer.upper = node.y;
        limits[repeatType].footer.node = node;
      }
      if (node.y + node.height > limits[repeatType].footer.lower) {
        limits[repeatType].footer.lower = node.y + node.height;
        limits[repeatType].footer.node = node;
      }
    }
  } else {
    // if middle nodes not present
    if (node.y + node.height > limits[repeatType].header.lower) {
      limits[repeatType].header.node = node;
      limits[repeatType].header.lower = node.y + node.height;
    }
    if (node.y < limits[repeatType].header.upper) {
      limits[repeatType].header.node = node;
      limits[repeatType].header.upper = node.y;
    }
  }
};

export const validateNodes = ({
  flowchartInstance,
  Notify,
  hcrDiagramNodesData,
  canvasProperties = {},
}) => {
  let nodes;
  if (hcrDiagramNodesData?.length) {
    nodes = hcrDiagramNodesData.map((ele) => ({ ...ele }));
  } else {
    nodes =
      flowchartInstance?.current?.getNodes().map((shape) => {
        return getReqNodeData(shape.store.data.data);
      }) || [];
  }
  nodes = nodes.sort((a, b) => a.y - b.y);
  nodes = nodes.filter((node) => !node.isTableCell && !node.isCrosstabCell);
  const { layout = {}, groupProperties = {} } = canvasProperties;
  let pageHeight;
  if (["landscape"].includes(layout?.orientation?.toLowerCase())) {
    pageHeight =
      flowchartInstance?.current?.getArea().width || layout?.size.width;
  } else {
    pageHeight =
      flowchartInstance?.current?.getArea().height || layout?.size.height;
  }
  let limits = {
    rt: {
      header: {
        lower: 0,
        node: null,
        upper: pageHeight,
      },
      footer: {
        upper: pageHeight,
        node: null,
      },
    },
    pg: {
      header: {
        lower: 0,
        node: null,
        upper: pageHeight,
      },
      footer: {
        upper: pageHeight,
        node: null,
        lower: 0,
      },
    },
    cl: {
      header: {
        lower: 0,
        node: null,
        upper: pageHeight,
      },
      footer: {
        upper: pageHeight,
        node: null,
        lower: 0,
      },
    },
    gp: {
      upper: pageHeight,
      lower: 0,
      node: null,
    },
    rd: {
      upper: pageHeight,
      lower: 0,
      node: null,
    },
    lpf: {
      upper: pageHeight,
      lower: 0,
      node: null,
    },
    nd: {
      upper: pageHeight,
      lower: 0,
      node: null,
    },
  };
  const nodesToBeHighlighted = [],
    noRepeatTypeNodes = [],
    groupIds = [],
    grpRdNodes = [],
    grpOrder = [];
  const repeatTypesToCheck = {
    rt: ["pg", "cl", "gp", "rd"],
    pg: ["cl", "gp", "rd"],
    cl: ["gp", "rd"],
    gp: ["rd"],
  };

  groupProperties.options?.forEach((opt) => {
    groupIds.push(opt.id);
    limits[`gp${opt.id}`] = {
      header: {
        lower: 0,
        node: null,
        upper: pageHeight,
      },
      footer: {
        upper: pageHeight,
        node: null,
        lower: 0,
      },
    };
  });

  nodes.forEach((node, index) => {
    let rectElement;
    if (!["test"].includes(process.env.NODE_ENV)) {
      rectElement = document?.querySelector(`g[data-cell-id=${node.id}] rect`);
      rectElement?.classList.remove("inValid-node");
    }
    if (node.repeat && node.repeat !== "na") {
      if (node.repeat.includes("gp")) {
        grpRdNodes.push(node);
        headerFooterLogic({
          repeatType: node.repeat,
          limits,
          node,
          repeatTypesToCheck: { [node.repeat]: ["rd"] },
        });
      }
      const repeatType = node.repeat.includes("gp") ? "gp" : node.repeat;
      if (
        repeatType === "lpf" ||
        repeatType === "nd" ||
        repeatType === "rd" ||
        repeatType === "gp"
      ) {
        if (repeatType === "rd") {
          grpRdNodes.push(node);
        }
        if (node.y < limits[repeatType].upper) {
          limits[repeatType].upper = node.y;
          limits[repeatType].node = node;
        }
        if (node.y + node.height > limits[repeatType].lower) {
          limits[repeatType].lower = node.y + node.height;
          limits[repeatType].node = node;
        }
      } else {
        // not lpf, nd, rd, gp
        // if(!limits[repeatType].header.node) {
        //     limits[repeatType].header.node = node;
        //     limits[repeatType].header.lower = node.y+node.height;
        //     limits[repeatType].header.upper = node.y;
        // } else {
        headerFooterLogic({
          repeatType,
          limits,
          node,
          repeatTypesToCheck,
        });
        // }
      }
    } else {
      // no repeat present
      noRepeatTypeNodes.push({ id: node.id, label: node.label });
    }
  });

  if (noRepeatTypeNodes.length) {
    // repeat type not mentioned
    if (["test"].includes(process.env.NODE_ENV)) {
      return { isValid: false };
    }
    noRepeatTypeNodes.forEach((node) => {
      const rectElement = document.querySelector(
        `g[data-cell-id=${node.id}] rect`,
      );
      rectElement?.classList.add("inValid-node");
      Notify.error({
        type: "error",
        message: `Repeat Type should be specified for the above highlighted component(s) ${node.label}`,
      });
    });
    return { isValid: false };
  }

  grpRdNodes.forEach((ele) => {
    if (!grpOrder.includes(ele.repeat) && ele.repeat !== "rd") {
      grpOrder.push(ele.repeat);
    }
  });

  const hierarchyToBeCheck = {
    rt: [],
    pg: ["rt"],
    cl: ["rt", "pg"],
    // gp: ['rt', 'pg', 'cl'],
    rd: ["rt", "pg", "cl", ...(grpOrder || [])],
    lpf: ["rt", "pg", "cl", "gp", "rd"],
    nd: ["rt", "pg", "cl", "gp", "rd", "lpf"],
  };

  grpOrder.forEach((ele, i) => {
    hierarchyToBeCheck[ele] = [
      "rt",
      "pg",
      "cl",
      ...(grpOrder.slice(0, i) || []),
    ];
  });

  nodes.forEach((ele) => {
    const repeatType = false && ele.repeat.includes("gp") ? "gp" : ele.repeat;
    hierarchyToBeCheck[repeatType].find((aboveHeierar) => {
      if (ele.repeat !== "lpf" && ele.repeat !== "nd") {
        if (
          limits[aboveHeierar].header.node &&
          limits[aboveHeierar].header.lower > ele.y
        ) {
          nodesToBeHighlighted.push({ id: ele.id, label: ele.label });
          return true;
        }
        if (
          limits[aboveHeierar].footer.node &&
          limits[aboveHeierar].footer.upper < ele.y + ele.height
        ) {
          nodesToBeHighlighted.push({ id: ele.id, label: ele.label });
          return true;
        }
      } else {
        if (
          aboveHeierar === "rd" ||
          aboveHeierar === "lpf" ||
          aboveHeierar === "gp"
        ) {
          if (limits[aboveHeierar].node && limits[aboveHeierar].lower > ele.y) {
            nodesToBeHighlighted.push({
              id: ele.id,
              label: ele.label,
            });
            return true;
          }
        } else {
          if (
            (limits[aboveHeierar]?.footer?.node &&
              limits[aboveHeierar]?.footer?.lower > ele.y) ||
            (limits[aboveHeierar].header.node &&
              limits[aboveHeierar].header.lower > ele.y)
          ) {
            nodesToBeHighlighted.push({
              id: ele.id,
              label: ele.label,
            });
            return true;
          }
        }
        return false;
      }
    });
  });

  // Groups logic
  // if(groupIds.length && grpRdNodes.length) {
  //     const grpLimits = { rd: {
  //         upper: pageHeight,
  //         lower: 0,
  //         node: null
  //     }};
  //     const grpHierarchyToBeCheck = {};
  //     const grpOrder = [];
  //     groupIds.forEach(grpId => {
  //         grpLimits[`gp${grpId}`] =  {
  //             header: {
  //                 lower: 0,
  //                 node: null,
  //                 upper: pageHeight,
  //             },
  //             footer: {
  //                 upper: pageHeight,
  //                 node: null,
  //                 lower: 0,
  //             }
  //         };
  //     });
  //     grpRdNodes.forEach(ele => {
  //         if(!grpOrder.includes(ele.repeat) && ele.repeat !== 'rd') {
  //             grpOrder.push(ele.repeat);
  //         }
  //     })
  //     grpHierarchyToBeCheck.rd = [...grpOrder];
  //     grpOrder.forEach((grp, i) => {
  //             grpHierarchyToBeCheck[grp] = [...grpOrder.slice(1+i)];
  //     })

  //     grpRdNodes.forEach(grpRdNode => {
  //         const repeatType = grpRdNode.repeat;
  //         if(grpRdNode.repeat.includes('gp')) {
  //             // if(!grpLimits[repeatType].header.node) {
  //             //     grpLimits[repeatType].header.node = grpRdNode;
  //             //     grpLimits[repeatType].header.lower = grpRdNode.y+grpRdNode.height;
  //             //     grpLimits[repeatType].header.upper = grpRdNode.y;
  //             // } else {
  //                 headerFooterLogic({repeatType, limits: grpLimits, node: grpRdNode, repeatTypesToCheck: ['rd'], isIndividualGroup: true})
  //             // }
  //         } else {
  //             if(grpRdNode.y < grpLimits[grpRdNode.repeat].upper) {
  //                 grpLimits[grpRdNode.repeat].upper = grpRdNode.y;
  //                 grpLimits[grpRdNode.repeat].node = grpRdNode;
  //             }
  //             if(grpRdNode.y+grpRdNode.height > grpLimits[grpRdNode.repeat].lower) {
  //                 grpLimits[grpRdNode.repeat].lower = grpRdNode.y+grpRdNode.height;
  //                 grpLimits[grpRdNode.repeat].node = grpRdNode;
  //             }
  //         }
  //     })

  //     grpOrder.forEach(grp => {
  //         if(grpLimits[grp].header.lower === grpLimits[grp].footer.upper) {
  //             grpLimits[grp].footer = {
  //                 upper: pageHeight,
  //                 node: null,
  //                 lower: 0
  //             }
  //         }
  //     })

  //     grpRdNodes.forEach(ele => {
  //         const repeatType = ele.repeat;
  //         if(ele.repeat !== 'rd') {
  //             grpHierarchyToBeCheck[repeatType]?.find(aboveHeierar => {
  //                 if((ele.y+ele.height) <= grpLimits[repeatType].header.lower) { // ele is in header
  //                     if((ele.y+ele.height) >= grpLimits[aboveHeierar].header.upper) {
  //                         nodesToBeHighlighted.push({id: ele.id, label: ele.label})
  //                         return true;
  //                     }
  //                 } else {
  //                     if((grpLimits[aboveHeierar]?.footer?.node && (ele.y <= grpLimits[aboveHeierar]?.footer?.lower)) || (ele.y <= grpLimits[aboveHeierar]?.header.lower)) {
  //                         nodesToBeHighlighted.push({id: ele.id, label: ele.label})
  //                         return true;
  //                     }
  //                 }
  //                 return false;
  //             })
  //         } else {
  //              // is rd is inside 1st grp
  //             if((grpLimits[grpOrder[0]]?.header.upper >= ele.y) || (((ele.y+ele.height) >= grpLimits[grpOrder[0]]?.footer?.lower) && (grpLimits[grpOrder[0]]?.footer?.node))) {
  //                 nodesToBeHighlighted.push({id: ele.id, label: ele.label})
  //             } else {
  //                 grpHierarchyToBeCheck[repeatType]?.find(aboveHeierar => {
  //                     if(
  //                         ((grpLimits[aboveHeierar]?.header.upper <= ele.y) && (grpLimits[aboveHeierar]?.header.lower >= ele.y)) ||
  //                         ((grpLimits[aboveHeierar]?.header.upper <= (ele.y+ele.height)) && (grpLimits[aboveHeierar]?.header.lower >= (ele.y+ele.height))) ||
  //                          ((grpLimits[aboveHeierar]?.footer?.node) && (grpLimits[aboveHeierar]?.footer?.upper <= (ele.y+ele.height)) && (grpLimits[aboveHeierar]?.footer?.lower >= (ele.y+ele.height))) ||
  //                          ((grpLimits[aboveHeierar]?.footer?.node) && (grpLimits[aboveHeierar]?.footer?.upper <= (ele.y)) && (grpLimits[aboveHeierar]?.footer?.lower >= (ele.y)))
  //                         ) {
  //                         nodesToBeHighlighted.push({id: ele.id, label: ele.label})
  //                         return true;
  //                     }
  //                     return false;
  //                 })
  //             }
  //         }
  //     })

  //     if(!nodesToBeHighlighted.length) {
  //         delete grpLimits.rd;
  //         limits =  {...limits, ...grpLimits};
  //     }
  // }

  if (nodesToBeHighlighted.length) {
    if (["test"].includes(process.env.NODE_ENV)) {
      return { isValid: false };
    }
    nodesToBeHighlighted.forEach((node) => {
      const rectElement = document.querySelector(
        `g[data-cell-id=${node.id}] rect`,
      );
      rectElement?.classList.add("inValid-node");
      Notify.error({
        type: "error",
        message: `Please check component placement hierarchy for the highlighted component(s) ${node.label}`,
      });
    });
    return { isValid: false };
  }
  return { isValid: true, bandLimits: limits };
};

export const getFieldsMenu = ({ fields }) => {
  return (
    <Menu selectedKeys={["dummy"]} selectable={true}>
      {fields?.map((field) => {
        return (
          <Menu.Item
            onClick={() => {
              // dispatch(hcrActions.handleEditingDsPaneItem({ dataSourcePane: record.dataSourcePane, itemId, key: reqKey, value: conn.id, setForSql }));
            }}
            key={field.name}
            value={field.name}
          >
            {field.name}
          </Menu.Item>
        );
      })}
    </Menu>
  );
};

const getCopiedNodesFromLocalStorage = () => {
  const copiedNodes = localStorage.getItem("XFLOW_COPY_ITEMS");
  let { nodes = [] } = JSON.parse(copiedNodes) || {};
  return nodes;
};

export const getFlowChartNodes = ({
  flowchartInstance,
  pastingEles = false,
}) => {
  const reqNodes = flowchartInstance?.current?.getNodes() || [];
  let copiedNodes = [];
  if (pastingEles) {
    copiedNodes = getCopiedNodesFromLocalStorage();
  }
  return reqNodes.map((shape) => {
    let nodeData = shape.store.data.data;
    if (
      pastingEles &&
      (nodeData.isTableCell || nodeData.isGroup || nodeData.group)
    ) {
      const originalNode = copiedNodes.find(
        (node) => node.id === nodeData.originalId,
      );
      if (nodeData.group) {
        nodeData.parent = nodeData.group;
      }
      if (originalNode) {
        nodeData = {
          ...nodeData,
          x: originalNode.x + 30,
          y: originalNode.y + 25,
        };
        if (nodeData.isGroup) {
          nodeData = {
            ...nodeData,
            width: originalNode.width,
            height: originalNode.height,
          };
        }
      }
    }
    return getReqNodeData(nodeData);
  });
};

const getCustomProperties = (properties = []) => {
  if (!properties.length) return null;
  return properties.reduce((acc, prop) => {
    acc[prop.key] = prop.value;
    return acc;
  }, {})
}
export const getPreviewTextField = (textField = {}) => {
  const {
    label,
    borders,
    padding,
    width,
    height,
    name,
    renderKey,
    parentKey,
    isLeaf,
    repeat,
    category,
    zIndex,
    type,
    id,
    x,
    y,
    fontFamily,
    fontSize,
    bold,
    italic,
    strikeThrough,
    underLine,
    mode,
    fontFill,
    fill,
    horizontalAlign = "",
    verticalAlign = "",
    position,
    stretch,
    rotation,
    markUp,
    evalTime,
    pattern,
    patternExp,
    printWhenExp,
    printRepeatedValues,
    removeLineWhenBlank,
    printInFirstWholeSection,
    printWhenRecordOverflows,
    stretchWithOverflows,
    blankWhenNull,
    styleName,
    evalGroup,
    properties = []
  } = textField;

  const nodeDetails = {
    textFieldExpression: `${label}`,
    X: x,
    Y: y,
    shapeId: id,
    textHeight: height,
    textWidth: width,
    fontName: fontFamily,
    mode,
    positionType: position,
    textFontSize: fontSize,
    bold,
    // styleNameReference: styleName,
    ...(styleName && styleName !== "None"
      ? { styleNameReference: styleName }
      : {}),
    // horizontalTextAlign: horizontalAlign[0].toUpperCase() + horizontalAlign.slice(1),
    // verticalTextAlign: verticalAlign[0].toUpperCase() + verticalAlign.slice(1),
    textBackcolor: fill,
    textForecolor: fontFill,
    italic,
    underline: underLine,
    strikeThrough,
    removeLineWhenBlank,
    printRepeatedValues,
    markUp,
    stretchType: stretch,
    rotationType: rotation,
    printInFirstWholeBand: printInFirstWholeSection,
    printWhenDetailOverflows: printWhenRecordOverflows,
    // configFontName: ,
    pattern,
    printWhenExpression: printWhenExp,
    stretchWithOverFlow: stretchWithOverflows,
    blankWhenNull,
    // paragraph: {},
    evaluationTime: evalTime,
    patternExpression: patternExp,
    evaluationGroupName: evalGroup
  };

  if (horizontalAlign) {
    nodeDetails.horizontalTextAlign =
      horizontalAlign[0].toUpperCase() + horizontalAlign.slice(1);
  }

  if (verticalAlign) {
    nodeDetails.verticalTextAlign =
      verticalAlign[0].toUpperCase() + verticalAlign.slice(1);
  }

  if (Object.keys(borders).length) {
    nodeDetails.border = {
      line: {
        leftLine: {
          lineWidth: borders.Left.stroke,
          lineColor: borders.Left.color,
          lineStyle: borders.Left.style,
        },
        rightLine: {
          lineWidth: borders.Right.stroke,
          lineColor: borders.Right.color,
          lineStyle: borders.Right.style,
        },
        bottomLine: {
          lineWidth: borders.Bottom.stroke,
          lineColor: borders.Bottom.color,
          lineStyle: borders.Bottom.style,
        },
        topLine: {
          lineWidth: borders.Top.stroke,
          lineColor: borders.Top.color,
          lineStyle: borders.Top.style,
        },
      },
    };
    // if (Object.keys(padding).length) {
    //   let paddingObj = {
    //     bottomPadding: padding.Bottom,
    //     topPadding: padding.Top,
    //     leftPadding: padding.Left,
    //     rightPadding: padding.Right,
    //   };
    //   if (nodeDetails.border) {
    //     nodeDetails.border.padding = paddingObj;
    //   } else {
    //     nodeDetails.border = {
    //       padding: paddingObj,
    //     };
    //   }
    // }
  }
  if (Object.keys(padding).length) {
    let paddingObj = {
      bottomPadding: padding.Bottom,
      topPadding: padding.Top,
      leftPadding: padding.Left,
      rightPadding: padding.Right,
    };
    nodeDetails.padding = paddingObj;
  }
  const customSettings = getCustomProperties(properties);

  if (customSettings) {
    nodeDetails.customSettings = customSettings;
  }

  return nodeDetails;
};

export const getPreviewLine = (line = {}) => {
  const { width, height, id, x, y, lineStyles, properties = [] } = line;

  const nodeDetails = {
    X: x,
    Y: y,
    shapeId: id,
    lineHeight: height,
    lineWidth: width,
    // mode: [
    //     "Opaque",
    //     "Opaque"
    // ],
    penLineWidth: lineStyles.stroke,
    lineStyle: lineStyles.style,
    lineBackcolor: lineStyles.color,
    lineForecolor: lineStyles.color,
    // linePositionType: "Float",
    lineDirection: lineStyles.direction,
    // stretchType: "NoStretch",
    printRepeatedValues: lineStyles.printRepeatedValues,
  };
  const customSettings = getCustomProperties(properties);

  if (customSettings) {
    nodeDetails.customSettings = customSettings;
  }

  return nodeDetails;
};

export const getPreviewImage = (image = {}) => {
  const {
    width,
    height,
    id,
    x,
    y,
    label,
    mode,
    fontFill,
    fill,
    horizontalAlign,
    verticalAlign,
    borders,
    padding,
    fillType,
    scaleImage,
    onErrorType,
    styleName,
    imagePath = "",
    isUsingCache,
    isLazy,
    imageResourceId: initialImageResourceId,
    printWhenExpression,
    properties = [],
    evalTime,
    evalGroup,
    customExpression,
    customExpressionToggle
  } = image;

  const nodeDetails = {
    mode,
    imageBackcolor: fill,
    imageForecolor: fontFill,
    isLazy,
    isUsingCache,
    fill: fillType,
    scaleImage,
    onErrorType,
    dir: "",
    file: "",
    link: "",
    X: x,
    Y: y,
    imageHeight: height,
    imageWidth: width,
    printWhenExpression,
    evaluationTime: evalTime,
    evaluationGroupName: evalGroup,
  };

  if (imagePath?.startsWith("http://") || imagePath?.startsWith("https://")) {
    nodeDetails.link = imagePath;
  } else {
    const path = imagePath?.split("/") || [];
    nodeDetails.file = path.pop();
    nodeDetails.dir = path.join("/");
    if (initialImageResourceId) {
      nodeDetails.imageResourceId = initialImageResourceId;
    }
  }
  if (horizontalAlign) {
    nodeDetails.horizontalImageAlign =
      horizontalAlign[0].toUpperCase() + horizontalAlign.slice(1);
  }
  if (verticalAlign) {
    nodeDetails.verticalImageAlign =
      verticalAlign[0].toUpperCase() + verticalAlign.slice(1);
  }

  if (Object.keys(borders).length) {
    nodeDetails.border = {
      line: {
        leftLine: {
          lineWidth: borders.Left.stroke,
          lineColor: borders.Left.color,
          lineStyle: borders.Left.style,
        },
        rightLine: {
          lineWidth: borders.Right.stroke,
          lineColor: borders.Right.color,
          lineStyle: borders.Right.style,
        },
        bottomLine: {
          lineWidth: borders.Bottom.stroke,
          lineColor: borders.Bottom.color,
          lineStyle: borders.Bottom.style,
        },
        topLine: {
          lineWidth: borders.Top.stroke,
          lineColor: borders.Top.color,
          lineStyle: borders.Top.style,
        },
      },
    };
    // if (Object.keys(padding).length) {
    //   let paddingObj = {
    //     bottomPadding: padding.Bottom,
    //     topPadding: padding.Top,
    //     leftPadding: padding.Left,
    //     rightPadding: padding.Right,
    //   };
    //   if (nodeDetails.border) {
    //     nodeDetails.border.padding = paddingObj;
    //   } else {
    //     nodeDetails.border = {
    //       padding: paddingObj,
    //     };
    //   }
    // }
  }
  if (Object.keys(padding).length) {
    let paddingObj = {
      bottomPadding: padding.Bottom,
      topPadding: padding.Top,
      leftPadding: padding.Left,
      rightPadding: padding.Right,
    };
    nodeDetails.padding = paddingObj;
  }
  const customSettings = getCustomProperties(properties);

  if (customSettings) {
    nodeDetails.customSettings = customSettings;
  }

  if (customExpressionToggle) {
    nodeDetails.expression = customExpression;
    nodeDetails.dir = "";
    nodeDetails.file = "";
    nodeDetails.link = "";
  }

  return nodeDetails;
};

const getPreviewBreak = (pageBreak) => {
  const { width, height, id, x, y, printWhenExp, properties = [] } = pageBreak;

  const nodeDetails = {
    X: x,
    Y: y,
    shapeId: id,
    breakHeight: height,
    breakWidth: width,
    printWhenExpression: printWhenExp,
  };

  const customSettings = getCustomProperties(properties);
  if (customSettings) {
    nodeDetails.customSettings = customSettings;
  }
  return nodeDetails;
};

const getPreviewCrosstab = (crosstab = {}) => {
  const {
    positionType,
    stretchType,
    printWhenExpression,
    printRepeatedValues,
    removeLineWhenBlank,
    printInFirstWholeBand,
    printWhenDetailOverflows,
    stretchWithOverFlow,
    blankWhenNull,
    borders,
    padding,
    x,
    y,
    width,
    height,
    dataSets = [],
    selectedQueryID,
    formdata = {},
    repeatColumnHeaders,
    repeatRowHeaders,
    columnBreakOffset,
    properties = []
  } = crosstab || {};
  const selectedQuery =
    dataSets?.find((ele) => ele.id === selectedQueryID) || {};
  const { name = "", dataSetExpression = "" } = selectedQuery || {};

  const componentElementProperties = {
    X: x,
    Y: y,
    width,
    height,
    positionType,
    stretchType,
    printWhenExpression,
    printRepeatedValues,
    removeLineWhenBlank,
    printInFirstWholeBand,
    printWhenDetailOverflows,
    stretchWithOverFlow,
    blankWhenNull,
    repeatColumnHeaders,
    repeatRowHeaders,
    columnBreakOffset,
  };

  let dataSetRun = {};
  if (name && dataSetExpression) {
    dataSetRun = {
      dataSetName: name,
      dataSetExpression,
    };
  }

  let returnObj = {
    dataSetRun,
    ...formdata,
  };

  if (Object.keys(borders).length) {
    componentElementProperties.border = {
      line: {
        leftLine: {
          lineWidth: borders.Left.stroke,
          lineColor: borders.Left.color,
          lineStyle: borders.Left.style,
        },
        rightLine: {
          lineWidth: borders.Right.stroke,
          lineColor: borders.Right.color,
          lineStyle: borders.Right.style,
        },
        bottomLine: {
          lineWidth: borders.Bottom.stroke,
          lineColor: borders.Bottom.color,
          lineStyle: borders.Bottom.style,
        },
        topLine: {
          lineWidth: borders.Top.stroke,
          lineColor: borders.Top.color,
          lineStyle: borders.Top.style,
        },
      },
    };
  }
  if (Object.keys(padding).length) {
    let paddingObj = {
      bottomPadding: padding.Bottom,
      topPadding: padding.Top,
      leftPadding: padding.Left,
      rightPadding: padding.Right,
    };
    componentElementProperties.padding = paddingObj;
  }

  const customSettings = getCustomProperties(properties);
  if (customSettings) {
    componentElementProperties.customSettings = customSettings;
  }

  returnObj.componentElementProperties = componentElementProperties;

  return returnObj;
};

const getPreviewChart = (chart = {}) => {
  const {
    chartType,
    positionType,
    stretchType,
    printWhenExpression,
    printRepeatedValues,
    removeLineWhenBlank,
    printInFirstWholeBand,
    printWhenDetailOverflows,
    stretchWithOverFlow,
    blankWhenNull,
    borders,
    padding,
    x,
    y,
    width,
    height,
    mode,
    fontFill,
    fill,
    dataSets = [],
    selectedQueryID,
    xField,
    yField,
    colorField,
    // sizeField,
    labelField,
    renderType,
    theme,
    evaluationTime,
    evalGroup,
    chartTitle = {},
    chartSubtitle = {},
    chartLegend = {},
    chartItemLabel = {},
    chartColors = {},
    properties = []
  } = chart || {};

  const selectedQuery =
    dataSets?.find((ele) => ele.id === selectedQueryID) || {};
  const { name = "", dataSetExpression = "" } = selectedQuery || {};

  const componentElementProperties = {
    X: x,
    Y: y,
    width,
    height,
    positionType,
    stretchType,
    printWhenExpression,
    printRepeatedValues,
    removeLineWhenBlank,
    printInFirstWholeBand,
    printWhenDetailOverflows,
    stretchWithOverFlow,
    blankWhenNull,
    mode,
    foreColor: fontFill,
    backColor: fill,
  };

  let dataSetRun = {};
  if (name && dataSetExpression) {
    dataSetRun = {
      dataSetName: name,
      dataSetExpression,
    };
  }

  let chartDataSet = {
    dataSetRun,
  };

  switch (chartType) {
    case "bar":
      chartDataSet.categorySeries = {
        // seriesExpression: colorField ? `$F{${colorField}}` : "",
        // categoryExpression: xField ? `$F{${xField}}` : "\"\"",
        // valueExpression: yField ? `$F{${yField}}` : "",
        // labelExpression: labelField ? `$F{${labelField}}` : ""
        seriesExpression: colorField || "",
        categoryExpression: xField || "",
        valueExpression: yField || "",
        labelExpression: labelField || "",
      };
      break;
    case "arc":
      chartDataSet = {
        ...chartDataSet,
        // keyExpression: `$F{${colorField}}`,
        // valueExpression: `$F{${xField}}`,
        // labelExpression: labelField ? `$F{${labelField}}` : ""
        keyExpression: colorField || "",
        valueExpression: xField || "",
        labelExpression: labelField || "",
      };
      break;
    default:
      break;
  }

  const returnObj = {
    dataSet: chartDataSet,
    chartType: chartType === "bar" ? "Bar" : "Pie",
    // hyperlink:{},
    chart: {
      renderType,
      theme,
      evaluationTime,
      evaluationGroupName: evalGroup
    },
  };

  const commonProperties = (data = {}) => {
    const {
      expression,
      color = "",
      fontSize,
      bold,
      italic,
      underline,
      strikeThrough,
      ...rest
    } = data || {};
    return {
      expression: expression?.length ? `\"${expression}\"` : undefined,
      color,
      font: {
        size: fontSize,
        isBold: bold,
        isItalic: italic,
        isUnderline: underline,
        isStrikeThrough: strikeThrough,
      },
      ...rest,
    };
  };

  if (!isEmpty(chartTitle)) {
    returnObj.chart.chartTitle = commonProperties(chartTitle);
  }

  if (!isEmpty(chartSubtitle)) {
    returnObj.chart.chartSubtitle = commonProperties(chartSubtitle);
  }

  if (!isEmpty(chartLegend)) {
    let properties = commonProperties(chartLegend);
    delete properties.color;
    returnObj.chart.chartLegend = properties;
  }

  if (!isEmpty(chartItemLabel) && !isEmpty(chartColors)) {
    const { fontFamily, showLabels, ...rest } = chartItemLabel || {};
    const {
      backColor,
      backgroundAlpha,
      foregroundAlpha,
      orientation,
      seriesColors,
    } = chartColors || {};
    const itemLabelProperties = commonProperties(rest);
    itemLabelProperties.font.fontName = fontFamily;
    returnObj.chartPlot = {
      itemLabel: itemLabelProperties,
      plot: {
        backColor,
        backgroundAlpha,
        foregroundAlpha,
        orientation,
        seriesColors: seriesColors.map((color, index) => ({
          seriesOrder: index,
          color,
        })),
      },
    };
    if (labelField && showLabels) {
      returnObj.chartPlot.showLabels = true;
      returnObj.chartPlot.showTickLabels = true;
      returnObj.chartPlot.showTickMarks = true;
    }
  }

  if (Object.keys(borders).length) {
    componentElementProperties.border = {
      line: {
        leftLine: {
          lineWidth: borders.Left.stroke,
          lineColor: borders.Left.color,
          lineStyle: borders.Left.style,
        },
        rightLine: {
          lineWidth: borders.Right.stroke,
          lineColor: borders.Right.color,
          lineStyle: borders.Right.style,
        },
        bottomLine: {
          lineWidth: borders.Bottom.stroke,
          lineColor: borders.Bottom.color,
          lineStyle: borders.Bottom.style,
        },
        topLine: {
          lineWidth: borders.Top.stroke,
          lineColor: borders.Top.color,
          lineStyle: borders.Top.style,
        },
      },
    };
  }
  if (Object.keys(padding).length) {
    let paddingObj = {
      bottomPadding: padding.Bottom,
      topPadding: padding.Top,
      leftPadding: padding.Left,
      rightPadding: padding.Right,
    };
    componentElementProperties.padding = paddingObj;
  }

  const customSettings = getCustomProperties(properties);
  if (customSettings) {
    componentElementProperties.customSettings = customSettings;
  }
  returnObj.componentElementProperties = componentElementProperties;

  return returnObj;
};

export const isHCRDateVariable = (label) => {
  if (!label) return false;
  return ["new java.util.Date()"].includes(label)
}

export const getEmptyGroupCell = (name, height, styleNameReference) => {
  return {
    name,
    rowSpan: 1,
    height,
    styleNameReference,
    textField: []
  }
}

const getAdvancedTableColumns = (table = {}) => {
  const clonedTable = cloneDeep(table)
  const { nodes = {}, columns = {}, columnOrder = [], cells = {}, styles = [] } = clonedTable || {}
  if (isEmpty(nodes) || isEmpty(columns) || isEmpty(cells) || !columnOrder.length) return [];

  const cellIds = Object.keys(cells);

  function getBandData({ height, nodeIds, nodes, styleNameReference, availableWidth }) {
    const nodesMap = {
      text: "textField",
      image: "image",
    }
    const returnObj = {
      height,
      rowSpan: 1,
      enabled: true,
      textField: [],
      image: [],
      styleNameReference
    }
    const getNodeDetails = (node) => {
      let { band, category, ...rest } = node || {}
      let data = {}
      switch (category) {
        case "text": {
          const textF = getPreviewTextField({ ...rest, x: 0, y: 0 })
          data = isHCRDateVariable(node?.label) ? textF : addEscapedQuotes(textF);
          break;
        }
        case "image": {
          data = getPreviewImage({ ...rest, x: 0, y: 0 });
          break;
        }
        default:
          const textF = getPreviewTextField({ ...rest, x: 0, y: 0 })
          data = isHCRDateVariable(node?.label) ? textF : addEscapedQuotes(textF);
          category = "text";
          break;
      }
      return {
        category,
        data
      }
    }

    nodeIds.map((id, _i, _arr) => {
      let node = nodes[id];
      node.width = availableWidth / _arr.length;
      const { category, data } = getNodeDetails(node);
      returnObj[nodesMap[category]] = [...(returnObj[nodesMap[category]] || []), data];
    });

    if (isEmpty(returnObj.image)) {
      delete returnObj.image;
    }

    return returnObj;
  }

  const getGroupBandData = ({ name, height, nodeIds, nodes, styleNameReference, availableWidth }) => {
    const { enabled, ...rest } = getBandData({ height, nodeIds, nodes, styleNameReference, availableWidth }) || {};
    return {
      name,
      ...rest,
    }
  }

  return columnOrder.map((colId) => {
    let column = {}
    const columnCellsIds = cellIds.filter(cellId => cellId.includes(colId));
    if (columnCellsIds.length) {
      const columnInfo = columns[colId] || {};
      column.width = columnInfo.width
      columnCellsIds.forEach((cellId) => {
        const cell = cells[cellId];
        const { nodeIds = [], bandType = "", height, groupField, styleNameReference: stylesId, width } = cell || {}
        let currentStyle = styles.find(({ id }) => id === stylesId) || {};
        const { padding = {} } = currentStyle || {}
        const { Left = 0, Right = 0 } = padding || {}
        const availableWidth = width - (Left + Right);
        const styleNameReference = currentStyle?.styleName || null;
        const isGroup = isGroupBand(bandType);
        if (nodeIds.length) {
          if (isGroup) {
            if (!column[bandType]) {
              column[bandType] = [getGroupBandData({ height, nodeIds, nodes, name: groupField, styleNameReference, availableWidth })];
            } else {
              column[bandType] = [
                ...(column[bandType] || []),
                getGroupBandData({ height, nodeIds, nodes, name: groupField, styleNameReference, availableWidth })
              ]
            }
          } else {
            column[bandType] = getBandData({ height, nodeIds, nodes, styleNameReference, availableWidth });
          }
        } else {
          if (isGroup) { // [9003] group band cell can not be empty if group fields are present
            const isDeleted = cell.deleted || checkIfBandIsDeleted(bandType, cells, groupField);
            if (!column[bandType]) {
              column[bandType] = isDeleted ? [] : [getEmptyGroupCell(groupField, height, styleNameReference)]
            } else {
              column[bandType] = [
                ...(column[bandType] || []),
                ...(isDeleted ? [] : [getEmptyGroupCell(groupField, height, styleNameReference)])
              ]
            }
          } else {
            if (!column[bandType]) {
              const isDeleted = checkIfBandIsDeleted(bandType, cells);
              column[bandType] = {
                enabled: (cell.deleted || isDeleted) ? false : true,
                height,
                styleNameReference
              }
            }
          }
        }
      })

      hcrTableBandOrder.forEach((bandType) => {
        if (!column[bandType]) {
          if (isGroupBand(bandType)) {
            column[bandType] = []
          } else {
            column[bandType] = { enabled: false }
          }
        }
      })
    }
    return column
  }).filter((col) => !isEmpty(col))

}

const getPreviewAdvancedTable = (table = {}) => {
  const {
    positionType,
    stretchType,
    printWhenExpression,
    printRepeatedValues,
    removeLineWhenBlank,
    printInFirstWholeBand,
    printWhenDetailOverflows,
    stretchWithOverFlow,
    blankWhenNull,
    borders,
    padding,
    x,
    y,
    width,
    height,
    dataSets = [],
    selectedQueryID,
    properties = []
  } = table || {};
  const selectedQuery =
    dataSets?.find((ele) => ele.id === selectedQueryID) || {};
  const { name = "", dataSetExpression = "", subDatasetParameters = [] } = selectedQuery || {};

  const componentElementProperties = {
    X: x,
    Y: y,
    width,
    height,
    positionType,
    stretchType,
    printWhenExpression,
    printRepeatedValues,
    removeLineWhenBlank,
    printInFirstWholeBand,
    printWhenDetailOverflows,
    stretchWithOverFlow,
    blankWhenNull,
  };

  let dataSetRun = {};

  if (name && dataSetExpression) {
    dataSetRun = {
      dataSetName: name,
      dataSetExpression,
      parameters: subDatasetParameters.map((parameter) => ({ name: parameter?.name, expression: parameter?.mappingExpression }))
    };
  }

  let returnObj = {
    dataSetRun,
    columns: getAdvancedTableColumns(table),
  };

  if (Object.keys(borders).length) {
    componentElementProperties.border = {
      line: {
        leftLine: {
          lineWidth: borders.Left.stroke,
          lineColor: borders.Left.color,
          lineStyle: borders.Left.style,
        },
        rightLine: {
          lineWidth: borders.Right.stroke,
          lineColor: borders.Right.color,
          lineStyle: borders.Right.style,
        },
        bottomLine: {
          lineWidth: borders.Bottom.stroke,
          lineColor: borders.Bottom.color,
          lineStyle: borders.Bottom.style,
        },
        topLine: {
          lineWidth: borders.Top.stroke,
          lineColor: borders.Top.color,
          lineStyle: borders.Top.style,
        },
      },
    };
  }
  if (Object.keys(padding).length) {
    let paddingObj = {
      bottomPadding: padding.Bottom,
      topPadding: padding.Top,
      leftPadding: padding.Left,
      rightPadding: padding.Right,
    };
    componentElementProperties.padding = paddingObj;
  }

  const customSettings = getCustomProperties(properties);
  if (customSettings) {
    componentElementProperties.customSettings = customSettings;
  }

  returnObj.componentElementProperties = componentElementProperties;

  return returnObj;
}

export const addEscapedQuotes = (obj) => {
  if (Array.isArray(obj)) return obj.map(addEscapedQuotes);

  if (obj && typeof obj === "object") {
    const out = {};
    for (const [k, v] of Object.entries(obj)) {
      if (
        k === "textFieldExpression" &&
        typeof v === "string" &&
        !v.includes("$F") &&
        !v.includes("$V") &&
        !v.includes("$P")
      ) {
        const bare = v.replace(/^"?|"?$/g, "");
        out[k] = `\"${bare}\"`;
      } else {
        out[k] = addEscapedQuotes(v);
      }
    }
    return out;
  }
  return obj;
};


const getHeaderGroups = (
  data = [],
  headerKey = "",
  totalHeaderKey = "",
  others = {},
) => {
  let groupedData = data.reduce((acc, next) => {
    if (acc[next.value]) {
      acc[next.value].push(next);
    } else {
      acc[next.value] = [next];
    }
    return acc;
  }, {});
  let result = [];
  for (let key in groupedData) {
    const items = groupedData[key];
    const header = items.find((i) => i.cell === headerKey) || {};
    const totalHeader = items.find((i) => i.cell === totalHeaderKey) || {};
    let expression = `$F{${header.value}}`;
    let group = {
      bucket: {
        className: header?.backendDataType || "java.lang.String",
        expression,
      },
      [headerKey]: {
        textField: getPreviewTextField({
          ...header,
          x: 0,
          y: 0,
          label: header?.editedValue
            ? header.editedValue
            : `$V{${header.value}}`,
        }),
        // textField: getPreviewTextField({ ...header, x: 0, y: 0, label: `$V{${header.value}}` })
      },
      [totalHeaderKey]: {
        textField: addEscapedQuotes(
          getPreviewTextField({
            ...totalHeader,
            x: 0,
            y: 0,
            label: totalHeader?.editedValue
              ? totalHeader.editedValue
              : `Total ${totalHeader.value}`,
          }),
        ),
      },
      name: key,
      ...others,
    };
    result.push(group);
  }

  return result;
};

const getMeasures = (data = []) => {
  return data.map((item) => {
    const { aggregateFn = "", name = "", backendDataType = "" } = item || {};
    return {
      calculation: aggregateFn,
      className: backendDataType,
      measureExpression: `$F{${name}}`,
      name: `${name}_MEASURE`,
    };
  });
};

const getCTCellsGroupedData = (data = []) => {
  let result = {};
  if (!data.length) return [];
  data.forEach((item) => {
    const { identifier = {} } = item || {};
    if (isEmpty(identifier)) {
      result["measures"] = [...(result["measures"] || []), item];
    } else {
      let key = Object.entries(identifier)
        .map((keyValue) => keyValue.join("-"))
        .join("_");
      result[key] = [...(result[key] || []), item];
    }
  });
  return Object.values(result);
};

const getCrosstabCells = (data = []) => {
  let newData = getCTCellsGroupedData(data);
  return newData.map((items) => {
    const { identifier = {} } = items?.[0] || {};
    return {
      ...identifier,
      height: HCR_CROSSTAB_CELL_HEIGHT * (items.length > 1 ? items.length : 2),
      // width: HCR_CROSSTAB_CELL_WIDTH * (items.length > 1 ? items.length : 2),
      width: HCR_CROSSTAB_CELL_WIDTH,
      textField: items.map(({ measureLabel, ...rest }, index, _arr) => {
        return getPreviewTextField({
          ...rest,
          x: 0,
          y: HCR_CROSSTAB_CELL_HEIGHT * index,
          label: measureLabel,
        });
      }),
    };
  });
};

const getCrosstabCellsFormData = (crosstabCells = []) => {
  if (!crosstabCells.length) return {};
  const columnGroups = getHeaderGroups(
    crosstabCells.filter(
      (item) => item.identifier === HCR_CROSSTAB_COLUMN_GROUP,
    ),
    HCR_CROSSTAB_COLUMN_HEADER,
    HCR_CROSSTAB_COLUMN_TOTAL_HEADER,
    { height: HCR_CROSSTAB_CELL_HEIGHT },
  );
  const rowGroups = getHeaderGroups(
    crosstabCells.filter((item) => item.identifier === HCR_CROSSTAB_ROW_GROUP),
    HCR_CROSSTAB_ROW_HEADER,
    HCR_CROSSTAB_ROW_TOTAL_HEADER,
    { width: HCR_TABLE_DATA_CELL_WIDTH },
  );
  const measures = getMeasures(
    crosstabCells.filter(
      (item) => typeof item.identifier === "object" && isEmpty(item.identifier),
    ),
  );
  const cells = getCrosstabCells(
    crosstabCells.filter((item) => typeof item.identifier === "object"),
  );
  return {
    columnGroups,
    rowGroups,
    measures,
    crosstabCells: cells,
  };
};

const getCTFormData = (data = []) => {
  let groupedData = data.reduce((acc, next) => {
    if (acc[next.parent]) {
      acc[next.parent].push(next);
    } else {
      acc[next.parent] = [next];
    }
    return acc;
  }, {});
  let formData = {};
  for (let key in groupedData) {
    formData[key] = getCrosstabCellsFormData(groupedData[key]);
  }
  return formData;
};

const getDataSets = (
  data,
  connectionDetails = {},
  parametersList = [],
  variables = [],
  advancedTables = []
) => {
  let dataSets = data
    .map((item) => {
      if (!item?.temp_uuid) return null;
      const returnObj = {
        name: item.name,
        isMainDataset: false,
        dataSetExpression: `$P{${item.name}}`,
        connectionDetails: {
          temp_uuid: item?.temp_uuid,
          map_id: 1,
          ...connectionDetails,
        },
        fields: item?.executeQueryData?.field || [],
        parameters: parametersList?.length
          ? parametersList
          : item?.parameterList || [],
        id: item.id,
      };
      if (variables?.length) {
        returnObj.variables = variables;
      }
      if (advancedTables?.length) {
        const queryBasedGrpFields = advancedTables
          .filter(({ selectedQueryID, selectedGroupFields = [] }) => selectedQueryID === item.id && selectedGroupFields.length)
          .map(({ selectedGroupFields = [] }) => selectedGroupFields)
          .flat(1)
        const groupFields = [...new Set(queryBasedGrpFields)]
        if (groupFields.length) {
          returnObj.groups = groupFields.map((groupField) => {
            return {
              name: groupField,
              expression: `$F{${groupField}}`
            }
          });
        }

        // const queryBasedGrpFields = advancedTables.reduce((acc, { selectedQueryID = "", selectedGroupFields = [] }) => {
        //   if (acc[selectedQueryID]) {
        //     acc[selectedQueryID] = [...new Set([...(acc[selectedQueryID] || []), ...(selectedGroupFields || [])])];
        //   } else {
        //     acc[selectedQueryID] = selectedGroupFields || [];
        //   }
        //   return acc;
        // }, {})
        // const groupFields = queryBasedGrpFields[item.id] || [];
        // if (groupFields.length) {
        //   returnObj.groups = groupFields.map((groupField) => {
        //     return {
        //       name: groupField,
        //       expression: `$F{${groupField}}`
        //     }
        //   });
        // }
      }
      return returnObj;
    })
    .filter(Boolean);

  let parameters = dataSets.map(({ name = "", connectionDetails = {} }) => {
    return {
      name,
      className: "net.sf.jasperreports.engine.JRDataSource",
      connectionDetails,
    };
  });
  return { dataSets, parameters };
};


export const getSubDataSets = (
  subDataSets = [],
  allQueries = [],
  connectionDetails = {},
  parametersList = []
) => {
  let dataSets = subDataSets.map((item) => {
    if (item.isEmpty) return null;
    const query = allQueries.find(({ id }) => id === item.id);
    if (query) {
      const returnObj = {
        name: item.name,
        isMainDataset: false,
        dataSetExpression: `$P{${item.name}}`,
        connectionDetails: {
          temp_uuid: query?.temp_uuid,
          map_id: 1,
          ...connectionDetails,
        },
        fields: item?.fields?.map(({ id, ...rest }) => ({ ...rest })) || [],
        id: item.id,
        groups: [],
        variables: [],
        parameters: [],
        subDatasetParameters: []
      };
      if (item?.groups?.length) {
        item.groups.forEach((grp) => {
          const obj = {
            name: grp.name,
            groupNumber: grp.id,
            expression: grp.expression,
            reprintHeaderOnEachPage: grp.reprintHeaderOnEachPage,
            startNewColumn: grp.startNewColumn,
            startNewPage: grp.startNewPage,
            resetPageNumber: grp.resetPageNumber,
            reprintHeaderOnEachColumn: grp.reprintHeaderOnEachPage,
            keepTogether: grp.keepTogether,
            preventOrphanFooter: grp.preventOrphanFooter,
            minHeightToStartNewPage: grp.minHeightToStartNewPage,
            minDetailsToStartFromTop: grp.minRecordsToStartFromTop,
          };
          returnObj.groups.push(obj);
        });
      }
      if (item?.calculations?.length) {
        item.calculations.forEach((cal) => {
          const varObj = {
            name: cal.name,
            className: cal.className,
            calculation: cal.calculation,
            resetType: cal.resetType,
            expression: cal.expression,
            incrementType: cal.increment,
            incrementFactoryClassName: cal.incrementFactoryClassName,
            resetGroup: cal.resetGroup,
            incrementGroup: cal.incrementGroup,
            initialValueExpression: cal.initialValueExp,
            uniqId: cal.id,
            id: cal.calcId,
          };
          returnObj.variables.push(varObj);
        });
      }
      if (item?.parameters?.length) {
        returnObj.subDatasetParameters = item.parameters;
        function getValue(filter) {
          if (filter.type === "java.util.Collection") {
            if (filter?.canvasValues?.defaultValue) {
              return [filter.canvasValues.defaultValue]
            }
            return []
          }
          return filter?.canvasValues?.defaultValue || ""
        }
        item.parameters.forEach((filter) => {
          const filterValue = getValue(filter);
          const obj = {
            name: filter.name,
            className: filter.type,
            value: isArray(filterValue) ? filterValue?.[0] ? [filterValue[0].replaceAll('"', "")] : [] : filterValue,
          }
          returnObj.parameters.push(obj)
          returnObj.connectionDetails = {
            ...returnObj.connectionDetails,
            [filter.name]: isArray(filterValue) ? filterValue?.[0] ? [filterValue[0].replaceAll('"', "")] : [] : (filterValue.replaceAll('"', "") || "")
          }
        })
      }
      return returnObj;
    }
    return null;
  }).filter(Boolean)

  let parameters = dataSets.map(({ name = "", connectionDetails = {} }) => {
    return {
      name,
      className: "net.sf.jasperreports.engine.JRDataSource",
      connectionDetails,
    };
  });
  return { dataSets, parameters };
}

export const addBand = () => {
  return {
    bandHeight: 0,
    isImageAttached: false,
    staticText: [],
    textField: [],
    image: [],
    lines: [],
    break: [],
    table: [],
    crosstab: [],
    chart: [],
  };
};

const addNodeToBand = ({ formData, node, band, bandLimits }) => {
  let reqObj;
  if (band === "groups") {
    const grpObj = formData.designerProperties[band].find((ele) => {
      return `gp${ele.groupNumber}` === node.repeat;
    });
    if (
      bandLimits[node.repeat].header.node &&
      bandLimits[node.repeat].header.lower >= node.y + node.height
    ) {
      reqObj = grpObj.groupHeader;
      node.y = node.y - bandLimits[node.repeat].header.upper;
    } else if (bandLimits[node.repeat].footer.node) {
      reqObj = grpObj.groupFooter;
      node.y = node.y - bandLimits[node.repeat].footer.upper;
    }
  } else {
    if (!formData.designerProperties[band]) {
      formData.designerProperties[band] = addBand();
    }
    reqObj = formData.designerProperties[band];
  }
  // table and crosstab grouping issue is present here
  if (reqObj) {
    // if((node.y+node.height) > reqObj.bandHeight) {
    //     reqObj.bandHeight = node.y+node.height;
    // }
    if (node.category === "text") {
      reqObj.textField.push(getPreviewTextField(node));
    } else if (node.category === "line") {
      reqObj.lines.push(getPreviewLine(node));
    } else if (node.category === "image") {
      reqObj.image.push(getPreviewImage(node));
    } else if (node.category === "pageBreak") {
      reqObj.break.push(getPreviewBreak(node));
    } else if (node.category === "crosstab") {
      reqObj?.crosstab?.push(getPreviewCrosstab(node));
    } else if (node.category === "chart") {
      reqObj?.chart?.push(getPreviewChart(node));
    } else if (node.category === "advancedTable") {
      reqObj?.table?.push(getPreviewAdvancedTable(node));
    }
  }
};

function getPgStyles(pgStyl, isTableStyle = false) {
  const {
    blankWhenNull,
    bold,
    borders,
    defaultStyle,
    fill,
    fontFamily,
    fontFill,
    fontSize,
    horizontalAlign,
    id,
    italic,
    lineStyles,
    markUp,
    mode,
    padding,
    pattern,
    rotation,
    strikeThrough,
    styleName,
    underLine,
    verticalAlign,
    isConditionalStyleReq,
    expression,
    expressionBackColor
  } = pgStyl;

  const pgStylObj = {
    name: styleName,
    textBackcolor: fill,
    textForecolor: fontFill,
    imageBackcolor: fill,
    imageForecolor: fontFill,
    fontName: fontFamily,
    textFontSize: fontSize,
    bold,
    italic,
    strikeThrough,
    underline: underLine,
    rotationType: rotation,
    horizontalTextAlign: horizontalAlign,
    verticalTextAlign: verticalAlign,
    horizontalImageAlign: horizontalAlign,
    verticalImageAlign: verticalAlign,
    pattern,
    markUp,
    blankWhenNull,
    mode,
    isDefault: defaultStyle,
    border: {
      // padding: {
      //   bottomPadding: 1,
      //   topPadding: 1,
      //   leftPadding: 1,
      //   rightPadding: 1,
      // },
      line: {
        leftLine: {
          lineWidth: 0,
          lineColor: "#000000",
          lineStyle: "Solid",
        },
        rightLine: {
          lineWidth: 0,
          lineColor: "#000000",
          lineStyle: "Solid",
        },
        bottomLine: {
          lineWidth: 0,
          lineColor: "#000000",
          lineStyle: "Solid",
        },
        topLine: {
          lineWidth: 0,
          lineColor: "#000000",
          lineStyle: "Solid",
        },
      },
    },
    padding: {
      bottomPadding: 1,
      topPadding: 1,
      leftPadding: 1,
      rightPadding: 1,
    },
  };

  if (Object.keys(borders).length) {
    pgStylObj.border = {
      line: {
        leftLine: {
          lineWidth: borders.Left.stroke,
          lineColor: borders.Left.color,
          lineStyle: borders.Left.style,
        },
        rightLine: {
          lineWidth: borders.Right.stroke,
          lineColor: borders.Right.color,
          lineStyle: borders.Right.style,
        },
        bottomLine: {
          lineWidth: borders.Bottom.stroke,
          lineColor: borders.Bottom.color,
          lineStyle: borders.Bottom.style,
        },
        topLine: {
          lineWidth: borders.Top.stroke,
          lineColor: borders.Top.color,
          lineStyle: borders.Top.style,
        },
      },
    };
    // if (Object.keys(padding).length) {
    //   let paddingObj = {
    //     bottomPadding: padding.Bottom,
    //     topPadding: padding.Top,
    //     leftPadding: padding.Left,
    //     rightPadding: padding.Right,
    //   };
    //   if (pgStylObj.border) {
    //     pgStylObj.border.padding = paddingObj;
    //   } else {
    //     pgStylObj.border = {
    //       padding: paddingObj,
    //     };
    //   }
    // }
  }
  if (Object.keys(padding).length) {
    let paddingObj = {
      bottomPadding: padding.Bottom,
      topPadding: padding.Top,
      leftPadding: padding.Left,
      rightPadding: padding.Right,
    };
    pgStylObj.padding = paddingObj;
  }

  if (Object.keys(lineStyles).length) {
    pgStylObj.lineStyle = {
      lineForecolor: lineStyles.color,
      penLineWidth: lineStyles.stroke,
      lineStyle: lineStyles.style,
    };
  }

  if (isTableStyle) {
    pgStylObj.backColor = fill;
    pgStylObj.foreColor = fontFill;
    pgStylObj.fontSize = fontSize;

    if (isConditionalStyleReq && expression && expressionBackColor) {
      pgStylObj.conditionalStyle = {
        expression,
        backColor: expressionBackColor
      };
    }
  }

  return pgStylObj;
}

export const getPreviewFormData = ({
  flowchartInstance,
  query = {},
  canvasProperties = {},
  filters = [],
  hcrDiagramNodesData,
  reportName,
  bandLimits,
  updatedPageNo,
  isHCR,
  isExport,
  format = "html",
  saveDetails = {},
  isCache,
  openMode,
  allQueries = [],
  hcrExportProperties = [],
  tempUUIDsMap,
  subDataSets = [],
  tableStyles = []
}) => {
  const title = [];
  const image = [];
  const lines = [];
  const {
    margin,
    layout,
    pageProperties,
    calculations,
    groupProperties,
    pageStyles,
  } = canvasProperties;
  const {
    columnCount,
    columnSpacing,
    columnWidth,
    printOrder,
    whenNoData,
    summaryWithHeaderAndFooter,
    floatColumnFooter,
    titleNewPage,
    summaryInNewPage,
    ignorePagination,
  } = pageProperties;

  // let textBandHeight = 60;
  // const connectionDetails = {};
  let nodes;
  if (hcrDiagramNodesData?.length) {
    nodes = hcrDiagramNodesData.map((ele) => ({ ...ele }));
  } else {
    nodes = getFlowChartNodes({ flowchartInstance })?.map((ele) => {
      return { ...ele };
    });
  }
  const crosstabCells = nodes.filter((node) => node.isCrosstabCell);
  const advancedTables = nodes.filter((node) => node.category === "advancedTable");
  nodes = nodes.filter((node) => !node.isTableCell && !node.isCrosstabCell);
  const crosstabCellsFormData = getCTFormData(crosstabCells);
  const includeDataset = nodes.some((node) =>
    ["crosstab", "chart", "advancedTable"].includes(node.category),
  );
  const isTableOnlyNodes = nodes.every((node) =>
    ["crosstab", "chart", "advancedTable"].includes(node.category),
  );

  nodes = nodes.map((node) => { // assign table styles to table components
    if (node.category === "advancedTable") {
      node.styles = tableStyles
    }
    return node;
  })

  const crosstabAndChartNodes = nodes.filter((node) => ["crosstab", "chart"].includes(node.category));

  const formData = {
    format: format,
    page: updatedPageNo ? updatedPageNo - 1 : 0,
    connectionDetails: {},
    designerProperties: {
      reportName,
      groups: [],
      fields: [],
      designerStyles: [],
      parameters: [],
      variables: [],
      designerStyle: [],
      pageWidth: ["landscape"].includes(layout.orientation?.toLowerCase())
        ? layout.size.height
        : layout.size.width,
      pageHeight: ["landscape"].includes(layout.orientation?.toLowerCase())
        ? layout.size.width
        : layout.size.height,
      orientation: layout.orientation,
      columnWidth,
      columnSpacing,
      leftMargin: margin.left,
      rightMargin: margin.right,
      topMargin: margin.top,
      bottomMargin: margin.bottom,
      titleNewPage,
      floatColumnFooter,
      summaryNewPage: summaryInNewPage,
      columnCount,
      printOrder,
      whenNoDataType: whenNoData,
      ignorePagination,
      summaryWithPageHeaderAndFooter: summaryWithHeaderAndFooter,
    },
    type: format,
    isPreview: true,
    isHCR,
    isExport,
    reportName,
    dir: saveDetails.location,
    // genereteXML: true
  };

  if (hcrExportProperties?.length) {
    formData.designerProperties.applyCustomSettings = true;
    formData.designerProperties.customSettings = hcrExportProperties.reduce(
      (acc, { key, value }) => {
        if (!acc[key]) {
          acc[key] = value;
        }
        return acc;
      },
      {},
    );
  }

  if (!openMode) {
    formData.designerChange = {
      isChanged: true,
      printUUID: "printUUID",
    };
  }

  if (isCache) {
    formData.refresh = true;
  }

  if (groupProperties.options.length) {
    groupProperties.options.forEach((grp) => {
      const obj = {
        name: grp.name,
        groupNumber: grp.id,
        expression: grp.expression,
        reprintHeaderOnEachPage: grp.reprintHeaderOnEachPage,
        startNewColumn: grp.startNewColumn,
        startNewPage: grp.startNewPage,
        resetPageNumber: grp.resetPageNumber,
        reprintHeaderOnEachColumn: grp.reprintHeaderOnEachPage,
        keepTogether: grp.keepTogether,
        preventOrphanFooter: grp.preventOrphanFooter,
        minHeightToStartNewPage: grp.minHeightToStartNewPage,
        minDetailsToStartFromTop: grp.minRecordsToStartFromTop,
        groupHeader: {
          bandHeight: 0,
          textField: [],
          lines: [],
          image: [],
          break: [],
          table: [],
          crosstab: [],
          chart: [],
          splitType: "Stretch",
        },
        groupFooter: {
          bandHeight: 0,
          textField: [],
          lines: [],
          image: [],
          break: [],
          table: [],
          crosstab: [],
          chart: [],
          splitType: "Stretch",
        },
      };
      formData.designerProperties.groups.push(obj);
    });
  }

  filters.forEach((filter) => {
    const filObj = {
      name: filter.label,
      className: filter.className,
      expressionType: "simpleText",
    };
    if (filter.condition !== "IN_RANGE") {
      if (filter.className === "java.lang.Integer") {
        if (
          filter.condition === "IS_ONE_OF" ||
          filter.condition === "CONTAINS"
        ) {
          filObj.value = filter.values.join(",");
          filObj.defaultExpression = filter.values.join(",");
          formData.connectionDetails[filter.label] = filter.values.join(",");
        } else if (filter.condition === "EQUALS") {
          if (["date", "dateTime"].includes(filter.dataType)) {
            let val =
              moment(filter.values[0], filter.displayFormat).format(
                filter.valueFormat,
              ) || "";
            filObj.value = val;
            filObj.defaultExpression = val;
            formData.connectionDetails[filter.label] = val;
          } else {
            const filterValue = isArray(filter.values)
              ? filter.values?.[0]
              : filter.values;
            filObj.value = filterValue;
            filObj.defaultExpression = filterValue;
            formData.connectionDetails[filter.label] = filterValue;
          }
        }
      } else if (filter.className === "java.util.Collection") {
        if (filter.condition === "IS_ONE_OF") {
          filObj.value = filter.values;
          filObj.defaultExpression = filter.values;
          formData.connectionDetails[filter.label] = filter.values;
        } else if (filter.condition === "CONTAINS") {
          const reqValues =
            filter.values[0]?.split(",")?.map((ele) => ele.trim()) || [];
          filObj.value = reqValues;
          filObj.defaultExpression = reqValues;
          formData.connectionDetails[filter.label] = reqValues;
        } else if (filter.condition === "EQUALS") {
          if (["date", "dateTime"].includes(filter.dataType)) {
            let val = moment(filter.values[0], filter.displayFormat).format(
              filter.valueFormat,
            );
            filObj.value = [val];
            filObj.defaultExpression = [val];
            formData.connectionDetails[filter.label] = [val];
          } else {
            const filterValue = filter.values;
            filObj.value = filterValue;
            filObj.defaultExpression = filterValue;
            formData.connectionDetails[filter.label] = filterValue;
          }
        }
      } else {
        if (
          filter.condition === "IS_ONE_OF" ||
          filter.condition === "CONTAINS"
        ) {
          filObj.value = filter.values.join(",");
          filObj.defaultExpression = filter.values.join(",");
          formData.connectionDetails[filter.label] = filter.values.join(",");
        } else if (filter.condition === "EQUALS") {
          if (["date", "dateTime"].includes(filter.dataType)) {
            const val = moment(filter.values[0], filter.displayFormat).format(
              filter.valueFormat,
            );
            filObj.value = val;
            filObj.defaultExpression = val;
            formData.connectionDetails[filter.label] = val;
          } else {
            const filterValue = isArray(filter.values)
              ? filter.values?.[0]
              : filter.values;
            filObj.value = filterValue;
            filObj.defaultExpression = filterValue;
            formData.connectionDetails[filter.label] = filterValue;
          }
        }
      }
    } else {
      if (filter.range[0] === filter.label) {
        let val = moment(filter.values[0], filter.displayFormat).format(
          filter.valueFormat,
        );
        if (filter.className === "java.lang.Integer") {
          filObj.value = val;
          filObj.defaultExpression = val;
          formData.connectionDetails[filter.label] = val;
        } else if (filter.className === "java.util.Collection") {
          filObj.value = [val];
          filObj.defaultExpression = [val];
          formData.connectionDetails[filter.label] = [val];
        } else {
          filObj.value = val + "";
          filObj.defaultExpression = val + "";
          formData.connectionDetails[filter.label] = val + "";
        }
      } else if (filter.range[1] === filter.label) {
        let val = moment(filter.values[1], filter.displayFormat).format(
          filter.valueFormat,
        );
        if (filter.className === "java.lang.Integer") {
          filObj.value = val;
          filObj.defaultExpression = val;
          formData.connectionDetails[filter.label] = val;
        } else if (filter.className === "java.util.Collection") {
          filObj.value = [val];
          filObj.defaultExpression = [val];
          formData.connectionDetails[filter.label] = [val];
        } else {
          filObj.value = val + "";
          filObj.defaultExpression = val + "";
          formData.connectionDetails[filter.label] = val + "";
        }
      }
    }
    formData.designerProperties.parameters.push(filObj);
  });

  calculations.options.forEach((cal) => {
    const varObj = {
      name: cal.name,
      className: cal.className,
      calculation: cal.calculation,
      resetType: cal.resetType,
      expression: cal.expression,
      incrementType: cal.increment,
      incrementFactoryClassName: cal.incrementFactoryClassName,
      resetGroup: cal.resetGroup,
      incrementGroup: cal.incrementGroup,
      initialValueExpression: cal.initialValueExp,
      uniqId: cal.id,
      id: cal.calcId,
    };
    formData.designerProperties.variables.push(varObj);
  });

  let dataSets = [], parameters = []
  if (crosstabAndChartNodes.length) {
    const { dataSets: subDS = [], parameters: params = [] } = getDataSets(
      allQueries,
      formData.connectionDetails ?? {},
      formData.designerProperties.parameters ?? [],
      formData.designerProperties.variables ?? [],
      advancedTables
    );
    dataSets = subDS;
    parameters = params;

  } else {
    const { dataSets: subDS = [], parameters: params = [] } = getSubDataSets(
      subDataSets,
      allQueries,
      formData.connectionDetails ?? {},
      formData.designerProperties.parameters ?? [],
    )

    dataSets = subDS;
    parameters = params;
  }

  nodes = nodes.map((node) => {
    if (node.category === "crosstab") {
      return {
        ...node,
        formdata: crosstabCellsFormData?.[node.id] || {},
        dataSets,
      };
    }
    if (node.category === "chart") {
      return {
        ...node,
        dataSets,
      };
    }
    if (node.category === "advancedTable") {
      return {
        ...node,
        dataSets,
      };
    }
    return node;
  });

  if (includeDataset) {
    if (!dataSets.length) {
      // required to send empty dataset [#8490]
      dataSets.push({
        name: "Query1",
        isMainDataset: false,
        dataSetExpression: "$P{REPORT_CONNECTION}",
        id: "",
        connectionDetails: {},
        fields: [],
        parameters: [],
      });
    }
    formData.designerProperties.dataSets = dataSets?.map(
      ({ id, subDatasetParameters, ...rest }) => rest,
    );
    formData.designerProperties.parameters = [
      ...parameters,
      ...formData.designerProperties.parameters,
    ];
  }

  let tempConnectionDetails = {};
  if (isTableOnlyNodes) {
    tempConnectionDetails = cloneDeep(formData.connectionDetails);
    formData.connectionDetails = {};
  }

  pageStyles.options.forEach((pgStyl) => {
    const pgStylObj = getPgStyles(pgStyl);
    formData.designerProperties.designerStyle.push(pgStylObj);
  });

  if (tableStyles.length) {
    tableStyles.forEach((tblStyl) => {
      const tblStylObj = getPgStyles(tblStyl, true);
      formData.designerProperties.designerStyle.push(tblStylObj);
    });
  }

  const pageHeight = flowchartInstance.current?.getArea().height;

  const parseNumber = (value) => {
    return Number(Number.parseFloat(value).toFixed(1));
  };

  nodes.forEach((node) => {
    if (node.repeat === "rt") {
      if (
        bandLimits.rt.header.node &&
        bandLimits.rt.header.lower >= node.y + node.height
      ) {
        //title
        // if(node.y < titleMinY) {
        //     titleMinY = node.y;
        // }
        node.y = node.y - bandLimits.rt.header.upper;
        addNodeToBand({ formData, node, band: "title" });
        if (!formData.designerProperties.title.bandHeight) {
          formData.designerProperties.title.bandHeight = parseNumber(
            bandLimits.rt.header.lower - bandLimits.rt.header.upper,
          );
        }
      } else {
        // summary
        node.y = node.y - bandLimits.rt.footer.upper;
        // if(node.y < summaryMinY) {
        //     summaryMinY = node.y;
        // }
        addNodeToBand({ formData, node, band: "summary" });
        if (!formData.designerProperties.summary.bandHeight) {
          formData.designerProperties.summary.bandHeight = parseNumber(
            bandLimits.rt.footer.lower - bandLimits.rt.footer.upper,
          );
        }
      }
    } else if (node.repeat === "pg") {
      if (
        bandLimits.pg.header.node &&
        bandLimits.pg.header.lower >= node.y + node.height
      ) {
        //head
        // if(node.y < pgHeaderMinY) {
        //     pgHeaderMinY = node.y;
        // }
        node.y = node.y - bandLimits.pg.header.upper;
        addNodeToBand({ formData, node, band: "pageHeader" });
        if (!formData.designerProperties.pageHeader.bandHeight) {
          formData.designerProperties.pageHeader.bandHeight = parseNumber(
            bandLimits.pg.header.lower - bandLimits.pg.header.upper,
          );
        }
      } else {
        // summary
        node.y = node.y - bandLimits.pg.footer.upper;
        // if(node.y < pgFooterMinY) {
        //     pgFooterMinY = node.y;
        // }
        addNodeToBand({ formData, node, band: "pageFooter" });
        if (!formData.designerProperties.pageFooter.bandHeight) {
          formData.designerProperties.pageFooter.bandHeight = parseNumber(
            bandLimits.pg.footer.lower - bandLimits.pg.footer.upper,
          );
        }
      }
    } else if (node.repeat === "cl") {
      if (
        bandLimits.cl.header.node &&
        bandLimits.cl.header.lower >= node.y + node.height
      ) {
        //head
        // if(node.y < clmHeaderMinY) {
        //     clmHeaderMinY = node.y;
        // }
        node.y = node.y - bandLimits.cl.header.upper;
        addNodeToBand({ formData, node, band: "columnHeader" });
        if (!formData.designerProperties.columnHeader.bandHeight) {
          formData.designerProperties.columnHeader.bandHeight = parseNumber(
            bandLimits.cl.header.lower - bandLimits.cl.header.upper,
          );
        }
      } else {
        // summary
        node.y = node.y - bandLimits.cl.footer.upper;
        // if(node.y < clmFooterMinY) {
        //     clmFooterMinY = node.y;
        // }
        addNodeToBand({ formData, node, band: "columnFooter" });
        if (!formData.designerProperties.columnFooter.bandHeight) {
          formData.designerProperties.columnFooter.bandHeight = parseNumber(
            bandLimits.cl.footer.lower - bandLimits.cl.footer.upper,
          );
        }
      }
    } else if (node.repeat === "rd") {
      // if(node.y < detailsMinY) {
      //     detailsMinY = node.y;
      // }
      node.y = node.y - bandLimits.rd.upper;
      addNodeToBand({ formData, node, band: "details" });
      if (!formData.designerProperties.details.bandHeight) {
        formData.designerProperties.details.bandHeight = parseNumber(
          bandLimits.rd.lower - bandLimits.rd.upper,
        );
      }
    } else if (node.repeat === "lpf") {
      // if(node.y < lpfMinY) {
      //     lpfMinY = node.y;
      // }
      node.y = node.y - bandLimits.lpf.upper;
      addNodeToBand({ formData, node, band: "lastPageFooter" });
      if (!formData.designerProperties.lastPageFooter.bandHeight) {
        formData.designerProperties.lastPageFooter.bandHeight = parseNumber(
          bandLimits.lpf.lower - bandLimits.lpf.upper,
        );
      }
    } else if (node.repeat === "nd") {
      // if(node.y < noDataMinY) {
      //     noDataMinY = node.y;
      // }
      node.y = node.y - bandLimits.nd.upper;
      addNodeToBand({ formData, node, band: "noData" });
      if (!formData.designerProperties.noData.bandHeight) {
        formData.designerProperties.noData.bandHeight = parseNumber(
          bandLimits.nd.lower - bandLimits.nd.upper,
        );
      }
    } else if (node.repeat.includes("gp")) {
      // if(node.y < groupsMinY) {
      //     groupsMinY = node.y;
      // }
      addNodeToBand({ formData, node, band: "groups", bandLimits });
    }

    if (query?.executeQueryData?.field) {
      let tempUUID = tempUUIDsMap
        ? tempUUIDsMap?.temp_uuid || query?.temp_uuid
        : query?.temp_uuid;
      formData.designerProperties.fields = query?.executeQueryData?.field;
      formData.connectionDetails.temp_uuid = tempUUID;
      // formData.connectionDetails.temp_uuid = query?.temp_uuid;
      formData.connectionDetails.map_id = 1;
      formData.connectionDetails = {
        ...formData.connectionDetails,
        ...tempConnectionDetails,
      };
    } else {
      formData.connectionDetails = {};
    }
  });

  // handleNodeFstYVal({band: 'title', minY: bandLimits.rt.header.upper, formData});
  // handleNodeFstYVal({band: 'summary', minY: bandLimits.rt.footer.upper, formData});
  // handleNodeFstYVal({band: 'pageHeader', minY: bandLimits.pg.header.upper, formData});
  // handleNodeFstYVal({band: 'pageFooter', minY: bandLimits.pg.footer.upper, formData});
  // handleNodeFstYVal({band: 'columnHeader', minY: bandLimits.cl.header.upper, formData});
  // handleNodeFstYVal({band: 'columnFooter', minY: bandLimits.cl.footer.upper, formData});
  // handleNodeFstYVal({band: 'details', minY: bandLimits.rd.upper, formData});
  // handleNodeFstYVal({band: 'lastPageFooter', minY: bandLimits.lpf.upper, formData});
  // handleNodeFstYVal({band: 'noData', minY: bandLimits.nd.upper, formData});
  formData.designerProperties.groups.forEach((grp) => {
    // handleNodeFstYVal({band: `gp${grp.groupNumber}`, bandLimits, formData});
    const band = `gp${grp.groupNumber}`;
    const reqGrp = formData.designerProperties.groups.find((grp) => {
      return `gp${grp.groupNumber}` === band;
    });
    if (reqGrp.groupHeader) {
      if (!reqGrp.groupHeader.bandHeight) {
        reqGrp.groupHeader.bandHeight =
          bandLimits[band].header.lower - bandLimits[band].header.upper;
        if (reqGrp.groupHeader.bandHeight < 0) {
          reqGrp.groupHeader.bandHeight = 0;
        }
      }
      // updateBandHeight(reqGrp.groupHeader, formData, bandLimits[band].header.upper, band)
    }
    if (reqGrp.groupFooter) {
      if (!reqGrp.groupFooter.bandHeight && bandLimits[band].footer.node) {
        reqGrp.groupFooter.bandHeight =
          bandLimits[band].footer.lower - bandLimits[band].footer.upper;
        if (reqGrp.groupFooter.bandHeight < 0) {
          reqGrp.groupFooter.bandHeight = 0;
        }
      }
      // updateBandHeight(reqGrp.groupFooter, formData, bandLimits[band].footer.upper, band)
    }
  });

  if (formData.designerProperties.details) {
    formData.designerProperties.details = [formData.designerProperties.details];
  }
  return formData;
};

export const getTagContent = ({ startTag, inputString, endTag }) => {
  const startIndex = inputString.indexOf(startTag) + startTag.length;
  const endIndex = inputString.indexOf(endTag);
  let extractedValue = "";
  if (startIndex !== -1 && endIndex !== -1) {
    extractedValue = inputString.substring(startIndex, endIndex);
  }
  return extractedValue;
};

export const getDsConnTypesCols = ({ dispatch }) => {
  let columnsData = [];
  ["DataSource Pane"].forEach((ele) => {
    columnsData.push({
      title: () => (
        <Tooltip mouseEnterDelay={constants.mouseEnterDelay} title={ele}>
          {ele}
        </Tooltip>
      ),
      dataIndex: "dataSourcePane",
      key: ele,
      className: "table-ellipsis",
      render: (name, record) => {
        return (
          <Space>
            <Tooltip
              // overlayClassName= 'security-validateTable-tooltip'
              title={name}
            >
              <Paragraph
                style={{ maxWidth: 120, marginBottom: 0 }}
                ellipsis={true}
              >
                {name}
              </Paragraph>
            </Tooltip>
            <PlusOutlined
              onClick={() => {
                dispatch(hcrActions.handleAddingDsPaneItem(name));
              }}
            />
          </Space>
        );
      },
    });
  });
  return columnsData;
};

export const dsConnTabData = ({ dsPaneTypes = [], dispatch }) => {
  let data = [];
  dsPaneTypes.forEach((ele) => {
    data.push({
      ...ele,
    });
  });
  return data;
};

export const handleSaveHcr = ({
  selectedQueryId,
  type,
  location,
  fileName,
  dispatch,
  saveHcrRequest,
  presentActiveTabUuid,
  flowchartInstance,
  dsPaneTypes,
  hcrDiagramNodesData,
  uuid,
  canvasProperties,
  groupCount,
  groupsOrder,
  previewFormData = {},
  hcrExportProperties,
  handleUpdateQueryuuids = () => { },
  subDataSets = [],
  tableStyles = [],
  tempUUIDsMap
}) => {
  const reqNodes = getFlowChartNodes({ flowchartInstance });
  //console.log("Request Nodes" , reqNodes)
  let flowchartNodes = reqNodes.length ? reqNodes : hcrDiagramNodesData;
  // .map(ele => {
  //     delete ele.originData;
  //     return ele;
  // });
  const dsPanes = dsPaneTypes.map((dsPane) => {
    let obj = { ...dsPane };

    if (obj.dataSourcePane === hcrDSQuery) {
      obj.menu = obj.menu.map((query) => {
        if (query.id === selectedQueryId) {
          query.temp_uuid = tempUUIDsMap ? tempUUIDsMap?.temp_uuid || query?.temp_uuid : query?.temp_uuid;
        }
        return query
      })
    }

    // obj.menu = [...ob]
    obj.menu = obj.menu.map((ele) => {
      const { isSaved, isNameEditable, ...rest } = ele;
      if (rest.executeQueryData) {
        rest.executeQueryData = {
          ...rest.executeQueryData,
          data: [],
        };
      }
      return rest;
    });
    return obj;
  });

  if (Object.keys(previewFormData).length) {
    delete previewFormData.type;
    delete previewFormData.isPreview;
    delete previewFormData.isHCR;
    delete previewFormData.reportName;
    delete previewFormData.designerProperties?.reportName;
    previewFormData.generateXML = false;
  }

  let formData = {
    state: {
      dsPanes,
      canvasProperties,
      groupCount,
      groupsOrder,
      selectedQueryId,
      exportProperties: hcrExportProperties,
      subDataSets: subDataSets.filter((subDS) => !subDS.isEmpty),
      tableStyles
    },
    diagram: { nodes: flowchartNodes },
    name: fileName,
    dir: location,
    previewFormData,
  };
  //
  // formData.location = location;
  // formData.fileName = fileName;
  if (uuid) {
    formData.uuid = uuid;
  } else {
    formData.saveUUID = presentActiveTabUuid;
  }
  //
  saveHcrRequest(
    formData,
    (res) => {
      if (res?.data) {
        dispatch(fileBrowserActions.saveFileinFb(res.data));
      }
      const dsPanes = res.state.dsPanes.map((pane) => {
        const reqPane = { ...pane };
        reqPane.menu = reqPane.menu.map((ele) => {
          const item = { ...ele };
          if (reqPane.key === "parameter") {
            item.executeQueryData = item.executeQueryData || {
              data: [],
              field: [],
            };
            item.parameterList = item.parameterList || [];
          }
          item.isSaved = true;
          item.isNameEditable = false;
          return item;
        });
        return reqPane;
      });
      const stateArr = [
        { key: "dsPaneTypes", value: dsPanes },
        { key: "hcrDiagramNodesData", value: [...res.diagram.nodes] },
        { key: "canvasProperties", value: res.state.canvasProperties },
        { key: "groupCount", value: res.state.groupCount || 0 },
        { key: "groupsOrder", value: res.state.groupsOrder || [] },
        { key: "title", value: res.name },
        { key: "selectedQueryId", value: res.state.selectedQueryId },
        {
          key: "saveDetails",
          value: {
            location: res.location,
            uuid: res.uuid,
            hcrName: res.name,
          },
        },
        { key: "uuid", value: res.uuid },
      ];
      dispatch(hcrActions.storeHcrState(stateArr));
      handleUpdateQueryuuids(res.previewFormData.connectionDetails);
    },
    (err) => {
      console.log("in handle save ERROR", err);
    },
  );
};

export const validateHcrName = (name) => {
  if (name.length < 3) {
    return "HCR name should be atleast 3 characters long";
  }
  if (name.length > 60) {
    return "HCR name should not exceed 60 characters";
  }
  if (!/^\w[\w.\-&#+~]*/i.test(name)) {
    return "Please provide valid HCR name";
  }
  return false;
};

function handleReduxValue({ arr, reduxVal }) {
  let propertyPaneData = reduxVal;
  arr?.forEach((val) => {
    if (val === "$activeShape") {
      reduxVal = reduxVal[propertyPaneData[val]];
    } else {
      reduxVal = reduxVal[val];
    }
  });
  return reduxVal;
}

export function handleStaticPathValue({ hcrStaticData, optionsStaticPath }) {
  let staticPathValue = hcrStaticData?.HCR;
  optionsStaticPath?.forEach((key) => {
    staticPathValue = staticPathValue[key];
  });
  return staticPathValue;
}

export function onConfiguration({
  hcrStaticData,
  oldConfigContent,
  groups,
  propertyPaneData,
}) {
  return oldConfigContent
    .map((groupSet) => {
      if (groupSet.type === "settingsGroup") {
        groups.push({
          title: groupSet.name,
          key: groupSet.name,
        });
      }
      return groupSet.content.map((ele) => {
        let reduxVal = propertyPaneData;
        let staticPathValue = hcrStaticData?.HCR;
        const configuredProperty = {
          label: /*ele.title || */ ele.name || ele.label,
          key: /*ele.value || ele.title || */ ele.name || ele.label,
          groupId: groupSet.name,
          path: ele.path,
          callBack: ele.callBack,
        };
        if (ele.fromStatic) {
          staticPathValue = handleStaticPathValue({
            hcrStaticData,
            optionsStaticPath: ele.optionsStaticPath,
          });
          configuredProperty.staticPathValue = staticPathValue;
        }
        reduxVal = handleReduxValue({ arr: ele.path, reduxVal });
        if (ele.type === "DropDown") {
          configuredProperty.elementType = "Select";
          let value = Array.isArray(staticPathValue)
            ? staticPathValue
            : Object.keys(staticPathValue);
          configuredProperty.value = value.map((item) => {
            return { key: item, label: item };
          });
          // console.log(configuredProperty.value);
          configuredProperty.selectedKey = reduxVal;
        } else if (ele.type === "numericDropdown") {
          configuredProperty.elementType = "InputNumber";
          configuredProperty.tooltip = ele.name;
          configuredProperty.value = reduxVal;
        } else if (ele.type === "checkBox") {
          configuredProperty.elementType = "Checkbox";
          configuredProperty.value = reduxVal;
        } else if (ele.type === "textBox") {
          configuredProperty.elementType = "CodeEditor";
          configuredProperty.value = reduxVal;
        } else if (ele.type === "colorPicker") {
          configuredProperty.elementType = "ColorPicker";
          configuredProperty.value = reduxVal;
        } else if (ele.type === "textArea") {
          configuredProperty.elementType = "NormalTextArea";
          configuredProperty.value = { text: reduxVal };
        }
        return configuredProperty;
      });
    })
    .reduce((prev, curr) => {
      return [...prev, ...curr];
    });
}

export const reportViewHcrGenerateReport = ({
  updatedPageNo,
  queries,
  selectedQueryId,
  flowchartInstance,
  hcrDiagramNodesData,
  Notify,
  dispatch,
  canvasProperties,
  setIsPreviewLoading,
  filters,
  reportName,
  isPreviewing,
  saveDetails,
  openMode,
  hcrExportProperties,
  getApiInstance,
  isStreamToggle,
  setPreviewError,
  onStreamSuccess = () => { },
  reportKey,
  subDataSets,
  tableStyles
}) => {
  const query =
    queries.menu.find((query) => query.id === selectedQueryId) || {};
  const { isValid, bandLimits } = validateNodes({
    flowchartInstance,
    hcrDiagramNodesData,
    Notify,
    dispatch,
    canvasProperties,
  });
  if (isValid) {
    setIsPreviewLoading(true);
    const { previewRequest } = requests.cannedReport(dispatch);
    const instance = previewRequest(
      getPreviewFormData({
        hcrDiagramNodesData,
        flowchartInstance,
        query,
        canvasProperties,
        filters,
        reportName,
        bandLimits,
        updatedPageNo,
        saveDetails,
        openMode,
        hcrExportProperties,
        allQueries: queries.menu,
        subDataSets,
        tableStyles
      }),
      (res) => {
        if (isStreamToggle) {
          onStreamSuccess(res);
        } else {
          dispatch(hcrActions.handlePreviewTag({ previewTag: res.response, reportKey }));
          dispatch(
            hcrActions.handlePageDetails({
              reportKey,
              pageDetails: {
                totalPageCount: res?.reportPageInfo?.totalPageCount * 10 || 10,
                currentPageNo: parseInt(res?.reportPageInfo?.currentPageNo) + 1,
              }
            }),
          );
          setIsPreviewLoading(false);
        }
        if (!isPreviewing) {
          dispatch(hcrActions.handleTogglePreview(true));
        }
        // dispatch(storeHCRPreviewDetails(res));
        // Notify.success({
        //     type: "Network Call",
        //     ...res,
        // });
      },
      (e) => {
        setIsPreviewLoading(false);
        setPreviewError(getErrorMessage(e));
        // Notify.error({
        //     type: "Network Call",
        //     ...e,
        // });
      },
      isStreamToggle,
    );
    getApiInstance(instance);
  }
};

export const handleUrlParamsFilters = ({ urlParams, reqFilters = [] }) => {
  Object.entries(urlParams).forEach((entry) => {
    reqFilters.find((fil) => {
      if (fil.label === entry[0]) {
        if (Array.isArray(entry[1])) {
          if (fil.condition === "CONTAINS") {
            fil.values = [entry[1].join(",")];
          } else {
            fil.values = entry[1];
          }
        } else {
          if (fil.condition === "EQUALS") {
            fil.values[0] = entry[1];
            fil.dataId = uuidv4();
          } else {
            fil.values = [entry[1]];
          }
        }
        return true;
      }
      return false;
    });
  });
  return reqFilters.map((obj) => ({ ...obj }));
};

function getHcrUnmodifiedState() {
  const obj = cloneDeep(hcrTabInitialState);
  obj.dsPaneTypes[0].menu[0] = {
    id: 1,
    name: "Query1",
    config: "",
    connectionDetails: null,
    executeQueryData: {
      data: [],
      field: [],
    },
    isNameEditable: false,
    isSaved: true,
    parameterList: [],
  };
  obj.selectedDS = { dataSourcePane: "Query", id: 1 };
  delete obj.canvasProperties.calculations.keyValuePairs.id;
  delete obj.sidebarPaneActiveKey;
  return obj;
}

export const handleEmptyPaneCheck = (panes) => {
  const unModifiedSate = getHcrUnmodifiedState();
  return panes.find((pane) => {
    const modifiedPane = cloneDeep(pane);
    delete modifiedPane.key;
    delete modifiedPane.title;
    delete modifiedPane.uuid;
    delete modifiedPane.sidebarPaneActiveKey;
    delete modifiedPane.canvasProperties?.calculations?.keyValuePairs?.id;
    return isEqual(modifiedPane, unModifiedSate);
  });
};

export const getMargin = ({ canvasMargin = {}, key }) => {
  if (key in (canvasMargin || {})) {
    return canvasMargin[key];
  } else {
    return 20;
  }
};

export function intToStringFuncHcr(dataSources) {
  dataSources?.forEach((ele) => {
    ele.data && (ele.data.id += "");
  });
  return dataSources;
}

export const dataProcessorHcr = ({ data, dispatch }) => {
  let sortedAllDataTypes, advancedDataSourceTypes;
  let categoryTypes = [];
  let categoryInfo = {};
  let categories = {};
  sortedAllDataTypes = data.dataSources.sort((a, b) =>
    a["name"].localeCompare(b["name"]),
  );
  advancedDataSourceTypes = data.dataSourceTypes.sort((a, b) =>
    a.name.localeCompare(b.name),
  );
  sortedAllDataTypes.map(function (val) {
    if (categoryTypes.indexOf(val.categoryType.toLowerCase()) == -1) {
      categoryTypes.push(val.categoryType);
      categoryInfo[val.categoryType] = val.categoryName;
    }
  });
  let dataSources = data.dataSources,
    allDataSources = data.allDataSources;
  let datasourceListToRender = {};
  allDataSources = allDataSources.map((ds) => {
    if (!ds.driver) {
      ds.driver = "dynamicSwitch";
      ds.data.driver = "dynamicSwitch";
    }
    return ds;
  });
  dataSources.map((category) => {
    allDataSources.map((ds) => {
      if (
        category.driver &&
        ((category.sendVendorName && ds.vendorName === category.name) ||
          (!category.sendVendorName &&
            !ds.vendorName &&
            ds.driver === category.driver))
      ) {
        let key = randomString();
        if (!(category.name in datasourceListToRender)) {
          categories[key] = { ds: ds, category };
          datasourceListToRender[category.name] = {
            name: category.name,
            children: [],
            key,
            uuid: key,
            keyPath: key,
            category: "dsGroup",
            imgUrl: (category?.imgUrl || "")?.split("../")[1] || "",
          };
        }
        let _key = randomString();
        datasourceListToRender[category.name].children.push({
          ...ds,
          category: "dataSource",
          children: [],
          driverType: category.name,
          keyPath: `${datasourceListToRender[category.name].keyPath}/${_key}`,
          key: _key,
          uuid: _key,
        });
      }
    });
  });
  datasourceListToRender = Object.values(datasourceListToRender);

  let dataForRedux = {
    allDataSourceTypes: sortedAllDataTypes,
    driversList: data.driversList,
    // allDataSources: data.allDataSources,
    dataSourceTypes: advancedDataSourceTypes,
    datasourceListToRender,
  };

  dispatch(hcrActions.handleHcrDatasourceList(dataForRedux));
};

export const fetchGetDataSourceHcr = ({ dispatch, cb = () => { } }) => {
  let data = {};
  requests.metadata(dispatch).getDataSource(
    (res) => {
      data = res;
      requests.metadata(dispatch).listDataSources(
        { classifier: "all" },
        (listDsRes) => {
          intToStringFuncHcr(listDsRes.dataSources);
          let result = {
            allDataSources: listDsRes.dataSources,
            ...data,
          };
          dataProcessorHcr({ data: result, dispatch });
          cb()
        },
        () => { },
      );
    },
    () => { },
  );
};

export const getCalcParamsNode = ({ name, label }) => {
  return {
    name,
    width: 100,
    height: 40,
    label,
    renderKey: "text",
    isLeaf: true,
    repeat: "rd",
    category: "text",
    zIndex: 10,
    type: "calculatedParams",
    borders: {},
    padding: {},
    // fontSize: 10,
  };
};

export function hexaToRgba(hex = "#000000ff") {
  let r = 0,
    g = 0,
    b = 0,
    a = 1;
  if (hex.length === 7) {
    hex += "ff";
  }
  if (hex.length === 9) {
    r = parseInt(hex.slice(1, 3), 16);
    g = parseInt(hex.slice(3, 5), 16);
    b = parseInt(hex.slice(5, 7), 16);
    a = parseInt(hex.slice(7, 9), 16) / 255;
  }
  return `rgba(${r}, ${g}, ${b}, ${a})`;
}

export function getHexa(hex, alpha = 1) {
  if (hex.length === 7) {
    const a = Math.round(alpha * 255)
      .toString(16)
      .padStart(2, "0");
    return hex + a;
  }
  return hex;
}

export const fetchImagesData = (dispatch, refresh, getResources) => {
  dispatch(fileBrowserActions.setFbLoading(true));
  const makeRequest = () => {
    getResources(
      (res) => {
        // const filesWithLocations = addLogicalPaths(res);
        dispatch(
          hcrActions.handleHcrImagesList({
            // data: filesWithLocations.length ? filesWithLocations[0] : [],
            data: res,
            error: null,
            loading: false,
          }),
        );
        dispatch(fileBrowserActions.setSearchResults(null));
        dispatch(fileBrowserActions.setShowFileBrowser(true));
      },
      (error) => {
        dispatch(fileBrowserActions.setFbError(error));
      },
    );
  };
  if (refresh) {
    makeRequest();
    return;
  }
  dispatch((dispatch, getState) => {
    const files = getState().cannedReports?.imagesList?.data || [];
    if (files.length === 0) makeRequest();
    if (files.length > 0) dispatch(fileBrowserActions.setFbLoading(false));
  });
};

export const getFilterValueForHCR = ({
  reqQuery = {},
  dispatch,
  successCB = () => { },
  errorCB = () => { },
}) => {
  const { saveExecuteReportQuery } = requests.cannedReport(dispatch);
  const connectionDetails = reqQuery?.connectionDetails;

  const formData = {
    // formdata to fetch full data of the parameter query (filter)
    mapJson: {
      map_id: 1,
      type: connectionDetails?.baseType,
      temp_uuid: reqQuery.temp_uuid,
    },
    limit: "full",
  };

  return saveExecuteReportQuery(
    formData,
    (exeQryRes) => {
      if (typeof successCB === "function") successCB(exeQryRes);
    },
    (exeQryErr) => {
      console.log(exeQryErr);
      if (typeof errorCB === "function") errorCB(exeQryErr);
    },
  );
};

export const updateHCRFilterValuesList = (
  data = [],
  filter = {},
  setFilters = () => { },
) => {
  setFilters((prevFilters) => {
    return prevFilters.map((f) => {
      if (f.uid === filter.uid) {
        const { canvasValues = {} } = filter?.orgPara ?? {};
        f.valuesList = data.map((item) => ({
          display: item[canvasValues.display],
          value: item[canvasValues.value],
        }));
        f.loading = false;
      }
      return f;
    });
  });
};

export const updateDateRangeFilterValues = (filters = []) => {
  const rangeFilters = filters.filter((f) => f.condition === "IN_RANGE");
  if (!rangeFilters.length) return filters;

  return filters.map((fltr) => {
    if (fltr.condition === "IN_RANGE") return fltr;
    let filterName = fltr.label;
    let rangeFilterAssociatedWithFltr = rangeFilters.find((f) =>
      f?.range.includes(filterName),
    );
    if (rangeFilterAssociatedWithFltr) {
      let [start, end] = rangeFilterAssociatedWithFltr.range;
      let [startValue, endValue] = rangeFilterAssociatedWithFltr.values;
      if (start === filterName) fltr.values = [startValue];
      if (end === filterName) fltr.values = [endValue];
    }
    return fltr;
  });
};

export const getDefaulPropertiesFromExportProperties = (data = {}) => {
  if (!data || isEmpty(data)) return [];
  const properties = Object.values(data)?.flat(1);
  return properties
    .filter((p) => p.default)
    .map((property) => {
      const { description, key, value, alias = "" } = property || {};
      return {
        id: uuidv4(),
        description,
        key,
        value,
        default: true,
        alias,
      };
    });
};

export const getPastedNodeData = (item = {}, cell = {}) => {
  const { bandType, id, width, nodeIds = [], height } = cell || {}
  let prevNodes = nodeIds.length || 1
  let node = {
    ...item,
    cellId: id,
    band: bandType,
    id: `node-${uuidv4()}`,
    width: width / prevNodes,
    height: height,
  }
  return node;
}

export const checkIfBandIsDeleted = (bandType = "", cells = {}, groupField) => {
  if (!bandType || isEmpty(cells)) return false;
  const bandCells = [];
  for (let cId in cells) {
    const c = cells[cId];
    if (groupField) {
      if (c.bandType === bandType && c.groupField === groupField) {
        bandCells.push(c)
      }
    } else {
      if (c.bandType === bandType) {
        bandCells.push(c)
      }
    }
  }
  if (bandCells.length) {
    return bandCells.every((c) => c.deleted);
  }
  return false;
}

const initialSelectedPayload = {
  selectedColumnId: null,
  selectedBandType: null,
  selectedNodes: [],
  outlineDsSelectedField: null,
  selectedTable: null,
  selectedCalculation: [],
  selectedCells: [],
  selectedGroup: [],
  selectedParameter: [],
  selectedStyle: [],
}

export const updateCanvasTabViewComponent = (node, actionType, payload = {}) => {
  const {
    nodeId,
    cellId,
    columnId,
    bandType,
    width,
    height,
    record,
    targetCellId,
    properties = {},
    selectedNodes = [],
    copiedNodes = [],
    columnIndex,
    cellIds = [],
    selectedCells = [],
    cutNodesData = [],
    outlineDsSelectedField,
    selectedCalculation = [],
    selectedGroup = [],
    groupField = "",
    groupFields = [],
    selectedParameter = [],
    selectedStyle = [],
    activeReport = {},
  } = payload || {};
  const { columnOrder } = node || {};
  const { tableStyles = [] } = activeReport || {}
  function addNewGroup(groupField) {
    [hcrTableBandsTypes.GROUP_HEADER, hcrTableBandsTypes.GROUP_FOOTER].forEach((bandType) => {
      const styleNameReference = tableStyles.filter((s) => s.tableId).find((style) => style.bandsApplicable.includes(bandType))?.id;
      node.bands[bandType].groupFields.push(groupField)
      node.columnOrder.forEach((colId) => {
        const columnWidth = node.columns[colId].width
        const cell = createCell(colId, bandType, columnWidth, node.bands[bandType].height, groupField);
        cell.deleted = true;
        cell.styleNameReference = styleNameReference;
        node.cells[cell.id] = cell;
      })
    })
  }

  function deleteOldGroups() {
    [hcrTableBandsTypes.GROUP_HEADER, hcrTableBandsTypes.GROUP_FOOTER].forEach((bandType) => {
      const groupFields = node.bands[bandType].groupFields || []
      if (groupFields.length) {
        groupFields.forEach((groupField) => {
          node.columnOrder.forEach((colId) => {
            const cellId = makeCellId(colId, bandType, groupField);
            const cell = node.cells[cellId]
            if (cell.nodeIds.length) {
              cell.nodeIds.forEach((nodeId) => {
                delete node.nodes[nodeId]
              })
            }
            delete node.cells[cellId]
          })
        })
      }
      node.bands[bandType].groupFields = []
    })
  }

  function resetSelection() {
    for (let item in initialSelectedPayload) {
      node[item] = initialSelectedPayload[item]
    }
  }

  switch (actionType) {
    case "addColumn": {
      let colId = uuidv4(),
        colIndex = columnIndex;

      node.columnOrder.forEach((id) => {
        let column = node.columns[id];
        let cName = column.name.split(" ");
        if (cName[1] > colIndex) cName[1] = Number(cName[1]) + 1;
        column.name = cName.join(" ");
      })

      node.columnOrder.splice(colIndex, 0, colId);
      node.columns[colId] = { id: colId, width, name: `Column ${colIndex + 1}` };

      let availableBands = getAvailableBands(node.bands)
      availableBands.forEach((band) => {
        const styleNameReference = tableStyles.filter((s) => s.tableId).find((style) => style.bandsApplicable.includes(band))?.id;
        if (isGroupBand(band)) {
          const groupFields = node.bands[band].groupFields || [];
          groupFields.forEach((field) => {
            const cell = createCell(colId, band, width, node.bands[band].height, field);
            const isDeleted = checkIfBandIsDeleted(band, node.cells, field);
            if (isDeleted) cell.deleted = true;
            cell.styleNameReference = styleNameReference;
            node.cells[cell.id] = cell;
          })
        } else {
          const cell = createCell(colId, band, width, node.bands[band].height);
          const isDeleted = checkIfBandIsDeleted(band, node.cells);
          if (isDeleted) cell.deleted = true;
          cell.styleNameReference = styleNameReference;
          node.cells[cell.id] = cell;
        }
      })
      node.width += width;
      break;
    }
    case "removeColumn": {
      const colIndex = columnIndex;

      node.columnOrder.forEach((id) => {
        let column = node.columns[id];
        let cName = column.name.split(" ");
        if (cName[1] > colIndex) cName[1] = Number(cName[1]) - 1;
        column.name = cName.join(" ");
      })

      function deleteCell(columnId, bandType, field) {
        const cellId = makeCellId(columnId, bandType, field);
        const cell = node.cells[cellId];
        if (cell) {
          cell.nodeIds.forEach((nodeId) => {
            delete node.nodes[nodeId];
          })
          delete node.cells[cellId];
        }
      }

      hcrTableBandOrder.forEach((band) => {
        if (isGroupBand(band)) {
          const groupFields = node?.bands?.[band]?.groupFields || [];
          groupFields.forEach((field) => {
            deleteCell(columnId, band, field)
          })
        } else {
          deleteCell(columnId, band);
        }
      })

      node.columnOrder = node.columnOrder.filter(id => id !== columnId);
      delete node.columns[columnId];

      if (node.selectedColumnId === columnId) {
        node.selectedColumnId = null;
        node.selectedCells = [];
        node.selectedNodes = [];
      }

      break;
    }
    case "createCell": {
      const cell = node.cells[cellId];
      cell.deleted = false;
      break;
    }
    case "removeCell": {
      const cell = node.cells[cellId];
      if (!cell) break;

      cell.nodeIds.forEach((nodeId) => {
        delete node.nodes[nodeId];
      })
      cell.nodeIds = [];
      cell.deleted = true

      if (node.selectedCells.includes(cellId)) {
        node.selectedCells = [];
        node.selectedNodes = [];
      }

      break;
    }
    case "removeRow": {
      if (!cellIds.length) break;
      cellIds.forEach((cellId) => {
        const cell = node.cells[cellId];
        cell.nodeIds.forEach((nodeId) => {
          delete node.nodes[nodeId];
        })
        cell.nodeIds = [];
        cell.deleted = true
      })
      break;
    }
    case "createRow": {
      if (!cellIds.length) break;
      cellIds.forEach((cellId) => {
        const cell = node.cells[cellId];
        cell.deleted = false
      })
      break;
    }
    case "resizeCell": {

      function update(cellId, key, value) {
        if (node.cells[cellId]) {
          node.cells[cellId][key] = value

          node.cells[cellId].nodeIds.forEach((nodeId) => {
            if (node.nodes[nodeId]) node.nodes[nodeId][key] = value
          })
        }
      }
      function updateWidth(cell, width) {
        const newWidth = Math.max(50, width)
        node.columns[cell.columnId].width = newWidth

        hcrTableBandOrder.forEach((bandType) => {
          if (isGroupBand(bandType)) {
            const groupFields = node.bands[bandType].groupFields || [];
            groupFields.forEach((gpField) => {
              const cellId = makeCellId(cell.columnId, bandType, gpField);
              update(cellId, "width", newWidth)
            })
          } else {
            const cellId = makeCellId(cell.columnId, bandType);
            update(cellId, "width", newWidth)
          }
        })
      }

      function updateHeight(cell, height) {
        const newHeight = Math.max(25, height)
        node.bands[cell.bandType].height = newHeight

        node.columnOrder.forEach((colId) => {
          if (isGroupBand(cell.bandType)) {
            const cellId = makeCellId(colId, cell.bandType, cell.groupField);
            update(cellId, "height", newHeight)
          } else {
            const cellId = makeCellId(colId, cell.bandType);
            update(cellId, "height", newHeight)
          }
        })
      }

      if (cellIds.length) {
        cellIds.forEach((cellId) => {
          const cell = node.cells[cellId];
          if (width !== undefined) updateWidth(cell, width)
          if (height !== undefined) updateHeight(cell, height)
        })
      } else {
        const cell = node.cells[cellId];
        if (!cell) break;
        if (width !== undefined) updateWidth(cell, width)
        if (height !== undefined) updateHeight(cell, height)
      }
      break
    }
    case "addNode": {
      const cell = node.cells[cellId]
      if (!cell) break;

      const newNode = getDroppedNodeData(record, cell)
      node.nodes[newNode.id] = newNode;
      cell.nodeIds.push(newNode.id);

      // if nodes already present in this cell
      cell.nodeIds.forEach((nId, i, arr) => {
        let cellNode = node.nodes[nId]
        cellNode.width = cell.width / (arr.length || 1)
      })
      break;
    }
    case "deleteNode": {
      if (node.selectedNodes.length) {
        node.selectedNodes.forEach((nodeId) => {
          const nodeToDelete = node.nodes[nodeId]
          if (!nodeToDelete) return;

          const cell = node.cells[nodeToDelete.cellId]
          if (cell) {
            cell.nodeIds = cell.nodeIds.filter((id) => id !== nodeId);
          }
          delete node.nodes[nodeId];
        })
        node.selectedNodes = [];
      } else {
        if (nodeId) {
          const nodeToDelete = node.nodes[nodeId]
          if (!nodeToDelete) return;
          const cell = node.cells[nodeToDelete.cellId]
          if (cell) {
            cell.nodeIds = cell.nodeIds.filter((id) => id !== nodeId);
          }
          delete node.nodes[nodeId];
        }
      }

      break;
    }
    case "moveNode": {
      const nodeToMove = node.nodes[nodeId]
      const targetCell = node.cells[targetCellId]
      if (!nodeToMove || !targetCell) break;

      const sourceCell = node.cells[nodeToMove.cellId]
      if (sourceCell) {
        sourceCell.nodeIds = sourceCell.nodeIds.filter((id) => id !== nodeId);

        // reset source cell nodes width
        if (sourceCell.nodeIds.length) {
          sourceCell.nodeIds.forEach((nId, i, arr) => {
            let cellNode = node.nodes[nId]
            cellNode.width = sourceCell.width / (arr.length || 1)
          })
        }
      }
      targetCell.nodeIds.push(nodeId);


      nodeToMove.cellId = targetCellId;
      nodeToMove.band = targetCell.bandType;
      nodeToMove.width = targetCell.width;
      nodeToMove.height = targetCell.height;

      // set targetCell nodes width and other properties
      targetCell.nodeIds.forEach((nId, i, arr) => {
        let cellNode = node.nodes[nId]
        cellNode.width = targetCell.width / (arr.length || 1)
        HCR_TABLE_CELL_PROPERTIES.forEach((property) => {
          if (targetCell[property]) cellNode[property] = targetCell[property]
        })
      })
      break;
    }
    case "selectColumn": {
      resetSelection()
      node.selectedColumnId = columnId;
      break;
    }
    case "selectRow": {
      resetSelection()
      node.selectedBandType = bandType;
      break;
    }
    case "selectCells": {
      resetSelection()
      node.selectedColumnId = columnId;
      node.selectedBandType = bandType;
      node.selectedCells = selectedCells;
      break;
    }
    case "selectNode": {
      resetSelection()
      node.selectedNodes = [nodeId];
      break;
    }
    case "selectNodes": {
      resetSelection()
      node.selectedNodes = selectedNodes;
      break;
    }
    case "clearSelection": {
      resetSelection()
      break;
    }
    case "removeSelectedNode": {
      resetSelection()
      node.selectedNodes = (node.selectedNodes || []).filter((id) => id !== nodeId)
      break;
    }
    case "removeSelectedCell": {
      resetSelection()
      node.selectedCells = (node.selectedCells || []).filter((id) => id !== cellId)
      break;
    }
    case "selectOutlineDSField": {
      resetSelection()
      node.outlineDsSelectedField = outlineDsSelectedField;
      break;
    }
    case "selectTable": {
      resetSelection()
      node.selectedTable = node.id;
      break;
    }
    case "selectCalculation": {
      resetSelection()
      node.selectedCalculation = selectedCalculation;
      break;
    }
    case "selectGroup": {
      resetSelection()
      node.selectedGroup = selectedGroup;
      break;
    }
    case "selectParameter": {
      resetSelection()
      node.selectedParameter = selectedParameter;
      break;
    }
    case "selectTableStyle": {
      resetSelection()
      node.selectedStyle = selectedStyle;
      break;
    }
    case "pasteCopiedNodes": {
      if (node.selectedCells.length || node.selectedNodes.length) {
        function update(cell) {
          copiedNodes.forEach((n) => {
            let newNode = getPastedNodeData(n, cell)
            cell.nodeIds.push(newNode.id)
            node.nodes[newNode.id] = newNode
          })

          cell.nodeIds.forEach((nId, i, arr) => {
            let cellNode = node.nodes[nId]
            cellNode.width = cell.width / (arr.length || 1)
          })
        }
        if (node.selectedNodes.length) {
          node.selectedNodes.forEach((nodeId) => {
            let cell = node.cells[node.nodes[nodeId].cellId];
            if (cell) {
              update(cell)
            }
          })
        } else {
          let cell = node.cells[node.selectedCells?.[0]]
          if (cell) {
            update(cell)
          }
        }
      } else {
        copiedNodes.forEach((n) => {
          let newId = `node-${uuidv4()}`
          let cell = node.cells[n.cellId]
          if (cell) {
            cell.nodeIds.push(newId)
            node.nodes[newId] = { ...n, id: newId }

            cell.nodeIds.forEach((nId, i, arr) => {
              let cellNode = node.nodes[nId]
              cellNode.width = cell.width / (arr.length || 1)
            })
          }
        })
      }
      break;
    }
    case "cutNodes": {
      if (cutNodesData.length) {
        cutNodesData.forEach(({ id: nodeId, cellId }) => {
          delete node.nodes[nodeId]
          const cell = node.cells[cellId]
          if (cell) {
            cell.nodeIds = cell.nodeIds.filter((nId) => nId !== nodeId)
          }
        })
      }
    }
    case "updateNodeProperties": {
      if (node.selectedNodes.length) {
        node.selectedNodes.forEach((nodeId) => {
          let nodeToUpdate = node.nodes?.[nodeId]
          if (nodeToUpdate) {
            node.nodes[nodeId] = { ...nodeToUpdate, ...properties }
          }
        })
      }
      break;
    }
    case "updateCellProperties": {
      if (cellIds.length) {
        cellIds.forEach((cellId) => {
          let cellToUpdate = node.cells?.[cellId];
          if (cellToUpdate) {
            node.cells[cellId] = { ...cellToUpdate, ...properties }
          }
        })
      }
      break;
    }
    case "createNewColAndAddNodes": {
      let colId = uuidv4();
      node.columns[colId] = { id: colId, width, name: `Column ${node.columnOrder.length + 1}` };
      node.columnOrder.push(colId);
      node.width += width;
      const availableBands = getAvailableBands(node.bands)
      availableBands.forEach((band) => {
        const styleNameReference = tableStyles.filter((s) => s.tableId).find((style) => style.bandsApplicable.includes(band))?.id;
        if (isGroupBand(band)) {
          const groupFields = node.bands[band].groupFields || [];
          groupFields.forEach((field) => {
            const cell = createCell(colId, band, width, node.bands[band].height, field);
            const isDeleted = checkIfBandIsDeleted(band, node.cells, field);
            if (isDeleted) cell.deleted = true;
            cell.styleNameReference = styleNameReference
            node.cells[cell.id] = cell;
          })
        } else {
          const cell = createCell(colId, band, width, node.bands[band].height);
          const isDeleted = checkIfBandIsDeleted(band, node.cells);
          if (isDeleted) cell.deleted = true;

          if (band === hcrTableBandsTypes.COLUMN_HEADER && !isDeleted) {
            const newNode = getDroppedNodeData({ ...record, backendDataType: null, label: record.name }, cell)
            node.nodes[newNode.id] = newNode;
            cell.nodeIds.push(newNode.id);
          }
          if (band === hcrTableBandsTypes.COLUMN_DATA && !isDeleted) {
            const newNode = getDroppedNodeData(record, cell)
            node.nodes[newNode.id] = newNode;
            cell.nodeIds.push(newNode.id);
          }
          cell.styleNameReference = styleNameReference
          node.cells[cell.id] = cell;
        }
      })
      break;
    }
    case "tableProperties": {
      for (let property in properties) {
        node[property] = properties[property]
      }
      break;
    }
    case "updateImageNode": {
      let nodeToUpdate = node.nodes?.[nodeId]
      if (nodeToUpdate) {
        node.nodes[nodeId] = { ...nodeToUpdate, ...properties }
      }
      break;
    }
    case "addNewGroup": {
      addNewGroup(groupField);
      break;
    }
    case "deleteGroup": {
      [hcrTableBandsTypes.GROUP_HEADER, hcrTableBandsTypes.GROUP_FOOTER].forEach((bandType) => {
        node.bands[bandType].groupFields = node.bands[bandType].groupFields.filter((field) => field !== groupField)

        node.columnOrder.forEach((colId) => {
          const cellId = makeCellId(colId, bandType, groupField);
          const cell = node.cells[cellId]
          if (cell.nodeIds.length) {
            cell.nodeIds.forEach((nodeId) => {
              delete node.nodes[nodeId]
            })
          }
          delete node.cells[cellId]
        })
      })
      break;
    }
    case "deleteOldGroups": {
      deleteOldGroups()
      break;
    }
    case "removeOldGroupsAndAddNew": {
      deleteOldGroups(node)
      groupFields.forEach((field) => {
        addNewGroup(field)
      })
      break;
    }
    default:
      break
  }
}

const getConvertedAdvanceTableNodes = (nodes) => {
  if (!nodes.length) return []

  return nodes.map((node) => {
    let { tableCells = [], groupChildren = [], isGroup, tableConfig = {}, ...rest } = node || {}

    tableCells = tableCells.reduce((acc, tCell) => {
      const { cell, columnIndex, group, offset, parent, selectable, sibling, ...rest } = tCell || {}
      let newCell = { ...rest, band: cell }
      if (acc[columnIndex]) {
        acc[columnIndex].push(newCell)
      } else {
        acc[columnIndex] = [newCell]
      }
      return acc
    }, {})

    tableCells = Object.values(tableCells)

    const advanceTableConfig = getAdvancedTableConfig(
      tableCells.length,
      HCR_TABLE_DATA_CELL_WIDTH,
      tableConfig,
      tableCells
    ) || {}

    return {
      ...rest,
      ...advanceTableConfig,
      tableConfig,
      name: 'advancedTable',
      renderKey: 'advancedTable',
      category: 'advancedTable',
    }
  })
}

export const parseHCRNodesData = (nodes = []) => {
  if (!nodes.length) return []
  let copiedNodes = cloneDeep(nodes)
  const isTableNodePresent = copiedNodes.find(node => node.category === "table")
  if (!isTableNodePresent) return copiedNodes;

  const tableNodes = copiedNodes
    .filter(node => ["table"].includes(node.category))
    .map((tNode) => {
      const id = tNode.id
      let cells = copiedNodes.filter(node => node.parent === id) || []
      tNode.tableCells = cells
      return tNode
    });
  copiedNodes = copiedNodes.filter((node) => !["table"].includes(node.category) && !node.isTableCell);
  copiedNodes = [...copiedNodes, ...(getConvertedAdvanceTableNodes(tableNodes) || [])]
  return copiedNodes;
}

export const getSubDataSetsFromReportState = (nodes, dsPanes) => {
  if (!nodes.length) return []

  const advancedTables = nodes.filter(node => node.category === "advancedTable");
  if (!advancedTables.length) return [];

  return advancedTables.map((table) => {
    const { selectedQueryID, selectedGroupFields = [], selectedFields = [], id: tableId } = table || {}
    if (!selectedQueryID) { // empty table usecase
      return {
        ...getInitialSubDataSet(),
        id: `EMPTY_${tableId}`
      }
    }

    const queriesMenu = dsPanes
      ?.find((ele) => ele.dataSourcePane === hcrDSQuery)
      ?.menu?.filter(
        (ele) =>
          ele.executeQueryData?.data.length ||
          ele.executeQueryData?.field.length,
      ) || [];
    const selectedQuery = queriesMenu?.find((ele) => ele.id === selectedQueryID);
    if (!selectedQuery) return null;

    const { executeQueryData, name } = selectedQuery || {};
    const { field = [] } = executeQueryData || {};

    let initialSubDataSet = getInitialSubDataSet();
    initialSubDataSet = {
      ...initialSubDataSet,
      id: selectedQueryID,
      name,
      isEmpty: false,
      fields: field.map((f) => ({ ...f, id: uuidv4() })),
      selectedFields,
      selectedGroupFields,
    }

    if (selectedGroupFields.length) {
      initialSubDataSet.groups = selectedGroupFields.map((field) => getInitialGroupData(field));
    }
    return initialSubDataSet;

  }).filter(Boolean)
}

export const getTableStylesFromReportState = (nodes) => {
  if (!nodes.length) return {
    tableStyles: [],
    alteredNodes: nodes
  }

  const advancedTables = nodes.filter(node => node.category === "advancedTable");
  if (!advancedTables.length) return {
    tableStyles: [],
    alteredNodes: nodes
  }
  const tableStyles = advancedTables.map((table, index) => {
    const { id: tableId } = table || {}
    return getTableStyles(tableId, index)
  }).flat(1)

  const alteredNodes = nodes.map((node) => {
    if (node.category === "advancedTable") {
      const { cells = {}, id: tableId } = node || {}
      const styles = tableStyles.filter((style) => style.tableId === tableId)
      for (let cellId in cells) {
        let cell = cells[cellId];
        const styleNameReference = styles.find((style) => style.bandsApplicable.includes(cell.bandType))?.id || null;
        node.cells[cellId].styleNameReference = styleNameReference;
      }
    }
    return node;
  })
  return {
    tableStyles,
    alteredNodes
  }
}

export const getInitialSubDataSet = () => {
  return {
    id: "",
    name: "",
    isEmpty: true,
    fields: [],
    calculations: [],
    groups: [],
    variables: [],
    parameters: [],
    selectedFields: [],
    selectedGroupFields: [],
  }
}

export const getInitialParameter = (name) => {
  return {
    id: uuidv4(),
    name,
    config: "",
    connectionDetails: null,
    executeQueryData: { data: [], field: [] },
    isNameEditable: false,
    isSaved: true,
    type: 'java.lang.String',
    parameterList: [],
    canvasValues: {
      filterType: hcrParaInput,
      multipleType: false,
      disabled: false,
      isChecked: false,
      open: "'",
      close: "'",
      defaultValue: ""
    },
    mappingExpression: "",
    isSubDSParameter: true
  }
}

export const updateSubDataSets = (activeReport, actionType, payload = {}) => {
  const {
    id = "",
    name = "",
    fields = [],
    calculations = [],
    groups = [],
    selectedFields = [],
    selectedGroupFields = [],
    propertiesToUpdate = [],
    parameters = [],
    parameter = {}
  } = payload || {};


  function update(id, key, value) {
    activeReport.subDataSets = activeReport.subDataSets.map((subDataSet) => {
      if (subDataSet.id === id) {
        return {
          ...subDataSet,
          [key]: value
        }
      }
      return subDataSet
    })
  }

  switch (actionType) {
    case "add": {
      let initialSubDataSet = getInitialSubDataSet();
      initialSubDataSet = {
        ...initialSubDataSet,
        isEmpty: false,
        ...(fields.length && { fields: fields.map((field) => ({ ...field, id: uuidv4() })) }),
        ...(calculations.length && { calculations }),
        ...(groups.length && { groups }),
        ...(id && { id }),
        ...(name && { name }),
        ...(selectedFields.length && { selectedFields }),
        ...(selectedGroupFields.length && { selectedGroupFields })
      }
      activeReport.subDataSets.push(initialSubDataSet);
      break;
    }

    case "addEmpty": {
      let emptySubDataSet = getInitialSubDataSet();
      emptySubDataSet = {
        ...emptySubDataSet,
        id: `EMPTY_${id}`,
      }
      activeReport.subDataSets.push(emptySubDataSet);
      break;
    }
    case "updateFields": {
      update(id, "fields", fields);
      break;
    }
    case "updateGroups": {
      update(id, "groups", groups);
      break;
    }
    case "updateCalculations": {
      update(id, "calculations", calculations);
      break;
    }
    case "updateMany": {
      propertiesToUpdate.forEach(({ key, value }) => {
        update(id, key, value);
      })
      break;
    }
    case "addNewParameter": {
      const parameters = activeReport.subDataSets.find((subDataSet) => subDataSet.id === id)?.parameters || [];
      let name = "Parameter 1";
      let counter = 1
      while (parameters.some((parameter) => parameter.name === name)) {
        name = `Parameter ${counter}`
        counter++
      }
      const newParameter = getInitialParameter(name);
      update(id, "parameters", [...parameters, newParameter])

      break;
    }
    case "updateParameters": {
      update(id, "parameters", parameters);
      break;
    }
    case "updateParameterMapping": {
      const { paramId, expression } = parameter || {}
      activeReport.subDataSets = activeReport.subDataSets.map((subDataSet) => {
        if (subDataSet.id === id) {
          subDataSet.parameters = subDataSet.parameters.map((param) => {
            if (param.id == paramId) {
              return {
                ...param,
                mappingExpression: expression
              }
            }
            return param
          })
        }
        return subDataSet
      })
    }
    default:
      break;
  }
}

export const getInitialStyles = () => {
  return {
    borders: {},
    padding: {},
    fontFill: "#000000",
    fill: "#fefefe",
    fontSize: 10,
    fontFamily: "Serif",
    bold: false,
    italic: false,
    underLine: false,
    strikeThrough: false,
    verticalAlign: 'middle',
    horizontalAlign: "center",
    mode: "Transparent",
    rotation: "None",
    stroke: 1,
    style: 'Solid',
    color: "#000000",
    styleName: 'None'
  }
}

export const getNewStyle = (tableId, prevStyles) => {
  let styleName = "Style 1", counter = 1;
  while (prevStyles.some((style) => style.styleName === styleName)) {
    counter += 1;
    styleName = `Style ${counter}`;
  }

  return {
    styleName,
    bandsApplicable: [
      hcrTableBandsTypes.TABLE_HEADER,
      hcrTableBandsTypes.COLUMN_HEADER,
      hcrTableBandsTypes.GROUP_HEADER,
      hcrTableBandsTypes.COLUMN_DATA,
      hcrTableBandsTypes.GROUP_FOOTER,
      hcrTableBandsTypes.COLUMN_FOOTER,
      hcrTableBandsTypes.TABLE_FOOTER
    ],
    id: uuidv4(),
    tableId,
    isChanged: false,
    isConditionalStyleReq: true,
    expression: "",
    expressionBackColor: "#BFE1FF",
    borders: {
      Top: {
        stroke: 1,
        style: "SOLID",
        color: "#000103"
      },
      Bottom: {
        stroke: 1,
        style: "SOLID",
        color: "#000103"
      },
      Right: {
        stroke: 1,
        style: "SOLID",
        color: "#000103"
      },
      Left: {
        stroke: 1,
        style: "SOLID",
        color: "#000103"
      }
    },
    padding: {
      Top: 0,
      Bottom: 0,
      Right: 0,
      Left: 0
    },
    lineStyles: {
      stroke: 1,
      style: "SOLID",
      color: "#000000"
    },
    mode: "Opaque",
    fontFill: "#000000",
    fill: "#F0F8FF",
    fontSize: 10
  }
}

export const updateElementsWithStyles = (previousStyles = [], updatedStyle = [], nodes = []) => {
  if (!nodes.length || !previousStyles.length) return nodes;

  const prevStyleNames = previousStyles.map((style) => style.styleName),
    prevStyleIds = previousStyles.map((style) => style.id),
    updatedStyleNames = updatedStyle.map((style) => style.styleName),
    updatedStyleIds = updatedStyle.map((style) => style.id);

  function isStyleNameAvailableInBoth(styleName) {
    if (!prevStyleNames.includes(styleName)) return true;
    return prevStyleNames.includes(styleName) && updatedStyleNames.includes(styleName)
  }

  function isStyleIdAvailableInBoth(styleId) {
    return prevStyleIds.includes(styleId) && updatedStyleIds.includes(styleId)
  }

  function updateNormalNode(node = {}) {
    if (!node) return node;
    let copiedNode = { ...node }
    if (copiedNode.name === 'line') {
      if (copiedNode?.lineStyles?.styleName && !isStyleNameAvailableInBoth(copiedNode?.lineStyles?.styleName)) {
        copiedNode.lineStyles = {
          ...(copiedNode.lineStyles || {}),
          stroke: 1,
          style: 'Solid',
          color: "#000000",
          styleName: 'None'
        }
      }
    } else {
      if (copiedNode.styleName && !isStyleNameAvailableInBoth(copiedNode.styleName)) {
        copiedNode = {
          ...copiedNode,
          ...getInitialStyles()
        }
      }
    }
    return copiedNode
  }

  function updateTableCell(cell = {}) {
    const copiedCell = { ...cell }
    if (copiedCell.styleName && !isStyleNameAvailableInBoth(copiedCell.styleName)) {
      copiedCell.styleNameReference = undefined
    }
    return copiedCell;
  }


  return nodes.map((node) => {
    if (["advancedTable"].includes(node.category)) {
      const { cells = {}, nodes = {} } = node || {};
      for (let nodeId in nodes) {
        node.nodes[nodeId] = updateNormalNode(node.nodes[nodeId]);
      }

      for (let cellId in cells) {
        node.cells[cellId] = updateTableCell(node.cells[cellId])
      }
    } else {
      node = updateNormalNode(node);
    }
    return node;
  })
}

export const updateTableStyles = (activeReport, actionType, payload = {}) => {
  const {
    styleId,
    newStyles = [],
    tableId,
    updatedStyles = {},
    styles = []
  } = payload || {};

  switch (actionType) {
    case "addNewStyles": {
      activeReport.tableStyles = [...(activeReport.tableStyles || []), ...newStyles];
      break;
    }
    case "updateStyle": {
      activeReport.tableStyles = [...(activeReport.tableStyles || [])].map((style) => {
        if (style.id === styleId) {
          return { ...style, ...updatedStyles }
        }
        return style;
      });


      // update any component that is using this style 
      const currentStyle = activeReport.tableStyles.find((style) => style.id === styleId);
      const styleName = currentStyle?.styleName || '';

      function updateStyle(node, styleName, currentStyle) {
        if (node.name === 'line') {
          if (node.lineStyles.styleName === styleName) {
            const { id = '', ...restStyles } = currentStyle?.lineStyles || {}
            node = {
              ...node,
              lineStyles: {
                ...node.lineStyles,
                ...restStyles
              }
            }
          }
        } else {
          if (node?.styleName === styleName) {
            const { id = '', isChanged, isConditionalStyleReq, tableId, bandsApplicable, ...restStyles } = currentStyle || {}
            node = {
              ...node,
              ...restStyles,
            }
          }
        }
        return node;
      }

      activeReport.hcrDiagramNodesData = activeReport.hcrDiagramNodesData.map((node) => {
        if (node.category === "advancedTable") {
          const { nodes = {} } = node || {}
          for (let nId in nodes) {
            const element = nodes[nId];
            node.nodes[nId] = updateStyle(element, styleName, currentStyle);
          }
        } else {
          node = updateStyle(node, styleName, currentStyle)
        }
        return node;
      })

      break;
    }
    case "createStyle": {
      const newStyle = getNewStyle(tableId, activeReport.tableStyles);
      activeReport.tableStyles.push(newStyle);
      break;
    }
    case "deleteStyleByTableId": {
      activeReport.tableStyles = (activeReport.tableStyles || []).filter((style) => style.tableId !== tableId);
      break;
    }
    case "deleteStyleById": {
      const prevStyles = cloneDeep(activeReport.tableStyles);
      activeReport.tableStyles = (activeReport.tableStyles || []).filter((style) => style.id !== styleId);
      activeReport.hcrDiagramNodesData = updateElementsWithStyles(prevStyles, activeReport.tableStyles, activeReport.hcrDiagramNodesData)
      break;
    }
    case "updateStyles": {
      const prevStyles = cloneDeep(activeReport.tableStyles);
      activeReport.tableStyles = styles;
      activeReport.hcrDiagramNodesData = updateElementsWithStyles(prevStyles, styles, activeReport.hcrDiagramNodesData)
      break;
    }
    case "updateAltRowBanding": {
      activeReport.tableStyles = [...(activeReport.tableStyles || [])].map((style) => {
        if (style.id === styleId) {
          return { ...style, ...updatedStyles }
        }
        return style;
      });
      break;
    }
    default:
      break;
  }
}