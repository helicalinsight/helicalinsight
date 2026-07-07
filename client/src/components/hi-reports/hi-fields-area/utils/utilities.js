import { updateColumn, updateFormatProperty } from "../../../../redux/actions/hreport.actions"
import { getCanvasFieldDataType } from "../../../../utils/filter-utils"
import { getDefaultAFDataTypeValues } from "../../hi-editing-area/utils/property-utils"
import { parseDbFuncString } from "./parse-db-func"

export const saveDataBaseFunction = ({ databaseFunctions, fields, editingField }, dispatch) => {
    let updatedField = parseDbFuncString({ databaseFunctions, fields, editingField: { ...editingField } })
    updatedField.floatingType = ""
    dispatch(updateColumn({ editingField: updatedField }))

    // // auto formatting on field
    // let fieldDataType = getCanvasFieldDataType(updatedField)
    // if (['numeric', 'date', 'dateTime', 'time'].includes(fieldDataType)) {
    //     let newFormatProperties = {
    //         id: updatedField.id,
    //         values: getDefaultAFDataTypeValues(fieldDataType, "create"),
    //     }
    //     dispatch(updateFormatProperty(newFormatProperties))
    // }
}