import React, { useState, useEffect, useRef } from "react";
import { Typography, Input, Button, Form, Space, Checkbox, Row } from "antd";
import { useSelector } from "react-redux";
import { SelectedItemBreadcrumb } from ".";
import { capitalize } from "lodash";
const { Title } = Typography;

const EditFolderForm = ({
  onFormConfirm,
  title,
  onCancelClick,
  buttonText,
  addToRoot,
  setDrawerTitle,
  onDrawerSubmit,
  setOnDrawerSubmit,
  setLoading,
  action,
}) => {
  const [form] = Form.useForm();
  const clickedContextRecord = useSelector(
    (state) => state.fileBrowser.clickedContextItemDetails.clickedRecord
  );
  const editForm = useRef(null);
  // const layout = {
  //   labelCol: { span: 0 },
  //   wrapperCol: { span: 8 },
  // };

  const onSubmit = () => {
    setLoading(true);
    const { name, isPublic, isHidden } = form.getFieldsValue();
    onFormConfirm(name, clickedContextRecord, isPublic, isHidden);
    setOnDrawerSubmit(false);
  };

  useEffect(() => {
    if (title) {
      const tempTitle = !addToRoot ? title : "Add folder to root";
      setDrawerTitle(tempTitle);
    }
  }, []);

  useEffect(() => {
    if (editForm && editForm.current) {
      editForm.current.focus();
    }
    if (!addToRoot) {
      const defaultInput = clickedContextRecord
        ? clickedContextRecord.type === "file"
          ? clickedContextRecord.title
          : clickedContextRecord.name
        : "";
      form.setFieldsValue({
        name: defaultInput,
      });
    }
  }, [clickedContextRecord, title, addToRoot]);

  useEffect(() => {
    if (onDrawerSubmit) {
      form.submit();
    }
  }, [onDrawerSubmit]);

  useEffect(() => {
    if (action !== "add" && clickedContextRecord) {
      form.setFieldsValue({
        isPublic: clickedContextRecord.public ? clickedContextRecord.public : false,
      });
    }
  }, []);

  return (
    <React.Fragment>
      <Space direction="vertical" size="middle">
        {!addToRoot && <SelectedItemBreadcrumb pathItem={clickedContextRecord} action={action} />}
        <Form data-testid = "hi-file-browser-edit-form" form={form} name="control-hooks" onFinish={onSubmit}>
          <Form.Item
            label="Name"
            name="name"
            rules={[
              { required: true },
              ({ getFieldValue }) => ({
                validator(_, value) {
                  if (value && getFieldValue("name").length < 3) {
                    setOnDrawerSubmit(false);
                    return Promise.reject(
                      new Error("Name must be at least three characters long.")
                    );
                  }
                  if (value && getFieldValue("name").length > 60) {
                    setOnDrawerSubmit(false);
                    return Promise.reject(
                      new Error("Name must be less than sixty characters long.")
                    );
                  }
                  if (
                    value &&
                    getFieldValue("name").length >= 3 &&
                    getFieldValue("name").length <= 60 &&
                    !value.match(/^\w[\w \.\-\&\#\+\~]*/i)
                  ) {
                    setOnDrawerSubmit(false);
                    return Promise.reject(
                    new Error("Name should not starts with a special characters.")
                    );
                  }

                  return Promise.resolve();
                },
              }),
            ]}
          >
            <Input ref={editForm} />
          </Form.Item>
          <Row>
            <Form.Item name="isPublic" valuePropName="checked">
              <Checkbox onChange={(val) => form.setFieldsValue({ isPublic: val.target.checked })}>
                Public
              </Checkbox>
            </Form.Item>
            <Form.Item name="isHidden">
              <Checkbox
                // onChange={(val) =>
                //   form.setFieldsValue({ isHidden: val.target.checked })
                // }
                disabled
              >
                Hidden
              </Checkbox>
            </Form.Item>
          </Row>
          {/* <Form.Item>
            <Button type="primary" htmlType="submit" style={{ marginRight: 8 }}>
              {capitalize(buttonText)}
            </Button>
            <Button type="danger" htmlType="button" onClick={onCancelClick}>
              Cancel
            </Button>
          </Form.Item> */}
        </Form>
      </Space>
    </React.Fragment>
  );
};

export { EditFolderForm };
