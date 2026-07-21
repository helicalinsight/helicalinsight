import { Row, Col, Menu } from "antd";
import { useEffect, useRef, useState } from "react";
import { routesUrl } from "../../../app/constants";
import { appActions } from "../../../redux/actions";
import { useSelector, useDispatch } from "react-redux";
import { NavLink, Route, Switch } from "react-router-dom";
import { useRouteMatch, useHistory } from "react-router-dom";
import { HILicenseCard } from "../components/hi-overview/components";
import { setIsAdminTabsDataSet } from "../../../redux/actions/admin.actions";
import classNames from "classnames";
import TutorialInfo from "../../common/hi-tutorial";
import {
  HIOverview,
  HIUserManagement,
  HISystemDetails,
  HIPlugins,
  HIManagement,
  HIScheduling,
  HIAdfs,
  HIRecycleBin
} from "..";
import TokenUsageDashboard from "../components/hi-management/Components/TokenUsage/TokenUsageDashboard";
import "./hi-adminTabs.scss";

const { adminHomeUrl } = routesUrl;

const recycleBinRouteData = {
  tab: "Recycle Bin",
  tabPath: "/recycle-bin",
  tutorialElementKey: "hi-recycle-bin",
};

const tabs2 = [
  {
    tab: "Overview",
    tabPath: "/overview",
    tutorialElementKey: "hi-overview",
  },
  recycleBinRouteData
];

const systemDetailsRouteData = {
  tab: "System Details",
  tabPath: "/systemdetails",
  tutorialElementKey: "hi-system-details",
};

const usermanagementName = '/usermanagement';

export function getTabActiveStatus({ pathname, eachData }) {
  return (pathname.search(`admin${eachData.tabPath}`) !== -1) || (eachData.tabPath.includes(usermanagementName) && pathname.includes(usermanagementName))
}

const HIAdminTabs = () => {
  const dispatch = useDispatch();
  const [selectedKey, setSelectedKey] = useState('');
  const [adminTabsData, setAdminTabsData] = useState([]);
  const { isAdminTabsDataSet } = useSelector((state) => state.admin);
  let match = useRouteMatch();
  const { url, path } = match;
  const history = useHistory();
  let { location } = history || {};
  let { pathname } = location || {};
  const { applicationSettingsData, activeRoute } = useSelector((state) => state.app);
  const { user, saml } = applicationSettingsData.userData;
  const { organization } = user;
  const orgCheck = organization ? `${usermanagementName}/roles` : `${usermanagementName}/organizations`;
  const apiRef = useRef(null);

  function handleAbort(prop = {}) {
    apiRef.current?.abort(prop);
  }

  const tabsData = [
    {
      tab: "User Management",
      tabPath: orgCheck,
      tutorialElementKey: "hi-user-management",
    },
    {
      tab: "Management",
      tabPath: "/management",
      tutorialElementKey: "hi-management",
    },
    {
      tab: "Schedule",
      tabPath: "/Schedule",
      tutorialElementKey: "hi-Schedule",
    },
    {
      tab: "Plugins",
      tabPath: "/plugins",
      tutorialElementKey: "hi-plugins",
    },
    {
      tab: "Audit",
      tabPath: "/audit",
      tutorialElementKey: "hi-audit",
    }
  ];

  useEffect(() => {
    if (!organization) {
      let dupTabsData = [...tabsData];
      dupTabsData.splice(1, 0, systemDetailsRouteData);
      setAdminTabsData([...tabs2, ...dupTabsData]);
    } else {
      setAdminTabsData([recycleBinRouteData, ...tabsData]);
    }
    dispatch(setIsAdminTabsDataSet(true));
  }, [user]);

  useEffect(() => {
    if (saml.enabled) {
      adminTabsData.push({
        tab: "Adfs",
        tabPath: "/adfs",
      });
    }
  }, [saml]);

  if (adminTabsData.length > 0 && isAdminTabsDataSet) {
    if (
      (activeRoute === adminHomeUrl || activeRoute === `${adminHomeUrl}/`) &&
      (pathname === adminHomeUrl || pathname === `${adminHomeUrl}/`)
    ) {
      dispatch(appActions.updateRoute(path + "" + adminTabsData[0].tabPath));
      setSelectedKey(adminTabsData[0].tab);
    }
  }


  useEffect(() => {
    if (adminTabsData?.length && pathname?.includes(usermanagementName)) {
      setSelectedKey("");
    }
  }, [pathname, adminTabsData])

  return (
    <Row className="hi-admin-container hi-admin-tabs">
      <Col span={24} className="hi-admin-tabs-container">
        {adminTabsData.length > 0 && (
          <Menu selectedKeys={[selectedKey]} onClick={(ele) => setSelectedKey(ele.key === "User Management")} mode="horizontal" inlineCollapsed={false} style={{ width: !organization ? '69.18%' : '49.5%' }} className="hi-tabs-menu">{/* 6013 */}
            {adminTabsData.map((eachData) => {
              return (
                <Menu.Item
                  key={eachData.tab}
                  className={classNames({
                    "menu-item": true,
                    active: getTabActiveStatus({ pathname, eachData }),
                    "menu-first-item":
                      eachData.tab === "Overview" ||
                      (organization && eachData.tab === "User Management"),
                    "menu-last-item": eachData.tab === "Audit" || eachData.tab === "Adfs",
                  })}
                >
                  <NavLink
                    to={`${url}${eachData.tabPath}`}
                    className="link"
                    activeClassName="selected"
                  >
                    <TutorialInfo elementKey={eachData.tutorialElementKey} className="pad-top-10">
                      {eachData.tab}
                    </TutorialInfo>
                  </NavLink>
                </Menu.Item>
              );
            })}
          </Menu>
        )}
      </Col>
      <Col span={24} className="hi-admin-routes-container">
        <Switch>
          {organization && (
            <Route exact path={`${path}/`}>
              <TutorialInfo elementKey="hi-license-details">
                <HILicenseCard />
              </TutorialInfo>
            </Route>
          )}
          <Route path={`${path}/overview`}>
            <HIOverview apiRef={apiRef} handleAbort={handleAbort} />
          </Route>
          <Route path={`${path}/systemdetails`}>
            <HISystemDetails apiRef={apiRef} handleAbort={handleAbort} />
          </Route>
          <Route path={`${path}${usermanagementName}`}>
            <HIUserManagement apiRef={apiRef} handleAbort={handleAbort} />
          </Route>
          <Route path={`${path}/plugins`}>
            <HIPlugins apiRef={apiRef} handleAbort={handleAbort} />
          </Route>
          <Route path={`${path}/audit`}>
            <TokenUsageDashboard apiRef={apiRef} handleAbort={handleAbort} />
          </Route>
          <Route path={`${path}/schedule`}>
            <HIScheduling apiRef={apiRef} handleAbort={handleAbort} />
          </Route>
          <Route path={`${path}/management`}>
            <HIManagement apiRef={apiRef} handleAbort={handleAbort} />
          </Route>
          <Route path={`${path}/adfs`}>
            <HIAdfs apiRef={apiRef} handleAbort={handleAbort} />
          </Route>
          <Route path={`${path}/recycle-bin`}>
            <HIRecycleBin apiRef={apiRef} handleAbort={handleAbort} />
          </Route>
          {/* <Route path="*">
            <HIResults status="404" redirectRoute={path + "" + adminTabsData[0].tabPath} />
          </Route> */}
        </Switch>
      </Col>
    </Row>
  );
};

export default HIAdminTabs;
