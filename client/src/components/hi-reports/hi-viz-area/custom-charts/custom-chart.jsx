import * as MapCharts from "@ant-design/maps";
import * as Plots from "@ant-design/plots";
import muze from "@chartshq/muze";
import Muze, { Canvas, Layer } from "@chartshq/react-muze/components";
import * as MuzeConfig from "@chartshq/react-muze/configurations";
import * as AntdComponents from "antd";
import React from 'react';
import { LiveError, LivePreview, LiveProvider } from "react-live";
import { useDispatch } from "react-redux";
import CellCard from "../cell-card";
import Toolbar from "../toolbar";
import { getPreviewStyles, getPropertyElement, getPropertyStyle } from "../utils/utillities";
import { enableInteractivity, getPropertiesConfig, getTooltip, getGridChartLabels, getTableColumns, getGridChartConfig, changePageSize } from "./utilities";
import GridTable from "../s2-charts/s2chart";
import '../table/table.scss'
import { registerAntdTheme } from "../ant-charts/ant-utils";

const MuzeCharts = {
    Muze, Canvas, Layer, ...MuzeConfig, muze
}

const CustomChart = (props) => {
    const dispatch = useDispatch();
    const {
        customChart = {},
        data,
        report,
        interactiveMode,
        reportId,
        fields,
        properties,
        themeColors = [],
    } = props;
    if (themeColors.length) {
        registerAntdTheme({ colors: themeColors });
    }
    const scope = {
        components: { ...MapCharts, ...Plots, ...AntdComponents, ...MuzeCharts, GridTable },
        data,
        report: { ...props, dispatch },
        helperFunctions: { getTooltip, getPropertiesConfig, enableInteractivity, getGridChartLabels, getTableColumns, getGridChartConfig, changePageSize }
    };
    const { title = {}, subTitle = {}, format = {} } = report?.reportData?.properties || {};
    const reportForInteractiveMode = { properties, fields, reportData: report?.reportData }
    let { show, value, fontSize, padding } = title || {};
    let {
        show: subTitleShow,
        value: subTitleValue,
        fontSize: subTitleFontSize,
        padding: subTitlePadding,
    } = subTitle || {};
    let titleStyle = {};
    let subTitleStyle = {};
    if (show && value) {
        titleStyle = getPropertyStyle(title);
    }
    if (subTitleShow && subTitleValue) {
        subTitleStyle = getPropertyStyle(subTitle);
    }
    return (
        <LiveProvider code={customChart.code || ""} scope={scope} >
            <LiveError />
            <Toolbar
                addForwardFilterIPC={props.addForwardFilterIPC}
                deleteBackwardFilterIPC={props.deleteBackwardFilterIPC}
                refreshFiltersIPC={props.refreshFiltersIPC}
                reportId={reportId}
                getApi={props.getApi}
            />
            <div className="title-subtitle-container">
                {title?.position === "top" && getPropertyElement(titleStyle, title)}
                {subTitle?.position === "top" && getPropertyElement(subTitleStyle, subTitle)}
            </div>
            <LivePreview
                style={getPreviewStyles({ ...props, show, value, subTitleShow, subTitleValue, fontSize, padding, subTitleFontSize, subTitlePadding })}
            />
            {interactiveMode && <CellCard reportId={reportId} addFilter={props.addFilter} getApi={props.getApi} report={reportForInteractiveMode} format={format} />}
            <div className="title-subtitle-container">
                {title?.position === "bottom" && getPropertyElement(titleStyle, title)}
                {subTitle?.position === "bottom" && getPropertyElement(subTitleStyle, subTitle)}
            </div>
        </LiveProvider>
    );
};

const areEqual = (prevProps, nextProps) => {
    const { dataId, chartAreaHeight, chartAreaWidth } = prevProps;
    return dataId === nextProps.dataId
        && chartAreaHeight === nextProps.chartAreaHeight
        && chartAreaWidth === nextProps.chartAreaWidth;
};
export default React.memo(CustomChart, areEqual);