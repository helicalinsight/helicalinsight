const getOrgData = ({ dispatch, refresh = false, orgDataCheck, requests, successCB, errorCB }) => {
	let url;
	dispatch((dispatch, getState) => {
		url = getState().app.applicationSettingsData.settings.adminPaths.organisations;
	});
	if (orgDataCheck || refresh) {
		return requests.admin(dispatch).getUserManagementRequest(url, '', successCB, errorCB);
	}
};
export { getOrgData };
