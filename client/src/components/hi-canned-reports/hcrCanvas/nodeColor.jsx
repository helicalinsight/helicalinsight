
import NodeColorPicker from "./nodeColorPicker";

const nodeModes = ['Opaque', 'Transparent'];

export default function NodeColor({ popoverOverlayClass, nodeValues, onPropertyChange, SelectField, getTooltipInfo, tooltip_mode, tooltip_foreColor, tooltip_backColor, labelTooltip = null }) {
    return <div className="property-group-wrapper">
        <div className="property-group">{!labelTooltip ? "Color" : getTooltipInfo({ label: 'Color', tooltip: labelTooltip })}</div>
        <div className="color-properties">
            <SelectField
                label={getTooltipInfo({ label: 'Mode', tooltip: tooltip_mode })}
                value={nodeValues?.mode}
                options={nodeModes.map(mode => {
                    return {
                        label: mode,
                        value: mode
                    }
                })}
                width={110}
                onChange={(value) => {
                    onPropertyChange({ key: 'mode', value });
                }}
            />
            <NodeColorPicker popoverOverlayClass={popoverOverlayClass} onPropertyChange={onPropertyChange} clrVal={nodeValues?.fontFill} keyWord={"fontFill"} label={'Fore Color'} tooltip={tooltip_foreColor} />
            <NodeColorPicker onPropertyChange={onPropertyChange} clrVal={nodeValues?.fill} keyWord={"fill"} label={'Back Color'} tooltip={tooltip_backColor} />
        </div>
    </div>
}