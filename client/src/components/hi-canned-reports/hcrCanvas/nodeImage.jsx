import { Checkbox } from "antd";
import { useSelector } from "react-redux";

export default function NodeImage({ SelectField, getLabel, onPropertyChange, nodeValues, groupOptions }) {
    const HcrPropertiesConfiguration = useSelector(state => state.cannedReports.present?.hCROldConfigurations?.HcrPropertiesConfiguration || {});
    const { tooltipInfo = {} } = HcrPropertiesConfiguration || {};
    const { tooltip_fillImage, tooltip_styleNameReference, tooltip_scaleImage, tooltip_onErrorType, tooltip_isLazy, tooltip_isUsingCache, tooltip_evalTime, tooltip_evalGroupName } = tooltipInfo || {};
    const designerProperties = useSelector(state => state.cannedReports.present?.hCROldConfigurations?.HCR?.HCR?.designerProperties || {});
    const { fill, scaleImage, onErrorType, evaluationTime } = designerProperties;
    const activeTab = useSelector(state => state.cannedReports.present.hcrTabData.panes.find(pane => pane.key === state.cannedReports.present.hcrTabData.activeKey)) || {};
    const { canvasProperties, tableStyles = [] } = activeTab;
    const pageStyles = canvasProperties?.pageStyles;
    const customStyles = [...(pageStyles?.options || []), ...(tableStyles || [])];

    const selectTypes = [{
        title: "Fill",
        options: fill,
        key: "fillType",
        tooltip: tooltip_fillImage
    }, {
        title: "Scale Image",
        options: scaleImage,
        key: 'scaleImage',
        tooltip: tooltip_scaleImage
    }, {
        title: "On Error Type",
        options: onErrorType,
        key: 'onErrorType',
        tooltip: tooltip_onErrorType
    }, {
        title: "Style Name",
        options: ["None", ...(customStyles.map((ele) => ele.styleName) || [])],
        key: 'styleName',
        tooltip: tooltip_styleNameReference
    },
    {
        title: "Eval Time",
        options: evaluationTime,
        key: "evalTime",
        tooltip: tooltip_evalTime,
    },
    {
        key: "evalGroup",
        title: "Eval Group",
        options: nodeValues.evalTime === "Group" ? (groupOptions || []) : [],
        tooltip: tooltip_evalGroupName,
    }];

    const checkOptions = [
        {
            label: 'Is lazy',
            key: 'isLazy',
            tooltip: tooltip_isLazy
        },
        {
            label: 'Is using cache',
            key: 'isUsingCache',
            tooltip: tooltip_isUsingCache
        },
    ];

    const onChange = (isChecked, key) => {
        onPropertyChange({ key, value: isChecked });
    };

    return <>
        <div className="common-group">
            {
                selectTypes.map(ele => {
                    return <SelectField
                        label={getLabel({ label: ele.title, tooltip: ele.tooltip })}
                        value={nodeValues[ele.key]}
                        options={ele.options.map(opt => {
                            return {
                                label: opt,
                                value: opt
                            }
                        })}
                        width={105}
                        onChange={(value) => {
                            let payload = { key: ele.key, value }
                            if (ele.key === "styleName" && value !== 'None') {
                                const selectedStyles = customStyles.find(style => style.styleName === value);
                                const { id, name, isChanged, isConditionalStyleReq, tableId, bandsApplicable, ...rest } = selectedStyles;
                                payload = { ...payload, styles: rest };
                            }
                            if (ele.key === "styleName" && value === "None") {
                                payload = {
                                    ...payload,
                                    styles: {
                                        fill: "#ffffff",
                                        fontFill: "#000000",
                                        verticalAlign: 'middle',
                                        horizontalAlign: "center",
                                        borders: {},
                                        padding: {},
                                        mode: "Transparent",
                                        rotation: "None"
                                    }
                                }
                            }
                            onPropertyChange(payload);
                        }}
                    />
                })
            }
        </div>
        {checkOptions.map(ele => {
            return <div>
                <Checkbox style={{ marginLeft: 0 }} onChange={(e) => { onChange(e.target.checked, ele.key) }} checked={nodeValues[ele.key]}>
                    {getLabel({ label: ele.label, tooltip: ele.tooltip })}
                </Checkbox>
            </div>
        })}
    </>
}