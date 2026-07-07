import React, { useState, useEffect, useMemo, useCallback, useRef } from "react";
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
  Divider,
  Checkbox,
  Empty,
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

const ShareTable = (props) => {
  const {
    columnValues,
    dataSource,
    tabData,
    activeTab,
    modifyShareData,
  } = props;

  const HeaderRow = ({ columns }) => (
    <Row style={{ padding: "0 8px" }}>
      <Col span={3}></Col>
      {columns.map((column, i) => (
        <Col span={7} key={`header${column}${i}`}>
          {column}
        </Col>
      ))}
    </Row>
  );

  const ListBodyItem = (props) => {
    const { item, isChecked, tabDataObj, activeTab, modifyShareData, tabData } =
      props;
    const [form] = Form.useForm();
    const [checked, setChecked] = useState(isChecked);

    useEffect(() => {
      form.setFieldsValue({
        permission: isChecked ? Number(tabDataObj.permission) : null,
      });
    }, []);

    const permissionsText = getPermissionLevelsText().map(
      (e) => e && e.permission
    );
    const onCheckbox = (e, row) => {
      const check = e.target.checked;
      const { permission } = form.getFieldsValue();
      if (check) {
        form.validateFields();
      }
      if (permission !== null) {
        setChecked(check);
        modifyShareData(check, row.id, permission, activeTab);
      }
    };
    const onPermissionChange = (itemId, permission) => {
      modifyShareData(checked, itemId, permission, activeTab);
      form.setFieldsValue({ permission });
    };
    return (
      <Row style={{ padding: "8px" }}>
        <Col span={3}>
          <Checkbox
            onChange={(e) => onCheckbox(e, item)}
            checked={checked}
          ></Checkbox>
        </Col>
        <Col span={7}>{item.name}</Col>
        <Col span={7}>
          {activeTab === "3" ? item.description : item.orgName}
        </Col>
        <Col span={7}>
          <Form form={form} className="permission-column">
            <Form.Item
              name="permission"
              style={{ marginBottom: 0 }}
              rules={[
                {
                  required: true,
                  message: "Please choose permission level",
                },
              ]}
            >
              <Select
                placeholder="Choose..."
                style={{ width: 120 }}
                onChange={(id) => onPermissionChange(item.id, Number(id))}
              >
                {permissionsText.map((perm, id) => (
                  <Option key={id} value={id}>
                    {perm}
                  </Option>
                ))}
              </Select>
            </Form.Item>
          </Form>
        </Col>
      </Row>
    );
  };

  const ListBody = ({ list, tabData, activeTab, modifyShareData }) => {
    const checkedIds = tabData.map((e) => Number(e.id));
    return list.map((item, i) => (
      <ListBodyItem
        key={i}
        item={item}
        isChecked={checkedIds.includes(item.id)}
        tabDataObj={tabData.find((d) => Number(d.id) === item.id)}
        activeTab={activeTab}
        modifyShareData={modifyShareData}
        tabData={tabData}
      />
    ));
  };

  return dataSource.length > 0 ? (
    <>
      {/* header */}
      <HeaderRow columns={columnValues} />
      {/* header */}
      <Divider style={{ margin: "8px 0" }}></Divider>
      <div style={{ maxHeight: 200, overflowY: "overlay" }}>
        {/* Body */}
        <ListBody
          list={dataSource}
          tabData={tabData}
          activeTab={activeTab}
          modifyShareData={modifyShareData}
        />
        {/* Body */}
      </div>
    </>
  ) : (
    <Empty />
  );
};

const ShareData = (props) => {
  const [dataSource, setDataSource] = useState(null);
  const { columns, activeTab, tabData, modifyShareData } = props;
  let {
    user: users,
    role: roles,
    organization: organizations,
  } = useSelector((state) => state.fileBrowser.shareTableData);
  const currentUserEmail = useSelector(
    (state) => state.app.applicationSettingsData.userData.user.name
  );

  useEffect(() => {
    let data = [];
    if (activeTab === "1")
      data = users.filter((user) => user.name !== currentUserEmail);
    if (activeTab === "2") data = roles;
    if (activeTab === "3") data = organizations;
    setDataSource(data);
  }, [activeTab, users, roles, organizations]);

  return (
    dataSource && (
      <ShareTable
        columnValues={columns}
        dataSource={dataSource}
        tabData={tabData}
        activeTab={activeTab}
        modifyShareData={modifyShareData}
      />
    )
  );
};

const ShareModalTabs = (props) => {
  const dispatch = useDispatch();
  const { modifyShareData, shareOptions, fbRequests } = props;
  const user = useSelector((state) => state.app.applicationSettingsData.userData.user);
  const shareTableData = useSelector((state) => state.fileBrowser.shareTableData);
  let tabData = null;
  const [tabs, setTabs] = useState([
    {
      tab: "Users",
      key: "1",
      columns: ["User", "Organization", "Permission Level"],
    },
    {
      tab: "Roles",
      key: "2",
      columns: ["Role", "Organization", "Permission Level"],
    },
    {
      tab: "Organizations",
      key: "3",
      columns: ["Name", "Description", "Permission Level"],
    },
  ]);
  useEffect(() => {
    const { user, role, organization } = shareTableData
    const currentTabs = tabs.filter(e => {
      if (e.tab === "Users" && user) return e
      if (e.tab === "Roles" && role) return e
      if (e.tab === "Organizations" && organization) return e
    });
    setTabs(currentTabs)
  }, [shareTableData])

  const [activeTab, setActiveTab] = useState(tabs[0].key);
  useEffect(() => {
    const payloadObj = { "provide": { "provideUsers": "true", "provideRoles": "true", "provideOrganizations": "true" } }
    if (!user.organization) payloadObj["provide"]["id"] = "all";
    const options = payloadObj
    fbRequests.postShareTableData(
      options,
      "core/share/fetchInfo",
      (res) => {
        dispatch(fileBrowserActions.setShareTableData(res))
      },
      (error) => {
        console.log(error)
      }
    );
  }, []);

  const onTabChange = (activeKey) => setActiveTab(activeKey);
  const tableColumns = tabs.find((e) => e.key === activeTab).columns;
  if (activeTab === "1") tabData = shareOptions.user;
  if (activeTab === "2") tabData = shareOptions.role;
  if (activeTab === "3") tabData = shareOptions.organization;

  return (
    <Tabs defaultActiveKey={tabs[0].key} onChange={onTabChange}>
      {tabs.map((tablEle) => (
        <TabPane tab={tablEle.tab} key={tablEle.key}>
          <ShareData
            columns={tableColumns}
            activeTab={activeTab}
            tabData={tabData}
            modifyShareData={modifyShareData}
          />

        </TabPane>
      ))}
    </Tabs>
  );
};

const ShareModalNew = ({ applicationSettingsData, isShareVisible, clickedRecord, ...props }) => {
  const dispatch = useDispatch();
  const Notify = notify(dispatch);
  const fbRequests = requests.filebrowser(dispatch, applicationSettingsData.settings);
  const init = {
    user: [],
    role: [],
    organization: [],
  };
  const [sharedInfo, setSharedInfo] = useState(init);
  const [userShare, setUserShare] = useState(init);


  useEffect(() => {
  }, [clickedRecord, applicationSettingsData, isShareVisible])

  const handleCancel = () => {
    setSharedInfo(init);
    //userShare.current = init;
    setUserShare(init);
    dispatch(fileBrowserActions.setShareModalVisibility());
    dispatch(
      fileBrowserActions.setContextMenuItemDetails({ contextItem: null })
    );
  };

  const filteredList = (col) => {
    return userShare[col].filter((ele) => {
      const isRetrieved = sharedInfo[col].some((e) => (e.id === ele.id && e.permission === ele.permission));
      if (isRetrieved) return false;
      return true;
    });
  };

  const revokedList = (col) => {
    return sharedInfo[col].filter((ele) => {
      const isSelected = userShare[col].some((e) => e.id === ele.id);
      if (isSelected) return false;
      return true;
    });
  };

  const buildObj = (type) => {
    const func = type === "new" ? filteredList : revokedList;
    return {
      user: func("user"),
      role: func("role"),
      organization: func("organization"),
    };
  };

  const addToOptions = (obj, options, type) => {
    // obj here is share or revoke obj and is of structure {user:[], role:[], organization:[]}
    if ((obj.user.length || obj.role.length || obj.organization.length) >= 1) {
      const refinedObj = Object.fromEntries(
        Object.entries(obj).filter((e) => e[1].length > 0)
      );
      return { ...options, [type]: refinedObj };
    }
    return options;
  };
  const onShare = () => {
    let shareSuccessMessage = {
      "folder": "The selected folder privileges are updated successfully.",
      "file": "The selected file privileges are updated successfully.",
      "datasource": "The selected dataSource privileges are updated successfully."
    }
    let options = { ...props.shareOptions };
    const newlyShared = buildObj("new");
    const revokedInfo = buildObj("revoke");
    const cond =
      (newlyShared.user.length ||
        newlyShared.role.length ||
        newlyShared.organization.length) >= 1 ||
      (revokedInfo.user.length ||
        revokedInfo.role.length ||
        revokedInfo.organization.length) >= 1;
    if (!cond) {
      console.log("error")
      Notify.error({
        message: "Please select the checkbox to share or revoke",
        type: "Network Call",
      });
    }
    if (cond) {
      options = addToOptions(newlyShared, options, "share");
      options = addToOptions(revokedInfo, options, "revoke");
      if (!props.shareOptions) {
        options = {
          ...options,
          type: clickedRecord.type,
          dir: clickedRecord.path,
        };
        if (clickedRecord.type === "file") {
          const pathStringArray = clickedRecord.path.split("/");
          pathStringArray.pop();
          options = {
            ...options,
            file: clickedRecord.name,
            dir: pathStringArray.join("/"),
          };
        }
      }
      fbRequests.postShareFileBrowser(
        options,
        "core/share/update",
        (res) => {
          Notify.success({
            message: props.shareOptions
              ? shareSuccessMessage[props.from]
              : shareSuccessMessage[clickedRecord.type],
            type: "Network Call",
          });
          handleCancel();
        },
        (error) => {
          //
          handleCancel();
        }
      );
    }
  };

  useEffect(() => {
    if (isShareVisible) {
      let options;
      const { shareOptions } = props;
      options = shareOptions
        ? shareOptions
        : { type: clickedRecord.type, dir: clickedRecord.path };
      if (!shareOptions && clickedRecord.type === "file") {
        const pathStringArray = clickedRecord.path.split("/");
        pathStringArray.pop();
        options = {
          ...options,
          file: clickedRecord.name,
          dir: pathStringArray.join("/"),
        };
      }
      if (options) {
        fbRequests.postShareFileBrowser(
          options,
          "core/share/retrieveSharedInfo",
          (res) => {
            // if the folder/file is not shared at all
            if (res.message) {
              // Notify.success({
              //   message: res.message,
              //   type: "Network Call",
              // });
            }
            // if the folder/file has already some shared info
            if (!res.message) {
              const result = Object.entries(res);
              const info = { ...sharedInfo };
              result.forEach((re) => {
                info[re[0]] = re[1];
              });
              setSharedInfo(info);
              // userShare.current = info;
              setUserShare(info);
            }
          },
          (error) => {
            dispatch(fileBrowserActions.setShareModalVisibility());
          }
        );
      }
    }
  }, [isShareVisible]);

  const modifyShareData = useCallback((checked, id, permission, activeTab) => {
    let shareObj =
      activeTab === "1" ? "user" : activeTab === "2" ? "role" : "organization";
    let shareData = JSON.parse(JSON.stringify(userShare));
    if (!shareData[shareObj]) shareData[shareObj] = [];
    if (checked) {
      const data = shareData[shareObj];
      const doesRowExistIndex = data.findIndex((e) => Number(e.id) === id);
      const newObj = { id: String(id), permission: String(permission) };
      if (doesRowExistIndex > -1) {
        shareData[shareObj][doesRowExistIndex] = newObj;
      }
      if (doesRowExistIndex === -1) {
        shareData[shareObj] = [...shareData[shareObj], { ...newObj }];
      }
    }
    if (!checked) {
      shareData[shareObj] = shareData[shareObj].filter(
        (e) => Number(e.id) !== id
      );
    }
    //userShare.current = shareData;
    setUserShare(shareData);
  });

  useEffect(() => {
    console.log(userShare, 'hit')
  }, [userShare])

  const options = Object.values(userShare).filter(e => e.length).length ? userShare : sharedInfo

  return (
    <Modal
      centered={true}
      bodyStyle={{ padding: "8px 24px" }}
      title={"Share Options"}
      visible={isShareVisible}
      onOk={onShare}
      okText={"Share"}
      onCancel={handleCancel}
      width={800}
      destroyOnClose={true}
      okButtonProps={{ icon: <ShareAltOutlined /> }}
    >
      <ShareModalTabs
        modifyShareData={modifyShareData}
        shareOptions={options}
        fbRequests={fbRequests}
      />
    </Modal>
  );
};

const areEqual = (prevProps, nextProps) => {
  if (prevProps.isShareVisible !== nextProps.isShareVisible) {
    return false;
  } else {
    return true;
  }
};

const Wrapper = React.memo(ShareModalNew, areEqual)

const ShareModalWrapper = (props) => {
  const applicationSettingsData = useSelector((state) => state.app.applicationSettingsData);
  const isShareVisible = useSelector(
    (state) => state.fileBrowser.isShareModalVisible
  );
  const clickedRecord = useSelector(
    (state) => state.fileBrowser.clickedContextItemDetails.clickedRecord
  );
  return <Wrapper {...props} applicationSettingsData={applicationSettingsData} isShareVisible={isShareVisible} clickedRecord={clickedRecord} />
}

export default ShareModalWrapper;

