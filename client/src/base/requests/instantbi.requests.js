import { instantBIPostRequest, postRequest } from "../service";
import Base64 from "../utils/Base64";

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
  agentGenerate:"instantbi/instant/generateAgent",
  agentSave:"instantbi/instant/saveAiAgent",
  agentEdit:"instantbi/instant/getAiAgentForEdit",
  // instantBIChat: "ai/chat",
  instantBIChat: "ai/interactive-chat",
  instantLoadChat:"ai/load-chat",
  instantDataInsight:"ai/data-insight",
  instantConvertChart: "ai/convert-chart",
  instantListCharts: "ai/list-charts",
  instantBIDomain: "ai/recommendation/domain",
  instantBIRecommendation: "ai/recommendation/analyst",
  instantBIUtilityLlm: "ai/utility/llm",
  instantBIUtilitySettings: "ai/utility/settings",
  instantBIUtilityPrefix: "ai/utility",
  instantBISettingsModels: "ai/settings/models",
};

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
    return instantBIPostRequest(dispatch, uri, formData, callback, errback);
  }

  const instantBILoadChatRequest = ({
    formData,
    uri,
    callback = () => { },
    errback = () => { },
  }) => {
    let { formData: nestedFormData = null } = formData || {}
    if (nestedFormData) {
      nestedFormData = Base64.encode(JSON.stringify(nestedFormData))
      formData = { ...formData, formData: nestedFormData }
    }
    return instantBIPostRequest(dispatch, uri, formData, callback, errback);
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
    instantBIFetchDomain,
    instantBIFetchRecommendation,
    instantBIConvertChartRequest,
    instantBIListChartsRequest,
    instantBIUtilityRequest,
  };
}

export default instantBI;
