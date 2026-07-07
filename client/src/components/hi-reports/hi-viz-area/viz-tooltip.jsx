import { cloneDeep } from "lodash";
import { useMemo, useState } from "react";
import notify from "../../hi-notifications/notify";
import { getPropertyFieldInfo, getPropertyText, tooltipTemplateLiquidJS } from "./utils/utillities";
import "./viz-tooltip.scss";

const VizTooltip = (props) => {
  let { data, report, s2Chart, dispatch } = props;
  const { properties = {} } = report || {}
  const { tooltip: { tooltipTemplate = "", showTooltip = true, enableTemplate = false } = {} } = properties || {}
  const [show, setShow] = useState(false);
  const Notify = notify(dispatch);

  let itemsData = Object.keys(data).reduce((acc, key) => {
    if (key !== "key") {
      const { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(report, key);
      acc[key] = getPropertyText({
        text: data[key],
        applyOn: "tooltip",
        isApplyClicked,
        fieldType,
        formatField,
      })
    }
    return acc
  }, {})
  let displayData = cloneDeep(itemsData)

  const tooltipTemplateDisplay = useMemo(
    () => tooltipTemplateLiquidJS({ value: tooltipTemplate, scope: itemsData, Notify }),
    [show, tooltipTemplate]
  );

  if (!showTooltip) return null;

  const dataLength = Object.keys(displayData).length;
  let templateStyles = dataLength > 7 ? {
    height: !show ? 110 : "fit-content",
    overflowY: !show ? "hidden" : "auto"
  } : {}

  return (
    <div
      className="hr-viz-tooltip"
      style={s2Chart ? { background: "#616161", padding: "0", borderRadius: "2.5px", border: "5px solid #616161" } : {}}
    >
      {enableTemplate ?
        <div
          className="hreport-tooltip-template-container"
          style={templateStyles}
          dangerouslySetInnerHTML={{
            __html: tooltipTemplateDisplay,
          }}
        />
        :
        Object.keys(displayData).map((item, index) => {
          if (index > 5 && !show) return null;
          return (
            <div style={s2Chart ? { margin: "2px 5px" } : {}} key={item}>
              {item} :{" "} {displayData[item]}
            </div>
          );
        })
      }


      {/* {Object.keys(data).map((item, index) => {
        if (item === "key") return null;
        const { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(
          report,
          item
        );
        if (index > 5 && !show) return null;
        return (
          <div style={s2Chart ? { margin: "2px 5px" } : {}} key={item}>
            {item} :{" "}
            {getPropertyText({
              text: data[item],
              applyOn: "tooltip",
              isApplyClicked,
              fieldType,
              formatField,
            })}
          </div>
        );
      })} */}

      {Object.keys(data)?.length > 7 && (
        <ShowMore {...{ show }} onClick={() => setShow(!show)} />
      )}
    </div>
  );
};

export default VizTooltip;

export const ShowMore = ({ show, onClick = () => { } }) => {
  return (
    <div style={{ cursor: "pointer", fontWeight: "bold" }} onClick={onClick}>
      {!show ? "...More" : "Less"}
    </div>
  );
};
