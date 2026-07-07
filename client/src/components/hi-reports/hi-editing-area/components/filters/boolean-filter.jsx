
import FilterValues from "./filter-values"
import { valuesConditions,getFilterDataType } from "../../../../../utils/filter-utils";


const BooleanFilter = props =>{
    let { filter } = props
    let { condition,datePart } = filter
    if(datePart)return null
    if(!valuesConditions.includes(condition))return null
    let dataType = getFilterDataType(filter)
    if(dataType !== "boolean")return null
    return(
        <FilterValues  {...props}/>
    )
}

export default BooleanFilter