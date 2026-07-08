import { v4 as uuidv4 } from "uuid";
import requests from "../../../base/requests";
import { uriConfig } from "../../../base/requests/instantbi.requests";
import { fileBrowserActions } from "../../../redux/actions";
import {
  addIBRecommendations,
  loadingIBRecommendations,
  loadInstantBIMetadata,
  loadIBOpenChatResponse,
  loadInstantBIReportData,
  resetIBChatId,
  savedConfigInstantBIFile,
  setInstantBIPageLoading,
  updateBIBotStatus,
  updateInstantBIReportFile,
  updateRecommendationsVisibility,
} from "../../../redux/actions/instant-bi.actions";
import notify from "../../hi-notifications/notify";
import { IB_CHART_RENDER_ERROR } from "../components/ib-custom-chart";
import { getInsantBISaveData, getSaveData } from "./base";

const recommendationsRequestsByReportId = new Map();

export const abortRecommendationsRequest = (reportId) => {
  const request = recommendationsRequestsByReportId.get(reportId);
  if (request) {
    request.abort();
    recommendationsRequestsByReportId.delete(reportId);
  }
};

const finishRecommendationsRequest = ({
  dispatch,
  activeReportId,
  abortedRef,
}) => {
  if (abortedRef?.current) {
    abortedRef.current = false;
  }
  dispatch(loadingIBRecommendations({ loading: false, reportId: activeReportId }));
  recommendationsRequestsByReportId.delete(activeReportId);
};

export const generateReportBasedOnQueryStringAPI = ({
  dispatch,
  formData,
  successCB,
  errorCB,
}) => {
  // let url;
  // dispatch((dispatch, getState) => {
  //   url = getState().app.applicationSettingsData.settings.adminPaths.profiles;
  // });

  requests.instantBI(dispatch).postInstantBIRequest({
    uri: uriConfig.adhocReportGetDerivedFormdata,
    formData,
    callback: successCB,
    errback: errorCB,
  });
};

export const openMetadataAPI = ({ formData, dispatch, displayNotification = true, recommendations = true, activeReportId, chatId }) => {
  const Notify = notify(dispatch);
  dispatch(loadInstantBIMetadata({ loading: true, reportId: activeReportId }));
  const transformedFormData = {
    dir: formData.location,
    file: formData.metadataFileName
  };
  
  requests.instantBI(dispatch).getMetadata({
    formData: transformedFormData,
    uri: uriConfig.adhocMetadataGet,
    callback: (res) => {
      let { classifier, uniqueId, metadataName, metadataDir } = res;
      res.formData = formData;
      let metadata = {
        location: formData.location,
        classifier: classifier,
        metadataFileName: uniqueId + ".metadata",
        metadataName,
        metadataDir,
      };
      // Commented out getFunctions (type: adhoc, serviceType: metadata) and getContents (type: content, serviceType: static) calls
      // requests.instantBI(dispatch).getFunctions({
      //   formData: metadata,
      //   uri: uriConfig.adhocMetadataGetFunctions,
      //   callback: (functions) => {
      //     requests.instantBI(dispatch).getDateFunctions({
      //       formData: { contentId: "Static/standardDate" },
      //       uri: uriConfig.contentStaticGetContents,
      //       callback: (dateFunctions) => {
              dispatch(
                loadInstantBIMetadata({ formData, ...res, uid: uuidv4(), reportId: activeReportId, },
                  // funcs: functions,
                  // dateFunctions: dateFunctions || {},
                )
              );
              displayNotification && Notify.success({ type: "Frontend", message: "Agent connected successfully." });
              recommendations && fetchRecommendationsAPI({ metadata, formData, dispatch, activeReportId, chatId });
      //     },
      //     errback: (e) => {
      //       // Notify.error({ type: "Backend", message: e.message });
      //       dispatch(loadInstantBIMetadata({ loading: false, reportId: activeReportId }));
      //     },
      //   });
      // },
      // errback: (e) => {
      //   // Notify.error({ type: "Backend", message: e.message });
      //   dispatch(loadInstantBIMetadata({ loading: false, reportId: activeReportId }));
      // },
      // });
    },
    errback: (e) => {
      // Notify.error({ type: "Backend", message: e.message });
      dispatch(loadInstantBIMetadata({ loading: false, reportId: activeReportId }));
    },
  });
};

export const buildAgentConnectFormData = ({ path, title }) => {
  const fullPath = path;
  const pathParts = path.split("/");
  const metadataFileName = pathParts.pop();
  const location = pathParts.join("/");
  return {
    fullPath,
    formData: {
      location,
      metadataFileName,
      title,
      path: pathParts,
    },
  };
};

export const connectAgentToReport = ({ path, title, reportId, dispatch }) => {
  if (!reportId) {
    return;
  }
  const { fullPath, formData } = buildAgentConnectFormData({ path, title });
  const chatId = uuidv4();
  dispatch(resetIBChatId({ reportId, newChatId: chatId }));
  openMetadataAPI({
    formData: { ...formData, path: fullPath },
    activeReportId: reportId,
    dispatch,
    chatId,
  });
};

export const saveInstantBIReportAPI = ({
  activeReport,
  dispatch,
  saveFileInfo,
  // uuid,
  mode,
  // searchValue,
}) => {
  // const Notify = notify(dispatch);
  const saveData = getInsantBISaveData({ activeReport, saveFileInfo, dispatch })
  dispatch(
    savedConfigInstantBIFile({
      isSaving: true,
    })
  );

  requests.instantBI(dispatch).saveInstantBIReport({
    formData: saveData,
    uri: uriConfig.adhocInstantSaveReport,
    callback: (res) => {
      if (res && res.uuid) {
        dispatch(
          savedConfigInstantBIFile({
            location: saveFileInfo.location,
            reportName: saveFileInfo.reportName,
            uuid: res.uuid,
            mode,
            isSaving: false,
          })
        );
        if (res.data) {
          dispatch(fileBrowserActions.saveFileinFb(res.data[0]));
        }
      }
    },
    errback: (err) => {
      console.error(err);
      dispatch(
        savedConfigInstantBIFile({
          isSaving: false,
        })
      );
      // Notify.error({ type: "Backend", message: err.message });
    },
  });
};

export const fetchInstantBIReportAPI = ({
  file,
  dispatch,
  mode,
  setFileInfo,
  reportId,
  chatId
}) => {
  // const Notify = notify(dispatch);
  let { path, name } = file;
  let formData = {
    dir: path.replace(name, "").replace(/[\\|\/]+$/, ""),
    file: name,
  };
  dispatch(
    setInstantBIPageLoading({
      loading: true,
    })
  );
  let url = uriConfig.adhocInstantGetReport;
  let recommendations = true
  if (['edit'].includes(mode)) {
    url = uriConfig.adhocInstantGetReportForEdit
    recommendations = false
  }
  requests.instantBI(dispatch).getInstantBIReportForEdit({
    formData,
    uri: url,
    callback: (res) => {

      const { data = {} } = res || {}
      if (typeof setFileInfo === "function") {
        setFileInfo({ fileTitle: data?.reportName ?? '' });
      }
      const chatId = data?.state?.activeChatID || uuidv4();
      if (['edit'].includes(mode)) {
        const { metadata = {} } = data || {}
        let fullPath = `${metadata?.location}/${metadata?.metadataFileName}`
        let formData = {
          location: metadata?.location,
          metadataFileName: metadata?.metadataFileName,
          path: fullPath,
        };
        // openMetadataAPI({ formData: { ...formData, path: fullPath }, dispatch, displayNotification: false, recommendations, activeReportId: reportId, chatId });
      }
      let payload = {
        data,
        location: formData.dir,
        uuid: formData.file,
        loading: false,
        mode,
        reportId
      }
      dispatch(loadInstantBIReportData(payload))
    },
    errback: (err) => {
      // dispatch(setAccessDeniedInfo({ subTitle: err.message }));
      // dispatch(updateRoute("/access-denied"));
    },
  });
};

export const parseInstantBIChatResponse = (res) => {
  const { chat_response = {} } = res || {};
  const {
    viz = {},
    sql: sqlData = {},
    summary: summaryData = {},
    data = [],
    metadata = [],
  } = chat_response || {};
  const vf = viz.vf_template ? atob(viz.vf_template) : "";
  const vf_title = viz.vf_title || "";
  const sql = sqlData.raw_sql || "";
  const botMessage = summaryData.insight || "";
  const {
    data: _dataToRemove,
    metadata: _metadataToRemove,
    ...chatResponseWithoutData
  } = chat_response || {};

  return {
    vf,
    vf_title,
    sql,
    sqlDetails: sqlData,
    metadata,
    data,
    botMessage,
    createPreview: Boolean(vf),
    fullChatResponse: chatResponseWithoutData,
  };
};

export const instantBiChatAPI = ({ formData, dispatch, onAIMessage = () => { }, activeReportId, chatId, chatSequenceId, abortedRef }) => {
  dispatch(updateBIBotStatus({ status: true, reportId: activeReportId }))
  return instantBIInteractiveChatRequest({
    dispatch,
    uri: uriConfig.instantBIChat,
    input: formData.input,
    subject: formData.subject,
    chatId,
    chatSequenceId,
    successCB: (res) => {
      dispatch(updateBIBotStatus({ status: false, reportId: activeReportId }))
      onAIMessage({
        ...parseInstantBIChatResponse(res),
        userInput: formData.input,
        chatSequenceId,
      })
    },
    errorCB: (e) => {
      dispatch(updateBIBotStatus({ status: false, reportId: activeReportId }))
      if (abortedRef?.current) {
        abortedRef.current = false;
        return
      }
      onAIMessage({
        vf: "",
        sql: "",
        error: true,
        summary: e.message,
        botMessage: IB_CHART_RENDER_ERROR,
        abortedRequest: false,
      })
    },
  });
};

export const buildInstantBIInteractiveChatFormData = ({
  input,
  subject,
  chatId,
  chatSequenceId,
  requestId,
  nestedFormData,
}) => {
  const formData = {
    input,
    chatid: chatId,
    chat_sequence_id: chatSequenceId,
  };
  if (subject) {
    formData.subject = subject;
  }
  if (requestId) {
    formData.requestId = requestId;
  }
  if (nestedFormData) {
    formData.formData = nestedFormData;
  }
  return formData;
};

const instantBIInteractiveChatRequest = ({
  dispatch,
  uri,
  input,
  subject,
  chatId,
  chatSequenceId,
  requestId,
  nestedFormData,
  successCB = () => {},
  errorCB = () => {},
}) =>
  requests.instantBI(dispatch).instantBIChatRequest({
    formData: buildInstantBIInteractiveChatFormData({
      input,
      subject,
      chatId,
      chatSequenceId,
      requestId,
      nestedFormData,
    }),
    uri,
    callback: successCB,
    errback: errorCB,
  });

export const buildInstantBIChatRequestFormData = ({
  chatSequenceId,
  input,
  location,
  fileName,
  requestId,
}) => {
  const requestFormData = {
    chat_sequence_id: chatSequenceId,
    formData: {
      input,
      location,
      fileName,
    },
  };
  if (requestId) {
    requestFormData.requestId = requestId;
  }
  return requestFormData;
};

const instantBIChatFormRequest = ({
  dispatch,
  uri,
  chatSequenceId,
  input,
  location,
  fileName,
  requestId,
  successCB = () => {},
  errorCB = () => {},
}) =>
  requests.instantBI(dispatch).instantBILoadChatRequest({
    formData: buildInstantBIChatRequestFormData({
      chatSequenceId,
      input,
      location,
      fileName,
      requestId,
    }),
    uri,
    callback: successCB,
    errback: errorCB,
  });

export const instantLoadChatAPI = (params) =>
  instantBIChatFormRequest({ ...params, uri: uriConfig.instantLoadChat });

export const shouldUseLoadChatPayload = ({
  isOpenMode = false,
  isEditMode = false,
  needsLoadChat = false,
  persistedInFile = false,
} = {}) => {
  if (isOpenMode) return true;
  if (isEditMode) return Boolean(persistedInFile || needsLoadChat);
  return false;
};

export const shouldUseLoadChatPayloadForInsight = shouldUseLoadChatPayload;

export const instantDataInsightAPI = ({
  dispatch,
  useLoadChatPayload = false,
  chatSequenceId,
  input,
  location,
  fileName,
  chatId,
  subject,
  agent,
  requestId,
  successCB = () => {},
  errorCB = () => {},
}) => {
  if (useLoadChatPayload) {
    return instantBIChatFormRequest({
      dispatch,
      uri: uriConfig.instantDataInsight,
      chatSequenceId,
      input,
      location,
      fileName,
      requestId,
      successCB,
      errorCB,
    });
  }

  return instantBIInteractiveChatRequest({
    dispatch,
    uri: uriConfig.instantDataInsight,
    input,
    subject: subject || (agent ? { agent } : undefined),
    chatId,
    chatSequenceId,
    requestId,
    successCB,
    errorCB,
  });
};

export const loadInstantBIOpenChat = ({
  dispatch,
  reportId,
  chatSequenceId,
  userInput,
  location,
  fileName,
  source = "play-button",
  showSuccessNotification = true,
  time,
  Notify,
  onComplete = () => {},
  abortedRef,
}) => {
  if (!chatSequenceId || !userInput || !location || !fileName) {
    Notify?.error?.({
      type: "Frontend",
      message: "Required load chat data is missing.",
    });
    onComplete({ success: false });
    return;
  }

  return instantLoadChatAPI({
    dispatch,
    chatSequenceId,
    input: userInput,
    location,
    fileName,
    successCB: (response) => {
      const chatResponse = response?.chat_response;
      const hasChatResponse =
        chatResponse && Object.keys(chatResponse).length > 0;
      if (!hasChatResponse || !reportId) {
        onComplete({ success: false });
        return;
      }
      dispatch(
        loadIBOpenChatResponse({
          reportId,
          chatSequenceId,
          chatResponse,
          source,
          time,
        })
      );
      if (showSuccessNotification) {
        Notify?.success?.({
          type: "Frontend",
          message: response?.message || "Chat loaded successfully.",
        });
      }
      onComplete({ success: true, response });
    },
    errorCB: () => {
      onComplete({ success: false, aborted: abortedRef?.current });
    },
  });
};

export const loadInstantBIDataInsight = ({
  dispatch,
  reportId,
  chatSequenceId,
  userInput,
  location,
  fileName,
  chatId,
  agent,
  useLoadChatPayload = false,
  existingChatResponse = {},
  Notify,
  abortedRef,
  onComplete = () => {},
}) => {
  if (useLoadChatPayload) {
    if (!chatSequenceId || !userInput || !location || !fileName) {
      Notify?.error?.({
        type: "Frontend",
        message: "Required load chat data is missing.",
      });
      onComplete({ success: false });
      return;
    }
  } else if (!chatSequenceId || !userInput || !chatId) {
    Notify?.error?.({
      type: "Frontend",
      message: "Required chat data is missing.",
    });
    onComplete({ success: false });
    return;
  }

  return instantDataInsightAPI({
    dispatch,
    useLoadChatPayload,
    chatSequenceId,
    input: userInput,
    location,
    fileName,
    chatId,
    agent,
    successCB: (response) => {
      if (abortedRef?.current) {
        abortedRef.current = false;
        onComplete({ success: false, aborted: true });
        return;
      }
      if (response?.error) {
        Notify?.error?.({
          type: "Frontend",
          message: response.error,
        });
        onComplete({ success: false });
        return;
      }
      const insight = response?.insight;
      if (!insight || !reportId) {
        onComplete({ success: false });
        return;
      }
      const chatResponse = {
        ...existingChatResponse,
        data_insight: {
          insight,
          token_usage: response.token_usage || {},
        },
      };
      dispatch(
        loadIBOpenChatResponse({
          reportId,
          chatSequenceId,
          chatResponse,
          source: "data-insight",
        })
      );
      onComplete({ success: true, response });
    },
    errorCB: () => {
      const aborted = Boolean(abortedRef?.current);
      if (abortedRef?.current) {
        abortedRef.current = false;
      }
      onComplete({ success: false, aborted });
    },
  });
};


export const agentGenerateAPI = ({ dir, file, dispatch, successCB, errorCB }) => {
  const formData = ({dir: dir, file: file})
  return requests.instantBI(dispatch).postInstantBIRequest({
    uri: uriConfig.agentGenerate,
    formData,
    callback: successCB,
    errback: errorCB,
  });
};

export const agentSaveAPI = ({ dir, file, agentDir, agentName, uuid,content, dispatch, successCB, errorCB }) => {
  const parsedState =
    typeof content === "string" ? JSON.parse(content) : content;
  const formData = {
    metadata: { location: dir, metadataFileName: file },
    location: agentDir,
    state: parsedState,
    agentName: agentName,
  };
  if (uuid) {
    formData.uuid = uuid;
  }
  
  const encodedFormData =formData;
  
  return requests.instantBI(dispatch).postInstantBIRequest({
    uri: uriConfig.agentSave,
    formData: encodedFormData,
    callback: successCB,
    errback: errorCB,
  });
};

export const agentEditServiceAPI = ({ dir, file, dispatch, successCB, errorCB }) => {
  const formData = {
    dir: dir,
    file: file
  };
  
  return requests.instantBI(dispatch).postInstantBIRequest({
    uri: uriConfig.agentEdit,
    formData: formData,
    callback: successCB,
    errback: errorCB,
  });
};

export const fetchRecommendationsAPI = ({
  metadata,
  formData: originalFormData,
  dispatch,
  activeReportId,
  chatId,
  abortedRef,
}) => {
  abortRecommendationsRequest(activeReportId);

  dispatch(loadingIBRecommendations({ loading: true, reportId: activeReportId }));

  let activeRequest = null;
  const requestAbortedRef = abortedRef || { current: false };

  const abortController = {
    abort(prop = {}) {
      requestAbortedRef.current = true;
      activeRequest?.abort?.(prop);
      finishRecommendationsRequest({ dispatch, activeReportId, abortedRef: requestAbortedRef });
      activeRequest = null;
    },
  };

  recommendationsRequestsByReportId.set(activeReportId, abortController);

  const formData = {
    agent: {
      file: originalFormData.metadataFileName,
      dir: originalFormData.location,
    },
    chatid: chatId,
  };

  activeRequest = requests.instantBI(dispatch).instantBIFetchDomain({
    formData,
    uri: uriConfig.instantBIDomain,
    callback: (res) => {
      if (requestAbortedRef.current) {
        finishRecommendationsRequest({ dispatch, activeReportId, abortedRef: requestAbortedRef });
        return;
      }

      const { domain = "" } = res || {};
      if (domain) {
        activeRequest = requests.instantBI(dispatch).instantBIFetchRecommendation({
          formData: {
            ...formData,
            domain,
          },
          uri: uriConfig.instantBIRecommendation,
          callback: (recommendationRes) => {
            if (requestAbortedRef.current) {
              finishRecommendationsRequest({ dispatch, activeReportId, abortedRef: requestAbortedRef });
              return;
            }

            const { questions = [] } = recommendationRes || {};
            dispatch(addIBRecommendations({ recommendations: questions, reportId: activeReportId }));
            dispatch(updateRecommendationsVisibility({ visible: true, reportId: activeReportId }));
            finishRecommendationsRequest({ dispatch, activeReportId, abortedRef: requestAbortedRef });
          },
          errback: () => {
            if (requestAbortedRef.current) {
              finishRecommendationsRequest({ dispatch, activeReportId, abortedRef: requestAbortedRef });
              return;
            }
            finishRecommendationsRequest({ dispatch, activeReportId, abortedRef: requestAbortedRef });
          },
        });
        return;
      }

      finishRecommendationsRequest({ dispatch, activeReportId, abortedRef: requestAbortedRef });
    },
    errback: () => {
      if (requestAbortedRef.current) {
        finishRecommendationsRequest({ dispatch, activeReportId, abortedRef: requestAbortedRef });
        return;
      }
      finishRecommendationsRequest({ dispatch, activeReportId, abortedRef: requestAbortedRef });
    },
  });

  return abortController;
};