import { Card } from 'antd'
import React from 'react'
import { useDrop } from 'react-dnd'
import { useDispatch } from 'react-redux'
import ReportField from '../../../../hi-fields-area/field'
import { dropIntoMeasureNameAndValue } from '../../../utils/marks-utils'
import { addMeasureField } from '../../../../../../redux/actions/hreport.actions'
import { getFieldDisplayName } from '../../../../../../utils/utilities'

const MeasureMarks = (props) => {
    const { fields = [] } = props || {}
    const dispatch = useDispatch();
    const emptyFields = fields.length === 0;
    const handleAddMeasureField = (data, fields) => {
        const { type, field = {} } = data || {}
        switch (type) {
            case "metadata_field":
                dropIntoMeasureNameAndValue(data, fields, dispatch)
                break;
            case "canvas_field":
                const { floatingType = null } = field || {}
                if (
                    !field?.custom_frontend_field &&
                    ["continous", ""].includes(floatingType) &&
                    !fields.find(fld => getFieldDisplayName(fld) === getFieldDisplayName(field))
                ) {
                    let newField = { addedAs: 'measure_field', fieldId: field.id }
                    dispatch(addMeasureField(newField));
                }
                break;
            default:
                break;
        }
    }

    const [, drop] = useDrop(() => ({
        accept: ["metadata_field", "canvas_field"],
        drop: item => {
            handleAddMeasureField(item, fields);
        }
    }), [fields])
    return (
        <div ref={drop} data-testid="hr-measure_field_marks">
            <Card
                title={null}
                className={"hi-mark-field"}
                size="small"
                key={"all_measures"}
            >
                {emptyFields && <span className='placeholder-text'>Drag and drop your measure fields here.</span>}
                {fields?.map((field, _i, arr) => {
                    return (
                        <div className={_i !== arr.length ? 'hr-measure-field' : ''}>
                            <ReportField field={field} key={field.id} />
                        </div>
                    )
                })}
            </Card>
        </div>
    )
}

export default MeasureMarks;