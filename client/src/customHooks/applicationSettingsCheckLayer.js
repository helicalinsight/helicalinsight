import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useHistory, useLocation } from "react-router-dom";
import { roleTypes, routesUrl } from "../app/constants";
import { applicationSettings } from "../base/service";
import { loginHandlers } from "../components/hi-login/helperMethods";
import { appActions } from "../redux/actions";
import actionTypes from "../redux/actions/actionTypes";
import { updateActiveRoute } from "./helperMethods";
import qs from "qs";

const { loginUrl, adminHomeUrl, reportCEUrl, userHomeUrl, hiUrl, reportViewUrl } = routesUrl;
const {
  URL_AUTHENTICATION_FAILED,
  IMPERSONATE_LOGIN,
  IMPERSONATE_LOGOUT,
  NORMAL_LOGOUT,
  URL_AUTHENTICATION,
  NORMAL_LOGIN,
  SSO_LOGIN,
} = actionTypes;
const { updateAppDetails, onLogin, storeApplicationSettings, storeShowLicense, updateRoute } =
  appActions;

export function updateAppTitle(activeRoute, routes) {
  let appTitle = "HI: User";
  routes.forEach((item) => {
    if (activeRoute === loginUrl) {
      appTitle = `HI: Login`;
    } else if (activeRoute?.match(adminHomeUrl)) {
      appTitle = "HI: Admin";
    // } else if (activeRoute?.match(reportCEUrl)) {
    //   appTitle = "HI: CE-Report-Create";
    } else if (
      activeRoute.match(item.url) &&
      item.url !== loginUrl &&
      !["Home", "HI"].includes(item.title)
    ) {
      appTitle = `HI: ${item.title}`;
    }
  });
  if (appTitle === "HI: User" && !userHomeUrl.match(activeRoute) && !hiUrl.match(activeRoute)) {
    appTitle = "";
  }
  if (["test"].includes(process.env.NODE_ENV)) {
    return appTitle;
  } else {
    document.title = appTitle;
  }
}

const urlAuthentication = ({
  activeRoute,
  history,
  historyPathname,
  j_organization,
  username,
  password,
  isAuthenticated,
  dispatch,
  pathname,
  parameters,
  successNotification,
}) => {
  loginHandlers({
    pathname,
    activeRoute,
    history,
    logType: URL_AUTHENTICATION,
    urlPath: historyPathname,
    userDetails: {
      organization: j_organization,
      username: username,
      password: password,
    },
    dispatch,
    nxtRoute: "",
    isAuthenticated,
    parameters,
    successNotification
  });
};

export const applicationSettingsHandler = ({
  history,
  dispatch,
  nxtRoute,
  pathname,
  logType,
  activeRoute,
  parameters,
  report,
}) => {
  let nxtActiveRoute = "";
  
    applicationSettings(
      dispatch,
      (res) => {
        let data = JSON.parse(res.data);
        if (data.license) {
          dispatch(storeShowLicense(true));
        }
        if (!report) {
          if (!(data.licenseDetails?.expiryDays === 0)) {
            if (data.userData.user.name) {
              dispatch(onLogin(data));
              if (nxtRoute && nxtRoute !== loginUrl) {
                nxtActiveRoute = nxtRoute;
              } else if (pathname !== loginUrl && pathname) {
                nxtActiveRoute = pathname;
              } else {
                nxtActiveRoute = data.userData.user.roles?.includes(roleTypes.roleAdmin)
                  ? adminHomeUrl
                  : userHomeUrl;
              }
              if (parameters) {
                let { j_organization,username,password,authToken,successNotification,...rest } = parameters
                nxtActiveRoute += qs.stringify(rest,{ arrayFormat: 'repeat',addQueryPrefix:true })
              }
              dispatch(
                updateAppDetails({
                  nxtRoute: "",
                  logType: logType === IMPERSONATE_LOGOUT ? NORMAL_LOGIN : logType,
                })
              );
              if (nxtActiveRoute === activeRoute && logType === URL_AUTHENTICATION) {
                history.push(nxtActiveRoute);
              }
            } else {
              if(logType !== NORMAL_LOGOUT) {
                if (pathname !== loginUrl && pathname) {
                  nxtRoute = pathname;
                  if (parameters) {
                    let { j_organization,username,password,authToken,successNotification,...rest } = parameters
                    nxtRoute += qs.stringify(rest,{ arrayFormat: 'repeat',addQueryPrefix:true })
                  }
                } else {
                  nxtRoute = "";
                }
              }
              nxtActiveRoute = loginUrl;
              dispatch(
                updateAppDetails({
                  nxtRoute,
                })
              );
              activeRoute === loginUrl && history.push(loginUrl);
            }
            dispatch(updateRoute(nxtActiveRoute));
          }
          dispatch(
            updateAppDetails({
              isApplicationSettingsServiceCheck: true,
              isUrlAuthenticating: false,
            })
          );
        }
        dispatch(storeApplicationSettings(data));
      },
      (err) => {
        console.log("applicationSettings service failed");
      }
    );
};

//    ------------------------------------------------------------------------------------------------------------------

export default function useApplicationSettingsCheckLayer() {
  let nxtRoute = useSelector((state) => state.app.nxtRoute);
  let logType = useSelector((state) => state.app.logType);
  let isUrlAuthenticating = useSelector((state) => state.app.isUrlAuthenticating);
  let appSuccessNotification = useSelector((state) => state.app.applicationSettingsData?.settings?.successNotification);
  const routes = useSelector((state) => state.app.routes);
  const isApplicationSettingsServiceCheck = useSelector((state) => state.app.isApplicationSettingsServiceCheck);
  const isAuthenticated = useSelector((state) => state.app.isAuthenticated);
  const activeRoute = useSelector((state) => state.app.activeRoute);
  const history = useHistory();
  const { location: historyLocation } = history;
  const { pathname: historyPathname, search: historySearch } = historyLocation;
  let parameters = qs.parse(historySearch.split("").splice(1).join(""));
  let { j_organization, username, password, authToken, successNotification, ...rest } = parameters;
  if(successNotification) {
    successNotification = successNotification.toLowerCase() === "true";
  }
  const location = useLocation();
  const { pathname } = location;
  const dispatch = useDispatch();

  useEffect(() => {
    if (activeRoute) {
      (pathname !== activeRoute) && history.push(activeRoute);
      updateAppTitle(activeRoute, routes);
    }
  }, [activeRoute]);

  useEffect(() => {
    if (
      pathname !== activeRoute &&
      activeRoute &&
      logType !== URL_AUTHENTICATION_FAILED &&
      !(j_organization || username || password || authToken) &&
      pathname !== loginUrl
    ) {
      updateActiveRoute({ pathname, activeRoute, isAuthenticated, dispatch, history, parameters });
    }
  }, [pathname]);

  useEffect(( ) => {
    if(isApplicationSettingsServiceCheck) {
			if(!(j_organization || username || password || authToken)) {
        if(!Object.keys(rest).length && activeRoute !== pathname) {
          dispatch(updateRoute(pathname));
        }
      }
    }
  }, [Object.keys(parameters).length])

  useEffect(() => {
    if (
      !isApplicationSettingsServiceCheck &&
      !isUrlAuthenticating &&
      logType !== IMPERSONATE_LOGIN &&
      (!(username || password || j_organization || authToken) ||
        logType === URL_AUTHENTICATION_FAILED ||
        logType === NORMAL_LOGOUT)
    ) {
      applicationSettingsHandler({
        dispatch,
        nxtRoute: logType === IMPERSONATE_LOGOUT ? adminHomeUrl : nxtRoute,
        pathname,
        history,
        logType,
        activeRoute,
        parameters,
        historySearch,
      });
    }
  }, [isApplicationSettingsServiceCheck, isUrlAuthenticating]);

  useEffect(() => {
    if (username || password || j_organization) {
      dispatch(
        updateAppDetails({
          isApplicationSettingsServiceCheck: false,
          isUrlAuthenticating: true,
        })
      );
      urlAuthentication({
        history,
        dispatch,
        pathname,
        historyPathname,
        j_organization,
        username,
        password,
        isAuthenticated,
        activeRoute,
        parameters,
        successNotification: (typeof successNotification === "boolean") ? successNotification : appSuccessNotification,
      });
    }
    if (authToken) {
      dispatch(
        updateAppDetails({
          // SSO_LOGIN
          isApplicationSettingsServiceCheck: false,
          isUrlAuthenticating: true,
        })
      );
      loginHandlers({
        parameters,
        history,
        logType: SSO_LOGIN,
        dispatch,
        activeRoute,
        nxtRoute: "",
        urlPath: historyPathname,
        pathname,
        isAuthenticated,
        successNotification: (typeof successNotification === "boolean") ? successNotification : appSuccessNotification,
      });
    }
  }, [historySearch]);

  return rest
}
