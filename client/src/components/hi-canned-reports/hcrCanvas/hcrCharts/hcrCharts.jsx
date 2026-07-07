import { hcrCanvasPaneHelperMethods } from '../hcrCanvasPaneHelperMethods';
import HCRPreviewChart from "./hcrPreviewChart";

const { getHCRChartContainerStyles } = hcrCanvasPaneHelperMethods

const HCRCharts = (props) => {
    const { data } = props || {};
    let { width, height, chartType, colorField, padding, chartColors: { orientation = "Vertical" } = {} } = data || {}

    return (
        <div style={getHCRChartContainerStyles(data)}>
            <HCRPreviewChart {...{ width, height, chartType, colorField, padding, orientation }} />
        </div>
    );
}

export default HCRCharts