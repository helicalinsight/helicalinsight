import { cloneDeep } from "lodash-es"

export const changeDatasourceUtil = ({ record, allDataSourceTypes, dataSourceState, activeDS, allDataSources= [] }) => {
    let dataSourceLocal = cloneDeep(dataSourceState)
    let currentType = record.driver ? allDataSourceTypes.filter(type => type.driver === record.driver)[0] : allDataSourceTypes.filter(type => type.driver === 'dynamicSwitch')[0]
    dataSourceLocal = dataSourceLocal.map(ds => {
        if (ds.connId === activeDS) {
            let dataSourceItem = allDataSources.find(ds => {
                return ds.data.id == record.data?.id && ds.data?.type === record.data.type;
            })
            ds = { ...ds, ...record.data, type: record.type, baseType: record.baseType, name: record.name, classifier: record.classifier, changed: true, datasourceName: record.name, driver: dataSourceItem || record.driver, driverType: currentType.name }
        }
        return ds
    })
    return dataSourceLocal
}