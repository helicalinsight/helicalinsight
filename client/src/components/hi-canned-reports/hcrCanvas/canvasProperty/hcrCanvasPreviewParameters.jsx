import { useSelector } from "react-redux";
import { hcrActions } from "../../../../redux/actions";
import { Checkbox } from "antd";

export default function CanvasPreviewParameters({
  SelectField,
  dispatch,
  parametersInPreview,
  getLabel = () => { }
}) {
  const activeTab =
    useSelector((state) =>
      state.cannedReports.present.hcrTabData.panes.find(
        (pane) => pane.key === state.cannedReports.present.hcrTabData.activeKey
      )
    ) || {};
  const HcrPropertiesConfiguration = useSelector(state => state.cannedReports.present?.hCROldConfigurations?.HcrPropertiesConfiguration || {});
  const { canvasProperties } = activeTab;
  const previewParameters = canvasProperties?.previewParameters;
  const { position } = parametersInPreview;

  const { tooltipInfo = {} } = HcrPropertiesConfiguration;
  const {
    tooltip_previewParametersPosition = {},
    tooltip_previewParametersShowParameters = {}
  } = tooltipInfo || {}


  const defaultPreviewParams = {
    showParameters: previewParameters.showParameters,
    position: previewParameters.position || "Right",
  };

  const onChange = (isChecked, key) => {
    dispatch(hcrActions.setHcrCanvasPreviewParms({ key, value: isChecked }));
  };

  return (
    <>
      <SelectField
        label={<div className="property-label">{getLabel({ label: "Position", tooltip: tooltip_previewParametersPosition })}</div>}
        value={defaultPreviewParams.position}
        options={position.map((placement) => {
          return {
            label: placement,
            value: placement,
          };
        })}
        width={240}
        onChange={(value) => {
          dispatch(
            hcrActions.setHcrCanvasPreviewParms({ key: "position", value })
          );
        }}
      />
      <Checkbox
        onChange={(e) => {
          onChange(e.target.checked, "showParameters");
        }}
        checked={defaultPreviewParams.showParameters}
      >
        <div className="property-label">{getLabel({ label: "Show parameters", tooltip: tooltip_previewParametersShowParameters })}</div>
      </Checkbox>
    </>
  );
}
