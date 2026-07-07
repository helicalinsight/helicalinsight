import React from "react";
import { Tooltip, Select, Input } from "antd";
import { InfoCircleOutlined } from "@ant-design/icons";
import { ChipsEditor } from "./semantic-metadata-chips-editor";
import { getSemanticTooltipText } from "./semantic-metadata-tooltips";

const { Option } = Select;

export function InfoTooltip({ label, info }) {
  const tooltipTitle =
    info ??
    (typeof label === "string" && label.trim()
      ? getSemanticTooltipText(label)
      : "Info");

  return (
    <Tooltip
      title={<div style={{ whiteSpace: "pre-wrap" }}>{tooltipTitle}</div>}
      placement="top"
    >
      <InfoCircleOutlined className="me-info-ico" />
    </Tooltip>
  );
}

function LabelWithInfo({ label, info }) {
  return (
    <div className="me-label-row">
      <span className="me-label-text">{label}</span>
      <InfoTooltip label={label} info={info} />
    </div>
  );
}

export function TextField({ label, info, value, onChange, mono = false }) {
  return (
    <div className="me-field">
      <label>
        <LabelWithInfo label={label} info={info} />
      </label>
      <Input
        className={`me-input${mono ? " mono" : ""}`}
        type="text"
        value={value ?? ""}
        onChange={(e) => onChange(e.target.value)}
      />
    </div>
  );
}

export function TextareaField({ label, info, value, onChange, rows = 3 }) {
  return (
    <div className="me-field">
      <label>
        <LabelWithInfo label={label} info={info} />
      </label>
      <textarea
        className="me-textarea"
        rows={rows}
        value={value ?? ""}
        onChange={(e) => onChange(e.target.value)}
      />
    </div>
  );
}

export function SelectField({
  label,
  info,
  value,
  options,
  onChange,
  allowFree = false,
}) {
  const handleChange = (val) => {
    if (val === "custom") {
      const v = window.prompt("Custom value:", value || "");
      if (v) onChange(v);
    } else {
      onChange(val ?? "");
    }
  };

  const seen = new Set();
  const opts = [];

  if (value && !options.includes(value)) {
    opts.push(
      <Option key={value} value={value}>
        {value} (custom)
      </Option>,
    );
    seen.add(value);
  }

  options.forEach((opt) => {
    if (seen.has(opt)) return;
    seen.add(opt);
    opts.push(
      <Option key={opt} value={opt}>
        {opt}
      </Option>,
    );
  });

  if (allowFree) {
    opts.push(
      <Option key="custom" value="custom">
        + Custom...
      </Option>,
    );
  }

  return (
    <div className="me-field">
      <label>
        <LabelWithInfo label={label} info={info} />
      </label>
      <Select
        style={{ width: "100%" }}
        value={value || undefined}
        onChange={handleChange}
        placeholder="Select an option"
        allowClear
        onClear={() => onChange("")}
      >
        {opts}
      </Select>
    </div>
  );
}

export function StatTile({ label, info, value }) {
  return (
    <div className="me-card dense" style={{ margin: 0 }}>
      <div className="me-stat-label">
        <span>{label}</span>
      </div>
      <div className="me-stat-value">{value}</div>
    </div>
  );
}

export { ChipsEditor };
