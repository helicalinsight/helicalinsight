
import { useState, useRef, useEffect } from "react"
import { useDrag, useDrop } from 'react-dnd'
import { Tag, Dropdown, Input, Typography, Tooltip } from "antd"
import { useSelector } from "react-redux";
import { useDispatch } from "react-redux";
import {
    CheckCircleOutlined, CloseOutlined, SortAscendingOutlined, VerticalAlignMiddleOutlined, SortDescendingOutlined,
    NumberOutlined, CalendarOutlined, FileTextOutlined, CheckOutlined
} from '@ant-design/icons';
import { getFieldDisplayName, checkOrderIsApplied } from "../../../utils/utilities";
import { updateFieldAlias, moveFieldInCanvas, removeFieldFromCanvas, updateOrderBy } from "../../../redux/actions/hreport.actions";
import FieldMenu from "./menu.jsx"
import notify from "../../hi-notifications/notify";
import { getFloatingType } from "../../../utils/filter-utils";
import { dateTypes } from "../utils/constants";

const { Text } = Typography

const ReportField = props => {
    const dispatch = useDispatch()
    const Notify = notify(dispatch);
    const [editing, setEditing] = useState(false)
    const renameInput = useRef(null)
    const [dropdown, setDropdown] = useState(null)
    const { functions, fields, id: reportId, marksList, referenceLineList, dateFunctions, databaseFunctions } = useSelector(state => {
        let activeReport = state.hreport.present.reports.find(report => report.active)
        return activeReport
    })
    let { field } = props
    let { orderBy, id, type } = field
    const [{ }, drag] = useDrag(
        () => {
            return {
                type: "canvas_field",
                item: { type: "canvas_field", field },
                collect: (monitor) => ({
                    opacity: monitor.isDragging() ? 0.5 : 1
                })
            }
        },
        [field]
    )
    useEffect(() => {
        if (editing && renameInput.current) {
            renameInput.current.focus()
        }
    }, [editing])
    const handleDrop = item => {
        dispatch(moveFieldInCanvas({ source: item.field, target: field }))
    }
    const [, drop] = useDrop(() => ({
        accept: "canvas_field",
        drop: item => handleDrop(item),
    }))
    const handleDelete = field => {
        if (editing) {
            setEditing(false)
        } else {
            dispatch(removeFieldFromCanvas({ field }))
        }
    }
    const handleVisibleChange = flag => {
        setDropdown(flag)
    }
    const closeDropdown = () => {
        setDropdown(false)
    }
    const handleCycleOrder = (e) => {
        e.preventDefault();
        e.stopPropagation();
        let key = (!orderBy) ? "asc" : (checkOrderIsApplied("asc", field) ? "desc" : null)
        dispatch(updateOrderBy({ id, key }))
    }
    const handleRename = e => {
        e.stopPropagation()
        setEditing(true)
        setDropdown(false)
    }
    const hideAlias = () => {
        setEditing(false)
    }
    const handleAliasing = (event) => {
        switch (event.keyCode) {
            case 13:
                handleSetAlias();
                break;
            case 27:
                hideAlias();
                break;
        }
    }
    const handleSetAlias = () => {
        setEditing(false)
        let { value } = renameInput.current.input
        if (value === fieldName) return null
        if (!value) return null
        let isAliasExist = fields.some(field => getFieldDisplayName(field) === value)
        if (isAliasExist && field.addedAs !== "drillthrough_field") {
            return Notify.warning({ message: `${value} already exists in Rows, Columns, or Marks.` });
        }
        dispatch(updateFieldAlias({ id: field.id, alias: value }))
    }
    let fieldName = getFieldDisplayName(field)
    let { floatingType } = getFloatingType(field)
    let backgroundColor = floatingType === "discrete" ? "#337ab7" : "#4CAF50"
    const sideContent = (
        <>
            {!editing && <span className="field-side-item" onClick={handleCycleOrder}  >
                {!field.orderBy && <VerticalAlignMiddleOutlined data-testid={`order-${fieldName}`} />}
                {checkOrderIsApplied("asc", field) && <SortAscendingOutlined data-testid={`order-${fieldName}`} />}
                {checkOrderIsApplied("desc", field) && <SortDescendingOutlined data-testid={`order-${fieldName}`} />}
            </span>}
            <span data-testid={`remove_field-${fieldName}`} className="field-side-item" onClick={() => handleDelete(field)} ><CloseOutlined /></span>
        </>
    )
    let dataTypeIcon = null
    let { dataType } = type || {}
    if (["text", "other"].includes(dataType)) {
        dataTypeIcon = <FileTextOutlined />;
    }
    if (dataType === "numeric") {
        dataTypeIcon = <NumberOutlined />
    }
    if (dataType === "boolean") {
        dataTypeIcon = <CheckOutlined />
    }
    if (dateTypes.includes(dataType)) {
        dataTypeIcon = <CalendarOutlined />
    }
    return (
        <div ref={drop} onDoubleClick={e => e.stopPropagation()} >
            <Dropdown
                visible={editing ? false : dropdown}
                onVisibleChange={handleVisibleChange}
                overlay={
                    <FieldMenu
                        reportId={reportId}
                        field={field}
                        functions={functions}
                        closeDropdown={closeDropdown}
                        marksList={marksList}
                        referenceLineList={referenceLineList}
                        dateFunctions={dateFunctions}
                        databaseFunctions={databaseFunctions}
                        onRename={handleRename}
                    />

                }
                trigger={["click"]}
                placement="bottomRight"
            >

                <Tag closable ref={drag}
                    // color="#108ee9"
                    color={backgroundColor}
                    icon={editing ? null : dataTypeIcon}
                    closeIcon={sideContent}
                // onClose={e => onClose()}
                >
                    {editing &&
                        <Input
                            ref={renameInput}
                            defaultValue={fieldName}
                            data-testid={`rename-field-input-${fieldName}`}
                            onKeyDown={handleAliasing}
                            onBlur={handleSetAlias}
                            className="filter-rename-input"
                            bordered={false} />}
                    <Tooltip title={fieldName} >
                        {!editing && <Text
                            data-testid={`canvas-${field.addedAs}-${fieldName}`}
                            ellipsis={true}
                            className="field-label"
                            onDoubleClick={handleRename}  >
                            {fieldName}
                        </Text>}
                    </Tooltip>
                </Tag>
            </Dropdown>
        </div >
    )
}

export default ReportField;