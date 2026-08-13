import { useEffect } from "react";
import { Button, Form } from "antd";
import { SaveOutlined } from "@ant-design/icons";
import { UiFormGenerator } from "../../../../common/ui-generator";
import {
  readLayoutValues,
  writeLayoutValues,
} from "../utils/config-layout-values";

/**
 * Main-page form editor driven by a per-file *.ui.layout.json.
 * Not a drawer — renders inline in the configurations editor panel.
 */
const LayoutFormEditor = ({
  layout,
  content,
  format,
  saving,
  onSave,
}) => {
  const [form] = Form.useForm();
  const resolvedFormat = format || layout?.format || "xml";

  useEffect(() => {
    const values = readLayoutValues(layout, content, resolvedFormat);
    form.setFieldsValue(values);
  }, [layout, content, resolvedFormat, form]);

  const handleSave = async () => {
    const values = await form.validateFields();
    const nextContent = writeLayoutValues(layout, content, values, resolvedFormat);
    onSave?.(nextContent);
  };

  return (
    <div className="hi-config-layout-form-editor">
      <UiFormGenerator
        form={form}
        layout={layout}
        dense
        columns={1}
        className="hi-config-layout-form"
        formProps={{
          layout: "horizontal",
          labelAlign: "left",
          colon: false,
          labelCol: { flex: "160px" },
          wrapperCol: { flex: 1 },
        }}
      />
      <div className="hi-config-editor-actions">
        <Button
          type="primary"
          icon={<SaveOutlined />}
          loading={saving}
          onClick={handleSave}
        >
          Save
        </Button>
      </div>
    </div>
  );
};

export default LayoutFormEditor;
