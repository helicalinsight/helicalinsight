const checkIfObject = (obj) => typeof obj == 'object' && !Array.isArray(obj) && obj !== null
export const handleRawFnBuilder = ({ payload, self, alias }) => {
    /**validate payload -- start */
    let requiredKeys = ['functionName', 'dataType', 'parameters']
    let isValid = true
    isValid = checkIfObject(payload)
    isValid && requiredKeys.forEach(key => {
        if (isValid){
            isValid = Object.keys(payload).indexOf(key) !== -1
        }
    })
    if(isValid){
        isValid = typeof payload.functionName == 'string' && typeof payload.dataType == 'string' && checkIfObject(payload.parameters)
    }
    /**validate payload -- end */
    if(!isValid){
        console.warn('Invalid arguments for Raw function')
        return self
    }
    self.columns = self.columns.map((eachCol) => {
        if(eachCol.alias === alias){
            eachCol.databaseFunction = {...payload, raw : true}
        }
        return eachCol
    })
    return self
}