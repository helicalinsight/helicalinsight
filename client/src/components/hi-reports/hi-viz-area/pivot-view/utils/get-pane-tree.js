
import randomColor from 'randomcolor';

const getChildren = (list,fields,marksList) =>{
    let field = fields[0]
    fields = fields.slice(1)
    let result = []
    let tempPane = {}
    list.map(record=>{
        let remaingKeys = {}
        Object.keys(record).map(key=>{
            if(key !== field){
                remaingKeys[key] = record[key]
            }
        })
        if(tempPane.field_value === record[field]){
            tempPane.children.push(remaingKeys)
        }else{
            if(tempPane.field_value){
                if(fields.length){
                    tempPane.children = getChildren(tempPane.children,fields,marksList)
                }
                result.push(tempPane)
                tempPane = {}
            }
            tempPane.fieldName = field
            tempPane.field_value = record[field]
            if(fields.length){
                tempPane.children = [remaingKeys] 
            }else{
                tempPane.value = remaingKeys.value
            }
        }
    })
    if(tempPane.field_value){
        if(fields.length){
            tempPane.children = getChildren(tempPane.children,fields,marksList)
        }
        result.push(tempPane)
        tempPane = {}
    }
    return result
}


export const getPaneTree = (data,marksList,marksValues) =>{
    let marksFields = marksList.map(mark=> mark.name )
    let tempData = data.map(rec=>{
        let { headers } = rec
        let obj = {}
        headers = headers.split("-seperator-") 
        obj.value = rec.value
        marksFields.map((field,i)=>{
            obj[field] = headers[i]
            obj.value[field] = headers[i]
        })
        marksValues = createValues(headers,marksList,marksValues)
        return obj
    })
    return {children:getChildren(tempData,marksFields,marksList),marksValues}
}
const createValues = (headers,marksList,marksValues) =>{
    marksList.map((field,i)=>{
        if(!marksValues[field.name]){
            marksValues[field.name] = { name:field.name,markType:field.markType,values: [] }
        }
        if(!marksValues[field.name].values.find(val=> val.value === headers[i] ) ){
            marksValues[field.name].values.push({value:headers[i]})
        }
    })
    return marksValues
}

