


const getColumnChilds = (data,slicedColumns,columnHeaderParent,columns) =>{
    let columnHeaders = []
    let field = slicedColumns[0]
    let columnIndex = columns.indexOf(field)
    data.map(rec=>{
        let columnHeader = columnHeaderParent + "." +  rec[field]
        if(columnHeaders.some(header=> header.split(".")[columnIndex] === rec[field] ))return null
        let childData = data.filter(rec1 => rec1[field] === columnHeader.split(".")[columnIndex])
        if(slicedColumns.slice(1).length){
            columnHeader = getColumnChilds(childData,slicedColumns.slice(1),columnHeader,columns)
            columnHeaders = columnHeaders.concat(columnHeader)
        }else{
            columnHeaders.push(columnHeader)
        }
    })
    return columnHeaders
}


export const getColumnHeaders = (data,columns) =>{
    let columnHeaders = []
    data.map(rec=>{
        if(!columns.length){
            columnHeaders =  []
            return null
        }
        let field = columns[0]
        let columnHeader = rec[field]
        if(columnHeaders.some(header=> header.split(".")[0] === columnHeader ))return null
        if(columnHeaders.indexOf(columnHeader) > -1) return null
        let childData = data.filter(rec1 => rec1[field] === columnHeader )
        if(columns.slice(1).length){
            columnHeader = getColumnChilds(childData,columns.slice(1),columnHeader,columns)
            columnHeaders = columnHeaders.concat(columnHeader)
        }else{
            columnHeaders.push(columnHeader)
        }
    })
    return columnHeaders
}

//rowHeader is not defined in this function.
const getColumnPaneChilds = (data,slicedRows,rowHeaderParent,columns,cellWidth) =>{
    let columnPanes = []
    let field = slicedRows[0]
    let rowIndex = columns.indexOf(field)
    data.map(rec=>{
        let pane = {levelName:rowHeaderParent + "." +  rec[field],hasChild:false,axis:field,width:cellWidth} 
        if(columnPanes.some(pane=> pane.levelName.split(".")[rowIndex] === rec[field] ))return null
        let childData = data.filter(rec1 => rec1[field] === pane.levelName.split(".")[rowIndex])
        if(slicedRows.slice(1).length){
            pane.hasChild = true
            pane.members = getColumnPaneChilds(childData,slicedRows.slice(1),rowHeader,cellWidth)
            pane.members = pane.members.sort((a,b)=> (a.levelName > b.levelName) ? 1 : (a.levelName < b.levelName) ? -1 :0 )
            pane.width = pane.members.reduce((a, b) => a + b.width, 0)
        }
        columnPanes.push(pane)
    })
    return columnPanes
}
export const getColumnPanes1 = (data,columns,cellWidth) =>{
    let columnPanes = []
    data.map(rec=>{
        if(!columns.length){
            return null
        }
        let field = columns[0]
        let pane = {levelName:rec[field],hasChild:false,axis:field,width:cellWidth} 
        if(columnPanes.some(pane=> pane.levelName.split(".")[0] === rec[field] ))return null
        if(columnPanes.indexOf(pane.levelName) > -1) return null
        let childData = data.filter(rec1 => rec1[field] === pane.levelName )
        if(columns.slice(1).length){
            pane.hasChild = true
            pane.members = getColumnPaneChilds(childData,columns.slice(1),pane.levelName,columns,cellWidth)
            pane.members = pane.members.sort((a,b)=> (a.levelName > b.levelName) ? 1 : (a.levelName < b.levelName) ? -1 :0 )
            pane.width = pane.members.reduce((a, b) => a + b.width, 0)
        }
        columnPanes.push(pane)
    })
    return columnPanes
}
