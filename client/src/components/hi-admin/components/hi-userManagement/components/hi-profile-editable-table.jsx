import React, { useContext, useState, useEffect, useRef } from "react";
import { DeleteOutlined, EditOutlined } from "@ant-design/icons";
import {
  Table,
  Input,
  Popconfirm,
  Form,
  Button,
  Row,
  Col,
  Tooltip,
  Typography,
} from "antd";
import { useDispatch, useSelector } from "react-redux";
import {
  updateVisibleDrawers,
  deleteProfileItem,
} from "../../../../../redux/actions/admin.actions";
import requests from "../../../../../base/requests";
import { profileItem } from "../utils";
import "../index.scss";
import notify from "../../../../hi-notifications/notify";
import { fetchUsersData } from "../helperMethods";
import PopconfirmBody from "../../../../common/components/Hi-Popconfirm";

const { Text } = Typography;

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
      dataIndex === "name" ? (
        <Form.Item
          style={{
            margin: 0,
          }}
          name={dataIndex}
          rules={[
            {
              required: true,
              message:
                "Profile name can only use A-Z, a-z, @, 0-9, . and _ and cannot have spaces",
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
                      "Profile name can only use A-Z, a-z, @, 0-9, . and _ and can have spaces"
                    )
                  );
                }

                return Promise.resolve();
              },
            }),
          ]}
        >
          <Input ref={inputRef} onPressEnter={save} onBlur={save} />
        </Form.Item>
      ) : (
        <Form.Item
          style={{
            margin: 0,
          }}
          name={dataIndex}
          rules={[
            {
              required: true,
              message:
                "Profile value can only use A-Z, a-z, @, 0-9,  . and _ and cannot have spaces",
            },
            () => ({
              validator(_, value) {
                if (value && !value.match(/^[a-zA-Z0-9@`',._\s]+$/)) {
                  return Promise.reject(
                    new Error(
                      "Profile value can only use A-Z, a-z, @, 0-9, .,`,' and _ and can have spaces"
                    )
                  );
                }

                return Promise.resolve();
              },
            }),
          ]}
        >
          <Input ref={inputRef} onPressEnter={save} onBlur={save} />
        </Form.Item>
      )
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

const HIProfileTable = () => {
  const userData = useSelector((state) => state.admin.userData);
  const editUser = useSelector((state) => state.admin.editUser);
  const { userId } = editUser;
  const activeRecord = userData?.find((item) => item.id === userId);
  const profileData = activeRecord?.profiles;

  const dispatch = useDispatch();
  const Notify = notify(dispatch);

  useEffect(() => {
    fetchUsersData(dispatch, false);
  }, []);

  const getSortedProfileData = (array) => {
    return array.sort((a, b) => a.name - b.name);
  };

  const getProfileDataWithSlNo = (array) => {
    return array.map((item, index) => ({
      ...item,
      slno: (index + 1).toString(),
    }));
  };
  const sortedProfileData = getSortedProfileData([...profileData]);
  const profileDataWithSlNo = getProfileDataWithSlNo([...sortedProfileData]);
  const deleteProfileItemAPI = (key) => {
    profileItem({
      dispatch,
      requests,
      data: {
        action: "delete",
        id: key,
      },
      successCB: (res) => {
        // Notify.success({ ...res, type: "Network Call" }, dispatch);
        dispatch(
          deleteProfileItem({ userId: activeRecord.id, profileId: key })
        );
      },
      errorCB: (e) => {
        // Notify.error({ ...e, type: "Network Call" });
      },
    });
  };

  const columns = [
    {
      title: "S No",
      dataIndex: "slno",
      className: "table-ellipsis",
    },
    {
      title: "Name",
      dataIndex: "name",
      maxWidth: "300px",
      editable: true,
      render: (text, record) => (
        <Row className="hi-text-container" justify="space-between">
          <Col span={20}>
            <Tooltip placement="top" title={text}>
              <Text>{text}</Text>
            </Tooltip>
          </Col>
          <Col span={4} style={{display: "grid" , placeItems:"center"}}>
            <Button
              size="small"
              type="text"
              className="hi-um-edit-icon"
              key={record.id}
              icon={<EditOutlined />}
            />
          </Col>
        </Row>
      ),
    },
    {
      title: "Value",
      dataIndex: "value",
      editable: true,
      maxWidth: "415px",
      render: (text, record) => (
        <Row className="hi-text-container" justify="space-between">
          <Col span={20}>
            <Tooltip placement="top" title={text}>
              <Text>{text}</Text>
            </Tooltip>
          </Col>
          <Col span={4} style={{display: "grid" , placeItems:"center"}}>
            <Button
              size="small"
              type="text"
              className="hi-um-edit-icon"
              key={record.id}
              icon={<EditOutlined />}
            />
          </Col>
        </Row>
      ),
    },
    {
      title: "Actions",
      dataIndex: "actions",
      render: (_, record) =>
        profileData.length > 0 ? (
          <Popconfirm
            placement="left"
            title={<PopconfirmBody intent="delete" />}
            onConfirm={() => handleDelete(record.id)}
          >
            <Button size="small" type="text" icon={<DeleteOutlined />} />
          </Popconfirm>
        ) : null,
    },
  ];

  const handleDelete = (key) => {
    deleteProfileItemAPI(key);
  };

  // const handleAdd = (values) => {
  //   const { name, value } = values;
  //   const newData = {
  //     key: count,
  //     name,
  //     value,
  //   };
  // };

  const handleSave = (row) => {
    profileItem({
      dispatch,
      requests,
      data: {
        action: "update",
        id: row.id,
        formData: { ...row },
      },
      successCB: (res) => {
        // Notify.success({ ...res, type: "Network Call" });
        // dispatch(updateVisibleDrawers({ key: "editUser", status: false }));
        fetchUsersData(dispatch, true);
      },
      errorCB: (e) => {
        // Notify.error({ ...e, type: "Network Call" });
        // dispatch(updateVisibleDrawers({ key: "editUser", status: false }));
      },
    });
  };

  const rowSelection = {
    selections: [
      {
        key: "add",
        text: "Add Profile",
        onSelect: () => {
          dispatch(
            updateVisibleDrawers({
              key: "addProfile",
              status: "true",
            })
          );
        },
      },
    ],
    getCheckboxProps: (record) => ({
      disabled: record.name,
    }),
  };

  const components = {
    body: {
      row: EditableRow,
      cell: EditableCell,
    },
  };
  const columns1 = columns.map((col) => {
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

  // if (profileData.length > 5) {
  //   tableVirtualProps = {
  //     scroll: { y: 200 },
  //     components: VList({
  //       height: 200,
  //       vid: "hi-um-profiletable",
  //     }),
  //   };
  // }

  return (
    <Table
      style={{wordBreak:"break-word"}}
      size="small"
      components={components}
      rowClassName={(record, index) => {
        let className = index % 2 && "table-row-color";
        return className;
      }}
      // Cannot use virtual table because of inline edit in table cell
      // {...tableVirtualProps}
      data-testid="hi-profile-editable-table"
      className="hi-profile-table"
      rowKey="id"
      pagination={false}
      rowSelection={{ type: "checkbox", ...rowSelection }}
      dataSource={[...profileDataWithSlNo]}
      columns={columns1}
    />
  );
};
export { HIProfileTable };
