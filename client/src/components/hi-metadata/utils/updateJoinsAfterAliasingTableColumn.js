import { cloneDeep } from 'lodash-es'
export const updateJoinsAfterAliasingTableColumn = ({ joins, tables = false }) => {
    joins = cloneDeep(joins)
    tables = cloneDeep(tables)
    if (!tables) return joins
    const getAlias = ({ info, tables }) => {
        if (!info.table || !info.column) {
            return info
        }
        let currentTable = tables[info.table]
        if (!currentTable) {
            return info
        }
        info.tableAlias = currentTable.alias
        if (currentTable?.columns) {
            info.columnAlias = currentTable.columns[info.column]?.alias || info.column
        }
        return info
    }

    let updatedJoins = joins.map(eachJoin => {
        if (eachJoin.noAccess === 'true') return false
        if (!(eachJoin.left) || !(eachJoin.right)) return false
        eachJoin.left = getAlias({ info: eachJoin.left, tables })
        eachJoin.right = getAlias({ info: eachJoin.right, tables })
        return eachJoin
    }).filter(Boolean)
    return updatedJoins
}
