import { cloneDeep } from "lodash-es"
import { metadataActions } from "../../../redux/actions"
import { handleDeleteInvalidJoinsLogic, handleDeleteSelectedJoinsLogic, handleDiscardJoinLogic, handleJoinCreateLogic } from "../../../redux/reducers/reducer.helperMethods"
import { utilsJoinsActions } from "./joins/joins"
import {checkInvalidJoins} from "./joins/utils"
import { updateJoinsAfterAliasingTableColumn } from './updateJoinsAfterAliasingTableColumn'


export const handleJoinActions = ({
    record,
    type,
    action,
    dispatch,
    joins,
    selectedJoins,
    returnDataForJest = false,
    listOfTableColumnNames = false,
    tables
}) => {
const {handleJoinCreate, handleDeleteSelectedJoins, handleDeleteInvalidJoins, handleDiscardJoin} = utilsJoinsActions({dispatch, joins});

    if (!Array.isArray(joins)) {
        return false
    }
    joins = cloneDeep(joins)
    if (action === 'addNewJoin') {
        // validating to add new join - start
        if (checkEmptyJoins({ joins })) {
            return false
        }
        // validating to add new join - end
        // if (returnDataForJest) {
            let join =  handleJoinCreate({}) 
            joins = [join, ...joins];
            
        // handleJoinCreate({dispatch})
    } else if (action === 'discardSelectedJoins') {
        if (returnDataForJest) {
            joins = handleDeleteSelectedJoinsLogic({state: {joins}, draft: {joins: cloneDeep(joins)}, selectedJoins})  
        }
        handleDeleteSelectedJoins({selectedJoins})
    } else if (action === 'deleteInvalid') {
        if (returnDataForJest) {
            joins = handleDeleteInvalidJoinsLogic({state: {joins}, draft: {joins: cloneDeep(joins)}, listOfTableColumnNames, tables})
        }
        handleDeleteInvalidJoins({joins, listOfTableColumnNames, tables})
    }else if (action === 'discardJoin') {
        if (returnDataForJest) {
            joins = handleDiscardJoinLogic({state: {joins}, draft: {joins: cloneDeep(joins)}, record})
        }
        handleDiscardJoin({record})
    } else {
            if (type === 'type') {// to change join type
                joins.forEach(join => {
                    if (record.uuid === join.uuid) {
                        join.type = action
                        join.action = join.action === 'add' ? 'add' : 'update'
                    }
                })
            }
            else if (action === 'swapColumns') {
                joins.forEach(join => {
                    if (record.uuid === join.uuid) {
                        let oldLeft = { ...join.left }
                        join.left = join.right
                        join.right = oldLeft
                        join.leftColumn = `${join.left.table}.${join.left.column}`
                        join.rightColumn = `${join.right.table}.${join.right.column}`
                        join.action = join.action === 'add' ? 'add' : 'update'
                    }
                })
            }
            else if (action === 'discardColumn') {
                joins.forEach(join => {
                    if (record.uuid === join.uuid) {
                        join[type] = { table: false, column: false } 
                        join.leftColumn = `${join.left.table}.${join.left.column}`
                        join.rightColumn = `${join.right.table}.${join.right.column}`
                        join.action = join.action === 'add' ? 'add' : 'update'
                    }
                })
            }
            // else if (action === 'discardJoin'
            // /* || action === 'discardSelectedJoins' 5956 */ 
            // ) {
            //     // joins = joins.filter(join => join.uuid !== record.uuid)
            //     let joinIndex = 1
            //     let idsToDelete = []
            //     if (record.uuid) {
            //         idsToDelete.push(record.uuid)
            //     }
            //     else if (record.length) {
            //         idsToDelete = record.map(join => join.uuid)
            //     }
            //     /* if (action === 'discardSelectedJoins') {
            //         idsToDelete = selectedJoins
            //         if (!selectedJoins.length) {
            //             notify(dispatch).warn('Validation', 'Please select join(s) to delete')
            //         }
            //     } 5956*/
            //     joins = joins.map((join) => {
            //         if (idsToDelete.indexOf(join.uuid) !== -1) {
            //             join.action = 'delete'
            //             delete join.index
            //         }
            //         else if (join.action !== 'delete') {
            //             join.index = joinIndex
            //             joinIndex++
            //         }
            //         return join
            //     })
            // }
            // else if (action === 'addNewJoin') {
            //     // validating to add new join - start
            //     if (checkEmptyJoins({ joins })) {
            //         return false
            //     }
            //     // validating to add new join - end
            //     let unique = uuid(),
            //         joinIndex = 1
            //     joins = [{
            //         "type": "inner",
            //         "operator": "=",
            //         "left": {
            //             "table": false,
            //             "column": false,
            //             "alias": false
            //         },
            //         "right": {
            //             "table": false,
            //             "column": false,
            //             "alias": false
            //         },
            //         key: unique,
            //         uuid: unique,
            //         action: 'add'
            //     }, ...joins]
            //     joins = joins.map((join) => {
            //         if (join.action !== 'delete') {
            //             join.index = joinIndex;
            //             joinIndex++
            //         }
            //         return join
            //     })
            // }
            // else if (action === 'deleteInvalid') {
            //     joins = updateJoinsAfterAliasingTableColumn({ joins, tables }).map((join, index) => {
            //         if (validateTableColumn({ record: join.left, listOfTableNames: listOfTableColumnNames }) && validateTableColumn({ record: join.right, listOfTableNames: listOfTableColumnNames })) {
            //             return join
            //         }
            //         join.action = 'delete';
            //         delete join.index;
            //         return join
            //     });
            //     joins = joins.filter(join => !!join.index);
            //     joins = joins.map((join, index) => {
            //         join.index = index + 1;
            //         return join;
            //     });
            //     if (joins.filter(j => j.action === 'delete').length) {
            //         notify(dispatch).warn('Validation', 'No invalid joins present')
            //     }       
            // }
            else if (action === 'validateJoins') {
                let emptyJoins = false, invalidJoins = false
                let joinsToCheck = joins.filter(eachJoin => eachJoin.noAccess !== 'true')
                if (checkEmptyJoins({ joins: joinsToCheck })) {
                    emptyJoins = true
                }
                invalidJoins = checkInvalidJoins(joins, tables).length > 0;
                // let result = updateJoinsAfterAliasingTableColumn({ joins: joinsToCheck, tables }).map((join) => {
                //     if (validateTableColumn({ record: join.left, listOfTableNames: listOfTableColumnNames })
                //         &&
                //         validateTableColumn({ record: join.right, listOfTableNames: listOfTableColumnNames })) {
                //         return true
                //     }
                //     return join.action === 'delete' // return false if not deleted and invalid, returns true if it is deleted irrespective of join values
                // })
                // if (result.indexOf(false) !== -1) {
                //     invalidJoins = true
                // }
                return { emptyJoins, invalidJoins }
            }
        //Not storing joins in store that does not have index
        // const updatedJoins = joins.filter(join => !!join.index);
        dispatch && dispatch(metadataActions.updateJoinsData({ data: joins, override: true }))
    }
   
    if (returnDataForJest) {
        return joins
    }
    
}

const checkEmptyJoins = ({ joins }) => {
    let isInValid = false
    joins.every(join => {
        if (join.noAccess === 'true') {
            return true
        }
        if (join.action !== 'delete' && (!join?.left?.table || !join?.right?.table || !join?.left?.column || !join?.right?.column)) {
            isInValid = true
            return false
        }
        return true
    })
    return isInValid
}

export const validateTableColumn = ({ record, listOfTableNames }) => {
    record = cloneDeep(record)
    let table, column;
    if (record.tableAlias) (table = record.tableAlias)
    if (record.columnAlias) (column = record.columnAlias)
    if (!record.columnAlias) (column = record.column)
    if (record && table && column) {
        if (listOfTableNames.includes(`${table}.${column}`)) {
            return true
        }
        else {
            return (listOfTableNames.includes(`${table}`))
        }
    }
    else if (record && table && !column) {
        if (listOfTableNames.includes(`${table}.${column}`)) {
            return true
        }
        else {
            return (listOfTableNames.includes(`${table}`))
        }
    }
    else if (!table) {
        return false
    }
    return true
}
