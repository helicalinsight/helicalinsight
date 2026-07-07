import React, { useEffect, useState } from "react";
import "./beat-loader.scss";

const BeatLoader = ({ color, size }) => {
    const [dotCount, setDotCount] = useState(3);
    const delay = 170;

    useEffect(() => {
        const interval = setInterval(() => {
            setDotCount((prevDotCount) => (prevDotCount + 1) % 4);
        }, delay);

        return () => clearInterval(interval);
    }, []);

    const dots = Array.from({ length: 3 }, (_, index) => (
        <div
            key={index}
            className={`dot ${index === dotCount ? "active" : ""}`}
            style={{ backgroundColor: color || "#096DD9", width: size || 7, height: size || 7 }}
            data-testid="dots"
        />
    ));

    return <div className="beat-loader" data-testid="beat-loader-id">{dots}</div>;
};

export default BeatLoader;
