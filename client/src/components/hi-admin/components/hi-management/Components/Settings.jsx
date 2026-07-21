import { useState, useEffect } from "react";
import { useSelector, useDispatch } from "react-redux";

import { Row, Col, Form, Input, Button, Select, Switch as Toggler, Tooltip } from "antd";
import { CheckOutlined, InfoCircleFilled, UpOutlined, DownOutlined } from "@ant-design/icons";

import requests from "../../../../../base/requests";
import notify from "../../../../hi-notifications/notify";
import { updateManagementData } from "../../../../../redux/actions/admin.actions";

const { Option } = Select;

const HostIP = "127.0.0.1"; // your host ip address


const Settings = () => {
  const managementData = useSelector((store) => store.admin.managementData);
  const dataWarehouseInputValue =
    Array.isArray(managementData?.drillStorageLocation) &&
      managementData?.drillStorageLocation[0] &&
      managementData?.drillStorageLocation[0]["@path"]
      ? managementData?.drillStorageLocation[0]["@path"]
      : "";

  const [isMoreOptionsClicked, setIsMoreOptionsClicked] = useState(false);
  const [isSecurityEnabled, setIsSecurityEnabled] = useState(false);
  const [isDistributedModeEnabled, setIsDistributedModeEnabled] = useState(false);
  const [storageType, setStorageType] = useState(
    managementData.storageImpl ? managementData.storageImpl : "Select"
  );
  const [portNumber, setPortNumber] = useState(managementData?.urlConfig?.port);
  const [hostNumber, setHostNumber] = useState(managementData?.urlConfig?.host);
  const [securityCheck, setSecurityCheck] = useState({
    enabled: managementData?.urlConfig?.httpsState === "true" ? true : false,
    type: managementData?.urlConfig?.https,
  });
  const [isLoading, setIsLoading] = useState(false);

  const [form] = Form.useForm();
  const dispatch = useDispatch();
  const Notify = notify(dispatch);

  useEffect(() => {
    form.setFieldsValue({
      host: hostNumber,
      port: portNumber,
    });
  }, []);

  const onFinish = (values) => {
    setIsLoading(true);
    const { dataWarehousePath, storageImplementation } = values;

    const urlConfig = {
      host: HostIP,
      port: "8047",
      dbPort: "31010",
      extraParam: [],
      securityEnabled: isSecurityEnabled,
      distributedMode: isDistributedModeEnabled,
      username: [],
      password: [],
      securityCheckType: "/j_security_check",
      securityMode: "plain",
      zookeeperPort: "2181",
      httpsState: securityCheck.enabled,
      https: securityCheck.type,
    };

    const fileSystemConfiguration = {
      hdfs: {
        description:
          " Use the hdfs storage to upload your flat files into hadoop ecosystem. \\n\\t\\t\\tHadoop should be up and running. Hdfs Host is ip address of the name node server. Hdfs port is the datanode port. The Data Warehouse path will be created in hadoop datanode. The path should have read and write access.",
        host: [],
        port: "54310",
      },
      sftp: {
        description:
          " Use SFTP When the drill/middleware is installed in separate server and Helical Insight is installed in different Server. The files will be uploaded to the server where drill is running. Incase drill/middleware is installed in the Windows machine, please use linux sytle path in Datawarehouse path. Example /C:/Users/Helical/your/path/to/datawarehouse",
        host: HostIP,
        password: "helical",
        port: "22",
        username: "helical",
      },
      standalone: {
        description:
          " Use standalone when middleware and helical insight are in the same machine. The dataware house path will be created inside the System Directory of the hi-repository folder. All the files uploaded will be saved in that location",
        subDescription: [],
      },
    };

    let formData = {
      drillStorageLocation: [{ path: dataWarehousePath }],
      urlConfig: urlConfig,
      fileSystemConfiguration: fileSystemConfiguration,
      enabled: true,
      storageImpl: storageImplementation,
    };

    if (isMoreOptionsClicked) {
      const { host, port, dbPort } = values;
      formData = {
        ...formData,
        urlConfig: {
          ...urlConfig,
          host: host,
          port: port,
          dbPort: dbPort,
        },
      };

      if (isSecurityEnabled) {
        const { username, password, securityCheckType, securityMode } = values;
        formData = {
          ...formData,
          urlConfig: {
            ...urlConfig,
            host: host,
            port: port,
            dbPort: dbPort,
            username: username,
            password: password,
            securityCheckType: securityCheckType,
            securityMode: securityMode,
          },
        };
      }

      if (isDistributedModeEnabled) {
        const { zookeeperPort } = values;
        formData = {
          ...formData,
          urlConfig: {
            ...urlConfig,
            host: host,
            port: port,
            dbPort: dbPort,
            zookeeperPort: zookeeperPort,
          },
        };
      }

      if (isSecurityEnabled && isDistributedModeEnabled) {
        const { username, password, securityCheckType, securityMode, zookeeperPort } = values;
        formData = {
          ...formData,
          urlConfig: {
            ...urlConfig,
            host: host,
            port: port,
            dbPort: dbPort,
            username: username,
            password: password,
            securityCheckType: securityCheckType,
            securityMode: securityMode,
            zookeeperPort: zookeeperPort,
          },
        };
      }
    }

    if (storageImplementation === "sftp") {
      const { sftpHost, sftpPassword, sftpPort, sftpUsername } = values;
      formData = {
        ...formData,
        fileSystemConfiguration: {
          ...fileSystemConfiguration,
          sftp: {
            description:
              " Use SFTP When the drill/middleware is installed in separate server and Helical Insight is installed in different Server. The files will be uploaded to the server where drill is running. Incase drill/middleware is installed in the Windows machine, please use linux sytle path in Datawarehouse path. Example /C:/Users/Helical/your/path/to/datawarehouse",
            host: sftpHost,
            password: sftpPassword,
            port: sftpPort,
            username: sftpUsername,
          },
        },
      };
    }

    if (storageImplementation === "hdfs") {
      const { hdfsHost, hdfsPort } = values;
      formData = {
        ...formData,
        fileSystemConfiguration: {
          ...fileSystemConfiguration,
          hdfs: {
            description:
              " Use the hdfs storage to upload your flat files into hadoop ecosystem. \\n\\t\\t\\tHadoop should be up and running. Hdfs Host is ip address of the name node server. Hdfs port is the datanode port. The Data Warehouse path will be created in hadoop datanode. The path should have read and write access.",
            host: hdfsHost,
            port: hdfsPort,
          },
        },
      };
    }

    const updateDrillUri = "core/dataSource/updateDrillConfig";

    const updatedData = {
      ...managementData,
      ...formData,
      drillStorageLocation: [{ "@path": formData.drillStorageLocation[0].path }],
      enabled: "true",
    };

    dispatch(updateManagementData(updatedData));

    console.log({ updatedData });

    requests.admin(dispatch).postManagementData(
      formData,
      updateDrillUri,
      (res) => {
        // Notify.success({
        //   type: "Network Call",
        //   ...res,
        // });
        setIsLoading(false);
      },
      (e) => {
        // Notify.error({
        //   type: "Network Call",
        //   ...e,
        // });
        setIsLoading(false);
      }
    );
  };

  const handleStorageImplementationChange = (value) => {
    setStorageType(value);
  };

  const handleSecurityCheck = () => {
    setSecurityCheck({
      enabled: !securityCheck.enabled,
      type: securityCheck.enabled ? "http" : "https",
    });
  };

  return (
    <Form layout="vertical" form={form} onFinish={onFinish}>
      <Row gutter={16} justify="space-between">
        <Col span={11}>
          <Row>
            <Col sm={24} md={24}>
              <p
                onClick={() => {
                  setIsMoreOptionsClicked(!isMoreOptionsClicked);
                }}
                className="more-options"
              >
                {isMoreOptionsClicked ? (
                  <span>
                    Less <UpOutlined />
                  </span>
                ) : (
                  <span>
                    More <DownOutlined />
                  </span>
                )}
              </p>
              <Form.Item
                name="url"
                htmlFor={null}
                label={[
                  "URL",
                  <Tooltip title="Drill server web url">
                    <InfoCircleFilled style={{ marginLeft: "5px", fontSize: "12px" }} />
                  </Tooltip>,
                ]}
              >
                <Input disabled value={`${securityCheck.type}://${hostNumber}:${portNumber}`} />
                <p className="more-options">
                  <a target="_blank" href={`http://${hostNumber}:${portNumber}`}>
                    Open Url
                  </a>
                </p>
              </Form.Item>
            </Col>

            {isMoreOptionsClicked && (
              <Col span={24}>
                <Row justify="space-between">
                  <Col span={11}>
                    <Form.Item
                      name="host"
                      label={[
                        "Host",
                        <Tooltip title="The IP address of the Apache drill server">
                          <InfoCircleFilled style={{ marginLeft: "5px", fontSize: "12px" }} />
                        </Tooltip>,
                      ]}
                      rules={[
                        {
                          required: true,
                          message: "Please provide a valid Input",
                        },
                      ]}
                    >
                      <Input onChange={(e) => setHostNumber(e.target.value)} />
                    </Form.Item>
                  </Col>
                  <Col span={11}>
                    <Form.Item
                      name="port"
                      rules={[
                        {
                          required: true,
                          message: "Please provide a valid Input",
                        },
                      ]}
                      label={[
                        "Port",
                        <Tooltip title="The webconsole port number to access Apache Drill using browser/client total. Default is 8047">
                          <InfoCircleFilled style={{ marginLeft: "5px", fontSize: "12px" }} />
                        </Tooltip>,
                      ]}
                    >
                      <Input onChange={(e) => setPortNumber(e.target.value)} />
                    </Form.Item>
                  </Col>
                  <Col span={11}>
                    <Form.Item
                      name="dbPort"
                      label={[
                        "Database/UserPort",
                        <Tooltip title="The JDBC Connection part of Apache Drill. Default is 31010">
                          <InfoCircleFilled style={{ marginLeft: "5px", fontSize: "12px" }} />
                        </Tooltip>,
                      ]}
                      initialValue={managementData?.urlConfig?.dbPort}
                    >
                      <Input />
                    </Form.Item>
                  </Col>
                  <Col span={11}>
                    <Form.Item
                      label={[
                        "Extra Paramters",
                        <Tooltip title="Extra configuration that needs to be part of jdbc url. Eg: key1=value1 & key2= value2...">
                          <InfoCircleFilled style={{ marginLeft: "5px", fontSize: "12px" }} />
                        </Tooltip>,
                      ]}
                    >
                      <Input placeholder="Eg: key1=value1 & key2= value2..." />
                    </Form.Item>
                  </Col>
                  <Col span={11}>
                    <Form.Item
                      label={[
                        "Securiy Enabled",
                        <Tooltip title="Please enable this if Apache Drill is having authentication mechanism.">
                          <InfoCircleFilled style={{ marginLeft: "5px", fontSize: "12px" }} />
                        </Tooltip>,
                      ]}
                    >
                      <Toggler
                        checked={isSecurityEnabled}
                        onChange={() => setIsSecurityEnabled(!isSecurityEnabled)}
                      />
                    </Form.Item>
                  </Col>
                  <Col span={11}>
                    <Form.Item
                      label={[
                        "Distributed Mode",
                        <Tooltip title="If Apache Drill is installed in distributed mode then please enable this to add the zookeeperPort">
                          <InfoCircleFilled style={{ marginLeft: "5px", fontSize: "12px" }} />
                        </Tooltip>,
                      ]}
                    >
                      <Toggler
                        checked={isDistributedModeEnabled}
                        onChange={() => setIsDistributedModeEnabled(!isDistributedModeEnabled)}
                      />
                    </Form.Item>
                  </Col>
                  {isSecurityEnabled && (
                    <>
                      <Col span={11}>
                        <Form.Item
                          name="username"
                          label={[
                            "Username",
                            <Tooltip title="Please enter the username that allows to access Apache Drill">
                              <InfoCircleFilled style={{ marginLeft: "5px", fontSize: "12px" }} />
                            </Tooltip>,
                          ]}
                        >
                          <Input />
                        </Form.Item>
                      </Col>
                      <Col span={11}>
                        <Form.Item
                          name="password"
                          label={[
                            "Password",
                            <Tooltip title="Please enter the password that is associated with the username to access Apache Drill">
                              <InfoCircleFilled style={{ marginLeft: "5px", fontSize: "12px" }} />
                            </Tooltip>,
                          ]}
                        >
                          <Input type="password" />
                        </Form.Item>
                      </Col>
                      <Col span={11}>
                        <Form.Item
                          label={[
                            "Security Check Type",
                            <Tooltip title="The end point for form authentication. The default value is /j_security_check">
                              <InfoCircleFilled style={{ marginLeft: "5px", fontSize: "12px" }} />
                            </Tooltip>,
                          ]}
                          name="securityCheckType"
                          initialValue={managementData.urlConfig.securityCheckType}
                        >
                          <Input />
                        </Form.Item>
                      </Col>
                      <Col span={11}>
                        <Form.Item
                          label={[
                            "Security Mode",
                            <Tooltip title="Type of security mechanism used by Apache Drill such as plain, libpam4j, SPNEGO">
                              <InfoCircleFilled style={{ marginLeft: "5px", fontSize: "12px" }} />
                            </Tooltip>,
                          ]}
                          name="securityMode"
                          initialValue={managementData.urlConfig.securityMode}
                        >
                          <Input />
                        </Form.Item>
                      </Col>
                    </>
                  )}
                  {isDistributedModeEnabled && (
                    <Col span={12}>
                      <Form.Item
                        label={[
                          "Zookeeper Port",
                          <Tooltip title="The port for zookeeper server when using distributed mode. Default port is 2181">
                            <InfoCircleFilled style={{ marginLeft: "5px", fontSize: "12px" }} />
                          </Tooltip>,
                        ]}
                        name="zookeeperPort"
                        initialValue={managementData.urlConfig.zookeeperPort}
                      >
                        <Input />
                      </Form.Item>
                    </Col>
                  )}
                </Row>
              </Col>
            )}
            <Col span={24}>
              <Form.Item
                label={[
                  "SSL/HTTPS",
                  <Tooltip title="Please make sure that SSL settings are configurable(enabled/disabled) the same from drill as well.">
                    <InfoCircleFilled style={{ marginLeft: "5px", fontSize: "12px" }} />
                  </Tooltip>,
                ]}
              >
                <Toggler
                  onChange={handleSecurityCheck}
                  checked={securityCheck.enabled}
                  checkedChildren={<CheckOutlined />}
                />
              </Form.Item>
            </Col>
          </Row>
        </Col>
        <Col span={11}>
          <Row>
            <Col span={24}>
              <Form.Item
                name="dataWarehousePath"
                label={[
                  "Data Warehouse Path",
                  <Tooltip title="Please enter valid path for data warehouse. Make sure the path is accessible has read write access">
                    <InfoCircleFilled style={{ marginLeft: "5px", fontSize: "12px" }} />
                  </Tooltip>,
                ]}
                initialValue={dataWarehouseInputValue}
                rules={[{ required: true, message: "Please provide a valid input" }]}
              >
                <Input />
              </Form.Item>
            </Col>
            <Col span={24}>
              <Form.Item
                name="storageImplementation"
                label={[
                  "Storage Implementation",
                  <Tooltip title="Please choose datawarehouse storage meachanism such as sftp, hdfs, standalone">
                    <InfoCircleFilled style={{ marginLeft: "5px", fontSize: "12px" }} />
                  </Tooltip>,
                ]}
                initialValue={storageType}
              >
                <Select
                  onChange={handleStorageImplementationChange}
                  placeholder="Select Storage Implementation"
                >
                  <Option value="Select" disabled>
                    -- Select --
                  </Option>
                  <Option value="hdfs"> hdfs</Option>
                  <Option value="sftp">sftp</Option>
                  <Option value="standalone"> standalone</Option>
                </Select>
              </Form.Item>
            </Col>
            {storageType === "sftp" && (
              <Row justify="space-between">
                <Col span={24}>
                  <Form.Item>
                    <InfoCircleFilled />
                    {managementData.fileSystemConfiguration.sftp.description}
                  </Form.Item>
                </Col>
                <Col span={11}>
                  <Form.Item
                    name="sftpHost"
                    label="Sftp Host"
                    initialValue={managementData.fileSystemConfiguration.sftp.host}
                  >
                    <Input />
                  </Form.Item>
                </Col>
                <Col span={11}>
                  <Form.Item
                    name="sftpPort"
                    label="Sftp Port"
                    initialValue={managementData.fileSystemConfiguration.sftp.port}
                  >
                    <Input />
                  </Form.Item>
                </Col>
                <Col span={11}>
                  <Form.Item
                    name="sftpUsername"
                    label="Sftp Username"
                    initialValue={managementData.fileSystemConfiguration.sftp.username}
                  >
                    <Input />
                  </Form.Item>
                </Col>
                <Col span={11}>
                  <Form.Item
                    name="sftpPassword"
                    label="Sftp Password"
                    initialValue={managementData.fileSystemConfiguration.sftp.password}
                  >
                    <Input type="password" />
                  </Form.Item>
                </Col>
              </Row>
            )}

            {storageType === "hdfs" && (
              <Row justify="space-between">
                <Col span={24}>
                  <Form.Item>
                    <InfoCircleFilled />
                    {managementData.fileSystemConfiguration.hdfs.description}
                  </Form.Item>
                </Col>
                <Col span={11}>
                  <Form.Item
                    name="hdfsHost"
                    label="Hdfs Host"
                    initialValue={managementData.fileSystemConfiguration.hdfs.host}
                  >
                    <Input />
                  </Form.Item>
                </Col>
                <Col span={11}>
                  <Form.Item
                    name="hdfsPort"
                    label="Hdfs Port"
                    initialValue={managementData.fileSystemConfiguration.hdfs.port}
                  >
                    <Input />
                  </Form.Item>
                </Col>
              </Row>
            )}
            {storageType === "standalone" && (
              <Col span={24}>
                <Form.Item>
                  <InfoCircleFilled />
                  {managementData.fileSystemConfiguration.standalone.description}
                </Form.Item>
              </Col>
            )}
          </Row>
        </Col>
      </Row>
      <Row gutter={16}>
        <Col span={24} style={{ textAlign: "end" }}>
          <Button type="primary" htmlType="submit" loading={isLoading}>
            Save
          </Button>
        </Col>
      </Row>
    </Form>
  );
};

export default Settings;
