import { useSelector } from "react-redux";
import { hcrCanvasPaneHelperMethods } from "../hcrCanvasPaneHelperMethods";
const { getHcrPropertyTooltipInfo } = hcrCanvasPaneHelperMethods

export default function NodeLineStyles({ hideLineStyleTitle, InputNumberFiled, SelectField, nodeValues, onPropertyChange }) {
    const designerProperties = useSelector(state => state.cannedReports.present?.hCROldConfigurations?.HCR?.HCR?.designerProperties || {});
    const { lineStyle: lineStyles } = designerProperties;

    const HcrPropertiesConfiguration = useSelector(state => state.cannedReports.present?.hCROldConfigurations?.HcrPropertiesConfiguration || {});
    const { tooltipInfo = {} } = HcrPropertiesConfiguration || {};
    const {
        tooltip_stroke,
        tooltip_style
    } = tooltipInfo || {};

    return <div className="property-group-wrapper">
        {!hideLineStyleTitle && <div className="property-group">Line Styles</div>}
        <div className="common-group">
            <InputNumberFiled
                label={<div className="property-label">{getHcrPropertyTooltipInfo({ label: "Stroke", tooltip: tooltip_stroke })}</div>}
                value={nodeValues.lineStyles.stroke}
                width={110}
                onChange={(value) => {
                    onPropertyChange({ key: 'lineStyles', value: { ...nodeValues.lineStyles, stroke: value } });
                }}
            />
            <SelectField
                label={<div className="property-label">{getHcrPropertyTooltipInfo({ label: "Style", tooltip: tooltip_style })}</div>}
                value={nodeValues.lineStyles.style}
                options={lineStyles.map(lineStyle => {
                    return {
                        label: lineStyle,
                        value: lineStyle
                    }
                })}
                width={110}
                onChange={(value) => {
                    onPropertyChange({ key: 'lineStyles', value: { ...nodeValues.lineStyles, style: value } });
                }}
            />
        </div>
    </div>
}