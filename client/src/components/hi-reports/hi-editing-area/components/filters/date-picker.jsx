import { useEffect } from "react";
import moment from "moment"
import { getCurrentDate,getFilterDataType } from "../../../../../utils/filter-utils";
import { HiDatePicker } from "./hi-datepicker.jsx";

const DatePicker = props => {
    let { filter, valueDateFunc } = props
    let { datePart } = filter
    let dataType = getFilterDataType(filter)
    useEffect(()=>{
        if(!filter.values.length) {
            changeDateTime(moment())
        }
    },[datePart])
    const changeDateTime = dateTime => {
        if (valueDateFunc === "individual") {
            if (dataType === "dateTime") {
                dateTime = dateTime.format('YYYY-MM-DD HH:mm:ss.S')
            }
            if (dataType === "date") {
                dateTime = dateTime.format('YYYY-MM-DD')
            }
            if (dataType === "time") {
                dateTime = dateTime.format('HH:mm:ss')
            }
        } else if (valueDateFunc === "date") {
            dateTime = dateTime.format('YYYY-MM-DD')
        } else if (valueDateFunc === "time") {
            dateTime = dateTime.format('HH:mm:ss')
        } else if (valueDateFunc === "year") {
            dateTime = dateTime.year();
        } else if (valueDateFunc === "quarter") {
            dateTime = dateTime.quarter();
        } else if (valueDateFunc === "monthname") {
            dateTime = dateTime.format('MMMM')
        } else if (valueDateFunc === "month") {
            dateTime = dateTime.month() + 1
        } else if (valueDateFunc === "day") {
            dateTime = dateTime.date();
        } else if (valueDateFunc === "hour") {
            dateTime = dateTime.hour()
        } else if (valueDateFunc === "minute") {
            dateTime = dateTime.minute()
        } else if (valueDateFunc === "second") {
            dateTime = dateTime.second()
        }
        props.onChange(dateTime);
    }
    let currentDate = moment()
    try {
        currentDate = getCurrentDate({ filter, datePart, dataType, valueDateFunc })
    } catch (e) {
    }
    currentDate = moment(currentDate._d).isValid() ? currentDate : moment()
    return (
        <div>
            <HiDatePicker
                value={currentDate}
                onChange={changeDateTime}
                dataType={dataType}
                datePart={datePart}
                mode={props.mode}
                format={props?.format}
            />
        </div>
    )
}

export default DatePicker