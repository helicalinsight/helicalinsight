const userItem = ({ dispatch, data, successCB, errorCB, requests }) => {
  let url;
  dispatch((dispatch, getState) => {
    url = getState().app.applicationSettingsData.settings.adminPaths.users;
  });

  requests.admin(dispatch).postUserManagementRequest({
    url,
    data,
    callback: successCB,
    errback: errorCB,
  });
};
export { userItem };
