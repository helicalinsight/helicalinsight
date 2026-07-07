import { cloneDeep } from 'lodash-es';
import { v4 as uuidv4 } from 'uuid';
import { metadataActions, setSecurityTableData } from '../../../redux/actions';
import { getColumnUniqueKey, getTableUniqueKey, mapStoredTablesUniqueKeysWithFetchedTables, transformSecurityTable } from '../../../utils/reportQuery/utils/handleGetUniqueKey';
// import { syncFetchedTablesWithStoreTables } from './syncFetchedTablesWithStoreTables';

export function handleUpdatedStoreDs({updatedStoreDs, tempDataSource}) { // 6313
    return updatedStoreDs.map(ds => { 
        delete ds.changed;
        if(ds.id === tempDataSource.id && ds.type === tempDataSource.type) {
            return tempDataSource;
        }
        return ds;
    })
}

export const getMatchedDriverTypes = ({allDataSourceTypes, tempDataSource }) => {
    return allDataSourceTypes.filter(type => {
        if(type.sendVendorName) {
            if(!tempDataSource.driver?.vendorName)
            return false;
        }
        if(type.driver === tempDataSource.driver?.driver) {
            return true;
        }
    })[0]?.name;
}

export const updateStoreWithFetchedMetadata = ({ fetchedMetadata = {}, dispatch = () => { }, store, failedFetching = false, parentRecord = null, returnData = false
}) => {
    let storeTables = cloneDeep(store.getState().metadata.present.tables)
    let storeDs = cloneDeep(store.getState().metadata.present.dataSource)

    // let storeViews = cloneDeep(store.getState().metadata.views)
    let allDataSourceTypes = store.getState().metadata.present.allDataSourceTypes
    let securityTableData = cloneDeep(store.getState().metadata.present.securityTableData);
    const storeDataSourceList = store.getState().metadata.present.dataSourcesAddedToMetadata;
    let savedTableIds = []
    let savedColumnIds = []
    let { tables: __fetchedTables, joins: fetchedJoins, dataSource, classifier, connections = false } = fetchedMetadata
    let fetchedViews = [];
    let fetchedTables = {}
    // let connId = dataSource.dbId || uuidv4(); //  this will only work for single connection
    const storeDataSources = []
    let updatedStoreDs = JSON.parse(JSON.stringify(storeDs));
    
    function transformDataSources(dataSource, classifier) {
        let tempDataSource = {...dataSource};
        try {
            if (!tempDataSource.driver) {
                let dataSourceItem = store.getState().metadata.present.allDataSources.find(ds => {
                    return ds.data.id == tempDataSource?.id && ds.data.type === tempDataSource.type;
                })
                tempDataSource.driver = dataSourceItem;
                tempDataSource.datasourceName = dataSourceItem?.name
                tempDataSource.driverType = getMatchedDriverTypes({allDataSourceTypes, tempDataSource }) || allDataSourceTypes.filter(type => type.driver === 'dynamicSwitch')[0]?.name;
                // dsAddedToMeta[0].database = `${dsAddedToMeta[0].catalog}.${dsAddedToMeta[0].schema}`
            }
            if(tempDataSource.catalog && tempDataSource.schema) {
                tempDataSource.database = `${tempDataSource.catalog}.${tempDataSource.schema}`;
            } else if(tempDataSource.catalog) {
                tempDataSource.database = `${tempDataSource.catalog}`;
            } else if(tempDataSource.schema) {
                tempDataSource.database = `${tempDataSource.schema}`;
            } else {
                tempDataSource.database = '';
            }
            tempDataSource.connId = tempDataSource.dbId;
            tempDataSource.classifier = classifier;
            tempDataSource.joinsFetched = true;
            tempDataSource.oldDbId = getOldConnectionId(dataSource)
        }
        catch (err) {
            console.log('error in transformDataSources function in updateStoreWithFetchedMetadata', err)
        }
        //update store data source as some datasources are expanded but tables are not added to metadata
        // This is only for after save scenario
        if(storeDs.length) {
            updatedStoreDs = handleUpdatedStoreDs({updatedStoreDs, tempDataSource})
        }
        return tempDataSource;
    }

    const mainDataSource = transformDataSources(dataSource, classifier);
    storeDataSources.push(mainDataSource);

    function transformFetchedColumns({columns, tableKey = null, dbId, table}) {
        let fetchedColumns = columns;
        for(let colKey in fetchedColumns) {
            const [tableName, columnName] = columns[colKey].fullyQualifiedColumn.split('.');
            fetchedColumns[colKey] = {
                ...columns[colKey],
                category: 'column',
                columnKey: colKey,
                name: columnName,
                tableKey: tableKey ?? tableName,
                connId: dbId,
                tableId: table.id,
                parentCategory: table.category,
                uniqueKey: !["test"].includes(process.env.NODE_ENV) && getColumnUniqueKey({mode: 'edit', id: columns[colKey]?.id, dbId: dbId}),
                
                // originalId: columns[colKey].id,

                // uuid: uuidv4(),
                // duplicateIndex: 0,
            }
            if(fetchedColumns[colKey].duplicate === "true") {
                fetchedColumns[colKey].duplicate = true;
            }
        }
        return fetchedColumns;
    }

    function transformFetchedTable(table, key, dataSource, oldDbId) {
        let uuid = uuidv4()
        // updatedSecurityTableData = transformSecurityTable({table: result[0], securityTableData: updatedSecurityTableData, tableUniqueKey});
        let updatedTable = {
            ...table,
            keyName: key,
            dataSource,
            category: table?.type === 'view' ? 'view' : 'table',
            selected: true,
            columnsFetched: true,
            keyPath: uuid,
            uniqueKey: !["test"].includes(process.env.NODE_ENV) && getTableUniqueKey({mode: 'edit', id: table.id, dbId: dataSource.dbId}),
            connId: dataSource.dbId,
            isSaved: true,
            oldDbId
            // dbId: dataSource.dbId,

            //TODO: remove below properties from tables
            // data: {
            //     id: dataSource.id,
            //     type: dataSource.type,
            // },
            // key: uuid,
            // uuid: uuid,
        };
        if(updatedTable.duplicate === "true") {
            updatedTable.duplicate = true;
        }
        updatedTable.columns = transformFetchedColumns({columns: {...updatedTable.columns}, tableKey: key, dbId: dataSource.dbId, table: {...updatedTable}})
         if(updatedTable.duplicate) {  // 6271
            delete updatedTable.selected;
            delete updatedTable.cacheId;
         }
        return updatedTable;
    }

    function getOldConnectionId(newdataSource) {
        let oldDs = {...newdataSource};
        if(storeDataSourceList.length) {
            oldDs = storeDataSourceList.find(ds => ds.type === newdataSource.type && ds.id === newdataSource.id)
        }
        return oldDs.oldDbId ?? oldDs.dbId;
    }

    for (let [key, value] of Object.entries(__fetchedTables)) {
        // let result = Object.values(storeTables).filter(t => t.id === value.id)
        // let keyName = result[0]?.keyName || key
        fetchedTables[key] = JSON.parse(JSON.stringify(value));
        // const oldDbId = storeTables[key]?.connId ?? mainDataSource.dbId;
        const oldDbId = getOldConnectionId(mainDataSource);
        // fetchedTables[key].dataSource = mainDataSource;
        fetchedTables[key] = transformFetchedTable(value, key, mainDataSource, oldDbId);
        // fetchedTables[key].columns = transformFetchedColumns({columns: value.columns, tableKey: null, dbId: mainDataSource.dbId, table: fetchedTables[key]})
    }

    if (connections) {
        try{
            connections.map(eachConnection => {
                const oldDbId = getOldConnectionId(eachConnection.dataSource);
                let currentDataSource = transformDataSources(eachConnection.dataSource, eachConnection.classifier);
                    currentDataSource.isExistedDS = true;
                for (let [key, value] of Object.entries(eachConnection.tables)) {
                    let tableKey = key;
                    if (key in fetchedTables) {
                        //check if the same table is present in store tables with different key
                        if(!eachConnection.dataSource.dbId) {
                           throw new Error('dbId is missing in connection dataSource');
                        }
                        tableKey = `${key}_${eachConnection.dataSource.dbId}`
                    }
                    fetchedTables[tableKey] = JSON.parse(JSON.stringify(value));  
                    fetchedTables[tableKey] = transformFetchedTable(value, tableKey, currentDataSource, oldDbId);
                }
                storeDataSources.push(currentDataSource);              
            })
        } catch(error){
            console.log('error in updateStoreWithFetchedMetadata. Joins part', error)
        }
        
    }

    //   ------ updating security data  -------
    if(Object.keys(fetchedTables).length) {
        const mappedUniqueKeys = mapStoredTablesUniqueKeysWithFetchedTables(storeTables, fetchedTables);
        const updatedSecurityTableData =  transformSecurityTable({mappedUniqueKeys, securityTableData});
        dispatch(setSecurityTableData(updatedSecurityTableData));
    }

    for (let [, value] of Object.entries(fetchedTables)) {
        if (value.type === 'view') {
            //As of Now: Views should have uuid as we are using uuid in views everywhere.
            fetchedViews.push({...value, uuid: uuidv4() })
        }
    }

    // if (Object.keys(storeTables).length && !failedFetching) {
    //     storeTables = syncFetchedTablesWithStoreTables({ storeTables, fetchedTables, fetchedMetadata, store, connections })
    // }
    // else {
    //     storeTables = fetchedTables
    // }
    // if (failedFetching) {
    //     let tableKeys = Object.keys(storeTables)
    //     try{
    //         tableKeys.forEach(key => {
    //             let uuid = uuidv4()
    //             fetchedTables[key] = {
    //                 ...fetchedTables[key],
    //                 keyName: key,

    //                 uuid: uuid,
    //                 key: uuid,
    //                 keyPath: uuid,
    //                 // dbId: dataSource.dbId,
    //                 category: fetchedTables[key].type === 'view' ? 'view' : 'table',
    //                 selected: true,
    //                 // dataSource,
    //                 columnsFetched: true,
    //                 connId: fetchedTables[key].dataSource.dbId,
    //                 data: {
    //                     id: fetchedTables[key].dataSource.id,
    //                     type: fetchedTables[key].dataSource.type,
    //                 },
    //                 columns: transformFetchedColumns({columns: fetchedTables[key].columns, tableKey: key, dbId: fetchedTables[key].dataSource.dbId, table: fetchedTables[key]})
    //             }
    //         })
    //     } catch(error) {
    //         console.log('Error in updateStoreWithFetchedMetadata', error)
    //     }
        
    // }
    savedTableIds = Object.values(fetchedTables).map(table => table.id)
    Object.values(fetchedTables).map(table => {
        Object.values(table?.columns).forEach(col => {
            // savedColumnIds.push(col.columnId)
            savedColumnIds.push({ columnId: col.id, tableId: table.id }) // added this to have about which all columns are saved and are of whihc table
        })
    })
    fetchedJoins = fetchedJoins.map((eachJoin, index) => {
        eachJoin = cloneDeep(eachJoin)
        eachJoin.action = 'noChange'
        eachJoin.uuid = eachJoin.id
        eachJoin.key = eachJoin.id
        eachJoin.index = index + 1
        return eachJoin
    })

    // let fetchedViewIds = fetchedViews.map(view => view.id)
    // if (!failedFetching && storeViews) {
    //     storeViews = storeViews.map(eachView => {
    //         if (!fetchedViewIds.includes(eachView.id)) return false
    //         delete eachView.isModified
    //         return eachView
    //     }).filter(Boolean)
    // }
    // let storeViewIds = storeViews.map(v => v.id)
    // fetchedViews.forEach(view => {
    //     if (!storeViewIds.includes(view.id)) {
    //         view.uuid = uuidv4()
    //         view.category = 'view'
    //         view.keyName = view.alias
    //         for (const [key, value] of Object.entries(view.columns)) {
    //             view.columns[key] = { ...value, category: 'column', parentCategory: 'view', uuid: uuidv4(), tableKey: view.alias, columnKey: key }
    //         }
    //         storeViews.push(view)
    //     }
    // })
    !failedFetching && dispatch(metadataActions.updateMetadataState({
        //TODO: if save api fails change fetched tables to store tables here
        key: 'tables', value: fetchedTables, others: [
            { key: 'savedTableIds', value: savedTableIds },
            { key: 'savedColumnIds', value: savedColumnIds },
            { key: 'joins', value: fetchedJoins },
            { key: 'views', value: fetchedViews },
            { key: 'activeView', value: false },
            { key: 'editViewsTempData', value: {} }
        ]
    }))
    // let dsAddedToMeta = [{ ...dataSource, classifier }]
    // if (parentRecord?.category === "dataSource") {
    //     dsAddedToMeta[0].datasourceName = parentRecord.name
    //     dsAddedToMeta[0].connId = connId
    // }
    //if no driver in dsAddedToMeta then check it from store.getState().metadata.allDataSources where it matches id and type

    failedFetching && dispatch(metadataActions.handleUpdateStoreWithFetchedMetadata({
        key: 'tables', value: fetchedTables, others: [
            { key: 'savedTableIds', value: savedTableIds },
            { key: 'savedColumnIds', value: savedColumnIds },
            { key: 'joins', value: fetchedJoins },
            { key: 'dataSourcesAddedToMetadata', value: storeDataSources },
            { key: 'dataSource', value: updatedStoreDs.length ? updatedStoreDs: storeDataSources },
            { key: 'views', value: fetchedViews },
            { key: 'activeDsInfoId', value: mainDataSource.dbId },
            { key: 'editViewsTempData', value: {} },
            { key: 'activeView', value: '' },
            { key: 'activeViewInfo', value: {} }
        ],
        __initiator: 'updateStoreWithFetchedMetadata.js'
    }));

    //formdata state cleanup from redux after save only
    //TODO: write a clean up function for formData properties stored in redux. E.g, duplicateTables, changedTables etc.
    dispatch(metadataActions.resetChangedTables());
    dispatch(metadataActions.isAfterSaveMode(true));
    if (returnData) {
        return { fetchedJoins, fetchedViews, updatedStoreDs }
    }
};
