import React, { useContext, useState, useEffect, useRef, useMemo } from "react";
import {
  Table,
  Typography,
  Input,
  Space,
  Popconfirm,
  Form,
  Button,
  Skeleton,
  Tooltip,
} from "antd";
import { DeleteOutlined, SearchOutlined } from "@ant-design/icons";
import { useDispatch, useSelector } from "react-redux";
import { deleteRoleItem } from "../../../../../redux/actions/admin.actions.js";
// import Highlighter from "react-highlight-words";
import requests from "../../../../../base/requests/index.js";
import { VList } from "virtuallist-antd";
import { roleItem } from "../utils";
import notify from "../../../../hi-notifications/notify";
import { fetchUsersData } from "../helperMethods.js";
import "../index.scss";
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

const HIRoleEditableTable = (props) => {
  const { isLoading } = props;
  const roleData = useSelector((state) => state.admin.roleData);
  const dispatch = useDispatch();
  const [, setSearchText] = useState("");
  const [searchedColumn, setSearchedColumn] = useState("");
  const components = {
    body: {
      row: EditableRow,
      cell: EditableCell,
    },
  };
  const Notify = notify(dispatch);
  let tableVirtualProps = {};

  const vComponents = useMemo(() => {
    return VList({
      height: 200,
      vid: "hi-um-roletable",
    });
  }, []);

  if (roleData.length > 5) {
    tableVirtualProps = {
      scroll: { y: 200 },
      components: vComponents,
    };
  } else {
    tableVirtualProps = components;
  }

  // useEffect(() => {
  //   fetchRolesData(dispatch, false, () => {
  //     removeLoading();
  //   });
  // }, []);

  const deleteRoleItemAPI = (id) => {
    roleItem({
      dispatch,
      requests,
      data: {
        action: "delete",
        formData: { id },
      },
      successCB: (res) => {
        // Notify.success({ ...res, type: "Network Call" }, dispatch);
        fetchUsersData(dispatch, true);
        dispatch(deleteRoleItem(id));
      },
      errorCB: (e) => {
        // Notify.error({ ...e, type: "Network Call" });
      },
    });
  };

  const handleSearch = (selectedKeys, confirm, dataIndex) => {
    confirm();
    setSearchText(selectedKeys[0]);
    setSearchedColumn(dataIndex);
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
          placeholder={`Search ${dataIndex === "orgName" ? "organisation" : dataIndex
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
    onFilter: (value, record) =>
      record[dataIndex]
        ? record[dataIndex]
          .toString()
          .toLowerCase()
          .includes(value.toLowerCase())
        : "",
    onFilterDropdownVisibleChange: (visible) => {
      if (visible) {
      }
    },
    render: (text) =>
      searchedColumn === dataIndex ? (
        <div
          highlightStyle={{ backgroundColor: "#ffc069", padding: 0 }}
          searchWords={[this.state.searchText]}
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
      width: "6%",
      defaultSortOrder: "ascend",
      sorter: (a, b) => a.slno - b.slno,
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
      title: () => <Tooltip title="Role">Role</Tooltip>, // #4199
      dataIndex: "name",
      className: "table-ellipsis",
      ...getColumnSearchProps("name"),
      render: (cell) => (
        <Tooltip placement="top" title={cell}>
          {cell}
        </Tooltip>
      ),
    },
    {
      title: () => <Tooltip title="Actions">Actions</Tooltip>, // #4199
      dataIndex: "actions",
      className: "table-ellipsis",
      width: "10%",
      render: (_, record) =>
        roleData.length >= 1 ? (
          <Space>
            <Tooltip placement="left" title="Delete role">
              <Popconfirm
                title={<PopconfirmBody intent="delete" description={<span>Are you sure you want to delete <strong>{record.name}</strong> role?</span>} />}
                placement="left"
                onConfirm={() => handleDelete(record.id)}
              >
                <Button type="text" size="small" icon={<DeleteOutlined />} />
              </Popconfirm>
            </Tooltip>
          </Space>
        ) : null,
    },
  ];

  const handleDelete = (id) => {
    deleteRoleItemAPI(id);
  };

  const getSortedRoleData = (array) => {
    return array.sort((a, b) => a.organisation - b.organisation);
  };

  const getRoleDataWithSlNo = (array) => {
    return array.map((item, index) => ({
      ...item,
      slno: (index + 1).toString(),
    }));
  };
  const sortedRoleData = getSortedRoleData([...roleData]);

  const roleDataWithSlNo = getRoleDataWithSlNo([...sortedRoleData]);

  return (
    <Table
      data-testid="hi-role-editable-table"
      className="hi-um-table"
      {...tableVirtualProps}
      rowClassName={(record, index) => {
        let className = index % 2 && "table-row-color";
        return className;
      }}
      size="small"
      columns={
        !isLoading
          ? columns
          : columns.map((column) => {
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
      rowKey="id"
      dataSource={[...roleDataWithSlNo]}
      pagination={false}
      showSorterTooltip={false} // #4199
    />
  );
};
export { HIRoleEditableTable };
