import { Waterfall } from '@ant-design/plots';
import { Row } from 'antd';
import { isEmpty } from 'lodash';
import React from 'react';
import { useDispatch } from 'react-redux';
import { setMenuData } from '../../../../../redux/actions/hreport.actions';
import { getFieldDisplayName } from '../../../../../utils/utilities';
import notify from '../../../../hi-notifications/notify';
import { generateColorRange, generateColorsFromRange, getEachFieldColor, getFieldData, getFieldName, getFiledValueColor, getHTMLColorFormat, getPropertyAxisRange } from '../../../hi-editing-area/utils/property-utils';
import CellCard from '../../cell-card';
import Toolbar from '../../toolbar';
import { checkIsContinousField, getPropertyElement, getPropertyFieldInfo, getPropertyText } from '../../utils/utillities';
import { antdChartCategoryColors, antdChartContinuousColors, divStyles, getChartTooltipTemplate, setStyles } from '../ant-utils';

const AntWaterFallChart = (props) => {
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
        rows,
        columns,
        fields,
        themeColors,
        measuresLabelFields,
        showTooltip,
        tooltipTemplate,
        enableTemplate
    } = props || {}

    const dispatch = useDispatch();
    const { legendPosition } = report?.reportData?.properties.legend;
    const { title, subTitle, labels, formatColor, axisRange } = report?.reportData?.properties || {};
    let labelsColor = isEmpty(labels?.labelsColor) ? null : getHTMLColorFormat(labels?.labelsColor);
    const Notify = notify(dispatch);
    const { showAxisName
    } = axisRange || {}
    let xField = columns[0],
        yField = rows[0];

    if (columnMeasures?.length) {
        Notify.error({
            type: "Frontend",
            message: "There is an issue with this generated chart. Please add Measure in rows to see the correct Waterfall Chart",
        });
    }

    let waterfallAxisConfig = {
        yAxisLabelColor: "#a1a1a1",
        yAxisFontSize: 12,
        yAxisHide: false,
        yAxisRotate: 0,
        yAxisRange: {},
        xAxisLabelColor: "#a1a1a1",
        xAxisFontSize: 12,
        xAxisHide: false,
        xAxisRotate: 0,
        xAxisRange: {}
    }
    // if (columnMeasures.length > 0) {
    //     yField = columnMeasures[0];
    // }

    // if (rowsMeasures.length > 0) {
    //     yField = rowsMeasures[0];
    // }

    if (axisRange?.fields && axisRange.fields.length) {
        axisRange.fields.forEach((field) => {
            const { hide, rotate, fontSize, fontColor, name } = field.data || {};
            if (name === yField) {
                let axisLabelColor = !isEmpty(fontColor) ? getHTMLColorFormat(fontColor) : null;
                const rangeData = getPropertyAxisRange(field, "antChart")
                if (rangeData) {
                    waterfallAxisConfig = {
                        ...waterfallAxisConfig,
                        yAxisRange: rangeData
                    }
                }
                waterfallAxisConfig = {
                    ...waterfallAxisConfig,
                    yAxisFontSize: fontSize,
                    yAxisRotate: rotate,
                    yAxisHide: hide
                }
                if (axisLabelColor) {
                    waterfallAxisConfig.yAxisLabelColor = axisLabelColor
                }
            }
            if (name === xField) {
                let axisLabelColor = !isEmpty(fontColor) ? getHTMLColorFormat(fontColor) : null;
                const rangeData = getPropertyAxisRange(field, "antChart")
                if (rangeData) {
                    waterfallAxisConfig = {
                        ...waterfallAxisConfig,
                        xAxisRange: rangeData
                    }
                }
                waterfallAxisConfig = {
                    ...waterfallAxisConfig,
                    xAxisFontSize: fontSize,
                    xAxisRotate: rotate,
                    xAxisHide: hide
                }
                if (axisLabelColor) {
                    waterfallAxisConfig.xAxisLabelColor = axisLabelColor
                }
            }
        })
    }

    const config = {
        data,
        height: props?.height,
        width: props?.chartAreaWidth,
        xField,
        yField,
        linkStyle: {
            lineDash: [4, 2],
            stroke: '#ccc',
        },
        label: {
            position: 'middle',
            content: (data) => {
                if (labelField || !isEmpty(measuresLabelFields)) {
                    let tempLabelField = labelField;
                    if (!isEmpty(measuresLabelFields)) {
                        tempLabelField = measuresLabelFields[yField] ?? labelField;
                    }
                    let text = data[tempLabelField];
                    const { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(report, tempLabelField)
                    let formattedText = getPropertyText({ text, applyOn: "label", isApplyClicked, fieldType, formatField })
                    return formattedText;
                }
                return null;
            },
            style: {
                fill: labelsColor ? labelsColor : '#fff'
            },
            rotate: labels?.rotateLabels ? 1.6 : 0
        },
        tooltip: !showTooltip ?
            false : {
                customContent: function (x, data) {
                    if (data && data.length > 0) {
                        const container = document.createElement("div");
                        const tootipData = Object.entries(data[0].data);
                        tootipData.forEach((e) => {
                            if ([labelField, colorField, xField, yField, tooltipField].includes(e[0])) {
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
                }
            },
        total: {
            label: 'Total'
        },
        legend: (legendPosition === "none" || colorField) ? false : { position: legendPosition ? legendPosition : "bottom" },
        xAxis: waterfallAxisConfig.xAxisHide ? false : {
            label: {
                formatter: (text) => {
                    const { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(report, xField)
                    let formattedText = getPropertyText({ text, applyOn: "axis", isApplyClicked, fieldType, formatField })
                    return formattedText;
                },
                rotate: waterfallAxisConfig.xAxisRotate / 57,
                style: {
                    fill: waterfallAxisConfig.xAxisLabelColor,
                    fontSize: waterfallAxisConfig.xAxisFontSize,
                },
                autoEllipsis: true
            },
            title: !showAxisName ? null : {
                text: xField,
                offset: 20
            },
            verticalFactor: (waterfallAxisConfig.xAxisRotate < 0 || waterfallAxisConfig.xAxisRotate > 0) ? -5 : -2,
            ...waterfallAxisConfig.xAxisRange
        },
        yAxis: waterfallAxisConfig.yAxisHide ? false : {
            label: {
                formatter: (text) => {
                    const { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(report, yField)
                    let formattedText = getPropertyText({ text, applyOn: "axis", isApplyClicked, fieldType, formatField })
                    return formattedText;
                },
                rotate: waterfallAxisConfig.yAxisRotate,
                style: {
                    fill: waterfallAxisConfig.yAxisLabelColor,
                    fontSize: waterfallAxisConfig.yAxisFontSize
                }
            },
            title: !showAxisName ? null : {
                text: yField
            },
            ...waterfallAxisConfig.yAxisRange
        },
        columnWidthRatio: 0.5,
        waterfallStyle: {
            width: 100,
            height: 100
        },
        color: null
    }

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

    if (waterfallAxisConfig.xAxisRotate > 0 && !waterfallAxisConfig.xAxisHide) {
        config.xAxis = { ...config.xAxis, verticalFactor: -5, verticalLimitLength: 80 }
    }

    if (colorField) {
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
            config.colorField = fieldName
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
            config.legend = false
        }
    }

    if (formatColor?.formatColorStyle === "gradient") {
        if (formatColor.minimum && formatColor.maximum) {
            let fieldName = getFieldName(formatColor?.formatColorField, report)
            if (fieldName) {
                const fieldData = getFieldData(data, fieldName);
                const uniqueData = [...new Set(fieldData)];
                let color = generateColorRange(formatColor?.minimum, formatColor?.maximum, uniqueData.length)
                if (formatColor?.enableSteps) {
                    color = generateColorsFromRange(uniqueData, formatColor);
                }
                config.color = color
                config.legend = false
            }
        }
    }

    if (themeColors?.length) {
        config.theme = 'custom-theme';
    }

    const onChartClick = (args, plot) => {
        const { x, y } = args;
        let isTotaBar = false
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
                if (barData[key] === 'Total') {
                    isTotaBar = true;
                }
                if ([labelField, colorField, xField, yField, tooltipField].includes(key)) {
                    payload.push({ field: key, value: barData[key] });
                }
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
            if (isTotaBar) return;
            dispatch(setMenuData({ reportId, menu: menuData }));
        }
    };


    return (
        <div style={{ height: `${height}px` }}>
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
            <Waterfall
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
};


export default AntWaterFallChart