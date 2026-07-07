import requests from "../../../base/requests";
import { uriConfig } from "../../../base/requests/admin.request";
import { storeWhatsNewContent, updateIsFetched } from "../../../redux/actions/admin.actions";

export const fetchWhatsNewContent = ({refresh = false,isFetchedWhatsNewContent,dispatch}) => {
    (!isFetchedWhatsNewContent || refresh) &&
      requests.admin(dispatch).postAdminRequest(
        {},
        uriConfig.monitorSystemRelease,
        (res) => {
          dispatch(storeWhatsNewContent(res));
          dispatch(updateIsFetched({ type: "whatsNewContent", value: true }));
        },
        (e) => {
          dispatch(updateIsFetched({ type: "whatsNewContent", value: true }));
        }
      );
  };