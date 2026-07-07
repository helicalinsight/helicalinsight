import { customPostRequest, postRequest } from "../service";

export const uriConfig = {
  saveDashboard: "dashboard/efwdd/designer",
  editDashboard: "dashboard/efwdd/fetch",
};

function dashboard(dispatch) {
  const postDashboardRequest = (
    formData,
    uri,
    callback = () => {},
    errback = () => {}
  ) => {
    postRequest(dispatch, uri, formData, callback, errback);
  };
  //   const getUserManagementRequest = (
  //     url,
  //     data,
  //     callback = () => {},
  //     errback = () => {}
  //   ) => {
  //     getRequest(dispatch, url, data, callback, errback);
  //   };

  const postDashboardRequestForUrl = ({
    url,
    data,
    callback = () => {},
    errback = () => {},
  }) => {
    customPostRequest({ dispatch, url, data, callback, errback });
  };

  //   const getProductInformation = (
  //     url,
  //     data,
  //     callback = () => {},
  //     errback = () => {}
  //   ) => {
  //     getRequest(dispatch, url, data, callback, errback);
  //   };

  //   //system details api calls

  //   const postJvmThreadDetails = (
  //     formData,
  //     uri,
  //     callback = () => {},
  //     errback = () => {}
  //   ) => {
  //     postRequest(dispatch, uri, formData, callback, errback);
  //   };

  //   const postOsDetails = (
  //     formData,
  //     uri,
  //     callback = () => {},
  //     errback = () => {}
  //   ) => {
  //     postRequest(dispatch, uri, formData, callback, errback);
  //   };

  //   //plugins tab api calls

  //   const postPluginsDetails = (
  //     formData,
  //     uri,
  //     callback = () => {},
  //     errback = () => {}
  //   ) => {
  //     postRequest(dispatch, uri, formData, callback, errback);
  //   };

  //   const deletePluginsDetails = (
  //     formData,
  //     uri,
  //     callback = () => {},
  //     errback = () => {}
  //   ) => {
  //     postRequest(dispatch, uri, formData, callback, errback);
  //   };

  //   //Managemet tab api calls

  //   const postManagementData = (
  //     formData,
  //     uri,
  //     callback = () => {},
  //     errback = () => {}
  //   ) => {
  //     postRequest(dispatch, uri, formData, callback, errback);
  //   };

  //   //scheduling tab api calls

  //   const postSchedulingData = (
  //     formData,
  //     uri,
  //     callback = () => {},
  //     errback = () => {}
  //   ) => {
  //     postRequest(dispatch, uri, formData, callback, errback);
  //   };

  return {
    postDashboardRequest,
    postDashboardRequestForUrl,
    // getUserManagementRequest,
    // postUserManagementRequest,
    // getProductInformation,
    // postJvmThreadDetails,
    // postOsDetails,
    // postPluginsDetails,
    // deletePluginsDetails,
    // postManagementData,
    // postSchedulingData,
  };
}

export default dashboard;
