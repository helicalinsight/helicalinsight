import {
  buildPreviewRequest,
  createAbortableFetch,
  createAbortableRequest,
  fetchPost,
  getRequest,
  postRequest,
  streamFetch,
  streamPostRequest,
} from "../service";
import Base64 from "../utils/Base64";
function cannedReport(dispatch) {
  const previewRequest = (
    formData,
    callback = () => {},
    errback = () => {},
    isStreamToggle = false,
  ) => {
    if (isStreamToggle) {
      return previewRequestStream(formData, callback, errback);
    }
    return previewRequestNormal(formData, callback, errback);
  };
  const previewRequestStream = (
    formData,
    callback = () => {},
    errback = () => {},
  ) => {
    const uri = "hcr/report/generateReport";
    return streamPostRequest(dispatch, uri, formData, callback, errback);
  };
  const previewRequestNormal = (
    formData,
    callback = () => {},
    errback = () => {},
  ) => {
    const uri = "hcr/report/generateReport";
    return postRequest(dispatch, uri, formData, callback, errback);
  };

  const hcrImageServiceRequest = (
    formData,
    uri,
    callback = () => {},
    errback = () => {},
  ) => {
    postRequest(dispatch, uri, formData, callback, errback);
  };
  const hcrConfigurationsRequest = (
    formData,
    uri,
    callback = () => {},
    errback = () => {},
  ) => {
    postRequest(dispatch, uri, formData, callback, errback);
  };

  const saveHcrRequest = (
    formData = {},
    callback = () => {},
    errback = () => {},
  ) => {
    const uri = "hcr/report/saveReportState";
    return postRequest(dispatch, uri, formData, callback, errback);
  };

  const editHcrRequest = (
    formData = {},
    callback = () => {},
    errback = () => {},
  ) => {
    const uri = "hcr/report/fetchReportState";
    return postRequest(dispatch, uri, formData, callback, errback);
  };

  const saveQueryReportState = (
    formData = {},
    callback = () => {},
    errback = () => {},
  ) => {
    const uri = "hcr/report/saveReportState";
    return postRequest(dispatch, uri, formData, callback, errback);
  };

  const saveExecuteReportQuery = (
    formData = {},
    callback = () => {},
    errback = () => {},
  ) => {
    const uri = "hcr/report/executeReportQuery";
    return postRequest(dispatch, uri, formData, callback, errback);
  };

  const getResources = (callback = () => {}, errback = () => {}) => {
    const formData = { extensions: "WyJpbWFnZSJd" };
    return getRequest(dispatch, "/getResources", formData, callback, errback);
  };

  const handleDeleteImage = (
    formData = {},
    callback = () => {},
    errback = () => {},
  ) => {
    return postRequest(
      dispatch,
      "util/io/imageService",
      formData,
      callback,
      errback,
    );
  };

  const getExportProperties = (
    formData = {},
    callback = () => {},
    errback = () => {},
  ) => {
    const uri = "content/static/getContents";
    return postRequest(dispatch, uri, formData, callback, errback);
  };

  return {
    handleDeleteImage,
    getResources,
    saveExecuteReportQuery,
    editHcrRequest,
    previewRequest,
    hcrConfigurationsRequest,
    saveHcrRequest,
    saveQueryReportState,
    getExportProperties,
    hcrImageServiceRequest,
  };
}

export default cannedReport;
