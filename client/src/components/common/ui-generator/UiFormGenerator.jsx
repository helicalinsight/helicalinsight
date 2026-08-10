import { Col, Form, Row } from "antd";
import { labelWithInfo } from "./labelWithInfo";
import { renderFieldControl } from "./fieldControls";
import { getFieldRules } from "./layoutUtils";
import "./ui-tick-toggle.scss";
import "./ui-form-item-tick-inline.scss";
import "./ui-form-generator.scss";

const isTickField = (field) =>
  (field.type === "boolean" || field.type === "switch") &&
  (field.control === "tick" || field.appearance === "tick");

/** Prefer layout JSON span; columns only supplies the default when span is omitted. */
const resolveFieldSpan = (field, columns) => {
  const span = Number(field.span);
  if (Number.isFinite(span) && span > 0) return span;
  if (columns === 2) return 12;
  return 24;
};

const resolveSectionDepth = (section, fallbackDepth = 0) => {
  const level = Number(section?.level);
  if (Number.isFinite(level) && level >= 0) return level;
  return fallbackDepth;
};

/**
 * Renders a vertical Ant Design form from backend layout JSON.
 *
 * Supports nested sections via `section.sections` and optional `section.level`
 * (0 = top YAML group, 1 = sub, 2 = child) for left-indent hierarchy.
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

  const renderSection = (section, depth = 0) => {
    const sectionDepth = resolveSectionDepth(section, depth);
    const nested = Array.isArray(section.sections) ? section.sections : [];
    const fields = Array.isArray(section.fields) ? section.fields : [];

    return (
      <div
        key={section.key || section.title || `section-${sectionDepth}`}
        className={`${sectionClassName} ui-form-generator-section--level-${Math.min(
          sectionDepth,
          3
        )}`}
        style={{
          marginBottom: dense ? 12 : 20,
          paddingTop: dense ? 4 : 8,
          marginLeft: sectionDepth * 16,
        }}
      >
        {(section.title || section.description) && (
          <div className="ui-form-generator-section__title">
            {labelWithInfo(section.title, section.description)}
          </div>
        )}
        {fields.length ? (
          <div className="ui-form-generator-section__body">
            <Row gutter={dense ? [12, 0] : [16, 0]}>
              {fields.map((field) => {
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
        ) : null}
        {nested.map((child) => renderSection(child, sectionDepth + 1))}
      </div>
    );
  };

  const sections = (layout?.sections || []).map((section) =>
    renderSection(section, 0)
  );

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
