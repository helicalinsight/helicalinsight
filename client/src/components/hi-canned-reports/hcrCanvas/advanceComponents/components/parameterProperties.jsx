import { Button, Collapse, Divider, Space } from 'antd';
import { useEffect, useState } from 'react';
import useHCRCascadeSelector from '../../../../../hooks/useHCRCascadeSelector';
import { hcrActions } from '../../../../../redux/actions';
import { getCategoryClassNames } from '../utils';
import FieldSelector from '../../fieldSelector';
import { NUMERIC_CLASSNAMES } from '../contants';

const ParameterProperties = (props = {}) => {
    const {
        dispatch,
        EditorPanels,
        selectedParameter,
        onClose,
        selectedSubDS,
        tableData: { id },
        classNames = {},
    } = props || {};

    const { InputFiled, SelectField, InputNumberFiled } = EditorPanels || {}
    const { calculations = [], fields = [], id: subDSId, groups = [], parameters = [] } = selectedSubDS || {}
    const currentParam = selectedParameter[0] || ""

    const { name: pName = "", type: className = "", canvasValues: { defaultValue: pValue = "" } = {}, ...restParam } = parameters.find((param) => param.id === currentParam) || {};

    const [paramState, setParamState] = useState({
        name: pName,
        type: className,
        defaultValue: pValue,
        open: "'",
        close: "'",
    })
    const categoryClassNames = getCategoryClassNames(classNames);

    const [disabled, setDisabled] = useState(false)
    const { getCascaderOptions } = useHCRCascadeSelector({ node: {}, fields, calculations, parameters })
    const cascaderOptions = getCascaderOptions()

    const handleChange = (key, value, otherProperties = {}) => {
        setParamState((prevState) => ({ ...prevState, [key]: value, ...otherProperties }))
        setDisabled(false)
    }

    const handleSaveClick = () => {
        setDisabled(true)
        const payload = {
            actionType: "updateParameters",
            id: subDSId,
            parameters: parameters.map((param) => {
                if (param.id === currentParam) {
                    return {
                        ...param,
                        name: paramState.name,
                        type: paramState.type,
                        canvasValues: {
                            ...param.canvasValues,
                            defaultValue: paramState.defaultValue,
                            open: paramState.open,
                            close: paramState.close
                        }
                    }
                }
                return param
            })
        }
        dispatch(hcrActions.hcrUpdateSubdataSets(payload))
    }

    const handleClassNameChange = (valueObj = {}) => {
        const { value, label } = valueObj || {}
        const isNumeric = NUMERIC_CLASSNAMES.includes(label);
        const quotes = isNumeric ? "" : "'"
        handleChange("type", value, { open: quotes, close: quotes })
    }

    useEffect(() => {
        setParamState({
            name: pName,
            type: className,
            defaultValue: pValue
        })
    }, [pName, className, pValue])

    return (
        <Collapse
            defaultActiveKey={"field"}
            size={"small"}
            className="node-property-collapse"
        >
            <Collapse.Panel
                header={<span className="node-property-title">Parameter</span>}
                key={"field"}
                data-testid="hcr-outline-ds-node-field-collapse"
            >
                <div className="property-group-wrapper" >
                    <InputFiled
                        label={<div className="property-label" >Name</div>}
                        value={paramState.name}
                        onChange={(value) => {
                            handleChange("name", value)
                        }}
                    />
                    <div>
                        <div className="property-label" >Class Name</div>
                        <FieldSelector
                            onChange={handleClassNameChange}
                            value={paramState.type}
                            options={categoryClassNames}
                            appendValue={false}
                        />
                    </div>
                    <InputFiled
                        label={<div className="property-label" >Value</div>}
                        value={paramState.defaultValue}
                        onChange={(value) => {
                            handleChange("defaultValue", value)
                        }}
                    />
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

export default ParameterProperties