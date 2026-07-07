import { Row, Col, Tooltip } from "antd";
import { useState, useEffect, useRef } from "react";
import { Button, Menu, Skeleton, Card, Typography } from "antd";
import {
  BankOutlined,
  RedoOutlined,
  UsergroupAddOutlined,
} from "@ant-design/icons";
import { useDispatch, useSelector } from "react-redux";
import { NavLink, Route, Switch } from "react-router-dom";
import { useRouteMatch, useLocation } from "react-router-dom";
import {
  HIUserEditableTable,
  HIOrgEditableTable,
  HIRoleEditableTable,
  HIAddOrgFormWithDrawer,
  HIAddRoleFormWithDrawer,
  HIAddUserFormWithDrawer,
  HIEditUserDetails,
} from "./components";
import { updateVisibleDrawers } from "../../../../redux/actions/admin.actions";
import "./index.scss";
import {
  fetchOrganizationData,
  fetchRolesData,
  fetchUsersData,
} from "./helperMethods";
import { isEqual } from "lodash-es";
import LoadingBar from "../../../common/components/hi-loading-bar";
import OrgRoleSkeleton from "../../../common/custom-icons/CustomSkeletons/userManagement/OrgRoleSkeleton";
import CustomUserSkeleton from "../../../common/custom-icons/CustomSkeletons/userManagement/CustomUserSkeleton";

const { Text } = Typography;

const HIUserManagement = ({ apiRef, handleAbort }) => {
  const userData = useSelector((state) => state.admin.userData);
  const userDataCheck = !userData?.length;
  const orgData = useSelector((state) => state.admin.orgData);
  const orgDataCheck = !orgData?.length;
  const roleData = useSelector((state) => state.admin.roleData);
  const roleDataCheck = !roleData?.length;
  const visible = useSelector((state) => state.admin.visibleDrawersUM);
  const [isLoading, setIsLoading] = useState({
    userTable: userDataCheck,
    orgTable: orgDataCheck,
    roleTable: roleDataCheck,
    refreshBtn: false,
  });
  // #4194 - start
  const areEqual = (prevProps, nextProps) => {
    return isEqual(prevProps.user, nextProps.user);
  };
  const { applicationSettingsData } = useSelector((state) => state.app);
  const organization = applicationSettingsData.userData?.user?.organization;
  const user = useSelector(
    (state) => state.app.applicationSettingsData?.userData,
    areEqual
  );
  // const organization = "wewe";
  let orgCheck = organization ? ["2"] : ["1"];
  const [currentMenuItem, setCurrentMenuItem] = useState(orgCheck);
  // #4194 - end

  let match = useRouteMatch();
  let location = useLocation();

  const { url, path } = match;
  const { pathname } = location;

  const dispatch = useDispatch();


  // const Notify = notify(dispatch);

  useEffect(() => {
    fetchUsersData(dispatch, false, () => {
      setIsLoading((state) => ({
        ...state,
        userTable: false,
        refreshBtn: false,
      }));
    });
    fetchOrganizationData(dispatch, false, () => {
      setIsLoading((state) => ({
        ...state,
        orgTable: false,
        refreshBtn: false,
      }));
    });
    fetchRolesData(dispatch, false, () => {
      setIsLoading((state) => ({
        ...state,
        roleTable: false,
        refreshBtn: false,
      }));
    });
  }, [user]);

  const userManagementTabsData = [
    ...(organization
      ? []
      : [
          {
            id: "1",
            tab: "Organizations",
            tabPath: "organizations",
          },
        ]),
    {
      id: "2",
      tab: "Roles",
      tabPath: "roles",
    },
    {
      id: "3",
      tab: "Users",
      tabPath: "users",
    },
  ];

  const removeLoading = () => {
    setIsLoading((state) => ({
      ...state,
      orgTable: false,
      roleTable: false,
      userTable: false,
      refreshBtn: false,
    }));
  };

  const reloadOnClick = () => {
    if (pathname.search("organisations") !== -1) {
      setIsLoading((state) => ({
        ...state,
        orgTable: true,
        refreshBtn: true,
      }));
      return fetchOrganizationData(dispatch, true, () => {
        setIsLoading((state) => ({
          ...state,
          orgTable: false,
          refreshBtn: false,
        }));
      });
    } else if (pathname.search("roles") !== -1) {
      setIsLoading((state) => ({
        ...state,
        roleTable: true,
        refreshBtn: true,
      }));
      return fetchRolesData(dispatch, true, () => {
        setIsLoading((state) => ({
          ...state,
          roleTable: false,
          refreshBtn: false,
        }));
      });
    } else if (pathname.search("users") !== -1) {
      setIsLoading((state) => ({
        ...state,
        userTable: true,
        refreshBtn: true,
      }));

      return fetchUsersData(dispatch, true, () => {
        setIsLoading((state) => ({
          ...state,
          userTable: false,
          refreshBtn: false,
        }));
      });
    } else {
      setIsLoading((state) => ({
        ...state,
        orgTable: true,
        refreshBtn: true,
      }));

      return fetchOrganizationData(dispatch, true, () => {
        setIsLoading((state) => ({
          ...state,
          orgTable: false,
          refreshBtn: false,
        }));
      });
    }
  };

  let result =
    pathname.search("organisations") !== -1
      ? ["1"]
      : pathname.search("roles") !== -1
      ? ["2"]
      : pathname.search("users") !== -1
      ? ["3"]
      : ["1"];

  useEffect(() => {
    const defaultActive = organization
      ? userManagementTabsData.find((item) => item.id === "2")
      : userManagementTabsData.find((item) => item.id === "1");
    if (pathname === `${path}/${defaultActive.tabPath}`) {
      setCurrentMenuItem(defaultActive.id);
    }
  }, [pathname]);

  return (
    <Skeleton paragraph={{ rows: 10 }} loading={isLoading.component}>
      <div className="hi-user-management">
        <Row
          gutter={[8, 8]}
          justify="center"
          className="hi-user-management-box"
        >
          <Col span={24}>
            <Card hoverable="true">
              <Row className="hi-card">
                {organization ? null : (
                  <Col xs={24} lg={9}>
                    <Row
                      className="hi-org-card hi-remove-right-border"
                      justify="center"
                      align="middle"
                    >
                      <Col>
                        <Card className="hi-card-body" bordered={false}>
                          <BankOutlined className="hi-icon" />
                        </Card>
                      </Col>
                      <Col>
                        <Row className="hi-ellipses" justify="center">
                          <Col span={24}>
                            <Text className="hi-title-count">
                              {orgData.length}
                            </Text>
                          </Col>
                          <Col>
                            <Text ellipses="true" className="hi-title">
                              Organizations
                            </Text>
                          </Col>
                        </Row>
                      </Col>
                    </Row>
                  </Col>
                )}
                <Col xs={24} lg={organization ? 18 : 9}>
                  <Row className="hi-user-card" justify="center" align="middle">
                    <Col>
                      <Card className="hi-card-body" bordered={false}>
                        <UsergroupAddOutlined className="hi-icon" />
                      </Card>
                    </Col>
                    <Col>
                      <Row className="hi-ellipses" justify="center">
                        <Col span={24}>
                          <Text className="hi-title-count">
                            {userData.length}
                          </Text>
                        </Col>
                        <Col>
                          <Text
                            ellipses="true"
                            className="hi-title hi-ellipses"
                          >
                            Users
                          </Text>
                        </Col>
                      </Row>
                    </Col>
                  </Row>
                </Col>
                <Col xs={24} lg={6}>
                  <Card className="hi-card-body" bordered={false}>
                    <Row justify="center" gutter={[16, 16]}>
                      <Col span={24}>
                        <Button
                          className="hi-um-button"
                          type="primary"
                          onClick={() => {
                            dispatch(
                              updateVisibleDrawers({
                                key: "addUser",
                                status: true,
                              })
                            );
                          }}
                        >
                          Add User
                        </Button>
                        <HIAddUserFormWithDrawer setIsLoading={setIsLoading}/>
                      </Col>
                      {
                        // #4194 - start
                        organization ? null : (
                          <Col span={24}>
                            <Button
                              onClick={() => {
                                dispatch(
                                  updateVisibleDrawers({
                                    key: "addOrg",
                                    status: true,
                                  })
                                );
                              }}
                              className="hi-um-button"
                              type="primary"
                            >
                              Add Organization
                            </Button>
                            <HIAddOrgFormWithDrawer />
                          </Col>
                        )
                        // #4194 - end
                      }
                      <Col span={24}>
                        <Button
                          onClick={() => {
                            dispatch(
                              updateVisibleDrawers({
                                key: "addRole",
                                status: true,
                              })
                            );
                          }}
                          className="hi-um-button"
                          type="primary"
                        >
                          Add Role
                        </Button>
                        <HIAddRoleFormWithDrawer />
                      </Col>
                      {visible.editUser && <HIEditUserDetails />}
                    </Row>
                  </Card>
                </Col>
              </Row>
            </Card>
          </Col>
          <Col span={24}>
            <Card className="hi-card" hoverable="true">
              {/* className="hi-card" */}
              <Row>
                <Col span={23}>
                  <Menu
                    onClick={(e) => {
                      setCurrentMenuItem(e.key);
                    }}
                    defaultSelectedKeys={result}
                    selectedKeys={[currentMenuItem]}
                    mode="horizontal"
                  >
                    {userManagementTabsData
                      .filter((item) => {
                        // #4194 - start
                        if (item.id === "1") {
                          return !organization;
                        }
                        return true;
                        // #4194 - end
                      })
                      .map((eachData) => {
                        return (
                          <Menu.Item
                            key={eachData.id}
                            onClick={() => {
                              // dispatch(updateRoute(`${url}/${eachData.tabPath}`));
                            }}
                          >
                            <NavLink to={`${url}/${eachData.tabPath}`}>
                              {eachData.tab}
                            </NavLink>
                          </Menu.Item>
                        );
                      })}
                  </Menu>
                </Col>
                <Col className="hi-um-reload-btn" span={1}>
                <Tooltip placement="top" title={'Refresh'}>
                  <Button
                    type="text"
                    loading={isLoading.refreshBtn}
                    onClick={() => {
                      apiRef.current = reloadOnClick();
                    }}
                    icon={<RedoOutlined className="hi-um-icon" />}
                  />
                  </Tooltip>
                </Col>
                <Col className="hi-um-table-container" span={24}>
                  <Switch>
                    <Route path={`${path}/roles`}>
                    {(isLoading.roleTable) ? <><LoadingBar handleClick={() => {handleAbort({setIsLoading, table: 'roleTable'})}} /><OrgRoleSkeleton />
                    </> :
                      <HIRoleEditableTable
                        isLoading={isLoading.roleTable}
                        removeLoading={removeLoading}
                        className="hi-um-user-table"
                      />}
                    </Route>
                    <Route path={`${path}/users`}>
                    {(isLoading.userTable) ? <><LoadingBar handleClick={() => {handleAbort({setIsLoading, table: 'userTable'})}} /><CustomUserSkeleton /></> :
                      <HIUserEditableTable
                        removeLoading={removeLoading}
                        isLoading={isLoading.userTable}
                        visible={visible.addUser}
                        className="hi-um-user-table"
                      />}
                    </Route>
                    {
                      // #4194 - start
                      organization ? null : (
                        <Route>
                          {(isLoading.orgTable) ? <><LoadingBar handleClick={() => {handleAbort({setIsLoading, table: 'orgTable'})}} />
                          <OrgRoleSkeleton />
                          </> :
                          <HIOrgEditableTable
                            path={`${path}/organizations`}
                            removeLoading={removeLoading}
                            isLoading={isLoading.orgTable}
                            className="hi-um-user-table"
                          />}
                        </Route>
                      )
                      // #4194 - end
                    }
                    {/* <Redirect to={`${path}/organisations`} /> */}
                  </Switch>
                </Col>
              </Row>
            </Card>
          </Col>
        </Row>
      </div>
    </Skeleton>
  );
};
export { HIUserManagement };
