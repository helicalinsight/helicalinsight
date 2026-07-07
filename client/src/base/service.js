import Base64 from "./utils/Base64";
import qs from "qs";
import notify from "../components/hi-notifications/notify";
import HIStreamClient from "../app/HiStreamClient";

export const ssoLogin = async (
  dispatch,
  { authToken },
  successNotification,
) => {
  let hiAxios;
  dispatch((dispatch, getState, services) => {
    hiAxios = services(dispatch, successNotification);
  });
  return await hiAxios.instance.post(`/`, qs.stringify({ authToken }));
};

export const axiosLogin = async (
  dispatch,
  { organization, username, password },
  successNotification,
) => {
  let hiAxios;
  dispatch((dispatch, getState, services) => {
    hiAxios = services(dispatch, successNotification);
  });
  const data = { username: username, password: password };
  organization && (data["j_organization"] = organization);
  let res;
  try {
    res = await hiAxios.instance.post(`/login`, qs.stringify(data));
  } catch (e) {
    return e.response;
  }
  return res;
};

export const axiosLogout = (dispatch, successCB, errorCB) => {
  let hiAxios;
  dispatch((dispatch, getState, services) => {
    hiAxios = services(dispatch);
  });
  hiAxios.instance
    .get("/logout" /*'rest/logout'*/)
    .then((data) => {
      successCB(data);
    })
    .catch((err) => {
      errorCB(err);
    });
};

export const applicationSettings = (dispatch, successCB, errorCB) => {
  let hiAxios;
  dispatch((dispatch, getState, services) => {
    hiAxios = services(dispatch);
  });
  hiAxios.instance
    .get(`applicationSettings` /*'rest/logout'*/)
    .then((data) => {
      successCB(data);
    })
    .catch((err) => {
      errorCB(err);
    });
};

export const impersonateUserLogin = async ({
  impersonateUserDetails,
  dispatch,
  successNotification,
}) => {
  let hiAxios;
  dispatch((dispatch, getState, services) => {
    hiAxios = services(dispatch, successNotification);
  });
  let res;
  try {
    let { name, organization } = impersonateUserDetails;
    name = encodeURIComponent(name);
    organization = encodeURIComponent(organization);
    res = await hiAxios.instance.get(
      `mock/impersonate?username=${name}${organization !== "Null" ? `:${organization}` : ""
      }`,
    );
  } catch (e) {
    return e.response;
  }
  return res;
};

export const impersonateUserLogout = (dispatch, callback, errback) => {
  let hiAxios;
  dispatch((dispatch, getState, services) => {
    hiAxios = services(dispatch);
  });
  hiAxios.instance
    .get(`/logoutMock`)
    .then((res) => {
      callback(res);
    })
    .catch((err) => {
      errback(err);
    });
};

export const samlLogin = ({ dispatch, samlId, callback, errback }) => {
  let hiAxios;
  dispatch((dispatch, getState, services) => {
    hiAxios = services(dispatch);
  });
  hiAxios.instance
    .get(`saml/login?idp=${samlId}`)
    .then((res) => {
      callback(res);
    })
    .catch((err) => {
      errback(err);
    });
};

export const postRequest = function (
  dispatch,
  uri,
  formData,
  callback,
  errback,
) {
  let [type, serviceType, service] = uri.split("/");
  let requestId;
  if (formData.hasOwnProperty("requestId")) {
    requestId = formData.requestId;
    delete formData["requestId"];
  }
  if (formData["htmlId"]) {
    delete formData["htmlId"];
  }

  formData = Base64.encode(JSON.stringify(formData));

  let options = { type, serviceType, service, formData, requestId };
  let url;
  dispatch((dispatch, getState) => {
    url =
      getState().app.applicationSettingsData.settings?.DashboardGlobals
        .services;
  });
  return axiosPost(dispatch, url, qs.stringify(options), callback, errback);
};

export const customPostRequest = function ({
  url,
  data,
  dispatch,
  callback,
  errback,
}) {
  let {
    action,
    formData,
    id,
    userId,
    roleIds,
    organisation,
    destination,
    source,
    sourceArray,
  } = data;
  let options = data;
  if (formData) {
    formData = Base64.encode(JSON.stringify(formData));
    options = {
      action,
      formData,
      id,
      userId,
      roleIds,
      organisation,
      destination,
      source,
      sourceArray,
    };
  }

  return axiosPost(
    // window.HDI.adhoc.urls.services,
    dispatch,
    url,
    qs.stringify(options, { arrayFormat: "brackets" }),
    callback,
    errback,
    {},
    { arrayFormat: "brackets" },
  );
};

export const uploadFilePostRequest = function ({
  dispatch,
  url,
  formData,
  config,
  callback,
  errback,
}) {
  return axiosPost(dispatch, url, formData, callback, errback, config);
};

export const appendRequestId = (obj, requestId) => {
  obj.requestId = requestId;
  return obj;
};

const axiosPost = (
  dispatch,
  url,
  data,
  successCB,
  errorCB,
  config = {},
  stringifyConfig = {},
) => {
  let hiAxios;
  dispatch((dispatch, getState, services) => {
    hiAxios = services(dispatch);
  });
  if (typeof data === "string") {
    data = appendRequestId(qs.parse(data), hiAxios.requestId);
    data = qs.stringify(data, { ...(stringifyConfig || {}) });
  }
  hiAxios.instance
    .post(url, data, config) // window.baseURL removed due to error
    .then((res) => {
      // typeof successCB == "function" && successCB(data);

      if (typeof res.data === "string") {
        try {
          data = JSON.parse(res.data);
        } catch (e) {
          return errorCB(e.response);
        }
      } else {
        data = res.data;
      }
      // if (typeof errorCB === "function")
      if (data.status === undefined) {
        return successCB(data);
      }
      let { status, response, lastModified } = data;
      if (lastModified) {
        response.lastModified = lastModified;
      }
      if (status) {
        successCB(response);
      } else {
        errorCB(response);
      }
    })
    .catch((error) => typeof errorCB == "function" && errorCB(error));
  // })
  return hiAxios;
};

export const getRequest = (dispatch, url, data, successCB, errorCB) => {
  // if (process.env.NODE_ENV === "development") {
  //   data.j_password = "hiadmin";
  //   data.j_username = "hiadmin";
  // }
  let hiAxios;
  dispatch((dispatch, getState, services) => {
    hiAxios = services(dispatch);
  });

  hiAxios.instance
    .get(process.env.NODE_ENV === "development" ? `${url}` : url, {
      params: data,
    })
    .then((res) => {
      // typeof successCB == "function" && successCB(data);

      let data = res.data;
      if (typeof res.data === "string") {
        try {
          data = JSON.parse(res.data);
        } catch (e) {
          return;
        }
      }
      typeof successCB == "function" && successCB(data);
    })
    .catch((error) => typeof errorCB == "function" && errorCB(error));
  return hiAxios;
};

export const getResources = (dispatch, data, successCB, errorCB) => {
  // if (process.env.NODE_ENV === "development") {
  //   data.j_password = "hiadmin";
  //   data.j_username = "hiadmin";
  // }
  let hiAxios;
  dispatch((dispatch, getState, services) => {
    hiAxios = services(dispatch);
  });
  let url;
  dispatch((dispatch, getState) => {
    url =
      getState().app.applicationSettingsData.settings.DashboardGlobals.openEfw;
  });
  hiAxios.instance
    .get(url, {
      params: data,
    })
    .then((res) => {
      let data = res.data;
      typeof successCB == "function" && successCB(data);
    })
    .catch((error) => typeof errorCB == "function" && errorCB(error));
};

export const getResources__test = (dispatch, url, data, successCB, errorCB) => {
  // if (process.env.NODE_ENV === "development") {
  //   data.j_password = "hiadmin";
  //   data.j_username = "hiadmin";
  // }
  let hiAxios;
  dispatch((dispatch, getState, services) => {
    hiAxios = services(dispatch);
  });
  hiAxios.instance
    .get(url, {
      params: data,
    })
    .then((res) => {
      let data = res.data;
      typeof successCB == "function" && successCB(data);
    })
    .catch((error) => typeof errorCB == "function" && errorCB(error));
};

// export const createFormDataObject = ({ data }) => {
//   return qs.stringify(data);
// };

const handleDownloadFile = (res, fileName) => {
  const reader = new FileReader();
  reader.onload = function () {
    const link = document.createElement("a");
    link.href = reader.result;
    link.setAttribute("download", fileName);
    link.style.display = "none";
    document.body.appendChild(link);
    link.click();
    link.remove();
  };
  reader.readAsDataURL(new Blob([res.data]));
};

export const postDownloadRequest = function ({
  dispatch,
  formData,
  reportName = "",
  callback,
  isReportNameInResponse,
}) {
  let hiAxios;
  dispatch((dispatch, getState, services) => {
    hiAxios = services(dispatch);
  });
  // if (process.env.NODE_ENV === "development") {
  //   //  comment for dashboardGlobal
  //   const {name, password, organization} = getDevConstants()
  //   formData.j_password = password;
  //   formData.j_username = name;
  //   if(organization) formData.j_organization = organization;
  // }
  let url;
  dispatch((dispatch, getState) => {
    url =
      getState().app.applicationSettingsData.settings.DashboardGlobals
        .reportDownload;
  });
  let data = qs.stringify({ data: Base64.encode(JSON.stringify(formData)) });
  hiAxios
    .instance({
      url,
      method: "POST",
      responseType: "blob",
      data,
      onDownloadProgress: (event) => {
        const percentCompleted = Math.round((event.loaded * 100) / event.total);
        if (callback) {
          callback({ progress: percentCompleted });
        }
      },
    }) // window.baseURL removed due to error
    .then((res) => {
      let fileInfo = res.headers["content-disposition"] || "";
      let fileName = fileInfo?.split("=")[1] || "";
      fileName =
        fileName?.substr(1, fileName.length - 2) ||
        (reportName ? "" : "Untitled1");
      if (!isReportNameInResponse) {
        // report name is not in response
        fileName = reportName + "_" + fileName;
      }
      handleDownloadFile(res, fileName);

      // commented this below of using url as it was giving console error (bug id : 6608)

      // const url = window.URL.createObjectURL(new Blob([res.data]));
      // const link = document.createElement("a");
      // link.href = url;
      // link.setAttribute("download", fileName); //or any other extension
      // document.body.appendChild(link);
      // link.click();
      // link.remove();
      if (callback) {
        callback(null);
      }
    });
};

export const getDownloadRequest = function ({ dispatch, url }) {
  let hiAxios;
  dispatch((dispatch, getState, services) => {
    hiAxios = services(dispatch);
  });
  hiAxios
    .instance({
      url,
      method: "GET",
      responseType: "blob",
      // data,
    }) // window.baseURL removed due to error
    .then((res) => {
      let fileInfo = res.headers["content-disposition"];
      let fileName = fileInfo.split("=")[1];
      fileName = fileName.substr(1, fileName.length - 2);
      // console.log(fileInfo,fileName)
      const url = window.URL.createObjectURL(new Blob([res.data]));
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", fileName); //or any other extension
      document.body.appendChild(link);
      link.click();
      link.remove();
    });
};

export const postPrintDownloadRequest = function ({
  dispatch,
  formData,
  callback,
}) {
  let hiAxios, url;
  const Notify = notify(dispatch);
  dispatch((dispatch, getState, services) => {
    hiAxios = services(dispatch);
    url =
      getState().app.applicationSettingsData.settings.DashboardGlobals
        .reportDownload;
  });
  formData.reportParameters = Base64.encode(
    JSON.stringify(formData.reportParameters),
  );
  let data = qs.stringify(formData);
  callback({ progress: 0 });
  hiAxios
    .instance({
      url,
      method: "POST",
      responseType: "arraybuffer",
      data,
      onDownloadProgress: (progressEvent) => {
        const percentCompleted = Math.round(
          (progressEvent.loaded * 100) / progressEvent.total,
        );
        callback({ progress: percentCompleted });
      },
    })
    .then((res) => {
      let fileInfo = res.headers["content-disposition"] || "";
      let fileName = fileInfo.split("=") || [];
      fileName = fileName[1] || "";
      if (fileName) {
        fileName = fileName.substr(1, fileName.length - 2) || "Untitled1";

        // added this method to download the files (6608)
        handleDownloadFile(res, fileName);

        // commented below  method of using url as it was giving console error (bug id : 6608)
      } else {
        try {
          let encoding = new TextDecoder("utf-8");
          let arr = new Uint8Array(res.data);
          let resString = encoding.decode(arr);
          let data = JSON.parse(resString);
          Notify.error({ type: "Backend", message: data.response.message });
        } catch (e) {
          Notify.error({
            type: "Backend",
            message: "ExportException: Error Occurred while exporting",
          });
        }
      }

      callback(null);
    })
    .catch((e) => {
      // Notify.error({ type: "Backend", message: e.message });
    });
  return hiAxios;
};

export const postExportDownloadRequest = function ({
  dispatch,
  url,
  formData,
  callback,
  downLoadProgress,
}) {
  let hiAxios;

  dispatch((dispatch, getState, services) => {
    hiAxios = services(dispatch);
  });

  let newFormData = {
    formData: Base64.encode(JSON.stringify(formData)),
  };
  newFormData = qs.stringify(newFormData);
  hiAxios
    .instance({
      url,
      method: "POST",
      responseType: "arraybuffer",
      data: newFormData,
      onDownloadProgress: (pr) => downLoadProgress(pr),
    }) // window.baseURL removed due to error
    .then((res) => {
      callback(res);
    });
};

export const instantBIPostRequest = function (
  dispatch,
  url,
  formData,
  callback,
  errback,
) {
  let requestId;
  if (formData.hasOwnProperty("requestId")) {
    requestId = formData.requestId;
    delete formData["requestId"];
  }
  if (formData["htmlId"]) {
    delete formData["htmlId"];
  }
  let options = { ...formData, requestId };
  return axiosPost(dispatch, url, qs.stringify(options), callback, errback);
};

export const streamPostRequest = function (
  dispatch,
  uri,
  formData,
  callback,
  errback,
) {
  let [type, serviceType, service] = uri.split("/");
  let requestId;
  if (formData.hasOwnProperty("requestId")) {
    requestId = formData.requestId;
    delete formData["requestId"];
  }
  if (formData["htmlId"]) {
    delete formData["htmlId"];
  }

  formData = Base64.encode(JSON.stringify(formData));

  let options = { type, serviceType, service, formData, requestId };
  let url;
  dispatch((_, getState) => {
    url =
      getState().app.applicationSettingsData.settings?.DashboardGlobals
        .services;
  });
  return streamPost(dispatch, url, qs.stringify(options), callback, errback);
};

const streamPost = (
  dispatch,
  url,
  data,
  successCB,
  errorCB,
) => {
  const hiStreamClient = new HIStreamClient({ dispatch });
  let requestId = hiStreamClient.getReqId();

  if (typeof data === "string") {
    data = appendRequestId(qs.parse(data), requestId);
    data = qs.stringify(data);
  }
  const reqURL = hiStreamClient.getReqURL(url, { stream: true });

  hiStreamClient.post(reqURL, data, {
    onChunk: (chunk) => {
      successCB(chunk);
    },
  }).then((res) => { })
    .catch((error) => typeof errorCB == "function" && errorCB(error));

  return hiStreamClient;
};
