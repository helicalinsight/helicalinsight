import { InfoCircleOutlined } from '@ant-design/icons'
import { Button, Collapse, Space, Tooltip } from 'antd'
import { useEffect, useState } from 'react'
import useHCRCascadeSelector from '../../../../../hooks/useHCRCascadeSelector'
import { hcrActions } from '../../../../../redux/actions'
import FieldSelector from '../../fieldSelector'
import { getMappedParameters } from '../utils'
import PropertiesConfigurator from './propertiesConfigurator'

const SubDatasetProperties = (props = {}) => {
    const {
        dispatch,
        EditorPanels,
        onClose,
        selectedSubDS
    } = props || {}

    const { SelectField } = EditorPanels || {}
    const { parameters = [], id: subDSId } = selectedSubDS || {}
    const [parameter, setParameter] = useState({
        paramId: null,
        expression: ""
    })
    const [disabled, setDisabled] = useState(true)
    const { getCascaderOptions } = useHCRCascadeSelector({ node: {} })
    const cascaderOptions = getCascaderOptions()

    const [drawerVisible, setDrawerVisible] = useState(false);
    const mappedParameters = parameters.filter(param => param.mappingExpression)

    const onChange = (key, value) => {
        setDisabled(false)
        setParameter((prev) => ({ ...prev, [key]: value }))
    }

    const updateParameterMapping = (parameter = {}) => {
        const payload = {
            actionType: "updateParameterMapping",
            id: subDSId,
            parameter
        }
        dispatch(hcrActions.hcrUpdateSubdataSets(payload))

    }

    const handleSaveClick = () => {
        updateParameterMapping(parameter)
        setParameter({
            parameterId: null,
            expression: ""
        })
        setDisabled(true)
    }

    const handleDrawerClose = () => {
        setDrawerVisible(false);
    }

    const handleEditParameterMapping = (parameter = {}) => {
        const { id, expression } = parameter;
        console.log({ parameter })
        updateParameterMapping({ paramId: id, expression })
    }

    const handleDeleteParameterMapping = (id) => {
        updateParameterMapping({ paramId: id, expression: "" })
    }

    useEffect(() => {
        if (parameter.paramId) {
            const param = parameters.find(({ id }) => id === parameter.paramId)
            setParameter((prev) => ({ ...prev, expression: param.mappingExpression }))
        }
    }, [parameter.paramId])

    return (
        <Collapse
            defaultActiveKey={"parametersMapping"}
            size={"small"}
            className="node-property-collapse"
        >
            <Collapse.Panel
                header={<span className="node-property-title">Parameters Mapping
                    <Tooltip
                        placement='topRight'
                        title="Maps a value from the parent report dataset context (parameter, field, variable, constant, or expression) to the target parameter. The mapped value is evaluated at runtime and overrides the target parameter's default value."
                    >
                        <InfoCircleOutlined style={{ cursor: "pointer", marginLeft: 8 }} />
                    </Tooltip>
                </span>}
                key={"parametersMapping"}
                data-testid="hcr-sub-ds-parameters-collapse"
            >

                <SelectField
                    label={<div className="property-label">Parameters</div>}
                    value={parameter.paramId}
                    options={parameters.filter(param => !param.mappingExpression).map(({ name, id }) => ({ label: name, value: id }))}
                    onChange={(value) => {
                        onChange("paramId", value)
                    }}
                    width={248}
                />
                {parameter.paramId && <div>
                    <div className="property-label" >Expression</div>
                    <FieldSelector
                        onChange={(valueObj) => {
                            const { value } = valueObj || {}
                            onChange("expression", value)
                        }}
                        options={cascaderOptions}
                        value={parameter.expression}

                    />
                </div>}

                <Space align="end">
                    <Button
                        disabled={disabled}
                        type="link"
                        onClick={handleSaveClick}
                    >
                        Save
                    </Button>
                </Space>
                {mappedParameters.length ?
                    (
                        <div className="property-label" style={{ cursor: 'pointer' }} onClick={() => setDrawerVisible(true)}>
                            {mappedParameters.length} {mappedParameters.length === 1 ? "parameter" : "parameters"} mapped (click to edit)
                        </div>
                    )
                    : null}

                <PropertiesConfigurator
                    title={"Parameters Mapping"}
                    visible={drawerVisible}
                    onClose={handleDrawerClose}
                    data={getMappedParameters(mappedParameters)}
                    size="large"
                    onEditProperty={handleEditParameterMapping}
                    onDeleteProperty={handleDeleteParameterMapping}
                    panelLabel={"Parameters Mapping"}
                    displayCols={["parameter", "expression"]}
                    disabledCols={["parameter"]}
                    colTypes={{ parameter: "input", expression: "expressionEditor" }}
                    fieldSelectorOptions={cascaderOptions}
                    propertyValueKeys={["id", "parameter", "expression"]}
                />
            </Collapse.Panel>
        </Collapse >
    )
}

export default SubDatasetProperties