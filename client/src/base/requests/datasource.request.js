import { getRequest, uploadFilePostRequest, postRequest } from "../service";

function datasource(dispatch, settings) {
  const postDataSourceData = (formData, uri, callback = () => {}, errback = () => {}) => {
    postRequest(dispatch, uri, formData, callback, errback);
  };

  const getDataSourceData = (data, callback = () => {}, errback = () => {}) => {
    return getRequest(dispatch, settings.HDI.adhoc.urls.listDataSources, data, callback, errback);
  };

  const postUploadFile = (formData, config, callback = () => {}, errback = () => {}) => {
    let url;
    dispatch((dispatch, getState) => {
      url = getState().app.applicationSettingsData.settings.DashboardGlobals.importFile;
    });
    return uploadFilePostRequest({
      dispatch,
      url,
      formData,
      config,
      callback,
      errback,
    });
  };

  return { postDataSourceData, getDataSourceData, postUploadFile };
}

export default datasource;
