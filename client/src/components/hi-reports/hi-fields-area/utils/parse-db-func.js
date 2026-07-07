

import { getFieldDisplayName } from '../../../../utils/utilities';

const makeDbFunc = ({ allFuncsList, fields, expression }) => {
    let funcName = expression.split('(')[0]
    let selectedFunc = allFuncsList.find(func => func.value === funcName)
    let databaseFunction = {}
    if (selectedFunc) {
        databaseFunction = {...selectedFunc}
        let argString = expression.substring(expression.indexOf("(") + 1, expression.lastIndexOf(")"))
        // argString = argString.replace(/ /g, '')
        let args = []
        let arg = ""
        let childFunc = false
        let arrayStart = false
        let objectStart = false
        for(let i = 0; i < argString.length; i++) {
            if(argString[i] === "["){
                arrayStart = true
            }
            if(argString[i] === "{"){
                objectStart = true
            }
            if(argString[i] === "}"){
                objectStart = false
            }
            if(argString[i] === "]"){
                arrayStart = false
            }
            if(argString[i] === "," && !childFunc && !arrayStart && !objectStart){
                args.push(arg)
                arg = ""
            }else{
                if(argString[i] === "("){
                    childFunc = true
                }
                if(argString[i] === ")"){
                    childFunc = false
                }
                arg = arg + argString[i]
            }
            if(i === argString.length - 1){
                args.push(arg)
            }
        }
        // let args = argString.split(',')
        // CAST(longitude,DECIMAL(20,2))
        let parameters = []
        args.map((arg,i)=>{
            let param = {...databaseFunction.parameters[i]}
            param.column =  false
            if(arg.includes("(") && arg.includes(")")){
                param.value =  makeDbFunc({ allFuncsList, fields, expression:arg })
            }else{
                let selectedCol = fields.find(col => getFieldDisplayName(col) === arg) 
                if(selectedCol){ 
                    param.column = true
                    param.value = `${(selectedCol?.databaseName && !selectedCol?.isView) ? selectedCol.databaseName + "." : ""}${selectedCol.column}` //fix for bug id 6587 not adding database name if it is view type column
                    // param.value = `${selectedCol?.databaseName ? selectedCol.databaseName + "." : ""}${selectedCol.column}`
                    // param.value = selectedCol.column
                }else{
                    param.value = arg
                }
            } 
            parameters.push(param)
        })
        databaseFunction = {...databaseFunction,parameters}
    }else{
        databaseFunction = expression
    }
    return databaseFunction
}

export const parseDbFuncString = ({ databaseFunctions, fields, editingField }) => {
    let funcString = editingField.functionsDefinition
    // funcString = funcString.replace(/ /g, '')
    let allFuncsList = []
    Object.keys(databaseFunctions).map(type => {
        databaseFunctions[type].map(func => {
            allFuncsList.push(func)
        })
    })
    let bdFunc = makeDbFunc({ allFuncsList, fields, expression: funcString })
    return {...editingField,databaseFunction:bdFunc}
}