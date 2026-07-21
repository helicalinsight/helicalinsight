import React from "react";
import { Tooltip } from "antd";
import { ApartmentOutlined, FileTextOutlined } from "@ant-design/icons";

export const TOOL_BUSINESS_VIEW = "business-view";
export const TOOL_AGENT_JSON = "agent-json";

const TOOLS = [
  {
    key: TOOL_BUSINESS_VIEW,
    label: "Business View",
    tooltip: "Business View",
    icon: <ApartmentOutlined />,
  },
  {
    key: TOOL_AGENT_JSON,
    label: "JSON",
    tooltip: "Generated JSON",
    icon: <FileTextOutlined />,
  },
];

export function ToolShelf({ activeTool, onSelect }) {
  return (
    <div className="tool-shelf">
      {TOOLS.map((tool) => (
        <Tooltip key={tool.key} title={tool.tooltip} placement="right">
          <button
            type="button"
            className={`tool-shelf-item${
              activeTool === tool.key ? " is-active" : ""
            }`}
            onClick={() => onSelect(tool.key)}
            aria-label={tool.label}
          >
            <span className="tool-shelf-icon">{tool.icon}</span>
            <span className="tool-shelf-label">{tool.label}</span>
          </button>
        </Tooltip>
      ))}
    </div>
  );
}

export default ToolShelf;
