// import React, { useRef, useEffect,  } from "react";
// import {
//   PivotViewComponent,
//   Inject,
//   DrillThrough,
//   PivotChart,
// } from "@syncfusion/ej2-react-pivotview";
// import { getFieldDisplayName } from "../../../../utils/utilities";
// import "./pivot.scss";
// import { getFloatingType } from "../../../../utils/filter-utils";
// import {
//   getNumberFormat,
//   getPropertyElement,
//   getPropertyFieldInfo,
//   getPropertyStyle,
//   getPropertyText,
// } from "../utils/utillities";

// const vizTypes = [
//   "Column",
//   "Line",
//   "Spline",
//   "Area",
//   "SplineArea",
//   "StepLine",
//   "StepArea",
//   "StackingColumn",
//   "StackingArea",
//   "StackingColumn100",
//   "StackingArea100",
//   "Scatter",
//   "Bubble",
//   "Pareto",
// ];
// const Chart = (props) => {
//   let { data, metadata, fields, marksList, report,properties } = props;
//   let subVizType = marksList[0]?.subVizType || "Column";
//   subVizType = vizTypes.includes(subVizType) ? subVizType : "Column";
//   let pivotInstance = useRef(null);
//   const { title, subTitle, } = report?.reportData?.properties || {};

//   useEffect(() => {
//     if (typeof pivotInstance.current.refresh === "function") {
//       pivotInstance.current.refresh()
//    }
//   })

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

//   let { show, value, padding, fontSize} = title;
//   if (show && value) {
//     height = height - 20 - fontSize - 2 * padding
//     titleStyle = getPropertyStyle(title)
//   }

//   let { show: subTitleShow, value: subTitleValue, padding: subTitlePadding, fontSize:subTitleFontSize} = subTitle;
//   if (subTitleShow && subTitleValue) {
//     subTitleStyle = getPropertyStyle(subTitle)
//     height = height - 20 - subTitleFontSize - 2 * subTitlePadding
//   }

//   const onCellClick = (e) => {
//     console.log("e", e);
//   };

//   if (!data) return null;
//   let metadataColumns = Object.keys(metadata[0]).map((key) => metadata[0][key].name);
//   let dataSourceSettings = {
//     enableSorting: false,
//     dataSource: data,
//     showRowSubTotals: false,
//     showColumnSubTotals: false,
//     showRowGrandTotals: false,
//     showColumnGrandTotals: false,
//     expandAll: true,
//   };
//   let values = [],
//     rows = [],
//     columns = [];
//   fields.map((field) => {
//     let label = getFieldDisplayName(field);
//     if (!metadataColumns.includes(label)) return null;
//     let { floatingType } = getFloatingType(field);
//     if (floatingType === "continous") {
//       if (field.addedAs === "row") {
//         values.push({ name: label, addedIn: "rows" });
//       }
//       if (field.addedAs === "column") {
//         values.push({ name: label, addedIn: "columns" });
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
//     values.push({ name: "_no_Value_", addedIn: "rows" });
//     data = [...data].map((record) => {
//       return { ...record, _no_Value_: "Abc" };
//     });
//   }
//   dataSourceSettings.rows = rows;
//   dataSourceSettings.columns = columns;
//   dataSourceSettings.values = values;
//   dataSourceSettings.valueSortSettings = { headerDelimiter: "-seperator-" };
//   dataSourceSettings.dataSource = data;
//   dataSourceSettings.expandAll = true;

//   return (
//     <>
//       <div className="title-subtitle-container">
//         {title?.position === "top" && getPropertyElement(titleStyle, title)}
//         {subTitle?.position === "top" && getPropertyElement(subTitleStyle, subTitle)}
//       </div>
//       <div>
//         <div className="pivot-chart-container">
//           <PivotViewComponent
//             ref={pivotInstance}
//             dataSourceSettings={dataSourceSettings}
//             width={width}
//             height={height}
//             // dataBound={this.onDataBound}
//             showTooltip={false}
//             allowDrillThrough={true}
//             cellClick={onCellClick}
//             displayOption={{ view: "Chart" }}
//             chartSettings={{
//               background: "transparent",
//               chartSeries: { type: subVizType, dashArray: "" },
//               primaryXAxis: { title: values[0].name },
//               primaryYAxis: { title: values[0].name },
//               tooltip: {
//                 template: `<div class='pivot-tooltip-template'> <span>\${valueField}: \${value} </span> 
//               <span> ${"${columnHeaders}" ? "Column: ${columnHeaders}" : ""}</span> </div>`,
//                 enableAnimation: true,
//               },
//               axisLabelRender: (data) => {
//                 if (data.axis.name !== "primaryXAxis") {
//                   if (Number.isInteger(data.value)) {
//                     const { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(report, data?.axis?.properties?.title)
//                     let formattedText = getPropertyText({
//                       text: data.value,
//                       applyOn: "axis",
//                       isApplyClicked,
//                       fieldType,
//                       formatField
//                     });
//                     data.text = formattedText.toString();
//                   }
//                 }
//               },
//             }}
//             enableVirtualization={true}
//             allowDataCompression={true}
//             // created={e => this.created(controlId)}
//           >
//             <Inject services={[DrillThrough, PivotChart]} />
//           </PivotViewComponent>
//         </div>
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

// export default React.memo(Chart, areEqual);
