import { instantBIPostRequest, instantBIStreamPostRequest, postRequest } from "../service";
import Base64 from "../utils/Base64";
import InstantBIStreamHandler from "../../components/hi-instant-bi/utils/instant-bi-stream-handler";

export const uriConfig = {
  //     service:getDerivedFormdata
  // serviceType:aiagent
  // type:adhoc
  contentStaticGetContents: "content/static/getContents",
  adhocMetadataGetFunctions: "instantbi/aiagent/getFunctions",
  adhocMetadataGet: "instantbi/instant/getAgent",
  adhocReportGetDerivedFormdata: "instantbi/report/getDerivedFormdata",
  adhocInstantSaveReport: "instantbi/instant/saveReport",
  adhocInstantGetReportForEdit: "instantbi/instant/getReportForEdit",
  adhocInstantGetReport: "instantbi/instant/getReport",
  agentGenerate: "instantbi/instant/generateAgent",
  agentSave: "instantbi/instant/saveAiAgent",
  agentEdit: "instantbi/instant/getAiAgentForEdit",
  // instantBIChat: "ai/chat",
  instantBIChat: "ai/interactive-chat",
  instantLoadChat: "ai/load-chat",
  instantDataInsight: "ai/data-insight",
  instantConvertChart: "ai/convert-chart",
  instantConvertDashboard: "ai/convert-dashboard",
  instantListCharts: "ai/list-charts",
  instantBIDomain: "ai/recommendation/domain",
  instantBIRecommendation: "ai/recommendation/analyst",
  instantBIUtilityLlm: "ai/utility/llm",
  instantBIUtilitySettings: "ai/utility/settings",
  instantBIUtilityPrefix: "ai/utility",
  instantBISettingsModels: "ai/settings/models",
};

function wantsStream(dispatch) {
  let streamResponse = false;
  dispatch((_, getState) => {
    streamResponse = Boolean(getState().app.applicationSettingsData?.streamResponse);
  });
  return streamResponse;
}

function postInstantBIMaybeStream(dispatch, uri, formData, callback, errback, onProgress) {
  const streamable = [
    uriConfig.instantBIChat,
    uriConfig.instantDataInsight,
    uriConfig.instantConvertDashboard,
  ].includes(uri);
  if (!wantsStream(dispatch) || !streamable) {
    return instantBIPostRequest(dispatch, uri, formData, callback, errback);
  }
  const handler = new InstantBIStreamHandler({
    onProgress,
    onComplete: callback,
    onError: (payload) => {
      if (typeof errback === "function") {
        errback(payload);
      }
    },
  });
  return instantBIStreamPostRequest(
    dispatch,
    uri,
    formData,
    (chunk) => handler.handleChunk(chunk),
    errback,
  );
}

function instantBI(dispatch) {
  const postInstantBIRequest = ({
    formData,
    uri,
    callback = () => { },
    errback = () => { },
  }) => {
    return postRequest(dispatch, uri, formData, callback, errback);
  };
  const getMetadata = ({
    formData,
    uri,
    callback = () => { },
    errback = () => { },
  }) => {
    postRequest(dispatch, uri, formData, callback, errback);
  };
  const getFunctions = ({
    formData,
    uri,
    callback = () => { },
    errback = () => { },
  }) => {
    return postRequest(dispatch, uri, formData, callback, errback);
  };
  const getDateFunctions = ({
    formData,
    uri,
    callback = () => { },
    errback = () => { },
  }) => {
    return postRequest(dispatch, uri, formData, callback, errback);
  };
  const saveInstantBIReport = ({
    formData,
    uri,
    callback = () => { },
    errback = () => { },
  }) => {
    return postRequest(dispatch, uri, formData, callback, errback);
  };
  const getInstantBIReportForEdit = ({
    formData,
    uri,
    callback = () => { },
    errback = () => { },
  }) => {
    return postRequest(dispatch, uri, formData, callback, errback);
  };
  //   const postDashboardRequestForUrl = ({
  //     url,
  //     data,
  //     callback = () => {},
  //     errback = () => {},
  //   }) => {
  //     customPostRequest({ dispatch, url, data, callback, errback });
  //   };

  const instantBIChatRequest = ({
    formData,
    uri,
    callback = () => { },
    errback = () => { },
    onProgress,
  }) => {
    let { subject = null, formData: nestedFormData = null } = formData || {}
    if (subject) {
      subject = Base64.encode(JSON.stringify(subject))
      formData = { ...formData, subject }
    }
    if (nestedFormData) {
      nestedFormData = Base64.encode(JSON.stringify(nestedFormData))
      formData = { ...formData, formData: nestedFormData }
    }
    return postInstantBIMaybeStream(dispatch, uri, formData, callback, errback, onProgress);
  }

  const instantBIConvertDashboardRequest = ({
    formData,
    uri,
    callback = () => { },
    errback = () => { },
    onProgress,
  }) => {
    let { subject = null, items = null } = formData || {};
    if (subject && typeof subject !== "string") {
      subject = Base64.encode(JSON.stringify(subject));
      formData = { ...formData, subject };
    }
    if (items && typeof items !== "string") {
      items = Base64.encode(JSON.stringify(items));
      formData = { ...formData, items };
    }
    return postInstantBIMaybeStream(
      dispatch,
      uri || uriConfig.instantConvertDashboard,
      formData,
      callback,
      errback,
      onProgress,
    );
  }

  const instantBILoadChatRequest = ({
    formData,
    uri,
    callback = () => { },
    errback = () => { },
    onProgress,
  }) => {
    let { formData: nestedFormData = null } = formData || {}
    if (nestedFormData) {
      nestedFormData = Base64.encode(JSON.stringify(nestedFormData))
      formData = { ...formData, formData: nestedFormData }
    }
    return postInstantBIMaybeStream(dispatch, uri, formData, callback, errback, onProgress);
  }

  const instantBIFetchDomain = ({
    formData,
    uri,
    callback = () => { },
    errback = () => { },
  }) => {
    let { model } = formData || {}
    model = Base64.encode(JSON.stringify(model))
    formData = { ...formData, model }
    return instantBIPostRequest(dispatch, uri, formData, callback, errback);
  }

  const instantBIFetchRecommendation = ({
    formData,
    uri,
    callback = () => { },
    errback = () => { },
  }) => {
    let { model } = formData || {}
    model = Base64.encode(JSON.stringify(model))
    formData = { ...formData, model }
    return instantBIPostRequest(dispatch, uri, formData, callback, errback);
  }

  const instantBIConvertChartRequest = ({
    formData,
    uri,
    callback = () => { },
    errback = () => { },
  }) => {
    return instantBIPostRequest(dispatch, uri, formData, callback, errback);
  }

  const instantBIListChartsRequest = ({
    formData = {},
    uri,
    callback = () => { },
    errback = () => { },
  }) => {
    return instantBIPostRequest(dispatch, uri, formData, callback, errback);
  }

  const instantBIUtilityRequest = ({
    formData = {},
    uri,
    callback = () => { },
    errback = () => { },
  }) => {
    return instantBIPostRequest(dispatch, uri, formData, callback, errback);
  }

  return {
    postInstantBIRequest,
    getMetadata,
    getFunctions,
    getDateFunctions,
    saveInstantBIReport,
    getInstantBIReportForEdit,
    // postDashboardRequestForUrl,
    instantBIChatRequest,
    instantBILoadChatRequest,
    instantBIConvertDashboardRequest,
    instantBIFetchDomain,
    instantBIFetchRecommendation,
    instantBIConvertChartRequest,
    instantBIListChartsRequest,
    instantBIUtilityRequest,
  };
}

export default instantBI;
