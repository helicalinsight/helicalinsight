export const FDForSaveView = ({ views = false, activeView = false, dataSourcesAddedToMetadata = false, editViewsTempData = false }) => {
    if (!views || !activeView || !dataSourcesAddedToMetadata)
        return false
    if (dataSourcesAddedToMetadata?.length === 1) {
        let ds = dataSourcesAddedToMetadata[0]
        let formData = {}
        formData.catSchemaPredicted = ds.catSchemaPredicted
        formData.baseType = ds.baseType
        formData.catalog = ds.catalog
        formData.schema = ds.schema
        formData.type = ds.type
        formData.id = ds.id
        formData.sync = ds.sync
        formData.classifier = ds.classifier
        if(ds.dir){
            formData.dir = ds.dir
        }
        let currentView = views.filter(eachView => eachView.uuid === activeView)
        if (editViewsTempData && (currentView.uuid in editViewsTempData)) {
            currentView = editViewsTempData[currentView.uuid]
        }
        if (currentView?.length) {
            currentView = currentView[0]
            formData.queryType = currentView.queryType
            formData.query = currentView.query
            formData.viewName = currentView.alias || currentView.name
            formData.labels = currentView.labels.filter(column => column.checked)
            if (currentView.id) {
                formData.viewId = currentView.id
            }
            if (currentView.queryType === 'groovy') {
                formData.hasStoredProcedure = false
                formData.validate = currentView.validate
                formData.processedQuery = currentView.processedQuery
            }
            return formData
        }
    }
    return false
}