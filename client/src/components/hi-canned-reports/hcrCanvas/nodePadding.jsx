import { useSelector } from "react-redux";
import { hcrCanvasPaneHelperMethods } from "./hcrCanvasPaneHelperMethods";
const { getHcrPropertyTooltipInfo } = hcrCanvasPaneHelperMethods

const directions = ['Top', 'Bottom', 'Left', 'Right'];

export default function NodePadding({ InputNumberFiled, nodeValues, onPropertyChange }) {
    const HcrPropertiesConfiguration = useSelector(state => state.cannedReports.present?.hCROldConfigurations?.HcrPropertiesConfiguration || {});
    const { tooltipInfo = {} } = HcrPropertiesConfiguration || {};
    const {
        tooltip_paddingBottom,
        tooltip_paddingLeft,
        tooltip_paddingRight,
        tooltip_paddingTop,
    } = tooltipInfo || {}

    const tooltips = {
        Top: tooltip_paddingTop,
        Bottom: tooltip_paddingBottom,
        Left: tooltip_paddingLeft,
        Right: tooltip_paddingRight,
    }

    return <div className="padding-properties">
        {directions.map(direction => {
            return <InputNumberFiled
                label={<div className="property-label">{getHcrPropertyTooltipInfo({ label: direction, tooltip: tooltips[direction] })}</div>}
                value={nodeValues?.padding[direction]}
                width={110}
                onKeyDown={(e) => {
                    if (e.key === "-" || e.key === "e") {
                        e.preventDefault();
                    }
                }}
                min={0}
                onChange={(value) => {
                    onPropertyChange({ key: 'padding', value: { ...nodeValues.padding, [direction]: value } })
                }}
            />
        })}
    </div>
}