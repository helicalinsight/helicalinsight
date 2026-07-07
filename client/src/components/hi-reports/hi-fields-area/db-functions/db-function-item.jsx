
import { useRef } from "react"
import { useDrag } from 'react-dnd'
import { NumberOutlined, CalendarOutlined, FileTextOutlined, CheckOutlined } from "@ant-design/icons";
import { getFieldDisplayName } from "../../../../utils/utilities"
import { getCanvasFieldDataType } from "../../../../utils/filter-utils";
import { dateTypes } from "../../utils/constants";

const Param = props => {
    const clickRef = useRef(0)
    const timerRef = useRef()
    let { item, type } = props
    let displayName = ""
    let icon = null
    if (type === "column") {
        displayName = getFieldDisplayName(item)
        let dataType = getCanvasFieldDataType(item)
        icon = <span className="hr-metadata-field-icon" >
            {dataType === "numeric" && <NumberOutlined />}
            {dataType === "text" && <FileTextOutlined />}
            {dataType === "boolean" && <CheckOutlined />}
            {dateTypes.includes(dataType) && <CalendarOutlined />}
        </span>
    }
    if (type === "function") {
        displayName = item.value
        icon = <span className="suggestion-column"> f  </span>
    }
    const [{ }, drag] = useDrag(
        () => {
            if (type === "datatype") {
                return {}
            }
            return {
                type: "functionText",
                item: { type: "functionText", value: displayName, draggingFrom: type },
                collect: (monitor) => ({
                    opacity: monitor.isDragging() ? 0.5 : 1
                })
            }
        },
        []
    )
    const handleDoubleClick = e => {
        console.log(e.deatil)
        e.stopPropagation()
        if (type === "column") {
            displayName = getFieldDisplayName(item)
            props.handleDoubleClick({ value: displayName, type: "column" }, e)
        }
        if (type === "function") {
            props.handleDoubleClick({ value: displayName, type: "function" }, e)

        }
    }
    const handleClick = e => {
        clickRef.current = clickRef.current + 1
        if (clickRef.current > 1) {
            clearInterval(timerRef.current)
            clickRef.current = 0
            return null
        }
        timerRef.current = setTimeout(() => {
            if (clickRef.current < 2) {
                type === "function" && props.handleFunctionClick({ value: displayName, type: "function" }, e)
                clickRef.current = 0
            }
        }, 500)

    }
    return (
        <div ref={drag}
        data-testid= "hi-report-db-func-item"
            className="db-func-list-item"
            onClick={handleClick}
            onDoubleClick={handleDoubleClick}
        >
            {icon}
            {displayName}
        </div>
    )
}

export default Param