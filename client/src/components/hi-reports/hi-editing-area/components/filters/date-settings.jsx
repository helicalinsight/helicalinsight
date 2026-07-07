


import React from 'react'
import { Menu } from "antd"
import { useDispatch } from "react-redux"
import { funcMap, getDatePart, getFilterDataType } from "../../../../../utils/filter-utils"
import { updateFilter } from "../../../../../redux/actions/hreport.actions"

// This component is not used in the entire applicaton.
const DateSettings = props => {
    const dispatch = useDispatch()
    let { filter, dateFunctions } = props
    let { datePart, mapping } = filter
    let dataType = getFilterDataType(filter)

    const handleClick = part => {
        dispatch(updateFilter({ ...filter, datePart: part,values:[]  }))
        // props.updateMapping(part)
    }

    let datePartList = dateFunctions[dataType]
    datePartList = datePartList ? datePartList : dateFunctions["dateTime"]

    let displayDateFunc = datePart, valueDateFunc = datePart
    if (mapping && mapping.DisplayDBFunction && mapping.DisplayDBFunction.functionName) {
        let val = mapping.DisplayDBFunction.functionName
        val = val ? val : ""
        displayDateFunc = getDatePart(val)
    }
    if (mapping && mapping.valueDBFunction && mapping.valueDBFunction.functionName) {
        let val = mapping.valueDBFunction.functionName
        val = val ? val : ""
        valueDateFunc = getDatePart(val)
    }

    if (funcMap.indexOf(displayDateFunc) === -1) {
        return null
    }
    return (
        <>
            {/* <Menu className="filter-conditions-list" > */}
                {datePartList.map((format,i) => {
                    return (
                        <Menu.Item key={format.part ? format.part : "key"+i } onClick={e => handleClick(format.part)} >
                            {format.label}
                        </Menu.Item>
                    )
                })}
            {/* </Menu> */}
        </>
    )
}


export default DateSettings

