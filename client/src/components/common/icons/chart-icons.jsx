import Icon from "@ant-design/icons";
import {
  PivotTableSvg, DoughnutChartSvg, SplineChartSvg, SplineAreaChartSvg, StepLineChartSvg, StackingColumnSvg,
  StackingAreaSvg, StackingColumn100Svg, StackingArea100Svg, ScatterChartSvg, ParetoChartSvg, StepAreaChartSvg, MapChartSvg, VFChartSvg, S2TableSvg, RelationChartSvg,
  PivotTableSvgNew,
  VFChartSvgNew,
  WordCloudSvg,
  HorizontalBarChartSvg,
  ColumnChartSvg,
  HistogramChartSvg,
  GroupedColumnSvg,
  TinyColumnSvg,
  DualLineSvg,
  ColumnLineSvg,
  TinyLineSvg,
  TinyAreaSvg,
  RoseChartSvg,
  SunburstSvg,
  TreemapSvg,
  CirclePackingSvg,
  BubbleChartSvg,
  FunnelChartSvg,
  GaugeChartSvg,
  GroupedColumnLineSvg,
  StackedColumnLineSvg,
  StackedAndGroupedColumnLineSvg,
  GridTableChartSvg,
} from "./chart-icons-svg"

const style = { marginRight: "5px" }

const PivotTableIcon = () => <Icon component={PivotTableSvg} />;
const PivotTableIconNew = () => <Icon component={PivotTableSvgNew} />;
const DoughnutChartIcon = () => <Icon component={DoughnutChartSvg} style={style} />;
const SplineChartIcon = () => <Icon component={SplineChartSvg} style={style} />;
const SplineAreaChartIcon = () => <Icon component={SplineAreaChartSvg} style={style} />;
const StepLineChartIcon = () => <Icon component={StepLineChartSvg} style={style} />;
const StepAreaChartIcon = () => <Icon component={StepAreaChartSvg} style={style} />;
const StackingColumnIcon = () => <Icon component={StackingColumnSvg} style={style} />;
const StackingAreaIcon = () => <Icon component={StackingAreaSvg} style={style} />;
const StackingColumn100Icon = () => <Icon component={StackingColumn100Svg} style={style} />;
const StackingArea100Icon = () => <Icon component={StackingArea100Svg} style={style} />;
const ScatterChartIcon = () => <Icon component={ScatterChartSvg} style={style} />;
const ParetoChartIcon = () => <Icon component={ParetoChartSvg} style={style} />;
const TickChartIcon = () => <Icon component={ParetoChartSvg} style={style} />;
const MapChartIcon = () => <Icon component={MapChartSvg} style={style} />;
const VFChartIcon = () => <Icon component={VFChartSvg} style={style} />;
const VFChartIconNew = () => <Icon component={VFChartSvgNew} style={style} />;
const S2TableIcon = () => <Icon component={S2TableSvg} />;
const RelationChartIcon = () => <Icon component={RelationChartSvg} />;
const WordCloudIcon = () => <Icon component={WordCloudSvg} />;
const HorizontalBarChartIcon = () => <Icon component={HorizontalBarChartSvg} style={style} />;
const ColumnChartIcon = () => <Icon component={ColumnChartSvg} style={style} />;
const HistogramChartIcon = () => <Icon component={HistogramChartSvg} style={style} />;
const GroupedColumnIcon = () => <Icon component={GroupedColumnSvg} style={style} />;
const TinyColumnIcon = () => <Icon component={TinyColumnSvg} style={style} />;
const DualLineIcon = () => <Icon component={DualLineSvg} style={style} />;
const ColumnLineIcon = () => <Icon component={ColumnLineSvg} style={style} />;
const TinyLineIcon = () => <Icon component={TinyLineSvg} style={style} />;
const TinyAreaIcon = () => <Icon component={TinyAreaSvg} style={style} />;
const RoseChartIcon = () => <Icon component={RoseChartSvg} style={style} />;
const SunburstIcon = () => <Icon component={SunburstSvg} style={style} />;
const TreemapIcon = () => <Icon component={TreemapSvg} style={style} />;
const CirclePackingIcon = () => <Icon component={CirclePackingSvg} style={style} />;
const BubbleChartIcon = () => <Icon component={BubbleChartSvg} style={style} />;
const FunnelChartIcon = () => <Icon component={FunnelChartSvg} style={style} />;
const GaugeChartIcon = () => <Icon component={GaugeChartSvg} style={style} />;
const GroupedColumnLineIcon = () => <Icon component={GroupedColumnLineSvg} style={style} />;
const StackedColumnLineIcon = () => <Icon component={StackedColumnLineSvg} style={style} />;
const StackedAndGroupedColumnLineIcon = () => <Icon component={StackedAndGroupedColumnLineSvg} style={style} />;
const GridTableChartIcon = () => <Icon component={GridTableChartSvg} style={style} />;


const ChartIcon = (props) => {
  const { name } = props;
  switch (name) {
    case "pivot-table":
      return <PivotTableIcon />;
    case "pivot-table-new":
      return <PivotTableIconNew />;
    case "doughnut-chart":
      return <DoughnutChartIcon />;
    case "spline-chart":
      return <SplineChartIcon />;
    case "spline-area-chart":
      return <SplineAreaChartIcon />;
    case "step-line-chart":
      return <StepLineChartIcon />;
    case "step-area-chart":
      return <StepAreaChartIcon />;
    case "stacking-column-chart":
      return <StackingColumnIcon />;
    case "stacking-area-chart":
      return <StackingAreaIcon />;
    case "stacking-column100-chart":
      return <StackingColumn100Icon />;
    case "stacking-area100-chart":
      return <StackingArea100Icon />;
    case "scatter-chart":
      return <ScatterChartIcon />;
    case "pareto-chart":
      return <ParetoChartIcon />;
    case "tick-chart":
      return <TickChartIcon />;
    case "map-chart":
      return <MapChartIcon />;
    case "vf":
      return <VFChartIcon />;
    case "vf-new":
      return <VFChartIconNew />;
    case "s2-table":
      return <PivotTableIcon />;
    case "s2-table-new":
      return <PivotTableIconNew />;
    case 'relation-chart':
      return <RelationChartIcon />;
    case "word-cloud":
      return <WordCloudIcon />;
    case "horizontal-bar-chart":
      return <HorizontalBarChartIcon />;
    case "column-chart":
      return <ColumnChartIcon />;
    case "histogram-chart":
      return <HistogramChartIcon />;
    case "grouped-column-chart":
      return <GroupedColumnIcon />;
    case "tiny-column-chart":
      return <TinyColumnIcon />;
    case "dual-line-chart":
      return <DualLineIcon />;
    case "column-line-chart":
      return <ColumnLineIcon />;
    case "tiny-line-chart":
      return <TinyLineIcon />;
    case "tiny-area-chart":
      return <TinyAreaIcon />;
    case "rose-chart":
      return <RoseChartIcon />;
    case "sunburst-chart":
      return <SunburstIcon />;
    case "treemap-chart":
      return <TreemapIcon />;
    case "circle-packing-chart":
      return <CirclePackingIcon />;
    case "bubble-chart":
      return <BubbleChartIcon />;
    case "funnel-chart":
      return <FunnelChartIcon />;
    case "gauge-chart":
      return <GaugeChartIcon />;
    case "grouped-column-line-chart":
      return <GroupedColumnLineIcon />;
    case "stacked-column-line-chart":
      return <StackedColumnLineIcon />;
    case "stacked-and-grouped-column-line-chart":
      return <StackedAndGroupedColumnLineIcon />;
    case "grid-table-chart":
      return <GridTableChartIcon />;
    default:
      return <ParetoChartIcon />;

  }
}

export default ChartIcon