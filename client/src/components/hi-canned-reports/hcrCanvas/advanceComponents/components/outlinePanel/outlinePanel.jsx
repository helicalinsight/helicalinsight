import { DownOutlined } from '@ant-design/icons';
import { Card, Tree } from 'antd';
import { isEmpty } from 'lodash';
import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import useSubDataSet from '../../../../../../hooks/useSubDataSet';
import { hcrActions } from '../../../../../../redux/actions';
import { getSubDataSet } from '../../../hcrCanvasPaneHelperMethods';
import { getOutlinePanelTitle, getOutlineTreeData, getParentKeys, getQueryItems, getSelectedKeys } from '../../utils';
import OutlineTitle from './outlineTitle';



const OutlinePanel = (props = {}) => {
    const dispatch = useDispatch()
    const [expandedKeys, setExpandedKeys] = useState([])

    const activeTab = useSelector((state) =>
        state.cannedReports.present.hcrTabData.panes.find(
            (pane) => pane.key === state.cannedReports.present.hcrTabData.activeKey
        )
    ) || {};

    const {
        hcrDiagramNodesData = [],
        canvasView = "canvas",
        canvasTabViews: { active = "canvas" } = {},
        dsPaneTypes = [],
        hcrTableClipboardData = {},
        subDataSets = [],
        tableStyles = [],
    } = activeTab;

    const currentComponent = hcrDiagramNodesData.find((node) => node.id === active);
    const {
        selectedCells,
        id,
        selectedQueryID,
        category = ""
    } = currentComponent || {}

    let copiedNodes = [];
    const { copy = [], cut = [] } = hcrTableClipboardData?.[id] || {}
    if (copy.length) copiedNodes = copy
    if (cut.length) copiedNodes = cut

    const selectedKey = getSelectedKeys(currentComponent);

    const selectedSubDataSet = getSubDataSet(subDataSets, (selectedQueryID || id));
    let { id: subDSId, name } = selectedSubDataSet || {};

    const isTable = category === "advancedTable",
        isCrosstab = category === "crosstabv2",
        queriesMenu = getQueryItems(dsPaneTypes),
        outlineTitle = getOutlinePanelTitle(category);

    const originalQuery = queriesMenu?.find((ele) => ele.id === subDSId);
    if (originalQuery) name = originalQuery.name;

    const { fields = [], calculations = [], groups = [], parameters = [] } = selectedSubDataSet || {}
    const { getSubDataSetOptions } = useSubDataSet({ fields, calculations, groups, parameters })
    const subDSOptions = getSubDataSetOptions({ fields, calculations, groups, parameters })

    const treeData = getOutlineTreeData({ currentComponentData: currentComponent, subDataSetOptions: subDSOptions, selectedSubDataSet, name, tableStyles });

    const handleSelect = (selectedKeys, info) => {
        const { selectedNodes = [] } = info || {}
        const { isNode = false, columnId = "", bandType = "", isField = false, selectKey = "" } = selectedNodes?.[0] || {}
        let selectedKey = selectedKeys[0], payload = {};
        switch (selectKey) {
            case "node": {
                payload = {
                    id,
                    nodeId: selectedKey,
                    actionType: "selectNode",
                }
                break;
            }
            case "cell": {
                payload = {
                    id,
                    actionType: "selectCells",
                    columnId,
                    bandType,
                    selectedCells: [selectedKey],
                }
                break;
            }
            case "fields-item": {
                payload = {
                    id,
                    actionType: "selectOutlineDSField",
                    outlineDsSelectedField: selectedKey,
                }
                break;
            }
            case "table": {
                payload = {
                    id,
                    actionType: "selectTable",
                }
                break;
            }
            case "calculations-item": {
                payload = {
                    id,
                    actionType: "selectCalculation",
                    selectedCalculation: [selectedKey]
                }
                const calculation = calculations.find((cal) => cal.id === selectedKey) || {}

                dispatch(hcrActions.setHcrCanvasCalculations({
                    key: 'selectCalculationFromAdvComp',
                    fromAdvanceComp: true,
                    editValues: calculation,
                }))
                break;
            }
            case "groups-item": {
                payload = {
                    id,
                    actionType: "selectGroup",
                    selectedGroup: [selectedKey]
                }
                break;
            }
            case "parameters-item": {
                payload = {
                    id,
                    actionType: "selectParameter",
                    selectedParameter: [selectedKey]
                }
                break;
            }
            case "table-style-item": {
                payload = {
                    id,
                    actionType: "selectTableStyle",
                    selectedStyle: [selectedKey]
                }
                break;
            }
            case "crosstab-group-item": {
                payload = {
                    id,
                    actionType: "selectCTGroup",
                    selectedCTGroup: [selectedKey]
                }
                break;
            }
            case "crosstab-measure-item": {
                payload = {
                    id,
                    actionType: "selectCTMeasure",
                    selectedCTMeasure: [selectedKey]
                }
                break;
            }
            case "crosstabv2": {
                payload = {
                    id,
                    actionType: "selectCrosstab",
                }
                break;
            }
            default:
                break;
        }

        if (!isEmpty(payload)) {
            if (isTable) {
                dispatch(hcrActions.hcrUpdateCanvasTabComponent(payload))
            }
            if (isCrosstab) {
                dispatch(hcrActions.hcrUpdateCrosstabComponent(payload))
            }
        }
    }

    const handleExpand = (expandedKeysValue) => {
        setExpandedKeys(expandedKeysValue);
    };

    useEffect(() => {
        if (selectedKey) {
            const keys = getParentKeys(treeData, selectedKey) || [];
            setExpandedKeys((prev) => [...new Set([...prev, ...keys])]);
        }
    }, [selectedKey])

    if ([active, canvasView].includes("canvas") || !currentComponent) return null;

    return (
        <Card title={outlineTitle} className="elements-card hcr-table-outline-container">
            <Tree
                switcherIcon={<DownOutlined />}
                treeData={treeData}
                showLine
                selectedKeys={[selectedKey]}
                expandedKeys={expandedKeys}
                onSelect={handleSelect}
                onExpand={handleExpand}
                titleRender={
                    (data) =>
                        <OutlineTitle
                            {...data}
                            copiedNodes={copiedNodes}
                            selectedSubDataSet={selectedSubDataSet}
                        />
                }
            />
        </Card>
    )
}

export default OutlinePanel