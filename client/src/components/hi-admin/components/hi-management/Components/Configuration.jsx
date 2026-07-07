import { useState, useEffect, useMemo } from "react";
import { useSelector } from "react-redux";
import { Card, Row, Col, Table, Button, Drawer, Form, Input } from "antd";
import { VList } from "virtuallist-antd";
import { PlusOutlined } from "@ant-design/icons";

const configColumns = [
  {
    title: "Key",
    id: 30,
    key: "key",
    dataIndex: "configKey",
  },
  {
    title: "Value",
    id: 40,
    key: "value",
    dataIndex: "configValue",
  },
];

const Configuration = () => {
  const advancedData = useSelector(
    (store) => store.admin.managementAdvancedData
  );
  const [configInfo, setConfigInfo] = useState([]);
  const [visible, setVisible] = useState(false);
  const [form] = Form.useForm();

  const vComponents = useMemo(() => {
    return VList({
      height: 250, //same value for scrolly
    });
  }, []);

  useEffect(() => {
    if (advancedData) {
      const configuration = advancedData.config;
      const converetedConfig = [];

      if (configuration) {
        Object.keys(configuration).map((key) => {
          const configKey = key.replace(/(^\w|\s\w)/g, (m) => m.toUpperCase());
          const configValue = configuration[key];
          converetedConfig.push({ configKey, configValue });
          setConfigInfo(converetedConfig);
        });
      }
    }
  }, [advancedData]);

  const showDrawer = () => {
    setVisible(true);
  };

  const onClose = () => {
    setVisible(false);
    form.resetFields();
  };

  const onFinish = (values) => {
    // const { key, value } = values;
  };

  return (
    <Card hoverable>
      <Row gutter={[16, 16]} className="config-container">
        {advancedData && advancedData.config ? (
          <>
            <Col span={24}>
              <Button
                type="primary"
                icon={<PlusOutlined />}
                onClick={showDrawer}
              >
                Add configuration
              </Button>
            </Col>
            <Drawer
              title="Add configuration"
              size="large"
              placement="right"
              onClose={onClose}
              visible={visible}
            >
              <Form data-testid ="hi-advanced-config-form" layout="vertical" onFinish={onFinish} form={form}>
                <Row gutter={16}>
                  <Col span={24}>
                    <Form.Item
                      label="Key"
                      name="key"
                      rules={[
                        {
                          required: true,
                          message: "Please enter a valid key.",
                        },
                      ]}
                    >
                      <Input placeholder="Key" />
                    </Form.Item>
                  </Col>
                  <Col span={24}>
                    <Form.Item
                      label="Value"
                      name="value"
                      rules={[
                        {
                          required: true,
                          message: "Please enter a valid value.",
                        },
                      ]}
                    >
                      <Input placeholder="Value" />
                    </Form.Item>
                  </Col>
                  <Col span={3}>
                    <Button
                      type="primary"
                      htmlType="submit"
                      icon={<PlusOutlined />}
                    >
                      Add
                    </Button>
                  </Col>
                  <Col span={3}>
                    <Button type="danger" onClick={onClose}>
                      Cancel
                    </Button>
                  </Col>
                </Row>
              </Form>
            </Drawer>
            <Table
              columns={configColumns}
              dataSource={configInfo}
              pagination={false}
              scroll={{
                y: 250,
              }}
              vid="dice-config"
              components={vComponents}
            />
          </>
        ) : (
          <Col data-testid ="hi-advanced-config-text">No configuration to display</Col>
        )}
      </Row>
    </Card>
  );
};

export default Configuration;
