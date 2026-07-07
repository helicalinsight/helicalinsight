import { cloneDeep } from 'lodash-es';
import { mergeDeep } from '../../../utils/merge-deep';
import { v4 } from 'uuid'
import { handleDSTableCheck } from './handleDSTableCheck'
import { metadataActions } from '../../../redux/actions/metadata.actions'
import { findNestedObj } from './findNestedObject'
import {isObject} from '../../../utils/is-object'

const toFindDuplicates = arry => arry.filter((item, index) => arry.indexOf(item) !== index)

// This function is not used in the application.


export const syncFetchedTablesWithStoreTables = ({ storeTables, fetchedTables, fetchedMetadata = false, store = false }) => {
    let storeTableKeys = Object.keys(storeTables)
    let fetchedTableKeys = Object.keys(fetchedTables)
    let newTables = fetchedTableKeys.filter(eachKey => !storeTableKeys.includes(eachKey))
    newTables.map(key => {
        storeTables[key] = updateTableData({ table: fetchedTables[key], key, fetchedMetadata, store })
        let requiredObject = findNestedObj(store.getState().metadata.present.datasourceListToRender, 'id', fetchedTables[key].id)
        storeTables[key] = { ...storeTables[key], ...requiredObject }
        checkTable({ value: true, record: requiredObject, store }) // checkbox UI related
    })
    Object.keys(storeTables).map(key => {
        if (!fetchedTables[key]) {
            checkTable({ value: false, record: storeTables[key], store })
            delete storeTables[key]
            return
        }
        if (storeTables[key]?.columns && fetchedTables[key]?.columns) {
            let updatedIds = updateIds({ storeCols: storeTables[key].columns, fetchedCols: fetchedTables[key].columns }) // 5212
            if (updatedIds?.fetchedCols && updatedIds?.storeCols) {
                storeTables[key].columns = updatedIds.storeCols
                fetchedTables[key].columns = updatedIds.fetchedCols
            }
        }
        /**
         * this is to be checked for columns
         */
        let updatedColumns = updateColumns(cloneDeep({ storeColumns: storeTables[key].columns, fetchedColumns: fetchedTables[key].columns, tableKey: key, connId: storeTables[key].connId }))
        if (updatedColumns?.storeColumns && updatedColumns?.fetchedColumns) {
            storeTables[key].columns = updatedColumns.storeColumns
            fetchedTables[key].columns = updatedColumns.fetchedColumns
        }
        storeTables[key] = mergeDeep(storeTables[key], fetchedTables[key])
    })
    let columnsToMerge = {}
    Object.keys(storeTables).map(tableKey => {
        let columns = storeTables[tableKey].columns
        if (storeTables[tableKey].columns) {
            let duplicateAliases = toFindDuplicates(Object.values(storeTables[tableKey].columns).map(col => col.alias))
            if (duplicateAliases.length) {
                let columnsWithSameAlias = Object.keys(columns).filter(colKey => duplicateAliases.indexOf(columns[colKey].alias) !== -1)
                columnsToMerge.columns = columnsWithSameAlias
                columnsToMerge.table = tableKey
            }
        }
    }
    )
    let columnKeyToDelete = false
        , columnToUse = false
    if (!columnsToMerge.table) {
        return storeTables
    }
    let columns = storeTables[columnsToMerge.table].columns
    Object.keys(columns).forEach(colKey => {
        if (columnsToMerge.columns.indexOf(colKey) === -1)
            return
        let colValue = columns[colKey]
        if (colValue.columnId) {
            columnKeyToDelete = colKey
        }
        else {
            columnToUse = colKey
        }

    }
    )
    storeTables[columnsToMerge.table].columns[columnToUse] = { ...storeTables[columnsToMerge.table].columns[columnToUse], ...storeTables[columnsToMerge.table].columns[columnKeyToDelete] }
    delete storeTables[columnsToMerge.table].columns[columnKeyToDelete]
    return storeTables
}

const updateTableData = ({ table, key, fetchedMetadata, store }) => {
    let uuid = v4()
    table = cloneDeep(table)
    let connId = false
    let storeDs = store?.getState()?.metadata?.present.dataSource
    let currentDs = storeDs.filter(ds => ds.id === fetchedMetadata.dataSource.id && ds.type === fetchedMetadata.dataSource.type)[0]
    if (currentDs?.connId) {
        connId = currentDs.connId
    }
    table.key = uuid
    table.uuid = uuid
    table.connId = connId
    table.dataSource = currentDs
    table.columnsFetched = true
    table.keyName = key
    table.nameWithConnId = `${key}_${connId}`
    table.data = { id: table.dataSource.id, type: table.dataSource.type }
    table.selected = true
    table.category = table.type === 'view' ? 'view' : 'table'
    let columnKeys = Object.keys(table.columns)
    let columns = table.columns
    columnKeys.map(colKey => {
        let data = columns[colKey]
        data.tableKey = key
        data.originalName = data.alias
        data.originalId = data.columnId
        data._columnId = data.columnId
        data.connId = connId
        data.duplicateIndex = 0
        data.columnKey = colKey
        data.uuid = v4()
        data.category = 'column'
        delete data.columnId
        table.columns[colKey] = data
    })
    return table
}

const updateColumns = ({ storeColumns, fetchedColumns, tableKey, connId }) => {
    if (!isObject(storeColumns) || !isObject(fetchedColumns)) return {}
    let storeColumnsKeys = Object.keys(storeColumns)
    let fetchedColKeys = Object.keys(fetchedColumns)
    let nowColKeys = fetchedColKeys.filter(eachKey => !storeColumnsKeys.includes(eachKey))
    let deletedCols = storeColumnsKeys.filter(eachKey => !fetchedColKeys.includes(eachKey))
    nowColKeys && nowColKeys.map(key => {
        let data = fetchedColumns[key]
        storeColumns[key] = fetchedColumns[key]
        // storeColumns[key].tableKey = tableKey
        data.tableKey = tableKey
        data.originalName = data.alias
        data.originalId = data.columnId
        data._columnId = data.columnId
        data.connId = connId
        data.duplicateIndex = 0
        data.columnKey = key
        data.uuid = v4()
        data.category = 'column'
        delete data.columnId
        // table.columns[colKey] = data
        fetchedColumns[key] = data
    })
    if (deletedCols) {
        deletedCols.map(col => {
            delete storeColumns[col]
        })
    }
    return { storeColumns, fetchedColumns }

}

const checkTable = ({ value, record, store }) => {
    try {
        handleDSTableCheck({
            checked: value,
            record: record,
            dispatch: store.dispatch,
            dsListToRender: store.getState().metadata.present.datasourceListToRender,
        });
        store.dispatch(
            metadataActions.updateMetadataState({
                mode: "selectedTables",
                key: record.uuid,
                checked: value,
            })
        )
    }
    catch (e) {
        console.error('Failed selecting table in sidebar', e)
    }
}

const updateIds = ({ storeCols, fetchedCols }) => {
    let storeKeys = Object.keys(storeCols)
    let fetchedKeys = Object.keys(fetchedCols)
    storeKeys.map(key => {
        if (storeCols[key] && fetchedCols[key]) {
            storeCols[key].id = fetchedCols[key].id
            storeCols[key].originalId = fetchedCols[key].columnId
            storeCols[key]._columnId = fetchedCols[key].columnId
            delete storeCols[key].columnId
        }
    })
    let newColKeys = fetchedKeys.filter(eachKey => !storeKeys.includes(eachKey))
    if (newColKeys) { // checking if the key is changed as alias is changed
        let storeAliases = Object.values(storeCols).map(col => col.alias)
        let storeKeys = Object.keys(storeCols)
        let requiredCol = {} // {fethedKey : storeKey}
        newColKeys.map(newKey => {
            storeAliases.map((alias, index) => {
                if (alias === fetchedCols[newKey].alias) {
                    requiredCol[newKey] = storeKeys[index]
                }
            })
        })
        Object.keys(requiredCol).forEach(fetchedKey => {
            // storeCols[requiredCol[fetchedKey]] = fetchedCols[fetchedKey].columnId
            storeCols[requiredCol[fetchedKey]].id = fetchedCols[fetchedKey].id
            storeCols[requiredCol[fetchedKey]].originalId = fetchedCols[fetchedKey].columnId
            storeCols[requiredCol[fetchedKey]]._columnId = fetchedCols[fetchedKey].columnId
            delete fetchedCols[fetchedKey]
        })
    }

    return { storeCols, fetchedCols }
}