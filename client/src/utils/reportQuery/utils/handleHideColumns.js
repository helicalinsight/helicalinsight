export const handleHideColumns = ({hide, hideAndIncludeInResultSet, data, self}) => {
    let { columns, hideAndIncludeInResultSetColumns, hiddenColumns } = self
    let aliases = columns.map((eachCol) => {
        return eachCol.alias
    })
    let hCols = new Set(hiddenColumns)
    let hINRSCols = new Set(hideAndIncludeInResultSetColumns)
    data.map((value) => {
        if (aliases.indexOf(value) !== -1) {
            if (hide) {
                hCols.add(value)
            }
            if (hideAndIncludeInResultSet) {
                hCols.add(value)
                hINRSCols.add(value)
            }
        }
        return null
    })
    self.hiddenColumns = [...hCols]
    self.hideAndIncludeInResultSetColumns = [...hINRSCols]
    return self
    
}