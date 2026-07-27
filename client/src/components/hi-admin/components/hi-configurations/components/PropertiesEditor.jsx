import { Input, Button, Empty, Row, Col } from "antd";
import { useEffect, useState } from "react";

const PropertiesEditor = ({ content, saving, onSave }) => {
  const [values, setValues] = useState(content || {});

  useEffect(() => {
    setValues(content || {});
  }, [content]);

  const entries = Object.keys(values || {});

  if (!entries.length) {
    return <Empty description="No properties found in this file" />;
  }

  return (
    <div className="hi-config-properties-form">
      <Row gutter={[16, 0]}>
        {entries.map((key) => (
          <Col span={12} key={key}>
            <div className="hi-config-property-field">
              <label className="hi-config-property-label" title={key}>
                {key}
              </label>
              <Input
                allowClear
                value={values[key] ?? ""}
                onChange={(event) =>
                  setValues((prev) => ({
                    ...prev,
                    [key]: event.target.value,
                  }))
                }
              />
            </div>
          </Col>
        ))}
      </Row>
      <div className="hi-config-editor-actions">
        <Button type="primary" loading={saving} onClick={() => onSave(values)}>
          Save
        </Button>
      </div>
    </div>
  );
};

export default PropertiesEditor;
