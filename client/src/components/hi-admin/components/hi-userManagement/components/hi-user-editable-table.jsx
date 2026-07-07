import React, { useContext, useState, useEffect, useRef } from "react";
import {
  Table,
  Row,
  Typography,
  Input,
  Space,
  Popconfirm,
  Form,
  Button,
  Skeleton,
  Checkbox,
  Tooltip,
  Col,
} from "antd";
import {
  DeleteOutlined,
  EditOutlined,
  UserOutlined,
  SearchOutlined,
  ExclamationCircleFilled
} from "@ant-design/icons";
import { useDispatch, useSelector } from "react-redux";
import { VList } from "virtuallist-antd";
import {
  updateVisibleDrawers,
  updateEditUser,
  deleteUserItem,
} from "../../../../../redux/actions/admin.actions.js";
import requests from "../../../../../base/requests/index.js";
import "../index.scss";
import notify from "../../../../hi-notifications/notify";
import { userItem } from "../utils";
import { loginHandlers } from "../../../../hi-login/helperMethods.js";
import { fetchUsersData } from "../helperMethods.js";
import { roleTypes, routesUrl } from "../../../../../app/constants.js";
import actionTypes from "../../../../../redux/actions/actionTypes.js";
import PopconfirmBody from "../../../../common/components/Hi-Popconfirm.jsx";

const EditableContext = React.createContext(null);

const { Text } = Typography;

const EditableRow = ({ ...props }) => {
  const [form] = Form.useForm();
  return (
    <Form form={form} component={false}>
      <EditableContext.Provider value={form}>
        <tr {...props} />
      </EditableContext.Provider>
    </Form>
  );
};

const EditableCell = ({
  title,
  editable,
  children,
  dataIndex,
  record,
  handleSave,
  ...restProps
}) => {
  const [editing, setEditing] = useState(false);
  const inputRef = useRef(null);
  const form = useContext(EditableContext);
  useEffect(() => {
    if (editing) {
      inputRef.current.focus();
    }
  }, [editing]);

  const toggleEdit = () => {
    setEditing(!editing);
    form.setFieldsValue({
      [dataIndex]: record[dataIndex],
    });
  };

  const save = async () => {
    try {
      const values = await form.validateFields();
      toggleEdit();
      handleSave({ ...record, ...values });
    } catch (errInfo) { }
  };

  let childNode = children;

  if (editable) {
    childNode = editing ? (
      <Form.Item
        style={{
          margin: 0,
        }}
        name={dataIndex}
        rules={[
          {
            required: true,
            message: `${title} is required.`,
          },
        ]}
      >
        <Input ref={inputRef} onPressEnter={save} onBlur={save} />
      </Form.Item>
    ) : (
      <div
        className="editable-cell-value-wrap"
        style={{
          paddingRight: 24,
        }}
        onClick={toggleEdit}
      >
        {children}
      </div>
    );
  }

  return <td {...restProps}>{childNode}</td>;
};
const HIUserEditableTable = (props) => {
  const { isLoading } = props;
  const userData = useSelector((state) => state.admin.userData);
  const { applicationSettingsData, isAuthenticated } = useSelector(
    (state) => state.app
  );
  const { name, email, actualUserName } =
    applicationSettingsData?.userData.user;
  // const actualUserName = applicationSettingsData?.userData.user?.actualUserName;
  const dispatch = useDispatch();
  const [searchText, setSearchText] = useState("");
  const [searchedColumn] = useState("");
  const Notify = notify(dispatch);
  const components = {
    body: {
      row: EditableRow,
      cell: EditableCell,
    },
  };

  let tableVirtualProps = {};

  if (userData.length > 5) {
    tableVirtualProps = {
      scroll: { y: 200 },
      components: VList({
        height: 200,
        vid: "hi-um-usertable",
      }),
    };
  } else {
    tableVirtualProps = components;
  }

  // useEffect(() => {
  //   fetchUsersData(dispatch, false, () => {
  //     removeLoading();
  //   });
  // }, []);

  const deleteUserItemAPI = (id) => {
    userItem({
      dispatch,
      requests,
      data: {
        action: "delete",
        id,
      },
      successCB: (res) => {
        // Notify.success({ ...res, type: "Network Call" });
        dispatch(deleteUserItem(id));
      },
      errorCB: (e) => {
        // Notify.error({ ...e, type: "Network Call" });
      },
    });
  };

  const handleSearch = (selectedKeys, confirm, dataIndex) => {
    confirm();
    setSearchText(selectedKeys[0]);
    setSearchText(dataIndex);
  };

  const handleReset = (clearFilters) => {
    clearFilters();
    setSearchText("");
  };

  const getColumnSearchProps = (dataIndex) => ({
    filterDropdown: ({
      setSelectedKeys,
      selectedKeys,
      confirm,
      clearFilters,
    }) => (
      <div style={{ padding: 8 }}>
        <Input
          placeholder={`Search ${dataIndex === "orgName" ? "organization" : dataIndex
            }`}
          value={selectedKeys[0]}
          onChange={(e) =>
            setSelectedKeys(e.target.value ? [e.target.value] : [])
          }
          onPressEnter={() => handleSearch(selectedKeys, confirm, dataIndex)}
          style={{ marginBottom: 8, display: "block" }}
        />
        <Space>
          <Button
            type="primary"
            onClick={() => handleSearch(selectedKeys, confirm, dataIndex)}
            icon={<SearchOutlined />}
            size="small"
            style={{ width: 90 }}
          >
            Search
          </Button>
          <Button
            onClick={() => {
              handleReset(clearFilters);
              handleSearch(selectedKeys, confirm, dataIndex);
            }}
            size="small"
            style={{ width: 90 }}
          >
            Reset
          </Button>
        </Space>
      </div>
    ),
    filterIcon: (filtered) => (
      <SearchOutlined style={{ color: filtered ? "#1890ff" : undefined }} />
    ),
    onFilter: (value, record) => {
      if (dataIndex === "roles") {
        return record[dataIndex]
          ? record[dataIndex].some((item) =>
            item.role.toString().toLowerCase().includes(value.toLowerCase())
          )
          : "";
      } else if (dataIndex === "profiles") {
        return record[dataIndex]
          ? record[dataIndex].some((item) =>
            item.name.toString().toLowerCase().includes(value.toLowerCase())
          )
          : "";
      } else {
        return record[dataIndex]
          ? record[dataIndex]
            .toString()
            .toLowerCase()
            .includes(value.toLowerCase())
          : "";
      }
    },
    onFilterDropdownVisibleChange: (visible) => {
      if (visible) {
      }
    },
    render: (text) =>
      searchedColumn === dataIndex ? (
        <div
          highlightStyle={{ backgroundColor: "#ffc069", padding: 0 }}
          searchWords={[searchText]}
          autoEscape
          textToHighlight={text ? text.toString() : ""}
        />
      ) : (
        text
      ),
  });

  const columns = [
    {
      title: () => <Tooltip title="Serial No">S No</Tooltip>, // #4199
      dataIndex: "slno",
      className: "table-ellipsis",
      width: "7%",
      defaultSortOrder: "ascend",
      sorter: (a, b) => a.slno - b.slno,
      render: (cell) => (
        <Tooltip placement="top" title={cell}>
          {cell}
        </Tooltip>
      ),
    },
    {
      title: () => <Tooltip title="User Name">User Name</Tooltip>, //#4199
      dataIndex: "name",
      width: "20%",
      className: "table-ellipsis",
      sorter: (a, b) => a.name.localeCompare(b.name),
      ...getColumnSearchProps("name"),
      render: (text, record) => (
        <Row className="hi-text-container" justify="space-between">
          <Col span={20}>
            <Tooltip placement="top" title={text}>
              <Text>{text}</Text>
            </Tooltip>
          </Col>
          <Col span={4}>
            <Button
              type="primary"
              size="small"
              className="hi-um-edit-icon"
              key={record.id}
              onClick={() => {
                dispatch(
                  updateVisibleDrawers({ key: "editUser", status: true })
                );
                dispatch(updateEditUser({ type: "user", userId: record.id }));
              }}
              icon={<EditOutlined />}
            />
          </Col>
        </Row>
      ),
    },
    {
      title: () => <Tooltip data-testid="hi-user-editable-table-title" title="User Email">Email</Tooltip>, // #4199
      dataIndex: "email",
      width: "15%",
      className: "table-ellipsis",
      sorter: (a, b) => a.email.localeCompare(b.email),
      ...getColumnSearchProps("email"),
      render: (cell) => (
        <Tooltip placement="top" title={cell}>
          {cell}
        </Tooltip>
      ),
    },
    {
      title: () => (
        <Tooltip title="Organization Name">Organization Name</Tooltip>
      ), // #4199
      dataIndex: "orgName",
      width: "20%",
      className: "table-ellipsis",
      sorter: (a, b) => a.orgName.localeCompare(b.orgName),
      ...getColumnSearchProps("orgName"),
      render: (text) => (
        <Tooltip placement="top" title={text}>
          <Text>{text}</Text>
        </Tooltip>
      ),
    },
    {
      title: () => <Tooltip title="User Roles">Roles</Tooltip>, // #4199
      dataIndex: "roles",
      className: "table-ellipsis",
      width: "10%",
      sorter: (a, b) => a.roles.length - b.roles.length,
      ...getColumnSearchProps("roles"),
      render: (text, record) => (
        <Row className="hi-text-container" justify="space-between">
          <Tooltip placement="top" title={text.length}>
            <Text>{text.length}</Text>
          </Tooltip>
          <Button
            size="small"
            type="primary"
            className="hi-um-edit-icon"
            key={record.id}
            onClick={() => {
              dispatch(updateVisibleDrawers({ key: "editUser", status: true }));
              dispatch(updateEditUser({ type: "role", userId: record.id }));
            }}
            icon={<EditOutlined />}
          ></Button>
        </Row>
      ),
    },
    {
      title: () => <Tooltip title="User Profiles">Profiles</Tooltip>, // #4199
      dataIndex: "profiles",
      className: "table-ellipsis",
      width: "11%",
      sorter: (a, b) => a.profiles.length - b.profiles.length,
      ...getColumnSearchProps("profiles"),
      render: (text, record) => (
        <Row className="hi-text-container" justify="space-between">
          <Tooltip placement="top" title={text?.length}>
            <Text>{text?.length}</Text>
          </Tooltip>
          <Button
            size="small"
            type="primary"
            className="hi-um-edit-icon"
            key={record.id}
            onClick={() => {
              dispatch(updateVisibleDrawers({ key: "editUser", status: true }));
              dispatch(updateEditUser({ type: "profile", userId: record.id }));
            }}
            icon={<EditOutlined />}
          ></Button>
        </Row>
      ),
    },
    {
      title: () => <Tooltip title="Enable">Enable</Tooltip>, // #4199
      dataIndex: "enabled",
      key: "enabled",
      className: "table-ellipsis",
      width: "9%",
      render: (value, record) => (
        <Checkbox
          checked={record.enabled}
          onChange={handleCheckboxChangeFactory(record, "enabled")}
        />
      ),
    },
    {
      title: () => <Tooltip title="Actions">Actions</Tooltip>, // #4199
      dataIndex: "actions",
      className: "table-ellipsis",
      width: "15%",
      render: (_, record) =>
        userData.length >= 1 ? (
          <Space>
            <Tooltip placement="bottom" title="Edit User">
              <Button
                size="small"
                type="text"
                key={record.id}
                onClick={() => {
                  dispatch(
                    updateVisibleDrawers({ key: "editUser", status: true })
                  );
                  dispatch(updateEditUser({ type: "all", userId: record.id }));
                }}
                icon={<EditOutlined />}
              ></Button>
            </Tooltip>
            <Tooltip placement="bottom" title="Impersonate User">
              <Button
                onClick={() => {
                  loginHandlers({
                    impersonateUserDetails: {
                      name: record.name,
                      organization: record.orgName,
                    },
                    logType: actionTypes.IMPERSONATE_LOGIN,
                    dispatch,
                    isAuthenticated,
                    nxtRoute: record.roles?.some(
                      (ele) => ele.role === roleTypes.roleAdmin
                    )
                      ? routesUrl.adminHomeUrl
                      : routesUrl.userHomeUrl,
                    successNotification: applicationSettingsData?.settings?.successNotification,
                  });
                }}
                size="small"
                type="text"
                disabled={
                  (record.name === name && record.email === email) ||
                    record.name === actualUserName
                    ? true
                    : !record.enabled
                }
                key={record.id}
                icon={<UserOutlined />}
              />
            </Tooltip>
            <Tooltip placement="left" title="Delete User">
              <Popconfirm
                title={<PopconfirmBody intent="delete" description={<div>Are you sure you want to delete <strong>{record.name}</strong> user?</div>} />}
                onConfirm={() => handleDelete(record.id)}
                placement="left"
              >
                <Button type="text" size="small" icon={<DeleteOutlined />} />
              </Popconfirm>
            </Tooltip>
          </Space>
        ) : null,
    },
  ];

  const handleDelete = (id) => {
    deleteUserItemAPI(id);
  };

  const handleSave = (row) => {
    const newData = [...this.state.dataSource];
    const index = newData.findIndex((item) => row.key === item.key);
    const item = newData[index];
    newData.splice(index, 1, { ...item, ...row });
    this.setState({
      dataSource: newData,
    });
  };

  const columnsNew = columns.map((col) => {
    if (!col.editable) {
      return col;
    }

    return {
      ...col,
      onCell: (record) => ({
        record,
        editable: col.editable,
        dataIndex: col.dataIndex,
        title: col.title,
        handleSave: handleSave,
      }),
    };
  });

  const handleCheckboxChangeFactory = (record) => (event) => {
    const enabled = event.target.checked;
    userItem({
      dispatch,
      requests,
      data: {
        action: "update",
        id: record.id,
        formData: {
          id: record.id,
          name: record.name,
          email: record.email,
          password: "",
          enabled,
        },
      },
      successCB: (res) => {
        // Notify.success({ ...res, type: "Network Call" });
        fetchUsersData(dispatch, true);
        fetchUsersData(dispatch, true);
      },
    });
  };

  const getSortedUserData = (array) => {
    return array.sort((a, b) => a.name - b.name);
  };

  const getUserDataWithSlNo = (array) => {
    return array.map((item, index) => ({
      ...item,
      slno: (index + 1).toString(),
    }));
  };
  const sortedUserData = getSortedUserData([...userData]);

  const userDataWithSlNo = getUserDataWithSlNo([...sortedUserData]);

  return (
    <Table
      data-testid="hi-user-editable-table"
      className="hi-um-table"
      rowClassName={(record, index) => {
        let className = index % 2 && "table-row-color";
        return className;
      }}
      {...tableVirtualProps}
      rowKey="id"
      columns={
        !isLoading
          ? columnsNew
          : columnsNew.map((column) => {
            return {
              ...column,
              render: function renderPlaceholder() {
                return (
                  <Skeleton.Button
                    size="small"
                    active
                    className="hi-um-antd-table-skeleton"
                    key={column.key}
                    title={true}
                    paragraph={false}
                  />
                );
              },
            };
          })
      }
      dataSource={[...userDataWithSlNo]}
      pagination={false}
      size="small"
      showSorterTooltip={false} // #4199
    />
  );
};
export { HIUserEditableTable };
