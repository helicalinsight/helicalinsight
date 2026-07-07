const getRoleData = ({ dispatch, requests, roleDataCheck, successCB, errorCB, refresh = false }) => {
	let url;
	dispatch((dispatch, getState) => {
		url = getState().app.applicationSettingsData.settings.adminPaths.roles;
	});
	if (roleDataCheck || refresh) {
		return requests.admin(dispatch).getUserManagementRequest(url, '', successCB, errorCB);
	}
};
export { getRoleData };
