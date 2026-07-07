
import { Card, } from 'antd';
import { useState } from 'react';
import { useDrop } from 'react-dnd';
import { useDispatch, useSelector } from "react-redux";
import { removeFieldFromMarks } from '../../../../../redux/actions/hreport.actions';
import ReportField from '../../../hi-fields-area/field';
import { checkMarkFieldDisable, dropIntoMarks } from "../../utils/marks-utils";
import { getMarkTitle } from './utils';



const Mark = props => {
    const dispatch = useDispatch()
    const [dropdown, setDropdown] = useState(null)
    let disabled = false
    let { mark, markType, selectedType, subVizType, reportId, clicked = false, properties: { progress: { chartType: progressChartType = '' } = {} } = {} } = props;
    let { fields } = mark[markType]
    const { fields: allFields, customChart } = useSelector(state => {
        let activeReport = state.hreport.present.reports.find(report => report.active)
        return activeReport
    })
    const [, drop] = useDrop(() => ({
        accept: ["column", "canvas_field", "metadata_field"],
        drop: data => {
            addToMarks({ ...data, markType, mark, fields: allFields }, dispatch)
        }
    }))
    const addToMarks = (data) => {
        dropIntoMarks(data, dispatch)
    }
    let vfMarks = customChart?.selected;
    if (clicked) {
        disabled = checkMarkFieldDisable({ selectedType, subVizType, markType, vfMarks, progressChartType })
    } else {
        if (checkMarkFieldDisable({ selectedType, subVizType, markType, vfMarks, progressChartType })) return null
    }

    const handleVisibleChange = flag => {
        setDropdown(flag)
    }
    const closeDropdown = () => {
        setDropdown(false)
    }

    const handleRemove = (field) => {
        dispatch(removeFieldFromMarks({ mark, field, markType }))
    }

    return (
        <div ref={!disabled ? drop : null}>
            <Card
                title={getMarkTitle(selectedType, subVizType, markType)}
                className={!disabled ? "hi-mark-field" : "hi-mark-field-disabled"}
                // extra={<a href="#">More</a>}
                size="small" >
                {fields.map((field) => {
                    if (!field.addedAs) {
                        field = allFields.find(tempField => tempField.id === field.id)
                    }
                    return (
                        <ReportField field={field} key={field.id} />
                    )
                })}
            </Card>
        </div>
    )
}

export default Mark