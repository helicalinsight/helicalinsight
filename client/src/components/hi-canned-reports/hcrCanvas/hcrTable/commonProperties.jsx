import { Checkbox, Select } from "antd";
import { useMemo } from 'react';
import { v4 as uuidv4 } from 'uuid';
import { hcrCanvasPaneHelperMethods } from "../hcrCanvasPaneHelperMethods";

const { getHcrPropertyTooltipInfo } = hcrCanvasPaneHelperMethods

const CommonProperties = (props = {}) => {
    const {
        properties = [],
        excludeProperties = [],
        designerProperties = {},
        nodeValues = {},
        onPropertyChange = () => { },
        onCheckboxChange = () => { },
        tooltipInfo,
        EditorPanels = {}
    } = props || {}
    const { InputFiled, SelectField } = EditorPanels || {};

    const inputFieldKeys = useMemo(() => {
        const keys = {};
        properties?.forEach(prop => {
            if (prop?.type === 'textBox' && prop?.value) {
                keys[prop.value] = uuidv4();
            }
        });
        return keys;
    }, []);

    return (
        properties?.length ? properties
            ?.filter(item => !excludeProperties.includes(item.value))
            ?.map((property) => {
                const { type = "", name, value: pValue, label = "", info = [] } = property || {};
                const tooltipInfoLabel = info?.[0] || '';
                const tooltipValue = tooltipInfo?.[tooltipInfoLabel] || {};
                return {
                    DropDown:
                        <div>
                            <div className="property-label">{getHcrPropertyTooltipInfo({ label: name, tooltip: tooltipValue })}</div>
                            <Select
                                // label={getHcrPropertyTooltipInfo({ label: name, tooltip: tooltipValue })}
                                value={nodeValues?.[pValue] || ""}
                                options={designerProperties?.[pValue]?.map(item => {
                                    return {
                                        label: item,
                                        value: item
                                    }
                                })}
                                // width={"100%"}
                                style={{ width: '100%' }}
                                onChange={(value) => {
                                    onPropertyChange({ key: pValue, value })
                                }}
                                key={uuidv4()}
                                allowClear={nodeValues?.[pValue]}
                            />
                        </div>,
                    textBox: <InputFiled
                        label={getHcrPropertyTooltipInfo({ label: label, tooltip: tooltipValue })}
                        value={nodeValues?.[pValue] || ""}
                        onChange={(value) => {
                            onPropertyChange({ key: pValue, value })
                        }}
                        key={inputFieldKeys[pValue] || uuidv4()}
                    />,
                    checkBox: <><Checkbox
                        style={{ marginLeft: 0 }}
                        onChange={(e) => {
                            onCheckboxChange(e.target.checked, pValue);
                        }}
                        checked={nodeValues[pValue]}
                        key={uuidv4()}
                    >
                        {getHcrPropertyTooltipInfo({ label: label, tooltip: tooltipValue })}
                    </Checkbox><br /></>
                }[type]
            })
            : null
    )
}

export default CommonProperties