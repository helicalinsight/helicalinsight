import { CopyOutlined, DeleteOutlined, EditOutlined, MoreOutlined, PlusOutlined } from "@ant-design/icons";
import { Button, Popover, Tooltip } from "antd";
import { cloneDeep, isEmpty } from "lodash";
import moment from "moment";
import { copyColorPaletteToOtherReports, deleteCustomChartColorPalette } from "../../../../redux/actions/hreport.actions";
import { getIntialReportState, MEASURE_VALUE } from "../../../../redux/reducers/initialStates";
import { getCanvasFieldDataType } from "../../../../utils/filter-utils";
import { toCapitalize } from "../../../../utils/text-utils";
import { getFieldDisplayName } from "../../../../utils/utilities";
import { getTickValues } from "../../hi-viz-area/ant-charts/ant-utils";
import { defaultColorPaletteSchemes } from "../../hi-viz-area/ant-charts/constants";
import { checkMeasureFieldsExistInRowsColumns } from "../../hi-viz-area/table/table-utils";
import { isFloat } from "../../hi-viz-area/utils/utillities";
import { getCardIconValues } from "../components/properties/property-card-utils";
import { formatGroupOptions } from "./constants";

const numberItems = {
  thousandSperator: false,
  decimalPlace: 2,
  prefix: "",
  suffix: "",
  displayUnits: "None",
  percentage: false,
  numberCustom: "",
  apply: ["pane"],
  isApplyClicked: false,
  enableCustomFormatting: false
};

const numberItemsCreateMode = {
  thousandSperator: true,
  decimalPlace: 0,
  prefix: "",
  suffix: "",
  displayUnits: "None",
  percentage: false,
  numberCustom: "",
  apply: ["pane", "tooltip", "label", "axis", "actions", "legend"],
  isApplyClicked: true,
  autoFormatting: true,
  enableCustomFormatting: false
};

const dateItems = {
  day: "dayNumWithZero",
  week: "none",
  month: "monthNumWithZero",
  quarter: "none",
  year: "4digit",
  dateSeperator: "-",
  dateCustom: "",
  apply: ["pane"],
  enableCustomFormatting: false
};

const dateItemsCreateMode = {
  day: "dayNumWithZero",
  week: "none",
  month: "monthNumWithZero",
  quarter: "none",
  year: "4digit",
  dateSeperator: "-",
  dateCustom: "",
  apply: ["pane", "tooltip", "label", "axis", "actions", "legend"],
  autoFormatting: true,
  enableCustomFormatting: false
};

const timeItems = {
  hour: "24hr",
  minute: "mintuesNumber",
  second: "secondsNumber",
  milliSecond: "milliSecondsNumber",
  timeSeperator: ":",
  timeCustom: "",
  apply: ["pane"],
  enableCustomFormatting: false
};

const timeItemsCreateMode = {
  hour: "24hr",
  minute: "mintuesNumber",
  second: "secondsNumber",
  milliSecond: "milliSecondsNumber",
  timeSeperator: ":",
  timeCustom: "",
  apply: ["pane", "tooltip", "label", "axis", "actions", "legend",],
  autoFormatting: true,
  enableCustomFormatting: false
};

export const applyFormatProperties = (activeReport = {}) => {
  const tempReport = cloneDeep(activeReport);
  const { properties = {}, fields = [] } = tempReport || {};
  let tempProperties = cloneDeep(properties);
  if (!fields.length) return tempReport

  for (let field of fields) {
    let fieldType = getCanvasFieldDataType(field);
    if (!fieldType || fieldType === "text") continue;
    tempProperties.format.formatFields.push({
      id: field.id,
      values: getDefaultAFDataTypeValues(fieldType, "create"),
    })
  }
  tempReport.properties = tempProperties;
  return tempReport;
}

export const getPropertyAxisRange = (field, chartType) => {
  const { minRange, maxRange, dataType } = field?.data || {}
  let rangeData = {}
  let min;
  let max;
  if (dataType === "numeric" && (minRange || minRange === 0)) {
    if (isFloat(minRange.toString())) {
      min = parseFloat(minRange)
    } else {
      min = parseInt(minRange)
    }
  }
  if (dataType === "numeric" && (maxRange || maxRange === 0)) {
    if (isFloat(maxRange.toString())) {
      max = parseFloat(maxRange)
    } else {
      max = parseInt(maxRange)
    }
  }
  if ((min || min === 0) && (max || max === 0) && chartType === "antChart" && dataType === "numeric") {
    let diff = max - min
    if (diff > Math.round((min + max) / 2)) {
      rangeData.tickInterval = min
    }
  }
  if (dataType === "date" || dataType === "dateTime") {
    if (minRange) {
      min = moment(minRange).format('YYYY-MM-DD');
    }
    if (maxRange) {
      max = moment(maxRange).format('YYYY-MM-DD');
    }
    if (chartType === "antChart") {
      rangeData.type = "time"
    }
  }
  if (dataType === "dateTime" && chartType === "grid") {
    if (minRange) {
      min = moment(minRange).format('YYYY-MM-DD HH:mm:ss.SSS');
    }
    if (maxRange) {
      max = moment(maxRange).format('YYYY-MM-DD HH:mm:ss.SSS');
    }
  }
  if ((min || min === 0) || (max || max === 0)) {
    return { min, max, ...rangeData }
  }
  return null
}

const getAllData = (d1, d2) => {
  let data = []
  d1.forEach((row) => {
    let fieldData = d2?.map((item) => {
      return item[row]
    })
    data.push(fieldData)
  })
  data = data?.flat(Infinity)?.sort((a, b) => a - b)
  let avg = data?.reduce((a, b) => a + b)
  avg = parseInt(avg / data?.length)
  return { data, avg }
}

export const getClosestRoundNumber = (high) => {
  const magnitude = Math.floor(Math.log10(high));
  const nearestPowerOf10 = 10 ** magnitude;

  let multiple = 1;
  if (magnitude >= 1) {
    multiple = nearestPowerOf10;
  } else {
    while (high / multiple > 10) {
      multiple *= 10;
    }
  }

  const roundNumber = Math.round(high / multiple) * multiple;
  return roundNumber;
}

export const getSingleDimensionData = (data) => {
  if (data?.length) {
    let tempData = data?.map((item) => {
      if (Array.isArray(item)) {
        return item?.map((i) => {
          if (typeof i === 'object') return i?.getMembers()
          return i
        })
      }
      return item
    })
    return tempData?.flat(Infinity);
  }
  return data
}

export const checkIsCustomContinuousField = (field) => {
  return field?.custom && field?.floatingType === "continous";
}

export const getMinAndMaxRangeForSynchronize = (rows, columns, fields, actualData) => {
  let rangeData = {}
  let min;
  let max;
  let axis;
  rows = getSingleDimensionData(rows);
  columns = getSingleDimensionData(columns);
  if (rows?.length && rows?.length >= 2) {
    const filteredFields = fields.filter((field) => {
      return rows.includes(getFieldDisplayName(field))
    })
    if (filteredFields?.every((field) => (field?.type?.dataType === 'numeric' || checkIsCustomContinuousField(field)))) {
      let { data, avg } = getAllData(rows, actualData)
      min = data[0]
      max = getClosestRoundNumber(data[data.length - 1])
      axis = 'y'
      // rangeData.tickInterval = avg
    }
  }
  if (columns?.length && columns?.length >= 2) {
    const filteredFields = fields.filter((field) => {
      return columns.includes(getFieldDisplayName(field))
    })
    if (filteredFields?.every((field) => (field?.type?.dataType === 'numeric' || checkIsCustomContinuousField(field)))) {
      let { data, avg } = getAllData(columns, actualData)
      min = data[0]
      max = getClosestRoundNumber(data[data.length - 1])
      axis = 'x'
      // rangeData.tickInterval = avg
    }
  }
  return { min, max, axis, ...rangeData }
}


const getRangeLabel = (dataType, x) => {
  if (x === 1 && dataType === "numeric") {
    return "Min value"
  }
  if (x === 2 && dataType === "numeric") {
    return "Max value"
  }
  if (x === 1 && (dataType === "dateTime" || dataType === "date")) {
    return "Start Date"
  }
  if (x === 2 && (dataType === "dateTime" || dataType === "date")) {
    return "End Date"
  }
}

const getRangeElementType = (dataType) => {
  if (dataType === "numeric") {
    return "Input"
  }
  if (dataType === "dateTime" || dataType === "date") {
    return "DatePicker"
  }
  return ""
}

export const getRangeTooltipInfo = (dataType, index) => {
  if (dataType === "numeric" && index === 1) {
    return `Range/domain setting value lets you enter a value in an input box to define a range of chart. Give lower limit of the range in this input box (Percentage charts do not support range options).`
  }
  if (dataType === "numeric" && index === 2) {
    return `Range/domain setting value lets you enter a value in an input box to define a range of chart. Give upper limit of the range in this input box (Percentage charts do not support range options).`
  }
  if ((dataType === "dateTime" || dataType === "date") && index === 1) {
    return `Range/domain setting value lets you enter a value in an input box to define a range of chart. Give lower limit of the date range in this input box.`
  }
  if ((dataType === "dateTime" || dataType === "date") && index === 2) {
    return `Range/domain setting value lets you enter a value in an input box to define a range of chart. Give upper limit of the date range in this input box.`
  }
  return ""
}

const getCardPrefixElementType = (cardPrefixType) => {
  switch (cardPrefixType) {
    case "selectIcon":
      return "Select";
    case "svg":
      return "NormalTextArea"
    default:
      return "Input"
  }
}

export const getUpdatedProperties = (itemsData, groupKey, initialReport) => {
  const groupData = itemsData.filter((item) => item.groupId === groupKey);
  var updatedData = groupData.reduce(
    (obj, item) => Object.assign(obj, { [item.key]: item.value }),
    {}
  );
  return updatedData;
};

export const getUpdatedRangeProperties = (reportData, itemsData, groupKey, fieldsData = [], fieldId, axisRangeDataType, synchronize, metadata = []) => {
  const groupData = itemsData.filter((item) => item.groupId === groupKey);
  const dataField = groupData.find((data) => data.key === "applyRangeOn");
  const selectedField = fieldsData.find((field) => field.id === dataField.value);
  const convertedData = groupData.reduce(
    (obj, item) => Object.assign(obj, { [item.key]: item.value }),
    { ...selectedField?.values }
  );
  const selectedReportField = reportData?.fields?.find((field) => field.id === dataField.value);
  // delete convertedData.formatDataType;
  const updatedData = fieldsData.map((eachField) => {
    if (eachField.id === dataField.value) {
      const name = getFieldDisplayName(selectedReportField) || ""
      let dataType = getCanvasFieldDataType(selectedReportField)
      if (selectedReportField?.floatingType === "continous") dataType = "numeric"
      if (selectedReportField?.custom) {
        dataType = getCustomFieldDataType(metadata, selectedReportField)
      }
      return {
        id: convertedData.applyRangeOn,
        data: {
          ...convertedData,
          name,
          dataType
        },
      };
    }
    return eachField;
  });
  return {
    fields: updatedData,
    activeDatatype: axisRangeDataType,
    activeId: fieldId,
    gridLines: convertedData.gridLines || [],
    synchronize: synchronize,
    showAxisName: convertedData.showAxisName,
    showGridChartAxisName: convertedData.showGridChartAxisName
  };
};
export const applyColor = (obj, layer, formatColor, propertyColorField) => {
  if (formatColor?.showAll) {
    const singleFiledcolor = getFiledValueColor(obj, formatColor);
    if (singleFiledcolor) {
      return singleFiledcolor;
    }
    return getHTMLColorFormat({ r: 84, g: 108, b: 230, a: 1 });
  } else if (
    layer.options.xField === propertyColorField ||
    layer.options.yField === propertyColorField
  ) {
    return getHTMLColorFormat(formatColor.defaultColor);
  }
  return getHTMLColorFormat({ r: 84, g: 108, b: 230, a: 1 });
};


export const getFormatFields = (itemsData, groupKey, fieldsData, fieldId, showAllFormatFields) => {
  const groupData = itemsData.filter((item) => item.groupId === groupKey);
  const dataField = groupData.find((data) => data.key === "field");
  const selectedField = fieldsData.find((field) => field.id === dataField.value);
  const convertedData = groupData.reduce(
    (obj, item) => Object.assign(obj, { [item.key]: item.value }),
    { ...selectedField?.values }
  );
  delete convertedData.formatDataType;
  const updatedData = fieldsData.map((eachField) => {
    if (eachField.id === dataField.value) {
      return {
        id: convertedData.field,
        values: {
          ...convertedData,
          isApplyClicked: true,
          suffix: convertedData.suffix,
        },
      };
    }
    return eachField;
  });
  return {
    formatFields: updatedData,
    formatDatatype: convertedData.formatDatatype,
    activeFieldId: fieldId,
    showAll: showAllFormatFields,
  };
};

export const getUpdatedColorProperties = (
  itemsData,
  groupKey,
  showAllColorFields,
  formatColorField,
  formatColorStyle,
  report
) => {
  const groupData = itemsData.filter((item) => item.groupId === groupKey);
  var updatedData = groupData.reduce((prev, item) => {
    return [...prev, [item.key, item.value]]; //savingd data in array, array[0] is text, array[1] is color
  }, []);
  const defaultColor = updatedData.find((data) => data[0] === "defaultColor");
  const minimum = updatedData.find((data) => data[0] === "minimum");
  const maximum = updatedData.find((data) => data[0] === "maximum");
  const backgroundColor = updatedData.find((data) => data[0] === "backgroundColor")
  const enableSteps = updatedData.find((data) => data[0] === "enableSteps");
  const steps = updatedData.find((data) => data[0] === "steps");
  const enableReverse = updatedData.find((data) => data[0] === "enableReverse");
  const minValue = updatedData.find((data) => data[0] === "minValue");
  const maxValue = updatedData.find((data) => data[0] === "maxValue");
  const centerValue = updatedData.find((data) => data[0] === "centerValue");
  const enableAdvanceSteps = updatedData.find((data) => data[0] === "enableAdvanceSteps");

  return {
    defaultColor: defaultColor ? defaultColor[1] : report.properties.formatColor.defaultColor,
    minimum: minimum ? minimum[1] : report.properties.formatColor.minimum,
    maximum: maximum ? maximum[1] : report.properties.formatColor.maximum,
    showAll: showAllColorFields,
    dataColors: updatedData,
    formatColorField,
    formatColorStyle,
    backgroundColor: backgroundColor ? backgroundColor[1] : false,
    enableSteps: enableSteps ? enableSteps[1] : false,
    steps: steps ? steps[1] : null,
    enableReverse: enableReverse ? enableReverse[1] : false,
    minValue: minValue ? minValue[1] : null,
    maxValue: maxValue ? maxValue[1] : null,
    centerValue: centerValue ? centerValue[1] : null,
    enableAdvanceSteps: enableAdvanceSteps ? enableAdvanceSteps[1] : false
  };
};

export const getUpdatedCardProperties = (itemsData, groupKey) => {
  const groupData = itemsData.filter((item) => item.groupId === groupKey);
  const cardData = {};
  groupData.forEach((obj) => {
    if (obj.key === "cardTitle") {
      cardData["title"] = obj.value;
    } else {
      cardData[obj.key] = obj.value;
    }
  });
  return cardData
};

export const getDataTypeValues = (dataType) => {
  if (dataType === "numeric") {
    return numberItems;
  }
  if (dataType === "date") {
    return { ...dateItems, isApplyClicked: false };
  }
  if (dataType === "time") {
    return { ...timeItems, isApplyClicked: false };
  }
  if (dataType === "dateTime") {
    return { ...dateItems, ...timeItems, isApplyClicked: false };
  }
};

export const getDefaultAFDataTypeValues = (dataType) => {
  if (dataType === "numeric") {
    return numberItemsCreateMode;
  }
  if (dataType === "date") {
    return { ...dateItemsCreateMode, isApplyClicked: true };
  }
  if (dataType === "time") {
    return { ...timeItemsCreateMode, isApplyClicked: true };
  }
  if (dataType === "dateTime") {
    return { ...dateItemsCreateMode, ...timeItemsCreateMode, isApplyClicked: true };
  }
  return {};
};

export const getFields = (report, showAll, type, addTooltip = false) => {
  const dataFields = report.fields?.filter((field) => {
    let dataType = field ? getCanvasFieldDataType(field) : "";
    if (type === "axisRange") {
      return ["column", "row"].includes(field?.addedAs)
    }
    if (showAll) return true
    if (dataType === "text" && field?.floatingType === "continous" && ["format"].includes(type)) return true
    return dataType !== "text"
  }) || [];

  const rowsAndColums = dataFields.filter((field) => ["row", "column", "trend_field", ...(type === "format" ? ["measure_field"] : [])].includes(field.addedAs))
  const markLayers = dataFields.filter((field) => !["row", "column"].includes(field.addedAs))

  let finalFields = [...rowsAndColums]

  markLayers.forEach((mark) => {
    let isPresent = false
    isPresent = rowsAndColums.find((field) => field.column === mark.column) || finalFields.find((field) => field.column === mark.column)
    if (!isPresent) {
      finalFields.push(mark)
    }
  })

  const modifiedFields = finalFields.map((field) => {
    let tempField = {
      key: field.id, label: field.alias ? field.alias : field.autogen_alias,
    }
    if (addTooltip) {
      tempField.tooltip = ['trend_field'].includes(field.addedAs) ? 'This is Card Trend Field.' : (field?.alias ?? field?.autogen_alias)
    }
    return tempField;
  });

  return modifiedFields;
};

export const getFieldsForFormat = (fields = [], allFields = []) => {
  const measureValueKey = allFields.find((f) => f.column === MEASURE_VALUE)?.id || "";
  const nonMeasureFields = fields.filter((field) => {
    let actualField = allFields.find((f) => f.id === field.key);
    return MEASURE_VALUE !== actualField.column && !['measure_field'].includes(actualField.addedAs);
  })
  const measureFields = fields.filter((field) => {
    let actualField = allFields.find((f) => f.id === field.key);
    return MEASURE_VALUE === actualField.column || ['measure_field'].includes(actualField.addedAs);
  }).sort((a, b) => {
    if (a.key === measureValueKey) return -1;
    if (b.key === measureValueKey) return 1;
    return 0;
  }).map((field) => {
    let returnValue = { ...field }
    if (field.key !== measureValueKey) {
      returnValue.space = true;
      returnValue.tooltip = `${field.label}, note : This is measure field that is added under measures in marks.`;
    }
    return returnValue;
  })

  return [...measureFields, ...nonMeasureFields];
}

export const getFieldName = (formatColorField, report) => {
  const field = report.fields.find((field) => {
    return field.id === formatColorField;
  });
  let name = "";
  if (field) {
    name = getFieldDisplayName(field);
  }
  return name;
};

export const getDataIndex = (uniqueData, text, fieldType) => {
  let num = text
  if (fieldType === "numeric") {
    const isFloatType = isFloat(num?.toString())
    if (isFloatType) {
      num = parseFloat(num)
    } else {
      num = parseInt(num)
    }
  }
  uniqueData.sort((a, b) => a - b)
  const index = uniqueData.indexOf(num)
  return index
}

export const getColorField = (formatColor, text) => {
  const field = formatColor?.dataColors.find((eachData) => {
    if (typeof text === "number") {
      return eachData[0] === text.toString();
    }
    return eachData[0] === text;
  });
  return field;
};

export const getEachFieldColor = (formatColor, text) => {
  let color = "";
  const field = getColorField(formatColor, text);
  if (field?.length) {
    const colorValue = field[1];
    color = getHTMLColorFormat(colorValue);
  }
  return color;
};

export const getHTMLColorFormat = (colorValue = { a: 1, b: 0, g: 0, r: 0 }) => {
  return `rgba(${colorValue.r}, ${colorValue.g}, ${colorValue.b}, ${colorValue.a})`
}

export const generateColorRange = (startColor, endColor, N) => {
  let colors = [`rgba(${startColor.r}, ${startColor.g}, ${startColor.b}, ${startColor.a})`];
  for (var i = 1; i <= N - 2; i++) {
    let Ri = Math.round(startColor.r + ((endColor.r - startColor.r) * i) / N);
    let Gi = Math.round(startColor.g + ((endColor.g - startColor.g) * i) / N);
    let Bi = Math.round(startColor.b + ((endColor.b - startColor.b) * i) / N);
    let Ai = Math.round(startColor.a + ((endColor.a - startColor.a) * i) / N);
    colors.push(`rgba(${Ri}, ${Gi}, ${Bi}, ${Ai})`);
  }
  return [...colors, `rgba(${endColor.r}, ${endColor.g}, ${endColor.b}, ${endColor.a})`];
};

// export const generateColorRange = (startColor, endColor, N, step = null) => {
//   const colorCount = step || N;

//   let uniqueColors = [];

//   for (let i = 0; i < colorCount; i++) {
//     const ratio = colorCount === 1 ? 0 : i / (colorCount - 1);
//     const r = Math.round(startColor.r + (endColor.r - startColor.r) * ratio);
//     const g = Math.round(startColor.g + (endColor.g - startColor.g) * ratio);
//     const b = Math.round(startColor.b + (endColor.b - startColor.b) * ratio);
//     const a = startColor.a + (endColor.a - startColor.a) * ratio;

//     uniqueColors.push(`rgba(${r}, ${g}, ${b}, ${a})`);
//   }

//   let colors = [];
//   const itemsPerColor = N / colorCount;

//   for (let i = 0; i < N; i++) {
//     const colorIndex = Math.min(Math.floor(i / itemsPerColor), colorCount - 1);
//     colors.push(uniqueColors[colorIndex]);
//   }

//   return colors;
// };

export const getColorFromValue = (text, colorRanges = []) => {
  let num = text
  const isFloatType = isFloat(num?.toString())
  if (isFloatType) {
    num = parseFloat(num)
  } else {
    num = parseInt(num)
  }
  let colorRange = colorRanges.filter(({ min, max }) => num >= min && num <= max) || [];
  colorRange = colorRange?.[colorRange.length - 1] || { color: "#000" };
  return colorRange.color;
}

export const getMinMax = (data) => {
  let largestPositive = -Infinity;
  let largestAbsolute = -Infinity;

  for (const num of data) {
    const abs = Math.abs(num);
    if (num > 0 && num > largestPositive) largestPositive = num;
    if (abs > largestAbsolute) largestAbsolute = abs;
  }

  const hasNegatives = data.some(n => n < 0);

  if (hasNegatives) {
    const max = largestAbsolute;
    const min = -largestAbsolute;
    return { min, max };
  } else {
    const min = Math.min(...data);
    const max = Math.max(...data);
    return { min, max };
  }
}

export const generateColorRanges = (data = [], formatColor = {}) => {
  const { minimum: startColor, maximum: endColor, steps: step, enableAdvanceSteps } = formatColor || {}
  if (!step || !data?.length) return [];
  let minValue, maxValue, centerValue;
  const { min, max } = getMinMax(data);
  minValue = min;
  maxValue = max;
  centerValue = parseInt((min + max) / 2);

  if (enableAdvanceSteps) {
    minValue = formatColor?.minValue
    maxValue = formatColor?.maxValue
    centerValue = formatColor?.centerValue
  }

  const center = centerValue !== undefined ? centerValue : parseInt((minValue + maxValue) / 2);
  const isOddStep = step % 2 === 1;
  const colorsPerSide = Math.floor(step / 2);

  const generateColor = (ratio) => {
    const r = Math.round(startColor.r + (endColor.r - startColor.r) * ratio);
    const g = Math.round(startColor.g + (endColor.g - startColor.g) * ratio);
    const b = Math.round(startColor.b + (endColor.b - startColor.b) * ratio);
    const a = startColor.a + (endColor.a - startColor.a) * ratio;
    return `rgba(${r}, ${g}, ${b}, ${a})`;
  };

  const createRanges = (rangeMin, rangeMax, count, startIdx) => {
    const rangeSize = (rangeMax - rangeMin) / count;
    return Array.from({ length: count }, (_, i) => ({
      min: rangeMin + (i * rangeSize),
      max: rangeMin + ((i + 1) * rangeSize),
      color: generateColor((startIdx + i) / (step - 1))
    }));
  };

  let colorRanges = [];

  colorRanges.push(...createRanges(minValue, center, colorsPerSide, 0));

  if (isOddStep) {
    colorRanges.push({
      min: center,
      max: center,
      color: generateColor(colorsPerSide / (step - 1))
    });
  }

  const upperStartIdx = isOddStep ? colorsPerSide + 1 : colorsPerSide;
  colorRanges.push(...createRanges(center, maxValue, colorsPerSide, upperStartIdx));

  return colorRanges;
};

export const generateColorsFromRange = (data = [], formatColor) => {
  const colorRanges = generateColorRanges(data, formatColor);
  const sortedData = data.sort((a, b) => a - b);
  return sortedData.map((d) => getColorFromValue(d, colorRanges));
}

export const getDefaultValue = (report, formatColorField) => {
  const selectedField = report.fields.find((field) => field.id === formatColorField);
  const dataType = selectedField ? getCanvasFieldDataType(selectedField) : "";
  if (dataType === "text") return "fieldValue";
  return "gradient";
};
function isValidTimestamp(timestamp) {
  if (typeof timestamp !== "number") {
    return false;
  }
  if (isNaN(timestamp)) {
    return false;
  }
  const date = new Date(timestamp);
  return !isNaN(date);
}

export const getGradientColor = (obj, formatColor, data) => {
  const colors = generateColorRange(formatColor?.minimum, formatColor?.maximum, data.length);
  for (let key in obj) {
    let color = getHTMLColorFormat(formatColor.defaultColor);
    let value = obj[key];

    if (isValidTimestamp(value)) {
      value = moment(value).format("YYYY-MM-DD");
    }
    const fieldData = data.map((d) => d[key]);
    const index = fieldData.indexOf(value);
    // console.log({ index });
    if (index !== -1) {
      color = colors[index];
    }
    return color;
  }
};

export const getFiledValueColor = (obj, formatColor) => {
  let singleFiledcolor = null;
  for (let key in obj) {
    if (formatColor?.dataColors?.length) {
      let value = obj[key];

      if (isValidTimestamp(value)) {
        value = moment(value).format("YYYY-MM-DD");
      }
      singleFiledcolor = getEachFieldColor(formatColor, value);
    }
    if (singleFiledcolor) {
      return singleFiledcolor;
    }
  }
};

export const getFieldData = (data, fieldName) => {
  let fieldData = []
  if (data?.length && fieldName) {
    data.forEach((eachData) => {
      if (eachData[fieldName] && !fieldData.includes(eachData[fieldName])) {
        fieldData.push(eachData[fieldName]);
      }
    });
  }
  return fieldData
}

export const getGridChartColorFormatScheme = (formatColor, domainData, combine) => {
  const colorScheme = []
  if (combine) {
    if (domainData.length && formatColor?.dataColors.length) {
      domainData.forEach((eachData) => {
        if (formatColor?.showAll) {
          let colorData = formatColor.dataColors.find((eachColor) => {
            return eachColor[0] === eachData
          })
          if (colorData) {
            colorScheme.push(getHTMLColorFormat(colorData[1]))
          }
        } else {
          colorScheme.push(getHTMLColorFormat(formatColor.defaultColor))
        }
      })
    }
  } else {
    if (formatColor?.showAll) {
      if (domainData.length && formatColor?.dataColors.length > 5) {
        domainData.forEach((eachData) => {
          let colorData = formatColor.dataColors.find((eachColor) => {
            return eachColor[0] === eachData
          })
          if (colorData) {
            colorScheme.push(getHTMLColorFormat(colorData[1]))
          }
        })
        // formatColor?.dataColors.forEach((eachData, i) => {
        //   if (i > 4) {
        //     colorScheme.push(getHTMLColorFormat(eachData[1]))
        //   }
        // })
      }
    } else {
      domainData.forEach(() => {
        colorScheme.push(getHTMLColorFormat(formatColor.defaultColor))
      })
    }
  }
  return colorScheme
}

const getColorDataFields = (data, formatColor, report, showAllColorFields, formatColorField) => {
  let dataWithField = []
  let fieldName = getFieldName(formatColorField, report);

  if (fieldName) {
    dataWithField = getFieldData(data, fieldName);
  }

  dataWithField = [...new Set(dataWithField)];
  let colorFileds = [];
  if (showAllColorFields) {
    if (data?.length) {
      colorFileds = dataWithField.reduce((prev, current) => {
        let propertyField = {};
        let color = formatColor.defaultColor;
        if (formatColor?.dataColors.length) {
          const field = getColorField(formatColor, current);
          if (field) {
            color = field[1];
          }
        }
        propertyField = {
          key: `${current}`,
          label: `${current}`,
          value: color,
          elementType: "ColorPicker",
          groupId: "color",
        };
        return [...prev, propertyField];
      }, []);
    }
  }
  return colorFileds;
};

export const getPropertyItems = ({
  report: actualReport = {},
  formatDatatype = "",
  fields = {},
  fieldId = {},
  data = {},
  showAllColorFields = false,
  showAllFormatFields = false,
  formatColorField = "",
  formatColorStyle = "",
  formatColorSteps,
  formatColorEnableSteps,
  formatColorEnableReverse,
  formatMinColor,
  formatMaxColor,
  isPercentageEnabled = false,
  axisRangeId = "", axisRangeDataType = "", axisRangeFields = [],
  cardPrefixType = "None",
  cardSuffixType = "None",
  cardTitle = "",
  cardPrefix = "",
  cardSuffix = "",
  gridLines = [],
  showAxisName,
  showGridChartAxisName,
  cardPrefixColor = { a: 1, b: 0, g: 0, r: 0 },
  cardSuffixColor = { a: 1, b: 0, g: 0, r: 0 },
  showMoreCardIcons,
  synchronize = false,
  recordsPerPage,
  mapStyle,
  longitude,
  latitude,
  mapZoom,
  horizontalScroll = false,
  smooth = false,
  fetchAllRecords = false,
  disableDefaultOptions = false,
  onRemoveFormatColorField = () => { },
  progressTarget = 0,
  progressChartType = '',
  isProgressTargetStatic = true,
  // showTargetAndValue = false,
  statisticType = 'percentage',
  doughnutPrefixType = "",
  doughnutSuffixType = "",
  doughnutPrefix = "",
  doughnutSuffix = "",
  doughnutTitle = '',
  showDoughnutTitle = false,
  radarChartType = 'area',
  progressRange = 5,
  progressRangeColors = {},
  mapDefaultShape = 'circle',
  mapShowAllShapes = false,
  selectedMColor = '',
  setSelectedMColor = () => { },
  customColors,
  enableCustomColors,
  customColorPaletteName,
  onCustomColorPaletteAdd = () => { },
  chartColorPalette = {},
  setCustomColors = () => { },
  setEnableCustomColors = () => { },
  setCustomColorPaletteName = () => { },
  enablePagination = false,
  // treeMapChartType = '',
  relationChartType,
  dispatch,
  // autoFormatting = true,
  isTrend,
  showCardTitle,
  trendPagination,
  displayTrend,
  spaceBeforeDisplayUnits = false,
  canvasView,
  canvasWidth,
  canvasHeight,
  canvasAlign,
  trendPrefix,
  trendPrefixPosition,
  cardView,
  cardWidth,
  cardHeight,
  // mapType,
  showTooltip,
  onOpenTooltipTemplateDrawer = () => { },
  formatColorMinValue,
  formatColorMaxValue,
  formatColorCenterValue,
  enableAdvanceSteps,
  enableTemplate,
  enableCustomFormatting,
  customFormat
}) => {
  const reportForFormat = cloneDeep(actualReport)
  let report = { ...actualReport, fields: actualReport?.fields?.filter((field) => field.addedAs !== "measure_field") }
  const { fields: rFields = [], marksList = [] } = report || {}
  const { shape = {}, subVizType = '' } = marksList?.[0] || {};
  let reportFields = rFields.filter((f) => ['row', 'column'].includes(f?.addedAs))?.map((field) => {
    return {
      name: getFieldDisplayName(field),
      type: field?.floatingType || ''
    }
  }) || [];
  let numericItems = [];
  let dateItems = [];
  let timeItems = [];
  let axisItems = [];
  let autoFormattingProperty = [];
  let tableFields = [];
  let shapeFields = [];
  let canvasFields = [];
  // let sankeyChartFields = [];
  let isPresent = false
  let activeField = null
  let activeAxisRangeField = null, formatColorFieldType = ""
  let { title = {}, subTitle = {}, format = {}, cache = {}, formatColor = {}, bar = {}, legend = {}, radial = {}, axisRange = {}, card = {}, labels = {}, crosstab = {}, table = {}, line = {}, map = {}, shape: shapeP = {}, chartTheme = {}, charts = {}, filter = {} } = report?.reportData?.properties || {};
  const colorPaletteColors = { ...defaultColorPaletteSchemes, ...chartColorPalette };

  activeField = fields?.length && fields.find((field) => field.id === fieldId);
  activeAxisRangeField = axisRangeFields?.length && axisRangeFields.find((field) => field.id === axisRangeId);

  const { isPresent: isMeasureValuePresent } = checkMeasureFieldsExistInRowsColumns(reportForFormat?.fields)

  if (activeField && reportForFormat?.fields?.length) {
    isPresent = reportForFormat.fields.find((field) => field.id === activeField.id);
    if (!isPresent) {
      activeField = null
      fieldId = ""
    }
  }
  const isCreateMode = ["create"].includes(report.mode)

  if (activeAxisRangeField && report?.fields.length) {
    isPresent = report.fields.find((field) => field.id === activeAxisRangeField.id);
    if (!isPresent) {
      activeAxisRangeField = null
      axisRangeId = ""
    }
  }

  if (formatColorField && report?.fields.length) {
    isPresent = report.fields?.find((field) => field.id === formatColorField);
    if (isPresent) {
      formatColorFieldType = getCanvasFieldDataType(isPresent)
    }
    if (!isPresent) {
      formatColorField = ""
      formatColorStyle = ""
      formatColorFieldType = ""
    }
  }

  const getProgressRangeColorsItems = (progressRange, progressRangeColors) => {
    let ticks = getTickValues(Array.from({ length: progressRange }, (_, i) => i + 1, progressRange), true);
    return ticks.map((item, index) => {
      if (index === 0) return null;
      return {
        key: `${item}%`,
        label: `${item}%`,
        value: progressRangeColors[`${item}%`] || { r: 84, g: 108, b: 230, a: 1 },
        elementType: "ColorPicker",
        groupId: "progress",
      }
    }).filter(Boolean);
  }

  const handleColorPaletteEdit = (name) => {
    if (name) {
      setEnableCustomColors(true)
      setCustomColors(chartColorPalette['Custom Colors'][name])
      setCustomColorPaletteName(name)
    }
  }

  const handleColorPaletteDelete = (name) => {
    if (name) {
      dispatch(deleteCustomChartColorPalette(name))
    }
  }

  const handleColorPaletteCopy = (name) => {
    if (name) {
      let payload = {
        [name]: chartColorPalette['Custom Colors'][name]
      }
      dispatch(copyColorPaletteToOtherReports(payload))
    }
  }

  const crosstabItems = [
    {
      key: "showGrandTotals",
      label: "Show Grand Totals",
      value: crosstab?.showGrandTotals,
      elementType: "Switch",
      groupId: "crosstab",
      tooltip: "Show grand total is a property that enables or disables the display of the grand total value in a report.",
      showWarning: isMeasureValuePresent,
      warningMessage: "This is not a valid case to use CrossTab properties with the Measure Value field. However, you may still proceed if desired."
    },
    {
      key: "showRowGrandTotals",
      label: "Show Row Grand Totals",
      value: crosstab?.showRowGrandTotals,
      elementType: "Switch",
      groupId: "crosstab",
      tooltip: "Show row grand total is a property that enables or disables the display of the grand total value for each row in a report.",
      showWarning: isMeasureValuePresent,
      warningMessage: "This is not a valid case to use CrossTab properties with the Measure Value field. However, you may still proceed if desired."
    },
    {
      key: "showColumnGrandTotals",
      label: "Show Column Grand Totals",
      value: crosstab?.showColumnGrandTotals,
      elementType: "Switch",
      groupId: "crosstab",
      tooltip: "Show column grand total is a property that enables or disables the display of the grand total value for each column in a report.",
      showWarning: isMeasureValuePresent,
      warningMessage: "This is not a valid case to use CrossTab properties with the Measure Value field. However, you may still proceed if desired."
    },
    {
      key: "showSubTotals",
      label: "Show Sub Totals",
      value: crosstab?.showSubTotals,
      elementType: "Switch",
      groupId: "crosstab",
      tooltip: "Show sub total is a property that enables or disables the display of the sub total value for each group or category in a report.",
      showWarning: isMeasureValuePresent,
      warningMessage: "This is not a valid case to use CrossTab properties with the Measure Value field. However, you may still proceed if desired."
    },
    {
      key: "showRowSubTotals",
      label: "Show Row Sub Totals ",
      value: crosstab?.showRowSubTotals,
      elementType: "Switch",
      groupId: "crosstab",
      tooltip: "Show sub total row is a property that enables or disables the display of the sub total value for each row in a report.",
      showWarning: isMeasureValuePresent,
      warningMessage: "This is not a valid case to use CrossTab properties with the Measure Value field. However, you may still proceed if desired."
    },
    {
      key: "showColumnSubTotals",
      label: "Show Column Sub Totals ",
      value: crosstab?.showColumnSubTotals,
      elementType: "Switch",
      groupId: "crosstab",
      tooltip: "Show sub total column is a property that enables or disables the display of the sub total value for each column in a report.",
      showWarning: isMeasureValuePresent,
      warningMessage: "This is not a valid case to use CrossTab properties with the Measure Value field. However, you may still proceed if desired."
    },
    {
      key: "grandTotalsPosition",
      label: "Grand Totals position",
      value: crosstab?.grandTotalsPosition,
      elementType: "Select",
      groupId: "crosstab",
      values: [
        { key: "Top", label: "Top" },
        { key: "Bottom", label: "Bottom" },
      ],
      tooltip: "Grand total position is a property that determines where the grand total value is displayed in a report: at the top, or bottom."
    },
    {
      key: "crosstabCollapse",
      label: "Collapse",
      value: crosstab?.crosstabCollapse,
      elementType: "Select",
      groupId: "crosstab",
      values: [
        { key: "All", label: "All" },
        { key: "Columns", label: "Columns" },
        { key: "Rows", label: "Rows" },
        { key: "None", label: "None" },
      ],
      tooltip: "Collpase Cross Tab"
    },
    // {
    //   key: "subTotalsPosition",
    //   label: "Sub Totals position",
    //   value: crosstab?.subTotalsPosition,
    //   elementType: "Select",
    //   groupId: "crosstab",
    //   values: [
    //     { key: "Auto", label: "Auto" },
    //     { key: "Top", label: "Top" },
    //     { key: "Bottom", label: "Bottom" },
    //   ],
    //   tooltip:"Sub total position is a property that determines where the sub total value is displayed in a report: at the auto, top, or bottom."
    // },

  ]


  const filterItems = [
    {
      key: "filterHeaderBGColor",
      label: "Header Background Color",
      value: filter.filterHeaderBGColor,
      elementType: "ColorPicker",
      groupId: "filter",
      tooltip: "This property will change the background color of the filter header."
    },
    {
      key: "filterHeaderTitleColor",
      label: "Header Text Color",
      value: filter.filterHeaderTitleColor,
      elementType: "ColorPicker",
      groupId: "filter",
      tooltip: "This property will change the text color of the filter header."
    },
    {
      key: "filterHeaderFontSize",
      label: "Header Font Size",
      value: filter.filterHeaderFontSize,
      elementType: "InputNumber",
      groupId: "filter",
      tooltip: "This property will change the font size of the filter header."
    },
    {
      key: "filterBGColor",
      label: "Filter Background Color",
      value: filter.filterBGColor,
      elementType: "ColorPicker",
      groupId: "filter",
      tooltip: "This property will change the background color of the filter."
    },
    {
      key: "filterListItemColor",
      label: "List Text Color",
      value: filter.filterListItemColor,
      elementType: "ColorPicker",
      groupId: "filter",
      tooltip: "This property will change the text color of the filter list items."
    },
    // {
    //   key: "filterListItemTickColor",
    //   label: "Selected Tick Background Color",
    //   value: filter.filterListItemTickColor,
    //   elementType: "ColorPicker",
    //   groupId: "filter",
    // },
    {
      key: "filterListItemFontSize",
      label: "List Font Size",
      value: filter.filterListItemFontSize,
      elementType: "InputNumber",
      groupId: "filter",
      tooltip: "This property will change the font size of the filter list items."
    },
  ]

  const items = [
    {
      key: "show",
      label: "Show",
      value: title.show,
      elementType: "Switch",
      groupId: "title",
    },
    {
      key: "value",
      label: "Text",
      placeHolder: "Enter title",
      value: title.value,
      elementType: "Input",
      groupId: "title",
    },
    {
      key: "padding",
      label: "Padding",
      value: title.padding,
      elementType: "InputNumber",
      groupId: "title",
    },
    {
      key: "fontSize",
      label: "Font Size",
      tooltip: "Font Size in px",
      value: title.fontSize,
      elementType: "InputNumber",
      groupId: "title",
    },
    {
      key: "fontColor",
      label: "Font Color",
      value: title.fontColor,
      elementType: "ColorPicker",
      groupId: "title",
    },
    {
      key: "alignment",
      label: "Alignment",
      value: title.alignment,
      elementType: "Select",
      groupId: "title",
      values: [
        { key: "left", label: "Left" },
        { key: "right", label: "Right" },
        { key: "center", label: "Center" },
      ],
    },
    {
      key: "position",
      label: "Position",
      value: title.position,
      elementType: "Select",
      groupId: "title",
      values: [
        { key: "top", label: "Top" },
        { key: "bottom", label: "Bottom" },
      ],
    },
    {
      key: "show",
      label: "Show",
      value: subTitle.show,
      elementType: "Switch",
      groupId: "subTitle",
    },
    {
      key: "value",
      label: "Text",
      placeHolder: "Enter sub title",
      value: subTitle.value,
      elementType: "Input",
      groupId: "subTitle",
    },
    {
      key: "padding",
      label: "Padding",
      value: subTitle.padding,
      elementType: "InputNumber",
      groupId: "subTitle",
    },
    {
      key: "fontSize",
      label: "Font Size",
      tooltip: "Font Size in px",
      value: subTitle.fontSize,
      elementType: "InputNumber",
      groupId: "subTitle",
    },
    {
      key: "fontColor",
      label: "Font Color",
      value: subTitle.fontColor,
      elementType: "ColorPicker",
      groupId: "subTitle",
    },
    {
      key: "alignment",
      label: "Alignment",
      value: subTitle.alignment,
      elementType: "Select",
      groupId: "subTitle",
      values: [
        { key: "left", label: "Left" },
        { key: "right", label: "Right" },
        { key: "center", label: "Center" },
      ],
    },
    {
      key: "position",
      label: "Position",
      value: subTitle.position,
      elementType: "Select",
      groupId: "subTitle",
      values: [
        { key: "top", label: "Top" },
        { key: "bottom", label: "Bottom" },
      ],
    },
    {
      key: "isCacheEnabled",
      label: "Cache",
      value: cache.isCacheEnabled,
      elementType: "Switch",
      groupId: "cache",
      tooltip: "Set how often the report data is refreshed from the source. This affects the data accuracy and freshness in open and dashboard modes. No effect in edit mode."
    },
    {
      key: "interval",
      label: "Interval",
      value: cache.interval,
      elementType: "TimePicker",
      defaultValue: "00:00:01",
      showNow: false,
      groupId: "cache",
      tooltip: "The report will be refreshed after predefined interval(HH:MM:SS).",
    },
    {
      key: "showAllFormatFields",
      label: "Show all fields",
      value: showAllFormatFields,
      elementType: "Switch",
      groupId: "format",
      tooltip: "On enable, it will show all fields of the report in field dropdown."
    },
    {
      key: "field",
      label: "Field",
      value: fieldId,
      elementType: "Select",
      groupId: "format",
      values: getFieldsForFormat(getFields(reportForFormat, showAllFormatFields, "format", true), (reportForFormat.fields)),
      // dropdownTooltipEnabled: true
    },
    {
      key: "apply",
      label: "Apply",
      tooltip: "Apply On",
      value: activeField?.values?.apply ? activeField.values.apply : [],
      elementType: "Select",
      groupId: "format",
      multiSelect: true,
      values: [
        { key: "axis", label: "Axis" },
        { key: "pane", label: "Pane" },
        { key: "tooltip", label: "Tooltip" },
        { key: "label", label: "Label" },
        { key: "actions", label: "Actions" },
        { key: "legend", label: "Legend" },
      ],
    },
    {
      key: "formatDatatype",
      label: "Data Type",
      tooltip: "Data Type",
      value: !isPresent ? "" : formatDatatype ? formatDatatype : format.formatDatatype,
      elementType: "Select",
      groupId: "format",
      disabled: true,
      values: [
        { key: "numeric", label: "Number" },
        { key: "varchar", label: "VARCHAR" },
        { key: "date", label: "Date" },
        { key: "time", label: "Time" },
        { key: "dateTime", label: "DateTime" },
      ],
    },
    (fieldId && {
      key: "enableCustomFormatting",
      label: "Enable Custom Formatting",
      value: enableCustomFormatting,
      elementType: "Switch",
      groupId: "format",
      tooltip: "Allow custom formatting to be applied to the field value.",
    }),
    ...((fieldId && enableCustomFormatting) ? [
      {
        key: "selectedFormat",
        label: "Formats",
        value: activeField.values.selectedFormat,
        elementType: "Select",
        groupId: "format",
        tooltip: "Select from the list of available formats.",
        options: formatGroupOptions,
        isGroupType: true,
        allowClear:true
      },
      {
        key: "customFormat",
        label: "Custom Format",
        value: customFormat,
        elementType: "Input",
        groupId: "format",
        tooltip: "Enter a custom format for the field. For example: #,##0.00;(#,##0.00)",
      }
    ] : []),
    {
      key: "formatColorField",
      label: "Field",
      value: formatColorField,
      elementType: "Select",
      groupId: "color",
      values: getFields(report, true),
      dropdownTooltipEnabled: true,
      allowClear: formatColorField,
      onClear: onRemoveFormatColorField
    },
    {
      key: "formatColorStyle",
      label: "Format Style",
      value: formatColorStyle,
      elementType: "Select",
      groupId: "color",
      values: [
        { key: "gradient", label: "Gradient" },
        { key: "fieldValue", label: "Field Value" },
      ],
    },
    {
      key: "barType",
      label: "Type",
      value: bar.barType,
      elementType: "Select",
      groupId: "bar",
      values: [
        { key: "stacked", label: "Stacked", tooltip: "This is the standard bar mode. You can use it for any chart type." },
        { key: "grouped", label: "Grouped", tooltip: "To observe the effect, apply color marks to the chart." },
        { key: "percentage", label: "Percentage", tooltip: "To view the percentage chart, select the chart option and apply color marks." },
      ],
      tooltip: "A bar chart shows values or frequencies of categories or groups using rectangular bars. It can support percentage, stacked, or grouped modes."
    },
    {
      key: "showRadial",
      label: "Show Percentage in Label",
      value: radial?.showRadial,
      elementType: "Switch",
      groupId: "radial",
      tooltip: "For radial charts such as arc or doughnut, you can use this property. It displays the data as percentages in the chart. For best visualization, use color marks."
    },
    {
      key: "showRadialLabel",
      label: "Arc Legend",
      value: radial?.showRadialLabel,
      elementType: "Switch",
      groupId: "radial",
      tooltip: "On enabling this property, it'll display the arc legend in chart."
    },
    {
      key: "showRadialValue",
      label: "Arc Value",
      value: radial?.showRadialValue,
      elementType: "Switch",
      groupId: "radial",
      tooltip: "On enabling this property, it'll display the data(value) in label in chart. Note: if label field is available then that field value will be displayed."
    },
    {
      key: "chartType",
      label: "Chart Type",
      value: radial?.chartType,
      elementType: "Select",
      groupId: "radial",
      tooltip: "Select Chart Type for Radial Chart, if Arc/Doughnut is selected , in that case whatever is selected(Arc or Doughnut) in marks, will be generated.",
      values: [
        { key: "arc", label: "Arc/Doughnut" },
        { key: "rose", label: "Rose" },
        { key: "radial bar", label: "Radial Bar" },
      ]
    },
    {
      key: "showDoughnutTitle",
      label: "Show Title",
      value: showDoughnutTitle,
      elementType: "Switch",
      groupId: "radial",
      tooltip: "Show Title in Doughnut Chart. Select Chart option to see the effect. This option will be disabled in case of arc chart.",
      disabled: ['arc'].includes(subVizType)
    },
    {
      key: "legendPosition",
      label: "Position",
      value: legend.legendPosition,
      elementType: "Select",
      groupId: "legend",
      values: [
        { key: "top", label: "Top" },
        { key: "right", label: "Right" },
        { key: "left", label: "Left" },
        { key: "bottom", label: "Bottom" },
        { key: "none", label: "None" },
      ],
      tooltip: "You can choose where to place the legend from the following options: right, left, top, bottom, or none."
    },
    {
      key: "gridLines",
      label: "Gridlines",
      value: gridLines,
      elementType: "Select",
      groupId: "axisRange",
      multiSelect: true,
      values: [
        { key: "x", label: "x-axis" },
        { key: "y", label: "y-axis" },
      ],
      tooltip: "Gridline settings show or hide lines from axes to plot area . They align with tick marks and make data easier to read . Gridlines are disabled by default for both the axes."
    },
    {
      key: "showAxisName",
      label: "Show Axis Name (Chart)",
      value: showAxisName,
      elementType: "Switch",
      groupId: "axisRange",
      tooltip: "Show chart axis name, if this property is disabled, x and y axis's names will be hidden."
    },
    {
      key: "showGridChartAxisName",
      label: "Show Axis Name (Grid Chart)",
      value: showGridChartAxisName,
      elementType: "Switch",
      groupId: "axisRange",
      tooltip: "Show grid chart axis name, if this property is disabled, x and y axis's names will be hidden."
    },
    {
      key: "applyRangeOn",
      label: "Axis",
      value: axisRangeId,
      elementType: "Select",
      groupId: "axisRange",
      values: getFields(report, false, "axisRange"),
      tooltip: "Axis selection setting lets you choose an axis on which the settings will be applied.",
      dropdownTooltipEnabled: true
    },
    {
      key: "synchronize",
      label: "Synchronize",
      value: synchronize,
      elementType: "Switch",
      groupId: "axisRange",
      tooltip: "Synchronize axis allows you to use the same scale for two different measures on a combined chart."
    },
    {
      key: "showTitle",
      label: "Show Title",
      value: showCardTitle,
      elementType: "Switch",
      groupId: "card",
      tooltip: "Display Card Title"
    },
    {
      key: "cardTitle",
      label: "Title",
      value: cardTitle,
      elementType: "Input",
      groupId: "card",
    },
    {
      key: "prefixType",
      label: "Prefix Type",
      value: cardPrefixType,
      elementType: "Select",
      groupId: "card",
      values: [
        { key: "selectIcon", label: "Select" },
        { key: "staticValue", label: "Static Value" },
        { key: "url", label: "URL" },
        { key: "svg", label: "SVG" },
      ],
      tooltip: "Select Prefix Type for Card. It can be Static Value, Select Icon, URL or SVG."
    },
    {
      key: "suffixType",
      label: "Suffix Type",
      value: cardSuffixType,
      elementType: "Select",
      groupId: "card",
      values: [
        { key: "selectIcon", label: "Select" },
        { key: "staticValue", label: "Static Value" },
        { key: "url", label: "URL" },
        { key: "svg", label: "SVG" },
      ],
      tooltip: "Select Suffix Type for Card. It can be Static Value, Select Icon, URL or SVG."
    },
    {
      key: "prefix",
      label: "Prefix",
      value: cardPrefix,
      elementType: getCardPrefixElementType(cardPrefixType),
      groupId: "card",
      values: getCardIconValues(showMoreCardIcons),
      showMore: true
    },
    {
      key: "suffix",
      label: "Suffix",
      value: cardSuffix,
      elementType: getCardPrefixElementType(cardSuffixType),
      groupId: "card",
      values: getCardIconValues(showMoreCardIcons),
      showMore: true
    },
    {
      key: "prefixColor",
      label: "Prefix Color",
      value: cardPrefixColor,
      elementType: "ColorPicker",
      groupId: "card",
    },
    {
      key: "suffixColor",
      label: "Suffix Color",
      value: cardSuffixColor,
      elementType: "ColorPicker",
      groupId: "card",
    },
    {
      key: "trendPagination",
      label: "Table Records",
      placeHolder: "Enter Number",
      value: trendPagination,
      elementType: "InputNumber",
      groupId: "card",
      tooltip: "This is the number of records visbile in card table. -1 means all trend records. By default it is 2.",
    },
    {
      key: "rotateLabels",
      label: "Rotate Labels",
      value: labels?.rotateLabels,
      elementType: "Switch",
      groupId: "labels",
      tooltip: "To turn the labels to 90 degrees, select the rotate option. To observe the effect, apply labels to the chart."
    },
    {
      key: "labelsColor",
      label: "Color",
      value: labels?.labelsColor,
      elementType: "ColorPicker",
      groupId: "labels",
      tooltip: "Pick color for chart labels",
      remove: true
    },
    {
      key: "recordsPerPage",
      label: "Pagination Option",
      value: recordsPerPage,
      elementType: "Input",
      groupId: "table",
      tooltip: "Configure pagination settings. This will influence the 'per page' options list. If the configuration exists, it will be disregarded; if not, it will be included. After apply this property , you will be able to see the added option in page size dropdown and you can select it from there. Previously, this was referred to as 'Records per page'.",
    },
    {
      key: "horizontalScroll",
      label: "Horizontal Scroll",
      value: horizontalScroll,
      elementType: "Switch",
      groupId: "table",
      tooltip: "Enable horizontal scroll in the table. If number of columns in table are greater, then you can enable horizontal scroll."
    },
    {
      key: "fetchAllRecords",
      label: "Fetch All Records",
      value: fetchAllRecords,
      elementType: "Switch",
      groupId: "table",
      tooltip: "Fetch all records in one go. Applying the 'Fetch All Records' option for generating reports with large data sets may decrease performance and result in longer loading times."
    },
    {
      key: "disableDefaultOptions",
      label: "Disable Default Pagination Options",
      value: disableDefaultOptions,
      elementType: "Switch",
      groupId: "table",
      tooltip: "Disable Default Pagination Options"
    },
    // {
    //   key: "mapType",
    //   label: "Map Type",
    //   value: mapType,
    //   elementType: "Select",
    //   groupId: "map",
    //   values: [
    //     { key: 'mapbox', label: 'MapBox' },
    //     { key: 'osm', label: 'Open Street Map' },
    //   ],
    //   tooltip: "Choose a style for the map: Dark, Light, or Normal. Dark and Light change the map's background color, while Normal is the default style."
    // },
    {
      key: "mapStyle",
      label: "Map Style",
      value: mapStyle,
      elementType: "Select",
      groupId: "map",
      values: [
        { key: 'normal', label: 'Normal' },
        { key: 'dark', label: 'Dark' },
        { key: 'light', label: 'Light' },
      ],
      tooltip: "Choose a style for the map: Dark, Light, or Normal. Dark and Light change the map's background color, while Normal is the default style."
    },
    {
      key: "smooth",
      label: "Smooth",
      value: smooth,
      elementType: "Switch",
      groupId: "line",
      tooltip:
        "Transform your line chart into a spline chart. When you enable this option, it will connect the data points with smooth curves.",
    },
    {
      key: "longitude",
      label: "Longitude",
      value: longitude,
      elementType: "InputNumber",
      groupId: "map",
      tooltip: "Set the longitude for the center of map.(longitude range varies from -180 to 180)",
      step: "0.000001",
      stringMode: true
    },
    {
      key: "latitude",
      label: "Latitude",
      value: latitude,
      elementType: "InputNumber",
      groupId: "map",
      tooltip: "Set the latitude for the center of map.(latitude range varies from -90 to 90)",
      step: "0.000001",
      stringMode: true
    },
    {
      key: "zoom",
      label: "Zoom",
      value: mapZoom,
      elementType: "InputNumber",
      groupId: "map",
      tooltip: "Set the zoom level of the map.(zoom range varies from 0 to 22)",
      step: "0.01",
      stringMode: true
    },
    {
      key: "isStatic",
      label: "Static Target",
      value: isProgressTargetStatic,
      elementType: "Switch",
      groupId: "progress",
      tooltip: "Set static or field target for progress chart, by default it is static.",
    },

    {
      key: "chartType",
      label: "Type",
      tooltip: "Available types for progress chart. Note: Ring and Gauge did not support tooltip so interactivity won't work for them.",
      elementType: "Select",
      value: progressChartType,
      groupId: "progress",
      values: [
        { key: "ring", label: "Ring" },
        { key: "gauge", label: "Gauge" },
        { key: "bullet", label: "Bullet" },
      ],
    }, {
      key: "target",
      label: "Target",
      tooltip: "Set target for progress chart.",
      value: progressTarget,
      elementType: isProgressTargetStatic ? "InputNumber" : "Select",
      groupId: "progress",
      values: getFields(report),
    },
    // {
    //   key: "showTargetAndValue",
    //   label: "Show Target & Value",
    //   value: showTargetAndValue,
    //   elementType: "Switch",
    //   groupId: "progress",
    //   tooltip: "Show target and value fields in ring and gauge chart.",
    // },
    {
      key: "statisticType",
      label: "Statistic Label Type",
      value: statisticType,
      elementType: "Select",
      groupId: "progress",
      tooltip: "Set statistic label type for ring and gauge chart. By default it is percentage.",
      values: [
        { key: "percentage", label: "Percentage" },
        { key: "numeric", label: "Numeric" },
      ],
    },
    {
      key: "radarChartType",
      label: "Chart Type",
      tooltip: "Selecte type of the radar chart.",
      value: radarChartType,
      elementType: "Select",
      groupId: "radar",
      values: [
        { key: "area", label: "area" },
        { key: "line", label: "line" },
      ],
    },
    // {
    //   key: "progressRange",
    //   label: "Gauge Range",
    //   tooltip: "Set Range for Gauge Chart Parts. By default it is 5 , i.e (0-100) will be divided into 5 parts.",
    //   value: progressRange,
    //   elementType: "InputNumber",
    //   groupId: "progress",
    // },
    {
      key: "mapDefaultShape",
      label: "Default Shape",
      value: mapDefaultShape,
      elementType: "Select",
      groupId: "shape",
      values: [
        { key: "circle", label: "Circle" },
        { key: "square", label: "Square" },
        { key: "hexagon", label: "Hexagon" },
        { key: "triangle", label: "Triangle" },
        { key: "pentagon", label: "Pentagon" },
        { key: "octogon", label: "Octogon" },
        { key: "hexagram", label: "Hexagram" },
        { key: "rhombus", label: "Rhombus" },
        { key: "vesica", label: "Vesica" },
        { key: "dot", label: "Dot" },
      ],
      tooltip: "Default point shape for map chart. If Shape field is present in marks then this won't apply."
    },
    {
      key: "mapShowAllShapes",
      label: "Show All",
      value: mapShowAllShapes,
      elementType: "Switch",
      groupId: "shape",
      tooltip: "Show all the values of Mark's Shape field. Please regenerate report after adding urls."
    },
    {
      key: "position",
      label: "Position",
      value: labels?.position || 'middle',
      elementType: "Select",
      groupId: "labels",
      tooltip: "Select position for labels for bar.",
      values: [
        { key: "middle", label: "Middle" },
        { key: "top", label: "Top" },
      ],
    },
    {
      key: "offsetX",
      label: "OffsetX",
      tooltip: "Set offsetX for labels for bar.",
      value: labels?.offsetX,
      elementType: "InputNumber",
      groupId: "labels",
    },
    {
      key: "offsetY",
      label: "OffsetY",
      tooltip: "Set offsetY for labels for bar.",
      value: labels?.offsetY,
      elementType: "InputNumber",
      groupId: "labels",
    },
    {
      key: "fontSize",
      label: "Font Size",
      tooltip: "Set font size for labels for bar.",
      value: labels?.fontSize,
      elementType: "InputNumber",
      groupId: "labels",
    },
    {
      key: "maxBarWidth",
      label: "Max Width",
      tooltip: "Set max width for bar. This is not the actual width that will get apply to bar but the max width that bar can have if sufficient space is present.",
      value: bar?.maxBarWidth,
      elementType: "InputNumber",
      groupId: "bar",
    },
    // { [7739]
    //   key: "autoFit",
    //   label: "Auto Fit",
    //   tooltip: "Set auto fit for bar.",
    //   value: bar?.autoFit,
    //   elementType: "Switch",
    //   groupId: "bar",
    // },
    {
      key: "colorPalette",
      label: "Color palette",
      value: chartTheme?.colorPalette || [],
      elementType: "Select",
      multiSelect: true,
      isGroupType: true,
      groupId: "chartTheme",
      tooltip: "Select color palette for chart. This option enables the custom colors for charts, you can select multiple colors from this dropdown and colors will get applied on the charts accordingly.",
      options: getColorSelectOptions({ data: colorPaletteColors, chartColorPalette, onEdit: handleColorPaletteEdit, onDelete: handleColorPaletteDelete, onCopy: handleColorPaletteCopy })
    },
    {
      key: "enableCustomColors",
      label: "Add Custom Colors Palette",
      tooltip: "Add custom colors for chart. You can add 10 colors only. After adding custom color palette you can choose from the dropdown.",
      value: enableCustomColors,
      elementType: "Switch",
      groupId: "chartTheme",
    },
    {
      key: "enablePagination",
      label: "Enable Pagination",
      tooltip: "Enable slider for chart. This option will add the slider in the chart in x axis , it is only applicable for charts with axes and for Chart type visualization only. This property is for percentage (0-100%) of records shown as per data.",
      value: enablePagination,
      elementType: "Switch",
      groupId: "charts",
    },

    // relation chart items
    {
      key: "chartType",
      label: "Type",
      tooltip: "Available types for relation charts. Note: for sankey chart , label mark wont't work.",
      elementType: "Select",
      value: relationChartType,
      groupId: "relationChart",
      values: [
        { key: "treemap", label: "Treemap" },
        { key: "circlePacking", label: "Circle Packing" },
        { key: "sunburst", label: "Sunburst" },
        { key: "sankey", label: "Sankey Chart" },
      ],
    },
    {
      key: "view",
      label: "View",
      tooltip: "Choose a view for the visualization area, and it will adjust accordingly. Fit Width adapts to the page width or a specified custom width. Fit Height adjusts to the page height or a specified custom height. Entire View ensures the visualization fits both width and height.",
      value: canvasView,
      elementType: "Select",
      groupId: "canvas",
      values: [
        { key: "standard", label: "Entire View" },
        { key: "fitWidth", label: "Fit Width" },
        { key: "fitHeight", label: "Fit Height" },
        { key: "entireView", label: "Standard" },
      ],
    },
    {
      key: "align",
      label: "Align",
      tooltip: "Select alignment for visualization area. It will adjust accordingly.",
      value: canvasAlign,
      elementType: "Select",
      groupId: "canvas",
      values: [
        { key: "left", label: "Left" },
        { key: "center", label: "Center" },
        { key: "right", label: "Right" },
        { key: "top", label: "Top" },
        { key: "bottom", label: "Bottom" },
        { key: "topLeft", label: "Top Left" },
        { key: "topRight", label: "Top Right" },
        { key: "bottomLeft", label: "Bottom Left" },
        { key: "bottomRight", label: "Bottom Right" },
      ],
    },
    {
      key: "cardView",
      label: "Card View",
      tooltip: "Choose a view for the card visualization , and it will adjust accordingly. Fit Width adapts to the page width or a specified custom width. Fit Height adjusts to the page height or a specified custom height. Entire View ensures the visualization fits both width and height.",
      value: cardView,
      elementType: "Select",
      groupId: "card",
      values: [
        { key: "entireView", label: "Entire View" },
        { key: "fitWidth", label: "Fit Width" },
        { key: "fitHeight", label: "Fit Height" },
        { key: "standard", label: "Standard" },
      ],
    },
    {
      key: "isTrend",
      label: "Trend",
      value: isTrend,
      elementType: "Switch",
      groupId: "card",
      tooltip: "Enable trend for card. If enabled, trend record along with the details and value will be added in the card. By default first value of first record of the data will be considered as primary value."
    },
    {
      key: "showTooltip",
      label: "Show Tooltips",
      tooltip: "This enables the tooltips in the visualization. Important: Disabling this option will also disable interactivity for Chart, Map, and Card components.",
      value: showTooltip,
      elementType: "Switch",
      groupId: "tooltip",
    },
    {
      key: "enableTemplate",
      label: "Enable Tooltip Template",
      tooltip: "This enables the tooltip template in the visualization. You can create your own template and apply it to the tooltip.",
      value: enableTemplate,
      elementType: "Switch",
      groupId: "tooltip",
      disabled: !showTooltip
    },
    {
      key: "tooltipOpenTemplate",
      label: "Tooltip Template",
      title: "Open Template",
      icon: true,
      value: '',
      onClick: onOpenTooltipTemplateDrawer,
      elementType: "Button",
      groupId: "tooltip",
      tooltip: "Click to open tooltip template in drawer.",
      disabled: !showTooltip || !enableTemplate
    },
    ...crosstabItems,
    ...filterItems
  ];

  const backgroundColorItem = {
    key: "backgroundColor",
    label: "Background",
    value: formatColor.backgroundColor,
    elementType: "Switch",
    groupId: "color",
    tooltip: "The color will be applied as background instead of foreground color.This property is applicable to only Table and Crosstab."
  }

  const customColorPaletteItems = [
    {
      key: "paletteName",
      label: "Name",
      value: customColorPaletteName || '',
      elementType: "Input",
      groupId: "chartTheme",
      tooltip: "Enter custom color palette name.",
    },
    {
      key: "customColors",
      label: `Colors (${customColors?.length || 0})`,
      value: customColors || [],
      elementType: "MultipleColorPicker",
      groupId: "chartTheme",
      tooltip: "Select custom colors for chart. You can select 10 colors only (Min and Max).",
      selectedColor: selectedMColor,
      handleColorChange: (color) => setSelectedMColor(color)
    },
    {
      key: "paletteButton",
      label: "",
      title: "Add",
      icon: <PlusOutlined />,
      value: '',
      onClick: onCustomColorPaletteAdd,
      elementType: "Button",
      groupId: "chartTheme",
      tooltip: "Add this color theme to your custom color collection.",
    },
  ]

  const gradientColorFields = [
    backgroundColorItem,
    {
      key: "minimum",
      label: "Minimum",
      value: formatMinColor,
      elementType: "ColorPicker",
      groupId: "color",
      tooltip: "Minimun color from where gradient color range will start generating.",
    },
    {
      key: "maximum",
      label: "Maximum",
      value: formatMaxColor,
      elementType: "ColorPicker",
      groupId: "color",
      tooltip: "Maximum color to where gradient color range will end.",
    },
    {
      key: "enableReverse",
      label: "Reverse",
      value: formatColorEnableReverse,
      elementType: "Switch",
      groupId: "color",
      tooltip: "Reverse the gradient color. Minimum will become maximum and Maximum will become minimum and vice versa.",
    },
    ...(["numeric"].includes(formatColorFieldType) ? [
      {
        key: "enableSteps",
        label: "Enable Steps",
        value: formatColorEnableSteps,
        elementType: "Switch",
        groupId: "color",
        tooltip: "Enable steps for gradient color range. Color will be divided into equal steps. Note: this property is applicable on numeric fields only.",
      },
      ...(formatColorEnableSteps ? [
        {
          key: "steps",
          label: "Steps",
          value: formatColorSteps || data.length,
          elementType: "InputNumber",
          groupId: "color",
          tooltip: "Number of steps for gradient color range. Default value is field's data count. Color range will be generated based on the value provided into steps. If min, max and center value is not provided through advance step options, then these values will be calculated at runtime. Note: min value required is 2.",
          minInput: 2,
        },
        {
          key: "enableAdvanceSteps",
          label: "Advance Step Options",
          value: enableAdvanceSteps,
          elementType: "Switch",
          groupId: "color",
          tooltip: "Enable advance step options for gradient color (start, end and center value for color range).",
        },
        ...(enableAdvanceSteps ? [
          {
            key: "minValue",
            label: "Start",
            value: formatColorMinValue || 0,
            elementType: "InputNumber",
            groupId: "color",
            tooltip: "Starting value, from where color range will start apply.",
          },
          {
            key: "maxValue",
            label: "End",
            value: formatColorMaxValue || 0,
            elementType: "InputNumber",
            groupId: "color",
            tooltip: "Ending value, where color range will end.",
          },
          {
            key: "centerValue",
            label: "Center",
            value: formatColorCenterValue || 0,
            elementType: "InputNumber",
            groupId: "color",
            tooltip: "Center value, where color range will be divided into equal steps.",
          },
        ] : [])
      ] : [])
    ]
      : [])
  ];

  const valueColorFields = [
    backgroundColorItem,
    {
      key: "defaultColor",
      label: "Default",
      value: formatColor.defaultColor,
      elementType: "ColorPicker",
      groupId: "color",
    },
    {
      key: "showAll",
      label: "Show all",
      value: showAllColorFields,
      elementType: "Switch",
      groupId: "color",
    },
  ];

  if (showDoughnutTitle) {
    let doughnutItems = [
      {
        key: "doughnutTitle",
        label: "Title",
        value: doughnutTitle,
        elementType: "Input",
        groupId: "radial",
      },
      {
        key: "doughnutPrefixType",
        label: "Prefix Type",
        value: doughnutPrefixType,
        elementType: "Select",
        groupId: "radial",
        values: [
          { key: "selectIcon", label: "Select" },
          { key: "staticValue", label: "Static Value" },
          { key: "url", label: "URL" },
          { key: "svg", label: "SVG" },
        ],
      },
      {
        key: "doughnutSuffixType",
        label: "Suffix Type",
        value: doughnutSuffixType,
        elementType: "Select",
        groupId: "radial",
        values: [
          { key: "selectIcon", label: "Select" },
          { key: "staticValue", label: "Static Value" },
          { key: "url", label: "URL" },
          { key: "svg", label: "SVG" },
        ],
      },
      {
        key: "doughnutPrefix",
        label: "Prefix",
        value: doughnutPrefix,
        elementType: getCardPrefixElementType(doughnutPrefixType),
        groupId: "radial",
        values: getCardIconValues(true),
      },
      {
        key: "doughnutSuffix",
        label: "Suffix",
        value: doughnutSuffix,
        elementType: getCardPrefixElementType(doughnutSuffixType),
        groupId: "radial",
        values: getCardIconValues(true),
      },
    ]
    items.push(...doughnutItems)
  }

  if (activeAxisRangeField?.data && isPresent) {
    axisItems = [
      {
        key: "hide",
        label: "Hide",
        value: activeAxisRangeField.data.hide,
        elementType: "Switch",
        groupId: "axisRange",
        tooltip: "Axis hide setting lets you hide an axis from the chart."
      },
      {
        key: "rotate",
        label: "Rotate",
        value: activeAxisRangeField.data.rotate,
        elementType: "InputNumber",
        groupId: "axisRange",
        tooltip: "Axis rotate setting lets you change the direction of the axis labels. The options are in degrees, such as 0, 90, or 270."
      },
      {
        key: "fontColor",
        label: "Tick Color",
        value: activeAxisRangeField.data.fontColor,
        elementType: "ColorPicker",
        groupId: "axisRange",
        tooltip: "Change the font color of the axis ticks.",
        remove: true
      },
      {
        key: "fontSize",
        label: "Tick Size",
        value: activeAxisRangeField.data.fontSize,
        elementType: "InputNumber",
        groupId: "axisRange",
        tooltip: "Change the font size of the axis ticks."
      }
    ]
    const axisRangeItems = [
      {
        key: "minRange",
        label: getRangeLabel(axisRangeDataType, 1),
        value: activeAxisRangeField.data.minRange,
        elementType: getRangeElementType(axisRangeDataType),
        showTime: axisRangeDataType === "dateTime" ? true : false,
        tooltip: getRangeTooltipInfo(axisRangeDataType, 1),
        groupId: "axisRange",
      },
      {
        key: "maxRange",
        label: getRangeLabel(axisRangeDataType, 2),
        value: activeAxisRangeField.data.maxRange,
        elementType: getRangeElementType(axisRangeDataType),
        showTime: axisRangeDataType === "dateTime" ? true : false,
        tooltip: getRangeTooltipInfo(axisRangeDataType, 2),
        groupId: "axisRange",
      },
    ]
    if (["numeric", "date", "dateTime", "time"].includes(axisRangeDataType)) {
      axisItems = [...axisRangeItems, ...axisItems]
    }
  }

  if (activeField?.values && isPresent && !enableCustomFormatting) {
    numericItems = [
      {
        key: "thousandSperator",
        label: "Thousand Separator",
        value: activeField.values.thousandSperator,
        elementType: "Switch",
        groupId: "format",
      },
      {
        key: "decimalPlace",
        label: "Decimal Place",
        placeHolder: "Enter Number",
        value: activeField.values.decimalPlace,
        elementType: "InputNumber",
        groupId: "format",
      },
      {
        key: "prefix",
        label: "Prefix",
        placeHolder: "Enter prefix",
        value: activeField.values.prefix,
        elementType: "Input",
        groupId: "format",
      },
      {
        key: "suffix",
        label: "Suffix",
        placeHolder: "Enter suffix",
        value: activeField?.values?.suffix ? activeField.values.suffix === "%" && !isPercentageEnabled ? "" : activeField.values.suffix : isPercentageEnabled ? "%" : "",
        elementType: "Input",
        groupId: "format",
      },
      {
        key: "spaceBeforeDisplayUnits",
        label: "Space Before Display Units",
        value: spaceBeforeDisplayUnits,
        elementType: "Switch",
        groupId: "format",
        tooltip: "It'll add a space before the display units."
      },
      {
        key: "displayUnits",
        label: "Display Units",
        value: activeField.values.displayUnits,
        elementType: "Select",
        groupId: "format",
        values: [
          { key: "auto", label: "Auto" },
          { key: "k", label: "Thousands" },
          { key: "m", label: "Millions" },
          { key: "b", label: "Billions" },
          { key: "none", label: "None" },
        ],
      },
      {
        key: "percentage",
        label: "Percentage",
        value: isPercentageEnabled,
        elementType: "Switch",
        groupId: "format",
        tooltip: "On Enable/Disable the percentage , whatever values are updated in the format properties will reamain as it is, only suffix value will get update with % if it is not updated already."
      },
      // {
      //   key: "numberCustom",
      //   label: "Custom",
      //   value: activeField.values.numberCustom,
      //   elementType: "Select",
      //   groupId: "format",
      //   values: [],
      // },
    ];

    dateItems = [
      {
        key: "day",
        label: "Day",
        value: activeField.values.day,
        elementType: "Select",
        groupId: "format",
        values: [
          { key: "dayNumber", label: "Number" },
          { key: "dayNumWithZero", label: "Number starts with zero" },
          { key: "none", label: "None" },
        ],
      },
      {
        key: "week",
        label: "Week",
        value: activeField.values.week,
        elementType: "Select",
        groupId: "format",
        values: [
          { key: "weekNumber", label: "Number" },
          { key: "weekAbbrevation", label: "Abbreviation" },
          { key: "weekFull", label: "Full" },
          { key: "none", label: "None" },
        ],
      },
      {
        key: "month",
        label: "Month",
        value: activeField.values.month,
        elementType: "Select",
        groupId: "format",
        values: [
          { key: "monthNum", label: "Number" },
          { key: "monthNumWithZero", label: "Number starts with zero" },
          { key: "monthAbbrevation", label: "Abbreviation" },
          { key: "monthFull", label: "Full" },
          { key: "none", label: "None" },
        ],
      },
      {
        key: "quarter",
        label: "Quarter",
        value: activeField.values.quarter,
        elementType: "Select",
        groupId: "format",
        values: [
          { key: "quaterNumber", label: "Number" },
          { key: "quaterAbbrevation", label: "Abbreviation" },
          { key: "none", label: "None" },
        ],
      },
      {
        key: "year",
        label: "Year",
        value: activeField.values.year,
        elementType: "Select",
        groupId: "format",
        values: [
          { key: "4digit", label: "4 Digit" },
          { key: "2digit", label: "2 Digit" },
          { key: "none", label: "None" },
        ],
      },
      {
        key: "dateSeperator",
        label: "Date Separator",
        value: activeField.values.dateSeperator,
        elementType: "Select",
        groupId: "format",
        values: [
          { key: "-", label: "-" },
          { key: ",", label: "," },
          { key: "/", label: "/" },
          { key: ".", label: "." },
        ],
      },
      // {
      //   key: "dateCustom",
      //   label: "Custom",
      //   value: activeField.values.dateCustom,
      //   elementType: "Select",
      //   groupId: "format",
      //   values: [],
      // },
    ];

    timeItems = [
      {
        key: "hour",
        label: "Hour",
        value: activeField.values.hour,
        elementType: "Select",
        groupId: "format",
        values: [
          { key: "12hr", label: "12 Hr" },
          { key: "24hr", label: "24 Hr" },
          { key: "none", label: "None" },
        ],
      },
      {
        key: "minute",
        label: "Minute",
        value: activeField.values.minute,
        elementType: "Select",
        groupId: "format",
        values: [
          { key: "mintuesNumber", label: "Number" },
          { key: "none", label: "None" },
        ],
      },
      {
        key: "second",
        label: "Second",
        value: activeField.values.second,
        elementType: "Select",
        groupId: "format",
        values: [
          { key: "secondsNumber", label: "Number" },
          { key: "none", label: "None" },
        ],
      },
      {
        key: "milliSecond",
        label: "Millisecond",
        value: activeField.values.milliSecond,
        elementType: "Select",
        groupId: "format",
        values: [
          { key: "milliSecondsNumber", label: "Number" },
          { key: "none", label: "None" },
        ],
      },
      {
        key: "timeSeperator",
        label: "Time Separator",
        value: activeField.values.timeSeperator,
        elementType: "Select",
        groupId: "format",
        values: [{ key: ":", label: ":" }],
      },
    ];

    autoFormattingProperty.push({
      key: "autoFormatting",
      label: "Auto Formatting",
      value: activeField?.values?.autoFormatting ?? isCreateMode,
      elementType: "Switch",
      groupId: "format",
      tooltip: "Auto Formatting is enabled by default for new reports, while it remains disabled for older reports.",
    })
  }

  if (reportFields?.length && fetchAllRecords) {
    let newTableField = {
      key: 'showHiddenFields',
      label: 'Show Hidden Fields',
      tooltip: "This will show all hidden fields in the table's Show Total menu.",
      value: table.showHiddenFields,
      elementType: 'Switch',
      groupId: "table",
    }

    tableFields = reportFields.map(({ name, type }) => {
      let tempProps = {
        key: name,
        label: name,
        tooltip: `${name} : Choose an action to add to this column`,
        value: table[name] || [],
        elementType: "Select",
        groupId: "table",
        multiSelect: true,
        values: [
          { key: "sort", label: "Sort" },
          { key: "search", label: "Search" },
          { key: "filter", label: "Filter" },
          { key: "hide", label: "Hide" },
        ],
      }
      if (type !== "discrete") {
        tempProps.values.push({ key: "total", label: "Total" });
      }
      return tempProps;
    })
    tableFields = [newTableField, ...tableFields];
  }

  if (shape?.fields?.length && mapShowAllShapes) {
    let { id } = shape.fields[0];
    let field = rFields.find(tempField => tempField.id === id);
    let shapeField = getFieldDisplayName(field);
    if (shapeField) {
      let dataWithField = getFieldData(data, shapeField);
      dataWithField = [...new Set(dataWithField)];
      if (dataWithField?.length) {
        shapeFields = dataWithField.map((tempF) => {
          let tempProps = {
            key: tempF,
            label: tempF,
            tooltip: `${tempF} : Add URL of custom icon for this record`,
            value: shapeP[tempF] || '',
            elementType: "Input",
            groupId: "shape",
          }
          return tempProps;
        })
      }
    }
  }

  // if (['sankey'].includes(relationChartType)) {
  //   sankeyChartFields = [
  //     {
  //       key: "sourceField",
  //       label: "Source Field",
  //       tooltip: "Select Source Field for relation chart.",
  //       elementType: "Select",
  //       value: relationChart?.sourceField,
  //       groupId: "relationChart",
  //       values: getFields(report, true, "relationChart"),
  //     },
  //     {
  //       key: "targetField",
  //       label: "Target Field",
  //       tooltip: "Select Target Field for relation chart.",
  //       elementType: "Select",
  //       value: relationChart?.targetField,
  //       groupId: "relationChart",
  //       values: getFields(report, true, "relationChart"),
  //     },
  //     {
  //       key: "weightField",
  //       label: "Weight Field",
  //       tooltip: "Select Weight Field for relation chart.",
  //       elementType: "Select",
  //       value: relationChart?.weightField,
  //       groupId: "relationChart",
  //       values: getFields(report, false, "relationChart"),
  //     },
  //   ]
  // }
  let visualizationWidth = {
    key: "width",
    label: "Width",
    value: canvasWidth,
    elementType: "InputNumber",
    groupId: "canvas",
    tooltip: "This will be the width of the visaulization area in pixels."
  }

  let visualizationHeight = {
    key: "height",
    label: "Height",
    value: canvasHeight,
    elementType: "InputNumber",
    groupId: "canvas",
    tooltip: "This will be the height of the visaulization area in pixels."
  }

  let cardVizWidth = {
    key: "cardWidth",
    label: "Width",
    value: cardWidth,
    elementType: "InputNumber",
    groupId: "card",
    tooltip: "This will be the width of the visaulization area in pixels."
  }

  let cardVizHeight = {
    key: "cardHeight",
    label: "Height",
    value: cardHeight,
    elementType: "InputNumber",
    groupId: "card",
    tooltip: "This will be the height of the visaulization area in pixels."
  }

  if (canvasView) {
    switch (canvasView) {
      case 'fitWidth':
        canvasFields.push(visualizationHeight)
        break;
      case 'fitHeight':
        canvasFields.push(visualizationWidth)
        break;
      case 'entireView':
        canvasFields.push(visualizationWidth, visualizationHeight)
        break;
      default:
        break;
    }
  }

  if (cardView) {
    switch (cardView) {
      case 'fitWidth':
        canvasFields.push(cardVizHeight)
        break;
      case 'fitHeight':
        canvasFields.push(cardVizWidth)
        break;
      case 'standard':
        canvasFields.push(cardVizWidth, cardVizHeight)
        break;
      default:
        break;
    }
  }

  let finalItems = [...items, ...autoFormattingProperty, ...canvasFields]

  if (enablePagination) {
    finalItems = [
      ...finalItems,
      {
        key: "starts",
        label: "Start",
        tooltip: "This property set, 'from' how much percentage you want to see data. By default it is 0",
        value: charts?.starts,
        elementType: "InputNumber",
        groupId: "charts",
        minInput: 0,
        maxInput: 100
      },
      {
        key: "ends",
        label: "End",
        tooltip: "This property set, 'to' how much percentage you want to see data. By default it is 10%",
        value: charts?.ends,
        elementType: "InputNumber",
        groupId: "charts",
        minInput: 0,
        maxInput: 100
      },
    ]
  }

  if (isTrend) {
    finalItems = [
      ...finalItems,
      // {
      //   key: "group",
      //   label: "Group",
      //   value: card?.group,
      //   elementType: "Select",
      //   groupId: "card",
      //   multiSelect: true,
      //   values: [
      //     { key: 'autoAggregate', label: 'Auto Aggregate' },
      //     { key: 'sum', label: 'Sum' },
      //     { key: 'min', label: 'Min' },
      //     { key: 'max', label: 'Max' },
      //     { key: 'distinct', label: 'Distinct' },
      //     { key: 'count', label: 'Count' },
      //     { key: 'average', label: 'Average' },
      //   ],
      // },
      {
        key: "displayTrend",
        label: "Display",
        value: displayTrend,
        elementType: "Select",
        groupId: "card",
        multiSelect: true,
        values: [
          { key: 'trend', label: 'Trend' },
          { key: 'value', label: 'Value' },
        ],
        tooltip: "Select what needs to be display in the trend table/chart. By default both trend and value will be displayed."
      },
      {
        key: "trendPrefix",
        label: "Trend Prefix",
        placeHolder: "Enter trend prefix",
        value: trendPrefix,
        elementType: "Input",
        groupId: "card",
        tooltip: "Trend prefix will be displayed in the trend table/chart. By default it is vs."
      },
      {
        key: "trendPrefixPosition",
        label: "Position",
        value: trendPrefixPosition,
        elementType: "Select",
        groupId: "card",
        tooltip: "Set the position of the trend prefix. By default it is center. Start means before trend, center means after trend and before detail, end means after detail.",
        values: [
          { key: 'start', label: 'Start' },
          { key: 'center', label: 'Center' },
          { key: 'end', label: 'End' },
        ],
      }
    ]
  }

  if (enableCustomColors) {
    finalItems = [...finalItems, ...customColorPaletteItems]
  }

  if (formatDatatype === "numeric") {
    finalItems = [...finalItems, ...numericItems];
  }
  if (formatDatatype === "date") {
    finalItems = [...finalItems, ...dateItems];
  }
  if (formatDatatype === "time") {
    finalItems = [...finalItems, ...timeItems];
  }
  if (formatDatatype === "dateTime") {
    finalItems = [...finalItems, ...dateItems, ...timeItems];
  }
  if (formatColorStyle === "gradient") {
    finalItems = [...finalItems, ...gradientColorFields];
  }
  if (formatColorStyle === "fieldValue") {
    finalItems = [...finalItems, ...valueColorFields];
    if (showAllColorFields) {
      finalItems = [
        ...finalItems,
        ...getColorDataFields(data, formatColor, report, showAllColorFields, formatColorField),
      ];
    }
  }


  if (progressChartType === "gauge") {
    finalItems = [...finalItems, ...getProgressRangeColorsItems(progressRange, progressRangeColors),]
  }

  if (axisRangeId) {
    finalItems = [...finalItems, ...axisItems];
  }

  if (tableFields?.length) {
    finalItems = [...finalItems, ...tableFields]
  }

  if (shapeFields?.length) {
    finalItems = [...finalItems, ...shapeFields]
  }

  // if (sankeyChartFields?.length) {
  //   finalItems = [...finalItems, ...sankeyChartFields]
  // }

  return finalItems;
};

export const getInfoItems = () => {
  let infoItems = [{
    key: "crosstab",
    infos: ["The front end will perform the calculation. It will compute the total and subtotal values."],
  },];
  return infoItems
};

export const getCustomFieldDataType = (metadata = [], field = {}) => {
  if (metadata && Array.isArray(metadata)) {
    let tempMetadata = [...metadata]
    const fieldName = getFieldDisplayName(field)
    for (const obj of tempMetadata) {
      for (const key in obj) {
        if (obj[key].name === fieldName) {
          return obj[key].type;
        }
      }
    }
  } else {
    if (field && !isEmpty(field)) {
      if (field?.custom && field?.floatingType === "continous") {
        return "numeric"
      }
      if (field?.custom && field?.floatingType === "discrete") {
        return "text"
      }
    }
  }
  return "";
}

export const rgbaToHex = (rgba) => {
  const regex = /rgba?\((\d+),\s*(\d+),\s*(\d+),?\s*([\d\.]+)?\)/;
  const result = rgba.replace(/\s/g, '').match(regex);

  if (!result) {
    return rgba;
  }

  const r = parseInt(result[1], 10);
  const g = parseInt(result[2], 10);
  const b = parseInt(result[3], 10);

  const toHex = (value) => value.toString(16).padStart(2, '0');

  const hexR = toHex(r);
  const hexG = toHex(g);
  const hexB = toHex(b);

  return `#${hexR}${hexG}${hexB}`;
}


export const getAppliedUpdatedProperties = (data = {}) => {
  let initialReport = getIntialReportState({ active: true });
  const {
    itemsData,
    fields,
    fieldId,
    showAllColorFields,
    showAllFormatFields,
    formatColorField,
    formatColorStyle,
    reportData,
    axisRangeId,
    axisRangeDataType,
    axisRangeFields,
    synchronize,
    report,
    isTemplateEdited,
    tooltipTemplate
  } = data

  let title = getUpdatedProperties(itemsData, 'title');
  let subTitle = getUpdatedProperties(itemsData, 'subTitle');
  let format = getFormatFields(itemsData, 'format', fields, fieldId, showAllFormatFields);
  let card = getUpdatedCardProperties(itemsData, 'card');
  let axisRange = getUpdatedRangeProperties(reportData, itemsData, 'axisRange', axisRangeFields, axisRangeId, axisRangeDataType, synchronize, report?.reportData?.metadata);
  let cache = getUpdatedProperties(itemsData, 'cache', initialReport);
  let formatColor = getUpdatedColorProperties(
    itemsData,
    'color',
    showAllColorFields,
    formatColorField,
    formatColorStyle,
    report
  )
  let bar = getUpdatedProperties(itemsData, 'bar');
  let radial = getUpdatedProperties(itemsData, 'radial');
  let legend = getUpdatedProperties(itemsData, 'legend');
  let labels = getUpdatedProperties(itemsData, 'labels');
  let crosstab = getUpdatedProperties(itemsData, 'crosstab');
  let table = getUpdatedProperties(itemsData, 'table');
  let map = getUpdatedProperties(itemsData, 'map');
  let line = getUpdatedProperties(itemsData, "line");
  let progress = getUpdatedProperties(itemsData, "progress");
  let radar = getUpdatedProperties(itemsData, "radar");
  let shape = getUpdatedProperties(itemsData, "shape");
  let chartTheme = getUpdatedProperties(itemsData, "chartTheme");
  let charts = getUpdatedProperties(itemsData, "charts");
  let treemap = getUpdatedProperties(itemsData, "treemap");
  let relationChart = getUpdatedProperties(itemsData, "relationChart");
  let canvas = getUpdatedProperties(itemsData, "canvas")
  let filter = getUpdatedProperties(itemsData, "filter")
  let tooltip = getUpdatedProperties(itemsData, "tooltip")
  tooltip = { ...cloneDeep(tooltip), isTemplateEdited, tooltipTemplate }

  return {
    title,
    subTitle,
    format,
    card,
    axisRange,
    cache,
    formatColor,
    bar,
    radial,
    legend,
    labels,
    crosstab,
    table,
    map,
    line,
    progress,
    radar,
    shape,
    chartTheme,
    charts,
    treemap,
    relationChart,
    canvas,
    filter,
    tooltip
  }
}

export const getTooltipForSelectComp = ({
  data, showEditButton = false,
  handleClick = () => { },
  handleDelete = () => { },
  handleCopy = () => { }
}) => {

  let parentStyles = {
    display: 'flex',
    maxWidth: '600px',
    flexWrap: 'wrap'
  }

  let childStyles = {
    width: '16px',
    height: '16px',
    margin: '2px',
    borderRadius: '50%',
  }

  const content = (
    <div>
      <Tooltip zIndex={6000} title="Edit"> <Button size="small" type="default" onClick={handleClick} style={{ border: "unset" }} icon={<EditOutlined />} /></Tooltip>
      <Tooltip zIndex={6000} title="Delete"> <Button size="small" type="default" onClick={handleDelete} style={{ border: "unset" }} icon={<DeleteOutlined />} /></Tooltip>
      <Tooltip zIndex={6000} title="Copy to all the open reports"> <Button size="small" type="default" onClick={handleCopy} style={{ border: "unset" }} icon={<CopyOutlined />} /></Tooltip>
    </div>
  );

  return (
    <div style={parentStyles} onClick={(e) => e.stopPropagation()}>
      {data?.map((item) => {
        return (
          <div style={{ ...childStyles, background: item, userSelect: 'none' }} />
        )
      })}
      {showEditButton && <Popover placement="bottom" content={content} zIndex={5000}>
        <Tooltip title="Actions"><Button size="small" type="default" style={{ border: "unset" }} icon={<MoreOutlined />} /></Tooltip>
      </Popover>}


    </div>
  )
}

export const checkIfColorPaletteHasItem = (item, data) => {
  if (!data || !typeof data === 'object' || !item) return false
  return data?.['Custom Colors'] ? Object.keys(data['Custom Colors']).includes(item) || false : false;
}


export const getColorSelectOptions = ({ data = {}, chartColorPalette = {}, onEdit = () => { }, onDelete = () => { }, onCopy = () => { } }) => {
  return Object.keys(data)?.map((key) => {
    let colors = data[key]
    if (isEmpty(colors)) return null;
    return {
      label: key,
      options: Object.keys(colors).map((item) => {
        let tooltipTitle = getTooltipForSelectComp({
          data: colors[item],
          showEditButton: checkIfColorPaletteHasItem(item, chartColorPalette) || false,
          handleClick: () => onEdit(item),
          handleDelete: () => onDelete(item),
          handleCopy: () => onCopy(item)
        })
        return {
          key: item, value: item, label: <Tooltip title={tooltipTitle} color="#fff" placement="left">{toCapitalize(item)}</Tooltip>,
        }
      }),
    }
  }).filter(Boolean);
}
