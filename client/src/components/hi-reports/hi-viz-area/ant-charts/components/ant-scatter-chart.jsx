
import {
    Scatter
} from "@ant-design/plots";
import { Row } from "antd";
import { isEmpty } from "lodash-es";
import React, { useRef, useState } from "react";
import { useDispatch } from "react-redux";
import { setMenuData } from "../../../../../redux/actions/hreport.actions";
import "../../../../../utils/polyfill/url";
import {
    getFieldName,
    getFiledValueColor,
    getGradientColor,
    getHTMLColorFormat,
    getPropertyAxisRange
} from "../../../hi-editing-area/utils/property-utils";
import CellCard from "../../cell-card";
import Toolbar from "../../toolbar";
import {
    getPropertyElement,
    getPropertyFieldInfo,
    getPropertyText
} from "../../utils/utillities";
import { plotReferenceLineContent } from "../ant-chart";
import "../ant-chart.scss";
import { divStyles, getAntChartLegendLabelConfig, getChartTooltipTemplate, setStyles } from "../ant-utils";

const ScatterChart = (props) => {
    const dispatch = useDispatch();
    const {
        data,
        dimensions,
        columnMeasures,
        rowsMeasures,
        interactiveMode,
        drillDown,
        drillThrough,
        reportId,
        addFilter,
        report,
        format,
        colorField,
        sizeField,
        addForwardFilterIPC,
        deleteBackwardFilterIPC,
        refreshFiltersIPC,
        labelField,
        formatColor,
        shapeField,
        metadata,
        titleStyle,
        subTitleStyle,
        height,
        referenceLineList,
        fields,
        themeColors,
        measuresLabelFields,
        Notify,
        showTooltip,
        tooltipTemplate,
        enableTemplate
    } = props;
    const { legendPosition } = report?.reportData?.properties.legend;
    const { title, subTitle, axisRange, labels, charts = {} } = report?.reportData?.properties || {};
    let labelsColor = isEmpty(labels?.labelsColor) ? null : getHTMLColorFormat(labels?.labelsColor);
    const [chartMaxScaleValue, setChartMaxScaleValue] = useState(null)
    const cRef = useRef()
    const { showAxisName } = axisRange || {}
    let axisNameStyles = {
        fill: '#5f5f5f',
        fontSize: 14
    }

    let yField = "";
    if (columnMeasures.length > 0) {
        yField = columnMeasures[0];
    }

    if (rowsMeasures.length > 0) {
        yField = rowsMeasures[0];
    }
    const config = {
        appendPadding: 10,
        data,
        xField: dimensions[0],
        yField,
        legend:
            legendPosition === "none" ? false : { position: legendPosition ? legendPosition : "right", ...getAntChartLegendLabelConfig(report, colorField) },
        xAxis: {
            grid: null,
            label: {
                formatter: (text) => {
                    const { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(
                        report,
                        dimensions[0]
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
            title: !showAxisName ? null : {
                text: dimensions[0],
                style: axisNameStyles
            }
        },
        yAxis: {
            grid: null,
            nice: true,
            line: {
                style: {
                    stroke: "#aaa",
                },
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
            title: !showAxisName ? null : {
                text: yField
            }
        },
        tooltip: !showTooltip ?
            false : {
                customContent: function (x, data) {
                    if (data && data.length > 0) {
                        const container = document.createElement("div");
                        const tootipData = Object.entries(data[0].data);
                        tootipData.forEach((e) => {
                            if (e[0] !== "name") {
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
                            }
                        });
                        setStyles(container, divStyles);
                        if (enableTemplate) {
                            return getChartTooltipTemplate({ data: tootipData, tooltipTemplate, report, Notify });
                        }
                        return container;
                    }
                },
            },
        interactions: [
            {
                type: "element-active",
            },
        ],
    };

    if (labelField || !isEmpty(measuresLabelFields)) {
        config.label = {
            content: (obj) => {
                let tempLabelField = labelField;
                if (!isEmpty(measuresLabelFields)) {
                    tempLabelField = measuresLabelFields[yField] ?? labelField;
                }
                const { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(report, tempLabelField);
                let formattedText = getPropertyText({
                    text: obj[tempLabelField],
                    applyOn: "label",
                    isApplyClicked,
                    fieldType,
                    formatField,
                });
                return formattedText;
            },
            rotate: labels?.rotateLabels ? 55 : 0,
            style: {}
        };
        if (labelsColor) {
            config.label.style.fill = labelsColor;
        } else {
            config.label.style.fill = 'auto';
        }
        if (labels?.fontSize) {
            config.label.style.fontSize = labels?.fontSize;
        }
        if (labels?.offsetY || labels?.offsetX) {
            config.label.offsetY = labels?.offsetY || 0
            config.label.offsetX = labels?.offsetX || 0
        } else {
            config.label.offsetY = 0;
            config.label.offsetX = 0;
        }
        if (labels?.position === 'top') {
            config.label.offsetY = 8 + (labels?.offsetY || 0)
            if (labels?.rotateLabels) {
                config.label.offsetX = 12 + (labels?.offsetX || 0)
            }
        }
    }
    if (!shapeField) {
        config.shape = "circle";
    }
    if (shapeField) {
        config.shapeField = shapeField;
    }
    if (colorField) config.colorField = colorField;
    if (sizeField) {
        config.sizeField = sizeField;
        config.size = [4, 20];
        config.sizeLegend = true
    }
    if (metadata.length > 0 && metadata[0]) {
        const columnsArray = Object.entries(metadata[0]).map((e) => e[1]);
        for (let i = 0; i < columnsArray.length; i++) {
            if (columnsArray[i].name === dimensions[0] && ["dateTime"].includes(columnsArray[i].type)) {
                config.xAxis = { ...config.xAxis, tickCount: 5 };
            }
        }
    }

    if (formatColor.formatColorStyle === "gradient") {
        if (formatColor.minimum && formatColor.maximum) {
            config.colorField = getFieldName(formatColor?.formatColorField, report);
            config.color = (obj) => getGradientColor(obj, formatColor, data);
        }
    }
    if (formatColor?.formatColorStyle === "fieldValue") {
        config.colorField = getFieldName(formatColor?.formatColorField, report);
        config.color = (obj) => {
            if (formatColor?.showAll) {
                return getFiledValueColor(obj, formatColor);
            }
            return getHTMLColorFormat(formatColor.defaultColor);
        };
    }

    if (axisRange?.gridLines?.length) {
        if (axisRange.gridLines.includes("x")) {
            config.xAxis = {
                ...config.xAxis,
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
            config.yAxis = {
                ...config.yAxis,
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
            const { hide, rotate, fontColor, fontSize } = field.data || {};
            const axisLabelColor = !isEmpty(fontColor) ? getHTMLColorFormat(fontColor) : null;
            if (config.xField === field.data.name) {
                config.xAxis = { ...config.xAxis, label: { ...config.xAxis.label, rotate: (rotate / 57), autoEllipsis: true }, verticalFactor: (rotate < 0 || rotate > 0) ? -5 : -2, }
                if (rotate < 0 || rotate > 0) {
                    config.xAxis = { ...config.xAxis, verticalLimitLength: !showAxisName ? 80 : 160, title: { ...config.xAxis.title, offset: 20 } }
                }
                config.xAxis = { ...config.xAxis, visible: !hide }
                const rangeData = getPropertyAxisRange(field, "antChart")
                if (rangeData) {
                    config.xAxis = { ...config.xAxis, ...rangeData }
                }
                config.xAxis.label.style = { fontSize };
                if (axisLabelColor) {
                    config.xAxis.label.style.fill = axisLabelColor;
                } else {
                    config.xAxis.label.style.fill = '#a1a1a1'
                }
            }
            if (config.yField === field.data.name) {
                if (rotate || rotate === 0) {
                    config.yAxis = { ...config.yAxis, label: { ...config.yAxis.label, rotate: (rotate / 57) } }
                }
                config.yAxis = { ...config.yAxis, visible: !hide }
                const rangeData = getPropertyAxisRange(field, "antChart")
                if (rangeData) {
                    config.yAxis = { ...config.yAxis, ...rangeData }
                }
                config.yAxis.label.style = { fontSize };
                if (axisLabelColor) {
                    config.yAxis.label.style.fill = axisLabelColor;
                } else {
                    config.xAxis.label.style.fill = '#a1a1a1'
                }
            }
        })
    }

    if (themeColors?.length) {
        config.theme = 'custom-theme';
    }

    if (charts?.enablePagination) {
        config.slider = {
            start: charts?.starts ? charts?.starts / 100 : 0,
            end: charts?.ends ? (charts?.ends - 1) / 100 : 0.1
        }
    }

    const onPieClick = (args, plot) => {
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
                if (key !== "name") payload.push({ field: key, value: barData[key] });
            });
            // let { top, left } = args.view.ele.getBoundingClientRect();
            let { clientX: left, clientY: top } = args || {}
            if (!['dashboard'].includes(props.mode)) {
                top = args.data.y;
                left = args.data.x;
            }
            let menuData = {
                payload,
                position: { top, left },
                drillDownFilterValues: data,
            };
            dispatch(setMenuData({ reportId, menu: menuData }));
        }
    };

    return (
        <div style={{ height: `${height}px` }}>
            <div className="title-subtitle-container">
                {title?.position === "top" && getPropertyElement(titleStyle, title)}
                {subTitle?.position === "top" && getPropertyElement(subTitleStyle, subTitle)}
            </div>
            <Scatter
                {...config}
                chartRef={cRef}
                onReady={(plot) => {
                    setChartMaxScaleValue(0)
                    plot.on("click", (args) => {
                        onPieClick(args, plot);
                    });
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
                {subTitle?.position === "bottom" && getPropertyElement(subTitleStyle, subTitle)}
            </div>
            {
                cRef?.current
                &&
                plotReferenceLineContent({ chartAreaWidth: props.chartAreaWidth, chartAreaHeight: height, referenceLineList, fields: fields, chartContext: cRef?.current, config, columnMeasures, rowsMeasures })
            }
        </div>
    );
};

export default ScatterChart;