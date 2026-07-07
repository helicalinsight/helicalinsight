import React, { useState } from 'react'
import { referenceLineAll, referenceLineMarkTypes } from '../../../../utils/constants'
import { Card, Checkbox, Tooltip, Input, Row } from 'antd'
import { useDrop } from 'react-dnd'
import { toTitleCase } from '../../../../../../utils/text-utils'
import { useDispatch } from 'react-redux'
import { changeReferenceLine, updateReferenceLineValue } from '../../../../../../redux/actions/hreport.actions'

const ReferenceLineMark = (props) => {
    const {
        display,
        id,
        referenceType,
        value,
        enabled,
        isStatic
    } = props || {}
    const dispatch = useDispatch()
    const [, drop] = useDrop(() => ({
        accept: ["column", "canvas_field", "metadata_field"],
        drop: data => {
            console.log(data)
        }
    }))

    const payload = {
        enabled,
        value,
        isStatic,
        id,
        display
    }

    const setStatic = () => {
        let newPayload = { ...payload, isStatic: !isStatic }
        dispatch(updateReferenceLineValue({ payload: newPayload }))
    }
    const setEnabled = () => {
        let newPayload = { ...payload, enabled: !enabled }
        dispatch(updateReferenceLineValue({ payload: newPayload }))
    }

    const handleChange = (valueStr, name) => {
        let newPayload = {
            enabled,
            value,
            isStatic,
            id,
            display
        }
        newPayload = { ...newPayload, [name]: valueStr }
        dispatch(updateReferenceLineValue({ payload: newPayload }))
    }

    return (
        <div>
            <Row justify='end'>
                {display === referenceLineAll && <div>
                    <Checkbox
                        checked={isStatic}
                        data-testid={`reference-line-static-check`}
                        onChange={setStatic} >
                        <Tooltip title={"Static Reference Line"}>Static</Tooltip>
                    </Checkbox>
                </div>}
                <div>
                    <Checkbox
                        checked={enabled}
                        data-testid={`reference-line-enable-check`}
                        onChange={setEnabled}>
                        <Tooltip title={"Enable"}>Enable</Tooltip>
                    </Checkbox>
                </div>
            </Row>
            {isStatic && referenceLineMarkTypes.map((item) => (
                <Card
                    title={item}
                    className={"hi-mark-field"}
                    size="small"
                    key={item}
                >
                    <Input
                        className=""
                        value={isStatic && typeof payload[item] !== "object" ? payload[item] : ""}
                        type={item === 'value' ? 'number' : 'text'}
                        placeholder={`Enter ${toTitleCase(item)}`}
                        onChange={(e) => {
                            handleChange(e.target.value, item)
                        }}
                    />
                </Card>
            ))
            }

            <div ref={drop}>
                {!isStatic && referenceLineMarkTypes.map((item) => (
                    <Card
                        title={item}
                        className={"hi-mark-field"}
                        size="small"
                        key={item}
                    >
                        {/* {fields.map((field) => {
                        if (!field.addedAs) {
                            field = allFields.find(tempField => tempField.id === field.id)
                        }
                        return (
                            <ReportField field={field} key={field.id} />
                            )
                        })} */}
                    </Card>
                ))}
            </div>
        </div>
    )
}

export default ReferenceLineMark