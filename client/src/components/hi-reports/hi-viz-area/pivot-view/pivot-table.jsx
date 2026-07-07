// import React, { useRef, useState, useEffect, act } from "react";
// import { useDispatch } from "react-redux";
// import randomColor from "randomcolor";
// import { Tooltip, Row } from "antd";
// import { PivotViewComponent } from "@syncfusion/ej2-react-pivotview";
// import { getFieldDisplayName } from "../../../../utils/utilities";
// import notify from "../../../hi-notifications/notify";
// import "./pivot.scss";
// import {
//   getColorsByThemeColors,
//   getCustomFormatColor,
//   getMarks,
//   getPropertyElement,
//   getPropertyFieldInfo,
//   getPropertyStyle,
//   getPropertyText,
//   hrNumberFormat,
//   isColorFieldDimension,
// } from "../utils/utillities";
// import generateColor from "../utils/gradient";
// import VizTooltip from "../viz-tooltip";
// import CellCard from "../cell-card";
// import Toolbar from "../toolbar";
// import { setMenuData } from "../../../../redux/actions/hreport.actions";
// import { getFloatingType } from "../../../../utils/filter-utils";
// import { generateColorRange, generateColorsFromRange, getDataIndex, getEachFieldColor, getFieldData, getFieldName, getHTMLColorFormat } from "../../hi-editing-area/utils/property-utils";
// import { getElementError } from "@testing-library/react";
// import { checkParentElementBoundingRect } from "./utils/chart";
// import { changeExapandSettings, getLastNode, isAggregatedTitle } from "./utils/pivot-table-utils";
// import { getCrossTabBooleanConfig } from "./crosstab-boolean-config";
// import { cloneDeep, isArray, isEmpty } from "lodash";
// import { getGridChartColorSchemeFromPalette } from "../utils/grid-chart-utils";
// import { checkMeasureFieldsExistInRowsColumns, handleMeasureFieldFormattingForCrosstab, handleMeasureFieldsFormatting } from "../table/table-utils";

// const getMax = (val, d) => {
//   if (d === undefined) d = 1;
//   if (Math.floor(val / 10) === 0) {
//     return (val + 1) * d;
//   } else {
//     d = d * 10;
//     return getMax(Math.floor(val / 10), d);
//   }
// };
// const CrossTab = (props) => {
//   let {
//     data, metadata, fields, marksList, drillDown, drillThrough, interactiveMode, reportId, properties, report: activeReport, themeColors = []
//   } = props;
//   const dispatch = useDispatch();
//   let actualData = cloneDeep(data);
//   let report = { fields, properties, reportData: activeReport?.reportData };
//   const { title, subTitle, format, formatColor, crosstab, tooltip: { showTooltip = true } = {} } = report.properties || {};
//   const { backgroundColor } = formatColor
//   const { isPresent: isMeasureNameAndValuePresent, measureFieldName } = checkMeasureFieldsExistInRowsColumns(fields)

//   let height;
//   let width;
//   let titleStyle = {};
//   let subTitleStyle = {};

//   if (props.chartAreaHeight) {
//     height = props.chartAreaHeight
//   }
//   if (props.chartAreaWidth) {
//     width = props.chartAreaWidth
//   }

//   useEffect(() => {
//     if (pivotInstance.current) {
//       pivotInstance.current.refresh();
//     }
//   });

//   let pivotInstance = useRef(null);
//   let valueRef = useRef(null)
//   let colorsListRef = null;
//   let sizesListRef = null;
//   let maxValueRef = null;
//   const Notify = notify(dispatch);
//   let { show, value } = title;
//   let { show: subTitleShow, value: subTitleValue } = title;

//   if (show, value) {
//     titleStyle = getPropertyStyle(title)
//   }
//   if (subTitleShow && subTitleValue) {
//     subTitleStyle = getPropertyStyle(subTitle)
//   }

//   const createColorsList = (colorMarkField) => {
//     let rows = dataSourceSettings.rows;
//     let columns = dataSourceSettings.columns;
//     let values = dataSourceSettings.values;
//     rows = rows.map((row) => row.name);
//     columns = columns.map((column) => column.name);
//     values = values.map((values) => values.name);

//     let colors = {},
//       sampleColors = ["blue", "green", "red", "yellow", "orange"];
//     if (columns.includes(colorMarkField)) {
//       let colorMarks = [...pivotInstance.current.pivotValues[columns.indexOf(colorMarkField)]];
//       colorMarks
//         .filter((colorMark) => colorMark)
//         .map((colorMark) => {
//           if (Object.keys(colors).indexOf(colorMark.actualText) === -1) {
//             let colorCode = sampleColors[Object.keys(colors).length];
//             colorCode = colorCode ? colorCode : randomColor();
//             colors[colorMark.actualText] = colorCode;
//           }
//         });
//       return {
//         colors,
//         colorIndex: columns.indexOf(colorMarkField),
//         markedFrom: "columnHeaders",
//       };
//     }
//     if (rows.includes(colorMarkField)) {
//       let colorMarks = [...pivotInstance.current.pivotValues.map((row) => row[0])];
//       colorMarks
//         .filter((colorMark) => {
//           if (colorMark && colorMark.valueSort.axis === colorMarkField) {
//             return true;
//           }
//         })
//         .map((colorMark) => {
//           if (Object.keys(colors).indexOf(colorMark.actualText) === -1) {
//             let colorCode = sampleColors[Object.keys(colors).length];
//             colorCode = colorCode ? colorCode : randomColor();
//             colors[colorMark.actualText] = colorCode;
//           }
//         });
//       return {
//         colors,
//         colorIndex: rows.indexOf(colorMarkField),
//         markedFrom: "rowHeaders",
//       };
//     }

//     if (values.includes(colorMarkField)) {
//       return { markedFrom: "values" };
//     }
//   };
//   const createSizeList = (sizeMarkField) => {
//     let rows = pivotInstance.current.dataSourceSettings.rows;
//     let columns = pivotInstance.current.dataSourceSettings.columns;
//     let values = pivotInstance.current.dataSourceSettings.values;
//     rows = rows.map((row) => row.name);
//     columns = columns.map((column) => column.name);
//     values = values.map((value) => value.name);
//     let sizes = {};
//     if (columns.includes(sizeMarkField)) {
//       let sizeMarks = [...pivotInstance.current.pivotValues[columns.indexOf(sizeMarkField)]];
//       sizeMarks
//         .filter((sizeMark) => sizeMark)
//         .map((sizeMark) => {
//           if (!Object.keys(sizes).includes(sizeMark.actualText)) {
//             sizes[sizeMark.actualText] = 13;
//           }
//         });
//       let totalLength = Object.keys(sizes).length;
//       Object.keys(sizes).map((key, i) => {
//         sizes[key] = ((i + 1) / totalLength) * 10 + 3;
//       });
//       return {
//         sizes,
//         sizeIndex: columns.indexOf(sizeMarkField),
//         markedFrom: "columnHeaders",
//       };
//     }
//     if (rows.includes(sizeMarkField)) {
//       let sizeMarks = [...pivotInstance.current.pivotValues.map((row) => row[0])];
//       sizeMarks
//         .filter((sizeMark) => sizeMark)
//         .map((sizeMark) => {
//           if (!Object.keys(sizes).includes(sizeMark.actualText)) {
//             sizes[sizeMark.actualText] = 13;
//           }
//         });
//       let totalLength = Object.keys(sizes).length;
//       Object.keys(sizes).map((key, i) => {
//         sizes[key] = ((i + 1) / totalLength) * 10 + 3;
//       });
//       return {
//         sizes,
//         sizeIndex: rows.indexOf(sizeMarkField),
//         markedFrom: "rowHeaders",
//       };
//     }
//     if (values.includes(sizeMarkField)) {
//       return { markedFrom: "values" };
//     }
//   };
//   const createMaxValue = () => {
//     let max = 0,
//       values = [];
//     pivotInstance.current.pivotValues.map((xRow) => {
//       xRow.map((cell) => {
//         if (cell.axis === "value") {
//           values.push(cell.value);
//         }
//         return null;
//       });
//       return null;
//     });
//     max = getMax(Math.max(...values));
//     return max;
//   };
//   const onCellClick = (args) => {
//     if (!interactiveMode) {
//       return null;
//     }
//     if (!drillDown && !drillThrough) {
//       return null;
//     }
//     let { currentCell, data } = args;
//     if (data?.axis !== "value") {
//       return null;
//     }
//     // let { top, right, bottom, left } = currentCell.getBoundingClientRect();
//     let { actualText, actualValue, columnHeaders, rowHeaders, indexObject, colIndex } = data;
//     let payload = [{ field: actualText, value: actualValue }];
//     columnHeaders = columnHeaders.split("-seperator-");
//     rowHeaders = rowHeaders.split("-seperator-");
//     let columns = pivotInstance.current.dataSourceSettings.columns.map((clmn) => clmn.name);
//     let rows = pivotInstance.current.dataSourceSettings.rows.map((row) => row.name);
//     const currentIndex = Object.keys(indexObject)?.[0]
//     let drillthroughField = fields?.find((field) => field?.addedAs === "drillthrough_field")
//     let drillThroughFieldName = ''
//     if (drillthroughField) {
//       drillThroughFieldName = getFieldDisplayName(drillthroughField)
//       if (drillthroughField?.floatingType === 'discrete') {
//         columns = [...new Set([...columns, drillThroughFieldName])]
//       }
//     }
//     let { dataSource } = pivotInstance.current.dataSourceSettings;
//     const indexObjLength = Object.keys(indexObject).length;
//     if (indexObjLength) {
//       if (rowHeaders) {
//         rowHeaders.push("_all_")
//       }
//       if (columnHeaders) {
//         columnHeaders.push("_all_")
//       }
//     }
//     rows.map((row, i) => {
//       payload.push({ field: row, value: rowHeaders[i] });
//     });
//     columns.map((column, i) => {
//       if (drillThroughFieldName && drillThroughFieldName === column && indexObjLength) {
//         let datasourceCurrentObj = dataSource[currentIndex]
//         payload.push({ field: column, value: datasourceCurrentObj[column] });
//       } else {
//         payload.push({ field: column, value: columnHeaders[i] });
//       }
//     });

//     // 8239 changes
//     let recordForActionWindow = cloneDeep(payload)

//     if (isMeasureNameAndValuePresent) {
//       const { formattedTextForActionWindow } = handleMeasureFieldFormattingForCrosstab({ report, rowHeaders: data.rowHeaders, columnHeaders: data.columnHeaders, fields, actualValue, text: '' })
//       recordForActionWindow = recordForActionWindow.map((item) => {
//         if (item?.field === measureFieldName) {
//           item.value = formattedTextForActionWindow
//         }
//         return item
//       })
//     }

//     let { top, right, bottom, left } = checkParentElementBoundingRect("hi-editing-area-container", currentCell, payload)
//     let menuData = {
//       payload: recordForActionWindow,
//       position: { top, right, bottom, left },
//       drillDownFilterValues: dataSource,
//     };
//     dispatch(setMenuData({ reportId, menu: menuData }));
//   };

//   const getColorDataIndex = (data, fieldName, formattedText, fieldType, formatColor) => {
//     // let colors = generateColorRange(formatColor?.minimum, formatColor?.maximum, data.length)
//     const fieldData = getFieldData(data, fieldName);
//     const uniqueData = [...new Set(fieldData)];
//     let colors = generateColorRange(formatColor?.minimum, formatColor?.maximum, uniqueData.length)
//     if (formatColor?.enableSteps) {
//       colors = generateColorsFromRange(uniqueData, formatColor);
//     }
//     let index = getDataIndex(uniqueData, formattedText, fieldType)
//     return { index, colors, uniqueData };
//   }

//   const getSubTotalData = (instance, fieldName) => {
//     let instanceData = instance?.grid?.currentViewData
//     const getFieldData = (obj) => {
//       switch (obj.axis) {
//         case 'value':
//           return { [obj.actualText]: obj.actualValue }
//         case 'row':
//           if (obj?.valueSort) {
//             return { [obj.valueSort?.axis]: obj.actualText }
//           }
//           break;
//         default:
//           break;
//       }
//     }
//     if (isArray(instanceData) && instanceData?.length) {
//       let tempData = instanceData.map((obj) => Object.values(obj)).flat(Infinity)
//       let data = tempData.map((item) => {
//         return getFieldData(item, fieldName);
//       })?.filter((d) => Object.values(d)[0] !== "Grand Total") || []
//       if (data.some((item) => item.hasOwnProperty(fieldName))) {
//         return data.filter((item) => Object.keys(item)[0] === fieldName);
//       } else {
//         data = data.map((item) => ({ [fieldName]: Object.values(item)[0] }));
//       }
//       return data
//     }
//     return [];
//   }

//   const isDimensionColorApplicable = (axis, fieldName, colorFieldId) => axis === "value" && fieldName && isColorFieldDimension(colorFieldId, report)

//   const renderCell = (element) => {
//     let { cellInfo, targetCell } = element;
//     let { axis, actualValue, columnHeaders, rowHeaders, actualText, colIndex, indexObject } = cellInfo || {};
//     let className = "hr-report-cell ";
//     if (axis === "value") {
//       let cellBackgroundColor = "transparent";
//       targetCell.querySelector(".e-cellvalue").style.display = "none";
//       let text = cellInfo.formattedText;
//       let columns = dataSourceSettings.columns.map((clmn) => clmn.name);
//       let rows = dataSourceSettings.rows.map((row) => row.name);
//       let record = { [actualText]: actualValue };
//       let color = "#000";
//       if (colorMarks.length) {
//         try {
//           colorsListRef = colorsListRef || createColorsList(colorMarks[0]);
//           let colorsList = colorsListRef;
//           let { colors, colorIndex, markedFrom } = colorsList;
//           if (themeColors?.length) {
//             let newData = getSubTotalData(pivotInstance.current, colorMarks[0]);
//             let nColors = getColorsByThemeColors(colorMarks[0], themeColors, newData)
//             if (["values", "columnHeaders"].includes(markedFrom)) {
//               color = nColors[actualValue]
//             } else {
//               color = nColors[cellInfo[markedFrom].split("-seperator-")[colorIndex]];
//             }
//           } else {
//             if (markedFrom === "values") {
//               maxValueRef = maxValueRef || createMaxValue(colorMarks[0], data);
//               let max = maxValueRef;
//               let colorValue = (100 * actualValue) / max;
//               color = "#" + generateColor("#0000ff", "#fffff0", colorValue);
//             } else {
//               color = colors[cellInfo[markedFrom].split("-seperator-")[colorIndex]];
//             }
//           }
//         } catch (e) {
//           console.log(e);
//         }
//       }
//       color = color ? color : "#000";

//       let fontSize = 13;
//       if (sizeMarks.length) {
//         try {
//           sizesListRef = sizesListRef || createSizeList(sizeMarks[0]);
//           let sizeList = sizesListRef;
//           let { sizes, sizeIndex, markedFrom } = sizeList;
//           if (markedFrom === "values") {
//             maxValueRef = maxValueRef || createMaxValue(colorMarks[0], data);
//             let max = maxValueRef;
//             fontSize = (100 * actualValue) / max;
//             fontSize = (10 * fontSize) / 100 + 5;
//             fontSize = Math.floor(fontSize);
//           } else {
//             fontSize = sizes[cellInfo[markedFrom].split("-seperator-")[sizeIndex]];
//           }
//         } catch (e) {
//           console.log(e);
//         }
//       }
//       rows.map((row, i) => {
//         record[row] = rowHeaders.split("-seperator-")[i];
//       });
//       columns.map((column, i) => {
//         record[column] = columnHeaders.split("-seperator-")[i];
//       });
//       text = hrNumberFormat(actualValue);
//       className += "text-right ";
//       // targetCell.innerHTML = `<div class=${className}> ${text} </div>`
//       targetCell.innerHTML = "";
//       let valueIndex = 0
//       let { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(report, cellInfo?.actualText)
//       let formattedText = getPropertyText({ text: cellInfo.formattedText, applyOn: "pane", isApplyClicked, fieldType, formatField })
//       let recordForTooltip = cloneDeep(record)

//       if (isMeasureNameAndValuePresent) {
//         const { formattedText: newFormattedtext, formattedTextForTooltip } = handleMeasureFieldFormattingForCrosstab({ report, rowHeaders, columnHeaders, fields, actualValue, text: formattedText })
//         formattedText = newFormattedtext
//         recordForTooltip[measureFieldName] = formattedTextForTooltip
//       }

//       const indexArr = Object.keys(indexObject);
//       let drillthroughField = fields?.find((field) => field?.addedAs === "drillthrough_field")
//       let drillThroughFieldName = getFieldDisplayName(drillthroughField)
//       if (drillthroughField && drillThroughFieldName && !columns?.includes(drillThroughFieldName) && indexArr?.length) {
//         let currentValueIndex = indexArr[0]
//         let currentCellValueObj = dataSourceSettings?.dataSource[currentValueIndex]
//         let currentCellValue = currentCellValueObj[drillThroughFieldName]
//         record[drillThroughFieldName] = currentCellValue
//       }
//       if (indexArr.length) {
//         valueIndex = indexArr[0]
//         const newObject = {};
//         for (let [key, value] of Object.entries(record)) {
//           if (typeof value === "undefined") {
//             newObject[key] = "_all_";
//           } else {
//             newObject[key] = value;
//           }
//         }
//         record = newObject
//       }
//       if (formatColor.formatColorStyle === "gradient") {
//         if (formatColor.minimum && formatColor.maximum) {
//           const colorFieldId = formatColor?.formatColorField || "";
//           const fieldName = getFieldName(colorFieldId, report)
//           const isApplicable = isDimensionColorApplicable(axis, fieldName, colorFieldId)
//           if (
//             (fieldName === cellInfo?.actualText && (cellInfo?.formattedText || cellInfo.formattedText === "0")) ||
//             (cellInfo?.formattedText && isApplicable)
//           ) {
//             let preFormattedText = cellInfo?.formattedText;

//             if (isApplicable) {
//               preFormattedText = recordForTooltip[fieldName];
//               fieldType = "text";
//             }

//             let { index, colors, uniqueData = [] } = getColorDataIndex(data, fieldName, preFormattedText, fieldType, formatColor)
//             valueIndex = index;
//             if (valueIndex === -1) {
//               valueIndex = uniqueData.findIndex((item) => item === +preFormattedText)
//               if (valueIndex === -1 && pivotInstance.current) {
//                 let newData = getSubTotalData(pivotInstance.current, fieldName);
//                 let { index, colors: newColors } = getColorDataIndex(newData, fieldName, preFormattedText, fieldType, formatColor)
//                 valueIndex = index;
//                 colors = newColors;
//               }
//             }
//             color = colors[valueIndex]
//           }
//         }
//       }

//       if (formatColor?.formatColorStyle === "fieldValue") {
//         const colorFieldId = formatColor?.formatColorField || "";
//         const fieldName = getFieldName(colorFieldId, report)
//         const isApplicable = isDimensionColorApplicable(axis, fieldName, colorFieldId)
//         if (fieldName === cellInfo?.actualText || isApplicable) {
//           let preFormattedText = cellInfo?.formattedText;

//           if (isApplicable) {
//             preFormattedText = recordForTooltip[fieldName];
//           }

//           if (formatColor?.showAll) {
//             color = getEachFieldColor(formatColor, preFormattedText);
//           } else {
//             color = getHTMLColorFormat(formatColor.defaultColor);
//           }
//         }
//       }

//       if (backgroundColor) {
//         const fieldName = getFieldName(formatColor?.formatColorField, report)
//         if (
//           (fieldName === cellInfo?.actualText && (cellInfo?.formattedText || cellInfo.formattedText === "0")) ||
//           (cellInfo?.formattedText && isDimensionColorApplicable(axis, fieldName, formatColor?.formatColorField))
//         ) {
//           cellBackgroundColor = color
//           color = "#000000"
//         }
//         targetCell.setAttribute('style', `color: ${color} !important; background-color: ${cellBackgroundColor} !important`);
//       }

//       const customFormatColor = getCustomFormatColor({ text: actualValue, applyOn: "pane", isApplyClicked, fieldType, formatField })
//       if (customFormatColor) color = customFormatColor;

//       return (
//         <Tooltip
//           title={
//             !showTooltip ?
//               null : () => <VizTooltip data={recordForTooltip} report={report} format={format} dispatch={dispatch} />
//           }
//           overlayClassName="hi-viz-tooltip-overlay"
//         >
//           <div
//             ref={valueRef}
//             className={className}
//             onClick={() => targetCell.click()}
//             data-testid={`crosstab-cell-${text}`}
//             style={{ color: color, fontSize: `${fontSize}px`, background: cellBackgroundColor, display: "inline", }}
//           >
//             {formattedText}
//           </div>
//         </Tooltip>
//       );
//     }
//     if (axis === "row") {
//       let valueIndex = 0
//       targetCell.querySelector(".e-cellvalue").style.display = "none";
//       let color = "#000";
//       let cellBackgroundColor = "transparent";
//       const { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(report, cellInfo?.valueSort.axis)

//       return (
//         <div style={{ color, fontWeight: 700, background: cellBackgroundColor, display: "inline", }}>
//           <span>
//             {getPropertyText({ text: cellInfo.actualText, applyOn: "axis", isApplyClicked, fieldType, formatField })}
//           </span>
//         </div>
//       );
//     } else {
//       const { isTrue, text } = isAggregatedTitle(targetCell.innerText)
//       if (isTrue && text) {
//         targetCell.style.visible = "false"
//         targetCell.innerHTML = ""
//         return <span> {text} </span>
//       }
//     }
//     return null;
//   };
//   if (!data) return null;
//   let { colorMarks, sizeMarks } = getMarks(marksList, fields);
//   let metadataColumns = Object.keys(metadata[0]).map((key) => metadata[0][key].name);

//   const crosstabConfig = getCrossTabBooleanConfig({ crosstab })
//   let dataSourceSettings = {
//     enableSorting: false,
//     dataSource: data,
//     expandAll: true,
//     ...crosstabConfig
//   };
//   let values = [],
//     rows = [],
//     columns = [];
//   fields.map((field) => {
//     let label = getFieldDisplayName(field);
//     if (!metadataColumns.includes(label)) return null;
//     let { floatingType } = getFloatingType(field);
//     if (floatingType === "continous") {
//       let aggregateType = ""
//       if (field.aggregate && field.aggregate.length) {
//         const type = field.aggregate[0].split(".")[3]
//         if (["max", "min", "avg"].includes(type)) {
//           let capitalized = type.charAt(0).toUpperCase() + type.substring(1);
//           aggregateType = capitalized;
//         } else {
//           aggregateType = "Sum"
//         }
//       }
//       if (field.addedAs === "row") {
//         if (aggregateType) {
//           values.push({ name: label, addedIn: "rows", type: aggregateType });
//         } else {
//           values.push({ name: label, addedIn: "rows" });
//         }
//       }
//       if (field.addedAs === "column") {
//         if (aggregateType) {
//           values.push({ name: label, addedIn: "columns", type: aggregateType });
//         } else {
//           values.push({ name: label, addedIn: "columns" });
//         }
//       }
//     } else {
//       if (field.addedAs === "row") {
//         rows.push({ name: label });
//       }
//       if (field.addedAs === "column") {
//         columns.push({ name: label });
//       }
//     }
//   });
//   if (!values.length) {
//     const message = `
//     Invalid data. Please use proper configuration. Atleast 1 measure is required in rows or columns to 
//     generate Pivot Table
//     `
//     Notify.warning({ message });
//     return (
//       <div style={{ width: "100%", height: "100%" }}>
//         <div className="muze-message-container" style={{ width: "100%", height: "100%" }}>
//           <div className="viz-invalid-state">
//             {message}
//           </div>
//         </div>
//       </div>
//     );
//   }
//   dataSourceSettings.rows = rows;
//   dataSourceSettings.columns = columns;
//   dataSourceSettings.values = values;
//   dataSourceSettings.valueSortSettings = { headerDelimiter: "-seperator-" };
//   dataSourceSettings.dataSource = data;
//   dataSourceSettings.expandAll = true;
//   dataSourceSettings.drilledMembers = []

//   const { crosstabCollapse = "None" } = crosstab || {}
//   const collapseConditions = ["All", "Columns", "Rows"]
//   if (collapseConditions.includes(crosstabCollapse)) {
//     switch (crosstabCollapse) {
//       case "All":
//         dataSourceSettings.expandAll = false;
//         break;
//       case "Columns":
//         dataSourceSettings.drilledMembers = changeExapandSettings(columns, data)
//         break;
//       case "Rows":
//         dataSourceSettings.drilledMembers = changeExapandSettings(rows, data)
//         break;
//       default:
//         break;
//     }
//   }
//   return (
//     <>
//       <div className="title-subtitle-container">
//         {title?.position === "top" && getPropertyElement(titleStyle, title)}
//         {subTitle?.position === "top" && getPropertyElement(subTitleStyle, subTitle)}
//       </div>
//       <div data-testid="pivot-table-container">
//         <Row justify="end">
//           <Toolbar
//             addForwardFilterIPC={props.addForwardFilterIPC}
//             deleteBackwardFilterIPC={props.deleteBackwardFilterIPC}
//             refreshFiltersIPC={props.refreshFiltersIPC}
//             reportId={reportId}
//             getApi={props.getApi}
//           />
//         </Row>
//         <div className="pivot-table-container">
//           <PivotViewComponent
//             ref={pivotInstance}
//             dataSourceSettings={dataSourceSettings}
//             width={width}
//             height={height}
//             minHeight={10}
//             // dataBound={this.onDataBound}
//             cellTemplate={renderCell}
//             showTooltip={false}
//             allowDrillThrough={true}
//             cellClick={onCellClick}
//             enableVirtualization={true}
//             allowDataCompression={true}
//             gridSettings={{
//               headerCellInfo: (headerArgs) => {
//                 const headerCell = headerArgs?.cell?.column?.customAttributes || ""
//                 let valueIndex = 0
//                 let cellBackgroundColor = "transparent"
//                 let color = "#000"
//                 if (headerCell) {
//                   const lastNode = getLastNode(headerArgs.node)
//                   if (lastNode) {
//                     if (backgroundColor) {
//                       const fieldName = getFieldName(formatColor?.formatColorField, report)
//                       if (fieldName === headerCell.cell.valueSort.axis) {
//                         // cellBackgroundColor = color
//                         color = "#000000"
//                       }
//                       headerArgs.node.setAttribute('style', `background-color: ${cellBackgroundColor} !important`, `color: ${color} !important`);
//                     }
//                     lastNode.setAttribute('style', `color: ${color} !important`);
//                   }
//                 }

//               },
//               columnRender: (args) => {
//                 const { stackedColumns } = args
//                 for (let i = 0; i < stackedColumns.length; i++) {
//                   let eachColumn = args.stackedColumns[i]
//                   if (eachColumn?.customAttributes?.cell?.valueSort?.axis) {
//                     const { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(report, eachColumn.customAttributes.cell.valueSort.axis)
//                     const formattedText = getPropertyText({ text: eachColumn.customAttributes.cell.actualText, applyOn: "axis", isApplyClicked, fieldType, formatField })
//                     args.stackedColumns[i].headerText = formattedText;
//                   }
//                 }
//               }
//             }}
//           // created={e => this.created(controlId)}
//           >
//             {/* <Inject services={[DrillThrough]} /> */}
//           </PivotViewComponent>
//         </div>
//         {interactiveMode && <CellCard reportId={reportId} addFilter={props.addFilter} report={report}
//           getApi={props.getApi} format={format} />}
//       </div>
//       <div className="title-subtitle-container">
//         {title?.position === "bottom" && getPropertyElement(titleStyle, title)}
//         {subTitle?.position === "bottom" && getPropertyElement(subTitleStyle, subTitle)}
//       </div>
//     </>
//   );
// };
// const areEqual = (prevProps, nextProps) => {
//   if (
//     prevProps.dataId !== nextProps.dataId ||
//     prevProps.chartAreaHeight !== nextProps.chartAreaHeight ||
//     prevProps.chartAreaWidth !== nextProps.chartAreaWidth
//   ) {
//     return false;
//   } else {
//     return true;
//   }
// };

// export default React.memo(CrossTab, areEqual);
