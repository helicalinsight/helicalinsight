import { useEffect } from 'react';
import { useMap, ZoomControl } from 'react-leaflet';
import { updateMapChartProperties } from '../../../../../redux/actions/hreport.actions';

const ZoomControlOSM = (props) => {
    const { dispatch, chartAreaHeight, chartAreaWidth } = props || {}
    const map = useMap();
    useEffect(() => {
        const updateZoom = () => {
            const { lng, lat } = map.getCenter();
            dispatch(updateMapChartProperties({
                properties: {
                    zoom: map.getZoom(),
                    longitude: parseFloat(lng?.toFixed(6)),
                    latitude: parseFloat(lat?.toFixed(6))
                }
            }));
        };

        const updateLatLong = () => {
            const { lng, lat } = map.getCenter();
            dispatch(updateMapChartProperties({ properties: { longitude: parseFloat(lng?.toFixed(6)), latitude: parseFloat(lat?.toFixed(6)) } }));
        }

        updateZoom();
        map.on("zoomend", updateZoom);
        map.on("moveend", updateLatLong)
        return () => {
            map.off("zoomend", updateZoom);
            map.off("moveend", updateLatLong)
        }
    }, [map]);

    useEffect(() => {
        map.invalidateSize()
    }, [chartAreaHeight, chartAreaWidth, map]);

    return (
        <ZoomControl position="bottomright" />
    )
}

export default ZoomControlOSM