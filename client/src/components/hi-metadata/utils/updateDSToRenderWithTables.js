import { uuid } from '../../../utils/uuid'
import { updateDatasourceListToRender } from '../utils'
import { cloneDeep } from 'lodash-es'
import { updateViewsConnId } from '../../../redux/actions'
import { getTableUniqueKey } from '../../../utils/reportQuery/utils/handleGetUniqueKey'

export const updateDSToRenderWithTables = (arg) => {
    let {dispatch, result, record, store = false, datasourceListToRender, catalog = false, schema = false, fetchedMetadata = false, catSchema = false, info = false } = arg
    let { keyPath } = record
    let tables = result.metadata.catalogs[0].schemas[0].tables
    let views = store ? store.getState().metadata.present.views : []
    
    let storeTables = store ? store.getState().metadata.present.tables : []
    let storeDataSource = store ? store.getState().metadata.present.dataSource : []
    let tablesIdsInMetadata = []
    let dataSource = result.metadata.dataSource
    if (Object.keys(storeTables).length) {
        tablesIdsInMetadata = Object.values(storeTables).map(table => (table.dataSource.id === dataSource.id && table.dataSource.type === dataSource.type) ? (table.cacheId ?? table.id) : false).filter(Boolean)
    }
    if (info?.schema && info?.catalog) {
        if (record.children?.length) {
            record.children.forEach((child) => {
                if (child.name === info.schema) {
                    keyPath = child.keyPath
                }
            })
        }
    }
    let duplicateTables = []
    let _duplicateTables = []
    if (fetchedMetadata?.tables) {
        Object.values(fetchedMetadata.tables).forEach((table, index) => {
            if (table.duplicate === 'true') {
                _duplicateTables.push(table)
            }
        })
    }

    let availableDs = storeDataSource.filter(ds => ds.id === dataSource.id && ds.type === dataSource.type)
    let dsName = record.datasourceName
    let categories = store?.getState()?.metadata?.present.categories

    if (!dsName && categories) {
        try {
            dsName = categories[record.keyPath.split('/')[0]].ds.name
        }
        catch (e) {
        }
    }

    let shortId =  availableDs[0]?.dbId || dataSource.dbId || uuid('short');

    if (availableDs.length === 1) {
        dataSource = availableDs[0]
    }
    else { // create mode if datasource not coming from backend
        let dataSourceListItem = datasourceListToRender.filter(ds => ds.uuid === keyPath.split('/')[0])[0];
        dataSource.connId = shortId
        dataSource.dbId = shortId // dbId for edit mode and connId for create mode
        dataSource.classifier = result.classifier
        dataSource.datasourceName = record.category === 'dataSource' ? record.name : (record.datasourceName || dsName)
        dataSource.dsKeyPath = record.keyPath
        dataSource.driverType = dataSourceListItem?.name;
        dataSource.database = '';
        if(catalog && schema)
            dataSource.database = catalog + '.' + schema;
        else if(catalog)
            dataSource.database = catalog;
        else if (schema)
            dataSource.database = schema;
        if(views?.length) {
            dispatch(updateViewsConnId({dataSource}));
        }
    }
    let newTablesAdded = []

    tables = cloneDeep([...tables, ..._duplicateTables]).map(table => {
        table.alias = table.alias || table.name
        table.dataSource = dataSource
        table.category = table.type === 'view' ? 'view' : 'table';   
        table.connId = shortId
        table.oldDbId = shortId;
        
        let _uuid = uuid()
        // table.uuid = _uuid
        // table.key = _uuid
        table.keyPath = `${(catSchema && (info?.schema && info?.catalog)) ? keyPath : record.keyPath}/${_uuid}`
        // table.data = record.data
        // table.dataSourceName = record.datasourceName
        // table.nameWithConnId = `${table.name}_${shortId}`
        // table.database = record.name
        table.uniqueKey = !["test"].includes(process.env.NODE_ENV) && getTableUniqueKey({mode: 'create', id: table.id, dbId: shortId})
        if (catalog) {
            table.catalog = catalog
        }
        if (schema) {
            table.schema = schema
        }
        if (table.duplicate === 'true') {
            table.duplicate = true
            duplicateTables.push(table)
            return false
        }
        table.selected = tablesIdsInMetadata.includes(table.id);
        //check this code
        newTablesAdded.push(table.uniqueKey)
        return table;
    }).filter(Boolean)?.sort((x, y) => {
        return x.alias.localeCompare(y.alias);
    })
    let tablesInSchema = []
    tablesInSchema = tables.map(eachTable => !eachTable.duplicate ? eachTable : false).filter(Boolean)
    datasourceListToRender = updateDatasourceListToRender({
        datasourceListToRender, keyPath, options: {
            type: 'addTablesToSchema',
            value: tablesInSchema
        }
    })
    return [datasourceListToRender, dataSource, newTablesAdded, tables, tablesInSchema, duplicateTables]
}