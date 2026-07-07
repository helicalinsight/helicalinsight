import { fromJS } from "immutable"

let currentDbFn = {}
const randomString = () => `___${(Math.random() + 1).toString(36).substring(7)}`
let argumentsInfo = {}
const applyFunctionToCol = (databaseFunction, dbf, self, fnParams = [], index = 0) => {// databaseFunction -> existing db function. dbf -> new db function to be added
    if (self.selectBuilder.inSelectBuilder) { // added this for select builder for db funcitons
        let swapTemp = dbf
        dbf = databaseFunction
        databaseFunction = swapTemp
    }
    dbf = fromJS(dbf).toJS()
    databaseFunction = fromJS(databaseFunction).toJS()
    if (fnParams?.length == 1) {
        if (databaseFunction?.parameters?.length && typeof databaseFunction?.parameters[0]?.value == 'object') {
            databaseFunction = applyFunctionToCol(databaseFunction.parameters[0].value, dbf, self, fnParams, index)
        }
        else {
            databaseFunction.parameters[0].value = dbf
        }
    }
    else {
        fnParams.map((eachParam, index) => {
            //check if a paramnter is a string, not a column
            if (/['||"]/.test(eachParam)) {
                databaseFunction.parameters[index].value = currentDbFn[eachParam]
            }
            else {
                databaseFunction.parameters[index].value = currentDbFn[eachParam]
            }
        })
    }
    return databaseFunction
}

export const handleApplyDBFunction = (payload, self, dbf) => { //dbf -> database function
    payload = [...payload]
    if(typeof payload.flat === 'function'){
        payload = payload?.flat()
    }
    else{
        payload = payload.reduce((acc, val) => acc.concat(val), [])
    }
    let { columns } = self
    // currentDbFn = []
    let fnParams = []
    if (!dbf) return self

    if (payload.length != dbf.parameters.length) {
        console.warn(`Invalid parameters. Required ${dbf.parameters.length} paramenters but recieved ${payload.length}`)
        return self
    }
    else {
        if (self.selectBuilder.inSelectBuilder) {
            payload.map(arg => {
                if (typeof arg == 'string' && !/['||"]/.test(arg)) { // cehck for a string without quotes
                    self.selectBuilder.payloadInfo.push(arg)
                    if (!self.selectBuilder.isColumnAdded) {
                        let alias = randomString()
                        self.select(alias, alias) // creating a temporary select
                        self.selectBuilder.addedColumnAlias = alias
                        self.selectBuilder.isColumnAdded = true
                    }
                }
            })

            //apply dbfunction
            // let modifiedDBF = fromJS(dbf).toJS()
            // modifiedDBF.parameters = modifiedDBF.parameters.map((e, i) => {
            //     if (typeof payload[i] != 'undefined' && payload[i] != null) {
            //         if ((payload[i] || '').includes('___')) {
            //             e.value = argumentsInfo[payload[i]]
            //         }
            //         else {
            //             e.value = payload[i]
            //         }
            //     }
            //     else {
            //         console.warn('Argument missing')
            //     }
            //     return e
            // })
        }
        let modifiedDBF = modifyDBF({ dbf, payload, self })
        let random = randomString()
        argumentsInfo[random] = { ...modifiedDBF }
        self.selectBuilder.finalDBF = { ...modifiedDBF }
        if (!self.selectBuilder.paramsMapping) {
            self.selectBuilder.paramsMapping = {}
        }
        self.selectBuilder.paramsMapping[random] = payload
        fnParams.push(random)
        // else if (self?.aggBuilder?.inBuilder){
        //     let modifiedDBF = modifyDBF({ dbf, payload })
        // }

        if (self.columns && false) {
            columns = [...self.columns].map(eachCol => {
                if (true) {
                    dbf.parameters = dbf.parameters.map((eachParam, index) => {
                        eachParam.value = fnParams[index]
                        return eachParam
                    })
                    if (!('databaseFunction' in eachCol)) {
                        eachCol.databaseFunction = dbf
                    }
                    else {
                        eachCol.databaseFunction = applyFunctionToCol({ ...eachCol.databaseFunction }, { ...dbf }, self, fnParams) // dbf -> new function to be added. eachCol.databaseFuntion -> function that is already applied to column
                    }
                    if (fnParams[fnParams.indexOf(eachCol.column)]) {
                        currentDbFn[fnParams[fnParams.indexOf(eachCol.column)]] = fromJS(eachCol.databaseFunction).toJS()
                    }
                    else {
                        currentDbFn[eachCol.column] = fromJS(eachCol.databaseFunction).toJS()
                    }
                }
                return eachCol
            })
            self.columns = columns
        }

    }
    if (self.selectBuilder?.inSelectBuilder || self?.aggBuilder?.inBuilder) {
        return fnParams
    }
    return self
}

const modifyDBF = ({ dbf, payload, self }) => {
    let modifiedDBF = fromJS(dbf).toJS()
    payload = fromJS(payload).toJS()
    // let allAliases = _.pluck(self.columns, 'alias')
    let allAliases = self.columns.map(eachCol => (eachCol.alias))
    // let allColumns = _.pluck(self.columns, 'column')
    let allColumns = self.columns.map(eachCol => (eachCol.column))
    modifiedDBF.parameters = modifiedDBF.parameters.map((e, i) => {
        if (typeof payload[i] != 'undefined' && payload[i] != null) {
            if ((payload[i] || '').includes('___')) {
                e.value = argumentsInfo[payload[i]]
            }
            else {
                let result = payload[i]
                if (self.selectBuilder?.fromFnBuilder && allAliases.indexOf(result) !== -1){
                    result = allColumns[allAliases.indexOf(result)]
                }
                e.value = result
            }
        }
        else {
            console.warn('Argument missing')
        }
        return e
    })
    return modifiedDBF
}