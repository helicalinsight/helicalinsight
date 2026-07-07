import { useSelector } from "react-redux";
import { hcrActions } from "../../../../redux/actions";
import { getMargin } from "../../hcrHelperMethods";

const directions = [
    { label: 'Top', key: 'top' },
    { label: 'Bottom', key: 'bottom' },
    { label: 'Left', key: 'left' },
    { label: 'Right', key: 'right' }
];

export default function CanvasMargin({ InputNumberFiled, dispatch, getLabel }) {
    const activeTab = useSelector(state => state.cannedReports.present.hcrTabData.panes.find(pane => pane.key === state.cannedReports.present.hcrTabData.activeKey)) || {};
    const { canvasProperties } = activeTab;
    const { margin } = canvasProperties;
    const HcrPropertiesConfiguration = useSelector(state => state.cannedReports.present?.hCROldConfigurations?.HcrPropertiesConfiguration || {});
    const { tooltipInfo = {} } = HcrPropertiesConfiguration || {};
    const {
        tooltip_marginBottom = {},
        tooltip_marginLeft = {},
        tooltip_marginRight = {},
        tooltip_marginTop = {}
    } = tooltipInfo || {};

    const marginTooltips = {
        top: tooltip_marginTop,
        right: tooltip_marginRight,
        bottom: tooltip_marginBottom,
        left: tooltip_marginLeft
    }

    const marginProps = {
        top: getMargin({ canvasMargin: margin || {}, key: 'top' }),
        left: getMargin({ canvasMargin: margin || {}, key: 'left' }),
        bottom: getMargin({ canvasMargin: margin || {}, key: 'bottom' }),
        right: getMargin({ canvasMargin: margin || {}, key: 'right' }),
    }

    return <div className="margin-properties">
        {
            directions.map((direction, i) => {
                return <>
                    <InputNumberFiled
                        label={<div className="property-label">{getLabel({ label: direction.label, tooltip: marginTooltips[direction.key], placement: i % 2 === 0 ? "topLeft" : "left" })} </div>}
                        value={marginProps[direction.key]}
                        width={110}
                        onChange={(value) => {
                            dispatch(hcrActions.setHcrCanvasMargin({ key: direction.key, value: (value <= 0) ? 0 : value }))
                        }}
                    />
                </>
            })
        }
    </div>
}