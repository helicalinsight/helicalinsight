
import { useEffect } from "react"
import moment from "moment"
import { getRange,getFilterDataType } from "../../../../../utils/filter-utils";
import { HiDateRangePicker } from "./hi-datepicker";

const DateRange = props => {
    let { filter, valueDateFunc } = props
    let { datePart, condition } = filter
    // let { toolTipFormat } = datePickerFormat(filter, dataType)
    let dataType = getFilterDataType(filter)
    useEffect(() => {
        try {
            if (!filter.values.length) {
                changeDateTime([moment(),moment()])
            }
        } catch (e) {
            console.error(e)
        }
    },[datePart, condition])
    const changeDateTime = (selectedVal) => {
        let [startDate, endDate] = selectedVal
        if (valueDateFunc === "individual") {
            if (dataType === "dateTime") {
                startDate = startDate.format('YYYY-MM-DD HH:mm:ss.S')
                endDate = endDate.format('YYYY-MM-DD HH:mm:ss.S')
            }
            if (dataType === "date") {
                startDate = startDate.format('YYYY-MM-DD')
                endDate = endDate.format('YYYY-MM-DD')
            }
            if (dataType === "time") {
                startDate = startDate.format('HH:mm:ss')
                endDate = endDate.format('HH:mm:ss')
            }
        } else if (valueDateFunc === "date") {
            startDate = startDate.format('YYYY-MM-DD')
            endDate = endDate.format('YYYY-MM-DD')
        } else if (valueDateFunc === "time") {
            startDate = startDate.format('HH:mm:ss')
            endDate = endDate.format('HH:mm:ss')
        } else if (valueDateFunc === "year") {
            startDate = startDate.year()
            endDate = endDate.year()
        } else if (valueDateFunc === "quarter") {
            startDate = startDate.quarter()
            endDate = endDate.quarter()
        } else if (valueDateFunc === "monthname") {
            startDate = startDate.format("MMMM")
            endDate = endDate.format("MMMM")
        } else if (valueDateFunc === "month") {
            startDate = startDate.month() + 1
            endDate = endDate.month() + 1
        } else if (valueDateFunc === "day") {
            startDate = startDate.date()
            endDate = endDate.date()
        } else if (valueDateFunc === "hour") {
            startDate = startDate.hour()
            endDate = endDate.hour()
        } else if (valueDateFunc === "minute") {
            startDate = startDate.minute()
            endDate = endDate.minute()
        } else if (valueDateFunc === "second") {
            startDate = startDate.second()
            endDate = endDate.second()
        }
        props.onChange([startDate, endDate]);
    }
    let { startDate, endDate } = getRange({ filter, datePart, dataType, valueDateFunc })
    startDate = moment(startDate._d).isValid() ? startDate : moment()
    endDate = moment(endDate._d).isValid() ? endDate : moment()

    return (
        <div>
            <HiDateRangePicker
                data-testid = "Hi-report-date-range"
                startDate={startDate}
                endDate={endDate}
                datePart={datePart}
                dataType={dataType}
                onChange={changeDateTime}
                format={props?.format}
            />
        </div>
    )
}

export default DateRange