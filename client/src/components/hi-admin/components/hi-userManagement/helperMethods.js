import requests from "../../../../base/requests/index.js";
import {
  storeUserData,
  storeOrgData,
  storeRoleData,
} from "../../../../redux/actions/admin.actions";
import { getUserData, getRoleData, getOrgData } from "./utils";
import notify from "../../../hi-notifications/notify.js";
import { Button, Input, Space } from "antd";
import { SearchOutlined } from "@ant-design/icons";
import Highlighter from "react-highlight-words";
import { capitalize, handleReset, handleSearch } from "../hi-recycle-bin/helperMethods.js";

export const getRolesEditTableData = ({activeRecord, roleData}) => {
  const result =  (activeRecord?.orgName === "Null"
      ? [...roleData]
      : [
        ...roleData?.filter(
          (item) => item.orgName === activeRecord.orgName
        ),
        ...activeRecord?.roles.filter((item) => {
          if (roleData.some((obj) => obj.id === item.id)) {
            return false;
          }
          return true;
        }),
      ]).map(ele => {
        const name = `${ele.name ? ele.name : ele.role} | ${ele.orgName ? ele.orgName : activeRecord?.orgName
          }`
        return { ...ele, name };
      })
  console.log({activeRecord, roleData, result})
  return result;
}

export const getTableColumnSearchProps = ({dataIndex, searchText, searchedColumn, searchInput, setSearchText, setSearchedColumn}) => ({
  // filterDropdownOpen: false,
  filterDropdown: ({ setSelectedKeys, selectedKeys, confirm, clearFilters, close=()=> {} }) => (
    <div
      style={{
        padding: 8,
      }}
      onKeyDown={(e) => e.stopPropagation()}
    >
      <Input
        ref={searchInput}
        placeholder={`Search ${capitalize(dataIndex)}`}
        value={selectedKeys[0]}
        onChange={(e) => setSelectedKeys(e.target.value ? [e.target.value] : [])}
        onPressEnter={() => handleSearch(selectedKeys, confirm, dataIndex, setSearchText, setSearchedColumn)}
        style={{
          marginBottom: 8,
          display: 'block',
        }}
      />
      <Space>
        <Button
          type="primary"
          onClick={() => handleSearch(selectedKeys, confirm, dataIndex, setSearchText, setSearchedColumn)}
          icon={<SearchOutlined />}
          size="small"
          style={{
            width: 90,
          }}
        >
          Search
        </Button>
        <Button
          onClick={() => clearFilters && handleReset(clearFilters, setSearchText, confirm)}
          size="small"
          style={{
            width: 90,
          }}
        >
          Reset
        </Button>
      </Space>
    </div>
  ),
  filterIcon: (filtered) => (
    <SearchOutlined
      style={{
        color: filtered ? '#1677ff' : undefined,
      }}
    />
  ),
  onFilter: (value, record) => {
    return record[dataIndex].toString().toLowerCase().includes(value.toLowerCase());
  },
  onFilterDropdownOpenChange: (visible) => {
    if (visible) {
      setTimeout(() => searchInput.current?.select(), 100);
    }
  },
  render: (text) =>
    searchedColumn === dataIndex ? (
      <Highlighter
        highlightStyle={{
          backgroundColor: '#ffc069',
          padding: 0,
        }}
        searchWords={[searchText]}
        autoEscape
        textToHighlight={text ? text.toString() : ''}
      />
    ) : (
      text
    ),
});

export const fetchUsersData = (dispatch, refresh, isLoading) => {
  const Notify = notify(dispatch);
  let apiInstance;
  const makeRequest = (userDataCheck, refresh) => {
    return getUserData({
      refresh: refresh !== undefined ? refresh : false,
      dispatch,
      successCB: (res) => {
        
        if (isLoading) {
          isLoading();
        }
        if (Array.isArray(res.users)) {
          dispatch(storeUserData(res.users));
        } else {
          dispatch(storeUserData([res.users]));
        }
      },
      errorCB: (e) => {
        // Notify.error({ ...e, type: "Network Call" });
      },
      userDataCheck,
      requests,
    });
  };
  dispatch((_, getState) => {
    const userDataCheck = !getState().admin.userData?.length;
    apiInstance = makeRequest(userDataCheck, refresh);
  });
  return apiInstance;
};

export const fetchOrganizationData = (dispatch, refresh, isLoading) => {
  const Notify = notify(dispatch);
  let apiInstance;
  const makeRequest = (orgDataCheck, refresh) => {
    return getOrgData({
      refresh: refresh !== undefined ? refresh : false,
      dispatch,
      successCB: (res) => {
        if (isLoading) {
          isLoading();
        }
        if (Array.isArray(res.organisations)) {
          dispatch(storeOrgData(res.organisations));
        } else {
          dispatch(storeOrgData([res.organisations]));
        }
      },
      errorCB: (e) => {
        // Notify.error({ ...e, type: "Network Call" });
      },
      requests,
      orgDataCheck,
    });
  };
  dispatch((_, getState) => {
    const orgDataCheck = !getState().admin.orgData?.length;
    apiInstance = makeRequest(orgDataCheck, refresh);
  });
  return apiInstance;
};

export const fetchRolesData = (dispatch, refresh, isLoading) => {
  const Notify = notify(dispatch);
  let apiInstance;
  const makeRequest = (roleDataCheck, refresh) => {
    return getRoleData({
      refresh: refresh !== undefined ? refresh : false,
      dispatch,
      successCB: (res) => {
        if (isLoading) {
          isLoading();
        }
        if (Array.isArray(res.roles)) {
          dispatch(storeRoleData(res.roles));
        } else {
          dispatch(storeRoleData([res.roles]));
        }
      },
      errorCB: (e) => {
        // Notify.error({ ...e, type: "Network Call" });
      },
      requests,
      roleDataCheck,
    });
  };
  dispatch((_, getState) => {
    const roleDataCheck = !getState().admin.roleData?.length;
    apiInstance = makeRequest(roleDataCheck, refresh);
  });
  return apiInstance;
};
