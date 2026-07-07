const roleItem = ({ dispatch, data, successCB, errorCB, requests }) => {
  let url;
  dispatch((dispatch, getState) => {
    url = getState().app.applicationSettingsData.settings.adminPaths.roles;
  });
  requests.admin(dispatch).postUserManagementRequest({
    url,
    data,
    callback: successCB,
    errback: errorCB,
  });
};
export { roleItem };
