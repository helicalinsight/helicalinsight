import produce from "immer";
import { createTabTitle } from "../../utils/utilities";
import actionTypes from "../actions/actionTypes";
import initialStates, { getIBInitialReportState } from "./initialStates";
import { v4 as uuidv4 } from "uuid";
import { isEmpty } from "lodash";


const instantBIReducer = (
  state = initialStates.instantBIInitialState,
  action
) => {
  switch (action.type) {
    case actionTypes.UPDATE_SEARCH_VALUE:
      return { ...state, searchValue: action.payload };
    case actionTypes.LOAD_INSTANTBI_METADATA: {
      const { reportId, ...rest } = action.payload
      return produce(state, (draft) => {
        draft.reports = draft.reports.map((report) => {
          if (report.id === reportId) {
            let metadata = rest;
            if (!isEmpty(metadata) && metadata.tables) {
              Object.keys(metadata.tables).forEach((tableName) => {
                metadata.tables[tableName].key = uuidv4();
              });
            }
            report.metadata = metadata;
          }
          return report;
        })
      })
    }
    case actionTypes.RESET_IB_CHAT_ID: {
      const { reportId, newChatId } = action.payload
      return produce(state, (draft) => {
        draft.reports = draft.reports.map((report) => {
          if (report.id === reportId) {
            report.activeChatID = newChatId;
            report.chats = [
              {
                chatID: newChatId,
                messageList: []
              }
            ];
            report.previews = [];
            report.activePreviewID = null;
            report.loadedChatResponses = {};
            report.loadedChatResponseSources = {};
            report.recommendations = [];
            report.recommendationsVisible = false;
            report.inputValue = '';
          }
          return report;
        })
      })
    }
    case actionTypes.UPDATE_INSTANTBI_REPORT_FILE:
      let {
        location,
        reportName,
        mode,
        // derivedFormdata,
        // searchValue,
        loading,
        uuid,
        metadata,
      } = action.payload;
      return produce(state, (draft) => {
        draft.activeInstantBIReport = {
          reportInfo: { location, reportName, uuid },
        };
        draft.loading = loading;
        draft.mode = mode;
        // draft.searchValue = searchValue;
        // draft.derivedFormdata = derivedFormdata;
        draft.metadata = metadata;
      });
    case actionTypes.UPDATE_INSTANTBI_MODE:
      return {
        ...initialStates.instantBIInitialState,
        mode: action.payload.mode,
      };
    case actionTypes.SAVED_INSTANT_BI_REPORT_FILE:
      let { isSaving } = action.payload;

      return produce(state, (draft) => {
        if (
          action.payload.location &&
          action.payload.reportName &&
          action.payload.uuid
        ) {
          let reportInfo = {
            location: action.payload.location,
            reportName: action.payload.reportName,
            uuid: action.payload.uuid,
          }
          draft.reports = draft.reports.map((report) => {
            if (report.active) {
              report.reportInfo = reportInfo;
            }
            return report;
          })
        }
        draft.isSaving = isSaving;
      });
    case actionTypes.INSTANT_BI_PAGE_LOADING:
      return { ...state, loading: action.payload.loading };
    case actionTypes.INSTANT_BI_LOAD_DERIVED_FORMDATA:
      const { searchButtonClicked } = action.payload;
      return {
        ...state,
        derivedFormdata: action.payload.derivedFormdata,
        searchButtonClicked,
      };
    case actionTypes.UPDATE_INSTANT_BI_LAYOUT: {
      let { key, value } = action.payload;
      return produce(state, (draft) => {
        draft.layout = { ...draft.layout, [key]: value };
      });
    }

    case actionTypes.UPDATE_IB_CHAT_MESSAGE_LIST: {
      const { activeChatID = "", newMessage = {}, reportId } = action.payload || {};
      return produce(state, (draft) => {
        draft.reports = draft.reports.map((report) => {
          if (report.id === reportId) {
            report.chats = report.chats.map((chat) => {
              if (chat.chatID === activeChatID) {
                chat.messageList = [newMessage, ...chat.messageList]
              }
              return chat
            })
          }
          return report
        })
      })
    }

    case actionTypes.UPDATE_IB_BOT_STATUS: {
      const { reportId, status } = action.payload
      return produce(state, (draft) => {
        draft.reports = draft.reports.map((report) => {
          if (report.id === reportId) {
            report.botStatus = status
          }
          return report
        })
      })
    }

    case actionTypes.ADD_NEW_IB_REPORT: {
      return produce(state, (draft) => {
        let report = getIBInitialReportState({ active: true });
        draft.reports = draft.reports.map((item) => {
          item.active = false;
          return item;
        });
        let reportName = createTabTitle(draft.reports.map((report) => report.reportInfo.reportName));
        report.reportInfo.reportName = reportName;
        draft.reports.push(report);
        draft.activeReportId = report.id;
      });
    }

    case actionTypes.LOAD_INITIAL_IB_REPORT: {
      let { reportId } = action.payload;
      return produce(state, (draft) => {
        let report = getIBInitialReportState({ active: true });
        report.id = reportId;
        draft.reports = [report];
        draft.activeReportId = report.id;
      });
    }

    case actionTypes.REMOVE_IB_REPORT: {
      let { id } = action.payload;
      return produce(state, (draft) => {
        if (draft.activeReportId === id) {
          let index = draft.reports.findIndex((item) => item.id === id);
          let nextActiveIndex = index - 1;
          if (draft.reports[index - 1]) {
            nextActiveIndex = index - 1;
          } else if (draft.reports[index + 1]) {
            nextActiveIndex = index + 1;
          }
          if (nextActiveIndex > -1) {
            draft.reports[nextActiveIndex].active = true;
            draft.activeReportId = draft.reports[nextActiveIndex].id;
          } else {
            let report = getIBInitialReportState({ active: true });
            report.reportInfo.reportName = `Untitled 1`;
            draft.reports.push(report);
            draft.activeReportId = report.id;
          }
        }
        draft.reports = draft.reports.filter((item) => {
          return item.id !== id;
        });
      });
    }

    case actionTypes.CHANGE_IB_REPORT: {
      let { id } = action.payload;
      return produce(state, (draft) => {
        draft.reports = draft.reports.map((item) => {
          item.active = false;
          if (item.id === id) {
            item.active = true;
          }
          return item;
        });
        draft.activeReportId = id;
      });
    }

    case actionTypes.UPDATE_IB_PREVIEW_DATA: {
      let { reportId, previewData } = action.payload;
      return produce(state, (draft) => {
        draft.reports = draft.reports.map((item) => {
          if (item.id === reportId) {
            item.previews = [...item.previews, previewData];
          }
          return item;
        })
      });
    }

    case actionTypes.UPDATE_IB_ACTIVE_PREVIEW: {
      let { previewID, reportId } = action.payload;
      return produce(state, (draft) => {
        draft.reports = draft.reports.map((report) => {
          if (report.id === reportId) {
            report.activePreviewID = previewID;
          }
          return report;
        });
      });
    }

    case actionTypes.REMOVE_IB_PREVIEW: {
      let { previewID, reportId } = action.payload;
      return produce(state, (draft) => {
        draft.reports = draft.reports.map((report) => {
          if (report.id === reportId) {
            report.previews = report.previews.length > 1 ?
              report.previews.filter((preview) => {
                return preview.id !== previewID;
              }) : report.previews
            report.activePreviewID = report.previews[0].id;
          }
          return report
        })
      })
    }

    case actionTypes.LOAD_IB_OPEN_CHAT_RESPONSE: {
      const { reportId, chatSequenceId, chatResponse = {}, source = "unknown", time } = action.payload || {};
      return produce(state, (draft) => {
        draft.reports = draft.reports.map((report) => {
          if (report.id === reportId) {
            const { viz = {}, sql: sqlData = {}, summary: summaryData = {} } = chatResponse || {};
            const vf = viz?.vf_template ? atob(viz.vf_template) : '';
            const vf_title = viz?.vf_title || '';
            const sql = sqlData?.raw_sql || '';
            const insight = summaryData?.insight || '';
            report.loadedChatResponses = {
              ...(report.loadedChatResponses || {}),
              [chatSequenceId]: chatResponse
            };
            report.loadedChatResponseSources = {
              ...(report.loadedChatResponseSources || {}),
              [chatSequenceId]: source
            };
            report.chats = report.chats.map((chat) => {
              if (chat.chatID === report.activeChatID) {
                chat.messageList = chat.messageList.map((message) => {
                  if (message?.isUser) return message;
                  if (message?.chatSequenceId !== chatSequenceId) return message;
                  if (source === "data-insight") {
                    const dataInsight = chatResponse?.data_insight || {};
                    return {
                      ...message,
                      ...(time ? { time } : {}),
                      fullChatResponse: {
                        ...(message.fullChatResponse || {}),
                        data_insight: dataInsight,
                      },
                      dataInsight: dataInsight.insight || "",
                      dataInsightTokenUsage: dataInsight.token_usage || {},
                      needsLoadChat: false,
                    };
                  }
                  return {
                    ...message,
                    ...(time ? { time } : {}),
                    text: insight || message.text,
                    data: chatResponse?.data || [],
                    metadata: chatResponse?.metadata || [],
                    vf: vf || message.vf || '',
                    vf_title: vf_title || message.vf_title || '',
                    sql: sql || message.sql || '',
                    fullChatResponse: chatResponse,
                    needsLoadChat: false,
                    error: false,
                    dataInsight: "",
                    dataInsightTokenUsage: {},
                  };
                });
              }
              return chat;
            });
            if (source === "refresh") {
              const message = report.chats
                .find((chat) => chat.chatID === report.activeChatID)
                ?.messageList?.find(
                  (item) => !item.isUser && item.chatSequenceId === chatSequenceId
                );
              if (message?.id) {
                report.previews = report.previews.map((preview) => {
                  if (preview.id !== message.id) return preview;
                  return {
                    ...preview,
                    dataId: uuidv4(),
                    data: chatResponse?.data || [],
                    metadata: chatResponse?.metadata || [],
                    vf: vf || preview.vf || "",
                    vf_title: vf_title || preview.vf_title || "",
                    sql: sql || preview.sql || "",
                    sqlDetails: chatResponse?.sql || preview.sqlDetails || {},
                    vizDetails: chatResponse?.viz || preview.vizDetails || {},
                    tokenUsage: chatResponse?.token_usage || preview.tokenUsage || {},
                  };
                });
              }
            }
          }
          return report;
        })
      })
    }

    case actionTypes.LOADING_IB_RECOMENDATIONS: {
      const { reportId, loading } = action.payload
      return produce(state, (draft) => {
        draft.reports = draft.reports.map((report) => {
          if (report.id === reportId) {
            report.loadingRecommendation = loading
          }
          return report;
        })
      });
    }

    case actionTypes.ADD_IB_RECOMMENDATIONS: {
      let { recommendations = [], reportId } = action.payload;
      return produce(state, (draft) => {
        draft.reports = draft.reports.map((report) => {
          if (report.id === reportId) {
            report.recommendations = recommendations;
          }
          return report;
        })
      });
    }

    case actionTypes.UPDATE_RECOMMENDATIONS_VISIBILITY: {
      let { reportId, visible } = action.payload;
      return produce(state, (draft) => {
        draft.reports = draft.reports.map((report) => {
          if (report.id === reportId) {
            report.recommendationsVisible = visible;
          }
          return report;
        })
      });
    }

    case actionTypes.LOAD_IB_REPORT_DATA: {
      let {
        data,
        location,
        uuid,
        loading,
        mode,
        reportId
      } = action.payload;
      const { metadata = {}, reportName, state: stateData = {} } = data || {}
      const { inputs = [], chat_responses = [], ...restState } = stateData || {}
      const chatId = restState.activeChatID || uuidv4();
      const sortedInputs = [...inputs].sort((a, b) => a.chat_sequence_id - b.chat_sequence_id);
      const sortedResponses = [...chat_responses].sort((a, b) => a.chat_sequence_id - b.chat_sequence_id);
      const uniqueSequenceIds = [...new Set([
        ...sortedInputs.map(input => input.chat_sequence_id),
        ...sortedResponses.map(res => res.chat_sequence_id)
      ])].sort((a, b) => a - b);
      const messageList = [];
      uniqueSequenceIds.forEach(sequenceId => {
        const input = sortedInputs.find(indec => indec.chat_sequence_id === sequenceId);
        const response = sortedResponses.find(response => response.chat_sequence_id === sequenceId);
        if (input) {
          messageList.push({
            id: uuidv4(),
            isUser: true,
            user: true,
            text: input.input,
            // time: getTimeStamp(),
            isTyping: false,
            createdDate: new Date().toISOString()
          });
        }
        if (response) {
          const messageId = uuidv4();
          messageList.push({
            id: messageId,
            isUser: false,
            user: false,
            text: '',
            // time: getTimeStamp(),
            isTyping: false,
            createdDate: new Date().toISOString(),
            data: [],
            metadata: [],
            vf: '',
            vf_title: '',
            sql: '',
            userInput: input?.input,
            chatSequenceId: sequenceId,
            needsLoadChat: true,
            persistedInFile: true,
          });
        }
      });
      
      let reportInfo = {
        location,
        uuid,
        reportName
      }
      const finalStateData = {
        ...restState,
        reportInfo,
        mode,
        metadata: { formData: metadata },
        id: reportId,
        loadedChatResponses: {},
        loadedChatResponseSources: {},
        chats: [
          {
            chatID: chatId,
            messageList: messageList.reverse()
          }
        ],
        activeChatID: chatId,
        previews: [],
        activePreviewID: null
      }
      return produce(state, (draft) => {
        draft.loading = loading;
        draft.mode = mode;
        draft.activeReportId = reportId;
        draft.reports = [finalStateData]
      })
    }

    case actionTypes.CHANGE_INPUT_VALUE: {
      let { value } = action.payload;
      return produce(state, (draft) => {
        draft.reports = draft.reports.map((report) => {
          if (report.active) {
            report.inputValue = value;
          }
          return report;
        })
      });
    }

    default:
      return { ...state };
  }
};

export default instantBIReducer;
