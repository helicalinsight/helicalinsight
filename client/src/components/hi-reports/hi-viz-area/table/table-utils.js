import { Input, Table } from "antd";
import React from "react";
import { v4 as uuidv4 } from "uuid";
import { getFieldDisplayName } from "../../../../utils/utilities";
import { getPropertyFieldInfo, getPropertyText } from "../utils/utillities";
import { SearchOutlined } from "@ant-design/icons";
import { MEASURE_NAME, MEASURE_VALUE } from "../../../../redux/reducers/initialStates";

export const checkIsNumeric = (columnList, fieldName) => {
  let isNumeric = false
  const column = Object.entries(columnList).find(([key, value]) => value.name === fieldName) || []
  if (column?.length > 1) {
    isNumeric = column[1].type === "numeric"
  }
  return isNumeric
}

export const getTableTotal = (data = [], column, aggregate = 'sum') => {
  let columnName = column?.name;
  let defaultResult = data.reduce((acc, next) => {
    return acc + next[columnName];
  }, 0);
  switch (aggregate) {
    case 'sum':
    case 'count':
      return defaultResult;
    case 'avg':
      return defaultResult / data.length;
    case 'min':
      return Math.min(...data.map((d) => d[columnName]));
    case 'max':
      return Math.max(...data.map((d) => d[columnName]));
    default:
      return defaultResult;
  }
}

export const getTotals = ({ columns = [], data = [], hiddenFields = {}, fields = [], report = {}, totalFields = {} }) => {
  let totals = columns.map((clm) => {
    let isHidden = checkIsItemPresentByName(hiddenFields?.activeFields || [], clm.name);
    let isVisibleField = !checkIsItemPresentByName(totalFields?.activeFields || [], clm.name);
    if (clm.type !== "numeric") return { ...clm, isHidden };
    let aggregate = 'sum';
    const field = fields.find((f) => getFieldDisplayName(f) === clm?.name);
    if (field?.aggregate && field?.aggregate.length) {
      aggregate = field?.aggregate[0]?.split(".")[3];
    }
    let total = getTableTotal(data, clm, aggregate);
    const { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(report, getFieldDisplayName(field))
    let formattedTotal = getPropertyText({ text: total, applyOn: "pane", isApplyClicked, fieldType, formatField });
    return {
      ...clm,
      isHidden,
      total: formattedTotal,
      style: {
        color: '#000',
        visibility: isVisibleField ? 'hidden' : 'visible'
      }
    }
  })
  return totals;
}

export const getTableTotalFragment = ({ data = [] }) => {
  return (
    <React.Fragment >
      <Table.Summary.Row style={{ padding: "8px 16px", backgroundColor: '#fafafa' }} className="ant-table-row">
        {data?.map(({ name, type, total = null, style = {}, isHidden }) => {
          if (isHidden) return null;
          return (
            <Table.Summary.Cell key={name} align={type === "numeric" ? "right" : "left"}>
              <div className="hi-table-summary-title" style={style}>
                {total}
              </div>
            </Table.Summary.Cell>
          )
        })
        }
      </Table.Summary.Row >
    </React.Fragment >
  )
}

export const isTableHasAnyFieldWithThisProperty = ({ fields = [], table = {}, property = '' }) => {
  return fields?.find((field) => {
    const fieldName = getFieldDisplayName(field);
    return table[fieldName]?.includes(property)
  })
}

export const checkIsItemPresent = (data, id) => {
  return data.find((item) => item.id === id);
}

export const checkIsItemPresentByName = (data, name) => {
  return data.find((item) => item.name === name);
}

const checkProperty = (table, fieldName, property) => {
  return table[fieldName]?.includes(property);
}

const prepareNewField = (field = {}) => {
  return {
    ...field,
    id: uuidv4(),
  }
}

export const prepareTempField = (name = '', id = '') => {
  return {
    name,
    id
  }
}

const getTempNewFields = (field) => {
  let tempNewHideField = prepareNewField(field);
  let tempNewTotalField = prepareNewField(field);
  return {
    tempNewHideField,
    tempNewTotalField
  }
}

export const getTableHiddenAndTotalFields = (fields = [], table = {}) => {
  const { hiddenFlds = [], totalFlds = [] } = fields.reduce((acc, field) => {
    const { tempNewHideField = {}, tempNewTotalField = {} } = getTempNewFields(field);

    acc.hiddenFlds.fields.push(tempNewHideField);
    if (tempNewTotalField?.floatingType !== "discrete") {
      acc.totalFlds.fields.push(tempNewTotalField);
    }

    const fieldName = getFieldDisplayName(field);
    if (checkProperty(table, fieldName, "hide")) acc.hiddenFlds.activeFields.push(prepareTempField(fieldName, tempNewHideField?.id));
    if (checkProperty(table, fieldName, "total")) acc.totalFlds.activeFields.push(prepareTempField(fieldName, tempNewTotalField?.id));

    return acc;
  }, {
    hiddenFlds: {
      fields: [],
      activeFields: []
    },
    totalFlds: {
      fields: [],
      activeFields: []
    },
  });
  return { hiddenFlds, totalFlds }
}

export const checkIsTotalEnabled = (table) => {
  if (!table) return null;
  let isTotalEnabled = false;
  for (let i in table) {
    if (Array.isArray(table[i])) {
      if (table[i].includes("total")) isTotalEnabled = true;
    }
  }
  return isTotalEnabled;
}

const getUniqueFilterOptions = (data, filterField) => {
  const uniqueValues = new Map();

  data.forEach(item => {
    const value = item[filterField];
    if (!uniqueValues.has(value)) {
      uniqueValues.set(value, { text: value, value });
    }
  });

  return Array.from(uniqueValues.values());
};

export const addContainerHeightForPrint = async (numberOfRecords, table) => {
  return new Promise((resolve) => {
    let timeout = setTimeout(() => {
      clearTimeout(timeout);
      let hreportTable = document.querySelector(".hreport-table");
      let tableRows = document.querySelectorAll(".ant-table-row");
      if (hreportTable && tableRows) {
        resolve({ hreportTable, table, tableRows, numberOfRecords });
      }
    }, 100)
  }).then(({ hreportTable, table, tableRows }) => {
    let rowHeight = tableRows?.[0]?.offsetHeight;
    let tableHeight = hreportTable?.offsetHeight;
    let totalPages = tableHeight / 1380;

    if (checkIsTotalEnabled(table)) {
      tableHeight += (totalPages * rowHeight)
      totalPages = tableHeight / (1380 + totalPages)
    }

    let parent = document.getElementById("hi-editing-area-container");
    let styleElement = document.createElement("style");
    let styles = `@media print {
             .ant-layout-content{
               height : 100% !important;
             }
             .hi-content-area{
               height : ${totalPages * 100}vh !important;
             }
           }`
    styleElement.innerHTML = styles;
    parent.appendChild(styleElement);
  }).catch((err) => {
    console.log(err)
  })
}

export const getTableOperations = (info) => {
  let returnObj = {};
  const { sort, sortField, search, searchField, fieldType, filter, filterField = '', fetchAllRecords, searchInput, data = [], tableFilters = {} } = info || {};
  if (!fetchAllRecords) return returnObj;
  if (sort) {
    returnObj.sorter = (a, b) => {
      switch (fieldType) {
        case "numeric":
          return a[sortField] - b[sortField];
        case "text":
          return a[sortField].localeCompare(b[sortField]);
        case "date":
        case "dateTime":
          return new Date(a[sortField]) - new Date(b[sortField]);
        default:
          return 0;
      }
    }
  }

  if (search && !filter) {
    returnObj = {
      ...returnObj,
      filterDropdown: ({ setSelectedKeys, selectedKeys, confirm }) => {
        return (
          <Input
            size="middle"
            ref={searchInput}
            placeholder={`Search ${searchField}`}
            value={selectedKeys[0]}
            onChange={(e) => {
              setSelectedKeys(e.target.value ? [e.target.value] : [])
              confirm({ closeDropdown: false })
            }
            }
            onPressEnter={() => confirm()}
            style={{
              marginBottom: 8,
              display: 'flex',
            }}
            allowClear={true}
          />
        )
      },
      onFilter: (value, record) => record[searchField].toString().toLowerCase().includes(value.toLowerCase()),
      onFilterDropdownOpenChange: (visible) => {
        if (visible) {
          let timeout = setTimeout(() => {
            searchInput.current?.select()
            clearTimeout(timeout);
          }, 100);
        }
      },
      filterIcon: (filtered) => {
        return <SearchOutlined
          style={{
            color: filtered ? '#1677ff' : undefined,
          }}
        />
      }
    }
  }

  if (filter) {
    let filters = getUniqueFilterOptions(data, filterField)
    returnObj = {
      ...returnObj,
      onFilter: (value, record = {}) => record?.[filterField] === value,
      filters,
      filterSearch: search,
      filteredValue: tableFilters?.[filterField] || []
    }
  }
  return returnObj;
}


export const measureTextWidth = (text) => {
  const span = document.createElement('span');
  span.innerHTML = text;
  span.style.visibility = 'hidden';
  span.style.position = 'absolute';
  document.body.appendChild(span);
  const width = span.offsetWidth;
  document.body.removeChild(span);
  return width;
};


export const calculateScrollWidth = (columns, measureTextWidth) => {
  let scrollWidth = 0;
  for (let i = 0; i < columns.length; i++) {
    scrollWidth += measureTextWidth(columns[i].name) * 3;
  }
  return scrollWidth;
};

export const checkMeasureFieldsExistInRowsColumns = (fields = []) => {
  if (!fields.length) return false;
  let measureField = fields.find((f) => {
    const { column, addedAs } = f || {}
    return MEASURE_VALUE === column && ["row", "column"].includes(addedAs);
  })
  const isPresent = !!measureField;
  let measureFieldName = '';
  if (isPresent) {
    measureFieldName = getFieldDisplayName(measureField)
  }
  return { isPresent, measureFieldName }
}

export const handleMeasureFieldsFormatting = (data, index, formattedText, report, measureName, text, name, measureFieldName) => {
  let newFormattedText = formattedText;
  let formattedTextForTooltip = text;
  let formattedTextForActionWindow = text;
  let measureRecord = data.find((_, i) => i === index);
  if (measureRecord) {
    const { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(report, measureName);
    if (isApplyClicked) {
      if (measureRecord?.[measureFieldName]) {
        newFormattedText = getPropertyText({ text: measureRecord[measureFieldName], applyOn: "pane", isApplyClicked, fieldType, formatField });
        formattedTextForTooltip = getPropertyText({ text: measureRecord[measureFieldName], applyOn: "tooltip", isApplyClicked, fieldType, formatField });
        formattedTextForActionWindow = getPropertyText({ text: measureRecord[measureFieldName], applyOn: "actions", isApplyClicked, fieldType, formatField });
      }
    } else {
      const { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(report, name)
      newFormattedText = getPropertyText({ text: formattedText, applyOn: "pane", isApplyClicked, fieldType, formatField });
      formattedTextForTooltip = getPropertyText({ text: measureRecord[measureFieldName], applyOn: "tooltip", isApplyClicked, fieldType, formatField });
      formattedTextForActionWindow = getPropertyText({ text: measureRecord[measureFieldName], applyOn: "actions", isApplyClicked, fieldType, formatField });
    }
  }
  return { newFormattedText, formattedTextForTooltip, formattedTextForActionWindow };
}


export const handleMeasureFieldFormattingForCrosstab = ({ report, rowHeaders = '', columnHeaders = '', fields = [], actualValue, text, measureFieldName = null }) => {
  let formattedText = text
  let formattedTextForTooltip = actualValue
  let formattedTextForActionWindow = actualValue
  const measureFields = fields.filter((f) => f?.addedAs === "measure_field").map((i) => getFieldDisplayName(i));
  let measureName = ''
  if (measureFieldName) {
    measureName = measureFieldName
  } else {
    const splittedRowHeaders = rowHeaders.split('-seperator-')
    const splittedColumnHeaders = columnHeaders.split('-seperator-')
    measureFields.forEach((field) => {
      if (splittedColumnHeaders.includes(field)) {
        measureName = splittedColumnHeaders.find((f) => f === field)
      } else if (splittedRowHeaders.includes(field)) {
        measureName = splittedRowHeaders.find((f) => f === field)
      }
    })
  }
  const { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(report, measureName);
  if (isApplyClicked) {
    formattedText = getPropertyText({ text: actualValue, applyOn: "pane", isApplyClicked, fieldType, formatField });
    if (formatField[0]?.values?.apply.includes("tooltip")) {
      formattedTextForTooltip = getPropertyText({ text: actualValue, applyOn: "tooltip", isApplyClicked, fieldType, formatField });
    }
    if (formatField[0]?.values?.apply.includes("actions")) {
      formattedTextForActionWindow = getPropertyText({ text: actualValue, applyOn: "actions", isApplyClicked, fieldType, formatField });
    }
  }

  return { formattedText, formattedTextForTooltip, formattedTextForActionWindow }
}