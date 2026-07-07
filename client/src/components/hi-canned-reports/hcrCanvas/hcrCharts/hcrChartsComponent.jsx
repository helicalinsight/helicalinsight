import { AreaChartOutlined } from '@ant-design/icons';
import HCRCharts from './hcrCharts';

const HCRChartsComponent = (props) => {
    const { isElementRender, data: { label = "Charts" } = {} } = props;
    return (
        isElementRender ? (
            <div className="element-wrapper">
                <AreaChartOutlined className="ele-icn" />
                <span>{label}</span>
            </div>
        ) : (
            <HCRCharts {...props} />
        )
    );
}

export default HCRChartsComponent