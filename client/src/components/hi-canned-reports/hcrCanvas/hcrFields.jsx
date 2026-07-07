import { Dropdown, Menu, Space } from "antd";
import { DownOutlined } from "@ant-design/icons";
import { useDrag } from "react-dnd";
import { useSelector } from "react-redux";
import { useEffect, useState } from "react";

export const FieldItem = ({ field }) => {
    const canvasMargin = useSelector(state => state.cannedReports.present.hcrTabData.panes.find(pane => pane.key === state.cannedReports.present.hcrTabData.activeKey)?.canvasProperties?.margin || {});
    const [{ }, drag] = useDrag(() => {
        return {
            type: "customNodes",
            item: { record: field, canvasMargin, type: "customNodes" },
            collect: (monitor) => ({
                opacity: monitor.isDragging() ? 0.5 : 1,
            }),
        };
    }, [canvasMargin, field]);

    return <div ref={drag} style={{ fontSize: 12 }}>
        {field.name}
    </div>
}

const getFieldsMenu = ({ modifiedFields }) => {
    return <Menu selectedKeys={['dummy']} selectable={true}>
        {modifiedFields?.map(field => {
            return <Menu.Item
                style={{ padding: "1px 12px" }}
                onClick={() => {
                    // dispatch(hcrActions.handleEditingDsPaneItem({ dataSourcePane: record.dataSourcePane, itemId, key: reqKey, value: conn.id, setForSql }));
                }}
                key={field.name}
                value={field.name}
            >
                <FieldItem field={field} />
            </Menu.Item>
        })}
    </Menu>
}

const HCRFields = ({ selectedQuery, modifiedFields, setModifiedFields }) => {
    const { id, executeQueryData } = selectedQuery || {};
    const { field } = executeQueryData || {}

    useEffect(() => {
        const reqArr = (field || [])?.map(ele => {
            return {
                // parentKey: 'hcr_fields',
                name: ele.name,
                width: 100,
                height: 40,
                label: `$F{${ele.name}}`,
                renderKey: 'text',
                isLeaf: true,
                zIndex: 10,
                type: 'queryField',
                category: 'text',
                repeat: 'rd',
                borders: {},
                padding: {},
                // fontSize: 10,
                backendDataType: ele.clazz
            }
        }) || [];
        setModifiedFields(reqArr);
    }, [id, field])

    return <Dropdown
        trigger={['click']}
        overlay={getFieldsMenu({ modifiedFields })}
    >
        <Space style={{ fontSize: 12 }}>Fields <DownOutlined /></Space>
    </Dropdown>

}

export default HCRFields;