
const getRowChilds = (data,slicedRows,rowHeaderParent,rows) =>{
    let rowHeaders = []
    let field = slicedRows[0]
    let rowIndex = rows.indexOf(field)
    data.map(rec=>{
        let rowHeader = rowHeaderParent + "." +  rec[field]
        if(rowHeaders.some(header=> header.split(".")[rowIndex] === rec[field] ))return null
        let childData = data.filter(rec1 => rec1[field] === rowHeader.split(".")[rowIndex])
        if(slicedRows.slice(1).length){
            rowHeader = getRowChilds(childData,slicedRows.slice(1),rowHeader)
            rowHeaders = rowHeaders.concat(rowHeader)
        }else{
            rowHeaders.push(rowHeader)
        }
    })
    return rowHeaders
}


export const getRowHeaders1 = (data,rows) =>{
    let rowHeaders = []
    data.map(rec=>{
        if(!rows.length){
            return null
        }
        let field = rows[0]
        let rowHeader = rec[field]
        if(rowHeaders.some(header=> header.split(".")[0] === rowHeader ))return null
        let childData = data.filter(rec1 => rec1[field] === rowHeader )
        if(rows.slice(1).length){
            rowHeader = getRowChilds(childData,rows.slice(1),rowHeader,rows)
            rowHeaders = rowHeaders.concat(rowHeader)
        }else{
            rowHeaders.push(rowHeader)
        }
    })
    return rowHeaders
}
const getRowPaneChilds = (data,slicedRows,rowHeaderParent,rows,cellHeight) =>{
    let rowPanes = []
    let field = slicedRows[0]
    let rowIndex = rows.indexOf(field)
    data.map(rec=>{
        let pane = {levelName:rowHeaderParent + "." +  rec[field],hasChild:false,axis:field,height:cellHeight} 
        if(rowPanes.some(pane=> pane.levelName.split(".")[rowIndex] === rec[field] ))return null
        let childData = data.filter(rec1 => rec1[field] === pane.levelName.split(".")[rowIndex])
        if(slicedRows.slice(1).length){
            pane.hasChild = true
            pane.members = getRowPaneChilds(childData,slicedRows.slice(1),rowHeader,cellHeight)
            pane.height = pane.members.reduce((a, b) => a + b.height, 0)
        }
        rowPanes.push(pane)
    })
    return rowPanes
}
export const createRowPanes1 =  (data,rows,cellHeight) =>{
    let rowPanes = []
    try{

    data.map(rec=>{
        if(!rows.length){
            return null
        }
        let field = rows[0]
        let pane = {levelName:rec[field],hasChild:false,axis:field,height:cellHeight} 
        if(rowPanes.some(pane=> pane.levelName.split(".")[0] === rec[field] ))return null
        if(rowPanes.indexOf(pane.levelName) > -1) return null
        let childData = data.filter(rec1 => rec1[field] === pane.levelName )
        if(rows.slice(1).length){
            pane.hasChild = true
            pane.members = getRowPaneChilds(childData,rows.slice(1),pane.levelName,rows,cellHeight)
            pane.height = pane.members.reduce((a, b) => a + b.height, 0)
        }
        rowPanes.push(pane)
        
    })
    
}catch(e){
}
return rowPanes


}
