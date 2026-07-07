

import React, { useState, useEffect } from 'react';
import { Button, DatePicker, TimePicker } from 'antd';
import moment from "moment";
// import RelativeList from "../../helical-reports/components/filters/date/relative-list";
const { RangePicker } = DatePicker;
const timeConfig = { "time": "HH:mm:ss", "hour": "HH", "minute": "mm", "second": "ss" }

export const HiDatePicker = props => {
    let { datePart, value, elementId,dataType, mode='' } = props
    const [showRanges, setShowRanges] = useState(false)
    value = value || moment()
    datePart = datePart || "individual"
    let showTime = false
    let picker = "date"
    if (datePart === "individual" && dataType !== "date") {
        showTime = true
    }
    if (["year", "month", "quarter"].indexOf(datePart) > -1) {
        picker = datePart
    }
    const onChange = (date) => {
        date = date || moment()
        props.onChange(date);
    }
    const toggleRanges = () => {
        setShowRanges(!showRanges)
    }

    useEffect(() => {
        if (value && mode !== "filter") return props.onChange(value); // 7138 fix
    }, [])

    if (timeConfig[datePart]) {
        let format = timeConfig[datePart]
        return (
            <div className="hi-datepicker-main"  >
                <TimePicker
                    onChange={onChange}
                    format={format}
                    value={value}
                />
            </div>
        )
    }
    let format = ""
    if (picker === "quarter") {
        format = "[Q]Q"
    }
    if (picker === "month") {
        format = "MMM"
    }
    if (datePart === "day") {
        format = "DD"
        value = value.set("month",0)
    }
    format = props?.format || format
    if(datePart === "individual"){
        value = moment(value)
    }
    if (props.dataType === "dateTime" && showTime) {
        format="YYYY-MM-DD HH:mm:ss"
    }
    if(props?.format && ["date","dateTime"].includes(dataType)){
        format = props.format
    }
    let dropdownClassName = ["quarter", "month", "day"].includes(datePart) ? "hi-picker-header-hidden" : ""
    return (
        <div data-testid =  "Hi-report-hidate-picker" className="hi-datepicker-main"  >
            <DatePicker
                onChange={onChange}
                dropdownClassName={dropdownClassName}
                showTime={showTime}
                picker={picker}
                format={format}
                value={value}
            />
            {elementId && <div className="rel-icon" >
                <Button onClick={toggleRanges}>
                    R
                </Button>
            </div>}
            {/* {showRanges && <RelativeList
                filter={iMap({
                    mapping: mapping,
                    datePart: datePart,
                    anchor: anchor,
                    condition: condition,
                    dataType: dataType,
                    values: values,
                })}
                elementId={elementId}
                updateFilterValues={updateFilterValues}
                updateAnchorDate={updateAnchorDate}
                toggleRelList={toggleRanges}
            />} */}
        </div>
    )
}
export const HiDateRangePicker = props => {
    const [showRanges, setShowRanges] = useState(false)
    let { startDate, endDate, datePart, elementId, dataType } = props;
    startDate = startDate || moment()
    endDate = endDate || moment()
    datePart = datePart || "individual"
    let showTime = false
    let picker = "date"
    if (datePart === "individual" && dataType!== "date") {
        showTime = true
    }
    if (["year", "month", "quarter", "day"].indexOf(datePart) > -1) {
        picker = datePart
    }
    const toggleRanges = () => {
        setShowRanges(!showRanges)
    }
    const onChange = (date, date1) => {
        date = date || [moment(), moment()]
        props.onChange(date, date1)
    }

    if (timeConfig[datePart]) {
        let format = timeConfig[datePart]
        return (
            <div className="hi-datepicker-main"    >
                <TimePicker.RangePicker
                    value={[startDate, endDate]}
                    onChange={onChange}
                    showTime={showTime}
                    format={format}
                    picker={picker}
                />
            </div>
        )
    }

    let format = ""
    if (picker === "quarter") {
        format = "[Q]Q"
    }
    if (picker === "month") {
        format = "MMM"
    }
    if (picker === "day") {
        format = "DD"
        startDate = startDate.set("month",0)
        endDate = endDate.set("month",0)
    }
    format = props?.format || format
    if(props?.format && ["date","dateTime"].includes(dataType)){
        format = props.format
    }
    let dropdownClassName = ["quarter","month","day"].includes(datePart) ? "hi-picker-header-hidden" : ""
    if (["month", "quarter", "day"].indexOf(datePart) > -1) {
        showTime = true
    }
    return (
        <div data-testid =  "Hi-report-hidate-rangepicker" className="hi-datepicker-main"
        >
            <RangePicker
                showTime={showTime}
                dropdownClassName={dropdownClassName}
                value={[startDate, endDate]}
                onChange={onChange}
                picker={picker}
                format={format}
                ranges={{
                    Today: [moment(), moment()],
                    'This Week': [moment().startOf('week'), moment().endOf('week')],
                    'This Month': [moment().startOf('month'), moment().endOf('month')],
                    'This Quarter': [moment().startOf('quarter'), moment().endOf('quarter')],
                    'This Year': [moment().startOf('year'), moment().endOf('year')],
                }}
            />
            {elementId && <div className="rel-icon" >
                <Button onClick={toggleRanges}>
                    R
                </Button>
            </div>}
            {/* {showRanges && <RelativeList
                filter={iMap({
                    mapping: mapping,
                    datePart: datePart,
                    anchor: anchor,
                    condition: condition,
                    dataType: dataType,
                    values: values,
                })}
                elementId={elementId}
                updateRangeValues={updateRangeValues}
                updateAnchorDate={updateAnchorDate}
                toggleRelList={toggleRanges}
            />} */}
        </div>
    )
}

