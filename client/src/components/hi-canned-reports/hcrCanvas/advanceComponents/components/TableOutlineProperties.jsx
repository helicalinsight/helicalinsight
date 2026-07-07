import { Collapse } from 'antd'
import React from 'react'
import SubDatasetProperties from './subDatasetProperties'
const TableOutlineProperties = (props = {}) => {
    const { onChange = () => { }, tableData = {}, EditorPanels, queriesMenu = [] } = props || {}
    const { id, selectedQueryID } = tableData || {}
    const { InputFiled, SelectField } = EditorPanels || {}

    return (
        <div>
            <Collapse
                defaultActiveKey={"table"}
                size={"small"}
                className="node-property-collapse"
            >
                <Collapse.Panel
                    header={<span className="node-property-title">Table</span>}
                    key={"table"}
                    data-testid="hcr-outline-ds-node-table-collapse"
                >
                    <div className="property-group-wrapper" >
                        <SelectField
                            label={<div className="property-label" >Query</div>}
                            value={selectedQueryID}
                            options={queriesMenu.map((query) => ({
                                label: query.name,
                                value: query.id,
                            }))}
                            onChange={(value) => {
                                onChange({ key: "selectedQueryID", value })
                            }}
                            width={248}
                        />
                    </div>

                </Collapse.Panel>
            </Collapse>
            <SubDatasetProperties {...props} />
        </div>

    )
}

export default TableOutlineProperties