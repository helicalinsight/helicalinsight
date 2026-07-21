import "core-js/stable";
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
  it("it should include summary and exclude metadata from chat responses", () => {
    const saveData = getInsantBISaveData({
      activeReport: buildActiveReportForSave(),
      saveFileInfo: {
        reportName: "MyReport",
        location: "reports",
      },
    });
    const savedResponse = saveData.state.chat_responses[0];
    expect(savedResponse.chat_sequence_id).toBe(1);
    expect(savedResponse.summary).toEqual({ insight: "Sales insight" });
    expect(savedResponse).not.toHaveProperty("metadata");
    expect(savedResponse.viz).toEqual({ vf_title: "Sales chart" });
    expect(savedResponse.sql).toEqual({ raw_sql: "SELECT 1" });
  });

  it("it should exclude runtime metadata & loaded chat cache from state", () => {
    const saveData = getInsantBISaveData({
      activeReport: buildActiveReportForSave(),
      saveFileInfo: {
        reportName: "MyReport",
        location: "reports",
      },
    });
    expect(saveData.state).not.toHaveProperty("metadata");
    expect(saveData.state).not.toHaveProperty("loadedChatResponses");
    expect(saveData.state).not.toHaveProperty("loadedChatResponseSources");
    expect(saveData.state).not.toHaveProperty("botStatus");
    expect(saveData.metadata).toEqual({
      location: "agents",
      metadataFileName: "sales.metadata",
    });
  });
});

describe("instantBIReducer LOAD_IB_REPORT_DATA", () => {
  it("it should build bot messages that require scroll load without inline response data", () => {
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
      expect(message.needsLoadChat).toBe(true);
      expect(message.persistedInFile).toBe(true);
      expect(message.data).toEqual([]);
      expect(message.metadata).toEqual([]);
      expect(message.vf).toBe("");
      expect(message.sql).toBe("");
      expect(message.text).toBe("");
      expect(message).not.toHaveProperty("fullChatResponse");
    });
    expect(report.previews).toEqual([]);
    expect(report.loadedChatResponses).toEqual({});
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
});
