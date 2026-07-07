import "./hi-fields.scss"
import { useDispatch, useSelector } from "react-redux";
import FieldDropArea from "./field-drop-area";
import CustomQuery from "./custom-query";
import FunctionsEditor from "./db-functions/editor";
import { checkIfDrillThrough } from "../hi-viz-area/utils/utillities";
const columnText = "Add Column or Double click this area to add Custom Column"
const rowText = "Add row or Double click this area to add Custom Column"

const FieldsArea = (props) => {
    const { fields, editingField, activeDrillthroughId = "", id = "", measures = {} } = useSelector(state => {
        let activeReport = state.hreport.present.reports.find(report => report.active)
        return activeReport
    })
    const dispatch = useDispatch();
    const isDrillThroughReport = checkIfDrillThrough(dispatch, id, activeDrillthroughId)
    let field = fields.find(item => item.id === editingField?.id)
    const visible = props.savedLayout?.current?.filter(item => item.i === 'chart-area')?.map((i) => i.w)[0] > 32
    return (
        <div>
            {(editingField && editingField.id === field.id) && <FunctionsEditor field={field} />}
            <CustomQuery />
            <div className={ isDrillThroughReport ? "space-align-container hi-fields-disabled" : "space-align-container"}>
                <div className="space-align-block hi-fields-list">
                    <FieldDropArea fields={fields} fieldType="column" defaultText={visible ? columnText : ""} {...{ visible, measures }} />
                </div>
                <div className="space-align-block hi-fields-list">
                    <FieldDropArea fields={fields} fieldType="row" defaultText={visible ? rowText : ""} {...{ visible, measures }} />
                </div>
            </div>
            {/* <Button onClick={hanldeGenerate} >Generate</Button> */}
        </div>
    )
}

export default FieldsArea