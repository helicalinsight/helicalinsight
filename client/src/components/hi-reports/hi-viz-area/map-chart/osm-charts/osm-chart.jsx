import randomColor from 'randomcolor'
import { MapContainer, TileLayer } from 'react-leaflet'
import "./osm-charts.scss"
import OSMDotMapLayer from './osm-dot-map-layer'
import OSMHeatMapLayer from './osm-heat-map-layer'
import OSMLineMapLayer from './osm-line-map-layer'
import ZoomControlOSM from './zoom-control-osm'

const MAP_TILES = {
    normal: "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",
    light: "https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png",
    dark: "https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png"
}

const OpenStreetMapChart = (props = {}) => {
    let {
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
        isLongLatMapType,
        mapStyle,
        mapCenter,
        mapZoom,
        mapColors,
        shapeField,
        properties,
        chartAreaHeight,
        chartAreaWidth,
        subVizType,
        dispatch,
        formatColor,
        mapTileURLs = {}
    } = props;

    if (colorField && mapColors?.length < 4) {
        mapColors = randomColor({
            count: data?.length || 20,
            hue: 'random',
        })
    }

    const propsList = {
        report,
        data,
        colorField,
        tooltipField,
        sizeField,
        labelField,
        shapeField,
        geoGraphicRoleFields,
        isLongLatMapType,
        dispatch,
        mapColors,
        tooltipFields,
        reportId,
        interactiveMode,
        formatColor
    }
    const mapURL = mapTileURLs?.[mapStyle] || MAP_TILES[mapStyle]

    return (
        <div className='osm-chart-container'>
            <MapContainer
                center={mapCenter.reverse()}
                zoom={mapZoom}
                style={{ height: "100%", width: "100%" }}
                scrollWheelZoom={true}
                zoomControl={false}

            >
                <TileLayer url={mapURL} key={mapStyle} />
                {
                    {
                        point: <OSMDotMapLayer {...propsList} />,
                        line: <OSMLineMapLayer {...propsList} />,
                        heatmap: <OSMHeatMapLayer {...propsList} />
                    }[subVizType]
                }
                <ZoomControlOSM {...{ dispatch, chartAreaHeight, chartAreaWidth }} />
            </MapContainer>
        </div>
    )
}

export default OpenStreetMapChart