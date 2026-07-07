//this function is not used in the application.
export const prepareFormDataForViewQuery = ({ viewInfo }) => {
    let formData = {
        ...viewInfo.dataSource,
        query: viewInfo.query,
        queryType: viewInfo.queryType,
        hasStoredProcedure: false,
    }
    console.log('in prepared formdata for view query', { formData })
    return formData
}