import React, { useEffect, useMemo } from "react";
import { useDispatch, useSelector, useStore } from "react-redux";
import { Empty, Layout, Modal } from "antd";
import {
  Switch,
  Route,
  Redirect,
  useHistory,
  useLocation,
  Prompt,
} from "react-router-dom";
import {
  HILogin,
  AdminPage,
  DatasourcePage,
  ReportViewer,
  HIReportCEPage,
  HelicalReports,
  CannedReportsPage,
  DashboardDesigner,
  HIMetadataPage,
  Welcome,
  HILicenseExpiredPage,
  HICubePage,
  HIInstantBI,
  HIAGENT
} from "../../pages";
import useApplicationSettingsCheckLayer from "../../customHooks/applicationSettingsCheckLayer";
import ErrorBoundry from "../../errorBoundry/ErrorBoundry";
import { HIResults } from "..";
import { defaultRoutes, roleTypes, routesUrl } from "../../app/constants";
import { appActions } from "../../redux/actions";

const { Content } = Layout;

const {
  loginUrl,
  adminHomeUrl,
  dataSourceUrl,
  metadataUrl,
  reportCEUrl,
  helicalReportUrl,
  agentUrl,
  cannedReportsUrl,
  dashboardDesignerUrl,
  userHomeUrl,
  hiUrl,
  reportViewUrl,
  cubeUrl,
  instantBIUrl,
  hiceUrl,
  hwfUrl
} = routesUrl;
export const HIRoutes = () => {
  const urlObj = useApplicationSettingsCheckLayer();
  const applicationSettingsData = useSelector(
    (state) => state.app.applicationSettingsData
  );
  const routes = useSelector((state) => state.app.routes);
  const isApplicationSettingsServiceCheck = useSelector(
    (state) => state.app.isApplicationSettingsServiceCheck
  );
  const isSessionOver = useSelector((state) => state.app.isSessionOver);
  const isAuthenticated = useSelector((state) => state.app.isAuthenticated);
  const activeRoute = useSelector((state) => state.app.activeRoute);
  const accessDeniedInfo = useSelector((state) => state.app.accessDeniedInfo);
  const location = useLocation();
  const { pathname } = location;
  const store = useStore();

  const routesMap = useMemo(
    () => ({
      [adminHomeUrl]: <AdminPage />,
      [dataSourceUrl]: <DatasourcePage />,
      [metadataUrl]: <HIMetadataPage />,
      [cubeUrl]: <HICubePage />,
      [reportCEUrl]: <HIReportCEPage />,
      [helicalReportUrl]: <HelicalReports />,
      [agentUrl]:<HIAGENT/>,
      [cannedReportsUrl]: <CannedReportsPage />,
      [dashboardDesignerUrl]: <DashboardDesigner />,
      [loginUrl]: <HILogin />,
      [userHomeUrl]: <Welcome />,
      [hiUrl]: <Welcome />,
      [reportViewUrl]: <ReportViewer />,
      [instantBIUrl]: <HIInstantBI />,
      [hiceUrl]: <Empty />,
      [hwfUrl]: <Empty />
    }),
    []
  );

  let filteredRoutes = routes.filter(
    (item) =>
      item.url === loginUrl ||
      item.url === userHomeUrl || // custom role or no role
      item.accessbleForAll || //  eg: '/hi'
      applicationSettingsData.userData.user.roles?.some((role) =>
        item.roles.includes(role)
      )
  );

  filteredRoutes = filteredRoutes.filter((item) => {
    if(applicationSettingsData.settings?.experimentalModules?.includes(item.expId)){
     return applicationSettingsData.showExperimentalFeatures;
    }
    return true;
  })

  if (process.env.NODE_ENV === "test") {
    return <HelicalReports />;
  }
  const checkWhen = () => {
    let routeStoreMap = {
      [metadataUrl]: "metadata",
      [cubeUrl]: "cube",
      [helicalReportUrl]: "hreport",
      [dashboardDesignerUrl]: "designer",
      [instantBIUrl]: "instantBI",
      [agentUrl]: "agent",
      [cannedReportsUrl]: "cannedReports",
      // [dataSourceUrl]: 'datasource'
    };
    let storeName = routeStoreMap[activeRoute];
    if (!(activeRoute in routeStoreMap && pathname === activeRoute)) {
      // if(activeRoute.includes(dataSourceUrl) && (pathname === activeRoute)) {
      //   storeName = routeStoreMap[dataSourceUrl];
      // } else {
      return false;
      // }
    }
    const undoRedoReducers = ["hreport", "designer", "cannedReports", "metadata"];
    let hasUnsavedData = undoRedoReducers.includes(storeName) ? store.getState()[storeName].present.hasUnsavedData : store.getState()[storeName].hasUnsavedData;
    // console.log('in checking when', hasUnsavedData)
    if (typeof hasUnsavedData === "boolean") return hasUnsavedData;
    return false;
  };

  return (
    isApplicationSettingsServiceCheck &&
    (applicationSettingsData.license?.remainingDays === 0 ? (
      <HILicenseExpiredPage />
    ) : (
      <>
        {isSessionOver && <Modal className="session-modal" visible={true} />}
        <div id="report-drilldown-menu"></div>
        <Content style={{ height: "100%" }}>
          <Switch>
            {filteredRoutes.map((item) => {
              let component = routesMap[item.url];
              if (item.url === loginUrl) {
                if (isAuthenticated && pathname === loginUrl) {
                  // history.push()
                  return <Redirect to={activeRoute} />;
                }
                return (
                  <Route key={item.url} exact path={item.url}>
                    {component}
                  </Route>
                );
              }
              // if (!isAuthenticated) {
              //   return <Redirect to={loginUrl} />;
              // }
              return (
                <Route key={item.url} path={item.url}>
                  <Prompt
                    // when={/metadata/.test(activeRoute) || /helical-report/.test(activeRoute) || /dashboard-designer/.test(activeRoute)}
                    when={checkWhen()}
                    message={() =>
                      `Are you sure you want to leave ${defaultRoutes.find(
                        (ele) => ele.addInNavbar && activeRoute.match(ele.url)
                      )?.title
                      } ?`
                    }
                  />
                  {React.cloneElement(component, { urlObj })}
                </Route>
              );
            })}
            <Route path="/access-denied">
              <HIResults
                status="404"
                btnContent="Go To HomePage"
                title={
                  accessDeniedInfo?.title
                    ? accessDeniedInfo?.title
                    : "Access Denied"
                }
                subTitle={
                  accessDeniedInfo?.subTitle
                    ? accessDeniedInfo?.subTitle
                    : "Access Denied. You don't have sufficient privileges to access the requested resource."
                }
                redirectRoute={
                  applicationSettingsData.userData.user.roles?.includes(
                    roleTypes.roleAdmin
                  )
                    ? routesUrl.adminHomeUrl
                    : routesUrl.userHomeUrl
                }
              // subTitle="Sorry, you don't have the proper authority to visit this page."
              />
            </Route>
            <Route path="*">
              <HIResults
                status="404"
                btnContent="Go To HomePage"
                redirectRoute={
                  applicationSettingsData.userData.user.roles?.includes(
                    roleTypes.roleAdmin
                  )
                    ? routesUrl.adminHomeUrl
                    : routesUrl.userHomeUrl
                }
              // subTitle="Sorry, you don't have the proper authority to visit this page."
              />
            </Route>
          </Switch>
        </Content>
      </>
    ))
  );
};
