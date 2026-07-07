import { Collapse } from 'antd'
import React, { useEffect } from 'react'
import CanvasCalculations from '../../canvasProperty/hcrCanvasCalculations'
import { getLabel } from '../../hcrCanvasPaneHelperMethods'
import { useSelector } from 'react-redux'
import { hcrActions } from '../../../../../redux/actions'
import notify from '../../../../hi-notifications/notify'

const CalculationProperties = (props = {}) => {
    const { EditorPanels, tableData: { id }, dispatch, selectedCalculation = [], onClose = () => { }, selectedSubDS = {} } = props || {}
    const { InputFiled, SelectField } = EditorPanels || {}
    const { calculations = [], fields = [], id: subDSId, groups = [] } = selectedSubDS || {}
    const designerProperties = useSelector((state) => state.cannedReports.present?.hCROldConfigurations?.HCR?.HCR?.designerProperties || {});
    const { variables, calculations: calculationOptions, resetType, incrementType, } = designerProperties;
    const { classNames } = variables || {};
    const currentCalculation = selectedCalculation?.[0] ?? null;
    const editValues = calculations.find((cal) => cal.id === currentCalculation) || {}
    const Notify = notify(dispatch);

    const clearValues = () => {
        dispatch(hcrActions.setHcrCanvasCalculations({ clearKeyValuePairs: true }));
    }

    const reset = () => {
        dispatch(hcrActions.hcrUpdateCanvasTabComponent({
            actionType: "clearSelection", id,
        }))
        onClose()
        clearValues()
    }

    const handleSaveCalculation = (values) => {
        const isCalculationPresent = calculations.some((cal) => cal.id === values.id);
        let payload = {}
        if (isCalculationPresent) {
            payload = {
                actionType: "updateCalculations",
                id: subDSId,
                calculations: calculations.map((cal) => {
                    if (cal.id === values.id) {
                        return { ...cal, ...values }
                    } else {
                        return cal
                    }
                })
            }
        } else {
            payload = {
                actionType: "updateCalculations",
                id: subDSId,
                calculations: [...calculations, values]
            }
        }
        dispatch(hcrActions.hcrUpdateSubdataSets(payload))
        reset()
        Notify.success({ message: 'Calculation updated succesfully.' });
    }

    const handleDeleteCalculation = (calId) => {
        dispatch(hcrActions.hcrUpdateSubdataSets({
            actionType: "updateCalculations",
            id: subDSId,
            calculations: calculations.filter((cal) => cal.id !== calId)
        }))
        reset()
        Notify.success({ message: 'Calculation deleted succesfully.' });
    }

    useEffect(() => {
        return () => {
            clearValues()
        }
    }, [])
    return (
        <Collapse size={"small"} className="canvas-property-collapse" defaultActiveKey={"calculations"}>
            <Collapse.Panel
                header={<span className="canvas-property-title">Calculations</span>}
                key={"calculations"}
            >
                <CanvasCalculations
                    getLabel={getLabel}
                    InputFiled={InputFiled}
                    SelectField={SelectField}
                    dispatch={dispatch}
                    classNames={classNames}
                    calculationOptions={calculationOptions}
                    resetType={resetType}
                    incrementType={incrementType}
                    fromAdvanceComp={true}
                    saveCB={handleSaveCalculation}
                    deleteCB={handleDeleteCalculation}
                    editValues={editValues}
                    fields={fields}
                    groupOptions={groups}
                    calculationsFromAdvanceComp={calculations}
                />
            </Collapse.Panel>
        </Collapse>
    )
}

export default CalculationProperties