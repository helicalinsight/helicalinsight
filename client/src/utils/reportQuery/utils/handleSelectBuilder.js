export const handleSelectBuilder = (payload, self, fromFnBuilder = false, alias = false) => {
    self.selectBuilder.inSelectBuilder = true
    self.selectBuilder.payloadInfo = []
    self.selectBuilder.paramsMapping = {}
    self.selectBuilder.fromFnBuilder = fromFnBuilder
    self.selectBuilder.fromFnBuilderAlias = alias
    self.selectBuilder.isColumnAdded = fromFnBuilder
    payload[0](self)
    if (!self.selectBuilder.isColumnAdded) {
        console.warn('Please specify atleast one column in select')
    }
    else {
        self.columns = self.columns.map((eachCol) => {
            if (eachCol.column === self.selectBuilder.addedColumnAlias && !self.selectBuilder.fromFnBuilder) {
                eachCol.column = self.selectBuilder.payloadInfo[0] || eachCol.column
                eachCol.alias = payload[1] || eachCol.alias
                eachCol.databaseFunction = { ...self.selectBuilder.finalDBF }
            }
            else if (self.selectBuilder.fromFnBuilder && eachCol.alias === self.selectBuilder.fromFnBuilderAlias){
                eachCol.databaseFunction = { ...self.selectBuilder.finalDBF }
            }
            else{
            }
            return eachCol
        })
    }
    if (!self.selectBuilder.isAliasSet && false) {
        console.warn(`Alias name not set for ${self.selectBuilder.superColumn}`)
    }
    self.selectBuilder.inSelectBuilder = false
    self.selectBuilder = {}
    return self
}