import { Checkbox, Collapse, Input, Tooltip } from "antd";
import { useSelector } from "react-redux";
import NodeLineStyles from "./canvasProperty/hcrCanvasLineStyles";
import NodeAlignment from "./nodeAlignment";
import NodeColorPicker from "./nodeColorPicker";
import { hcrCanvasPaneHelperMethods } from "./hcrCanvasPaneHelperMethods";
import ElementProperties from "./elementProperties";
const { getHcrPropertyTooltipInfo } = hcrCanvasPaneHelperMethods

const { TextArea } = Input;

export default function LineProperties({ EditorPanels, onNodeConfigChange, nodeConfig }) {
    const { InputFiled, Position, InputNumberFiled, Size, SelectField } = EditorPanels;
    const designerProperties = useSelector(state => state.cannedReports.present?.hCROldConfigurations?.HCR?.HCR?.designerProperties || {});
    const { lineDirection } = designerProperties;
    const HcrPropertiesConfiguration = useSelector(state => state.cannedReports.present?.hCROldConfigurations?.HcrPropertiesConfiguration || {});
    const activeTab = useSelector(state => state.cannedReports.present.hcrTabData.panes.find(pane => pane.key === state.cannedReports.present.hcrTabData.activeKey)) || {};
    const { canvasProperties, tableStyles = [] } = activeTab;
    const pageStyles = canvasProperties?.pageStyles;
    const customStyles = [...(pageStyles?.options || []), ...(tableStyles || [])];
    const { tooltipInfo = {} } = HcrPropertiesConfiguration || {};
    const {
        tooltip_printRepeatedValues,
        tooltip_lineDirection,
        tooltip_lintStyleName,
        tooltip_color
    } = tooltipInfo || {};

    const selectTypes = [{
        title: "Direction",
        options: lineDirection,
        key: 'direction',
        tooltip: tooltip_lineDirection
    }, {
        title: "Style Name",
        options: ["None", ...(customStyles.map((ele) => ele.styleName) || [])],
        key: 'styleName',
        tooltip: tooltip_lintStyleName
    }];

    const checkOptions = [
        {
            label: 'Print repeated values',
            key: 'printRepeatedValues',
            tooltip: tooltip_printRepeatedValues
        }
    ];

    const nodeValues = {
        ...nodeConfig,
        lineStyles: {
            stroke: nodeConfig.lineStyles.stroke || 1,
            style: nodeConfig.lineStyles.style || 'solid',
            color: nodeConfig.lineStyles.color || '#000000',
            direction: nodeConfig.lineStyles.direction || 'TopDown',
            styleName: nodeConfig.lineStyles.styleName || '',
            printRepeatedValues: nodeConfig.lineStyles.printRepeatedValues
        },
        properties: nodeConfig.properties || [],
    }

    function onPropertyChange({ key, value }) {
        onNodeConfigChange(key, value);
    }

    function getTooltipInfo({ label, tooltip, placement }) {
        const tooltipTitle = tooltip?.content?.[0]?.displayContent || "";
        let divTag = document.createElement('div');
        divTag.innerHTML = tooltipTitle;
        return <Tooltip placement={placement || "top"} title={<div dangerouslySetInnerHTML={{ __html: divTag.innerHTML }} />}>
            <div className="property-label">{label}</div>
        </Tooltip>;
    }

    return <>
        <Collapse defaultActiveKey={'alignment'} size={'small'} className="node-property-collapse" >
            <Collapse.Panel header={<span className="node-property-title">Line Styles</span>}
                key={'alignment'}
            >
                <NodeLineStyles hideLineStyleTitle={true} InputNumberFiled={InputNumberFiled} SelectField={SelectField} nodeValues={nodeValues} onPropertyChange={onPropertyChange} />
                <div className="textfield-select-properties">
                    {selectTypes.map(ele => {
                        return <SelectField
                            label={<div className="property-label">{getHcrPropertyTooltipInfo({ label: ele.title, tooltip: ele.tooltip })}</div>}
                            value={nodeValues.lineStyles[ele.key]}
                            options={ele.options.map(option => {
                                return {
                                    label: option,
                                    value: option
                                }
                            })}
                            width={110}
                            onChange={(value) => {
                                let styleNameStyles = {}
                                if (ele.key === "styleName" && value !== "None") {
                                    const selectedStyles = customStyles.find(style => style.styleName === value);
                                    const { lineStyles } = selectedStyles;
                                    styleNameStyles = { ...lineStyles };
                                }
                                if (ele.key === "styleName" && value === "None") {
                                    styleNameStyles = {
                                        stroke: 1,
                                        style: 'Solid',
                                        color: "#000000",
                                        borders: {},
                                        padding: {},
                                        mode: "Transparent",
                                        rotation: "None",
                                    }
                                }
                                onPropertyChange({ key: 'lineStyles', value: { ...nodeValues.lineStyles, [ele.key]: value, ...styleNameStyles } });
                            }}
                        />
                    })}
                </div>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'end' }}>
                    <NodeColorPicker onPropertyChange={({ value }) => { onPropertyChange({ key: 'lineStyles', value: { ...nodeValues.lineStyles, color: value } }) }} clrVal={nodeValues.lineStyles.color} keyWord="color" label={'Color'} placement={'bottomLeft'} tooltip={tooltip_color} />
                    {checkOptions.map(ele => {
                        return <div>
                            <Checkbox style={{ marginLeft: 0, verticalAlign: 'sub' }}
                                onChange={(e) => {
                                    onPropertyChange({ key: 'lineStyles', value: { ...nodeValues.lineStyles, printRepeatedValues: e.target.checked } })
                                }}
                                checked={nodeValues.lineStyles[ele.key]}>
                                {getTooltipInfo({ label: ele.label, tooltip: ele.tooltip, placement: 'left' })}
                            </Checkbox>
                        </div>
                    })}
                </div>
            </Collapse.Panel>
        </Collapse>
        {/* <Divider className="group-divider" /> */}
        <Collapse size={'small'} className="node-property-collapse" >
            <Collapse.Panel header={<span className="node-property-title">Alignment</span>}
                key={'alignment'}
            >
                <NodeAlignment onPropertyChange={onPropertyChange} nodeValues={nodeValues} InputNumberFiled={InputNumberFiled} Position={Position} Size={Size} />
            </Collapse.Panel>
        </Collapse>
        <Collapse size={"small"} className="node-property-collapse">
            <Collapse.Panel
                header={<span className="node-property-title">Properties</span>}
                key={"Properties"}
            >
                <ElementProperties
                    InputFiled={InputFiled}
                    getLabel={getTooltipInfo}
                    component="Line"
                    properties={nodeValues.properties || []}
                    componentId={nodeValues.id}
                    onPropertyChange={(updatedProperties) => {
                        onPropertyChange({ key: "properties", value: updatedProperties });
                    }}
                />
            </Collapse.Panel>
        </Collapse>
    </>
}