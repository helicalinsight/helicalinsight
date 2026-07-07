import {
    CheckOutlined,
    CloseOutlined,
    DeleteOutlined,
    EditOutlined,
} from "@ant-design/icons";
import { Button, Col, Collapse, Drawer, Input, Row, Space, Tooltip } from 'antd';
import { useEffect, useState } from "react";

const HCRExportPropertiesDrawer = (props = {}) => {
    const {
        visible,
        title = "",
        size = "default",
        onClose = {} = () => { },
        data = [],
        // emptyText = "No export properties configured",
        onEditProperty = () => { },
        onDeleteProperty = () => { },
        showSystemDefined = true,
        labelPanelOne = "User Defined",
    } = props || {};
    const [editId, setEditId] = useState(null);
    let initialEditProperty = {
        key: "",
        value: "",
        alias: ""
    }
    const [editProperty, setEditProperty] = useState(initialEditProperty);

    const getSystemDefinedProperties = (data) => {
        if (!data?.length) return [];
        return data.filter((p) => p.default);
    }

    const getUserDefinedProperties = (data) => {
        if (!data?.length) return [];
        return data.filter((p) => !p.default);
    }

    const [systemDefinedProperties, setSystemDefinedProperties] = useState(getSystemDefinedProperties(data))
    const [userDefinedProperties, setUserDefinedProperties] = useState(getUserDefinedProperties(data))

    const handleEdit = (id, key, value, alias) => {
        setEditId(id);
        setEditProperty({ key, value, alias });
    }
    const handlePrpopertyChange = (key, value) => {
        setEditProperty((prev) => ({
            ...prev,
            [key]: value
        }))
    }

    const handleEditDone = () => {
        onEditProperty(editId, {
            key: editProperty.key || "",
            value: editProperty.value || "",
            alias: editProperty.alias || ""
        })
        setEditProperty(initialEditProperty);
        setEditId(null);
    }

    const hadleCancelEdit = () => {
        setEditProperty(initialEditProperty);
        setEditId(null);
    }

    useEffect(() => {
        if (data) {
            setSystemDefinedProperties(getSystemDefinedProperties(data))
            setUserDefinedProperties(getUserDefinedProperties(data))
        }
    }, [data])

    const getRow = ({
        item,
        onChange = () => { },
        allowDelete = true,
        editId,
        editProperty,
        handleEdit = () => { },
        onDeleteProperty = () => { },
        handleEditDone = () => { },
        hadleCancelEdit = () => { }
    }) => {
        let { id, key, value, alias, description } = item
        const isEditing = editId === id;
        if (isEditing) {
            key = editProperty.key;
            value = editProperty.value;
            alias = editProperty.alias
        }
        const disabled = !isEditing;
        return (
            <Row key={id} gutter={16} style={{ width: "100%", marginBottom: 8 }}>
                <Col span={7}>
                    {!disabled ? <Input
                        width={"100%"}
                        value={alias}
                        onChange={(e) => onChange("alias", e.target.value)}
                        placeholder={!disabled ? "Property" : ""}
                        // disabled={disabled}
                        style={{ fontSize: 12 }}
                    />
                        :
                        <Tooltip title={alias}>
                            <div className="hcr-export-property-disabled">
                                <span>{alias}&nbsp;</span>
                            </div>
                        </Tooltip>
                    }
                </Col>
                <Col span={7}>
                    {!disabled ? <Input
                        width={"100%"}
                        value={key}
                        onChange={(e) => onChange("key", e.target.value)}
                        placeholder={!disabled ? "Property Name" : ""}
                        // disabled={disabled}
                        style={{ fontSize: 12 }}
                    />
                        :
                        <Tooltip title={description || key}>
                            <div className="hcr-export-property-disabled">
                                <span>{key}&nbsp;</span>
                            </div>
                        </Tooltip>
                    }
                </Col>
                <Col span={7}>
                    {!disabled ? <Input
                        width={"100%"}
                        value={value}
                        onChange={(e) => onChange("value", e.target.value)}
                        placeholder={!disabled ? "Value" : ""}
                        // disabled={disabled}
                        style={{ fontSize: 12 }}
                    />
                        :
                        <Tooltip title={value}>
                            <div className="hcr-export-property-disabled">
                                <span>{value}&nbsp;</span>
                            </div>
                        </Tooltip>
                    }
                </Col>
                <Col span={3}>
                    {
                        disabled ?
                            <Space>
                                <Button
                                    size="small"
                                    icon={<EditOutlined />}
                                    onClick={() => handleEdit(id, key, value, alias)}
                                />
                                {
                                    allowDelete ? <Button
                                        size="small"
                                        icon={<DeleteOutlined />}
                                        onClick={() => onDeleteProperty(id)}
                                    /> : null
                                }
                            </Space>
                            :
                            <Space>
                                <Button
                                    type="primary"
                                    size="small"
                                    icon={<CheckOutlined />}
                                    onClick={() => handleEditDone()}
                                />
                                <Button
                                    size="small"
                                    icon={<CloseOutlined />}
                                    onClick={() => hadleCancelEdit()}
                                />
                            </Space>
                    }
                </Col>
            </Row>
        )
    }

    const headerRow = (
        <Row gutter={16} style={{ width: "100%", }}>
            <Col span={7}>
                <span className="property-label">Property</span>
            </Col>
            <Col span={7}>
                <span className="property-label">Property Name</span>
            </Col>
            <Col span={7}>
                <span className="property-label">Property Value</span>
            </Col>
        </Row>
    )

    useEffect(() => {
        if (!visible) setEditId(null);
    }, [visible])

    return (
        <Drawer
            title={title}
            placement="right"
            onClose={onClose}
            open={visible}
            size={size}
        >
            <Collapse size={'small'} className="canvas-property-collapse">
                <Collapse.Panel header={
                    <span className="canvas-property-title">
                        {labelPanelOne} {`(${userDefinedProperties?.length})`}
                    </span>
                }
                    key={'user_defined_properties'}
                >
                    {userDefinedProperties?.length ?
                        <>
                            {headerRow}
                            {userDefinedProperties?.map((item) => {
                                return getRow({
                                    item,
                                    onChange: handlePrpopertyChange,
                                    editId,
                                    editProperty,
                                    handleEdit,
                                    onDeleteProperty,
                                    handleEditDone,
                                    hadleCancelEdit
                                })
                            })}
                        </>
                        : null}
                </Collapse.Panel>
            </Collapse>
            {showSystemDefined && <Collapse size={'small'} className="canvas-property-collapse">
                <Collapse.Panel header={
                    <span className="canvas-property-title">
                        Default (System Defined) {`(${systemDefinedProperties?.length})`}
                    </span>
                }
                    key={'system_defined_properties'}
                >
                    {systemDefinedProperties?.length ?
                        <>
                            {headerRow}
                            {systemDefinedProperties?.map((item) => {
                                return getRow({
                                    item,
                                    onChange: handlePrpopertyChange,
                                    editId,
                                    editProperty,
                                    handleEdit,
                                    onDeleteProperty,
                                    handleEditDone,
                                    hadleCancelEdit,
                                    allowDelete: false
                                })
                            })}
                        </>
                        : null}

                </Collapse.Panel>
            </Collapse>}
        </Drawer>
    )
}

export default HCRExportPropertiesDrawer