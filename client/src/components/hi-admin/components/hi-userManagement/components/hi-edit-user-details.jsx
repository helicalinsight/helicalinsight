import { useMemo, useState, useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  Form,
  Drawer,
  Row,
  Button,
  Divider,
  Input,
  Checkbox,
  Select,
  Col,
  Space,
  Tooltip,
  Anchor,
} from "antd";
import requests from "../../../../../base/requests";
import { updateVisibleDrawers } from "../../../../../redux/actions/admin.actions";
import {
  HIAddOrgFormWithDrawer,
  HIAddProfileFormWithDrawer,
  HIAddRoleFormWithDrawer,
  HIProfileTable,
  HIRoleCheckboxTable,
} from ".";
import { userItem, roleItem } from "../utils";
import { InfoCircleOutlined } from "@ant-design/icons";
import notify from "../../../../hi-notifications/notify";
import { fetchRolesData, fetchUsersData } from "../helperMethods";
import { Typography } from "antd";

const { Paragraph } = Typography;
const { Link } = Anchor;

const HIEditUserDetails = () => {
  const visible = useSelector((state) => state.admin.visibleDrawersUM.editUser);
  const orgData = useSelector((state) => state.admin.orgData);
  const userData = useSelector((state) => state.admin.userData);
  const dispatch = useDispatch();
  const [roleIds, setRoleIds] = useState([]);
  const editUser = useSelector((state) => state.admin.editUser);
  const { type, userId } = editUser;
  const activeRecord = userData?.find((item) => item.id === userId);
  const { applicationSettingsData } = useSelector((state) => state.app);
  const organization = applicationSettingsData.userData.user?.organization;
  const Notify = notify(dispatch);

  const updateUserFormAPI = (values) => {
    switch (type) {
      case "all":
        userItem({
          dispatch,
          requests,
          data: {
            action: "update",
            id: activeRecord?.id,
            formData: {
              id: activeRecord?.id,
              name: activeRecord?.name,
              email: values?.email,
              enabled: values?.enabled,
              // roleIds: !roleIds.length ? activeRecordRoleIds : roleIds,
              roleIds,
              password: values?.password ? values?.password : "",
            },
          },
          successCB: (res) => {
            // Notify.success({ ...res, type: "Network Call" }, dispatch);
            fetchUsersData(dispatch, true);
            form.resetFields();
            dispatch(updateVisibleDrawers({ key: "editUser", status: false }));
          },
          errorCB: (e) => {
            // Notify.error({ ...e, type: "Network Call" });
          },
        });

        break;
      case "user":
        const { password, email, enabled } = values;
        userItem({
          dispatch,
          requests,
          data: {
            action: "update",
            id: activeRecord?.id,
            formData: {
              id: activeRecord?.id,
              name: activeRecord?.name,
              password: password ? password : "", // #4200 - fix
              email,
              enabled,
            },
          },
          successCB: (res) => {
            // Notify.success({ ...res, type: "Network Call" }, dispatch);
            fetchUsersData(dispatch, true);
            form.resetFields();
            dispatch(updateVisibleDrawers({ key: "editUser", status: false }));
          },
          errorCB: (e) => {
            // Notify.error({ ...e, type: "Network Call" });
            dispatch(updateVisibleDrawers({ key: "editUser", status: false }));
          },
        });

        break;
      case "role":
        roleItem({
          requests,
          dispatch,
          data: {
            action: "userRoles",
            userId: activeRecord?.id,
            // roleIds: !roleIds.length ? activeRecordRoleIds : roleIds,
            roleIds, // #4178 fix
            organisation: activeRecord?.orgName,
          },
          successCB: (res) => {
            form.resetFields();
            // Notify.success({ ...res, type: "Network Call" });

            dispatch(updateVisibleDrawers({ key: "editUser", status: false }));
            fetchUsersData(dispatch, true);
            fetchRolesData(dispatch, true);
          },
          errorCB: (e) => {
            // Notify.error({ ...e, type: "Network Call" });
          },
        });
        break;
      default:
        break;
    }
  };

  const [form] = Form.useForm();

  const defaultValues = useMemo(() => {
    return {
      name: activeRecord?.name,
      email: activeRecord?.email,
      organisation: activeRecord?.organisation ? activeRecord.organisation : -1,
      enabled: activeRecord?.enabled,
    };
  }, [activeRecord]);

  const getRoleIds = (roleIds) => {
    setRoleIds(roleIds);
  };
  // fix for bug-6315
  // useEffect(() => {
  //   form.setFieldsValue(defaultValues);
  // }, [form, defaultValues]);

  const handleClick = (e, link) => {
    e.preventDefault();
    document.getElementById(link?.title?.toLowerCase()).scrollIntoView(true);
  };

  return (
    <Drawer
      data-testid="hi-edit-user-details-edit-icon"
      visible={visible}
      onClose={() => {
        form.resetFields();
        dispatch(updateVisibleDrawers({ key: "editUser", status: false }));
      }}
      title={
        <span className="hi-drawer-title">
          Edit
          {
            // #4200 fix
            ` ${activeRecord?.name} ${type.toLowerCase()} `
          }
          details
        </span>
      }
      placement="right"
      width={"60%"}
      className="my-drawer hi-add-user-form hi-um-edit-drawer"
    >
      <section
        id="hi-um-edit-all"
        // className="hi-um-edit-all-scrolling-container"
        style={{ height: "100%", overflowY: "auto", overflowX: "hidden" }}
      >
        <Form
          layout="vertical"
          initialValues={defaultValues}
          form={form}
          onFinish={updateUserFormAPI}
        >
          <Row
            gutter={type === "all" ? [2, 2] : [4, 4]}
            className="hi-user-management"
          >
            <Col span={type === "all" ? 21 : 23}>
              {type === "all" || type === "user" ? (
                <section id="user">
                  <Row
                    gutter={[16, 16]}
                    className="hi-user-name-container"
                    align="middle"
                    justify="center"
                  >
                    <Col span={24} />
                    {type === "all" ? (
                      <Divider orientation="left">User</Divider>
                    ) : null}

                    <Col span={12}>
                      <Form.Item
                        label="New Password"
                        name="password"
                        hasFeedback
                        rules={[
                          ({ getFieldValue }) => ({
                            validator(_, value) {
                              // #4190 -start
                              if (
                                value &&
                                getFieldValue("password").length < 6 &&
                                getFieldValue("password").length <= 60
                              ) {
                                return Promise.reject(
                                  new Error("Password at least six characters!")
                                );
                              }
                              if (
                                value &&
                                getFieldValue("password").length > 60
                              ) {
                                return Promise.reject(
                                  new Error(
                                    "Password not more than 60 characters"
                                  )
                                );
                              }
                              // #4190 -end
                              return Promise.resolve();
                            },
                          }),
                        ]}
                      >
                        <Input.Password disabled ={activeRecord.isExternalUser}/>
                      </Form.Item>
                    </Col>
                    <Col span={12}>
                      <Form.Item
                        name="email"
                        label="Email"
                        rules={[
                          {
                            required: true,
                            message: "Please enter a valid email address", // #4191 fix
                          },
                          {
                            type: "email",
                            message: "Please enter a valid email address", // #4191 fix
                          },
                        ]}
                      >
                        <Input placeholder="Enter email address" />
                      </Form.Item>
                    </Col>

                    <Col span={12}>
                      <Form.Item
                        label="Confirm Password"
                        name="confirmpassword"
                        dependencies={["password"]}
                        hasFeedback
                        rules={[
                          ({ getFieldValue }) => ({
                            validator(_, value) {
                              if (getFieldValue("password") === value) {
                                return Promise.resolve();
                              }

                              return Promise.reject(
                                new Error("Passwords do not match!")
                              );
                            },
                          }),
                        ]}
                      >
                        <Input.Password disabled ={activeRecord.isExternalUser}/>
                      </Form.Item>
                    </Col>
                    <Col span={12}>
                      <Form.Item
                        label="   "
                        name="enabled"
                        valuePropName="checked"
                      >
                        <Checkbox>Enabled</Checkbox>
                      </Form.Item>
                    </Col>
                    {
                      // #4207 - start
                      <Col span={24}>
                        <Form.Item>
                          <Paragraph
                            className="hi-important-info"
                            type="danger"
                          >
                            * Mandatory fields
                          </Paragraph>
                          <Paragraph
                            className="hi-important-info"
                            type="danger"
                          >
                            Note: Please leave password field blank if you do
                            not want to update
                          </Paragraph>
                        </Form.Item>
                      </Col>
                      // #4207 - end
                    }
                    {type === "user" ? null : (
                      <>
                        <Col span={organization ? 24 : 12}>
                          <Form.Item
                            wrapperCol={organization ? { span: 12 } : null}
                            name="organisation"
                            label="Organization"
                          >
                            <Select
                              disabled={true}
                              placeholder="--Please select an organization--"
                              allowClear
                            >
                              <Select.Option key={"default_value"} value={-1}>
                                --Please select an organization--
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

                        {
                          // #4200 - start
                          organization ? null : (
                            <Col span={12}>
                              <Form.Item label="   ">
                                <Button
                                  onClick={() => {
                                    dispatch(
                                      updateVisibleDrawers({
                                        key: "addOrg",
                                        status: true,
                                      })
                                    );
                                  }}
                                  type="primary"
                                  className="hi-um-add-org-button"
                                >
                                  Add new Organization
                                </Button>
                              </Form.Item>
                              {visible && <HIAddOrgFormWithDrawer />}
                            </Col>
                          )
                          // #4200 - end
                        }
                      </>
                    )}
                  </Row>
                </section>
              ) : null}

              {type === "all" || type === "role" ? (
                <section id="role">
                  <Row gutter={[8, 8]} className="hi-user-role-container">
                    {type === "all" ? (
                      <Divider orientation="left">Role</Divider>
                    ) : null}
                    <Col span={23}></Col>
                    <Col span={1}>
                      <Tooltip placement="left" title="Click on table selections to the left of ID to add a role">
                        <InfoCircleOutlined />
                      </Tooltip>
                    </Col>
                    <Col span={23}>
                      <Form.Item>
                        {visible && (
                          <HIRoleCheckboxTable getRoleIds={getRoleIds} />
                        )}
                      </Form.Item>
                    </Col>

                    <HIAddRoleFormWithDrawer
                      record={activeRecord}
                      editRole={true}
                    />
                  </Row>
                </section>
              ) : null}
              {type === "all" || type === "profile" ? (
                <section id="profile">
                  <Row gutter={[8, 8]} className="hi-user-profile-container">
                    {type === "all" ? (
                      <Divider orientation="left">Profile</Divider>
                    ) : null}
                    <Col span={23}>{visible && <HIProfileTable />}</Col>
                    <Col span={1}>
                      <Tooltip placement='left' title="Click on table selections to the left of S No to add a profile">
                        <InfoCircleOutlined />
                      </Tooltip>
                    </Col>

                    <Col span={24}>
                      <HIAddProfileFormWithDrawer />
                    </Col>
                  </Row>
                </section>
              ) : null}
              {type !== "profile" && (
                <Form.Item>
                  <Button htmlType="submit" type="primary">
                    Save
                  </Button>
                </Form.Item>
              )}
            </Col>
            {type === "all" ? (
              <Col span={3}>
                <Space className="hi-drawer-menu">
                  <Anchor
                    getContainer={() =>
                      document.getElementById("hi-um-edit-all")
                    }
                    targetOffset={window.innerHeight / 2}
                    onClick={handleClick}
                  >
                    <Link
                      // href="/#/admin/usermanagement/users/#user"
                      href="/#user"
                      title="User"
                    />
                    <Link
                      // href="/#/admin/usermanagement/users/#role"
                      href="/#role"
                      title="Role"
                    />
                    <Link
                      href="/#profile"
                      // href="/#/admin/usermanagement/users/#profile"
                      title="Profile"
                    />
                  </Anchor>
                </Space>
              </Col>
            ) : null}
          </Row>
        </Form>
      </section>
    </Drawer>
  );
};

export { HIEditUserDetails };
