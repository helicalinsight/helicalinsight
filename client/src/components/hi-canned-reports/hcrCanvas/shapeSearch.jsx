import { Dropdown, Menu, Space } from "antd";
import { DownOutlined } from "@ant-design/icons";
import { useDrag } from "react-dnd";
import { useSelector } from "react-redux";
import { useEffect, useState } from "react";

const FieldItem = ({ field }) => {
    const canvasMargin = useSelector(state => state.cannedReports.present.hcrTabData.panes.find(pane => pane.key === state.cannedReports.present.hcrTabData.activeKey)?.canvasProperties?.margin || {});
    const [{ }, drag] = useDrag(() => {
        return {
            type: "customNodes",
            item: { record: field, canvasMargin },
            collect: (monitor) => ({
                opacity: monitor.isDragging() ? 0.5 : 1,
            }),
        };
    }, [canvasMargin]);

    return <div ref={drag} style={{ fontSize: 12 }}>
        {field.name}
    </div>
}

const getFieldsMenu = ({ searchedList }) => {
    return <Menu selectedKeys={['dummy']} selectable={true}>
        {searchedList?.map(field => {
            return <Menu.Item
                style={{ padding: "1px 12px" }}
                key={field.name}
                value={field.name}
            >
                <FieldItem field={field} />
            </Menu.Item>
        })}
        {(searchedList?.length === 0) && <Menu.Item
            style={{ padding: "1px 12px" }}
            key={'fileds-empty'}
        >
            <span><i style={{ fontSize: 12 }}>No Items To Display</i></span>
        </Menu.Item>}
    </Menu>
}

const HCRShapeSearch = ({ shapeSearch = '', modifiedBuiltVariables = [], modifiedFields = [] }) => {
    const [searchedList, setSearchedList] = useState([]);
    const [open, setOpen] = useState(true);

    useEffect(() => {
        const wholeList = [...modifiedBuiltVariables, ...modifiedFields]
        const reqList = wholeList.filter(ele => ele?.name?.toLowerCase()?.includes(shapeSearch?.toLowerCase())) || []
        setSearchedList(reqList);
    }, [shapeSearch, modifiedBuiltVariables, modifiedFields])

    return <Dropdown
        trigger={['click']}
        overlay={getFieldsMenu({ searchedList })}
        visible={open}
        onVisibleChange={(isOpen) => {
            setOpen(isOpen);
        }}
    >
        <Space style={{ fontSize: 12 }}>Search Results <DownOutlined /></Space>
    </Dropdown>

}

export default HCRShapeSearch;