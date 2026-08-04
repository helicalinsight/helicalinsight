import {
  AreaChartOutlined,
  AppstoreOutlined,
  BarChartOutlined,
  CalendarOutlined,
  CodeOutlined,
  CreditCardOutlined,
  DotChartOutlined,
  FunnelPlotOutlined,
  HeatMapOutlined,
  LayoutOutlined,
  LineChartOutlined,
  Loading3QuartersOutlined,
  PieChartOutlined,
  RadarChartOutlined,
  SettingOutlined,
  SlidersOutlined,
  TableOutlined,
} from "@ant-design/icons";
import { Button, Drawer, Typography } from "antd";
import { useEffect, useMemo, useRef, useState } from "react";
import { useSelector } from "react-redux";
import ChartIcon from "../../common/icons/chart-icons";
import InstantBITooltip from "../instant-bi-tooltip-title";
import {
  IbVfEditorBody,
  IbVfEditorFooter,
  IbVfEditorTitle,
} from "./ib-vf-editor";
import "./ib-chart-preferences.scss";

const { Text } = Typography;
const OPTIONS_WIDTH = 280;
const VF_WIDTH = "45%";

const stripVf = (name = "") =>
  String(name)
    .trim()
    .replace(/^vf\./i, "");

const chartKey = (name = "") =>
  stripVf(name)
    .toLowerCase()
    .replace(/[\s-]+/g, "_");

const chartLabel = (name = "") =>
  stripVf(name)
    .replace(/_/g, " ")
    .replace(/\b\w/g, (c) => c.toUpperCase());

const CHART_ICONS = {
  bar: <BarChartOutlined />,
  column: <ChartIcon name="column-chart" />,
  line: <LineChartOutlined />,
  area: <AreaChartOutlined />,
  point: <DotChartOutlined />,
  pie: <PieChartOutlined />,
  arc: <PieChartOutlined />,
  donut: <ChartIcon name="doughnut-chart" />,
  doughnut: <ChartIcon name="doughnut-chart" />,
  heatmap: <HeatMapOutlined />,
  waterfall: <SlidersOutlined />,
  radar: <RadarChartOutlined />,
  progress: <Loading3QuartersOutlined />,
  calendar: <CalendarOutlined />,
  wordcloud: <ChartIcon name="word-cloud" />,
  relation: (
    <span className="ib-chart-preferences__custom-icon" data-icon="relation">
      <ChartIcon name="relation-chart" />
    </span>
  ),
  table: <TableOutlined />,
  grid_table: <ChartIcon name="s2-table-new" />,
  pivot_table: <ChartIcon name="pivot-table-new" />,
  kpi: <CreditCardOutlined />,
  other: <AppstoreOutlined />,
  scatter: <ChartIcon name="scatter-chart" />,
  bubble_chart: <ChartIcon name="bubble-chart" />,
  stacked_column: <ChartIcon name="stacking-column-chart" />,
  tiny_column: <ChartIcon name="tiny-column-chart" />,
  histogram: <ChartIcon name="histogram-chart" />,
  grouped_column: <ChartIcon name="grouped-column-chart" />,
  grouped_column_line: <ChartIcon name="grouped-column-line-chart" />,
  stacked_column_line: <ChartIcon name="stacked-column-line-chart" />,
  stacked_and_grouped_column_line: (
    <ChartIcon name="stacked-and-grouped-column-line-chart" />
  ),
  tiny_line: <ChartIcon name="tiny-line-chart" />,
  dual_line: <ChartIcon name="dual-line-chart" />,
  column_line: <ChartIcon name="column-line-chart" />,
  tiny_area: <ChartIcon name="tiny-area-chart" />,
  rose_chart: <ChartIcon name="rose-chart" />,
  sunburst: <ChartIcon name="sunburst-chart" />,
  treemap: <LayoutOutlined />,
  circle_packing: <ChartIcon name="circle-packing-chart" />,
  funnel_chart: <FunnelPlotOutlined />,
  gauge: <ChartIcon name="gauge-chart" />,
};

const getChartIcon = (type = "") => {
  const key = chartKey(type);
  return CHART_ICONS[key] || <BarChartOutlined />;
};

const parseSimilarItem = (item) => {
  if (typeof item === "string" && item.trim()) {
    const raw = item.trim();
    const name = stripVf(raw);
    return {
      id: /^vf\./i.test(raw) ? raw.toLowerCase() : `vf.${chartKey(name)}`,
      name,
    };
  }
  if (item && typeof item === "object" && !Array.isArray(item)) {
    const [key, value] = Object.entries(item)[0] || [];
    if (!key && !value) return null;
    const name = stripVf(value || key);
    return {
      id: String(key || `vf.${chartKey(name)}`)
        .trim()
        .toLowerCase(),
      name,
    };
  }
  return null;
};

const parseSimilarCharts = (list = []) => {
  if (!Array.isArray(list)) return [];
  const seen = new Set();
  return list.reduce((acc, item) => {
    const parsed = parseSimilarItem(item);
    if (!parsed?.name) return acc;
    const key = chartKey(parsed.name);
    if (seen.has(key)) return acc;
    seen.add(key);
    acc.push(parsed);
    return acc;
  }, []);
};

const ChartIconButton = ({
  name,
  active = false,
  disabled = false,
  onClick,
  testId,
}) => {
  const label = chartLabel(name);
  return (
    <InstantBITooltip title={label}>
      <button
        type="button"
        className={`ib-chart-preferences__icon-item${
          active ? " is-active" : ""
        }${disabled ? " is-disabled" : ""}`}
        aria-current={active ? "true" : undefined}
        aria-disabled={disabled ? "true" : undefined}
        onClick={() => !active && !disabled && onClick?.(name)}
        data-testid={testId}
      >
        <span className="ib-chart-preferences__icon">{getChartIcon(name)}</span>
        <span className="ib-chart-preferences__icon-title">{label}</span>
      </button>
    </InstantBITooltip>
  );
};

const IbChartPreferences = ({
  chartName = "",
  similarChart = [],
  editable = true,
  converting = false,
  vfCode = "",
  openVfEditor = false,
  onSelectSimilarChart,
  onApplyVf,
  onVfEditorOpened,
}) => {
  const [open, setOpen] = useState(false);
  const [showMore, setShowMore] = useState(false);
  const [vfExpanded, setVfExpanded] = useState(false);
  const [draftVf, setDraftVf] = useState(vfCode || "");
  const [vfSlide, setVfSlide] = useState(1);
  const [vfHover, setVfHover] = useState(false);
  const carouselRef = useRef(null);
  const chartList = useSelector((s) => s.instantBI.chartList || []);

  const similarItems = useMemo(
    () => parseSimilarCharts(similarChart),
    [similarChart]
  );
  const currentKey = chartKey(chartName);

  const chartOptions = useMemo(
    () =>
      similarItems.map(({ name }) => ({
        name,
        active: chartKey(name) === currentKey,
      })),
    [similarItems, currentKey]
  );

  const moreCharts = useMemo(() => {
    const excluded = new Set(
      [currentKey, currentKey && `vf.${currentKey}`].filter(Boolean)
    );
    similarItems.forEach(({ id, name }) => {
      excluded.add(chartKey(name));
      excluded.add(String(id || "").toLowerCase());
      excluded.add(`vf.${chartKey(name)}`);
    });

    const seen = new Set();
    return (chartList || [])
      .map((item) => String(item || "").trim())
      .filter(Boolean)
      .filter((raw) => {
        const key = chartKey(raw);
        if (excluded.has(raw.toLowerCase()) || excluded.has(key) || seen.has(key)) {
          return false;
        }
        seen.add(key);
        return true;
      })
      .map(stripVf);
  }, [chartList, similarItems, currentKey]);

  useEffect(() => {
    if (vfExpanded) setDraftVf(vfCode || "");
    else setVfSlide(1);
  }, [vfExpanded, vfCode]);

  useEffect(() => {
    if (!openVfEditor) return;
    setOpen(true);
    setShowMore(false);
    setVfExpanded(true);
    setDraftVf(vfCode || "");
    setVfSlide(1);
    onVfEditorOpened?.();
  }, [openVfEditor]);

  const collapseVf = () => {
    setVfExpanded(false);
    setVfSlide(1);
  };

  const openDrawer = () => {
    setShowMore(false);
    setVfExpanded(false);
    setOpen(true);
  };

  const onClose = () => {
    if (vfExpanded) return collapseVf();
    setOpen(false);
    setShowMore(false);
  };

  const toggleVf = () =>
    setVfExpanded((prev) => {
      if (!prev) setDraftVf(vfCode || "");
      return !prev;
    });

  const applyVf = () => {
    if (!draftVf?.length) return;
    onApplyVf?.(draftVf);
    collapseVf();
  };

  const toggleVfInfo = () => {
    carouselRef.current?.[vfSlide === 1 ? "next" : "prev"]?.();
    setVfSlide((s) => (s === 1 ? 2 : 1));
  };

  if (!editable || !similarItems.length) return null;

  return (
    <div className="ib-chart-preferences" data-testid="ib-chart-preferences">
      <InstantBITooltip title="Convert Chart">
        <Button
          size="small"
          type="text"
          className="ib-chart-preferences__trigger"
          icon={<SettingOutlined />}
          onClick={openDrawer}
          data-testid="ib-chart-preferences-open"
        />
      </InstantBITooltip>

      <Drawer
        title={
          vfExpanded ? (
            <IbVfEditorTitle slide={vfSlide} onToggleInfo={toggleVfInfo} />
          ) : (
            "Chart options"
          )
        }
        placement="right"
        width={vfExpanded ? VF_WIDTH : OPTIONS_WIDTH}
        visible={open}
        onClose={onClose}
        maskClosable={false}
        keyboard={false}
        destroyOnClose={false}
        className={`ib-chart-preferences-drawer${
          vfExpanded
            ? " hi-custom-chart-editor-drawer ib-chart-preferences-drawer--vf"
            : ""
        }`}
        footer={
          vfExpanded && vfSlide === 1 ? (
            <IbVfEditorFooter onApply={applyVf} />
          ) : null
        }
      >
        {vfExpanded ? (
          <IbVfEditorBody
            code={draftVf}
            onChange={(v) => setDraftVf(v ?? "")}
            slide={vfSlide}
            carouselRef={carouselRef}
          />
        ) : (
          <div className="ib-chart-preferences__body">
            {!!chartOptions.length && (
              <div className="ib-chart-preferences__row">
                <div className="ib-chart-preferences__similar-header">
                  <Text type="secondary" className="ib-chart-preferences__label">
                    Similar charts
                  </Text>
                  <div
                    className={`ib-chart-preferences__vf-hotspot${
                      vfHover ? " is-hover" : ""
                    }`}
                    onMouseEnter={() => setVfHover(true)}
                    onMouseLeave={() => setVfHover(false)}
                  >
                    <InstantBITooltip
                      title="Update your selected chart."
                      open={vfHover ? undefined : false}
                      placement="left"
                    >
                      <button
                        type="button"
                        className="ib-chart-preferences__vf-fab"
                        onClick={toggleVf}
                        data-testid="ib-chart-preferences-vf-toggle"
                        aria-label="Update your selected chart"
                      >
                        <span className="ib-chart-preferences__icon">
                          <CodeOutlined />
                        </span>
                      </button>
                    </InstantBITooltip>
                  </div>
                </div>
                <div className="ib-chart-preferences__icon-grid">
                  {chartOptions.map(({ name, active }) => (
                    <ChartIconButton
                      key={chartKey(name)}
                      name={name}
                      active={active}
                      disabled={converting && !active}
                      testId={`ib-similar-chart-${name}`}
                      onClick={(type) =>
                        !converting && onSelectSimilarChart?.(type)
                      }
                    />
                  ))}
                </div>
              </div>
            )}
            {!!moreCharts.length && (
              <div className="ib-chart-preferences__row">
                <button
                  type="button"
                  className="ib-chart-preferences__show-more"
                  onClick={() => setShowMore((v) => !v)}
                  data-testid="ib-chart-preferences-show-more"
                >
                  {showMore ? "Show less" : "Show More"}
                </button>
                {showMore && (
                  <>
                    <Text
                      type="secondary"
                      className="ib-chart-preferences__label"
                    >
                      More charts
                    </Text>
                    <div className="ib-chart-preferences__icon-grid">
                      {moreCharts.map((name) => (
                        <ChartIconButton
                          key={name}
                          name={name}
                          disabled
                          testId={`ib-more-chart-${name}`}
                        />
                      ))}
                    </div>
                  </>
                )}
              </div>
            )}
          </div>
        )}
      </Drawer>
    </div>
  );
};

export default IbChartPreferences;
