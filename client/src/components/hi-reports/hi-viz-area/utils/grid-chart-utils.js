
import moment from "moment";
import Muze from "@chartshq/react-muze/components";
import { getFloatingType } from "../../../../utils/filter-utils";
import { getFieldDisplayName } from '../../../../utils/utilities';
import { calculateAxesValues, calculateRadialPercentage, checkIsContinousField, checkIsDateType, detectOverlapping, getFormattedLabel, getPropertyFieldInfo, getPropertyText, hrNumberFormat, removeOverlapping, tooltipTemplateLiquidJS } from './utillities';
import { isEmpty } from "lodash";
import notify from "../../../hi-notifications/notify";

const { Operators } = Muze;
const { html } = Operators;

export const removeAllChildNodes = (parent) => {
    while (parent.firstChild) {
        parent.removeChild(parent.firstChild);
    }
}

export const pointInPolygon = (point, polygon) => {
    let x = point[0], y = point[1];

    let inside = false;
    for (let i = 0, j = polygon.length - 1; i < polygon.length; j = i++) {
        let xi = polygon[i][0];
        let yi = polygon[i][1];
        let xj = polygon[j][0];
        let yj = polygon[j][1];

        let intersect = ((yi > y) != (yj > y)) && (x < (xj - xi) * (y - yi) / (yj - yi) + xi);
        if (intersect) {
            inside = !inside;
        }
    }

    return inside;
};
export const dateFormat = (val, part) => {
    switch (part) {
        case "sql.dateTime.year":
            return moment(val).format("YYYY")
        case "sql.dateTime.month":
            return moment(val).format("MMMM")
        case "sql.dateTime.day":
            return moment(val).format("DD")
        case "sql.dateTime.hour":
            return moment(val).format("HH")
        case "sql.dateTime.minute":
            return moment(val).format("mm")
        case "sql.dateTime.second":
            return moment(val).format("ss")
        case "dateTime":
            return moment(val).format("YYYY-MM-DD HH:mm:ss.S")
    }
    return moment(val).format("YYYY-MM-DD")
}
export const numberFormat = ({ num, actualNum, markLayers }) => {
    num = actualNum ? actualNum : num
    return hrNumberFormat(num)
}
export const getLegendItem = () => {
    return {
        text: {
            formatter: function (val) {
                return numberFormat({ num: val })
            }
        }
    }
}

export const checkMonthFnIndex = (fields, alias) => {
    const dimensionFields = fields?.filter((item) => item?.floatingType === "discrete")
    if (dimensionFields?.length) {
        let index = dimensionFields.findIndex((item) => {
            let itemAlias = getFieldDisplayName(item)
            return alias === itemAlias
        })
        return (index !== -1 && index >= 0 && index !== dimensionFields?.length - 1)
    }
    return false
}

export const getFieldInfo = reportData => {
    let { data, fields, marksList, metadata, axisRange } = reportData
    let { color, size, label, shape, detail, tooltip } = marksList[0]
    fields = fields || []
    fields = fields.filter((field) => !field["hidden"])
    let schema = []
    fields.map((field, i) => {
        let { fieldType, fieldDataType, databaseFunction } = field
        let alias = getFieldDisplayName(field)
        let { floatingType } = getFloatingType(field);
        fieldType = floatingType === "discrete" ? "dimension" : "measure"
        if (!fieldDataType) {
            let metadataInfo = metadata[0][i + 1] || {}
            fieldDataType = { dataType: metadataInfo.type }
        }
        if (fieldDataType.dataType === "dateTime" || fieldDataType.dataType === "date") {
            let funcName = fieldDataType.dataType
            let subtype = "temporal"
            let format = "%Y-%m-%d %H:%M:%S"
            if (fieldDataType.dataType === "date") {
                format = "%Y-%m-%d"
            }
            if (fieldDataType.dataType === "time") {
                format = "%H:%M:%S"
            }
            let schemaObj = {}
            schemaObj.format = format
            if (databaseFunction) {
                let tempList = ["sql.dateTime.month", "sql.dateTime.monthname", "sql.dateTime.day", "sql.dateTime.hour", "sql.dateTime.minute",
                    "sql.dateTime.second"
                ]
                funcName = databaseFunction.value
                if (tempList.indexOf(funcName) > -1) {
                    subtype = "categorical"
                }
                if (funcName === "sql.dateTime.year") {
                    data = data.map(r => {
                        r[alias] = "" + r[alias]
                        return r
                    })
                    schemaObj.format = '%Y'
                }
            }
            schemaObj.name = alias
            schemaObj.type = fieldType
            schemaObj.subtype = subtype
            schemaObj.funcName = funcName
            schema.push(schemaObj)
        }
        let funcName = null
        // if (fieldDataType.dataType === "numeric" && databaseFunction && ["sql.dateTime.month"].includes(databaseFunction.key)) {
        //     if (checkMonthFnIndex(fields, alias)) {
        //         let newdata = data.map(r => {
        //             let temp = { ...r }
        //             temp[alias] = `${temp[alias] < 10 ? temp[alias] : 'Z_' + temp[alias]}`
        //             return temp
        //         })
        //         data = newdata
        //     }
        // }
        if (databaseFunction && databaseFunction.key === "sql.dateTime.year") {
            funcName = databaseFunction.value
        }
        if (schema.find(item => item.name === alias && item.type === fieldType)) {
            return null
        }
        if (floatingType === "continous") {
            let aggregateType = ""
            if (field.aggregate && field.aggregate.length) {
                const type = field.aggregate[0].split(".")[3]
                if (["max", "min", "avg"].includes(type)) {
                    aggregateType = type;
                } else {
                    aggregateType = "sum"
                }
            }
            schema.push({ name: alias, type: fieldType, funcName, defAggFn: aggregateType })
        } else {
            schema.push({ name: alias, type: fieldType, funcName })
        }
    })
    let columns = fields.filter(field => field.addedAs === "column").map(field => {
        return getFieldDisplayName(field)
    })
    let rows = fields.filter(field => field.addedAs === "row").map(field => {
        return getFieldDisplayName(field)
    });
    let colorField, colorStep;
    if (color.fields.length) {
        let { step, id } = color.fields[0]
        let field = fields.find(tempField => tempField.id === id)
        colorField = getFieldDisplayName(field)
        colorStep = step ? true : false
    }
    let sizeField;
    if (size.fields.length) {
        let { id } = size.fields[0]
        let field = fields.find(tempField => tempField.id === id)
        sizeField = getFieldDisplayName(field)
    }
    let detailField;
    if (detail.fields.length) {
        let { id } = detail.fields[0]
        let field = fields.find(tempField => tempField.id === id)
        detailField = getFieldDisplayName(field)
    }
    let labelField;
    if (label.fields.length) {
        let { id } = label.fields[0]
        let field = fields.find(tempField => tempField.id === id)
        labelField = getFieldDisplayName(field)
    }
    let shapeField;
    if (shape.fields.length) {
        let { id } = shape.fields[0]
        let field = fields.find(tempField => tempField.id === id)
        shapeField = getFieldDisplayName(field)
    }
    let tooltipField;
    if (tooltip.fields.length) {
        let { id } = tooltip.fields[0]
        let field = fields.find(tempField => tempField.id === id)
        tooltipField = getFieldDisplayName(field)
    }
    let measuresLabelFields = {};
    let marksOtherThanDefault = marksList.filter((item) => item.value !== "_all_");
    if (marksOtherThanDefault.length) {
        marksOtherThanDefault.forEach(({ label, value, id: markID }) => {
            if (label.fields.length) {
                let markName = value
                let markIdField = fields.find(field => field.id === markID)
                if (markIdField) {
                    markName = getFieldDisplayName(markIdField)
                }
                let { id } = label.fields[0]
                let field = fields.find(field => field.id === id)
                if (field) {
                    measuresLabelFields[markName] = getFieldDisplayName(field)
                }
            }
        });
    }

    let filteredData = data;
    return {
        rows, columns, schema, chartData: filteredData, colorField, colorStep, sizeField, labelField, shapeField,
        detailField, tooltipField, measuresLabelFields
    }
    // return { 
    //     rows, columns, schema, colorField, colorStep, sizeField, labelField, shapeField,
    //     data:[
    //         {booking_platform: "Agent", mode_of_payment: "A_SubTotal", sum_travel_cost: 1000000},
    //         {booking_platform: "Makemytrip", mode_of_payment: "A_SubTotal", sum_travel_cost: 1000000},
    //         {booking_platform: "Website", mode_of_payment: "A_SubTotal", sum_travel_cost: 1000000},
    //         {booking_platform: "Z_SubTotal", mode_of_payment: "Cash", sum_travel_cost: 1000000},
    //         {booking_platform: "Z_SubTotal", mode_of_payment: "Credit", sum_travel_cost: 1000000},
    //         {booking_platform: "Z_SubTotal", mode_of_payment: "Cheque", sum_travel_cost: 1000000},
    //         {booking_platform: "Z_SubTotal", mode_of_payment: "Net Banking", sum_travel_cost: 1000000},
    //         {booking_platform: "Z_SubTotal", mode_of_payment: "A_SubTotal", sum_travel_cost: 1000000},
    //         ...data,
    //     ]
    // }
}

const getTooltipDatafromChartDm = (chartDm, tooltipDataFromDatastore = []) => {
    let tooltipData = tooltipDataFromDatastore
    let tooltipIndex = tooltipDataFromDatastore[tooltipDataFromDatastore.length - 1]
    const chartDataModelData = chartDm?.getData()?.data;
    if (chartDataModelData?.length) {
        tooltipData = chartDataModelData?.find((data) => {
            return data[data?.length - 1] === tooltipIndex;
        })
        return tooltipData;
    }
    return tooltipData
}

export const getTooltipProperties = (report) => {
    const { properties = {} } = report || {}
    const { tooltip = {} } = properties || {}
    return {
        ...(tooltip || {})
    }
}

export const formatterFn = ({ dataStore, context, color, size, schema, tooltipField, report, format, chartDm = null, dispatch, rowsMeasures, columnMeasures, dimensions, radial, subVizType, chartData }) => {
    try {
        let index = 0;
        if (color.length && color[0] && color[0].field) {
            let colorField = color[0].field;
            if (context && context.payload) {
                let target = context.payload.target;
                let [fields, values] = target || []
                let colorFieldIndex = Array.isArray(fields) ? fields.indexOf(colorField) : 0
                let colorFieldValue = Array.isArray(values) ? values[colorFieldIndex] : ""
                index = dataStore
                    .getData()
                    .data.findIndex((vals) => vals[colorFieldIndex] === colorFieldValue);
            }
        }
        if (size.length) {
            let sizeField = size[0].field;
            if (context && context.payload) {
                let target = context.payload.target;
                let [fields, values] = target || []
                let sizeFieldIndex = Array.isArray(fields) ? fields.indexOf(sizeField) : 0
                let sizeFieldValue = Array.isArray(values) ? values[sizeFieldIndex] : ""
                index = dataStore
                    .getData()
                    .data.findIndex((vals) => vals[sizeFieldIndex] === sizeFieldValue);
            }
        }

        if (index < 0) index = 0;

        let tooltipData = dataStore?.getData().data[index];
        if (chartDm) {
            tooltipData = getTooltipDatafromChartDm(chartDm, tooltipData)
            dataStore = chartDm
        }
        tooltipData = tooltipData ? tooltipData : dataStore.getData().data[0];

        let isRadialChart = ['doughnut', 'arc'].includes(subVizType)
        let chartSchema = {};
        let dataObj = {}
        dataStore?.getData().schema.map((item, i) => {
            dataObj[item.name] = tooltipData[i]
        });

        dataStore?.getData().schema.map((item, i) => {
            let str = tooltipData[i]
            if (`${str}`.includes("Z_")) {
                str = `${tooltipData[i]}`?.split("_")[1]
            }

            if (tooltipData && isRadialChart && radial?.showRadial) {
                const measureField = rowsMeasures?.[0] || columnMeasures?.[0];
                if (measureField && item.name === measureField) {
                    str = calculateRadialPercentage({ data: chartData, obj: dataObj, measureField, dimensions });
                }
            }
            chartSchema[item.name] = str;
        });
        let tooltipContent = "<div class='hr-viz-tooltip muze-tooltip'>";
        let tooltipValues = []
        if (!tooltipData) return html`${tooltipContent}</div>`;
        let tooltipTemplateData = {};
        const { showTooltip = true, tooltipTemplate = "<p></p>", enableTemplate = false } = getTooltipProperties(report) || {};
        if (!showTooltip) return { data: tooltipValues, content: html`<div></div>` };
        const Notify = notify(dispatch);

        schema.map((field, i) => {
            let funcName = schema.find((f) => field.name === f.name).funcName;
            let val = chartSchema[field.name];
            let isTooltipValueTypeDimension = field.name === tooltipField && field.type === "dimension"
            if (val) {
                if (isTooltipValueTypeDimension && !color.length && dataStore.getData()?.data?.length > 1) {
                    val = "*"
                } else {
                    if (field.type === "measure") {
                        // val = numberFormat({num:val});
                    } else {
                        if (field.subtype === "temporal") {
                            val = dateFormat(val, funcName);
                        } else {
                            val = val;
                        }
                    }
                }
            } else {
                val = "*";
            }
            const { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(report, field?.name)
            tooltipValues.push({ field: field.name, value: val })
            let formattedValue = getPropertyText({ text: val, applyOn: "tooltip", isApplyClicked, fieldType, formatField })
            tooltipTemplateData[field.name] = formattedValue;
            tooltipContent += `<div> 
                                        <span>${field.name} : </span>
                                        <span>${formattedValue}</span>
                                    </div> `;
        });
        if (enableTemplate) {
            tooltipContent = `<div class='hr-viz-tooltip muze-tooltip'>
                                    <div className="hreport-tooltip-template-container">
                                        ${tooltipTemplateLiquidJS({ value: tooltipTemplate, scope: tooltipTemplateData, Notify })}
                                    </div>
                                `
        }
        return { data: tooltipValues, content: html`${tooltipContent}</div>` }
    } catch (e) {
        console.log(e)
    }
};


export const getGridChartColorSchemeFromPalette = (palette = [], data = [], isContinuousColorField) => {
    if (Array.isArray(palette) && Array.isArray(data) && palette.length) {
        if (isContinuousColorField) {
            return [palette[0], palette[palette.length - 1]]
        }
        let result = palette;
        let dL = data?.length, pL = palette?.length;
        if (dL > pL) {
            result = [];
            let rValue = Math.ceil(dL / pL);
            for (let i = 0; i < rValue; i++) {
                result = result.concat(palette);
            }
        }
        return result;
    }
    return ["#a9d3f2", "#f4a4c7"];
}

export const checkIfColorFieldContinuous = (fields, colorField) => {
    let field = fields?.find((field) => getFieldDisplayName(field) === colorField)
    if (field) {
        return checkIsContinousField(field)
    }
    return false;
}


export const getActualFieldDataType = field => {
    if (!field) return false;
    let { type } = field
    let { dataType } = type || {}
    return dataType
}

const checkIsDataChanged = (prevData, currentData) => {
    return prevData.length !== currentData.length;
};

export const pathPlotFn = () => {
    let prevIndex = 0;

    return ({
        index,
        singleData,
        data,
        children,
        angleOfPie,
        radius,
        xOffset,
        yOffset,
        pieXPos,
        pieYPos,
        anchor,
        arcColorData,
        path = true,
    }) => {
        let child = children;
        let el = child[index];
        const { source } = singleData || {};
        const color = source[0];
        const paths = el.getElementsByTagName("path");
        if (paths && paths.length) {
            for (let path of paths) {
                path.remove();
            }
        }
        const textChild = el.children; // text element
        const textEl = textChild[0];
        el.style.textAnchor = anchor;

        // for curve
        const x1 = (radius + 20) * Math.cos(angleOfPie) + xOffset;
        const y1 = (radius + 20) * Math.sin(angleOfPie) + yOffset;

        // x and y points of text
        const attrX = textEl.getAttribute("x");
        const attrY = textEl.getAttribute("y");

        // create new path element
        let pathEl = document.createElementNS("http://www.w3.org/2000/svg", "path");
        pathEl.setAttribute(
            "d",
            `M ${pieXPos} ${pieYPos} Q ${x1} ${y1} , ${attrX} ${attrY}`
        );
        pathEl.setAttribute("fill", "transparent");
        pathEl.style.stroke = arcColorData[color];
        pathEl.style.strokeWidth = "1px";

        if (index > 0) {
            let children;
            if (index === 1 && checkIsDataChanged(Object.keys(arcColorData), data)) {
                children = child[0]
            } else {
                let childIndex = index === data.length - 1 ? 0 : prevIndex
                children = child[childIndex]
            }
            const prevChild = children.children[0];
            const isOverlapping = detectOverlapping(prevChild, textEl);
            if (!isOverlapping) {
                prevIndex = index;
                if (path) {
                    el.appendChild(pathEl);
                }
            } else {
                textEl.remove();
            }
        } else {
            prevIndex = 0
            if (path) {
                el.appendChild(pathEl);
            }
        }
    }
};

export const textAngleFn = () => {
    let prevAngle = 0;
    return (val, i, data, ctx, columns, plotPath, arcAngleMappingData, path, arcColorData) => {
        const d = data[i];
        const { rowId } = d || {};
        const totalSum = data
            .map((item) => {
                const { source = [] } = item || {};
                return source[1];
            })
            .filter(Boolean);
        const total = totalSum.length
            ? totalSum.reduce((acc, next) => acc + next)
            : 0;
        const { source = [] } = d;
        const singleValue = source[1];
        const pieAngle = (singleValue / total) * 360;
        prevAngle += pieAngle;
        const { startAngle, endAngle } = arcAngleMappingData[rowId];
        let angleOfPie = startAngle + (endAngle - startAngle) / 2;
        const { radius } = val || {};

        let anchor;
        if (prevAngle > 0 && prevAngle <= 180) {
            anchor = "start";
        }
        if (prevAngle > 180 && prevAngle <= 360) {
            anchor = "end";
        }

        const measurement = ctx.measurement();
        const xOffset = measurement.width / 2;
        const yOffset = measurement.height / 2;

        const pieXPos = radius * Math.cos(angleOfPie) + xOffset;
        const pieYPos = radius * Math.sin(angleOfPie) + yOffset;

        new Promise((res, rej) => {
            const mountedEl = ctx.mount();
            const firstChild = mountedEl.firstChild;
            if (firstChild.children) {
                res({
                    index: i,
                    singleData: d,
                    data,
                    children: firstChild.children,
                    angleOfPie,
                    radius,
                    xOffset,
                    yOffset,
                    pieXPos,
                    pieYPos,
                    anchor,
                    arcColorData,
                    path: path
                });
            } else {
                rej("child not found");
            }
        })
            .then((data) => plotPath(data))
            .catch((e) => console.log(e));
        return !columns.length ? val.angle : angleOfPie;
    }
}

export const getTransformType = (barType) => {
    if (barType === "stacked") return "stack";
    if (barType === "grouped") return "group";
    return "stack";
};

const calculateGroupedAxesValues = (data, axis, subVizType) => {
    const { update = {} } = data || {};
    return subVizType === 'bar' ? +update[axis] + (axis === "x" ? update.width : update.height) / 2 : update[axis]
}

const getGropedChartAxesData = (subVizType, ctx) => {
    let data = {};
    const store = ctx?.store() ?? {};
    const visualUnits = store?._contextMap?.VisualUnit ?? {};
    const base = store?._contextMap?.base ?? {};
    if (!isEmpty(visualUnits) && !isEmpty(base)) {
        let unitIds = Object.keys(visualUnits);
        unitIds.forEach((uid) => {
            let baseUnitIds = Object.keys(base);
            baseUnitIds.forEach((baseUid) => {
                if (baseUid.includes(uid) && !baseUid.includes("text")) {
                    let baseUnit = base[baseUid];
                    data = { ...data, ...baseUnit?._pointMap || {} };
                }
            })
        })
    }

    return {
        subVizType,
        data
    }
}

export const getGridChartTextLayerConfig = (props = {}) => {
    const {
        labelField = '',
        axisName = '',
        // measuresLabelFields = {},
        isRadialPropertyEnabled,
        subVizType = '',
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
        markLayers
    } = props || {}
    let plotPath = pathPlotFn();
    const calculateTextAngel = textAngleFn();
    const textLayer = {
        mark: "text",
        encoding: {
            text: {
                field: labelField,
                formatter: (...args) => {
                    const [text, , data, ctx] = args || []
                    let actualText = text;
                    let { dataObj = {} } = data?.find((d) => d?.text === text) || {}
                    let { name: yAxisName = '' } = ctx?.axes()?.y?.config() ?? {};
                    actualText = dataObj?.[labelField]
                    let isRadialChart = ['doughnut', 'arc'].includes(subVizType)
                    if ((yAxisName !== axisName) && !isRadialChart) return text;
                    if (isRadialPropertyEnabled && isRadialChart) {
                        let name = data?.find((d) => d?.text === text)?.angle ?? '';
                        const total = data?.reduce((acc, next) => {
                            return acc + next?.text
                        }, 0)
                        const percentage = (text / total) * 100;
                        let content = ``;
                        if (radial?.showRadialLabel) {
                            content = `${name}${(radial?.showRadial || radial?.showRadialValue) ? ',' : ''}`;
                        }
                        if (radial?.showRadial) {
                            content += `${percentage.toFixed(2)}% ${radial?.showRadialValue ? ',' : ''}`;
                        }
                        if (radial?.showRadialValue) {
                            content += `${text}`;
                        }
                        return content;
                    }

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
                value: () => labelsColor ? labelsColor : labels?.position === 'top' ? "#000" : subVizType === "bar" ? "#fff" : "#000"
            },
            x: {
                value: (...args) => {
                    const [val, i, data, context] = args || [];
                    let groupedAxesMapData = getGropedChartAxesData(subVizType, context);
                    if (labels?.position === 'top' && !isDimensionInColumn) {
                        let factor = 24
                        if (labels?.rotateLabels) {
                            factor -= 12;
                        }
                        return val?.x + factor;
                    }
                    if (isDimensionInColumn && isDimensionInRow && isMeasureInRows) {
                        if (getTransformType(barType) === "group" && !checkIsDateType(schema)) {
                            let { rowId } = data[i];
                            if (markLayers.length > 1) {
                                return calculateGroupedAxesValues(
                                    groupedAxesMapData['data'][rowId],
                                    "x",
                                    groupedAxesMapData.subVizType,
                                    labels?.rotateLabels
                                );
                            }
                            const finalValue = calculateGroupedAxesValues(
                                groupedAxesMapData['data'][rowId],
                                "x",
                                groupedAxesMapData.subVizType
                            );
                            return finalValue;
                        }
                        return val.x
                    }
                    if ((isDimensionInRow || isMeasureInColumn) && getTransformType(barType) !== "group" && !checkIsDateType(schema)) {
                        const value = calculateAxesValues(
                            subVizType,
                            chartsLocationDataOnAxes,
                            "x",
                            val,
                            i,
                            data,
                            context,
                            colorField,
                            labels?.rotateLabels
                        );
                        const mountedEl = context.mount()
                        new Promise((res) => res(mountedEl)).then((parentEl) => removeOverlapping({ el: parentEl, index: i, axis: "x" }))
                        return value;
                    }
                    if (getTransformType(barType) === "group" && !checkIsDateType(schema)) {
                        let { rowId } = data[i];
                        if (markLayers.length > 1) {
                            return calculateGroupedAxesValues(
                                groupedAxesMapData['data'][rowId],
                                "x",
                                groupedAxesMapData.subVizType,
                                labels?.rotateLabels
                            );
                        }
                        const finalValue = calculateGroupedAxesValues(
                            groupedAxesMapData['data'][rowId],
                            "x",
                            groupedAxesMapData.subVizType
                        );
                        return finalValue;
                    }
                    const mountedEl = context.mount()
                    new Promise((res) => res(mountedEl)).then((parentEl) => removeOverlapping({ el: parentEl, index: i, axis: "x" }))
                    return val.x;
                },
            },
            y: {
                value: (val, i, data, context) => {
                    let groupedAxesMapData = getGropedChartAxesData(subVizType, context)
                    if (labels?.position === 'top') {
                        let factor = -4
                        if (labels?.rotateLabels) {
                            factor -= 28;
                        }
                        return val?.y + factor;
                    }
                    const mountedEl = context.mount()
                    if ((!isDimensionInRow || isMeasureInRows) && !checkIsDateType(schema)) {
                        const value = calculateAxesValues(
                            subVizType,
                            chartsLocationDataOnAxes,
                            "y",
                            val,
                            i,
                            data,
                            context,
                            colorField,
                            labels?.rotateLabels
                        );
                        new Promise((res) => res(mountedEl)).then((parentEl) => removeOverlapping({ el: parentEl, data, index: i, axis: "y" }))
                        if (labels?.rotateLabels && subVizType === "bar") {
                            const barY = (data[i]?.y)
                            let axes = context?.axes();
                            return axes['y']?.getScaleValue(barY / 2)
                        }
                        return value;
                    }
                    if (getTransformType(barType) === "group" && isDimensionInRow && !checkIsDateType(schema)) {
                        let { rowId } = data[i];
                        if (markLayers.length > 1) {
                            return calculateGroupedAxesValues(
                                groupedAxesMapData['data'][rowId],
                                "y",
                                groupedAxesMapData.subVizType,
                                labels?.rotateLabels
                            );
                        }
                        const finalValue = calculateGroupedAxesValues(
                            groupedAxesMapData['data'][rowId],
                            "y",
                            groupedAxesMapData.subVizType,
                            labels?.rotateLabels
                        );
                        return finalValue;
                    }
                    new Promise((res) => res(mountedEl)).then((parentEl) => removeOverlapping({ el: parentEl, index: i, axis: "y" }))
                    return val.y;
                },
            },
            angle: {
                value: (val, i, data, ctx) => {
                    return calculateTextAngel(val, i, data, ctx, columns, plotPath, arcAngleMappingData, true, arcColorData)
                },
            },
            radius: {
                value: (v) => v.radius + 30,
            },
            rotation: {
                value: () => {
                    return labels?.rotateLabels ? 90 : 0
                }
            }
        },
    };
    return textLayer;
}

const sortMonthFns = (canvasFields) => {
    if (canvasFields?.length) {
        const isMonthFnApplied = canvasFields?.find((item) => ["sql.dateTime.month"].includes(item?.databaseFunction?.key))
        if (isMonthFnApplied) {
            new Promise((res) => {
                const elements = document.getElementsByClassName("muze-text-cell")
                res(elements)
            }).then((childElements) => {
                if (childElements?.length) {
                    for (let element of childElements) {
                        if (element?.innerText?.includes("Z_")) {
                            element.innerText = element.innerText?.split("_")[1]
                        }
                    }
                }
            })
        }
    }
}

export const getGridChartSingleMeasureLayer = (
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
    arcAngleMappingData
) => {
    let layer = {
        mark: subVizType,
        name: subVizType,
    };
    if (
        subVizType === "bar" ||
        subVizType === "point" ||
        subVizType === "line" ||
        subVizType === "area" ||
        subVizType === "tick" ||
        subVizType === "heatmap"
    ) {
        layer.encoding = {
            y: {
                value: (...args) => {
                    const [val, i, data, ctx] = args || []
                    if (checkIsDateType(schema) && subVizType === "bar") {
                        new Promise((res, rej) => {
                            let ind = i
                            const parentEl = ctx.mount()
                            if (parentEl) {
                                res({ el: parentEl, ind })
                            } else {
                                rej("no parent element found")
                            }
                        }).then(({ el, ind }) => {
                            const element = el.getElementsByClassName(`muze-layer-bar-0-${ind}`)[0]
                            const rect = element.children[0]
                            let rectWidth = rect.getAttribute("width")
                            rectWidth = +rectWidth + 1
                            let timeout = setTimeout(() => {
                                rect.setAttribute("width", rectWidth)
                                clearTimeout(timeout)
                            }, 1)
                        }).catch((e) => console.log(e))
                    }
                    chartsLocationDataOnAxes.push(data);
                    if (labelField || subVizType === "heatmap") {
                        // to remove the double lables and fade color
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
                                    let child = firstChild?.children[0]
                                    child?.remove()
                                }
                            }
                        })
                    }
                    sortMonthFns(canvasFields)
                    return val.y;
                },
            },
            x: {
                value: (val) => {
                    if (checkIsDateType(schema) && subVizType === "tick")
                        return val.x + 2;
                    return val.x;
                },
            },
            strokeWidth: { //fix for 6430
                value: () => {
                    if (checkIsDateType(schema)) return 1;
                    return 0;
                },
            },
            stroke: { //fix for 6430
                value: () => {
                    if (checkIsDateType(schema)) return "rgb(51, 122, 183)"
                },
            },
        };
    }
    if (subVizType === "area") {
        layer.encoding.strokeOpacity = {
            value: (item) => {
                item.style['fill-opacity'] = 0.8
                return 0.8
            }
        }
    }
    if (subVizType === "arc" || subVizType === "doughnut") {
        let measureField = schema.find((field) => field.type === "measure");
        layer.mark = "arc";
        layer.name = "arc";
        if (measureField) {
            layer.encoding = {
                angle: {
                    field: measureField.name,
                    value: (val, i, data, context) => {
                        const d = data[i];
                        const { source = {} } = d || {};
                        const color = source[0];
                        arcColorData[color] = val.color;
                        const axes = context.axes();
                        const { angle: angleAxis } = axes || {};
                        const angles = angleAxis.getScaleValue(d.angle);
                        const { rowId } = d;
                        const formedData = {
                            ...angles[0],
                            angleAxis,
                        };
                        arcAngleMappingData[rowId] = formedData;

                        // to set the proper position of arc/doughnut chart label axis
                        new Promise((res, rej) => {
                            const el = document.querySelector(".hr-grid-chart")
                            if (el) {
                                res({ element: el, index: totalOfIndexes })
                                totalOfIndexes += 1
                            } else {
                                rej('element not found')
                            }
                        }).then(({ element, index }) => {
                            const firstChild = element.children[0]
                            if (firstChild && rows.length) {
                                const textChildren = firstChild.querySelectorAll(".muze-text-cell")
                                let child = textChildren[index]
                                child.style.padding = "1px 8px 10px 8px"
                                const { chartDm } = formattedData || {}
                                const data = chartDm?.getData().data[index]
                                schema.map((field) => {
                                    let funcName = schema.find((f) => field.name === f.name).funcName;
                                    const { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(report, field?.name)
                                    if (isApplyClicked && formatField[0]?.values?.apply.includes('axis') && field.subtype === "temporal") {
                                        const formattedLabel = getFormattedLabel({ value: data[0], funcName, isApplyClicked, fieldType, formatField })
                                        child.innerText = formattedLabel
                                    }
                                });
                            }
                        }).catch(e => console.log(e))
                        return val.angle;
                    },
                },
            };
        }
    }
    if (subVizType === "heatmap") {
        layer.mark = "bar";
    }

    return layer;
}