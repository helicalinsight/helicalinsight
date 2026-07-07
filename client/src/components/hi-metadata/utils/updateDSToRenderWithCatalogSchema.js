import { cloneDeep } from 'lodash-es'
import { updateDSToRenderWithTables } from '.'
import requests from '../../../base/requests'
import { metadataActions } from '../../../redux/actions'
import { uuid } from '../../../utils/uuid'


export const updateDSToRenderWithCatalogSchema = ({ result, record, datasourceListToRender, recordNeeded = {}, info, dispatch, store,
    afterFetching = () => { },
    noCatSchema = false,
    fetchedMetadata = false,
    updateDSToRenderToRedux = true,
}) => {
    let { catalogs } = result.metadata || {}
    if (catalogs?.length === 1 && catalogs[0].name === 'Null' && ('schemas' in catalogs[0])) {
        let { schemas } = catalogs[0]
        datasourceListToRender = datasourceListToRender.map(ds => {
            ds.children.forEach(child => {
                if (child.keyPath === record.keyPath) {
                    if (!child?.children?.length) {
                        child.children = []
                    }
                    child.fetched = true
                    child.children = schemas.map(_eachSchema => {
                        let _uuid = uuid()
                        let eachSchema = Object.assign({}, _eachSchema)
                        eachSchema.children = []
                        eachSchema.keyPath = `${record.keyPath}/${_uuid}`
                        eachSchema.key = _uuid
                        eachSchema.uuid = _uuid
                        eachSchema.data = record.data
                        eachSchema.category = 'schema'
                        eachSchema.datasourceName = record.name
                        if (eachSchema.name === info.schema) {
                            recordNeeded.data = eachSchema
                        }
                        // child.children.push(eachSchema)
                        return eachSchema
                    })
                }
                return child
            })
            return ds
        })
        if (schemas.length === 0) {
            let formData = {
                ...record.data,
                parameters: {
                    fetchTables: true,
                    fetchData: [{
                        catalog: 'Null',
                        schemas: []
                    }]
                }
            }
            if(formData.requestId) {
                delete formData.requestId;
            }
            requests.metadata(dispatch).getMetadataWorkflow(formData, (res) => {
                // let result = res
                let result = {
                    fetchSchemas: true,
                    fetchCatalogs: true,
                    working: true,
                    connData: record.data,
                    connId: record?.data?.id,
                    dsUUID: record.uuid,
                    ...res
                }
                let data = updateDSToRenderWithTables({
                    // let data = updateDSToRenderWithTables({
                    result,
                    record,
                    store,
                    datasourceListToRender: cloneDeep(store.getState().metadata.present.datasourceListToRender),
                    // catalog: record.name
                    fetchedMetadata,
                    dispatch
                })
                datasourceListToRender = data[0]
                // let tables = data[3]
                // let duplicateTables = data[5]
                let [, dataSource, newTablesAdded, tables, duplicateTables] = data
                //data = [datasourceListToRender, dataSource, newTablesAdded, tables, tablesInSchema, duplicateTables]
                if (noCatSchema) {
                    afterFetching({
                        dsListToRender: datasourceListToRender,
                        duplicateTables,
                        tables,
                        dataSource,
                        newTablesAdded
                    })
                }
                let dataToSend = {
                    mode: 'add',
                    data: result,
                    dataSource: data[1],
                    datasourceListToRender: updateDSToRenderToRedux ? datasourceListToRender : false
                }
                if (noCatSchema) {
                    dataToSend.newTablesAdded = newTablesAdded
                    dataToSend.noCatSchema = noCatSchema
                }
                else{
                    dispatch(metadataActions.updateTableKeys(newTablesAdded))
                }
                dispatch(metadataActions.updateWorkflowDataList(dataToSend))
                // dispatch(metadataActions.updateMetadataState({
                //     key: 'datasourceListToRender', value: datasourceListToRender
                // }))
                return datasourceListToRender
            })
        }

    }
    else if (catalogs?.length && catalogs[0].name !== 'Null' && catalogs[0]?.schemas?.length === 0) {
        datasourceListToRender = datasourceListToRender.map(ds => {
            ds.children.forEach(child => {
                if (child.keyPath === record.keyPath) {
                    if (!child?.children?.length) {
                        child.children = []
                    }
                    child.children = catalogs.map(_eachCatalog => {
                        let _uuid = uuid()
                        let eachCatalog = Object.assign({}, _eachCatalog)
                        eachCatalog.children = []
                        eachCatalog.keyPath = `${record.keyPath}/${_uuid}`
                        eachCatalog.key = _uuid
                        eachCatalog.uuid = _uuid
                        eachCatalog.data = record.data
                        eachCatalog.category = 'catalog'
                        eachCatalog.datasourceName = record.name
                        // child.children.push(eachCatalog)
                        if (eachCatalog.name === info.catalog) {
                            recordNeeded.data = eachCatalog
                        }
                        return eachCatalog
                    })
                }
                return child
            })
            return ds
        })
    }
    else if (catalogs?.length && catalogs[0].name !== 'Null' && catalogs[0]?.schemas?.length !== 0) {
        /**
         * both catalog and schemas are present (POSTGRES)
         */
        datasourceListToRender = datasourceListToRender.map(ds => {
            ds.children.forEach(child => {
                if (child.keyPath === record.keyPath) {
                    if (!child?.children?.length) {
                        child.children = []
                    }
                    child.children = catalogs.map(_eachCatalog => {
                        let _uuid = uuid()
                        let eachCatalog = Object.assign({}, _eachCatalog)
                        eachCatalog.children = eachCatalog.schemas.map(eachSchema => {
                            let schemaUUID = uuid()
                            eachSchema.data = record.data
                            eachSchema.keyPath = `${record.keyPath}/${_uuid}/${schemaUUID}`
                            eachSchema.key = schemaUUID
                            eachSchema.uuid = schemaUUID
                            eachSchema.category = 'schema'
                            eachSchema.children = []
                            eachSchema.catalog = _eachCatalog.name
                            eachSchema.datasourceName = record.name
                            return eachSchema
                        })
                        eachCatalog.keyPath = `${record.keyPath}/${_uuid}`
                        eachCatalog.key = _uuid
                        eachCatalog.uuid = _uuid
                        eachCatalog.data = record.data
                        eachCatalog.category = 'catalog'
                        eachCatalog.fetched = true
                        eachCatalog.datasourceName = record.name
                        if (eachCatalog.name === info.catalog) {
                            recordNeeded.data = eachCatalog
                        }
                        // child.children.push(eachCatalog)
                        return eachCatalog
                    })
                }
                return child
            })
            return ds
        })
    }
    return datasourceListToRender
}