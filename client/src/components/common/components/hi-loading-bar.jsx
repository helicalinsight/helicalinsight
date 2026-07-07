import { useEffect, useState } from "react";
import HITooltip from "./hi-tooltip";
import { useSelector } from "react-redux";

function LoadingBar({ handleClick = () => { } }) {
    const [info, setInfo] = useState(false);
    let apiTimeout = useSelector((state) => state.app.applicationSettingsData?.serviceSpeedLimitAlert) || 30000;
    const requestTimeoutTitle = "Request taking longer. Click to abort."
    useEffect(() => {
        let timeout = setTimeout(() => {
            setInfo(true)
        }, apiTimeout)
        return () => {
            clearTimeout(timeout)
        }   
    }, [])

    return (
        <div className="loading-bar-main-container">
            <HITooltip title={info ? requestTimeoutTitle : "abort"} >
                <div onClick={handleClick} className={`load-bar-container ${info ? "loading-container-height-auto" : ""}`}>
                    <div className="metadata-loader load-bar">
                        <div className="bar"></div>
                        <div className="bar"></div>
                        <div className="bar"></div>
                    </div>
                </div>
            </HITooltip>
            {info ? <div className="info-bar"><HITooltip title={requestTimeoutTitle}><span>{requestTimeoutTitle}</span></HITooltip></div> : null}
        </div>
    )
};

export default LoadingBar;