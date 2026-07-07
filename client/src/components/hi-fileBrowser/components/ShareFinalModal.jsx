import React, { useState, useEffect, useRef } from "react";
import { useSelector, useDispatch } from "react-redux";
import {
  getPermissionLevelsText
} from "../../../utils/utilities";
import requests from "../../../base/requests";
import { fileBrowserActions } from "../../../redux/actions";
import {
  Typography,
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
  Popover,
  Input,
  Tooltip,
  Table,
} from "antd";
import { CloseOutlined, SearchOutlined, ShareAltOutlined } from "@ant-design/icons";
import notify from "../../hi-notifications/notify.js";
import ShareModalSkeleton from "../../common/custom-icons/CustomSkeletons/Modal/ShareSkeleton";
import { useDebounce } from "../../../hooks/useDebounce.js";
import { handleShareOptionsWithSearchTerm } from "../helperMethods.js";

const { Option } = Select;
const { TabPane } = Tabs;

const HeaderRow = ({ columns, searchTerm, setSearchTerm }) => {
  const searchInput = useRef(null);
  const searchClick = useRef(null);
  const [inputVisible, setInputVisible] = useState(false);

  useEffect(() => {
    if (inputVisible && searchInput.current) {
      searchInput.current.focus()
    }
  }, [inputVisible])

  const handleSearchClick = () => {
    setInputVisible(true)
  }

  return <>
    <Row style={{ padding: "0 8px" }}>
      <Col span={3}></Col>
      {columns.map((column, i) => (
        <>
          <Col span={5} key={`header${column}${i}`}>
            {column}
          </Col>
          {(!column.includes("Permission")) && <Col span={2} className="share-option-search">
            {/* {true ? <SearchOutlined /> : <CloseOutlined />} */}
            <Popover
              align={{ offset: [0, 11] }}
              id="hi-popover-padding-0"
              content={
                <Input
                  ref={searchInput}
                  value={searchTerm.col === column ? searchTerm.value : null}
                  placeholder="Search..."
                  allowClear
                  onChange={(e) => setSearchTerm({ value: e.target.value, col: column })}
                  onBlur={() => {
                    searchInput.current.focus()
                    // setSearchTerm({ value: null, col: null })
                  }}
                />
              }
              placement="leftBottom"
              trigger="click"
            >
              <Tooltip title="Search">
                <SearchOutlined
                  ref={searchClick}
                  onClick={handleSearchClick}
                  style={{
                    fontSize: 16, color: (searchTerm.value &&
                      (searchTerm.col === column)) ? "#1890ff" : ""
                  }}
                />
              </Tooltip>
            </Popover>
          </Col>}
        </>
      ))}
    </Row>
    <Divider style={{ margin: "8px 0" }}></Divider>
  </>
};

const TabListItem = (props) => {
  const dispatch = useDispatch();
  const { item, tabKey } = props
  const [form] = Form.useForm();
  const permissionsText = getPermissionLevelsText().map(
    (e) => e && e.permission
  );

  useEffect(() => {
    form.setFieldsValue({
      permission: item.permission,
    });
  }, [item]);

  useEffect(() => { })
  const onCheckbox = (e) => {
    const check = e.target.checked;
    if (check) {
      form.validateFields();
    }
    if (item.permission !== null) {
      const updatedItem = { ...item, checked: check }
      dispatch(fileBrowserActions.setShareTableDataKey(tabKey, updatedItem));
    }
  };

  const onPermissionChange = (permission) => {
    const updatedItem = { ...item, permission, checked: true }
    dispatch(fileBrowserActions.setShareTableDataKey(tabKey, updatedItem));
    form.setFieldsValue({ permission });
  };

  return (
    <Row style={{ padding: "8px" }}>
      <Col span={3}>
        <Checkbox
          onChange={onCheckbox}
          checked={item.checked}
        ></Checkbox>
      </Col>
      <Col span={7}>{item.name}</Col>
      <Col span={7}>
        {tabKey === "organization" ? item.description : item.orgName}
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
              onChange={(id) => onPermissionChange(Number(id))}
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
}

// const TabRows = (props) => {
//   const {columns, tabData, tabKey} = props
//   const TabList = (props) => {
//     const {data} = props
//     return (
//       <div style={{ maxHeight: 200, overflowY: "scroll" }}>
//         {data.map((item, i) => (
//           <TabListItem
//             key={i}
//             item={item}
//           />
//         ))}
//       </div>
//     );
//   }


//   return (
//     <>
//       {tabData.length > 0 ? (
//         <>
//           <HeaderRow />
//           <TabList data={tabData} />
//         </>
//       ) : (
//         <Empty />
//       )}
//     </>
//   );
// }

const ShareTabs = (props) => {
  const { tablesData } = props
  const [searchTerm, setSearchTerm] = useState({ value: "", col: null });
  let debouncedSearchTerm = useDebounce(searchTerm, 500);
  const [tabs, setTabs] = useState([
    {
      tab: "Users",
      key: "user",
      columns: ["User", "Organization", "Permission Level"],
    },
    {
      tab: "Roles",
      key: "role",
      columns: ["Role", "Organization", "Permission Level"],
    },
    {
      tab: "Organizations",
      key: "organization",
      columns: ["Name", "Description", "Permission Level"],
    },
  ]);

  useEffect(() => {
    if (tablesData) {
      const { user, role, organization } = tablesData
      const currentTabs = tabs.filter(e => {
        if (e.tab === "Users" && user) return e
        if (e.tab === "Roles" && role) return e
        if (e.tab === "Organizations" && organization) return e
      });
      setTabs(currentTabs)
    }
  }, [tablesData])

  const [tabData, setTabData] = useState(null);
  const [tabKey, setTabkey] = useState(tabs[0].key)

  const onTabChange = (key) => {
    debouncedSearchTerm = { value: "", col: null };
    setSearchTerm({ value: "", col: null })
    setTabkey(key)
    setTabData(tablesData[key])
  }

  // useEffect(() => {
  //   if (tablesData && !tabData) setTabData(tablesData[tabs[0].key])
  // }, [tablesData])

  useEffect(() => {
    // if (tablesData && tabData) setTabData(tablesData[tabKey])
    if (tablesData) {
      if (tabData) {
        setTabData(tablesData[tabKey])
      } else {
        setTabData(tablesData[tabs[0].key])
      }
    }
  }, [tablesData])

  const searchedData = handleShareOptionsWithSearchTerm({ tabData: tabData || [], debouncedSearchTerm, tabKey });

  return tablesData && tabData ? (
    <Tabs defaultActiveKey={tabs[0].key} onChange={onTabChange}>
      {tabs.map((tablEle) => (
        <TabPane tab={tablEle.tab} key={tablEle.key}>
          {tabData.length > 0 ? (
            <>
              <HeaderRow columns={tablEle.columns} searchTerm={searchTerm} setSearchTerm={setSearchTerm} />
              <div style={{ maxHeight: 200, overflowY: "auto" }}>
                {(searchedData.length ? (searchedData?.map((item, i) => (
                  <TabListItem key={i} item={item} tabKey={tablEle.key} />
                ))) : (<Table columns={[]} showHeader={false} dataSource={[]} />))}
              </div>
            </>
          ) : (
            <Empty />
          )}
          {/* <TabRows
            columns={tablEle.columns}
            tabData={tabData}
            tabKey={tablEle.key}
          /> */}
        </TabPane>
      ))}
    </Tabs>
  ) : (
    <ShareModalSkeleton />
  );
}

const ShareFinalModal = (props) => {
  const dispatch = useDispatch();
  const Notify = notify(dispatch);
  const [shareLoading, setShareLoading] = useState(false);
  const tablesData = useSelector((state) => state.fileBrowser.shareTableData);
  const applicationSettingsData = useSelector((state) => state.app.applicationSettingsData);
  const currentUser = useSelector((state) => state.app.applicationSettingsData.userData.user);
  const isShareVisible = useSelector(
    (state) => state.fileBrowser.isShareModalVisible
  );
  const clickedRecord = useSelector(
    (state) => state.fileBrowser.clickedContextItemDetails.clickedRecord
  );
  const fbRequests = requests.filebrowser(dispatch, applicationSettingsData.settings);
  const init = {
    user: [],
    role: [],
    organization: [],
  };
  const [sharedInfo, setSharedInfo] = useState(init);

  const handleCancel = () => {
    setShareLoading(false)
    dispatch(fileBrowserActions.setShareModalVisibility());
    dispatch(
      fileBrowserActions.setContextMenuItemDetails({ contextItem: null })
    );
    dispatch(fileBrowserActions.setShareTableData(null))
    setSharedInfo(init)
  };

  const updateSharedInfo = (data, retrievedInfo) => {
    const updatedResult = { ...data }
    const { user, role, organization } = retrievedInfo
    if (user.length > 0) {
      updatedResult["user"] = updatedResult["user"].map(e => {
        const isRetrieved = user.find(f => f.id === e.id)
        if (isRetrieved) {
          e["permission"] = isRetrieved.permission
          e["checked"] = true
        }
        return e
      })
    }
    if (role.length > 0) {
      updatedResult["role"] = updatedResult["role"].map(e => {
        const isRetrieved = role.find(f => f.id === e.id)
        if (isRetrieved) {
          e["permission"] = isRetrieved.permission
          e["checked"] = true
        }
        return e
      })
    }
    if (organization.length > 0) {
      updatedResult["organization"] = updatedResult["organization"].map(e => {
        const isRetrieved = organization.find(f => f.id === e.id)
        if (isRetrieved) {
          e["permission"] = isRetrieved.permission
          e["checked"] = true
        }
        return e
      })
    }
    return updatedResult
  }

  const retrieveSharedInfo = (allInfo) => {
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
            dispatch(fileBrowserActions.setShareTableData(allInfo));
          }
          // if the folder/file has already some shared info
          if (!res.message) {
            const result = Object.entries(res);
            const info = { ...sharedInfo };
            result.forEach((re) => {
              info[re[0]] = re[1];
            });
            const modifiedData = updateSharedInfo(allInfo, info)
            dispatch(fileBrowserActions.setShareTableData(modifiedData));
            setSharedInfo(info)
          }
        },
        (error) => {
          dispatch(fileBrowserActions.setShareModalVisibility());
        }
      );
    }
  }

  const cleanResult = (res) => {
    const { allUsers, allRoles, allOrganizations } = res
    return {
      user: allUsers ? Object.keys(allUsers.users).length === 0 ? [] : allUsers.users : null,
      role: allRoles ? Object.keys(allRoles.roles).length === 0 ? [] : allRoles.roles : null,
      organization: allOrganizations ? Object.keys(allOrganizations.organisations).length === 0 ? [] : allOrganizations.organisations : null
    }
  }

  const addKeys = (tableDataObj) => {
    let { user, role, organization } = tableDataObj
    if (user && user.length) {
      user = user.map(e => ({ ...e, checked: false, permission: null }))
    }
    if (role && role.length) {
      role = role.map(e => ({ ...e, checked: false, permission: null }))
    }
    if (organization && organization.length) {
      organization = organization.map(e => ({ ...e, checked: false, permission: null }))
    }
    return { user, role, organization }
  }

  useEffect(() => {
    if (isShareVisible) {
      const payloadObj = {
        provide: {
          provideUsers: "true",
          provideRoles: "true",
          provideOrganizations: "true",
        },
      };
      if (!currentUser.organization) payloadObj["provide"]["id"] = "all";
      const options = payloadObj;
      fbRequests.postShareTableData(
        options,
        "core/share/fetchInfo",
        (res) => {
          const cleanedResultObj = cleanResult(res)
          const updatedResult = addKeys(cleanedResultObj)
          // dispatch(fileBrowserActions.setShareTableData(updatedResult));
          retrieveSharedInfo(updatedResult);
        },
        (error) => {
          console.log(error);
        }
      );
    }
  }, [isShareVisible]);

  const filteredList = (col) => {
    if (!tablesData[col]) return null
    return tablesData[col].filter((ele) => {
      const isRetrieved = sharedInfo[col].some((e) => (e.id === ele.id && e.permission === ele.permission));
      if (!ele.checked || (ele.checked && isRetrieved)) return false;
      return true;
    });
  };

  const revokedList = (col) => {
    if (!sharedInfo[col]) return null
    return sharedInfo[col].filter((ele) => {
      const isSelected = tablesData[col].some((e) => ((e.id === ele.id) && (e.checked) && (e.permission === ele.permission)));
      if (isSelected) return false;
      return true;
    });
  };

  const buildObj = (type) => {
    const func = type === "new" ? filteredList : revokedList;
    let obj = {
      user: func("user"),
      role: func("role"),
      organization: func("organization"),
    }
    return Object.fromEntries(Object.entries(obj).filter(([_, v]) => v != null))
  };

  const addToOptions = (obj, options, type) => {
    // obj here is share or revoke obj and is of structure {user:[], role:[], organization:[]}
    if ((obj.user?.length || obj.role?.length || obj.organization?.length) >= 1) {
      const refinedObj = Object.fromEntries(
        Object.entries(obj).filter((e) => {
          if (e[1].length > 0) {
            e[1] = e[1].map(f => ({ "id": f.id, "permission": f.permission }))
            return e[1]
          }
          return false
        })
      );
      return { ...options, [type]: refinedObj };
    }
    return options;
  };

  const onShare = () => {
    // let shareSuccessMessage = {
    //   "folder": "The selected folder privileges are updated successfully.",
    //   "file": "The selected file privileges are updated successfully.",
    //   "datasource": "The selected dataSource privileges are updated successfully."
    // }
    let options = { ...props.shareOptions };
    const newlyShared = buildObj("new");
    const revokedInfo = buildObj("revoke");
    const cond =
      (newlyShared.user?.length ||
        newlyShared.role?.length ||
        newlyShared.organization?.length) >= 1 ||
      (revokedInfo.user.length ||
        revokedInfo.role?.length ||
        revokedInfo.organization?.length) >= 1;
    if (!cond) {
      console.log("error")
      Notify.error({
        message: "Please select the checkbox to share or revoke",
        type: "Network Call",
      });
      return
    }
    setShareLoading(true)
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
          // Notify.success({
          //   message: res.message,
          //   type: "Network Call",
          // });
          handleCancel();
        },
        (error) => {
          //
          handleCancel();
        }
      );
    }
  };

  return (
    <Modal
      data-testid="hi-file-browser-shareFinalModal"
      className="share-final-modal"
      centered={true}
      bodyStyle={{ padding: "8px 24px" }}
      title={"Share Options"}
      visible={isShareVisible}
      onOk={onShare}
      okText={"Share"}
      confirmLoading={shareLoading}
      onCancel={handleCancel}
      width={800}
      destroyOnClose={true}
      okButtonProps={{ icon: <ShareAltOutlined /> }}
    >
      <ShareTabs tablesData={tablesData} />
    </Modal>
  );
};


export { ShareFinalModal };