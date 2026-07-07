export const getDatasourceFD = (record = {}, refreshDataSource = false) => {
    let { id, type, dir } = record?.data || {}
    let formData = { id, type }
    if (dir) (formData.dir = dir)
    formData = {
        ...formData, parameters: {
            fetchCatalogs: true,
            fetchSchemas: true,
            view: 'tree',
            skipped: true
        }
    };
    if(refreshDataSource) formData.parameters.refresh = true;
    return formData
}