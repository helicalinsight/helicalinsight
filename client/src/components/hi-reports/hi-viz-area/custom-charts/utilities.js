import Muze, { Layer } from "@chartshq/react-muze/components";
import {
    Legend,
    SideEffects,
    Tooltip
} from "@chartshq/react-muze/configurations";
import { Tooltip as AntdTooltip } from "antd";
import { isEmpty, isEqual } from "lodash";
import { changeTableRecordsPerPage, setMenuData } from "../../../../redux/actions/hreport.actions";
import { getFieldDisplayName } from "../../../../utils/utilities";
import { applyColor, generateColorRange, getFieldData, getFieldName, getGridChartColorFormatScheme, getHTMLColorFormat, getPropertyAxisRange } from "../../hi-editing-area/utils/property-utils";
import { divStyles, getAntChartLegendLabelConfig, setStyles } from "../ant-charts/ant-utils";
import { checkParentElementBoundingRect } from "../pivot-view/utils/chart";
import { isAggregatedTitle } from "../pivot-view/utils/pivot-table-utils";
import { dateFormat, formatterFn, getFieldInfo, getGridChartColorSchemeFromPalette } from "../utils/grid-chart-utils";
import { createColorsList, createMaxValue, createsizesList, getAxisConfig, getColorsByThemeColors, getMarks, getPropertyFieldInfo, getPropertyText, gridChartNumberFormat, hrNumberFormat } from "../utils/utillities";
import VizTooltip from "../viz-tooltip";
import generateColor from "../utils/gradient";
import { checkIsNumeric } from "../table/table-utils";
const { Operators, Utils } = Muze;
const utils = Utils;
const { SpawnableSideEffect } = SideEffects;


export const getTooltip = (type, reportData, dataSourceSettings, tableColumnName) => {
    const { properties = {}, data = [], metadata, marksList = [], fields, dispatch, reportId, drillDown, drillThrough, interactiveMode, report, themeColors = [] } = reportData || {}
    const { format, axisRange, tooltip: { showTooltip = true, tooltipTemplate = "", enableTemplate = false } = {} } = properties || {};
    let canvasFields = fields.filter((field) => !field.hiddenIncludeInResultSet);
    let {
        schema,
        tooltipField,
    } = getFieldInfo({ data, metadata, fields: canvasFields, marksList, axisRange });

    async function createDataModel() {
        const DataModelClass = await Muze.DataModel.onReady();
        const formattedData = await DataModelClass.loadData(data, schema);
        return new DataModelClass(formattedData);
    }
    let chartDm = null
    createDataModel().then((dm) => {
        chartDm = dm
    });
    let color = [],
        size = []
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
            chartDm: chartDm,
            dispatch
        }).content;
    };
    const muzeChartTooltip = Tooltip.config()
        .on("highlight")
        .formatter(toolTipFormat)
        .create();

    const getTableTooltip = ({ text, record, report, onClick }) => {
        let columnList = metadata[0];
        let { colorMarks, sizeMarks } = getMarks(marksList, fields);
        let color = "#000000",
            fontSize = '13px'
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
                    let max = createMaxValue(colorMarks[0], data);
                    let colorValue = (100 * record[colorMarks[0]]) / max;
                    color = "#" + generateColor("#0000ff", "#fffff0", colorValue);
                } else {
                    let colorsList = createColorsList(colorMarks[0], data);
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
                let max = createMaxValue(colorMarks[0], data);
                fontSize = (100 * record[sizeMarks[0]]) / max;
                fontSize = (10 * fontSize) / 100 + 5;
                fontSize = Math.floor(fontSize);
            } else {
                let sizesList = createsizesList(sizeMarks[0], data);
                let { sizes } = sizesList;
                fontSize = sizes[record[sizeMarks[0]]];
            }
        }

        const { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(report, tableColumnName)
        text = getPropertyText({ text, applyOn: "pane", isApplyClicked, fieldType, formatField });
        return (
            <AntdTooltip title={!showTooltip ? null : () => <VizTooltip data={record} report={report} text={text} dispatch={dispatch} />} >
                <div
                    style={{
                        color,
                        fontSize,
                        textAlign: checkIsNumeric(columnList, tableColumnName) ? "right" : "left",
                        cursor: 'pointer'
                    }}
                    className={"hr-report-cell"}
                    onClick={typeof onClick === 'function' ? (e) => onClick(e, { record }) : null}
                >
                    {text}
                </div>
            </AntdTooltip>
        );
    }

    const onTableCellClick = (e, { record }) => {
        if (!interactiveMode) {
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
        let { top, right, bottom, left } = element.getBoundingClientRect();
        let menuData = {
            payload,
            position: { top, right, bottom, left },
            drillDownFilterValues: data,
        };
        dispatch(setMenuData({ reportId, menu: menuData }));
    }

    switch (type) {
        case "antd":
            return !showTooltip ?
                false : {
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
                            return container;
                        }
                    }
                }
        case 'muze':
            return muzeChartTooltip
        case 'table': {
            return {
                render: (text, record) => getTableTooltip({ text, record, report, onClick: onTableCellClick })
            }
        }
        default:
            return null
    }
}

export const checkAxesProperties = (axisProperties, axis, axisField) => {
    let config = {
        grid: null,
        rotate: 0,
        visible: true,
        rangeData: {}
    }
    if (axisProperties?.gridLines?.length) {
        if (axisProperties.gridLines.includes(axis)) {
            config.grid = {
                line: {
                    style: {
                        stroke: '#dddddd',
                        lineWidth: 1,
                    },
                },
            }
        }
    }
    if (axisProperties?.fields && axisProperties.fields.length) {
        axisProperties.fields.forEach((field) => {
            const { hide, rotate } = field.data || {};
            if (axisField === field.data.name) {
                if (rotate || rotate === 0) {
                    config.rotate = rotate
                    if (rotate > 0) {
                        config.verticalFactor = -5
                    }
                }
                config.visible = !hide
                const rangeData = getPropertyAxisRange(field, "antChart")
                if (rangeData) {
                    config.rangeData = { ...rangeData }
                }
            }
        })
    }
    return config;
}

export const getAntdColorProperties = (formatColor, report, { rows = [], columns = [], data = [], xField = '', yField = '' }) => {
    let colorConfig = {}
    if (formatColor.formatColorStyle === "gradient") {
        const { minimum, maximum, formatColorField } = formatColor
        if (minimum && maximum) {
            const prColorField = getFieldName(formatColorField, report);
            const fieldData = getFieldData(data, prColorField) || []
            const uniqueData = [...new Set(fieldData)];
            const colors = generateColorRange(minimum, maximum, uniqueData.length);
            if (prColorField) {
                if (![...rows, ...columns].includes(prColorField)) {
                    colorConfig.isStack = true;
                }
                colorConfig.seriesField = prColorField;
                colorConfig.color = colors
            }
        }
    }
    if (formatColor?.formatColorStyle === "fieldValue") {
        const propertyColorField = getFieldName(formatColor?.formatColorField, report);
        if (propertyColorField) {
            colorConfig.colorField = propertyColorField;
            if (![...rows, ...columns].includes(propertyColorField)) {
                colorConfig.isStack = true;
            }
            colorConfig.color = (obj) => applyColor(obj, { options: { xField, yField } }, formatColor, propertyColorField);
        }
    }
    return colorConfig;
}

export const getAntdPropertiesConfig = (props = {}) => {
    const { data = [], metadata, marksList = [], fields, report = {}, themeColors = [] } = props || {}
    let canvasFields = fields.filter((field) => !field.hiddenIncludeInResultSet);
    const { legend: { legendPosition }, formatColor, bar, axisRange, card, labels } = report?.reportData?.properties || {}
    let labelsColor = isEmpty(labels?.labelsColor) ? null : getHTMLColorFormat(labels?.labelsColor);
    let { chartData: filteredData, rows, columns, schema, colorField, sizeField, detailField, labelField, shapeField } =
        getFieldInfo({ data, metadata, fields: canvasFields, marksList, axisRange });
    let xField = columns?.[0] ?? '';
    let yField = rows?.[0] ?? '';
    let xAxisProperties = checkAxesProperties(axisRange, 'x', xField)
    let yAxisProperties = checkAxesProperties(axisRange, 'y', yField)
    let returnValue = {
        legend: legendPosition === "none" ? false : { position: legendPosition ? legendPosition : "right", ...getAntChartLegendLabelConfig(report, colorField) },
        xAxis: {
            grid: xAxisProperties?.grid,
            visible: xAxisProperties?.visible,
            label: {
                rotate: xAxisProperties?.rotate,
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
                }
            },
            verticalFactor: xAxisProperties?.verticalFactor,
            ...xAxisProperties?.rangeData
        },
        yAxis: {
            grid: yAxisProperties?.grid,
            visible: yAxisProperties?.visible,
            label: {
                rotate: yAxisProperties?.rotate,
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
                }
            },
            ...yAxisProperties?.rangeData
        },
        ...getAntdColorProperties(formatColor, report, { rows, columns, data: filteredData, xField, yField }),
    }
    if (colorField) {
        if (![...rows, ...columns].includes(colorField)) {
            returnValue.isStack = true;
        }
        returnValue.seriesField = colorField;
        returnValue.interactions = [{ type: "element-active" }, { type: "element-highlight" }];
    }
    if (labelField) {
        returnValue.label = {
            position: "middle",
            content: (obj) => {
                const { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(
                    report,
                    labelField
                );
                let formattedText = getPropertyText({
                    text: obj[labelField],
                    applyOn: "label",
                    isApplyClicked,
                    fieldType,
                    formatField,
                });
                if (bar.barType === "percentage") {
                    return formattedText.toFixed(2) * 100 + "%";
                }
                return formattedText;
            },
            rotate: labels?.rotateLabels ? 55 : 0

        };
        if (labelsColor) {
            returnValue.label['style'] = {
                fill: labelsColor
            }
        }
    }
    if (sizeField) {
        returnValue.sizeField = sizeField;
        returnValue.size = [4, 20];
        returnValue.sizeLegend = true
    }
    if (themeColors.length) {
        returnValue.theme = 'custom-theme'
    }
    return returnValue;
}

export const getMuzeChartPropertiesConfig = (props = {}, subVizType = 'bar', { canvasRef = null } = {}) => {
    const { data = [], metadata, marksList = [], fields, report = {}, combine, themeColors = [] } = props || {}
    const { formatColor, axisRange, legend } = report?.properties || {};
    const { legendPosition = "right" } = legend || {}
    let {
        rows,
        columns,
        colorField,
        colorStep,
        sizeField,
        shapeField, } = getFieldInfo({ data, metadata, fields, marksList, axisRange });
    let canvasProps = {}
    let showLegend = false;
    let legendColorField = colorField;
    if (formatColor?.formatColorStyle === "fieldValue") {
        const fieldName = getFieldName(formatColor?.formatColorField, report);
        const domainData = getFieldData(data, fieldName);
        const colors = getGridChartColorFormatScheme(formatColor, domainData, combine);
        if (fieldName) {
            canvasProps.color = {
                field: fieldName,
                domain: domainData,
                range: colors,
            }
            showLegend = true
            legendColorField = fieldName;
        }
    }
    if (formatColor?.formatColorStyle === "gradient") {
        if (formatColor?.minimum && formatColor?.maximum) {
            const fieldName = getFieldName(formatColor?.formatColorField, report);
            const colors = generateColorRange(
                formatColor?.minimum,
                formatColor?.maximum,
                data.length
            );
            if (fieldName) {
                canvasProps.color = {
                    field: fieldName,
                    domain: getFieldData(data, fieldName),
                    range: colors,
                }
                showLegend = true
                legendColorField = fieldName;
            }
        }
    }
    canvasProps.gridLines = {
        x: {
            show: axisRange?.gridLines?.includes("x") ? true : false
        },
        y: {
            show: axisRange?.gridLines?.includes("y") ? true : false
        }
    }
    let xAxes = [];
    let yAxes = [];
    if (canvasRef?.current) {
        xAxes = canvasRef?.current?.state?.canvas?.xAxes()?.flat(Infinity)
        yAxes = canvasRef?.current?.state?.canvas?.yAxes()?.flat(Infinity)
    }
    canvasProps.xAxis = (ri, ci, context) => getAxisConfig(context, report, axisRange, "x", data, subVizType, { rows, columns, actualFields: fields, actualData: data, synchronize: axisRange?.synchronize, combine, axes: xAxes })
    canvasProps.yAxis = (ri, ci, context) => getAxisConfig(context, report, axisRange, "y", data, subVizType, { rows, columns, actualFields: fields, actualData: data, synchronize: axisRange?.synchronize, combine, axes: yAxes })

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
            if ((shapeField || sizeField || colorField)) {
                return true
            }
            return false
        }
    }

    const muzeLegend = Legend.config().create({
        textWidth: 10,
        textFormatter: (num) => gridChartNumberFormat(num, { legendColorField, report }),
        position: legendPosition ? legendPosition : "right",
        show: checkShowLegend(),
    });


    if (colorField) {
        canvasProps.color = {
            field: colorField,
            step: colorStep,
        }
        if (themeColors.length) {
            canvasProps.colorScheme = getGridChartColorSchemeFromPalette(themeColors, data)
        }
    }
    canvasProps.colorLegend = muzeLegend;
    canvasProps.shapeLegend = muzeLegend;
    canvasProps.sizeLegend = muzeLegend;

    return canvasProps;
}

export const getPropertiesConfig = (type = '', report = {}, subVizType, otherProps) => {

    switch (type) {
        case "antd":
            return getAntdPropertiesConfig(report)
        case 'muze':
            return getMuzeChartPropertiesConfig(report, subVizType, otherProps)
        case 'table':
            return null
        default:
            return null
    }
}

export const enableInteractivity = (type, report, otherProps) => {
    const { dispatch, reportId, interactiveMode, drillDown, drillThrough, mode } = report || {}
    const { canvasRef, dataModel: chartDm } = otherProps || {}
    const onAntdCompClick = (args, plot) => {
        const { x, y } = args;
        const tooltipData = plot.chart.getTooltipItems({ x, y });
        if (tooltipData[0]?.data) {
            const data = tooltipData[0].data;
            if (!interactiveMode) {
                return null;
            }
            if (!drillDown && !drillThrough) {
                return null;
            }
            let payload = [];
            Object.keys(data).map((key) => {
                if (key !== "name") payload.push({ field: key, value: data[key] });
            });
            let { top, left } = args.view.ele.getBoundingClientRect();
            top = args.data.y;
            left = args.data.x;
            let menuData = {
                payload,
                position: { top: y, left: x },
                drillDownFilterValues: data,
            };
            dispatch(setMenuData({ reportId, menu: menuData }));
        }
    }

    const registerSideEffectForMuze = () => {
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
            apply(_, payload) {
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
                        const layer = this.firebolt.context.getLayerByName(layerName)
                        if (!layer) return null;
                        let mount = this.firebolt.context.mount();
                        let { top: mount_top, left: mount_left } =
                            mount.getBoundingClientRect();
                        let tempX = mount_left + payload.position?.x;
                        let tempY = mount_top + payload.position?.y;
                        let x = mode === "dashboard" ? payload.cardPos?.x : tempX;
                        let y = mode === "dashboard" ? payload.cardPos?.y : tempY;
                        const { data, schema } = chartDm?.getData() || {};
                        let mergedData = [];
                        schema?.map((item, i) => {
                            if (item.name !== "__id__") {
                                mergedData.push({
                                    field: item.name,
                                    value: data?.map((value) => {
                                        return value[i]
                                    }),
                                });
                            }
                        });
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
        Muze.Operators.registerSideEffects([Lasso])

        Muze.Operators.registerPhysicalActions({
            dataSelection: selectAction,
        });
    }

    switch (type) {
        case "antd":
            return {
                onReady: (plot) => {
                    plot.on("click", (args) => {
                        onAntdCompClick(args, plot);
                    });
                }
            }
        case 'muze':
            return registerSideEffectForMuze()
        default:
            return null
    }
}

export const reportInfo = {
    id: "id of current report",
    mode: "current mode i.e edit|open",
    metadata: "metadata of current report",
    functions: "info of function in current report",
    databaseFunctions: "info of database functions",
    dateFunctions: "info of date functions",
    fields: "fields present in fields pane",
    filters: "filters info of current active report",
    marksList: "list of the marks in marks section",
    activeTool: "active tool , i.e which tool is selected Visualization|Filters|Properties etc. ",
    scripts: "Info of scripts i.e post fetch , pre fetch, post execution, pre execution.",
    styles: "info of css written in css editor",
    sqlString: "info if sql written in sql editor",
    options: "info of table options",
    interactiveMode: "info of interactive mode",
    drillDownList: "info about drilldown report",
    currentDrillDown: "current drill down report",
    drillThroughList: "info about drillthrough report",
    selectedType: "info of selected visualization type",
    reportData: "info of report's other data , i.e columns, rows etc.",
    properties: "info of properties pane",
    reportInfo: "curent report's information",
    database: "info of database",
}

export const getGridChartLabels = (props) => {
    const { data = [], metadata, marksList = [], fields, report = {} } = props || {}
    const { labels } = report?.properties || {};
    const { axisRange } = report?.properties || {};
    let { labelField } = getFieldInfo({ data, metadata, fields, marksList, axisRange });
    if (labelField) {
        let layerProps = {
            mark: "text",
            encoding: {
                text: {
                    field: labelField,
                    formatter: (text) => {
                        const { isApplyClicked, fieldType, formatField } =
                            getPropertyFieldInfo(report, labelField);
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
                    value: () => "#000"
                }
            }
        };
        return <Layer key={"text_layer"} {...layerProps} />
    }
    return null;
}

export const getTableColumns = (report) => {
    const { metadata, fields } = report || {}
    const columnList = metadata[0]
    const columns = Object.keys(columnList)
        .map((clmn) => {
            let { name, type } = columnList[clmn];
            let isCanvasField = false;
            fields
                .filter((field) => ["row", "column"].includes(field.addedAs))
                .map((field) => {
                    if (getFieldDisplayName(field) === name) {
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
                ...getTooltip('table', report, null, name)
            };
        })
        .filter((clmn) => !!clmn);
    return columns
}

const getGridChartSortConfig = (report) => {
    const sortConfig = []
    const sortObject = {};
    report?.fields?.map((field) => {
        let { orderBy } = field;
        let alias = field.alias ? field.alias : field.autogen_alias;
        if (orderBy && orderBy.length > 0) {
            sortConfig.push([alias, orderBy[0]]);
            sortObject[alias] = orderBy[0];
        } else {
            sortObject[alias] = null;
        }
    });
    return { sortConfig, sortObject };
}

async function createDataModel(data, schema) {
    const DataModelClass = await Muze.DataModel.onReady();
    const formattedData = await DataModelClass.loadData(data, schema);
    return new DataModelClass(formattedData);
}

export const getGridChartConfig = (report, data, cSchema = []) => {
    const { sortConfig } = getGridChartSortConfig(report);
    return new Promise((resolve, reject) => {
        const { fields = [], marksList = [], metadata } = report || {}
        let canvasFields = fields?.filter((field) => !field.hiddenIncludeInResultSet);
        let { schema: chartSchema, chartData } = getFieldInfo({ data, metadata, fields: canvasFields, marksList });

        chartSchema = [...chartSchema, ...cSchema];
        createDataModel(chartData, chartSchema).then((dm) => {
            dm = dm.sort(sortConfig);
            resolve(dm);
        }).catch((error) => {
            reject(error);
        });
    });
}

export const changePageSize = (report, page) => {
    const { dispatch } = report || {}
    dispatch(changeTableRecordsPerPage({ page, refresh: true }))
}