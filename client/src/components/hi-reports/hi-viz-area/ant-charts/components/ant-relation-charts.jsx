import { CirclePacking, Sankey, Sunburst, Treemap } from '@ant-design/plots';
import { Row } from 'antd';
import { isEmpty } from 'lodash';
import React from 'react';
import { useDispatch } from 'react-redux';
import { setMenuData } from '../../../../../redux/actions/hreport.actions';
import { getFieldDisplayName } from '../../../../../utils/utilities';
import notify from '../../../../hi-notifications/notify';
import { generateColorRange, generateColorsFromRange, getEachFieldColor, getFieldData, getFieldName, getFiledValueColor, getHTMLColorFormat } from '../../../hi-editing-area/utils/property-utils';
import CellCard from '../../cell-card';
import Toolbar from '../../toolbar';
import { checkIsContinousField, getPropertyElement, getPropertyFieldInfo, getPropertyText } from '../../utils/utillities';
import { antdChartCategoryColors, antdChartContinuousColors, divStyles, getAntChartLegendLabelConfig, getChartTooltipTemplate, prepareHierarchicalData, setStyles } from '../ant-utils';
import ErrorArea from './ant-error-area';
import { getCanvasFieldDataType } from '../../../../../utils/filter-utils';

const AntRelationCharts = (props = {}) => {
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
        showTooltip,
        tooltipTemplate,
        enableTemplate
    } = props || {}

    const dispatch = useDispatch();
    const Notify = notify(dispatch);
    const { legendPosition } = report?.reportData?.properties.legend;
    let { title, subTitle, labels, formatColor, relationChart: { chartType = 'treemap' } = {} } = report?.reportData?.properties || {};
    let labelsColor = isEmpty(labels?.labelsColor) ? null : getHTMLColorFormat(labels?.labelsColor);

    let measures = [...columnMeasures, ...rowsMeasures]
    let sourceField, targetField, weightField = measures[0];

    const getRawFields = () => {
        let fields = [...dimensions, ...measures]
        if (colorField) fields.push(colorField)
        if (tooltipField) fields.push(tooltipField)
        if (labelField) fields.push(labelField)
        return fields
    }

    const getTooltipFields = (obj) => {
        let result = {}
        let rawFields = getRawFields();
        if (rawFields.length) {
            rawFields.forEach((field) => {
                result[field] = obj[field]
            })
        }
        return result;
    }

    const componentMap = {
        treemap: Treemap,
        circlePacking: CirclePacking,
        sunburst: Sunburst,
        sankey: Sankey,
    }
    let Comp = componentMap[chartType];

    let chartData = []

    if (["sankey"].includes(chartType) && dimensions?.length < 2) {
        const msg = "At least 2 dimensions are required for Sankey chart";
        Notify.warning({ type: "Frontend", message: msg });
        return <ErrorArea message={msg} />;
    }

    if (['treemap', 'circlePacking', 'sunburst'].includes(chartType)) {
        let tempDimensions = [...dimensions];
        if (colorField) {
            const actualColorField = fields.find((field) => getFieldDisplayName(field) === colorField);
            let fieldType = getCanvasFieldDataType(actualColorField);
            if (!checkIsContinousField(fieldType)) {
                tempDimensions.push(colorField);
            }
        }
        tempDimensions = [...new Set(tempDimensions)];
        const formattedData = prepareHierarchicalData(data, tempDimensions, measures);
        chartData = {
            name: 'root',
            children: formattedData
        }
        if (dimensions?.length === 0) {
            chartData.value = data.reduce((a, b) => a + b[measures[0]], 0)
        }
    } else {
        chartData = data
    }

    if (chartType === 'sankey') {
        const sankeyData = [];
        const keys = dimensions;
        data.forEach((d) => {
            keys.reduce((a, b) => {
                if (a && b) {
                    sankeyData.push({
                        source1: d[a],
                        target: d[b],
                        value: d[weightField],
                        ...d
                    });
                }
                return b;
            });
        });
        chartData = sankeyData;
        sourceField = 'source1';
        targetField = 'target';
        weightField = 'value';
    }

    let color = []
    let config = {
        data: chartData,
        tooltip: !showTooltip ?
            false : {
                customContent: function (x, r) {
                    if (r && r.length > 0) {
                        const { data = {}, name = '' } = r[0];
                        const container = document.createElement("div");
                        let tooltipData = Object.entries((['sankey'].includes(chartType) ? getTooltipFields(data) : data?.data) || []);

                        if (['circlePacking'].includes(chartType) && name === 'root') {
                            tooltipData = [[[measures[0]], data?.value]]
                        }
                        tooltipData.forEach((e) => {
                            if (!['name', 'value', 'children'].includes(e[0])) {
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
        legend: (legendPosition === "none" || !colorField) ? false : { position: legendPosition ? legendPosition : "bottom", ...getAntChartLegendLabelConfig(report, colorField) },
        label: false,
        syncViewPadding: true,
        color,
    }

    if (["sankey"].includes(chartType)) {
        delete config.color
        delete config.label
        config.sourceField = sourceField;
        config.targetField = targetField;
        config.weightField = weightField;
        config.rawFields = getRawFields();
    }
    if (['circlePacking'].includes(chartType)) {
        delete config.color
        config.renderer = 'svg'
    }


    if (labelField && !["sankey"].includes(chartType)) {
        config.label = {
            fields: [labelField],
            style: {
                fill: labelsColor ? labelsColor : '#fff'
            },
            content: (data) => {
                if (['circlePacking'].includes(chartType) && data?.childNodeCount && data?.childNodeCount > 0) {
                    return null;
                }
                const { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(report, labelField)
                let text = data[labelField]
                if (!text && chartType === 'sunburst') {
                    text = data?.value ?? ''
                }
                let formattedText = getPropertyText({ text, applyOn: "label", isApplyClicked, fieldType, formatField })
                return formattedText
            },
        }
        if (['sankey'].includes(chartType)) {
            delete config.label
        }
    }

    if (colorField) {
        config.colorField = colorField;
        if (!['circlePacking'].includes(chartType)) {
            config.interactions = [{ type: "element-active" }, { type: "element-highlight" }];
        }
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
            config.legend = { position: legendPosition ? legendPosition : "bottom" }
        }
    }

    if (formatColor?.formatColorStyle === "gradient") {
        if (formatColor.minimum && formatColor.maximum) {
            let fieldName = getFieldName(formatColor?.formatColorField, report)
            if (fieldName) {
                config.colorField = fieldName
                const fieldData = getFieldData(data, fieldName);
                const uniqueData = [...new Set(fieldData)];
                color = generateColorRange(formatColor?.minimum, formatColor?.maximum, uniqueData.length)
                if (formatColor?.enableSteps) {
                    color = generateColorsFromRange(uniqueData, formatColor);
                }
                let actualColorField = fields.find(field => getFieldDisplayName(field) === fieldName);
                config.color = checkIsContinousField(actualColorField) ? color : color.reverse();
                config.legend = { position: legendPosition ? legendPosition : "bottom" }
            }
        }
    }

    if (themeColors?.length) {
        config.theme = 'custom-theme';
    }

    if (chartType === 'sunburst') {
        config.innerRadius = 0.3
        config.hierarchyConfig = {
            field: 'value'
        }
        config.drilldown = null
    }

    const onChartClick = (args, plot) => {
        const { x, y } = args;
        const tooltipData = plot.chart.getTooltipItems({ x, y });
        const { data: tData = {} } = tooltipData?.[0] || {};
        let tempData = ['sankey'].includes(chartType) ? tData : tData?.data
        if (!isEmpty(tempData)) {
            const barData = ['sankey'].includes(chartType) ? getTooltipFields(tempData) : tempData;
            if (!interactiveMode) {
                return null;
            }
            if (!drillDown && !drillThrough) {
                return null;
            }
            if (barData?.name === 'root') return;
            let payload = [];
            Object.keys(barData).map((key) => {
                if (!['name', 'value', 'children'].includes(key)) {
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

export default AntRelationCharts