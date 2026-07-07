import { TinyArea, TinyColumn, TinyLine } from "@ant-design/plots";
import { Card, Row, Statistic, Table, Tooltip, Typography } from "antd";
import React, { useEffect, useRef } from "react";
import { useDispatch, useSelector } from "react-redux";
import { setMenuData } from "../../../../../redux/actions/hreport.actions";
import { getFieldDisplayName } from "../../../../../utils/utilities";
import notify from "../../../../hi-notifications/notify";
import { getCardValue } from "../../../hi-editing-area/components/properties/property-card-utils";
import { getFieldName, getHTMLColorFormat } from "../../../hi-editing-area/utils/property-utils";
import { getPropertyFieldInfo, getPropertyText } from "../../utils/utillities";
import "../ant-chart.scss";
import { calculateTrendPercentage, getAntCardRowContainerStyles, getCardTableData, getMonthNameSortedData } from "../ant-utils";

const HiCard = (props) => {
  let {
    markLayers,
    data,
    report,
    formatColor,
    subVizType,
    reportId,
    detailField,
    interactiveMode,
    chartAreaHeight,
    chartAreaWidth,
    card,
    isPrintMode,
    fields,
  } = props;
  const { canvas: canvasProperties = {}, tooltip: { showTooltip = true } = {} } = report?.reportData?.properties || {};

  const dispatch = useDispatch();
  let actualDetailField = fields.find((f) => getFieldDisplayName(f) === detailField)
  data = getMonthNameSortedData(data, actualDetailField)
  let showMoreCardIcons = useSelector((state) => state.propertyPane.showMore);
  const {
    prefixType,
    suffixType,
    prefix,
    suffix,
    prefixColor,
    suffixColor,
    trendFontSize = 20,
    isTrend = false,
    displayTrend = [],
    showTitle = true,
    trendPagination = 2,
    trendPrefix,
    trendPrefixPosition,
    cardView,
    cardHeight,
    cardWidth
  } = card || {}
  const isCardTable = subVizType === 'table';
  const isKPI = subVizType === 'kpi';
  let trendTitle = null;
  const Notify = notify(dispatch);
  const { mode = '' } = report || {}
  if (['open', 'dashboard'].includes(mode) || prefix || suffix) showMoreCardIcons = true
  let cardPrefix = getCardValue(prefixType, prefix, prefixColor, showMoreCardIcons)
  let cardSuffix = getCardValue(suffixType, suffix, suffixColor, showMoreCardIcons)

  let color = "#000";
  let measure = markLayers?.length ? markLayers[0] : "";
  let value = data?.reduce((a, item) => item[measure] + a, 0);
  const chartRef = useRef(null);
  const heightConstant = 84;

  const { align = "" } = canvasProperties || {}
  let rowContainerStyles = getAntCardRowContainerStyles(align)

  if (isCardTable && isTrend) {
    value = data[0]?.[measure] ?? 0;
    if (detailField) {
      trendTitle = data[0]?.[detailField];
    }
  }
  let config = {
    color: "rgba(76, 132, 238, 1)",
    height: 30,
    data: data?.map((rec) => rec[measure]),
    smooth: true,
    tooltip: !showTooltip ? false : {
      customContent: function (x, data) {
        let tooltipContent = "";
        if (data.length > 0) {
          tooltipContent = data[0].value;
        }
        if (tooltipContent) {
          const { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(report, measure);
          let formattedText = getPropertyText({
            text: tooltipContent,
            applyOn: "tooltip",
            isApplyClicked,
            fieldType,
            formatField,
          });
          return `${formattedText}`;
        }
      },
    },
    chartRef: chartRef
  };

  if (formatColor?.defaultColor && formatColor?.formatColorField) {
    const fieldName = getFieldName(formatColor?.formatColorField, report);
    if (measure === fieldName) {
      color = getHTMLColorFormat(formatColor.defaultColor);
    }
    if (detailField === fieldName) {
      let tinyColor = getHTMLColorFormat(formatColor.defaultColor);
      if (tinyColor) {
        config = { ...config, color: tinyColor };
      }
    }
  }

  const tinyVizConfig = {
    bar: TinyColumn,
    line: TinyLine,
    area: TinyArea,
    table: null,
    kpi: null
  };
  const TinyVizComp = tinyVizConfig[subVizType];

  const onStatsClick = (args) => {
    if (data?.length === 1 && !detailField) {
      let { top, left } = args.currentTarget.getBoundingClientRect();
      let menuData = {
        payload: [{ field: Object.keys(data[0])[0], value: Object.values(data[0])[0] }],
        position: { top, left },
        drillDownFilterValues: data,
      };
      dispatch(setMenuData({ reportId, menu: menuData }));
    }
  };

  const onTinyVizClick = (args, plot) => {
    const { x, y } = args;
    const tooltipData = plot.chart.getTooltipItems({ x, y });
    if (tooltipData[0]?.data) {
      const value = tooltipData[0].data.y;

      if (!interactiveMode) {
        return null;
      }
      // if (!drillDown && !drillThrough) {
      //   return null;
      // }
      let payload = [];
      payload.push(
        { field: measure, value },
        { field: detailField, value: data[tooltipData[0].data.x][detailField] }
      );
      let { top, left } = args.view.ele.getBoundingClientRect();
      let menuData = {
        payload,
        position: { top, left },
        drillDownFilterValues: data,
      };
      dispatch(setMenuData({ reportId, menu: menuData }));
    }
  };

  const getFontSize = (type) => {
    if (chartAreaHeight <= 100 || chartAreaWidth < 120) {
      if (type === "title") {
        return "8px"
      }
      if (type === "value") {
        return "10px"
      }
    }
    if (type === "title") {
      return "14px"
    }
    if (type === "value") {
      if (isCardTable) {
        return trendFontSize + 'px';
      }
      return "20px"
    }
  }

  if (formatColor?.formatColorStyle === "fieldValue" && formatColor?.showAll && detailField) {
    Notify.error({
      type: "Frontend",
      message: "You cannot use this property with card.",
    });
  }
  if (formatColor?.formatColorStyle === "gradient" && detailField) {
    Notify.error({
      type: "Frontend",
      message: "You cannot use this property with card.",
    });
  }

  const { columns = [], cardTableData = [] } = isCardTable ? getCardTableData({ data, measure, detailField, target: value, isTrend, displayTrend, report, trendPrefix, trendPrefixPosition }) : {};

  const pagination = {
    pageSize: trendPagination,
    total: cardTableData?.length,
    showSizeChanger: false,
    showLessItems: true,
    showQuickJumper: false,
    itemRender: (_, type, originalElement) => {
      if (['page', 'jump-next', 'jump-prev'].includes(type)) return null;
      return originalElement;
    }
  };

  let scrollHeight = chartAreaHeight - heightConstant;
  let cardTablePagination = (cardTableData?.length <= trendPagination && trendPagination !== -1) ? false : (isPrintMode || trendPagination === -1) ? { pageSize: 10000000, hideOnSinglePage: true } : pagination;
  const { height: canvasHeight, width: canvasWidth } = canvasProperties || {}

  if (cardView) {
    switch (cardView) {
      case "fitWidth":
        config.width = canvasWidth ? canvasWidth : chartAreaWidth;
        config.height = cardHeight - heightConstant;
        break;
      case "fitHeight":
        config.width = cardWidth;
        config.height = (canvasHeight ? canvasHeight : chartAreaHeight) - heightConstant;
        break;
      case "standard":
        config.width = cardWidth;
        config.height = cardHeight - heightConstant;
        break;
      case "entireView":
        config.width = canvasWidth ? canvasWidth : chartAreaWidth;
        config.height = (canvasHeight ? canvasHeight : chartAreaHeight) - heightConstant;
        break;
      default:
        break;
    }
  }

  let trendVizConfig = {
    ...config,
    data: data.map((item, i, _arr) => calculateTrendPercentage(item[measure], _arr[0][measure])),
    tooltip: !showTooltip ? false : {
      customContent: function (x, data) {
        let tooltipContent = "";
        if (data.length > 0) {
          tooltipContent = data[0].value;
        }
        if (tooltipContent) {
          const { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(report, "Trend");
          let formattedText = getPropertyText({
            text: tooltipContent,
            applyOn: "tooltip",
            isApplyClicked,
            fieldType,
            formatField,
          });
          return `${formattedText}`;
        }
      },
    },
  }



  useEffect(() => {
    if (chartRef?.current) {
      let chart = chartRef.current;
      if (chart) {
        chart?.changeSize(config.width, config.height);
      }
    }
  }, [config.width, config.height]);


  return (
    <Row style={rowContainerStyles}>
      {/* <Col> */}
      <Card
        data-testid="hi-report-ant-card"
        onClick={onStatsClick}
        style={{ border: "none", textAlign: "center" }}
        className="hi-viz-card card-container"
      >
        <Statistic
          title={
            showTitle ? <Tooltip title={!showTooltip ? null : card?.title ? card.title : measure}>
              <Typography.Text ellipsis={true} style={{ fontSize: getFontSize("title") }}>{card?.title ? card.title : measure}</Typography.Text>
            </Tooltip> : null
          }
          prefix={cardPrefix && cardPrefix}
          suffix={cardSuffix && cardSuffix}
          value={value}
          valueStyle={{
            color,
            fontSize: getFontSize("value"),
            // fontWeight: "bold",
          }}
          formatter={(val) => {
            const { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(
              report,
              measure
            );
            let formattedText = getPropertyText({
              text: val,
              applyOn: "pane",
              isApplyClicked,
              fieldType,
              formatField,
            });
            let formattedTextonTooltip = getPropertyText({
              text: val,
              applyOn: "tooltip",
              isApplyClicked,
              fieldType,
              formatField,
            });
            return (
              <Tooltip placement="top" title={showTooltip ? formattedTextonTooltip : null} color="#000">
                <span style={{ color }}>{formattedText}</span>
              </Tooltip>
            );
          }}
        />
        {(!isCardTable && detailField && !isTrend && !isKPI) || (isTrend && displayTrend.includes('value') && !isCardTable && !isKPI) ? (
          <TinyVizComp
            {...config}
            onReady={(plot) => {
              plot.on("click", (args) => {
                onTinyVizClick(args, plot);
              });
            }}
          />
        ) : null}

        {!isKPI && isTrend && !isCardTable && displayTrend.includes('trend') ?
          <>
            <TinyVizComp
              {...trendVizConfig}
              onReady={(plot) => {
                plot.on("click", (args) => {
                  onTinyVizClick(args, plot);
                });
              }}
            /></>
          : null}

        {isCardTable && detailField && (
          <React.Fragment>
            {trendTitle && <span className="hi-card-trend-title" data-testid="hi-card-trend-title">{trendTitle}</span>}
            <div className="hr-trend-container" data-testid="hr-trend-container">
              <Table
                columns={columns}
                dataSource={cardTableData}
                size="small"
                showHeader={false}
                pagination={cardTablePagination}
                scroll={{ y: isPrintMode ? 100000 : scrollHeight, x: 'auto' }}
              />
            </div>
          </React.Fragment>
        )}
      </Card>
      {/* </Col> */}
    </Row>
  );
};

export default HiCard