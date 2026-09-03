import { Col, Form, Row } from "antd";
import { labelWithInfo } from "./labelWithInfo";
import { renderFieldControl } from "./fieldControls";
import { getFieldRules, toSentenceCaseLabel } from "./layoutUtils";
import "./ui-tick-toggle.scss";
import "./ui-form-item-tick-inline.scss";
import "./ui-form-generator.scss";

const isTickField = (field) =>
  (field.type === "boolean" || field.type === "switch") &&
  field.control !== "switch" &&
  field.appearance !== "switch";

/** Prefer layout JSON span; columns only supplies the default when span is omitted. */
const resolveFieldSpan = (field, columns) => {
  const span = Number(field.span);
  if (Number.isFinite(span) && span > 0) return span;
  if (columns === 2) return 12;
  return 24;
};

const isFieldVisible = (field, values = {}) => {
  const when = field?.visibleWhen;
  if (!when) return true;
  const current = values[when.field];
  if (Object.prototype.hasOwnProperty.call(when, "equals")) {
    return current === when.equals;
  }
  if (Array.isArray(when.in)) {
    return when.in.includes(current);
  }
  return true;
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
  includeFields,
  excludeFields,
  hideSectionTitles = false,
  flattenNestedSections = false,
}) => {
  const sectionClassName = dense
    ? "ui-form-generator-section ui-form-generator-section--dense"
    : "ui-form-generator-section";
  const formClassName = dense ? `${className} ui-form-generator--dense` : className;

  const fieldAllowed = (field) => {
    const name = field?.name;
    if (!name) return true;
    if (Array.isArray(includeFields) && includeFields.length) {
      return includeFields.includes(name);
    }
    if (Array.isArray(excludeFields) && excludeFields.length) {
      return !excludeFields.includes(name);
    }
    return true;
  };

  const collectFields = (section) => {
    const own = (Array.isArray(section.fields) ? section.fields : []).filter(
      fieldAllowed
    );
    const nested = Array.isArray(section.sections) ? section.sections : [];
    return own.concat(nested.flatMap(collectFields));
  };

  const renderSection = (section, depth = 0) => {
    const sectionDepth = resolveSectionDepth(section, depth);
    const nested = flattenNestedSections
      ? []
      : Array.isArray(section.sections)
        ? section.sections
        : [];
    const fields = flattenNestedSections
      ? collectFields(section)
      : (Array.isArray(section.fields) ? section.fields : []).filter(fieldAllowed);
    const nestedNodes = nested
      .map((child) => renderSection(child, sectionDepth + 1))
      .filter(Boolean);

    if (!fields.length && !nestedNodes.length) {
      return null;
    }

    const sectionTitle = toSentenceCaseLabel(section.title);

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
        {!hideSectionTitles && (sectionTitle || section.description) && (
          <div className="ui-form-generator-section__title">
            {labelWithInfo(sectionTitle, section.description)}
          </div>
        )}
        {fields.length ? (
          <div className="ui-form-generator-section__body">
            <Form.Item noStyle shouldUpdate>
              {() => {
                const values = form?.getFieldsValue?.(true) || {};
                return (
                  <Row gutter={dense ? [12, 0] : [16, 0]}>
                    {fields
                      .filter((field) => isFieldVisible(field, values))
                      .map((field) => {
                        const tickInline = isTickField(field);
                        const labelNode = labelWithInfo(
                          toSentenceCaseLabel(field.label || field.name),
                          field.description || field.help
                        );
                        return (
                          <Col key={field.name} span={resolveFieldSpan(field, columns)}>
                            <Form.Item
                              name={field.name}
                              label={tickInline ? undefined : labelNode}
                              colon={!tickInline}
                              valuePropName={
                                field.type === "boolean" || field.type === "switch"
                                  ? "checked"
                                  : "value"
                              }
                              rules={getFieldRules(field)}
                              className={
                                tickInline ? "ui-form-item--checkbox" : undefined
                              }
                              style={dense ? { marginBottom: 12 } : undefined}
                            >
                              {renderFieldControl(field, {
                                isAdd,
                                label: tickInline ? labelNode : undefined,
                              })}
                            </Form.Item>
                          </Col>
                        );
                      })}
                  </Row>
                );
              }}
            </Form.Item>
          </div>
        ) : null}
        {nestedNodes}
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
