import { AutoComplete, Input, InputNumber, Select, Switch } from "antd";
import { isFieldReadOnly } from "./layoutUtils";

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
      return <Input.TextArea {...commonProps} rows={field.rows || 4} />;
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
