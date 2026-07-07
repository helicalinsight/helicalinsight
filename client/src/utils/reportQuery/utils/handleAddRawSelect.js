export const handleAddRawSelect = (payload, self) => {
    if (!(payload?.length == 2 && payload.every(e => typeof e == 'string' && e.length  ))) {
        console.warn('Invalid arguments for selectRaw')
        return self
    }
    let { columns } = self
    let isAliasAlreadyUsed = false
    columns.map(eachCol => {
        if(!isAliasAlreadyUsed && eachCol.alias == payload[1]){
            isAliasAlreadyUsed = true
        }
    })
    let newCOl = {
        column : payload[0],
        alias : payload[1],
        custom : true
    }
    if(isAliasAlreadyUsed){
        console.warn(`Alias name "${newCOl.alias}" already used`)
    }
    !isAliasAlreadyUsed && columns.push(newCOl)
    return self
}