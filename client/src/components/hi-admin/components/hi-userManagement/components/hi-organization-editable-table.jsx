import React, { useContext, useState, useEffect, useRef } from "react";
import {
  Table,
  Input,
  Space,
  Popconfirm,
  Form,
  Button,
  Skeleton,
  Tooltip,
} from "antd";
import { DeleteOutlined, SearchOutlined, ExclamationCircleFilled } from "@ant-design/icons";
import { useDispatch, useSelector } from "react-redux";
import { deleteOrgItem } from "../../../../../redux/actions/admin.actions.js";
// import Highlighter from "react-highlight-words";
import requests from "../../../../../base/requests/index.js";
import { VList } from "virtuallist-antd";
import { orgItem } from "../utils";
import notify from "../../../../hi-notifications/notify";
import { fetchUsersData, fetchRolesData } from "../helperMethods.js";
import "../index.scss";
import PopconfirmBody from "../../../../common/components/Hi-Popconfirm.jsx";

const EditableContext = React.createContext(null);

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

const HIOrgEditableTable = (props) => {
  const { isLoading } = props;
  const orgData = useSelector((state) => state.admin.orgData);
  const dispatch = useDispatch();
  const Notify = notify(dispatch);
  const [, setSearchText] = useState("");
  const [searchedColumn, setSearchedColumn] = useState("");
  const components = {
    body: {
      row: EditableRow,
      cell: EditableCell,
    },
  };

  let tableVirtualProps = {};

  if (orgData.length >= 5) {
    tableVirtualProps = {
      scroll: { y: 200 },
      components: VList({
        height: 200,
        vid: "hi-um-orgtable",
      }),
    };
  } else {
    tableVirtualProps = components;
  }

  // useEffect(() => {
  //   fetchOrganizationData(dispatch, false, () => {
  //     removeLoading();
  //   });
  // }, []);

  const deleteOrgItemAPI = (id) => {
    orgItem({
      dispatch,
      requests,
      data: {
        action: "delete",
        id,
      },
      successCB: (res) => {
        // Notify.success({ ...res, type: "Network Call" });
        dispatch(deleteOrgItem(id));
        fetchUsersData(dispatch, true);
        fetchRolesData(dispatch, true);
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
          // ref={(node) => {
          //   this.searchInput = node;
          // }}
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
          data-testid ="hi-org-editable-table-search-btn"
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
              handleReset(clearFilters, confirm);
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
        // setTimeout(() => this.searchInput.select(), 100);
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
      title: () => <Tooltip data-testid ="hi-org-editable-table-sno" title="Serial No">S No</Tooltip>, // #4199
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
        <Tooltip data-testid ="hi-org-editable-table-orgname" title="Organization Name">Organization Name</Tooltip>
      ), // #4199
      dataIndex: "name",
      width: "45%",
      className: "table-ellipsis",
      sorter: (a, b) => a.name.localeCompare(b.name),
      ...getColumnSearchProps("name"),
      render: (cell) => (
        <Tooltip placement="top" title={cell}>
          {cell}
        </Tooltip>
      ),
    },
    {
      title: () => (
        <Tooltip data-testid ="hi-org-editable-table-des" title="Organization Description">Description</Tooltip>
      ), // #4199
      dataIndex: "description",
      className: "table-ellipsis",
      width: "40%",
      ...getColumnSearchProps("description"),
      render: (cell) => (
        <Tooltip placement="top" title={cell}>
          {cell}
        </Tooltip>
      ),
    },
    {
      title: () => <Tooltip data-testid ="hi-org-editable-table-action" title="Actions">Actions</Tooltip>, // #4199
      dataIndex: "actions",
      className: "table-ellipsis",
      width: "10%",
      render: (_, record) =>
        orgData.length >= 1 ? (
          <Space>
            <Tooltip placement="left" title="Delete Organization">
              <Popconfirm
                placement="left"
                title ={<PopconfirmBody intent="delete" description={<span>Are you sure you want to delete <strong>{record.name}</strong> organization?</span>} />}
                onConfirm={() => handleDelete(record.id)}
              >
                <Button size="small" type="text" icon={<DeleteOutlined />} />
              </Popconfirm>
            </Tooltip>
          </Space>
        ) : null,
    },
  ];

  const handleDelete = (id) => {
    deleteOrgItemAPI(id);
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

  const getSortedOrgData = (array) => {
    return array.sort((a, b) => a.id - b.orgid);
  };

  const getOrgDataWithSlNo = (array) => {
    return array.map((item, index) => ({
      ...item,
      slno: (index + 1).toString(),
    }));
  };
  const sortedOrgData = getSortedOrgData([...orgData]);

  const orgDataWithSlNo = getOrgDataWithSlNo([...sortedOrgData]);

  return (
    <Table
      className="hi-um-table"
      size="small"
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
      dataSource={[...orgDataWithSlNo]}
      pagination={false}
      showSorterTooltip={false} // #4199
    />
  );
};
export { HIOrgEditableTable };
