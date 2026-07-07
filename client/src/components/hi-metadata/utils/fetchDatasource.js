import { cloneDeep } from 'lodash-es'
import requests from '../../../base/requests'
import { metadataActions, updateLoadingStatus } from '../../../redux/actions'
import { updateDSToRenderWithCatalogSchema } from '../utils'
import { getDatasourceFD } from './getDatasourceFD'

export const fetchDatasource = ({
    requestId,
    record,
    dispatch,
    store,
    afterFetching = () => { },
    config = { edit: false, info: {} },
    updateDSToRenderToRedux = true,
    noCatSchema = false,
    fetchedMetadata = false,
    handleError = () => { },
    getApi,
    refreshDataSource
}) => {
    dispatch(updateLoadingStatus({ type: record.key, status: true }))
    let parentRecord = cloneDeep(record)
    if (!record.data) {
        return
    }
    let formData = getDatasourceFD(record, refreshDataSource)
    // formData = record.data
    // formData = {
    //     ...formData, parameters: {
    //         fetchCatalogs: true,
    //         fetchSchemas: true,
    //         view: 'tree',
    //         skipped: true
    //     }
    // }
    // formData.requestId = requestId;
    const apiInstance = requests.metadata(dispatch).getMetadataWorkflow(formData, (res) => {
        let result = {
            fetchSchemas: true,
            fetchCatalogs: true,
            working: true,
            connData: record.data,
            connId: record?.data?.id,
            dsUUID: record.uuid,
            ...res
        }
        let recordNeeded = {}
        if (config.edit) {

        }
        let datasourceListToRender = updateDSToRenderWithCatalogSchema({
            result,
            record,
            datasourceListToRender: cloneDeep(store.getState().metadata.present.datasourceListToRender),
            recordNeeded,
            info: config?.info || {},
            dispatch,
            store,
            afterFetching,
            noCatSchema,
            fetchedMetadata,
            updateDSToRenderToRedux
        })
        !noCatSchema && recordNeeded.data && afterFetching({ record: recordNeeded.data, info: config.info, dsListToRender: datasourceListToRender })
        !recordNeeded.data && handleError({
            formData,
            response: res,
            parentRecord
        })
        dispatch(updateLoadingStatus({ type: record.key, status: false }))
        dispatch(metadataActions.updateWorkflowDataList({
            mode: 'add',
            data: result,
            datasourceListToRender: updateDSToRenderToRedux ? datasourceListToRender : false,
            __initiator: 'fetchDatasource.js'
        }))
        dispatch(metadataActions.setServiceErrorStatus({ key: record.key, status: false }))
    }, (err) => {
        dispatch(updateLoadingStatus({ type: record.key, status: false }))
        if (err?.message && err?.message?.includes('Cannot assign to read only property')) { //temp fix for 5296
            return
        }
        err?.message && dispatch(metadataActions.setServiceErrorStatus({ key: record.key, status: true, message: err.message }))
        // err?.message && notify(dispatch).error({
        //     type: "Network Call",
        //     message: err.message,
        // })
    })

    getApi && getApi(apiInstance);
}