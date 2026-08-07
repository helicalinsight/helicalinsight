import { Button, Collapse, Divider, Space } from 'antd'
import { useEffect, useState } from 'react'
import useHCRCascadeSelector from '../../../../../hooks/useHCRCascadeSelector'
import { hcrActions } from '../../../../../redux/actions'
import FieldSelector from '../../fieldSelector'
import { getCategoryClassNames, getCTSelectedGroup } from '../utils'

const CrosstabGroup = (props = {}) => {
  const {
    data = {},
    EditorPanels,
    dispatch,
    selectedGroup = [],
    classNames = {},
    selectedSubDS
  } = props || {}
  const { InputFiled, SelectField } = EditorPanels || {}
  const { calculations = [], fields = [], id: subDSId, groups = [], parameters = [] } = selectedSubDS || {}

  const { config = {}, id } = data || {}
  const { columnGroups = [], rowGroups = [] } = config || {}
  const categoryClassNames = getCategoryClassNames(classNames);
  const { getCascaderOptions } = useHCRCascadeSelector({ node: {}, fields, calculations, parameters })
  const cascaderOptions = getCascaderOptions()

  const currentGroup = getCTSelectedGroup(data, selectedGroup[0])

  const { label, className, expression, id: groupId, calculation, type } = currentGroup || {}

  const [groupState, setGroupState] = useState({
    label: label,
    expression: expression,
    className: className,
  })
  const [disabled, setDisabled] = useState(false)

  const handleChange = (key, value) => {
    setGroupState((prev) => ({ ...prev, [key]: value }))
    setDisabled(false)
  }

  const handleClassNameChange = (valueObj = {}) => {
    const { value } = valueObj || {}
    handleChange("className", value)
  }

  const handleExpressionChange = (valueObj = {}) => {
    const { value } = valueObj || {}
    handleChange("expression", value)
  }

  const handleSaveClick = () => {
    setDisabled(true)
    dispatch(hcrActions.hcrUpdateCrosstabComponent({
      id,
      actionType: "updateGroup",
      properties: groupState,
      groupId,
      groupType: type
    }))
  }


  const title = type === "columnGroup" ? "Column Group" : "Row Group"

  useEffect(() => {
    setGroupState({
      label: label,
      expression: expression,
      className: className,
    })
  }, [label, className, expression])

  return (
    <Collapse
      defaultActiveKey={"measure"}
      size={"small"}
      className="node-property-collapse"
    >
      <Collapse.Panel
        header={<span className="node-property-title">{title} {label}</span>}
        key={"measure"}
      >
        <div className="property-group-wrapper" >
          <InputFiled
            label={<div className="property-label" >Name</div>}
            value={groupState.label}
            onChange={(value) => {
              handleChange("label", value)
            }}
          />
          <div>
            <div className="property-label" >Expression</div>
            <FieldSelector
              onChange={handleExpressionChange}
              value={groupState.expression}
              options={cascaderOptions}
              appendValue={false}
            />
          </div>
          <div>
            <div className="property-label" >Value Class Name</div>
            <FieldSelector
              onChange={handleClassNameChange}
              value={groupState.className}
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

export default CrosstabGroup