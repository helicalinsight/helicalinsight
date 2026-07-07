import { CheckOutlined, CloseOutlined, DeleteOutlined, EditOutlined } from '@ant-design/icons';
import { Button, Col, Collapse, Drawer, Input, Row, Space, Tooltip } from 'antd';
import { useEffect, useState } from 'react';
import { toCapitalize } from '../../../../../utils/text-utils';
import FieldSelector from '../../fieldSelector';

const PropertiesConfigurator = (props = {}) => {
    const {
        visible,
        title = "",
        panelLabel = "Parameters Mapping",
        size = "default",
        onClose = () => { },
        onEditProperty = () => { },
        onDeleteProperty = () => { },
        data = [],
        displayCols = ["parameter", "expression"],
        disabledCols = [],
        colTypes = { parameter: "input", expression: "expressionEditor" },
        fieldSelectorOptions = [],
        propertyValueKeys = ["id", "parameter", "expression"]
    } = props || {}

    const initialEditProperty = propertyValueKeys.reduce((prev, curr) => ({ ...prev, [curr]: "" }), {});

    const [editProperty, setEditProperty] = useState(initialEditProperty);
    const [properties, setProperties] = useState(data);

    const headerRow = (
        <Row gutter={16} style={{ width: "100%" }}>
            {displayCols.map((col, index, _arr) => {
                return (
                    <Col span={Math.floor(21 / _arr.length)}>
                        <span className="property-label">{toCapitalize(col)}</span>
                    </Col>
                )
            })}
        </Row>
    )

    const getRow = ({
        item = {},
        onChange = () => { },
        allowDelete = true,
        editProperty,
        handleEdit = () => { },
        onDeleteProperty = () => { },
        handleEditDone = () => { },
        hadleCancelEdit = () => { }
    }) => {
        let { id } = item || {}
        const disabled = editProperty.id !== id
        return (
            <Row key={id} gutter={16} style={{ width: "100%", marginBottom: 8 }}>
                {displayCols.map((col, index, _arr) => {
                    function getCol(col) {
                        return (
                            <Tooltip title={item[col]}>
                                <div className="hcr-export-property-disabled">
                                    <span>{item[col]}</span>
                                </div>
                            </Tooltip>
                        )
                    }
                    if (disabledCols.includes(col)) {
                        return (
                            <Col span={Math.floor(21 / _arr.length)}>
                                {getCol(col)}
                            </Col>
                        )
                    }

                    return (
                        <Col span={Math.floor(21 / _arr.length)}>
                            {!disabled ?
                                {
                                    input: (
                                        <Input
                                            width={"100%"}
                                            value={editProperty[col]}
                                            onChange={(e) => onChange(col, e.target.value)}
                                            placeholder={!disabled ? toCapitalize(col) : ""}
                                            style={{ fontSize: 12 }}
                                        />
                                    ),
                                    expressionEditor: (
                                        <FieldSelector
                                            onChange={(valueObj) => {
                                                const { value } = valueObj || {}
                                                onChange(col, value)
                                            }}
                                            options={fieldSelectorOptions}
                                            value={editProperty[col]}
                                        />
                                    )
                                }[colTypes[col]]
                                :
                                getCol(col)
                            }
                        </Col>
                    )
                })}
                <Col span={3}>
                    {
                        disabled ?
                            <Space>
                                <Button
                                    size="small"
                                    icon={<EditOutlined />}
                                    onClick={() => handleEdit(id)}
                                />
                                <Button
                                    size="small"
                                    icon={<DeleteOutlined />}
                                    onClick={() => onDeleteProperty(id)}
                                />
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

    const handleEdit = (id) => {
        const property = properties.find((p) => p.id === id);
        setEditProperty(property);
    }

    const handlePrpopertyChange = (key, value) => {
        setEditProperty((prev) => ({
            ...prev,
            [key]: value
        }))
    }

    const handleEditDone = () => {
        onEditProperty(editProperty);
        setEditProperty(initialEditProperty);
    }

    const hadleCancelEdit = () => {
        setEditProperty(initialEditProperty);
    }

    useEffect(() => {
        if (data) {
            setProperties(data);
        }
    }, [data])


    return (
        <Drawer
            title={title}
            placement="right"
            onClose={onClose}
            open={visible}
            size={size}
        >
            <Collapse size={'small'} className="canvas-property-collapse" defaultActiveKey={["properties"]}>
                <Collapse.Panel
                    header={
                        <span className="canvas-property-title">
                            {panelLabel} {`(${properties?.length})`}
                        </span>
                    }
                    key={'properties'}
                >
                    {properties?.length ?
                        <>
                            {headerRow}
                            {properties?.map((item) => {
                                return getRow({
                                    item,
                                    onChange: handlePrpopertyChange,
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
        </Drawer>
    )
}

export default PropertiesConfigurator