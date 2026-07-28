import { Col, Form, Row } from "antd";
import { labelWithInfo } from "./labelWithInfo";
import { renderFieldControl } from "./fieldControls";
import { getFieldRules } from "./layoutUtils";

/**
 * Renders a vertical Ant Design form from backend layout JSON.
 *
 * @param {object} props
 * @param {import('antd').FormInstance} props.form
 * @param {object} props.layout - { sections: [{ title, description, fields }] }
 * @param {boolean} [props.isAdd]
 * @param {string} [props.className]
 * @param {object} [props.formProps] - extra Form props
 */
export const UiFormGenerator = ({
  form,
  layout,
  isAdd = false,
  className = "ui-form-generator",
  formProps = {},
}) => (
  <Form
    form={form}
    layout="vertical"
    className={className}
    requiredMark="optional"
    {...formProps}
  >
    {(layout?.sections || []).map((section) => (
      <div
        key={section.key || section.title}
        className="ui-form-generator-section"
        style={{
          marginBottom: 20,
          padding: "16px 16px 4px",
          border: "1px solid #f0f0f0",
          borderRadius: 8,
          background: "#fafafa",
        }}
      >
        {(section.title || section.description) && (
          <div style={{ marginBottom: 8, fontSize: 14, fontWeight: 600 }}>
            {labelWithInfo(section.title, section.description)}
          </div>
        )}
        <Row gutter={[16, 0]}>
          {(section.fields || []).map((field) => (
            <Col key={field.name} span={field.span || 24}>
              <Form.Item
                name={field.name}
                label={labelWithInfo(
                  field.label || field.name,
                  field.description || field.help
                )}
                valuePropName={
                  field.type === "boolean" || field.type === "switch"
                    ? "checked"
                    : "value"
                }
                rules={getFieldRules(field)}
              >
                {renderFieldControl(field, { isAdd })}
              </Form.Item>
            </Col>
          ))}
        </Row>
      </div>
    ))}
  </Form>
);

export default UiFormGenerator;
