import { updateColumn, updateFormatProperty } from "../../../../redux/actions/hreport.actions"
import { getCanvasFieldDataType } from "../../../../utils/filter-utils"
import { getDefaultAFDataTypeValues } from "../../hi-editing-area/utils/property-utils"
import notify from "../../../hi-notifications/notify"
import { parseDbFuncString } from "./parse-db-func"

export const CAST_RAW_FUNCTION_RE = /\b(CAST|RAW)\s*\(/i;

export const isCastOrRawFunctionUsed = (functionsDefinition = "") =>
    CAST_RAW_FUNCTION_RE.test(String(functionsDefinition || ""));

export const saveDataBaseFunction = ({ databaseFunctions, fields, editingField }, dispatch) => {
    let updatedField = parseDbFuncString({ databaseFunctions, fields, editingField: { ...editingField } })
    const pendingReturns = editingField?.pendingFunctionReturns;
    if (
        pendingReturns
        // && isCastOrRawFunctionUsed(updatedField?.functionsDefinition)
        && updatedField?.databaseFunction
        && typeof updatedField.databaseFunction === "object"
    ) {
        updatedField.databaseFunction = { ...updatedField.databaseFunction, returns: pendingReturns };
    }
    delete updatedField.pendingFunctionReturns;
    updatedField.floatingType = ""
    dispatch(updateColumn({ editingField: updatedField }))

    // if (typeof editingField?.functionsDefinition === "string" && /\bRAW\s*\(/i.test(editingField.functionsDefinition)) {
    //     notify(dispatch).info({
    //         type: "Frontend",
    //         message: "RAW function is used — the expression will be passed as it is, without parsing.",
    //     });
    // }

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