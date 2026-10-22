import Base64 from "./utils/Base64";
import qs from "qs";
import { v4 as uuidv4 } from "uuid";
import notify from "../components/hi-notifications/notify";
import HIStreamClient from "../app/HiStreamClient";
import {
  parseContentDispositionFileName,
  resolveDownloadChunkSize,
  runResumableDownload,
} from "./resumable-download";
import {
  formDataHasFile,
  isResumableUploadEnabled,
  resolveUploadChunkSize,
  runResumableUpload,
} from "./resumable-upload";

/**
 * Shared resumable entry: resolve app settings + run prepare → /fileDownload.
 * Used by report data export, print export, and resource ZIP export (DRY).
 * Enabled only when setting.xml <resumableDownload>true</resumableDownload>
 * (exposed as applicationSettingsData.resumableDownload; default false).
 */
const isResumableDownloadEnabled = (getState) =>
  Boolean(getState?.()?.app?.applicationSettingsData?.resumableDownload);

/**
 * Non-resumable transfers: after user abort/cancel, ignore late progress/success
 * callbacks so the progress dialog does not reopen.
 */
const attachTransferAbortGuard = (hiAxios, { cancelToken } = {}) => {
  let cancelled = false;
  const markCancelled = () => {
    cancelled = true;
  };
  const originalAbort = hiAxios.abort.bind(hiAxios);
  hiAxios.abort = (prop) => {
    markCancelled();
    try {
      hiAxios.source?.cancel?.("cancel");
    } catch (e) {
      /* already cancelled */
    }
    originalAbort(prop);
  };
  if (cancelToken?.promise && typeof cancelToken.promise.then === "function") {
    cancelToken.promise.then(markCancelled, markCancelled);
  }
  return {
    isCancelled: () => cancelled,
    guard:
      (fn) =>
      (...args) => {
        if (cancelled || typeof fn !== "function") return;
        return fn(...args);
      },
  };
};

const startResumableDownload = ({
  dispatch,
  prepareUrl,
  prepareBody,
  fallbackFileName,
  callback,
  requestId,
  onLegacyProgress,
}) => {
  let hiAxios;
  let chunkSize;
  let useStreamResponse;
  let resolvedUrl = prepareUrl;
  const Notify = notify(dispatch);

  dispatch((dispatch, getState, services) => {
    hiAxios = services(dispatch);
    if (!resolvedUrl) {
      resolvedUrl =
        getState().app.applicationSettingsData.settings.DashboardGlobals
          .reportDownload;
    }
    chunkSize = resolveDownloadChunkSize(getState);
    useStreamResponse = Boolean(
      getState().app.applicationSettingsData?.streamResponse,
    );
  });

  const rid = requestId || uuidv4();

  runResumableDownload({
    dispatch,
    hiAxios,
    prepareUrl: resolvedUrl,
    prepareBody,
    fallbackFileName,
    callback: (info) => {
      if (
        typeof onLegacyProgress === "function" &&
        info &&
        info.progress != null
      ) {
        onLegacyProgress({ loaded: info.loaded, total: info.total });
      }
      if (typeof callback === "function") {
        callback(info);
      }
    },
    Notify,
    chunkSize,
    requestId: rid,
    useStreamResponse,
    handleDownloadFile,
  }).catch((e) => {
    Notify.error({
      type: "Backend",
      message:
        e?.message ||
        "ExportException: Error Occurred while resumable export",
    });
    if (typeof callback === "function") {
      callback(null);
    }
  });

  return hiAxios;
};

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
  let useResumable = false;
  let useStreamResponse = false;
  let chunkSize = 1024 * 1024;
  let hiAxios;
  dispatch((dispatch, getState, services) => {
    hiAxios = services(dispatch);
    useResumable = isResumableUploadEnabled(getState);
    useStreamResponse = Boolean(getState?.()?.app?.applicationSettingsData?.streamResponse);
    chunkSize = resolveUploadChunkSize(getState);
  });

  if (useResumable && formDataHasFile(formData)) {
    const rid = hiAxios.requestId || uuidv4();
    hiAxios.requestId = rid;
    runResumableUpload({
      dispatch,
      hiAxios,
      targetUrl: url,
      formData,
      config: config || {},
      callback,
      errback,
      chunkSize,
      requestId: rid,
      useStreamResponse,
      onEvent: useStreamResponse ? config?.onEvent : undefined,
    });
    return hiAxios;
  }

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
  const { isCancelled, guard } = attachTransferAbortGuard(hiAxios, {
    cancelToken: config.cancelToken,
  });
  const requestConfig = {
    ...config,
    signal: config.signal || hiAxios.controller.signal,
    cancelToken: config.cancelToken || hiAxios.source.token,
    onUploadProgress: guard(config.onUploadProgress),
  };
  hiAxios.instance
    .post(url, data, requestConfig) // window.baseURL removed due to error
    .then((res) => {
      if (isCancelled()) return;
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

export const getRequest = (dispatch, url, data, successCB, errorCB, returnRaw = false) => {
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

      if(returnRaw && typeof successCB == "function") {
        return successCB(data);
      }

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
  const blob = res?.data instanceof Blob ? res.data : new Blob([res.data]);
  const objectUrl = URL.createObjectURL(blob);
  const link = document.createElement("a");
  link.href = objectUrl;
  link.setAttribute("download", fileName || "download");
  link.style.display = "none";
  document.body.appendChild(link);
  link.click();
  link.remove();
  // Revoke after the browser has a chance to start the download
  setTimeout(() => URL.revokeObjectURL(objectUrl), 1000);
};

export const postDownloadRequest = function ({
  dispatch,
  formData,
  reportName = "",
  callback,
  isReportNameInResponse,
}) {
  let useResumable = false;
  dispatch((dispatch, getState) => {
    useResumable = isResumableDownloadEnabled(getState);
  });

  const requestId = formData.requestId || uuidv4();
  const payload = {
    data: Base64.encode(JSON.stringify({ ...formData, requestId })),
    requestId,
  };
  if (useResumable) {
    payload.resumable = true;
  }
  if (formData.reportParameters != null) {
    payload.reportParameters =
      typeof formData.reportParameters === "string"
        ? formData.reportParameters
        : Base64.encode(JSON.stringify(formData.reportParameters));
  }

  const format =
    formData.format || formData.type || formData.printFormat || "bin";
  const baseName = reportName || formData.reportName || "report";
  const fallbackName = isReportNameInResponse
    ? `${baseName}.${format}`
    : `${baseName}_${format}`;

  if (useResumable) {
    return startResumableDownload({
      dispatch,
      prepareBody: qs.stringify(payload),
      fallbackFileName: fallbackName,
      callback,
      requestId,
    });
  }

  let hiAxios;
  let url;
  dispatch((dispatch, getState, services) => {
    hiAxios = services(dispatch);
    url =
      getState().app.applicationSettingsData.settings.DashboardGlobals
        .reportDownload;
  });
  hiAxios.requestId = requestId;
  const { isCancelled, guard } = attachTransferAbortGuard(hiAxios);
  const onProgress = guard((event) => {
    const total = event.total || 0;
    const percentCompleted =
      total > 0 ? Math.round((event.loaded * 100) / total) : 0;
    // No pause/resume — non-resumable
    callback?.({ progress: percentCompleted, phase: "downloading" });
  });
  hiAxios
    .instance({
      url,
      method: "POST",
      responseType: "blob",
      data: qs.stringify({ data: payload.data }),
      signal: hiAxios.controller.signal,
      cancelToken: hiAxios.source.token,
      onDownloadProgress: onProgress,
    })
    .then((res) => {
      if (isCancelled()) {
        callback?.(null);
        return;
      }
      let fileInfo = res.headers["content-disposition"] || "";
      let fileName = fileInfo?.split("=")[1] || "";
      fileName =
        fileName?.substr(1, fileName.length - 2) ||
        (reportName ? "" : "Untitled1");
      if (!isReportNameInResponse) {
        fileName = reportName + "_" + fileName;
      }
      handleDownloadFile(res, fileName);
      callback?.(null);
    })
    .catch(() => {
      callback?.(null);
    });
  return hiAxios;
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
  let useResumable = false;
  dispatch((dispatch, getState) => {
    useResumable = isResumableDownloadEnabled(getState);
  });

  const requestId = formData.requestId || uuidv4();
  const payload = { ...formData, requestId };
  if (useResumable) {
    payload.resumable = true;
  }
  if (payload.reportParameters != null) {
    payload.reportParameters = Base64.encode(
      JSON.stringify(payload.reportParameters),
    );
  }

  const fallbackName =
    (payload.reportName || payload.reportNameParam || "report") +
    (payload.format ? `.${payload.format}` : "");

  if (useResumable) {
    return startResumableDownload({
      dispatch,
      prepareBody: qs.stringify(payload),
      fallbackFileName: fallbackName,
      callback,
      requestId,
    });
  }

  let hiAxios;
  let url;
  const Notify = notify(dispatch);
  dispatch((dispatch, getState, services) => {
    hiAxios = services(dispatch);
    url =
      getState().app.applicationSettingsData.settings.DashboardGlobals
        .reportDownload;
  });
  hiAxios.requestId = requestId;
  const { isCancelled, guard } = attachTransferAbortGuard(hiAxios);
  const data = qs.stringify(payload);
  guard(callback)?.({ progress: 0, phase: "downloading" });
  hiAxios
    .instance({
      url,
      method: "POST",
      responseType: "arraybuffer",
      data,
      signal: hiAxios.controller.signal,
      cancelToken: hiAxios.source.token,
      onDownloadProgress: guard((progressEvent) => {
        const total = progressEvent.total || 0;
        const percentCompleted =
          total > 0 ? Math.round((progressEvent.loaded * 100) / total) : 0;
        callback({ progress: percentCompleted, phase: "downloading" });
      }),
    })
    .then((res) => {
      if (isCancelled()) {
        callback?.(null);
        return;
      }
      let fileInfo = res.headers["content-disposition"] || "";
      let fileName = fileInfo.split("=") || [];
      fileName = fileName[1] || "";
      if (fileName) {
        fileName = fileName.substr(1, fileName.length - 2) || "Untitled1";
        handleDownloadFile(res, fileName);
      } else {
        try {
          let encoding = new TextDecoder("utf-8");
          let arr = new Uint8Array(res.data);
          let resString = encoding.decode(arr);
          let parsed = JSON.parse(resString);
          Notify.error({ type: "Backend", message: parsed.response.message });
        } catch (e) {
          Notify.error({
            type: "Backend",
            message: "ExportException: Error Occurred while exporting",
          });
        }
      }
      callback(null);
    })
    .catch(() => {
      callback?.(null);
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
  let useResumable = false;
  dispatch((dispatch, getState) => {
    useResumable = isResumableDownloadEnabled(getState);
  });

  const requestId = formData.requestId || uuidv4();
  const bodyFields = {
    formData: Base64.encode(JSON.stringify(formData)),
    requestId,
  };
  if (useResumable) {
    bodyFields.resumable = true;
  }
  const prepareBody = qs.stringify(bodyFields);
  const fallbackName =
    (formData.file || formData.dir || "export").replace(/\//g, "_") + ".zip";

  if (useResumable) {
    return startResumableDownload({
      dispatch,
      prepareUrl: url,
      prepareBody,
      fallbackFileName: fallbackName,
      callback,
      requestId,
      onLegacyProgress: downLoadProgress,
    });
  }

  let hiAxios;
  dispatch((dispatch, getState, services) => {
    hiAxios = services(dispatch);
  });
  hiAxios.requestId = requestId;
  const { isCancelled, guard } = attachTransferAbortGuard(hiAxios);
  hiAxios
    .instance({
      url,
      method: "POST",
      responseType: "arraybuffer",
      data: prepareBody,
      signal: hiAxios.controller.signal,
      cancelToken: hiAxios.source.token,
      onDownloadProgress: guard((pr) => {
        if (typeof downLoadProgress === "function") {
          downLoadProgress(pr);
        }
        if (typeof callback === "function" && pr?.total) {
          // No pause/resume functions — non-resumable must not show those buttons
          callback({
            progress: Math.round((pr.loaded * 100) / pr.total),
            phase: "downloading",
          });
        }
      }),
    })
    .then((res) => {
      if (isCancelled()) {
        callback?.(null);
        return;
      }
      const fileName =
        parseContentDispositionFileName(res.headers?.["content-disposition"]) ||
        fallbackName;
      handleDownloadFile(res, fileName);
      if (typeof callback === "function") {
        callback(null);
      }
    })
    .catch(() => {
      if (typeof callback === "function") {
        callback(null);
      }
    });
  return hiAxios;
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

export const instantBIStreamPostRequest = function (
  dispatch,
  url,
  formData,
  onChunk,
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
  const hiStreamClient = new HIStreamClient({ dispatch });
  requestId = requestId || hiStreamClient.getReqId();
  let data = { ...formData, requestId };
  if (typeof data === "object") {
    data = qs.stringify(data);
  }
  const reqURL = hiStreamClient.getReqURL(url);
  hiStreamClient
    .post(reqURL, data, {
      onChunk,
    })
    .catch((error) => typeof errback === "function" && errback(error));
  return hiStreamClient;
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
