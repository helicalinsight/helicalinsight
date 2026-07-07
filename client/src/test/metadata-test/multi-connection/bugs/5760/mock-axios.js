import qs from "qs";
// import { getReportState, getMetadata, getDataBaseFunctions, getDateFunctions, getReportData } from "../../../app/mock-data";
import { mockData } from "./mock-data";


export const mockAxios = () => {
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
                            data: JSON.stringify(mockData.getStaticContents)
                        })
                    }

                }
                if (type === 'adhoc' && serviceType === 'metadata' && service === 'getMetadataForEdit') {
                    if (location === 'Gagan' && metadataFileName === 'Metadata_1.metadata') {
                        return resolve({
                            data: JSON.stringify(mockData.getMetadata)
                        })
                    }
                }
                // console.log('in mock axios', service, type === 'adhoc' && serviceType === 'metadata' && service === 'metadataWorkflow', formData.parameters)
                let { fetchCatalogs, fetchSchemas, fetchTables, fetchData } = formData.parameters
                if (id === '1000' && fetchCatalogs && fetchSchemas) {
                    return resolve({
                        data: JSON.stringify(mockData.workflow_1000_cat_schema)
                    })
                }
                if (id === '1000' && !fetchCatalogs && !fetchSchemas && fetchTables) {
                    return resolve({
                        data: JSON.stringify(mockData.workflow_1000_tables)
                    })
                }
                if (id === '1' && fetchCatalogs && fetchSchemas) {
                    return resolve({
                        data: JSON.stringify(mockData.workflow_1_cat_schema)
                    })
                }
                if (id === '1' && !fetchCatalogs && !fetchSchemas && fetchTables) {
                    return resolve({
                        data: JSON.stringify(mockData.workflow_1_tables)
                    })
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
                        data: JSON.stringify(mockData.listDS)
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