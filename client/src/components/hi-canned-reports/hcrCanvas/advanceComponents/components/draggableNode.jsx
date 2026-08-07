import { useState } from 'react';
import { useDrag } from 'react-dnd';
import { HCR_CROSSTAB_NODE, HCR_TABLE_NODE, hcrContextMenuTypes } from '../../../hcr-constants';
import HCRChartsComponent from '../../hcrCharts/hcrChartsComponent';
import HCRCrossTabComponentV2 from '../../hcrCrossTab/hcrCrossTabComponentv2';
import { ImageNode, LineNode, PageBreakNode } from '../../nodes';
import TextNode from '../../nodes/textNode';
import { HCR_NODE_RESIZE_HANDLES } from '../contants';
import HCRAdvancedTableComponent from '../table/hcrAdvancedTableComponent';
import { OutlineCrosstabContextMenu, OutlineTableContextMenu } from './outlinePanel/outlineContextMenu';

const DraggableNode = (props = {}) => {
    const { node = {}, cellId, onNodeClick = () => { }, selectedNodes = [], tableData = {}, crosstabData = {}, category, copiedNodes = [], mode = "edit" } = props || {}
    const isTable = category === "advancedTable",
        isCrosstab = category === "crosstabv2",
        editable = mode === "edit";

    const isSelected = selectedNodes.includes(node.id);
    const [visible, setVisible] = useState(null)
    const nodeType = isCrosstab ? HCR_CROSSTAB_NODE : HCR_TABLE_NODE;

    const [{ isDragging }, dragRef] = useDrag({
        type: nodeType,
        item: {
            type: nodeType,
            nodeId: node.id,
            sourceCellId: cellId
        },
        collect: (monitor) => ({
            isDragging: monitor.isDragging(),
        })
    })

    const handleClick = (e) => {
        e.stopPropagation();
        onNodeClick(e, node)
    }

    return (
        <div
            ref={dragRef}
            className={`draggable-node ${isDragging ? 'dragging' : ''} ${isSelected ? 'selected' : ''}`}
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
                                <TextNode data={node} />
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


            {isSelected ? (
                HCR_NODE_RESIZE_HANDLES.map(({ id, style, cursor }) => (
                    <div
                        className={`resize-handle resize-handle--${id}`}
                        key={id}
                        style={{ ...style, cursor }}
                    />
                ))
            )
                : null}
        </div>
    )
}

export default DraggableNode