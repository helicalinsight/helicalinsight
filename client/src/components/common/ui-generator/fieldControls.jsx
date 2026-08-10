import { AutoComplete, Input, InputNumber, Select, Switch } from "antd";
import { CheckCircleFilled, CheckCircleOutlined } from "@ant-design/icons";
import { isFieldReadOnly } from "./layoutUtils";

/**
 * Light-blue tick toggle (not a checkbox / switch).
 * Works with Form.Item valuePropName="checked".
 */
export const TickToggle = ({
  checked = false,
  onChange,
  disabled = false,
  className = "",
}) => (
  <button
    type="button"
    className={`ui-tick-toggle ${checked ? "ui-tick-toggle--on" : ""} ${className}`}
    aria-pressed={!!checked}
    disabled={disabled}
    onClick={() => {
      if (disabled) return;
      onChange?.(!checked);
    }}
  >
    {checked ? (
      <CheckCircleFilled className="ui-tick-toggle__icon" />
    ) : (
      <CheckCircleOutlined className="ui-tick-toggle__icon" />
    )}
  </button>
);

/**
 * Renders an Ant Design control for a layout field definition.
 */
export const renderFieldControl = (field, { isAdd = false } = {}) => {
  const readOnly = isFieldReadOnly(field, { isAdd });
  const commonProps = {
    disabled: readOnly || field.disabled,
    placeholder: field.placeholder,
  };

  switch (field.type) {
    case "boolean":
    case "switch":
      if (field.control === "tick" || field.appearance === "tick") {
        return <TickToggle disabled={commonProps.disabled} />;
      }
      return <Switch disabled={commonProps.disabled} />;
    case "number":
      return (
        <InputNumber
          {...commonProps}
          style={{ width: "100%" }}
          min={field.min}
          max={field.max}
        />
      );
    case "textarea":
      return (
        <Input.TextArea
          {...commonProps}
          autoSize={
            field.autoSize === false
              ? false
              : field.autoSize && typeof field.autoSize === "object"
                ? field.autoSize
                : {
                    minRows: field.minRows || field.rows || 1,
                    maxRows: field.maxRows || 12,
                  }
          }
          rows={field.autoSize === false ? field.rows || 4 : undefined}
        />
      );
    case "select":
      if (field.editable || field.allowCustom) {
        return (
          <AutoComplete
            {...commonProps}
            style={{ width: "100%" }}
            options={field.options || []}
            filterOption={(input, option) =>
              String(option?.value ?? option?.label ?? "")
                .toLowerCase()
                .includes(String(input || "").toLowerCase())
            }
            allowClear={field.allowClear !== false}
          />
        );
      }
      return (
        <Select
          {...commonProps}
          options={field.options || []}
          allowClear={field.allowClear}
          mode={field.mode}
          showSearch={field.showSearch}
          optionFilterProp={field.optionFilterProp || "label"}
        />
      );
    case "password":
      return <Input.Password {...commonProps} />;
    case "text":
    default:
      return <Input {...commonProps} />;
  }
};

export default renderFieldControl;
