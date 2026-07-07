
import { DotMap } from "@ant-design/maps";
import { isEmpty } from "lodash";
import React, { useEffect, useMemo, useRef, useState } from 'react';
import { useDispatch } from "react-redux";
import { setMenuData, updateMapChartProperties } from "../../../../redux/actions/hreport.actions";
import { getHTMLColorFormat } from "../../hi-editing-area/utils/property-utils";
import { constructGeoJsonData, getMapChartLegend, getMapChartShapeFieldDataConfig, getMapChartTooltip, getMapColorFieldScale, getMapPropertiesPayload, getValueForMapShapeField, registerMapChartImages } from "../utils/utillities";

const DotMapChart = (props) => {
    const dispatch = useDispatch();
    const {
        data,
        interactiveMode,
        reportId,
        report,
        geoGraphicRoleFields,
        tooltipField,
        colorField,
        sizeField,
        labelField,
        tooltipFields,
        token,
        isLongLatMapType,
        mapStyle,
        mapType,
        mapCenter,
        mapZoom,
        mapColors,
        shapeField,
        properties,
        Notify
    } = props;
    let maximumSize = 1;
    if (sizeField) {
        let sizeValues = data?.map((item) => item[sizeField]);
        maximumSize = Math.max(...sizeValues);
    }
    const [mapData, setMapData] = useState({});
    const mapLegendProperties = getMapChartLegend(report?.reportData?.properties, report, colorField);
    const { format, labels, map = {}, shape = {} } = report?.reportData?.properties || {};
    let labelsColor = isEmpty(labels?.labelsColor) ? null : getHTMLColorFormat(labels?.labelsColor);
    let { registeredImagesPresent = false } = registerMapChartImages(shapeField, data, shape);
    const chartRef = useRef(null);

    const constructConfig = (mapData, colorField, sizeField, labelField, maximumSize, mapColors, shapeField) => {
        let configuration = {
            map: {
                type: mapType,
                style: mapStyle,
                center: mapCenter,
                zoom: mapZoom,
                token: token,
                preserveDrawingBuffer: true,
            },
            preserveDrawingBuffer: true,
            source: {
                data: mapData ?? {},
                parser: {
                    type: 'geojson',
                },
            },
            style: {
                opacity: 1,
                stroke: '#000',
            },
            color: "#a9d3f2",
            tooltip: getMapChartTooltip(tooltipFields, report),
            state: {
                active: true,
                select: true
            },
            label: {
                visible: true,
                field: labelField ? `${labelField}-formatted` : 'displayText',
                style: {
                    fill: labelsColor ? labelsColor : '#000',
                    opacity: 0.8,
                    fontSize: 10,
                    stroke: '#fff',
                    strokeWidth: 1.5,
                    textAllowOverlap: false,
                    padding: [8, 8],
                },
            },
            zoom: {
                position: 'bottomright',
            },
            legend: mapLegendProperties,
            onReady: (chart) => {
                chart?.on("moveend", (event) => {
                    const propertiesPayload = getMapPropertiesPayload(event, chart);
                    dispatch(updateMapChartProperties({ properties: propertiesPayload }));
                })
                chart?.on('click', (args) => {
                    if (interactiveMode) {
                        let { tooltip: { tooltipComponent: { options: { items = [] } } } } = chart
                        let { point = {} } = args || {}
                        let { x, y } = point;
                        items = items?.map((item) => {
                            return {
                                ...item,
                                field: item?.name
                            }
                        })
                        let menuData = {
                            payload: items,
                            position: { top: y, left: x },
                            drillDownFilterValues: data,
                        };
                        dispatch(setMenuData({ reportId, menu: menuData }));
                    }
                });
            },
        }
        if (tooltipField) {
            configuration.tooltip = getMapChartTooltip(new Set([...configuration.tooltip.items, tooltipField]), report, Notify)
        }
        if (colorField) {
            configuration.color = {
                field: colorField,
                value: mapColors,
                scale: getMapColorFieldScale(report, colorField),
            }
        }
        if (sizeField) {
            configuration.size = {
                field: sizeField,
                value: (field) => {
                    let size = (field[sizeField] / maximumSize) * 25
                    return size;
                },
            }
        }

        if (!shapeField && shape?.mapDefaultShape) {
            configuration.shape = shape.mapDefaultShape;
        }
        if (shapeField) {
            const shapeDataConfig = getMapChartShapeFieldDataConfig({ data, shapeField })
            configuration.shape = {
                field: shapeField,
                value: (shapeData) => {
                    let { isRegisteredImg = false, value } = getValueForMapShapeField(shapeData, shapeField, shape);
                    if (!isRegisteredImg) return shapeDataConfig[value];
                    return value
                }
            }
            if (registeredImagesPresent) {
                configuration.color = '#fff';
            }
        }
        return configuration;
    }


    const config = useMemo(
        () => constructConfig(mapData, colorField, sizeField, labelField, maximumSize, mapColors, shapeField),
        [mapStyle, data, mapData, format, mapZoom, mapCenter]
    );

    useEffect(() => {
        if (data?.length && geoGraphicRoleFields?.length) {
            let generatedMapData = constructGeoJsonData(data, geoGraphicRoleFields, 'point', dispatch, isLongLatMapType, { labelField, report })
            setMapData(generatedMapData)
        }
    }, [data, format])

    useEffect(() => {
        if (chartRef?.current) {
            let map = chartRef.current;
            map?.updateOption(config)
        }
    }, [config])

    return (
        <DotMap {...config} chartRef={chartRef} visible />
    );
};

export default DotMapChart