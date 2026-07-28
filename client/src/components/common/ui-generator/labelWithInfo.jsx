import { Tooltip } from "antd";
import { InfoCircleFilled } from "@ant-design/icons";

const INFO_ICON_STYLE = { marginLeft: "5px", fontSize: "12px" };

/**
 * Label text with optional (i) tooltip — same pattern as admin Settings forms.
 */
export const labelWithInfo = (label, description) => {
  if (!description) {
    return label;
  }
  return [
    label,
    <Tooltip key="info" title={description}>
      <InfoCircleFilled style={INFO_ICON_STYLE} />
    </Tooltip>,
  ];
};

export default labelWithInfo;
