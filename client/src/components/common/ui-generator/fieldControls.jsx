import { AutoComplete, Checkbox, Input, InputNumber, Radio, Select, Switch } from "antd";
import { isFieldReadOnly } from "./layoutUtils";

/**
 * Square checkbox for boolean layout fields.
 * Works with Form.Item valuePropName="checked".
 */
export const TickToggle = ({
  checked = false,
  onChange,
  disabled = false,
  className = "",
  children,
}) => (
  <Checkbox
    className={`ui-tick-toggle ${className}`.trim()}
    checked={!!checked}
    disabled={disabled}
    onChange={onChange}
  >
    {children}
  </Checkbox>
);

/**
 * Renders an Ant Design control for a layout field definition.
 */
export const renderFieldControl = (field, { isAdd = false, label } = {}) => {
  const readOnly = isFieldReadOnly(field, { isAdd });
  const commonProps = {
    disabled: readOnly || field.disabled,
    placeholder: field.placeholder,
  };

  switch (field.type) {
    case "boolean":
    case "switch":
      if (field.control === "switch" || field.appearance === "switch") {
        return <Switch disabled={commonProps.disabled} />;
      }
      return (
        <TickToggle disabled={commonProps.disabled}>{label}</TickToggle>
      );
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
            loading={!!field.loading}
            filterOption={(input, option) =>
              String(option?.label ?? option?.value ?? "")
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
          loading={!!field.loading}
          filterOption={(input, option) =>
            String(option?.label ?? option?.children ?? option?.value ?? "")
              .toLowerCase()
              .includes(String(input || "").toLowerCase())
          }
        />
      );
    case "radio":
      return (
        <Radio.Group
          disabled={commonProps.disabled}
          options={field.options || []}
          optionType={field.optionType || "button"}
          buttonStyle={field.buttonStyle || "solid"}
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
