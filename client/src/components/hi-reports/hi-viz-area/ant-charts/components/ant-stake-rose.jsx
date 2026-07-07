import { RadialBar, Rose } from '@ant-design/plots';
import { Row } from 'antd';
import { isEmpty } from 'lodash';
import React from 'react';
import { useDispatch } from 'react-redux';
import { setMenuData } from '../../../../../redux/actions/hreport.actions';
import { getFieldDisplayName } from '../../../../../utils/utilities';
import { generateColorRange, generateColorsFromRange, getEachFieldColor, getFieldData, getFieldName, getFiledValueColor, getHTMLColorFormat } from '../../../hi-editing-area/utils/property-utils';
import CellCard from '../../cell-card';
import Toolbar from '../../toolbar';
import { checkIsContinousField, getPropertyElement, getPropertyFieldInfo, getPropertyText } from '../../utils/utillities';
import { antdChartCategoryColors, antdChartContinuousColors, divStyles, getChartTooltipTemplate, setStyles } from '../ant-utils';
import ErrorArea from './ant-error-area';
import notify from '../../../../hi-notifications/notify';


const AntStakedRoseChart = (props = {}) => {
    const {
        data,
        report,
        rowsMeasures = [],
        columnMeasures = [],
        dimensions = [],
        colorField,
        labelField,
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

    let { title, subTitle, labels, formatColor, legend: { legendPosition } = {}, } = report?.reportData?.properties || {};
    let labelsColor = isEmpty(labels?.labelsColor) ? null : getHTMLColorFormat(labels?.labelsColor);

    if (['rose'].includes(subVizType) && !dimensions?.length) {
        const msg = "At least 1 dimension is required to generate Rose chart";
        Notify.warning({ type: "Frontend", message: msg });
        return <ErrorArea message={msg} />;
    }

    let yField = "";
    if (columnMeasures.length > 0) {
        yField = columnMeasures[0];
    }

    if (rowsMeasures.length > 0) {
        yField = rowsMeasures[0];
    }

    let colorFieldKey = subVizType === "rose" ? "seriesField" : "colorField"

    const checkIsStack = () => {
        if (!colorField) return false;
        return colorField !== dimensions[0]
    }

    const config = {
        data,
        xField: dimensions[0],
        yField,
        tooltip: !showTooltip ?
            false : {
                shared: false,
                customContent: function (x, r) {
                    if (r && r.length > 0) {
                        const { data = {} } = r[0];
                        const container = document.createElement("div");
                        let tooltipData = Object.entries(data || []);
                        tooltipData.forEach((e) => {
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
                            return getChartTooltipTemplate({ data: tooltipData, tooltipTemplate, report, Notify });
                        }
                        return container;
                    }
                },
            },
        legend: (legendPosition === "none" || !colorField) ? false : { position: legendPosition ? legendPosition : "bottom" },
        isStack: checkIsStack(),
        label: false
    }

    if (colorField) {
        config[colorFieldKey] = colorField;
        config.interactions = [{ type: "element-active" }, { type: "element-highlight" }];
        let actualColorField = fields.find(field => getFieldDisplayName(field) === colorField);
        if (checkIsContinousField(actualColorField)) {
            config.color = themeColors?.length ? themeColors : antdChartContinuousColors
        } else {
            config.color = themeColors?.length ? themeColors : antdChartCategoryColors
        }
    }

    if (labelField) {
        config.label = {
            fields: [labelField],
            style: {
                fill: labelsColor ? labelsColor : '#000000'
            },
            content: (data) => {
                const { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(report, labelField)
                let text = data[labelField]
                let formattedText = getPropertyText({ text, applyOn: "label", isApplyClicked, fieldType, formatField })
                return formattedText
            },
        }
        if (!["radial bar"].includes(subVizType)) {
            config.label.offset = -15
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

    if (themeColors?.length) {
        config.theme = 'custom-theme';
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
                if (!['name', 'value'].includes(key)) {
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

    const compMap = {
        rose: Rose,
        "radial bar": RadialBar
    }

    const Comp = compMap[subVizType]

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
            <Comp
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

}

export default AntStakedRoseChart