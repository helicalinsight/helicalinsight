const profileItem = ({ dispatch, requests, data, successCB, errorCB }) => {
  let url;
  dispatch((dispatch, getState) => {
    url = getState().app.applicationSettingsData.settings.adminPaths.profiles;
  });

  requests.admin(dispatch).postUserManagementRequest({
    url,
    data,
    callback: successCB,
    errback: errorCB,
  });
};
export { profileItem };
