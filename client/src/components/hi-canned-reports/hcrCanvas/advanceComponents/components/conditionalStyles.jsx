import React, { useEffect } from 'react'
import notify from '../../../../hi-notifications/notify'
import { hcrActions } from '../../../../../redux/actions'
import { useState } from 'react'
import { Button, Collapse, Space } from 'antd'
import NodeColorPicker from '../../nodeColorPicker'
import FieldSelector from '../../fieldSelector'
import useHCRCascadeSelector from '../../../../../hooks/useHCRCascadeSelector'

const ConditionalStyles = (props = {}) => {
    const { tableData, EditorPanels, selectedConditionalStyle, onClose, tableStyles, dispatch, selectedSubDS } = props || {}
    const { styleId, conditionalStyleId } = selectedConditionalStyle[0]
    const { conditionalStyles = [] } = tableStyles.find((style) => style.id === styleId) || {}
    const { id: tableId } = tableData || {}
    const { InputFiled } = EditorPanels

    const tableStyle = conditionalStyles.find((style) => style.id === conditionalStyleId)
    const { styleName = "" } = tableStyle || {}
    const [localStyles, setLocalStyles] = useState(tableStyle)
    const [saveButtonDisabled, setSaveButtonDisabled] = useState(true)

    const { calculations = [], fields = [], groups = [], parameters = [] } = selectedSubDS || {}
    const { getCascaderOptions } = useHCRCascadeSelector({ node: {}, fields, calculations, parameters })
    const cascaderOptions = getCascaderOptions()
    const Notify = notify(dispatch);

    const handleChange = ({ key, value }) => {
        setSaveButtonDisabled(false)
        setLocalStyles((prev) => ({ ...prev, [key]: value }))
    }

    const handleSave = () => {

        dispatch(hcrActions.hcrUpdateTableStyles({
            actionType: "updateConditionalStyle",
            styleId,
            tableId,
            conditionalStyleId,
            updatedStyles: localStyles,
        }))

        Notify.success({
            type: "Frontend",
            message: "Style updated successfully.",
        });
        setSaveButtonDisabled(true)
    }

    useEffect(() => {
        setLocalStyles(tableStyle)
    }, [styleId, conditionalStyleId])

    if (!styleId && !conditionalStyleId) return null;

    return (
        <Collapse size={"small"} className="canvas-property-collapse" defaultActiveKey={"styles"}>
            <Collapse.Panel
                header={<span className="canvas-property-title">{styleName}</span>}
                key={"styles"}
            >
                <div className="property-group">Expression</div>
                <div>
                    <FieldSelector
                        onChange={(valueObj = {}) => {
                            const { value } = valueObj || {}
                            handleChange({ key: "expression", value })
                        }}
                        options={cascaderOptions}
                        value={localStyles.expression}
                    />
                </div>
                <NodeColorPicker
                    onPropertyChange={({ value }) => {
                        handleChange({ key: "expressionBackColor", value })
                    }}
                    clrVal={localStyles.expressionBackColor}
                    keyWord="color"
                    label={"Alt. Row Detail"}
                />

                <Space>
                    <Button
                        disabled={saveButtonDisabled}
                        type="link"
                        onClick={handleSave}
                    >
                        Save
                    </Button>
                </Space>
            </Collapse.Panel>
        </Collapse>
    )
}

export default ConditionalStyles