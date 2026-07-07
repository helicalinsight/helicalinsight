import { Menu, Dropdown, Button, Tooltip } from 'antd';
import {
    UserOutlined, BarChartOutlined, LineChartOutlined, AreaChartOutlined, DotChartOutlined, PieChartOutlined,
    HeatMapOutlined, DownOutlined, FileTextOutlined,
    RadarChartOutlined,
    LayoutOutlined,
    Loading3QuartersOutlined,
    SlidersOutlined,
    CalendarOutlined,
    TableOutlined,
    CreditCardOutlined
} from "@ant-design/icons";


import ChartIcon from '../../../../common/icons/chart-icons';
import { toTitleCase } from '../../../../../utils/text-utils';


export const subVizList = {
    SyncChart: [
        { name: 'Column', icon: <BarChartOutlined /> },
        { name: 'Line', icon: <LineChartOutlined /> },
        { name: 'Spline', icon: <ChartIcon name="spline-chart" /> },
        { name: 'Area', icon: <AreaChartOutlined /> },
        { name: 'SplineArea', icon: <ChartIcon name="spline-area-chart" /> },
        { name: 'StepLine', icon: <ChartIcon name="step-line-chart" /> },
        { name: 'StepArea', icon: <ChartIcon name="step-area-chart" /> },
        { name: 'StackingColumn', icon: <ChartIcon name="stacking-column-chart" /> },
        { name: 'StackingArea', icon: <ChartIcon name="stacking-area-chart" /> },
        { name: 'StackingColumn100', icon: <ChartIcon name="stacking-column100-chart" /> },
        { name: 'StackingArea100', icon: <ChartIcon name="stacking-area100-chart" /> },
        { name: 'Scatter', icon: <ChartIcon name="scatter-chart" /> },
        { name: 'Bubble', icon: <DotChartOutlined /> },
        { name: 'Pareto', icon: <ChartIcon name="pareto-chart" /> },
    ],
    GridChart: [
        {
            name: "bar", icon: <BarChartOutlined />,
            requires: "at least 1 dimension and 1 measure is required"
        },
        {
            name: "line", icon: <LineChartOutlined />,
            requires: "at least 1 dimension and 1 measure is required"
        },
        {
            name: "area", icon: <AreaChartOutlined />,
            requires: "at least 1 dimension and 1 measure is required"
        },
        {
            name: "point", icon: <DotChartOutlined />,
            requires: "at least 1 dimension and 1 measure is required"
        },
        {
            name: "tick", icon: <ChartIcon name="tick-chart" />,
            requires: "at least 1 dimension and 1 measure is required"
        },
        {
            name: "arc", icon: <PieChartOutlined />,
            requires: "at least 1 measure is required"
        },
        {
            name: "doughnut", icon: <ChartIcon name="doughnut-chart" />,
            requires: "at least 1 measure is required"
        },
        {
            name: "heatmap", icon: <HeatMapOutlined />,
            requires: "at least 2 dimension is required"
        },
        {
            name: "text", icon: <FileTextOutlined />,
            requires: "at least 1 dimension is required"
        },
    ],
    Antcharts: [
        { name: "bar", icon: <BarChartOutlined /> },
        { name: "line", icon: <LineChartOutlined /> },
        { name: "text", icon: <FileTextOutlined /> },
        { name: "arc", icon: <PieChartOutlined /> },
        { name: "area", icon: <AreaChartOutlined /> },
        { name: "doughnut", icon: <ChartIcon name="doughnut-chart" /> },
        { name: "point", icon: <DotChartOutlined /> },
        // { name: "treemap", icon: <LayoutOutlined /> },
        { name: "waterfall", icon: <SlidersOutlined /> },
        { name: "radar", icon: <RadarChartOutlined /> },
        { name: "progress", icon: <Loading3QuartersOutlined /> },
        { name: "relation", icon: <ChartIcon name="relation-chart" /> },
        // { name: "rose", icon: <LineChartOutlined /> },
        // { name: "radial bar", icon: <LineChartOutlined /> },
        { name: "calendar", icon: <CalendarOutlined /> },
    ],
    Ant_Card: [
        { name: "arc", icon: <PieChartOutlined /> },
        { name: "doughnut", icon: <ChartIcon name="doughnut-chart" /> },
        { name: "bar", icon: <BarChartOutlined /> },
        { name: "line", icon: <LineChartOutlined /> },
        { name: "area", icon: <AreaChartOutlined /> },
        { name: "progress", icon: <Loading3QuartersOutlined /> },
        { name: "table", icon: <TableOutlined /> }
    ],
    Card: [
        // { name: "kpi", title: 'KPI', icon: <CreditCardOutlined /> },
        { name: "bar", icon: <BarChartOutlined /> },
        { name: "line", icon: <LineChartOutlined /> },
        { name: "area", icon: <AreaChartOutlined /> },
        { name: "table", icon: <TableOutlined /> }
    ],
    MapChart: [
        // { name: "bar", icon: <BarChartOutlined /> },d
        { name: "line", icon: <LineChartOutlined /> },
        { name: "point", icon: <DotChartOutlined /> },
        { name: "heatmap", icon: <HeatMapOutlined /> },
        // { name: "map", icon: <ChartIcon name="map-chart" /> },
    ],
}

const SubVizList = props => {
    let { selectedType, subVizType, mapType } = {} = props
    const hanldeClick = item => {
        props.selectItem(item.name)
    }
    selectedType = selectedType || "GridChart"

    let sortedList = subVizList[selectedType].sort((a, b) => a.name > b.name ? 1 : -1)
    if (!subVizType) {
        subVizType = ["GridChart", "Antcharts", "Ant_Card", "Card"].includes(selectedType) ? "bar" : ['MapChart'].includes(selectedType) ? 'point' : "Column"
    }

    if (selectedType === "MapChart" && mapType === "osm") {
        sortedList = sortedList.filter(item => item.name !== "heatmap")
    }

    const menu = (
        <Menu className="sub-viz-list" selectedKeys={[subVizType]} >
            {sortedList.map(item => {
                return (
                    <Menu.Item key={item.name} icon={item.icon ? item.icon : <UserOutlined />}
                        onClick={() => hanldeClick(item)} >
                        <Tooltip title={item.requires || ""}>
                            <span data-testid={`sub-viz-type-${item.name}`} >{toTitleCase(item.name)}</span>
                        </Tooltip>
                    </Menu.Item>
                )
            })}
        </Menu>
    )

    return (
        <Dropdown
            className='sub-viz-selected'
            icon={<DownOutlined />}
            overlay={menu} trigger={["click"]}
        >
            <Button data-testid="selected-sub-viz-type" >
                {toTitleCase(subVizType)}
            </Button>
        </Dropdown>

    )
}


export default SubVizList