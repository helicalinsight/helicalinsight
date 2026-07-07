/* eslint-disable no-loop-func */
import { updateDatasourceListToRender } from ".";
import {
  handleJoinHighlight,
  metadataActions,
  updateTable,
  removedTablesState
} from "../../../redux/actions";
import { cloneDeep } from "lodash-es";
// import { uuid } from "../../../utils/uuid";
// import { updateTablesWithColumns } from "./updateTablesWithColumns";
import { genrateUniqueName } from "./utils";

export const handleTableDropToMetadata = (arg) => {
  let {
    record,
    // datasourceListToRender,
    dispatch = () => {},
    store,
    type = "merge",

    //parameters not required for this function
    // afterDropping = () => {
    //   return "_default_function_";
    // },

    // fetchedMetadata,
    // duplicateTables,
    noCatSchema = false,
    // multiConnection = false,
    returnData = false,
    returnDataWithDuplicateTables = false,
    tablesAddedToMetadata_test = false,
    fetchedTables = false,
    returnDataForJest = false,
    tablesToAdd = false,
  } = arg;
  let { keyPath } = record;
  //same as type: Not required
  // let actualMergeType = type;
  keyPath = keyPath.split("/");
  keyPath.pop();
  let schemaPath = keyPath.join("/");
  let tablesAddedToMetadata = store?.getState()?.metadata?.present.tables || {};
  let savedTableIds = store?.getState()?.metadata?.present.savedTableIds || [];
  let savedColumnIds = store?.getState()?.metadata?.present.savedColumnIds || [];
  let datasourceListToRender = store?.getState().metadata.present.datasourceListToRender;
  let dataSourcesAddedToMetadata = store.getState().metadata.present.dataSourcesAddedToMetadata;
  let removedTables = store.getState().metadata.present.removedTables;
  removedTables = JSON.parse(JSON.stringify(removedTables));

  // let mode = store?.getState()?.metadata?.mode
  // let initialTableCount = Object.keys(tablesAddedToMetadata || {}).length;
  let manipulatedResult = {};
  let deleteViews = false;
  // let areViewsAvailable = Object.values(fetchedMetadata?.tables || {}).filter(
  //   (table) => table.type === "view"
  // ).length;

  Object.keys(tablesAddedToMetadata).map((key) => {
    if (tablesAddedToMetadata[key].type === "view") {
      manipulatedResult[key] = tablesAddedToMetadata[key];
    }
  });

  let { selected = [], notSelected = [] } =
    updateDatasourceListToRender({
      datasourceListToRender: cloneDeep(datasourceListToRender),
      keyPath: schemaPath,
      options: {
        type: "dragTablesToMetadata",
        returnResult: true,
        tablesToAdd,
      },
    }) || {};

  if (type === "reload") {
    // collapsing expanded tables using JS
    let expandedElements = [
      ...document.querySelectorAll(
        ".metadata-table-section .ant-table-row-expand-icon-expanded"
      ),
    ];
    expandedElements.forEach((element) => {
      typeof element?.click === "function" && element.click();
    });
    tablesAddedToMetadata = {};
    manipulatedResult = {};
    savedTableIds = [];
    savedColumnIds = [];
    deleteViews = true;

    selected.forEach((table) => {
      table.keyName = table.keyName ?? table.name;
      manipulatedResult[table.keyName] = { ...table };
    });
  }
  if (tablesAddedToMetadata_test) {
    // added this to check reload and merge
    tablesAddedToMetadata = tablesAddedToMetadata_test;
  }

  // if (initialTableCount === 0 && selected?.length === 0) {
  //   //5354
  //   if (!areViewsAvailable) return;
  // }

  if (type === "merge") {
    //tablesAddedToMetadata will have keyName

    for (const [storeTableKey, storeTable] of Object.entries(
      tablesAddedToMetadata
    )) {
      const storeTableId = storeTable.cacheId ?? storeTable.id;
      let tableIsUnselected = true;
      notSelected.forEach((notSelectedTable) => {
        if (
          (storeTable.oldDbId ?? storeTable.connId) === notSelectedTable.connId &&
          storeTableId === notSelectedTable.id && !storeTable.duplicate
        ) {
          tableIsUnselected = false;
          //dispatch an action for table delete and check for mode
          removedTables.push({id: storeTable.id, connId: storeTable.connId})
        }
      });
      if (tableIsUnselected)
        manipulatedResult[storeTableKey] = { ...storeTable };
    }
    selected.forEach((table) => {
      let flag = true;
      for (const [key, value] of Object.entries(manipulatedResult)) {
        const addedTableId = value.cacheId ?? value.id;
        if ((value.oldDbId ?? value.connId) === table.connId && addedTableId === table.id) {
          flag = false;
        }
      }
      if (flag) {
        let keyName = table.keyName ?? table.name;
        const tableDs = dataSourcesAddedToMetadata.find(ds => ds.id === table.dataSource.id && ds.type === table.dataSource.type);
        if(tableDs) {
          table.connId = tableDs.connId;
          table.dataSource.connId = tableDs.connId;
          table.dataSource.dbId = tableDs.dbId;
        }
        if (keyName in tablesAddedToMetadata) {
          keyName = `${keyName}_${table.connId}`;
          table.alias = genrateUniqueName({
            allItems: Object.values(tablesAddedToMetadata).map(
              (table) => table.alias
            ),
            item: table.alias,
          });
          dispatch(
            updateTable({
              id: table.id,
              alias: table.alias,
              connId: table.connId,
            })
          );
        }
        //attach new connId to table as datasource panel contains tables with old connID
        
        table.keyName = keyName;
        manipulatedResult[keyName] = { ...table };
      }
    });
    // for (const [key, storeTable] of Object.entries(tablesAddedToMetadata)) {
    //   Object.values(notSelected).forEach((notSelectedTable) => {
    //     const storeTableId = storeTable.cacheId ?? storeTable.id;
    //     if (
    //       notSelectedTable.id === storeTableId && notSelectedTable.dataSource.dbId === storeTable.dataSource.dbId
    //     ) {
    //         console.log('test')
    //     } else {
    //         manipulatedResult[key] = {...storeTable};

    //     }
    //   });
    // }
    // Object.keys(tablesAddedToMetadata).forEach(key => {
    //     manipulatedResult[key] = tablesAddedToMetadata[key]
    // });
    // selected = selected.filter(newlyDraggedTable => { // fixed getting table twice issue
    //     let isExisted = false;
    //     Object.values(tablesAddedToMetadata).forEach(tableAlreadyExisted => {
    //         if((newlyDraggedTable.id === (tableAlreadyExisted.cacheId ?? tableAlreadyExisted.id)) && (newlyDraggedTable.dataSource.dbId === tableAlreadyExisted.dataSource.dbId)) {
    //             isExisted = true;
    //         }
    //     })
    //     return !isExisted;
    // })
    if (returnDataForJest) {
      return selected; // this is used in JEST dont change
    }
    // let tablesAlreadyAvailable = []
    // selected = selected.map(selectedTable => {
    //     if (Object.values(tablesAddedToMetadata).filter(metadataTable => metadataTable.id === selectedTable.id).length) { // for single connection
    //         if (!(Object.values(tablesAddedToMetadata).filter(metadataTable => metadataTable.uuid === selectedTable.uuid).length)) {
    //             tablesAlreadyAvailable.push(selectedTable.name)
    //         }
    //         return cloneDeep(Object.values(tablesAddedToMetadata).filter(metadataTable => metadataTable.id === selectedTable.id)[0])
    //     }

    //     //for multi connection
    //     // if (Object.values(tablesAddedToMetadata).filter(metadataTable => metadataTable.uuid === selectedTable.uuid).length) {
    //     //     return cloneDeep(Object.values(tablesAddedToMetadata).filter(metadataTable => metadataTable.uuid === selectedTable.uuid)[0])
    //     // }
    //     return selectedTable
    // })
    // if (tablesAlreadyAvailable.length && afterDropping.toString().includes('_default_function_')) {
    //     notify(dispatch).warn('validation', `Catalog, schema and table name are appearing to be same for ${tablesAlreadyAvailable.join(', ')}. This is not supported at this time`
    // }
  }
  // selected.map(table => {
  //     let keyName = table.keyName || `${table.name}`
  //     if (keyName in tablesAddedToMetadata) {
  //         keyName = `${keyName}_${table.connId}`;
  //         table.alias = genrateUniqueName({allItems: Object.values(tablesAddedToMetadata).map(table => table.alias), item: table.alias });
  //         dispatch(updateTable({id: table.id, alias: table.alias, connId: table.connId}))
  //     }
  //     table.keyName = keyName;
  //     if (!Object.values(tablesAddedToMetadata).map(t => t.uniqueKey).includes(table.uniqueKey)) {
  //         manipulatedResult[keyName] = table
  //     }
  //     return null
  // })

  //TODO: Remove below statement. Used in reducer function. but can be removed from here are we removed this dependency
  let notSelectedKeys = notSelected.map((table) => {
    return table.keyName;
  });

  // add connection details and data from selected tables
  //TODO: fetchedMetadata is not used now
  // if (fetchedMetadata?.tables) {
  //     let fetchedTables = cloneDeep(fetchedMetadata.tables)
  //     Object.keys(fetchedMetadata.tables).forEach(key => {
  //         if (!fetchedTables[key].keyName) {
  //             let backUpTables = fetchedTables[key]
  //             delete fetchedTables[key]
  //             fetchedTables[key] = cloneDeep(
  //                 { ...backUpTables, keyName: key, columnsFetched: true }
  //             )
  //         }
  //     })
  //     fetchedMetadata.tables = fetchedTables
  //     //merge manipulatedResult and tables in fetched metadata
  //     Object.keys(fetchedTables).forEach(fetchedKey => {
  //         let manipulatedKey = fetchedKey;
  //         try {
  //             manipulatedKey = Object.values(manipulatedResult).filter(data => data.id === fetchedTables[fetchedKey].id)[0]?.keyName
  //         }
  //         catch (e) {
  //             console.log('Error getting actual Key', e)
  //         }

  //         if (manipulatedResult && manipulatedResult[manipulatedKey]?.alias) {
  //             manipulatedResult = { ...manipulatedResult, [manipulatedKey]: { ...manipulatedResult[manipulatedKey], alias: fetchedTables[fetchedKey].alias } }
  //         }
  //         else {
  //             if (manipulatedKey && duplicateTables.length && duplicateTables.filter(table => (table.name === fetchedKey)).length === 1) {
  //                 // manipulatedResult[key] = fetchedTables[key]
  //                 manipulatedResult[manipulatedKey] = { ...duplicateTables.filter(table => (table.name === fetchedKey))[0], ...fetchedTables[fetchedKey] } //5733
  //             }
  //         }
  //         if ((manipulatedKey && !manipulatedResult[manipulatedKey].columnsFetched && fetchedTables[fetchedKey].columns) || multiConnection) {
  //             manipulatedResult[manipulatedKey].columnsFetched = fetchedTables[fetchedKey].columnsFetched;
  //             manipulatedResult[manipulatedKey].columns = fetchedTables[fetchedKey].columns;
  //             manipulatedResult = updateTablesWithColumns({
  //                 duplicateTable: false,
  //                 result: {
  //                     metadata: {
  //                         table: {
  //                             [manipulatedKey]: {
  //                                 columns: manipulatedResult[manipulatedKey].columns
  //                             }
  //                         }
  //                     }
  //                 },
  //                 tables: manipulatedResult,
  //                 dispatch,
  //                 currentTable: manipulatedResult[manipulatedKey],
  //                 returnTables: true,
  //                 mode
  //             })
  //         }
  //     })
  // }

  // console.log('before updating tables', cloneDeep({ manipulatedResult, tab: fetchedMetadata.tables }))
  // Object.keys(manipulatedResult).map(manipulatedKey => {
  //     console.log('in fetched md', fetchedMetadata.tables)
  //     let fetchedResult = Object.values(fetchedMetadata.tables).filter(table => {
  //         return table.id === manipulatedResult[manipulatedKey].id
  //     }
  //     )[0]
  //     if (fetchedResult) {
  //         manipulatedResult[manipulatedKey].alias = fetchedResult.alias
  //     }
  // })

  //update aliases

  manipulatedResult = JSON.parse(JSON.stringify(manipulatedResult));
  // Object.keys(manipulatedResult).forEach(manipulatedKey => {
  //     if (fetchedMetadata?.tables) {
  //         let fetchedFilterd = Object.values(fetchedMetadata.tables).filter(t => {
  //             return t.id === manipulatedResult[manipulatedKey]?.id
  //         }
  //         )
  //         if (fetchedFilterd.length) {
  //             manipulatedResult[manipulatedKey].alias = fetchedFilterd[0].alias
  //         }
  //     }
  // })
  if (returnData) return manipulatedResult;

  // !noCatSchema && afterDropping({ tables: manipulatedResult })

  //adding views to manipulatedResult

  // TODO: fetchedMetadata is also not used
  // Object.values(fetchedMetadata?.tables || []).forEach(eachTable => {
  //     try {
  //         if (eachTable.type === 'view' || eachTable.duplicate) {
  //             let _uuid = uuid()
  //             manipulatedResult[eachTable.keyName] = { ...eachTable, uuid: _uuid, key: _uuid, columnsFetched: true }
  //             if (eachTable.duplicate) {
  //                 manipulatedResult[eachTable.keyName].category = 'table'
  //             }
  //             let tableValues = Object.values(fetchedMetadata?.tables)?.filter(t => !t.duplicate)
  //             if (tableValues.length) {
  //                 let filteredResult = Object.values(manipulatedResult).filter(result => result.id === tableValues[0].id)
  //                 // if (Object.values(manipulatedResult).filter())
  //                 if (filteredResult[0] && 'dataSource' in filteredResult[0]) {
  //                     let { dataSourceName, dataSource, connId } = filteredResult[0]
  //                     manipulatedResult[eachTable.keyName] = { ...manipulatedResult[eachTable.keyName], dataSourceName, dataSource, connId }
  //                 }
  //             }
  //             else if (manipulatedResult && Object.values(manipulatedResult)?.length && Object.values(manipulatedResult)[0]?.dataSource && Object.values(manipulatedResult)[0]?.dataSourceName) {
  //                 let { dataSourceName, dataSource, connId } = Object.values(manipulatedResult)[0]
  //                 manipulatedResult[eachTable.keyName] = { ...manipulatedResult[eachTable.keyName], dataSourceName, dataSource, connId }
  //             }
  //         }
  //     } catch (err) {
  //         console.error('Error caught in handle table drop to metadata', err)
  //     }
  // })

  if (returnDataWithDuplicateTables) return manipulatedResult;
  // noCatSchema && afterDropping({ tables: manipulatedResult })

  // TODO: Create Individual action for updating the store.
  !noCatSchema &&
    dispatch(
      metadataActions.updateMetadataState({
        key: "tables",
        mode: "updateTables",
        value: manipulatedResult,
        notSelectedKeys,
        mergeType: "reload",
        actualMergeType: type,
        metadataForEdit: !!fetchedTables,
        deleteViews,
        others: [
          {
            key: "savedTableIds",
            value: savedTableIds,
          },
          {
            key: "savedColumnIds",
            value: savedColumnIds,
          },
        ],
        __intitiator: "handleTableDropToMetadata",
      })
    );

    // dispatch action for removed tables
    dispatch(removedTablesState(removedTables))
  //TODO: Confirm if this is used or not
  if (type === "reload") {
    dispatch(handleJoinHighlight({}));
  }
};
