import { Bullet } from '@ant-design/plots';
import React from 'react'
import { getClosestRoundNumber, getHTMLColorFormat, rgbaToHex } from '../../../hi-editing-area/utils/property-utils';
import { getPropertyElement, getPropertyFieldInfo, getPropertyText } from '../../utils/utillities';
import { divStyles, getChartTooltipTemplate, setStyles } from '../ant-utils';
import CellCard from '../../cell-card';
import { setMenuData } from '../../../../../redux/actions/hreport.actions';
import Toolbar from '../../toolbar';
import { Row } from 'antd';

const AntBulletChart = (props) => {
    const {
        data,
        minField,
        maxField,
        target,
        progressColorRange = null,
        labelField,
        report,
        dimensions = [],
        reportId,
        format,
        titleStyle,
        subTitleStyle,
        title,
        subTitle,
        height,
        interactiveMode,
        dispatch,
        drillDown,
        drillThrough,
        Notify,
        showTooltip,
        tooltipTemplate,
        enableTemplate
    } = props || {}
    let dimensionField = dimensions[0];
    let chartData = data.map((item) => {
        return {
            ...item,
            title: dimensionField ? item[dimensionField] : minField,
            ranges: [0, target > 0 ? target : getClosestRoundNumber(item[maxField])],
        }
    })
    let colorRange = null;
    if (progressColorRange) {
        colorRange = progressColorRange?.color?.map((item) => rgbaToHex(item));
    }
    let xField = dimensionField ? dimensionField : 'title';

    const config = {
        data: chartData,
        measureField: minField,
        rangeField: 'ranges',
        targetField: maxField,
        xField: xField,
        color: {
            range: ['#FFbcb8', '#FFe0b0'],
            measure: ['#5B8FF9', '#61DDAA'],
            target: '#39a3f4',
        },
        yAxis: false,
        tooltip: !showTooltip ?
            false : {
                showMarkers: false,
                shared: false,
                customContent: function (x, data) {
                    if (data && data.length > 0) {
                        const container = document.createElement("div");
                        const tootipData = Object.entries(data[0].data);
                        tootipData.forEach((e) => {
                            if (!['mKey', 'tKey', 'rKey', 'title'].includes(e[0])) {
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
        label: false
    };
    if (labelField) {
        config.label = {
            measure: {
                position: 'middle',
                content: (d) => {
                    if (d['mKey'] === minField) {
                        if (dimensions.includes(labelField)) {
                            return d[xField];
                        }
                        return d[minField];
                    }
                }
            },
            target: {
                position: 'middle',
                content: (d) => {
                    if (d['tKey'] === maxField) {
                        return d[maxField];
                    }
                }
            }
        }
    }
    if (colorRange) {
        let index = 0
        config.color.measure = (a) => {
            if (a['mKey'] === minField) {
                let returnColor = ''
                returnColor = colorRange[index]
                index = index + 1
                return returnColor;
            }
        };
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
                if (!['mKey', 'tKey', 'rKey', 'title'].includes(key)) {
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
            <Bullet
                {...config}
                onReady={(plot) => {
                    plot.on("click", (args) => {
                        onChartClick(args, plot);
                    });
                }} />
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

export default AntBulletChart