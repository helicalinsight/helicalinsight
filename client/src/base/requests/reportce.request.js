import { postRequest } from "../service";

function reportce(dispatch) {
  const postTypesDetails = (
    uri,
    formData,
    callback = () => {},
    errback = () => {}
  ) => {
    postRequest(dispatch, uri, formData, callback, errback);
  };

  const saveReportCeFile = (
    uri,
    formData,
    callback = () => {},
    errback = () => {}
  ) => {
    postRequest(dispatch, uri, formData, callback, errback);
  };

  const editReportCeFile = (
    uri,
    formData,
    callback = () => {},
    errback = () => {}
  ) => {
    postRequest(dispatch, uri, formData, callback, errback);
  };

  return { postTypesDetails, saveReportCeFile, editReportCeFile };
}

export default reportce;
