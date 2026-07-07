
import { Row } from "antd";
import { cloneDeep } from "lodash-es";
import qs from "qs";
import randomColor from "randomcolor";
import React, { useRef } from "react";
import { useDispatch } from "react-redux";
import { useHistory } from "react-router-dom";
import "../../../../utils/polyfill/url";
import { getFieldAliasName } from "../../../../utils/utilities";
import { makeid } from "../../../hi-dashboard-designer/utils/common-functions";
import { generateColorRange, generateColorsFromRange, getEachFieldColor, getFieldData, getFieldName, getFiledValueColor, getHTMLColorFormat } from "../../hi-editing-area/utils/property-utils";
import { subVizTypes } from "../ant-charts/ant-utils";
import CellCard from "../cell-card";
import Toolbar from "../toolbar";
import { getFieldInfo, getGridChartColorSchemeFromPalette } from "../utils/grid-chart-utils";
import {
    getMapChartValues,
    getPropertyElement,
    getPropertyStyle
} from "../utils/utillities";
import DotMapChart from "./dot-map-chart";
import HeatMapChart from "./heat-map-chart";
import OpenStreetMapChart from "./osm-charts/osm-chart";
import PathMapChart from "./path-map-chart";
import notify from "../../../hi-notifications/notify";


const MapChart = (props) => {
    let {
        data: chartData,
        metadata,
        fields,
        marksList,
        interactiveMode,
        drillThrough,
        reportId,
        report,
        themeColors = [],
        mode
    } = props;
    let data = cloneDeep(chartData);
    const container = useRef();
    const dispatch = useDispatch();
    let mapColors = ['#faa307', '#e85d04', '#d00000'];
    let { token, mapType, mapTileURLs = {} } = getMapChartValues(dispatch)
    const history = useHistory();
    const { location } = history;
    const { search } = location;
    let parameters = qs.parse(search.split("").splice(1).join(""));
    const isDashboardPrintMode = parameters?.mode === "dashboard" && parameters?.navigatorUserAgent === "print"
    let canvasFields = fields.filter((field) => !field.hiddenIncludeInResultSet);
    const {
        title, subTitle, format, formatColor, axisRange, map: { mapStyle = "normal", longitude, latitude, zoom: mapZoom },
        tooltip: { showTooltip = true, tooltipTemplate = "", enableTemplate = false } = {}
    } = report?.reportData?.properties || {};

    let mapTypeValue = "antdMaps";
    if (mapType === "osm") {
        mapTypeValue = "osm"
    }

    let { chartData: filteredData, colorField, sizeField, labelField, tooltipField, shapeField } =
        getFieldInfo({ data, metadata, fields: canvasFields, marksList, axisRange });
    data = filteredData
    const mapCenter = [longitude, latitude]
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
    const Notify = notify(dispatch);

    const mapChartID = `hr-map-chart-container-${makeid({ hreport: true })}`
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

    if (colorField) {
        const reportField = report?.fields.find((field) => {
            if (field?.alias) {
                return field.alias === colorField;
            }
            return field?.autogen_alias === colorField;
        });
        if (reportField.floatingType === "discrete") {
            mapColors = randomColor({
                count: data?.length || 20,
                hue: 'random',
            });
        }
        if (themeColors?.length) {
            mapColors = getGridChartColorSchemeFromPalette(themeColors, chartData)
        }
    }

    const setMapColors = (data, formatColor) => {
        if (formatColor?.showAll) {
            const singleFiledcolor = getFiledValueColor(data, formatColor);
            if (singleFiledcolor) {
                return singleFiledcolor;
            } else {
                const { value } = data || {}
                if (value) {
                    return getEachFieldColor(formatColor, value);
                }
                return '#546ce6';
            }
        } else {
            return getHTMLColorFormat(formatColor.defaultColor);
        }
    }

    if (formatColor?.formatColorStyle === "fieldValue") {
        const fieldName = getFieldName(formatColor?.formatColorField, report);
        // colorField = fieldName;
        if (colorField && colorField === fieldName) {
            if (fieldName) {
                mapColors = (obj) => setMapColors(obj, formatColor)
            }
        }
        if (!colorField && fieldName) {
            colorField = fieldName;
            mapColors = (obj) => setMapColors(obj, formatColor)
        }
    }
    if (formatColor?.formatColorStyle === "gradient") {
        const fieldName = getFieldName(formatColor?.formatColorField, report);
        if (fieldName) colorField = fieldName;
        let colorsArray = [];
        if (formatColor?.minimum && formatColor?.maximum) {
            const fieldData = getFieldData(data, fieldName);
            colorsArray = generateColorRange(
                formatColor?.minimum,
                formatColor?.maximum,
                data.length
            );
            if (formatColor?.enableSteps) {
                colorsArray = generateColorsFromRange(fieldData, formatColor);
            }
        }
        if (colorField && colorField === fieldName) {
            mapColors = colorsArray
        }
        if (!colorField && fieldName) {
            colorField = fieldName;
            mapColors = colorsArray
        }
    }


    let subVizType = "";
    subVizType = marksList[0].subVizType;
    subVizType = subVizTypes.includes(subVizType) ? subVizType : "point";

    let geoGraphicRoleFields = fields?.map((field) => {
        if (field?.geographicType) return { name: getFieldAliasName(field), geographicRole: field?.geographicType };
        return null
    })?.filter(Boolean);
    const tooltipFields = new Set([...fields?.map((field) => getFieldAliasName(field))]);

    const getErrorMessage = (message) => {
        return (
            <div className="muze-message-container">
                <div className="viz-invalid-state"> {message} </div>
            </div>
        )
    }

    if (!geoGraphicRoleFields?.length) {
        return getErrorMessage('No Geographic role set to any of the dimension')
    }
    let geoRoles = geoGraphicRoleFields?.map(({ geographicRole }) => geographicRole)
    if (geoRoles?.length === 1) {
        let role = geoRoles[0];
        if (role === "lat") {
            return getErrorMessage('Longitude dimension is not present, please provide geographic role')
        }
        if (role === "long") {
            return getErrorMessage('Latitude dimension is not present, please provide geographic role')
        }
    }
    const isLongLatMapType = geoRoles?.includes("lat") && geoRoles?.includes("long")
    if (geoRoles?.length > 1 && !isLongLatMapType) {
        if (geoRoles?.includes("lat") || geoRoles?.includes("long")) {
            return getErrorMessage('One of the long or lat dimensions are not present in the report, please add them in the report or remove the other dimension')
        }
    }

    let propsList = {
        ...props,
        geoGraphicRoleFields,
        tooltipFields,
        token,
        colorField,
        sizeField,
        labelField,
        tooltipField,
        shapeField,
        isDashboardPrintMode,
        isLongLatMapType,
        mapStyle,
        mapType,
        mapCenter,
        mapZoom,
        mapColors,
        mapChartID,
        subVizType,
        dispatch,
        formatColor,
        mapTileURLs,
        Notify,
    }

    return (
        <div ref={container} style={{ height: `${height}px` }} className="hr-muze-container">
            <div className="title-subtitle-container">
                {title?.position === "top" && getPropertyElement(titleStyle, title)}
                {subTitle?.position === "top" && getPropertyElement(subTitleStyle, subTitle)}
            </div>
            <div id={mapChartID} className="hr-map-chart-div">
                {{
                    antdMaps: {
                        point: <DotMapChart {...propsList} />,
                        line: <PathMapChart {...propsList} />,
                        heatmap: <HeatMapChart {...propsList} />
                    }[subVizType],
                    osm: <OpenStreetMapChart {...propsList} />
                }[mapTypeValue]}
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

const areEqual = (prevProps, nextProps) => {
    if (
        prevProps.dataId !== nextProps.dataId ||
        prevProps.chartAreaHeight !== nextProps.chartAreaHeight ||
        prevProps.chartAreaWidth !== nextProps.chartAreaWidth
    ) {
        return false;
    } else {
        return true;
    }
};
export default React.memo(MapChart, areEqual);