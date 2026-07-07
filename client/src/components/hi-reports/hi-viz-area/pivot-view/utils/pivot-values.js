
import { getRowHeaders1, createRowPanes1 } from "./row-headers"
import { getColumnHeaders, getColumnPanes1 } from "./column-headers"

const getColumnValueChilds = (data, slicedColumns, columnHeaderParent, rowHeader,values,columns) => {
    let dataValues = []
    let field = slicedColumns[0]
    let columnIndex = columns.indexOf(field)
    data.map(rec => {
        let columnHeader = columnHeaderParent.columnHeaders + "." + rec[field]
        let value = rec[values[0]]
        let actualText = values[0]
        let dataValue = { rowHeaders: rowHeader, columnHeaders: columnHeader, value,actualText,axis:"value" }
        if (dataValues.some(dataValue1 => dataValue1.columnHeaders.split(".")[columnIndex] === rec[field])) return null
        let childData = data.filter(rec1 => rec1[field] === columnHeader.split(".")[columnIndex])
        if (slicedColumns.slice(1).length) {
            dataValue = getColumnValueChilds(childData, slicedColumns.slice(1), dataValue, rowHeader,values,columns)
            dataValues = dataValues.concat(dataValue)
        } else {
            dataValues.push(dataValue)
        }
    })
    return dataValues
}

const getColumnValues = (data, columns, rowHeader,rows,values) => {
    let dataValues = []
    let filteredData = data.filter(rec => {
        let isRowHeader = true
        rows.map((row, i) => {
            if (rec[row] !== rowHeader.split(".")[i]) {
                isRowHeader = false
            }
        })
        return isRowHeader
    })
    filteredData.map(rec => {
        if (!columns.length) {
            dataValues = []
            return null
        }
        let field = columns[0]
        let columnHeader = rec[field]
        let actualText = values[0]
        let value = rec[values[0]]
        let dataValue = { rowHeaders: rowHeader, columnHeaders: columnHeader, value,actualText,axis:"value" }
        if (dataValues.some(dataValue1 => dataValue1.columnHeaders.split(".")[0] === columnHeader)) return null
        let childData = filteredData.filter(rec1 => rec1[field] === columnHeader)
        if (columns.slice(1).length) {
            dataValue = getColumnValueChilds(childData, columns.slice(1), dataValue, rowHeader,values,columns)
            dataValues = dataValues.concat(dataValue)
        } else {
            dataValues.push(dataValue)
        }
    })
    return dataValues
}


const prepareChilds = (columnList,columns,parentHeader) =>{
    if(!columns.length)return
    let headers = []
    columnList[columns[0]].sort().map(header=>{
        header = parentHeader ? parentHeader + "." + header : header
        if(columns.slice(1).length){
            header = prepareChilds(columnList,columns.slice(1),header)
            headers = headers.concat(header)
        }else{
            headers.push(header)
        }
    })
    return headers
}
export const getRowFieldLabels = (data,rows) =>{
    let rowList = {}
    rows.map(row=>{
        data.map(rec=>{
            if(rowList[row]){
                if(rowList[row].indexOf(rec[row]) === -1){
                    rowList[row].push(rec[row])
                }
            }else{
                rowList[row] = [rec[row]]
            }
        })
    })
    return rowList
}

export const listColumns = (data,columns) =>{
    let columnList = {}
    columns.map(clmn=>{
        data.map(rec=>{
            if(columnList[clmn]){
                if(columnList[clmn].indexOf(rec[clmn]) === -1){
                    columnList[clmn].push(rec[clmn])
                }
            }else{
                columnList[clmn] = [rec[clmn]]
            }
        })
    })
    columnList = prepareChilds(columnList,columns,"")
    return columnList
}



const createPivotValues = (data, rows, columns,values) => {
    let rowHeaders = getRowHeaders1(data, rows)
    let columnHeaders = listColumns(data,columns)
    let pivotValues = rowHeaders.map(rowHeader => {
        try{
            return getColumnValues(data, columns, rowHeader,rows,values)
        }catch(e){
        }
    })
    try{
        pivotValues = pivotValues.map((tableRow,j)=>{
            tableRow = tableRow.sort((a,b)=> (a.columnHeaders > b.columnHeaders) ? 1 : (a.columnHeaders < b.columnHeaders) ? -1 : 0 )
            columnHeaders.map((columnHeader,i)=>{
                if(tableRow[i] && tableRow[i].columnHeaders !== columnHeader){
                    tableRow.splice(i,0,{
                        rowHeaders:tableRow[0].rowHeaders,
                        columnHeaders:columnHeader,
                        value:0,
                        actualText:tableRow[0].actualText,
                        axis:"value"
                    })
                }else if(!tableRow[i]){
                    tableRow.push({
                        rowHeaders:tableRow[0].rowHeaders,
                        columnHeaders:columnHeader,
                        value:0,
                        actualText:tableRow[0].actualText,
                        axis:"value"
                    })
                }
            })
            return tableRow
        })
    }catch(e){
    }
    return pivotValues
}

export default createPivotValues