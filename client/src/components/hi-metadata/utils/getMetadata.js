import { updateStoreWithFetchedMetadata } from './updateStoreWithFetchedMetadata'
import requests from '../../../base/requests'
import { fetchSchema } from './fetchSchema'
import { fetchDatasource } from './fetchDatasource'
import { handleTableDropToMetadata } from './handleTableDropToMetadata'
import { cloneDeep } from 'lodash-es'
import { handleDSTableCheck } from './handleDSTableCheck'
import { metadataActions, setMetadataLoadingStatus } from "../../../redux/actions";
import { updateTablesWithColumns } from './updateTablesWithColumns'
import { fetchJoins } from './fetchJoins'
import { handleFetchedViews } from './handleFetchedViews'
import { fetchCatalogs } from '.'

export const getMetadata = ({ location, uuid, dispatch, store, returnFetched, datasourceListToRender = false, mode, jestUtil = () => { } }) => {
    let formData = {
        location: location,
        uniqueId: true,
        metadataFileName: uuid,
        provideJoins: true
    }
    let expandKeysHierarchy = []
    return requests.metadata(dispatch).getMetadataForEdit(formData, "", fetchedMetadata => {
        dispatch(metadataActions.setIntialEditResponse({ ...cloneDeep(fetchedMetadata) }));
        dispatch(setMetadataLoadingStatus({ isMetadataFetched: true}));
        handleFetchedMetadata({
            fetchedMetadata, expandKeysHierarchy, location, uuid, dispatch, store, returnFetched, datasourceListToRender, mode, jestUtil, multiConnection: ('connections' in fetchedMetadata), checkForCrossJoins: !!(fetchedMetadata?.crossJoins?.length)
        });
        //TODO: check the below part. It may not be required
        fetchedMetadata?.connections.forEach(eachConn => {
            handleFetchedMetadata({
                fetchedMetadata: {
                    ...eachConn,
                    metadataName: fetchedMetadata.metadataName,
                    metadataDir: fetchedMetadata.metadataDir
                }, expandKeysHierarchy, location, uuid, dispatch, store, returnFetched, datasourceListToRender, mode, jestUtil, multiConnection: ('connections' in fetchedMetadata)
            })
        });
    });
}

const handleFetchedMetadata = ({
    fetchedMetadata, expandKeysHierarchy, location, uuid, dispatch, store, returnFetched, datasourceListToRender, jestUtil, multiConnection = false, checkForCrossJoins = false
}) => {
    let fetchedDS = fetchedMetadata.dataSource
    if (fetchedMetadata?.joins) {
        fetchJoins({ dataSource: fetchedDS, forEditMeta: true, joinsToUpdate: fetchedMetadata.joins, dispatch, multiConnection, checkForCrossJoins, fetchedMetadata })
    }
    let views = Object.values(fetchedMetadata.tables).filter(table => table.type === 'view')
    if (views.length) {
        handleFetchedViews({
            dispatch,
            views,
            fetchedDS,
            store
        })
    }
    dispatch(metadataActions.updateMetadataState({ key: 'saveDetails', value: { location, uuid }, updateEditorTab: false, others: [{ key: 'metadataName', value: fetchedMetadata.metadataName }] }))
    dispatch(metadataActions.updateLoadingStatus({ type: 'joins', status: true })) //4584 fix.
    let { id, type, dir } = fetchedDS
    let currentDS = null
    datasourceListToRender.every(eachDS => {
        let result = false
        eachDS.children.every(child => {
            if (child?.data?.id == id
                // && child?.data?.type === type
                // && ('dir' in child.data ? child.data.dir === dir : true)
            ) {
                result = true
                currentDS = child
                return false
            }
            if (result) {
                return false
            }
            return true
        })
        if (currentDS) {
            return false
        }
        return true
    })
    expandKeysHierarchy = currentDS?.keyPath?.split('/') || [];
    updateStoreWithFetchedMetadata({ fetchedMetadata, dispatch, store, failedFetching: true, datasourceListToRender }); 

    // TODO - Note: The below code is deprecated. We will be removing this code as this is unnecessary 
    
    // let finalTablesResult = null
    // let finalDSToRender = null
    // if (!currentDS) {
    //     // updateStoreWithFetchedMetadata({ fetchedMetadata, dispatch, store, failedFetching: true, datasourceListToRender, formData, response, parentRecord })
    //     updateStoreWithFetchedMetadata({ fetchedMetadata, dispatch, store, failedFetching: true, datasourceListToRender })
    // }
    // else if (currentDS && fetchedMetadata?.dataSource?.schema?.length && !fetchedMetadata?.dataSource?.catalog?.length) {
    //     /**
    //      * if schema is available and catalog is not available DERBY
    //      */
    //     fetchDatasource({
    //         record: currentDS,
    //         dispatch,
    //         store,
    //         config: {
    //             edit: true,
    //             info: {
    //                 schema: fetchedMetadata.dataSource.schema,
    //                 catalog: fetchedMetadata.dataSource.catalog
    //             }
    //         },
    //         updateDSToRenderToRedux: true,
    //         afterFetching: ({ record }) => {
    //             expandKeysHierarchy.push(record.uuid)
    //             fetchSchema({
    //                 record,
    //                 store,
    //                 dispatch,
    //                 updateDSToRenderToRedux: false,
    //                 fetchedMetadata,
    //                 afterFetching: ({ tables, datasourceListToRender, tablesInSchema, duplicateTables, updatedDS }) => {
    //                     jestUtil({ tables: tables, updatedDS, datasourceListToRender })
    //                     datasourceListToRender = cloneDeep(datasourceListToRender)
    //                     let requiredTable = null
    //                     Object.values(fetchedMetadata.tables).forEach(table => {
    //                         let { id } = table
    //                         requiredTable = tablesInSchema.filter(eachTable => eachTable.id === id)[0]
    //                         if (requiredTable) {
    //                             datasourceListToRender = handleDSTableCheck({
    //                                 checked: true,
    //                                 record: requiredTable,
    //                                 dispatch,
    //                                 returnData: true,
    //                                 dsListToRender: datasourceListToRender
    //                             })
    //                             dispatch(metadataActions.updateMetadataState({
    //                                 mode: 'selectedTables',
    //                                 key: requiredTable.uuid,
    //                                 checked: true
    //                             }))
    //                         }
    //                     })
    //                     finalDSToRender = cloneDeep(datasourceListToRender)
    //                     handleTableDropToMetadata({
    //                         record: requiredTable || tablesInSchema[0],
    //                         dispatch,
    //                         store,
    //                         datasourceListToRender,
    //                         fetchedMetadata,
    //                         fetchedTables: tables,
    //                         duplicateTables,
    //                         afterDropping: ({ tables }) => {
    //                             let tablesCopy = cloneDeep(tables)
    //                             let tableResult = tables
    //                             Object.values(tablesCopy).forEach(eachTable => {
    //                                 if (fetchedMetadata.tables[eachTable.name]?.columns) {
    //                                     tableResult = updateTablesWithColumns({
    //                                         duplicateTable: false,
    //                                         result: {
    //                                             metadata: {
    //                                                 table: {
    //                                                     [eachTable.name]: {
    //                                                         columns: fetchedMetadata.tables[eachTable.name].columns
    //                                                     }
    //                                                 }
    //                                             }
    //                                         },
    //                                         tables: tableResult,
    //                                         dispatch,
    //                                         currentTable: eachTable,
    //                                         returnTables: true,
    //                                     })
    //                                 }
    //                             })
    //                             finalTablesResult = tableResult
    //                             if (finalTablesResult) {
    //                                 dispatch(metadataActions.updateMetadataState({
    //                                     key: 'tables',
    //                                     mode: 'updateTables',
    //                                     value: finalTablesResult,
    //                                     others: [
    //                                         {
    //                                             key: 'datasourceListToRender',
    //                                             value: cloneDeep(finalDSToRender)
    //                                         },
    //                                         // {
    //                                         //     key: 'dataSourcesAddedToMetadata',
    //                                         //     value: [updatedDS]
    //                                         // }
    //                                     ],
    //                                     __intitator: 'getMetadata:168'
    //                                 }))
    //                             }
    //                         }
    //                     })
    //                     // }
    //                     if (finalTablesResult) {
    //                         dispatch(metadataActions.updateMetadataState({
    //                             // key: 'tables',
    //                             // mode: 'updateTables',
    //                             // value: finalTablesResult,
    //                             others: [
    //                                 // {
    //                                 //     key: 'datasourceListToRender',
    //                                 //     value: cloneDeep(finalDSToRender)
    //                                 // },
    //                                 {
    //                                     key: 'dataSourcesAddedToMetadata',
    //                                     value: [updatedDS]
    //                                 }
    //                             ]
    //                         }))
    //                     }
    //                 }
    //             })
    //         },
    //         handleError: ({ formData, response, parentRecord }) => {
    //             updateStoreWithFetchedMetadata({ fetchedMetadata, dispatch, store, failedFetching: true, datasourceListToRender, formData, response, parentRecord })

    //         }
    //     })
    // }
    // else if (fetchedMetadata?.dataSource?.schema?.length && fetchedMetadata?.dataSource?.catalog?.length) {
    //     /**
    //      * catalog and schema are both present -- POSTGRES
    //      */
    //     // ["postgres", "athena"].includes(currentDS.name) && fetchDatasource({ // #4803 fix, wrote this case whihc will only execute only for postgres
    //     // ["Postgresql", "Athena"].includes(currentDS.driverType) &&
    //     fetchDatasource({
    //         record: currentDS, dispatch, store,
    //         config: {
    //             edit: true,
    //             info: {
    //                 schema: fetchedMetadata.dataSource.schema,
    //                 catalog: fetchedMetadata.dataSource.catalog
    //             }
    //         },
    //         updateDSToRenderToRedux: true,
    //         afterFetching: ({ record, info, dsListToRender }) => {
    //             fetchSchema({
    //                 record,
    //                 store,
    //                 dispatch,
    //                 updateDSToRenderToRedux: false,
    //                 fetchedMetadata,
    //                 dsListToRender: cloneDeep(dsListToRender),
    //                 info,
    //                 catSchema: true,
    //                 afterFetching: ({ tables, datasourceListToRender, tablesInSchema, duplicateTables, updatedDS }) => {
    //                     expandKeysHierarchy.push(record.uuid);
    //                     if (info?.schema && info?.catalog) {
    //                         if (record.children?.length) {
    //                             record.children.forEach((child) => {
    //                                 if (child.name === info.schema) {
    //                                     expandKeysHierarchy.push(child.uuid);
    //                                 }
    //                             })
    //                         }
    //                     }
    //                     datasourceListToRender = cloneDeep(datasourceListToRender)
    //                     let requiredTable = null
    //                     Object.values(fetchedMetadata.tables).forEach(table => {
    //                         let { id } = table
    //                         requiredTable = tablesInSchema.filter(eachTable => eachTable.id === id)[0]
    //                         // requiredTable = tables.filter(eachTable => eachTable.id === id)[0]
    //                         if (requiredTable) {
    //                             datasourceListToRender = handleDSTableCheck({
    //                                 checked: true,
    //                                 record: requiredTable,
    //                                 dispatch,
    //                                 returnData: true,
    //                                 dsListToRender: datasourceListToRender
    //                             })
    //                             dispatch(metadataActions.updateMetadataState({
    //                                 mode: 'selectedTables',
    //                                 key: requiredTable.uuid,
    //                                 checked: true
    //                             }))
    //                         }
    //                     })
    //                     finalDSToRender = cloneDeep(datasourceListToRender)
    //                     if (requiredTable || true) {
    //                         handleTableDropToMetadata({
    //                             record: requiredTable || tablesInSchema[0],
    //                             dispatch,
    //                             store,
    //                             datasourceListToRender,
    //                             fetchedMetadata,
    //                             fetchedTables: tables,
    //                             duplicateTables,
    //                             afterDropping: ({ tables }) => {
    //                                 let tablesCopy = cloneDeep(tables)
    //                                 let tableResult = tables
    //                                 Object.values(tablesCopy).forEach(eachTable => {
    //                                     if (fetchedMetadata.tables[eachTable.name]?.columns) {
    //                                         tableResult = updateTablesWithColumns({
    //                                             duplicateTable: false,
    //                                             result: {
    //                                                 metadata: {
    //                                                     table: {
    //                                                         [eachTable.name]: {
    //                                                             columns: fetchedMetadata.tables[eachTable.name].columns
    //                                                         }
    //                                                     }
    //                                                 }
    //                                             },
    //                                             tables: tableResult,
    //                                             dispatch,
    //                                             currentTable: eachTable,
    //                                             returnTables: true,
    //                                         })
    //                                     }
    //                                 })
    //                                 finalTablesResult = tableResult
    //                             }
    //                         })
    //                     }
    //                     if (finalTablesResult) {
    //                         dispatch(metadataActions.updateMetadataState({
    //                             key: 'tables',
    //                             mode: 'updateTables',
    //                             value: finalTablesResult,
    //                             multiConnection,
    //                             others: [
    //                                 {
    //                                     key: 'datasourceListToRender',
    //                                     value: cloneDeep(finalDSToRender)
    //                                 },
    //                                 // {
    //                                 //     key: 'dataSourcesAddedToMetadata',
    //                                 //     value: [updatedDS]
    //                                 // }
    //                             ]
    //                         }))
    //                     }
    //                 }
    //             })
    //         },
    //         handleError: ({ formData, response, parentRecord }) => {
    //             updateStoreWithFetchedMetadata({ fetchedMetadata, dispatch, store, failedFetching: true, datasourceListToRender, formData, response, parentRecord })

    //         }
    //     }
    //     )
    // }
    // else if (!fetchedMetadata?.dataSource?.schema?.length && !fetchedMetadata?.dataSource?.catalog?.length) {
    //     /**
    //      * both schema and catalogs are not present  -  SQLITE
    //      */
    //     fetchDatasource({
    //         record: currentDS, dispatch, store,
    //         config: {
    //             edit: true,
    //             info: {
    //                 schema: fetchedMetadata.dataSource.schema,
    //                 catalog: fetchedMetadata.dataSource.catalog
    //             }
    //         },
    //         noCatSchema: true,
    //         fetchedMetadata,
    //         updateDSToRenderToRedux: false,
    //         afterFetching: ({
    //             dsListToRender,
    //             tables,
    //             duplicateTables
    //         }) => {
    //             // return
    //             let fetchedTables = fetchedMetadata.tables
    //             let requiredTable = null
    //             datasourceListToRender = dsListToRender
    //             Object.values(tables).forEach(table => {
    //                 let { id } = table
    //                 requiredTable = table
    //                 if (Object.values(fetchedTables).map(t => t.id).indexOf(id) !== -1) {
    //                     datasourceListToRender = handleDSTableCheck({
    //                         checked: true,
    //                         record: cloneDeep(requiredTable),
    //                         dispatch,
    //                         returnData: true,
    //                         dsListToRender: datasourceListToRender
    //                     })
    //                     dispatch(metadataActions.updateMetadataState({
    //                         mode: 'selectedTables',
    //                         key: requiredTable.uuid,
    //                         checked: true
    //                     }))
    //                 }
    //             })
    //             dispatch(metadataActions.updateMetadataState({
    //                 key: 'datasourceListToRender', value: datasourceListToRender, multiConnection
    //             }))
    //             handleTableDropToMetadata({
    //                 record: requiredTable,
    //                 dispatch,
    //                 store,
    //                 datasourceListToRender,
    //                 fetchedMetadata,
    //                 // fetchedTables: tables,
    //                 fetchedTables: fetchedTables,
    //                 duplicateTables: duplicateTables || [],
    //                 noCatSchema: true,
    //                 afterDropping: ({ tables }) => {
    //                     let tablesCopy = cloneDeep(tables)
    //                     let tableResult = cloneDeep(tables)
    //                     let dsInfoFromTable = null
    //                     Object.values(tablesCopy).map(eachTable => {
    //                         if (!fetchedMetadata.tables[eachTable.name]) {
    //                             return
    //                         }
    //                         dsInfoFromTable = cloneDeep(eachTable.dataSource)
    //                         let tablesWithColsResult = updateTablesWithColumns({
    //                             duplicateTable: false,
    //                             result: {
    //                                 metadata: {
    //                                     table: cloneDeep({
    //                                         [eachTable.name]: {
    //                                             columns: fetchedMetadata.tables[eachTable.name].columns
    //                                         }
    //                                     })
    //                                 }
    //                             },
    //                             tables: cloneDeep(tableResult),
    //                             dispatch,
    //                             currentTable: cloneDeep(eachTable),
    //                             returnTables: true,
    //                         })
    //                         tableResult = null
    //                         tableResult = tablesWithColsResult
    //                     })
    //                     finalTablesResult = tableResult
    //                     if (finalTablesResult) {
    //                         if (!dsInfoFromTable?.driverType && currentDS?.driverType) {
    //                             dsInfoFromTable.driverType = currentDS.driverType
    //                         }
    //                         dispatch(metadataActions.updateMetadataState({
    //                             key: 'tables',
    //                             mode: 'updateTables',
    //                             value: finalTablesResult,
    //                             multiConnection,
    //                             others: [
    //                                 {
    //                                     key: 'datasourceListToRender',
    //                                     value: cloneDeep(datasourceListToRender)
    //                                 },
    //                                 // {
    //                                 //     key: 'dataSourcesAddedToMetadata',
    //                                 //     value: [dsInfoFromTable]
    //                                 // }
    //                             ]
    //                         }))
    //                     }
    //                 }
    //             })

    //         },
    //         handleError: ({ formData, response, parentRecord }) => {
    //             updateStoreWithFetchedMetadata({ fetchedMetadata, dispatch, store, failedFetching: true, datasourceListToRender, formData, response, parentRecord })

    //         }
    //     })
    // }
    // else {
    //     /**
    //      * catalog is available and schema is not
    //      */
    //     fetchDatasource({
    //         record: currentDS, dispatch, store,
    //         config: {
    //             edit: true,
    //             info: {
    //                 schema: fetchedMetadata.dataSource.schema,
    //                 catalog: fetchedMetadata.dataSource.catalog
    //             }
    //         },
    //         afterFetching: ({ record, info }) => {
    //             expandKeysHierarchy.push(record.uuid);
    //             fetchCatalogs({
    //                 record,
    //                 store,
    //                 dispatch,
    //                 updateDSToRenderToRedux: false,
    //                 fetchedMetadata,
    //                 info,
    //                 afterFetching: ({ tables, datasourceListToRender, dataSource, tablesInSchema, duplicateTables, updatedDS }) => {
    //                     currentDS = dataSource
    //                     datasourceListToRender = cloneDeep(datasourceListToRender)
    //                     let requiredTable = null
    //                     Object.values(fetchedMetadata.tables).forEach(table => {
    //                         let { id } = table
    //                         requiredTable = tablesInSchema.filter(eachTable => eachTable.id === id)[0]
    //                         if (requiredTable) {
    //                             datasourceListToRender = handleDSTableCheck({
    //                                 checked: true,
    //                                 record: requiredTable,
    //                                 dispatch,
    //                                 returnData: true,
    //                                 dsListToRender: datasourceListToRender
    //                             })
    //                             dispatch(metadataActions.updateMetadataState({
    //                                 mode: 'selectedTables',
    //                                 key: requiredTable.uuid,
    //                                 checked: true
    //                             }))
    //                         }
    //                     })
    //                     finalDSToRender = cloneDeep(datasourceListToRender)
    //                     if (requiredTable || true) {
    //                         handleTableDropToMetadata({
    //                             record: requiredTable || tablesInSchema[0],
    //                             dispatch,
    //                             store,
    //                             datasourceListToRender,
    //                             fetchedMetadata,
    //                             fetchedTables: tables,
    //                             duplicateTables,
    //                             afterDropping: ({ tables }) => {
    //                                 let tablesCopy = cloneDeep(tables)
    //                                 let tableResult = tables
    //                                 Object.values(tablesCopy).forEach(eachTable => {
    //                                     if (fetchedMetadata.tables[eachTable.name]?.columns) {
    //                                         tableResult = updateTablesWithColumns({
    //                                             duplicateTable: false,
    //                                             result: {
    //                                                 metadata: {
    //                                                     table: {
    //                                                         [eachTable.name]: {
    //                                                             columns: fetchedMetadata.tables[eachTable.name].columns
    //                                                         }
    //                                                     }
    //                                                 }
    //                                             },
    //                                             tables: tableResult,
    //                                             dispatch,
    //                                             currentTable: eachTable,
    //                                             returnTables: true,
    //                                         })
    //                                     }
    //                                 })
    //                                 finalTablesResult = tableResult
    //                             }
    //                         })
    //                     }
    //                     if (finalTablesResult) {
    //                         dispatch(metadataActions.updateMetadataState({
    //                             key: 'tables',
    //                             mode: 'updateTables',
    //                             value: finalTablesResult,
    //                             multiConnection,
    //                             others: [
    //                                 {
    //                                     key: 'datasourceListToRender',
    //                                     value: cloneDeep(finalDSToRender)
    //                                 },
    //                                 // {
    //                                 //     key: 'dataSourcesAddedToMetadata',
    //                                 //     value: [updatedDS]
    //                                 // }
    //                             ]
    //                         }))
    //                     }
    //                 }
    //             })
    //         },
    //         handleError: ({ formData, response, parentRecord }) => {
    //             updateStoreWithFetchedMetadata({ fetchedMetadata, dispatch, store, failedFetching: true, datasourceListToRender, formData, response, parentRecord })

    //         }
    //     })

    // }
    // // expandTableUI({ expandKeysHierarchy })
    // expandTableUIQueue({ expandKeysHierarchy })
    if (returnFetched) {
        //TODO: check this condition. It may not be required
        if (fetchedMetadata?.tables) {
            let tableIds = Object.values(fetchedMetadata.tables).map(table => table.id)
            dispatch(metadataActions.updateMetadataState({
                key: 'savedTableIds', value: tableIds, multiConnection
            }))
        }
        dispatch(metadataActions.clearUndoRedoHistory())
        return fetchedMetadata
    }
    updateStoreWithFetchedMetadata({ fetchedMetadata, dispatch, store });
    dispatch(metadataActions.clearUndoRedoHistory())

}

let cache = []
let queue = []

const expandTableUIQueue = ({ expandKeysHierarchy }) => {
    if (!queue.length) {
        queue.push(expandKeysHierarchy)

        expandTableUI({ expandKeysHierarchy })
    }
    else {
        queue.push(expandKeysHierarchy)
    }
}

let count = 0

const expandTableUI = ({ expandKeysHierarchy = [], index = 0 }) => {
    if (!expandKeysHierarchy.length) {
        return
    }
    if (count > 250) { //failproof
        return
    }
    if (index === 0 && cache.indexOf(expandKeysHierarchy.join('__')) !== -1) return
    cache.indexOf(expandKeysHierarchy.join('__')) === -1 && cache.push(expandKeysHierarchy.join('__'))
    let elem = document.getElementById(expandKeysHierarchy[index])?.previousElementSibling
    if ((elem?.classList || '').toLocaleString().includes('collapsed')) {
        elem?.click()
    }
    try {
        elem.scrollIntoView({
            behavior: 'smooth'
        })
    }
    catch (e) {
    }
    if (expandKeysHierarchy.length >= index + 1) {
        setTimeout(() => {
            expandTableUI({ expandKeysHierarchy, index: index + 1 })
        }, 1000);
    }
    count++
    setTimeout(() => {
        expandTableUI({ expandKeysHierarchy: queue[0] || [] })
        queue.shift()
    }, 1000);
}
