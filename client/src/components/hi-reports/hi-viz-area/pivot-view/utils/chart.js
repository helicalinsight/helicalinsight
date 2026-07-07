import _ from "lodash"

export const checkIfPointChart = (subVizType) =>{
  let ponintCharts = ["circle","text","square","shape","density"]
  let nonPonintCharts = ["bar","line","area","polygon"]
  if(ponintCharts.indexOf(subVizType) > -1){
    return true
  }else{
    return false
  }
}

export const createCellId = cell =>{
  let { columnHeaders,rowHeaders } = cell
  let id = columnHeaders + "-" + rowHeaders
  id  = id.replace(/ /g,"-");
  return id

}

export const getColumnHeaders = (graphRef,pivotValues) => {
  let { columns } = graphRef
  let columnHeaders = [...pivotValues]
  columnHeaders = columnHeaders.filter(columnHeader => !columnHeader[0])
    .map(columnHeader => columnHeader.slice(1))
    .map(columnHeader => {
      columnHeader = columnHeader.map(cell => {
        return cell.valueSort.levelName
      })
      return columnHeader
    })
  return _.uniq(columnHeaders[columns.length - 1])
}

export const getColumnHeadersHeight = graphRef =>{
  let { columns,valuesList,columnsAxisHeight,rows } = graphRef
    let containerHeight = 0
    if (columns.length) {
      containerHeight = columns.reduce((a, b) => a + b.height, 0) + 20
    } 
    if (valuesList.columns.length) {
      containerHeight += columnsAxisHeight
    }
    if(!columns.length && !valuesList.columns.length && rows.length ){
      containerHeight += 20
    }
    return containerHeight
}

export const getRowHeadersWidth = (graphRef) =>{
  let { rows,valuesList,rowAxisWidth } = graphRef
    let containerWidth = 0
    if (rows.length) {
      containerWidth = rows.reduce((a, b) => a + b.width, 0)
    } 
    if (valuesList.rows.length && valuesList.rows[0].name !== "_no_Value_") {
      containerWidth += rowAxisWidth
    }
    return containerWidth
}

export const getGraphHeight = (graphRef,pivotValues) => {
    let rowHeaders = getRowHeaders(graphRef,pivotValues)
    let { cellHeight } = graphRef
    let rowsLength = 1
    if (Object.keys(rowHeaders).length) {
      rowsLength = rowHeaders[Object.keys(rowHeaders)[0]].length
    }
    let rowsHeight = rowsLength * cellHeight + 25
    if (rowsHeight > 400) {
      rowsHeight = 400
    }
    return rowsHeight
}

const getMax = (val, d) => {
    if (d === undefined) d = 1
    if (Math.floor(val / 10) === 0) {
      return (val + 1) * d
    } else {
      d = d * 10
      return getMax(Math.floor(val / 10), d)
    }
  }
export const getRange = (field,graphRef,pivotValues) => {
  let values = [], max = 0,min = 0
  let { rows, columns } = graphRef
  let axisValue = ""
  pivotValues.map(xRow => {
    xRow.map((cell, i) => {
      let rowHeaders = cell.rowHeaders ? cell.rowHeaders.split("-seperator-") : []
      let columnHeaders = cell.columnHeaders ? cell.columnHeaders.split("-seperator-") : []
      if (cell.axis === "value" && cell.actualText === field &&
        rowHeaders.length === rows.length && columnHeaders.length === columns.length) {
        values.push(cell.value)
      }
      axisValue = cell.actualText
      return null
    })
    return null
  })
  if(axisValue === "_no_Value_"){
    return { max:1,min:0,axisValue }
  }
  try {
    max = getMax(Math.max(...values))
    min = Math.min(...values) < 0 ? 0 - getMax(Math.abs(Math.min(...values))) : 0
  } catch (e) {
    max = 100
    min = 0
  }
  return { max,min }
}

export const getRowHeaders = (graphRef,pivotValues) => {
    let rowHeaders = {}
    let { rows } = graphRef
    if (!rows?.length) {
      return rowHeaders
    }
    pivotValues.map(pivotRow => {
      if (pivotRow[0] && pivotRow[0].valueSort.axis === rows[rows.length - 1].name) {
        pivotRow[0].valueSort.levelName.split("-seperator-").map((fieldValue, i) => {
          if (rowHeaders[rows[i].name]) {
            rowHeaders[rows[i].name].push(fieldValue)
          } else {
            rowHeaders[rows[i].name] = []
            rowHeaders[rows[i].name].push(fieldValue)
          }
          return null
        })
      }
      return null
    })
    return rowHeaders
}

export const getNumberRowOfCells = (pane) => {
    let { hasChild, members } = pane
    let numberOfCells = 0
    if (hasChild) {
      members.map(member => {
        if (member.hasChild) {
          numberOfCells += getNumberRowOfCells(member)
        } else {
          numberOfCells += 1
        }
      })
    } else {
      numberOfCells = 1
    }
    return numberOfCells
}

export const getNumberColumnCells = (pane) => {
    let { hasChild, members } = pane
    let numberOfCells = 0
    if (hasChild) {
      members.map(member => {
        if (member.hasChild) {
          numberOfCells += getNumberColumnCells(member)
        } else {
          numberOfCells += 1
        }
      })
    } else {
      numberOfCells = 1
    }
    return numberOfCells
  }

export const createEllipsis = (label,height) =>{
   
}

export const createHorizontalEllipsis = (label, width) => {
  let test = document.getElementById("ellipsis");
  test.innerHTML = label + "..."
  let textWidth = (test.clientWidth + 1)
  if(textWidth > width ){
    let extraWidth = textWidth - width
    label = label.substring(0,label.length - Math.floor(extraWidth/8)).trim() + "..."

  }
  return label;
}
export const getLabelLength = (label) => {
    return (label.length * 8 ) - 10
}

export const split = (left, right, parts) =>{
  var result = [],
        delta = (right - left) / (parts - 1);
    while (left < right) {
        result.push(left);
        left += delta;
    }
    result.push(right);
    return result;
}
export const getTicks = ({max, min,width,axisValue}) => {
  if(axisValue === "_no_Value_"){
    return [{value:0,displayValue:0 },{value:1,displayValue:1 }]
  }
  let range = 0,isNegative = false
  if(max < Math.abs(min)){
      isNegative = true
      range = Math.abs(min)
  }else{
      isNegative = false
      range = Math.abs(max)
  }
  let ticks = []
  let numberOfTicks = Math.floor(width/40)  
  let interval = getInterval(range,numberOfTicks)
  let positiveTicks = []
  for (let i = 0; i < 100; i++) {
      let tick = interval * (i+1)
      positiveTicks.push(tick)
      if(max <= tick){
          break
      }
  }
  let negativeTicks = []
  if(min < 0){
      for (let i = 0; i < numberOfTicks; i++) {
          let tick = interval * (i+1)
          negativeTicks.unshift(0-tick)
          if(Math.abs(min) <=  Math.abs(tick) ){
              break
          }
      }
  }
  ticks = [].concat(negativeTicks,[0],positiveTicks)
  ticks = ticks.map(tick => ({value:tick,displayValue: formatNumber(tick)}))
  return ticks
}

export const formatNumber = (labelValue) => {
  // Nine Zeroes for Billions
  let isNegative = 0 > labelValue ? true : false
  labelValue = Number(labelValue)
  if(Math.abs(labelValue) >= 1.0e+9){
      labelValue = Math.floor(Math.abs(labelValue) / 1.0e+9)
      labelValue =  isNegative ? 0 -labelValue + "B" : labelValue + "B" 
  }else if(Math.abs(labelValue) >= 1.0e+6){
      labelValue = Math.floor(Math.abs(labelValue) / 1.0e+6)
      labelValue =  isNegative ? 0 -labelValue + "M" : labelValue + "M" 
  }else if(Math.abs(labelValue) >= 1.0e+3){
      labelValue = Math.floor(Math.abs(labelValue) / 1.0e+3)
      labelValue =  isNegative ? 0 -labelValue + "K" : labelValue + "K" 
  }
  return labelValue
}

const getInterval = (range,numberOfTicks) =>{
  let interval = Math.floor(range/numberOfTicks)
  interval = Math.floor(interval / 10) === 0
      ? 1 * Math.floor(interval / 1)
      : Math.floor(interval / 100) === 0
          ? 10 *  Math.floor(interval / 10)
          : Math.floor(interval / 1000) === 0             
              ? 100 * Math.floor(interval / 100)
              : Math.floor(interval / 10000) === 0            //k
                  ? 1000 * Math.floor(interval / 1000)
                  : Math.floor(interval / 100000) === 0
                      ? 10000 * Math.floor(interval / 10000)
                      : Math.floor(interval / 1000000) === 0
                          ? 100000 * Math.floor(interval / 100000)
                          : Math.floor(interval / 10000000) === 0         //m
                              ? 1000000 * Math.floor(interval / 1000000)
                              : Math.floor(interval / 100000000) === 0
                                  ? 10000000 * Math.floor(interval / 10000000)
                                  : Math.floor(interval / 1000000000) === 0
                                      ? 100000000 * Math.floor(interval / 100000000)
                                      : Math.floor(interval / 10000000000) === 0      //b
                                      ? 1000000000 : 10000000000  * Math.floor(interval / 10000000000)
                                      
  return interval 
}

export const checkParentElementBoundingRect = (id, currentCell, payload) => {
  const parentElement = document.getElementById(id)
  let {
    top: t1,
    bottom: b1,
    left: l1,
    right: r1,
  } = parentElement?.getBoundingClientRect(parentElement);
  let {
    top: t2,
    bottom: b2,
    left: l2,
    right: r2,
  } = currentCell?.getBoundingClientRect();
  const constant = (payload?.length * 24) + 65
  let result = { top: t2, right: r2, bottom: b2, left: l2 }
  if (!parentElement) return result
  if (l2 < l1 || r2 > r1 || (t2 + constant) < t1 || (b2 + constant) > b1) {
    let left = l2;
    let top = t2 - constant;
    let right = r2;
    let bottom = b2;
    return { top, right, bottom, left }
  } else {
    return result
  }
}