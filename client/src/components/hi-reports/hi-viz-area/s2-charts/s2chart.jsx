import React, { useRef, useState } from "react";
import { SheetComponent } from "@antv/s2-react";
import "@antv/s2-react/dist/style.min.css";
import { getFieldDisplayName } from "../../../../utils/utilities";
import { calcFunc, getColorsByThemeColors, getCustomFormatColor, isColorFieldDimension } from "../utils/utillities";
import { getFloatingType } from "../../../../utils/filter-utils";
import {
  createColorsList,
  createMaxValue,
  createsizesList,
  getMarks,
  getPropertyElement,
  getPropertyFieldInfo,
  getPropertyStyle,
  getPropertyText,
} from "../utils/utillities";
import { cloneDeep, isEmpty } from "lodash";
import { Row } from "antd";
import Toolbar from "../toolbar";
import CellCard from "../cell-card";
import {
  generateColorRange,
  generateColorsFromRange,
  getDataIndex,
  getEachFieldColor,
  getFieldData,
  getFieldName,
  getHTMLColorFormat,
} from "../../hi-editing-area/utils/property-utils";
import { TableDataCell, setLang } from "@antv/s2";
import generateColor from "../utils/gradient";
import VizTooltip from "../viz-tooltip";
import { useDispatch } from "react-redux";
import { setMenuData } from "../../../../redux/actions/hreport.actions";
import { getFieldInfo } from "../utils/grid-chart-utils";
import { checkMeasureFieldsExistInRowsColumns, handleMeasureFieldFormattingForCrosstab } from "../table/table-utils";
setLang("en_US");


const S2Chart = (props) => {
  let {
    data: chartData,
    metadata,
    fields,
    marksList,
    interactiveMode,
    drillDown,
    drillThrough,
    reportId,
    report,
    addForwardFilterIPC,
    deleteBackwardFilterIPC,
    refreshFiltersIPC,
    themeColors = []
  } = props;

  const dispatch = useDispatch();
  const [dataCellClicked, setDataCellClicked] = useState("");
  const [rowCollapsedInfo, setRowCollapsedInfo] = useState({});
  const [areAllRowsCollapsed, setAreAllRowsCollapsed] = useState(false);
  const s2ref = useRef(null)

  let canvasFields = fields.filter((field) => !field.hiddenIncludeInResultSet && field.addedAs !== "measure_field");

  let data = cloneDeep(chartData);
  const { title, subTitle, format, formatColor, crosstab, axisRange, tooltip: { showTooltip = true } = {} } =
    report?.reportData?.properties || {};

  let {
    columns: rColumns,
    schema
  } = getFieldInfo({ data, metadata, fields: canvasFields, marksList, axisRange });

  let isDimensionInColumn = schema.find(
    (item) => item.type === "dimension" && rColumns.includes(item.name)
  );

  let columnDimensions = schema.filter(
    (item) => item.type === "dimension" && rColumns.includes(item.name)
  );

  let lastColumnDimension = "";
  if (columnDimensions?.length) {
    lastColumnDimension = columnDimensions[columnDimensions.length - 1]?.name;
  }

  const {
    showGrandTotals,
    showRowGrandTotals,
    showColumnGrandTotals,
    showSubTotals,
    showRowSubTotals,
    showColumnSubTotals,
    grandTotalsPosition,
    crosstabCollapse,
  } = crosstab;
  let { backgroundColor } = formatColor;

  const cellClick = (e) => {
    setDataCellClicked(true);
    if (!interactiveMode) {
      // let message = `Please enable interactivity in values tab.`
      // Notify.warning({message});
      return null;
    }
    if (!drillDown && !drillThrough) {
      return null;
    }
    const { $$extra$$, ...record } = {
      ...e.viewMeta.colQuery,
      ...e.viewMeta.rowQuery,
    };
    record[$$extra$$] = e.viewMeta.fieldValue;
    let payload = [];
    Object.keys(record).map((key) => {
      payload.push({ field: key, value: record[key] });
    });
    const { height, width, data: proxyData } = e.viewMeta;
    const { clientX: x, clientY: y } = e.event;
    const left = x;
    const right = x + width;
    const top = y;
    const bottom = y + height;

    let recordForActionWindow = cloneDeep(payload)
    if (isMeasureNameAndValuePresent) {  // 8246 fix
      let dataKeys = []
      for (const key in proxyData) {
        dataKeys.push(key)
      }
      if (dataKeys.length) {
        let measureField = dataKeys.find((f) => f.includes("MEASURE_FIELD_"))
        if (measureField) {
          let mFieldName = measureField.replace("MEASURE_FIELD_", "")
          const { formattedTextForActionWindow } = handleMeasureFieldFormattingForCrosstab({ report, fields, measureFieldName: mFieldName, text: e.viewMeta.fieldValue, actualValue: e.viewMeta.fieldValue })
          recordForActionWindow = recordForActionWindow.map((item) => {
            if (item?.field === measureFieldName) {
              item.value = formattedTextForActionWindow
            }
            return item
          })
        }
      }
    }

    let menuData = {
      payload: recordForActionWindow,
      position: { top, right, bottom, left },
      drillDownFilterValues: data,
    };
    dispatch(setMenuData({ reportId, menu: menuData }));
    setDataCellClicked(false);
  };

  let height;
  let width;
  let titleStyle = {};
  let subTitleStyle = {};
  let maxValueRef = null;
  let colorsListRef = null;
  let sizesListRef = null;
  if (props.chartAreaHeight) {
    height = props.chartAreaHeight;
  }
  if (props.chartAreaWidth) {
    width = props.chartAreaWidth;
  }

  let { show, value } = title;
  let { show: subTitleShow, value: subTitleValue } = title;

  if ((show, value)) {
    titleStyle = getPropertyStyle(title);
  }
  if (subTitleShow && subTitleValue) {
    subTitleStyle = getPropertyStyle(subTitle);
  }
  //this is a constructor function

  const { colorMarks, sizeMarks } = getMarks(marksList, fields);

  let isAnyRowCollapsed = Object.values(rowCollapsedInfo).some((item) => item === true);
  const fieldsAggregateFn = {}
  const crosstabPropertyEnabled = (showGrandTotals || showRowGrandTotals || showColumnGrandTotals || showSubTotals || showRowSubTotals || showColumnSubTotals)
  const hierarchyCollapse = ['Rows', 'All'].includes(crosstabCollapse);
  const isDimensionColorApplicable = (fieldName, colorFieldId, data) => fieldName && data && isColorFieldDimension(colorFieldId, report)

  class CustomDataCell extends TableDataCell {
    getBackgroundColor() {
      let cellBackgroundColor = "transparent";
      const { isTotals, data: record, colQuery } = this.meta;
      let tempData = data;

      let recordInfo = null;
      if (record) {
        recordInfo = Object.entries(record).reduce((acc, [key, value]) => {
          acc[key] = value;
          return acc;
        }, {});
      }


      if (isTotals) {
        let colorField = colorMarks[0] || this.meta.valueField
        let dimensionValue = colQuery[lastColumnDimension];
        if (dimensionValue) {
          let tempTotals = totals[dimensionValue];
          if (tempTotals?.length) {
            tempData = [...new Set(tempTotals)].map((item) => ({ [colorField]: item }))
          }
        } else {
          let tempTotals = totals[colorField];
          tempData = [...new Set(tempTotals)].map((item) => ({ [colorField]: item }))
        }
      }

      if (backgroundColor) {
        if (formatColor?.formatColorStyle === "gradient") {
          if (formatColor?.minimum && formatColor?.maximum) {
            const colorFieldId = formatColor?.formatColorField || "";
            const fieldName = getFieldName(
              colorFieldId,
              report
            );
            const isApplicable = isDimensionColorApplicable(fieldName, colorFieldId, recordInfo)

            if (fieldName === this.meta.valueField || isApplicable) {
              const fieldData = getFieldData(tempData, fieldName);
              const uniqueData = [...new Set(fieldData)];
              let colors = generateColorRange(
                formatColor?.minimum,
                formatColor?.maximum,
                uniqueData.length,
              );
              if (formatColor?.enableSteps) {
                colors = generateColorsFromRange(uniqueData, formatColor);
              }

              let fieldValue = this.meta.fieldValue;
              if (isApplicable) {
                fieldValue = recordInfo[fieldName];
              }

              let metaFieldValueType = fieldValue ? typeof fieldValue : null
              if (metaFieldValueType !== 'number') {
                let index = uniqueData.findIndex((item) => item === fieldValue)
                cellBackgroundColor = colors[index];
              } else {
                const index = getDataIndex(
                  uniqueData,
                  fieldValue,
                  "numeric"
                );
                cellBackgroundColor = colors[index];
              }
            }
          }
        }

        if (formatColor?.formatColorStyle === "fieldValue") {
          const colorFieldId = formatColor?.formatColorField || "";
          const fieldName = getFieldName(colorFieldId, report);
          const isApplicable = isDimensionColorApplicable(fieldName, colorFieldId, recordInfo)

          if (fieldName === this.meta.valueField || isApplicable) {
            let fieldValue = this.meta.fieldValue;
            if (isApplicable) {
              fieldValue = recordInfo[fieldName];
            }
            if (formatColor?.showAll) {
              cellBackgroundColor = getEachFieldColor(
                formatColor,
                fieldValue
              );
            } else {
              cellBackgroundColor = getHTMLColorFormat(
                formatColor.defaultColor
              );
            }
          }
        }
      }

      return {
        backgroundColor: cellBackgroundColor,
      };
    }

    getTextStyle() {
      const defaultTextStyle = super.getTextStyle();
      let fontSize = 13;
      let color = "#000000";
      const { data: record, rowId, isTotals, colQuery = {} } = this.meta;

      let recordInfo = null;
      if (record) {
        recordInfo = Object.entries(record).reduce((acc, [key, value]) => {
          acc[key] = value;
          return acc;
        }, {});
      }

      let tempData = data;
      if (isTotals) {
        let colorField = colorMarks[0] || this.meta.valueField
        let dimensionValue = colQuery[lastColumnDimension];
        if (dimensionValue) {
          let tempTotals = totals[dimensionValue];
          if (tempTotals?.length) {
            tempData = [...new Set(tempTotals)].map((item) => ({ [colorField]: item }))
          }
        } else {
          let tempTotals = totals[colorField];
          tempData = [...new Set(tempTotals)].map((item) => ({ [colorField]: item }))
        }
      }

      if (colorMarks.length && record) {
        const colorField = Object.getOwnPropertyDescriptor(
          record,
          colorMarks[0]
        );
        let isValue = false;
        Object.keys(metadata[0]).map((index) => {
          if (metadata[0][index].name === colorMarks[0]) {
            if (metadata[0][index].type === "numeric") {
              isValue = true;
            }
          }
        });
        if (themeColors?.length) {
          const { $$value$$ = '' } = record || {}
          const tempColors = getColorsByThemeColors(colorMarks[0], themeColors, tempData);
          if (colorField?.value) {
            color = tempColors[colorField?.value] || tempColors[$$value$$] || "#000000";
          } else {
            color = "#000000"
          }
        } else {
          if (isValue) {
            maxValueRef = maxValueRef || createMaxValue(colorMarks[0], data);
            if (record) {
              let max = maxValueRef;
              let colorValue = (100 * (colorField.value)) / max;
              color = "#" + generateColor("#0000ff", "#fffff0", colorValue);
            }
          } else {
            colorsListRef =
              colorsListRef || createColorsList(colorMarks[0], data);
            let colorsList = colorsListRef;
            let { colors } = colorsList;
            color = colors[colorField?.value] || "#000000";
          }
        }
      }

      if (sizeMarks.length) {
        let isValue = false;
        Object.keys(metadata[0]).map((index) => {
          if (metadata[0][index].name === colorMarks[0]) {
            if (metadata[0][index].type === "numeric") {
              isValue = true;
            }
          }
        });

        if (isValue) {
          maxValueRef = maxValueRef || createMaxValue(colorMarks[0], data);
          let max = maxValueRef;
          if (record) {
            fontSize = (100 * record[sizeMarks[0]]) / max;
            fontSize = (10 * fontSize) / 100 + 5;
            fontSize = Math.floor(fontSize);
          }
        } else {
          sizesListRef = sizesListRef || createsizesList(sizeMarks[0], data);
          if (record) {
            let sizesList = sizesListRef;
            let { sizes } = sizesList;
            fontSize = sizes[record[sizeMarks[0]]];
          }
        }
      }
      if (formatColor?.formatColorStyle === "gradient") {
        if (formatColor.minimum && formatColor.maximum) {
          const colorFieldId = formatColor?.formatColorField || "";
          const fieldName = getFieldName(colorFieldId, report);
          const isApplicable = isDimensionColorApplicable(fieldName, colorFieldId, recordInfo)

          if (fieldName === this.meta.valueField || isApplicable) {
            let colors = generateColorRange(
              formatColor?.minimum,
              formatColor?.maximum,
              tempData.length
            );
            const fieldData = getFieldData(tempData, fieldName);
            const uniqueData = [...new Set(fieldData)];
            if (record) {
              colors = generateColorRange(
                formatColor?.minimum,
                formatColor?.maximum,
                uniqueData.length
              );
            }
            if (formatColor?.enableSteps) {
              colors = generateColorsFromRange(uniqueData, formatColor);
            }

            let fieldValue = this.meta.fieldValue;
            if (isApplicable) {
              fieldValue = recordInfo[fieldName];
            }

            let metaFieldValueType = fieldValue ? typeof fieldValue : null
            if (metaFieldValueType !== 'number') {
              let index = uniqueData.findIndex((item) => item === fieldValue)
              color = colors[index];
            } else {
              const index = getDataIndex(
                uniqueData,
                fieldValue,
                "numeric"
              );
              color = colors[index];
            }
          }
        }
      }

      if (formatColor?.formatColorStyle === "fieldValue") {
        const colorFieldId = formatColor?.formatColorField || "";
        const fieldName = getFieldName(colorFieldId, report);
        const isApplicable = isDimensionColorApplicable(fieldName, colorFieldId, recordInfo)
        if (fieldName === this.meta.valueField || isApplicable) {
          let fieldValue = this.meta.fieldValue;
          if (isApplicable) {
            fieldValue = recordInfo[fieldName];
          }

          if (formatColor?.showAll) {
            color = getEachFieldColor(formatColor, fieldValue);
          } else {
            color = getHTMLColorFormat(formatColor.defaultColor);
          }
        }
      }

      if (backgroundColor) {
        const colorFieldId = formatColor?.formatColorField || "";
        const fieldName = getFieldName(colorFieldId, report);
        const isApplicable = isDimensionColorApplicable(fieldName, colorFieldId, recordInfo)

        if (fieldName === this.meta.valueField || isApplicable) {
          color = "#000000";
        }
      }

      const { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(report, this.meta.valueField);
      const customFormatColor = getCustomFormatColor({ text: this.meta.fieldValue, applyOn: "pane", isApplyClicked, fieldType, formatField })
      if (customFormatColor) color = customFormatColor;

      if ((isTotals && !areAllRowsCollapsed && !hierarchyCollapse) && !rowCollapsedInfo[rowId] && !crosstabPropertyEnabled) color = 'transparent';

      return {
        ...defaultTextStyle,
        fontSize,
        fill: color,
      };
    }
  }

  const totals = {}

  let s2Options = {
    width,
    height,
    hierarchyType: "tree",
    hierarchyCollapse: hierarchyCollapse,
    tooltip: {
      content: (cell, options) => {
        const cellData = options.data?.details;
        const colsData = options.data?.headInfo?.cols;
        const rowData = options.data?.headInfo?.rows;
        if (cellData && cellData.length === 1 && cellData[0].value === "-") {
          return null; // Return null to prevent rendering the tooltip
        }
        if (dataCellClicked) {
          return null; // Returning null to prevent rendering the tooltip content
        }

        let reportData = {
          properties: report.properties,
          fields: report.fields,
          reportData: report?.reportData
        };
        let data = {};

        if (cellData) {
          for (let i = 0; i < cellData.length; i++) {
            if (isMeasureNameAndValuePresent && cellData[i].name === measureFieldName) { // 8246 fix
              const proxyData = cell?.meta?.data || {};
              const dataKeys = []
              for (const key in proxyData) {
                dataKeys.push(key)
              }
              if (dataKeys.length) {
                let measureField = dataKeys.find((f) => f.includes("MEASURE_FIELD_"))
                if (measureField) {
                  let fieldValue = cell?.meta?.fieldValue;
                  let mFieldName = measureField.replace("MEASURE_FIELD_", "")
                  const { formattedTextForTooltip } = handleMeasureFieldFormattingForCrosstab({ report, fields, measureFieldName: mFieldName, text: fieldValue, actualValue: fieldValue })
                  data[cellData[i].name] = formattedTextForTooltip;
                }
              } else {
                data[cellData[i].name] = cellData[i].value;
              }
            } else {
              data[cellData[i].name] = cellData[i].value;
            }
          }
        }

        if (colsData) {
          for (let i = 0; i < colsData.length; i++) {
            data[colsData[i].name] = colsData[i].value;
          }
        }

        if (rowData) {
          for (let i = 0; i < rowData.length; i++) {
            data[rowData[i].name] = rowData[i].value;
          }
        }

        return (
          <VizTooltip
            data={data}
            report={reportData}
            format={format}
            s2Chart={true}
            dispatch={dispatch}
          />
        );
      },
      showTooltip: showTooltip,
    },
    showDefaultHeaderActionIcon: false,
    totals: {
      row: {
        showGrandTotals: showGrandTotals || showRowGrandTotals,
        showSubTotals: showSubTotals || showRowSubTotals || isAnyRowCollapsed || areAllRowsCollapsed || hierarchyCollapse,
        reverseLayout: grandTotalsPosition === "Top" ? true : false,
        calcTotals: {
          calcFunc: (query, arr) => calcFunc(query, arr, fieldsAggregateFn, totals, lastColumnDimension, { report, fields, isMeasureNameAndValuePresent }),
        },
        calcSubTotals: {
          calcFunc: (query, arr) => calcFunc(query, arr, fieldsAggregateFn, totals, lastColumnDimension, { report, fields, isMeasureNameAndValuePresent }),
        },
      },
      col: {
        showGrandTotals: showGrandTotals || showColumnGrandTotals,
        showSubTotals: showSubTotals || showColumnSubTotals,
        reverseLayout: grandTotalsPosition === "Top" ? true : false,
        calcTotals: {
          calcFunc: (query, arr) => calcFunc(query, arr, fieldsAggregateFn, totals, lastColumnDimension, { report, fields, isMeasureNameAndValuePresent }),
        },
        calcSubTotals: {
          calcFunc: (query, arr) => calcFunc(query, arr, fieldsAggregateFn, totals, lastColumnDimension, { report, fields, isMeasureNameAndValuePresent }),
        },
      },
    },

    dataCell: (viewMeta) => {
      return new CustomDataCell(viewMeta, viewMeta?.spreadsheet);
    },
  };
  let rows = [];
  let columns = [];
  let values = [];
  let metadataColumns = Object.keys(metadata[0]).map(
    (key) => metadata[0][key].name
  );

  fields.map((field) => {
    let label = getFieldDisplayName(field);
    if (!metadataColumns.includes(label)) return null;
    let { floatingType } = getFloatingType(field);
    if (floatingType === "continous") {
      let aggregateType = ""
      if (field.aggregate && field.aggregate.length) {
        const type = field.aggregate[0].split(".")[3]
        if (["max", "min", "avg"].includes(type)) {
          let capitalized = type.charAt(0).toUpperCase() + type.substring(1);
          aggregateType = capitalized;
        } else {
          // aggregateType = "Sum"
        }
      }
      if (field.addedAs === "row") {
        if (aggregateType) {
          fieldsAggregateFn[label] = aggregateType.toLowerCase();
          values.push(label, "rows", aggregateType);
        } else {
          values.push(label);
        }
      }
      if (field.addedAs === "column") {
        if (aggregateType) {
          fieldsAggregateFn[label] = aggregateType.toLowerCase();
          values.push(label, "columns", aggregateType);
        } else {
          // values.push(label, "columns");
          values.push(label);
        }
      }
    } else {
      if (field.addedAs === "row") {
        rows.push(label);
      }
      if (field.addedAs === "column") {
        columns.push(label);
      }
    }
  });

  const { isPresent: isMeasureNameAndValuePresent, measureFieldName } = checkMeasureFieldsExistInRowsColumns(fields)

  const s2DataConfig = {
    fields: {
      rows,
      columns,
      values,
    },
    data: props.data,
    meta: Object.entries(metadata[0]).map(([key, value]) => {
      return {
        ...value,
        field: value.name,
        formatter: (text, proxyData) => {
          const { isApplyClicked, fieldType, formatField } =
            getPropertyFieldInfo(report, value.name);

          let formattedText = getPropertyText({
            text,
            applyOn: "pane",
            isApplyClicked,
            fieldType,
            formatField,
          });


          if (isMeasureNameAndValuePresent && value.name === measureFieldName) {  // 8246 fix
            let dataKeys = []
            for (const key in proxyData) {
              dataKeys.push(key)
            }
            if (dataKeys.length) {
              let measureField = dataKeys.find((f) => f.includes("MEASURE_FIELD_"))
              if (measureField) {
                let mFieldName = measureField.replace("MEASURE_FIELD_", "")
                const { formattedText: newFormattedText } = handleMeasureFieldFormattingForCrosstab({ report, fields, measureFieldName: mFieldName, text: formattedText, actualValue: text })
                formattedText = newFormattedText
              }
            }
          }

          return formattedText;
        },
      };
    }),
  };

  return (
    <div>
      <div className="title-subtitle-container">
        {title?.position === "top" && getPropertyElement(titleStyle, title)}
        {subTitle?.position === "top" &&
          getPropertyElement(subTitleStyle, subTitle)}
      </div>
      <SheetComponent
        ref={s2ref}
        dataCfg={s2DataConfig}
        options={s2Options}
        onDataCellClick={cellClick}
        themeCfg={{ name: "gray" }}
        onRowCellCollapseTreeRows={({ isCollapsed, id }) => {
          setRowCollapsedInfo((prev) => ({ ...prev, [id]: isCollapsed }))
        }}
        onCollapseRowsAll={(d) => {
          setRowCollapsedInfo({})
          setAreAllRowsCollapsed(!d)
        }}
      />

      <Row justify="end">
        <Toolbar
          addForwardFilterIPC={addForwardFilterIPC}
          deleteBackwardFilterIPC={deleteBackwardFilterIPC}
          refreshFiltersIPC={refreshFiltersIPC}
          reportId={reportId}
          getApi={props.getApi}
        />
      </Row>
      {interactiveMode && (
        <CellCard
          reportId={reportId}
          addFilter={props.addFilter}
          getApi={props.getApi}
          report={report}
          format={format}
        />
      )}
      <div className="title-subtitle-container">
        {title?.position === "bottom" && getPropertyElement(titleStyle, title)}
        {subTitle?.position === "bottom" &&
          getPropertyElement(subTitleStyle, subTitle)}
      </div>
    </div>
  );
};
const areEqual = (prevProps, nextProps) => {
  if (
    prevProps.dataId !== nextProps.dataId ||
    prevProps.chartAreaHeight !== nextProps.chartAreaHeight ||
    prevProps.chartAreaWidth !== nextProps.chartAreaWidth
  ) {
    return false;
  } else {
    return true;
  }
};

export default React.memo(S2Chart, areEqual);
