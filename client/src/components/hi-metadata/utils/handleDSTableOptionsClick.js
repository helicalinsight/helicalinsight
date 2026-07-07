import { metadataActions } from "../../../redux/actions";
import { handleTableDropToMetadata, updateDatasourceListToRender } from '.'
import { cloneDeep } from 'lodash-es'
import { isObject } from '../../../utils/is-object'

export const handleDSTableOptionsClick = ({ record, option, dsListToRender, dispatch, store, type, returnData = false }) => {
    if (!Array.isArray(dsListToRender)) return false;
    if (!isObject(record)) return dsListToRender
    if (option === 'selectAll' || option === 'reset') {
        let keyPath = record.keyPath
        let keyPathArray = keyPath.split('/')
        keyPathArray.pop()
        keyPath = keyPathArray.join('/')
        let allTables = updateDatasourceListToRender({
            datasourceListToRender: dsListToRender, keyPath: keyPath, options: {
                returnChildren: true
            }
        })
        allTables.map(eachTable => {
            dsListToRender = updateDatasourceListToRender({
                datasourceListToRender: cloneDeep(dsListToRender), keyPath: eachTable.keyPath, options: {
                    type: 'selectTables',
                    value: option === 'selectAll'
                }
            })
        })
        if (returnData) {
            return dsListToRender
        }
        dispatch(metadataActions.updateMetadataState({ key: 'datasourceListToRender', value: dsListToRender }))
        dispatch(metadataActions.updateMetadataState({ mode: 'selectedTables', key: allTables.map(table => table.uuid), checked: option === 'selectAll' }))
    }
    else if (option === 'addToMetadata') {
        let result = handleTableDropToMetadata({ dispatch, record, datasourceListToRender: dsListToRender, store, type, returnData })
        if (returnData) {
            return result
        }
    }
}
