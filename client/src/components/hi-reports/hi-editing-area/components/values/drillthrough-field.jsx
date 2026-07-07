
import { Card, Checkbox, Input, Tooltip, Typography } from 'antd';
import { useDrop } from 'react-dnd';
import { useDispatch } from "react-redux"
import { InfoCircleOutlined } from "@ant-design/icons"
import { rangeConditions } from '../../../../../utils/filter-utils';
import { dropIntoParams } from '../../utils/marks-utils';
import { getFieldDisplayName } from '../../../../../utils/utilities';
import Field from '../../../hi-fields-area/field';
import { useEffect, useRef } from 'react';
import { removeFieldFromCanvas } from '../../../../../redux/actions/hreport.actions';


const { Text } = Typography;

const DrillThroughField = props => {
    const dispatch = useDispatch()
    let { fltr, drillThroughId, fields } = props
    const prevDrillThroughFieldId = useRef(null)
    let { label, values, mappedColumn, isStatic, staticValue, condition, mappedColumnId } = fltr
    staticValue = staticValue || ""
    mappedColumn = mappedColumn || ""
    useEffect(() => {
        if (prevDrillThroughFieldId.current && prevDrillThroughFieldId.current !== mappedColumnId) {
            dispatch(removeFieldFromCanvas({ field: { id: prevDrillThroughFieldId.current } }))
        }
        prevDrillThroughFieldId.current = mappedColumnId
    }, [mappedColumnId])
    const [, drop] = useDrop(() => ({
        accept: ["metadata_field"],
        drop: data => {
            dropIntoParams({ ...data, filterId: fltr.uid, drillThroughId }, dispatch)

        }
    }))
    let isRange = rangeConditions.includes(condition)
    let field = fields.find(field => field.id === mappedColumnId) || {}
    mappedColumn = getFieldDisplayName(field) || mappedColumn
    return (
        <Card key={label} type="inner" title={<div>
            {isRange && <Tooltip title={`${label} requires more than 1 value`} >
                <InfoCircleOutlined className="drill-through-field-info"
                    style={{
                        fontSize: 12,
                    }}
                />{" "}
            </Tooltip>}
            {/* {label} */}
            {fltr?.alias || label}  {/* added this for bug 6548 */}
        </div>} extra={<div>
            <Checkbox checked={isStatic}
                data-testid={`report1-filter1-static-check`}
                onChange={e => {
                    props.updateColumn({ isStatic: !isStatic })
                }} >
                Static
            </Checkbox>
            <Tooltip title={"Static value will be passed all the time"} ><InfoCircleOutlined
                style={{
                    fontSize: 12,
                }} /></Tooltip>
        </div >}  >
            {!isStatic ?
                <>
                    <div ref={drop} >
                        {(field && mappedColumnId) ? <Field field={field} /> : <Input className="drill-field" value={mappedColumn} placeholder="Please drag and drop fields"
                            readOnly />}
                    </div>
                    <Text ellipsis={true}>{values.join(",")}</Text>
                </> : <Input className="" value={staticValue} placeholder="Enter filter values..."
                    data-testid={`report1-filter1-static-input`}
                    onChange={e => props.updateColumn({ staticValue: e.target.value })} />}
        </Card >
    )
}

export default DrillThroughField