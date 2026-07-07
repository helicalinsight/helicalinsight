import React from 'react'
import CanvasGroupProperties from '../../canvasProperty/hcrGroupProperties';
import { getLabel } from '../../hcrCanvasPaneHelperMethods';
import { hcrActions } from '../../../../../redux/actions';

const GroupProperties = (props = {}) => {
    const {
        dispatch,
        EditorPanels,
        selectedGroup,
        onClose,
        selectedSubDS,
        tableData: { id }
    } = props || {};
    const { InputFiled, SelectField, InputNumberFiled } = EditorPanels || {}
    const { calculations = [], fields = [], id: subDSId, groups = [] } = selectedSubDS || {}
    const currentGrp = selectedGroup[0] || ""

    const reset = () => {
        dispatch(hcrActions.hcrUpdateCanvasTabComponent({ actionType: "clearSelection", id }))
        onClose()
    }

    const onChange = ({ key, value }) => {
        let payload = {
            actionType: "updateGroups",
            id: subDSId,
            groups: groups.map((grp) => {
                if (grp.id === currentGrp) {
                    return {
                        ...grp,
                        [key]: value
                    }
                }
                return grp;
            })
        }
        dispatch(hcrActions.hcrUpdateSubdataSets(payload))
    }
    const onDelete = () => {
        let payload = {
            actionType: "updateGroups",
            id: subDSId,
            groups: groups.filter((grp) => grp.id !== currentGrp)
        }
        dispatch(hcrActions.hcrUpdateSubdataSets(payload))
        reset()
    }

    return (
        <CanvasGroupProperties
            getLabel={getLabel}
            fromAdvanceComp={true}
            fields={fields}
            calculations={calculations}
            groupOptions={groups}
            handleChange={onChange}
            handleDelete={onDelete}
            selectedGroup={currentGrp}
            {
            ...{
                dispatch,
                SelectField,
                InputNumberFiled,
            }
            }
        />
    )
}

export default GroupProperties