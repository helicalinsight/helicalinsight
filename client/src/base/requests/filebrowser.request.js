import { getRequest, postRequest, customPostRequest, postExportDownloadRequest, uploadFilePostRequest } from "../service";

function filebrowser(dispatch, settings) {
  const getResources = (formData, callback = () => {}, errback = () => {}) => {
    getRequest(
      dispatch,
      settings.DashboardGlobals.solutionLoader, // 'getSolutionResources',
      {
        // extensions: "WyJtZXRhZGF0YSJd&v=1632915600392"
      },
      callback,
      errback
    );
  };
  // const postSaveFileToFolder = (uri, formData, callback = () => { }, errback = () => { }) => {
  //     return postRequest(dispatch, uri, formData, callback, errback);
  // }
  const postContextMenuOperations = (formData, callback = () => {}, errback = () => {}) => {
    return customPostRequest({
      dispatch,
      url: settings.DashboardGlobals.fsop, //'fileSystemOperations',
      data: formData,
      callback,
      errback,
    });
  };
  const postShareFileBrowser = (formData, uri, callback = () => {}, errback = () => {}) => {
    return postRequest(dispatch, uri, formData, callback, errback);
  };
  const postShareTableData = (formData, uri, callback = () => {}, errback = () => {}) => {
    return postRequest(dispatch, uri, formData, callback, errback);
  };
  const postExportData = (formData, downLoadProgress, callback) => {
    const url = "/exportResource";
    return postExportDownloadRequest({ dispatch, url, formData, callback, downLoadProgress });
  };

  const postImageUpload = (formData, config, callback = () => {}, errback = () => {}) => {
    const url = "/importFile";
    return uploadFilePostRequest({
          dispatch,
          url,
          formData,
          config,
          callback,
          errback
        });
  };

  return {
    postImageUpload,
    getResources,
    postContextMenuOperations,
    postShareFileBrowser,
    postShareTableData,
    postExportData,
  };
}

export default filebrowser;
