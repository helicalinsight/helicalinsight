
import { DownOutlined, InfoCircleOutlined } from "@ant-design/icons";
import { Button, Checkbox, Col, Drawer, Dropdown, Input, Menu, Radio, Row, Tooltip } from "antd";
import moment from "moment";
import React, { useEffect, useState } from 'react';
import {
    changeEqualDateTime,
    changeRangeDateTime,
    createEffectedDate,
    createRelativePart,
    datePickerFormat, funcMap, getDatePart, getRelativeList, getRelativePartList, momentDateFormat, rangeConditions
} from "../../../../../utils/filter-utils";


const RelativeList = props => {
    let { filter, elementId, dateValuesType } = props
    let { condition, dataType, datePart, anchor, mapping } = filter
    let displayDateFunc = datePart, valueDateFunc = datePart
    if (mapping && mapping.DisplayDBFunction && mapping.DisplayDBFunction.functionName) {
        let val = mapping.DisplayDBFunction.functionName
        displayDateFunc = getDatePart(val)
    }
    if (mapping && mapping.valueDBFunction && mapping.valueDBFunction.functionName) {
        valueDateFunc = getDatePart(mapping.valueDBFunction.functionName)
    }
    let { toolTipFormat } = datePickerFormat(filter, dataType)
    let range = rangeConditions.indexOf(condition) > -1
    const [active, setActive] = useState(anchor.active)
    const [isAnchor, toggleAnchor] = useState(anchor.isAnchor)
    const [anchorDate, setAnchorDate] = useState(moment(anchor.anchorDate, "YYYY-MM-DD HH:mm:ss")._d)
    const [anchorStr, setAnchorStr] = useState(moment(anchor.anchorDate).format(toolTipFormat ? toolTipFormat : "YYYY-MM-DD HH:mm:ss"))
    let [relativePart, setRelativePart] = useState(anchor.relativePart)
    const [value, setValue] = useState(anchor.value)
    let anchorParts = getRelativeList({ filter, dataType, selectedPart: relativePart, isAnchor })
    const [part, setPart] = useState(anchor.part ? anchor.part : anchorParts[0].part)
    const [nextInput, setNextInput] = useState(anchor.nextInput)
    const [lastInput, setLastInput] = useState(anchor.lastInput)
    const [direction, setDirection] = useState(anchor.direction)
    useEffect(() => {
        if (['filter', 'open'].includes(props?.mode)) {
            setActive(anchor.active)
            setValue(anchor.value)
            setNextInput(anchor.nextInput)
            setLastInput(anchor.lastInput)
            setDirection(anchor.direction)
            setRelativePart(anchor.relativePart)
            toggleAnchor(anchor.isAnchor)
            setPart(anchor.part)
        }
    }, [anchor.active, anchor.value, anchor.nextInput, anchor.lastInput, anchor.direction, anchor.relativePart, anchor.isAnchor, anchor.part])
    useEffect(() => {
        if (['filter', 'open'].includes(props?.mode)) {
            let momentDate = getDateFromValue(anchor.anchorDate, datePart)
            if (momentDate._isValid) {
                setAnchorStr(moment(momentDate).format(toolTipFormat ? toolTipFormat : "YYYY-MM-DD HH:mm:ss"))
                setAnchorDate(momentDate._d)
            } else {
                const tempDate = moment(anchor.anchorDate, "YYYY-MM-DD HH:mm:ss")._d
                setAnchorDate(tempDate)
                setAnchorStr(moment(tempDate).format(toolTipFormat ? toolTipFormat : "YYYY-MM-DD HH:mm:ss"))
            }
        }
    }, [anchor.anchorDate])

    if (funcMap.indexOf(displayDateFunc) === -1) return null
    if (!relativePart) {
        relativePart = createRelativePart(dataType, displayDateFunc)
    }

    const changeRelativePart = relativePart => {
        setRelativePart(relativePart)
        setPart(getRelativeList({ filter, dataType, selectedPart: relativePart, isAnchor })[0].part)
    }
    const changeRelative = (val, timePart, direction, index, custom) => {
        if (custom) {
            if (direction === "last") setLastInput(val)
            if (direction === "next") setNextInput(val)
        }
        if (val === "") val = 0
        setActive(index)
        setValue(val)
        setPart(timePart)
        setDirection(direction)
    }
    const applyChanges = () => {
        let dateNow = isAnchor ? anchorDate : moment()._d
        let startValue = "01", endValue = "01"
        if (direction === "last") {
            if (value === "toDate") {
                startValue = createEffectedDate(dateNow, 0, part, direction, toolTipFormat, relativePart, "start")
                endValue = createEffectedDate(dateNow, 0, part, direction, toolTipFormat, relativePart, "now")
            } else {
                startValue = createEffectedDate(dateNow, value, part, direction, toolTipFormat, relativePart, "start")
                endValue = createEffectedDate(dateNow, 1, part, direction, toolTipFormat, relativePart, "end")
            }
        } else if (direction === "next") {
            startValue = createEffectedDate(dateNow, 1, part, direction, toolTipFormat, relativePart, "start")
            endValue = createEffectedDate(dateNow, value, part, direction, toolTipFormat, relativePart, "end")
        } else {
            startValue = createEffectedDate(dateNow, 0, part, direction, toolTipFormat, relativePart, "start")
            endValue = createEffectedDate(dateNow, 0, part, direction, toolTipFormat, relativePart, "end")
        }
        if (range) {
            try {
                let anchorDateData = {
                    filter, isAnchor, active, value, direction, lastInput, nextInput, part, relativePart,
                    anchorDate: moment(anchorDate).format("YYYY-MM-DD HH:mm:ss")
                }
                const [startDate, endDate] = changeRangeDateTime(startValue, endValue, filter)
                if (filter.values[0] !== startDate || filter.values[1] !== endDate) {
                    let data = { uid: filter.uid, value: [startDate, endDate], anchorDateData }
                    props.updateFilterValues(data)
                }
            } catch (e) {
                console.error(e)
            }
        } else {
            let dateValue = createEffectedDate(dateNow, value, part, direction, toolTipFormat, relativePart, "start")
            try {
                let anchorDateData = {
                    filter, isAnchor, active, value, direction, lastInput, nextInput, part, relativePart,
                    anchorDate: moment(anchorDate).format("YYYY-MM-DD HH:mm:ss")
                }
                const dateTime = changeEqualDateTime(dateValue, valueDateFunc, dataType)
                props.updateFilterValues({ uid: filter.uid, value: dateTime, anchorDateData })
            } catch (e) {
            }
        }
    }

    const handleAnchorChange = e => {
        setAnchorStr(e.target.value)
    }
    const getDateFromValue = (value, datePart) => {
        return moment(value, momentDateFormat.get(datePart) || "YYYY-MM-DD HH:mm:ss")
    }

    const handleAnchorSave = e => {
        if (e.keyCode === 13 || e.which === 13) {
            let value = e.target.value
            let momentDate = getDateFromValue(value, datePart)
            if (momentDate._isValid) {
                setAnchorDate(momentDate._d)
            } else {
                setAnchorStr(moment(momentDate).format("YYYY-MM-DD HH:mm:ss"))
                // notify.warn("invalid date")
            }
        }
    }
    const handleBlur = (e) => {
        let value = e.target.value
        let momentDate = getDateFromValue(value, datePart)
        if (momentDate._isValid) {
            setAnchorDate(momentDate._d)
        } else {
            setAnchorStr(moment(momentDate).format("YYYY-MM-DD HH:mm:ss"))
            // notify.warn("invalid date")
        }
        // setAnchorStr(moment(anchorDate).format("YYYY-MM-DD HH:mm:ss"))
    }
    let startValue = "01", endValue = "01"

    let dateNow = isAnchor ? anchorDate : moment()._d
    if (direction === "last") {
        if (value === "toDate") {
            startValue = createEffectedDate(dateNow, 0, part, direction, toolTipFormat, relativePart, "start")
            endValue = createEffectedDate(dateNow, 0, part, direction, toolTipFormat, relativePart, "now")
        } else {
            startValue = createEffectedDate(dateNow, value, part, direction, toolTipFormat, relativePart, "start")
            endValue = createEffectedDate(dateNow, 1, part, direction, toolTipFormat, relativePart, "end")
        }
    } else if (direction === "next") {
        startValue = createEffectedDate(dateNow, 1, part, direction, toolTipFormat, relativePart, "start")
        endValue = createEffectedDate(dateNow, value, part, direction, toolTipFormat, relativePart, "end")
    } else {
        startValue = createEffectedDate(dateNow, 0, part, direction, toolTipFormat, relativePart, "start")
        endValue = createEffectedDate(dateNow, 0, part, direction, toolTipFormat, relativePart, "end")
    }
    let dateValue = "01"
    if (!range) {
        dateValue = createEffectedDate(dateNow, value, part, direction, toolTipFormat, relativePart, "start")
    }
    const handleRadioClick = (index, type) => {
        if (active !== index) {
            setActive(index)
        }
        if (type === "last") {
            setValue(lastInput)
            setDirection(type)
            setPart(relativePart)
        }
        if (type === "next") {
            setValue(nextInput)
            setPart(relativePart)
            setDirection(type)
        }

    }
    let topPos = 0, marginTop = 83
    var element = document.querySelector("#relative-icon")
    if (elementId) {
        topPos = 60
    } else if (element) {
        topPos = element.getBoundingClientRect().top + window.scrollY - 100
        if ((window.innerHeight - topPos) < 220) {
            topPos = topPos - 220
        } else {
            topPos = topPos + marginTop
        }
    }

    const tooltipInfo = (
        <Tooltip title={"The values you have selected differ from the current settings. To update and apply your new selections, please click the 'Apply' button."}>
            <span className="relative-list-info-icon" data-testid={`relative-list-info-icon`}>
                <InfoCircleOutlined />
            </span>
        </Tooltip>
    )

    const menu = (
        <Menu selectedKeys={[relativePart]} >
            {getRelativePartList(dataType, displayDateFunc, datePart).map(part => {
                let { label, value } = part
                let className = ""
                if (["Years"].includes(label)) {
                    className = className + "hr-relative-func-group-end"
                }
                return (
                    <Menu.Item key={value} onClick={() => changeRelativePart(value)} className={className} >
                        {label}
                    </Menu.Item>
                )
            })}
        </Menu>
    );
    const getDropdownLabel = (displayDateFunc) => {
        return getRelativePartList(dataType, displayDateFunc, datePart).find(item => {
            if (item.value === relativePart) return true
        })?.label
    }
    return (
        <Drawer
            data-testid="hi-report-relativelist-drawer"
            title={
                <span data-testid="hi-report-relativelist-drawer-title" className="">Relative Date{'('}s{')'}</span>
            }
            placement="right"
            width={"30%"}
            onClose={() => props.toggleRelList(filter)}
            visible={dateValuesType === "relative-list"}
        >
            <div className="relative-list"  >
                {!range && <li className="relative-value" data-testid={`hr-relative-list-value-${datePart}`}>
                    Selected Value : {moment(dateValue).format(toolTipFormat)}
                    {tooltipInfo}
                </li>}
                {range && <li className="relative-value" >
                    <div>
                        Selected Value : {moment(startValue).format(toolTipFormat)}
                        <span className="seperator" >to</span>
                        {moment(endValue).format(toolTipFormat)}
                        {tooltipInfo}
                    </div>
                </li>}
                <li className="relative-row anchor-date" >
                    <Checkbox onChange={() => toggleAnchor(!isAnchor)} checked={isAnchor} data-testid={`hr-relative-list-checkbox-${datePart}`}>
                        Anchor Relative To
                    </Checkbox>
                    {isAnchor && <Input
                        value={anchorStr}
                        onChange={handleAnchorChange}
                        onKeyDown={handleAnchorSave}
                        onBlur={handleBlur}
                        className="anchor-input"
                        data-testid={`hr-relative-list-input-field-${datePart}`}
                    />}
                </li>
                {(funcMap.indexOf(displayDateFunc) > -1) && <li>
                    <Row justify="end" >
                        <Dropdown overlay={menu} placement="bottomLeft" arrow>
                            <Button>
                                {getDropdownLabel(displayDateFunc)}
                                <DownOutlined />
                            </Button>
                        </Dropdown>
                    </Row>
                </li>}
                {getRelativeList({ filter, dataType, selectedPart: relativePart, isAnchor }).map((item, i) => {
                    let index = i + 1
                    if (item.val === "custom") {
                        return (
                            <li key={item.label1}  >
                                <Row>
                                    <Col flex="150px">
                                        <Radio
                                            onChange={() => handleRadioClick(index, item.type)}
                                            checked={active === index}
                                        >{item.label1}</Radio>
                                    </Col>
                                    <Col flex="170px">
                                        <Input
                                            disabled={active !== (index)}
                                            value={item.type === "last" ? lastInput : nextInput}
                                            className="numeric-input"
                                            onChange={e => {
                                                e.preventDefault()
                                                changeRelative(e.target.value, item.part, item.type, index, true)
                                            }}
                                            suffix={item.label2} />
                                    </Col>
                                </Row>
                            </li>
                        )
                    } else if (item.val === "toDate") {
                        if (!range) return null
                        return (
                            <li key={item.label}
                            >
                                <Radio
                                    onChange={() => changeRelative(item.val, item.part, item.type, index)}
                                    checked={active === index}
                                >
                                    {item.label}
                                </Radio>
                            </li>
                        )
                    } else {
                        return (
                            <li key={item.label}  >
                                <Radio
                                    onChange={() => changeRelative(item.val, item.part, item.type, index)}
                                    checked={active === index}
                                >
                                    {item.label}
                                </Radio>
                            </li>
                        )
                    }
                })}
                <li>
                    <div className="relative-footer" >
                        <Button onClick={applyChanges} type="primary">
                            Apply
                        </Button>
                    </div>
                </li>
            </div>
        </Drawer>
    )
}



export default RelativeList