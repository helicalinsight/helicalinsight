
import {
    CaretDownOutlined, CaretLeftOutlined, CaretRightOutlined
} from '@ant-design/icons';
import { Dropdown, Menu, Space, Typography } from "antd";
import { useEffect, useRef } from "react";
import { useDrop } from 'react-dnd';
// import ReportField from "./field"
import { useDispatch, useSelector } from "react-redux";

import { capitalize, cloneDeep } from "lodash-es";
import {
    addFieldToCanvas,
    clearFiledsShelf,
    swapCanvasField,
    toggleHidenFields,
    toggleQueryEditor
} from "../../../redux/actions/hreport.actions";
import { toTitleCase } from "../../../utils/text-utils";
import TutorialInfo from "../../common/hi-tutorial";
import HIIcon from "../../common/icons/hi-icons";
import ReportField from "./field";

const { Text } = Typography


const FieldDropArea = props => {
    const dispatch = useDispatch()
    const fieldsLength = useRef(null)
    const scrollArrows = useRef(null)
    let { fieldType, fields, defaultText, visible = true } = props
    let filteredFields = fields.filter((c) => {
        return (!c.hidden && !c.hiddenIncludeInResultSet && c.addedAs !== "measure_field")
    })
    const { showHiddenRows, showHiddenColumns } = useSelector(state => {
        let activeReport = state.hreport.present.reports.find(report => report.active)
        return activeReport
    })
    useEffect(() => {
        let length = filteredFields.filter(item => item.addedAs === fieldType).length
        let elem = document.querySelector(`#spaceElem-${fieldType}`)
        if (elem && scrollArrows.current) {
            if (fieldsLength.current < length) {
                elem.scrollBy(elem.scrollWidth, 0);
            }
            fieldsLength.current = length
            if (elem.scrollWidth > elem.offsetWidth) {
                scrollArrows.current.style.display = 'block';
            } else {
                scrollArrows.current.style.display = 'none';
            }
        }
    }, [filteredFields])
    const [, drop] = useDrop(() => ({
        accept: ["canvas_field", "metadata_field"],
        drop: item => handleDrop(item),
    }))
    const handleDrop = item => {
        if (item.type === "canvas_field") {
            if (item?.field?.addedAs === fieldType) {
                return null
            }
            dispatch(swapCanvasField({ fieldIds: [item.field.id] }))
        }
        if (item.type === "metadata_field") {
            handleAdd(item)
        }
    }
    const handleAdd = data => {
        let { table, column, dataSource, ...rest } = data.field
        let addedAs = fieldType;
        let payload = { table, column, addedAs, dataSource, ...rest }
        dispatch(addFieldToCanvas(cloneDeep(payload)))
    }
    const showEditor = () => {
        dispatch(toggleQueryEditor({ fieldType }))
    }
    const scrollBy = (value) => {
        let elem = document.querySelector(`#spaceElem-${fieldType}`)
        elem.scrollBy(value, 0);
    }
    const swapFields = () => {
        dispatch(swapCanvasField({ fieldIds: fields.map(field => field.id) }))
    }
    const toggleHidden = () => {
        if (fieldType === "column") {
            dispatch(toggleHidenFields({ showHiddenColumns: !showHiddenColumns }))
        } else {
            dispatch(toggleHidenFields({ showHiddenRows: !showHiddenRows }))
        }
    }
    const clearShelf = () => {
        dispatch(clearFiledsShelf({ fieldType }))
    }
    let selectedKeys = []
    if (fieldType === "column" && showHiddenColumns) {
        // filteredFields = fields
        filteredFields = fields.filter(field => field.addedAs !== "measure_field")
        selectedKeys.push("showHidden")
    }
    if (fieldType === "row" && showHiddenRows) {
        // filteredFields = fields
        filteredFields = fields.filter(field => field.addedAs !== "measure_field")
        selectedKeys.push("showHidden")
    }
    const menuItems = (
        <Menu selectedKeys={selectedKeys} className="hr-dropdown-menu" >
            <Menu.Item key="clear" onClick={clearShelf}  >
                Clear
            </Menu.Item>
            <Menu.Item key="custom" onClick={showEditor}  >
                Custom Column
            </Menu.Item>
            <Menu.Item key="showHidden" onClick={toggleHidden} data-testid={`show-hidden-${fieldType}s`}  >
                Show Hidden {capitalize(fieldType)}s
            </Menu.Item>
            <Menu.Item key="swap" onClick={swapFields}  >
                Swap
            </Menu.Item>
        </Menu>
    )

    const renderDefult = (text) => {
        return <span className='default-text'> {text} </span>
    }

    return (
        <div ref={drop} className="field-drop-area" >
            <Dropdown
                overlay={menuItems}
                trigger={["click"]}
                placement="bottomLeft">
                <div className="field-heading" data-testid={`${fieldType}s-heading`}  >
                    <div className="canvas-fields-icon" >
                        <HIIcon name={`hi-${fieldType}s`} />
                    </div>
                    <TutorialInfo elementKey={`hr-${fieldType}s-shelf`} >
                        {visible ? <div className="field-heading-label" >
                            <Text ellipsis={true} >
                                {toTitleCase(fieldType)}s
                            </Text>
                        </div> : null}
                    </TutorialInfo>
                    <div data-testid={`${fieldType}-shelf-dropdown`} className="canvas-fields-dropdown-icon" >
                        <CaretDownOutlined />
                    </div>
                </div>
            </Dropdown>
            {/* <span onClick={showEditor} ><PlusCircleOutlined /></span> */}
            <Space align="center" className="fields-list"
                data-testid={`canvas-${fieldType}-fields-list`}
                id={`spaceElem-${fieldType}`} onDoubleClick={showEditor} >
                {filteredFields.length ?
                    filteredFields.filter(item => (item.addedAs === fieldType)).length === 0 ? renderDefult(defaultText) :
                        filteredFields.map((field) => {
                            if (field.addedAs !== fieldType) return null;
                            return (
                                <ReportField field={field} key={field.id} />
                            )
                        }) : renderDefult(defaultText)}

            </Space>
            <div className="scroll-items" ref={scrollArrows} >
                <span onClick={() => scrollBy(-100)} ><CaretLeftOutlined /></span>
                <span onClick={() => scrollBy(100)}><CaretRightOutlined /></span>
            </div>
        </div>
    )
}

export default FieldDropArea