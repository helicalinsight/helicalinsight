import qs from "qs";
// import { getReportState, getMetadata, getDataBaseFunctions, getDateFunctions, getReportData } from "../../../app/mock-data";
import { metadataMockData } from "../multi-connection/derby_pg_mock-data";


export const derby_pg_mockAxios = () => {
    // return null
    let data = {
        post: function (url, data, config) {
            let payload = qs.parse(data)
            let { type, serviceType, service, formData } = payload
            try {
                formData = window.atob(formData)
            }
            catch (e) {

            }
            formData = JSON.parse(formData)
            return new Promise((resolve, reject) => {
                let { contentId, location, metadataFileName, id } = formData
                if (serviceType === 'static' && service === 'getContents') {
                    if (formData.contentId === 'Static/DataSourcesList') {
                        return resolve({
                            data: JSON.stringify(metadataMockData.getStaticDSList)
                        })
                    }

                }
                if (type === 'adhoc' && serviceType === 'metadata' && service === 'get') {
                    if (location === 'Gagan' && metadataFileName === 'Metadata_2_pg_derby.metadata') {
                        return resolve({
                            data: JSON.stringify(metadataMockData.getMetadata_2_pg_derby)
                        })
                    }
                }
                if (type === 'adhoc' && serviceType === 'metadata' && service === 'metadataWorkflow') {
                    let { fetchCatalogs, fetchSchemas, fetchTables, fetchData } = formData.parameters
                    if (id === '1001' && formData.type === 'dynamicDataSource' && fetchCatalogs && fetchSchemas) {
                        return resolve({
                            data: JSON.stringify(metadataMockData.workflow_1001_fetchSchema_fetchCatalog)
                        })
                    }
                    if (id === '1' && formData.type === 'dynamicDataSource' && fetchCatalogs && fetchSchemas) {
                        return resolve({
                            data: JSON.stringify(metadataMockData.workflow_1_fetchSchema_fetchCatalog)
                        })
                    }
                    if (id === '1001' && formData.type === 'dynamicDataSource' && !fetchCatalogs && !fetchSchemas && fetchTables && fetchData[0].catalog === 'booking1662345944710kenxhxumioueuzfj' && fetchData[0].schemas[0].name === 'public') {
                        return resolve({
                            data: JSON.stringify(metadataMockData.workflow_1001_public_booking)
                        })
                    }
                    if (id === '1' && formData.type === 'dynamicDataSource' && !fetchCatalogs && !fetchSchemas && fetchTables && fetchData[0].schemas[0].name === 'HIUSER') {
                        return resolve({
                            data: JSON.stringify(metadataMockData.workflow_1_HIUSER)
                        })
                    }
                }
                console.log("POST REQ", type, serviceType, service, formData)

                return resolve({})
            })
        },
        get: (url, data, config) => {
            let { params } = data
            return new Promise((resolve, reject) => {
                if (url === 'listDataSources' && params.classifier === 'all') {
                    return resolve({
                        data: JSON.stringify(metadataMockData.getGetListDS)
                    })
                }
                console.log('GET REQ', url, data)
                resolve({
                    data: {}
                })
            })
        }
    }
    return { instance: data }
}