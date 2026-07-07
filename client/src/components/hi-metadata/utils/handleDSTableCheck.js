import { cloneDeep } from "lodash-es";
import { updateDatasourceListToRender } from "./updateDatasourceListToRender";
import { metadataActions } from "../../../redux/actions";
import { isObject } from '../../../utils/is-object'
import { findNestedObj } from '../../../utils/find-nested-obj'

export const handleDSTableCheck = ({ checked, record, dispatch, dsListToRender, returnData = false, task }) => {
    if (!Array.isArray(dsListToRender)) return false;
    if (!isObject(record)) return dsListToRender
    if (record.type === 'view') {
        /**
         * if the deleted category is a view then delete view also
         */
        dispatch(metadataActions.deleteView({ id: record.id }))
        return
    }
    if (typeof checked !== 'boolean') return dsListToRender
    let keyPath = record.keyPath
    if (keyPath.split('/')?.length <= 1) {
        keyPath = findNestedObj(dsListToRender, 'id', record.id)?.keyPath
        console.log('in found keypath', keyPath)
    }
    let result = updateDatasourceListToRender({
        datasourceListToRender: cloneDeep(dsListToRender),
        keyPath: keyPath, options: {
            type: 'selectTables',
            value: checked
        },
        record,
        task
    })
    if (returnData) {
        return result
    }
    dispatch(metadataActions.updateMetadataState({ key: 'datasourceListToRender', value: result }))
};
