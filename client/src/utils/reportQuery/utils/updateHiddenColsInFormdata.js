export const updateHiddenColsInFormdata = ({ self, formData }) => {
    let { hiddenColumns, hideAndIncludeInResultSetColumns } = self
    let { columns, functions } = formData
    let { groupBy } = functions || {}
    columns = columns.map(eachCol => {
        if (hiddenColumns.indexOf(eachCol.alias) !== -1) {
            eachCol.hidden = true
            if (groupBy && !(hideAndIncludeInResultSetColumns.indexOf(eachCol.alias) !== -1)) (groupBy = groupBy.filter(elem => elem.column !== eachCol.alias))
        }
        if (hideAndIncludeInResultSetColumns.indexOf(eachCol.alias) !== -1) {
            // eachCol.hidden = true
            eachCol.includeInResultset = true
        }
        return eachCol
    })
    formData.columns = columns
    if (functions && Array.isArray(groupBy)) {
        formData.functions.groupBy = groupBy
        if(!groupBy.length) {
            delete formData.functions.groupBy
        }
    }
    return formData
}