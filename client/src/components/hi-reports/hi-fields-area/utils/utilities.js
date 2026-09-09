import { updateColumn, updateFormatProperty } from "../../../../redux/actions/hreport.actions"
import { getCanvasFieldDataType } from "../../../../utils/filter-utils"
import { getDefaultAFDataTypeValues } from "../../hi-editing-area/utils/property-utils"
import notify from "../../../hi-notifications/notify"
import { parseDbFuncString } from "./parse-db-func"

export const saveDataBaseFunction = ({ databaseFunctions, fields, editingField }, dispatch) => {
    let updatedField = parseDbFuncString({ databaseFunctions, fields, editingField: { ...editingField } })
    updatedField.floatingType = ""
    dispatch(updateColumn({ editingField: updatedField }))

    if (typeof editingField?.functionsDefinition === "string" && /\bRAW\s*\(/i.test(editingField.functionsDefinition)) {
        notify(dispatch).info({
            type: "Frontend",
            message: "RAW function is used — the expression will be passed as it is, without parsing.",
        });
    }

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