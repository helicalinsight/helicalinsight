import { useSelector } from "react-redux";
import NodeColorPicker from "./nodeColorPicker";
import { Divider } from "antd";
import { hcrCanvasPaneHelperMethods } from "./hcrCanvasPaneHelperMethods";
const { getHcrPropertyTooltipInfo } = hcrCanvasPaneHelperMethods


export default function NodeBorders({ directions = ['Top', 'Bottom', 'Left', 'Right'], onPropertyChange, InputNumberFiled, SelectField, nodeValues }) {
    const designerProperties = useSelector(state => state.cannedReports.present?.hCROldConfigurations?.HCR?.HCR?.designerProperties || {});
    const { lineStyle: lineStyles } = designerProperties;
    const HcrPropertiesConfiguration = useSelector(state => state.cannedReports.present?.hCROldConfigurations?.HcrPropertiesConfiguration || {});
    const { tooltipInfo = {} } = HcrPropertiesConfiguration || {};
    const {
        tooltip_stroke = {},
        tooltip_style = {},
        tooltip_color = {},
        tooltip_borderBottom,
        tooltip_borderTop,
        tooltip_borderLeft,
        tooltip_borderRight
    } = tooltipInfo || {}

    const borderTootips = {
        Top: tooltip_borderTop,
        Bottom: tooltip_borderBottom,
        Left: tooltip_borderLeft,
        Right: tooltip_borderRight
    }

    return directions.map(direction => {
        return <div className="property-group-wrapper">
            <div className="property-group">{getHcrPropertyTooltipInfo({ label: direction, tooltip: borderTootips[direction] || "" })}   </div>
            <div className="border-properties">
                <InputNumberFiled
                    label={<div className="property-label">{getHcrPropertyTooltipInfo({ label: "Stroke", tooltip: tooltip_stroke })}</div>}
                    value={nodeValues?.borders[direction]?.stroke}
                    width={110}
                    onKeyDown={(e) => {
                        if (e.key === "-" || e.key === "e") {
                            e.preventDefault();
                        }
                    }}
                    min={0}
                    onChange={(value) => {
                        onPropertyChange({
                            key: 'borders', value: {
                                ...nodeValues.borders,
                                [direction]: {
                                    ...(nodeValues?.borders[direction] || {}),
                                    stroke: value
                                },
                            }
                        });
                    }}
                />
                <SelectField
                    label={<div className="property-label">{getHcrPropertyTooltipInfo({ label: "Style", tooltip: tooltip_style })}</div>}
                    value={nodeValues?.borders[direction]?.style}
                    options={lineStyles.map(lineStyle => {
                        return {
                            label: lineStyle,
                            value: lineStyle
                        }
                    })}
                    width={110}
                    onChange={(value) => {
                        onPropertyChange({
                            key: 'borders', value: {
                                ...nodeValues?.borders,
                                [direction]: {
                                    ...(nodeValues?.borders[direction] || {}),
                                    style: value
                                },
                            }
                        });
                    }}
                />
                <NodeColorPicker onPropertyChange={onPropertyChange} borders={nodeValues.borders} clrVal={nodeValues.borders[direction]?.color} keyWord="color" label={'Color'} direction={direction} tooltip={tooltip_color} />
            </div>
            <Divider className="group-divider" />
        </div>
    })
}