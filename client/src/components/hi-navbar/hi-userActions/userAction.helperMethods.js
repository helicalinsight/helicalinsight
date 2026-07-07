import { routesUrl, roleTypes } from "../../../app/constants";
import { axiosLogout, impersonateUserLogout } from "../../../base/service";
import { applicationSettingsHandler } from "../../../customHooks/applicationSettingsCheckLayer";
import { appActions, hcrActions } from "../../../redux/actions";
import actionTypes from "../../../redux/actions/actionTypes";
import { resetNotificationData } from "../../../redux/actions/useractions.actions";
import notify from "../../hi-notifications/notify";

const { resetStore, setIsLogoutManually, updateRoute } = appActions;
const {
  adminHomeUrl,
  loginUrl,
  userHomeUrl,
  dataSourceUrl,
  metadataUrl,
  reportCEUrl,
  helicalReportUrl,
  cannedReportsUrl,
  dashboardDesignerUrl,
  reportViewUrl,
  instantBIUrl,
  hiUrl,
} = routesUrl;
const { NORMAL_LOGOUT, IMPERSONATE_LOGOUT } = actionTypes;

export const normalLogout = (dispatch, isSessionOver, activeRoute) => {
  // dispatch(setIsLogoutManually());
  axiosLogout(
    dispatch,
    (res) => {
      if (res.status === 200) {
        // dispatch(resetNotificationData());
        dispatch(resetStore({ logType: NORMAL_LOGOUT, isSessionOver, activeRoute }));
        // dispatch(updateRoute(loginUrl));
      }
    },
    (error) => {
      // dispatch(setIsLogoutManually());
    }
  );
};

export function logoutHandler({ user, dispatch, isSessionOver, activeRoute }) {
  if (!isSessionOver && user.actualUserName !== user.name) {
    impersonateUserLogout(
      dispatch,
      (res) => {
        if (res.status === 200) {
          // dispatch(saveActualApplicationSettings({}));
          dispatch(resetStore({ logType: IMPERSONATE_LOGOUT }));
          // applicationSettingsHandler({ dispatch, nxtRoute: adminHomeUrl });
          // let data = JSON.parse(res.data);
          // notify(dispatch).success({
          //   message: data.message,
          //   status: "1",
          //   type: "Network Call",
          // });
        }
      },
      (err) => {
        let data = JSON.parse(err.data);
        // notify(dispatch).error({
        // 	message: data.message,
        // 	status: '0',
        // 	type: 'Network Call'
        // });
      }
    );
  } else {
    normalLogout(dispatch, isSessionOver, "/");
  }
}
export const executeTutorial = ({ activeRoute, dispatch, toggleMainNavbar, user }) => {
  if (activeRoute.match(adminHomeUrl)) {
    if (user.organization) {
      dispatch(appActions.updateRoute("/admin"));

      dispatch(toggleMainNavbar(true));
      dispatch(
        appActions.changeTutorialData({
          moduleKey: "adminWithOrg",
          key: "hi-admin-sidebar",
        })
      );
    } else {
      dispatch(appActions.updateRoute("/admin/overview"));
      dispatch(toggleMainNavbar(true));
      dispatch(
        appActions.changeTutorialData({
          moduleKey: "admin",
          key: "hi-admin-sidebar",
        })
      );
    }
  } else if (activeRoute.match(userHomeUrl)) {
    if (user.roles.includes(roleTypes.roleUser)) {
      dispatch(
        appActions.changeTutorialData({
          moduleKey: "roleUser",
          key: "hi-admin-sidebar",
        })
      );
    }
  } else if (activeRoute.match("datasource")) {
    dispatch(appActions.updateRoute("/datasource/all"));
    dispatch(
      appActions.changeTutorialData({
        moduleKey: "datasource",
        key: "hi-datasource",
      })
    );
  } else if (activeRoute.match("report-viewer")) {
    dispatch(appActions.updateRoute("/report-viewer"));
    dispatch(
      appActions.changeTutorialData({
        moduleKey: "reportviewer",
        key: "hr-report-viewer",
      })
    );
  } else if (activeRoute.match("metadata")) {
    //4530
    dispatch(
      appActions.changeTutorialData({
        moduleKey: "metadata",
        key: "hi-metadata-create",
      })
    );
    return;
  } else if (activeRoute.match("helical-report")) {
    dispatch(
      appActions.changeTutorialData({
        moduleKey: "hreport",
        key: "hr-metadata",
      })
    );
  } else if (activeRoute.match(dashboardDesignerUrl)) {
    dispatch(
      appActions.changeTutorialData({
        moduleKey: "designer",
        key: "hi-designer-save",
      })
    );
  } else if (activeRoute.match(cannedReportsUrl)) {
    dispatch(hcrActions.handleHcrSidebarPaneActiveKey('canvas'));
    dispatch(
      appActions.changeTutorialData({
        moduleKey: "hcr",
        key: "hcr-tabs",
      })
    );
  } else if (activeRoute.match(instantBIUrl)) {
    dispatch(
      appActions.changeTutorialData({
        moduleKey: "instantBI",
        key: "hi-instant-bi-save",
      })
    );
  }
};
