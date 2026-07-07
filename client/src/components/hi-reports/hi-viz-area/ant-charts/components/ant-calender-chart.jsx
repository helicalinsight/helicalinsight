import { Heatmap } from '@ant-design/plots';
import React from 'react';

import { Row } from 'antd';
import { isEmpty } from 'lodash';
import { useDispatch } from 'react-redux';
import { setMenuData } from '../../../../../redux/actions/hreport.actions';
import { getFieldDisplayName } from '../../../../../utils/utilities';
import notify from '../../../../hi-notifications/notify';
import { generateColorRange, generateColorsFromRange, getEachFieldColor, getFieldData, getFieldName, getFiledValueColor, getHTMLColorFormat, getPropertyAxisRange } from '../../../hi-editing-area/utils/property-utils';
import CellCard from '../../cell-card';
import Toolbar from '../../toolbar';
import { getActualFieldDataType } from '../../utils/grid-chart-utils';
import { checkIsContinousField, getPropertyElement, getPropertyFieldInfo, getPropertyText } from '../../utils/utillities';
import { constructCalendarChartData, divStyles, getAntChartLegendLabelConfig, getCalendarData, getChartTooltipTemplate, setStyles } from '../ant-utils';
import ErrorArea from './ant-error-area';

const AntCalenderHeatMap = (props = {}) => {
    let {
        data,
        report,
        rowsMeasures = [],
        columnMeasures = [],
        rows = [],
        columns = [],
        dimensions = [],
        colorField,
        labelField,
        sizeField,
        interactiveMode,
        drillDown,
        drillThrough,
        reportId,
        format,
        titleStyle,
        subTitleStyle,
        height,
        fields,
        themeColors,
        tooltipField,
        subVizType,
        showTooltip,
        tooltipTemplate,
        enableTemplate
    } = props || {}
    const dispatch = useDispatch();
    const Notify = notify(dispatch);
    let { title, subTitle, labels, formatColor, legend: { legendPosition } = {}, axisRange } = report?.reportData?.properties || {};
    let labelsColor = isEmpty(labels?.labelsColor) ? null : getHTMLColorFormat(labels?.labelsColor);
    let canvasFields = [...columns, ...rows];

    let fieldsDataTypes = fields.filter((field) => canvasFields.includes(getFieldDisplayName(field))).map((item) => getActualFieldDataType(item)) ?? [];
    // let isDateTypeFieldAvailable = ['date', 'dateTime'].every((item) => fieldsDataTypes.includes(item));
    let isDateTypeFieldAvailable = fieldsDataTypes.every((item) => ['date', 'dateTime'].includes(item));
    let isSingleFieldPresent = canvasFields.length === 1;

    if (!columns.length) {
        const msg = "At least one date/date-time field is required in columns.";
        Notify.warning({ type: "Frontend", message: msg });
        return <ErrorArea message={msg} />;
    }

    if (canvasFields.length > 1 && !rows.length) {
        const msg = "Only 1 field is required in columns and 1 field is required in rows.";
        Notify.warning({ type: "Frontend", message: msg });
        return <ErrorArea message={msg} />;
    }


    if (!isDateTypeFieldAvailable) {
        const msg = "Only Date and DateTime fields are required for Calender chart.";
        Notify.warning({ type: "Frontend", message: msg });
        return <ErrorArea message={msg} />;
    }

    let dateColumn = columns[0];
    let { data: calendarData = '', xField = '', yField = '' } = constructCalendarChartData(data, dateColumn, columns, rows);
    if (!isSingleFieldPresent) {
        xField = dimensions[0]
        yField = rows[0]
    }

    data = getCalendarData(data, xField, yField);

    const { showAxisName } = axisRange || {};
    let axisNameStyles = {
        fontSize: 14,
        fill: '#5f5f5f'
    }

    let weekDays = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday']
    let months = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"]

    const config = {
        data: !isSingleFieldPresent ? data : calendarData,
        autoFit: true,
        xField,
        yField,
        meta: {
            [xField]: {
                type: 'cat'
            },
            [yField]: {
                type: 'cat',
            },
            day: {
                type: 'cat',
            },
            week_day: {
                type: 'cat',
                values: weekDays
            },
            month: {
                type: 'cat',
                values: months
            },
            week: {
                type: 'cat',
            },
            date: {
                type: 'cat',
            }
        },
        tooltip: !showTooltip ?
            false : {
                customContent: function (x, r) {
                    if (r && r.length > 0) {
                        const { data = {} } = r[0];
                        const container = document.createElement("div");
                        let tooltipData = Object.entries(data || []);
                        tooltipData.forEach((e) => {
                            if (!['day', 'month', 'week', 'date', 'week_day'].includes(e[0])) {
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
                            return getChartTooltipTemplate({ data: tooltipData, tooltipTemplate, report, Notify });
                        }
                        return container;
                    }
                },
            },
        interactions: [
            {
                type: 'element-active',
            },
        ],
        xAxis: {
            grid: null,
            title: !showAxisName ? null : {
                text: xField,
                style: axisNameStyles
            },
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
        },
        yAxis: {
            grid: null,
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
        },
        legend:
            legendPosition === "none" ? false : { position: legendPosition ? legendPosition : "bottom", ...getAntChartLegendLabelConfig(report, colorField) },
        color: ["#B8E1FF", "#9AC5FF", "#7DAAFF", "#5B8FF9", "#3D76DD", "#085EC0", "#0047A5", "#00318A", "#001D70"]
    };


    if (colorField) {
        if (themeColors?.length) {
            config.color = themeColors;
        }
        config.colorField = colorField;
    }

    if (sizeField) {
        config.sizeField = sizeField
    }

    if (labelField) {
        config.label = {
            fields: [labelField],
            style: {
                fill: labelsColor ? labelsColor : '#fff'
            },
            content: (data) => {
                const { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(report, labelField)
                let text = data[labelField]
                let formattedText = getPropertyText({ text, applyOn: "label", isApplyClicked, fieldType, formatField })
                return formattedText
            },
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
        }
    }

    if (formatColor?.formatColorStyle === "gradient") {
        if (formatColor.minimum && formatColor.maximum) {
            let fieldName = getFieldName(formatColor?.formatColorField, report)
            if (fieldName) {
                config.colorField = fieldName
                const fieldData = getFieldData(data, fieldName);
                const uniqueData = [...new Set(fieldData)];
                let color = generateColorRange(formatColor?.minimum, formatColor?.maximum, uniqueData.length)
                if (formatColor?.enableSteps) {
                    color = generateColorsFromRange(uniqueData, formatColor);
                }
                let actualColorField = fields.find(field => getFieldDisplayName(field) === fieldName);
                config.color = checkIsContinousField(actualColorField) ? color : color.reverse();
            }
        }
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
                if (rotate || rotate === 0) {
                    config.xAxis = { ...config.xAxis, label: { ...config.xAxis.label, rotate: (rotate / 57), autoEllipsis: true } }
                    if (rotate > 0) {
                        config.xAxis = { ...config.xAxis, verticalFactor: -5, verticalLimitLength: 80 }
                    }
                }
                config.xAxis = { ...config.xAxis, visible: !hide }
                const rangeData = getPropertyAxisRange(field, "antChart")
                if (rangeData) {
                    config.xAxis = { ...config.xAxis, ...rangeData }
                }
                config.xAxis.label.style = { fontSize };
                if (axisLabelColor) {
                    config.xAxis.label.style.fill = axisLabelColor
                } else {
                    config.xAxis.label.style.fill = '#a1a1a1'
                }
            }
            if (config.yField === field.data.name) {
                if (rotate || rotate === 0) {
                    config.yAxis = { ...config.yAxis, label: { ...config.yAxis.label, rotate: (rotate / 57) } }
                }
                config.yAxis.visible = { ...config.yAxis.visible, visible: !hide }
                const rangeData = getPropertyAxisRange(field, "antChart")
                if (rangeData) {
                    config.yAxis = { ...config.yAxis, ...rangeData }
                }
                config.yAxis.label.style = { fontSize };
                if (axisLabelColor) {
                    config.yAxis.label.style.fill = axisLabelColor
                } else {
                    config.yAxis.label.style.fill = '#a1a1a1'
                }
            }
        })
    }


    const onChartClick = (args, plot) => {
        const { x, y } = args;
        const tooltipData = plot.chart.getTooltipItems({ x, y });
        const { data = {} } = tooltipData?.[0] || {};
        if (!isEmpty(data)) {
            const barData = data;
            if (!interactiveMode) {
                return null;
            }
            if (!drillDown && !drillThrough) {
                return null;
            }
            let payload = [];
            Object.keys(barData).map((key) => {
                if (!['day', 'month', 'week', 'date', 'week_day'].includes(key)) {
                    payload.push({ field: key, value: barData[key] });
                }
            });
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
            <Heatmap
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
    )
};

export default AntCalenderHeatMap;