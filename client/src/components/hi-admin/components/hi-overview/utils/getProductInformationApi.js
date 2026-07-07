import requests from "../../../../../base/requests";
import {
  storeProductData,
  updateIsFetched,
} from "../../../../../redux/actions/admin.actions";

export const getProductData = ({ isFetched, dispatch, Notify }) => {
  let url;
  dispatch((dispatch, getState, services) => {
    url =
      getState().app.applicationSettingsData?.settings?.DashboardGlobals
        ?.productInfo;
  });
  !isFetched && url &&
    requests.admin(dispatch).getProductInformation(
      url,
      "",
      (res) => {
        dispatch(storeProductData(res));
        dispatch(updateIsFetched({ type: "productData", value: true }));
      },
      (e) => {
        // Notify.error({ ...e, type: "Network Call" });
        dispatch(updateIsFetched({ type: "productData", value: true }));
      }
    );
};
