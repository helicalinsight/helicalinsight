import { useState } from 'react';
import { useDrag } from 'react-dnd';
import { HCR_TABLE_NODE, hcrContextMenuTypes } from '../../../hcr-constants';
import HCRChartsComponent from '../../hcrCharts/hcrChartsComponent';
import HCRCrossTabComponent from '../../hcrCrossTab/hcrCrossTabComponent';
import { ImageNode, LineNode, PageBreakNode } from '../../nodes';
import TextNode from '../../nodes/textNode';
import { HCR_NODE_RESIZE_HANDLES } from '../contants';
import HCRAdvancedTableComponent from '../table/hcrAdvancedTableComponent';
import { HCRTableContextMenu } from './tableOutlinePanel';

const DraggableNode = (props = {}) => {
    const { node = {}, cellId, onNodeClick = () => { }, selectedNodes = [], tableData = {}, copiedNodes = [] } = props || {}

    const isSelected = selectedNodes.includes(node.id);
    const [visible, setVisible] = useState(null)

    const [{ isDragging }, dragRef] = useDrag({
        type: HCR_TABLE_NODE,
        item: {
            type: HCR_TABLE_NODE,
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
            <HCRTableContextMenu
                onVisibleChange={(value) => setVisible(value)}
                visible={visible}
                title={(
                    <div className='main-node'>
                        {{
                            text: <TextNode data={node} />,
                            image: <ImageNode data={node} />,
                            line: <LineNode data={node} />,
                            pageBreak: <PageBreakNode data={node} />,
                            crosstab: <HCRCrossTabComponent data={node} />,
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
            />


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