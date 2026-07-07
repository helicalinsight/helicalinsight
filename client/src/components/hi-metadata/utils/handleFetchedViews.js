import { uuid } from '../../../utils/uuid'
import { metadataActions } from "../../../redux/actions";

export const handleFetchedViews = ({
    dispatch = false,
    views = [],
    returnData = false,
    fetchedDS = false,
    store
}) => {
    if (!Array.isArray(views)) return false
    // const allDataSources = store?.getState()?.metadata?.allDataSources;
    views = views.map(eachView => {
        let uniqueId = uuid()
        // let shortId =  uuid('short') // revert
        // eachView.connId = shortId
        eachView.uuid = uniqueId
        // eachView.key = uniqueId
        eachView.columnsFetched = true
        eachView.catalog = fetchedDS.catalog
        eachView.schema = fetchedDS.schema
        eachView.category = 'view'
        // eachView.nameWithConnId = `${eachView.name}_${shortId}`
        // eachView.data = {id: fetchedDS.id, type: fetchedDS.type}
        let colKeys = Object.keys(eachView.columns)
        colKeys.map(eachColKey => {
            eachView.columns[eachColKey] = { ...eachView.columns[eachColKey], tableKey: eachView.name, columnKey: eachColKey, category: 'column', parentCategory: 'view' };
        })
        if (fetchedDS) {
            eachView.dataSource = fetchedDS
            // eachView.dataSource.connId = shortId
          
            // const datasource = allDataSources?.find(ele => ele.data.id === fetchedDS.id);
            // eachView.dataSourceName = datasource?.name;
        }
        return eachView
    })
    if (returnData) {
        return views
    }
    dispatch && dispatch(metadataActions.updateMetadataState({ key: 'views', value: views }))
}
