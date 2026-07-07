// import { notify } from "jquery";
import { createJoinAction, deleteInvalidJoinsAction, deleteSelectedJoinsAction, discardJoinAction, updateJoinAction } from "../../../../redux/actions";
import { uuid } from "../../../../utils/uuid"
import notify from "../../../hi-notifications/notify";
import {checkInvalidJoins} from "./utils";

export const utilsJoinsActions = ({dispatch, joins}) => {
    const handleJoinCreate = () => {
        let unique = uuid();
        let join = {
            "type": "inner",
            "operator": "=",
            "left": {
                "table": false,
                "column": false,
            },
            "right": {
                "table": false,
                "column": false,
            },
            id: unique,
            key: unique,
            uuid: unique,
            action: 'add',
        };
        dispatch && dispatch(createJoinAction(join));
        return join;
    }
    
    const handleJoinUpdate = ({record, type, dataSource, value, currentOption}) => {
        dispatch && dispatch(updateJoinAction({record, type, dataSource, value, currentOption}));
    }
    
    const handleJoinDelete = () => {
        
    }

    const handleDeleteSelectedJoins = ({selectedJoins}) => {
        if (!selectedJoins.length) {
            notify(dispatch).warn('Validation', 'Please select join(s) to delete')
        }
        dispatch && dispatch(deleteSelectedJoinsAction(selectedJoins))
    }

    const handleDeleteInvalidJoins = ({joins, listOfTableColumnNames, tables}) => {
        const invalidJoins = checkInvalidJoins(joins, tables)

        if (!invalidJoins.length) {
            notify(dispatch).warn('Validation', 'No invalid joins present')
        } else {
            dispatch && dispatch(deleteInvalidJoinsAction(invalidJoins.map(join => join.id)))
        }
    }

    const handleDiscardJoin = ({record}) => {
             
        dispatch && dispatch(discardJoinAction({record}))
    }

    return {
    handleDiscardJoin,
    handleDeleteInvalidJoins,
    handleDeleteSelectedJoins,
    handleJoinCreate,
    handleJoinUpdate,
    handleJoinDelete
    }
}

