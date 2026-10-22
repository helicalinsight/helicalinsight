import {
    CalendarOutlined,
    CheckOutlined,
    FileTextOutlined,
    NumberOutlined,
    SortAscendingOutlined,
    SortDescendingOutlined,
    VerticalAlignMiddleOutlined
} from '@ant-design/icons';
import { Dropdown, Input, Menu, Tag, Tooltip, Typography } from "antd";
import { useEffect, useRef, useState } from "react";
import { getFloatingType } from "../../../utils/filter-utils";
import { customFieldDataTypeList, dateTypes, orderBySubMenuOptions } from "../utils/constants";
import { useSelector } from 'react-redux';
const { SubMenu } = Menu;

const { Text } = Typography;

const dataTypeIcon = (dataType) => {
    if (["text", "other"].includes(dataType)) {
        return <FileTextOutlined />;
    }
    if (dataType === "numeric") {
        return <NumberOutlined />;
    }
    if (dataType === "boolean") {
        return <CheckOutlined />;
    }
    if (dateTypes.includes(dataType)) {
        return <CalendarOutlined />;
    }
    return <FileTextOutlined />;
};

const CustomColumnChip = ({
    alias = "",
    type,
    onAliasChange = () => { },
    onDataTypeChange = () => { },
    allowRename = true,
    allowDataTypeChange = true,
    allowMenu = false,
    tooltip = "",
    filter = {},
    columnType,
    onFilterChange = () => { }
}) => {
    const [editing, setEditing] = useState(false);
    const [dropdown, setDropdown] = useState(null)

    const [draft, setDraft] = useState(alias);
    const renameInput = useRef(null);
    const { dataType } = type || {};
    const { floatingType } = getFloatingType({ type });
    const backgroundColor = floatingType === "discrete" ? "#337ab7" : "#4CAF50";
    const displayName = alias?.trim() ? alias : "";
    let selectedKeys = [], orderByValue = "";

    if (filter) {
        const { mapping = {} } = filter || {}
        const {
            displayColumn,
            valueColumn,
            displayDBfunctionsDefinition,
            valueDBfunctionsDefinition,
            orderBy = {}
        } = mapping || {}
        if (columnType === "display") {
            if (displayDBfunctionsDefinition) {
                selectedKeys.push("functions")
            }
            selectedKeys.push(orderBy.display)
            orderByValue = orderBy.display
            if (displayColumn?.defaultFunction?.split(".")?.[2] === "groupBy") {
                selectedKeys.push("group-by")
            }
            if (displayColumn?.defaultFunction?.split(".")?.[2] === "aggregate") {
                selectedKeys.push(displayColumn.defaultFunction)
            }
        }
        if (columnType === "value") {
            if (valueDBfunctionsDefinition) {
                selectedKeys.push("functions")
            }
            selectedKeys.push(orderBy.value)
            orderByValue = orderBy.value
            if (valueColumn?.defaultFunction?.split(".")?.[2] === "groupBy") {
                selectedKeys.push("group-by")
            }
            if (valueColumn?.defaultFunction?.split(".")?.[2] === "aggregate") {
                selectedKeys.push(valueColumn.defaultFunction)
            }
        }
    }

    let functionsList = {
        groupBy: [],
        aggregate: [],
    };

    const { functions = {} } = useSelector((state) => {
        return state.hreport.present.reports.find((report) => report.active) || {};
    });

    Object.keys(functions).map((key) => {
        let group = key.split(".")[2];
        if (group === "orderBy") return null;
        functionsList[group].push({ key, value: functions[key] });
    });

    useEffect(() => {
        if (editing && renameInput.current) {
            renameInput.current.focus();
        }
    }, [editing]);

    useEffect(() => {
        if (!editing) {
            setDraft(alias);
        }
    }, [alias, editing]);

    const startEdit = (e) => {
        if (!allowRename) return;
        e?.stopPropagation?.();
        setDraft(alias);
        setEditing(true);
    };
    const cancelEdit = () => {
        setDraft(alias);
        setEditing(false);
    };
    const commitEdit = () => {
        setEditing(false);
        onAliasChange(draft.trim());
    };
    const handleAliasing = (event) => {
        switch (event.keyCode) {
            case 13:
                commitEdit();
                break;
            case 27:
                cancelEdit();
                break;
            default:
                break;
        }
    };

    const handleDataTypeChange = ({ key }) => {
        const match = customFieldDataTypeList.find((item) => item.backendDataType === key);
        if (match) {
            onDataTypeChange({ backendDataType: match.backendDataType, dataType: match.dataType });
        }
    };

    const handleMenuClick = (e) => {
        switch (e.key) {
            case "functions":
                onFilterChange({
                    columnType,
                    key: "functions",
                })
                break;
            case "group-by":
                onFilterChange({
                    columnType,
                    key: "groupBy",
                    value: "db.generic.groupBy.group"
                })
                break;
            default:
                if (e.keyPath.length === 2) {
                    switch (e.keyPath[1]) {
                        case "aggregate":
                            onFilterChange({
                                columnType,
                                key: "aggregate",
                                value: e.key
                            })
                            break;
                        case "order-by":
                            onFilterChange({
                                columnType,
                                key: "orderBy",
                                value: e.key
                            })
                            break;
                    }
                    break;
                }
                break;
        }
    }

    const dataTypeMenu = (
        <Menu
            selectedKeys={type?.backendDataType ? [type.backendDataType] : []}
            onClick={handleDataTypeChange}
            data-testid="hr-custom-column-data-type-menu"
        >
            {customFieldDataTypeList.map(({ backendDataType, label, dataType: optionDataType }) => (
                <Menu.Item key={backendDataType}>
                    {label || optionDataType}
                </Menu.Item>
            ))}
        </Menu>
    );

    const datatypeIconNode = (
        <span
            className="custom-column-chip-icon"
            data-testid="hr-custom-column-data-type-icon"
            onClick={(e) => {
                allowDataTypeChange && e.stopPropagation()
            }}
        >
            {dataTypeIcon(dataType)}
        </span>
    );

    const menu = (
        <Menu
            multiple={true}
            selectedKeys={selectedKeys}
            className="hi-field-menu"
            onClick={handleMenuClick}
        >
            <SubMenu key="aggregate" title={"Aggregate"}>
                {functionsList.aggregate.map((func) => {
                    const item = func.value;
                    return (
                        <Menu.Item
                            className="aggregation-item"
                            key={"db.generic.aggregate." + item}
                        >
                            {item[0].toUpperCase() + item.slice(1)}
                        </Menu.Item>
                    );
                })}
            </SubMenu>
            <Menu.Item key="functions">
                Functions
            </Menu.Item>
            <Menu.Item key="group-by">
                Group by
            </Menu.Item>
            <SubMenu key="order-by" title={"Order By"} className="group-start">
                {orderBySubMenuOptions.map(({ value, label }) => {
                    return (
                        <Menu.Item
                            className="aggregation-item"
                            key={value}
                        >
                            {label}
                        </Menu.Item>
                    );
                })}
            </SubMenu>
        </Menu>
    );

    const textToRender = (
        <Text
            data-testid="hr-custom-column-chip-label"
            ellipsis={true}
            className={`field-label${displayName ? "" : " custom-column-chip-placeholder"}`}
            onDoubleClick={startEdit}
            style={{ color: "#fff" }}
        >
            {displayName || "Custom column"}
        </Text>
    )


    const sideContent = (
        <span
            className="field-side-item"
            onClick={(e) => {
                e.preventDefault();
                e.stopPropagation();
                onFilterChange({
                    columnType,
                    key: "orderBy",
                })
            }}>
            {orderByValue === "none" && <VerticalAlignMiddleOutlined />}
            {orderByValue === "asc" && <SortAscendingOutlined />}
            {orderByValue === "desc" && <SortDescendingOutlined />}
        </span>
    )

    const tag = (
        <Tag
            color={backgroundColor}
            icon={editing ? null : (allowDataTypeChange ? (
                <Dropdown
                    overlay={dataTypeMenu}
                    trigger={["click"]}
                    placement="bottomLeft"
                >
                    <Tooltip title="Change data type">{datatypeIconNode}</Tooltip>
                </Dropdown>
            ) : datatypeIconNode)}
            className="custom-column-chip"
            style={{
                minWidth: 150
            }}
            closable={allowMenu}
            closeIcon={
                allowMenu ?
                    sideContent
                    : null
            }
        >
            {editing ? (
                <Input
                    ref={renameInput}
                    value={draft}
                    placeholder="Custom column"
                    data-testid="hr-custom-column-rename-input"
                    onChange={(e) => setDraft(e.target.value)}
                    onKeyDown={handleAliasing}
                    onBlur={commitEdit}
                    onClick={(e) => e.stopPropagation()}
                    className="filter-rename-input"
                    bordered={false}
                />
            ) : (
                <Tooltip title={tooltip || displayName || "Custom column"}>
                    {textToRender}
                </Tooltip>
            )}
        </Tag>
    )

    return (
        allowMenu ?
            <Dropdown
                placement="bottomRight"
                overlay={menu}
                trigger={["click"]}
                visible={dropdown}
                onVisibleChange={(visible) => setDropdown(visible)}
            >
                <div
                    style={{
                        cursor: "pointer",
                        maxWidth: 150
                    }}
                >{tag}</div>
            </Dropdown>
            : tag
    )
};

export default CustomColumnChip;
