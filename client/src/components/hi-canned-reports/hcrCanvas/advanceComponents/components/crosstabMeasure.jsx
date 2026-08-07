import { Button, Collapse, Divider, Space } from 'antd'
import { useEffect, useState } from 'react'
import useHCRCascadeSelector from '../../../../../hooks/useHCRCascadeSelector'
import { hcrActions } from '../../../../../redux/actions'
import { hcrCrosstabMeasuresAggregateFns } from '../../../hcr-constants'
import FieldSelector from '../../fieldSelector'
import { getCategoryClassNames } from '../utils'

const CrosstabMeasure = (props = {}) => {
    const {
        data = {},
        EditorPanels,
        dispatch,
        selectedMeasure = [],
        classNames = {},
        selectedSubDS
    } = props || {}
    const { InputFiled, SelectField } = EditorPanels || {}
    const { calculations = [], fields = [], id: subDSId, groups = [], parameters = [] } = selectedSubDS || {}

    const { config = {}, id } = data || {}
    const { measures = [] } = config || {}
    const categoryClassNames = getCategoryClassNames(classNames);
    const { getCascaderOptions } = useHCRCascadeSelector({ node: {}, fields, calculations, parameters })
    const cascaderOptions = getCascaderOptions()

    const currentMeasure = measures.find((m) => m.id === selectedMeasure[0]) || {}

    const { label, className, measureExpression, id: measureId, calculation } = currentMeasure || {}

    const [measureState, setMeasureState] = useState({
        label: label,
        calculation: calculation,
        measureExpression: measureExpression,
        className: className,
    })
    const [disabled, setDisabled] = useState(false)

    const handleChange = (key, value) => {
        setMeasureState((prev) => ({ ...prev, [key]: value }))
        setDisabled(false)
    }

    const handleClassNameChange = (valueObj = {}) => {
        const { value } = valueObj || {}
        handleChange("className", value)
    }

    const handleExpressionChange = (valueObj = {}) => {
        const { value } = valueObj || {}
        handleChange("measureExpression", value)
    }

    const handleSaveClick = () => {
        setDisabled(true)
        dispatch(hcrActions.hcrUpdateCrosstabComponent({
            id,
            actionType: "updateMeasure",
            properties: measureState,
            measureId
        }))
    }


    useEffect(() => {
        setMeasureState({
            label: label,
            calculation: calculation,
            measureExpression: measureExpression,
            className: className
        })
    }, [label, className, measureExpression, calculation])

    return (
        <Collapse
            defaultActiveKey={"measure"}
            size={"small"}
            className="node-property-collapse"
        >
            <Collapse.Panel
                header={<span className="node-property-title">Measure {label}</span>}
                key={"measure"}
            >
                <div className="property-group-wrapper" >
                    <InputFiled
                        label={<div className="property-label" >Name</div>}
                        value={measureState.label}
                        onChange={(value) => {
                            handleChange("label", value)
                        }}
                    />
                    <SelectField
                        label={<div className="property-label" >Calculation</div>}
                        value={measureState.calculation}
                        options={hcrCrosstabMeasuresAggregateFns.map((aggregate) => ({
                            label: aggregate,
                            value: aggregate,
                        }))}
                        onChange={(value) => {
                            handleChange("calculation", value)
                        }}
                        width={248}
                    />
                    <div>
                        <div className="property-label" >Value Expression</div>
                        <FieldSelector
                            onChange={handleExpressionChange}
                            value={measureState.measureExpression}
                            options={cascaderOptions}
                            appendValue={false}
                        />
                    </div>
                    <div>
                        <div className="property-label" >Value Class</div>
                        <FieldSelector
                            onChange={handleClassNameChange}
                            value={measureState.className}
                            options={categoryClassNames}
                            appendValue={false}
                        />
                    </div>
                </div>

                <Divider className="group-divider" />
                <Space align="end">
                    <Button
                        disabled={disabled}
                        type="link"
                        onClick={handleSaveClick}
                    >
                        Save
                    </Button>
                </Space>
            </Collapse.Panel>
        </Collapse >
    )
}

export default CrosstabMeasure