import { useEffect } from "react";
import { useSelector, useDispatch } from "react-redux";
import { Row, Col, Form, Typography, Button, Input, Skeleton } from "antd";
import {
  updateIsFetched,
  toggleMetadataContents,
} from "../../../../../redux/actions/admin.actions";
import "../index.scss";
import { downloadMetadata } from "../utils/metadata-download-api";
import { isEmpty } from "lodash-es";
import { fetchMetadataPreview } from "../utils/fetch-metadata-preview-api";

const { Title, Text } = Typography;

const HIMetaDataPreview = () => {
  const isFetched = useSelector(
    (state) => state.admin.isFetched.metadataPreview
  );
  const entityId = useSelector((state) => state.admin.entityId);
  const metadataPreviewData = useSelector(
    (state) => state.admin.metadataPreviewData
  );
  const metadataGenerationFormValues = useSelector(
    (state) => state.admin.metadataGenerationFormValues
  );
  const formValuesCheck = !isEmpty(metadataGenerationFormValues);
  const dispatch = useDispatch();
  const defaultValues = isFetched && {
    metadata: metadataPreviewData?.metadata,
    configuration: metadataPreviewData?.configuration,
  };

  useEffect(() => {
    dispatch(updateIsFetched({ type: "metadataPreview", value: false }));
    fetchMetadataPreview(
      true,
      isFetched,
      dispatch,
      formValuesCheck,
      metadataGenerationFormValues,
      entityId
    );
  }, [entityId]);

  const onFinish = () => {};

  const onFinishFailed = () => {};

  return (
    <Row justify="center">
      <Col>
        <Title level={4}>Metadata Preview</Title>
      </Col>
      <Col span={24}>
        <Skeleton active loading={!isFetched} paragraph={{ rows: 12 }}>
          <Form
            layout="horizontal"
            labelCol={{ span: 5 }}
            labelAlign={"left"}
            wrapperCol={{ span: 14 }}
            initialValues={defaultValues}
            onFinish={onFinish}
            onFinishFailed={onFinishFailed}
          >
            <Row gutter={[8, 8]} className="hi-form" justify="start">
              <Col sm={24} md={12}>
                <Form.Item label={<Text strong>Local entity</Text>}>
                  {metadataPreviewData?.localEntity ? "Yes" : "No"}
                </Form.Item>
              </Col>
              <Col sm={24} md={12}>
                <Form.Item label={<Text strong>Entity ID</Text>}>
                  {metadataPreviewData?.entityId}
                </Form.Item>
              </Col>
              <Col sm={24} md={12}>
                <Form.Item label={<Text strong>Empty alias</Text>}>
                  {metadataPreviewData?.emptyAlias
                    ? metadataPreviewData?.emptyAlias
                    : "-"}
                </Form.Item>
              </Col>
              <Col sm={24} md={12}>
                <Form.Item label={<Text strong>Signing key</Text>}>
                  {metadataPreviewData?.signingKey
                    ? metadataPreviewData?.signingKey
                    : "-"}
                </Form.Item>
              </Col>
              <Col sm={24} md={12}>
                <Form.Item label={<Text strong>Encryption key</Text>}>
                  {metadataPreviewData?.encryptionKey
                    ? metadataPreviewData?.encryptionKey
                    : "-"}
                </Form.Item>
              </Col>
              <Col span={24}>
                <Form.Item
                  labelCol={{ span: 2 }}
                  wrapperCol={{ span: 22 }}
                  label={<Text strong>Metadata</Text>}
                  name="metadata"
                >
                  <Input.TextArea rows={7}></Input.TextArea>
                </Form.Item>
              </Col>
              <Col span={24}>
                <Form.Item
                  labelCol={{ span: 2 }}
                  wrapperCol={{ span: 22 }}
                  label={<Text strong>Configuration</Text>}
                  name="configuration"
                >
                  <Input.TextArea rows={7}></Input.TextArea>
                </Form.Item>
              </Col>
            </Row>
            <Col span={24}>
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
                  <Button
                    onClick={() => {
                      downloadMetadata({
                        dispatch,
                        succesCB: () => {},
                        errorCB: () => {},
                      });
                    }}
                  >
                    Download Entity Metadata
                  </Button>
                </Col>
              </Row>
            </Col>
          </Form>
        </Skeleton>
      </Col>
    </Row>
  );
};
export { HIMetaDataPreview };
