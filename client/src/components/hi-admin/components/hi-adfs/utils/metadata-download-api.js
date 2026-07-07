import requests from "../../../../../base/requests";
const downloadMetadata = ({ dispatch }) => {
  let url;
  dispatch((dispatch, getState) => {
    url = getState().app.applicationSettingsData.userData.saml.metadataDownload;
  });
  requests.admin(dispatch).getDownloadAdminRequest(url);
};
export { downloadMetadata };
