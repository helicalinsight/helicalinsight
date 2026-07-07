import produce from "immer";
import { roleTypes, routesUrl, serviceUrls } from "../../app/constants";
// import { URL_AUTHENTICATION } from '../../components/hi-login/helperMethods';
import actionTypes from "../actions/actionTypes";
import initialStates from "./initialStates";
import tutorialItems from "../../components/common/hi-tutorial/tutorial-items";

const {
  APPLICATIONSETTINGS_SERVICECHECK,
  // ACTUAL_APPLICATIONSETTINGS,
  HAS_ERROR,
  ROUTE_CHANGE,
  APP_DETAILS,
  SESSION_EXPIRY,
  ISLOGOUTMANUALLY,
  ON_LOGIN,
  // RESTORE_APPLICATIONSETTINGS,
  TOGGLE_SIDEBAR,
  TOGGLE_NAVBAR,
  IS_SESSION_OVER,
  APPLICATION_SETTINGS,
  SHOW_LICENSE,
  IS_LICENSE_RENDERED,
  RESET_STORE,
  VIEW_MODE_INFO,
  CHANGE_TUTORIAL_DATA,
  TOGGLE_NAVBAR_ARROW,
  EDIT_MODE_INFO,
  CHANGE_LAST_MODIFIED,
  LOAD_REPORT_DATA,
  SET_ACCESS_DENIED_INFO,
  ABOUT_TO_CHANGE_ROUTE,
  SHOULD_BLOCK_NAVIGATION,
  TOGGLE_VIEWER_EMAIL,
  TOGGLE_VIEWER_SCHEDULE,
  SETSHORTCUTCURRENTLOCATION,
  UPDATEKEYSTRIGGER,
} = actionTypes;

const { roleAdmin, roleUser, roledownload, roleViewer } = roleTypes;
const { adminHomeUrl, userHomeUrl } = routesUrl;

export const appReducer = (state = initialStates.appInitialState, action) => {
  switch (action.type) {
    case SHOULD_BLOCK_NAVIGATION:
      return { ...state, shouldBlockNavigation: action.payload };
    case SESSION_EXPIRY:
      return { ...state, sessionExpiry: action.payload };
    case TOGGLE_NAVBAR_ARROW:
      return { ...state, toggleNavbarArrow: action.payload };
    case ON_LOGIN:
      return produce(state, (draft) => {
        let home = draft.routes.find((ele) => ele.title === "Home");
        if (!home) {
          home = {
            title: "Home",
            addInNavbar: true,
            tutorialElementKey: "hi-navbar-home",
          };
        }
        if (action.payload.userData.user.roles?.includes(roleAdmin)) {
          home.url = adminHomeUrl;
          home.roles = [roleAdmin];
        } else {
          home.url = userHomeUrl;
          home.roles = [roleUser, roleViewer, roledownload];
        }
        draft.routes = draft.routes.find((ele) => ele.title === "Home")
          ? [...draft.routes]
          : [{ ...home }, ...draft.routes];
        draft.isAuthenticated = true;
        // draft.isApplicationSettingsServiceCheck = false;
      });
    // return {
    // 	...state,
    // 	isAuthenticated: true,
    // 	routes: state.routes.find((ele) => ele.title === 'Home')
    // 		? [ ...state.routes ]
    // 		: [ {...home}, ...state.routes ],
    // 	isApplicationSettingsServiceCheck: false
    // };

    case APPLICATION_SETTINGS:
      action.payload.userData.clientTime = new Date().getTime();
      return process.env.NODE_ENV === "development"
        ? {
            ...state,
            applicationSettingsData: {
              ...action.payload,
              ...serviceUrls,
              settings: {
                ...serviceUrls.settings,
                ...(action.payload.settings?.fileExtensions
                  ? { fileExtensions: action.payload.settings.fileExtensions }
                  : {}),
              },
            },
          }
        : { ...state, applicationSettingsData: action.payload };
    // case ACTUAL_APPLICATIONSETTINGS:
    // 	return { ...state, actualApplicationSettings: action.payload };
    case APPLICATIONSETTINGS_SERVICECHECK:
      return { ...state, isApplicationSettingsServiceCheck: action.payload };
    // case RESTORE_APPLICATIONSETTINGS:
    // 	return {
    // 		...state,
    // 		applicationSettingsData: action.payload,
    // 		actualApplicationSettings: ''
    // 	};
    // case ON_LOGOUT:
    // 	return {
    // 		...initialStates.appInitialState,
    // 		activeRoute: routesUrl.loginUrl,
    // 		nxtRoute: '',
    // 		isApplicationSettingsServiceCheck: true
    // 	};
    case SHOW_LICENSE:
      return { ...state, showLicenseNotification: action.payload };
    case IS_LICENSE_RENDERED:
      return { ...state, isLicenseRendered: action.payload };
    case RESET_STORE:
      return {
        ...initialStates.appInitialState,
        logType: action.payload.logType,
        // isApplicationSettingsServiceCheck: action.payload === IMPERSONATE_LOGIN ? true : false,
        // activeRoute: action.payload.loginType === URL_AUTHENTICATION ? '' : routesUrl.loginUrl,
        nxtRoute: action.payload.isSessionOver
          ? action.payload.activeRoute
          : "",
        // isApplicationSettingsServiceCheck: false
      };
    case HAS_ERROR:
      return { ...state, hasError: !state.hasError };
    case ISLOGOUTMANUALLY:
      return { ...state, isLogoutManually: !state.isLogoutManually };
    case ROUTE_CHANGE:
      return { ...state, activeRoute: action.data };
    case APP_DETAILS:
      return { ...state, ...action.data };
    case TOGGLE_SIDEBAR:
      return {
        ...state,
        toggleSidebar:
          action.payload !== undefined ? action.payload : !state.toggleSidebar,
      };
    case TOGGLE_NAVBAR:
      return {
        ...state,
        showNavbar:
          action.payload !== undefined ? action.payload : !state.showNavbar,
      };
    case IS_SESSION_OVER:
      return { ...state, isSessionOver: action.payload };
    case VIEW_MODE_INFO: {
      return { ...state, viewModeInfo: action.payload };
    }
    case CHANGE_TUTORIAL_DATA:
      const { key, status, moduleKey } = action.payload;
      const tutorialIndex = tutorialItems[moduleKey]?.findIndex(
        (item) => item.key === key
      );
      // const tutorialData = tutorialItems[moduleKey].find((item) => item.key === key);
      // const { moduleKey } = tutorialData;
      let tutorialItem;

      switch (status) {
        case "next":
          if (tutorialIndex < tutorialItems[moduleKey]?.length) {
            tutorialItem = tutorialItems[moduleKey][tutorialIndex + 1] || {};
          }
          break;
        case "back":
          if (tutorialIndex >= 0) {
            tutorialItem = tutorialItems[moduleKey][tutorialIndex - 1] || {};
          }
          break;
        case "exit":
          tutorialItem = {};
          break;
        default:
          tutorialItem = tutorialItems[moduleKey][tutorialIndex] || {};
          break;
      }

      return { ...state, tutorialData: tutorialItem };
    case EDIT_MODE_INFO: {
      return { ...state, editModeInfo: action.payload };
    }
    case LOAD_REPORT_DATA: {
      let lastModified = action?.payload?.reportData?.lastModified || new Date().getTime();
      return { ...state, lastModified };
    }
    case CHANGE_LAST_MODIFIED: {
      return { ...state, lastModified: action.payload || new Date().getTime() };
    }
    case SET_ACCESS_DENIED_INFO: {
      return { ...state, accessDeniedInfo: action.payload };
    }
    case ABOUT_TO_CHANGE_ROUTE: {
      return { ...state, aboutToChangeRoute: action.payload };
    }
    case TOGGLE_VIEWER_EMAIL: {
      return { ...state, viewerEmailModalVisible: action.payload };
    }
    case TOGGLE_VIEWER_SCHEDULE: {
      return { ...state, viewerScheduleModalVisible: action.payload };
    }
    case UPDATEKEYSTRIGGER: {
      const altPressed =
        action.payload === "Alt" || state.keysPressed.includes("Alt");
      const twoAltsTriggerd =
        action.payload === "Alt" && state.keysPressed.includes("Alt");
      const resetPressed = action.payload === "reset";
      return {
        ...state,
        altTriggered: resetPressed ? false : altPressed && !twoAltsTriggerd,
        keysPressed: resetPressed
          ? []
          : twoAltsTriggerd
          ? []
          : [...state.keysPressed, action.payload],
      };
    }
    case SETSHORTCUTCURRENTLOCATION: {
      return { ...state, currentSCLocation: action.payload };
    }
    default:
      return { ...state };
  }
};
