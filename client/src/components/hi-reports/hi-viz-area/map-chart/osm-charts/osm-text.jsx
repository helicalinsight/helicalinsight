import { useEffect, useState } from 'react';
import { Circle, Tooltip, useMap } from 'react-leaflet';
import { v4 as uuidv4 } from 'uuid';

const OSMText = ({ coordinates, label, labelsColor }) => {
    const map = useMap();
    const [show, setShow] = useState(false);

    useEffect(() => {
        const update = () => {
            setShow(map.getZoom() >= 4);
        };

        update();
        map.on("zoomend", update);
        return () => map.off("zoomend", update);
    }, [map]);

    if (!show) return null;


    return (
        <Circle
            center={coordinates}
            pathOptions={{ fillColor: '#transparent', stroke: false, fill: false, fillOpacity: 0 }}
            radius={2000}
            key={uuidv4()}
        >
            <Tooltip
                direction="center"
                permanent
                offset={[0, -6]}
            >
                <span
                    style={{
                        fontWeight: 600,
                        textShadow: '1px 1px 2px #fff',
                        color: labelsColor ? labelsColor : '#000',
                    }}>
                    {label}
                </span>
            </Tooltip>
        </Circle>
    )
}

export default OSMText