import React from "react";
import requests from "../../../../../base/requests";
import { useDispatch, useSelector } from "react-redux";
import {
  updateVisibleDrawers,
  addProfileItem,
} from "../../../../../redux/actions/admin.actions";
import { Drawer, Form, Input, Button } from "antd";
import { profileItem } from "../utils";
import notify from "../../../../hi-notifications/notify";

const HIAddProfileFormWithDrawer = () => {
  const userData = useSelector((state) => state.admin.userData);
  const visible = useSelector(
    (state) => state.admin.visibleDrawersUM.addProfile
  );
  const dispatch = useDispatch();
  const editUser = useSelector((state) => state.admin.editUser);
  const { userId } = editUser;
  const activeRecord = userData?.filter((item) => item.id === userId)[0];
  const Notify = notify(dispatch);

  const addProfileFormAPI = (values) => {
    profileItem({
      dispatch,
      requests,
      data: {
        action: "add",
        formData: { ...values, id: activeRecord.id },
      },
      successCB: (res) => {
        // Notify.success({ ...res, type: "Network Call" });
        form.resetFields();
        dispatch(updateVisibleDrawers({ key: "addProfile", status: false }));
        dispatch(
          addProfileItem({
            userId: activeRecord.id,
            profileItem: { id: res.id, ...values },
          })
        );
      },
      errorCB: (e) => {
        // Notify.error({ ...e, type: "Network Call" });
        dispatch(updateVisibleDrawers({ key: "addProfile", status: false }));
      },
    });
  };
  const [form] = Form.useForm();

  return (
    <Drawer
    data-testid ="hi-add-profile-with-drawer"
      title={<span className="hi-drawer-title">Add Profile</span>}
      placement="right"
      width={"30%"}
      className="my-drawer"
      visible={visible}
      onClose={() => {
        form.resetFields();
        dispatch(updateVisibleDrawers({ key: "addProfile", status: false }));
      }}
    >
      <Form
        layout="vertical"
        initialValues={{
          modifier: "public",
        }}
        onFinish={addProfileFormAPI}
        form={form}
      >
        <Form.Item
          rules={[
            {
              required: true,
              message:
                "Profile name can only use A-Z, a-z, @, 0-9, ,,. and _ and spaces",
            },
            ({ getFieldValue }) => ({
              validator(_, value) {
                if (value && getFieldValue("name").length < 3) {
                  return Promise.reject(
                    new Error("Profile name at least three characters long")
                  );
                }
                if (
                  value &&
                  getFieldValue("name").length >= 3 &&
                  getFieldValue("name").length <= 60 &&
                  !value.match(/^[a-zA-Z0-9@,._\s]+$/)
                ) {
                  return Promise.reject(
                    new Error(
                      "Profile name can only use A-Z, a-z, @, 0-9, ,, ., _ and spaces"
                    )
                  );
                }
                return Promise.resolve();
              },
            }),
          ]}
          label="Name"
          name="name"
          wrapperCol={{ span: 12 }}
        >
          <Input placeholder="Profile Name" />
        </Form.Item>
        <Form.Item
          rules={[
            {
              required: true,
              message:
                "Profile value can only use A-Z, a-z, @, 0-9, ,, .,`,' and _ and spaces",
            },
            () => ({
              validator(_, value) {
                if (value && !value.match(/^[a-zA-Z0-9@`',._\s]+$/)) {
                  return Promise.reject(
                    new Error(
                      "Profile value can only use A-Z, a-z, @, 0-9, ,, ., `,', _ and spaces"
                    )
                  );
                }
                return Promise.resolve();
              },
            }),
          ]}
          label="Value"
          name="value"
          wrapperCol={{ span: 12 }}
        >
          <Input.TextArea />
        </Form.Item>
        <Form.Item data-testid ="hi-add-profile-with-drawer-save-btn">
          <Button  htmlType="submit" type="primary">
            Save
          </Button>
        </Form.Item>
      </Form>
    </Drawer>
  );
};
export { HIAddProfileFormWithDrawer };
