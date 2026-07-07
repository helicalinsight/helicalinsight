import { G2 } from '@ant-design/plots';
import { generateColorRange, getFieldName, getHTMLColorFormat } from "../../hi-editing-area/utils/property-utils";
import { getGridChartColorSchemeFromPalette } from '../utils/grid-chart-utils';
import moment from 'moment';
import { getCardValue } from '../../hi-editing-area/components/properties/property-card-utils';
import Tooltip from 'antd/es/tooltip';
import { getPropertyFieldInfo, getPropertyText, tooltipTemplateLiquidJS } from '../utils/utillities';
import _, { cloneDeep, isEmpty } from 'lodash';
import { getFieldDisplayName } from '../../../../utils/utilities';


export const subVizTypes = ["bar", "line", "area", "point", "tick", "arc", "doughnut", "heatmap", "text", "map", "waterfall", "radar", "gauge", "progress", "relation", "calendar", "table", "kpi", "trend"];

export const divStyles = {
  position: "absolute",
  color: "white",
  background: "#616161",
  boxShadow: "rgb(174, 174, 174) 0px 0px 10px",
  borderRadius: "4px",
  padding: "5px",
  fontSize: "12px",
};

export const setStyles = (container, styles) => {
  for (const key in styles) {
    container.style[key] = styles[key];
  }
};

export const calculateScaleWidthByValue = (value) => {
  let span = document.createElement('span');
  span.innerHTML = `${value}`;
  span.style.fontSize = '12px';
  span.style.visibility = 'hidden';
  span.style.position = 'absolute';
  document.body.appendChild(span);
  let spanWidth = span.offsetWidth;
  document.body.removeChild(span);
  return spanWidth;
}
export const prepareHierarchyData = (data, nameField, valueField) => {
  const result = [];
  data?.forEach(element => {
    let tempObj = {
      name: element[nameField],
      value: element[valueField],
      ...element
    }
    result.push(tempObj)
  });
  return result;
}


export const getProgressChartProperties = (data, fields, marks) => {
  let clrs = {
    minimum: {
      "r": 183,
      "g": 192,
      "b": 232,
      "a": 1
    },
    maximum: {
      "r": 84,
      "g": 108,
      "b": 230,
      "a": 1
    },
  }
  const { colorField = null, targetField = null, progressRangeColors = [], themeColors = [] } = marks || {}
  let fieldsValues = fields?.reduce((acc, next) => {
    acc[next] = data.reduce((p, n) => {
      return p + n[next]
    }, 0)
    return acc;
  }, {})
  let sortedValues = Object.values(fieldsValues).sort((a, b) => a - b)
  let min = sortedValues[0]
  let max = targetField ? fieldsValues[targetField] : sortedValues[sortedValues.length - 1]
  let minField = Object.keys(fieldsValues).find(key => fieldsValues[key] === min)
  let maxField = targetField ? targetField : Object.keys(fieldsValues).find(key => fieldsValues[key] === max)
  let progressColorRange = null;
  if (colorField) {
    let colorValues = data.map(item => item[colorField])
    progressColorRange = {
      ticks: getTickValues(colorValues),
      color: themeColors?.length ? getGridChartColorSchemeFromPalette(themeColors, data) : generateColorRange(clrs.minimum, clrs.maximum, data?.length),
    }
  } else {
    if (progressRangeColors?.length) {
      progressColorRange = {
        ticks: getTickValues(progressRangeColors),
        color: progressRangeColors.map((color) => getHTMLColorFormat(color)),
      }
    }
  }
  return { min, max, minField, maxField, progressColorRange }
}

export const getTickValues = (data, returnPercentValues = false) => {
  const length = data.length;
  const ticks = [];

  for (let i = 0; i <= length; i++) {
    if (returnPercentValues) {
      let percent = (i / length) * 100;
      if (percent % 10 === 0) {
        ticks.push(percent);
      } else {
        ticks.push(percent.toFixed(2));
      }
    } else {
      ticks.push(i / length);
    }
  }

  return ticks;
}


export const antdChartCategoryColors = [
  "#5B8FF9",
  "#5AD8A6",
  "#5D7092",
  "#F6BD16",
  "#6F5EF9",
  "#6DC8EC",
  "#945FB9",
  "#FF9845",
  "#1E9493",
  "#FF99C3"
]

export const antdChartContinuousColors = [
  "#B8E1FF",
  "#9AC5FF",
  "#7DAAFF",
  "#5B8FF9",
  "#3D76DD",
  "#085EC0",
  "#0047A5",
  "#00318A",
  "#001D70"
]

export const getAntChartTheme = (colorPalette, colors = {}) => {
  let themeColors = [];
  let allColors = Object.keys(colors)?.reduce((acc, next) => {
    acc = { ...acc, ...colors[next] };
    return acc;
  }, {});
  if (!colorPalette?.length) return themeColors;
  colorPalette?.forEach((color) => {
    let tempColor = allColors?.[color] || []
    themeColors = [...themeColors, ...tempColor];
  })
  return themeColors;
}

export const registerAntdTheme = ({ colors = [] }) => {
  const { registerTheme } = G2;
  registerTheme('custom-theme', {
    colors10: colors,
    colors20: [
      ...colors,
      // ...colors
    ],
  });
}

export const prepareHierarchicalData = (data, dimensions, measures) => {
  if (dimensions.length === 0 || measures.length === 0) {
    return [];
  }

  function buildHierarchy(data, dimensions, measures, depth = 0) {
    const dimension = dimensions[depth];
    const isLastDimension = depth === dimensions.length - 1;

    const groupedData = data.reduce((acc, element) => {
      const key = element[dimension];
      if (!acc[key]) {
        acc[key] = [];
      }
      acc[key].push(element);
      return acc;
    }, {});

    return Object.keys(groupedData).map(key => {
      const childrenData = groupedData[key];
      const node = {
        name: key,
        ...childrenData[0],
      };

      measures.forEach(measure => {
        node[measure] = isLastDimension
          ? childrenData.reduce((sum, element) => sum + element[measure], 0)
          : null;
        node.value = isLastDimension
          ? childrenData.reduce((sum, element) => sum + element[measure], 0)
          : null;
        if (!node.value) {
          delete node.value
        }
        if (!node[measure]) {
          delete node[measure]
        }
      });

      if (!isLastDimension) {
        node.children = buildHierarchy(childrenData, dimensions, measures, depth + 1);
        measures.forEach(measure => {
          node[measure] = node.children.reduce((sum, child) => sum + (child[measure] || 0), 0);
        });
      }

      return node;
    });
  }

  return buildHierarchy(data, dimensions, measures);
}

const getDateData = (inputDate) => {
  const date = moment(inputDate, ["YYYY-MM-DD HH:mm:ss.S", "YYYY-MM-DD"], true);

  if (!date.isValid()) return {};
  return {
    date: date.format("YYYY-MM-DD"),
    month: date.month(),
    day: date.date(),
    week: date.week().toString(),
    week_day: date.day(),
  };
}

export const constructCalendarChartData = (data, dateField, columns = [], rows = []) => {
  let xField = columns[0], yField = rows[0] ?? columns[0];
  let calendarData = data.map((item) => {
    return {
      ...item,
      ...getDateData(item[dateField]),
    }
  })
  if (calendarData?.every((item) => item.hasOwnProperty('date') && item.hasOwnProperty('week'))) {
    xField = 'week';
    yField = 'week_day';
  }
  return {
    data: calendarData,
    xField,
    yField,
  }
}

export const getCalendarData = (data, xField, yField) => {
  if (!data?.length || !xField || !yField) return data;
  return data.map((item) => {
    return {
      ...item,
      [xField]: String(item[xField]),
      [yField]: String(item[yField]),
    }
  })
}


export const calculateTrendPercentage = (value, primaryValue) => {
  return (((primaryValue - value) / value)).toFixed(2).toString();
};

let cardTableValueRenderFn = ({ value, report, measure, icon = null, isTrend = false, trendFieldId = null }) => {
  const { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(
    report,
    measure,
    isTrend,
    trendFieldId
  );
  let formattedText = getPropertyText({
    text: value,
    applyOn: "pane",
    isApplyClicked,
    fieldType,
    formatField,
  });

  let formattedTextonTooltip = getPropertyText({
    text: value,
    applyOn: "tooltip",
    isApplyClicked,
    fieldType,
    formatField,
  });
  return (
    <Tooltip placement="top" title={formattedTextonTooltip} color="#000">
      {icon ?
        <span className={"hr-card-table-percentage"}>
          <span>{icon}</span>
          <span>{formattedText}</span>
        </span>
        :
        <span>{formattedText}</span>}

    </Tooltip>
  );
}

export const getTrendData = ({ data, isTrend, target, measure, detailField }) => {
  let tempData = data;
  if (isTrend) {
    tempData = data.filter((_, i) => i > 0);
  }

  return tempData.map((item) => {
    return {
      trend: calculateTrendPercentage(item[measure], target),
      details: item[detailField],
      value: item[measure]
    }
  })
}

export const getCardTableData = ({ data, measure, detailField, target, isTrend = false, displayTrend = [], report, trendPrefix, trendPrefixPosition }) => {

  const { reportData = {} } = report || {};
  const { properties = {} } = reportData || {};
  const { card = {} } = properties || {};

  let compareColumn = {
    title: 'vs',
    dataIndex: 'vs' ?? '',
    render: () => {
      return (
        <span>{trendPrefix ?? 'vs.'}</span>
      )
    }
  }

  let columns = [
    {
      title: 'details',
      dataIndex: 'details',
      render: (_, { details }) => {
        return (
          <Tooltip placement="top" title={details} color="#000">
            <b>{details}</b>
          </Tooltip>
        )
      }
    },
  ]

  if (isTrend && trendPrefix) {
    if (trendPrefixPosition === 'center') {
      columns.unshift(compareColumn)
    }

    if (trendPrefixPosition === 'end') {
      columns.push(compareColumn)
    }
  }

  if (!isTrend) {
    columns.push({
      title: 'value',
      dataIndex: 'value',
      render: (_, { value }) => cardTableValueRenderFn({ value, report, measure })
    })
  }

  if (isTrend) {
    columns = [
      displayTrend?.includes('trend') ? {
        title: 'trend',
        dataIndex: 'trend',
        render: (_, { trend, value }) => {
          let percentage = calculateTrendPercentage(value, target)
          percentage = Number(percentage)
          let trendFieldId = report?.fields?.find(field => field.addedAs === 'trend_field')?.id;
          let iconName = percentage < 0 ? "ArrowDownOutlined" : "ArrowUpOutlined";
          let colorValue = percentage < 0 ? { r: 255, g: 0, b: 0, a: 1 } : { r: 0, g: 255, b: 0, a: 1 };
          let icon = getCardValue('selectIcon', iconName, colorValue, false)
          return cardTableValueRenderFn({ value: trend, report, measure: "Trend", icon, isTrend: true, trendFieldId })
        }
      } : null,
      ...columns,
      displayTrend.includes('value') ? {
        title: 'value',
        dataIndex: 'value',
        render: (_, { value }) => cardTableValueRenderFn({ value, report, measure })
      } : null
    ].filter(Boolean);

    if (trendPrefix && trendPrefixPosition === 'start') {
      columns.unshift(compareColumn)
    }
  }
  let cardTableData = getTrendData({ data, isTrend, measure, detailField, target })

  return {
    columns,
    cardTableData
  }
}


export const getDataOrderBy = (data, monthData, order, fieldName) => {
  return order === 'asc' ? data.sort((a, b) => monthData.indexOf(a[fieldName]) - monthData.indexOf(b[fieldName])) : data.sort((a, b) => monthData.indexOf(b[fieldName]) - monthData.indexOf(a[fieldName]));
}

export const getMonthNameSortedData = (data, field) => {
  if (!field) return data;
  let { orderBy, databaseFunction } = field;
  if (!orderBy || !databaseFunction) return data;
  let fieldName = getFieldDisplayName(field)
  const monthNames = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December",];
  const months = ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"];
  if (databaseFunction) {
    const { value = '' } = databaseFunction || {};
    const [order] = orderBy || [];
    switch (value) {
      case 'MONTHNAME':
        data = getDataOrderBy(data, monthNames, order, fieldName);
        break;
      case "MONTH":
        data = getDataOrderBy(data, months, order, fieldName);
        break;
      default:
        break;
    }
  }
  return data;
}

export const getPropertyColorField = (formatColor = {}, report) => {
  if (!formatColor || isEmpty(formatColor) || isEmpty(report)) return null;
  if (formatColor.formatColorStyle) {
    const { formatColorField = '' } = formatColor
    const propertyColorField = getFieldName(formatColorField, report) ?? ''
    return propertyColorField
  }
  return null;
}

export const getAntChartLegendLabelConfig = (report = {}, colorField) => {
  if (!report) return {};
  const { formatColor } = report?.reportData?.properties || {};
  return {
    label: {
      formatter: (text) => {
        let tempField = getPropertyColorField(formatColor, report) ?? colorField;
        const { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(report, tempField);
        const value = getPropertyText({
          text,
          applyOn: "legend",
          isApplyClicked,
          fieldType,
          formatField,
        });
        return value;
      }
    }
  }
};

export const getAntCardRowContainerStyles = (align = '') => {
  let baseStyles = {
    justifyContent: 'center',
    alignItems: 'center',
    height: '100%'
  }
  switch (align) {
    case 'left':
      baseStyles.justifyContent = 'flex-start';
      break;
    case 'top':
      baseStyles.alignItems = 'flex-start';
      break;
    case 'bottom':
      baseStyles.alignItems = 'flex-end';
      break;
    case 'right':
      baseStyles.justifyContent = 'flex-end';
      break;
    case 'topLeft':
      baseStyles.alignItems = 'flex-start';
      baseStyles.justifyContent = 'flex-start';
      break;
    case 'topRight':
      baseStyles.alignItems = 'flex-start';
      baseStyles.justifyContent = 'flex-end';
      break;
    case 'bottomLeft':
      baseStyles.alignItems = 'flex-end';
      baseStyles.justifyContent = 'flex-start';
      break;
    case 'bottomRight':
      baseStyles.alignItems = 'flex-end';
      baseStyles.justifyContent = 'flex-end';
      break;
    default:
      break;
  }

  return baseStyles;
}

export const getChartTooltipTemplate = ({ data = [], tooltipTemplate = "", report, Notify }) => {
  const tooltipTemplateData = {};

  const tooltipData = data;
  tooltipData.forEach((e) => {
    const { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(report, e[0]);
    const value = getPropertyText({
      text: e[1],
      applyOn: "tooltip",
      isApplyClicked,
      fieldType,
      formatField,
    });
    tooltipTemplateData[e[0]] = value;
  });

  let container = document.createElement("div");
  let child = document.createElement("div");

  container.classList.add("hreport-tooltip-template-container");
  child.innerHTML = tooltipTemplateLiquidJS({ value: tooltipTemplate, scope: tooltipTemplateData, Notify });
  container.appendChild(child);
  setStyles(container, divStyles);
  return container;
}