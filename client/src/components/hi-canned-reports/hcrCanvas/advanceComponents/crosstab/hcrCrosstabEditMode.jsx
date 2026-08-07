import { cloneDeep, isEmpty } from 'lodash';
import { useCallback, useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { v4 as uuidv4 } from 'uuid';
import { hcrActions } from '../../../../../redux/actions';
import { hcrDSQuery } from '../../../hcr-constants';
import { getSubDataSet } from '../../hcrCanvasPaneHelperMethods';
import SidebarPanel from '../components/sidebarPanel';
import "../table/hcrAdvancedTable.scss";
import CrosstabLayout from './components/crosstabLayout';
import "./hcrCrosstab.scss";
import { getCrosstabLayout } from '../utils';

const HCRCrosstabEditMode = (props = {}) => {
    const { data = {}, lastSelectedNodeRef } = props || {};

    const [sidePanelOpen, setSidePanelOpen] = useState(false);
    const [currentShortCut, setCurrentShortCut] = useState("");
    const dispatch = useDispatch()
    const widthConstant = 40, heightConstant = 50;

    const activeTab = useSelector((state) => state.cannedReports.present.hcrTabData.panes.find(
        (pane) => pane.key === state.cannedReports.present.hcrTabData.activeKey
    )) || {};

    const designerProperties = useSelector(
        (state) =>
            state.cannedReports.present?.hCROldConfigurations?.HCR?.HCR
                ?.designerProperties || {},
    );
    const { variables } = designerProperties;
    const { classNames } = variables || {};

    const {
        width,
        height,
        id,
        config = {},
        selectedNodes = [],
        selectedCells = [],
        outlineDsSelectedField = null,
        selectedCalculation = [],
        selectedGroup = [],
        selectedParameter = [],
        selectedStyle = [],
        selectedQueryID,
        selectedCTGroup = [],
        selectedCTMeasure = [],
        selectedCrosstab = null
    } = data || {};

    const {
        hcrTableClipboardData = {},
        dsPaneTypes = [],
        selectedQueryId: mainQuery,
        subDataSets = [],
        tableStyles = []
    } = activeTab || {}
    let selectedSubDS = getSubDataSet(subDataSets, (selectedQueryID || id));
    const { fields = [] } = selectedSubDS || {}
    let copiedNodes = [];
    const { copy = [], cut = [] } = hcrTableClipboardData?.[id] || {}
    if (copy.length) copiedNodes = copy
    if (cut.length) copiedNodes = cut


    let queriesMenu = dsPaneTypes
        ?.find((ele) => ele.dataSourcePane === hcrDSQuery)
        ?.menu?.filter((ele) => ele.executeQueryData?.data.length || ele.executeQueryData?.field.length) || []
    queriesMenu = queriesMenu?.filter((query) => query.id !== mainQuery) || []

    const selectedNodeId = selectedNodes[0],
        selectedCellId = selectedCells[0],
        currentCalculation = selectedCalculation[0],
        currentSelectedGroup = selectedGroup[0],
        currentParameter = selectedParameter[0],
        currentSelectedStyle = selectedStyle[0],
        currentSelectedGrp = selectedCTGroup[0],
        currentSelectedMeasure = selectedCTMeasure[0];

    const {
        nodes = {},
        rowHeights = [],
        colWidths = []
    } = config || {}

    const selectedNode = nodes[selectedNodeId] || null;

    const updateCrosstab = (actionType, payload = {}) => {
        dispatch(hcrActions.hcrUpdateCrosstabComponent({
            id,
            actionType,
            ...payload
        }))
    }

    const handleSidePanelClose = () => {
        setSidePanelOpen(false);
        updateCrosstab("clearSelection");
    }

    const handleSidePanelOpen = () => {
        setSidePanelOpen(true)
    }

    const handleNodeConfigChange = useCallback((key, value, styles = null, otherKeyValuePairs = {}) => {
        let obj = {
            [key]: value,
            ...otherKeyValuePairs
        };

        if (key === "strikeThrough") {
            if (selectedNode.underLine) {
                obj.underLine = false;
            }
        } else if (key === "underLine") {
            if (selectedNode.strikeThrough) {
                obj.strikeThrough = false;
            }
        }

        if (["styleName"].includes(key) && styles) {
            obj = {
                ...obj,
                ...styles,
            };
        }

        updateCrosstab("updateNodeProperties", {
            nodeId: selectedNodeId,
            properties: obj
        })
    }, [selectedNodeId])

    const getCellIdsToUpdate = (cells = [], type, ctCells = []) => {
        const updaterKey = { width: "widthUpdaters", height: "heightUpdaters" }[type]

        const indexToCellIds = new Map();
        for (const cell of ctCells) {
            const updaters = cell[updaterKey] || [];
            for (const idx of updaters) {
                let bucket = indexToCellIds.get(idx);
                if (!bucket) indexToCellIds.set(idx, (bucket = []));
                bucket.push(cell.id);
            }
        }

        const requestedIds = new Set(cells);
        const cellsToUpdate = new Set();

        for (const cell of ctCells) {
            if (!requestedIds.has(cell.id)) continue;
            const updaters = cell[updaterKey] || [];
            for (const idx of updaters) {
                const ids = indexToCellIds.get(idx);
                if (ids) for (const id of ids) cellsToUpdate.add(id);
            }
        }

        return [...cellsToUpdate];
    };

    const updateColwidthsAndRowHeights = (cellIds, ctCells, type, value) => {
        const updaterKey = { width: "widthUpdaters", height: "heightUpdaters" }[type]
        const tempRowHeights = cloneDeep(rowHeights);
        const tempColWidths = cloneDeep(colWidths);
        const indexToUpdate = ctCells.filter(({ id }) => cellIds.includes(id)).flatMap((cell) => {
            const updaters = cell[updaterKey] || [];
            return updaters
        })
        for (const idx of indexToUpdate) {
            if (type === "width") {
                tempColWidths[idx] = value;
            } else {
                tempRowHeights[idx] = value;
            }
        }
        updateCrosstab("updateCrosstabLayout", {
            colWidths: tempColWidths,
            rowHeights: tempRowHeights
        })
    }

    const handleCellPropertiesChange = ({ key, value, cellIds }) => {
        const ctCells = getCrosstabLayout(config);
        switch (key) {
            case "height": {
                const cellsToUpdate = getCellIdsToUpdate(cellIds, "height", ctCells)
                updateCrosstab("resizeCell", {
                    cellIds: cellsToUpdate,
                    height: value
                })
                updateColwidthsAndRowHeights(cellIds, ctCells, "height", value)
                break;
            }
            case "width": {
                const cellsToUpdate = getCellIdsToUpdate(cellIds, "width", ctCells)
                updateCrosstab("resizeCell", {
                    cellIds: cellsToUpdate,
                    width: value
                })
                updateColwidthsAndRowHeights(cellIds, ctCells, "width", value)
                break;
            }
            default:
                updateCrosstab("updateCellProperties", {
                    cellIds,
                    properties: { [key]: value },
                    cellIds
                })
                break;
        }
    }

    const handleOutlineDSItemChange = ({ type = "", id: fieldId, value = {}, subDSId }) => {
        switch (type) {
            case "field-item": {
                let newFields = fields.map((field) => {
                    if (field.id === fieldId) {
                        return {
                            ...field,
                            ...value
                        }
                    }
                    return field;
                })
                dispatch(hcrActions.hcrUpdateSubdataSets({
                    actionType: "updateFields",
                    id: subDSId,
                    fields: newFields
                }))
                break;
            }
            default:
                break;
        }
    }

    const handleNodeDelete = (nodeId) => {
        updateCrosstab("deleteNode", { nodeId })
        handleSidePanelClose();
    }

    const selectAllNodes = () => {
        updateCrosstab("selectNodes", { selectedNodes: Object.keys(nodes) })
    }

    const handleCopyNodes = (nodes) => {
        dispatch(hcrActions.hcrUpdateTableClipboard({
            id,
            type: "copy",
            nodes
        }))
    }

    const handleCutNodes = (nodes) => {
        dispatch(hcrActions.hcrUpdateTableClipboard({
            id,
            type: "cut",
            nodes
        }))
        updateCrosstab("cutNodes", { cutNodesData: nodes })
    }

    const handlePasteNodes = (nodes) => {
        updateCrosstab("pasteCopiedNodes", { copiedNodes: nodes })
    }

    const handleNodeClick = (e, node) => {
        if (!sidePanelOpen) {
            handleSidePanelOpen()
        }
        if (node) {
            if (e.ctrlKey) {
                const isPresent = selectedNodes.includes(node.id);
                updateCrosstab(isPresent ? "removeSelectedNode" : "selectNodes",
                    {
                        selectedNodes: [...selectedNodes, node.id],
                        nodeId: node.id
                    }
                )
            } else {
                updateCrosstab("selectNodes", { selectedNodes: [node.id] })
            }
        }
    }

    const handleCrosstabPropertiesChange = ({ key, value }) => {
        let payload = {}
        switch (key) {
            case "selectedQueryID": {
                let subDataSet = subDataSets.find((ds) => ds.id === value);
                if (subDataSet) {
                    const { id: subDSId, groups = [], selectedGroupFields = [], selectedFields = [] } = subDataSet || {}
                    payload.selectedQueryID = subDSId;
                    payload.selectedGroupFields = selectedGroupFields;
                    payload.selectedFields = selectedFields;
                } else {
                    const selectedQuery = queriesMenu?.find((ele) => ele.id === value)
                    const { executeQueryData, name, id: dsID } = selectedQuery || {};
                    const { field = [] } = executeQueryData || {};
                    const subDSPayload = {
                        actionType: "add",
                        id: dsID,
                        name,
                        groups: [],
                        fields: field?.map((f) => ({ ...f, id: uuidv4() })) || [],
                        selectedFields: field?.map(({ name }) => name) || [],
                        selectedGroupFields: []
                    }
                    dispatch(hcrActions.hcrUpdateSubdataSets(subDSPayload))
                    payload = {
                        selectedQueryID: dsID,
                        selectedGroupFields: [],
                        selectedFields: subDSPayload.selectedFields
                    }
                }
                break;
            }
            default:
                break;
        }
        if (!isEmpty(payload)) {
            updateCrosstab("crosstabProperties", { properties: payload })
        }
    }

    const handleOutsideClick = (e) => {
        e.preventDefault();
        handleSidePanelClose()
    }

    const sidebarPanelProps = {
        open: sidePanelOpen,
        onClose: handleSidePanelClose,
        nodeConfig: selectedNode,
        onNodeConfigChange: handleNodeConfigChange,
        onCellPropertyChange: handleCellPropertiesChange,
        data: data,
        classNames: classNames,
        onOutlineDsItemChange: handleOutlineDSItemChange,
        lastSelectedNodeRef,
        selectedSubDS,
        activeTab,
        queriesMenu
    }

    const layoutProps = {
        data,
        mode: "edit",
        onNodeClick: handleNodeClick,
        selectedCells,
        selectedNodes,
        onCloseSidePanel: handleSidePanelClose,
        copiedNodes,
        onCrosstabPropertiesChange: handleCrosstabPropertiesChange
    }

    useEffect(() => {
        function handleCtrlKeys(e) {
            const activeElement = document.activeElement;
            const isInputField = activeElement.tagName === "INPUT" || activeElement.tagName === "TEXTAREA" || activeElement.isContentEditable;
            if (!isInputField) {
                if (e.code === "Delete" && selectedNodeId) {
                    e.preventDefault();
                    handleNodeDelete(selectedNodeId)
                    setCurrentShortCut("Delete");
                }
                if (e.ctrlKey && e.key === "a") {
                    e.preventDefault();
                    selectAllNodes();
                    setCurrentShortCut("SelectAll");
                }
                if (e.ctrlKey && e.key === "c") {
                    e.preventDefault();
                    setCurrentShortCut("copy");
                    if (selectedNodes.length) {
                        let cNodes = selectedNodes.reduce((acc, next) => {
                            acc.push(nodes[next]);
                            return acc;
                        }, [])
                        handleCopyNodes(cNodes)
                    }
                }
                if (e.ctrlKey && e.key === "x") {
                    e.preventDefault();
                    setCurrentShortCut("cut");
                    if (selectedNodes.length) {
                        let cNodes = selectedNodes.reduce((acc, next) => {
                            acc.push(nodes[next]);
                            return acc;
                        }, [])
                        handleCutNodes(cNodes)
                    }
                    handleSidePanelClose()
                }
                if (e.ctrlKey && e.key === "v") {
                    e.preventDefault();
                    setCurrentShortCut("paste");
                    if (copiedNodes.length) {
                        handlePasteNodes(copiedNodes)
                    }
                }
            }
        }
        window.addEventListener("keydown", handleCtrlKeys);
        if (selectedNodeId || selectedCellId || outlineDsSelectedField || currentCalculation || currentSelectedGroup || currentParameter || currentSelectedStyle || currentSelectedGrp || currentSelectedMeasure || selectedCrosstab) {
            setSidePanelOpen(true);
        }
        return () => window.removeEventListener("keydown", handleCtrlKeys);
    }, [selectedNodeId, selectedCellId, currentShortCut, selectedNodes, outlineDsSelectedField, currentCalculation, currentSelectedGroup, currentParameter, currentSelectedStyle, currentSelectedGrp, currentSelectedMeasure, selectedCrosstab]);

    useEffect(() => {
        return () => {
            updateCrosstab("clearSelection");
        }
    }, [])

    return (
        <div
            className='hcr-crosstab-edit-container'
            style={{
                width: width + widthConstant,
                height: height + heightConstant,
            }}
        >
            <div className='ct-wrapper' style={{ width, height }} onClick={handleOutsideClick}>
                <div style={{ width: 'max-content' }}>
                    <CrosstabLayout {...layoutProps} />
                </div>
            </div>
            <div className='flowchart-editor-panel-body hcr-side-bar-wrapper'>
                <SidebarPanel {...sidebarPanelProps} />
            </div>
        </div >
    )
}

export default HCRCrosstabEditMode