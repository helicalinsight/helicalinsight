import { DownOutlined } from '@ant-design/icons';
import { Card, Dropdown, Menu, Tooltip, Tree } from 'antd';
import { isEmpty } from 'lodash';
import { useEffect, useMemo, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { v4 as uuidv4 } from 'uuid';
import useSubDataSet from '../../../../../hooks/useSubDataSet';
import { hcrActions, hcrRedo, hcrUndo } from '../../../../../redux/actions';
import { HCR_TABLE_DATA_CELL_WIDTH, hcrDSQuery } from '../../../hcr-constants';
import { FieldItem } from '../../hcrFields';
import { getHCRTableContextMenu, getHcrTableOutlineData, getHCRTableOutlineDSContextMenu, getParentKeys } from '../utils';
import { getInitialGroupData, getSubDataSet } from '../../hcrCanvasPaneHelperMethods';

export const HCRTableContextMenu = (props = {}) => {
    const {
        onVisibleChange = () => { },
        visible,
        title = null,
        menuType,
        bandType,
        columnId,
        tableData = {},
        cellId,
        nodeId,
        deleted,
        children = [],
        copiedNodes = [],
        cb = () => { },
        ...rest
    } = props || {}
    const dispatch = useDispatch();
    const { id: tableId, columnOrder = [], cells = {}, nodes = {}, selectedNodes = [] } = tableData || {}
    if (!tableId) return null;
    const deleteRowOption = children.length ? children.every((child) => child.deleted) : false;
    const menu = getHCRTableContextMenu({
        menuType,
        bandType,
        cell_deleted: deleted,
        row_deleted: deleteRowOption,
        copiedNodes
    });
    const updateTable = (actionType, payload) => {
        dispatch(hcrActions.hcrUpdateCanvasTabComponent({
            actionType,
            id: tableId,
            ...payload,
        }))
    }

    const selectCell = () => {
        if (cellId) {
            dispatch(hcrActions.hcrUpdateCanvasTabComponent({
                actionType: "selectCells",
                id: tableId,
                selectedCells: [cellId]
            }))
        }
        return;
    }

    const handleMenuClick = ({ key, domEvent: e }) => {
        e.stopPropagation();

        switch (key) {
            case "create_col_at_beginning": {
                updateTable("addColumn", { columnIndex: 0, width: HCR_TABLE_DATA_CELL_WIDTH })
                break;
            }
            case "create_col_at_end": {
                updateTable("addColumn", { columnIndex: columnOrder.length, width: HCR_TABLE_DATA_CELL_WIDTH })
                break;
            }
            case "create_col_after": {
                const colIndex = columnOrder.findIndex((col) => col === columnId);
                updateTable("addColumn", { columnIndex: colIndex > -1 ? colIndex + 1 : 0, width: HCR_TABLE_DATA_CELL_WIDTH })
                break;
            }
            case "create_col_before": {
                const colIndex = columnOrder.findIndex((col) => col === columnId);
                updateTable("addColumn", { columnIndex: colIndex > 0 ? colIndex : 0, width: HCR_TABLE_DATA_CELL_WIDTH })
                break;
            }
            case "delete_column": {
                const colIndex = columnOrder.findIndex((col) => col === columnId);
                updateTable("removeColumn", { columnIndex: colIndex, columnId });
                break;
            }
            case "delete_cell": {
                updateTable("removeCell", { cellId, bandType })
                break;
            }
            case "create_cell": {
                updateTable("createCell", { cellId, bandType })
                break;
            }
            case "delete_row": {
                let cellsToDelete = []
                for (let cell in cells) {
                    if (cell.includes(bandType)) cellsToDelete.push(cell)
                }
                updateTable("removeRow", { cellIds: cellsToDelete, bandType })
                break;
            }
            case "create_row": {
                let cellsToAdd = []
                for (let cell in cells) {
                    if (cell.includes(bandType)) cellsToAdd.push(cell)
                }
                updateTable("createRow", { cellIds: cellsToAdd, bandType })
                break;
            }
            case "select_all_cells": {
                const cellIds = Object.keys(cells) || []
                if (cellIds.length) {
                    updateTable("selectCells", { selectedCells: cellIds })
                }
                break;
            }
            case "select_all_nodes": {
                let nodeIds = Object.keys(nodes) || []
                if (nodeIds.length) {
                    updateTable("selectNodes", { selectedNodes: nodeIds })
                }
                break;
            }
            case "undo": {
                dispatch(hcrUndo())
                break;
            }
            case "redo": {
                dispatch(hcrRedo())
                break;
            }
            case "cut_node": {
                if (selectedNodes.length) {
                    let cNodes = selectedNodes.reduce((acc, next) => {
                        acc.push(nodes[next]);
                        return acc;
                    }, [])
                    dispatch(hcrActions.hcrUpdateTableClipboard({
                        id: tableId,
                        type: "cut",
                        nodes: cNodes
                    }))
                    updateTable("cutNodes", { cutNodesData: cNodes })
                } else {
                    let node = nodes[nodeId]
                    dispatch(hcrActions.hcrUpdateTableClipboard({
                        id: tableId,
                        type: "cut",
                        nodes: [node]
                    }))
                    updateTable("cutNodes", { cutNodesData: [node] })
                }
                break;
            }
            case "copy_node": {
                if (selectedNodes.length) {
                    let cNodes = selectedNodes.reduce((acc, next) => {
                        acc.push(nodes[next]);
                        return acc;
                    }, [])
                    dispatch(hcrActions.hcrUpdateTableClipboard({
                        id: tableId,
                        type: "copy",
                        nodes: cNodes
                    }))
                } else {
                    let node = nodes[nodeId]
                    dispatch(hcrActions.hcrUpdateTableClipboard({
                        id: tableId,
                        type: "copy",
                        nodes: [node]
                    }))
                }
                break;
            }
            case "delete_node": {
                updateTable("deleteNode", { nodeId })
                selectCell()
                break;
            }
            case "paste_node": {
                selectCell()
                updateTable("pasteCopiedNodes", { copiedNodes })
                break;
            }
            default:
                break;
        }
        cb()
    }

    const contextMenu = (
        <Menu onClick={handleMenuClick} className='hcr-table-context-menu' style={{ width: 200 }}>
            {menu?.map((ele) => {
                return <Menu.Item style={{ fontSize: 12 }} className={ele.className || ""} key={ele.key}>{ele.label}</Menu.Item>
            })}
        </Menu>
    )
    return (
        <Dropdown
            visible={visible}
            onVisibleChange={(visible) => onVisibleChange(visible)}
            overlay={contextMenu}
            trigger={["contextMenu"]}
            placement="bottomRight"
        >
            {title}
        </Dropdown>
    )
}

const HCRTableDatasetContextMenu = (props = {}) => {
    const {
        visible,
        title = null,
        onVisibleChange = () => { },
        menuType,
        tableData = {},
        fieldId = null,
        selectedSubDataSet = {},
        groupId = null,
        calculationId = null,
        parameterId = null,
        styleId = null,
    } = props || {}
    const { fields = [], id: subDSId, groups = [], calculations = [], parameters = [] } = selectedSubDataSet || {}
    const { outlineDSFields = [], id: tableId } = tableData || {}

    const dispatch = useDispatch()

    const menu = getHCRTableOutlineDSContextMenu({ menuType })
    const handleMenuClick = ({ key, domEvent: e }) => {
        e.stopPropagation();
        let payload = {};
        switch (key) {
            case "create_field": {
                if (fields.length) {
                    let name = "Field 1"

                    let counter = 1
                    while (fields.some((field) => field.name === name)) {
                        name = `Field ${counter}`
                        counter++
                    }

                    payload = {
                        actionType: "updateFields",
                        id: subDSId,
                        fields: [
                            ...fields,
                            {
                                id: uuidv4(),
                                name,
                                clazz: "java.lang.String",
                            }
                        ]
                    }
                } else {
                    payload = {
                        actionType: "updateFields",
                        id: subDSId,
                        fields: [
                            {
                                id: uuidv4(),
                                name: "Field 1",
                                clazz: "java.lang.String",
                            }
                        ]
                    }
                }
                break;
            }
            case "delete_fields_item": {
                payload = {
                    actionType: "updateFields",
                    id: subDSId,
                    fields: fields.filter((field) => field.id !== fieldId)
                }
                dispatch(hcrActions.hcrUpdateCanvasTabComponent({ actionType: "clearSelection", id: tableId }))
                break;
            }
            case "create_calculation": {
                payload = {
                    id: tableId,
                    actionType: "selectCalculation",
                    selectedCalculation: ["create_calculation"]
                }
                dispatch(hcrActions.setHcrCanvasCalculations({ clearKeyValuePairs: true }));
                break;
            }
            case "create_group": {
                if (groups.length) {
                    let name = "Group 1"

                    let counter = 1
                    while (groups.some((field) => field.name === name)) {
                        name = `Group ${counter}`
                        counter++
                    }
                    payload = {
                        actionType: "updateGroups",
                        id: subDSId,
                        groups: [
                            ...groups,
                            getInitialGroupData(name.replace("group_", ""))
                        ]
                    }
                    dispatch(hcrActions.hcrUpdateCanvasTabComponent({
                        id: tableId,
                        actionType: "addNewGroup",
                        groupField: name
                    }))
                } else {
                    payload = {
                        actionType: "updateGroups",
                        id: subDSId,
                        groups: [
                            ...groups,
                            getInitialGroupData("Group 1")
                        ]
                    }
                    dispatch(hcrActions.hcrUpdateCanvasTabComponent({
                        id: tableId,
                        actionType: "addNewGroup",
                        groupField: "Group 1"
                    }))
                }
                break;
            }
            case "delete_group_item": {
                payload = {
                    actionType: "updateGroups",
                    id: subDSId,
                    groups: groups.filter((group) => group.id !== groupId)
                }
                const group = groups.find((group) => group.id === groupId)
                dispatch(hcrActions.hcrUpdateCanvasTabComponent({
                    id: tableId,
                    actionType: "deleteGroup",
                    groupField: group?.name
                }))
                break;
            }
            case "delete_calculation_item": {
                payload = {
                    actionType: "updateCalculations",
                    id: subDSId,
                    calculations: calculations.filter((cal) => cal.id !== calculationId)
                }
                dispatch(hcrActions.setHcrCanvasCalculations({ clearKeyValuePairs: true }));
                break;
            }
            case "create_parameter": {
                payload = {
                    actionType: "addNewParameter",
                    id: subDSId,
                }
                break;
            }
            case "delete_parameter_item": {
                payload = {
                    actionType: "updateParameters",
                    id: subDSId,
                    parameters: parameters.filter((param) => param.id !== parameterId)
                }
                break;
            }
            case "create_style": {
                dispatch(hcrActions.hcrUpdateTableStyles({
                    actionType: "createStyle",
                    tableId,
                }))
                return;
                break;
            }
            case "delete_style_item": {
                dispatch(hcrActions.hcrUpdateTableStyles({
                    actionType: "deleteStyleById",
                    tableId,
                    styleId
                }))
                dispatch(hcrActions.hcrUpdateCanvasTabComponent({
                    id: tableId,
                    actionType: "clearSelection",
                }))
                return;
                break;
            }
            default:
                break;
        }
        if (!isEmpty(payload)) {
            if (payload.actionType === "selectCalculation") {
                dispatch(hcrActions.hcrUpdateCanvasTabComponent(payload))
            } else {
                dispatch(hcrActions.hcrUpdateSubdataSets(payload))
            }
        }
    }

    const contextMenu = (
        <Menu onClick={handleMenuClick} className='hcr-table-context-menu' style={{ width: 200 }}>
            {menu?.map((ele) => {
                return <Menu.Item style={{ fontSize: 12 }} className={ele.className || ""} key={ele.key}>{ele.label}</Menu.Item>
            })}
        </Menu>
    )

    return (
        <Dropdown
            visible={visible}
            onVisibleChange={(visible) => onVisibleChange(visible)}
            overlay={contextMenu}
            trigger={["contextMenu"]}
            placement="bottomLeft"
        >
            {title}
        </Dropdown>
    )
}

const RenderTitle = (props = {}) => {
    const {
        title = "",
        data = {},
        draggable = false,
        contextMenu,
        dsContextMenu = false,
        ...rest
    } = props || {}
    const [visible, setVisible] = useState(null)

    function getTooltipTitle(title, tooltip) {
        return <Tooltip title={tooltip} placement="topRight"><div>{title}</div></Tooltip>
    }

    if (draggable) {
        let titleToRender = (
            <div>
                <FieldItem field={data} />
            </div>
        )
        if (!dsContextMenu) return titleToRender;
        return (
            <HCRTableDatasetContextMenu
                title={titleToRender}
                visible={visible}
                onVisibleChange={(value) => setVisible(value)}
                {...rest}
            />

        )
    }
    if (contextMenu) {
        const { deleted } = rest || {}
        return (
            <HCRTableContextMenu
                onVisibleChange={(value) => setVisible(value)}
                visible={visible}
                title={<div style={{ opacity: deleted ? 0.5 : 1 }}>{title}</div>}
                {...rest}
            />
        )
    }
    if (dsContextMenu) {
        return (
            <HCRTableDatasetContextMenu
                title={props.tooltip ? getTooltipTitle(title, props.tooltip) : <div>{title}</div>}
                visible={visible}
                onVisibleChange={(value) => setVisible(value)}
                {...rest}
            />

        )
    }
    if (props.tooltip) {
        return getTooltipTitle(title, props.tooltip)
    }
    return title;
}

const TableOutlinePanel = () => {
    const dispatch = useDispatch()
    const [expandedKeys, setExpandedKeys] = useState([])
    const activeTab =
        useSelector((state) =>
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
        tableStyles = []
    } = activeTab;
    const selectedNode = hcrDiagramNodesData.find((node) => node.id === active);
    const {
        selectedCells,
        selectedNodes: tableSelectedNodes = [],
        id,
        selectedQueryID,
        selectedGroupFields = [],
        outlineDsSelectedField,
        selectedTable,
        selectedCalculation,
        selectedGroup,
        selectedParameter = [],
        selectedStyle = [],
    } = selectedNode || {}
    let selectedSubDataSet = getSubDataSet(subDataSets, (selectedQueryID || id));
    let copiedNodes = [];
    const { copy = [], cut = [] } = hcrTableClipboardData?.[id] || {}
    if (copy.length) copiedNodes = copy
    if (cut.length) copiedNodes = cut

    const queriesMenu = dsPaneTypes
        ?.find((ele) => ele.dataSourcePane === hcrDSQuery)
        ?.menu?.filter(
            (ele) =>
                ele.executeQueryData?.data.length ||
                ele.executeQueryData?.field.length,
        ) || [];

    let { id: subDSId, name } = selectedSubDataSet || {};
    const actualQuery = queriesMenu?.find((ele) => ele.id === subDSId);

    if (actualQuery) name = actualQuery.name
    const selectedKey = selectedCells?.[0] || tableSelectedNodes?.[0] || outlineDsSelectedField || selectedTable || selectedCalculation?.[0] || selectedGroup?.[0] || selectedParameter?.[0] || selectedStyle?.[0];

    const { getSubDataSetOptions } = useSubDataSet({
        fields: selectedSubDataSet?.fields || [],
        calculations: selectedSubDataSet?.calculations || [],
        groups: selectedSubDataSet?.groups || [],
        parameters: selectedSubDataSet?.parameters || [],
    })
    const subDataSetOptions = getSubDataSetOptions();
    const treeData = getHcrTableOutlineData(selectedNode, subDataSetOptions, selectedSubDataSet, name, tableStyles)
    const { calculations = [] } = selectedSubDataSet || {}

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
            default:
                break;
        }

        if (!isEmpty(payload)) {
            dispatch(hcrActions.hcrUpdateCanvasTabComponent(payload))
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

    if ([active, canvasView].includes("canvas") || !selectedNode) return null;
    return (
        <Card title="Table Outline" className="elements-card hcr-table-outline-container">
            <Tree
                switcherIcon={<DownOutlined />}
                treeData={treeData}
                showLine
                selectedKeys={[selectedKey]}
                expandedKeys={expandedKeys}
                onSelect={handleSelect}
                onExpand={handleExpand}
                titleRender={(data) => <RenderTitle {...data} copiedNodes={copiedNodes} selectedSubDataSet={selectedSubDataSet} />}
            />
        </Card>
    )
}

export default TableOutlinePanel