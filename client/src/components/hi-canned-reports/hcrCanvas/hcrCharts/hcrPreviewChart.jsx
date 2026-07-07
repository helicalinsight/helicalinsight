import { Column, Pie, Bar } from '@ant-design/plots';
import { isEmpty } from 'lodash';

const HCRPreviewChart = (props) => {
    const { width, height, chartType, colorField, padding, orientation = "Vertical" } = props || {}
    const data = [
        { name: 'One', sales: 120 },
        { name: 'Two', sales: 95 },
        { name: 'Three', sales: 88 },
        { name: 'Four', sales: 76 },
        { name: 'Five', sales: 64 },
        { name: 'Six', sales: 58 },
    ]

    const Comp = {
        Vertical: Column,
        Horizontal: Bar
    }[orientation]
    const isArc = chartType === "arc", isVertical = orientation === "Vertical";;

    const config = {
        data,
        xField: 'name',
        yField: 'sales',
        width,
        height,
        tooltip: false,
        animation: false,
        legend: false
    };

    if (!isVertical) {
        config.xField = "sales"
        config.yField = "name"
    }

    if (!isEmpty(padding)) {
        config.padding = [padding.Top, padding.Right, padding.Bottom, padding.Left]
    }

    if (isArc) {
        delete config.xField
        delete config.yField
        config.radius = 0.7
        config.angleField = 'sales'
        config.label = { type: 'outer' }
        if (colorField) config.colorField = 'name'

        return (
            <Pie {...config} />
        )
    }

    if (colorField) {
        if (isVertical) {
            config.color = 'name'
        } else {
            config.seriesField = "name"
        }
    }

    return (
        <Comp {...config} />
    )
}

export default HCRPreviewChart