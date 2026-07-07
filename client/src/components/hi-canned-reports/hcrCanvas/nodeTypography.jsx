import { BoldOutlined, ItalicOutlined, StrikethroughOutlined, UnderlineOutlined } from "@ant-design/icons";
import { Button, Divider } from "antd";
import { useSelector } from "react-redux";
import HorizontalVerticalAlign from "./horizontalVerticalAlign";
import NodeColor from "./nodeColor";
import { hcrCanvasPaneHelperMethods } from "./hcrCanvasPaneHelperMethods";
const { getHcrPropertyTooltipInfo } = hcrCanvasPaneHelperMethods

const directions = ['Top', 'Bottom', 'Left', 'Right'];

export default function NodeTypography({ InputNumberFiled, SelectField, nodeValues, onPropertyChange, getTooltipInfo }) {
    const designerProperties = useSelector(state => state.cannedReports.present?.hCROldConfigurations?.HCR?.HCR?.designerProperties || {});
    const { fontName: fontNames = [] } = designerProperties || [];
    const HcrPropertiesConfiguration = useSelector(state => state.cannedReports.present?.hCROldConfigurations?.HcrPropertiesConfiguration || {});
    const { tooltipInfo = {} } = HcrPropertiesConfiguration || {};
    const {
        tooltip_mode,
        tooltip_fontFamily,
        tooltip_fontSize,
        tooltip_fontStyles,
        tooltip_fontStylesBold,
        tooltip_fontStylesItalics,
        tooltip_fontStylesStrikeThrough,
        tooltip_fontStylesUnderlined,
        tooltip_foreColor,
        tooltip_backColor
    } = tooltipInfo;

    return <>
        <div className="property-group-wrapper">
            <div className="property-group">Fonts</div>
            <div className="group-properties">
                <SelectField
                    label={<div className="property-label" >{getHcrPropertyTooltipInfo({ label: "Family", tooltip: tooltip_fontFamily })}</div>}
                    value={nodeValues?.fontFamily}
                    options={fontNames.map(fontName => {
                        return {
                            label: fontName,
                            value: fontName
                        }
                    })}
                    width={110}
                    onChange={(value) => {
                        onPropertyChange({ key: 'fontFamily', value })
                    }}
                />
                <InputNumberFiled
                    label={<div className="property-label">{getHcrPropertyTooltipInfo({ label: "Font Size", tooltip: tooltip_fontSize })}</div>}
                    value={nodeValues?.fontSize}
                    // width={224}
                    onChange={(value) => {
                        onPropertyChange({ key: 'fontSize', value })
                    }}
                />
            </div>
        </div>
        <Divider className="group-divider" />
        <div className="property-group-wrapper">
            <div className="property-group">{getHcrPropertyTooltipInfo({ label: "Styles", tooltip: tooltip_fontStyles })}</div>
            <div className="property-group-wrapper-div">
                {getHcrPropertyTooltipInfo({
                    label: <Button onClick={() => {
                        onPropertyChange({ key: 'bold', value: !nodeValues?.bold });
                    }}
                        type={nodeValues?.bold ? 'primary' : 'default'} icon={<BoldOutlined />} size={'small'}
                    />, tooltip: tooltip_fontStylesBold
                })}
                {getHcrPropertyTooltipInfo({
                    label: <Button onClick={() => {
                        onPropertyChange({ key: 'italic', value: !nodeValues?.italic });
                    }}
                        type={nodeValues?.italic ? 'primary' : 'default'}
                        icon={<ItalicOutlined />}
                        size={'small'}
                    />,
                    tooltip: tooltip_fontStylesItalics
                })}
                {getHcrPropertyTooltipInfo({
                    label: <Button
                        onClick={() => {
                            onPropertyChange({ key: 'strikeThrough', value: !nodeValues?.strikeThrough });
                        }}
                        type={nodeValues?.strikeThrough ? 'primary' : 'default'}
                        icon={<StrikethroughOutlined />}
                        size={'small'} />,
                    tooltip: tooltip_fontStylesStrikeThrough
                })}
                {getHcrPropertyTooltipInfo({
                    label: <Button
                        onClick={() => {
                            onPropertyChange({ key: 'underLine', value: !nodeValues?.underLine });
                        }}
                        type={nodeValues?.underLine ? 'primary' : 'default'}
                        icon={<UnderlineOutlined />}
                        size={'small'} />,
                    tooltip: tooltip_fontStylesUnderlined
                })}
            </div>
        </div>
        <Divider className="group-divider" />
        <NodeColor nodeValues={nodeValues} onPropertyChange={onPropertyChange} SelectField={SelectField} getTooltipInfo={getTooltipInfo} tooltip_mode={tooltip_mode}
            {...{
                tooltip_foreColor,
                tooltip_backColor
            }} />
        <Divider className="group-divider" />
        <HorizontalVerticalAlign nodeValues={nodeValues} onPropertyChange={onPropertyChange} />
    </>
}