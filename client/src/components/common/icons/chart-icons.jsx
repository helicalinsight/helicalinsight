import Icon from "@ant-design/icons";
import {
  PivotTableSvg, DoughnutChartSvg, SplineChartSvg, SplineAreaChartSvg, StepLineChartSvg, StackingColumnSvg,
  StackingAreaSvg, StackingColumn100Svg, StackingArea100Svg, ScatterChartSvg, ParetoChartSvg, StepAreaChartSvg, MapChartSvg, VFChartSvg, S2TableSvg, RelationChartSvg,
  PivotTableSvgNew,
  VFChartSvgNew,
  WordCloudSvg
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
    default:
      return <ParetoChartIcon />;

  }
}

export default ChartIcon