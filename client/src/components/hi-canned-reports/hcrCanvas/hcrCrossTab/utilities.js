import { v4 as uuidV4 } from "uuid";
import { HCR_CROSSTAB_CELL_HEIGHT, HCR_CROSSTAB_CELL_WIDTH } from "../../hcr-constants";


export const createHCRCrosstabGrid = (rows = [], columns = [], measures = []) => {
    if (!rows?.length || !columns?.length || !measures?.length) return [];
    let r = Array.from({ length: (rows.length + 1) * measures.length }).map((_, i) => i)
    let c = Array.from({ length: (columns.length + 1) }).map((clmn, i) => i)
    return r.map((row) => {
        return c.map((column) => {
            return `${row}-${column}`
        })
    })
}

const renderHCRCrossTabCells = (rows = [], columns = [], measures = []) => {
    const grid = createHCRCrosstabGrid(rows, columns, measures)
    let groupColor = "#bfe1ff", totalColor = "#005fb3", whiteColor = "#fff";
    return grid.map((g, _i, _arr) => {
        return (
            <div style={{ display: 'flex' }} key={uuidV4()}>
                {
                    g.map((_, i, arr) => {
                        let bgColor = groupColor;
                        if ((i === arr.length - 1 || _i >= _arr.length - 2)) {
                            bgColor = totalColor;
                        }

                        if ((_i === 0 && i === 0) || (_i === 1 && i === 0 && measures.length > 1)) {
                            bgColor = whiteColor;
                        }

                        return (
                            <div
                                key={uuidV4()}
                                style={{
                                    width: HCR_CROSSTAB_CELL_WIDTH,
                                    height: HCR_CROSSTAB_CELL_HEIGHT,
                                    border: '1px solid gray',
                                    backgroundColor: bgColor
                                }}
                            >
                                {null}
                            </div>
                        )
                    })
                }
            </div>
        )

    })
}

const getCrosstabContainerStyles = (data = {}) => {
    const defaultPadding = 0;
    const { padding = {} } = data || {}
    return {
        paddingTop: padding.Top || defaultPadding,
        paddingBottom: padding.Bottom || defaultPadding,
        paddingLeft: padding.Left || defaultPadding,
        paddingRight: padding.Right || defaultPadding,
    }
}


export {
    renderHCRCrossTabCells,
    getCrosstabContainerStyles
}