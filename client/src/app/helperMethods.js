import { message } from "antd";
import Cookies from "js-cookie";
import notify from "../components/hi-notifications/notify";
import { appActions } from "../redux/actions";
import { authTypes } from "./constants";

const { JWT, DIRECT, SSO_TOKEN } = authTypes;

export function checkReport({ report, auth }) {
  if (!report) {
    auth && message.warning("report obj is missing");
    return false;
  }
  const result = "dir" in report && "file" in report;
  !result && message.warning("Please re-check sending report");
  return result;
}

export function handleAuthDetails({ auth, report }) {
  if (auth) {
    if (!auth.type) {
      !["test"].includes(process.env.NODE_ENV) &&
        message.warning("auth Type is missing");
      return false;
    }
    let result;
    switch (auth.type) {
      case JWT:
      case DIRECT:
      case SSO_TOKEN:
        result = "authToken" in auth;
        !["test"].includes(process.env.NODE_ENV) &&
          !result &&
          message.warning("authToken is missing");
        return result;
      case DIRECT:
        break;
      case SSO_TOKEN:;
        break;
      default:
        return false;
    }
  } else {
    if (report) {
      !["test"].includes(process.env.NODE_ENV) &&
        message.warning("auth obj is missing");
      return false;
    }
    return true;
  }
}

export function responseHandler({ dispatch }) {
  dispatch(appActions.setSessionExpiry(Cookies.get("sessionExpiry")));
}

export function handleAxiosObject({
  cancelToken,
  signal,
  auth,
  baseURL,
  dispatch,
  successNotification
}) {
  const axiosObject = {
    cancelToken,
    signal,
    transformRequest: (data) => {
      return data;
    },
    headers: {
      "X-Requested-With": "XMLHttpRequest",
    },
    transformResponse: [
      function (data) {
        responseHandler({ dispatch });
        try{
          // data can be ArrayBuffer,Blob
          const parsedData = JSON.parse(data);
          if(parsedData.response) {
            if(parsedData.status === 0) {
              notify(dispatch).error({ ...parsedData.response, type: "Network Call" });
            } else if(parsedData.status === 1) {
              parsedData.response.message && notify(dispatch).success({
                type: "Network Call",
                ...parsedData.response,
              });
            }
          } else {
            if(parsedData.message) {
              if(parsedData.status === '0' || parsedData.status === 0) {
                notify(dispatch).error({ ...parsedData, type: "Network Call" });
              } else {
                if(parsedData.message.toLowerCase().includes('login')) {
                  if(successNotification) {
                    notify(dispatch).success({
                      type: "Network Call",
                      ...parsedData,
                    });
                  }
                } else {
                  notify(dispatch).success({
                    type: "Network Call",
                    ...parsedData,
                  });
                }
              }
            }
          }
        }catch(e){

        }
        return data;
      },
    ],
  };
  if (auth) {
    switch (auth.type) {
      case JWT:
      case SSO_TOKEN:
      case DIRECT:
        const { authToken, type } = auth;
        // axiosObject.headers["Authorization"] = authToken;
        axiosObject.headers["type"] = type;
        axiosObject.headers["authToken"] = authToken;
        axiosObject["baseURL"] = baseURL || window.baseURL;
        break;
      case DIRECT:
        break;
      default:
        message.warning('Provided auth "type" is not matching');
    }
  }
  if(auth?.type !== JWT && auth?.type !== SSO_TOKEN && auth?.type !== DIRECT) {
    axiosObject["baseURL"] = window.baseURL;
  }
  return axiosObject;
}
