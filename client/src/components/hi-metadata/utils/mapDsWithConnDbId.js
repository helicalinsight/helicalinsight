import { v4 as uuid } from 'uuid'

export const mapDsWithConnDbId = ({ initialEditResponse, dataSource }) => {
    let editData = {}
    // if (!initialEditResponse) return {}
    try {
        [initialEditResponse, ...(initialEditResponse?.connections || [])].filter(Boolean).forEach((ds, index) => {
            let key = `${ds.dataSource.id}__${ds.dataSource.type}`
            editData[key] = {
                id: ds.dataSource.id,
                type: ds.dataSource.type,
                firstConnection: index === 0,
                ...{
                    ...ds.dataSource.connectionDatabaseId ? { connectionDatabaseId: ds?.dataSource?.connectionDatabaseId } : {}
                }
            }
        })
    } catch (error) {
        console.error('in error caught', error)
    }
    let result = {}
    dataSource = dataSource.map((ds, index) => {
        let key = `${ds.id}__${ds.type}`
        result[key] = {
            id: ds.id,
            type: ds.type,
            firstConnection: index === 0
        }
        // if (index !== 0) {
        result[key].connectionDatabaseId = editData[key]?.connectionDatabaseId ? editData[key].connectionDatabaseId : `${uuid()}`
        // result[key].connectionDatabaseId = editData[key]?.connectionDatabaseId ? editData[key].connectionDatabaseId : ``
        ds.connectionDatabaseId = result[key].connectionDatabaseId
        // }
        return ds
    })
    return { dsWithConnDbId: result, dataSource }
}