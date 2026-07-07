import React, { useLayoutEffect, useState } from 'react';

export const  useWindowSize = () => {
    const [size, setSize] = useState([0, 0]);
    useLayoutEffect(() => {
        function updateSize() {
            setSize([window.innerWidth, window.innerHeight+20]);
        }
        window.addEventListener('resize', updateSize);
        updateSize();
        return () => window.removeEventListener('resize', updateSize);
    }, []);
    return size;
}

// function ShowWindowDimensions(props) {
//     const [width, height] = useWindowSize();
//     return <span>Window size: {width} x {height}</span>;
// }