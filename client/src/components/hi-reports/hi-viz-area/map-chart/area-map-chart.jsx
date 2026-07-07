
import { AreaMap } from "@ant-design/maps";
import { useEffect, useMemo, useState } from "react";
import { useDispatch } from "react-redux";
import { setMenuData } from "../../../../redux/actions/hreport.actions";
import { constructGeoJsonData } from "../utils/utillities";
// import { countries } from "./countries";

const destinationData = [
    { "destination": "India" },
    { "destination": "United States" },
    { "destination": "China" },
    { "destination": "Russia" },
    { "destination": "Brazil" },
    { "destination": "Australia" },
    { "destination": "Canada" },
    { "destination": "Germany" },
    { "destination": "France" },
    { "destination": "United Kingdom" },
    { "destination": "Japan" },
    { "destination": "South Korea" },
    { "destination": "Italy" },
    { "destination": "Mexico" },
    { "destination": "Spain" },
    { "destination": "Netherlands" },
    { "destination": "Switzerland" },
    { "destination": "Sweden" },
    { "destination": "Norway" },
    { "destination": "South Africa" }
]

//  this is not completed yet as we don't have the supported data in as of now


const AreaMapChart = (props) => {
    const dispatch = useDispatch();
    const {
        actualData,
        colorField,
        interactiveMode,
        reportId,
        sizeField,
        report,
        labelField,
        tooltipField,
        geoGraphicRoleFields,
        tooltipFields,
        token
    } = props;
    const { fields = [] } = report || {}
    let maximumSize = 1;
    if (sizeField) {
        let sizeValues = actualData?.map((item) => item[sizeField]);
        maximumSize = Math.max(...sizeValues);
    }
    const { geoJsonData = [] } = report || {}
    const [mapData, setMapData] = useState({})
    const constructConfig = (data, colorField, labelField) => {
        let configuration = {
            map: {
                type: 'mapBox',
                style: 'normal',
                center: [120.19382669582967, 30.258134],
                zoom: 3,
                pitch: 0,
                token: 'pk.eyJ1IjoidGFibGVhdS1lbnRlcnByaXNlIiwiYSI6ImNrY29iZjN6MzA4ZzgycHF6MHd0cXhyaXoifQ.rtfFAKyzk-qMYue5C-8RVA'
            },
            source: {
                data: data || {},
                parser: {
                    type: 'geojson',
                },
            },
            autoFit: true,
            style: {
                opacity: 1,
                stroke: '#72A0C1',
                color: '#fff',
                lineType: 'solid',
                lineOpacity: 1,
                lineWidth: '1px'
            },
            tooltip: {
                items: ['name', ...tooltipFields],
            },
            state: {
                active: true,
                select: true
            },
            label: {
                visible: true,
                field: labelField ?? 'name',
                style: {
                    fill: '#000',
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
            legend: {
                position: 'bottomleft',
            },
            onReady: (chart) => {
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
                            drillDownFilterValues: actualData,
                        };
                        dispatch(setMenuData({ reportId, menu: menuData }));
                    }
                });
            },
        }
        if (tooltipField) {
            configuration.tooltip = {
                items: new Set([...configuration.tooltip.items, tooltipField]),
            }
        }
        if (colorField) {
            configuration.color = {
                field: colorField,
                value: ['#fee5d9', '#fcae91', '#fb6a4a', '#de2d26', '#a50f15'],
                scale: {
                    type: 'quantile',
                },
            }
        }
        // if (sizeField) {
        //     configuration.size = {
        //         field: sizeField,
        //         value: (field) => {
        //             let size = (field[sizeField] / maximumSize) * 25
        //             return size;
        //         },
        //     }
        // }

        return configuration;
    }

    const config = useMemo(
        () => constructConfig(mapData, colorField, labelField),
        [report, actualData, mapData]
    );

    useEffect(() => {
        if (actualData?.length && geoGraphicRoleFields?.length) {
            let generatedMapData = constructGeoJsonData(actualData, [], geoGraphicRoleFields)
            setMapData(generatedMapData)
        }
    }, [actualData])

    return (
        <AreaMap {...config} />
    );
};

export default AreaMapChart