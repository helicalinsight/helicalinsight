import { useState, useEffect, useMemo } from "react";
import { useDispatch, useSelector } from "react-redux";
import { Form, Drawer, Row, Button, Input, Select, Col, Checkbox } from "antd";
import requests from "../../../../../base/requests";
import {
  updateVisibleDrawers,
  addUserItem,
} from "../../../../../redux/actions/admin.actions";
import { HIAddOrgFormWithDrawer } from ".";
import { userItem } from "../utils";
import notify from "../../../../hi-notifications/notify";
import { Typography } from "antd";
import "../index.scss";
import { fetchRolesData } from "../helperMethods";

const { Paragraph } = Typography;

const HIAddUserFormWithDrawer = ({ setIsLoading }) => {
  const visible = useSelector((state) => state.admin.visibleDrawersUM.addUser);
  const orgData = useSelector((state) => state.admin.orgData);
  const newOrgItem = useSelector((state) => state.admin.newOrgItem);
  const userData = useSelector((state) => state.admin.userData);
  const dispatch = useDispatch();
  const [orgId, setOrgId] = useState(""); // #4597 - fix
  const Notify = notify(dispatch);
  // #4195 - start
  const { applicationSettingsData } = useSelector((state) => state.app);
  const organization = applicationSettingsData?.userData.user?.organization;
  const organizationInOrgData = orgData?.find(
    (item) => item.name === organization
  );
  // #4195 - end
  const addUserFormAPI = (values) => {
    userItem({
      dispatch,
      requests,
      data: {
        action: "add",
        formData: {
          id: "",
          email: values?.email,
          name: values?.name,
          enabled: values?.enabled,
          password: values?.password,
          organisation: values?.organisation === -1 ? "" : values?.organisation,
        },
      },
      successCB: (res) => {
        // form.resetFields();
        // Notify.success({ ...res, type: "Network Call" });
        dispatch(updateVisibleDrawers({ key: "addUser", status: false }));
        dispatch(
          addUserItem({
            slno: userData.length + 1,
            ...res.data,
          })
        );
        fetchRolesData(dispatch, true, () => {
          setIsLoading((state) => ({
            ...state,
            roleTable: false,
            refreshBtn: false,
          }));
        });
      },
      errorCB: (e) => {
        // Notify.error({ ...e, type: "Network Call" });
      },
    });
  };

  const getOrgId = (id) => {
    setOrgId(id); // #4597 - fix
  };

  const [form] = Form.useForm();
  const { setFieldValue } = form;

  // const inititalValues = useMemo(
  //   () => ({
  //     enabled: true,
  //     organisation: organization
  //       ? organizationInOrgData
  //         ? organizationInOrgData?.id
  //         : ""
  //       : orgId, // #4195 - fix & #4597 - fix
  //   }),
  //   [orgId, organization, organizationInOrgData]
  // );

  useEffect(() => {
    setFieldValue(
      "organisation", organization
      ? organizationInOrgData
        ? organizationInOrgData?.id
        : ""
      : orgId, // #4195 - fix & #4597 - fix,
    )
  }, [orgId, organization, organizationInOrgData])

  const initialValues = {
     enabled: true,
    organisation: organization ? organizationInOrgData?.id : -1
   };


  useEffect(() => {
    visible && form.resetFields();
  }, [visible]);

  // useEffect(() => {
  //   form.setFieldsValue(inititalValues);
  // }, [form, inititalValues]);

  useEffect(() => {
    if (newOrgItem) {
      form.setFieldValue('organisation', newOrgItem.id);
    }
  }, [newOrgItem]);

  return (
    <Drawer
    data-testid = "hi-add-user-form-with-drawer"
      visible={visible}
      onClose={() => {
        form.resetFields();
        dispatch(updateVisibleDrawers({ key: "addUser", status: false }));
      }}
      title={<span className="hi-drawer-title">Add User</span>}
      placement="right"
      width={"60%"}
      className="my-drawer hi-add-user-form"
    >
      <Form
        layout="vertical"
        initialValues={initialValues}
        form={form}
        onFinish={addUserFormAPI}
      >
        <Row gutter={[8, 8]} align="middle">
          <Col span={12}>
            <Form.Item
              name="name"
              label="Username"
              rules={[
                {
                  required: true,
                  message:
                    "Username can only use A-Z, a-z, 0-9, @, —,–,$, -, _, ', &, ., and can have spaces",
                },
                ({ getFieldValue }) => ({
                  validator(_, value) {
                    if (value && getFieldValue("name").length > 120) {
                      return Promise.reject(
                        new Error("Username not more than 120 characters!")
                      );
                    }
                    else if (
                      value &&
                      getFieldValue("name").length <= 120 &&
                      !value.match(/^[A-Za-z0-9.@—\$–,_&\/!+ ]+$/)
                    ) {
                      return Promise.reject(
                        new Error(
                          "Username can only use A-Z, a-z, 0-9, @, —,–,$, &, _, -, ', ., and can have spaces"
                        ) // #4189 fix
                      );
                    }
                    return Promise.resolve();
                  },
                }),
              ]}
            >
              <Input placeholder="Username" />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item
              name="email"
              label="Email"
              rules={[
                {
                  required: true,
                  message: "Please enter a valid email address",
                },
                {
                  type: "email",
                  message: "Please enter a valid email address",
                },
              ]}
            >
              <Input placeholder="Enter email address" />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item
              label="Password"
              name="password"
              hasFeedback
              rules={[
                {
                  required: true,
                  message: "Password at least six characters",
                },
                ({ getFieldValue }) => ({
                  validator(_, value) {
                    if (value && getFieldValue("password").length < 6) {
                      return Promise.reject(
                        new Error("Password at least six characters!")
                      );
                    }
                    if (
                      value &&
                      getFieldValue("password").length > 6 &&
                      getFieldValue("password").length > 60
                    ) {
                      return Promise.reject(
                        new Error("Password not more than 60 characters")
                      );
                    }
                    if (/[?%/]/.test(value)) {
                      return Promise.reject(
                        new Error("Password cannot contain %, ?, or /")
                      );
                    }
                    return Promise.resolve();
                  },
                }),
              ]}
            >
              <Input.Password />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item
              label="Confirm Password"
              name="confirmpassword"
              dependencies={["password"]}
              hasFeedback
              rules={[
                {
                  required: true,
                  message: "Passwords do not match!",
                },
                ({ getFieldValue }) => ({
                  validator(_, value) {
                    if (!value || getFieldValue("password") === value) {
                      return Promise.resolve();
                    }

                    return Promise.reject(new Error("Passwords do not match!"));
                  },
                }),
              ]}
            >
              <Input.Password />
            </Form.Item>
          </Col>

          <Col span={12}>
            <Form.Item name="organisation" label="Organization">
              <Select
                disabled={organization}
                placeholder="--Search organization--"
                defaultValue="--Search organization--"
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
          </Col>

          {organization ? null : (
            <Col span={12}>
              {visible && <HIAddOrgFormWithDrawer getOrgId={getOrgId} />}
              <Form.Item label={"  "}>
                <Button
                  onClick={() => {
                    dispatch(
                      updateVisibleDrawers({ key: "addOrg", status: true })
                    );
                  }}
                  type="primary"
                  className="hi-um-add-org-button"
                >
                  Add new Organization
                </Button>
              </Form.Item>
            </Col>
          )}
          <Col span={24}>
            <Form.Item
              name="enabled"
              valuePropName="checked"
              defaultChecked={true}
            >
              <Checkbox>Enabled</Checkbox>
            </Form.Item>
          </Col>
          {
            // #4207 - start
            <Col span={24}>
              <Form.Item>
                <Paragraph className="hi-important-info" type="danger">
                  * Mandatory fields
                </Paragraph>
              </Form.Item>
            </Col>
            // #4207 - end
          }

          <Col span={12}>
            <Form.Item>
              <Button
                data-testid="hi-add-user-form"
                htmlType="submit"
                type="primary"
              >
                Save
              </Button>
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </Drawer>
  );
};
export { HIAddUserFormWithDrawer };
