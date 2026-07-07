import { registerImages } from "@ant-design/maps";
import { isArray, isEmpty } from "lodash";
import moment from "moment";
import randomColor from "randomcolor";
import { v4 as uuidv4 } from "uuid";
import { reRenderGridChart } from "../../../../redux/actions/hreport.actions";
import { getCanvasFieldDataType } from "../../../../utils/filter-utils";
import { getFieldDisplayName } from "../../../../utils/utilities";
import { getCustomFieldDataType, getEachFieldColor, getFieldData, getFieldName, getHTMLColorFormat, getMinAndMaxRangeForSynchronize, getPropertyAxisRange } from "../../hi-editing-area/utils/property-utils";
import { mapColors } from "../../utils/constants";
import { divStyles, getChartTooltipTemplate, setStyles } from "../ant-charts/ant-utils";
import { dateFormat, getGridChartColorSchemeFromPalette } from "./grid-chart-utils";
import { handleMeasureFieldFormattingForCrosstab } from "../table/table-utils";
import { Liquid } from "liquidjs";
import { wrapSpecialVariables } from "../../../hi-dashboard-designer/utils/common-functions";
import { isValidFormat, format as numfmt, formatColor as numfmtColor } from "numfmt";

export const hrNumberFormat = (num) => {
  if (isNaN(parseInt(num, 10)) || isNaN(parseFloat(num))) {
    return num;
  }
  if (num >= 1000000) {
    return (num / 1000000).toFixed(1).replace(/\.0$/, "") + "M";
  }
  if (num >= 1000) {
    return (num / 1000).toFixed(1).replace(/\.0$/, "") + "K";
  }
  return num;
};

export const gridChartNumberFormat = (num, { legendColorField, report }) => {
  if (isNaN(parseInt(num, 10)) || isNaN(parseFloat(num))) {
    return num;
  }

  if (legendColorField) {
    const { isApplyClicked, fieldType, formatField } =
      getPropertyFieldInfo(report, legendColorField);
    let applyOn = 'legend'
    if (formatField[0]?.values?.apply.includes(applyOn)) {
      let formattedText = getPropertyText({
        text: num,
        applyOn,
        isApplyClicked,
        fieldType,
        formatField,
      });
      return formattedText;
    }
  }

  if (num >= 1000000) {
    return (num / 1000000).toFixed(1).replace(/\.0$/, "") + "M";
  }
  if (num >= 1000) {
    return (num / 1000).toFixed(1).replace(/\.0$/, "") + "K";
  }
  return num;
};


export const hrPropertyNumberFormat = (num, decimalPlace, spaceBeforeDisplayUnits = false) => {
  if (isNaN(parseInt(num, 10)) || isNaN(parseFloat(num))) {
    return num;
  }
  if (num >= 1000000000) {
    return (num / 1000000000).toFixed(decimalPlace).replace(/\.0$/, "") + `${spaceBeforeDisplayUnits ? " " : ""}B`;
  }
  if (num >= 1000000) {
    return (num / 1000000).toFixed(decimalPlace).replace(/\.0$/, "") + `${spaceBeforeDisplayUnits ? " " : ""}M`;
  }
  if (num >= 1000) {
    return (num / 1000).toFixed(decimalPlace).replace(/\.0$/, "") + `${spaceBeforeDisplayUnits ? " " : ""}K`;
  }
  return num;
};

export const getMarks = (marksList, fields) => {
  let mainMarks = {};
  mainMarks = marksList.find((mark) => mark.value === "_all_");
  let colorMarks = [],
    sizeMarks = [];
  mainMarks.color.fields.map((field) => {
    let column = fields?.find((tempField) => tempField.id === field.id);
    colorMarks.push(getFieldDisplayName(column));
  });
  mainMarks.size.fields.map((field) => {
    let column = fields.find((tempField) => tempField.id === field.id);
    sizeMarks.push(getFieldDisplayName(column));
  });
  return { colorMarks, sizeMarks };
};
export const createColorsList = (colorMarkField, tableValues) => {
  let colors = {},
    sampleColors = ["blue", "green", "red", "yellow", "orange"];
  tableValues = tableValues.map((item) => item[colorMarkField]);
  tableValues = tableValues.filter((item, i) => i === tableValues.lastIndexOf(item));
  tableValues.map((colorMark) => {
    if (!Object.keys(colors).includes(colorMark)) {
      let colorCode = sampleColors[Object.keys(colors).length];
      colorCode = colorCode ? colorCode : randomColor();
      colors[colorMark] = colorCode;
    }
  });
  return { colors };
};

export const createsizesList = (sizeMarkField, data) => {
  let sizes = {};
  data = data.map((item) => item[sizeMarkField]);
  data = data.filter((item, i) => i === data.lastIndexOf(item));
  data.map((sizeMark) => {
    if (!Object.keys(sizes).includes(sizeMark)) {
      sizes[sizeMark] = 13;
    }
  });
  let totalLength = Object.keys(sizes).length;
  Object.keys(sizes).map((key, i) => {
    sizes[key] = ((i + 1) / totalLength) * 10 + 3;
  });
  return { sizes };
};

export const createMaxValue = (colorMarkField, tableValues) => {
  let sortedTableValues = [...tableValues].sort((a, b) =>
    a[colorMarkField] < b[colorMarkField] ? 1 : -1
  );
  return sortedTableValues[0][colorMarkField];
};

export const addFilter = (filtersList, fields) => {
  return filtersList.map((filter) => {
    let { field, value, condition, drillownFilter, drillDownFilterValues } = filter;
    let drillDownId = uuidv4();
    let clmn = fields.find((clmn) => getFieldDisplayName(clmn) === field);
    let values = Array.isArray(value) ? value : [value];
    let newClmn = {
      ...clmn,
      values,
      condition,
      drillDownId,
      drillownFilter,
      drillDownFilterValues,
    };
    return newClmn;
  });
};

export const getPropertyStyle = (data) => {
  let { padding, fontSize, fontColor, alignment } = data;
  return {
    padding: `${padding}px`,
    fontSize: `${fontSize}px`,
    textAlign: alignment,
    color: `rgba(${fontColor.r}, ${fontColor.g}, ${fontColor.b}, ${fontColor.a})`,
  };
};

export const getPropertyElement = (style, element) => {
  // console.log({style,element})
  if (!element.show || !element.value) return null;
  return <span style={style}>{element.value}</span>;
};

export const thousandSperatorFunc = (value) => {
  const newValue = value.toString().split(".");
  const applySepator = newValue[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
  if (newValue.length === 2) {
    return applySepator + "." + newValue[1];
  }
  return applySepator;
};

export const getDisplayUnit = (value, unitType, decimalPlace = 2, isFloatType, spaceBeforeDisplayUnits = false) => {
  let num = value
  if (isFloatType) {
    num = parseFloat(num)
  } else {
    num = parseInt(num)
  }
  if (unitType === "b") {
    return (num / 1000000000).toFixed(decimalPlace).replace(/\.0$/, "") + `${spaceBeforeDisplayUnits ? " " : ""}B`;
  }
  if (unitType === "m") {
    return (num / 1000000).toFixed(decimalPlace).replace(/\.0$/, "") + `${spaceBeforeDisplayUnits ? " " : ""}M`;
  }
  if (unitType === "k") {
    return (num / 1000).toFixed(decimalPlace).replace(/\.0$/, "") + `${spaceBeforeDisplayUnits ? " " : ""}K`;
  }
  if (unitType === "auto") {
    return hrPropertyNumberFormat(value, decimalPlace, spaceBeforeDisplayUnits);
  }
  return value;
};

export const genaratePercentage = (value, isFloatType) => {
  if (isFloatType) {
    return (parseFloat(value) * 100).toString();
  }
  return (parseInt(value) * 100).toString();
};

export const addPrefixAndSuffix = (value, prefix = "", suffix = "") => {
  return prefix + value + suffix;
};

export const isFloat = (n) => {
  if (typeof n !== 'undefined' && n !== null && n.match(/^-?\d*(\.\d+)?$/) && !isNaN(parseFloat(n)) && (n % 1 != 0))
    return true;
  return false;
}

// export const checkNaNType = (text) => {
//   return isNaN(Number(text))
// }

export const getNumberFormat = ({ text, format }) => {
  if (isNaN(Number(text))) return text
  if (!text && text !== 0 && text !== "0") return text;
  const isFloatType = isFloat(text.toString())
  const { decimalPlace, displayUnits, percentage, prefix, suffix, thousandSperator, spaceBeforeDisplayUnits = false } = format;
  let initialNum = text;
  // Note:  Please do not change the order below.
  if (percentage) {
    initialNum = genaratePercentage(text, isFloatType);
  }
  if (decimalPlace || decimalPlace === 0) {
    if (isFloatType) {
      initialNum = parseFloat(initialNum).toFixed(decimalPlace).toString();
    } else {
      initialNum = parseInt(initialNum).toFixed(decimalPlace).toString();
    }
  }
  if (displayUnits) {
    initialNum = getDisplayUnit(initialNum, displayUnits, decimalPlace, isFloatType, spaceBeforeDisplayUnits);
  }
  if (thousandSperator) {
    initialNum = thousandSperatorFunc(initialNum);
  }
  if (prefix || suffix) {
    initialNum = addPrefixAndSuffix(initialNum, prefix, suffix);
  }
  return initialNum;
};

// export const get24HrTime = (text, milliSecond, condition) => {
//   if (milliSecond === "milliSecondsNumber") {
//     return moment(text, "HHmmss.SSS").format(condition + ".S");
//   }
//   return moment(text, "HHmmss.SSS").format(condition);
// };

// export const get12HrTime = (text, milliSecond, condition) => {
//   if (milliSecond === "milliSecondsNumber") {
//     return moment(text, "hhmmss.SSS").format(`${condition}.S a`);
//   }
//   return moment(text, "hhmmss.SSS").format(`${condition} a`);
// };

const getHour = (hour, text) => {
  if (!hour) return {}
  let newHour;
  if (hour === "none") return { newHour: "", type: "" }
  if (hour === "12hr") {
    newHour = moment(text, "hhmmss.SSS").format(`hh a`)
  }
  if (hour === "24hr") {
    newHour = moment(text, "HHmmss.SSS").format(`HH`);
  }
  newHour = newHour.split(" ")
  if (newHour.length > 1) {
    return { newHour: newHour[0], type: newHour[1] }
  }
  return { newHour: newHour[0], type: "" }
}
const getMinute = (minute, text) => {
  if (minute === "none") return "";
  if (minute === "mintuesNumber") return moment(text, "HHmmss.SSS").format(`mm`);
  return ""
}
const getSecond = (second, text) => {
  if (second === "none") return "";
  if (second === "secondsNumber") return moment(text, "HHmmss.SSS").format(`ss`);
  return ""
}
const getMilliSecond = (milliSecond, text) => {
  if (milliSecond === "none") return "";
  if (milliSecond === "milliSecondsNumber") return moment(text, "HHmmss.SSS").format(`.S`);
  return ""
}

export const getNewTimeFormat = ({ text, format = {} }) => {
  const { hour, minute, second, timeSeperator, milliSecond } = format || {};
  if (hour === "none" && minute === "none" && second === "none" && milliSecond === "none") return "";
  const formattedHour = getHour(hour, text)
  const { newHour, type } = formattedHour || {}
  const newMinute = getMinute(minute, text)
  const newSecond = getSecond(second, text)
  const newMilliSecond = getMilliSecond(milliSecond, text)
  const formattedTime = [newHour, newMinute, newSecond].filter(item => item && item !== "").join(timeSeperator) || ""
  if (type) {
    return `${formattedTime}${newMilliSecond} ${type}`
  }
  return `${formattedTime}${newMilliSecond}`
}

// export const getTimeFormat = ({ text, format }) => {
//   const { hour, minute, second, timeSeperator, milliSecond } = format;
//   if (hour === "none" && minute === "none" && second === "none") return "";
//   if (hour === "12hr") {
//     if (minute === "none" && second === "none") {
//       return get12HrTime(text, milliSecond, "hh");
//     } else if (minute === "none") {
//       return get12HrTime(text, milliSecond, `hh${timeSeperator}ss`);
//     } else if (second === "none") {
//       return get12HrTime(text, milliSecond, `hh${timeSeperator}mm`);
//     }
//     return get12HrTime(text, milliSecond, `hh${timeSeperator}mm${timeSeperator}ss`);
//   }
//   if (hour === "none" && minute === "none") {
//     return get24HrTime(text, milliSecond, "ss");
//   } else if (hour === "none" && second === "none") {
//     return get24HrTime(text, milliSecond, "mm");
//   } else if (minute === "none" && second === "none") {
//     return get24HrTime(text, milliSecond, "HH");
//   } else if (hour === "none") {
//     return get24HrTime(text, milliSecond, "mm:ss");
//   } else if (minute === "none") {
//     return get24HrTime(text, milliSecond, `HH${timeSeperator}ss`);
//   } else if (second === "none") {
//     return get24HrTime(text, milliSecond, `HH${timeSeperator}mm`);
//   }
//   if (milliSecond === "milliSecondsNumber") {
//     return moment(text, "HHmmss.SSS").format("HH:mm:ss.S");
//   }
//   return moment(text, "HHmmss").format("HH:mm:ss");
// };

const getWeekOfTheYear = (text, week) => {
  const weekValue = moment(text).week();
  if (week === "none") return "";
  if (week === "weekNumber") return weekValue;
  if (week === "weekAbbrevation") {
    return `W${weekValue.toString()}`;
  }
  if (week === "weekFull") {
    return `Week${weekValue.toString()}`;
  }
  return ""
};

const getQuarter = (text, quarter) => {
  let quarterValue = Math.ceil((moment(text).month() + 1) / 3);
  if (quarter === "none") {
    return "";
  }
  if (quarter === "quaterNumber") {
    return quarterValue;
  }
  if (quarter === "quaterAbbrevation") {
    return `Q${quarterValue.toString()}`;
  }
  return ""
};

const getYearFormat = (year, newYear, text) => {
  if (year === "4digit") return newYear;
  if (year === "2digit") return moment(text).format("YY");
  if (year === "none") return "";
  return ""
}
const getMonthFormat = (month, newMonth, text) => {
  newMonth = (parseInt(newMonth) + 1).toString(); // date.getMonth gives us from 0 - 11. So we are incrementing 1 
  if (month === "monthNum" || (newMonth >= 10 && month === "monthNumWithZero")) return newMonth;
  if (month === "monthNumWithZero" && newMonth < 10) return `0${newMonth}`;
  if (month === "monthAbbrevation") return moment(text).format("MMM");;
  if (month === "monthFull") return moment(text).format("MMMM");;
  if (month === "none") return "";
  return ""
}
const getDayFormat = (day, newDay) => {
  if (day === "dayNumber" || (newDay >= 10 && day === "dayNumWithZero")) return newDay;
  if (day === "dayNumWithZero" && newDay < 10) return `0${newDay}`;
  if (day === "none") return "";
  return ""
}

export const getNewDateFormat = ({ text, format }) => {
  let { day, month, year, dateSeperator } = format;
  if (day === "none" && month === "none" && year === "none") return "";
  const date = new Date(text);
  let [newYear, newMonth, newDay] = [date.getUTCFullYear(), date.getUTCMonth(), date.getUTCDate()];
  newYear = getYearFormat(year, newYear, text)
  newMonth = getMonthFormat(month, newMonth, text)
  newDay = getDayFormat(day, newDay, text)
  const formattedDate = [newYear, newMonth, newDay].filter((item) => item && item !== "").join(`${dateSeperator}`)
  return formattedDate
}

// export const getDateFormat = ({ text, format }) => {
//   let { day, month, year, dateSeperator } = format;
//   if (day === "none" && month === "none" && year === "none") return "";

//   const date = new Date(text);
//   let [newMonth, newDay, newYear] = [date.getMonth(), date.getDate(), date.getFullYear()];

//   newMonth = (parseInt(newMonth) + 1).toString();

//   if (day === "dayNumWithZero" && newDay < 10) {
//     newDay = `0${newDay}`;
//   }
//   if (month === "monthNumWithZero" && newMonth < 10) {
//     newMonth = `0${newMonth}`;
//   }
//   if (month === "monthAbbrevation") {
//     newMonth = moment(text).format("MMM");
//   }
//   if (month === "monthFull") {
//     newMonth = moment(text).format("MMMM");
//   }
//   if (year === "2digit") {
//     newYear = moment(text).format("YY");
//   }
//   if (day === "none" && month === "none") {
//     return `${newYear}`;
//   } else if (day === "none" && year === "none") {
//     return `${newMonth}`;
//   } else if (month === "none" && year === "none") {
//     return `${newDay}`;
//   } else if (day === "none") {
//     return `${[newYear, newMonth].join(`${dateSeperator}`)}`;
//   } else if (month === "none") {
//     return `${[newYear, newDay].join(`${dateSeperator}`)}`;
//   } else if (year === "none") {
//     return `${[newMonth, newDay].join(`${dateSeperator}`)}`;
//   }
//   return `${[newYear, newMonth, newDay].join(`${dateSeperator}`)}`;
// };

// const isValidDate = (dateObject) => new Date(dateObject).toString() !== "Invalid Date";

const isValidDate = (value) => {
  if (value === null || value === undefined || value === "") return false;

  if (typeof value === "number" || /^\d+$/.test(value)) {
    if (value < 1e11 || value > 1e13) return false;
    const date = moment(Number(value));
    return date.isValid();
  }

  const date = moment(value);
  return date.isValid();
};


function time_format(time_val) {
  let regEx = /^([01][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])(:|\.)\d{1,2}?$/;
  // console.log(regEx.test(time_val));
  return regEx.test(time_val);
}

const getDateTimeFormat = ({ text, format }) => {
  if (typeof text === "number") {
    return text
  }
  const newText = text.split(" ");

  if (newText.length === 2) {
    if (!isValidDate(newText[0]) && !time_format(newText[1])) {
      return text;
    }
  }

  if (newText.length === 1) {
    if (!isValidDate(newText[0])) {
      return newText[0];
    }
    return getNewDateFormat({ text: newText[0], format });
  }
  return `${getNewDateFormat({ text: newText[0], format })} ${getNewTimeFormat({
    text: newText[1],
    format,
  })}`;
};

const getNumFmtColor = (pattern, value) => numfmtColor(pattern, value)

export const getCustomFormatColor = ({ text, applyOn, isApplyClicked, formatField }) => {
  let color = null;
  if (isNaN(Number(text))) return color;
  if (formatField?.length && isApplyClicked && formatField[0]?.values?.apply.includes(applyOn)) {
    const { enableCustomFormatting = false, customFormat } = formatField[0]?.values || {};
    if (enableCustomFormatting && customFormat && isValidFormat(customFormat)) {
      let fText = Number(text);
      color = getNumFmtColor(customFormat, fText);
    }
  }
  return color;
}


export const handleApplyCustomFormatting = (format = {}, text, fieldType) => {
  const { customFormat = "" } = format || {};
  if (!customFormat || !isValidFormat(customFormat)) return text;

  let returnValue = text;
  switch (fieldType) {
    case "numeric":
      if (isNaN(Number(text))) return text
      returnValue = numfmt(customFormat, Number(text));
      break;
    case "date":
    case "dateTime":
    case "time":
      if (isValidDate(text)) {
        let parsedDate = new Date(text);
        returnValue = numfmt(customFormat, parsedDate);
      }
      break;
    default:
      returnValue = numfmt(customFormat, text);
      break;
  }
  return returnValue;
}

export const applyProperties = ({ fieldType, text, format, applyOn }) => {
  const { quarter, week, enableCustomFormatting } = format;
  if (enableCustomFormatting) return handleApplyCustomFormatting(format, text, fieldType);
  if (fieldType === "numeric") {
    return getNumberFormat({ text, format });
  }
  if (fieldType === "date") {
    if (!isValidDate(text)) {
      return text;
    }
    return `${getNewDateFormat({ text, format })} ${getWeekOfTheYear(text, week)} ${getQuarter(
      text,
      quarter
    )}`;
  }
  if (fieldType === "dateTime") {
    if (!isValidDate(text)) {
      return text;
    }
    if (isValidTimestamp(text)) {
      text = getUnFormmatedText(text, fieldType, applyOn)
    }
    return `${getDateTimeFormat({ text, format })} ${getWeekOfTheYear(text, week)} ${getQuarter(
      text,
      quarter
    )}`;
  }
  if (fieldType === "time") {
    return getNewTimeFormat({ text, format });
  }
  return text;
};

function isValidTimestamp(timestamp) {
  const newTimestamp = new Date(timestamp).getTime();
  return isNumeric(newTimestamp);
}

function isNumeric(n) {
  return !isNaN(n) && isFinite(n);
}


export const getUnFormmatedText = (text, fieldType, applyOn) => {
  if (applyOn === "label") {
    if (isValidTimestamp(text)) {
      if (fieldType === "date") {
        return moment(text).format('YYYY-MM-DD');
      }
      if (fieldType === "dateTime") {
        return moment(text).format('YYYY-MM-DD HH:mm:ss.S');
      }
    }
  }
  return text
}

export const getPropertyText = ({ text, applyOn, isApplyClicked, fieldType, formatField }) => {
  const newText =
    formatField?.length && isApplyClicked && formatField[0]?.values?.apply.includes(applyOn)
      ? applyProperties({ fieldType, text, format: formatField[0]?.values, applyOn })
      : getUnFormmatedText(text, fieldType, applyOn);
  return newText;
};

export const getPropertyFieldInfo = (report, name, isTrend, trendFieldId) => {
  let { reportData = {} } = report || {}
  let isApplyClicked;
  const reportField = report?.fields.find((field) => {
    if (isTrend) {
      return field.id === trendFieldId;
    }
    if (field?.alias) {
      return field.alias === name;
    }
    return field?.autogen_alias === name;
  });

  const formatField = report?.properties?.format?.formatFields?.filter(
    (field) => field?.id === reportField?.id
  );
  let fieldType = reportField ? getCanvasFieldDataType(reportField) : "";

  if (fieldType === "text" && reportField.floatingType === "continous") {
    fieldType = "numeric";
  }
  // ---- added for 7162 & 7163 changes -----
  if (reportField?.custom && !isEmpty(reportData)) {
    const { metadata = [] } = reportData;
    fieldType = getCustomFieldDataType(metadata, reportField);
  }
  // ---------------------------------------

  // trend field data type 
  if (reportField?.addedAs === 'trend_field') {
    fieldType = 'numeric'
  }

  if (formatField?.length) {
    isApplyClicked = formatField[0].values?.isApplyClicked;
  }

  return { isApplyClicked, fieldType, formatField, reportField };
};

export const getGridAxisTextFormat = ({ text, data, index, report, context }) => {
  let axis = null
  const axisFieldName = context?.axisFields[0] || "";
  const { fieldType, reportField } = getPropertyFieldInfo(
    report,
    axisFieldName
  );
  if (axisFieldName && fieldType) {
    axis = {
      field: axisFieldName,
      type: ["date", "dateTime"].includes(fieldType) && !reportField?.aggregate?.length ? "temporal" : ""
    }
  }
  if (axis) {
    let axisFieldName = axis?.field
    let type = axis?.type
    if (axisFieldName && data.length) {
      const { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(
        report,
        axisFieldName
      );
      let isDateTypeAppliedOnAxis = type === "temporal" && isApplyClicked && formatField[0]?.values?.apply.includes('axis')
      let formattedText = getPropertyText({
        text: isDateTypeAppliedOnAxis ? fieldType === "dateTime" ? moment(data[index]).format("YYYY-MM-DD HH:mm:ss") : moment(data[index]).format("YYYY-MM-DD") : text,
        applyOn: "axis",
        isApplyClicked,
        fieldType,
        formatField,
      });
      return formattedText;
    }
  }
  return text;
}

export const getDividedArray = (arr) => {
  return arr.reduce((acc, next, i) => {
    if (i % 2 === 0) {
      if (!acc["firstArray"]) {
        acc["firstArray"] = [next];
      } else {
        acc["firstArray"] = [...acc["firstArray"], next];
      }
    } else {
      if (!acc["secondArray"]) {
        acc["secondArray"] = [next];
      } else {
        acc["secondArray"] = [...acc["secondArray"], next];
      }
    }
    return acc;
  }, {});
};

export const removeNullKeysFromObject = (obj) => Object.fromEntries(Object.entries(obj).filter(([_, v]) => v != null));

export const getFormattedLabel = ({ value, funcName, isApplyClicked, fieldType, formatField }) => {
  if (value) {
    let val = dateFormat(value, funcName);
    return getPropertyText({ text: val, applyOn: "axis", isApplyClicked, fieldType, formatField })
  }
  return value
}

export const changeToTotal = (data) => data.map((item) => Object.fromEntries(Object.entries(item).map(([_, v]) => v === '---' ? [_, 'Total'] : [_, v])))

export const calculateAxesValues = (
  subVizType,
  axesData,
  axis,
  value,
  index,
  data,
  ctx,
  colorField,
  rotate
) => {
  if (
    (subVizType === "bar" ||
      subVizType === "point" ||
      subVizType === "line" ||
      subVizType === "area" ||
      subVizType === "tick") &&
    axesData.length
  ) {
    let { rowId = "" } = data[index] || {};
    axesData = axesData.flat();
    let lastIndex = axesData.findLastIndex((ele) => ele.rowId === rowId);
    if (lastIndex >= 0) {
      const axisVal = data[index][axis];
      let axisValue = axesData[lastIndex][axis];
      let axes = ctx.axes();
      let axisFinalValue = axes[axis].getScaleValue(
        (axis === 'x' && colorField) ? axisValue +
          (subVizType === "point" ||
            subVizType === "line" ||
            subVizType === "tick" ||
            subVizType === "area"
            ? 0
            : axisVal / 1 / 2)
          :
          axisValue -
          (subVizType === "point" ||
            subVizType === "line" ||
            subVizType === "tick" ||
            subVizType === "area"
            ? 0
            : axisVal / 1 / 2)
      );
      return axisFinalValue - (subVizType === "point" ||
        subVizType === "line" ||
        subVizType === "tick" ||
        subVizType === "area" ? 10 : 0);
    }
  }
  return value[axis];
};

export const detectOverlapping = (prevEl, nextEl) => {
  let {
    top: t1,
    bottom: b1,
    left: l1,
    right: r1,
  } = prevEl.getBoundingClientRect();
  let {
    top: t2,
    bottom: b2,
    left: l2,
    right: r2,
  } = nextEl.getBoundingClientRect();
  return !(t1 > b2 || r1 < l2 || b1 < t2 || l1 > r2);
};

const getPositionOfElement = (element) => {
  const { x, y, top, left, right, bottom } = element.getBoundingClientRect()
  return { x: parseInt(x), y: parseInt(y), top, left, right, bottom }
}

const checkTwoRects = (rect1, rect2) => (rect1.left < rect2.right && rect1.right > rect2.left && rect1.top < rect2.bottom && rect1.bottom > rect2.top)

const generateRectangles = (nodes, axis) => {
  return [...nodes].map((el) => {
    return getPositionOfElement(el)
  }).sort((a, b) => axis === "x" ? a.x - b.x : a.y - b.y)
}

const checkIfOverlappingIsPresent = (nodes, axis) => {
  let result = []
  let rectangles = generateRectangles(nodes, axis)
  for (let i = 0; i < nodes.length; i++) {
    let rect1 = rectangles[i];
    if (rect1) {
      for (let j = i + 1; j < nodes.length; j++) {
        let rect2 = rectangles[j]
        if (checkTwoRects(rect1, rect2)) {
          result.push(true)
        }
      }
    }
  }
  return result.some((i) => i === true)
}

const handleRemoveLables = (element, rectangles, axis) => {
  let childNodes = element?.childNodes || [];
  for (let i = 0; i < childNodes.length; i++) {
    let currentRect = rectangles[i];
    if (currentRect) {
      for (let j = i + 1; j < childNodes.length; j++) {
        let nextRect = rectangles[j]
        if (checkTwoRects(currentRect, nextRect)) {
          childNodes[j].parentNode.removeChild(childNodes[j])
        }
      }
    }
  }
  if (checkIfOverlappingIsPresent(childNodes, axis)) {
    let rectangles = generateRectangles(childNodes, axis)
    handleRemoveLables(element, rectangles, axis)
  }
}

export const removeOverlapping = ({ el, index, axis }) => {
  if (el) {
    let firstChild = el?.children[0]
    const childNodes = firstChild?.childNodes || []
    if (childNodes && index === childNodes?.length - 1) {
      let rectangles = generateRectangles(childNodes, axis)
      handleRemoveLables(firstChild, rectangles, axis)
    }
  }
}

export const checkIsDateType = (schema) => {
  const dimensions = schema.filter((i) => i.type === "dimension");
  if (!dimensions.length) {
    return false;
  } else {
    if (dimensions.length > 1) {
      return false;
    }
    if (
      dimensions.length === 1 &&
      dimensions.filter((i) => i.subtype).length
    ) {
      return true;
    } else {
      return false;
    }
  }
};

export const getSortObject = (alias, sortObject, months, sortingObjKeys, orderBy) => {
  if (!sortingObjKeys.length) {
    sortObject[alias] = (a, b) => {
      return months.indexOf(a) - months.indexOf(b)
    };
  }
  if (sortingObjKeys.length && sortingObjKeys.includes(alias)) {
    sortObject[alias] = (a, b) => {
      if (orderBy && orderBy.length > 0 && orderBy[0] === 'desc') {
        return months.indexOf(b) - months.indexOf(a)
      }
      return months.indexOf(a) - months.indexOf(b)
    };
  }
}

export const getGridLineConfig = (axis, data) => {
  let show = false
  const { fields } = axis || []
  if (fields.length) {
    fields.forEach((field) => {
      if (field?.data?.name === "") {
        show = field.data.gridlines
      }
    })
  }
  return show
}

export const applyStylesToMuzeTicks = async ({ fontSize, fontColor, type, muzeChartId }) => {
  let color, size = null;

  if (!isEmpty(fontColor)) {
    color = getHTMLColorFormat(fontColor)
  }

  if (fontSize) {
    size = `${fontSize}px`
  }

  return new Promise((resolve) => {
    let element = document.getElementById(muzeChartId);
    if (element) {
      resolve({ element, fontSize: size, color, type })
    }
  }).then(({ element, fontSize, color, type }) => {
    if (element) {
      const style = document.createElement("style");
      let styleInnerHtml = "";
      switch (type) {
        case 'x':
          styleInnerHtml = `#${muzeChartId} .muze-axis-container-bottom`
          break;
        case 'y':
          styleInnerHtml = `#${muzeChartId} .muze-axis-container-left`
          break
        default:
          break;
      }
      styleInnerHtml += `
         .muze-ticks { ${color ? `fill: ${color};` : ''} ${fontSize ? `font-size: ${fontSize};` : ''}  }}
      `;
      style.innerHTML = styleInnerHtml;
      element.appendChild(style);
    }
  }).catch((e) => {
    console.error(e)
  })
}

export const getAxisConfig = (context, report, axis, type, data, subVizType, configOptions = {}) => {
  const {
    rows = [],
    columns = [],
    actualFields = [],
    actualData = [],
    synchronize = false,
    combine = false,
    axes = [],
    muzeChartId
  } = configOptions;
  let axisFieldDataType = ""
  const axisFieldName = context?.axisFields[0] || "";
  const axisField = report?.fields?.find((field) => getFieldDisplayName(field) === axisFieldName) || ""
  const domainData = getFieldData(data, axisFieldName);
  const unqiueData = [...new Set(domainData)]
  if (axisField) {
    axisFieldDataType = getCanvasFieldDataType(axisField)
  }
  const { fields, showGridChartAxisName } = axis || []
  let axisData = {
    name: axisFieldName,
    showAxisName: !showGridChartAxisName ? false : type === "y" && subVizType === "text" ? false : true,
    tickFormat: (text, d, index, ctx) => getGridAxisTextFormat({ text, data: ctx, index, report, context })
  }
  if (subVizType === "text") {
    axisData.padding = 0
  }
  if (type === "x") {
    axisData.labels = {
      rotation: unqiueData.length > 7 && axisFieldDataType === "text" ? -90 : 0,
    }
  }

  if (fields?.length) {
    let axisField = fields.find((field) => field?.data?.name === axisFieldName)
    if (axisField) {
      const { hide, rotate, fontSize, fontColor } = axisField?.data || {}
      const rangeData = getPropertyAxisRange(axisField, "grid")
      let tempMaxValue = unqiueData?.[unqiueData?.length - 1]
      let tempMinValue = unqiueData?.[0];
      let currentAxis = axes?.find((item) => {
        let { name } = item?.config()
        return name === axisField.data.name;
      })
      if (currentAxis) {
        const [_min, _max] = currentAxis?.domain();
        tempMaxValue = _max;
        tempMinValue = _min;
        switch (axisField?.data?.dataType) {
          case "date":
            tempMaxValue = moment(_max).format('YYYY-MM-DD')
            tempMinValue = moment(_min).format('YYYY-MM-DD')
            break;
          case "dateTime":
            tempMaxValue = moment(_max).format('YYYY-MM-DD HH:mm:ss.SSS');
            tempMinValue = moment(_min).format('YYYY-MM-DD HH:mm:ss.SSS');
            break;
          default:
            break;
        }
        applyStylesToMuzeTicks({ fontSize, fontColor, currentAxis, type, muzeChartId })
        // switch (type) {
        //   case "x":
        //     break;
        //   case "y":
        //     applyStylesToMuzeTicks({ fontSize, fontColor, currentAxis, type })
        //     break;
        //   default:
        //     break;
        // }
      }
      if (rangeData) {
        if (rangeData?.min && rangeData?.max) {
          axisData.domain = [rangeData.min, rangeData.max]
        }
        if (rangeData?.min && !rangeData?.max) {
          axisData.domain = [rangeData.min, tempMaxValue]
        }
        if (rangeData?.max && !rangeData?.min) {
          axisData.domain = [tempMinValue, rangeData.max]
        }
      }
      if (hide === true) {
        axisData.show = false
      }
      if ((rotate || rotate === 0) && type === "x") {
        axisData.labels = {
          rotation: rotate
        }
      }
    }
  }
  if (synchronize && combine) {
    let { min, max, axis } = getMinAndMaxRangeForSynchronize(rows, columns, actualFields, actualData)
    if (min || min === 0 && max) {
      if (axis === type) {
        axisData.domain = [min, max]
      }
    }
  }
  if (combine) {
    delete axisData.name;
  }
  return axisData
};


export const checkIfDrillThrough = (dispatch, reportId, activeDrillthroughId) => {
  let activeReport = {}
  dispatch((dispatch, getState) => {
    activeReport = getState().hreport.present.reports.find(
      (report) => !activeDrillthroughId ? report.id === reportId : report.id === activeDrillthroughId
    );
  })
  return activeReport?.drillThrough && !activeReport?.active;
}

export const getGeometryForMap = (item, i, arr, type, longitudeField, latitudeField) => {
  return {
    type: (type === "point" || type === "heatmap") ? "Point" : "LineString",
    coordinates: (type === "point" || type === "heatmap") ?
      [Number(item[longitudeField]), Number(item[latitudeField])]
      :
      [[arr[i]?.[longitudeField], arr[i]?.[latitudeField]], [arr[i + 1]?.[longitudeField], arr[i + 1]?.[latitudeField]]]
  }
}

export const getFormattedDataForMap = ({ data = [], labelField, report }) => {
  if (!report) return data;
  if (labelField) {
    return data.map((item) => {
      const { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(report, labelField);
      let formattedText = getPropertyText({
        text: item[labelField],
        applyOn: "label",
        isApplyClicked,
        fieldType,
        formatField,
      });
      return { ...item, [`${labelField}-formatted`]: formattedText };
    })
  }
  return data;
}

export const getFormatAppliedFieldValue = ({ report, field, value }) => {
  const { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(report, field);
  return getPropertyText({
    text: value,
    applyOn: "label",
    isApplyClicked,
    fieldType,
    formatField,
  });
}


export const prepareLongitudeLatitudeData = (data, geographicRoles, type, { labelField = null, report } = {}) => {
  let longitudeField = geographicRoles?.find((field) => field?.geographicRole === "long")?.name;
  let latitudeField = geographicRoles?.find((field) => field?.geographicRole === "lat")?.name;
  data = getFormattedDataForMap({ data, labelField, report });
  if (!data?.length) {
    return {
      "type": "FeatureCollection",
      "features": []
    }
  }
  return {
    "type": "FeatureCollection",
    "features": data?.map((item, i, arr) => {
      if (i === arr.length - 1 && !['point', 'heatmap'].includes(type)) return null;
      let long = getFormatAppliedFieldValue({ report, field: longitudeField, value: item[longitudeField] }),
        lat = getFormatAppliedFieldValue({ report, field: latitudeField, value: item[latitudeField] }),
        longLat = `(${long},${lat})`
      return {
        type: "Feature",
        "id": longLat,
        properties: {
          name: longLat,
          ...item,
          displayText: longLat
        },
        geometry: getGeometryForMap(item, i, arr, type, longitudeField, latitudeField)
      }
    })?.filter(Boolean)
  }
}

export const constructGeoJsonData = (actualData, geographicRoleFields, type, dispatch, isLongLatMapType, { labelField = null, report } = {}) => {
  if (isLongLatMapType) {
    return prepareLongitudeLatitudeData(actualData, geographicRoleFields, type, { labelField, report })
  }
  let mapData = [];
  const { name, geographicRole } = geographicRoleFields[0];
  dispatch((_, getState, services) => {
    let geoJsonData = getState()?.hreport?.present?.geoJsonData;
    mapData = geoJsonData[geographicRole];
  })
  if (!mapData || !mapData?.length) return {};
  const dataMap = new Map();
  actualData = getFormattedDataForMap({ data: actualData, labelField, report });
  for (const item of actualData) {
    dataMap.set(item[name], item);
  }
  mapData = mapData?.filter((item) => dataMap?.has(item['name']))?.map((item) => {
    return { ...item, ...dataMap?.get(item['name']) };
  })
  return {
    "type": "FeatureCollection",
    "features": mapData?.map((item, i, arr) => {
      if (i === arr.length - 1 && !['point', 'heatmap'].includes(type)) return null;
      return {
        type: "Feature",
        "id": item['name'],
        properties: {
          "name": item['name'],
          ...item,
          displayText: getFormatAppliedFieldValue({ report, field: name, value: item[name] })
        },
        geometry: getGeometryForMap(item, i, arr, type, item?.lng ? 'lng' : 'longitude', item?.lat ? 'lat' : 'latitude')
      }
    })?.filter(Boolean)
  }
}

export const generateFormatColorRamp = (colors) => {
  if (!colors?.length) {
    colors = mapColors
  }
  return colors?.map((color, index, arr) => {
    return {
      color: color,
      position: (index + 1) / arr.length
    }
  })
}

export const getChartMaxScaleValue = (chart, yField) => {
  const scaleMap = chart?.chart?.scalePool?.scales;
  let scaleData = {}
  scaleMap?.forEach((item) => {
    const { key, scale } = item || {}
    const splittedFieldKeys = key.split("-")
    if (splittedFieldKeys?.includes(yField)) {
      scaleData = scale
    }
  })
  return [scaleData?.min, scaleData?.max]
}

export const calculateReferenceLinePosition = (max, min, containerHeight, referenceValue, referenceLineAtY) => {
  if (max) {
    if (referenceLineAtY) {
      const unitHeight = containerHeight / (max - min);
      const positionFromTop = (max - referenceValue) * unitHeight
      return positionFromTop;
    }
    if (!referenceLineAtY) {
      return (referenceValue - min) / (max - min) * containerHeight
    }
  }
  return 100;
}

export const createImageElement = (src) => {
  const image = document.createElement("img");
  image.src = src;
  image.alt = "Map_Chart";
  image.style.width = "100%";
  image.style.background = "transparent";
  image.style.position = 'absolute'
  image.style.top = 0

  const printMediaStyles = `@media print {
    .ant-layout-content {
      height: 100% !important;
    }
    .hi-content-area {
      height: 100% !important;
    }
    .report-viwer-content {
      height: 100% !important;
      overflow-y: visible !important;
    }
  }`;

  const style = document.createElement("style");
  style.innerHTML = printMediaStyles;
  document.head.appendChild(style);

  return image;
}



export const replaceChildrenWithImage = (imageData, id) => {
  let image = createImageElement(imageData)
  const parentElement = document.getElementById(id);
  // while (parentElement.firstChild) {
  //   parentElement.removeChild(parentElement.firstChild);
  // }
  parentElement.appendChild(image)
}

export const getPreviewStyles = (props) => {
  const { chartAreaHeight, chartAreaWidth, autoFit, show, value, subTitleShow, subTitleValue, fontSize, padding, subTitleFontSize, subTitlePadding, isPrintMode } = props || {}
  if (autoFit) {
    return {
      width: '100%',
      height: '100%',
    }
  }
  let height = chartAreaHeight - 15
  if (show && value) {
    height = height - 15 - fontSize - 2 * padding;
  }
  if (subTitleShow && subTitleValue) {
    height = height - 15 - subTitleFontSize - 2 * subTitlePadding;
  }
  return {
    width: isPrintMode ? '100%' : chartAreaWidth,
    height: isPrintMode ? '100%' : height,
  }
}

export const getMapChartLegendCustomHTML = (data = []) => {
  if (!data.length) return '<span></span>'
  const container = document.createElement("div");
  Object.assign(container.style, { display: 'flex', flexDirection: 'column', alignItems: 'flex-start' })
  const divStyles = { display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '8px' }
  data.forEach(({ value = [], color = '' }) => {
    const defaultMarkerStyle = { width: '10px', height: '10px', background: color, marginRight: '4px' }
    let container1 = document.createElement("div")
    Object.assign(container1.style, divStyles);
    let childDiv = document.createElement("div");
    Object.assign(childDiv.style, defaultMarkerStyle);
    container1.appendChild(childDiv)
    let childSpan = document.createElement("span")
    childSpan.innerText = value?.join(' - ')
    container1.appendChild(childSpan)
    container.appendChild(container1)
  })
  return container;
}


export const getMapChartLegend = (properties, report = {}, colorField) => {
  const { legendPosition = '' } = properties?.legend || {};
  const { formatColor = {} } = properties || {}
  let legendProps = {}
  if (formatColor?.formatColorStyle === "fieldValue") {
    const fieldName = getFieldName(formatColor?.formatColorField, report);
    if (fieldName) {
      let fieldData = report?.reportData?.data?.map((item) => item[fieldName]);
      fieldData = [...new Set(fieldData)];
      const legendItems = fieldData?.map((item) => {
        let color = '';
        if (formatColor?.showAll) {
          color = getEachFieldColor(formatColor, item);
        } else {
          color = getHTMLColorFormat(formatColor.defaultColor);
        }
        return {
          id: item,
          color,
          value: item
        }
      })
      legendProps.items = legendItems;
    }
  }

  // formatting on legend for map
  if (colorField) {
    let actualField = report?.fields.find((f) => getFieldDisplayName(f) === colorField)
    if (checkIsContinousField(actualField)) {
      legendProps.customContent = (_, items = []) => {
        const { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(report, colorField);
        let tempItems = items?.map(({ value: legendValues, color }) => {
          if (!Array.isArray(legendValues)) return {};
          let newValues = legendValues.map((i) => getPropertyText({ text: i, applyOn: "legend", isApplyClicked, fieldType, formatField })) ?? [];
          return {
            value: newValues,
            color
          }
        }) ?? [];
        return getMapChartLegendCustomHTML(tempItems);
      }
    }
  }

  if (legendPosition === 'none') return null
  switch (legendPosition) {
    case 'top':
      legendProps.position = 'topcenter';
      break;
    case 'bottom':
      legendProps.position = 'bottomcenter';
      break;
    case 'left':
      legendProps.position = 'topleft';
      break;
    case 'right':
      legendProps.position = 'topright';
      break;
    default:
      legendProps.position = 'bottomleft';
  }
  return legendProps;
}

export const getMapChartTooltip = (tooltipItems = [], report = {}, Notify) => {
  const { tooltip: { showTooltip = true, tooltipTemplate = "", enableTemplate = false } } = report?.reportData?.properties || {};
  if (!showTooltip) return false;
  return {
    items: [...tooltipItems],
    customContent: (_, data) => { // data[] contains id , name and value of item
      const container = document.createElement("div");
      const tooltipTemplateData = {};
      if (data?.length > 0) {
        data.forEach((item) => {
          const { name, value: tooltipValue } = item || {}
          tooltipTemplateData[name] = tooltipValue
          const { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(report, name);
          const value = getPropertyText({
            text: tooltipValue,
            applyOn: "tooltip",
            isApplyClicked,
            fieldType,
            formatField,
          });
          const child = document.createElement("div");
          child.innerHTML = `${name} : ${value}`;
          container.appendChild(child);
        });
        setStyles(container, divStyles);
        if (enableTemplate) {
          return getChartTooltipTemplate({ data: Object.entries(tooltipTemplateData), tooltipTemplate, report, Notify });
        }
        return container;
      }
      container.innerHTML = ''
      return container;
    }
  }

}

export const getMapChartValues = (dispatch) => {
  let token = undefined,
    mapType = 'mapbox',
    mapTileURLs = {};
  let mapObject = {}
  dispatch((_, getState) => {
    mapObject = getState()?.app?.applicationSettingsData?.map
  })
  if (mapObject && typeof mapObject === 'object' && Object.keys(mapObject)?.length) {
    mapType = Object.keys(mapObject)?.[0]
    mapTileURLs = mapObject?.[mapType]?.tileUrls || {}
    const { token: mToken } = mapObject?.[mapType]
    token = mToken;
  }
  return { token, mapType, mapTileURLs }
}

export const getMapPropertiesPayload = (e, chart = {}) => {
  const { target: { transform: { center: { lng, lat } = {}, zoom } = {} } = {} } = e || {}
  const { zoomControl: { zoomInButton, zoomOutButton } = {} } = chart || {}
  if (zoom < 0.5) {
    zoomOutButton.style.cursor = 'not-allowed'
  } else {
    zoomOutButton.style.cursor = 'pointer'
  }
  if (zoom > 20) {
    zoomInButton.style.cursor = 'not-allowed'
  } else {
    zoomInButton.style.cursor = 'pointer'
  }
  return {
    longitude: parseFloat(lng?.toFixed(6)),
    latitude: parseFloat(lat?.toFixed(6)),
    zoom: parseFloat(zoom?.toFixed(2))
  }
}

export const getMapColorFieldScale = (report, name) => {
  const reportField = report?.fields.find((field) => {
    if (field?.alias) {
      return field.alias === name;
    }
    return field?.autogen_alias === name;
  });
  const isDiscrete = reportField.floatingType === "discrete";
  if (isDiscrete) {
    return {
      type: "cat",
    }
  }
  return {
    type: 'quantile'
  }
}

export const calcFunc = (query, arr, fieldsAggregateFn, totals = {}, dimension = '', { report, fields, isMeasureNameAndValuePresent } = {}) => {
  if (!arr || arr.length === 0) {
    return 0;
  }
  const { $$extra$$ = "" } = query || {};
  let measureFieldNames = [];

  let values = arr.map((item) => {
    let dataKeys = []
    for (const key in item) {
      dataKeys.push(key)
    }
    let mFieldKey = dataKeys.find((f) => f.includes("MEASURE_FIELD_"))
    if (mFieldKey) {
      measureFieldNames.push(mFieldKey.replace("MEASURE_FIELD_", ""));
    }
    const totalField = Object.getOwnPropertyDescriptor(item, $$extra$$);
    if (!isEmpty(totalField)) {
      const fieldValue = totalField.value;
      return fieldValue;
    }
    return 0;
  }) || [];
  let value = values.reduce((prev, curr) => prev + curr);
  if (value === 0) return ''
  measureFieldNames = [...new Set(measureFieldNames)]

  let aggregateFn = fieldsAggregateFn[$$extra$$];
  if (aggregateFn) {
    switch (aggregateFn) {
      case "sum":
        value = value;
        break;
      case "count":
        value = value;
        break;
      case "min":
        value = Math.min(...values);
        break;
      case "max":
        value = Math.max(...values);
        break;
      case "avg":
        value = value / arr.length;
        break;
      default:
        break;
    }
  }

  if (dimension?.length) {
    let dimensionVal = query[dimension]
    if (totals[dimensionVal]) {
      totals[dimensionVal]?.push(value)
    } else {
      totals[dimensionVal] = [value]
    }
  } else {
    if (totals[$$extra$$]) {
      totals[$$extra$$]?.push(value)
    } else {
      totals[$$extra$$] = [value]
    }
  }

  if (isMeasureNameAndValuePresent && measureFieldNames.length === 1 && report) {
    const { formattedText } = handleMeasureFieldFormattingForCrosstab({ report, fields, measureFieldName: measureFieldNames[0], text: value, actualValue: value })
    return formattedText;
  }

  return value;
};

export const clearGridChart = (id, reports, dispatch) => {
  let reportToBeActive = reports.find((report) => report.id === id);
  if (reportToBeActive?.selectedType === "GridChart") {
    dispatch(reRenderGridChart({ selectedType: "", reportId: id }))
    let timeout = setTimeout(() => {
      dispatch(reRenderGridChart({ selectedType: "GridChart", reportId: id }))
      clearTimeout(timeout)
    }, 10)
  }
}

export const checkIsContinousField = (field) => {
  return ['', 'continous'].includes(field?.floatingType);
}

export const getMapChartShapeFieldDataConfig = ({ data = [], shapeField = '' }) => {
  let shapes = ['circle', 'square', 'hexagon', 'triangle', 'pentagon', 'octogon', 'hexagram', 'rhombus', 'vesica', 'dot']
  let fieldData = getFieldData(data, shapeField)
  fieldData = [...new Set(fieldData)]
  let shapesMap = {}
  if (fieldData?.length) {
    let tempIndex = 0;
    fieldData.forEach((item) => {
      shapesMap[item] = shapes[tempIndex]
      tempIndex = tempIndex === shapes.length - 1 ? 0 : tempIndex + 1;
    })
  }
  return shapesMap;
}
export const getValueForMapShapeField = (shapeData, shapeField, mapProperties) => {
  let shapeFieldValue = shapeData[shapeField];
  if (!mapProperties?.mapShowAllShapes || !mapProperties[shapeFieldValue]) {
    return { value: shapeFieldValue };
  }
  return { value: shapeFieldValue, isRegisteredImg: true }
}

export const registerMapChartImages = (shapeField, data = [], mapProperties) => {
  let registeredImagesPresent = false;
  if (!shapeField || !data.length) return { registeredImagesPresent }
  let fieldData = getFieldData(data, shapeField)
  fieldData = [...new Set(fieldData)]
  if (fieldData?.length) {
    let imagesToRegister = fieldData.map((item) => {
      if (!mapProperties?.mapShowAllShapes || !mapProperties[item]) return null;
      registeredImagesPresent = true;
      return {
        id: item,
        image: mapProperties[item],
      }
    }).filter((item) => item);
    if (imagesToRegister?.length) registerImages(imagesToRegister)
  }
  return { registeredImagesPresent }
}



// export const calculateTotalByField = (data, field, factor = 1, record) => {
//   if (!isArray(data) || !data?.length || !field) return [];
//   const { $$extra$$ = '' } = record || {}
//   const calculateSum = (nestedData) => {
//     let totalSum = 0;
//     if (isArray(nestedData)) {
//       nestedData.forEach(item => {
//         if (isArray(item)) {
//           totalSum += calculateSum(item);
//         } else if (item && typeof item === 'object' && item?.[field]) {
//           totalSum += (item?.[$$extra$$] / factor);
//         }
//       });
//     }
//     return totalSum;
//   };

//   return data.map(indexData => {
//     return { [field]: calculateSum(indexData) }
//   }).filter(item => item?.[field]);
// }


export const getColorsByThemeColors = (markField = null, themeColors = [], data = []) => {
  if (!markField || !themeColors?.length || !data?.length) return [];
  let tempColors = getGridChartColorSchemeFromPalette(themeColors, data) || []
  return data.reduce((acc, next, i) => {
    acc[next[markField]] = tempColors[i];
    return acc;
  }, {})
}
export const tooltipTemplateLiquidJS = ({ value = "<p></p>", scope, Notify }) => {
  const engine = new Liquid();
  let result;
  try {
    value = wrapSpecialVariables(value)
    result = engine.parseAndRenderSync(value, scope);
  } catch (e) {
    Notify.error({ message: e.message, type: "Frontend" });
  }
  return result;
};

export const calculateRadialPercentage = ({ data = [], obj = {}, measureField = "", dimensions = [] }) => {
  if (!measureField) return null;
  const value = obj[measureField];
  const getTotal = (data) => data.reduce((acc, item) => acc + item[measureField], 0);

  if (!dimensions.length) {
    const total = getTotal(data);
    return `${((value / total) * 100).toFixed(2)}%`
  }
  let dimension = dimensions[0];
  let currentDimensionData = data.filter(item => item[dimension] === obj[dimension]);
  const total = getTotal(currentDimensionData);
  return `${((value / total) * 100).toFixed(2)}%`
}

export const isColorFieldDimension = (clrFldId, report) => {
  if (!clrFldId || !report) return false;
  const field = report.fields.find((f) => {
    return f.id === clrFldId;
  });
  if (!field) return false;
  return ["discrete"].includes(field.floatingType);
}