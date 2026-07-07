import { cloneDeep } from 'lodash-es'

function tableLevelConfig({tableLevelItem, value, table}) {
    if(tableLevelItem.name === table.name) {
        delete tableLevelItem.selected
        tableLevelItem.selected = value
        return 1;
    }
    return 0;
}

function schemaLevelConfig({schemaLevelItem, value, table, dataSource}) {
    if(schemaLevelItem.name === dataSource.schema) {
        return schemaLevelItem.children?.find(tableLevel => {
            return tableLevelConfig({tableLevelItem: tableLevel, value, table});
        })
    }
    return 0;
}

function catalogLevelConfig({catalogLevelItem, value, table, dataSource}) {
    if(catalogLevelItem.name === dataSource.catalog) {
        return catalogLevelItem.children?.find(schemaOrTableLevel => {
            if(schemaOrTableLevel.category === 'table') {
                return tableLevelConfig({tableLevelItem: schemaOrTableLevel, value, table});
            } else if(schemaOrTableLevel.category === 'schema') {
                return schemaLevelConfig({schemaLevelItem: schemaOrTableLevel, value, table, dataSource})
            }
            return 0;
        })
    }
    return 0;
}

export const updateDatasourceListToRender = ({ datasourceListToRender, keyPath, options, record : table, task }) => {
    let { type, value, returnResult = false, returnChildren = false  } = options
    let __data = datasourceListToRender
    let result = null
    let childrenResult = null
    if(task === 'remove') {
        if(typeof value !== 'boolean' || type !== 'selectTables') {
            throw  new Error('Err in updateDatasourceListToRender func:- type / value are not proper')
        }
        if(!table || !table?.dataSource) {
            throw  new Error('Err in updateDatasourceListToRender func:- table / table.datasource value is missing')
        }
        let dataSource = table?.dataSource;
        ['driverType', 'type', 'id', 'datasourceName', 'schema', 'catalog'].forEach(datasourceitem => {
            if(!(datasourceitem in dataSource)) {
                throw  new Error(`Err in updateDatasourceListToRender func:- ${datasourceitem} is missing in dataSource. Check for other values too.`)
            }
        })
        __data?.find(dsGroupItem => {
            if(dsGroupItem.name === dataSource.driverType || dsGroupItem.name === dataSource?.vendorName) {
                return dsGroupItem.children?.find(dataSourceItem => {
                    if((dataSourceItem.data?.id+'') === (dataSource.id+'') && dataSourceItem.data?.type === dataSource.type && dataSourceItem.name === dataSource.datasourceName) {
                        return dataSourceItem.children?.find(catalogOrschemaOrTableLevel => {
                            if(catalogOrschemaOrTableLevel.category === 'catalog') {
                                return catalogLevelConfig({catalogLevelItem: catalogOrschemaOrTableLevel, value, table, dataSource})
                            } else if(catalogOrschemaOrTableLevel.category === 'schema') {
                                return schemaLevelConfig({schemaLevelItem: catalogOrschemaOrTableLevel, value, table, dataSource})
                            } else if(catalogOrschemaOrTableLevel.category === 'table') {
                                return tableLevelConfig({tableLevelItem: catalogOrschemaOrTableLevel, value, table});
                            }
                            return 0;
                        })
                    }
                    return 0;
                })
            }
            return 0;
        })
    } else {
        keyPath?.split('/')?.map((key, index) => {
            __data.map(ds => {
                if (ds.keyPath === keyPath.split('/').slice(0, index + 1).join('/')) {
                    let dsChildren = ds.children
                    __data = dsChildren
                    if (ds.keyPath === keyPath) {
                        if (returnChildren) {
                            childrenResult = cloneDeep(ds.children)
                        }
                        if (type === 'addTablesToSchema') {
                            ds.fetched = true
                            ds.children = value
                        }
                        else if (type === 'selectTables' && typeof value === 'boolean') {
                            // ds = {...ds, selected : value}
                            delete ds.selected
                            ds.selected = value
                        }
                        else if (type === 'selectAllTables' && typeof value === 'boolean') {
                            // ds.children.forEach(element => {
                            //     element.selected = value
                            // });
                            // ds.children = []
                            dsChildren = [...dsChildren].map(val => {
                                let child = cloneDeep(val)
                                delete child.selected
                                child.selected = value
                                return child
                            })
                        }
                        //get all selected tables from schema
                        else if (type === 'dragTablesToMetadata') {
                            result = { selected: [], notSelected: [] }
                            ds.children.forEach(child => {
                                result[child.selected ? 'selected' : 'notSelected'].push(child)
                            })
                            return result
                        }
    
                    }
    
                }
            })
        })
    }
    if (returnResult) {
        return cloneDeep(result)
    }
    if (returnChildren) {
        return childrenResult
    }
    return datasourceListToRender
}


 // if (type === 'addTablesToSchema') {
                                    //     catalogOrschemaOrTableLevel.fetched = true
                                    //     catalogOrschemaOrTableLevel.children = value
                                    //     return 1;
                                    // } else if (type === 'selectAllTables' && typeof value === 'boolean') {
                                    //     catalogOrschemaOrTableLevel.children = [...catalogOrschemaOrTableLevel.children].map(val => {
                                    //         let child = cloneDeep(val)
                                    //         delete child.selected
                                    //         child.selected = value
                                    //         return child
                                    //     })
                                    //     return 1;
                                    // } else if (type === 'dragTablesToMetadata') {
                                    //     result = { selected: [], notSelected: [] }
                                    //     catalogOrschemaOrTableLevel.children.forEach(child => {
                                    //         result[child.selected ? 'selected' : 'notSelected'].push(child)
                                    //     })
                                    //     return 1