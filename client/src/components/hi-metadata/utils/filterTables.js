import cloneDeep from 'lodash-es/cloneDeep';
export const filterTables = ({ tables, searchText = false }) => {
    tables = cloneDeep(tables)
    let filteredTables = {}
    if (searchText === false || typeof searchText !== 'string')
        return tables
    if (!searchText.length) {
        return tables
    }
    let searchTable = searchText.toLowerCase()
        , searchColumn = searchText.toLowerCase()
    let exactTableMatch = false
    if (searchText.includes('.')) {
        exactTableMatch = true

        searchTable = searchText.split('.')[0].toLowerCase() //.match(searchedVal.toString().toLowerCase())
        searchColumn = searchText.split('.')[1].toLowerCase()
    }

    Object.keys(tables).map(key => {

        if (!exactTableMatch && tables[key].alias.toLowerCase().includes(searchTable) && tables[key].alias !== searchTable) {
            filteredTables[key] = tables[key]
            // filteredTables[key].columns = filterColumns({
            //     columns: filteredTables[key].columns,
            //     searchText: searchColumn
            // })
        } else if (!exactTableMatch && !tables[key].alias.toLowerCase().includes(searchTable)) {
            filteredTables[key] = tables[key]
            filteredTables[key].columns = filterColumns({
                columns: filteredTables[key].columns,
                searchText: searchColumn
            })
        } else if (!exactTableMatch && tables[key].alias === (searchTable)) {
            filteredTables[key] = tables[key]
            // filteredTables[key].columns = filterColumns({
            //     columns: filteredTables[key].columns,
            //     searchText: searchColumn
            // })
        } else if (exactTableMatch && tables[key].alias === searchTable) {

            filteredTables[key] = tables[key]
            filteredTables[key].columns = filterColumns({
                columns: filteredTables[key].columns,
                searchText: searchColumn
            })
        }
        if (key in filteredTables && !filteredTables[key]?.columns && !filteredTables[key].alias.toLowerCase().includes(searchTable)) {
            // if there are no columns then filter table name
            delete filteredTables[key]
        } else if (key in filteredTables && filteredTables[key]?.columns && !Object.keys(filteredTables[key]?.columns).length) {
            // if there are columns
            delete filteredTables[key]
        }

    }
    )
    return filteredTables
}
const filterColumns = ({ columns, searchText }) => {
    if (!columns) {
        return columns
    }
    let colResult = {}
    if (typeof searchText !== 'string' || !searchText.length) {
        return
    }
    Object.keys(columns).forEach(colKey => {
        if (columns[colKey].alias.toLowerCase().includes(searchText)) {
            colResult[colKey] = columns[colKey]
        }
    }
    )
    return colResult
}
