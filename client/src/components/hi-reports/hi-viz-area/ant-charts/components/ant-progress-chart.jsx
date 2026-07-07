import { RingProgress } from '@ant-design/plots';
import { Row, Tooltip } from 'antd';
import React from 'react';
import { useDispatch } from 'react-redux';
import { getFieldDisplayName } from '../../../../../utils/utilities';
import { generateColorRange, generateColorsFromRange, getFieldData, getFieldName, getGridChartColorFormatScheme } from '../../../hi-editing-area/utils/property-utils';
import { getPropertyElement } from '../../utils/utillities';
import VizTooltip from '../../viz-tooltip';
import { getProgressChartProperties } from '../ant-utils';
import AntBulletChart from './ant-bullet-chart';
import AntGaugeChart from './ant-gauge-chart';


const AntProgressChart = (props) => {
    const { data = [],
        report = {},
        columnMeasures,
        rowsMeasures,
        chartAreaWidth,
        height,
        colorField,
        labelField,
        dimensions,
        titleStyle,
        subTitleStyle,
        chartAreaHeight,
        themeColors
    } = props || {}
    const {
        properties: {
            progress: {
                chartType = "ring",
                target,
                // showTargetAndValue,
                statisticType = 'percentage',
                ...restProgessProperties
            } = {},
            formatColor,
            title,
            subTitle,
            labels,
            format
        } = {}, fields } = report || {}
    let measures = [...columnMeasures, ...rowsMeasures];
    let isPercentageStatistic = statisticType === 'percentage';
    const responsiveTextSize = Math.min(chartAreaWidth, chartAreaHeight) / 18;
    let progressRangeColors = []
    let progressRange = ['20%', '40%', '60%', '80%', '100%'];
    if (progressRange.some((item) => restProgessProperties[item])) {
        progressRangeColors = progressRange.map((r) => restProgessProperties[r])
    }
    let targetField = null;
    if (typeof target === 'string') {
        targetField = getFieldDisplayName(fields?.find((field) => field?.id === target) || {});
    }
    const dispatch = useDispatch()
    let { min, max, minField, maxField, progressColorRange = null } = getProgressChartProperties(data, measures, { colorField, targetField, progressRangeColors, themeColors })

    let percent = min / max
    let isNumericTarget = typeof target === 'number' && target > 0;
    if (typeof target === 'number' && target && target > 0) {
        percent = min / target
    }

    if (formatColor?.formatColorStyle === "fieldValue") {
        const fieldName = getFieldName(formatColor?.formatColorField, report);
        const domainData = getFieldData(data, fieldName);
        const colors = getGridChartColorFormatScheme(formatColor, domainData);
        if (fieldName && colorField) {
            progressColorRange.color = colors;
        }
    }
    if (formatColor?.formatColorStyle === "gradient") {
        if (formatColor?.minimum && formatColor?.maximum) {
            const fieldName = getFieldName(formatColor?.formatColorField, report);
            let colors = generateColorRange(
                formatColor?.minimum,
                formatColor?.maximum,
                data.length
            );
            const fieldData = getFieldData(data, fieldName);
            if (formatColor?.enableSteps) {
                colors = generateColorsFromRange(fieldData, formatColor);
            }
            if (fieldName && colorField) {
                progressColorRange.color = colors;
            }
        }
    }

    const getStatisticTooltip = (...args) => {
        const { percent } = args[2];
        let record = { [minField]: min, [maxField]: max }
        return (
            <Tooltip
                title={() => (
                    <VizTooltip data={record} report={report} format={format} dispatch={dispatch} />
                )}
            >
                <div style={{ fontSize: `${responsiveTextSize}px` }}>
                    {isPercentageStatistic ? <span>{(percent * 100).toFixed(2)}%</span> :
                        <span>
                            <span>{min}</span>
                            <span>/</span>
                            <span>{isNumericTarget ? target : max}</span>
                        </span>
                    }
                </div>
            </Tooltip>
        )
    }

    const listProps = {
        ...props,
        percent,
        min,
        max,
        minField,
        maxField,
        target,
        labelField,
        dimensions,
        progressColorRange,
        title,
        subTitle,
        labels,
        dispatch,
        // showTargetAndValue,
        isPercentageStatistic,
        isNumericTarget,
        getStatisticTooltip
    }

    switch (chartType) {
        case "ring":
            const config = {
                height: height,
                width: chartAreaWidth,
                autoFit: false,
                percent: percent,
                color: ['#5B8FF9', '#E8EDF3'],
                statistic: {
                    content: {
                        style: {
                            pointerEvents: 'all',
                        },
                        customHtml: getStatisticTooltip
                    },
                }
            };
            return (
                <Row justify={"center"} height={chartAreaHeight}>
                    <div style={{ height: `${height}px` }}>
                        <div className="title-subtitle-container">
                            {title?.position === "top" && getPropertyElement(titleStyle, title)}
                            {subTitle?.position === "top" && getPropertyElement(subTitleStyle, subTitle)}
                        </div>
                        <RingProgress {...config} />
                        <div className="title-subtitle-container">
                            {title?.position === "bottom" && getPropertyElement(titleStyle, title)}
                            {subTitle?.position === "bottom" && getPropertyElement(subTitleStyle, subTitle)}
                        </div>
                    </div>
                </Row>
            );
        case "gauge":
            return (
                <AntGaugeChart {...listProps} />
            );
        case "bullet":
            return (
                <AntBulletChart {...listProps} />
            );
        default:
            return (
                <h3>No Chart Found!</h3>
            )
    }
}

export default AntProgressChart