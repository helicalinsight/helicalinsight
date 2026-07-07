export const updateJoinsWithCrossJoins = ({ fetchedMetadata }) => {
    let { crossJoins = [] } = fetchedMetadata
    if (crossJoins.length === 0) return []
    let dsMap = {};
    [fetchedMetadata, ...(fetchedMetadata?.connections || [])].forEach(conn => {
        if (conn?.dataSource?.connectionDatabaseId) {
            dsMap[conn.dataSource.connectionDatabaseId] = conn.dataSource
        }
    })
    crossJoins = crossJoins.map(join => {
        if (join.databaseId in dsMap) {
            join.left.dataSource = dsMap[join.databaseId]
        }
        if (join.referenceId in dsMap) {
            join.right.dataSource = dsMap[join.referenceId]
        }
        if(!('action' in join)){
            join.action = 'noChange'
        }
        return join
    })
    console.log('in update joins with cross joins', crossJoins, fetchedMetadata)
    return crossJoins
}