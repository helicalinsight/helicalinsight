import { Gauge } from '@ant-design/plots';
import React from 'react';
import { getClosestRoundNumber } from '../../../hi-editing-area/utils/property-utils';
import { getPropertyElement } from '../../utils/utillities';

const AntGaugeChart = (props) => {
    const {
        percent,
        progressColorRange = null,
        min,
        max,
        isPercentageStatistic,
        titleStyle,
        subTitleStyle,
        title,
        subTitle,
        height,
        target,
        isNumericTarget,
        report,
        getStatisticTooltip = () => { }
    } = props || {}
    const { format } = report.properties || {}
    let closestRoundNumber = null
    if (min === max) {
        closestRoundNumber = max
    } else {
        closestRoundNumber = getClosestRoundNumber(max)
    }
    let colorRange = {
        color: "#6294fa",
    }
    if (progressColorRange) {
        colorRange = progressColorRange
    }
    const config = {
        percent: percent,
        range: colorRange,
        indicator: {
            pointer: {
                style: {
                    stroke: '#D0D0D0',
                },
            },
            pin: {
                style: {
                    stroke: '#D0D0D0',
                },
            },
        },
        axis: {
            label: {
                formatter(v) {
                    if (isNumericTarget && !isPercentageStatistic) {
                        return Number(v) * target
                    }
                    if (typeof target === 'string' && !isPercentageStatistic) {
                        return Number(v) * closestRoundNumber;
                    }
                    return Number(v) * 100;
                },
            },
            subTickLine: {
                count: 3,
            },
        },
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
        <div style={{ height: `${height}px` }}>
            <div className="title-subtitle-container">
                {title?.position === "top" && getPropertyElement(titleStyle, title)}
                {subTitle?.position === "top" && getPropertyElement(subTitleStyle, subTitle)}
            </div>
            <Gauge {...config} />
            <div className="title-subtitle-container">
                {title?.position === "bottom" && getPropertyElement(titleStyle, title)}
                {subTitle?.position === "bottom" && getPropertyElement(subTitleStyle, subTitle)}
            </div>
        </div>
    )

}

export default AntGaugeChart