import React from "react";
import { Tooltip } from "antd";

function activateOnEnterOrSpace(e, action, { stopPropagation = false } = {}) {
  if (e.key !== "Enter" && e.key !== " ") return;
  e.preventDefault();
  if (stopPropagation) e.stopPropagation();
  action?.(e);
}
export function ToolbarIconButton({
  title,
  ariaLabel,
  placement = "left",
  onClick,
  className = "cube-add-metric-action",
  stopPropagation = false,
  children,
}) {
  return (
    <Tooltip title={title} placement={placement}>
      <span
        className={className}
        role="button"
        tabIndex={0}
        aria-label={ariaLabel || title}
        onClick={(e) => {
          if (stopPropagation) e.stopPropagation();
          onClick?.(e);
        }}
        onKeyDown={(e) =>
          activateOnEnterOrSpace(e, onClick, { stopPropagation })
        }
      >
        {children}
      </span>
    </Tooltip>
  );
}

export default ToolbarIconButton;
