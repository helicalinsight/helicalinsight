import React, { useState, useEffect, useMemo } from "react";
import { useSelector, useDispatch } from "react-redux";
import {
  convertToBase64,
  getPermissionLevelsText,
} from "../../../utils/utilities";
import requests from "../../../base/requests";
import { fileBrowserActions } from "../../../redux/actions";
import {
  Table,
  Typography,
  Tooltip,
  Row,
  Col,
  Modal,
  Tabs,
  Select,
  Form,
  Skeleton,
} from "antd";
import {
  fetchUsersData,
  fetchRolesData,
  fetchOrganizationData,
} from "../../hi-admin/components/hi-userManagement/helperMethods";
import { ShareAltOutlined } from "@ant-design/icons";
import notify from "../../hi-notifications/notify.js";

const { Text } = Typography;
const { Option } = Select;
const { TabPane } = Tabs;

//TODO
// 1. Make table virtualized
// 2. Change UI for columns when validating
// 3. Write test cases for utility functions used
// 4. Success/Fail notification on sharing
// 5. Loader on tab click

const ShareTable = ({
  columns,
  data,
  shareOptions,
  setShareOptions,
  tableFor,
  selectedPermissionRowKeys, setSelectedPermissionRowKeys, selectedRowKeys, setSelectedRowKeys
}) => {
  const [form] = Form.useForm();
  const [validations, setValidations] = useState([]);
  console.log(selectedPermissionRowKeys, 'lul')
  useEffect(() => {
    if (!selectedPermissionRowKeys && shareOptions.share[tableFor]) {
      setSelectedRowKeys(shareOptions.share[tableFor].map((e) => Number(e.id)));
      setSelectedPermissionRowKeys(
        shareOptions.share[tableFor].map((e) => ({
          id: Number(e.id),
          permission: Number(e.permission),
        }))
      );
    }
  }, [shareOptions, selectedPermissionRowKeys]);

  const updateInoptions = (updateObj) => {
    const options = shareOptions.share[tableFor];
    const isIdChecked = selectedRowKeys.find(id => id === updateObj.id)
    if (options && isIdChecked) {
      options.map((e) => {
        if (Number(e.id) === updateObj.id) {
          e.permission = String(updateObj.permission);
        }
        return e;
      });
      setShareOptions((prev) => ({
        ...prev,
        share: { ...prev.share, [tableFor]: options },
      }));
    }
  };

  const handleChange = (permission, record) => {
    // const name = `permission${record.id}`
    // form.setFieldsValue({ [name]: permission });
    const keys = selectedPermissionRowKeys.filter((key) => key.id != record.id);
    keys.push({ id: Number(record.id), permission: Number(permission) });
    setSelectedPermissionRowKeys(keys);
    updateInoptions({ id: Number(record.id), permission: Number(permission) });
  };
  useEffect(() => {
    form.validateFields();
  }, [validations]);

  const permissionColumn = {
    title: "Premission Level",
    // align: "center",
    dataIndex: "id",
    width: 350,
    render: (permission, record) => {
      const permissionsText = getPermissionLevelsText().map(
        (e) => e && e.permission
      );
      const validate = validations.find((e) => e.id === record.id);
      return selectedPermissionRowKeys ? (
        <Form form={form} className="permission-column">
          <Form.Item
            name={`permission${record.id}`}
            style={{ marginBottom: 0 }}
            rules={[
              {
                required: validate ? validate.isError : false,
                message: "Please choose permission level",
              },
            ]}
          >
            <Select
              placeholder="Choose..."
              style={{ width: 120 }}
              onChange={(e) => handleChange(e, record)}
              defaultValue={
                selectedPermissionRowKeys.find(
                  (e) => e.id === Number(record.id)
                )?.permission
              }
            >
              {permissionsText.map((permission, id) => (
                <Option key={id} value={id}>
                  {permission}
                </Option>
              ))}
            </Select>
          </Form.Item>
        </Form>
      ) : (
        <></>
      );
    },
  };

  const addToOptions = (id) => {
    const permissionObj = selectedPermissionRowKeys.find((e) => e.id === id);
    const addObj = {};
    addObj["id"] = Number(id);
    addObj["permission"] = permissionObj
      ? Number(permissionObj.permission)
      : null;
    setShareOptions((prev) => {
      prev.share[tableFor] = [...prev.share[tableFor], addObj];
      return prev;
    });
  };

  const removeFromOptions = (id) => {
    const options = shareOptions;
    const permission = selectedPermissionRowKeys.find(
      (e) => e.id === Number(id)
    ).permission;
    const updateObj = { id: Number(id), permission: Number(permission) };
    if (options["revoke"]) {
      if (options["revoke"][tableFor])
        options["revoke"][tableFor] = [
          ...options["revoke"][tableFor],
          updateObj,
        ];
      else
        options["revoke"] = { ...options["revoke"], [tableFor]: [updateObj] };
    } else {
      options["revoke"] = { [tableFor]: [updateObj] };
    }
    setShareOptions(options);
  };

  const rowSelection = {
    onSelect: (record, selected) => {
      const pValidations = [...validations];
      if (selected) {
        const permission = selectedPermissionRowKeys.find(
          (e) => e.id === Number(record.id)
        );
        if (!permission) {
          pValidations.push({ id: record.id, isError: true });
          setValidations(pValidations);
        }
        if (permission) {
          setValidations((prev) => prev.filter((e) => e.id != record.id));
          setSelectedRowKeys((prev) => [...prev, record.id]);
          addToOptions(record.id);
        }
      }
      if (!selected) {
        setSelectedRowKeys((prev) => prev.filter((id) => id != record.id));
        removeFromOptions(record.id);
      }
    },
  };

  return shareOptions.share[tableFor] ? (
    <Table
      rowSelection={{
        columnTitle: <></>,
        type: "checkbox",
        ...rowSelection,
        selectedRowKeys,
      }}
      rowKey={"id"}
      columns={[...columns, permissionColumn]}
      dataSource={data}
      pagination={false}
      selections={false}
      scroll={{ y: 240 }}
      size="small"
    />
  ) : (
    <Skeleton active />
  );
};

const ShareData = ({ columnValues, tabKey, shareOptions, setShareOptions, selectedPermissionRowKeys, setSelectedPermissionRowKeys, selectedRowKeys, setSelectedRowKeys }) => {
  const dispatch = useDispatch();
  let {
    userData: users,
    roleData: roles,
    orgData: organizations,
  } = useSelector((state) => state.admin);
  const currentUserEmail = useSelector(
    (state) => state.app.applicationSettingsData.userData.user.name
  );
  useEffect(() => {
    if (tabKey === "user") fetchUsersData(dispatch);
    if (tabKey === "role") fetchRolesData(dispatch);
    if (tabKey === "organization") fetchOrganizationData(dispatch);
  }, []);

  const columns = columnValues.map((column) => {
    return {
      title: column[0],
      // align: "center",
      dataIndex: column[1],
      ellipsis: true,
      render: (val) => (
        <Tooltip
          placement="top"
          title={val}
          overlayInnerStyle={{ height: "100%", fontSize: 14 }}
        >
          <Text>{val}</Text>
        </Tooltip>
      ),
    };
  });

  const dataObj = useMemo(() => {
    // use email or some unique id to filter
    users = users.filter((user) => user.name !== currentUserEmail);
    return {
      user: users,
      role: roles,
      organization: organizations,
    };
  }, [users, roles, organizations]);

  return (
    <ShareTable
      columns={columns}
      data={dataObj[tabKey]}
      shareOptions={shareOptions}
      setShareOptions={setShareOptions}
      tableFor={tabKey}
      selectedPermissionRowKeys={selectedPermissionRowKeys}
      setSelectedPermissionRowKeys={setSelectedPermissionRowKeys}
      selectedRowKeys={selectedRowKeys}
      setSelectedRowKeys={setSelectedRowKeys}
    />
  );
};

const ShareModalTabs = (props) => {
  const dispatch = useDispatch();
  const Notify = notify(dispatch);
  const applicationSettingsData = useSelector((state) => state.app.applicationSettingsData);
  const fbRequests = requests.filebrowser(dispatch, applicationSettingsData.settings);
  useEffect(() => {
    let options;
    const { modalOptions, setShareOptions, setSelectedPermissionRowKeys } = props;
    const { type, path } = modalOptions;
    options = path ? { type, dir: path } : modalOptions;
    if (options) {
      fbRequests.postShareFileBrowser(
        options,
        "core/share/retrieveSharedInfo",
        (res) => {
          if (Object.keys(res).length === 0 || res.message) {
            if (res.message) {
              // Notify.success({
              //   message: res.message,
              //   type: "Network Call",
              // })
            }
            setShareOptions((prev) => {
              return {
                share: {
                  user: [],
                  role: [],
                  organization: [],
                },
              };
            });
          } else
            setShareOptions((prev) => {
              return {
                share: {
                  user: res.user || [],
                  role: res.role || [],
                  organization: res.organization || [],
                },
              };
            });
        },
        (error) => {
          //
          dispatch(fileBrowserActions.setShareModalVisibility());
        }
      );
    }
  }, []);

  const userColumns = [
    ["User", "name"],
    ["Organization", "orgName"],
  ];
  const roleColumns = [
    ["Role", "name"],
    ["Organization", "orgName"],
  ];
  const orgColumns = [
    ["Name", "name"],
    ["Description", "description"],
  ];
  return (
    <Tabs defaultActiveKey="1">
      <TabPane tab="Users" key="1">
        <ShareData {...props} columnValues={userColumns} tabKey="user" />
      </TabPane>
      <TabPane tab="Roles" key="2">
        <ShareData {...props} columnValues={roleColumns} tabKey="role" />
      </TabPane>
      <TabPane tab="Organizations" key="3">
        <ShareData {...props} columnValues={orgColumns} tabKey="organization" />
      </TabPane>
    </Tabs>
  );
};

const ShareModal = (props) => {
  const dispatch = useDispatch();
  const Notify = notify(dispatch);
  const applicationSettingsData = useSelector((state) => state.app.applicationSettingsData);
  const fbRequests = requests.filebrowser(dispatch, applicationSettingsData.settings);
  const [selectedPermissionRowKeys, setSelectedPermissionRowKeys] =
    useState(null);
  const [selectedRowKeys, setSelectedRowKeys] = useState([]);
  const clickedRecord = useSelector(
    (state) => state.fileBrowser.clickedContextItemDetails.clickedRecord
  );
  const isShareVisible = useSelector(
    (state) => state.fileBrowser.isShareModalVisible
  );
  const [shareOptions, setShareOptions] = useState({
    share: {
      user: null,
      role: null,
      organization: null,
    },
  });

  const checkIfChecked = (keys, checked) => {
    const bool = keys.every(e => checked.some(f => f === e.id))
    return bool
  }

  const onShare = () => {
    if (!checkIfChecked(selectedPermissionRowKeys, selectedRowKeys)) {
      Notify.error({
        message: "Please select the checkbox to share or revoke",
        type: "Network Call",
      });
      return;
    }
    const options = { ...props.shareOptions, ...shareOptions };
    fbRequests.postShareFileBrowser(
      options,
      "core/share/update",
      (res) => {
        Notify.success({
          message: "The selected folder privileges are updated successfully",
          type: "Network Call",
        });
        handleCancel();
      },
      (error) => {
        //
        handleCancel();
      }
    );
  };
  const handleCancel = () => {
    const init = {
      share: {
        user: null,
        role: null,
        organization: null,
      },
    };
    setShareOptions(init);
    setSelectedPermissionRowKeys(null)
    dispatch(fileBrowserActions.setShareModalVisibility());
  };

  const modalOptions = props.shareOptions || clickedRecord;
  return (
    <Modal
      centered={true}
      bodyStyle={{ paddingTop: 0 }}
      // style={{ height: 'calc(100vh - 200px)' }}
      // bodyStyle={{ overflowY: 'scroll' }}
      title={"Share Options"}
      visible={isShareVisible}
      onOk={onShare}
      okText={"Share"}
      onCancel={handleCancel}
      width={800}
      destroyOnClose={true}
      okButtonProps={{ icon: <ShareAltOutlined /> }}
    >
      {modalOptions && (
        <ShareModalTabs
          modalOptions={modalOptions}
          shareOptions={shareOptions}
          setShareOptions={setShareOptions}
          selectedPermissionRowKeys={selectedPermissionRowKeys}
          setSelectedPermissionRowKeys={setSelectedPermissionRowKeys}
          selectedRowKeys={selectedRowKeys}
          setSelectedRowKeys={setSelectedRowKeys}
        />
      )}
    </Modal>
  );
};
export { ShareModal };
