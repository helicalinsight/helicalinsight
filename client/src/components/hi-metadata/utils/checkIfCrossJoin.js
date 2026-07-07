export const checkIfCrossJoin = ({ join, tables, initialEditResponse, dsWithConnDbId = {} }) => {
    if(join?.left?.table && join.right.table){
        let leftConnDBId = findConnDBId({ tableName: join.left.table, tables, initialEditResponse, dsWithConnDbId })
        let rightConnDBId = findConnDBId({ tableName: join.right.table, tables, initialEditResponse, dsWithConnDbId })
        if (leftConnDBId || rightConnDBId) {
            return {
                leftConnDBId, rightConnDBId
            }
        }
    }
    return false
}
const findConnDBId = ({ tableName, tables, initialEditResponse, dsWithConnDbId }) => {
    let tableConn = tables[tableName].dataSource
    // let result = null
    // if (!initialEditResponse.connections) return false
    return dsWithConnDbId[`${tableConn.id}__${tableConn.type}`]
    // result = initialEditResponse.connections.filter(conn => conn.dataSource.id === tableConn.id && conn.dataSource.type === tableConn.type)
    // return result.length && result[0].connectionDatabaseId
}