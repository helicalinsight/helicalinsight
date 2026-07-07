import React, { useState, useEffect, useMemo, useRef } from "react";
import { Table, Tooltip } from "antd";
import { useSelector, useDispatch } from "react-redux";
import "../index.scss";
import { updateVisibleDrawers } from "../../../../../redux/actions/admin.actions";
import { VList } from "virtuallist-antd";
import { fetchRolesData, getRolesEditTableData, getTableColumnSearchProps } from "../helperMethods.js";

const HIRoleCheckboxTable = (props) => {
  const { getRoleIds } = props;
  const roleData = useSelector((state) => state.admin.roleData);
  const userData = useSelector((state) => state.admin.userData);
  const editUser = useSelector((state) => state.admin.editUser);
  const { userId } = editUser;
  const activeRecord = userData?.find((item) => item.id === userId);
  const activeRecordRoleIds = activeRecord?.roles.map((item) => item.id);
  const [selectedRowKeys, setSelectedRowKeys] = useState(activeRecordRoleIds);
  const dispatch = useDispatch();
  const [searchText, setSearchText] = useState('');
  const [searchedColumn, setSearchedColumn] = useState('');
  const searchInput = useRef(null);
  const [dataSource, setDataSource] = useState([]);
  // const Notify = notify(dispatch);

  useEffect(() => {
    setDataSource(getRolesEditTableData({ activeRecord, roleData }))
  }, [activeRecord, roleData])

  useEffect(() => {
    setSelectedRowKeys(activeRecordRoleIds);
  }, [userId]);

  useEffect(() => {
    getRoleIds(selectedRowKeys);
  }, [selectedRowKeys]);

  useEffect(() => {
    fetchRolesData(dispatch, false);
  }, []);

  let tableVirtualProps = {};

  const vComponents = useMemo(() => {
    return VList({
      resetTopWhenDataChange: false,
      height: 250, // #4716 - fix
      vid: "hi-um-rolecheckboxtable", // #4188 fix
    })
  }, []);

  if (roleData.length > 5) {
    tableVirtualProps = {
      scroll: { y: 250 },
      components: vComponents,
    };
  } else {
    tableVirtualProps = {};
  }

  const columns = [
    {
      title: "ID",
      dataIndex: "id",
      className: "table-ellipsis",
      width: "10%",
      render: (id) => (
        <Tooltip placement="topLeft" title={id}>
          {id}
        </Tooltip>
      ),
    },
    {
      title: "Name",
      dataIndex: "name",
      className: "table-ellipsis",
      ...getTableColumnSearchProps({ dataIndex: 'name', searchText, searchedColumn, searchInput, setSearchText, setSearchedColumn }),
      render: (name, record) => (
        <Tooltip placement="topLeft" title={name}>
          {name}
        </Tooltip>
      ),

    },
  ];

  const rowSelection = {
    selectedRowKeys,
    selections: [
      {
        key: "add",
        text: "Add Role",
        onSelect: () => {
          dispatch(
            updateVisibleDrawers({
              key: "addRole",
              status: "true",
            })
          );
        },
      },
    ],
    onChange: (selectedRowKeys, selectedRows) => {
      const Ids = selectedRows.map((item) => item.id);
      setSelectedRowKeys(Ids);
      getRoleIds(Ids);
    },
    onSelect: (record, selectedRowKeys, selectedRows) => {
      const Ids = selectedRows.map((item) => item.id);
      setSelectedRowKeys(Ids);
      getRoleIds(Ids);
    },
    onSelectAll: (record, selectedRowKeys, selectedRows) => {
      const Ids = selectedRows.map((item) => item.id);
      setSelectedRowKeys(Ids);
      getRoleIds(Ids);
    },
  };

  return (
    <Table
      className="table-in-drawer"
      rowSelection={{
        type: "checkbox",
        ...rowSelection,
      }}
      rowClassName={(record, index) => {
        let className = index % 2 && "table-row-color";
        return className;
      }}
      {...tableVirtualProps}
      pagination={false}
      data-testid="hi-role-checkbox-table"
      size="small"
      rowKey="id"
      columns={columns}
      dataSource={dataSource}
    />
  );
};

export { HIRoleCheckboxTable };
