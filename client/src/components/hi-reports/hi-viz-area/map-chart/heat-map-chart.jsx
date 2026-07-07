
import { GeographicHeatmap } from "@ant-design/maps";
import { useEffect, useMemo, useRef, useState } from "react";
import { useDispatch } from "react-redux";
import { setMenuData, updateMapChartProperties } from "../../../../redux/actions/hreport.actions";
import { constructGeoJsonData, generateFormatColorRamp, getMapChartLegend, getMapPropertiesPayload } from "../utils/utillities";
import { isEmpty } from "lodash";
import { getHTMLColorFormat } from "../../hi-editing-area/utils/property-utils";


const HeatMapChart = (props) => {
    const dispatch = useDispatch();
    const {
        data,
        interactiveMode,
        reportId,
        report,
        geoGraphicRoleFields,
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
    } = props;
    const [mapData, setMapData] = useState({})
    const { format, labels } = report?.reportData?.properties || {}
    let labelsColor = isEmpty(labels?.labelsColor) ? null : getHTMLColorFormat(labels?.labelsColor);

    const mapLegendPosition = getMapChartLegend(report?.reportData?.properties, report, colorField)
    const chartRef = useRef(null)
    const constructConfig = (data, colorField, sizeField, labelField) => {
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
                    type: 'geojson',
                },
            },
            style: {
                intensity: 2,
                radius: 15,
                opacity: 1,
                colorsRamp: generateFormatColorRamp(mapColors)
            },
            color: "#a9d3f2",
            tooltip: {
                items: [...tooltipFields],
            },
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
            legend: mapLegendPosition,
            onReady: (chart) => {
                chart?.on("moveend", (event) => {
                    const propertiesPayload = getMapPropertiesPayload(event, chart);
                    dispatch(updateMapChartProperties({ properties: propertiesPayload }));
                })
                chart.on('click', (args) => {
                    if (interactiveMode) {
                        let { tooltip: { tooltipComponent: { options: { items = [] } } } } = chart
                        console.log(chart)

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
        if (sizeField) {
            configuration.size = {
                field: sizeField,
                value: [0, 1]
            }
        }

        return configuration;
    }

    const config = useMemo(
        () => constructConfig(mapData, colorField, sizeField, labelField),
        [data, mapData, mapStyle, format, mapCenter, mapZoom]
    );
    useEffect(() => {
        if (data?.length && geoGraphicRoleFields?.length) {
            let generatedMapData = constructGeoJsonData(data, geoGraphicRoleFields, 'heatmap', dispatch, isLongLatMapType, { labelField, report })
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
        <GeographicHeatmap {...config} chartRef={chartRef} />
    );
};

export default HeatMapChart