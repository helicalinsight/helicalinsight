import { ExperimentOutlined } from "@ant-design/icons";
import { Card, Input, Space, Tooltip } from "antd";
import { cloneDeep, isEmpty } from "lodash";
import { useState } from "react";
import { useDrag } from "react-dnd";
import { useSelector } from "react-redux";
import { HCR_CROSSTAB_CELL_HEIGHT, HCR_CROSSTAB_CELL_WIDTH, HCR_TABLE_DATA_CELL_HEIGHT, HCR_TABLE_DATA_CELL_WIDTH, hcrTableBandOrder, } from "../hcr-constants";
import TableOutlinePanel from "./advanceComponents/components/tableOutlinePanel";
import HCRAdvancedTableComponent from "./advanceComponents/table/hcrAdvancedTableComponent";
import HCRChartsComponent from "./hcrCharts/hcrChartsComponent";
import HCRCrossTabComponent from "./hcrCrossTab/hcrCrossTabComponent";
import { ImageNode, LineNode, PageBreakNode, TextNode } from "./nodes";

const { Search } = Input;

const textData = {
    label: 'Text',
    borders: {},
    padding: {},
    width: 103,
    height: 20,
    name: 'text',
    renderKey: 'text',
    parentKey: 'elements',
    isLeaf: true,
    repeat: 'na',
    category: 'text',
    zIndex: 10,
    type: 'defaultNodes',
    fontSize: 14,
    fontFamily: 'questrialregular'
};

const advancedTableData = {
    width: 103,
    height: 20,
    nodeWidth: HCR_TABLE_DATA_CELL_WIDTH,
    nodeHeight: HCR_TABLE_DATA_CELL_HEIGHT * 5 + 5,
    name: 'advancedTable',
    label: 'Table',
    renderKey: 'advancedTable',
    parentKey: 'elements',
    isLeaf: true,
    repeat: 'na',
    category: 'advancedTable',
    zIndex: 10,
    type: 'defaultNodes',
    fontSize: 14,
    fontFamily: 'questrialregular',
    borders: {},
    padding: {},
    isAppliedClicked: false,
};

const crossTabData = {
    width: 103,
    height: 20,
    nodeWidth: HCR_CROSSTAB_CELL_WIDTH * 5,
    nodeHeight: HCR_CROSSTAB_CELL_HEIGHT * 3,
    name: 'crossTab',
    label: 'Cross Tab',
    renderKey: 'crosstab',
    parentKey: 'elements',
    isLeaf: true,
    repeat: 'na',
    category: 'crosstab',
    zIndex: 10,
    type: 'defaultNodes',
    fontSize: 14,
    fontFamily: 'questrialregular',
    // isGroup: true,
    borders: {},
    padding: {},
};

const chartsData = {
    width: 103,
    height: 20,
    nodeWidth: 270,
    nodeHeight: 120,
    name: 'chart',
    label: 'Chart',
    renderKey: 'chart',
    parentKey: 'elements',
    isLeaf: true,
    repeat: 'na',
    category: 'chart',
    zIndex: 10,
    type: 'defaultNodes',
    fontSize: 14,
    fontFamily: 'questrialregular',
    borders: {},
    padding: {},
}

const textNode = {
    component: <TextNode data={textData} isElementRender={true} />,
    data: textData,
    key: 'text'
}

const advancedTableNode = {
    component: <HCRAdvancedTableComponent data={advancedTableData} isElementRender={true} />,
    data: advancedTableData,
    key: 'advancedTable'
}

const crossTabNode = {
    component: <HCRCrossTabComponent data={crossTabData} isElementRender={true} />,
    data: crossTabData,
    key: 'crosstab'
}

const hcrChartsNode = {
    component: <HCRChartsComponent data={chartsData} isElementRender={true} />,
    data: chartsData,
    key: 'charts'
}

const lineData = {
    name: 'line',
    width: 103,
    height: 20,
    label: 'Line',
    renderKey: 'line',
    parentKey: 'elements',
    isLeaf: true,
    repeat: 'na',
    category: 'line',
    zIndex: 10,
    type: 'defaultNodes',
    lineStyles: { printRepeatedValues: true }
};

const lineNode = {
    component: <LineNode data={lineData} isElementRender={true} />,
    data: lineData,
    key: 'line'
};

const imageData = {
    label: 'Image',
    borders: {},
    padding: {},
    width: 103,
    height: 20,
    name: 'image',
    renderKey: 'image',
    parentKey: 'elements',
    isLeaf: true,
    repeat: 'na',
    category: 'image',
    zIndex: 10,
    type: 'defaultNodes',
};

const imageNode = {
    component: <ImageNode data={imageData} isElementRender={true} />,
    data: imageData,
    key: 'image'
}

const pageBreakData = {
    name: 'pageBreak',
    width: 103,
    height: 20,
    label: 'Page Break',
    renderKey: 'pageBreak',
    parentKey: 'elements',
    isLeaf: true,
    repeat: 'na',
    category: 'pageBreak',
    zIndex: 10,
    type: 'defaultNodes',
    lineStyles: {}
};

const pageBreakNode = {
    component: <PageBreakNode data={pageBreakData} isElementRender={true} />,
    data: pageBreakData,
    key: 'pageBreak'
};

const defaultNodes = [imageNode, lineNode, pageBreakNode, textNode, advancedTableNode, crossTabNode, hcrChartsNode];

const DefaultNodeItem = ({ node, isExperimental = false }) => {
    const canvasMargin = useSelector(state => state.cannedReports.present.hcrTabData.panes.find(pane => pane.key === state.cannedReports.present.hcrTabData.activeKey)?.canvasProperties?.margin || {});
    const [{ }, drag] = useDrag(() => {
        return {
            type: "customNodes",
            item: { record: node.data, canvasMargin, type: "customNodes" },
            collect: (monitor) => ({
                opacity: monitor.isDragging() ? 0.5 : 1,
            }),
        };
    }, [canvasMargin]);

    return <span ref={drag} style={{ cursor: 'pointer' }}>
        <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
            {node.component}
            {isExperimental ? <Tooltip title="Experimental Module" placement="right">
                <ExperimentOutlined
                    style={{
                        fontSize: "10px",
                        backgroundColor: "#1890F5",
                        color: "#fff",
                        padding: "3px",
                        margin: "2px",
                        borderRadius: "50%",
                    }}
                />
            </Tooltip> : null}
        </div>
    </span>
}

// const getdefaultNodesMenu = ({ nodes }) => {
//     return <Menu selectable={true}>
//         {nodes?.map(node => {
//             return <Menu.Item
//                 onClick={() => {
//                     // dispatch(hcrActions.handleEditingDsPaneItem({ dataSourcePane: record.dataSourcePane, itemId, key: reqKey, value: conn.id, setForSql }));
//                 }}
//                 key={node.key}
//                 value={node.key}
//             >
//                 <DefaultNodeItem node={node} />
//             </Menu.Item>
//         })}
//     </Menu>
// }

export default function HcrCanvasSidebar() {
    const [elementSearch, setElementSearch] = useState('');
    let visibleNodes = cloneDeep(defaultNodes);
    const { settings: { experimentalSubModules = {} } = {}, showExperimentalFeatures = false, } = useSelector((state) => state.app.applicationSettingsData) || {};
    let experimentalModules = [];
    if (!isEmpty(experimentalSubModules)) {
        const hcrExperimentalSubModules = experimentalSubModules.hcr || {};
        if (!isEmpty(hcrExperimentalSubModules)) {
            experimentalModules = hcrExperimentalSubModules?.disabledComponents || [];
            experimentalModules = experimentalModules.map(m => m === "chart" ? "charts" : m);
            visibleNodes = visibleNodes.map((node) => {
                if (experimentalModules?.includes(node.key)) {
                    return showExperimentalFeatures ? { ...node, isExperimental: true } : null
                }
                return { ...node, isExperimental: false };
            }).filter(Boolean);
        }
    }

    const onSearch = (e) => {
        setElementSearch(e.target.value)
    }

    return <Space direction="vertical" size='middle' className="canvas-sidebar">
        <Input
            placeholder="Search Element"
            allowClear
            // onSearch={onSearch}
            onChange={onSearch}
            style={{
                width: 205,
            }}
        />
        <Card title="Elements" className="elements-card">
            <Space direction="vertical" className="gap-4" style={{ paddingLeft: 15 }}>
                {visibleNodes.filter(ele => (ele.key.toLowerCase()).includes(elementSearch.toLowerCase())).map(node => {
                    return <DefaultNodeItem node={node} isExperimental={node?.isExperimental} />;
                })}
            </Space>
        </Card>
        <TableOutlinePanel />
    </Space>
}