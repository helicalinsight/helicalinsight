import { useEffect } from "react";
import { useSelector, useDispatch } from "react-redux";
import {
  Row,
  Col,
  Typography,
  Select,
  Input,
  Button,
  Form,
  Radio,
  Checkbox,
  Skeleton,
} from "antd";
import {
  toggleMetadataContents,
  storeMetadataGenerationFormValues,
  updateEntityId,
} from "../../../../../redux/actions/admin.actions";
import "../index.scss";
import { fetchMetadataGeneration } from "../utils/fetch-metadata-generation-api";

const { Title, Text } = Typography;
const { Option } = Select;

const HIMetaDataGeneration = () => {
  const isFetched = useSelector(
    (state) => state.admin.isFetched.metadataGeneration
  );
  const defaultValues = {};
  const metadataGenerationData = useSelector(
    (state) => state.admin.metadataGenerationData?.data
  );

  const dataWithSelectAndInput = metadataGenerationData?.filter(
    (item) => item.type === "select" || item.type === "inputBox"
  );
  const dataWithCheckbox = metadataGenerationData?.filter(
    (item) => item.type === "checkbox"
  );
  const dataWithRadio = metadataGenerationData?.filter(
    (item) => item.type === "radio"
  );

  isFetched &&
    dataWithSelectAndInput?.forEach((item) => {
      defaultValues[item.key] = item.options
        ? item.options[0].split("_#_")[0]
        : item.value;
    });
  isFetched &&
    dataWithCheckbox?.forEach((item) => {
      const value = item.options[0]?.split("_#_")[0];
      defaultValues[item.key] = [value];
    });
  isFetched &&
    dataWithRadio?.forEach((item) => {
      const value = item.options[0]?.split("_#_")[0];
      defaultValues[item.key] = value;
    });

  const dispatch = useDispatch();

  useEffect(() => {
    fetchMetadataGeneration(false, isFetched, dispatch);
  }, []);

  const onFinish = (values) => {
    dispatch(storeMetadataGenerationFormValues(values));
    dispatch(updateEntityId(values.entityId));
    dispatch(
      toggleMetadataContents({
        page: "metadataPreview",
        status: true,
      })
    );
  };

  const onFinishFailed = () => {};
  return (
    <Row justify="center">
      <Col>
        <Title level={4}>Metadata Generation</Title>
      </Col>
      <Col span={24}>
        <Skeleton active loading={!isFetched} paragraph={{ rows: 12 }}>
          <Form
            layout="vertical"
            name="basic"
            // labelCol={{
            //   span: 8,
            // }}
            // wrapperCol={{
            //   span: 16,
            // }}
            initialValues={defaultValues}
            onFinish={onFinish}
            onFinishFailed={onFinishFailed}
            autoComplete="off"
          >
            <Row className="hi-form" gutter={[8, 0]}>
              {dataWithSelectAndInput?.map((item) => {
                defaultValues[item.key] = item.options
                  ? item.options[0].split("_#_")[0]
                  : item.value;
                return (
                  <Col sm={24} md={12} key={item.key}>
                    <Form.Item
                      label={<Text strong>{item.label}</Text>}
                      name={item.key}
                      tooltip={
                        item.descs ? (
                          <span>
                            {item.desc ? item.desc : ""}
                            <br />
                            <span>
                              {item.descs?.map((item, index) => (
                                <>
                                  {index + 1}. {item}
                                  <br />
                                </>
                              ))}
                            </span>
                          </span>
                        ) : (
                          item.desc
                        )
                      }
                      // rules={[
                      //   {
                      //     required: true,
                      //     message: "Please input your username!",
                      //   },
                      // ]}
                    >
                      {item.type === "select" ? (
                        <Select>
                          {item.options.map((item) => {
                            const [value, label] = item.split("_#_");
                            return (
                              <Option key={value} value={value}>
                                {label}
                              </Option>
                            );
                          })}
                        </Select>
                      ) : item.type === "inputBox" ? (
                        <Input value={item.value}></Input>
                      ) : null}
                    </Form.Item>
                  </Col>
                );
              })}
              {dataWithRadio?.map((item) => (
                <Col key={item.key} sm={24} md={8}>
                  <Form.Item
                    label={<Text strong>{item.label}</Text>}
                    name={item.key}
                    tooltip={
                      item.descs ? (
                        <span>
                          {item.desc ? item.desc : ""}
                          <br />
                          <span>
                            {item.descs?.map((item, index) => (
                              <>
                                {index + 1}. {item}
                                <br />
                              </>
                            ))}
                          </span>
                        </span>
                      ) : (
                        item.desc
                      )
                    }
                    // rules={[
                    //   {
                    //     required: true,
                    //     message: "Please input your username!",
                    //   },
                    // ]}
                  >
                    <Radio.Group>
                      <Row>
                        {item.options.map((item) => {
                          const [value, label] = item.split("_#_");
                          return (
                            <Col key={value} span={24}>
                              <Form.Item
                                name={value}
                                valuePropName="checked"
                                // wrapperCol={{ offset: 8, span: 16 }}
                              >
                                <Radio value={value}>{label}</Radio>
                              </Form.Item>
                            </Col>
                          );
                        })}
                      </Row>
                    </Radio.Group>
                  </Form.Item>
                </Col>
              ))}
              {dataWithCheckbox?.map((item) => (
                <Col key={item.key} sm={24} md={8}>
                  <Form.Item
                    label={<Text strong>{item.label}</Text>}
                    name={item.key}
                    tooltip={
                      item.descs ? (
                        <span>
                          {item.desc ? item.desc : ""}
                          <br />
                          <span>
                            {item.descs?.map((item, index) => (
                              <>
                                {index + 1}. {item}
                                <br />
                              </>
                            ))}
                          </span>
                        </span>
                      ) : (
                        item.desc
                      )
                    }
                    // rules={[
                    //   {
                    //     required: true,
                    //     message: "Please input your username!",
                    //   },
                    // ]}
                  >
                    <Checkbox.Group>
                      <Row>
                        {item.options.map((item) => {
                          // will implement later the validation of item.options array
                          // if (item.split("_#_").length < 2) {
                          //   return null;
                          // }
                          const [value, label] = item.split("_#_");
                          return (
                            <Col key={value} span={24}>
                              <Form.Item
                                name={value}
                                valuePropName="checked"
                                // wrapperCol={{ offset: 8, span: 16 }}
                              >
                                <Checkbox value={value}>{label}</Checkbox>
                              </Form.Item>
                            </Col>
                          );
                        })}
                      </Row>
                    </Checkbox.Group>
                  </Form.Item>
                </Col>
              ))}
            </Row>
            <Row justify="end" gutter={[16, 16]}>
              <Col>
                <Button
                  onClick={() => {
                    dispatch(
                      toggleMetadataContents({
                        page: "metadataAdministration",
                        status: true,
                      })
                    );
                  }}
                >
                  Back
                </Button>
              </Col>
              <Col>
                <Button htmlType="submit">Generate Metadata</Button>
              </Col>
            </Row>
          </Form>
        </Skeleton>
      </Col>
      <Col span={24}></Col>
    </Row>
  );
};

export { HIMetaDataGeneration };
