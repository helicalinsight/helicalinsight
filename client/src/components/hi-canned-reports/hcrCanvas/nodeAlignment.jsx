import { useSelector } from "react-redux";
import { hcrCanvasPaneHelperMethods } from "./hcrCanvasPaneHelperMethods";
const { getHcrPropertyTooltipInfo } = hcrCanvasPaneHelperMethods

const props = ['x', 'y', 'width', 'height']

export default function NodeAlignment({ InputNumberFiled, nodeValues, onPropertyChange }) {
    const HcrPropertiesConfiguration = useSelector(state => state.cannedReports.present?.hCROldConfigurations?.HcrPropertiesConfiguration || {});
    const { tooltipInfo = {} } = HcrPropertiesConfiguration || {};
    const {
        tooltip_alignmentHeight = {},
        tooltip_alignmentWidth = {},
        tooltip_alignmentX = {},
        tooltip_alignmentY = {},
    } = tooltipInfo || {}

    const tooltips = {
        x: tooltip_alignmentX,
        y: tooltip_alignmentY,
        width: tooltip_alignmentWidth,
        height: tooltip_alignmentHeight,
    }
    return <div className="alignment-properties">
        {props.map(ele => {
            return <InputNumberFiled
                label={<div className="property-label">{getHcrPropertyTooltipInfo({ label: ele[0].toUpperCase() + ele?.slice(1), tooltip: tooltips[ele] })}{ }</div>}
                value={nodeValues[ele]}
                width={110}
                onKeyDown={(e) => {
                    if (e.key === "-" || e.key === "e") {
                        e.preventDefault();
                    }
                }}
                min={0}
                onChange={(value) => {
                    onPropertyChange({ key: ele, value })
                }}
            />
        })}
    </div>
}