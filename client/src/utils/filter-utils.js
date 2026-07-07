import produce from "immer";
import { v4 as uuidv4 } from "uuid";
import { fromJS } from "immutable";
import _, { flow, isEmpty, template } from "lodash";
import moment from "moment";
import notify from "../components/hi-notifications/notify";
import { selectBuilder, whereBuilder } from "../components/hi-reports/utils/base";
import { loadFilterValues, loadValuesRange } from "../redux/actions/hreport.actions";
import ReportQuery from "./reportQuery";
import { extractDatabaseFunctions, getFieldDisplayName, isArray } from "./utilities";

export const dateFiltersTypes = ["dateTime", "date", "time"]
export const singleValueConditions = [
    "CONTAINS", "DOES_NOT_CONTAINS", "STARTS_WITH", "DOES_NOT_STARTS_WITH", "EQUALS", "NOT_EQUALS", "ENDS_WITH",
    "DOES_NOT_ENDS_WITH", "IS_LESS_THAN", "IS_GREATER_THAN", "IS_LESS_THAN_OR_EQUAL_TO", "IS_GREATER_THAN_OR_EQUAL_TO",
    "CUSTOM"
]
export const mulipleValueConditions = ["IS_ONE_OF", "IS_NOT_ONE_OF"]
export const inputValueConditions = ["CONTAINS", "DOES_NOT_CONTAINS", "STARTS_WITH", "DOES_NOT_STARTS_WITH", "ENDS_WITH",
    "DOES_NOT_ENDS_WITH", "CUSTOM", "IS_NULL", "IS_NOT_NULL"]

export const searchConditions = ["EQUALS", "NOT_EQUALS", "IS_ONE_OF", "IS_NOT_ONE_OF", "IS_LESS_THAN", "IS_GREATER_THAN",
    "IS_LESS_THAN_OR_EQUAL_TO", "IS_GREATER_THAN_OR_EQUAL_TO"]
export const valuesConditions = ["EQUALS", "NOT_EQUALS", "IS_ONE_OF","IS_NOT_ONE_OF", "IS_LESS_THAN", 
    "IS_GREATER_THAN","IS_NOT_ONE_OF","IS_LESS_THAN_OR_EQUAL_TO", "IS_GREATER_THAN_OR_EQUAL_TO"]

export const valuesModeCostomConditions = [
    "CONTAINS", "DOES_NOT_CONTAINS", "STARTS_WITH", "DOES_NOT_STARTS_WITH", "ENDS_WITH", "DOES_NOT_ENDS_WITH", "IS_NULL",
    "IS_NOT_NULL"
]
export const equalConditions = ["EQUALS", "NOT_EQUALS", "IS_LESS_THAN", "IS_GREATER_THAN",
    "IS_LESS_THAN_OR_EQUAL_TO", "IS_GREATER_THAN_OR_EQUAL_TO"]

export const multiValueConditions = ["IS_ONE_OF", "IS_NOT_ONE_OF"]
export const rangeConditions = ["IS_BETWEEN", "IS_NOT_BETWEEN", "IN_RANGE", "NOT_IN_RANGE"]

export const valuesModeAutoConditions = ["EQUALS", "NOT_EQUALS", "IS_ONE_OF", "IS_NOT_ONE_OF", "CUSTOM", "IS_LESS_THAN", "IS_GREATER_THAN", "IS_LESS_THAN_OR_EQUAL_TO", "IS_GREATER_THAN_OR_EQUAL_TO"]

export const radioButtonConditions = ["EQUALS", "NOT_EQUALS", "IS_LESS_THAN", "IS_GREATER_THAN", "IS_LESS_THAN_OR_EQUAL_TO",
    "IS_GREATER_THAN_OR_EQUAL_TO"]
export const funcMap = [
    "individual",
    "datetime",
    "date",
    "time",
    "year",
    "quarter",
    "month",
    "monthname",
    "day",
    "hour",
    "minute",
    "second",
]

export const dateTypes = {
    INDIVIDUAL: "individual",
    DATETIME: "datetime",
    DATEANDTIME: "dateTime",
    DATE: "date",
    TIME: "time",
    YEAR: "year",
    QUARTER: "quarter",
    MONTH: "month",
    MONTHNAME: "monthname",
    DAY: "day",
    HOUR: "hour",
    MINUTE: "minute",
    SECOND: "second",
}

export const dateFormats = {
    DATETIME:"YYYY-MM-DD HH:mm:ss.S",
    DATE:"YYYY-MM-DD",
    TIME:"HH:mm:ss",
    MONTHNAME:"MMMM"
}


export const momentDateFormat = new Map([
    ['date', 'YYYY-MM-DD'],
    ['year', 'YYYY'],
    ['month', 'MM'],
    ['quarter', '0Q'],
    ['day', 'DD'],
    ['time', 'HH:mm:ss'],
    ['hour', 'HH'],
    ['minute', 'mm'],
    ['second', 'ss'],
    ['individual', "YYYY-MM-DD HH:mm:ss"],
])

const relativeDateRegex = /^(anchor_)?(today|tomorrow|yesterday|month|year|quarter|hour|minute|second|to_date)([-+]\d+)?$/;
export const anchorConstant = '__anchor__';

export const datePickerFormat = (filter, dataType, displayDateFunc) => {
    let { datePart, mapping } = filter
    if (!displayDateFunc) {
        displayDateFunc = datePart
        if (mapping && mapping.DisplayDBFunction && mapping.DisplayDBFunction.functionName) {
            displayDateFunc = getDatePart(mapping.DisplayDBFunction.functionName)
        }
    }
    let format = "M/d/y"
    let toolTipFormat = "YYYY-MM-DD"
    let start = "Month"
    if (!displayDateFunc) {
        format = "M/d/y"
    }
    if (displayDateFunc === "date") {
        format = "yyyy-MM-dd"
        start = "Month"
        toolTipFormat = "YYYY-MM-DD"
    }
    if (displayDateFunc === "time") {
        format = "HH:mm:ss"
        start = "Month"
        toolTipFormat = "HH:mm:ss"
    }
    if (displayDateFunc === "year") {
        format = "y"
        toolTipFormat = "YYYY"
        start = "Decade"
    }
    // if (displayDateFunc === "quarter") {
    //     format = "MM"
    //     start = "Year"
    //     toolTipFormat = "MM"
    // }
    if (displayDateFunc === "quarter") {
        format = "QQ"
        start = "Year"
        toolTipFormat = "0Q"
    }
    if (displayDateFunc === "month") {
        format = "MM"
        start = "Year"
        toolTipFormat = "MM"
    }
    if (displayDateFunc === "monthname") {
        format = "MMM"
        start = "Year"
        toolTipFormat = "MMM"
    }
    if (displayDateFunc === "day") {
        format = "dd"
        start = "Month"
        toolTipFormat = "DD"
    }
    if (displayDateFunc === "hour") {
        format = "HH"
        start = "Month"
        toolTipFormat = "HH"
    }
    if (displayDateFunc === "minute") {
        format = "mm"
        start = "Month"
        toolTipFormat = "mm"
    }
    if (displayDateFunc === "second") {
        format = "ss"
        start = "Month"
        toolTipFormat = "ss"
    }
    if (displayDateFunc === "individual") {
        if (dataType === "date") {
            format = "yyyy-MM-dd"
            start = "Month"
            toolTipFormat = "YYYY-MM-DD"
        }
        if (dataType === "dateTime") {
            format = "yyyy-MM-dd HH:mm:ss"
            start = "Month"
            toolTipFormat = "YYYY-MM-DD HH:mm:ss"
        }
        if (dataType === "time") {
            format = "HH:mm:ss"
            start = "Month"
            toolTipFormat = "HH:mm:ss"
        }
    }
    return { format, start, toolTipFormat }
}
export const addDateValues = ({ filter, emptyValues }) => {
    if (!filter) return null
    let { mapping } = filter
    if (mapping.isEnabled) return filter
    if (emptyValues) {
        filter.values = []
    }
    let { databaseFunction, datePart, values, condition, dataType } = filter
    dataType = (databaseFunction && databaseFunction.returns) ? databaseFunction.returns : dataType
    if ((dateFiltersTypes.indexOf(dataType) > -1) && (!values.length)) {
        let { toolTipFormat } = datePickerFormat(filter, dataType)
        let value = moment().format(toolTipFormat)
        if (["year", "quarter", "month", "day", "hour", "minute", "second"].indexOf(datePart) > -1) {
            value = parseInt(value)
        }
        if (datePart === "quarter") {
            value = Math.ceil(5 / 3)
        }
        if (["IS_BETWEEN", "IS_NOT_BETWEEN", "IN_RANGE", "NOT_IN_RANGE"].indexOf(condition) > -1) {
            filter = { ...filter, values: [value, value], maxInput: value, minInput: value }
        } else {
            let { condition, values } = filter
            if (Array.isArray(value)) {
                values = value
            } else {
                let currentIndex = values.indexOf(value)
                if (singleValueConditions.indexOf(condition) > -1) {
                    values = [value]
                } else if (mulipleValueConditions.indexOf(condition) > -1) {
                    if (currentIndex > -1) {
                        values.splice(currentIndex, 1)
                    } else {
                        values.push(value)
                    }
                }
            }
            if (valuesModeCostomConditions.indexOf(condition) > -1) {
                filter.valuesMode = "custom"
            } else if (valuesModeAutoConditions.indexOf(condition) > -1) {
                filter.valuesMode = "auto"
            }
            // filter.encloseInQuotes = false
            filter.values = values
        }
    }
    return filter
}

export const getDatePart = fullName => {
    let val = fullName.split(".").pop()
    if (val === "todate") return "date"
    if (val === "totime") return "time"
    if (val === "todatetime") return "datetime"
    return val
}

export const defaultAnchor = () => {
    return {
        anchorDate: moment().format("YYYY-MM-DD HH:mm:ss"),
        isAnchor: false,
        active: 1,
        relativePart: "",
        part: "",
        value: 0,
        direction: "",
        lastInput: 3,
        nextInput: 3
    }
}

export const createDbFuncObj = (func, databaseFunction = {}, column) => {
    let dbObj = {
        "functionName": func,
        "dataType": "text",
        "parameters": {
            "column": (databaseFunction != null && Object.keys(databaseFunction).length) ? databaseFunction : column,
        }
    };
    return dbObj;
}

export const createDbFunc = (filter, datePart, databaseFunctions, dateFunctions) => {
    let key = dateFunctions["dateTime"].find(item => item.part === datePart)?.key
    let dbFunc = null
    Object.keys(databaseFunctions).map(type => {
        databaseFunctions[type].map(item => {
            if (item.key === key) {
                dbFunc = item
            }
        })
    })
    if (dbFunc) {
        dbFunc.parameters = dbFunc.parameters.map(param => {
            param.value = filter.column
            return param
        })
    }
    filter.databaseFunction = dbFunc
    if (filter.mapping) {
        filter.mapping = { ...filter.mapping, DisplayDBFunction: dbFunc }
        filter.mapping = { ...filter.mapping, valueDBFunction: dbFunc }
    }
    return filter
}
export const addDateSpecific = (data) => {
    let { filter, dataType, databaseFunctionName, databaseFunctions, dateFunctions,drillownFilter } = data
    // let datePart = dataType === "time" ? "hour" : "year"
    let datePart = "individual"
    // datePart = drillownFilter ? "individual" : datePart
    if (databaseFunctionName) {
        switch (databaseFunctionName) {
            case "year":
                datePart = "year"
                break;
            case "hour":
                datePart = "hour"
                break;
            case "minute":
                datePart = "minute"
                break;
            case "second":
                datePart = "second"
                break;
            case "month":
                datePart = "month"
                break;
            case "day":
                datePart = "day"
                break; 
            case "quarter":
                datePart = "quarter"
                break;
            case "monthname":
                datePart = "month"
                break;
            case "datetime":
                datePart = "year"
                break;
            case "date":
                datePart = "date"
                // datePart = "year"
                break;
            case "time":
                datePart = "time"
                // datePart = "hour"
            case "maketime":
                datePart = "time"
            case "maketime":
                datePart = "time"
                break;
            case "today":
                datePart = "date"
            case "now":
                datePart = "individual"
            case "makedatetime":
                datePart = "individual"
            default:
                break;
        }
    } else {
        filter = createDbFunc(filter, datePart, databaseFunctions, dateFunctions)
    }
    let dateValuesType = "datePicker"
    filter = {
        ...filter, datePart,
        currentDate: (new Date()).toString(),
        condition: drillownFilter? filter.condition : "EQUALS", dateValuesType,
        anchor: defaultAnchor()
    }
    return filter
}

export const getFilterDataType = filter => {
    return filter.dataType
}
export const getCanvasFieldDataType = field => {
    let {type,databaseFunction} = field
    let {dataType} = type || {}
    if(databaseFunction && Object.keys(databaseFunction).length){
        dataType = databaseFunction.returns
    }
    return dataType
}

export const getFloatingType = field => {
    let { type, databaseFunction, aggregate, applyBeforeAggregate } = field
    let isMeasure = false, floatingType = "discrete"
    if (type?.dataType === "numeric") {
        isMeasure = true
        floatingType = "continous"
    }
    if (databaseFunction && Object.keys(databaseFunction).length && databaseFunction.returns === "numeric") {
        isMeasure = true
        floatingType = "continous"
    }
    if (aggregate && aggregate.length && !applyBeforeAggregate) {
        isMeasure = true
        floatingType = "continous"
    }
    if (field.floatingType) {
        floatingType = field.floatingType
    }
    if (floatingType === "continous") isMeasure = true
    return { isMeasure, floatingType }
}
export const getCurrentDate = ({ filter, dataType, valueDateFunc }) => {
    let currentDate = moment()
    if (filter.values[0] !== "_all_") {
        if (valueDateFunc === "individual") {
            let value = filter.values.length ? filter.values[0] : currentDate
            currentDate = moment(value)._d
            if (dataType === "dateTime") {
                currentDate = moment(value, "YYYY-MM-DD HH:mm:ss")
            }
            if (dataType === "date") {
                currentDate = moment(value, "YYYY-MM-DD")
            }
            if (dataType === "time") {
                currentDate = moment(value, "HH:mm:ss")
            }
        } else if (valueDateFunc === "date") {
            let value = filter.values.length ? filter.values[0] : moment(currentDate).format("YYYY-MM-DD")
            currentDate = moment(value, "YYYY-MM-DD")
        } else if (valueDateFunc === "time") {
            let value = filter.values.length ? filter.values[0] : moment(currentDate).format("HH:mm:ss")
            currentDate = moment(value, "HH:mm:ss")
        } else if (valueDateFunc === "year") {
            let value = filter.values.length ? filter.values[0] : moment(currentDate).year()
            currentDate = moment(value, "YYYY")
        } else if (valueDateFunc === "quarter") {
            let value
            if (filter.values.length) {
                value = filter.values[0]
                currentDate = moment(value, "QQ")
            } else {
                currentDate = moment()
            }
        } else if (valueDateFunc === "monthname") {
            let value = filter.values.length ? filter.values[0] : moment(currentDate).format("MMMM")
            currentDate = moment(value, "MMMM")
        } else if (valueDateFunc === "month") {
            let value = filter.values.length ? filter.values[0] : moment(currentDate).month() + 1
            currentDate = moment(value, "MM")
        } else if (valueDateFunc === "day") {
            let value = filter.values.length ? filter.values[0] : moment(currentDate).date()
            currentDate = moment().set("month",0).set("date",value) 
        } else if (valueDateFunc === "hour") {
            let value = filter.values.length ? filter.values[0] : moment(currentDate).hour()
            currentDate = moment(value, "HH")
        } else if (valueDateFunc === "minute") {
            let value = filter.values.length ? filter.values[0] : moment(currentDate).minute()
            currentDate = moment(value, "mm")
        } else if (valueDateFunc === "second") {
            let value = filter.values.length ? filter.values[0] : moment(currentDate).second()
            currentDate = moment(value, "ss")
        }
    }
    return currentDate
}

export const getRange = ({ filter, dataType, valueDateFunc }) => {
    let startDate = filter.values.length ? filter.values[0] : ""
    let endDate = filter.values.length ? filter.values[1] : ""
    try {
        if (valueDateFunc === "individual") {
            if (dataType === "time") {
                startDate = startDate ? moment(startDate, "HH:mm:ss") : moment()
                endDate = endDate ? moment(endDate, "HH:mm:ss") : moment()
            } else {
                startDate = startDate ? moment(startDate) : moment()
                endDate = endDate ? moment(endDate) : moment()
            }
        } else if (valueDateFunc === "date") {
            startDate = startDate ? moment(startDate, "YYYY-MM-DD") : moment()
            endDate = endDate ? moment(endDate, "YYYY-MM-DD") : moment()
        } else if (valueDateFunc === "time") {
            startDate = startDate ? moment(startDate, "HH:mm:ss") : moment()
            endDate = endDate ? moment(endDate, "HH:mm:ss") : moment()
        } else if (valueDateFunc === "year") {
            startDate = startDate ? moment(startDate, "YYYY") : moment()
            endDate = endDate ? moment(endDate, "YYYY") : moment()
        } else if (valueDateFunc === "quarter") {
            startDate = startDate ? moment(startDate, "QQ") : moment()
            endDate = endDate ? moment(endDate, "QQ") : moment()
        } else if (valueDateFunc === "monthname") {
            startDate = startDate ? moment(startDate, "MMMM") : moment()
            endDate = endDate ? moment(endDate, "MMMM") : moment()
        } else if (valueDateFunc === "month") {
            startDate = startDate ? moment(startDate, "MM") : moment()
            endDate = endDate ? moment(endDate, "MM") : moment()
        } else if (valueDateFunc === "day") {
            startDate = startDate ? moment(startDate, "DD") : moment()
            endDate = endDate ? moment(endDate, "DD") : moment()
        } else if (valueDateFunc === "hour") {
            startDate = (startDate || startDate >= 0) ? moment(startDate, "HH") : moment()
            // startDate = startDate ? moment(startDate, "HH") : moment()
            endDate = endDate ? moment(endDate, "HH") : moment()
        } else if (valueDateFunc === "minute") {
            startDate = (startDate || startDate >= 0) ? moment(startDate, "mm") : moment()
            // startDate = startDate ? moment(startDate, "mm") : moment()
            endDate = endDate ? moment(endDate, "mm") : moment()
        } else if (valueDateFunc === "second") {
            startDate = (startDate || startDate >= 0) ? moment(startDate, "ss") : moment()
            // startDate = startDate ? moment(startDate, "ss") : moment()
            endDate = endDate ? moment(endDate, "ss") : moment()
        }
    } catch (e) {
    }
    return { startDate, endDate }
}

export const getRelativePartList = (dataType, displayDateFunc, datePart) => {
    try {
        dataType = displayDateFunc === "date" ? datePart : dataType
        dataType = displayDateFunc === "time" ? datePart : dataType
        const list = [
            { label: "Days", value: "day" },
            { label: "Months", value: "month" },
            { label: "Quarters", value: "quarter" },
            { label: "Years", value: "year" },
            { label: "Seconds", value: "second" },
            { label: "Minutes", value: "minute" },
            { label: "Hours", value: "hour" },
        ]
        if (dataType === "dateTime") {
            return list
        } else if (dataType === "date") {
            return list.slice(0, 4)
        } else if (dataType === "time") {
            return list.slice(4)
        }
        return list
    } catch (e) {
    }
}

export const getRelativeList = ({filter, dataType, selectedPart,isAnchor}) => {
    try {
        let { datePart } = filter
        if (selectedPart) datePart = selectedPart
        if (datePart === "individual") {
            datePart = dataType === "time" ? "hour" : "year"
        } else if (datePart === "date") {
            datePart = "year"
        } else if (datePart === "time") {
            datePart = "hour"
        }
        let list = []
        switch (datePart) {
            case "year":
                list = [
                    { label: `${isAnchor ? "Anchor": "This"} Year`, val: 0, part: "years" },
                    { label: `${isAnchor ? "Anchor" : ""} Next Year`, type: "next", val: 1, part: "years" },
                    { label: `${isAnchor ? "Anchor" : ""} Previous Year`, type: "last", val: 1, part: "years" },
                    { label1: `${isAnchor ? "Anchor" : ""} Next`, val: "custom", type: "next", part: "years", label2: "Years" },
                    { label1: `${isAnchor ? "Anchor" : ""} Last`, val: "custom", type: "last", part: "years", label2: "Years" },
                    { label: `${isAnchor ? "Year to Anchor" : "Year to Date"} `, type: "last", val: "toDate", part: "years" },
                ]
                break;
            case "quarter":
                list = [
                    { label: `${isAnchor ? "Anchor": "This"} Quarter`, val: 0, part: "quarters" },
                    { label: `${isAnchor ? "Anchor" : ""} Next Quarter`, type: "next", val: 1, part: "quarters" },
                    { label: `${isAnchor ? "Anchor" : ""} Previous Quarter`, type: "last", val: 1, part: "quarters" },
                    { label1: `${isAnchor ? "Anchor" : ""} Next`, val: "custom", type: "next", part: "quarters", label2: "Quarters" },
                    { label1: `${isAnchor ? "Anchor" : ""} Last`, val: "custom", type: "last", part: "quarters", label2: "Quarter" },
                    { label: `${isAnchor ? "Quarter to Anchor" : "Quarter to Date"} `, type: "last", val: "toDate", part: "years" },
                ]
                break;
            case "month":
                list = [
                    { label: `${isAnchor ? "Anchor": "This"} Month`, val: 0, part: "months" },
                    { label: `${isAnchor ? "Anchor" : ""} Next Month`, type: "next", val: 1, part: "months" },
                    { label: `${isAnchor ? "Anchor" : ""} Previous Month`, type: "last", val: 1, part: "months" },
                    { label1: `${isAnchor ? "Anchor" : ""} Next`, val: "custom", type: "next", part: "months", label2: "Months" },
                    { label1: `${isAnchor ? "Anchor" : ""} Last`, val: "custom", type: "last", part: "months", label2: "Months" },
                    { label: `${isAnchor ? "Month to Anchor" : "Month to Date"} `, type: "last", val: "toDate", part: "years" },
                ]
                break;
            case "day":
                list = [
                    { label: isAnchor ? "Anchor day" : "Today", val: 0, part: "days" },
                    { label: isAnchor ? "Anchor Next day" : "Tomorrow", type: "next", val: 1, part: "days" },
                    { label: isAnchor ? "Anchor Previous day" :"Yesterday", type: "last", val: 1, part: "days" },
                    { label1: isAnchor ? "Anchor Next" : "Next", val: "custom", type: "next", part: "days", label2: "Days" },
                    { label1: isAnchor ? "Anchor Last" : "Last", val: "custom", type: "last", part: "days", label2: "Days" },
                    { label: isAnchor ? "Day to Anchor" : "Day to Date", type: "last", val: "toDate", part: "years" },
                ]
                break;
            case "hour":
                list = [
                    { label: `${isAnchor ? "Anchor": "This"} Hour`, val: 0, part: "hours" },
                    { label: `${isAnchor ? "Anchor" : ""} Next Hour`, type: "next", val: 1, part: "hours" },
                    { label: `${isAnchor ? "Anchor" : ""} Previous Hour`, type: "last", val: 1, part: "hours" },
                    { label1: `${isAnchor ? "Anchor" : ""} Next`, val: "custom", type: "next", part: "hours", label2: "Hours" },
                    { label1: `${isAnchor ? "Anchor" : ""} Last`, val: "custom", type: "last", part: "hours", label2: "Hours" },
                    { label: `${isAnchor ? "Hour to Anchor" : "Hour to Date"} `, type: "last", val: "toDate", part: "years" },
                ]
                break;
            case "minute":
                list = [
                    { label: `${isAnchor ? "Anchor": "This"} Minute`, val: 0, part: "minutes" },
                    { label: `${isAnchor ? "Anchor" : ""} Next Minute`, type: "next", val: 1, part: "minutes" },
                    { label: `${isAnchor ? "Anchor" : ""} Previous Minute`, type: "last", val: 1, part: "minutes" },
                    { label1: `${isAnchor ? "Anchor" : ""} Next`, val: "custom", type: "next", part: "minutes", label2: "Minutes" },
                    { label1: `${isAnchor ? "Anchor" : ""} Last`, val: "custom", type: "last", part: "minutes", label2: "Minutes" },
                    { label: `${isAnchor ? "Minute to Anchor" : "Minute to Date"} `, type: "last", val: "toDate", part: "years" },
                ]
                break;
            case "second":
                list = [
                    { label: `${isAnchor ? "Anchor": "This"} Second`, val: 0, part: "seconds" },
                    { label: `${isAnchor ? "Anchor" : ""} Next Second`, type: "next", val: 1, part: "seconds" },
                    { label: `${isAnchor ? "Anchor" : ""} Previous Second`, type: "last", val: 1, part: "seconds" },
                    { label1: `${isAnchor ? "Anchor" : ""} Next`, val: "custom", type: "next", part: "seconds", label2: "Seconds" },
                    { label1: `${isAnchor ? "Anchor" : ""} Last`, val: "custom", type: "last", part: "seconds", label2: "Seconds" },
                    { label: `${isAnchor ? "Second to Anchor" : "Second to Date"} `, type: "last", val: "toDate", part: "years" },
                ]
                break;
            default:
                list = [
                    { label: "Today", val: 0, part: "days" },
                    { label: "Tomorrow", type: "next", val: 1, part: "days" },
                    { label: "Yesterday", type: "last", val: 1, part: "days" },
                    { label1: "Next", val: "custom", type: "next", part: "days", label2: "Days" },
                    { label1: "Last", val: "custom", type: "last", part: "days", label2: "Days" },
                    { label: "Day to Date", type: "last", val: "toDate", part: "years" },
                ]
                break;
        }
        return list
    } catch (e) {
    }
}


export const addFilterValue = (filter, value) => {
    return produce(filter, draft => {
        let { condition, values } = draft
        if(value === "_all_"){
            values = values.includes("_all_") ? [] : ["_all_"]
        }else if (Array.isArray(value)) {
            values = value
        } else {
            let currentIndex = values.indexOf(value)
            if (singleValueConditions.includes(condition)) {
                values = [value]
            } else if (mulipleValueConditions.includes(condition)) {
                if (currentIndex > -1) {
                    values.splice(currentIndex, 1)
                } else {
                    values.push(value)
                }
            }
        }
        // draft.encloseInQuotes = false
        draft.values = values
        draft.dataId = uuidv4()
    })
}


export const getColumnbject = ({
    fullyQualifiedColumn, columnFunction, type, isAggregated, defaultAggregate, isMappingCreate, tables }) => {
    let segregatedColumns = {}
    let onlyColumns = []
    _.map(tables, function (obj) {
        segregatedColumns[obj.name] = { columns: obj.columns }
        onlyColumns.push(Object.values(obj.columns))
    })
    onlyColumns = _.flatten(onlyColumns)
    if (type == 'getAllColumns') {
        return onlyColumns
    }
    if (type == 'getColumnObj') {
        let result = _.find(onlyColumns, (obj) => obj.fullyQualifiedColumn === fullyQualifiedColumn)
        result = { ...result }
        if (isMappingCreate) {
            if (defaultAggregate && result) {
                result.defaultFunction = defaultAggregate
            }
            return result
        }
        if (!isAggregated && result && result.defaultFunction && result.defaultFunction.split('.').indexOf('aggregate') != -1) {
            delete result.defaultFunction
        }
        if (columnFunction && columnFunction != 'None' && columnFunction.length) {
            result.defaultFunction = columnFunction
        }
        else {
            if (result && !isAggregated) {
                delete result.defaultFunction
            }
        }
        return result
    }
}

export const getDefaultDisplayDateFormats = () => {
    const dateFormats = [
        { key: 'HH:mm:ss', label: 'HH:mm:ss', example: '05:06:07' },
        { key: 'HH', label: 'HH', example: '05' },
        { key: 'mm', label: 'mm', example: '06' },
        { key: 'ss', label: 'ss', example: '07' },
        { key: 'DD', label: 'DD', example: '05' },
        { key: 'MMM', label: 'MMM', example: 'Aug' },
        { key: '[Q]Q', label: '[Q]Q', example: 'Q3' },
        { key: 'YYYY', label: 'YYYY', example: '2024' },
        { key: 'YYYY-MM-DD', label: 'YYYY-MM-DD', example: '2024-06-05' },
        { key: 'MM/DD/YYYY', label: 'MM/DD/YYYY', example: '06/05/2024' },
        { key: 'DD/MM/YYYY', label: 'DD/MM/YYYY', example: '05/06/2024' },
        { key: 'YYYY/MM/DD', label: 'YYYY/MM/DD', example: '2024/06/05' },
        { key: 'DD-MM-YYYY', label: 'DD-MM-YYYY', example: '05-06-2024' },
        { key: 'DD.MM.YYYY', label: 'DD.MM.YYYY', example: '05.06.2024' },
        { key: 'YYYY.MM.DD', label: 'YYYY.MM.DD', example: '2024.06.05' },
        { key: 'MMM DD, YYYY', label: 'MMM DD, YYYY', example: 'Jun 05, 2024' },
        { key: 'MDD, YYYY', label: 'MMMM DD, YYYY', example: 'June 05, 2024' },
        { key: 'DD MMMMM M YYYY', label: 'DD MMM YYYY', example: '05 Jun 2024' },
        { key: 'DD MMMM YYYY', label: 'DD MMMM YYYY', example: '05 June 2024' },
        { key: 'dddd, MMMM DD, YYYY', label: 'dddd, MMMM DD, YYYY', example: 'Wednesday, June 05, 2024' },
        { key: 'MMM DD', label: 'MMM DD', example: 'Jun 05' },
        { key: 'DD MMM', label: 'DD MMM', example: '05 Jun' },
        { key: 'DD-MM-YYYY HH:mm:ss', label: 'DD-MM-YYYY HH:mm:ss', example: '05-06-2024 14:30:00' },
        { key: 'YYYY-MM-DDTHH:mm:ssZ', label: 'YYYY-MM-DDTHH:mm:ssZ', example: '2024-06-05T14:30:00Z' }
    ];

    return dateFormats;
}

export const getDBFunctionObject = ({ value, defaultReturn, databaseFunctions }) => {
    let dbObj;
    databaseFunctions = _.flatten(Object.values(databaseFunctions))
    try {
        databaseFunctions.map((obj) => {
            if (obj.key == value) {
                dbObj = obj

            }
        })
        return dbObj
    }
    catch (err) {
        return defaultReturn
    }
}


export const getInitialMapping = ({ filter, defaultValueDisplayMap, database, databaseFunctions, tables,orderBy, isCube }) => {
    let DBkey = (filter.databaseFunction && filter.databaseFunction.value) || ""
    let valueDBFunction = defaultValueDisplayMap[DBkey]
    valueDBFunction = getDBFunctionObject({ value: valueDBFunction, defaultReturn: false, databaseFunctions })
    let DisplayDBFunction = DBkey
    DisplayDBFunction = getDBFunctionObject({ value: DisplayDBFunction, defaultReturn: false, databaseFunctions })
    let intialOrderBy = (!orderBy || !orderBy.length) ? {
        display: 'asc',
        value: 'none'
    } : {
        display: orderBy,
        value: 'none' 
    }
    return {
        "isEnabled": true,
        unique:true,
        "valueDBFunction": valueDBFunction ? extractDatabaseFunctions(fromJS(valueDBFunction), `${database}.${filter.column}`) : filter.databaseFunction, //-- commented for replacing function nem with function object
        "DisplayDBFunction": DisplayDBFunction ? extractDatabaseFunctions(fromJS(DisplayDBFunction), `${database}.${filter.column}`) : filter.databaseFunction, //-- commented for replacing function nem with function object
        "isDefaultFunction": true,
        "valueDisplayMap": [],
        "valueAliasName": "random",
        "orderBy": intialOrderBy,
        valueDBFuntionInfo: DisplayDBFunction || {},
        "valueColumn": getColumnbject({
            tables,
            type: 'getColumnObj',
            fullyQualifiedColumn: filter.column,
            isAggregated: filter.aggregate ? true : true,
            isMappingCreate: true,
            defaultAggregate: filter.groupBy && filter.groupBy.length ? filter.groupBy[0] :
                filter.aggregate && filter.aggregate.length ? filter.aggregate[0] : 'none'
        }),
        // isCube ? {
        //     alias: "dim_id",
        //     columnId: "26580d9f-f17e-4f69-9b95-2005885b4e47",
        //     defaultFunction: "none",
        //     fullyQualifiedColumn: "dimdate.dim_id"
        // } :
        "displayColumn":  getColumnbject({
            tables,
            type: 'getColumnObj',
            fullyQualifiedColumn: filter.column,
            isAggregated: filter.aggregate ? true : true,
            isMappingCreate: true,
            defaultAggregate: filter.groupBy && filter.groupBy.length ? filter.groupBy[0] :
                filter.aggregate && filter.aggregate.length ? filter.aggregate[0] : 'none'
        }),
    }

}

// const setParameters = (parameters,formData,callbackFn) => {
//     return parameters?.reduce((accumulator, param) => {
//         accumulator[param.name] = param.column ?
//             typeof (formData.columns[0].column) == "object" ?
//                 formData.columns[0].column?.name
//                 : formData.columns[0].column
//             : param.value ?
//                 typeof param.value === "object" ? callbackFn(param?.value,formData,callbackFn)
//                     : param.value
//                 :
//                 param.defaultValue
//         return accumulator
//     }, {})
// }

export const setParameters = (parameters, callbackFn) => {
    return parameters?.reduce((accumulator, param) => {
        accumulator[param.name] = param.column ?
            param?.value
            : param.value ?
                typeof param.value === "object" ? callbackFn(param?.value, callbackFn)
                    : param.value
                :
                param.defaultValue
        return accumulator
    }, {})
}


const setDatabaseFunction = (dbFunction) => {
    return {
        functionName: dbFunction.key,
        dataType: dbFunction.returns,
        // parameters:setParameters(dbFunction?.parameters,formData,setDatabaseFunction)
        parameters:setParameters(dbFunction?.parameters,setDatabaseFunction)
    }
}


export const filterSearchFormdata = ({filter,formData}) => {
    let { mapping,dataType,column } = filter
    let displayDBFunc = "";
    let databaseFunction = {}
    if (mapping && mapping.DisplayDBFunction) {
        displayDBFunc = getDatePart(mapping.DisplayDBFunction.key)
        // let parameters = mapping.DisplayDBFunction.parameters.reduce((accumulator, param) => {
        //     accumulator[param.name] = param.column ? typeof (formData.columns[0].column) == "object" ? formData.columns[0].column?.name : formData.columns[0].column : param.value ? param.value :
        //         param.defaultValue
        //     return accumulator
        // }, {})
        // databaseFunction = {
        //     functionName: mapping.DisplayDBFunction.key,
        //     dataType: mapping.DisplayDBFunction.returns,
        //     parameters
        // }

        databaseFunction = setDatabaseFunction(mapping.DisplayDBFunction,formData)
    }
    if (displayDBFunc === "monthname") {
        databaseFunction = {
            dataType: "text",
            functionName: mapping.DisplayDBFunction.key,
            parameters: {
                [mapping.DisplayDBFunction.parameters[0].name]: mapping.displayColumn.fullyQualifiedColumn
            }
        }
    }
    if (Object.keys(databaseFunction).length) {
        dataType = databaseFunction.dataType || databaseFunction.returns
    }
    if (!dataType.match(new RegExp(["text"].join("|"), "i"))) {
        databaseFunction = createDbFuncObj("sql.text." + dataType + "ToString", databaseFunction,
            `${column}`.replace(/^\./, "")
        );
    }
    if (Object.keys(databaseFunction).length) {
        // formData.filters[0].databaseFunction = databaseFunction
        formData.filters[filter.cascade.isEnabled ? 1 : 0].databaseFunction = databaseFunction
    }
    return formData
}

export const fetchFilterValues = (data,dispatch,getApi) => {
    const Notify = notify(dispatch);
    let {
        filter,filters,debouncedSearchTerm,dateFunctions,metadata,reportId,returnFormData,databaseFunctions
    } = data
    const { columnID } = filter || {}
    
    try{
        let {name,formData} = metadata || {};
        name = name || filter.columnDatabase; 
        let tempFilter = { ...filter };
        let { mapping, cascade, datePart,uid } = tempFilter;
        dispatch(loadFilterValues({ values: filter.values, uid, reportId, loading: true }));
        tempFilter.alias = "display";
        let query = new ReportQuery({...formData,useDBFuntion:databaseFunctions}, dispatch);
        if (mapping.isEnabled) {
          let { orderBy } = mapping;
          let displayColumn = {
            ...mapping.displayColumn,
            alias: "display",
            databaseFunction: mapping.DisplayDBFunction,
            orderBy: [orderBy.display],
            columnID
          };
          let valueColumn = {
            ...mapping.valueColumn,
            alias: "value",
            databaseFunction: mapping.valueDBFunction,
            orderBy: [orderBy.value],
            columnID
          };
          if (displayColumn.defaultFunction !== "none") {
            if (displayColumn.defaultFunction.includes("groupBy")) {
              displayColumn.groupBy = [mapping.displayColumn.defaultFunction];
            }
            if (displayColumn.defaultFunction.includes("aggregate")) {
              displayColumn.aggregate = [mapping.displayColumn.defaultFunction];
            }
          }
          if (valueColumn.defaultFunction !== "none") {
            if (valueColumn.defaultFunction.includes("groupBy")) {
              valueColumn.groupBy = [mapping.valueColumn.defaultFunction];
            }
            if (valueColumn.defaultFunction.includes("aggregate")) {
              valueColumn.aggregate = [mapping.valueColumn.defaultFunction];
            }
          }
          let datePartList = dateFunctions["dateTime"];
          let dateFunc = datePartList.find((func) => func.part === datePart);
          if (!displayColumn.databaseFunction && datePart && datePart !== "individual") {
            if (dateFunc && dateFunc.key) {
              displayColumn.databaseFunction = dateFunc;
            }
          }
          if (!valueColumn.databaseFunction && datePart && datePart !== "individual") {
            if (dateFunc && dateFunc.key) {
              valueColumn.databaseFunction = dateFunc;
            }
          }
          selectBuilder(displayColumn, name, query);
          selectBuilder(valueColumn, name, query);
          if (cascade.isEnabled) {
            cascade.filters.map((fltr) => {
              let { uid, condition } = fltr;
              let cascadeFilter = filters.find((cascadeFilter) => cascadeFilter.uid === uid);
              if (cascadeFilter) {
                let tempFilter = { ...cascadeFilter };
                whereBuilder({field:tempFilter, query, filterCondition:condition,database:name});
    
                // query.select("HIUSER.travel_details.source", "source")
                // query.where("source", "IN"  , ['Ahmedabad'])
              }
            });
          }
        }
        if (debouncedSearchTerm) {
          query.where("display", "LIKE", debouncedSearchTerm);
        }
        query.limit(-1);
        return new Promise((resolve) => {
            query
              .reportFormData()
              .manipulateFormData((formData) => {
                if (debouncedSearchTerm) {
                  formData = filterSearchFormdata({ filter, formData, dateFunctions });
                }
                if(mapping.unique){
                    formData.distinctResults = true;
                }
                if (returnFormData){
                    resolve(formData)
                }
                return formData;
              })
              .execute({
                getApi:getApi,
                callBack: (res) => {
                  dispatch(loadFilterValues({ values: res.data, uid, reportId, loading: false }));
                },
                errorBack: (err) => {
                    dispatch(loadFilterValues({ values: [], uid, reportId, loading: false }))
                    if (err?.message) {
                        Notify.error({ type: "Back end", message: err?.message });
                    }
                },
              });
        })
    }catch(e){
        Notify.error({message:e.message})
    }
  };

const checkForDistinctAndCountFnApplied = (aggregateList) => {
    if (aggregateList && aggregateList?.length > 0 && aggregateList?.length <= 2) {
        let fns = []
        if(aggregateList?.length === 2){
            fns = aggregateList?.map((item) => {
                if (item?.includes("count") || item?.includes("distinct")) {
                    return true;
                }
            })
        }
        if(aggregateList?.length === 1){
            fns = aggregateList?.map((item) => {
                if (item?.includes("count")) {
                    return true;
                }
            })
        }
        return fns?.every((item) => item === true)
    }
    return false
}  
 
  export const getMinMaxValues = (data,dispatch) => {
    const Notify = notify(dispatch);
    let { filter,metadata,reportId,returnFormData,databaseFunctions,filters } = data
    const { aggregate= [] } = filter || {}
    let {name,formData} = metadata
    name = name || filter.columnDatabase; 
    let tempFilter = { ...filter };
    let { mapping,uid,cascade,columnID } = tempFilter;
    let query = new ReportQuery({...formData,useDBFuntion:databaseFunctions}, dispatch);
    if (mapping.isEnabled) {
      let minColumn = {
        ...mapping.displayColumn,
        alias: "min",
        databaseFunction: mapping.DisplayDBFunction,
        orderBy: ["asc"],
        columnID:columnID
      };
      let maxColumn = {
        ...mapping.displayColumn,
        alias: "max",
        databaseFunction: mapping.DisplayDBFunction,
        orderBy: ["asc"],
        columnID:columnID
      };
      minColumn.aggregate = ['db.generic.aggregate.min'];
    //   maxColumn.aggregate = ['db.generic.aggregate.max'];
        if (checkForDistinctAndCountFnApplied(aggregate)) { // added this function and check for bug 6531, if distinct and count together applied to filter
            maxColumn.aggregate = ['db.generic.aggregate.count'];
        } else {
            maxColumn.aggregate = ['db.generic.aggregate.max'];
        }
      selectBuilder(minColumn, name, query);
      selectBuilder(maxColumn, name, query);
      if (cascade.isEnabled) {
        cascade.filters.map((fltr) => {
          let { uid, condition } = fltr;
          let cascadeFilter = filters.find((cascadeFilter) => cascadeFilter.uid === uid);
          if (cascadeFilter) {
            let tempFilter = { ...cascadeFilter };
            whereBuilder({field:tempFilter, query, filterCondition:condition,database:name});

            // query.select("HIUSER.travel_details.source", "source")
            // query.where("source", "IN"  , ['Ahmedabad'])
          }
        });
      }
    }
    query.limit(-1);
    if(returnFormData){
        return new Promise((resolve)=>{
            resolve(query.reportFormData({show:true}).formData)
        })
    }
    return new Promise(() => {
        query
          .reportFormData()
          .execute({
            callBack: (res) => {
                let { data } = res;
                  dispatch(loadValuesRange({ valuesRange: { min: checkForDistinctAndCountFnApplied(aggregate) ? 1 : data[0].min, max: data[0].max }, uid, reportId }));
            //   dispatch(loadValuesRange({ valuesRange: { min: data[0].min, max: data[0].max }, uid,reportId }));
            },
            errorBack: (err) => {
              Notify.error({ type: "Back end", message: err.message });
            },
          });
    })
  };

export const getRelativeDate = (filter) => {
        const { anchor: { isAnchor, anchorDate, value, direction, part, relativePart }, condition, dataType, datePart, mapping } = filter || {}
        let displayDateFunc = datePart, valueDateFunc = datePart
        if (mapping && mapping.DisplayDBFunction && mapping.DisplayDBFunction.functionName) {
            let val = mapping.DisplayDBFunction.functionName
            displayDateFunc = getDatePart(val)
        }
        if (mapping && mapping.valueDBFunction && mapping.valueDBFunction.functionName) {
            valueDateFunc = getDatePart(mapping.valueDBFunction.functionName)
        }
        if (funcMap.indexOf(displayDateFunc) === -1) return filter;
        let { toolTipFormat } = datePickerFormat(filter, dataType);
        let isRange = rangeConditions.includes(condition);
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
        if (isRange) {
            const [startDate, endDate] = changeRangeDateTime(startValue, endValue, filter)
            return {
                ...filter,
                values: [startDate, endDate]
            }
        } else {
            let dateValue = createEffectedDate(dateNow, value, part, direction, toolTipFormat, relativePart, "start")
            const dateTime = changeEqualDateTime(dateValue, valueDateFunc, dataType)
            return {
                ...filter,
                values: [dateTime]
            }
        }
}

const getDateNow = (value, datePart) => {
    value = Array.isArray(value) ? value[0] : value;
    let format = momentDateFormat.get(datePart) || "YYYY-MM-DD HH:mm:ss";
    let date =  moment(value,format)
    if(date?.isValid()){
        return date
    }else{
        return moment(value, "YYYY-MM-DD HH:mm:ss");
    }
}

export const getDateFromValue = (val, filter, index, returnRelativeDateParts = false) => {
    if(typeof val !== 'string') return val;
    let tempValue = val?.replace(/\s/g, '+');
    const regex = relativeDateRegex;
    const match = tempValue.match(regex);
    if (!match) return val;
    const { anchor: { part: datePart, relativePart: dateRelativePart, isAnchor: isDateAnchor, anchorDate: anchorDateStr }, condition } = filter || {}
    let isRange = rangeConditions.includes(condition);
    let dateNow = moment()._d,
        value = 0,
        part = null,
        direction = undefined,
        relativePart = null,
        isAnchor = isDateAnchor,
        anchorDate = anchorDateStr
    const [, anchorPart, base, modifier] = match;
    if(!isRange && base === "to_date"){
        return val
    }
    switch (base) {
        case 'today':
            part = "days";
            relativePart = "day";
            break;
        case 'tomorrow':
            part = "days";
            relativePart = "day";
            direction = "next";
            value = 1
            break;
        case 'yesterday':
            part = "days";
            relativePart = "day";
            direction = "last";
            value = 1;
            break;
        case 'month':
            part = "months";
            relativePart = "month";
            break;
        case 'year':
            part = "years";
            relativePart = "year";
            break;
        case 'quarter':
            part = "quarters";
            relativePart = "quarter";
            break;
        case 'hour':
            part = "hours";
            relativePart = "hour";
            break;
        case 'minute':
            part = "minutes";
            relativePart = "minute";
            break;
        case 'second':
            part = "seconds";
            relativePart = "second";
            break;
        case 'to_date':
            part = datePart;
            direction = "last";
            relativePart = dateRelativePart;
            break
        default:
            return null;
    }
    if (modifier) {
        const operator = modifier[0];
        const dateValue = parseInt(modifier.slice(1));
        if (operator === '+' && !["tomorrow","yesterday","to_date"].includes(base)) {
            direction = "next";
            value = dateValue
        } else if (operator === '-' && !["tomorrow","yesterday","to_date"].includes(base)) {
            direction = "last";
            value = dateValue
        }
    }
    if(anchorPart){
        isAnchor = true;
        // dateNow = isArray(anchorDate) ? moment(new Date(anchorDate?.[0])) : anchorDate;
        dateNow = getDateNow(anchorDate, relativePart);
    }
    if (returnRelativeDateParts) {
        return {
            value,
            part,
            direction,
            relativePart,
            isAnchor,
            anchorDate
        }
    }
    const {dataType} = filter || {}
    let { toolTipFormat } = datePickerFormat(filter, dataType);
    let isTodate = base === "to_date";
    let dateValue = createEffectedDate(dateNow, value, part, direction, toolTipFormat, relativePart, isTodate ? "now" : index === 2 ? "end" : "start")
    return moment(dateValue).format(toolTipFormat);
}

export const matchDateStr = (str) => {
    if(!str || typeof str !== 'string') return false;
    let tempValue = str?.replace(/\s/g, '+');
    const regex = relativeDateRegex;
    const match = tempValue.match(regex);
    return match
}

export const checkIsObject = (obj) => {
    return typeof obj === 'object' && obj !== null
}

export const compareObjects = (obj1, obj2) => {
    if (obj1.value === obj2.value) {
      return obj1;
    } else if (obj1.value > obj2.value) {
      return obj1;
    } else {
      return obj2;
    }
  }

export const prepareRelativeOptionFromAnchor = (data={}) => {
    const  {anchor = {}, isRange = false, index = 1, actualValue} = data
    const {
        relativePart = "",
        value = 0,
        direction = "",
        isAnchor
    } = anchor;
    // if(isAnchor) return actualValue;
    if(!relativePart && !matchDateStr(actualValue)) return actualValue;
    let operator = '';
    if(value === "toDate" && isRange){
        if(index === 1) return relativePart;
        if(index === 2) return 'to_date'
    }
    let numericValue = parseInt(value);
    if (direction === 'last') {
        operator = '-';
    } else if (direction === 'next') {
        operator = '+';
    }
    let relativeOption = relativePart;
    if(relativePart === "day") relativeOption = "today";
    if(isRange){
        if(index === 1){
            if(isAnchor){
                relativeOption  = 'anchor_' + relativeOption;
            }
            if(direction === "next" && value > 1 ){
                relativeOption += operator + 1
                return relativeOption
            }
            if(operator) {
                relativeOption += operator + numericValue;
            }
            return relativeOption;
        }
        if(index === 2){
            if(direction === "last" && value > 1){
                relativeOption += operator + 1
                return relativeOption
            }
        }
    }
    if (operator) {
        relativeOption += operator + numericValue;
    }
    if(isAnchor){
        relativeOption  = 'anchor_' + relativeOption;
    }
    return relativeOption;
}

export const getRelativeAnchor = (returnObj) => {
    if(typeof returnObj === "string") return returnObj
    const { value: fValue, part, direction, relativePart, isAnchor, anchorDate  } = returnObj || {}
    let anchor = { value: fValue, direction, relativePart, part, isAnchor, anchorDate }
    if (fValue > 1) {
        let lastInput = 3,
            nextInput = 3,  
            active = 1
        if (direction === "last") {
            lastInput = fValue;
            active = 5
        }
        if (direction === "next") {
            nextInput = fValue;
            active = 4
        }
        anchor = { ...anchor, nextInput, lastInput, active }
    } else {
        let active = 1
        if (direction === "last" && fValue === 1) {
            active = 3
        }
        if (direction === "next" && fValue === 1) {
            active = 2
        }
        anchor = { ...anchor, active }
    }
    return anchor
}

export const updateFilterAnchorDate = (anchor, parameters, filterName) => {
    let newAnchor = { ...anchor };
    if (`${anchorConstant}${filterName}` in parameters) {
        let newAnchorDate = parameters[`${anchorConstant}${filterName}`];
        newAnchor = {
            ...newAnchor,
            anchorDate: newAnchorDate
        };
    }
    return newAnchor;
}

export const updateRelativeDateAnchor = (filter, parameters = {}, isRange) => {
    let value = parameters[getFieldDisplayName(filter)];
    if (!value) return filter;
    let { anchor = {} } = filter || {};
    anchor = updateFilterAnchorDate(anchor, parameters, getFieldDisplayName(filter));
    filter.anchor = anchor;
    if (isRange) {
        let val1, val2;
        if (Array.isArray(value)) {
            val1 = value[0];
            val2 = value[1];
        }
        if (val2 === "to_date") {
            const returnValue = getDateFromValue(val1, filter, 1, true)
            if (typeof returnValue === "object") {
                const tempAnchor = getRelativeAnchor(returnValue)
                filter.anchor = { ...anchor, ...tempAnchor, active: 6 }
            }
        } else {
            const returnValue1 = getDateFromValue(val1, filter, 1, true)
            const returnValue2 = getDateFromValue(val2, filter, 1, true)
            if (checkIsObject(returnValue1) || checkIsObject(returnValue2)) {
                const tempAnchor1 = getRelativeAnchor(returnValue1)
                const tempAnchor2 = getRelativeAnchor(returnValue2)
                if (checkIsObject(tempAnchor1) && checkIsObject(tempAnchor2)) {
                    const anchorResult = compareObjects(tempAnchor1, tempAnchor2)
                    filter.anchor = { ...anchor, ...anchorResult }
                }
            }
        }

    } else {
        value = Array.isArray(value) ? value[0] : value;
        let returnValue = getDateFromValue(value, filter, 1, true)
        if (typeof returnValue === "object") {
            const tempAnchor = getRelativeAnchor(returnValue)
            filter.anchor = { ...anchor, ...tempAnchor }
        }
    }
    return filter
}

export const getRelativeDateFromValue = (value, filter) => {
    if (Array.isArray(value)) {
        return value?.map((val, index) => {
            let newValue = typeof val === "string" ? getDateFromValue(val, filter, index + 1) : val;
            return newValue
        })
    } else {
        return typeof value === "string" ? [getDateFromValue(value, filter)] : [value]
    }
}

export const checkIsValidDateFilter = (filter) => {
    if (!filter) return false
    const validTypes = ["dateTime", "date"]
    const isValidType = validTypes.includes(filter?.dataType)
    return isValidType || filter?.datePart
}

export const isDateFilter = (filter) => {
    return checkIsValidDateFilter(filter) && filter?.dateValuesType !== "select"
}

export const checkRelativeDateFilter = (filters = [], parameters) => {
    if (!filters?.length) return filters
    return filters?.map((filter) => {
        if (checkIsValidDateFilter(filter)) {
            const { values: filterValues, condition } = filter || {}
            let isRange = rangeConditions.includes(condition);
            filter = updateRelativeDateAnchor(filter, parameters, isRange);
            const newValues = getRelativeDateFromValue(filterValues, filter);
            filter = {...filter, values : newValues }
        }
        return filter
    })
}

export const changeRangeDateTime = (startDate, endDate, filter) => {
    let { datePart, dataType } = filter
    switch (datePart) {
        case dateTypes.INDIVIDUAL:
            switch (dataType) {
                case dateTypes.DATETIME:
                    startDate = startDate.format(dateFormats.DATETIME)
                    endDate = endDate.format(dateFormats.DATETIME)
                    break;
                case dateTypes.DATEANDTIME:
                    startDate = startDate.format(dateFormats.DATETIME)
                    endDate = endDate.format(dateFormats.DATETIME)
                    break;
                case dateTypes.DATE:
                    startDate = startDate.format(dateFormats.DATE)
                    endDate = endDate.format(dateFormats.DATE)
                    break;
                case dateTypes.TIME:
                    startDate = startDate.format(dateFormats.TIME)
                    endDate = endDate.format(dateFormats.TIME)
                    break;
            }
        break;
        case dateTypes.DATE:
            startDate = startDate.format(dateFormats.DATE)
            endDate = endDate.format(dateFormats.DATE)
            break;
        case dateTypes.TIME:
            startDate = startDate.format(dateFormats.TIME)
            endDate = endDate.format(dateFormats.TIME)
            break;
        case dateTypes.YEAR:
            startDate = startDate.year()
            endDate = endDate.year()
            break;
        case dateTypes.QUARTER:
            startDate = startDate.quarter()
            endDate = endDate.quarter()
            break;
        case dateTypes.MONTH:
            startDate = startDate.month() + 1
            endDate = endDate.month() + 1
            break;
        case dateTypes.DAY:
            startDate = startDate.date()
            endDate = endDate.date()
            break;
        case dateTypes.HOUR:
            startDate = startDate.hour()
            endDate = endDate.hour()
            break;
        case dateTypes.MINUTE:
            startDate = startDate.minute()
            endDate = endDate.minute()
            break;
        case dateTypes.SECOND:
            startDate = startDate.second()
            endDate = endDate.second()
            break;
    }
    return [startDate, endDate]
}

export const changeEqualDateTime = (dateTime, valueDateFunc, dataType) => {
    switch (valueDateFunc) {
        case dateTypes.INDIVIDUAL:
            switch (dataType) {
                case dateTypes.DATETIME:
                    dateTime = moment(dateTime).format(dateFormats.DATETIME)
                    break;
                case dateTypes.DATEANDTIME:
                    dateTime = moment(dateTime).format(dateFormats.DATETIME)
                    break;
                case dateTypes.DATE:
                    dateTime = moment(dateTime).format(dateFormats.DATE)
                    break;
                case dateTypes.TIME:
                    dateTime = moment(dateTime).format(dateFormats.TIME)
                    break;
            }
        break;
        case dateTypes.DATE:
            dateTime = moment(dateTime).format(dateFormats.DATE)
            break;
        case dateTypes.TIME:
            dateTime = moment(dateTime).format(dateFormats.TIME)
            break;
        case dateTypes.YEAR:
            dateTime = moment(dateTime).year();
            break;
        case dateTypes.QUARTER:
            dateTime = moment(dateTime).quarter();
            break;
        case dateTypes.MONTHNAME:
            dateTime = moment(dateTime).format(dateFormats.MONTHNAME)
            break;
        case dateTypes.MONTH:
            dateTime = moment(dateTime).month() + 1
            break;
        case dateTypes.DAY:
            dateTime = moment(dateTime).date();
            break;
        case dateTypes.HOUR:
            dateTime = moment(dateTime).hour()
            break;
        case dateTypes.MINUTE:
            dateTime = moment(dateTime).minute()
            break;
        case dateTypes.SECOND:
            dateTime = moment(dateTime).second()
            break;
    }
    return dateTime
}

export const createRelativePart = (dataType, displayDateFunc) => {
    switch (displayDateFunc) {
        case dateTypes.INDIVIDUAL:
            displayDateFunc = dataType === dateTypes.TIME ? dateTypes.HOUR : dateTypes.YEAR
            break;
        case dateTypes.DATE:
            displayDateFunc = dateTypes.YEAR
        case dateTypes.TIME:
            displayDateFunc = dateTypes.HOUR
        case dateTypes.MONTHNAME:
            displayDateFunc = dateTypes.MONTH
    }
    return displayDateFunc
}

export const createEffectedDate = (dateNow, value, part, direction, toolTipFormat, relativePart, dType) => {
    let date = null
    if (direction === "last") {
        value = 0 - value
    }
    date = moment(dateNow).add(value, part)
    if (part === "quarters") {
        date = moment(dateNow).add(value * 3, "months")
    }
    if (part === "months") {
        date = moment(dateNow).add(value, "months")
    }
    if (dType === "start") {
        date = date.startOf(relativePart)
    }
    if (dType === "end") {
        date = date.endOf(relativePart)
    }
    return date
}