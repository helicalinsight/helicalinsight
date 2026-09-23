import "regenerator-runtime/runtime";
import { getInsantBISaveData } from "../../components/hi-instant-bi/utils/base";
import { shouldUseLoadChatPayloadForInsight, buildInstantBIInteractiveChatFormData, parseInstantBIChatResponse } from "../../components/hi-instant-bi/utils/instant-bi-requests";
import instantBIReducer from "../../redux/reducers/instant-bi.reducer";
import actionTypes from "../../redux/actions/actionTypes";
import initialStates from "../../redux/reducers/initialStates";

const buildActiveReportForSave = () => ({
  activeChatID: "chat-1",
  id: "report-1",
  botStatus: true,
  loadedChatResponses: {
    1: { summary: { insight: "loaded" } },
  },
  loadedChatResponseSources: { 1: "scroll-load" },
  metadata: {
    formData: {
      title: "Metadata title",
      tables: { employees: { columns: [] } },
    },
  },
  subject: {
    model: {
      dir: "agents",
      file: "sales.metadata",
    },
  },
  chats: [
    {
      chatID: "chat-1",
      messageList: [
        {
          isUser: true,
          text: "Show sales",
        },
        {
          isUser: false,
          userInput: "Show sales",
          chatSequenceId: 1,
          fullChatResponse: {
            summary: { insight: "Sales insight" },
            metadata: [{ name: "col1" }],
            viz: { vf_title: "Sales chart" },
            sql: { raw_sql: "SELECT 1" },
          },
        },
      ],
    },
  ],
});

describe("getInsantBISaveData", () => {
  // NOTE: getInsantBISaveData requires a dispatch fn for getAllHreports;
  // provide a minimal thunk-capable mock so tests don't depend on source guards.
  const mockDispatch = (thunk) => {
    if (typeof thunk === "function") {
      return thunk(jest.fn(), () => ({ hreport: { present: { reports: [] } } }));
    }
    return undefined;
  };
  it("it should include summary and exclude metadata from chat responses", () => {
    const saveData = getInsantBISaveData({
      activeReport: buildActiveReportForSave(),
      saveFileInfo: {
        reportName: "MyReport",
        location: "reports",
      },
      dispatch: mockDispatch,
    });
    const savedResponse = saveData.state.chat_responses[0];
    expect(savedResponse.chat_sequence_id).toBe(1);
    expect(savedResponse.summary).toEqual({ insight: "Sales insight" });
    expect(savedResponse).not.toHaveProperty("metadata");
    expect(savedResponse.viz).toEqual({ vf_title: "Sales chart" });
    expect(savedResponse.sql).toEqual({ raw_sql: "SELECT 1" });
  });

  it("it should persist preferred chart and similar_chart on save", () => {
    const activeReport = buildActiveReportForSave();
    activeReport.chats[0].messageList[1].fullChatResponse.viz = {
      chart_name: "bar",
      vf_title: "Sales chart",
      similar_chart: [{ "vf.column": "column" }],
      settings: {
        color: ["#5B8FF9"],
        backgroundColor: "#f5f5f5",
      },
    };
    const saveData = getInsantBISaveData({
      activeReport,
      saveFileInfo: {
        reportName: "MyReport",
        location: "reports",
      },
      dispatch: mockDispatch,
    });
    expect(saveData.state.chat_responses[0].viz).toEqual({
      chart_name: "bar",
      vf_title: "Sales chart",
      similar_chart: [{ "vf.column": "column" }],
    });
  });

  it("it should exclude runtime metadata & loaded chat cache from state", () => {
    const saveData = getInsantBISaveData({
      activeReport: buildActiveReportForSave(),
      saveFileInfo: {
        reportName: "MyReport",
        location: "reports",
      },
      dispatch: mockDispatch,
    });
    expect(saveData.state).not.toHaveProperty("metadata");
    expect(saveData.state).not.toHaveProperty("loadedChatResponses");
    expect(saveData.state).not.toHaveProperty("loadedChatResponseSources");
    expect(saveData.state).not.toHaveProperty("botStatus");
    expect(saveData.state.subject).toEqual({
      model: { dir: "agents", file: "sales.metadata" },
    });
  });
});

describe("instantBIReducer LOAD_IB_REPORT_DATA", () => {
  it("it should build bot messages with inline response data", () => {
    const state = instantBIReducer(initialStates.instantBIInitialState, {
      type: actionTypes.LOAD_IB_REPORT_DATA,
      payload: {
        reportId: "report-1",
        location: "reports",
        uuid: "report.instant",
        loading: false,
        mode: "open",
        data: {
          reportName: "MyReport",
          metadata: {
            location: "agents",
            metadataFileName: "sales.metadata",
          },
          state: {
            inputs: [
              { chat_sequence_id: 1, input: "Question 1" },
              { chat_sequence_id: 2, input: "Question 2" },
            ],
            chat_responses: [
              {
                chat_sequence_id: 1,
                viz: { vf_title: "Chart 1" },
                sql: { raw_sql: "SELECT 1" },
                summary: { insight: "Insight 1" },
              },
              {
                chat_sequence_id: 2,
                viz: { vf_title: "Chart 2" },
                sql: { raw_sql: "SELECT 2" },
                summary: { insight: "Insight 2" },
              },
            ],
          },
        },
      },
    });
    const report = state.reports[0];
    const botMessages = report.chats[0].messageList.filter((message) => !message.isUser);
    expect(botMessages).toHaveLength(2);
    botMessages.forEach((message) => {
      const seq = message.chatSequenceId;
      expect(message.needsLoadChat).toBe(false);
      expect(message.persistedInFile).toBe(false);
      expect(message.data).toEqual([]);
      expect(message.metadata).toEqual([]);
      expect(message.vf).toBe("");
      expect(message.text).toBe(`Insight ${seq}`);
      expect(message.sql).toEqual({ raw_sql: `SELECT ${seq}` });
      expect(message.viz).toEqual({ vf_title: `Chart ${seq}` });
      expect(message.fullChatResponse).toEqual({
        viz: { vf_title: `Chart ${seq}` },
        sql: { raw_sql: `SELECT ${seq}` },
        summary: { insight: `Insight ${seq}` },
      });
    });
    expect(report.previews).toEqual([]);
    expect(report.loadedChatResponses).toEqual({});
  });
});

describe("instantBIReducer UPDATE_IB_VIZ_PREFERENCE", () => {
  it("it should update chart_name and strip legacy settings on preference update", () => {
    const baseReport = {
      id: "report-1",
      active: true,
      activeChatID: "chat-1",
      loadedChatResponses: {
        1: {
          viz: {
            chart_name: "bar",
            settings: { backgroundColor: "#ffffff" },
          },
        },
      },
      loadedChatResponseSources: { 1: "scroll-load" },
      chats: [
        {
          chatID: "chat-1",
          messageList: [
            {
              id: "bot-1",
              isUser: false,
              chatSequenceId: 1,
              fullChatResponse: {
                viz: {
                  chart_name: "bar",
                  settings: { backgroundColor: "#ffffff" },
                },
              },
            },
          ],
        },
      ],
      previews: [],
    };
    const state = instantBIReducer(
      {
        ...initialStates.instantBIInitialState,
        activeReportId: "report-1",
        reports: [baseReport],
      },
      {
        type: actionTypes.UPDATE_IB_VIZ_PREFERENCE,
        payload: {
          reportId: "report-1",
          chatSequenceId: 1,
          chart_name: "column",
        },
      }
    );
    const report = state.reports[0];
    expect(report.chats[0].messageList[0].fullChatResponse.viz.chart_name).toBe(
      "column"
    );
    expect(report.chats[0].messageList[0].fullChatResponse.viz.settings).toBeUndefined();
    expect(report.loadedChatResponses[1].viz.chart_name).toBe("column");
    expect(report.loadedChatResponses[1].viz.settings).toBeUndefined();
  });
});

describe("instantBIReducer LOAD_IB_OPEN_CHAT_RESPONSE", () => {
  it("it should cache loaded chat response by sequence id", () => {
    const baseReport = {
      id: "report-1",
      active: true,
      activeChatID: "chat-1",
      loadedChatResponses: {},
      loadedChatResponseSources: {},
      chats: [
        {
          chatID: "chat-1",
          messageList: [
            {
              id: "bot-1",
              isUser: false,
              chatSequenceId: 2,
              needsLoadChat: true,
              text: "",
            },
          ],
        },
      ],
    };
    const state = instantBIReducer(
      {
        ...initialStates.instantBIInitialState,
        activeReportId: "report-1",
        reports: [baseReport],
      },
      {
        type: actionTypes.LOAD_IB_OPEN_CHAT_RESPONSE,
        payload: {
          reportId: "report-1",
          chatSequenceId: 2,
          source: "scroll-load",
          chatResponse: {
            summary: { insight: "Loaded insight" },
            data: [{ region: "East" }],
            metadata: [{ name: "region" }],
            viz: { vf_title: "Regional chart" },
            sql: { raw_sql: "SELECT region" },
          },
        },
      }
    );
    const report = state.reports[0];
    const botMessage = report.chats[0].messageList[0];
    expect(report.loadedChatResponses[2].summary.insight).toBe("Loaded insight");
    expect(report.loadedChatResponseSources[2]).toBe("scroll-load");
    expect(botMessage.text).toBe("Loaded insight");
    expect(botMessage.needsLoadChat).toBe(false);
    expect(botMessage.data).toEqual([{ region: "East" }]);
  });

  it("should keep message data when storing data insight", () => {
    const baseReport = {
      id: "report-1",
      active: true,
      activeChatID: "chat-1",
      loadedChatResponses: {
        2: {
          summary: { insight: "Loaded insight" },
          data: [{ region: "East" }],
        },
      },
      loadedChatResponseSources: { 2: "scroll-load" },
      chats: [
        {
          chatID: "chat-1",
          messageList: [
            {
              id: "bot-1",
              isUser: false,
              chatSequenceId: 2,
              needsLoadChat: false,
              text: "Loaded insight",
              data: [{ region: "East" }],
              metadata: [{ name: "region" }],
            },
          ],
        },
      ],
    };
    const state = instantBIReducer(
      {
        ...initialStates.instantBIInitialState,
        activeReportId: "report-1",
        reports: [baseReport],
      },
      {
        type: actionTypes.LOAD_IB_OPEN_CHAT_RESPONSE,
        payload: {
          reportId: "report-1",
          chatSequenceId: 2,
          source: "data-insight",
          chatResponse: {
            data_insight: {
              insight: "East leads revenue.",
              token_usage: { total_tokens: 42 },
            },
          },
        },
      },
    );
    const report = state.reports[0];
    const botMessage = report.chats[0].messageList[0];
    expect(botMessage.data).toEqual([{ region: "East" }]);
    expect(botMessage.metadata).toEqual([{ name: "region" }]);
    expect(botMessage.dataInsight).toBe("East leads revenue.");
    expect(botMessage.fullChatResponse.data_insight.insight).toBe(
      "East leads revenue.",
    );
    expect(report.loadedChatResponseSources[2]).toBe("data-insight");
  });

  it("should clear data insight & update preview on refresh", () => {
    const baseReport = {
      id: "report-1",
      active: true,
      activeChatID: "chat-1",
      loadedChatResponses: {
        2: {
          summary: { insight: "Old insight" },
          data: [{ region: "East" }],
          data_insight: { insight: "Old chart insight" },
        },
      },
      loadedChatResponseSources: { 2: "scroll-load" },
      previews: [
        {
          id: "bot-1",
          dataId: "old-data-id",
          vf: "old-vf",
          data: [{ region: "East" }],
          sql: "SELECT old",
        },
      ],
      chats: [
        {
          chatID: "chat-1",
          messageList: [
            {
              id: "bot-1",
              isUser: false,
              chatSequenceId: 2,
              needsLoadChat: false,
              text: "Old insight",
              data: [{ region: "East" }],
              dataInsight: "Old chart insight",
              dataInsightTokenUsage: { total_tokens: 5 },
            },
          ],
        },
      ],
    };
    const state = instantBIReducer(
      {
        ...initialStates.instantBIInitialState,
        activeReportId: "report-1",
        reports: [baseReport],
      },
      {
        type: actionTypes.LOAD_IB_OPEN_CHAT_RESPONSE,
        payload: {
          reportId: "report-1",
          chatSequenceId: 2,
          source: "refresh",
          time: "1:00 PM",
          chatResponse: {
            summary: { insight: "New insight" },
            data: [{ region: "West" }],
            metadata: [{ name: "region" }],
            viz: { vf_title: "Updated chart", vf_template: btoa("new vf") },
            sql: { raw_sql: "SELECT west" },
            token_usage: { total_tokens: 12 },
          },
        },
      },
    );
    const report = state.reports[0];
    const botMessage = report.chats[0].messageList[0];
    expect(botMessage.text).toBe("New insight");
    expect(botMessage.time).toBe("1:00 PM");
    expect(botMessage.data).toEqual([{ region: "West" }]);
    expect(botMessage.dataInsight).toBe("");
    expect(botMessage.dataInsightTokenUsage).toEqual({});
    expect(report.loadedChatResponseSources[2]).toBe("refresh");
    expect(report.previews[0].data).toEqual([{ region: "West" }]);
    expect(report.previews[0].sql).toBe("SELECT west");
    expect(report.previews[0].dataId).not.toBe("old-data-id");
  });
});

describe("parseInstantBIChatResponse", () => {
  it("it should parse chat responsee fields for refresh & send flow", () => {
    const parsed = parseInstantBIChatResponse({
      chat_response: {
        summary: { insight: "Insight text" },
        data: [{ value: 1 }],
        metadata: [{ name: "value" }],
        viz: {
          vf_title: "Chart",
          vf_template: btoa("function DrawChart() {}"),
        },
        sql: { raw_sql: "SELECT 1" },
        token_usage: { total_tokens: 4 },
      },
    });
    expect(parsed.mode).toBe("fast");
    expect(parsed.botMessage).toBe("Insight text");
    expect(parsed.data).toEqual([{ value: 1 }]);
    expect(parsed.metadata).toEqual([{ name: "value" }]);
    expect(parsed.sql).toBe("SELECT 1");
    expect(parsed.vf_title).toBe("Chart");
    expect(parsed.createPreview).toBe(true);
    expect(parsed.fullChatResponse).not.toHaveProperty("data");
    expect(parsed.fullChatResponse).not.toHaveProperty("metadata");
    expect(parsed.fullChatResponse.token_usage).toEqual({ total_tokens: 4 });
  });

  it("should parse think mode chat_responses into a list", () => {
    const parsed = parseInstantBIChatResponse({
      mode: "think",
      asked_questions: ["What is total cost?", "Cost by month?"],
      opening_insight: "This looks at travel spend from a few angles.",
      final_answer: "Costs rose",
      chat_responses: [
        {
          sub_question: "What is total cost?",
          title: "KPI",
          analysis: "Total is 100",
          chat_seq_id: "1-1",
          chat_response: {
            summary: { insight: "Total is 100" },
            viz: { vf_title: "KPI", vf_template: btoa("function A(){}") },
            sql: { raw_sql: "SELECT 100" },
            data: [{ v: 100 }],
          },
        },
        {
          sub_question: "Cost by month?",
          title: "Trend",
          analysis: "Upward",
          chat_seq_id: "1-2",
          chat_response: {
            summary: { insight: "Upward" },
            viz: { vf_title: "Trend" },
            sql: { raw_sql: "SELECT 2" },
          },
        },
      ],
    });
    expect(parsed.mode).toBe("think");
    expect(parsed.askedQuestions).toEqual(["What is total cost?", "Cost by month?"]);
    expect(parsed.openingInsight).toBe("This looks at travel spend from a few angles.");
    expect(parsed.finalAnswer).toBe("Costs rose");
    expect(parsed.chatResponses).toHaveLength(2);
    expect(parsed.chatResponses[0].subQuestion).toBe("What is total cost?");
    expect(parsed.chatResponses[0].botMessage).toBe("Total is 100");
    expect(parsed.chatResponses[0].createPreview).toBe(true);
    expect(parsed.chatResponses[1].vf_title).toBe("Trend");
    expect(parsed.chatResponses[1].createPreview).toBe(false);
  });
});

describe("shouldUseLoadChatPayloadForInsight", () => {
  it("should uses loadchat payload in open mode", () => {
    expect(
      shouldUseLoadChatPayloadForInsight({ isOpenMode: true, isEditMode: false })
    ).toBe(true);
  });

  it("should uses load-chat payload in edit mode for persisted messages", () => {
    expect(
      shouldUseLoadChatPayloadForInsight({
        isEditMode: true,
        persistedInFile: true,
      })
    ).toBe(true);
  });

  it("should uses interactive payload in edit mode for new unsaved messages", () => {
    expect(
      shouldUseLoadChatPayloadForInsight({
        isEditMode: true,
        needsLoadChat: false,
        persistedInFile: false,
      })
    ).toBe(false);
  });

  it("uses interactive payload in create mode", () => {
    expect(shouldUseLoadChatPayloadForInsight({})).toBe(false);
  });
});

describe("buildInstantBIInteractiveChatFormData", () => {
  it("should not include nested formData when not provided", () => {
    expect(
      buildInstantBIInteractiveChatFormData({
        input: "Show sales",
        chatId: "chat-1",
        chatSequenceId: 2,
      })
    ).toEqual({
      input: "Show sales",
      chatid: "chat-1",
      chat_sequence_id: 2,
    });
  });

  it("should include mode when provided", () => {
    expect(
      buildInstantBIInteractiveChatFormData({
        input: "Why is travel cost high?",
        chatId: "chat-1",
        chatSequenceId: 2,
        mode: "think",
      })
    ).toEqual({
      input: "Why is travel cost high?",
      chatid: "chat-1",
      chat_sequence_id: 2,
      mode: "think",
      show_llm_activity_details: true,
    });
  });

  it("should send auto mode with activity details", () => {
    expect(
      buildInstantBIInteractiveChatFormData({
        input: "Total travel cost",
        chatId: "chat-1",
        chatSequenceId: 2,
        mode: "auto",
      })
    ).toEqual({
      input: "Total travel cost",
      chatid: "chat-1",
      chat_sequence_id: 2,
      mode: "auto",
      show_llm_activity_details: true,
    });
  });
});
