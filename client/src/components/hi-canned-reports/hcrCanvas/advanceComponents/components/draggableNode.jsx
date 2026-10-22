import { useCallback, useState } from 'react';
import { useDrag } from 'react-dnd';
import { useDispatch } from 'react-redux';
import { HCR_CROSSTAB_NODE, HCR_TABLE_NODE, hcrContextMenuTypes } from '../../../hcr-constants';
import HCRChartsComponent from '../../hcrCharts/hcrChartsComponent';
import HCRCrossTabComponentV2 from '../../hcrCrossTab/hcrCrossTabComponentv2';
import { ImageNode, LineNode, PageBreakNode } from '../../nodes';
import TextNode from '../../nodes/textNode';
import { HCR_NODE_RESIZE_HANDLES } from '../contants';
import HCRAdvancedTableComponent from '../table/hcrAdvancedTableComponent';
import { OutlineCrosstabContextMenu, OutlineTableContextMenu } from './outlinePanel/outlineContextMenu';

const DraggableNode = (props = {}) => {
    const {
        node = {},
        cell = {},
        cellId,
        onNodeClick = () => { },
        selectedNodes = [],
        tableData = {},
        crosstabData = {},
        category,
        copiedNodes = [],
        mode = "edit",
        resize = false,
        updateNode = () => { },
        isFreeLayout = false
    } = props || {}
    const isTable = category === "advancedTable",
        isCrosstab = category === "crosstabv2",
        editable = mode === "edit";

    const isSelected = selectedNodes.includes(node.id);
    const [visible, setVisible] = useState(null)
    const nodeType = isCrosstab ? HCR_CROSSTAB_NODE : HCR_TABLE_NODE;
    const dispatch = useDispatch();

    const [{ isDragging }, dragRef] = useDrag({
        type: nodeType,
        item: {
            isFreeLayout,
            type: nodeType,
            nodeId: node.id,
            sourceCellId: cellId
        },
        collect: (monitor) => ({
            isDragging: monitor.isDragging(),
        })
    })

    const freePlace = isCrosstab && isFreeLayout;
    const freeLayoutStyle = freePlace ? {
        position: "absolute",
        left: Number(node.x) || 0,
        top: Number(node.y) || 0,
        width: node.width,
        height: node.height,
        zIndex: isDragging ? 10 : (isSelected ? 6 : (node.zIndex || 1)),
        cursor: editable ? "move" : undefined,
    } : undefined;

    const handleClick = (e) => {
        e.stopPropagation();
        onNodeClick(e, node)
    }

    const handleResizeWidth = useCallback((e, handleId = "") => {
        e.stopPropagation();
        e.preventDefault();
        const startX = e.clientX;
        const startWidth = Number(node.width) || 0;
        const isWestHandle = handleId.includes("w");
        const onMouseMove = (mouseEvent) => {
            const delta = mouseEvent.clientX - startX;
            const width = startWidth + (isWestHandle ? -delta : delta);
            updateNode("resizeNode", { width: Math.max(10, width), nodeId: node.id })
        }

        const onMouseUp = () => {
            document.removeEventListener('mousemove', onMouseMove);
            document.removeEventListener('mouseup', onMouseUp);
        }

        document.addEventListener('mousemove', onMouseMove);
        document.addEventListener('mouseup', onMouseUp);


    }, [cellId, cell?.width, dispatch]);

    const handleResizeHeight = useCallback((e, handleId = "") => {
        e.stopPropagation();
        e.preventDefault();
        const startY = e.clientY;
        const startHeight = Number(node.height) || 0;
        const isNorthHandle = handleId.includes("n")


        const onMouseMove = (mouseEvent) => {
            const delta = mouseEvent.clientY - startY;
            const height = startHeight + (isNorthHandle ? -delta : delta);
            updateNode("resizeNode", { height: Math.max(10, height), nodeId: node.id })
        }

        const onMouseUp = () => {
            document.removeEventListener('mousemove', onMouseMove);
            document.removeEventListener('mouseup', onMouseUp);
        }

        document.addEventListener('mousemove', onMouseMove);
        document.addEventListener('mouseup', onMouseUp);


    }, [cellId, cell?.height, dispatch]);


    const handleResizeBoth = (e, handleId) => {
        handleResizeWidth(e, handleId);
        handleResizeHeight(e, handleId);
    }

    const controls = (
        <>
            {isSelected ? (
                HCR_NODE_RESIZE_HANDLES.map(({ id, style, cursor, resizeType }) => (
                    <div
                        className={`resize-handle resize-handle--${id}`}
                        key={id}
                        style={{ ...style, cursor }}
                        onMouseDown={(e) => {
                            if (resize && isCrosstab) {
                                switch (resizeType) {
                                    case "width":
                                        handleResizeWidth(e, id);
                                        break;
                                    case "height":
                                        handleResizeHeight(e, id);
                                        break;
                                    case "both":
                                        handleResizeBoth(e, id);
                                        break;
                                }
                            }
                        }}
                    />
                ))
            )
                : null}
        </>
    )

    return (
        <div
            ref={dragRef}
            className={`draggable-node ${isDragging ? 'dragging' : ''} ${isSelected ? 'selected' : ''}${freePlace ? ' free-placed' : ''}`}
            style={freeLayoutStyle}
            onClick={handleClick}
        >
            {isTable &&
                <OutlineTableContextMenu
                    onVisibleChange={(value) => setVisible(value)}
                    visible={visible}
                    title={(
                        <div className='main-node'>
                            {{
                                text: <TextNode data={node} />,
                                image: <ImageNode data={node} />,
                                line: <LineNode data={node} />,
                                pageBreak: <PageBreakNode data={node} />,
                                crosstabv2: <HCRCrossTabComponentV2 data={node} />,
                                chart: <HCRChartsComponent data={node} />,
                                advancedTable: <HCRAdvancedTableComponent data={node} />
                            }[node.category]}
                        </div>
                    )}
                    menuType={hcrContextMenuTypes.NODE}
                    tableData={tableData}
                    nodeId={node.id}
                    copiedNodes={copiedNodes}
                    cb={() => setVisible(false)}
                    cellId={cellId}
                />}

            {isCrosstab ?
                editable ?
                    <OutlineCrosstabContextMenu
                        onVisibleChange={(value) => setVisible(value)}
                        visible={visible}
                        title={(
                            <div className='main-node'>
                                <div style={{ width: node.width, height: node.height, position: "relative" }}>
                                    <TextNode data={node} />
                                    {controls}
                                </div>

                            </div>
                        )}
                        menuType={hcrContextMenuTypes.NODE}
                        nodeId={node.id}
                        copiedNodes={copiedNodes}
                        cb={() => setVisible(false)}
                        cellId={cellId}
                        crosstabData={crosstabData}
                    /> :
                    <div className='main-node'>
                        <TextNode data={node} />
                    </div>
                : null
            }

            {!isCrosstab ? controls : null}
        </div>
    )
}

export default DraggableNode