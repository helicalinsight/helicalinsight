import Muze, { Canvas, Layer } from "@chartshq/react-muze/components";
import { getPropertyFieldInfo, getPropertyText } from "../utils/utillities";
import {
  generateColorRange,
  getDataIndex,
  getEachFieldColor,
  getFieldData,
  getFieldName,
  getHTMLColorFormat,
} from "../../hi-editing-area/utils/property-utils";
import randomColor from "randomcolor";
import { getGridChartColorSchemeFromPalette } from "../utils/grid-chart-utils";

const GridTextChart = (props) => {
  const {
    canvasRef,
    canvasProps,
    labelField,
    colorField,
    tooltip,
    report,
    formatColor,
    data,
    themeColors
  } = props.data;
  const {
    color,
    gridLines,
    height,
    width,
    colorLegend,
    shapeLegend,
    sizeLegend,
    xAxis,
    yAxis,
  } = canvasProps;
  const { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(report, labelField);

  let rows = [canvasProps.rows, []] || [];
  let columns = [canvasProps.columns, []] || [];
  let domainData = []
  let uniqueData = []
  let colors = []

  if (colorField && colorField !== labelField) {
    domainData = getFieldData(data, colorField);
    uniqueData = [...new Set(domainData)] || []
    colors = themeColors?.length ? getGridChartColorSchemeFromPalette(themeColors, data) : randomColor({ count: uniqueData.length })
    color.range = colors
  }

  if (colorField && colorField === labelField) {
    domainData = getFieldData(data, labelField);
    uniqueData = [...new Set(domainData)] || []
    colors = themeColors?.length ? getGridChartColorSchemeFromPalette(themeColors, data) : generateColorRange(
      { r: 173, g: 209, b: 240, a: 1 },
      { r: 239, g: 168, b: 202, a: 1 },
      uniqueData.length
    );
    color.range = colors
  }

  if (formatColor?.formatColorStyle === "gradient") {
    if (formatColor.minimum && formatColor.maximum) {
      const fieldName = getFieldName(formatColor?.formatColorField, report);
      domainData = getFieldData(data, fieldName);
      uniqueData = [...new Set(domainData)];
      colors = generateColorRange(
        formatColor?.minimum,
        formatColor?.maximum,
        uniqueData.length
      );
    }
    color.range = colors
  }

  if (formatColor?.formatColorStyle === "fieldValue") {
    const fieldName = getFieldName(formatColor?.formatColorField, report);
    domainData = getFieldData(data, fieldName);
    uniqueData = [...new Set(domainData)];
    uniqueData.forEach((value) => {
      if (formatColor?.showAll) {
        let eachColor = getEachFieldColor(formatColor, value);
        colors.push(eachColor)
      } else {
        const defaultColor = getHTMLColorFormat(formatColor.defaultColor);
        colors.push(defaultColor)
      }
    })
    color.range = colors
  }

  const textCanvasProps = {
    rows,
    columns,
    xAxis,
    yAxis,
    showHeaders: true,
    facetRows: { verticalAlign: "middle" },
    gridLines,
    color,
    tooltips: [tooltip],
    height,
    width,
    colorLegend,
    shapeLegend,
    sizeLegend,
  };
  let textColor = "#000";

  return (
    <Canvas ref={canvasRef} {...textCanvasProps}>
      <Layer
        key="name"
        mark="bar"
        name="layer1"
        encoding={{
          color: {
            value: () => {
              return "#ffffff00"
            },
          },
        }}
      />
      <Layer
        key="text"
        mark="text"
        name="layer2"
        encoding={{
          text: {
            // Sets which field to show in text
            field: labelField,
            formatter: (text, i, j, k) => {
              let formattedText = getPropertyText({
                text,
                applyOn: "label",
                isApplyClicked,
                fieldType,
                formatField,
              });
              return formattedText;
            },
          },
          color: {
            value: (textInfo, i, _, k) => {
              let index = 0
              if (colorField) {
                if (colorField !== labelField) {
                  let dataMap = k["_normalizedData"][0]
                  const textData = dataMap.find((d) => d.text === textInfo.text)
                  if (textData) {
                    let values = Object.values(textData.dataObj)
                    index = uniqueData.findIndex((eachData) => values.includes(eachData))
                    textColor = colors[index]
                  }
                }
                if (colorField === labelField) {
                  index = getDataIndex(uniqueData, textInfo.text);
                  textColor = colors[index]
                }
              }
              if (formatColor?.formatColorStyle === "gradient") {
                if (formatColor.minimum && formatColor.maximum) {
                  const fieldName = getFieldName(formatColor?.formatColorField, report);
                  if (fieldName === labelField) {
                    index = getDataIndex(uniqueData, textInfo.text);
                    textColor = colors[index];
                  }
                  if (fieldName !== labelField) {
                    let dataMap = k["_normalizedData"][0]
                    const textData = dataMap.find((d) => d.text === textInfo.text)
                    if (textData) {
                      let values = Object.values(textData.dataObj)
                      index = uniqueData.findIndex((eachData) => values.includes(eachData))
                      textColor = colors[index]
                    }
                  }
                }
              }
              if (formatColor?.formatColorStyle === "fieldValue") {
                const fieldName = getFieldName(
                  formatColor?.formatColorField,
                  report
                );
                if (fieldName === labelField) {
                  if (formatColor?.showAll) {
                    textColor = getEachFieldColor(formatColor, textInfo.text);
                  } else {
                    textColor = getHTMLColorFormat(formatColor.defaultColor);
                  }
                } else {
                  if (formatColor?.showAll) {
                    let dataMap = k["_normalizedData"][0]
                    const textData = dataMap.find((d) => d.text === textInfo.text)
                    if (textData) {
                      let values = Object.values(textData.dataObj)
                      index = uniqueData.findIndex((eachData) => values.includes(eachData))
                      textColor = colors[index]
                    }
                  } else {
                    textColor = getHTMLColorFormat(formatColor.defaultColor);
                  }
                }
              }
              return textColor;
            },
          },
        }}
      />
    </Canvas>
  );
};

export default GridTextChart;
