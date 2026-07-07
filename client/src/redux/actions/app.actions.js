import actionTypes from './actionTypes';

const {
	IS_SESSION_OVER,
	HAS_ERROR,
	ROUTE_CHANGE,
	APP_DETAILS,
	SESSION_EXPIRY,
	ON_LOGOUT,
	ISLOGOUTMANUALLY,
	ON_LOGIN,
	TOGGLE_SIDEBAR,
	TOGGLE_NAVBAR,
	APPLICATION_SETTINGS,
	SHOW_LICENSE,
	IS_LICENSE_RENDERED,
	RESET_STORE,
	APPLICATIONSETTINGS_SERVICECHECK,
	TOGGLE_NAVBAR_ARROW,
} = actionTypes;

const updateRoute = (data) => {
	return { type: ROUTE_CHANGE, data };
};

const updateAppDetails = (data) => {
	return { type: APP_DETAILS, data };
};

const setSessionExpiry = (data) => {
	return { type: SESSION_EXPIRY, payload: data };
};

const onLogout = () => {
	return { type: ON_LOGOUT };
};

const setIsLogoutManually = () => {
	return { type: ISLOGOUTMANUALLY };
};

const onLogin = (payload) => {
	return { type: ON_LOGIN, payload };
};

const toggleSidebar = (payload) => {
	return { type: TOGGLE_SIDEBAR, payload };
};

const showNavbar = (payload) => {
	return { type: TOGGLE_NAVBAR, payload };
};

const toggleHasError = () => {
	return { type: HAS_ERROR };
};

const setSessionOver = (payload) => {
	return { type: IS_SESSION_OVER, payload };
};

const toggleMainNavbar = (payload) => {
	return { type: TOGGLE_NAVBAR_ARROW, payload };
};

const storeApplicationSettings = (payload) => {
	return { type: APPLICATION_SETTINGS, payload };
};

const storeShowLicense = (payload) => {
	return { type: SHOW_LICENSE, payload };
};

const storeIsLicenseRendered = (payload) => {
	return { type: IS_LICENSE_RENDERED, payload };
};

const resetStore = (logType) => {
	return { type: RESET_STORE, payload: logType };
};

const applicationSettingsServiceCheck = (payload) => {
	return { type: APPLICATIONSETTINGS_SERVICECHECK, payload };
};

const setViewModeInfo = (data) => {
	return { type: actionTypes.VIEW_MODE_INFO, payload: data };
};

const changeTutorialData = (data) => {
	return { type: actionTypes.CHANGE_TUTORIAL_DATA, payload: data };
};

const setEditModeInfo = (obj) => {
	return { type: actionTypes.EDIT_MODE_INFO, payload: obj };
};

const changeLastModified = (obj) => {
	return { type: actionTypes.CHANGE_LAST_MODIFIED, payload: obj };
};
const setAccessDeniedInfo = (obj) => {
	return { type: actionTypes.SET_ACCESS_DENIED_INFO, payload: obj };
};

const aboutToChangeRoute = (data) => {
	return { type: actionTypes.ABOUT_TO_CHANGE_ROUTE, payload: data };
};

const setShouldBlockNavigation = (data) => {
	return { type: actionTypes.SHOULD_BLOCK_NAVIGATION, payload: data };
};

const setViewerEmailModalVisibility = (bool) => {
  return { type: actionTypes.TOGGLE_VIEWER_EMAIL, payload: bool };
};

const setViewerScheduleVisibility = (bool) => {
  return { type: actionTypes.TOGGLE_VIEWER_SCHEDULE, payload: bool };
};

export const setKeysPressed = (key) => {
	return { type: actionTypes.UPDATEKEYSTRIGGER, payload: key };
};

export const setShotCutCurrentLocation = (location) => {
	return { type: actionTypes.SETSHORTCUTCURRENTLOCATION, payload: location };
};

export const appActions = {
	setKeysPressed,
	setShotCutCurrentLocation,
	setShouldBlockNavigation,
	applicationSettingsServiceCheck,
	resetStore,
	storeApplicationSettings,
	storeShowLicense,
	storeIsLicenseRendered,
	setSessionOver,
	updateRoute,
	updateAppDetails,
	setSessionExpiry,
	onLogout,
	setIsLogoutManually,
	onLogin,
	toggleSidebar,
	showNavbar,
	toggleHasError,
	setViewModeInfo,
	changeTutorialData,
	toggleMainNavbar,
	setEditModeInfo,
	changeLastModified,
	setAccessDeniedInfo,
	aboutToChangeRoute,
	setViewerEmailModalVisibility,
	setViewerScheduleVisibility
};
