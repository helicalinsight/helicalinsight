import { Collapse, Divider, message } from 'antd'
import React, { useEffect, useState } from 'react'
import NodeTypography from '../../nodeTypography'
import { getLabel } from '../../hcrCanvasPaneHelperMethods';
import { useDispatch, useSelector } from 'react-redux';
import CanvasPageStyles from '../../canvasProperty/hcrPageStyles';
import { hcrActions } from '../../../../../redux/actions';
import notify from '../../../../hi-notifications/notify';

const TableStyles = (props = {}) => {
    const {
        tableData = {},
        dispatch,
        EditorPanels,
        selectedStyle = [],
        onClose = () => { },
        tableStyles = []
    } = props || {}
    const { InputFiled, InputNumberFiled, SelectField } = EditorPanels || {}
    const { id: tableId } = tableData || {}

    const styleId = selectedStyle[0] || ""
    const tableStyle = tableStyles.find((style) => style.id === styleId) || {}
    const { styleName = "", isConditionalStyleReq, ...restStyles } = tableStyle || {}
    const [localStyles, setLocalStyles] = useState(tableStyle)
    const [saveButtonDisabled, setSaveButtonDisabled] = useState(true)
    const Notify = notify(dispatch);

    const handleChange = ({ key, value }) => {
        setSaveButtonDisabled(false)
        setLocalStyles((prev) => ({ ...prev, [key]: value }))
    }

    const handleSave = () => {
        const styleNames = tableStyles.filter(({ id }) => id !== styleId).map(({ styleName = "" }) => styleName)
        if (styleNames.includes(localStyles.styleName)) {
            Notify.warning({
                type: "Frontend",
                message: "Style name is already present."
            })
            return;
        }
        dispatch(hcrActions.hcrUpdateTableStyles({
            actionType: "updateStyle",
            styleId,
            tableId,
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
    }, [styleId])

    if (!styleId) return null;

    return (
        <Collapse size={"small"} className="canvas-property-collapse" defaultActiveKey={"styles"}>
            <Collapse.Panel
                header={<span className="canvas-property-title">{styleName}</span>}
                key={"styles"}
            >
                <CanvasPageStyles
                    fromAdvanceComponent={true}
                    onChange={handleChange}
                    onSave={handleSave}
                    styleValues={localStyles}
                    addConditionalStyleField={isConditionalStyleReq}
                    {...{
                        InputFiled,
                        InputNumberFiled,
                        SelectField,
                        dispatch,
                        getLabel,
                        saveButtonDisabled
                    }}
                />

            </Collapse.Panel>
        </Collapse>
    )
}

export default TableStyles