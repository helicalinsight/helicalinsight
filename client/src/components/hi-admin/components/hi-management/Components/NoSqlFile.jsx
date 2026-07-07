import { useState, useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { updateNoSqlFileData } from "../../../../../redux/actions/admin.actions";
import { SyncOutlined } from "@ant-design/icons";
import { Row, Col, Form, Input, Drawer, Button, Skeleton, Switch, Tooltip } from "antd";
import requests from "../../../../../base/requests";
import notify from "../../../../hi-notifications/notify";

const NoSqlFile = () => {
  const dispatch = useDispatch();
  const noSqlData = useSelector((store) => store.admin.noSqlData);
  const [visible, setVisible] = useState(false);
  const [loading, setLoading] = useState(false);
  const [refreshLoading, setRefreshLoading] = useState(false);
  const [form] = Form.useForm();
  const Notify = notify(dispatch);

  useEffect(() => {
    form.setFieldsValue({
      ...noSqlData,
    });
  }, [noSqlData]);

  useEffect(() => {
    noSqlData === null && fetchNoSqlData();
  }, []);

  const fetchNoSqlData = () => {
    requests.admin(dispatch).fetchNoSqlData(
      {},
      "monitor/system/noSqlConfig",
      (res) => {
        dispatch(updateNoSqlFileData(res.noSqldata.noSql));
        setRefreshLoading(false);
      },
      (e) => {
        setRefreshLoading(false);
      }
    );
  };

  const handleClick = () => {
    form.setFieldsValue({
      ...noSqlData,
    });
    setVisible(true);
  };

  const onClose = () => {
    setVisible(false);
  };

  const onFinish = (values) => {
    setLoading(true);
    requests.admin(dispatch).fetchNoSqlData(
      { noSql: { ...noSqlData, ...values } },
      "monitor/system/updateNoSql",
      (res) => {
        setLoading(false);
        // Notify.success({
        //   type: "Network Call",
        //   ...res,
        // });
        dispatch(updateNoSqlFileData({ ...noSqlData, ...values }));
      },
      (e) => {
        setLoading(false);
        // Notify.error({
        //   type: "Network Call",
        //   ...e,
        // });
      }
    );
  };

  const onRefresh = () => {
    setRefreshLoading(true);
    dispatch(updateNoSqlFileData(null));
    fetchNoSqlData();
  };

  const renderForm = () => {
    return (
      <Form
        layout="vertical"
        hideRequiredMark
        style={{ padding: "10px" }}
        form={form}
        onFinish={onFinish}
      >
        <Row gutter={16}>
          <Col span={12}>
            <Form.Item
              name="url"
              label="Url"
              rules={[
                {
                  required: true,
                  message: "Please enter url",
                },
              ]}
            >
              <Input
                style={{
                  width: "100%",
                }}
                placeholder="Please enter url"
              />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item
              name="host"
              label="Host"
              rules={[
                {
                  required: true,
                  message: "Please enter user name",
                },
              ]}
            >
              <Input placeholder="Please enter host" />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={16}>
          <Col span={12}>
            <Form.Item
              name="userName"
              label="Username"
              rules={[
                {
                  required: true,
                  message: "Please enter user name",
                },
              ]}
            >
              <Input placeholder="Please enter user name" />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item
              name="password"
              label="Password"
              rules={[
                {
                  required: true,
                  message: "Please enter password",
                },
              ]}
            >
              <Input
                style={{
                  width: "100%",
                }}
                placeholder="Please enter password"
              />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={16}>
          <Col span={8}>
            <Form.Item
              name="inMemoryPort"
              label="InMemoryPort"
              rules={[
                {
                  required: true,
                  message: "Please enter inMemoryPort",
                },
              ]}
            >
              <Input
                style={{
                  width: "100%",
                }}
                placeholder="Please enter inMemoryPort"
              />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item
              name="restApiPort"
              label="RestApiPort"
              rules={[
                {
                  required: true,
                  message: "Please enter restApiPort",
                },
              ]}
            >
              <Input placeholder="Please enter restApiPort" />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item
              name="applicationUiPort"
              label="ApplicationUiPort"
              rules={[
                {
                  required: true,
                  message: "Please enter applicationUiPort",
                },
              ]}
            >
              <Input placeholder="Please enter applicationUiPort" />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={8}>
            <Form.Item
              name="driverClass"
              label="DriverClass"
              rules={[
                {
                  required: true,
                  message: "Please enter driverClass",
                },
              ]}
            >
              <Input placeholder="Please enter driverClass" />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item
              name="templatePath"
              label="TemplatePath"
              rules={[
                {
                  required: true,
                  message: "Please enter templatePath",
                },
              ]}
            >
              <Input placeholder="Please enter templatePath" />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item
              name="sparkHome"
              label="SparkHome"
              rules={[
                {
                  required: true,
                  message: "Please enter sparkHome",
                },
              ]}
            >
              <Input placeholder="Please enter sparkHome" />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={16}>
          <Col span={8}>
            <Form.Item
              name="masterPort"
              label="MasterPort"
              rules={[
                {
                  required: true,
                  message: "Please enter masterPort",
                },
              ]}
            >
              <Input placeholder="Please enter masterPort" />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item
              name="masterWebUiPort"
              label="MasterWebUiPort"
              rules={[
                {
                  required: true,
                  message: "Please enter masterWebUiPort",
                },
              ]}
            >
              <Input placeholder="Please enter masterWebUiPort" />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item
              name="workerWebUiport"
              label="WorkerWebUiport"
              rules={[
                {
                  required: true,
                  message: "Please enter workerWebUiport",
                },
              ]}
            >
              <Input placeholder="Please enter workerWebUiport" />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={16}>
          <Col span={8}>
            <Form.Item
              name="metaStoreJdbcUrl"
              label="MetaStoreJdbcUrl"
              rules={[
                {
                  required: true,
                  message: "Please enter metaStoreJdbcUrl",
                },
              ]}
            >
              <Input placeholder="Please enter metaStoreJdbcUrl" />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item
              name="metaStoreDriver"
              label="MetaStoreDriver"
              rules={[
                {
                  required: true,
                  message: "Please enter metaStoreDriver",
                },
              ]}
            >
              <Input placeholder="Please enter metaStoreDriver" />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item
              name="wareHousePath"
              label="WareHousePath"
              rules={[
                {
                  required: true,
                  message: "Please enter wareHousePath",
                },
              ]}
            >
              <Input placeholder="Please enter wareHousePath" />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={16}>
          <Col span={8}>
            <Form.Item
              name="metaStoreDriver"
              label="MetaStoreDriver"
              rules={[
                {
                  required: true,
                  message: "Please enter MetaStoreDriver",
                },
              ]}
            >
              <Input placeholder="Please enter MetaStoreDriver" />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item
              name="metaStoreUserName"
              label="MetaStoreUserName"
              rules={[
                {
                  required: true,
                  message: "Please enter MetaStoreUserName",
                },
              ]}
            >
              <Input
                style={{
                  width: "100%",
                }}
                placeholder="Please enter metaStoreUserName"
              />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item
              name="metaStorePassword"
              label="MetaStorePassword"
              rules={[
                {
                  required: true,
                  message: "Please enter metaStorePassword",
                },
              ]}
            >
              <Input placeholder="Please enter metaStorePassword" />
            </Form.Item>
          </Col>
        </Row>
        <Row>
          <Col span={8}>
            <Form.Item
              name="executeAtStart"
              label="Execute At Start"
              rules={[
                {
                  required: true,
                  message: "Please enter executeAtStart",
                },
              ]}
            >
              <Switch />
            </Form.Item>
          </Col>
        </Row>
        <Row>
          <Col>
            <Button type="primary" htmlType="submit" loading={loading}>
              Save
            </Button>
          </Col>
        </Row>
      </Form>
    );
  };

  return (
    <>
      <Button   data-testid = "hi-nosqlfile-btn" type="primary" onClick={handleClick}>
        No SQL Configuration
      </Button>
      <Drawer
        data-testid = "hi-nosqlfile-drawer"
        title="No SQL Configuration"
        width={720}
        onClose={onClose}
        visible={visible}
        bodyStyle={{ paddingBottom: 80 }}
        extra={
          <Tooltip title="Refresh" placement="left">
            <Button
              onClick={onRefresh}
              icon={<SyncOutlined />}
              loading={refreshLoading}
              type="text"
            ></Button>
          </Tooltip>
        }
      >
        {noSqlData ? renderForm() : <Skeleton />}
      </Drawer>
    </>
  );
};

export default NoSqlFile;
