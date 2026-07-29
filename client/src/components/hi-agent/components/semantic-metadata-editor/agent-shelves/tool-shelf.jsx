import React from "react";
import { Tooltip } from "antd";
import { ApartmentOutlined, FileTextOutlined } from "@ant-design/icons";
import TutorialInfo from "../../../../common/hi-tutorial";

export const TOOL_BUSINESS_VIEW = "business-view";
export const TOOL_AGENT_JSON = "agent-json";

const TOOLS = [
  {
    key: TOOL_BUSINESS_VIEW,
    label: "Business View",
    tooltip:
      "Organize your data into domains and topics. Domains group related business areas, topics hold fields that belong together.",
    icon: <ApartmentOutlined />,
    tutorialKey: "hi-agent-tool-business-view",
  },
  {
    key: TOOL_AGENT_JSON,
    label: "JSON",
    tooltip:
      "View and edit the raw JSON for this semantic model. Changes here update the model configuration.",
    icon: <FileTextOutlined />,
    tutorialKey: "hi-agent-tool-json",
  },
];

export function ToolShelf({
  activeTool,
  onSelect,
  disabledTools = {},
  disabledToolTips = {},
}) {
  return (
    <div className="tool-shelf">
      {TOOLS.map((tool) => {
        const isDisabled = Boolean(disabledTools[tool.key]);
        const tooltip =
          (isDisabled && disabledToolTips[tool.key]) || tool.tooltip;
        return (
          <Tooltip key={tool.key} title={tooltip} placement="right">
            <TutorialInfo elementKey={tool.tutorialKey}>
              <span className={isDisabled ? "tool-shelf-item-wrap is-disabled" : "tool-shelf-item-wrap"}>
                <button
                  type="button"
                  className={`tool-shelf-item${
                    activeTool === tool.key ? " is-active" : ""
                  }${isDisabled ? " is-disabled" : ""}`}
                  onClick={() => {
                    if (isDisabled) return;
                    onSelect(tool.key);
                  }}
                  disabled={isDisabled}
                  aria-disabled={isDisabled}
                  aria-label={tool.label}
                >
                  <span className="tool-shelf-icon">{tool.icon}</span>
                  <span className="tool-shelf-label">{tool.label}</span>
                </button>
              </span>
            </TutorialInfo>
          </Tooltip>
        );
      })}
    </div>
  );
}

export default ToolShelf;
