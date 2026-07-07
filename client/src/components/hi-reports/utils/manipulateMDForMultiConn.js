import { shortId } from "../../../utils/uuid"

export const manipulateMDForMultiConn = ({ metadata = {} }) => {
    let result = metadata
    if (!metadata.connections) return metadata
    metadata.connections.map(eachConn => {
        Object.keys(eachConn?.tables || []).map(key => {
            if (key in (result.tables || {})) {
                // result.tables[`${key}_${shortId()}`] = eachConn.tables[key]
                result.tables[`${key}_${shortId()}`] = { ...eachConn.tables[key], dataSource: eachConn?.dataSource || {}, dataSourceName: eachConn?.name, isMultiConnection: true }
            }
            else {
                result.tables[key] = { ...eachConn.tables[key], dataSource: eachConn?.dataSource || {}, dataSourceName: eachConn?.name, isMultiConnection: true }
            }
        })
    })
    return result

}
