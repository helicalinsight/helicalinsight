import { useSelector } from "react-redux";
import { hcrActions } from "../../../../redux/actions";

export default function CanvasPageSetup({ SelectField, dispatch, pageProperties, orientation, getLabel = () => { } }) {
    const activeTab = useSelector(state => state.cannedReports.present.hcrTabData.panes.find(pane => pane.key === state.cannedReports.present.hcrTabData.activeKey)) || {};
    const { canvasProperties } = activeTab;
    const { layout } = canvasProperties;
    const HcrPropertiesConfiguration = useSelector(state => state.cannedReports.present?.hCROldConfigurations?.HcrPropertiesConfiguration || {});
    const { tooltipInfo = {} } = HcrPropertiesConfiguration || {};
    const { tooltip_pageSize = {}, tooltip_pageSetupOreientation = {} } = tooltipInfo || {};


    return <div className="page-setup-properties">
        <SelectField
            label={getLabel({ label: 'Page Size', tooltip: tooltip_pageSize })}
            value={layout.name}
            options={Object.keys(pageProperties).map(pageName => {
                return {
                    label: pageName,
                    value: pageName
                }
            })}
            width={110}
            onChange={(value) => {
                dispatch(hcrActions.setHcrCanvasLayoutSize({ name: value, size: pageProperties[value].pixel }))
            }}
        />
        <SelectField
            label={<div className="property-label">{getLabel({ label: 'Orientation', tooltip: tooltip_pageSetupOreientation, placement: "left" })}</div>}
            value={layout.orientation}
            options={orientation.map(type => {
                return {
                    label: type,
                    value: type
                }
            })}
            width={110}
            onChange={(value) => {
                dispatch(hcrActions.hcrOrientation(value));
            }}
        />
    </div>
}