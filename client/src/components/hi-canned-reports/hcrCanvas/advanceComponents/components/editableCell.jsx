import { useCallback, useState } from 'react';
import { useDrop } from 'react-dnd';
import { useDispatch } from 'react-redux';
import { hcrActions } from '../../../../../redux/actions';
import { HCR_OUTSIDE_NODE, HCR_TABLE_NODE, hcrContextMenuTypes } from '../../../hcr-constants';
import DraggableNode from './draggableNode';
import { HCRTableContextMenu } from './tableOutlinePanel';

const EditableCell = (props = {}) => {
    const {
        cells = {},
        nodes = {},
        cellId,
        selectedCells = [],
        id,
        selectedNodes = [],
    } = props || {}

    const {
        onNodeClick = () => { },
        cellStyles = {},
        onCloseSidePanel = () => { },
        copiedNodes,
        ...tableData
    } = props || {}
    const dispatch = useDispatch();
    const cell = cells[cellId]
    const isSelected = selectedCells?.includes(cellId);
    const [visible, setVisible] = useState(false)
    let widthFactor = 2, heightFactor = 1;

    const { columnId, bandType } = cell || {};

    const updateNode = (actionType, otherKeyValuePairs = {}) => {
        dispatch(hcrActions.hcrUpdateCanvasTabComponent({
            actionType,
            id,
            columnId,
            cellId,
            bandType,
            ...(otherKeyValuePairs || {})
        }))
    }

    const [{ isOver, canDrop }, dropRef] = useDrop({
        accept: [HCR_OUTSIDE_NODE, HCR_TABLE_NODE],
        drop: (item) => {
            if (item.type === HCR_OUTSIDE_NODE) {
                const { record } = item || {}
                updateNode("addNode", {
                    record
                })
            } else if (item.type === HCR_TABLE_NODE) {
                if (item.sourceCellId !== cellId) {
                    updateNode("moveNode", {
                        nodeId: item.nodeId,
                        targetCellId: cellId
                    })
                }
            }
        },
        canDrop: () => {
            if (cell.deleted) return false;
            return true
        },
        collect: (monitor) => ({
            isOver: monitor.isOver(),
            canDrop: monitor.canDrop(),
        })
    })

    const dropHighlight = isOver && canDrop ? 'drop-active' : canDrop ? 'drop-ready' : '';
    const deleted = cell.deleted ? "deleted-cell" : ""

    const handleCellClick = (e) => {
        e.stopPropagation();
        updateNode("selectCell")

        if (e.ctrlKey) {
            const isPresent = selectedCells.includes(cellId);
            if (isPresent && selectedCells.length === 1) {
                onCloseSidePanel()
            }
            dispatch(hcrActions.hcrUpdateCanvasTabComponent({
                actionType: isPresent ? "removeSelectedCell" : "selectCells",
                id,
                selectedCells: [...selectedCells, cellId],
                cellId
            }))
        } else {
            dispatch(hcrActions.hcrUpdateCanvasTabComponent({
                actionType: "selectCells",
                id,
                selectedCells: [cellId],
            }))
        }
        onNodeClick()
    }

    const handleResizeWidth = useCallback((e) => {
        e.stopPropagation();
        e.preventDefault();
        const startX = e.clientX;
        const startWidth = cell.width;

        const onMouseMove = (mouseEvent) => {
            const delta = mouseEvent.clientX - startX;
            updateNode("resizeCell", { width: startWidth + delta })
        }

        const onMouseUp = () => {
            document.removeEventListener('mousemove', onMouseMove);
            document.removeEventListener('mouseup', onMouseUp);
        }

        document.addEventListener('mousemove', onMouseMove);
        document.addEventListener('mouseup', onMouseUp);


    }, [cellId, cell?.width, dispatch]);

    const handleResizeHeight = useCallback((e) => {
        e.stopPropagation();
        e.preventDefault();
        const startY = e.clientY;
        const startHeight = cell.height;

        const onMouseMove = (mouseEvent) => {
            const delta = mouseEvent.clientY - startY;
            updateNode("resizeCell", { height: startHeight + delta })
        }

        const onMouseUp = () => {
            document.removeEventListener('mousemove', onMouseMove);
            document.removeEventListener('mouseup', onMouseUp);
        }

        document.addEventListener('mousemove', onMouseMove);
        document.addEventListener('mouseup', onMouseUp);


    }, [cellId, cell?.height, dispatch]);


    const handleResizeBoth = (e) => {
        handleResizeWidth(e);
        handleResizeHeight(e);
    }

    if (!cell) return null

    return (
        <td
            ref={dropRef}
            className={`editable-cell ${isSelected ? 'selected' : ''} ${dropHighlight} ${deleted}`}
            style={{
                ...cellStyles,
                width: cell.width,
                height: cell.height,
                minWidth: cell.width,
                minHeight: cell.height,
                position: "relative",
            }}
            onClick={handleCellClick}
        >
            <HCRTableContextMenu
                onVisibleChange={(value) => setVisible(value)}
                visible={visible}
                title={<div style={{ width: "100%", height: "100%", position: 'absolute' }} onContextMenu={handleCellClick}>{null}</div>}
                menuType={hcrContextMenuTypes.CELL}
                cb={() => setVisible(false)}
                {...{
                    columnId,
                    cellId,
                    bandType,
                    deleted,
                    copiedNodes,
                    tableData
                }}
            />
            <div className="cell-elements">
                {cell?.nodeIds?.map((nodeId) => {
                    const node = nodes[nodeId] || {};
                    return <DraggableNode key={nodeId} {...{ node, cellId, updateNode, selectedNodes, tableData, copiedNodes }} onNodeClick={onNodeClick} />
                })}
            </div>

            <div className='resize-handle-right' onMouseDown={handleResizeWidth} />
            <div className='resize-handle-bottom' onMouseDown={handleResizeHeight} />
            <div className='resize-handle-corner' onMouseDown={handleResizeBoth} />
        </td>
    )
}

export default EditableCell