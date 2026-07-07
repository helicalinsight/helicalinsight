import produce from "immer"
import cloneDeep from "lodash-es/cloneDeep"
import merge from "lodash-es/merge"
import actionTypes from "../actions/actionTypes"
import initialStates from "./initialStates"
import { handleDeleteSelectedJoinsLogic, handleDiscardJoinLogic, handleJoinCreateLogic } from "./reducer.helperMethods"
let { METADATA: {
    SERVICE_GET_DATASOURCE,
    UPDATE_LOADING_STATUS,
    UPDATE_METADATA_STATE,
    FETCH_LIST_DATASOURCES,
    UPDATE_FETCHED_DATASOURCES,
    UPDATE_WORKFLOW_DATALIST,
    UPDATE_METADATA_TABLES,
    UPDATE_ACTIVE_EDITOR_TAB,
    RESET_METADATA_STATE,
    UPDATE_DATASOURCE_INFO,
    RESET_DUPLICATE_DATA,
    UPDATE_JOINS_DATA,
    METADATA_DETAILS_TO_EDIT,
    INITIAL_STATE_FOR_JEST,
    INITIAL_EDIT_RESPONSE,
    SERVICE_ERROR_STATUS,
    DELETE_VIEW,
    TOGGLE_MD_EDITOR_VIEW,
    SELECTED_TABLE_OR_COLUMN_KEY,
    SAVE_EXPRESSION_ID,
    SECURITY_CONSTANTS,
    SECURITY_EDIT,
    IS_ALLOW_SERVICE_CALL,
    IS_VALIDATED_TABLE_SHOW,
    SECURITY_TABLE_DATA,
    ONE_MORE_SECURITY,
    TEXT_EDITING_OBJ,
    JOIN_HIGHLIGHT,
    MENU_FILTER,
    FIRST_RENDER,
    SECURITY_FORM_DATA,
    ACCESS_TYPE,
    ENTITY_NAMES,
    EXECUTION_TYPE,
    EXPRESSION_NAME,
    EXPRESSION_TYPE,
    SET_APPLY_BTN_STATE,
    IS_INFO_SHOW,
    SECURITY_KEYS_CHECKED,
    UPDATE_TABLE_KEYS,
    GETSECURITY_TABLE_DATA,
    SET_RESET_FORMDATA,
    AFTER_METADATA_SAVE,
    UPDATE_ACTIVE_DS_INFO_ID,
    JOINS_LOADING_STATUS,
    SET_METADATA_LOADING_STATUS,
    VIEWS_CONN_ID,
    UPDATE_TABLES_WITH_NEW_VIEWS,
    CREATE_JOIN,
    UPDATE_JOIN,
    DELETE_SELECTED_JOINS,
    DELETE_INVALID_JOINS,
    DISCARD_JOIN,
    UPDATE_JOINS_WITH_CHANGED_DS,
    UPDATE_TABLES_JOINS_WITH_CHANGED_DS,
    ADD_DUPLICATE_TABLE,
    IS_SHOW_JOIN_MODAL,
    RESET_CAHNGED_TABLES_DATA,
    UPDATE_TABLE,
    UPDATE_VIEW_ALIAS,
    UPDATE_REMOVED_TABLES_STATE,
    IS_AFTER_SAVE_MODE,
    VIEW_FETCHING,
    ADD_COLUMNS_TO_TABLE,
    IS_GET_SECURITY_SERVICE_DONE,
    DATASOURCE_EXPANDED_ROW_KEYS,
    METADATA_SECTION_EXPANDED_ROWS,
    METADATA_SAVING_PROGRESS,
    UPDATE_STORE_WITH_FETCHED_METADATA_STATE,
    SET_ACTIVE_VIEW_INFO,
    SET_VIEW_NAME,
    SET_CONDITION_AND_FILTER_VALUE,
    UPDATE_SECURITY_ITEMS_ON_EXP_EDIT,
    UNDO_REDO_PURPOSE,
    EXPRESSION_DELETE_SELECTED,
    OUTSIDE_CLICKED
} } = actionTypes

const initialState = initialStates.metadataInitialState

const metadataReducer = (state = initialState, action) => {
    switch (action.type) {
        case SET_CONDITION_AND_FILTER_VALUE: {
            const data = action.payload || []
            return produce(state, draft => {
                data.forEach(item => {
                    if(item.key === 'conditionIfCondition') {
                        draft.conditionAndFilterValue["conditionIf"]["condition"].value = item.value;
                    } else  if(item.key === 'groovyCondition') {
                        draft.conditionAndFilterValue["groovy"]["condition"].value = item.value;
                    } else  if(item.key === 'conditionIfFilter') {
                        draft.conditionAndFilterValue["conditionIf"]["filter"].value = item.value;
                    } else  if(item.key === 'groovyFilter') {
                        draft.conditionAndFilterValue["groovy"]["filter"].value = item.value;
                    }
                })
            })
        }
        case UNDO_REDO_PURPOSE: {
            const data = action.payload || []
            return produce(state, draft => {
                data.forEach(item => {
                    if(item.key === 'joins') {
                        if (!draft.dataFetchedFor.joins) {
                            draft.dataFetchedFor.joins = true
                        }
                        draft.tablesMergeType = false
                    }
                    draft[item.key] = item.value;
                })
            })
        }
        case UPDATE_SECURITY_ITEMS_ON_EXP_EDIT: {
            const formData = action.payload;
            return { ...state, accessType: formData["Access Type"], executionType: formData["Execution Type"], expressionName: formData["Expression Name"], expressionType: formData["Expression Type"], securityKeysChecked: formData.securityKeysToBeCheck || []}
        }
        case SET_VIEW_NAME: {
            return { ...state, viewName: action.payload}
        }
        case SET_ACTIVE_VIEW_INFO: {
            return { ...state, activeViewInfo: action.payload}
        }
        case METADATA_SAVING_PROGRESS: {
            return { ...state, isSavingInProgress: action.payload}
        }
        case DATASOURCE_EXPANDED_ROW_KEYS: {
            return { ...state, datasourceExpandedRowKeys: state.datasourceExpandedRowKeys.includes(action.payload) ? state.datasourceExpandedRowKeys.filter(ele => ele !== action.payload) : [...state.datasourceExpandedRowKeys, action.payload]}
        }
        case METADATA_SECTION_EXPANDED_ROWS: {
            return { ...state, metadataSectionExpandedRows: state.metadataSectionExpandedRows.includes(action.payload) ? state.metadataSectionExpandedRows.filter(ele => ele !== action.payload) : [...state.metadataSectionExpandedRows, action.payload]}
        }
        case IS_GET_SECURITY_SERVICE_DONE: {
            return { ...state, isGetSecurityCallDone: action.payload || false }
        }
        case VIEW_FETCHING: {
            return { ...state, isViewFetching: {...state.isViewFetching, [action.payload.type]: action.payload.status}}
        }
        case IS_SHOW_JOIN_MODAL: {
            return { ...state, isShowJoinModal: action.payload || false }
        }
        
        case ADD_COLUMNS_TO_TABLE: {
            const table = action.payload
            return produce(state, draft => {
                if(state.tables[table.keyName]) {
                    draft.tables[table.keyName] = {...table}
                }
            })
        }
        case RESET_CAHNGED_TABLES_DATA: {
            return produce(state, draft => {
                draft.changedTables = [];
                draft.changedColumns = [];
                draft.removedTables = [];
                draft.removedColumns = [];
            })
        }
        case IS_AFTER_SAVE_MODE: {
            return produce(state, draft => {
                draft.isAfterSaveMode = action.payload
            })
        }
        case UPDATE_REMOVED_TABLES_STATE: {
            return produce(state, draft => {
                draft.removedTables = action.payload
            })
        }
        case UPDATE_TABLES_JOINS_WITH_CHANGED_DS: {
            return produce(state, draft => {
                let dataSource = state.dataSource[0];
                   Object.keys(state.tables).map(tableName => {
                        draft.tables[tableName].connId = dataSource.connId
                        draft.tables[tableName].keyPath = dataSource.dsKeyPath
                        draft.tables[tableName].nameWithConnId = `${draft.tables[tableName].name}_${dataSource.connId}`
                        let colKeys = Object.keys(draft.tables[tableName].columns)
                        colKeys.forEach(eachColKey => {
                            draft.tables[tableName].columns[eachColKey].connId = dataSource.connId;
                        })
                        draft.tables[tableName].dataSource = {
                            ...state.tables[tableName].dataSource,
                            id: dataSource.id,
                            type: dataSource.type,
                            baseType: dataSource.baseType,
                            catalog: dataSource.catalog,
                            schema: dataSource.schema,
                            connId: dataSource.connId,
                            classifier: dataSource.classifier,
                            datasourceName: dataSource.datasourceName,
                            driverType: dataSource.driverType,
                            database: dataSource.database
                        }
                        draft.tables[tableName].database = dataSource.database
                        draft.tables[tableName].schema = dataSource.schema
                        draft.tables[tableName].catalog = dataSource.catalog
                        draft.tables[tableName].dataSourceName = dataSource.dataSourceName
                        draft.tables[tableName].data = {id: dataSource.id, type: dataSource.type}
                        // let joinDataSource= {
                        //     id: state.dataSource.id,
                        //     catalog: state.dataSource.catalog,
                        //     schema: state.dataSource.schema,
                        //     type: state.dataSource.type,
                        //     baseType: state.dataSource.baseType
        // }
                        // draft.joins.forEach(join => {
                        //   join.left.dataSource = {...join.left.dataSource, ...joinDataSource};
                        //   join.right.dataSource = {...join.right.dataSource, ...joinDataSource};
                        // })
                   })
            })
        }

        case UPDATE_TABLE: {
            const changedTable = action.payload;
            return produce(state, draft => {
                draft.changedTables.push(changedTable)
            })
        }

        case UPDATE_VIEW_ALIAS: {
            const changedView = action.payload;
            return produce(state, draft => {
                draft.views = state.views.map(view => {
                    if(view.id === changedView.id) {
                        return {...view, alias: changedView.alias};
                    }
                    return view;
                })
            })
        }
      
        case UPDATE_JOINS_WITH_CHANGED_DS: {
            return produce(state, draft => {
                // let dataSource= {
                //     id: state.dataSource[0].id,
                //     catalog: state.dataSource[0].catalog,
                //     schema: state.dataSource[0].schema,
                //     type: state.dataSource[0].type,
                //     baseType: state.dataSource[0].baseType
        // }
                // draft.joins.forEach(join => {
                //   join.left.dataSource = {...join.left.dataSource, ...dataSource};
                //   join.right.dataSource = {...join.right.dataSource, ...dataSource};
                // })
            })
        }
        case DISCARD_JOIN: {
            return produce(state, draft => {
                const {record} = action.payload;
                handleDiscardJoinLogic({state, draft, record})
            })
        }
        case DELETE_INVALID_JOINS: {
            const invalidJoins = action.payload;
            let index = 0;
            return produce(state, draft => {
                draft.joins = state.joins.map(join => {
                    index++;
                    if(invalidJoins.includes(join.id)) {
                        index--;
                        const newJoin = {...join, action: "delete"};
                        delete newJoin.index;
                        return newJoin;
                    }
                    if(join.index) {
                        return {...join, index: index};
                    } else {
                        index--;
                        return join;
                    }
                })
                // handleDeleteInvalidJoinsLogic({state, draft, listOfTableColumnNames, tables})
            })
        }
        case DELETE_SELECTED_JOINS: {
            return produce(state, draft => {
                const selectedJoins = action.payload;
                handleDeleteSelectedJoinsLogic({state, draft, selectedJoins})         
            })
        }
        case ADD_DUPLICATE_TABLE: {
            const table = action.payload;
            return produce(state, (draft) => {
                draft.duplicateTableList = [...state.duplicateTableList, table.id];
                draft.tables[table.keyName] = {...table}
            })
        }

        case UPDATE_JOIN: {
            return produce(state, draft => {
                const {record, type, dataSource, value, currentOption} = action.payload;
                // updating left / right of join
                draft.joins.forEach(join => {
                    if (join.uuid === record.key) {
                        join[type === 'left' ? 'leftColumn' : 'rightColumn'] = value
                        join[type] = {
                            ...join[type],
                            table: currentOption.tableName,//value.split('.')[0],
                            column: currentOption.columnName, //value.split('.')[1],
                            // dataSource: dataSource,
                            tableId: currentOption?.tableId,
                            columnId: currentOption?.columnId,
                            dbId: currentOption?.dbId,
                            // connId: currentOption?.connId,
                        }
                    }
                })
            })
        }
        case CREATE_JOIN: {
            return produce(state, draft => {
                // action.payload  === new join object
                // adding new join to existed ones
                handleJoinCreateLogic({state, draft, action})
            })
        }
        case UPDATE_TABLES_WITH_NEW_VIEWS: {
            return produce(state, draft => {
                state.views.forEach(view => {
                   Object.keys(state.tables).map(tableName => {
                    if(state.tables[tableName].category === 'view' && !state.tables.connId && state.tables[tableName]?.data?.id === view?.data?.id && state.tables[tableName]?.data?.type === view.data?.type) {
                        draft.tables[tableName].connId = view.connId || draft.tables[tableName].connId 
                        draft.tables[tableName].nameWithConnId = `${draft.tables[tableName].name}_${view.connId || draft.tables[tableName].connId}`
                        let colKeys = Object.keys(draft.tables[tableName].columns)
                        colKeys.forEach(eachColKey => {
                            draft.tables[tableName].columns[eachColKey].connId = view.connId || draft.tables[tableName].columns[eachColKey].connId;
                        })
                        draft.tables[tableName].dataSource.connId = view.connId || draft.tables[tableName].dataSource.connId
                        draft.tables[tableName].database = view.database || draft.tables[tableName].database
                    }
                   })
                })

            })
        }
        case VIEWS_CONN_ID: {
           const payload = action.payload.dataSource;
           const dupViews = cloneDeep(state.views);
           const dupTables = cloneDeep(state.tables);
           console.log(payload, dupTables)
           dupViews?.forEach(dupView => {
            if(dupView.dataSource.id === payload.id && dupView.dataSource.type === payload.type) {
                dupView.connId = payload.connId
                dupView.nameWithConnId = `${dupView.name}_${payload.connId}`
                let colKeys = Object.keys(dupView.columns)
                colKeys.forEach(eachColKey => {
                    dupView.columns[eachColKey].connId = payload.connId;
                })
                dupView.dataSource.connId = payload.connId
                dupView.database = payload.database
            }
           })
            return { ...state, views: dupViews };
        }
        case AFTER_METADATA_SAVE: {
            let dupGetSecuriyTableData = { tables: [], columns: [] };
            state.securityTableData.forEach(ele => {
                if (ele["Access Type"] === 'deny') {
                    if (ele["Expression Type"] !== 'column') {
                        dupGetSecuriyTableData.tables = [...dupGetSecuriyTableData.tables, ...ele["Entity Names"].split(",")];
                    } else {
                        ele["Entity Names"].split(",").forEach((name) => {
                            let arr = name.split('.');
                            dupGetSecuriyTableData.columns = [
                                ...dupGetSecuriyTableData.columns,
                                { table: arr[0], column: arr[1] }
                            ];
                        });
                    }
                }
            })
            return { ...state, getSecurityTableData: dupGetSecuriyTableData };
        }

        case SET_METADATA_LOADING_STATUS: {
            const payload = action.payload;
            return {...state, setMetadataLoading: {...state.setMetadataLoading, ...payload}};
        }

        case SET_RESET_FORMDATA: {
            return produce(state, draft => {
                draft.doResetFormData = !state.doResetFormData;
                const {securityConstants} = state;
                if(Object.keys(securityConstants).length) {
                    draft.accessType = securityConstants.access[1];
                    draft.executionType = securityConstants.type[0];
                    draft.expressionName = '';
                    draft.expressionType = securityConstants.expressionType[0];
                    draft.securityKeysChecked = [];
                    draft.conditionAndFilterValue["conditionIf"]["condition"].value = securityConstants.conditionIf?.conditionTemplate;
                    draft.conditionAndFilterValue["groovy"]["condition"].value = securityConstants.groovy?.conditionTemplate;
                    draft.conditionAndFilterValue["conditionIf"]["filter"].value = securityConstants.conditionIf?.filterTemplate;
                    draft.conditionAndFilterValue["groovy"]["filter"].value = securityConstants.groovy?.filterTemplate;
                    draft.formData = {};
                }
            })
        }
        case GETSECURITY_TABLE_DATA: {
            return { ...state, getSecurityTableData: action.payload };
        }
        case SECURITY_KEYS_CHECKED: {
            return { ...state, securityKeysChecked: action.payload };
        }
        case IS_INFO_SHOW: {
            return { ...state, isInfoShow: action.payload };
        }
        case SET_APPLY_BTN_STATE: {
            return { ...state, isApplyDisabled: action.payload };
        }
        case EXPRESSION_TYPE: {
            const payload = action.payload;
            return produce(state, draft => {
                // if(payload === 'column') {
                //    draft.expressionType = payload;
                // }
                // if(!state.expressionType) {
                    draft.expressionType = payload;
                // }
            })
            // return { ...state, expressionType: action.payload };
        }
        case EXPRESSION_NAME: {
            return { ...state, expressionName: action.payload };
        }
        case EXECUTION_TYPE: {
            return { ...state, executionType: action.payload };
        }
        case ENTITY_NAMES: {
            return { ...state, entityNames: action.payload };
        }
        case ACCESS_TYPE: {
            return { ...state, accessType: action.payload };
        }
        case SECURITY_FORM_DATA: {
            return { ...state, securityFormData: action.payload };
        }
        case FIRST_RENDER: {
            return { ...state, isFirstRender: action.payload };
        }
        case MENU_FILTER: {
            return { ...state, filterbyData: action.payload };
        }
        case JOIN_HIGHLIGHT: {
            return { ...state, selectedJoinNameData: action.payload };
        }
        case TEXT_EDITING_OBJ: {
            return { ...state, textEditingObj: action.payload };
        }
        case ONE_MORE_SECURITY: {
            return { ...state, addOneMoreSecurity: action.payload };
        }
        case SECURITY_TABLE_DATA: {
            return { ...state, securityTableData: action.payload };
        }
        case IS_VALIDATED_TABLE_SHOW: {
            return { ...state, isValidatedTableShow: action.payload };
        }
        case SECURITY_EDIT: {
            return { ...state, edit: action.payload };
        }
        case SECURITY_CONSTANTS: {
            return { ...state, securityConstants: action.payload };
        }
        case OUTSIDE_CLICKED: {
            return { ...state, outsideClicked: action.payload };
        }
        case EXPRESSION_DELETE_SELECTED: {
            let nonDuplicateArr = [];
            nonDuplicateArr = state.expressionObj.filter(deletedExp => {
                return !action.payload.find(ele => ele.expressionId === deletedExp.expressionId);
            })
            nonDuplicateArr = [...nonDuplicateArr, ...action.payload];
            return { ...state, expressionObj: nonDuplicateArr };
        }
        case SAVE_EXPRESSION_ID: {
            let duplicateArr = [];
            if (['edit', 'delete'].includes(action.payload.action)) {
                duplicateArr = state.expressionObj.filter(ele => ele.expressionId !== action.payload.expressionId);
                duplicateArr = [...duplicateArr, action.payload];
            } else if (action.payload.action === 'retrive') {
                duplicateArr = [];
            } else {
                duplicateArr = [...state.expressionObj, action.payload];
            }
            return { ...state, expressionObj: Array.isArray(action.payload) ? [] : duplicateArr };
        }
        case SELECTED_TABLE_OR_COLUMN_KEY: {
            return produce(state, draft => {
                const {securityConstants} = state;
                // if(Object.keys(securityConstants).length) {
                    if(action.payload.category !== 'column') {
                        draft.expressionType = state.expressionType || 'global';
                    } else {
                        draft.expressionType = 'column';
                    }
                // }
                draft.selectedTableOrColumnKey = action.payload;
            })
        }
        case IS_ALLOW_SERVICE_CALL: {
            return { ...state, isAllowServiceCall: action.payload }
        }
        case SERVICE_GET_DATASOURCE: {
            return { ...state, fetchedDSInfo: action.payload }
        }
        case FETCH_LIST_DATASOURCES: {
            return { ...state, listDataSource: action.payload }
        }
        case UPDATE_FETCHED_DATASOURCES: {
            return Object.assign({}, { ...state, ...action.payload })
        }
        case UPDATE_LOADING_STATUS: {
            return produce(state, draft => {
                let { onlyDataFetchedFor = false } = action.payload
                draft.dataFetchedFor[action.payload.type] = action.payload.status
                !onlyDataFetchedFor && (draft.loadingStatus[action.payload.type] = action.payload.status)
            })
        }
        case UPDATE_STORE_WITH_FETCHED_METADATA_STATE:
        case UPDATE_METADATA_STATE: {
            let { key, value, mode, metadataForEdit = false, notSelectedKeys = false, others = false, checked = false, mergeType = false, actualMergeType = false, updateEditorTab = true, deleteViews = false, multiConnection = false, type } = action.payload
            return produce(state, draft => {
                // draft[action.payload.key] = action.payload.value
                if (key === 'datasourceListToRender') {
                    // draft.datasourceListToRender = [...value]
                    draft.datasourceListToRender = multiConnection ? merge(draft.datasourceListToRender, value) : [...value]
                } else if(key === 'activeDsInfoId') {
                    draft[key] = value;
                }
                else if (mode === 'selectedTables') {
                    if (typeof key === 'string') {
                        if (checked) {
                            // draft.selectedTableKeys = [...draft.selectedTableKeys].push(key)
                            draft.selectedTableKeys = [...new Set([...draft.selectedTableKeys, key])]
                        }
                        else {
                            draft.selectedTableKeys = [...draft.selectedTableKeys].filter(val => val !== key)
                        }
                    }
                    else if (Array.isArray(key)) {
                        if (checked) {
                            draft.selectedTableKeys = [...new Set([...draft.selectedTableKeys, ...key])]
                        }
                        else {
                            draft.selectedTableKeys = [...draft.selectedTableKeys].filter(val => key.indexOf(val) === -1)
                        }
                    }
                }
                else if (key === 'saveDetails') {
                    draft[key] = {
                        location: value.location,
                        uuid: value.uuid
                    }
                    if (value.metadataName) {
                        let metadataName = value.metadataName
                        if (metadataName.includes('.')) {
                            metadataName = metadataName.split('.')[0]
                        }
                        draft.metadataName = metadataName
                    }
                    // if (updateEditorTab) {
                    //     draft.activeEditorTab = 'saveActions'
                    // }

                }
                else if (key === 'savedTableIds') {
                    draft[key] = multiConnection ? [...new Set([...draft[key], ...value])] : value
                }
                else {
                    if (mode === 'updateTables') {
                        if (mergeType === 'reload') {
                            draft.dataFetchedFor.joins = false
                            draft['tables'] = { ...value }
                            //  database : (catalog && schema) ? `${catalog}.${schema}` : (catalog ? catalog : schema)
                            draft.activeEditorTab = 'info'
                            if (metadataForEdit) { //4584
                                draft.dataFetchedFor.joins = true
                            }
                        }
                        else {
                            draft['tables'] = { ...draft.tables, ...value }
                        }
                        notSelectedKeys && notSelectedKeys.map(key => {
                            delete draft.tables[key]
                        })
                        let result = updateDatasourcesInMetadata({ tables: draft.tables, dataSource: draft.dataSource, removedDataSources: draft.removedDataSources, mergeType, actualMergeType })
                        draft['dataSourcesAddedToMetadata'] = result[0]
                        // draft.dataSource = result[0] // #5092 -> this is for single connection
                        if (result[1] && result[0][0]) { //-> this is for multiple connections. 
                            draft.dataSource.push(result[0][0])
                            // draft.dataSource = [result[0][0]]
                        }
                        if (actualMergeType === 'reload') {
                            draft.getSecurityTableData = { tables: [], columns: [] };
                            if (!draft.isValidatedTableShow) {
                                draft.doResetFormData = !draft.doResetFormData;
                                draft.isValidatedTableShow = false;
                                if (!draft.edit && !draft.addOneMoreSecurity) {
                                    draft.selectedTableOrColumnKey = {};
                                    draft.isInfoShow = true;
                                    draft.securityKeysChecked = [];
                                }
                                if (draft.addOneMoreSecurity) {
                                    draft.securityKeysChecked = [];
                                }
                            }
                        }
                    }
                    else {
                        draft[action.payload.key] = action.payload.value
                        if (others) {
                            others.map(({ key, value, type }) => {
                                if(type === 'loading') {
                                    draft.dataFetchedFor[key] = value
                                    draft.loadingStatus[key] = value
                                } else {
                                    draft[key] = value
                                }
                            })
                        }
                    }
                }
                if (others) {
                    others.map(({ key, value, type }) => {
                        if (key === 'datasourceListToRender') {
                            // draft.datasourceListToRender = [...value]
                            draft.datasourceListToRender = multiConnection ? merge(draft.datasourceListToRender, value) : [...value]
                        }
                        else {
                            if (key === 'dataSourcesAddedToMetadata' || key === 'dataSource') {
                                let database;
                                if (value[0].catalog && value[0].schema) {
                                    database = `${value[0].catalog}.${value[0].schema}`
                                } else if (value[0].catalog) {
                                    database = value[0].catalog
                                } else {
                                    database = value[0].schema
                                }
                                // draft[key] = value;
                                // value[0].database = database
                                value = cloneDeep(value.map((eachVal, index) => {
                                    if (index === 0) {
                                        // eachVal.database = database
                                        eachVal = produce(eachVal, draft => {
                                            draft.database = database
                                        })
                                    }
                                    return eachVal
                                }))
                                value[0].driver && (value[0].driverType = state.allDataSourceTypes.find(ele => {
                                    if(ele.sendVendorName) {
                                        if(!value[0].driver?.vendorName)
                                        return false;
                                    }
                                    if(ele.driver === value[0].driver?.driver) {
                                        return true;
                                    }
                                })?.name);

                                draft[key] = value
                                // draft[key][0].database = database;
                            } else {
                                if(type !== 'loading') {
                                    draft[key] = value
                                }
                            }
                        }
                    })
                }
                if (deleteViews) {
                    draft.views = []
                }
                if(actualMergeType) {
                    draft.tablesMergeType = actualMergeType;
                    draft.isShowJoinModal = true;
                }
            })
        }
        case UPDATE_METADATA_TABLES: {
            return produce(state, draft => {
                let { override = false,
                    data,
                    duplicateTableList = false,
                    changedTables = false,
                    removedTables = false,
                    changedColumns = false,
                    removedColumns = false,
                    updateDS = false,
                    updateDSAddedToMetada = false,
                    dataSource = false,
                    removedDS = false,
                } = action.payload
                if(duplicateTableList) {
                    draft.duplicateTableList = duplicateTableList
                }
                if (changedTables) {
                    draft.changedTables = changedTables
                }
                if (removedTables) {
                    draft.removedTables = removedTables
                }
                if (changedColumns) {
                    draft.changedColumns = changedColumns
                }
                if (removedColumns) {
                    draft.removedColumns = removedColumns
                }
                draft.tables = override ? data : { ...state.tables, ...action.payload.data }
                if (updateDS && dataSource !== false) {
                    draft.dataSource = dataSource
                }
                if (updateDSAddedToMetada && dataSource !== false) {
                    draft.dataSourcesAddedToMetadata = dataSource
                }
                if (Array.isArray(removedDS)) {
                    function getUniqueListBy(arr, key) {
                        return [...new Map(arr.map(item => [item[key], item])).values()]
                    }
                    // draft.removedDataSources = [...state.removedDataSources, ...removedDS]
                    draft.removedDataSources = getUniqueListBy([...state.removedDataSources, ...removedDS], 'connId')
                }
            })
            // return {...state, tables : {...state.tables, ...action.payload.data}}
        }
        case UPDATE_WORKFLOW_DATALIST: {
            let { mode, data, datasourceListToRender = false, dataSource = false, newTablesAdded = false, noCatSchema = false } = action.payload
            if (mode === 'add') {
                return produce(state, draft => {
                    let isDataAvailable = false
                    draft.workFlow.dataList = draft.workFlow.dataList.map(ds => {
                        if (ds.dsUUID === data.dsUUID) {
                            isDataAvailable = true
                            return data
                        }
                        return ds
                    })
                    !isDataAvailable && draft.workFlow.dataList.push(data)
                    if (datasourceListToRender) {
                        draft.datasourceListToRender = datasourceListToRender
                    }
                    if (dataSource) {
                        // draft.dataSourcesAddedToMetadata = draft.dataSourcesAddedToMetadata.map(ds => { // this is for single connection
                        //     if (ds.id === dataSource.id && ds.type === dataSource.type) {
                        //         ds = { ...ds, ...dataSource }
                        //     }
                        //     return ds
                        // })
                        if (draft.dataSource.filter(ds => ds?.connId === dataSource.connId).length === 0) {
                            let availableDS = draft.dataSource.filter(ds => (ds?.id === dataSource.id && ds?.type === dataSource.type))
                            if (noCatSchema && availableDS.length) {
                                //check if ds with id and type already available
                                if (availableDS.length) {
                                    draft.dataSource = draft.dataSource.map(ds => {
                                        if (ds?.id === dataSource.id && ds?.type === dataSource.type) {
                                            ds = { ...ds, ...dataSource }
                                        }
                                        return ds
                                    })
                                }
                            }
                            else {
                                draft.dataSource = [...draft.dataSource, dataSource]
                            }
                        }
                        let connIds = Object.values(draft.tables).map(table => table.connId)
                        let tableConns = draft.dataSource.filter(ds => connIds.includes(ds.connId))
                        draft.dataSourcesAddedToMetadata = tableConns
                    }
                    if (newTablesAdded) {
                        draft.allTablesKeys = [...new Set([...draft.allTablesKeys, ...newTablesAdded])]
                    }
                    //update dsToRender
                })
            }
            return { ...state }
        }
        case UPDATE_ACTIVE_EDITOR_TAB: {
            // if (state.activeEditorTab !== action.payload.data){
            return produce(state, draft => {
                draft.activeEditorTab = action.payload
            })
            // }
        }
        case UPDATE_DATASOURCE_INFO: {
            let { data } = action.payload
            return produce(state, draft => {
                draft.dataSource = draft.dataSource.map(ds => {
                    if (ds.connId === data.connId) {
                        return data
                    }
                    return ds
                })
                draft.dataSourcesAddedToMetadata = draft.dataSourcesAddedToMetadata.map(ds => {
                    if (ds.connId === data.connId) {
                        return data
                    }
                    return ds
                })
                // draft.dataSource = data
            })
        }
        case UPDATE_JOINS_DATA: {
            let { data, override = true, multiConnection = false } = action.payload
            return produce(state, draft => {
                if (override && !multiConnection) {
                    draft.joins = data
                }
                else {
                    draft.joins = [...draft.joins, ...data]
                    if (multiConnection) {
                        draft.joins = draft.joins.map((join, index) => {
                            return { ...join, index: index + 1 }
                        })
                    }
                }
                if (!draft.dataFetchedFor.joins) {
                    draft.dataFetchedFor.joins = true
                }
                draft.tablesMergeType = false
            })
        }
        case RESET_DUPLICATE_DATA: {
            let { payload } = action
            if (payload?.resetChanged === false) {
                return produce(state, draft => {
                    draft.duplicateColumnList = []
                    draft.duplicateTableList = []
                    draft.unsavedViews = []
                    draft.removedTables = draft.removedTables.map(table => { table.alreadySaved = true; return table })
                    draft.removedColumns = draft.removedColumns.map(table => { table.alreadySaved = true; return table })
                    draft.changedTables = draft.changedTables.map(table => { table.alreadySaved = true; return table })
                    draft.changedColumns = draft.changedColumns.map(table => { table.alreadySaved = true; return table })
                    if (payload?.dataFetchedForJoins) {
                        draft.dataFetchedFor = { ...draft.dataFetchedFor, joins: true }
                    }
                })
            }
            return {
                ...state,
                duplicateColumnList: [],
                duplicateTableList: [],
                unsavedViews: [],
                removedTables: [],
                removedColumns: [],
                changedTables: [],
                changedColumns: [],
            }
        }
        case RESET_METADATA_STATE: {
            let { mode, others } = action.payload
            return { ...initialState, mode, ...others }
        }
        case METADATA_DETAILS_TO_EDIT: {
            let { details } = action.payload
            return produce(state, draft => {
                draft.metadataToEdit = details
                draft.mode = 'edit'
            })
        }
        case INITIAL_STATE_FOR_JEST: {
            let data = action.payload
            return cloneDeep({ ...state, ...data, inititalStateFromJest: true })
        }
        case INITIAL_EDIT_RESPONSE: {
            let data = action.payload
            return produce(state, draft => {
                draft.initialEditResponse = cloneDeep(data)
            })
        }
        case SERVICE_ERROR_STATUS: {
            let data = action.payload
            return produce(state, draft => {
                // if(data.status && draft.serviceErrorStatus.indexOf(data.key) === -1){
                //     draft.serviceErrorStatus.push(data.key)
                // }
                // if (!data.status && draft.serviceErrorStatus.indexOf(data.key) !== -1){
                //     draft.serviceErrorStatus = draft.serviceErrorStatus.filter(elem => elem !== data.key)
                // }
                if (data.status && !(data.key in draft.serviceErrorStatus)) {
                    draft.serviceErrorStatus[data.key] = data.message
                }
                if (!data.status && data.key in draft.serviceErrorStatus) {
                    delete draft.serviceErrorStatus[data.key]
                }
            })
        }
        case DELETE_VIEW: {
            let data = action.payload
            return produce(state, draft => {
                draft.views = draft.views.map(eachView => {
                    if (eachView.id === data.id) {
                        eachView.isDeleted = true
                    }
                    return eachView
                })
            })
        }
        case TOGGLE_MD_EDITOR_VIEW: {
            return produce(state, draft => {
                draft.editorFullView = !draft.editorFullView
            })
        }
        case UPDATE_TABLE_KEYS: {
            let data = action.payload
            return produce(state, draft => {
                draft.allTablesKeys = [...new Set([...draft.allTablesKeys, ...data])]
            })
        }
        case UPDATE_ACTIVE_DS_INFO_ID: {
            let data = action.payload
            return produce(state, draft => {
                draft.activeDsInfoId = data
            })
        }
        case JOINS_LOADING_STATUS: {
            let { id, status } = action.payload
            let { joinsLoadingStatus } = state
            if (status) {
                joinsLoadingStatus = joinsLoadingStatus.length ? `${joinsLoadingStatus}__${id}__` : `__${id}__`
            }
            else {
                joinsLoadingStatus = joinsLoadingStatus.split(`__${id}__`).join('')
            }
            return { ...state, joinsLoadingStatus }
        }
        default:
            return { ...state }
    }
}

export const updateDatasourcesInMetadata = ({ tables, dataSource, removedDataSources, mergeType, actualMergeType }) => {
    let result = [...new Set(Object.values(tables).map(eachTable => eachTable.dataSource?.dbId ?? eachTable.connId))]
    let ds = dataSource.filter(ds => {
        return result.indexOf(ds?.connId) != -1
    })
    if (!actualMergeType) {
        actualMergeType = mergeType;
    }
    if (actualMergeType === 'reload') {
        if (ds[0]) {
            if ('originalCatalog' in ds[0]) {
                ds[0].catalog = ds[0].originalCatalog;
                ds[0].schema = ds[0].originalSchema;
                delete ds[0].originalSchema;
                delete ds[0].originalCatalog;
            }
            if (ds[0].catalog && ds[0].schema) {
                ds[0].database = `${ds[0].catalog}.${ds[0].schema}`
            } else if (ds[0].catalog) {
                ds[0].database = ds[0].catalog
            } else {
                ds[0].database = ds[0].schema
            }
        }
    }
    if (!ds.length) {
        ds = removedDataSources.filter(ds => {
            return result.indexOf(ds.connId) != -1
        })
        return [ds, true] // tru :: update datasource
    }
    return [ds, false] // false :: donot update datasource
}

window.__test_updateDatasourcesInMetadata = updateDatasourcesInMetadata
export default metadataReducer;
