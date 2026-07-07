import Muze, { Canvas, Layer } from "@chartshq/react-muze/components";
import {
  Axes,
  Behaviours,
  Encoding,
  Legend,
  SideEffects,
  Tooltip
} from "@chartshq/react-muze/configurations";
import { Row } from "antd";
import { isEmpty, isEqual } from "lodash-es";
import React, { useEffect, useRef, useState } from "react";
import { useDispatch } from "react-redux";
import { v4 as uuidv4 } from 'uuid';
import { setMenuData } from "../../../../redux/actions/hreport.actions";
import { getFieldDisplayName } from "../../../../utils/utilities";
import notify from "../../../hi-notifications/notify";
import CustomReferenceLine from "../../hi-editing-area/components/values/reference-line/custom-reference-line";
import {
  generateColorRange,
  generateColorsFromRange,
  getFieldData,
  getFieldName,
  getGridChartColorFormatScheme,
  getHTMLColorFormat
} from "../../hi-editing-area/utils/property-utils";
import { referenceLineAll } from "../../utils/constants";
import { getAntChartTheme } from "../ant-charts/ant-utils";
import { defaultColorPaletteSchemes } from "../ant-charts/constants";
import CellCard from "../cell-card";
import Toolbar from "../toolbar";
import {
  checkIfColorFieldContinuous,
  checkMonthFnIndex,
  dateFormat,
  formatterFn,
  getFieldInfo,
  getGridChartColorSchemeFromPalette,
  getGridChartSingleMeasureLayer,
  getGridChartTextLayerConfig,
  getTransformType
} from "../utils/grid-chart-utils";
import {
  changeToTotal,
  getAxisConfig,
  getDividedArray,
  getPropertyElement,
  getPropertyFieldInfo,
  getPropertyStyle,
  getPropertyText,
  getSortObject,
  gridChartNumberFormat,
  hrNumberFormat,
  removeNullKeysFromObject
} from "../utils/utillities";
import "./grid-chart.scss";
import GridTextChart from "./grid-text-chart";

// const share = Muze.Operators.share;
const composeLayers = Layer.Operators.compose;
const { SpawnableSideEffect } = SideEffects;
const { DataModel, Operators, Utils } = Muze;
// const { html } = Operators;
const utils = Utils;
const createDataModel = async (data, schema) => {
  const DataModelClass = await DataModel.onReady();
  data = changeToTotal(data)
  const formattedData = await DataModelClass.loadData(data, schema);
  return new DataModelClass(formattedData);
};
const subVizTypes = [
  "bar",
  "line",
  "area",
  "point",
  "tick",
  "arc",
  "doughnut",
  "heatmap",
  "text"
];
const GridChart = (props) => {
  let {
    data,
    metadata,
    fields,
    marksList,
    interactiveMode,
    reportId,
    properties,
    combine,
    mode,
    analytics,
    referenceLineList,
    report: activeReport,
    chartColorPalette
  } = props;
  let report = { properties, fields, reportData: activeReport?.reportData };
  const muzeChartId = `hr-muze-chart-container-${uuidv4()}`;
  const dispatch = useDispatch();
  const container = useRef();
  const canvasRef = useRef(null);
  const gridRef = useRef();
  const Notify = notify(dispatch);
  const { title, subTitle, format, formatColor, bar, radial, axisRange, labels, line, chartTheme: { colorPalette } } = report?.properties || {};
  const { barType } = bar || {};
  let themeColors = getAntChartTheme(colorPalette, { ...chartColorPalette, ...defaultColorPaletteSchemes });
  const { legendPosition } = report.properties.legend || "";
  let { show, value, padding } = title || {};
  let {
    show: subTitleShow,
    value: subTitleValue,
    padding: subTitlePadding,
  } = subTitle || {}; // destructure with With alias

  let height;
  let width;
  let titleStyle = {};
  let subTitleStyle = {};

  const [formattedData, setFormattedData] = useState({
    chartDm: null,
    rows: [],
    columns: [],
    layers: [],
    config: [],
    detail: [],
    color: [],
    size: [],
    shape: [],
    xAxis: {},
    yAxis: {},
    sortObject: {},
  });
  const [dataId, setDataId] = useState(null)
  const [hmMessageVisible, setHmMessageVisible] = useState(false);
  const timer = useRef();
  useEffect(() => {
    let message = "Invalid data. Please use proper configuration";
    let { subVizType } = props.marksList[0];
    subVizType = subVizType || "bar";
    if (["bar", "line", "point", "area", "tick"].includes(subVizType)) {
      message = `${message} . Atleast 1 dimension is required in rows and 1 measure is required in columns 
      to generate ${subVizType} chart`;
    }
    if (["heatmap"].includes(subVizType)) {
      message = `${message} . Atleast 1 dimension is required in rows and 1 dimension is required in columns 
        to generate ${subVizType} chart`;
    }
    if (["arc", "doughnut"].includes(subVizType)) {
      message = `${message} . Atleast 1 measure is required in rows or columns to generate ${subVizType}
       chart`;
    }
    setTimeout(function () {
      let invalidTemplate = container.current?.querySelector(
        ".muze-message-container"
      );
      if (invalidTemplate) {
        invalidTemplate.innerHTML = `<div class="viz-invalid-state" > ${message} </div>`;
        timer.current = () => {
          Notify.warning({ type: "Front end", message });
        };
      }
    }, 500);
    setTimeout(() => {
      if (timer.current) {
        timer.current();
        timer.current = null;
      }
    }, 1000);
    const getNearestGridAnsector = (elem) => {
      if (!elem) return null;
      if (elem.tagName === "BODY") return null;
      if (elem.className?.includes("react-grid-item")) {
        gridRef.current = elem;
        return elem;
      }
      return getNearestGridAnsector(elem.parentElement);
    };
    gridRef.current =
      gridRef.current || getNearestGridAnsector(container.current);
  });

  let canvasFields = fields.filter((field) => !field.hiddenIncludeInResultSet && field.addedAs !== "measure_field");
  useEffect(() => {
    if (marksList[0].subVizType === "heatmap") {
      if (
        !rows.length ||
        !columns.length ||
        !isDimensionInRow ||
        !isDimensionInColumn
      ) {
        return null;
      }
    }
    createChart();
  }, [dataId]);

  useEffect(() => {
    setDataId(props.dataId)
  }, [props.dataId])

  let isRadialPropertyEnabled = radial?.showRadial || radial?.showRadialLabel || radial?.showRadialValue;
  let labelsColor = isEmpty(labels?.labelsColor) ? null : getHTMLColorFormat(labels?.labelsColor);

  if (props.chartAreaHeight) {
    height = props.chartAreaHeight;
  }

  if (props.chartAreaWidth) {
    width = props.chartAreaWidth;
  }

  let {
    rows,
    columns,
    schema,
    chartData,
    colorField,
    colorStep,
    sizeField,
    labelField,
    tooltipField,
    shapeField,
    measuresLabelFields = {}
  } = getFieldInfo({ data, metadata, fields: canvasFields, marksList, axisRange });
  let rowsMeasures = schema
    .filter((field) => field.type === "measure" && rows.includes(field.name))
    .map((field) => field.name);
  let columnMeasures = schema
    .filter((field) => field.type === "measure" && columns.includes(field.name))
    .map((field) => field.name);
  let markLayers =
    columnMeasures.length > rowsMeasures.length ? columnMeasures : rowsMeasures;
  let dimensions = schema.filter(
    (field) =>
      field.type === "dimension" && (rows.includes(field.name) || columns.includes(field.name))
  ).map((field) => field.name);
  let layers = [];
  let subVizType = "";
  let heatMapField = "";
  let chartsLocationDataOnAxes = [];
  let arcAngleMappingData = {};
  let arcColorData = {};
  let totalOfIndexes = 0;
  const vizTypes = [];
  let legendColorField = colorField;
  let isDimensionInRow = schema.find(
    (item) => item.type === "dimension" && rows.includes(item.name)
  );
  let isDimensionInColumn = schema.find(
    (item) => item.type === "dimension" && columns.includes(item.name)
  );
  let isMeasureInColumn = schema.find(
    (item) => item.type === "measure" && columns.includes(item.name)
  );
  let isMeasureInRows = schema.find(
    (item) => item.type === "measure" && rows.includes(item.name)
  );

  if (marksList[0].subVizType === "heatmap") {
    if (
      !rows.length ||
      !columns.length ||
      !isDimensionInRow ||
      !isDimensionInColumn
    ) {
      let message = "Invalid data. Please use proper configuration";
      let { subVizType } = props.marksList[0];
      subVizType = subVizType || "bar";
      if (["bar", "line", "point", "area", "tick"].includes(subVizType)) {
        message = `${message} . Atleast 1 dimension is required in rows and 1 measure is required in columns 
        to generate ${subVizType} chart`;
      }
      if (["heatmap"].includes(subVizType)) {
        message = `${message} . Atleast 1 dimension is required in rows and 1 dimension is required in columns 
          to generate ${subVizType} chart`;
      }
      if (["arc", "doughnut"].includes(subVizType)) {
        message = `${message} . Atleast 1 measure is required in rows or columns to generate ${subVizType}
        chart`;
      }
      return (
        <div ref={container} style={{ width: "100%", height: "100%" }}>
          <div
            className="muze-message-container"
            style={{ width: "100%", height: "100%" }}
          >
            <div className="viz-invalid-state"> {message}</div>
          </div>
        </div>
      );
    }
  }
  if (markLayers.length < 2) {
    subVizType = marksList[0].subVizType;
    subVizType = subVizTypes.includes(subVizType) ? subVizType : "bar";
    let layer = getGridChartSingleMeasureLayer(
      subVizType,
      schema,
      canvasFields,
      formattedData,
      labelField,
      totalOfIndexes,
      rows,
      report,
      chartsLocationDataOnAxes,
      arcColorData,
      arcAngleMappingData)
    layers.push(layer);
    if (subVizType === "heatmap") {
      let measureField = schema.find((field) => field.type === "measure");
      if (measureField) {
        heatMapField = measureField.name;
        let index = rows.indexOf(heatMapField);
        if (index > -1) {
          rows.splice(index, 1);
        }
        index = columns.indexOf(heatMapField);
        if (index > -1) {
          columns.splice(index, 1);
        }
        if (labelField) {
          let textLayer = {
            mark: "text",
            encoding: {
              text: {
                field: labelField,
                formatter: (...args) => {
                  const [text] = args || []
                  let actualText = text;
                  const { isApplyClicked, fieldType, formatField } =
                    getPropertyFieldInfo(report, labelField);
                  let formattedText = getPropertyText({
                    text: actualText,
                    applyOn: "label",
                    isApplyClicked,
                    fieldType,
                    formatField,
                  });
                  return formattedText;
                },
              },
              color: {
                value: () => labelsColor ? labelsColor : "#fff"
              },
              rotation: {
                value: () => {
                  return labels?.rotateLabels ? 90 : 0
                }
              }
            },
          }
          layers.push(textLayer);
        }
        // layers.push({
        //   mark: "text",
        //   encoding: {
        //     text: {
        //       field: heatMapField,
        //       formatter: hrNumberFormat,
        //     },
        //     color: {
        //       value: () => "#fff",
        //     },
        //   },
        //   // interactive: false
        // });
      }
      if (!hmMessageVisible) {
        if (!colorField) {
          Notify.warning({ message: "Generated heatmap may not display properly. Please add a measure field to the Color mark to visualize meaningful results." })
          setHmMessageVisible(true);
        }
      }
    }
    if (vizTypes.includes(subVizType)) {
      if (rows.includes(colorField) || columns.includes(colorField)) {
        let message = `${colorField} can not use as color mark for ${vizTypes.join(
          ","
        )} charts .since 
					it is present in rows/columns`;
        colorField = "";
        Notify.warning({ message });
      }
    }
  } else {
    const subVizTypeMap = new Map([
      ["bar", "Bar"],
      ["line", "Line"],
      ["area", "Area"],
      ["point", "Point"],
      ["arc", "Arc"],
      ["tick", "Tick"],
    ]);
    layers = markLayers.map((markLayer) => {
      let { id, addedAs } =
        canvasFields.find((clmn) => getFieldDisplayName(clmn) === markLayer) ||
        {};
      let { subVizType } =
        marksList.find((markItem) => markItem.id === id) || {};
      subVizType = subVizTypes.includes(subVizType) ? subVizType : "bar";
      let axis = addedAs === "column" ? "x" : "y";
      let layer = {
        mark: subVizType,
        name: subVizType,
      };

      const layerEncoding =
        Encoding[subVizTypeMap.get(subVizType)].config().create({
          [axis]: {
            field: markLayer,
            value: (d, i, data, ctx) => {
              if (labelField && subVizType === "bar") {
                // to remove the double lables and faded bar color
                new Promise((res) => {
                  const element = ctx.mount()
                  res(element)
                }).then((el) => {
                  if (el) {
                    el?.classList?.remove("muze-layer-text")
                  }
                  let parentElement = el.parentNode
                  if (parentElement) {
                    let firstChild = parentElement?.children[0]
                    if (firstChild && firstChild?.children?.length > 3) {
                      let child = firstChild.children[0]
                      child?.remove()
                    }
                  }
                })
              }
              return d[axis]
            }
          },
          labelField: labelField,
        });
      if (subVizType === 'area') {
        layerEncoding.strokeOpacity = {
          value: (item) => {
            item.style['fill-opacity'] = 0.8
            return 0.8
          }
        }
      }
      layer.encoding = layerEncoding


      if (formatColor?.formatColorStyle === "fieldValue") {
        const fieldName = getFieldName(formatColor?.formatColorField, report);
        const fieldType = schema.find((schema) => schema.name === fieldName)
        if (fieldName && markLayer !== fieldName && fieldType?.type === 'measure') {
          layer.encoding = {
            ...layer.encoding,
            color: {
              value: () => "rgb(51, 122, 183)"
            }
          };
        }
      }

      if (formatColor?.formatColorStyle === "gradient") {
        if (formatColor?.minimum && formatColor?.maximum) {
          const fieldName = getFieldName(formatColor?.formatColorField, report);
          const fieldType = schema.find((schema) => schema.name === fieldName)
          if (fieldName && markLayer !== fieldName && fieldType?.type === 'measure') {
            layer.encoding = {
              ...layer.encoding,
              color: {
                value: () => "rgb(51, 122, 183)"
              }
            };
          }
        }
      }

      if (subVizType === "arc" || subVizType === "doughnut") {
        let measureField = schema.find((field) => field.type === "measure");
        layer.mark = "arc";
        if (measureField) {
          layer.encoding.angle = measureField.name;
        }
      }
      if (vizTypes.includes(subVizType)) {
        if (rows.includes(colorField) || columns.includes(colorField)) {
          let message = `${colorField} can not use as color mark for ${vizTypes.join(
            ","
          )} charts .since 
						it is present in rows/columns`;
          colorField = "";
          Notify.warning({ message });
        }
      }
      return layer;
    });
  }

  if (combine) {
    layers = [
      {
        name: "combinedChart",
        mark: "combinedChart",
        encoding: {},
      },
    ];

    let sub_layers = [];
    markLayers.map((markLayer) => {
      let { id, addedAs } =
        canvasFields.find((clmn) => getFieldDisplayName(clmn) === markLayer) ||
        {};
      let { subVizType, value } =
        marksList.find((markItem) => markItem.id === id) || {};
      layers[0].encoding[value] = markLayer;
      subVizType = subVizTypes.includes(subVizType) ? subVizType : "bar";
      let layer = {
        mark: subVizType,
      };
      layer.encoding = {
        x: "combinedChart.encoding." + (addedAs === "column" ? value : "x"),
        y: "combinedChart.encoding." + (addedAs === "row" ? value : "y"),
        color: "combinedChart.encoding.color",
        size: "combinedChart.encoding.size",
        shape: "combinedChart.encoding.shape",
        tooltip: "combinedChart.encoding.shape",
      };
      if (subVizType === "arc" || subVizType === "doughnut") {
        let measureField = schema.find((field) => field.type === "measure");
        layer.mark = "arc";
        if (measureField) {
          layer.encoding.angle = measureField.name;
        }
      }
      if (formatColor?.formatColorStyle === "fieldValue") {
        const fieldName = getFieldName(formatColor?.formatColorField, report);
        const fieldType = schema.find((schema) => schema.name === fieldName)
        if (fieldName && markLayer !== fieldName && fieldType?.type === 'measure') {
          layer.encoding = {
            ...layer.encoding,
            color: {
              value: () => "rgb(51, 122, 183)"
            }
          };
        }
      }
      if (subVizType === "line") {
        layer.interpolate = line?.smooth ? 'catmullRom' : 'linear';
      }
      if (formatColor?.formatColorStyle === "gradient") {
        if (formatColor?.minimum && formatColor?.maximum) {
          const fieldName = getFieldName(formatColor?.formatColorField, report);
          const fieldType = schema.find((schema) => schema.name === fieldName)
          if (fieldName && markLayer !== fieldName && fieldType?.type === 'measure') {
            layer.encoding = {
              ...layer.encoding,
              color: {
                value: () => "rgb(51, 122, 183)"
              }
            };
          }
        }
      }
      if (vizTypes.includes(subVizType)) {
        if (rows.includes(colorField) || columns.includes(colorField)) {
          let message = `${colorField} can not use as color mark for ${vizTypes.join(
            ","
          )} charts .since 
						it is present in rows/columns`;
          colorField = "";
          Notify.warning({ message });
        }
      }
      sub_layers.push(layer);
      if (labelField || !isEmpty(measuresLabelFields)) {
        let tempLabelField = labelField;
        if (!isEmpty(measuresLabelFields)) {
          tempLabelField = measuresLabelFields[markLayer] ?? labelField;
        }
        let textLayer = {
          // name: layer.name + "text",
          mark: "text",
          encoding: {
            x: layer.encoding.x,
            y: layer.encoding.y,
            // text: "combinedChart.encoding." + labelField,
            text: {
              field: tempLabelField,
              formatter: (text) => {
                const { isApplyClicked, fieldType, formatField } =
                  getPropertyFieldInfo(report, tempLabelField);
                let formattedText = getPropertyText({
                  text,
                  applyOn: "label",
                  isApplyClicked,
                  fieldType,
                  formatField,
                });
                return formattedText;
              },
            },
            rotation: {
              value: () => {
                return labels?.rotateLabels ? 90 : 0
              }
            },
            color: {
              value: () => labelsColor ? labelsColor : "#000"
            }
          }
        };
        sub_layers.push(textLayer);
      }
    });
    composeLayers("combinedChart", sub_layers);
  }

  if ((((labelField || !isEmpty(measuresLabelFields)) && !combine) || (isRadialPropertyEnabled && colorField)) && !heatMapField) {
    markLayers.map((mLayer) => {
      let tempLabelField = isRadialPropertyEnabled ? (rowsMeasures[0] || columnMeasures[0]) : labelField;
      if (!isEmpty(measuresLabelFields) && !isRadialPropertyEnabled) {
        tempLabelField = measuresLabelFields[mLayer] ?? labelField
      }
      let textLayer = getGridChartTextLayerConfig({
        labelField: tempLabelField,
        measuresLabelFields,
        axisName: mLayer,
        isRadialPropertyEnabled,
        subVizType,
        radial,
        labelsColor,
        labels,
        isDimensionInColumn,
        isDimensionInRow,
        isMeasureInRows,
        isMeasureInColumn,
        barType,
        schema,
        chartsLocationDataOnAxes,
        colorField,
        columns,
        arcAngleMappingData,
        arcColorData,
        report,
        markLayers,
        heatMapField
      })
      if (subVizType !== "arc" && subVizType !== "doughnut") {
        delete textLayer.encoding.radius;
        delete textLayer.encoding.angle;
      }
      layers.push(textLayer);
    })
  }

  let sortConfig = [];
  let sortObject = {};

  let hiddenCanvasFields = fields.filter((field) => field.hiddenIncludeInResultSet);
  let allCanvasFields = hiddenCanvasFields.length ? [...canvasFields, ...hiddenCanvasFields] : canvasFields
  let {
    schema: allFieldsSchema,
  } = getFieldInfo({ data, metadata, fields: hiddenCanvasFields.length ? allCanvasFields : canvasFields, marksList })
  const sortingObjKeys = allCanvasFields.filter((field) => field?.orderBy && field?.orderBy?.length).map((field) => field?.alias || field?.autogen_alias)
  allCanvasFields.map((field) => {
    let { orderBy, databaseFunction } = field;
    let alias = field.alias ? field.alias : field.autogen_alias;
    let isPresentSchema = allFieldsSchema.find((field) => field.name === alias);
    if (!isPresentSchema) return null;
    if (databaseFunction && databaseFunction.value === "MONTHNAME") {
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
      getSortObject(alias, sortObject, months, sortingObjKeys, orderBy)
    } else if (databaseFunction && databaseFunction.value === "MONTH" && !checkMonthFnIndex(canvasFields, alias)) {
      const months = [
        "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"
      ];
      getSortObject(alias, sortObject, months, sortingObjKeys, orderBy)
    } else if (orderBy && orderBy.length > 0) {
      sortConfig.push([alias, orderBy[0]]);
      sortObject[alias] = orderBy[0];
    } else {
      sortObject[alias] = null;
    }
  });
  let xAxis = {};
  let yAxis = {};
  if (subVizType === "heatmap") {
    yAxis.padding = 0;
    xAxis.padding = 0;
  } else {
    yAxis.padding = 0.2;
    xAxis.padding = 0.2;
  }
  let color = [],
    size = [],
    shape = [];
  // if (heatMapField) {
  //   color = [{ field: heatMapField }];
  // }
  if (
    !colorField &&
    columns.length &&
    rows.length &&
    labelField
  ) {
    color = [
      {
        field: labelField ? labelField : rows[0],
        range: ["rgb(51, 122, 183)", "rgb(51, 122, 183)"]
      },
    ];
  }
  if (colorField) {
    color = [
      {
        field: colorField,
        step: colorStep,
      },
    ];
  }
  if (formatColor?.formatColorStyle === "fieldValue") {
    const fieldName = getFieldName(formatColor?.formatColorField, report);
    const domainData = getFieldData(data, fieldName);
    const colors = getGridChartColorFormatScheme(formatColor, domainData, combine);
    if (fieldName) {
      legendColorField = fieldName;
      color = [
        {
          field: fieldName,
          domain: domainData,
          range: colors,
        },
      ];
    }
  }
  if (formatColor?.formatColorStyle === "gradient") {
    if (formatColor?.minimum && formatColor?.maximum) {
      const fieldName = getFieldName(formatColor?.formatColorField, report);
      const fieldData = getFieldData(data, fieldName);
      let colors = generateColorRange(
        formatColor?.minimum,
        formatColor?.maximum,
        data.length,
      );
      if (formatColor?.enableSteps) {
        colors = generateColorsFromRange(fieldData, formatColor);
      }
      if (fieldName) {
        legendColorField = fieldName;
        color = [
          {
            field: fieldName,
            domain: getFieldData(data, fieldName),
            range: colors,
          },
        ];
      }
    }
  }
  if (sizeField) {
    size = [
      {
        field: sizeField,
      },
    ];
  }
  if (shapeField) {
    shape = [
      {
        field: shapeField,
      },
    ];
  }
  const createChart = () => {
    createDataModel(chartData, schema).then((dm) => {
      dm = dm.sort(sortConfig);
      setFormattedData({
        chartDm: dm,
      });
    });
  };
  class Lasso extends SpawnableSideEffect {
    constructor(...params) {
      super(...params);
      this._path = [];
    }

    static formalName() {
      return "lasso-selection";
    }

    static target() {
      return "visual-unit";
    }

    apply(selectionSet, payload) {
      let selectable = false;
      let cellMenuData = null;
      dispatch((dispatch, getState) => {
        let activeReport =
          getState().hreport.present.reports.find((item) => item.id === reportId) || {};
        selectable = activeReport.toolbarConfig.selectable;
        cellMenuData = activeReport.cellMenuData;
      });
      try {
        if ((payload.dragEnd && selectable) || payload.type === "singleClick") {
          let [selectedField] = payload.criteria?.dimensions.flat() || [];
          let selectedDataArr = payload.criteria?.dimensions;
          let selectedDataObj = {};
          if (!selectedField) return null;
          selectedDataArr[0].map((field, i) => {
            selectedDataObj[field] = selectedDataArr
              .filter((vals, i) => i !== 0)
              .map((vals) => {
                return vals[i];
              });
          });
          let layers = canvasRef.current.state.layerConfig;
          let layerKey = Object.keys(layers)[0];
          let layerName = layers[layerKey].name;
          const layer = !combine ? this.firebolt.context.getLayerByName(layerName) : this.firebolt.context.layers()[0]

          if (!layer) return null;

          let mount = this.firebolt.context.mount();
          let { top: mount_top, left: mount_left } =
            mount.getBoundingClientRect();
          let { top: parent_top, left: parent_left } =
            container.current.getBoundingClientRect();
          let tempX = mount_left - parent_left + payload.position?.x;
          let tempY = mount_top - parent_top + payload.position?.y;
          let x = mode === "dashboard" ? payload.cardPos?.x : tempX;
          let y = mode === "dashboard" ? payload.cardPos?.y : tempY;
          if (payload.dragEnd) {
            x = parent_left;
            y = parent_top;
          }
          if (payload.dragEnd) {
            x = parent_left;
            y = parent_top;
          }
          const { data, schema: cSchema } = chartDm?.getData() || {};
          let mergedData = [];
          if (['arc', 'doughnut'].includes(subVizType)) {
            let contextData = layer.data()._model._parent._context.getData();
            const { data: cData } = contextData;
            contextData.schema.map((item, i) => {
              if (item.name !== "__id__") {
                mergedData.push({
                  field: item.name,
                  value: cData.map((value) => {
                    if (item?.subtype === "temporal") {
                      return dateFormat(value[i], item?.format)
                    }
                    return value[i]
                  }),
                });
              }
            });
          } else {
            let dataIndexes = payload?.uids || []
            cSchema?.map((item, i) => {
              if (item.name !== "__id__") {
                mergedData.push({
                  field: item.name,
                  value: data.filter((_, i) => dataIndexes.includes(i))?.map((value) => {
                    return value[i]
                  }),
                  // value: data?.map((value) => {
                  //   return value[i]
                  // }),
                });
              }
            });
          }
          Object.keys(selectedDataObj).map((key) => {
            let indexes = [];
            selectedDataObj[key].map((val) => {
              mergedData.map((fieldInfo) => {
                if (fieldInfo.field === key) {
                  fieldInfo.value.map((value, i) => {
                    if (value === val) {
                      indexes.push(i);
                    }
                  });
                }
              });
            });
            if (!indexes.length) return null;
            mergedData = mergedData.map((fieldInfo) => {
              let funcName = schema.find(
                (f) => f.name === fieldInfo.field
              )?.funcName;
              fieldInfo.value = fieldInfo.value
                .filter((val, i) => {
                  return indexes.includes(i);
                })
                .map((val) => (funcName ? dateFormat(val, funcName) : val));
              return fieldInfo;
            });
          });
          let menuData = {
            payload: mergedData,
            position: { top: y, left: x, right: 0, bottom: 0 },
            drillDownFilterValues: data,
          };
          if (cellMenuData && isEqual(cellMenuData.payload, menuData.payload))
            return null;
          dispatch(setMenuData({ reportId, menu: menuData }));
        }
      } catch (e) {
        console.log(e);
      }
      return null;
    }
  }
  const selectAction = (firebolt) => (targetEl) => {
    targetEl.on("click", function (data) {
      const event = utils.getEvent();
      const mousePos = utils.getClientPoint(this, event);
      let { x, y } = event;
      let cardPos = { x, y };
      const payload = firebolt.getPayloadFromEvent("dataSelection", mousePos, {
        data,
        event,
      });
      firebolt.triggerPhysicalAction("dataSelection", {
        ...payload,
        cardPos,
        mousePos,
        type: "singleClick",
      });
    });
  };

  Muze.Operators.registerSideEffects([Lasso]);

  Muze.Operators.registerPhysicalActions({
    dataSelection: selectAction,
  });

  let share = Muze.Operators.share;
  const sideEffectMap = SideEffects.config()
    .for(["lasso-selection"])
    .on(["brush"])
    .create();

  const behaviourConfig = Behaviours.config()
    .for(["brush"])
    .on(["dataSelection"])
    .dissociateFrom(["select", "click"])
    .create();

  const { chartDm } = formattedData;

  const checkShowLegend = () => {
    if (legendPosition !== "none" && (!shapeField && !sizeField && !colorField)) {
      return false
    }
    if (legendPosition !== "none") {
      return true
    }
    if (legendPosition === "none") {
      if ((!shapeField && !sizeField && !colorField)) {
        return false
      }
      if ((shapeField || sizeField)) {
        return true
      }
      return false
    }
  }

  const toolTipFormat = (dataStore, config, context) => {
    return formatterFn({
      dataStore,
      config,
      context,
      color,
      size,
      schema,
      tooltipField,
      report,
      format,
      chartDm,
      dispatch,
      rowsMeasures,
      columnMeasures,
      dimensions,
      radial,
      subVizType,
      chartData: data
    }).content;
  };
  const tooltip = Tooltip.config()
    .on("highlight")
    .formatter(toolTipFormat)
    .create();

  // const axis = Axes.LinearAxis.config().name('Number of tests done').create();
  const radiusAxis = Axes.RadialAxis.config()
    .range((r) => [r[0] + r[1] / 3, r[1] - r[1] / 4])
    .create();
  const arcRadiusAxis = Axes.RadialAxis.config()
    .range((r) => [r[0], r[1] - r[1] / 4])
    .create();

  const isValidChart =
    (isMeasureInRows && isDimensionInColumn) ||
    (isMeasureInColumn && isDimensionInRow);

  const checkIsValidChart = () =>
    // combine &&
    isValidChart &&
    subVizType !== "doughnut" &&
    subVizType !== "arc";

  const legend = Legend.config().create({
    textWidth: 10,
    textFormatter: (num) => gridChartNumberFormat(num, { legendColorField, report }),
    position: legendPosition ? legendPosition : "right",
    show: checkShowLegend(),
  });

  if (legendColorField) { // added padding for bigger legend label
    const { formatField } = getPropertyFieldInfo(report, legendColorField);
    if (formatField[0]?.values) {
      const { prefix = "", suffix = "" } = formatField[0]?.values || {}
      let legendPadding = 0;
      legendPadding += (prefix?.length * 7) + (suffix?.length * 7);
      legend.padding = legendPadding;
    }
  }

  if (combine && columnMeasures.length) {
    let measuresInColumns = schema
      .filter(
        (field) => field.type === "measure" && columns.includes(field.name)
      )
      .map((field) => field.name);
    let dimensionsWithoutMeasures = columns
      .map((i) => (!measuresInColumns.includes(i) ? i : null))
      .filter(Boolean);
    if (measuresInColumns.length && measuresInColumns.length > 1) {
      let { firstArray, secondArray } = getDividedArray(measuresInColumns);
      firstArray = [...firstArray, ...dimensionsWithoutMeasures];
      firstArray = firstArray?.length > 1 ? [share(...firstArray)] : firstArray;
      secondArray = secondArray?.length > 1 ? [share(...secondArray)] : secondArray;
      columns = [firstArray, secondArray];
    } else {
      columns = [...columns];
    }
  }
  if (combine && rowsMeasures.length) {
    let dimensionsInRows = schema
      .filter(
        (field) => field.type === "dimension" && rows.includes(field.name)
      )
      .map((field) => field.name);
    if (rowsMeasures.length > 1) {
      let { firstArray = [], secondArray = [] } = getDividedArray(rowsMeasures);
      const { fields } = axisRange || {}
      if (fields.length) {
        let axisHiddenField = fields?.map((item) => {
          const { data } = item || {}
          const { name, hide } = data || {}
          if (rowsMeasures.length === 2 && (firstArray?.includes(name) || secondArray.includes(name)) && hide) {
            return name
          }
        })?.[0]
        let arr = []
        if (firstArray.includes(axisHiddenField)) {
          arr = firstArray
          firstArray = secondArray
          secondArray = arr
        }
      }
      if (dimensionsInRows.length) {
        firstArray = [...firstArray, ...dimensionsInRows];
      }
      firstArray = firstArray?.length > 1 ? [share(...firstArray)] : firstArray;
      secondArray = secondArray?.length > 1 ? [share(...secondArray)] : secondArray;
      rows = [firstArray, secondArray];
    } else {
      rows = [...rows];
    }

  }
  sortObject = removeNullKeysFromObject(sortObject)
  let canvasProps = {
    columns,
    rows,
    tooltips: [tooltip],
    yAxis,
    xAxis,
    colorLegend: legend,
    shapeLegend: legend,
    sizeLegend: legend,
    sort: sortObject,
    detail: [],
  };
  let xAxes = [];
  let yAxes = [];
  let isContinuousColorField = colorField ? checkIfColorFieldContinuous(fields, colorField) : false
  if (canvasRef.current && canvasRef.current !== null) {
    let tempXAxes = canvasRef?.current?.state?.canvas?.xAxes()
    let tempYAxes = canvasRef?.current?.state?.canvas?.yAxes()
    if (Array.isArray(tempXAxes)) {
      xAxes = tempXAxes?.flat(Infinity)
    }
    if (Array.isArray(tempYAxes)) {
      yAxes = tempYAxes?.flat(Infinity)
    }
  }

  if ((!xAxes?.length && !yAxes?.length) && checkIsValidChart()) {
    let timeout = setTimeout(() => {
      setDataId(uuidv4())
      clearTimeout(timeout)
    }, 100)
  }
  if (color.length) canvasProps.color = color[0];
  if (size.length) canvasProps.size = size[0];
  if (shape.length) canvasProps.shape = shape[0];
  if (shape.length) canvasProps.shape = shape[0];
  if (tooltipField) canvasProps.detail.push(tooltipField);
  if (subVizType === "doughnut") canvasProps.radiusAxis = radiusAxis;
  if (subVizType === "arc") {
    canvasProps.radiusAxis = arcRadiusAxis;
  }
  if (subVizType === "arc" || subVizType === "doughnut") {
    delete canvasProps.sort;
  }

  if (!chartDm) return null;
  let scrollHeight = height;
  if (show && value) {
    titleStyle = getPropertyStyle(title);
    scrollHeight = scrollHeight - 60 - 2 * padding;
  }
  if (subTitleShow && subTitleValue) {
    subTitleStyle = getPropertyStyle(subTitle);
    scrollHeight = scrollHeight - 60 - 2 * subTitlePadding;
  }
  canvasProps.height = scrollHeight;
  canvasProps.width = width;
  canvasProps.gridLines = {
    x: {
      show: axisRange?.gridLines?.includes("x") ? true : false
    },
    y: {
      show: axisRange?.gridLines?.includes("y") ? true : false
    }
  }
  canvasProps.xAxis = (ri, ci, context) => getAxisConfig(context, report, axisRange, "x", data, subVizType, { rows, columns, actualFields: fields, actualData: data, synchronize: axisRange?.synchronize, combine, axes: xAxes, muzeChartId })
  canvasProps.yAxis = (ri, ci, context) => getAxisConfig(context, report, axisRange, "y", data, subVizType, { rows, columns, actualFields: fields, actualData: data, synchronize: axisRange?.synchronize, combine, axes: yAxes, muzeChartId })

  if (subVizType === "area" && isMeasureInColumn && !canvasRef?.current) {
    Notify.error({
      type: "Frontend",
      message: "There is an issue with this generated chart. Please add Measure in rows to see the correct Area Chart",
    });
  }

  if ((subVizType === "arc" || subVizType === "doughnut") && (columnMeasures.length === 1 && rows.length == 0 && columns.length === 1 || rowsMeasures.length === 1 && columns.length === 0 && rows.length === 1)) {
    if (["fieldValue", "gradient"].includes(formatColor?.formatColorStyle) && canvasProps?.color) {
      canvasProps.color = {
        field: canvasProps.color.field,
        range: canvasProps?.color?.range,
      }
    }
  }
  let className = "hr-muze-container"
  if (subVizType === "text") {
    className = className + " grid-text-chart"
  }

  let referenceLineContent = null
  const getReferenceLineContent = ({ referenceLineAtY, chartAreaHeight, chartAreaWidth, height = 46, width = 76, referenceLineTop, positionFactor = 0 }) => {
    return (
      <CustomReferenceLine
        height={referenceLineAtY ? chartAreaHeight : props.chartAreaHeight - height}
        width={referenceLineAtY ? chartAreaWidth - width : chartAreaWidth}
        top={referenceLineTop}
        referenceLineAtY={referenceLineAtY}
        {...{ positionFactor }}
      />
    )
  }

  const getDimensionScaleheight = (allDimensions, allAxes) => {
    return allDimensions.map((d, i) => {
      if (i > 0) {
        let axis = allAxes?.find((item) => {
          let { name } = item?.config()
          return d === name
        })
        if (!axis) return 0;
        let { height } = axis?.availableSpace() || {};
        return height
      }
      return 0
    })?.reduce((a, b) => a + b);
  }

  const getHeightAndWidthOfAllAxes = (axes) => {
    let greaterHeight = 0
    let greaterWidth = 0
    let allMeasures = [...rowsMeasures, ...columnMeasures]
    allMeasures?.forEach((measure) => {
      let axis = axes?.find((item) => {
        let { name } = item?.config()
        return measure === name
      })
      const { height, width } = axis?.availableSpace() || {}
      if (height > greaterHeight) {
        greaterHeight = height
      }
      if (width > greaterWidth) {
        greaterWidth = width
      }
    })
    return [greaterWidth, greaterHeight]
  }


  if (referenceLineList && referenceLineList?.length && canvasRef?.current) {
    let enabledReferenceLines = referenceLineList.filter((item) => item?.enabled);
    if (enabledReferenceLines?.length) {
      let yAxis = canvasRef?.current?.state?.canvas?.yAxes()
      let xAxis = canvasRef?.current?.state?.canvas?.xAxes()
      let allAxes = []
      if (Array.isArray(yAxis)) {
        yAxis = yAxis?.flat(Infinity)
        allAxes = [...allAxes, ...yAxis]
      }
      if (Array.isArray(xAxis)) {
        xAxis = xAxis?.flat(Infinity)
        allAxes = [...allAxes, ...xAxis]
      }
      if (allAxes.length) {
        let allDimensions = schema.filter((item) => item.type === "dimension")?.map((item) => item?.name)
        let isAllEnabled = referenceLineList.find((item) => item?.display === referenceLineAll && item.enabled)
        referenceLineContent = (
          <React.Fragment>
            {allAxes?.map((axis, _i, arr) => {
              let { name } = axis?.config()
              let displayName = ''
              let referenceField = enabledReferenceLines?.find((item) => {
                const { id } = item || {}
                let field = fields?.find((f) => {
                  return f?.id === id;
                })
                if (name?.includes(",")) {
                  return name?.split(",")?.includes(getFieldDisplayName(field));
                }
                if (getFieldDisplayName(field) === name) {
                  displayName = getFieldDisplayName(field)
                  return true
                }
                return false
              })
              if (!referenceField && !isAllEnabled) return null;
              const { value } = referenceField || {}
              let referenceLineAtY = rowsMeasures?.includes(displayName)
              let referenceLineTop = 0;
              if (value && !isAllEnabled) {
                let [min, max] = axis?.domain() || []
                if (+value > max) return null;
                referenceLineTop = axis?.getScaleValue(value) || {}
                let { width, height } = axis?.availableSpace() || {}
                let positionFactor = 0;
                if (combine) {
                  const [gWidth, gHeight] = getHeightAndWidthOfAllAxes(allAxes)
                  positionFactor = referenceLineAtY ? gWidth : gHeight
                  width = gWidth * 2
                  height = gHeight * 2
                }
                if (props?.isPrintMode && referenceLineAtY) {
                  positionFactor += 45
                }
                let { width: otherAxisWidth } = yAxis?.[0]?.availableSpace() || {}
                if (!referenceLineAtY) {
                  referenceLineTop += otherAxisWidth;
                }
                if (arr.length > 1 && _i > 0 && !combine) {
                  if (!referenceLineAtY) {
                    referenceLineTop += width * (_i - 1);
                  } else {
                    referenceLineTop += height * _i;
                  }
                }
                if (allDimensions?.length > 1 && referenceLineAtY) {
                  let dimensionsScalesHeight = getDimensionScaleheight(allDimensions, allAxes)
                  referenceLineTop += (dimensionsScalesHeight / 2) * (allDimensions?.length - 1);
                }
                return getReferenceLineContent({ referenceLineAtY, width, height, chartAreaHeight: props.chartAreaHeight, chartAreaWidth: props.chartAreaWidth, referenceLineTop, combine, positionFactor })
              }
              if (isAllEnabled) {
                const referenceLineAtY = rowsMeasures?.length >= 1 && !columnMeasures?.length ? true : false
                const { value } = isAllEnabled || {}
                referenceLineTop = axis?.getScaleValue(+value) || null
                if (typeof referenceLineTop !== 'number') return null
                if (!referenceLineAtY) {
                  let { width: otherAxisWidth } = yAxis?.[0]?.availableSpace() || {}
                  referenceLineTop += otherAxisWidth;
                }
                if (allDimensions?.length > 1 && referenceLineAtY) {
                  let dimensionsScalesHeight = getDimensionScaleheight(allDimensions, allAxes)
                  referenceLineTop += (dimensionsScalesHeight / 2) * (allDimensions?.length - 1);
                }
                let { width, height } = axis?.availableSpace() || {}
                let positionFactor = 0;
                if (combine) {
                  positionFactor = referenceLineAtY ? width : height
                  width *= 2
                  height *= 2
                }
                if (arr.length > 1 && _i > 0 && !combine) {
                  if (!referenceLineAtY) {
                    referenceLineTop += width * (_i - 1);
                  } else {
                    referenceLineTop += height * _i;
                  }
                }
                return getReferenceLineContent({ referenceLineAtY, width, height, chartAreaHeight: props.chartAreaHeight, chartAreaWidth: props.chartAreaWidth, referenceLineTop, combine, positionFactor })
              }
              return null;
            })}
          </React.Fragment>)
      }
    }
  }

  const renderTextChart = () => {
    let textProps = { canvasRef, canvasProps, labelField, colorField, tooltip, report, formatColor, data, themeColors }
    return <GridTextChart data={textProps} />
  }

  return (
    <div ref={container} className={className} id={muzeChartId}>
      {!["arc", "doughnut"].includes(subVizType) && referenceLineContent}
      <div className="title-subtitle-container">
        {title?.position === "top" && getPropertyElement(titleStyle, title)}
        {subTitle?.position === "top" &&
          getPropertyElement(subTitleStyle, subTitle)}
      </div>
      <Row justify="end">
        <Toolbar
          addForwardFilterIPC={props.addForwardFilterIPC}
          deleteBackwardFilterIPC={props.deleteBackwardFilterIPC}
          refreshFiltersIPC={props.refreshFiltersIPC}
          reportId={reportId}
          getApi={props.getApi}
        />
      </Row>
      <Muze
        data={chartDm}
        behaviours={behaviourConfig}
        sideEffects={sideEffectMap}
        height={scrollHeight}
        width={width}
        // colorScheme working in this range ["#a9d3f2", "#f4a4c7"]
        colorScheme={getGridChartColorSchemeFromPalette(themeColors, data, isContinuousColorField)}
        crossInteractive
      >
        {subVizType === "text" ? renderTextChart() : <Canvas  {...canvasProps} ref={canvasRef} className="hr-grid-chart">
          {layers.map((layer) => {
            let { name, mark, encoding } = layer;
            let layerProps = { name, mark, transformType: getTransformType(barType) };
            if (encoding) {
              layerProps.encoding = encoding;
            }
            if (mark === "line") {
              if (line?.smooth) {
                layerProps.interpolate = "catmullRom"
              } else {
                layerProps.interpolate = "linear";
              }
            }
            return <Layer key={name} {...layerProps} />;
          })}
        </Canvas>}
      </Muze>
      {interactiveMode && (
        <CellCard
          reportId={reportId}
          addFilter={props.addFilter}
          report={report}
          format={format}
          getApi={props.getApi}
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
const GridChartMemo = React.memo(GridChart, areEqual);

const GridChartContainer = (props) => {
  let { marksList, reportId } = props;
  let subVizTypes = marksList.map((markLayer) => markLayer.subVizType);
  let invalid = false;
  let message = "";
  if (
    subVizTypes.length > 2 &&
    (subVizTypes.includes("arc") ||
      subVizTypes.includes("doughnut") ||
      subVizTypes.includes("heatmap"))
  ) {
    invalid = true;
    message = `Invalid data. Please use proper configuration.
        Arc, Doughnut and Heatmap cannot be combined with any other chart.`;
  }
  if (invalid) {
    return (
      <div
        className="muze-message-container"
        data-testid={`muze-chart-${reportId}`}
      >
        <div className="viz-invalid-state"> {message} </div>
      </div>
    );
  }
  return <GridChartMemo {...props} />;
};

export default React.memo(GridChartContainer, areEqual);
