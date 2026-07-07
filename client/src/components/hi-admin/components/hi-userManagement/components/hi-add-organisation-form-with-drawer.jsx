import React from "react";
import requests from "../../../../../base/requests";
import { useDispatch, useSelector } from "react-redux";
import {
  addOrgItem,
  updateVisibleDrawers,
} from "../../../../../redux/actions/admin.actions";
import { Drawer, Form, Input, Button } from "antd";
import { orgItem } from "../utils";
import notify from "../../../../hi-notifications/notify";
import { fetchRolesData, fetchOrganizationData } from "../helperMethods";
import { Typography } from "antd";
import "../index.scss";

const { Paragraph } = Typography;

const HIAddOrgFormWithDrawer = (props) => {
  const orgData = useSelector((state) => state.admin.orgData);
  const visible = useSelector((state) => state.admin.visibleDrawersUM.addOrg);
  const { getOrgId } = props;
  const dispatch = useDispatch();
  const Notify = notify(dispatch);
  const addOrgFormAPI = (values) => {
    orgItem({
      dispatch,
      requests,
      data: {
        action: "add",
        formData: { ...values },
      },
      successCB: (res) => {
        fetchOrganizationData(dispatch, true);
        // Notify.success({ ...res, type: "Network Call" });
        form.resetFields();
        dispatch(
          addOrgItem({
            slno: orgData.length + 1,
            ...res.data,
          })
        );
        dispatch(updateVisibleDrawers({ key: "addOrg", status: false }));
        getOrgId(res.data.id);
        fetchRolesData(dispatch, true);
      },
      errorCB: () => {
        // Notify.error({ ...e, type: "Network Call" });
        form.resetFields();
        dispatch(updateVisibleDrawers({ key: "addOrg", status: false }));
      },
    });
  };
  const [form] = Form.useForm();

  return (
    <Drawer
      title={<span className="hi-drawer-title">Add Organization</span>}
      placement="right"
      width="30%" // #4595 - fix
      className="my-drawer"
      visible={visible}
      onClose={() => {
        form.resetFields();
        dispatch(updateVisibleDrawers({ key: "addOrg", status: false }));
      }}
    >
      <Form
        layout="vertical"
        initialValues={{
          modifier: "public",
        }}
        onFinish={(values) => {
          addOrgFormAPI(values);
        }}
        form={form}
      >
        <Form.Item
          rules={[
            {
              required: true,
              message:
                "Organization name can only use A-Z, a-z, 0-9, . and _ and cannot have spaces",
            },
            ({ getFieldValue }) => ({
              validator(_, value) {
                if (
                  value &&
                  getFieldValue("name").length > 3 &&
                  getFieldValue("name").length > 60
                ) {
                  return Promise.reject(
                    new Error("Organization name not more than 60 characters")
                  );
                }

                if (value && getFieldValue("name").length < 3) {
                  return Promise.reject(
                    new Error(
                      "Please provide at least three characters for Organization Name!"
                    )
                  );
                }
                // #4187 - start
                if (
                  value &&
                  getFieldValue("name").length >= 3 &&
                  getFieldValue("name").length <= 60 &&
                  !value.match(/^[a-zA-Z0-9._]+$/)
                ) {
                  return Promise.reject(
                    new Error(
                      "Organization name can only use A-Z, a-z, 0-9, . and _ and cannot have spaces"
                    )
                  );
                }
                // #4187 - end

                return Promise.resolve();
              },
            }),
          ]}
          label="Name"
          name="name"
        >
          <Input placeholder="Organization" />
        </Form.Item>
        <Form.Item
          rules={[
            {
              required: true,
              message: "Please enter a description for the organization",
            },
            ({ getFieldValue }) => ({
              validator(_, value) {
                if (value && getFieldValue("description").length > 250) {
                  return Promise.reject(
                    new Error(
                      "Organization description not more than 250 characters"
                    )
                  );
                }
                return Promise.resolve();
              },
            }),
          ]}
          label="Description"
          name="description"
        >
          <Input.TextArea />
        </Form.Item>
        {
          // #4207 - start
          <Form.Item>
            <Paragraph className="hi-important-info" type="danger">
              * Mandatory fields
            </Paragraph>
          </Form.Item>
          // #4207 - end
        }
        <Form.Item>
          <Button
            data-testid="hi-add-org-submit"
            htmlType="submit"
            type="primary"
          >
            Save
          </Button>
        </Form.Item>
      </Form>
    </Drawer>
  );
};
export { HIAddOrgFormWithDrawer };
