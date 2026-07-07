import { cloneDeep } from 'lodash-es'
import { metadataActions, updateViewAlias } from '../../../redux/actions'
import { getColumnUniqueKey } from '../../../utils/reportQuery/utils/handleGetUniqueKey'
import { uuid } from '../../../utils/uuid'
import notify from '../../hi-notifications/notify'
import { handleDSTableCheck, handleTableExpand, updateTablesWithColumns } from './index'
import { genrateUniqueName } from './utils'

export const handleColumnsAliasChange = ({tables, tableKey, changedColumns, record, updatedAlias}) => {
    console.log({tables, tableKey, changedColumns: cloneDeep(changedColumns), record, updatedAlias})
    if(!(!tables[tableKey]?.isSaved && tables[tableKey]?.duplicate)) {
        let isColumnExist = false;
        changedColumns = changedColumns.map(ele => {
            if(((ele.duplicate ? ele.id : ele.columnId) === record.id) && ele.connId === record.connId && ele.tableId === record.tableId && ele.alias === record.alias) {
                isColumnExist = true;
                ele.alias = updatedAlias;
            } 
            return ele;
        })
        if(!isColumnExist) {
            changedColumns.push({
                alias: updatedAlias,
                columnName: record.alias,
                //record.orginalId for duplicates
                columnId: record.id,
                connId: record.connId,
                tableId: tables[tableKey].id,
                aliasChanged: true,
                isView: record.parentCategory === 'view',
                viewName : record.tableKey
            })
        }
    }
    console.log(changedColumns)
    return changedColumns;
}

export const handleTableAction = ({ action, record, dataSource, value, tables, dispatch, store,
    returnResultForJest = false, getApi = () =>{}
}) => {
    tables = cloneDeep(store.getState().metadata.present.tables)
    if (!record) { // validating record
        return false
    }
    let { category } = record
    let changedTables = cloneDeep(store.getState().metadata.present.changedTables)
    let changedColumns = cloneDeep(store.getState().metadata.present.changedColumns)
    let mode = cloneDeep(store.getState().metadata.present.mode)
    if (category === 'table' || category === 'view') {
        let keyName = `${record.keyName}`;
        if (action === 'updateAlias') { // alias for table
            if (!value || value.length < 3) {
                dispatch && notify(dispatch).error({
                    message: 'invalid alias name',
                    type: 'validation'
                })
                return false
            }
            let data = tables
            let currentTable = data[keyName]
            let isAliasTaken = (Object.values(tables)).some(table => table.alias === value);
            let updatedAlias = value;
            if(isAliasTaken && currentTable.alias !== value)
                updatedAlias = genrateUniqueName({ allItems: Object.values(tables).map(table => table.alias), item: value, currentAlias: currentTable.alias })
            
            // TODO: REMOVE COMMENTED CODE BELOW

            // if (isAliasTaken) {
            //     let indexs = [], reqIndex;
            //     let splitedArr = value.split("_");
            //     Object.values(tables).forEach(table => {
            //         const curSplitArr = table.alias.split("_");
            //         const curLstWrd = curSplitArr.pop();
            //         if ((table.alias.match(value) && splitedArr[0] === curSplitArr[0]) && (splitedArr.length === curSplitArr.length) && !isNaN(Number(curLstWrd)) && (record.name !== table.name)) {
            //             indexs.push(curLstWrd);
            //         }
            //     })
            //     if (indexs.length) {
            //         for (let i = 1; i <= indexs.length; i++) {
            //             if (!indexs.includes(i + '')) {
            //                 reqIndex = i;
            //                 i = indexs.length + 1;
            //             }
            //         }
            //         !reqIndex && (reqIndex = indexs.length + 1);
            //     } else {
            //         reqIndex = 1;
            //     }
            //     value += `_${reqIndex}`;
            // }
            // if (!value.length || (value.length && value.length < 3)) {
            //     dispatch && notify(dispatch).error({
            //         message: 'invalid alias name',
            //         type: 'validation'
            //     })
            //     return false
            // }
            // currentTable.alias = value;
            
            currentTable.alias = updatedAlias;
            if(currentTable.id) {
                let isItemAlreadyPresent = false;
                changedTables = changedTables.map(ele => {
                    if(ele.id === currentTable.id && ele.connId === currentTable.connId) {
                        ele.alias = updatedAlias;
                        isItemAlreadyPresent = true;
                    }
                    return ele;
                })
                if(!isItemAlreadyPresent) {
                    changedTables.push({
                        id: currentTable.id,
                        alias: updatedAlias,
                        connId: currentTable.connId
                    });
                }
            }

            //if table is alias update updated data in views also
            if(currentTable.category === "view") {
                dispatch(updateViewAlias(currentTable));
            }
            
            if (returnResultForJest) {
                return changedTables
            }
            dispatch && dispatch(metadataActions.updateMetadataTables({ data, override: true, changedTables  }))
        }
        else if (action === 'remove') { // remove table
            let removedTables = cloneDeep(store.getState().metadata.present.removedTables)
            let duplicateTableList = cloneDeep(store.getState().metadata.present.duplicateTableList)
            let changedColumns = cloneDeep(store.getState().metadata.present.changedColumns)
            let data = tables
            let duplicateTableIndex = duplicateTableList.findIndex(id => id === data[keyName].id);
            if(category !== 'view') {
                if(duplicateTableIndex >= 0) {
                    changedColumns = changedColumns.filter(ele => {
                        return ele.tableId !== duplicateTableList[duplicateTableIndex];
                    })
                    duplicateTableList.splice(duplicateTableIndex, 1);
                } else {
                    changedColumns = changedColumns.filter(ele => {
                        return ele.tableId !== data[keyName].id;
                    })
                    removedTables.push({ id: data[keyName].id, connId: data[keyName].connId });
                }
            }
            dispatch && dispatch(metadataActions.updateMetadataState({ mode: 'selectedTables', checked: false, key: data[keyName].uniqueKey }))
            let removedTable = {...data[keyName]};
            delete data[keyName]
            let dataSource = cloneDeep(store.getState().metadata.present.dataSource)
            let connIds = Object.values(data).map(table => table?.dataSource?.connId)
            let idType = Object.values(data).map(table => ({ id: table?.dataSource?.id, type: table?.dataSource?.type }))
            if (!dataSource.connId) {
                Object.values(data).forEach(table => {
                    if (table?.data?.id === dataSource.id && table?.data?.type === dataSource.type) dataSource.connId = table.connId
                })
            }
            let removedDS = []
            dataSource = dataSource.filter(ds => {
                if (connIds.indexOf(ds.connId) === -1) {
                    removedDS.push(ds)
                }
                if (idType.some(idType => idType.id === ds.id && idType.type === ds.type)) {
                    return true
                }
                return connIds.indexOf(ds.connId) !== -1
            })
            dispatch && dispatch(metadataActions.updateMetadataTables({ data, override: true, removedTables, updateDS: false, updateDSAddedToMetada: true, dataSource, removedDS, duplicateTableList, changedColumns }))
            let tableCheckResult = !removedTable.duplicate && handleDSTableCheck({
                checked: false,
                record: removedTable,
                dispatch,
                dsListToRender: cloneDeep(store.getState().metadata.present.datasourceListToRender),
                returnData: returnResultForJest,
                task: 'remove'
            })
            
            if (returnResultForJest) {
                return {
                    tableCheckResult,
                    removedTables
                }
            }
        }
        else if (action === 'duplicate') { // duplicate table
            /**
             * fetch columns if not fetched
             * then duplicate table
             */
            let data = tables
            let currentTable = data[keyName]
            if ((!currentTable.columns || !Object.keys(currentTable.columns || {}).length) && !currentTable.columnsFetched ) { //fetching columns
                handleTableExpand({
                    currentTable: record,
                    dataSource,
                    dispatch,
                    tables,
                    duplicateTable: true,
                    mode,
                    getApi
                })
            }
            else {
                let returnedTables = updateTablesWithColumns({
                    duplicateTable: true,
                    result: {
                        metadata: {
                            table: {
                                // [currentTable.name]: { // earlier it used to be .name but changed to .alias as it is causing issue when duplicating already duplicated table
                                [currentTable.alias]: {
                                    columns: currentTable.columns
                                }
                            }
                        }
                    },
                    tables,
                    dispatch,
                    currentTable,
                    returnTables: returnResultForJest,
                    mode
                })
                if (returnResultForJest) {
                    return returnedTables
                }
            }
        }
    }
    else if (category === 'column') {
        if (action === 'duplicate') {
            let { tableKey } = record;
            //TODO: pass col.name instead of col.orginalName to below function
            const currentTableColumns = Object.values(tables[tableKey]?.columns);
            let newColKey = genrateUniqueName({ allItems: currentTableColumns.map(col => col.name ?? col.originalName), item: record.name })
            let newUUID = uuid();
            let duplicateColumn = { ...record };
            delete duplicateColumn.uuid;// 6274
            delete duplicateColumn.key; // 6274
            // TODO: replace columnId as id for columns every where
            // TODO: remove `_columnId` from columns
            duplicateColumn.id = newUUID;
            duplicateColumn.name = newColKey;
            duplicateColumn.alias = genrateUniqueName({ allItems: currentTableColumns.map(col => col.alias), item: record.alias });
            // duplicateColumn.tableName = tables[tableKey].name;
            duplicateColumn.originalId = record.originalId ?? record.id;
            duplicateColumn.duplicate = true;
            duplicateColumn.columnKey = newColKey;
            duplicateColumn.uniqueKey = !["test"].includes(process.env.NODE_ENV) && getColumnUniqueKey({mode: 'create', id: newUUID, dbId: tables[tableKey].connId})
            //Todo: keep it only record.name
            duplicateColumn.originalName = record.name ?? record.originalName;
            duplicateColumn.tableId= tables[tableKey].id || tables[tableKey].originalId
            duplicateColumn.connId= tables[tableKey].connId;
            tables[tableKey].columns[newColKey] = duplicateColumn;
            if(!(!tables[tableKey]?.isSaved && tables[tableKey]?.duplicate)) {
                changedColumns.push({
                    alias: duplicateColumn.alias,
                    name: duplicateColumn.name,
                    id: duplicateColumn.id,
                    originalId: duplicateColumn.originalId,
                    connId: duplicateColumn.connId,
                    tableId: tables[tableKey].id,
                    duplicate: true
                })
            }
            if (returnResultForJest) {
                return { changedColumns, tables }
            }
            dispatch(metadataActions.updateMetadataTables({ data: tables, override: true, changedColumns }))
        }
        else if (action === 'remove') {
            let removedColumns = cloneDeep(store.getState().metadata.present.removedColumns)
            let changedColumns = cloneDeep(store.getState().metadata.present.changedColumns)
            if (!record) {
                return false
            }
            const duplicatecolIndex = changedColumns.findIndex(ele => record.id === ele.id && ele.duplicate);
            if (tables[record.tableKey]?.id) {
                if(duplicatecolIndex >= 0) {
                    changedColumns.splice(duplicatecolIndex, 1);
                }  else {
                    let removedData = {
                        id: record.id || record.originalId,
                        connId: record.connId,
                        tableId: record.tableId,
                        columnName: record.alias
                    }
                    if (record.parentCategory === 'view') {
                        removedData.tableKey = record.tableKey
                        removedData.viewName = record.tableKey
                        removedData.parentCategory = 'view'
                        removedData.isView = true
                    }
                    removedColumns.push(removedData)
                }
            }
            Object.keys(tables).map(key => {
                if (key === record.tableKey) {
                    delete tables[key].columns[record.columnKey]
                }
            })
            if (returnResultForJest) {
                return removedColumns
            }
            dispatch && dispatch(metadataActions.updateMetadataTables({ data: tables, override: true, removedColumns, changedColumns }))
        }
        else if (action === 'updateAlias') {
            if (!value || value.length < 3) {
                dispatch && notify(dispatch).error({
                    message: 'invalid alias name',
                    type: 'validation'
                })
                return false
            }
            let { tableKey } = record;
            let isAliasTaken = (Object.values(tables[tableKey].columns)).some(col => col.alias === value);
            let updatedAlias = value;
            if(isAliasTaken && record.alias !== value)
                updatedAlias = genrateUniqueName({ allItems: Object.values(tables[tableKey].columns).map(col => col.alias), item: value, currentAlias: record.alias })
            
            // TODO: REMOVE COMMENTED CODE BELOW

            // if (isAliasTaken) {
            //     let indexs = [], reqIndex;
            //     let splitedArr = value.split("_");
            //     Object.values(tables[tableKey].columns).forEach(col => {
            //         const curSplitArr = col.alias.split("_");
            //         const curLstWrd = curSplitArr.pop();
            //         if ((col.alias.match(value) && splitedArr[0] === curSplitArr[0]) && (splitedArr.length === curSplitArr.length) && !isNaN(Number(curLstWrd)) && (record.originalName !== col.originalName)) {
            //             indexs.push(curLstWrd);
            //         }
            //     })
            //     if (indexs.length) {
            //         for (let i = 1; i <= indexs.length; i++) {
            //             if (!indexs.includes(i + '')) {
            //                 reqIndex = i;
            //                 i = indexs.length + 1;
            //             }
            //         }
            //         !reqIndex && (reqIndex = indexs.length + 1);
            //     } else {
            //         reqIndex = 1;
            //     }
            //     value += `_${reqIndex}`;
            // }
            // let flag = true;
            // changedColumns = changedColumns.map(item => {
            //     if((item.columnId === record.id || item.columnId === record.originalId) && item.duplicate === record.duplicate && item.alias === record.alias && item.connId === record.connId && item.tableId === tables[tableKey].id){
            //         flag = false;
            //         return {...item, alias: updatedAlias };
            //     }
            //     return item;
            // })
            //|| ele.columnId === record.originalId
            changedColumns = handleColumnsAliasChange({tables, tableKey, changedColumns, record, updatedAlias})

            tables[tableKey].columns[record.columnKey].alias = updatedAlias;
            
            if (returnResultForJest) {
                return changedColumns
            }
            dispatch(metadataActions.updateMetadataTables({ data: tables, override: true, changedColumns }))
        }

    }
    else {
        console.log('in else -- table action', { action, record })
        return false
    }


}
