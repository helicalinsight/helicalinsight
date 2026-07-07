import { cloneDeep } from 'lodash-es'
import requests from '../../../base/requests'
import { metadataActions, updateLoadingStatus } from '../../../redux/actions'
import { updateDSToRenderWithTables } from '../utils'

export const fetchSchema = ({ requestId, record, dispatch, store, fetchedMetadata = false, info = false, afterFetching = () => { }, updateDSToRenderToRedux = true, dsListToRender = false, catSchema = false, getApi }) => {
    dispatch(updateLoadingStatus({ type: record.key, status: true }))
    let formData = {
        ...record.data,
        parameters: {
            fetchTables: true,
            fetchData: [{
                schemas: [{
                    name: record.name
                }]
            }]
        }
    }
    if (record.catalog) {
        formData.parameters.fetchData[0].catalog = record.catalog
    }
    if (info?.schema && info?.catalog) { // this usecase is for POSTGRES
        formData.parameters.fetchData[0].catalog = info.catalog
        formData.parameters.fetchData[0].schemas = [{
            name: info.schema
        }]
    }
    // formData.requestId = requestId;
    const apiInstance = requests.metadata(dispatch).getMetadataWorkflow(formData, (res) => {
        let result = res
        let [datasourceListToRender, dataSource, newTablesAdded, tables, tablesInSchema, duplicateTables] = updateDSToRenderWithTables({
            result,
            record,
            datasourceListToRender: dsListToRender || cloneDeep(store.getState().metadata.present.datasourceListToRender),
            schema: info.schema || record.name,
            catalog: info.catalog || record.catalog,
            fetchedMetadata,
            catSchema,
            info,
            store,
            dispatch
        })
        afterFetching({ tables, datasourceListToRender, tablesInSchema, duplicateTables, updatedDS: dataSource })
        dispatch(updateLoadingStatus({ type: record.key, status: false }))
        dispatch(metadataActions.updateWorkflowDataList({
            mode: 'add',
            data: result,
            datasourceListToRender: updateDSToRenderToRedux ? datasourceListToRender : false,
            dataSource,
            newTablesAdded
        }))
        dispatch(metadataActions.setServiceErrorStatus({ key: record.key, status: false }))
    }, (err) => {
        console.log('in fetch schema error caught', err)
        dispatch(updateLoadingStatus({ type: record.key, status: false }))
        err?.message && dispatch(metadataActions.setServiceErrorStatus({ key: record.key, status: true, message: err.message }))
        // err?.message && notify(dispatch).error({
        //     type: "Network Call",
        //     ...err,
        // })
    })
    getApi && getApi(apiInstance);
}