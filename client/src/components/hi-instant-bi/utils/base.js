import { isEmpty, isEqual, pickBy } from "lodash";
import { getInstantBIAgentSubject, getInstantBIMetadataForReport } from "./common-utils";
export const getSaveData = ({ saveFileInfo, searchValue, dispatch }) => {
  // isHrReport: true,
  // columns: [
  //   {
  //     column: "travel_details.booking_platform",
  //     label: "booking_platform",
  //     id: "8e3a297c-2b4b-41dc-b146-3a59dace675a",
  //     type: {
  //       backendDataType: "java.lang.String",
  //       dataType: "text",
  //     },
  //     autogen_alias: "booking_platform",
  //     isNormalTable: true,
  //     tableAlias: "travel_details",
  //     groupBy: ["db.generic.groupBy.group"],
  //     orderByColumn: false,
  //     showOrderByColumn: false,
  //     addedAs: "column",
  //     floatingType: "discrete",
  //     functionsDefinition: "",
  //     applyBeforeAggregate: false,
  //     hiddenIncludeInResultSet: false,
  //     metaDataAlias: "booking_platform",
  //   },
  //   {
  //     column: "travel_details.mode_of_payment",
  //     label: "mode_of_payment",
  //     id: "ea4b4bfb-1fa9-4199-8cff-f6ca87f1a3f1",
  //     type: {
  //       backendDataType: "java.lang.String",
  //       dataType: "text",
  //     },
  //     autogen_alias: "mode_of_payment",
  //     isNormalTable: true,
  //     tableAlias: "travel_details",
  //     groupBy: ["db.generic.groupBy.group"],
  //     orderByColumn: false,
  //     showOrderByColumn: false,
  //     addedAs: "column",
  //     floatingType: "discrete",
  //     functionsDefinition: "",
  //     applyBeforeAggregate: false,
  //     hiddenIncludeInResultSet: false,
  //     metaDataAlias: "mode_of_payment",
  //   },
  // ],
  let uuid;
  let derivedFormdata;
  let metadata;
  dispatch((dispatch, getState) => {
    uuid = getState().instantBI.activeInstantBIReport?.reportInfo?.uuid;
    derivedFormdata = getState().instantBI.derivedFormdata;
    metadata = getState().instantBI.metadata;
  });
  return {
    state: {
      searchValue,
      derivedFormdata,
      // fields: [
      //   {
      //     column: "travel_details.booking_platform",
      //     label: "booking_platform",
      //     id: "8e3a297c-2b4b-41dc-b146-3a59dace675a",
      //     type: {
      //       backendDataType: "java.lang.String",
      //       dataType: "text",
      //     },
      //     autogen_alias: "booking_platform",
      //     isNormalTable: true,
      //     tableAlias: "travel_details",
      //     groupBy: ["db.generic.groupBy.group"],
      //     orderByColumn: false,
      //     showOrderByColumn: false,
      //     addedAs: "column",
      //     floatingType: "discrete",
      //     functionsDefinition: "",
      //     applyBeforeAggregate: false,
      //     hiddenIncludeInResultSet: false,
      //     metaDataAlias: "booking_platform",
      //   },
      //   {
      //     column: "travel_details.mode_of_payment",
      //     label: "mode_of_payment",
      //     id: "ea4b4bfb-1fa9-4199-8cff-f6ca87f1a3f1",
      //     type: {
      //       backendDataType: "java.lang.String",
      //       dataType: "text",
      //     },
      //     autogen_alias: "mode_of_payment",
      //     isNormalTable: true,
      //     tableAlias: "travel_details",
      //     groupBy: ["db.generic.groupBy.group"],
      //     orderByColumn: false,
      //     showOrderByColumn: false,
      //     addedAs: "column",
      //     floatingType: "discrete",
      //     functionsDefinition: "",
      //     applyBeforeAggregate: false,
      //     hiddenIncludeInResultSet: false,
      //     metaDataAlias: "mode_of_payment",
      //   },
      // ],
      // filters: [],
      // marksList: [
      //   {
      //     value: "_all_",
      //     id: "7d5cc43f-62fa-4e11-adf5-f12dad7bde52",
      //     subVizType: "",
      //     color: {
      //       fields: [],
      //     },
      //     size: {
      //       fields: [],
      //     },
      //     label: {
      //       fields: [],
      //     },
      //     tooltip: {
      //       fields: [],
      //     },
      //     shape: {
      //       fields: [],
      //     },
      //     detail: {
      //       fields: [],
      //     },
      //   },
      // ],
      // activeMark: "7d5cc43f-62fa-4e11-adf5-f12dad7bde52",
      // activeTool: "2",
      // scripts: [
      //   {
      //     id: "pre-execution",
      //     value: "",
      //     title: "Pre Execution",
      //   },
      //   {
      //     id: "pre-fetch",
      //     value: "",
      //     title: "Pre Fetch",
      //   },
      //   {
      //     id: "post-fetch",
      //     value: "",
      //     title: "Post Fetch",
      //   },
      //   {
      //     id: "post-execution",
      //     value: "",
      //     title: "Post Execution",
      //   },
      // ],
      // selectedScript: "pre-execution",
      // styles: "",
      // options: {
      //   limitBy: 1000,
      //   sample: "sample",
      //   prependTableNameToAlias: false,
      // },
      // interactiveMode: false,
      // drillDown: false,
      // drillThrough: false,
      // drillDownList: [],
      // currentDrillDown: "",
      // drillThroughList: [],
      // toolbarConfig: {
      //   selectable: false,
      // },
      // selectedType: "Table",
      // customStyles: "",
      // customScripts: [],
      // analytics: [
      //   {
      //     value: false,
      //     key: "rowSubTotals",
      //     label: "Row Sub Totals",
      //   },
      //   {
      //     value: false,
      //     key: "columnSubTotals",
      //     label: "Column Sub Totals",
      //   },
      //   {
      //     value: false,
      //     key: "rowGrandTotals",
      //     label: "Row Grand Totals",
      //   },
      //   {
      //     value: false,
      //     key: "columnGrandTotals",
      //     label: "Column Grand Totals",
      //   },
      // ],
      // properties: {
      //   title: {
      //     show: false,
      //     value: "",
      //     padding: 0,
      //     fontSize: 32,
      //     fontColor: {
      //       a: 1,
      //       b: 0,
      //       g: 0,
      //       r: 0,
      //     },
      //     alignment: "center",
      //     position: "top",
      //   },
      //   subTitle: {
      //     show: false,
      //     value: "",
      //     padding: 0,
      //     fontSize: 24,
      //     fontColor: {
      //       a: 1,
      //       b: 0,
      //       g: 0,
      //       r: 0,
      //     },
      //     alignment: "center",
      //     position: "top",
      //   },
      //   format: {
      //     formatFields: [],
      //     formatDatatype: "",
      //     activeFieldId: "",
      //   },
      //   cache: {
      //     isCacheEnabled: true,
      //     interval: "00:00:00",
      //   },
      // },
      // showHiddenColumns: false,
      // showHiddenRows: false,
      // database: "HIUSER",
    },
    // metadata: {
    //   location: "sai_ganesh",
    //   metadataFileName: "Metadata_1.metadata",
    // },
    metadata,
    classifier: "db.generic",
    reportName: saveFileInfo.reportName,
    location: saveFileInfo.location,
    uuid,
  };
};

const getAllHreports = (dispatch) => {
  let reports = [];
  dispatch((_, getState) => {
    reports = getState().hreport.present.reports || [];
  })
  return reports
}

const getHreportDiffData = (activeReport = {}, dispatch) => {
  const returnObj = {};
  const hreports = getAllHreports(dispatch);
  if (!hreports?.length) return returnObj;

  const { hreportInteractions = {} } = activeReport || {}
  if (isEmpty(hreportInteractions)) return returnObj;

  for (let id in hreportInteractions) {
    returnObj[id] = {}
    const report = hreports.find(report => report.id === id);
    const hreportInteraction = hreportInteractions[id];
    if (report) {
      const allMark = report?.marksList?.find((item) => item?.value === "_all_") || {};
      returnObj[id] = {
        ...returnObj[id],
        selectedType: report.selectedType,
        subVizType: allMark?.subVizType,
        filters: hreportInteraction?.filters?.length ?
          hreportInteraction.filters.map((fltr) => {
            const hreportFilter = report?.filters?.find((filter) => filter.uid === fltr.uid) || null;
            if (hreportFilter) {
              delete hreportFilter.valuesList;
              hreportFilter.active = false;
              return hreportFilter
            }
            return null
          }).filter(Boolean)
          : []
      }
    }
  }

  return returnObj;
}


export const getInsantBISaveData = ({ activeReport = {}, saveFileInfo = {}, dispatch }) => {
  const {
    reportInfo = {},
    chats = [],
    previews,
    recommendations,
    recommendationsVisible,
    loadingRecommendation,
    inputValue,
    loadedChatResponses: _loadedChatResponses,
    loadedChatResponseSources: _loadedChatResponseSources,
    botStatus: _botStatus,
    metadataForHreport,
    ...rest
  } = activeReport || {}
  const activeChat = chats.find(chat => chat.chatID === activeReport.activeChatID)
  const { activeChatID, metadata: _reportMetadata, hreportInteractions, ...stateRest } = rest
  const messageList = activeChat?.messageList || []
  const botMessages = messageList.filter(msg => !msg.isUser && msg.userInput && msg.chatSequenceId)
  const inputs = botMessages.map(msg => ({
    chat_sequence_id: msg.chatSequenceId,
    input: msg.userInput
  }))

  const chat_responses = botMessages.map(msg => {
    const loaded = (activeReport.loadedChatResponses || {})[msg.chatSequenceId] || {};
    const {
      metadata: _metadata,
      ...chatResponseForSave
    } = msg.fullChatResponse || {};
    const {
      metadata: _loadedMetadata,
      data: _loadedData,
      ...loadedForSave
    } = loaded;

    // Prefer message preferences; fall back to loaded-open cache so open/edit
    // preference edits still persist when fullChatResponse was sparse.
    const messageViz = chatResponseForSave.viz || {};
    const loadedViz = loadedForSave.viz || {};
    const viz = {
      ...loadedViz,
      ...messageViz,
    };
    if (messageViz.chart_name || loadedViz.chart_name) {
      viz.chart_name = messageViz.chart_name || loadedViz.chart_name;
    }
    if (messageViz.similar_chart || loadedViz.similar_chart) {
      viz.similar_chart =
        messageViz.similar_chart || loadedViz.similar_chart;
    }
    // Do not persist legacy InstantBI chart settings.
    delete viz.settings;

    return {
      chat_sequence_id: msg.chatSequenceId,
      ...loadedForSave,
      ...chatResponseForSave,
      viz,
    };
  })

  const modelSubject = getInstantBIAgentSubject(activeReport) || {};
  // const metadataForReport = getInstantBIMetadataForReport(activeReport) || {};
  const subject = modelSubject.file && modelSubject.dir
    ? { model: modelSubject }
    : {}

  let data = {
    classifier: "db.generic",
    reportName: saveFileInfo.reportName,
    location: saveFileInfo.location,
    // metadata: metadataForReport,
    state: {
      inputs,
      chat_responses,
      subject,
      ...stateRest,
      activeChatId: activeChatID,
      hreportInteractions: getHreportDiffData(activeReport, dispatch)
    }
  }
  if (saveFileInfo.uuid) {
    data.uuid = saveFileInfo.uuid
  }
  return data;
}
