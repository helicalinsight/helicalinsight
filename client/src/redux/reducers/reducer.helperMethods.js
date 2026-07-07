import { updateJoinsAfterAliasingTableColumn, validateTableColumn } from "../../components/hi-metadata/utils"

export const handleDiscardJoinLogic = ({state, draft, record}) => {
    let joinIndex = 1
    let idsToDelete = []
    if (record.uuid) {
        idsToDelete.push(record.uuid)
    }
    else if (record.length) {
        idsToDelete = record.map(join => join.uuid)
    }
    draft.joins = draft.joins.filter((join) => {
        if (idsToDelete.indexOf(join.uuid) !== -1) {
            if(join.action === 'add') {
                return false;
            } else {
                join.action = 'delete'
                delete join.index
            }
        }
        else if (join.action !== 'delete') {
            join.index = joinIndex
            joinIndex++
        }
        return join
    })     
    return  draft.joins
}

export const handleDeleteInvalidJoinsLogic = ({state, draft, listOfTableColumnNames, tables}) => {
    draft.joins = updateJoinsAfterAliasingTableColumn({ joins: draft.joins, tables }).map((join, index) => {
        if (validateTableColumn({ record: join.left, listOfTableNames: listOfTableColumnNames }) && validateTableColumn({ record: join.right, listOfTableNames: listOfTableColumnNames })) {
            return join
        }
        join.action = 'delete';
        delete join.index;
        return join
    });
    draft.joins = draft.joins.filter(join => !!join.index);
    draft.joins = draft.joins.map((join, index) => {
        join.index = index + 1;
        return join;
    });       
    return  draft.joins
}

export const handleDeleteSelectedJoinsLogic = ({state, draft, selectedJoins}) => {
    let joinIndex = 1;
    draft.joins = draft.joins.filter((join) => {
        if (selectedJoins.indexOf(join.uuid) !== -1) {
            if(join.action === 'add') {
                return false;
            } else {
                join.action = 'delete'
                delete join.index
            }
        }
        else if (join.action !== 'delete') {
            join.index = joinIndex
            joinIndex++
        }
        return join
    })  
    return  draft.joins
}

export const handleJoinCreateLogic = ({state, draft, action}) => {
    // adding index to every join
    draft.joins = [action.payload, ...draft.joins];

    let joinIndex = 1;
    draft.joins = draft.joins.map((join) => {
            if (join.action !== 'delete') {
                join.index = joinIndex;
                joinIndex++
            }
        return join
    })
    return  draft.joins
}