const getUserData = ({ dispatch, successCB, errorCB, userDataCheck, requests, refresh = false }) => {
	let url;
	dispatch((dispatch, getState) => {
		url = getState().app.applicationSettingsData.settings.adminPaths.users;
	});
	if (userDataCheck || refresh) {
		return requests.admin(dispatch).getUserManagementRequest(url, '', successCB, errorCB);
	}
};
export { getUserData };
