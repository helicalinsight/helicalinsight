import { cloneDeep, isEmpty } from 'lodash';
import { getAntChartTheme, registerAntdTheme, subVizTypes } from '../ant-charts/ant-utils';
import { HiCard } from '../ant-charts/components';
import { getPropertyElement, getPropertyStyle } from '../utils/utillities';
import { getFieldInfo } from '../utils/grid-chart-utils';
import { getCanvasStyles } from '../../utils/utilities';
import { defaultColorPaletteSchemes } from '../ant-charts/constants';
import CellCard from '../cell-card';
import Toolbar from '../toolbar';
import { Row } from 'antd';

const KPICard = (props = {}) => {
    let {
        data: chartData,
        metadata,
        fields,
        marksList,
        interactiveMode,
        reportId,
        report,
        chartColorPalette,
        isPrintMode
    } = props;
    let data = cloneDeep(chartData);
    let canvasFields = fields.filter((field) => !field.hiddenIncludeInResultSet);
    const { title, subTitle, format, formatColor, bar, axisRange, card, labels, line, chartTheme: { colorPalette }, charts = {}, canvas: canvasProperties = {} } = report?.reportData?.properties || {};
    let { chartData: filteredData, rows, columns, schema, colorField, sizeField, detailField, labelField, measuresLabelFields = {}, shapeField, tooltipField } =
        getFieldInfo({ data, metadata, fields: canvasFields, marksList, axisRange });
    data = filteredData
    let { show, value, padding, fontSize } = title;
    let {
        show: subTitleShow,
        value: subTitleValue,
        padding: subTitlePadding,
        fontSize: subTitleFontSize,
    } = subTitle;

    let height;
    let titleStyle = {};
    let subTitleStyle = {};

    let themeColors = getAntChartTheme(colorPalette, { ...chartColorPalette, ...defaultColorPaletteSchemes });

    if (themeColors?.length) {
        registerAntdTheme({ colors: themeColors });
    }

    if (props.chartAreaHeight) {
        height = props.chartAreaHeight - 15;
    }

    if (show && value) {
        titleStyle = getPropertyStyle(title);
        height = height - 15 - fontSize - 2 * padding;
    }

    if (subTitleShow && subTitleValue) {
        subTitleStyle = getPropertyStyle(subTitle);
        height = height - 15 - subTitleFontSize - 2 * subTitlePadding;
    }

    let antContainerStyles = {
        height
    }

    if (canvasProperties?.view) {
        let newContainerStyles = getCanvasStyles({ canvasProperties, chartAreaHeight: height, width: true })
        if (!isEmpty(newContainerStyles)) {
            antContainerStyles = newContainerStyles;
        }
    }

    let rowsMeasures = schema
        .filter((field) => field.type === "measure" && rows.includes(field.name))
        .map((field) => field.name);
    let columnMeasures = schema
        .filter((field) => field.type === "measure" && columns.includes(field.name))
        .map((field) => field.name);
    let markLayers = columnMeasures.length > rowsMeasures.length ? columnMeasures : rowsMeasures;
    let subVizType = "";
    subVizType = marksList[0].subVizType;
    subVizType = subVizTypes.includes(subVizType) ? subVizType : "kpi";

    return (
        <div style={antContainerStyles} className="hr-muze-container">
            <Row justify="end">
                <Toolbar
                    addForwardFilterIPC={props.addForwardFilterIPC}
                    deleteBackwardFilterIPC={props.deleteBackwardFilterIPC}
                    refreshFiltersIPC={props.refreshFiltersIPC}
                    reportId={reportId}
                    getApi={props.getApi}
                />
            </Row>
            <div className="title-subtitle-container">
                {title?.position === "top" && getPropertyElement(titleStyle, title)}
                {subTitle?.position === "top" && getPropertyElement(subTitleStyle, subTitle)}
            </div>
            <HiCard
                markLayers={markLayers}
                data={data}
                report={report}
                formatColor={formatColor}
                subVizType={subVizType}
                reportId={reportId}
                detailField={detailField}
                interactiveMode={interactiveMode}
                addFilter={props.addFilter}
                getApi={props.getApi}
                chartAreaHeight={props.chartAreaHeight}
                chartAreaWidth={props.chartAreaWidth}
                format={format}
                card={card}
                isPrintMode={isPrintMode}
                fields={fields}
                canvasProperties={canvasProperties}
            />
            <div className="title-subtitle-container">
                {title?.position === "bottom" && getPropertyElement(titleStyle, title)}
                {subTitle?.position === "bottom" && getPropertyElement(subTitleStyle, subTitle)}
            </div>
            {interactiveMode && (
                <CellCard
                    reportId={reportId}
                    addFilter={props.addFilter}
                    report={report}
                    format={format}
                    getApi={props.getApi}
                />
            )}
        </div>
    )
}

export default KPICard