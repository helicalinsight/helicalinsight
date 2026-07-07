import { AlignCenterOutlined, AlignLeftOutlined, AlignRightOutlined, ItalicOutlined, StrikethroughOutlined, UnderlineOutlined, VerticalAlignBottomOutlined, VerticalAlignMiddleOutlined, VerticalAlignTopOutlined } from "@ant-design/icons";
import { Button, Card, Col, Collapse, Divider, Dropdown, Input, Popover, Row, Select, Space, Tooltip } from "antd";
import { SketchPicker } from "react-color";
import { useDispatch, useSelector } from "react-redux";
import NodeBorders from "./nodeBorder";
import NodeColorPicker from "./nodeColorPicker";
import NodePadding from "./nodePadding";
import NodeAlignment from "./nodeAlignment";
import NodeTextField from "./nodeTextField";
import NodeTypography from "./nodeTypography";
import NodeLineStyles from "./canvasProperty/hcrCanvasLineStyles";
import ElementProperties from "./elementProperties";
import useHCRCascadeSelector from "../../../hooks/useHCRCascadeSelector";
import FieldSelector from "./fieldSelector";

const { TextArea } = Input;

export default function PageBreakProperties({ EditorPanels, onNodeConfigChange, nodeConfig }) {
    const { InputFiled, Position, InputNumberFiled, Size, SelectField } = EditorPanels;
    const designerProperties = useSelector(state => state.cannedReports.present?.hCROldConfigurations?.HCR?.HCR?.designerProperties || {});
    const activeTab = useSelector(state => state.cannedReports.present.hcrTabData.panes.find(pane => pane.key === state.cannedReports.present.hcrTabData.activeKey)) || {};
    const { canvasProperties } = activeTab;
    const HcrPropertiesConfiguration = useSelector(state => state.cannedReports.present?.hCROldConfigurations?.HcrPropertiesConfiguration || {});
    const { tooltipInfo = {} } = HcrPropertiesConfiguration || {};
    const { tooltip_printWhenExpression } = tooltipInfo || {};

    const nodeValues = {
        ...nodeConfig,
        properties: nodeConfig.properties || [],
    }

    const { getCascaderOptions } = useHCRCascadeSelector({ node: nodeValues })
    const cascaderOptions = getCascaderOptions()

    function onPropertyChange({ key, value }) {
        onNodeConfigChange(key, value);
    }

    function getTooltipInfo({ label, tooltip }) {
        const tooltipTitle = tooltip?.content?.[0]?.displayContent || "";
        let divTag = document.createElement('div');
        divTag.innerHTML = tooltipTitle;
        return <Tooltip title={<div dangerouslySetInnerHTML={{ __html: divTag.innerHTML }} />}>
            <div className="property-label">{label}</div>
        </Tooltip>;
    }

    return <>
        <Collapse defaultActiveKey={'Page Break Field'} size={'small'} className="node-property-collapse" >
            <Collapse.Panel header={<span className="node-property-title">Page Break Field</span>}
                key={'Page Break Field'}
            >
                <div>
                    {getTooltipInfo({ label: 'Print When Expression', tooltip: tooltip_printWhenExpression })}
                    <FieldSelector
                        options={cascaderOptions}
                        value={nodeValues.printWhenExp}
                        onChange={(valueObj = {}) => {
                            const { value } = valueObj || {}
                            onPropertyChange({ key: 'printWhenExp', value: value ? value : undefined });
                        }}
                    />
                </div>
            </Collapse.Panel>
        </Collapse>
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
                    component="Page Break"
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