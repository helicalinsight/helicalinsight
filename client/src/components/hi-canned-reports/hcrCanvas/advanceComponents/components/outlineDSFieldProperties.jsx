import { Collapse, Input } from 'antd'
import { useEffect, useState } from 'react'
import { useDispatch } from 'react-redux'
import notify from '../../../../hi-notifications/notify'

const OutlineDSFieldProperties = (props = {}) => {
    const {
        EditorPanels,
        outlineDsSelectedField,
        onChange = () => { },
        tableData = {},
        classNames = {},
        dispatch,
        selectedSubDS = {}
    } = props || {}
    const { fields = [], id: subDSId } = selectedSubDS || {}
    const { InputFiled, SelectField } = EditorPanels || {}
    const [name, setName] = useState("")
    const [editing, setEditing] = useState(false)
    const field = fields.find((f) => f.id === outlineDsSelectedField);
    const { name: fName = "", clazz = "" } = field || {}

    const handleChange = (key, value) => {
        setEditing(true)
        if (key === "name") setName(value)
    }
    const isFieldNameExists = (name) => fields.some((f) => f.name === name.trimEnd() && f.id !== outlineDsSelectedField)
    const isBlank = (value) => !value || value.trim() === "";

    const handleStateValueChange = (key, value) => {
        function update() {
            onChange({
                type: "field-item",
                id: outlineDsSelectedField,
                value: {
                    ...field,
                    [key]: value
                },
                subDSId
            })
        }
        if (isBlank(value)) {
            notify(dispatch).warning({
                message: "Field name cannot be empty",
                type: "Front End",
            });
            return;
        }
        if (key === "name" && editing) {
            if (!isFieldNameExists(value)) {
                update()
            } else {
                notify(dispatch).warning({
                    message: "Field name already exists",
                    type: "Front End",
                });
            }
            setEditing(false)
            return;
        }
        update()
    }

    useEffect(() => {
        setName(fName)
    }, [fName])

    if (!field) return null;

    return (
        <Collapse
            defaultActiveKey={"field"}
            size={"small"}
            className="node-property-collapse"
        >
            <Collapse.Panel
                header={<span className="node-property-title">Field</span>}
                key={"field"}
                data-testid="hcr-outline-ds-node-field-collapse"
            >
                <div className="property-group-wrapper" >
                    <div>
                        <div className="property-label" >Name</div>
                        <Input
                            value={name}
                            onChange={(e) => {
                                const { value } = e.target || {}
                                handleChange("name", value)
                            }}
                            onBlur={() => handleStateValueChange("name", name)}
                            data-testid="field-name-input"
                        />
                    </div>
                    {/* <SelectField
                        label={<div className="property-label" >Class Name</div>}
                        value={clazz}
                        options={Object.entries(classNames || {}).map(([label, value]) => {
                            return { label, value }
                        })}
                        onChange={(value) => {
                            handleStateValueChange("clazz", value)
                        }}
                        width={248}
                    /> */}
                </div>
            </Collapse.Panel>
        </Collapse>
    )
}

export default OutlineDSFieldProperties