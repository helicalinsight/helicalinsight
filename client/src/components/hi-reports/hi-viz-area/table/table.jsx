import { CheckOutlined, MoreOutlined } from "@ant-design/icons";
import { Dropdown, Menu, Table, Tooltip } from "antd";
import cn from "classnames";
import { cloneDeep } from "lodash";
import React, { useEffect, useRef, useState } from "react";
import { useDispatch } from "react-redux";
import { changeTableRecordsPerPage, setMenuData, updateTableFilters } from "../../../../redux/actions/hreport.actions";
import { getFieldDisplayName } from "../../../../utils/utilities";
import { generateColorRange, generateColorsFromRange, getDataIndex, getEachFieldColor, getFieldData, getFieldName, getHTMLColorFormat } from "../../hi-editing-area/utils/property-utils";
import "../../hi-reports-print.scss";
import CellCard from "../cell-card";
import { checkParentElementBoundingRect } from "../pivot-view/utils/chart";
import Toolbar from "../toolbar";
import generateColor from "../utils/gradient";
import {
  createColorsList,
  createMaxValue,
  createsizesList,
  getColorsByThemeColors,
  getCustomFormatColor,
  getMarks,
  getPropertyElement,
  getPropertyFieldInfo,
  getPropertyStyle,
  getPropertyText
} from "../utils/utillities";
import VizTooltip from "../viz-tooltip";
import { addContainerHeightForPrint, calculateScrollWidth, checkIsItemPresent, checkIsItemPresentByName, checkIsNumeric, checkMeasureFieldsExistInRowsColumns, getTableHiddenAndTotalFields, getTableOperations, getTableTotalFragment, getTotals, handleMeasureFieldsFormatting, isTableHasAnyFieldWithThisProperty, measureTextWidth, prepareTempField } from "./table-utils";
import "./table.scss";

const initialHiddenFieldsData = {
  fields: [],
  activeFields: []
}

const initialTotalFieldsData = {
  fields: [],
  activeFields: []
}

const TableContainer = (props) => {
  let {
    interactiveMode,
    fields,
    marksList,
    drillDown,
    drillThrough,
    data,
    metadata,
    offset,
    pageSize,
    reportId,
    options,
    properties,
    isPrintMode,
    report: activeReport,
    tableRecordsPerPage,
    themeColors = [],
    tableFilters = {}
  } = props;
  let tempFields = fields;
  const dispatch = useDispatch();
  const [hiddenFields, setHiddenFields] = useState(initialHiddenFieldsData);
  const [totalFields, setTotalFields] = useState(initialTotalFieldsData);
  let searchInput = useRef(null)
  const { isPresent: isMeasureNameAndValuePresent, measureFieldName } = checkMeasureFieldsExistInRowsColumns(fields);
  let report = { properties, fields, reportData: activeReport?.reportData };
  const { title, subTitle, format, formatColor, table, tooltip: { showTooltip = true } = {} } = report.properties || {};

  const { recordsPerPage, horizontalScroll, fetchAllRecords, showHiddenFields = false } = table || {}
  if (!showHiddenFields) {
    tempFields = fields.filter((field) => !field.hidden);
  }
  let pageSizeOptions = table.disableDefaultOptions ? [] : [5, 10, 20, 50];
  if (recordsPerPage) { //6687
    // pageSize = recordsPerPage
    let pages = String(recordsPerPage).split(",").filter(Boolean).map(Number);
    pageSizeOptions = [...new Set([...pageSizeOptions, ...pages])].sort((a, b) => {
      return parseInt(a) - parseInt(b);
    })
  }
  if (tableRecordsPerPage > 0) pageSize = tableRecordsPerPage
  // if (recordsPerPage === -1) pageSize = 10
  let titleStyle = {}
  let subTitleStyle = {}

  metadata = metadata || [];
  let columnList = metadata[0];
  let maxValueRef = null;
  let colorsListRef = null;
  let sizesListRef = null;

  useEffect(() => {
    if (tempFields?.length) {
      const { hiddenFlds = {}, totalFlds = {} } = getTableHiddenAndTotalFields(tempFields, table)
      setHiddenFields(hiddenFlds);
      setTotalFields(totalFlds);
    }
  }, [fields, table])

  useEffect(() => {
    return () => {
      dispatch(updateTableFilters({ filters: {}, reportId }));
    }
  }, [])

  if (!data) {
    return null;
  }
  let currentPage = offset / pageSize + 1;
  // let totalRecordsCount = offset + data.length + 1;
  let totalRecordsCount = fetchAllRecords ? data.length + 1 : offset + data.length + 1;
  if (
    pageSize > data.length ||
    (options.sample !== "full" && totalRecordsCount > options.limitBy)
  ) {
    if (data.length) {
      totalRecordsCount = totalRecordsCount - 1;
    }
  }
  const changePage = ({ page }) => {
    if (page === currentPage) {
      return null;
    } else {
      handleGenerate({ page });
    }
  };
  const changePageSize = ({ selectedPageSize }) => {
    if (selectedPageSize === pageSize) {
      return null;
    } else {
      handleGenerate({ selectedPageSize: selectedPageSize, page: currentPage });
    }
  };
  const handleGenerate = ({ selectedPageSize, page }) => {
    let nextPageSize = selectedPageSize ? selectedPageSize : pageSize;
    page = page ? page : currentPage;
    let offset = (page - 1) * nextPageSize;
    let tableLimitBy = nextPageSize;
    if (offset + nextPageSize > options.limitBy) {
      tableLimitBy = options.limitBy - offset;
    }
    // dispatch(resetTableProperty({ property: 'recordsPerPage', value: selectedPageSize }))
    dispatch(changeTableRecordsPerPage({ page: selectedPageSize }))
    props.fetchMoreData({ tableLimitBy, offset, pageSize: nextPageSize, fetchAllRecords });
  };
  const cellClick = (e, { record }) => {
    if (!interactiveMode) {
      // let message = `Please enable interactivity in values tab.`
      // Notify.warning({message});
      return null;
    }
    if (!drillDown && !drillThrough) {
      return null;
    }
    let payload = [];
    Object.keys(record).map((key) => {
      if (key === "key") return null;
      payload.push({ field: key, value: record[key] });
    });
    let element = e.target;
    let { top, right, bottom, left } = checkParentElementBoundingRect(
      "hi-editing-area-container",
      element,
      payload
    );
    let menuData = {
      payload,
      position: { top, right, bottom, left },
      drillDownFilterValues: data,
    };
    dispatch(setMenuData({ reportId, menu: menuData }));
  };

  const renderCell = ({ text, type, record, data, columnList, name, index }) => {
    let isNumeric = checkIsNumeric(columnList, name)

    const { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(report, name)
    let formattedText = getPropertyText({ text, applyOn: "pane", isApplyClicked, fieldType, formatField });

    // --------------- fix for  bug 8239
    let recordForTooltip = cloneDeep(record)
    let recordForActionWindow = cloneDeep(record)
    if (isMeasureNameAndValuePresent) {
      let measureName = ''
      for (let i in record) {
        if (i.includes("MEASURE_FIELD_")) {
          delete recordForTooltip[i]
          delete recordForActionWindow[i]
          measureName = i.replace("MEASURE_FIELD_", "")
          break;
        }
      }
      let { newFormattedText, formattedTextForTooltip, formattedTextForActionWindow } = handleMeasureFieldsFormatting(data, index, formattedText, report, measureName, text, name, measureFieldName)
      if (isNumeric) {
        formattedText = newFormattedText
      }
      if (recordForTooltip?.[measureFieldName]) {
        recordForTooltip[measureFieldName] = formattedTextForTooltip
        recordForActionWindow[measureFieldName] = formattedTextForActionWindow
      }
    }
    // ------------

    let className = cn({
      "text-right": isNumeric,
      "hr-report-cell": true,
    });
    const { backgroundColor } = formatColor || false
    let color = "#000000";
    let cellBackgroundColor = "transparent";
    let fontSize = 13;

    let { colorMarks, sizeMarks } = getMarks(marksList, fields);

    if (colorMarks.length) {
      let isValue = false;
      Object.keys(columnList).map((index) => {
        if (columnList[index].name === colorMarks[0]) {
          if (columnList[index].type === "numeric") {
            isValue = true;
          }
        }
      });
      if (themeColors?.length) {
        let markField = colorMarks[0];
        let colorsList = getColorsByThemeColors(markField, themeColors, data) || {}
        color = colorsList[record[markField]] || "#000000";
      } else {
        if (isValue) {
          maxValueRef = maxValueRef || createMaxValue(colorMarks[0], data);
          let max = maxValueRef;
          let colorValue = (100 * record[colorMarks[0]]) / max;
          color = "#" + generateColor("#0000ff", "#fffff0", colorValue);
        } else {
          colorsListRef = colorsListRef || createColorsList(colorMarks[0], data);
          let colorsList = colorsListRef;
          let { colors } = colorsList;
          color = colors[record[colorMarks[0]]];
        }
      }
    }

    if (sizeMarks.length) {
      let isValue = false;
      Object.keys(columnList).map((index) => {
        if (columnList[index].name === colorMarks[0]) {
          if (columnList[index].type === "numeric") {
            isValue = true;
          }
        }
      });
      if (isValue) {
        maxValueRef = maxValueRef || createMaxValue(colorMarks[0], data);
        let max = maxValueRef;
        fontSize = (100 * record[sizeMarks[0]]) / max;
        fontSize = (10 * fontSize) / 100 + 5;
        fontSize = Math.floor(fontSize);
      } else {
        sizesListRef = sizesListRef || createsizesList(sizeMarks[0], data);
        let sizesList = sizesListRef;
        let { sizes } = sizesList;
        fontSize = sizes[record[sizeMarks[0]]];
      }
    }

    if (formatColor?.formatColorStyle === "gradient") {
      if (formatColor.minimum && formatColor.maximum) {
        const fieldName = getFieldName(formatColor?.formatColorField, report)
        if (fieldName === name) {
          let colors = generateColorRange(formatColor?.minimum, formatColor?.maximum, data.length)
          const fieldData = getFieldData(data, fieldName);
          const uniqueData = [...new Set(fieldData)];
          if (formatColor?.enableSteps) {
            colors = generateColorsFromRange(uniqueData, formatColor);
          }
          index = getDataIndex(uniqueData, text, fieldType)
          color = colors[index]
        }
      }
    }

    if (formatColor?.formatColorStyle === "fieldValue") {
      const fieldName = getFieldName(formatColor?.formatColorField, report)
      if (fieldName === name) {
        if (formatColor?.showAll) {
          color = getEachFieldColor(formatColor, text);
        } else {
          color = getHTMLColorFormat(formatColor.defaultColor);
        }
      }
    }

    if (backgroundColor) {
      const fieldName = getFieldName(formatColor?.formatColorField, report)
      if (fieldName === name) {
        cellBackgroundColor = color
        color = "#000000"
      }
    }

    const customFormatColor = getCustomFormatColor({ text, applyOn: "pane", isApplyClicked, fieldType, formatField })
    if (customFormatColor) color = customFormatColor;

    return (
      <Tooltip
        data-testid="hi-report-table-container-title"
        title={!showTooltip ?
          null : () => (
            <VizTooltip data={recordForTooltip} report={report} format={format} text={text} dispatch={dispatch} />
          )}
        overlayClassName="hi-viz-tooltip-overlay"
      >
        <div
          data-testid={`table-value-${formattedText}-${record.key}`}
          onClick={(e) => cellClick(e, { record: recordForActionWindow })}
          style={{
            color,
            fontSize: `${fontSize}px`,
            textAlign: isNumeric ? "right" : "left",
            backgroundColor: cellBackgroundColor,
            padding: "8px 16px"
          }}
          className={className}
        >
          {formattedText}
        </div>
      </Tooltip>
    );
  };

  const getSummary = () => {
    let columns = Object.keys(columnList)
      .map((clmn) => {
        let { name, type } = columnList[clmn];
        let isCanvasField = false;
        fields
          .filter((field) => {
            return ["row", "column"].includes(field.addedAs)
          })
          .map((field) => {
            let fieldName = getFieldDisplayName(field);
            if (fieldName === name) {
              isCanvasField = true;
            }
          });
        if (!isCanvasField) return null;
        return { name, type }
      })
      .filter((clmn) => !!clmn);

    let isTableHasNumericColumns = columns.some((clm) => clm.type === "numeric");
    const totals = getTotals({ columns, data: tableData, hiddenFields, fields, report, totalFields }) || []

    if (!isTableHasAnyFieldWithThisProperty({ fields, table, property: "total" }) || !isTableHasNumericColumns) return null;
    return (
      <React.Fragment>
        {getTableTotalFragment({ data: totals, title: 'Total' })}
      </React.Fragment>
    )
  }

  const getTableHideMenu = () => {
    let tempPayload = { fields, table }
    if (!isTableHasAnyFieldWithThisProperty({ ...tempPayload, property: "hide" }) && !isTableHasAnyFieldWithThisProperty({ ...tempPayload, property: "total" })) return null;

    const getFieldsMenu = ({ fields, data, onClick = () => { } }) => {
      return (
        fields.map((field) => {
          const fieldName = getFieldDisplayName(field)
          let menuKey = field?.id;
          let isKeyPresent = checkIsItemPresent(data, menuKey);
          return (
            <Menu.Item
              key={menuKey}
              value={menuKey}
              icon={isKeyPresent ? <CheckOutlined /> : null}
              onClick={() => onClick({ key: menuKey, fieldName })}
            >
              <span>{fieldName}</span>
            </Menu.Item>
          )
        })
      )
    }

    const toggleHiddenField = (key, fieldName) => {
      if (checkIsItemPresent(hiddenFields.activeFields, key)) {
        let tempHiddenFields = hiddenFields.activeFields.filter((field) => field?.id !== key)
        setHiddenFields({ ...hiddenFields, activeFields: tempHiddenFields })
      } else {
        let newHiddenField = prepareTempField(fieldName, key)
        setHiddenFields({ ...hiddenFields, activeFields: [...hiddenFields?.activeFields, newHiddenField] })
      }
    }

    const toggleTotalField = (key, fieldName) => {
      if (checkIsItemPresent(totalFields.activeFields, key)) {
        let tempTotalActualFields = totalFields.activeFields.filter((field) => field?.id !== key)
        setTotalFields({ ...totalFields, activeFields: tempTotalActualFields })
      } else {
        let newTotalField = prepareTempField(fieldName, key)
        setTotalFields({ ...totalFields, activeFields: [...totalFields?.activeFields, newTotalField] })
      }
    }

    const menu = (
      <Menu selectedKeys={[...(hiddenFields.activeFields.map(({ id }) => id)), ...(totalFields.activeFields.map(({ id }) => id))]}>
        {isTableHasAnyFieldWithThisProperty({ ...tempPayload, property: 'hide' }) ?
          <Menu.SubMenu key="hide_fields" title="Hidden Fields">
            {getFieldsMenu({ fields: hiddenFields?.fields, data: hiddenFields?.activeFields, onClick: ({ key, fieldName }) => toggleHiddenField(key, fieldName) })}
          </Menu.SubMenu>
          : null}

        {isTableHasAnyFieldWithThisProperty({ ...tempPayload, property: "total" }) ?
          <Menu.SubMenu key="total_fields" title="Show Total">
            {getFieldsMenu({ fields: totalFields?.fields, data: totalFields?.activeFields, onClick: ({ key, fieldName }) => toggleTotalField(key, fieldName) })}
          </Menu.SubMenu>
          : null}
      </Menu >
    )

    return (
      <Dropdown overlay={menu} trigger={["click"]}>
        <Tooltip title="Click this icon to hide or unhide, columns and totals in the table">
          <div className="hreport-table-hide-icon">
            <MoreOutlined />
          </div>
        </Tooltip>
      </Dropdown>
    )
  }

  let columns = Object.keys(columnList)
    .map((clmn) => {
      let { name, type } = columnList[clmn];
      let isCanvasField = false;
      const tableOperations = {
        sort: false,
        sortField: '',
        search: false,
        searchField: '',
        fieldType: type,
        filter: false,
        filterField: '',
        fetchAllRecords,
        searchInput,
        data,
        tableFilters
      }
      fields
        .filter((field) => {
          let fieldName = getFieldDisplayName(field);
          if (checkIsItemPresentByName(hiddenFields.activeFields, fieldName)) return false;
          return ["row", "column"].includes(field.addedAs)
        })
        .map((field) => {
          let fieldName = getFieldDisplayName(field);
          if (fieldName === name) {
            let tableFieldProperty = table[fieldName] || [];
            if (tableFieldProperty.includes("sort")) {
              tableOperations.sort = true;
              tableOperations.sortField = fieldName;
            }
            if (tableFieldProperty.includes("search")) {
              tableOperations.search = true;
              tableOperations.searchField = fieldName;
            }
            if (tableFieldProperty.includes("filter")) {
              tableOperations.filter = true;
              tableOperations.filterField = fieldName;
            }
            type = field.type ? field.type.dataType : type;
            isCanvasField = true;
          }
        });
      if (!isCanvasField) {
        return null;
      }
      return {
        title: name,
        dataIndex: name,
        key: name,
        render: (text, record, index) => renderCell({ text, type, name, record, data, columnList, index }),
        ...getTableOperations(tableOperations)
      };
    })
    .filter((clmn) => !!clmn);

  const pagination = {
    pageSize,
    total: totalRecordsCount,
    current: currentPage,
    pageSizeOptions: pageSizeOptions,
    showSizeChanger: true,
    showTotal: (total, range) => {
      return `${range[0]} - ${currentPage * pageSize} of ${fetchAllRecords ? data?.length : 'many'}`;
    },
    onChange: (e) => {
      changePage({ page: e });
    },
    onShowSizeChange: (current, size) => {
      changePageSize({ selectedPageSize: size });
    },
  };
  let tableData = data.map((record, i) => {
    return { ...record, key: i };
  });
  let height;
  if (props.chartAreaHeight) {
    height = props.chartAreaHeight - 100
  }
  let scrollHeight = height
  let columnsLength = Object.keys(columnList).length

  let scrollWidth = calculateScrollWidth(columns, measureTextWidth);
  // let scrollWidth = Math.pow(columnsLength, 3)
  // console.log({ columnList, scrollWdth, scrollWidth })
  let { show, value, padding, fontSize } = title;
  if (show && value) {
    titleStyle = getPropertyStyle(title)
    scrollHeight = scrollHeight - 30 - fontSize - 2 * padding
  }
  let { show: subShow, value: subValue, padding: subTitlePadding, fontSize: subTitleFontSize } = subTitle;
  if (subShow && subValue) {
    subTitleStyle = getPropertyStyle(subTitle)
    scrollHeight = scrollHeight - 30 - subTitleFontSize - 2 * subTitlePadding
  }
  scrollHeight = scrollHeight < 10 ? 50 : scrollHeight;
  if (isPrintMode) addContainerHeightForPrint(tableData?.length, table);

  const handleTableChange = (pagination, filters) => {
    dispatch(updateTableFilters({ filters, reportId }));
  }

  return (
    <div>
      <Toolbar
        addForwardFilterIPC={props.addForwardFilterIPC}
        deleteBackwardFilterIPC={props.deleteBackwardFilterIPC}
        refreshFiltersIPC={props.refreshFiltersIPC}
        reportId={reportId}
        getApi={props.getApi}
      />
      <div className={'hreport-table'}>
        {getTableHideMenu()}
        <Table
          data-testid="hi-report-table-container"
          scroll={isPrintMode ? { y: 10000000 } : { y: scrollHeight, x: horizontalScroll ? scrollWidth : 0 }}
          width={"100%"}
          columns={columns}
          title={() => {
            if (!title?.show && !subTitle?.show) return null;
            return (
              <div className="title-subtitle-container">
                {title?.position === "top" && getPropertyElement(titleStyle, title)}
                {subTitle?.position === "top" && getPropertyElement(subTitleStyle, subTitle)}
              </div>
            )
          }}
          pagination={isPrintMode ? { pageSize: 10000000, hideOnSinglePage: true } : pagination}
          dataSource={tableData}
          summary={getSummary}
          onChange={handleTableChange}
        />
      </div>
      {interactiveMode && <CellCard reportId={reportId} addFilter={props.addFilter} getApi={props.getApi}
        report={report} format={format} />}
      <div className="title-subtitle-container">
        {title?.position === "bottom" && getPropertyElement(titleStyle, title)}
        {subTitle?.position === "bottom" && getPropertyElement(subTitleStyle, subTitle)}
      </div>
    </div>
  );
};


export default TableContainer
