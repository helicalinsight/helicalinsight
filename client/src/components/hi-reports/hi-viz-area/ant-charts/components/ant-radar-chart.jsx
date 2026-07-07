import { Radar } from '@ant-design/plots';
import { Row } from 'antd';
import { isEmpty } from 'lodash';
import React from 'react';
import { useDispatch } from 'react-redux';
import { setMenuData } from '../../../../../redux/actions/hreport.actions';
import { getFieldDisplayName } from '../../../../../utils/utilities';
import { generateColorRange, generateColorsFromRange, getEachFieldColor, getFieldData, getFieldName, getFiledValueColor, getHTMLColorFormat, getPropertyAxisRange } from '../../../hi-editing-area/utils/property-utils';
import CellCard from '../../cell-card';
import Toolbar from '../../toolbar';
import { checkIsContinousField, getPropertyElement, getPropertyFieldInfo, getPropertyText } from '../../utils/utillities';
import { antdChartCategoryColors, antdChartContinuousColors, divStyles, getAntChartLegendLabelConfig, getChartTooltipTemplate, setStyles } from '../ant-utils';

const AntRadarChart = (props) => {
    const {
        data,
        report,
        rowsMeasures = [],
        columnMeasures = [],
        dimensions,
        colorField,
        labelField,
        tooltipField,
        interactiveMode,
        drillDown,
        drillThrough,
        reportId,
        format,
        titleStyle,
        subTitleStyle,
        height,
        fields,
        chartAreaWidth,
        chartAreaHeight,
        mode,
        themeColors,
        measuresLabelFields,
        Notify,
        showTooltip,
        tooltipTemplate,
        enableTemplate
    } = props || {}
    const dispatch = useDispatch();
    const { legendPosition } = report?.reportData?.properties.legend;
    const { title, subTitle, labels, axisRange, formatColor, radar: { radarChartType = "area" } = {} } = report?.reportData?.properties || {};
    let labelsColor = isEmpty(labels?.labelsColor) ? null : getHTMLColorFormat(labels?.labelsColor);
    let yField = "",
        xField = dimensions[0];

    let color = [];
    let isSmallRadar = colorField && ['dashboard'].includes(mode)
    let radarChartWidth = isSmallRadar ? (chartAreaWidth - (chartAreaWidth * 0.2)) : chartAreaWidth;
    let radarChartHeight = isSmallRadar ? (chartAreaHeight - (chartAreaHeight * 0.2)) : chartAreaHeight;

    let radarAxisConfig = {
        yAxisLabelColor: "#bababa",
        yAxisFontSize: 12,
        yAxisHide: false,
        yAxisRotate: 0,
        yAxisRange: {},
        xAxisLabelColor: "#bababa",
        xAxisFontSize: 12,
        xAxisHide: false,
        xAxisRotate: 0,
        xAxisRange: {}
    }

    if (columnMeasures.length > 0) {
        yField = columnMeasures[0];
    }

    if (rowsMeasures.length > 0) {
        yField = rowsMeasures[0];
    }
    if (axisRange?.fields && axisRange.fields.length) {
        axisRange.fields.forEach((field) => {
            const { hide, rotate, fontSize, fontColor, name } = field.data || {};
            if (name === yField) {
                let axisLabelColor = getHTMLColorFormat(fontColor);
                const rangeData = getPropertyAxisRange(field, "antChart")
                if (rangeData) {
                    radarAxisConfig = {
                        ...radarAxisConfig,
                        yAxisRange: rangeData
                    }
                }
                radarAxisConfig = {
                    ...radarAxisConfig,
                    yAxisLabelColor: axisLabelColor,
                    yAxisFontSize: fontSize,
                    yAxisRotate: rotate,
                    yAxisHide: hide
                }
            }
            if (name === xField) {
                let axisLabelColor = getHTMLColorFormat(fontColor);
                const rangeData = getPropertyAxisRange(field, "antChart")
                if (rangeData) {
                    radarAxisConfig = {
                        ...radarAxisConfig,
                        xAxisRange: rangeData
                    }
                }
                radarAxisConfig = {
                    ...radarAxisConfig,
                    xAxisLabelColor: axisLabelColor,
                    xAxisFontSize: fontSize,
                    xAxisRotate: rotate,
                    xAxisHide: hide
                }
            }
        })
    }
    const config = {
        data,
        xField,
        yField,
        tooltip: !showTooltip ?
            false : {
                shared: false,
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
            },
        legend: legendPosition === "none" ? false : { position: legendPosition ? legendPosition : "bottom", ...getAntChartLegendLabelConfig(report, colorField) },
        label: {
            style: {
                fill: labelsColor ? labelsColor : '#000'
            },
            content: (data) => {
                if (labelField || !isEmpty(measuresLabelFields)) {
                    let tempLabelField = labelField;
                    if (!isEmpty(measuresLabelFields)) {
                        tempLabelField = measuresLabelFields[yField] ?? labelField;
                    }
                    const { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(report, tempLabelField)
                    let text = data[tempLabelField]
                    let formattedText = getPropertyText({ text, applyOn: "label", isApplyClicked, fieldType, formatField })
                    return formattedText;
                }
                return null;
            }
        },
        xAxis: radarAxisConfig.xAxisHide ? false : {
            label: {
                formatter: (text) => {
                    const { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(report, xField)
                    let formattedText = getPropertyText({ text, applyOn: "axis", isApplyClicked, fieldType, formatField })
                    return formattedText;
                },
                rotate: radarAxisConfig.xAxisRotate,
                style: {
                    fill: radarAxisConfig.xAxisLabelColor,
                    fontSize: radarAxisConfig.xAxisFontSize,
                }
            },
            ...radarAxisConfig.xAxisRange
        },
        yAxis: radarAxisConfig.yAxisHide ? false : {
            label: {
                formatter: (text) => {
                    const { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(report, yField)
                    let formattedText = getPropertyText({ text, applyOn: "axis", isApplyClicked, fieldType, formatField })
                    return formattedText;
                },
                rotate: radarAxisConfig.yAxisRotate,
                style: {
                    fill: radarAxisConfig.yAxisLabelColor,
                    fontSize: radarAxisConfig.yAxisFontSize
                }
            },
            ...radarAxisConfig.yAxisRange
        },
        color,
        width: radarChartWidth,
        height: radarChartHeight,
    };
    if (radarChartType === "area") {
        config.area = {
            style: {
                fillOpacity: 0.5,
            },
        }
    } else {
        config.area = null;
    }
    if (tooltipField) {
        config.tooltip.fields = [tooltipField]
    }

    if (colorField) {
        config.seriesField = colorField
        let actualColorField = fields.find(field => getFieldDisplayName(field) === colorField);
        if (checkIsContinousField(actualColorField)) {
            config.color = themeColors?.length ? themeColors : antdChartContinuousColors
        } else {
            config.color = themeColors?.length ? themeColors : antdChartCategoryColors
        }
    }

    if (formatColor?.formatColorStyle === "fieldValue") {
        let fieldName = getFieldName(formatColor?.formatColorField, report)
        if (fieldName) {
            config.seriesField = fieldName
            config.color = (obj) => {
                if (formatColor?.showAll) {
                    const singleFiledcolor = getFiledValueColor(obj, formatColor);
                    if (singleFiledcolor) {
                        return singleFiledcolor;
                    } else {
                        const { value } = obj || {}
                        if (value) {
                            return getEachFieldColor(formatColor, value);
                        }
                        return '#546ce6';
                    }
                } else {
                    return getHTMLColorFormat(formatColor.defaultColor);
                }
            }
        }
    }

    if (formatColor?.formatColorStyle === "gradient") {
        if (formatColor.minimum && formatColor.maximum) {
            let fieldName = getFieldName(formatColor?.formatColorField, report)
            if (fieldName) {
                config.seriesField = fieldName
                const fieldData = getFieldData(data, fieldName);
                const uniqueData = [...new Set(fieldData)];
                color = generateColorRange(formatColor?.minimum, formatColor?.maximum, uniqueData.length, formatColor?.steps)
                if (formatColor?.enableSteps) {
                    color = generateColorsFromRange(uniqueData, formatColor);
                }
                let actualColorField = fields.find(field => getFieldDisplayName(field) === fieldName);
                config.color = checkIsContinousField(actualColorField) ? color : color.reverse();
            }
        }
    }

    if (themeColors?.length) {
        config.theme = 'custom-theme';
    }

    const onChartClick = (args, plot) => {
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
            let menuData = {
                payload,
                position: { top, left },
                drillDownFilterValues: data,
            };
            dispatch(setMenuData({ reportId, menu: menuData }));
        }
    };

    return (
        <div style={{ height: `${radarChartHeight}px` }}>
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
            <Radar
                {...config}
                onReady={(plot) => {
                    plot.on("click", (args) => {
                        onChartClick(args, plot);
                    });
                }}
            />
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
}

export default AntRadarChart