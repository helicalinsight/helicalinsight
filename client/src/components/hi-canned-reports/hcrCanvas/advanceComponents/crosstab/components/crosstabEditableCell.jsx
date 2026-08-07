import { useState } from 'react';
import { useDrop } from 'react-dnd';
import { useDispatch } from 'react-redux';
import { hcrActions } from '../../../../../../redux/actions';
import { HCR_CROSSTAB_NODE, hcrContextMenuTypes } from '../../../../hcr-constants';
import DraggableNode from '../../components/draggableNode';
import { OutlineCrosstabContextMenu } from '../../components/outlinePanel/outlineContextMenu';

const CrosstabEditableCell = (props = {}) => {
    const {
        nodes = {},
        componentId,
        label,
        cell = {},
        gridColumn,
        gridRow,
        cellStyles = {},
        onNodeClick = () => { },
        selectedCells = [],
        selectedNodes = [],
        onCloseSidePanel = () => { },
        crosstabData = {},
        copiedNodes = [],
        measureCell = false,
        mode = "view"
    } = props || {}
    const editable = mode === "edit";
    const { id: cellId, nodeIds = [] } = cell || {};
    const { category } = crosstabData || {}
    const dispatch = useDispatch();
    const [visible, setVisible] = useState(false)
    const isSelected = selectedCells?.includes(cellId);
    const isCrosstabHeaderCell = cellId === "crosstab_header_cell";

    const [{ isOver, canDrop }, dropRef] = useDrop({
        accept: HCR_CROSSTAB_NODE,
        drop: (item) => {
            updateCrosstab("moveNode", {
                targetCellId: cellId,
                nodeId: item.nodeId,
                sourceCellId: item.sourceCellId
            })
        },
        canDrop: () => {
            return !isCrosstabHeaderCell;
        },
        collect: (monitor) => ({
            isOver: monitor.isOver(),
            canDrop: monitor.canDrop(),
        })
    })

    const dropHighlight = isOver && canDrop ? 'drop-active' : canDrop ? 'drop-ready' : '';


    function updateCrosstab(actionType, payload = {}) {
        dispatch(hcrActions.hcrUpdateCrosstabComponent({
            id: componentId,
            cellId,
            actionType,
            ...payload
        }))
    }


    const handleCellClick = (e) => {
        e.stopPropagation();
        if (isCrosstabHeaderCell) return;
        if (e.ctrlKey) {
            const isPresent = selectedCells.includes(cellId);
            if (isPresent && selectedCells.length === 1) {
                onCloseSidePanel()
            }
            updateCrosstab(isPresent ? "removeSelectedCell" : "selectCells",
                { selectedCells: [...selectedCells, cellId] })
        } else {
            updateCrosstab("selectCells", { selectedCells: [cellId] })
        }
        onNodeClick()
    }

    const handleNodeClick = (e, node) => {
        onNodeClick(e, node)
    }

    return (
        <div
            ref={dropRef}
            key={cellId}
            className={`editable-cell ${isSelected ? 'selected' : ''} ${dropHighlight}`}
            style={{
                position: "relative",
                gridColumn,
                gridRow,
                ...cellStyles,
            }}
            onClick={handleCellClick}
        >
            {editable && <OutlineCrosstabContextMenu
                onVisibleChange={(value) => setVisible(value)}
                visible={visible}
                title={<div style={{ width: "100%", height: "100%", position: 'absolute' }} onContextMenu={handleCellClick}>{null}</div>}
                menuType={hcrContextMenuTypes.CELL}
                copiedNodes={copiedNodes}
                cb={() => setVisible(false)}
                cellId={cellId}
                crosstabData={crosstabData}
            />}
            <div className="cell-elements">
                {nodeIds.map((id, i) => {
                    const node = nodes[id];
                    return (
                        <>
                            {i > 0 ? <div style={{ height: 1, background: "#b9c7d8", width: "100%" }}></div> : null}
                            <DraggableNode
                                key={id}
                                selectedNodes={selectedNodes}
                                onNodeClick={handleNodeClick}
                                node={node}
                                cellId={cellId}
                                category={category}
                                crosstabData={crosstabData}
                                copiedNodes={copiedNodes}
                                mode={mode}
                            />
                        </>
                    )
                })}
            </div>
        </div>
    )
}

export default CrosstabEditableCell