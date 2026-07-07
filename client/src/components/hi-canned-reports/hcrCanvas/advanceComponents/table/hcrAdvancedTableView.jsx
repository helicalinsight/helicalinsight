import { useSelector } from "react-redux";
import { hcrTableBandOrder } from "../../../hcr-constants"
import { checkIfBandIsDeleted } from "../../../hcrHelperMethods";
import { getActiveBands, getAvailableBands, getTableBandStyle, isGroupBand, makeCellId } from "../../hcrCanvasPaneHelperMethods";
import HCRChartsComponent from "../../hcrCharts/hcrChartsComponent";
import HCRCrossTabComponent from "../../hcrCrossTab/hcrCrossTabComponent";
import { ImageNode, LineNode, PageBreakNode, TextNode } from "../../nodes";
import HCRAdvancedTableComponent from "./hcrAdvancedTableComponent";

const CategoryNode = ({ node }) => {
    const { category = "" } = node || {}
    return <div className='main-node'>
        {{
            text: <TextNode data={node} />,
            image: <ImageNode data={node} />,
            line: <LineNode data={node} />,
            pageBreak: <PageBreakNode data={node} />,
            crosstab: <HCRCrossTabComponent data={node} />,
            chart: <HCRChartsComponent data={node} />,
            advancedTable: <HCRAdvancedTableComponent data={node} />
        }[category]}
    </div>

}

const HCRAdvancedTable = (props = {}) => {
    const { data = {} } = props || {}
    const { bands = {}, columnOrder = [], cells = {}, nodes = {}, tableConfig: hcrTableConfig = {}, isAppliedClicked, id } = data || {}
    const availableBands = getActiveBands(bands, cells);

    const { borderStyle = "all", bordersColor = "#000000" } = hcrTableConfig || {}
    const outsideBorderStyle = {
        borderColor: borderStyle === "outside" ? bordersColor : "transparent"
    }
    const activeTab = useSelector((state) => state.cannedReports.present.hcrTabData.panes.find(
        (pane) => pane.key === state.cannedReports.present.hcrTabData.activeKey
    )) || {};

    const { tableStyles = [] } = activeTab || {}


    const getTData = (bandType, isFirstRow, groupField = null) => {
        return columnOrder.map((colId, colIndex, _arr) => {
            const cellId = makeCellId(colId, bandType, groupField);
            const isFirstCol = colIndex === 0;
            const cell = cells[cellId] || {};
            const styles = getTableBandStyle({ bandType, tableStyles, cell, isFirstRow, isFirstCol });

            if (!cell) return <td key={colId} className="hcr-table-empty-data-cell" />
            const deleted = cell.deleted;
            let widthFactor = 2, heightFactor = 1;
            return (
                <td
                    key={cellId}
                    className={`hcr-table-data-cell ${deleted}`}
                    style={{
                        width: cell.width,
                        height: cell.height,
                        minWidth: cell.width,
                        minHeight: cell.height,
                        ...(styles || {}),
                        ...(deleted ? { backgroundColor: "transparent" } : {})
                    }}
                >
                    <div style={{
                        ...(cell?.nodeIds?.length > 1 ? { display: "flex" } : {})
                    }}>
                        {cell?.nodeIds?.map((nodeId) => {
                            const node = nodes[nodeId];
                            if (!node) return null;
                            return (
                                <CategoryNode key={nodeId} node={node} />
                            )
                        })}
                    </div>
                </td>
            )
        })
    }

    return (
        <table
            className={`hcr-table hcr-table-view-mode ${borderStyle}`}
            style={isAppliedClicked ? outsideBorderStyle : {}}
        >
            <tbody>
                {
                    availableBands.map((bandType, rowIndex, arrLength) => {
                        const isGroup = isGroupBand(bandType);
                        const isFirstRow = rowIndex === 0;
                        if (isGroup) {
                            const groupFields = bands?.[bandType]?.groupFields || [];
                            return groupFields.map((groupField, index) => {
                                const isDeleted = checkIfBandIsDeleted(bandType, cells, groupField);
                                if (isDeleted) return null;
                                return (
                                    <tr key={`${bandType}-${groupField}-${index}`} className={`hcr-table-band-row`}>{getTData(bandType, isFirstRow, groupField)}</tr>
                                )
                            })
                        }
                        const isDeleted = checkIfBandIsDeleted(bandType, cells);
                        if (isDeleted) return null;
                        return (
                            <tr key={bandType} className={`hcr-table-band-row`}>{getTData(bandType, isFirstRow)}</tr>
                        )
                    })
                }
            </tbody>
        </table>
    )
}

export default HCRAdvancedTable