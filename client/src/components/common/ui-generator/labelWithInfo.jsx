import { Tooltip } from "antd";
import { InfoCircleFilled } from "@ant-design/icons";
import "./ui-label-info.scss";

/**
 * Label text with optional (i) tooltip — light-blue circular info chip.
 */
export const labelWithInfo = (label, description) => {
  if (!description) {
    return label;
  }
  return [
    label,
    <Tooltip key="info" title={description}>
      <span className="ui-label-info" aria-label="More information">
        <InfoCircleFilled className="ui-label-info__icon" />
      </span>
    </Tooltip>,
  ];
};

export default labelWithInfo;
