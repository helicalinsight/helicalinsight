import React from "react";
import requests from "../../../../../base/requests";
import { useDispatch, useSelector } from "react-redux";
import {
  addRoleItem,
  updateVisibleDrawers,
} from "../../../../../redux/actions/admin.actions";
import { Drawer, Form, Input, Button, Select } from "antd";
import notify from "../../../../hi-notifications/notify";
import { roleItem } from "../utils";
import { Typography } from "antd";
import "../index.scss";

const { Paragraph } = Typography;

const HIAddRoleFormWithDrawer = (props) => {
  const visible = useSelector((state) => state.admin.visibleDrawersUM.addRole);
  const roleData = useSelector((state) => state.admin.roleData);
  const orgData = useSelector((state) => state.admin.orgData);
  // #4196 -start
  const { applicationSettingsData } = useSelector((state) => state.app);
  const organization = applicationSettingsData?.userData.user?.organization;
  const organizationInOrgData = orgData?.find(
    (item) => item?.name === organization
  );
  // #4196 -end
  const dispatch = useDispatch();
  const { editRole, record } = props;
  const Notify = notify(dispatch);

  const addRoleFormAPI = (values) => {
    roleItem({
      dispatch,
      requests,
      data: {
        action: "add",
        formData: {
          ...values,
          // 4196 -start
          organisation: editRole
            ? record?.organisation === "" || !record?.organisation
              ? -1
              : record?.organisation
            : values.organisation
              ? values.organisation
              : -1,
          // 4196 -end
        },
      },
      successCB: (res) => {
        // Notify.success({ ...res, type: "Network Call" });
        form.resetFields();
        dispatch(addRoleItem({ slno: roleData.length + 1, ...res.data }));
        dispatch(updateVisibleDrawers({ key: "addRole", status: false }));
      },
      errorCB: (e) => {
        dispatch(updateVisibleDrawers({ key: "addRole", status: false }));
        form.resetFields();
        // Notify.error({ ...e, type: "Network Call" });
      },
    });
  };

  const [form] = Form.useForm();

  return (
    <Drawer
    data-testid = "hi-add-role-form-with-drawer"
      title={<span className="hi-drawer-title">Add Role</span>}
      placement="right"
      className="my-drawer"
      width={"30%"} // #4596 - fix
      visible={visible}
      onClose={() => {
        form.resetFields();
        dispatch(updateVisibleDrawers({ key: "addRole", status: false }));
      }}
    // footer={
    //   <Button
    //     htmlType="submit"
    //     type="primary"
    //     // disabled={disabledButtonCheck}
    //     onClick={() => {
    //       submitOrgForm();
    //       setIsLoading(true);
    //       onClose();
    //     }}
    //   >
    //     Save
    //   </Button>
    // }
    >
      <Form
        layout="vertical"
        initialValues={{
          modifier: "public",
          // #4196 -start
          organisation: organization ? organizationInOrgData?.id : -1,
          // #4196 -end
        }}
        onFinish={(values) => {
          addRoleFormAPI(values);
        }}
        form={form}
      >
        {editRole ? null : (
          <Form.Item name="organisation" label="Organization">
            <Select
              disabled={organization}
              placeholder="--Search organization--"
              allowClear
              showSearch 
                filterOption={(input,option)=>
                option.children
                .toLowerCase()
                .indexOf(input.toLowerCase()) >= 0 
              }
            >
              <Select.Option key={"default_value"} value={-1}>
                --Search organization--
              </Select.Option>
              {orgData?.length > 0 &&
                orgData?.map((item) => (
                  <Select.Option key={item.id} value={item.id}>
                    {item.name}
                  </Select.Option>
                ))}
            </Select>
          </Form.Item>
        )}
        <Form.Item
          rules={[
            {
              required: true,
              message:
                "Role name can only use A-Z, a-z, 0-9, ., —,–,$, _, @, ,, &, - , /, !, +, and can have spaces",
            },
            ({ getFieldValue }) => ({
              validator(_, value) {
                if (value && getFieldValue("name").length < 3) {
                  return Promise.reject(
                    new Error("Role name at least three characters")
                  );
                }
                if (
                  value &&
                  getFieldValue("name").length > 3 &&
                  getFieldValue("name").length > 60
                ) {
                  return Promise.reject(
                    new Error("Role name not more than 60 characters")
                  );
                }
                // #4179 - start
                if (
                  value &&
                  getFieldValue("name").length >= 3 &&
                  getFieldValue("name").length <= 60 &&
                  !value.match(/^[A-Za-z0-9.@—\$–,_&\/!+ ]+$/)
                ) {
                  return Promise.reject(
                    new Error(
                      "Role name can only use A-Z, a-z, 0-9, .,—,–, $, _, @, ,, &, - , /, !, +, and can have spaces"
                    )
                  );
                  // #4179 - end
                }
                return Promise.resolve();
              },
            }),
          ]}
          label="Name"
          name="name"
        >
          <Input placeholder="Role Name" />
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
            data-testid="hi-add-role-submit"
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
export { HIAddRoleFormWithDrawer };
