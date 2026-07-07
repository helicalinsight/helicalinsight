import {
  Area,
  Bar,
  Column,
  Line,
  Mix,
  Pie
} from "@ant-design/plots";
import { Row } from "antd";
import { cloneDeep, isEmpty } from "lodash-es";
import React, { useRef, useState } from "react";
import { useDispatch } from "react-redux";
import { setMenuData, updateSubVizType } from "../../../../redux/actions/hreport.actions";
import "../../../../utils/polyfill/url";
import { checkIfCalendarViz, getFieldDisplayName } from "../../../../utils/utilities";
import notify from "../../../hi-notifications/notify";
import CustomReferenceLine from "../../hi-editing-area/components/values/reference-line/custom-reference-line";
import {
  applyColor,
  generateColorRange,
  generateColorsFromRange,
  getFieldData,
  getFieldName,
  getHTMLColorFormat,
  getMinAndMaxRangeForSynchronize,
  getPropertyAxisRange
} from "../../hi-editing-area/utils/property-utils";
import { referenceLineAll } from "../../utils/constants";
import CellCard from "../cell-card";
import Toolbar from "../toolbar";
import { getFieldInfo } from "../utils/grid-chart-utils";
import {
  calculateReferenceLinePosition,
  getChartMaxScaleValue,
  getPropertyElement,
  getPropertyFieldInfo,
  getPropertyStyle,
  getPropertyText,
  tooltipTemplateLiquidJS,
} from "../utils/utillities";
import "./ant-chart.scss";
import { calculateScaleWidthByValue, divStyles, getAntChartLegendLabelConfig, getAntChartTheme, getChartTooltipTemplate, getPropertyColorField, registerAntdTheme, setStyles, subVizTypes } from "./ant-utils";
import {
  AntCalenderHeatMap,
  AntProgressChart,
  AntRadarChart,
  AntRelationCharts,
  AntWaterFallChart,
  ArcChart,
  AreaChart,
  DonutChart,
  ErrorArea,
  HiCard,
  ScatterChart,
  WordCloudForSingleDimension
} from "./components";
import { defaultColorPaletteSchemes } from "./constants";
import { getCanvasStyles } from "../../utils/utilities";

const AntChart = (props) => {
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
    properties,
    addForwardFilterIPC,
    deleteBackwardFilterIPC,
    refreshFiltersIPC,
    chartColorPalette,
    isPrintMode
  } = props;
  const { referenceLineList, selectedType } = report || {}
  const [chartContext, setChartContext] = useState(null)
  let data = cloneDeep(chartData);
  const container = useRef();
  const cRef = useRef()
  const dispatch = useDispatch();
  const Notify = notify(dispatch);
  const [width] = useState(100);
  let canvasFields = fields.filter((field) => !field.hiddenIncludeInResultSet);
  const {
    title, subTitle, format, formatColor, bar, axisRange, card, labels, line,
    chartTheme: { colorPalette },
    charts = {},
    canvas: canvasProperties = {},
    tooltip: { showTooltip = true, tooltipTemplate = "", enableTemplate = false } = {} } = report?.reportData?.properties || {};
  let labelsColor = isEmpty(labels?.labelsColor) ? null : getHTMLColorFormat(labels?.labelsColor);
  let { chartData: filteredData, rows, columns, schema, colorField, sizeField, detailField, labelField, measuresLabelFields = {}, shapeField, tooltipField } =
    getFieldInfo({ data, metadata, fields: canvasFields, marksList, axisRange });
  data = filteredData
  const { legendPosition } = report?.reportData?.properties.legend;
  let { show, value, padding, fontSize } = title;
  let {
    show: subTitleShow,
    value: subTitleValue,
    padding: subTitlePadding,
    fontSize: subTitleFontSize,
  } = subTitle;

  let height;
  let titleStyle = {};
  let subTitleStyle = {};

  let themeColors = getAntChartTheme(colorPalette, { ...chartColorPalette, ...defaultColorPaletteSchemes });

  if (themeColors?.length) {
    registerAntdTheme({ colors: themeColors });
  }

  if (props.chartAreaHeight) {
    height = props.chartAreaHeight - 15;
  }

  if (show && value) {
    titleStyle = getPropertyStyle(title);
    height = height - 15 - fontSize - 2 * padding;
  }

  if (subTitleShow && subTitleValue) {
    subTitleStyle = getPropertyStyle(subTitle);
    height = height - 15 - subTitleFontSize - 2 * subTitlePadding;
  }

  let antContainerStyles = {
    height
  }

  if (canvasProperties?.view) {
    let newContainerStyles = getCanvasStyles({ canvasProperties, chartAreaHeight: height, width: true })
    if (!isEmpty(newContainerStyles)) {
      antContainerStyles = newContainerStyles;
    }
  }

  let dimensions = schema
    .filter(
      (field) =>
        field.type === "dimension" && (rows.includes(field.name) || columns.includes(field.name))
    )
    .map((field) => field.name);
  let rowsMeasures = schema
    .filter((field) => field.type === "measure" && rows.includes(field.name))
    .map((field) => field.name);
  let columnMeasures = schema
    .filter((field) => field.type === "measure" && columns.includes(field.name))
    .map((field) => field.name);
  let markLayers = columnMeasures.length > rowsMeasures.length ? columnMeasures : rowsMeasures;
  let subVizType = "";

  subVizType = marksList[0].subVizType;
  subVizType = subVizTypes.includes(subVizType) ? subVizType : "bar";

  let propsList = {
    data: data,
    dimensions: dimensions,
    columnMeasures: columnMeasures,
    rowsMeasures: rowsMeasures,
    interactiveMode: interactiveMode,
    drillDown: drillDown,
    drillThrough: drillThrough,
    reportId: reportId,
    addFilter: props.addFilter,
    report: report,
    format: format,
    addForwardFilterIPC: addForwardFilterIPC,
    deleteBackwardFilterIPC: deleteBackwardFilterIPC,
    refreshFiltersIPC: refreshFiltersIPC,
    labelField: labelField,
    sizeField: sizeField,
    colorField: colorField,
    shapeField: shapeField,
    tooltipField: tooltipField,
    formatColor: formatColor,
    metadata: metadata,
    titleStyle,
    subTitleStyle,
    height,
    chartAreaWidth: props.chartAreaWidth,
    chartAreaHeight: props.chartAreaHeight,
    fields: fields,
    referenceLineList: referenceLineList,
    getApi: props.getApi,
    dataId: props.dataId,
    rows,
    columns,
    mode: props.mode,
    themeColors,
    subVizType,
    measuresLabelFields,
    Notify,
    showTooltip,
    tooltipTemplate,
    enableTemplate
  }

  if (columnMeasures.length === 0 && rowsMeasures.length === 0 && dimensions.length === 1 && subVizType !== "map") {
    let isCalendarViz = checkIfCalendarViz(fields, selectedType)

    if (isCalendarViz) {
      dispatch(updateSubVizType({ value: "_all_", name: "calendar" }));
      return (
        <AntCalenderHeatMap {...propsList} subVizType={"calendar"} />
      )
    }

    return (
      <WordCloudForSingleDimension
        cloudData={data}
        cloudKey={dimensions[0]}
        colorField={colorField}
        interactiveMode={interactiveMode}
        drillDown={drillDown}
        drillThrough={drillThrough}
        reportId={reportId}
        addFilter={props.addFilter}
        sizeField={sizeField}
        report={report}
        format={format}
        addForwardFilterIPC={addForwardFilterIPC}
        deleteBackwardFilterIPC={deleteBackwardFilterIPC}
        refreshFiltersIPC={refreshFiltersIPC}
        metadata={metadata}
        formatColor={formatColor}
        {...{
          titleStyle,
          subTitleStyle,
          height,
          themeColors,
          Notify,
          showTooltip,
          tooltipTemplate,
          enableTemplate
        }}
      />
    );
  }

  if (dimensions.length > 1 && rowsMeasures.length > 0 && columnMeasures.length >= 0) {
    dispatch(updateSubVizType({ value: "_all_", name: "relation" }));
    subVizType = 'relation';
  }

  if (
    (rowsMeasures.length > 1 ||
      columnMeasures.length > 1 ||
      (rowsMeasures.length > 0 && columnMeasures.length > 0)) &&
    ["arc", "doughnut", "text", "point"].includes(subVizType)
  ) {
    const msg =
      "Invalid data. Please use proper configuration. Two measures in column or rows is not supported.";
    Notify.warning({ type: "Frontend", message: msg });
    return <ErrorArea message={msg} />;
  }

  // throwing an error if there are more than 1 dimensions and 0 measures
  if (dimensions.length > 1 && rowsMeasures.length === 0 && columnMeasures.length === 0 &&
    !["calendar"].includes(subVizType)) {
    const msg =
      "Invalid data. Please use proper configuration. Only 1 dimension is required in columns and 1 measure is required in rows to generate chart.";
    Notify.warning({ type: "Frontend", message: msg });
    return <ErrorArea message={msg} />;
  }

  // throwing an error if there are more than 1 dimensions and 1 measures, if both are in only either column or row
  if (
    dimensions.length > 0 &&
    (rowsMeasures.length > 0 || columnMeasures.length > 0) &&
    (!columns.length || !rows.length)
  ) {
    const msg =
      "Invalid data. Please use proper configuration. Only 1 dimension is required in columns and 1 measure is required in rows to generate chart.";
    Notify.warning({ type: "Frontend", message: msg });
    return <ErrorArea message={msg} />;
  }

  if ((dimensions.length > 1 || rowsMeasures.length > 2 || columnMeasures.length > 2) && !["relation", "calendar"].includes(subVizType)) {
    const msg =
      "Invalid data. Please use proper configuration. Only 1 dimension is required in columns and 1 measure is required in row to generate chart.";
    Notify.warning({ type: "Frontend", message: msg });
    return <ErrorArea message={msg} />;
  }

  if (
    columnMeasures.length > 0 &&
    dimensions.length > 0 &&
    ["arc", "doughnut"].includes(subVizType)
  ) {
    const msg =
      "Invalid data. Please use proper configuration. 1 dimension in columns and 1 measure in rows is required to generate this chart";
    Notify.warning({ type: "Frontend", message: msg });
    return <ErrorArea message={msg} />;
  }

  // if(subVizType === "line" && columnMeasures.length > 0){
  //   const msg = "Invalid data. Please use proper configuration. Only 1 dimension is required in rows and 1 measure is required in columns to generate line chart."
  //   Notify.warning({ type: "Frontend", message: msg })
  //   return <ErrorArea message={msg} />
  // }

  if (subVizType === "text" && (columnMeasures.length > 0 || rowsMeasures.length > 0)) {
    const msg =
      "Invalid data. Please use proper configuration. Only 1 dimension is required in rows to generate cloud chart.";
    Notify.warning({ type: "Frontend", message: msg });
    return <ErrorArea message={msg} />;
  }

  // if(subVizType === "arc" && (columnMeasures.length > 0 || rowsMeasures.length > 0) && dimensions.length > 0){
  //   const msg = "Invalid data. Please use proper configuration."
  //   Notify.warning({ type: "Frontend", message: msg })
  //   return <ErrorArea message={msg} />
  // }
  let axisNameStyles = {
    fill: '#5f5f5f',
    fontSize: 14
  }

  let plots = [];
  const { showAxisName } = axisRange || {}
  markLayers.map((markLayer, i) => {
    let { id } = canvasFields.find((clmn) => getFieldDisplayName(clmn) === markLayer) || {};
    let { subVizType } = marksList.find((markItem) => markItem.id === id) || {};
    subVizType = subVizTypes.includes(subVizType) ? subVizType : "bar";
    if (subVizType === "bar" && rowsMeasures.length) {
      subVizType = "column";
    }
    let xField = columns[0];
    let layer = {
      type: subVizType === "point" ? "scatter" : subVizType,
      options: {
        xField,
        yField: markLayer,
        xAxis: {
          grid: null,
          visible: true,
          title: !showAxisName ? null : {
            text: xField,
            style: axisNameStyles
          }
        },
        yAxis: {
          grid: null,
          visible: true,
          title: !showAxisName ? null : {
            text: markLayer,
            style: axisNameStyles
          }
        },
        autoFit: bar?.autoFit
      },
    };
    if (charts?.enablePagination) {
      layer.options.slider = {
        start: charts?.starts ? charts?.starts / 100 : 0,
        end: charts?.ends ? (charts?.ends - 1) / 100 : 0.1
      }
    }
    if (themeColors?.length) {
      layer.options.theme = 'custom-theme';
    }
    if (subVizType === "point") layer.options.shape = "circle"
    if (subVizType === "column") {
      layer.options.minColumnWidth = 1
      if (bar?.maxBarWidth) {
        layer.options.maxColumnWidth = bar.maxBarWidth;
      }
    }
    if (subVizType === "bar") {
      layer.options.minBarWidth = 1
      if (bar?.maxBarWidth) {
        layer.options.maxBarWidth = bar.maxBarWidth;
      }
    }
    if (subVizType === "line") {
      layer.options.smooth = line?.smooth;
    }
    if (columnMeasures.length) {
      let yField = rows[0];
      layer.options.xField = markLayer;
      layer.options.yField = yField;
      layer.options.xAxis = {
        ...layer.options.xAxis,
        label: {
          formatter: (text) => {
            const { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(
              report,
              markLayer
            );
            let formattedText = getPropertyText({
              text,
              applyOn: "axis",
              isApplyClicked,
              fieldType,
              formatField,
            });
            return formattedText;
          },
        },
      };
      layer.options.yAxis = {
        ...layer.options.yAxis,
        title: !showAxisName ? null : {
          text: yField,
          style: axisNameStyles
        },
        label: {
          formatter: (text) => {
            const { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(report, yField);
            let formattedText = getPropertyText({
              text,
              applyOn: "axis",
              isApplyClicked,
              fieldType,
              formatField,
            });
            return formattedText;
          },
        },
      };
    } else {
      layer.options.xAxis = {
        ...layer.options.xAxis,
        label: {
          formatter: (text) => {
            const { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(report, xField);
            let formattedText = getPropertyText({
              text,
              applyOn: "axis",
              isApplyClicked,
              fieldType,
              formatField,
            });
            return formattedText;
          },
        },
      };
      layer.options.yAxis = {
        ...layer.options.yAxis,
        label: {
          formatter: (text) => {
            const { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(
              report,
              markLayer
            );
            let formattedText = getPropertyText({
              text,
              applyOn: "axis",
              isApplyClicked,
              fieldType,
              formatField,
            });
            return formattedText;
          },
        },
      };
    }
    if (colorField) {
      if (![...rows, ...columns].includes(colorField)) {
        layer.options.isStack = true;
      }
      layer.options.seriesField = colorField;
      layer.options.interactions = [{ type: "element-active" }, { type: "element-highlight" }];
    }

    if (formatColor.formatColorStyle === "gradient") {
      const { minimum, maximum, formatColorField } = formatColor
      if (minimum && maximum) {
        const prColorField = getFieldName(formatColorField, report);
        const fieldData = getFieldData(data, prColorField) || []
        const uniqueData = [...new Set(fieldData)];
        let colors = generateColorRange(minimum, maximum, uniqueData.length);
        if (formatColor?.enableSteps) {
          colors = generateColorsFromRange(uniqueData, formatColor);
        }
        if (prColorField) {
          if (![...rows, ...columns].includes(prColorField)) {
            layer.options.isStack = true;
          }
          layer.options.seriesField = prColorField;
          layer.options.color = colors
        }
      }
    }

    if (formatColor?.formatColorStyle === "fieldValue") {
      const propertyColorField = getFieldName(formatColor?.formatColorField, report);
      if (propertyColorField) {
        layer.options.colorField = propertyColorField;
        if (![...rows, ...columns].includes(propertyColorField)) {
          layer.options.isStack = true;
        }
        layer.options.color = (obj) => applyColor(obj, layer, formatColor, propertyColorField);
      }
    }

    if (i > 0) {
      layer.options.xAxis = false;
      layer.options.yAxis.position = "right";
    }
    if (labelField || !isEmpty(measuresLabelFields)) {
      layer.options.label = {
        position: labels?.position ? labels?.position : "middle",
        content: (obj) => {
          let tempLabelField = labelField;
          if (!isEmpty(measuresLabelFields)) {
            tempLabelField = measuresLabelFields[markLayer] ?? labelField;
          }
          const { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(
            report,
            tempLabelField
          );
          let formattedText = getPropertyText({
            text: obj[tempLabelField],
            applyOn: "label",
            isApplyClicked,
            fieldType,
            formatField,
          });
          if (bar.barType === "percentage") {
            if (!isNaN(+formattedText)) {
              return (+formattedText).toFixed(2) * 100 + "%"; // 8339 fix
            }
            // return formattedText.toFixed(2) * 100 + "%";
          }
          return formattedText;
        },
        rotate: labels?.rotateLabels ? 55 : 0,
        style: {}
      };
      if (labelsColor) {
        layer.options.label.style.fill = labelsColor;
      }
      if (labels?.fontSize) {
        layer.options.label.style.fontSize = labels?.fontSize;
      }
      if (labels?.offsetY || labels?.offsetX) {
        layer.options.label.offsetY = labels?.offsetY || 0
        layer.options.label.offsetX = labels?.offsetX || 0
      }
      if (labels?.position === 'top') {
        layer.options.label.offsetY = 8 + (labels?.offsetY || 0)
        if (labels?.rotateLabels) {
          layer.options.label.offsetX = 12 + (labels?.offsetX || 0)
        }
      }
    }
    // below method works only for 1 column value
    const fieldObj = fields.find((e) => e.label === columns[0]);
    if (fieldObj) {
      if (
        (!fieldObj.databaseFunction || Object.keys(fieldObj.databaseFunction).length === 0) &&
        fieldObj.type.dataType === "dateTime"
      ) {
        layer.options.xAxis = {
          ...layer.options.xAxis,
          tickCount: 5,
          // type: "timeCat",
          label: {
            formatter: (text) => {
              const { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(
                report,
                xField
              );
              let formattedText = getPropertyText({
                text,
                applyOn: "axis",
                isApplyClicked,
                fieldType,
                formatField,
              });
              return formattedText;
            },
          },
        };
      }
      if (fieldObj.databaseFunction) {
        if (fieldObj.databaseFunction.value === "MONTHNAME") {
          const months = [
            "January",
            "February",
            "March",
            "April",
            "May",
            "June",
            "July",
            "August",
            "September",
            "October",
            "November",
            "December",
          ];
          data.sort((a, b) => months.indexOf(a[columns[0]]) - months.indexOf(b[columns[0]]));
        }
      }
    }
    if (colorField) {
      if (bar.barType === "stacked") {
        if (![...rows, ...columns].includes(colorField)) {
          layer.options.isStack = true;
        }
      }
      if (bar.barType === "grouped") {
        layer.options.isGroup = true;
      }
      if (bar.barType === "percentage") {
        if (![...rows, ...columns].includes(colorField)) {
          layer.options.isStack = true;
        }
        layer.options.isPercent = true;
      }
    }

    if (axisRange?.gridLines?.length) {
      if (axisRange.gridLines.includes("x")) {
        layer.options.xAxis = {
          ...layer.options.xAxis,
          grid: {
            line: {
              style: {
                stroke: '#dddddd',
                lineWidth: 1,
                // lineDash: [2, 2],
              },
            },
          },
        }
      }
      if (axisRange?.gridLines?.includes("y")) {
        layer.options.yAxis = {
          ...layer.options.yAxis,
          grid: {
            line: {
              style: {
                stroke: '#dddddd',
                lineWidth: 1,
                // lineDash: [2, 2],
              },
            },
          },
        }
      }
    }

    if (axisRange?.fields && axisRange.fields.length) {
      axisRange.fields.forEach((field) => {
        const { hide, rotate, fontSize, fontColor } = field.data || {};
        let axisLabelColor = !isEmpty(fontColor) ? getHTMLColorFormat(fontColor) : null;
        if (layer.options.xField === field.data.name) {
          if (rotate || rotate === 0) {
            layer.options.xAxis = { ...layer.options.xAxis, label: { ...layer.options.xAxis.label, rotate: (rotate / 57), autoEllipsis: true, }, verticalFactor: (rotate < 0 || rotate > 0) ? -5 : -2 }
            if (rotate < 0 || rotate > 0) {
              layer.options.xAxis = { ...layer.options.xAxis, verticalLimitLength: !showAxisName ? 80 : 160 }
              if (showAxisName && i < 1) {
                layer.options.xAxis.title = { ...layer.options.xAxis.title, offset: 20 };
              }
            }
          }
          layer.options.xAxis = { ...layer.options.xAxis, visible: !hide }
          const rangeData = getPropertyAxisRange(field, "antChart")
          if (rangeData) {
            layer.options.xAxis = { ...layer.options.xAxis, ...rangeData }
          }
          layer.options.xAxis.label = { ...layer.options.xAxis.label, style: { fontSize } };
          if (axisLabelColor) {
            layer.options.xAxis.label = { ...layer.options.xAxis.label, style: { ...layer.options.xAxis.label.style, fill: axisLabelColor } }; // 7142
          }
        }
        if (layer.options.yField === field.data.name) {
          if (rotate || rotate === 0) {
            layer.options.yAxis = { ...layer.options.yAxis, label: { ...layer.options.yAxis.label, rotate: (rotate / 57) } }
          }
          layer.options.yAxis = { ...layer.options.yAxis, visible: !hide }
          const rangeData = getPropertyAxisRange(field, "antChart")
          if (rangeData) {
            layer.options.yAxis = { ...layer.options.yAxis, ...rangeData }
          }
          layer.options.yAxis.label = { ...layer.options.yAxis.label, style: { fontSize } };
          if (axisLabelColor) {
            layer.options.yAxis.label = { ...layer.options.yAxis.label, style: { ...layer.options.yAxis.label.style, fill: axisLabelColor } }; // 7142
          }
        }
      })
    }

    if (axisRange?.synchronize) {
      const { axis, ...rest } = getMinAndMaxRangeForSynchronize(rows, columns, fields, data)
      if (axis && axis === 'y') {
        layer.options.yAxis = { ...layer.options.yAxis, ...rest }
      }
      if (axis && axis === 'x') {
        layer.options.xAxis = { ...layer.options.yAxis, ...rest }
      }
    }
    let modifiedData = data;
    if (columnMeasures.length) {
      let yField = rows[0];
      modifiedData = data.map((item) => {
        return { ...item, [yField]: String(item[yField]) };
      });
    }
    layer.options.data = modifiedData;
    plots.push(layer);
  });

  let barComp = Column;
  subVizType = subVizType || "bar";
  if (subVizType === "bar" && columnMeasures.length) {
    barComp = Bar;
  }

  let VizConfig = {
    bar: barComp,
    line: Line,
    area: Area,
    arc: Pie,
  };
  let VizComp = VizConfig[subVizType];

  let config = {
    width,
    height,
  };
  VizComp = Mix;
  config.plots = plots;
  config.tooltip = !showTooltip ? false : {
    customContent: function (x, data) {
      if (data && data.length > 0) {
        const container = document.createElement("div");
        const tootipData = Object.entries(data[0].data);
        tootipData.forEach((e) => {
          const { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(report, e[0]);
          const value = getPropertyText({
            text: e[1],
            applyOn: "tooltip",
            isApplyClicked,
            fieldType,
            formatField,
          });
          const child = document.createElement("div");
          child.innerHTML = `${e[0]} : ${value}`;
          container.appendChild(child);
        });
        setStyles(container, divStyles);
        if (enableTemplate) {
          return getChartTooltipTemplate({ data: tootipData, tooltipTemplate, report, Notify });
        }
        return container;
      }
    },
  };

  config.legend =
    legendPosition === "none" ? false : {
      position: legendPosition ? legendPosition : "right",
      ...getAntChartLegendLabelConfig(report, colorField),
    };

  config.syncViewPadding = true;

  const onBarClick = (args, plot) => {
    const { x, y } = args;
    const tooltipData = plot.chart.getTooltipItems({ x, y });
    if (tooltipData[0]?.data) {
      const barData = tooltipData[0].data;

      if (!interactiveMode) {
        return null;
      }
      if (!drillDown && !drillThrough) {
        return null;
      }
      let payload = [];
      Object.keys(barData).map((key) => {
        payload.push({ field: key, value: barData[key] });
      });
      // let { top, left } = args.view.ele.getBoundingClientRect();
      let { clientX: left, clientY: top } = args || {}
      if (!['dashboard'].includes(props.mode)) {
        top = y;
        left = x;
      }
      // top = args.data.y;
      // left = args.data.x;
      let menuData = {
        payload,
        position: { top, left },
        drillDownFilterValues: data,
      };
      dispatch(setMenuData({ reportId, menu: menuData }));
    }
  };

  // showing tooltip data for only highlighted fields for stack bar chart,
  // assuming adding property in "color" produces stacked chart
  if (!colorField && showTooltip) {
    config.tooltip.shared = true;
  }

  if (themeColors?.length) {
    config.theme = 'custom-theme';
  }

  propsList.subVizType = subVizType;

  if (subVizType === "arc" && (columnMeasures.length === 1 || rowsMeasures.length === 1)) {
    return (
      <ArcChart {...propsList} />
    );
  }

  if (
    subVizType === "area" &&
    dimensions.length === 1 &&
    (columnMeasures.length === 1 || rowsMeasures.length === 1)
  ) {
    return (
      <AreaChart {...propsList} />
    );
  }

  if (subVizType === "doughnut" && (columnMeasures.length === 1 || rowsMeasures.length === 1)) {
    return (
      <DonutChart {...propsList} />
    );
  }

  if (
    subVizType === "point" &&
    dimensions.length === 1 &&
    (columnMeasures.length === 1 || rowsMeasures.length === 1)
  ) {
    return (
      <ScatterChart {...propsList} />
    );
  }


  if (subVizType === "waterfall") {
    return (
      <AntWaterFallChart {...propsList} />
    )
  }

  if (subVizType === "radar") {
    return <AntRadarChart {...propsList} />
  }

  if (subVizType === "progress") {
    return <AntProgressChart  {...propsList} />
  }


  if (subVizType === "relation") {
    return (
      <AntRelationCharts {...propsList} />
    )
  }

  if (subVizType === "calendar") {
    return (
      <AntCalenderHeatMap {...propsList} />
    )
  }

  return (
    <div ref={container} style={antContainerStyles} className="hr-muze-container">
      <div className="title-subtitle-container">
        {title?.position === "top" && getPropertyElement(titleStyle, title)}
        {subTitle?.position === "top" && getPropertyElement(subTitleStyle, subTitle)}
      </div>
      {(interactiveMode || drillThrough) && (
        <Row justify="end">
          <Toolbar
            addForwardFilterIPC={props.addForwardFilterIPC}
            deleteBackwardFilterIPC={props.deleteBackwardFilterIPC}
            refreshFiltersIPC={props.refreshFiltersIPC}
            reportId={reportId}
            getApi={props.getApi}
          />
        </Row>
      )}
      {!dimensions.length && !["arc", "donut"].includes(subVizType) ? (
        <HiCard
          markLayers={markLayers}
          data={data}
          report={report}
          formatColor={formatColor}
          subVizType={subVizType}
          reportId={reportId}
          detailField={detailField}
          interactiveMode={interactiveMode}
          addFilter={props.addFilter}
          getApi={props.getApi}
          chartAreaHeight={props.chartAreaHeight}
          chartAreaWidth={props.chartAreaWidth}
          format={format}
          card={card}
          isPrintMode={isPrintMode}
          fields={fields}
          canvasProperties={canvasProperties}
        />
      ) : (
        <VizComp
          {...config}
          chartRef={cRef}
          onReady={(plot) => {
            plot.on("click", (args) => {
              onBarClick(args, plot);
            });
          }}
          onEvent={(plot, event) => {
            if (event?.type === "beforerender") {
              setChartContext(plot)
            }
          }}
        />
      )}
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
        {subTitle?.position === "bottom" && getPropertyElement(subTitleStyle, subTitle)}
      </div>
      {
        chartContext
        &&
        plotReferenceLineContent({ chartAreaWidth: props.chartAreaWidth, chartAreaHeight: props.chartAreaHeight, referenceLineList, fields: fields, chartContext, config, columnMeasures, rowsMeasures })
      }
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
export default React.memo(AntChart, areEqual);


export const plotReferenceLineContent = ({ chartAreaHeight, chartAreaWidth, fields, referenceLineList, chartContext, columnMeasures, rowsMeasures }) => {
  let enabledReferenceLines = referenceLineList.filter((item) => item?.enabled);
  let isAllEnabled = referenceLineList.find((item) => item?.display === referenceLineAll && item.enabled)
  let referenceLineContent = null;
  referenceLineContent = <React.Fragment>
    {enabledReferenceLines?.map((rLine, _i, arr) => {
      const { value, id, display } = rLine || {}
      const field = fields?.find((item) => item?.id === id);
      const fieldName = !isAllEnabled ? getFieldDisplayName(field) : rowsMeasures?.[0] || columnMeasures?.[0]
      const [scaleMinValue, maxScaleValue] = getChartMaxScaleValue(chartContext, fieldName)
      const referenceLineAtY = !columnMeasures?.includes(fieldName)
      const factor = referenceLineAtY ? chartAreaHeight - 42 : chartAreaWidth
      if (!value > maxScaleValue) return null;
      const scaleWidth = calculateScaleWidthByValue(maxScaleValue)
      if (value && !isAllEnabled) {
        let referenceLineTop = calculateReferenceLinePosition(maxScaleValue, scaleMinValue || 0, factor, value, referenceLineAtY)
        return (
          <CustomReferenceLine
            height={referenceLineAtY ? chartAreaHeight : chartAreaHeight - scaleWidth}
            width={chartAreaWidth - scaleWidth}
            top={referenceLineTop}
            referenceLineAtY={referenceLineAtY}
          />
        )
      }
      if (isAllEnabled && display === referenceLineAll) {
        let referenceLineTop = calculateReferenceLinePosition(maxScaleValue, scaleMinValue || 0, factor, value, referenceLineAtY)
        return (
          <CustomReferenceLine
            height={referenceLineAtY ? chartAreaHeight : chartAreaHeight - scaleWidth}
            width={chartAreaWidth - scaleWidth}
            top={referenceLineTop}
            referenceLineAtY={referenceLineAtY}
          />
        )
      }
      return null;
    })}
  </React.Fragment>
  return referenceLineContent;
}
