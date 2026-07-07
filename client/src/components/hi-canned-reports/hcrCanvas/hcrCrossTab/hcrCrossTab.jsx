import { v4 as uuidV4 } from "uuid";
import "./hcrCrosstab.scss";
import { getCrosstabContainerStyles, renderHCRCrossTabCells } from "./utilities";

const HCRCrossTab = (props = {}) => {
    const { data } = props || {}
    const {
        columnHeaders = [],
        rowHeaders = [],
        selectedMeasures = [],
        selectedRowFields = [],
        selectedColumnFields = [],
        padding = {},
        groupChildren = []
    } = data

    const template = (
        <table className="crosstab-template">
            <thead>
                <tr>
                    <th></th>
                    <th></th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td></td>
                    <td></td>
                    <td></td>
                </tr>
                <tr>
                    <td></td>
                    <td></td>
                    <td></td>
                </tr>
            </tbody>
        </table>
    )
    const showActualCrosstab = columnHeaders.length && rowHeaders.length

    const emptyBoxStyles = (width, height) => {
        return {
            minWidth: width,
            maxWidth: width,
            height,
            background: "#ffffff",
        }
    }

    const renderColumnHeader = (node, totalBGColor) => {
        const levelRows = [];
        const currentRow = (
            <div
                key={uuidV4()}
                style={{
                    display: 'flex',
                    height: `${node.items[0].height}px`,
                }}

            >
                {node.items.map((item, index) => {
                    return (
                        <div
                            key={`${node.key}-item-${index}`}
                            style={{
                                minWidth: item.width,
                                maxWidth: item.width,
                                height: item.height,
                                border: '1px solid gray',
                                backgroundColor: index === 0 ? "#f0f8ff" : totalBGColor
                            }}
                        >
                            {null}
                        </div>
                    );
                })}
            </div>
        );

        levelRows.push(currentRow);
        if (node?.children?.length) {
            node.children.forEach(child => {
                const childRows = renderColumnHeader(child, "#bfe1ff");
                levelRows.push(...childRows);
            });
        }
        return levelRows;
    };

    const renderRowHeader = (node, measures, totalBGColor) => {
        const levelColumns = [];
        const currentColumn = (
            <div
                key={`level-${node.key}`}
                style={{
                    display: 'flex',
                    flexDirection: 'column',
                    width: node.items[0].width,
                    height: node.items[0].height
                }}
            >
                {node.items.map((item, i) => {
                    return (
                        <div
                            key={uuidV4()}
                            style={{
                                minWidth: item.width,
                                maxWidth: item.width,
                                minHeight: item.height,
                                maxHeight: item.height,
                                border: '1px solid gray',
                                backgroundColor: i === 0 ? "#f0f8ff" : totalBGColor
                            }}
                        >
                            {null}
                        </div>
                    );
                })}
            </div>
        )
        levelColumns.push(currentColumn);
        if (node?.children?.length) {
            node.children.forEach(child => {
                const childColumns = renderRowHeader(child, measures, "#bfe1ff");
                levelColumns.push(...childColumns);
            });
        }
        return levelColumns;
    };

    return (
        <div style={getCrosstabContainerStyles(data)}>
            {!showActualCrosstab ? template : null}
            {showActualCrosstab ?
                <>
                    {/* column group headers */}
                    {columnHeaders.map((rootNode, index) => {
                        const { emptyCellHeight } = rootNode
                        const { emptyCellWidth } = rowHeaders[0]
                        return (
                            <div key={`root-node-${index}`} style={{ display: 'flex' }}>
                                <div style={emptyBoxStyles(emptyCellWidth, emptyCellHeight)}></div>
                                <div key={`root-${index}`}>
                                    {renderColumnHeader(rootNode, "#005fb3")}
                                </div>
                            </div>
                        )
                    })}

                    {/* row group headers */}
                    {rowHeaders.map((rootNode) => {
                        const { key } = rootNode
                        return (
                            <div key={`root-node-${key}`} style={{ display: 'flex' }}>
                                {renderRowHeader(rootNode, selectedMeasures, "#005fb3")}
                                <div>
                                    {renderHCRCrossTabCells(selectedRowFields, selectedColumnFields, selectedMeasures)}
                                </div>
                            </div>
                        )
                    })}
                </>
                : null}
        </div >
    );
}

export default HCRCrossTab