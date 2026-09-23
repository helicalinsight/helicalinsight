import { v4 as uuidv4 } from "uuid";
import requests from "../../../base/requests";
import { uriConfig } from "../../../base/requests/instantbi.requests";
import { fileBrowserActions } from "../../../redux/actions";
import {
  addIBRecommendations,
  loadIBOpenChatResponse,
  loadingIBRecommendations,
  loadInstantBIMetadata,
  loadInstantBIReportData,
  resetIBChatId,
  savedConfigInstantBIFile,
  setIBChartList,
  setInstantBIPageLoading,
  updateBIBotStatus,
  updateHreportInitialInteraction,
  updateIBVizPreference,
  updateMetadataForHreport,
  updateRecommendationsVisibility
} from "../../../redux/actions/instant-bi.actions";
import createHReportBridge from "../../bridges/hreport/hreport-bridge";
import notify from "../../hi-notifications/notify";
import { openMetadata } from "../../hi-reports/utils/base";
import { IB_CHART_RENDER_ERROR } from "../components/ib-custom-chart";
import { getInsantBISaveData } from "./base";
import { getMetadataForHreport, loadInstantBiHreports } from "./common-utils";

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

const loadMetadataForHreport = async (res, dispatch, callback) => {
  const { data = {} } = res || {}
  const { metadata = {} } = data || {}
  const { location, metadataFileName } = metadata || {}
  const formData = { location, metadataFileName };
  const response = await openMetadata(formData, dispatch, () => { }, true);
  if (response && !response.error) {
    if (typeof callback === "function") {
      return callback(response)
    }
    dispatch(updateMetadataForHreport(response));
  }
  if (typeof callback === "function") {
    callback()
  }
}

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
      dispatch(
        loadInstantBIMetadata({ formData, ...res, uid: uuidv4(), reportId: activeReportId, })
      );
      displayNotification && Notify.success({ type: "Frontend", message: "Semantic model connected successfully." });
      recommendations && fetchRecommendationsAPI({ metadata, formData, dispatch, activeReportId, chatId });
      loadMetadataForHreport(res, dispatch)
    },
    errback: (e) => {
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
  const saveData = getInsantBISaveData({ activeReport, saveFileInfo, dispatch, mode })
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
          dispatch(fileBrowserActions.saveFileinFb(res.data));
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
      // const chatId = data?.state?.activeChatID || uuidv4();
      // if (['edit'].includes(mode)) {
      //   const { metadata = {} } = data || {}
      //   let fullPath = `${metadata?.location}/${metadata?.metadataFileName}`
      //   let formData = {
      //     location: metadata?.location,
      //     metadataFileName: metadata?.metadataFileName,
      //     path: fullPath,
      //   };
      //   // openMetadataAPI({ formData: { ...formData, path: fullPath }, dispatch, displayNotification: false, recommendations, activeReportId: reportId, chatId });
      // }
      let payload = {
        data,
        location: formData.dir,
        uuid: formData.file,
        loading: false,
        mode,
        reportId
      }
      loadMetadataForHreport(
        res,
        dispatch,
        (reportMetadata) => {
          dispatch(loadInstantBIReportData({ ...payload, reportMetadata }));
          loadInstantBiHreports({
            dispatch,
            reportState: data.state,
          })
        })
    },
    errback: (err) => {
      dispatch(
        setInstantBIPageLoading({
          loading: false,
        })
      );
      // dispatch(setAccessDeniedInfo({ subTitle: err.message }));
      // dispatch(updateRoute("/access-denied"));
    },
  });
};

const handleUpdateHreportEvent = (reportId, { event, hreportId, data }, dispatch) => {
  dispatch(updateHreportInitialInteraction({ reportId, hreportId, data, event }))
}

export const parseInstantBIChatResponseBody = (chat_response = {}) => {
  const {
    viz = {},
    sql: sqlData = {},
    summary: summaryData = {},
    data = [],
    metadata = [],
    error = "",
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
    error: error || "",
  };
};

export const parseInstantBIChatResponse = (res) => {
  const rawMode = res?.mode;
  const mode = String(rawMode || "fast").toLowerCase();
  const steps = Array.isArray(res?.chat_responses) ? res.chat_responses : [];
  const askedQuestions = Array.isArray(res?.asked_questions) ? res.asked_questions : [];
  const questionHistory = Array.isArray(res?.question_history)
    ? res.question_history
    : askedQuestions.map((question, index) => ({
      index: index + 1,
      question,
      title: "",
    }));
  const phase = String(res?.phase || "").toLowerCase();
  const isThink = mode === "think" || (!rawMode && (steps.length > 0 || askedQuestions.length > 0));

  if (isThink) {
    const chatResponses = steps.map((step) => {
      const parsed = parseInstantBIChatResponseBody(step?.chat_response || {});
      const subQuestion = step?.sub_question || "";
      const title = step?.title || "";
      const analysis = step?.analysis || "";
      return {
        ...parsed,
        subQuestion,
        title,
        analysis,
        stepChatSeqId: step?.chat_seq_id || "",
        botMessage: parsed.botMessage || analysis || "",
        vf_title: parsed.vf_title || title || parsed.vf_title,
        error: parsed.error || "",
      };
    });
    return {
      mode: "think",
      phase: phase || (chatResponses.length ? "execute" : "plan"),
      chatResponses,
      askedQuestions,
      questionHistory,
      citedQuestionIndexes: Array.isArray(res?.cited_question_indexes)
        ? res.cited_question_indexes
        : [],
      openingInsight: res?.opening_insight || res?.openingInsight || "",
      finalAnswer: res?.final_answer || "",
      plan: res?.plan || {},
      llmActivityDetails: res?.llm_activity_details || null,
      dashboardModel: (() => {
        const dashboard = res?.dashboard;
        const model = res?.dashboard_model;
        if (dashboard && typeof dashboard === "object" && Object.keys(dashboard).length) return dashboard;
        if (model && typeof model === "object" && Object.keys(model).length) return model;
        return dashboard || model || null;
      })(),
      error: res?.error || "",
    };
  }

  return {
    mode: "fast",
    ...parseInstantBIChatResponseBody(res?.chat_response || {}),
    error: res?.error || (res?.chat_response || {}).error || "",
  };
};

const deliverParsedChatViaBridge = async ({
  parsedResponse,
  dispatch,
  activeReportId,
  onAIMessage,
  userInput,
  chatSequenceId,
}) => {
  if (parsedResponse.error) {
    onAIMessage({
      botMessage: "",
      ...parsedResponse,
      error: true,
      abortedRequest: false,
      userInput,
      chatSequenceId,
    });
    return;
  }

  const hreportProps = {
    reportId: uuidv4(),
  };
  hreportProps.reportMetadata = getMetadataForHreport(dispatch);
  if (parsedResponse.fullChatResponse) {
    parsedResponse.fullChatResponse.hreportId = hreportProps.reportId;
  }

  await new Promise((resolve) => {
    const bridge = createHReportBridge({
      dispatch,
      reportModel: parsedResponse?.fullChatResponse?.report_model || {},
      onComplete: () => {
        onAIMessage({
          ...parsedResponse,
          userInput,
          chatSequenceId,
        });
        resolve();
      },
      onError: () => {
        onAIMessage({
          botMessage: "",
          ...parsedResponse,
          error: true,
          abortedRequest: false,
          userInput,
          chatSequenceId,
        });
        resolve();
      },
      eventUpdater: (e) => {
        handleUpdateHreportEvent(activeReportId, e, dispatch);
      },
      ...hreportProps,
    });
    bridge.init().catch(() => {
      onAIMessage({
        botMessage: "",
        ...parsedResponse,
        error: true,
        abortedRequest: false,
        userInput,
        chatSequenceId,
      });
      resolve();
    });
  });
};

/** Hydrate a think-step Preview from stored report_model (no /interactive LLM call). */
export const hydrateThinkStepFromReportModel = async ({
  stepItem = {},
  dispatch,
  activeReportId,
  chatSequenceId,
  userInput = "",
}) => {
  const chatResponse = stepItem.fullChatResponse
    || stepItem.chat_response
    || stepItem.chatResponse
    || {};
  const reportModel = stepItem.report_model
    || stepItem.reportModel
    || chatResponse.report_model
    || {};
  if (!reportModel || !Object.keys(reportModel).length) {
    return {
      error: true,
      botMessage: "No report model available for this step.",
    };
  }
  const fullChatResponse = {
    ...chatResponse,
    report_model: reportModel,
  };
  const parsed = {
    ...parseInstantBIChatResponseBody(fullChatResponse),
    fullChatResponse,
    botMessage: chatResponse?.summary?.insight
      || stepItem.answer
      || stepItem.analysis
      || "",
  };
  return new Promise((resolve) => {
    deliverParsedChatViaBridge({
      parsedResponse: parsed,
      dispatch,
      activeReportId,
      userInput: userInput || stepItem.question || "",
      chatSequenceId: chatSequenceId
        || stepItem.chat_seq_id
        || stepItem.chatSeqId
        || "",
      onAIMessage: (payload) => resolve(payload),
    });
  });
};

const normalizeThinkQuestionHistory = (questionHistory = []) =>
  (Array.isArray(questionHistory) ? questionHistory : []).map((item, index) => {
    const chatResponse = item?.fullChatResponse
      || item?.chat_response
      || item?.chatResponse
      || {};
    const reportModel = item?.report_model
      || item?.reportModel
      || chatResponse?.report_model
      || {};
    const fullChatResponse = {
      ...chatResponse,
      ...(reportModel && Object.keys(reportModel).length
        ? { report_model: reportModel }
        : {}),
    };
    return {
      ...item,
      index: item?.index || index + 1,
      fullChatResponse,
      chat_response: fullChatResponse,
      report_model: reportModel,
      chat_seq_id: item?.chat_seq_id || item?.chatSeqId || "",
    };
  });

const processThinkChatResponses = async ({
  chatResponses = [],
  dispatch,
  activeReportId,
  formData,
  chatSequenceId,
  onAIMessage,
  abortedRef,
}) => {
  try {
    for (let index = 0; index < chatResponses.length; index += 1) {
      if (abortedRef?.current) {
        break;
      }
      const step = chatResponses[index];
      const stepSeqId = step.stepChatSeqId || `${chatSequenceId}-${index + 1}`;
      await deliverParsedChatViaBridge({
        parsedResponse: step,
        dispatch,
        activeReportId,
        onAIMessage,
        userInput: step.subQuestion || formData.input,
        chatSequenceId: stepSeqId,
      });
    }
  } finally {
    dispatch(updateBIBotStatus({ status: false, reportId: activeReportId }));
  }
};

const deliverThinkPlanMessage = ({
  parsedResponse,
  dispatch,
  activeReportId,
  formData,
  chatSequenceId,
  onAIMessage,
}) => {
  dispatch(updateBIBotStatus({ status: false, reportId: activeReportId }));
  onAIMessage({
    mode: "think",
    phase: parsedResponse.phase || "plan",
    isThinkPlan: true,
    askedQuestions: parsedResponse.askedQuestions || [],
    questionHistory: normalizeThinkQuestionHistory(parsedResponse.questionHistory || []),
    citedQuestionIndexes: parsedResponse.citedQuestionIndexes || [],
    openingInsight: parsedResponse.openingInsight || "",
    finalAnswer: parsedResponse.finalAnswer || "",
    botMessage: parsedResponse.finalAnswer || "",
    text: parsedResponse.finalAnswer || "",
    createPreview: false,
    userInput: formData.input,
    chatSequenceId,
    plan: parsedResponse.plan || {},
    llmActivityDetails: parsedResponse.llmActivityDetails || null,
    dashboardModel: parsedResponse.dashboardModel || null,
    error: Boolean(parsedResponse.error),
  });
};

export const instantBiChatAPI = ({
  formData,
  dispatch,
  onAIMessage = () => { },
  onProgress,
  activeReportId,
  chatId,
  chatSequenceId,
  abortedRef
}) => {
  dispatch(updateBIBotStatus({ status: true, reportId: activeReportId }))
  return instantBIInteractiveChatRequest({
    dispatch,
    uri: uriConfig.instantBIChat,
    input: formData.input,
    subject: formData.subject,
    chatId,
    chatSequenceId,
    mode: formData.mode,
    onProgress,
    successCB: async (res) => {
const parsedResponse = parseInstantBIChatResponse(res);
const { error, mode, chatResponses = [], ...fastParsed } = parsedResponse;

if (!error && !res.error) {
  const hreportProps = {
    reportId: uuidv4(),
  };
  hreportProps.reportMetadata = getMetadataForHreport(dispatch);
  if (parsedResponse.fullChatResponse) {
    parsedResponse.fullChatResponse.hreportId = hreportProps.reportId;
  } else {
    parsedResponse.fullChatResponse = { hreportId: hreportProps.reportId };
  }
}

if (mode === "think") {
  if (error && !(parsedResponse.askedQuestions || []).length && !chatResponses.length) {
    dispatch(updateBIBotStatus({ status: false, reportId: activeReportId }))
    onAIMessage({
      botMessage: "",
      error: true,
      abortedRequest: false,
      userInput: formData.input,
      chatSequenceId,
    });
    return;
  }
  // Think execute returns text findings (question_history.analysis + final_answer).
  // Empty chat_responses means no chart hydration — show the investigation panel.
  if (
    !(chatResponses || []).length &&
    ((parsedResponse.askedQuestions || []).length || parsedResponse.finalAnswer)
  ) {
    deliverThinkPlanMessage({
      parsedResponse,
      dispatch,
      activeReportId,
      formData,
      chatSequenceId,
      onAIMessage,
    });
    return;
  }
  await processThinkChatResponses({
    chatResponses,
          dispatch,
          activeReportId,
          formData,
          chatSequenceId,
          onAIMessage,
          abortedRef,
        });
        return;
      }

      if (!error) {
        await deliverParsedChatViaBridge({
          parsedResponse: { ...fastParsed, error },
          dispatch,
          activeReportId,
          onAIMessage: (payload) => {
            dispatch(updateBIBotStatus({ status: false, reportId: activeReportId }))
            onAIMessage(payload);
          },
          userInput: formData.input,
          chatSequenceId,
        });
      } else {
        dispatch(updateBIBotStatus({ status: false, reportId: activeReportId }))
        onAIMessage({
          botMessage: "",
          ...fastParsed,
          error: true,
          abortedRequest: false,
          userInput: formData.input,
          chatSequenceId
        })
      }

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
        botMessage: "",
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
  mode,
  sql,
  chatResponseItem,
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
  if (mode) {
    formData.mode = mode;
  }
  if (["think", "auto"].includes(String(mode || "").toLowerCase())) {
    formData.show_llm_activity_details = true;
  }
  if (sql) {
    formData.sql = sql;
  }
  if (chatResponseItem && typeof chatResponseItem === "object") {
    formData.chat_response_item = chatResponseItem;
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
  mode,
  sql,
  chatResponseItem,
  successCB = () => { },
  errorCB = () => { },
  onProgress,
}) =>
  requests.instantBI(dispatch).instantBIChatRequest({
    formData: buildInstantBIInteractiveChatFormData({
      input,
      subject,
      chatId,
      chatSequenceId,
      requestId,
      nestedFormData,
      mode,
      sql,
      chatResponseItem,
    }),
    uri,
    callback: successCB,
    errback: errorCB,
    onProgress,
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
  successCB = () => { },
  errorCB = () => { },
  onProgress,
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
    onProgress,
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
  sql,
  chatResponseItem,
  successCB = () => { },
  errorCB = () => { },
  onProgress,
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
      onProgress,
    });
  }

  return instantBIInteractiveChatRequest({
    dispatch,
    uri: uriConfig.instantDataInsight,
    input,
    subject: subject || (agent ? { model: agent } : undefined),
    chatId,
    chatSequenceId,
    requestId,
    sql,
    chatResponseItem,
    successCB,
    errorCB,
    onProgress,
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
  onComplete = () => { },
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
  sql,
  Notify,
  abortedRef,
  onComplete = () => { },
  onProgress,
}) => {
  const resolvedSql = sql
    || existingChatResponse?.sql?.raw_sql
    || existingChatResponse?.sql
    || "";
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
    sql: typeof resolvedSql === "string" ? resolvedSql : "",
    chatResponseItem: existingChatResponse,
    onProgress,
    successCB: (response) => {
      if (abortedRef?.current) {
        abortedRef.current = false;
        onComplete({ success: false, aborted: true });
        return;
      }
      if (response?.error) {
        Notify?.error?.({
          type: "Frontend",
          message: response?.error || IB_CHART_RENDER_ERROR,
        });
        onComplete({ success: false, response });
        return;
      }
      const insight = response?.insight;
      if (!insight || !reportId) {
        Notify?.error?.({
          type: "Frontend",
          message: "Unable to generate explanation for this chart.",
        });
        onComplete({ success: false, response });
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
      onComplete({ success: true, response, insight, data_insight: chatResponse.data_insight });
    },
    errorCB: (err) => {
      const aborted = Boolean(abortedRef?.current);
      if (abortedRef?.current) {
        abortedRef.current = false;
      } else {
        Notify?.error?.({
          type: "Frontend",
          message: err?.message || IB_CHART_RENDER_ERROR,
        });
      }
      onComplete({ success: false, aborted, error: err });
    },
  });
};

export const buildInstantBIConvertChartFormData = ({
  vfTemplate,
  selectedChart,
  chatId,
  chatSequenceId,
}) => ({
  vf_template: vfTemplate,
  selected_chart: selectedChart,
  chat_id: chatId,
  chat_sequence_id: chatSequenceId,
});

export const instantConvertChartAPI = ({
  dispatch,
  vfTemplate,
  selectedChart,
  chatId,
  chatSequenceId,
  successCB = () => { },
  errorCB = () => { },
}) =>
  requests.instantBI(dispatch).instantBIConvertChartRequest({
    uri: uriConfig.instantConvertChart,
    formData: buildInstantBIConvertChartFormData({
      vfTemplate,
      selectedChart,
      chatId,
      chatSequenceId,
    }),
    callback: successCB,
    errback: errorCB,
  });

export const convertInstantBIChart = ({
  dispatch,
  reportId,
  chatSequenceId,
  chatId,
  vfTemplate,
  selectedChart,
  Notify,
  onComplete = () => { },
}) => {
  if (!chatSequenceId || !chatId || !vfTemplate || !selectedChart) {
    Notify?.error?.({
      type: "Frontend",
      message: "Required convert chart data is missing.",
    });
    onComplete({ success: false });
    return;
  }

  const notifyError = (message) =>
    Notify?.error?.({ type: "Frontend", message });

  const applyConvertedViz = (payload = {}) => {
    const viz = payload?.chat_response?.viz || payload?.viz || {};
    const nextTemplate =
      viz.vf_template ||
      payload?.vf_template ||
      (payload?.vf ? btoa(payload.vf) : null);

    let nextVf = payload?.vf || null;
    if (!nextVf && nextTemplate) {
      try {
        nextVf = atob(nextTemplate);
      } catch {
        nextVf = null;
      }
    }
    if (!nextVf || !reportId) return null;

    dispatch(
      updateIBVizPreference({
        reportId,
        chatSequenceId,
        chart_name: viz.chart_name || payload?.chart_name || selectedChart,
        vf: nextVf,
        vf_template: nextTemplate || btoa(nextVf),
      })
    );
    return nextVf;
  };

  // Apply viz when present (even if error). Open VF editor on partial convert.
  const finish = (payload = {}, fallbackMessage) => {
    const errorMessage = payload?.error || fallbackMessage;
    const vfCode = applyConvertedViz(payload);

    if (vfCode) {
      if (errorMessage) notifyError(errorMessage);
      onComplete({
        success: true,
        openVfEditor: Boolean(errorMessage),
        vfCode,
      });
      return;
    }

    notifyError(errorMessage || payload?.message || IB_CHART_RENDER_ERROR);
    onComplete({ success: false });
  };

  return instantConvertChartAPI({
    dispatch,
    vfTemplate,
    selectedChart,
    chatId,
    chatSequenceId,
    successCB: (response) => finish(response),
    errorCB: (err) => {
      finish(err, IB_CHART_RENDER_ERROR);
    },
  });
};

export const parseInstantBIListChartsResponse = (response) => {
  const raw =
    response?.charts ||
    response?.chart_list ||
    response?.chartList ||
    response?.data ||
    response;
  if (!Array.isArray(raw)) return [];
  return raw
    .map((item) => {
      if (typeof item === "string") return item.trim();
      if (item && typeof item === "object") {
        return String(
          item.name || item.chart_name || item.chart || item.type || ""
        ).trim();
      }
      return "";
    })
    .filter(Boolean);
};

export const fetchInstantBIChartList = ({
  dispatch,
  successCB = () => { },
  errorCB = () => { },
}) =>
  requests.instantBI(dispatch).instantBIListChartsRequest({
    uri: uriConfig.instantListCharts,
    formData: {},
    callback: (response) => {
      const charts = parseInstantBIListChartsResponse(response);
      dispatch(setIBChartList(charts));
      successCB(charts);
    },
    errback: (err) => {
      dispatch(setIBChartList([]));
      errorCB(err);
    },
  });


export const agentGenerateAPI = ({ dir, file, dispatch, successCB, errorCB }) => {
  const formData = ({ dir: dir, file: file })
  return requests.instantBI(dispatch).postInstantBIRequest({
    uri: uriConfig.agentGenerate,
    formData,
    callback: successCB,
    errback: errorCB,
  });
};

export const agentSaveAPI = ({ dir, file, agentDir, modelName, description, uuid, content, dispatch, successCB, errorCB }) => {
  const parsedState =
    typeof content === "string" ? JSON.parse(content) : content;
  const formData = {
    metadata: { location: dir, metadataFileName: file },
    location: agentDir,
    state: parsedState,
    modelName: modelName,
    description: description ?? "",
  };
  if (uuid) {
    formData.uuid = uuid;
  }

  const encodedFormData = formData;

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
    model: {
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

export const collectConvertDashboardItems = (messageList = []) =>
  (messageList || [])
    .filter((message) => !message.isUser && message.fullChatResponse && !message.error && !message.isStreaming)
    .map((message) => ({
      id: message.chatSequenceId || message.id,
      sql: message.sql || message.fullChatResponse?.sql?.raw_sql || "",
      viz: message.fullChatResponse?.viz || {},
      report_model: message.fullChatResponse?.report_model,
    }));

export const convertInstantBIDashboard = ({
  dispatch,
  chatId,
  items,
  subject,
  input,
  onProgress,
  successCB = () => { },
  errorCB = () => { },
}) =>
  requests.instantBI(dispatch).instantBIConvertDashboardRequest({
    formData: {
      chatid: chatId,
      items,
      subject,
      input,
    },
    uri: uriConfig.instantConvertDashboard,
    callback: successCB,
    errback: errorCB,
    onProgress,
  });
