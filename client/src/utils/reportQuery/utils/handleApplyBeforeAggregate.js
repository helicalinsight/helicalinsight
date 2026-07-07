export const handleApplyBeforeAggregate = ({ data = [], self }) => {
    let { columns } = self
    let isValid = data.filter(eachValue => typeof eachValue !== 'string').length
    if (!isValid) {
        console.warn('Please provide valid alias names')
    }
    let aliases = columns.map((eachCol) => {
        return eachCol.alias
    })
    data.map(eachAlias => {
        if (!aliases.includes(eachAlias)) {
            console.warn(`Alias name '${eachAlias}' is not present please check`)
        }
    })
    columns = columns.map(column => {
        if (data.includes(column.alias)) {
            column.applyBeforeAggregate = true
        }
        return column
    })
    return self

}