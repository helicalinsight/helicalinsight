import { Tooltip } from "antd";
import { QuestionCircleOutlined } from "@ant-design/icons";
import "./ui-label-info.scss";

const tooltipTitle = (description) => {
  if (typeof description === "string" && description.includes("\n")) {
    return <span style={{ whiteSpace: "pre-line" }}>{description}</span>;
  }
  return description;
};

/**
 * Label text with optional (?) tooltip — matches datasource info icons.
 */
export const labelWithInfo = (label, description) => {
  if (!description) {
    return label;
  }
  return [
    label,
    <Tooltip key="info" title={tooltipTitle(description)}>
      <QuestionCircleOutlined
        className="ui-label-info"
        aria-label="More information"
      />
    </Tooltip>,
  ];
};

export default labelWithInfo;
