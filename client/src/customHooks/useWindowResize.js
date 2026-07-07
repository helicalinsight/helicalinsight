import { useLayoutEffect, useState } from 'react';

const useWindowResize = (delay = 100) => {
    const [resizing, setResizing] = useState(false)
    const handleResize = () => {
        setResizing(false)
    };
    useLayoutEffect(() => {
        let resizeTimer;
        const handleResizeDebounced = () => {
            clearTimeout(resizeTimer);
            setResizing(true)
            resizeTimer = setTimeout(handleResize, delay);
        };
        window.addEventListener('resize', handleResizeDebounced);
        return () => {
            window.removeEventListener('resize', handleResizeDebounced);
        };
    }, [delay]);
    return resizing;
};

export default useWindowResize;
