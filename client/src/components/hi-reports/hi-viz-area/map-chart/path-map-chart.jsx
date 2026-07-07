
import { PathMap } from "@ant-design/maps";
import { useEffect, useMemo, useRef, useState } from "react";
import { useDispatch } from "react-redux";
import { setMenuData, updateMapChartProperties } from "../../../../redux/actions/hreport.actions";
import { constructGeoJsonData, getMapChartLegend, getMapChartTooltip, getMapColorFieldScale, getMapPropertiesPayload } from "../utils/utillities";

const PathMapChart = (props) => {
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
        Notify
    } = props;
    let maximumSize = 1;
    if (sizeField) {
        let sizeValues = data?.map((item) => item[sizeField]);
        maximumSize = Math.max(...sizeValues);
    }
    const [mapData, setMapData] = useState({})
    const { format } = report?.reportData?.properties || {}
    const chartRef = useRef(null)
    const constructConfig = (data, colorField, sizeField, labelField, maximumSize) => {
        let configuration = {
            map: {
                type: mapType,
                style: mapStyle,
                center: mapCenter,
                zoom: mapZoom,
                token: token,
                preserveDrawingBuffer: true
            },
            preserveDrawingBuffer: true,
            source: {
                data: data || {},
                parser: {
                    type: 'geojson'
                }
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
            zoom: {
                position: 'bottomright',
            },
            legend: getMapChartLegend(report?.reportData?.properties, report, colorField),
            onReady: (chart) => {
                chart?.on("moveend", (event) => {
                    const propertiesPayload = getMapPropertiesPayload(event, chart);
                    dispatch(updateMapChartProperties({ properties: propertiesPayload }));
                })
                chart.on('click', (args) => {
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

        return configuration;
    }

    const config = useMemo(
        () => constructConfig(mapData, colorField, sizeField, labelField, maximumSize),
        [data, mapData, mapStyle, format, mapCenter, mapZoom]
    );

    useEffect(() => {
        if (data?.length && geoGraphicRoleFields?.length) {
            let generatedMapData = constructGeoJsonData(data, geoGraphicRoleFields, 'line', dispatch, isLongLatMapType, { labelField, report })
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
        <PathMap {...config} chartRef={chartRef} />
    );
};

export default PathMapChart