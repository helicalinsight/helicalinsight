import { Col, Form, Row } from "antd";
import { labelWithInfo } from "./labelWithInfo";
import { renderFieldControl } from "./fieldControls";
import { getFieldRules } from "./layoutUtils";
import "./ui-tick-toggle.scss";
import "./ui-form-item-tick-inline.scss";

const isTickField = (field) =>
  (field.type === "boolean" || field.type === "switch") &&
  (field.control === "tick" || field.appearance === "tick");

const resolveFieldSpan = (field, columns) => {
  if (columns === 2) {
    // Keep explicit full-width rows; otherwise always half (never span 8 / 3-col).
    if (Number(field.span) === 24) return 24;
    return 12;
  }
  return field.span || 24;
};

/**
 * Renders a vertical Ant Design form from backend layout JSON.
 *
 * @param {object} props
 * @param {import('antd').FormInstance} props.form
 * @param {object} props.layout - { sections: [{ title, description, fields }] }
 * @param {boolean} [props.isAdd]
 * @param {string} [props.className]
 * @param {object} [props.formProps] - extra Form props
 * @param {boolean} [props.embedded] - render fields only (parent owns Form)
 * @param {boolean} [props.dense] - tighter section/field spacing for side panels
 * @param {2} [props.columns] - force a two-column grid (non-24 spans → 12)
 */
export const UiFormGenerator = ({
  form,
  layout,
  isAdd = false,
  className = "ui-form-generator",
  formProps = {},
  embedded = false,
  dense = false,
  columns,
}) => {
  const sectionClassName = dense
    ? "ui-form-generator-section ui-form-generator-section--dense"
    : "ui-form-generator-section";
  const formClassName = dense ? `${className} ui-form-generator--dense` : className;

  const sections = (layout?.sections || []).map((section) => (
    <div
      key={section.key || section.title}
      className={sectionClassName}
      style={{
        marginBottom: dense ? 12 : 20,
        padding: dense ? "4px 0 0" : "8px 0 4px",
      }}
    >
      {(section.title || section.description) && (
        <div
          style={{
            marginBottom: dense ? 6 : 8,
            fontSize: dense ? 13 : 14,
            fontWeight: 600,
          }}
        >
          {labelWithInfo(section.title, section.description)}
        </div>
      )}
      <Row gutter={dense ? [12, 0] : [16, 0]}>
        {(section.fields || []).map((field) => {
          const tickInline = isTickField(field);
          return (
            <Col key={field.name} span={resolveFieldSpan(field, columns)}>
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
                className={tickInline ? "ui-form-item--tick-inline" : undefined}
                style={dense ? { marginBottom: 12 } : undefined}
              >
                {renderFieldControl(field, { isAdd })}
              </Form.Item>
            </Col>
          );
        })}
      </Row>
    </div>
  ));

  if (embedded) {
    return <div className={formClassName}>{sections}</div>;
  }

  return (
    <Form
      form={form}
      layout="vertical"
      size={dense ? "small" : undefined}
      className={formClassName}
      requiredMark={false}
      {...formProps}
    >
      {sections}
    </Form>
  );
};

export default UiFormGenerator;
