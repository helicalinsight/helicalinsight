import { isObject } from "../../../utils/is-object"

export const checkTablesAvailability = ({ tables }) => {
    if (!isObject(tables)) {
        return false
    }
    if (!Object.values(tables).length) {
        return false
    }
    return true
}

export const CheckTablesToHaveAtleastOneTableOrView = ({tables = {}}) => {
   const values = Object.values(tables);
   const tablesOrViews = values.filter(ele => {
       return !ele.duplicate;
   })
   return tablesOrViews.length;  // truthy  === table/view present;
}