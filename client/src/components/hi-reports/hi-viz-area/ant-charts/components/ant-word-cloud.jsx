import { WordCloud } from "@ant-design/plots";
import { Row } from "antd";
import randomColor from "randomcolor";
import { useEffect } from "react";
import { useDispatch } from "react-redux";
import { setMenuData, updateSubVizType } from "../../../../../redux/actions/hreport.actions";
import notify from "../../../../hi-notifications/notify";
import { generateColorRange, generateColorsFromRange, getFieldData, getFieldName, getFiledValueColor, getHTMLColorFormat } from "../../../hi-editing-area/utils/property-utils";
import CellCard from "../../cell-card";
import Toolbar from "../../toolbar";
import { getPropertyElement, getPropertyFieldInfo, getPropertyText } from "../../utils/utillities";
import "../ant-chart.scss";
import { divStyles, getChartTooltipTemplate } from "../ant-utils";
import ErrorArea from "./ant-error-area";


const WordCloudForSingleDimension = (props) => {
  const dispatch = useDispatch();
  const {
    cloudData,
    cloudKey,
    colorField,
    interactiveMode,
    drillDown,
    drillThrough,
    reportId,
    addFilter,
    sizeField,
    report,
    addForwardFilterIPC,
    deleteBackwardFilterIPC,
    refreshFiltersIPC,
    metadata,
    formatColor,
    format,
    titleStyle,
    subTitleStyle,
    height,
    themeColors,
    showTooltip,
    tooltipTemplate,
    enableTemplate
  } = props;
  const data = cloudData.map((item) => ({ ...item, name: item[cloudKey], value: 1 }));
  const { title, subTitle, axisRange } = report?.reportData?.properties || {};
  const Notify = notify(dispatch);
  const { legendPosition } = report?.reportData?.properties.legend;

  useEffect(() => {
    dispatch(updateSubVizType({ value: "_all_", name: "text" }));
  }, []);

  if (metadata.length > 0 && metadata[0]) {
    const columnsArray = Object.entries(metadata[0]).map((e) => e[1]);
    for (let i = 0; i < columnsArray.length; i++) {
      if (columnsArray[i].name === sizeField && ["text"].includes(columnsArray[i].type)) {
        const msg =
          "Invalid data. Please use proper configuration. Only numeric data can be added to size to generate chart.";
        Notify.warning({ type: "Frontend", message: msg });
        return <ErrorArea message={msg} />;
      }
    }
  }

  const setStyles = (container, styles) => {
    for (const key in styles) {
      container.style[key] = styles[key];
    }
  };
  const config = {
    data,
    wordField: "name",
    weightField: "value",
    color: "rgba(76, 132, 238, 1)",
    legend:
      legendPosition === "none" ? false : { position: legendPosition ? legendPosition : "right" },
    // tooltip: false,
    tooltip: !showTooltip ?
      false : {
        customContent: function (title, data) {
          if (data && data.length > 0) {
            const container = document.createElement("div");
            let tooltipData = Object.entries(data[0].data.datum);
            tooltipData = tooltipData.filter((e) => e[0] !== "value" && e[1] !== 1);
            tooltipData.forEach((e) => {
              if (e[0] !== "name") {
                const { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(report, e[0]);
                const value = getPropertyText({
                  text: e[1],
                  applyOn: "tooltip",
                  isApplyClicked,
                  fieldType,
                  formatField,
                });
                const child = document.createElement("div");
                child.innerHTML = `${e[0]} : ${value}`;
                container.appendChild(child);
              }
            });
            setStyles(container, divStyles);
            if (enableTemplate) {
              return getChartTooltipTemplate({ data: tooltipData, tooltipTemplate, report, Notify });
            }
            return container;
          }
        },
      },
    interactions: [
      {
        type: "element-active",
      },
    ],
  };

  if (colorField) {
    config.colorField = colorField;
    const randomColors = randomColor({
      count: data?.length || 20,
      hue: 'random',
    })
    config.color = themeColors?.length ? themeColors : randomColors
  }

  if (formatColor.formatColorStyle === "gradient") {
    if (formatColor.minimum && formatColor.maximum) {
      const fieldName = getFieldName(formatColor?.formatColorField, report);
      config.colorField = fieldName
      const fieldData = getFieldData(data, fieldName);
      let color = generateColorRange(
        formatColor?.minimum,
        formatColor?.maximum,
        data.length
      );

      if (formatColor?.enableSteps) {
        color = generateColorsFromRange(fieldData, formatColor);
      }
      config.color = color
    }
  }

  if (formatColor?.formatColorStyle === "fieldValue") {
    config.colorField = getFieldName(formatColor?.formatColorField, report);
    config.color = (obj) => {
      if (formatColor?.showAll) {
        return getFiledValueColor(obj, formatColor) || getHTMLColorFormat(formatColor.defaultColor);
      }
      return getHTMLColorFormat(formatColor.defaultColor);
    };
  }

  if (sizeField) {
    config.weightField = sizeField;
  }

  const onBarClick = (args, plot) => {
    // console.log(args)
    const { x, y } = args;
    const tooltipData = plot.chart.getTooltipItems({ x, y });
    if (tooltipData[0]?.data) {
      const barData = tooltipData[0].data.datum;

      if (!interactiveMode) {
        return null;
      }
      if (!drillDown && !drillThrough) {
        return null;
      }
      let payload = [];
      Object.keys(barData).map((key) => {
        if (key !== "name") payload.push({ field: key, value: barData[key] });
      });
      let { top, left } = args.view.ele.getBoundingClientRect();
      top = args.data.y;
      left = args.data.x;
      let menuData = {
        payload,
        position: { top, left },
        drillDownFilterValues: data,
      };
      dispatch(setMenuData({ reportId, menu: menuData }));
    }
  };

  return (
    <div style={{ height: `${height}px` }}>
      <div className="title-subtitle-container">
        {title?.position === "top" && getPropertyElement(titleStyle, title)}
        {subTitle?.position === "top" && getPropertyElement(subTitleStyle, subTitle)}
      </div>
      <WordCloud
        {...config}
        onReady={(plot) => {
          plot.on("click", (args) => {
            onBarClick(args, plot);
          });
        }}
      />
      <Row justify="end">
        <Toolbar
          addForwardFilterIPC={addForwardFilterIPC}
          deleteBackwardFilterIPC={deleteBackwardFilterIPC}
          refreshFiltersIPC={refreshFiltersIPC}
          reportId={reportId}
          getApi={props.getApi}
        />
      </Row>
      {interactiveMode && (
        <CellCard
          reportId={reportId}
          addFilter={props.addFilter}
          getApi={props.getApi}
          report={report}
          format={format}
        />
      )}
      <div className="title-subtitle-container">
        {title?.position === "bottom" && getPropertyElement(titleStyle, title)}
        {subTitle?.position === "bottom" && getPropertyElement(subTitleStyle, subTitle)}
      </div>
    </div>
  );
};

export default WordCloudForSingleDimension