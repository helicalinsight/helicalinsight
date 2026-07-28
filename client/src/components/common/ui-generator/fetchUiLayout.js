import requests from "../../../base/requests";
import { uriConfig } from "../../../base/requests/admin.request";

/**
 * Loads a UI layout JSON from Static via content/static/getContents.
 * @param {object} options
 * @param {*} options.dispatch
 * @param {string} options.contentId - e.g. "Static/layout/foo.ui.layout"
 * @param {function} [options.onSuccess]
 * @param {function} [options.onError]
 */
export const fetchUiLayout = ({
  dispatch,
  contentId,
  onSuccess = () => {},
  onError = () => {},
}) => {
  if (!contentId) {
    onError(new Error("contentId is required"));
    return null;
  }
  return requests.admin(dispatch).postAdminRequest(
    { contentId },
    uriConfig.contentStaticgetContents,
    onSuccess,
    onError
  );
};

export default fetchUiLayout;
