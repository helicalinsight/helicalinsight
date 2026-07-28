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
  disabled = false,
  showIndicator = false,
  children,
}) {
  const runAction = (e) => {
    if (disabled) return;
    onClick?.(e);
  };

  return (
    <Tooltip title={title} placement={placement}>
      <span
        className={`${className}${disabled ? " is-disabled" : ""}${
          showIndicator ? " has-unsaved-indicator" : ""
        }`}
        role="button"
        tabIndex={disabled ? -1 : 0}
        aria-disabled={disabled}
        aria-label={ariaLabel || title}
        onClick={(e) => {
          if (stopPropagation) e.stopPropagation();
          runAction(e);
        }}
        onKeyDown={(e) =>
          activateOnEnterOrSpace(e, runAction, { stopPropagation })
        }
      >
        {children}
        {showIndicator ? (
          <span className="toolbar-unsaved-dot" aria-hidden="true" />
        ) : null}
      </span>
    </Tooltip>
  );
}

export default ToolbarIconButton;
