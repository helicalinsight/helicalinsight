import {
    Facet,
    Pie
} from "@ant-design/plots";
import { Row, Tooltip } from "antd";
import { isEmpty } from "lodash-es";
import React from "react";
import { useDispatch } from "react-redux";
import { setMenuData } from "../../../../../redux/actions/hreport.actions";
import "../../../../../utils/polyfill/url";
import { getCardValue } from "../../../hi-editing-area/components/properties/property-card-utils";
import {
    getFieldName,
    getFiledValueColor,
    getGradientColor,
    getHTMLColorFormat
} from "../../../hi-editing-area/utils/property-utils";
import CellCard from "../../cell-card";
import Toolbar from "../../toolbar";
import {
    calculateRadialPercentage,
    getPropertyElement,
    getPropertyFieldInfo,
    getPropertyText
} from "../../utils/utillities";
import "../ant-chart.scss";
import { divStyles, getAntChartLegendLabelConfig, getChartTooltipTemplate, setStyles } from "../ant-utils";
import AntStakedRoseChart from "./ant-stake-rose";


const DonutChart = (props) => {
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
        addForwardFilterIPC,
        deleteBackwardFilterIPC,
        refreshFiltersIPC,
        labelField,
        formatColor,
        colorField,
        titleStyle,
        subTitleStyle,
        height,
        chartAreaWidth,
        chartAreaHeight,
        themeColors,
        Notify,
        showTooltip,
        tooltipTemplate,
        enableTemplate
    } = props;
    const { legendPosition } = report?.reportData?.properties.legend;
    const { title, subTitle, radial, labels } = report?.reportData?.properties || {};
    const { chartType: radialChartType } = radial || {}

    if (["rose", "radial bar"].includes(radialChartType)) {
        return (
            <AntStakedRoseChart {...props} subVizType={radialChartType} />
        )
    }
    const {
        doughnutPrefixType,
        doughnutSuffixType,
        doughnutPrefix,
        doughnutSuffix,
        showDoughnutTitle,
        doughnutTitle
    } = radial || {}
    let doughnutPrefixValue = getCardValue(doughnutPrefixType, doughnutPrefix, { r: 0, g: 0, b: 0, a: 1 }, true)
    let doughnutSuffixValue = getCardValue(doughnutSuffixType, doughnutSuffix, { r: 0, g: 0, b: 0, a: 1 }, true)
    let labelsColor = isEmpty(labels?.labelsColor) ? null : getHTMLColorFormat(labels?.labelsColor);
    let chartType = "facetDonut";
    let doughnutChartContentFontSize = Math.min(chartAreaWidth, chartAreaHeight) / 22;
    if (dimensions.length === 0 && (columnMeasures.length === 1 || rowsMeasures.length === 1)) {
        chartType = "simpleDonut";
    }

    let measureField = columnMeasures?.length === 1 ? columnMeasures?.[0] : rowsMeasures?.[0];

    const fields = dimensions[0]
        ? dimensions[0]
        : rowsMeasures[0]
            ? rowsMeasures[0]
            : columnMeasures[0];
    const donutObj = {
        type: "pie",
        options: {
            angleField: rowsMeasures[0],
            radius: 1,
            innerRadius: 0.6,
            pieStyle: {
                stroke: null,
            },
            statistic: false,
        },
    };
    if (colorField) donutObj["options"]["colorField"] = colorField;
    if (labelField || radial?.showRadial) {
        if (radial?.showRadial) {
            donutObj["options"]["label"] = {
                content: ({ percent }) => `${(percent * 100).toFixed(0)}%`,
                type: "outer",
                style: {
                    fill: labelsColor ? labelsColor : "#000"
                }
            };
        } else {
            donutObj["options"]["label"] = {
                content: (obj) => obj[labelField],
                rotate: labels?.rotateLabels ? 55 : 0
            };
            if (labelsColor) {
                donutObj["options"]["label"]["style"] = {
                    fill: labelsColor
                };
            }
        }
    }
    const getTooltip = (content) => {
        return (
            <span className="doughnut-analytics-ellipsis-tooltip">{content}</span>
        )
    }
    const facetDonutConfig = {
        type: "rect",
        fields: [fields],
        data,
        legend:
            legendPosition === "none" ? false : { position: legendPosition ? legendPosition : "bottom", ...getAntChartLegendLabelConfig(report, colorField) },
        tooltip: !showTooltip ?
            false : {
                customContent: function (x, dataObj) {
                    if (dataObj && dataObj.length > 0) {
                        const actualData = dataObj[0].data;
                        const container = document.createElement("div");
                        const tootipData = Object.entries(actualData);
                        tootipData.forEach((e) => {
                            if (e[0] !== "name") {
                                const { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(report, e[0]);
                                let value = getPropertyText({
                                    text: e[1],
                                    applyOn: "tooltip",
                                    isApplyClicked,
                                    fieldType,
                                    formatField,
                                });
                                if (measureField === e[0] && radial?.showRadial) {
                                    value = calculateRadialPercentage({ data, obj: actualData, measureField, dimensions });
                                }
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
                showMarkers: false,
            },
        meta: {
            cut: {
                sync: true,
            },
        },
        eachView: (_v, f) => {
            return { ...donutObj, options: { ...donutObj.options, data: f.data } };
        },
    };
    const config = {
        appendPadding: 10,
        data,
        radius: 0.8,
        innerRadius: 0.6,
        tooltip: !showTooltip ?
            false : {
                customContent: function (x, dataObj) {
                    if (dataObj && dataObj.length > 0) {
                        const actualData = dataObj[0].data;
                        const container = document.createElement("div");
                        const tootipData = Object.entries(actualData);
                        tootipData.forEach((e) => {
                            if (e[0] !== "name") {
                                const { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(report, e[0]);
                                let value = getPropertyText({
                                    text: e[1],
                                    applyOn: "tooltip",
                                    isApplyClicked,
                                    fieldType,
                                    formatField,
                                });
                                if (measureField === e[0] && radial?.showRadial) {
                                    value = calculateRadialPercentage({ data, obj: actualData, measureField, dimensions });
                                }
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
        label: {
            type: "inner",
            offset: "-50%",
            content: "{value}",
            style: {
                textAlign: "center",
                fontSize: 14,
            },
        },
        statistic: {
            title: {
                style: {
                    color: '#000',
                    fontSize: `${doughnutChartContentFontSize}px`
                },
                formatter: () => {
                    if (!showDoughnutTitle) return null
                    return doughnutTitle ? doughnutTitle : measureField;
                }
            },
            content: {
                style: {
                    pointerEvents: 'all',
                },
                customHtml: (c, v, datum, data) => {
                    if (!showDoughnutTitle) return null
                    let total = data?.reduce((acc, next) => {
                        return acc + next[measureField];
                    }, 0);
                    const { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(
                        report,
                        measureField
                    );
                    let formattedText = getPropertyText({
                        text: total,
                        applyOn: "pane",
                        isApplyClicked,
                        fieldType,
                        formatField,
                    });
                    return (
                        <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: `${doughnutChartContentFontSize}px` }}>
                            <Tooltip title={getTooltip(doughnutPrefixValue)}>
                                <span className="doughnut-analytics-ellipsis">{doughnutPrefixValue}</span>
                            </Tooltip>
                            <Tooltip title={getTooltip(formattedText)}>
                                <span className="doughnut-analytics-text-ellipsis">{formattedText}</span>
                            </Tooltip>
                            <Tooltip title={getTooltip(doughnutSuffixValue)}>
                                <span className="doughnut-analytics-ellipsis">{doughnutSuffixValue}</span>
                            </Tooltip>
                        </div >
                    )
                }
            }
        }
    };
    let isRadialPropertyEnabled = radial?.showRadial || radial?.showRadialLabel || radial?.showRadialValue;
    if (colorField && (labelField || isRadialPropertyEnabled)) {
        config.label = {
            type: "outer",
            content: (obj) => {
                let fieldValue = obj[labelField] ?? (radial?.showRadialValue ? obj[measureField] : null);
                const { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(report, labelField ?? (radial?.showRadialValue ? measureField : null));
                let formattedText = getPropertyText({
                    text: fieldValue,
                    applyOn: "label",
                    isApplyClicked,
                    fieldType,
                    formatField,
                });
                formattedText = formattedText ?? '';
                if (!formattedText && radial?.showRadialValue) {
                    formattedText += `${obj[measureField]}`
                }
                if (radial?.showRadial) {
                    formattedText += `\n${(obj?.percent * (100)).toFixed(2)}%`
                }
                if (radial?.showRadialLabel) {
                    formattedText += `\n${obj?.[colorField]}`
                }
                return formattedText;
            },
            style: {
                fill: labelsColor ? labelsColor : "#000"
            }
        };
    }

    if (!labelField && !isRadialPropertyEnabled) {
        config.label = null;
    }

    if (radial?.showRadial) {
        // config.label.labelLine = null
    }

    if (columnMeasures.length > 0) {
        config["angleField"] = columnMeasures[0];
    }

    if (rowsMeasures.length > 0) {
        config["angleField"] = rowsMeasures[0];
    }
    if (colorField) {
        // config.label = false
        config.colorField = colorField;
        config.legend =
            legendPosition === "none" ? false : { position: legendPosition ? legendPosition : "bottom", ...getAntChartLegendLabelConfig(report, colorField) };
    }

    if (formatColor.formatColorStyle === "gradient") {
        if (formatColor.minimum && formatColor.maximum) {
            config.colorField = getFieldName(formatColor?.formatColorField, report);
            config.color = (obj) => getGradientColor(obj, formatColor, data);

            //Facet Color
            donutObj["options"]["color"] = (obj) => getGradientColor(obj, formatColor, data);
            donutObj["options"]["colorField"] = getFieldName(formatColor?.formatColorField, report);
        }
    }
    if (formatColor?.formatColorStyle === "fieldValue") {
        config.colorField = getFieldName(formatColor?.formatColorField, report);
        config.color = (obj) => {
            const color = getFiledValueColor(obj, formatColor);
            if (formatColor?.showAll && color) {
                return color;
            }
            return getHTMLColorFormat(formatColor.defaultColor);
        };

        //Facet color
        donutObj["options"]["colorField"] = getFieldName(formatColor?.formatColorField, report);
        donutObj["options"]["color"] = (obj) => {
            const color = getFiledValueColor(obj, formatColor);
            if (formatColor?.showAll && color) {
                return color;
            }
            return getHTMLColorFormat(formatColor.defaultColor);
        };
    }

    if (themeColors?.length) {
        config.theme = 'custom-theme';
    }

    const onDonutClick = (args, plot) => {
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
                top = y;
                left = x;
            }
            // top = args.data.y;
            // left = args.data.x;
            let menuData = {
                payload,
                position: { top, left },
                // position: { top: top[0], left: left[0] },
                drillDownFilterValues: data,
            };
            dispatch(setMenuData({ reportId, menu: menuData }));
        }
    };

    console.log("config", config);

    return (
        <div style={{ height: `${height}px` }}>
            <div className="title-subtitle-container">
                {title?.position === "top" && getPropertyElement(titleStyle, title)}
                {subTitle?.position === "top" && getPropertyElement(subTitleStyle, subTitle)}
            </div>
            {chartType === "simpleDonut" ? (
                <Pie
                    {...config}
                    onReady={(plot) => {
                        plot.on("click", (args) => {
                            onDonutClick(args, plot);
                        });
                    }}
                />
            ) : (
                <Facet
                    {...facetDonutConfig}
                    onReady={(plot) => {
                        plot.on("click", (args) => {
                            onDonutClick(args, plot);
                        });
                    }}
                />
            )}
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
        </div>
    );
};

export default DonutChart;