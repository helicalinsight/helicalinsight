
import { cloneDeep } from 'lodash-es'
import { getMetadata, updateStoreWithFetchedMetadata, validateMetadataName } from '.'
import requests from '../../../base/requests'
import { metadataActions, fileBrowserActions, afterMetadataSave, saveExpressionData } from '../../../redux/actions'
import notify from '../../hi-notifications/notify'
import { mapDsWithConnDbId } from './mapDsWithConnDbId'

const getAllSavedIds = (initialEditResponse) => {
    let tableIds = [], columnIds = [];
    [initialEditResponse, ...(initialEditResponse?.connections || [])].map(e => {
        Object.values((e?.tables || {})).forEach(table => {
            tableIds.push({id: table.id, dbId: e.dataSource.dbId})
            Object.values((table?.columns || [])).map(col => {
                columnIds.push(col.id)
            })
        })
    })
    return { tableIds, columnIds }
}
export const handleSave = ({ store, dispatch, type = 'save', location = false, fileName = false,
    returnDataForJest = false }) => {
    if (!location && !fileName) { // if no location or filename then return
        return
    }

    const fileNameValidation = validateMetadataName(fileName)
    if (fileName && fileNameValidation) {
        notify(dispatch).warning({
            type: "Validation",
            message: fileNameValidation,
        })
        return
    }

    let metadata = store.getState().metadata.present;
    if (!(metadata?.dataSource?.length) || !metadata.tables) {
        return false
    }

    let { expressionObj = [], dataSource, tables, changedTables, removedTables, saveDetails, changedColumns, removedColumns, joins, initialEditResponse, views, dataSourcesAddedToMetadata, duplicateTableList, mode } = cloneDeep(metadata)


    let { dsWithConnDbId } = mapDsWithConnDbId({ initialEditResponse, dataSource }) // datasource object is also updated here
    let connIdsAddedToMeta = dataSourcesAddedToMetadata.map(ds => `${ds.id}_${ds.type}`)
    dataSource = dataSource.filter(ds => connIdsAddedToMeta.includes(`${ds.id}_${ds.type}`))
    let savedIds = getAllSavedIds(initialEditResponse)
    let savedTableIds = savedIds.tableIds
    let savedColumnIds = savedIds.columnIds
    let multiConnection = connIdsAddedToMeta.length > 1
    let formData = getConnFormData({
        dispatch,
        connection: dataSource[0],
        type,
        tables,
        expressionObj,
        changedTables,
        removedTables,
        isAlreadySaved: !!saveDetails,
        changedColumns,
        removedColumns,
        savedTableIds,
        savedColumnIds,
        joins,
        initialEditResponse, // this is the response of get metadata when edited/saved
        views,
        multiConnection,
        connectionIndex: 0,
        dsWithConnDbId,
        duplicateTableList,
        mode
    })
    let connections = []
    if (dataSource.length > 1) {
        dataSource.shift()
        connections = dataSource.map((conn, index) => {
            return getConnFormData({
                expressionObj,
                connection: conn,
                type,
                tables,
                changedTables,
                removedTables,
                isAlreadySaved: !!saveDetails,
                changedColumns,
                removedColumns,
                savedTableIds,
                savedColumnIds,
                joins,
                initialEditResponse,
                views,
                multiConnection,
                connectionIndex: index + 1,
                dsWithConnDbId,
                duplicateTableList,
                mode
            })
        })
        connections = connections.map(ele => {
            delete ele.joins
            return ele
        })
        // delete connections[0].joins // doubt
        formData.connections = connections
    }

    formData.addItem = {...formData.addItem, connections: []}
    formData.removeItem = {...formData.removeItem, connections: []}
    formData.changeItem = {...formData.changeItem, connections: []}
    formData.access = { expression: [...expressionObj] }

    if(formData.connections) {
        let dbIds = formData.connections.map(ele => {
            if(ele.dataSource.isExistedDS) {
                delete ele.dataSource.isExistedDS;
                if(ele.dataSource.changed) {
                    formData.changeItem.connections.push(ele.dataSource.dbId)
                }
            } else {
                formData.addItem.connections.push(ele.dataSource.dbId)
            }
            return ele.dataSource.dbId
        });

        initialEditResponse?.connections?.forEach(connection => {
            if(!dbIds.includes(connection.dataSource.dbId)) {
                formData.removeItem.connections.push(connection.dataSource.dbId)
            }
        })
    }

    formData.fileName = fileName
    formData.location = location // DEV
    formData["metadataReload"] = true
    // formData.location = "1643108936943" //local
    // 1643108936943
    if (saveDetails?.location && saveDetails.uuid) {
        formData.uuid = saveDetails.uuid
        formData.location = saveDetails.location
        if (type === 'save') {
            formData["metadataReload"] = false
            formData.uniqueId = true
        }
        else {
            formData.newLocation = location
            formData["metadataReload"] = false
            // delete formData.uuid
            delete formData.uniqueId
        }
    }
    if (type !== 'save') {
        delete formData.uniqueId
    } else {
        // formData.uniqueId = true
        // formData.changed = false
        // formData.databaseType = "Derby"
        // formData.datasourceName = "SampleTravelDataDerby"
    }
    // delete formData.dataSource.databaseType
    // delete formData.dataSource.originalSchema
    // delete formData.dataSource.originalCatalog
    // delete formData.dataSource.catSchemaPredicted
    // delete formData.dataSource.sync
    // delete formData.dataSource.classifier
    // delete formData.dataSource.connectionDatabaseId
    // delete formData.dataSource.driver
    // delete formData.dataSource.name
    // delete formData.dataSource.datasourceName
    // formData.dataSource.database = "HIUSER"
    // formData.dataSource.classifier = "db.generic"
    if (initialEditResponse?.classifier) {
        formData.classifier = initialEditResponse.classifier
    }
    // formData.database = 'HIUSER'

    if (returnDataForJest) {
        return formData
    }
    dispatch(metadataActions.handleMetadataSavingProgress(true));
    requests.metadata(dispatch).saveMetadata(formData, res => {
        dispatch(metadataActions.handleMetadataSavingProgress(false));
        if (res?.data) {
            dispatch(fileBrowserActions.saveFileinFb(res.data))
        }
        // if (res.metadata) {
        // }
        if (res?.uuid && res?.location) {
            dispatch(metadataActions.updateMetadataState({
                key: 'saveDetails', value: {
                    location: res.location,
                    uuid: res.uuid,
                    metadataName: res.metadata?.metadataName || ''
                }
            }))
            dispatch(metadataActions.resetDuplicateData({
                resetChanged: false,
                dataFetchedForJoins: true
            }))
            if (res.metadata) {
                dispatch(metadataActions.setIntialEditResponse({ ...cloneDeep(res.metadata) }))
                dispatch(metadataActions.setShowJoinModal(false))
                updateStoreWithFetchedMetadata({ fetchedMetadata: res.metadata, dispatch, store, failedFetching: true })
                dispatch(metadataActions.updateActiveEditorTab('saveActions'))
            }
            else {
                //get metadata after saved // this is not in use for this application - this was used in old application
                getMetadata({ location: res.location, uuid: res.uuid, store, dispatch })
            }
            dispatch(saveExpressionData([])); // 6272
            dispatch(afterMetadataSave());
            // notify(dispatch).success({
            //     type: "Network Call",
            //     message: res.message,
            // })
            dispatch(metadataActions.clearUndoRedoHistory());
        }
    }, err => {
        dispatch(metadataActions.handleMetadataSavingProgress(false));
        console.log('in handle save ERROR', err)
        // notify(dispatch).error({
        //     type: "Network Call",
        //     message: err?.message || '',
        // })
    })
}

export const removeKeysFromDataSource = (ds) => {
    let {driverType, dsKeyPath, originalSchema, originalCatalog, catSchemaPredicted, sync, classifier, connectionDatabaseId, driver, name, dataSource, joinsFetched, condition, userName, password, driverName, jdbcUrl, ...rest} = ds;
    return rest;
}

export function getDuplicateTableFormdata({tables, connection, duplicateTableList, savedTableIds}) {
    let output = Object.values(tables).map(table => {
        if ((table.connId === connection.connId) && table.duplicate && duplicateTableList.includes(table.id)) {
            let result = {
                alias: table.alias,
                id: table.id,
                columns: Object.values(table.columns)?.map(col => {
                    let colResult = {
                        alias: col.alias,
                        connId: col.connId,
                        originalId: col.originalId, // removed as this is not required
                        // originalName: col.originalName, // removed as these are unwanted
                        id: col.id,
                        name: col.name
                    }
                    if (col.duplicate && col.name) {
                        colResult.name = col.name
                    }
                    return colResult
                }),
                connId: table.connId,
                originalName: table.originalName,
                originalId: table.originalId,
                name: table.name
                // root: table.root
            }
            return savedTableIds.find(ele => (ele.id === table.id) && (ele.dbId === connection.dbId)) ? false : result;
            // return savedTableIds.indexOf(table.id) === -1 ? result : false
            // return result
        }
        return false
    }).filter(Boolean)
    return output
}

const getConnFormData = ({
    connection,
    type = 'save',
    tables,
    expressionObj = [],
    changedTables,
    removedTables,
    isAlreadySaved,
    changedColumns,
    removedColumns,
    savedTableIds,
    savedColumnIds,
    joins,
    initialEditResponse = {},
    views,
    multiConnection,
    connectionIndex = -1,
    dsWithConnDbId = {},
    duplicateTableList,
    mode
}) => {
    let { database, classifier } = connection;
    //formdata should not contain oldDbid
    delete connection.oldDbId;

    if (type === 'saveAs') {
        savedTableIds = []
        savedColumnIds = []
    }
    let result = {
        database,
        classifier,
        duplicate: {
            table: [],
            column: []
        },
        joins: [],
        dataSource: connection,
        removeItem: {
            tables: [],
            columns: [],
            views: []
        },
        addItem: {
            tables: [],
            columns: [],
            views: []
        },
        changeItem: {
            tables: [],
            columns: []
        }
    };
    // code for adding 'connectionDatabaseId' to formdata incase of multi connections
    let connData = initialEditResponse?.connections?.filter(conn => conn.dataSource.id === connection.id && conn.dataSource.type === connection.type) || []
    if (connData.length === 1 && connData[0]?.connectionDatabaseId) {
        connection.connectionDatabaseId = connData[0].connectionDatabaseId
        // result.connectionDatabaseId = connection.connectionDatabaseId
    }
    else if (initialEditResponse?.dataSource?.id === connection.id && initialEditResponse?.dataSource?.type === connection?.type && initialEditResponse?.dataSource?.connectionDatabaseId) {
        connection.connectionDatabaseId = initialEditResponse.dataSource.connectionDatabaseId
    }
    expressionObj.length && (result.uniqueId = true)
    // dispatch && dispatch(saveExpressionData({}));
    result.dataSource.databaseType = result.dataSource.driverType
    result.dataSource = removeKeysFromDataSource(result.dataSource);
    // delete result.dataSource.driverType
    // delete result.dataSource.dsKeyPath
    // delete result.dataSource.originalSchema
    // delete result.dataSource.originalCatalog
    // delete result.dataSource.catSchemaPredicted
    // delete result.dataSource.sync
    // delete result.dataSource.classifier
    // delete result.dataSource.connectionDatabaseId
    // delete result.dataSource.driver
    // delete result.dataSource.name
    // delete result.data.dataSource
    if (type === 'save' || true) {

        result.addItem.tables = Object.values(tables).map(table => {
            if(!table.isSaved && table.category !== "view" && !table.duplicate && table.connId === connection.connId){
                return table.id;
            }
            return false;
        }).filter(Boolean)

        //for added tables
        // result.addItem.tables = Object.values(tables).map(table => {
        //     /**
        //      * table should be from same connection
        //      * table should not be duplicate
        //      * if it is duplicated then it should already be saved at backend
        //      */
        //     if (table.category !== 'view' && table.connId === connection.connId && (!table.duplicate ||
        //          savedTableIds.find(ele => (ele.id === table.id) && (ele.dbId === connection.dbId)))) {//indexOf(table.id) !== -1)) {
        //         //for duplicate columns
        //         // result.duplicate.column = [
        //         //     ...result.duplicate.column, ...(Object.values(table.columns || {}).map(col => {
        //         //         if (col.duplicate && (savedColumnIds.map(col => col.columnId)).indexOf(col.columnId) === -1 && !col._columnId && col.originalId) {
        //         //             let { originalId, originalName, connId, alias, name } = col
        //         //             let result = { originalId, originalName, connId, alias, name }
        //         //             result.tableId = table.id
        //         //             return result
        //         //         }
        //         //         return false
        //         //     }).filter(Boolean))
        //         // ]
        //         // if (type === 'saveAs' && table.id) {
        //         //     return false
        //         // } 5983
        //         //return savedTableIds.indexOf(table.id) === -1 ? table.id : false
        //         return savedTableIds.find(ele => (ele.id === table.id) && (ele.dbId === connection.dbId)) ? false : table.id;
        //         // return table.ids
        //     }
        //     else if (table.duplicate && (table.connId === connection.connId)) {
        //         // return null
        //         return false
        //     }
        //     else {
        //         return false
        //     }
        // }).filter(table => table !== false)

        if (result?.addItem?.tables?.filter(Boolean)?.length === 0) { //4425 fix
            result.addItem.tables = []
        }

        // for changed tables
        result.changeItem.tables = changedTables.map(table => {
            const isViewModified = Object.values(tables).some(item => item.id === table.id && item.type === 'view');
            if (table.connId === connection.connId || isViewModified) {
                if (table.alreadySaved && type === 'save') {
                    return false
                }
                return table
            }
            return false
        }).filter(Boolean)

        // for changed columns
        result.changeItem.columns = changedColumns.map(col => {
            // if ((table.connId === connection.connId) && table.tableId) {
            if (col.tableId && col.connId === connection.connId) {
                if (col.alreadySaved && type === 'save') {
                    return false
                }
                if ((col.duplicate || !col.aliasChanged)) {
                    return false
                }
                return col
            }
            return false
        }).filter(Boolean)


        //for removed tables
        if (isAlreadySaved) {
            result.removeItem.tables = removedTables.map(table => {
                if (table.connId === connection.connId) {
                    if (table.alreadySaved && type === 'save') {
                        return false
                    }
                    return table.id
                }
                return false
            }).filter(Boolean)
        }

        //for duplicate column
        //TODO: generate result.duplicate.column and result.changeItem.column from single iteration of store.changedColumns
        result.duplicate.column = changedColumns.map(column => {
            if(!column.alreadySaved && column.tableId && column.duplicate && column.connId === connection.connId){
                // const formDataColumn =  {
                //     ...column,
                //     originalId: column.columnId,
                // };
                // delete formDataColumn.columnId;
                return column;
            }
            return false;
        }).filter(Boolean);


        //for duplicate tables
        result.duplicate.table = getDuplicateTableFormdata({tables, connection, duplicateTableList, savedTableIds})


        // for removed columns
        result.removeItem.columns = removedColumns.map(col => {
            if(!col.parentCategory) return col.id;
            // if(col.parentCategory === 'view') {
            //     if(tables[col.tableKey] && tables[col.tableKey].id){
            //         if (result.removeItem.views?.filter(data => data.id === col.tableId).length) {
            //             result.removeItem.views = result.removeItem.views.map(item => {
            //                 if (item?.id === tables[col.tableKey].id) {
            //                     item.columns.push(col.id)
            //                 }
            //                 return item
            //             })
            //         }
            //         else {
            //             result.removeItem.views.push({
            //                 id: tables[col.tableKey].id,
            //                 columns: [col.id]
            //             })
            //         }
            //     }
            //     else {
            //         result.removeItem.views.push({
            //             id: col.tableId,
            //             columns: []
            //         })
            //     }
            // }

            if (col.parentCategory === 'view' && tables[col.tableKey] && tables[col.tableKey].id) { // 6330
                if (result.removeItem.views?.filter(data => data.id === tables[col.tableKey].id).length) {
                    result.removeItem.views = result.removeItem.views.map(item => {
                        if (item?.id === tables[col.tableKey].id) {
                            item.columns.push(col.id);
                        }
                        return item;
                    });
                }
                else {
                    result.removeItem.views.push({
                        id: tables[col.tableKey].id,
                        columns: [col.id]
                    })
                }
                return false
            }
            return false;
        }).filter(Boolean)

        //for joins 
        joins = joins.sort((a, b) => a.index - b.index)
        result.joins = joins
        .filter(eachJoin => {
            return eachJoin.left.table && eachJoin.right.table;
        })
        .map(eachJoin => {
            // let { leftConnDBId, rightConnDBId } = checkIfCrossJoin({ join: eachJoin, tables, initialEditResponse, dsWithConnDbId }) || {}
            // if ((eachJoin?.left?.dataSource?.id !== connection?.id || eachJoin?.left?.dataSource?.type !== connection?.type) && (
            //     !(leftConnDBId && rightConnDBId && connectionIndex === 0 && leftConnDBId !== rightConnDBId) // checking if it is a crossjoin
            // )) return false; // joins formdata change 5957
            if(eachJoin.action === 'delete' || eachJoin.action === 'noChange'){
                if(!eachJoin.id) return false;
                return {
                    id: eachJoin.id,
                    action: eachJoin.action
                } 
            } else {
                eachJoin = { // 5957
                    action: eachJoin.action,      
                    type: eachJoin.type,
                    operator: eachJoin.operator,
                    id: eachJoin.id || eachJoin.key,
                    left: {
                        column: eachJoin.left.column,
                        tableId: eachJoin.left.tableId,
                        dbId: eachJoin.left?.dbId,
                        table: eachJoin.left?.table,
                        // alias: `${eachJoin.left?.tableAlias}.${eachJoin.left?.columnAlias}`
                    },
                    right: {
                        column: eachJoin.right.column,
                        tableId: eachJoin.right.tableId,
                        dbId: eachJoin.right?.dbId,
                        table: eachJoin.right?.table,
                        // alias: `${eachJoin.right?.tableAlias}.${eachJoin.right?.columnAlias}`
                    }
                }
                return eachJoin;
            }

            // if (eachJoin.action === 'noChange' || eachJoin.action === 'delete') {
            //     if (connectionIndex === 0) {
            //         // console.log('in each joins', leftConnDBId, rightConnDBId)
            //         // if ((leftConnDBId || rightConnDBId) && (leftConnDBId !== rightConnDBId)) {
            //         //     if (!('crossJoins' in result)) result.crossJoins = []
            //         //     if (leftConnDBId) {
            //         //         eachJoin.databaseId = leftConnDBId
            //         //     }
            //         //     if (rightConnDBId) {
            //         //         eachJoin.referenceId = rightConnDBId
            //         //     }
            //         //     // result.crossJoins.push(eachJoin)
            //         //     result.crossJoins.push({
            //         //         action: eachJoin.action,
            //         //         id: eachJoin.id
            //         //     })
            //         //     return false
            //         // }
            //     }
            //     if (eachJoin.action === 'delete' && !eachJoin.id) {
            //         return false
            //     }
            //     return {
            //         action: eachJoin.action,
            //         id: eachJoin.id
            //     }
            // }
            // else if (eachJoin.action === 'add' || eachJoin.action === 'update') {
            //     eachJoin.left.alias = eachJoin.leftColumn
            //     eachJoin.right.alias = eachJoin.rightColumn
            //     delete eachJoin.rightColumn
            //     delete eachJoin.leftColumn
            //     // let { leftConnDBId, rightConnDBId } = checkIfCrossJoin({ join: eachJoin, tables, initialEditResponse, dsWithConnDbId }) || {}
            //     if (connectionIndex === 0) {
            //         // console.log('in each joins', leftConnDBId, rightConnDBId)
            //         // if ((leftConnDBId || rightConnDBId) && (leftConnDBId !== rightConnDBId)) {
            //         //     if (!('crossJoins' in result)) result.crossJoins = []
            //         //     if (leftConnDBId) {
            //         //         eachJoin.databaseId = leftConnDBId
            //         //     }
            //         //     if (rightConnDBId) {
            //         //         eachJoin.referenceId = rightConnDBId
            //         //     }
            //         //     result.crossJoins.push(eachJoin)
            //         //     return false
            //         // }
            //     }
            //     // else {
            //     if (leftConnDBId === connection.connectionDatabaseId && rightConnDBId === connection.connectionDatabaseId) {
            //         return eachJoin
            //     }
            //     // return false
            // }
            
        }).filter(Boolean)

        //check fi joins available in initialEditResponse
        //4812 - start
        if (initialEditResponse) {
            let { joins } = initialEditResponse
            if (Array.isArray(joins)) {
                let idsAdded = result.joins.map(join => join.id).filter(Boolean)
                // let idsNotAddedWhichAreInInitialEdit = joins.filter(join => idsAdded.indexOf(join.id) !== -1)
                joins.map(eachJoin => {
                    if (eachJoin.id && idsAdded.indexOf(eachJoin.id) === -1) {
                        result.joins.push({ id: eachJoin.id, action: 'delete' })
                    }
                    return null
                })
            }
            //4811 - start
            let initialTables = [];
            if (initialEditResponse.dataSource?.dbId === connection.dbId) {
                initialTables = Object.values(initialEditResponse?.tables || []).map(table => table.id);
            } else if(initialEditResponse.connections) {
                const currentConnection = initialEditResponse.connections?.filter(conn => conn.dataSource?.dbId === connection.dbId);
                initialTables = Object.values(currentConnection?.tables || []).map(table => table.id);
            } else {
                initialTables = Object.values(initialEditResponse?.tables || []).map(table => table.id)
            }

            // initialTables = Object.values(initialEditResponse?.tables || []).map(table => table.id)
            let currentAvailableTables = Object.values(tables || []).map(eachTable => eachTable.id).filter(Boolean)
            let tablesInRemove = result?.removeItem?.tables.filter(Boolean) || []
            initialTables.map(tableID => {
                if (currentAvailableTables.indexOf(tableID) === -1) { // if the table fetched is present in metadata-section
                    if (tablesInRemove.indexOf(tableID) === -1 && result.removeItem.tables.indexOf(tableID) === -1) { // if the removed table is present in result.removeItem.tables or not
                        result.removeItem.tables.push(tableID);
                    }
                }
            })
            //4811 - end
        }
        //4811 - end
        /**
         * checking if same table id is present in both add and remove tables - removing form removetables if present in addtables
         */
        result.addItem.tables.forEach(eachTable => {
            if (typeof eachTable === 'string') {
                if (result.removeItem.tables.includes(eachTable)) {
                    result.removeItem.tables.splice(result.removeItem.tables.indexOf(eachTable), 1)
                }
            }
        })
        /**
         * checking if any table present in removeItem.tables is present 
         */
        Object.values(tables).forEach(eachTable => {
            if (typeof eachTable?.id === 'string') {
                if (result.removeItem.tables.includes(eachTable.id)) {
                    result.removeItem.tables.splice(result.removeItem.tables.indexOf(eachTable.id), 1)
                }
            }
        })
        //4812 - end
        //4319 - start
        /**
         * add views to formdata - if an id is present in initial tables then that view is already present
         */
        if (Array.isArray(views)) { //  6330
            let viewsInIntialResponse = Object.values(initialEditResponse?.tables || {}).map(table => table.type === 'view' ? table.id : false).filter(Boolean);
            let currentViews = (views || []).filter(eachView => eachView)//views in current metadata-section
            let deletedViews = (views || []).filter(eachView => eachView.isDeleted)
            let deletedViewIds = deletedViews.map(view => view.id)
            currentViews.forEach(view => {
                if (!(viewsInIntialResponse || []).includes(view.id) || view.isModified || type === 'saveAs') { // newly added views
                    if (!view.isDeleted && view.id && !multiConnection) {
                        result.addItem.views.push(view.id)
                    }
                }
                else if (viewsInIntialResponse && viewsInIntialResponse.includes(view.id)) {
                    let initialColumnIds = Object.values(Object.values(initialEditResponse?.tables).filter(table => table.id === view.id)[0]?.columns).map(col => col.id) || false
                    //if viewColIds are not present in initialColumnIds then it is deleted
                    let viewColIds = Object.values(view.columns).map(col => col.id)
                    let deletedColumns = {}
                    let removedColumnsIds = removedColumns.map(col => col.id)
                    viewColIds.forEach(id => {
                        if (removedColumnsIds.includes(id)) {
                            if (deletedColumns[view.id]) {
                                deletedColumns[view.id] = [...deletedColumns[view.id], id]
                            }
                            else {
                                deletedColumns[view.id] = [id]
                            }
                        }
                    })
                    if (deletedViewIds.includes(view.id)) {
                        deletedColumns[view.id] = []
                        result.removeItem.tables = result.removeItem.tables.filter(id => id !== view.id);
                    }
                    Object.keys(deletedColumns).forEach(key => {
                        Array.isArray(deletedColumns[key]) && (deletedColumns[key].length === 0) &&  result.removeItem.views.push({ //6330
                            id: key,
                            columns: deletedColumns[key]
                        })
                        deletedColumns[key].map(colKey => {
                            result.removeItem.columns = result.removeItem.columns.filter(id => id !== colKey);
                        })
                    })
                }
            })
        }
        //4319 - end
    }

    !result.dataSource.changed && (delete result.dataSource.changed)
    return result

}