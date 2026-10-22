import "regenerator-runtime/runtime";
import { getInsantBISaveData } from "../../components/hi-instant-bi/utils/base";
import { shouldUseLoadChatPayloadForInsight, buildInstantBIInteractiveChatFormData, parseInstantBIChatResponse } from "../../components/hi-instant-bi/utils/instant-bi-requests";
import { loadInstantBiHreports, resolveInstantBiSql } from "../../components/hi-instant-bi/utils/common-utils";
import createHReportBridge from "../../components/bridges/hreport/hreport-bridge";
import instantBIReducer from "../../redux/reducers/instant-bi.reducer";
import actionTypes from "../../redux/actions/actionTypes";
import initialStates from "../../redux/reducers/initialStates";

jest.mock("../../pages", () => {
  const React = require("react");
  return {
    HelicalReports: () => <div data-testid="ib-custom-chart" />,
  };
});

jest.mock("../../components/hi-instant-bi/components/ib-custom-chart", () => {
  const React = require("react");
  return {
    __esModule: true,
    default: () => <div data-testid="ib-custom-chart" />,
    IB_CHART_RENDER_ERROR: "Something went wrong. Please try again.",
    IbResponseError: () => <div data-testid="ib-vf-template-error" />,
  };
});

jest.mock("../../components/bridges/hreport/hreport-bridge", () => ({
  __esModule: true,
  default: jest.fn((props = {}) => ({
    init: () => {
      props.onComplete?.();
      return Promise.resolve();
    },
  })),
}));

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

  it("it should persist preferred chart and drop similar_chart on save", () => {
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
    });
  });

  it("it should save think steps slim without fullChatResponse duplicates", () => {
    const liveStep = {
      index: 1,
      question: "Q1",
      title: "T1",
      viz_hint: "bar",
      purpose: "purpose",
      components: ["a"],
      measure_hints: [],
      dimensions: [],
      answer: "ans",
      analysis: "analysis",
      chat_seq_id: "1_1",
      data: [{ v: 1 }],
      vf: "function Draw(){}",
      vizDetails: { chart_name: "bar" },
      vizLoading: false,
      vizError: "",
      vizActivityTrail: ["trail"],
      report_model: { viz_model: { id: "m1" } },
      chat_response: {
        viz: { vf_title: "T1" },
        sql: { raw_sql: "SELECT 1" },
        summary: { insight: "ans" },
        report_model: { viz_model: { id: "m1" } },
        token_usage: {},
        error: "",
        hreportId: "hr-1",
      },
      fullChatResponse: {
        viz: { vf_title: "T1" },
        sql: { raw_sql: "SELECT 1" },
        summary: { insight: "ans" },
        report_model: { viz_model: { id: "m1" } },
        token_usage: {},
        error: "",
        hreportId: "hr-1",
      },
    };
    const activeReport = buildActiveReportForSave();
    activeReport.chats[0].messageList.push({
      isUser: false,
      userInput: "Think question",
      chatSequenceId: 2,
      isThinkPlan: true,
      askedQuestions: ["Q1"],
      questionHistory: [liveStep],
      citedQuestionIndexes: [1],
      openingInsight: "Opening",
      finalAnswer: "Final",
      text: "Final",
      plan: { strategy_id: "s1" },
      dashboardModel: { title: "Dash" },
      llmActivityDetails: {
        steps: 1,
        final_answer: "nested final",
        path: [
          {
            name: "final_answer",
            stage: "final_answer",
            result: { final_answer: "Final", cited_question_indexes: [1] },
          },
        ],
      },
      fullChatResponse: {
        final_answer: "from full chat",
        opening_insight: "from full chat",
      },
    });
    const saveData = getInsantBISaveData({
      activeReport,
      saveFileInfo: { reportName: "MyReport", location: "reports" },
      dispatch: mockDispatch,
    });
    const savedThink = saveData.state.chat_responses.find((entry) => entry.is_think_plan);
    const savedStep = savedThink.question_history[0];
    [
      "fullChatResponse",
      "report_model",
      "reportModel",
      "vf",
      "vizDetails",
      "vizLoading",
      "vizError",
      "vizActivityTrail",
      "answer",
    ].forEach((key) => expect(savedStep).not.toHaveProperty(key));
    expect(savedStep.hreportId).toBe("hr-1");
    expect(savedStep.chat_response).not.toHaveProperty("hreportId");
    expect(savedStep.chat_response.report_model).toEqual({ viz_model: { id: "m1" } });
    expect(savedStep.data).toEqual([{ v: 1 }]);
    expect(savedThink).not.toHaveProperty("dashboard");
    expect(savedThink).not.toHaveProperty("fullChatResponse");
    expect(savedThink).not.toHaveProperty("viz");
    expect(savedThink).not.toHaveProperty("final_answer");
    expect(savedThink).not.toHaveProperty("finalAnswer");
    expect(savedThink.opening_insight).toBe("Opening");
    expect(savedThink).not.toHaveProperty("openingInsight");
    expect(savedThink).not.toHaveProperty("summary");
    expect(savedThink.llm_activity_details).not.toHaveProperty("final_answer");
    expect(savedThink.llm_activity_details.path || []).toHaveLength(0);
    expect(JSON.stringify(savedThink)).not.toMatch(/"final_answer"/);
    expect(savedThink.dashboard_model).toEqual({ title: "Dash" });
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

describe("resolveInstantBiSql", () => {
  const savedResponse = {
    sql: {
      raw_sql: "```sql\nSELECT SUM(\"travel_details\".\"travel_cost\") AS \"Travel Cost\"\nFROM \"travel_details\"\nLIMIT 100",
    },
    report_model: {
      data_model: {
        sql: "SELECT SUM(\"travel_details\".\"travel_cost\") AS \"Travel Cost\" FROM \"travel_details\" LIMIT 100",
      },
    },
  };

  it("reads saved raw_sql when open and edit leave message sql empty", () => {
    expect(resolveInstantBiSql({
      messageSql: "",
      fullChatResponse: savedResponse,
    })).toBe('SELECT SUM("travel_details"."travel_cost") AS "Travel Cost"\nFROM "travel_details"\nLIMIT 100');
  });

  it("prefers a live message sql over the saved response", () => {
    expect(resolveInstantBiSql({
      messageSql: "SELECT 1",
      fullChatResponse: savedResponse,
    })).toBe("SELECT 1");
  });

  it("falls back to data_model sql when raw_sql is missing", () => {
    expect(resolveInstantBiSql({
      messageSql: "",
      fullChatResponse: {
        report_model: savedResponse.report_model,
      },
    })).toBe(savedResponse.report_model.data_model.sql);
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
      expect(message.sql).toBe("");
      expect(message.text).toBe(`Insight ${seq}`);
      // Raw backend payload must not leak onto the message (no duplication).
      expect(message).not.toHaveProperty("viz");
      expect(message).not.toHaveProperty("summary");
      expect(message).not.toHaveProperty("activity_trail");
      expect(message.fullChatResponse).toEqual({
        viz: { vf_title: `Chart ${seq}` },
        sql: { raw_sql: `SELECT ${seq}` },
        summary: { insight: `Insight ${seq}` },
      });
    });
    expect(report.previews).toEqual([]);
    expect(report.loadedChatResponses).toEqual({});
  });

  it("it should load think-plan responses without duplicating snake_case fields", () => {
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
            inputs: [{ chat_sequence_id: 1, input: "Think question" }],
            chat_responses: [
              {
                chat_sequence_id: 1,
                is_think_plan: true,
                asked_questions: ["Q1", "Q2"],
                question_history: [
                  {
                    index: 1,
                    question: "Q1",
                    chat_seq_id: "1_1",
                    report_model: { viz_model: {} },
                    chat_response: {
                      hreportId: "hr-1",
                      report_model: { viz_model: {} },
                    },
                    fullChatResponse: {
                      hreportId: "hr-1",
                      report_model: { viz_model: {} },
                    },
                  },
                ],
                cited_question_indexes: [1],
                opening_insight: "Opening",
                final_answer: "Final",
                plan: { strategy_id: "s1" },
                dashboard_model: { title: "Dash" },
                dashboard: { title: "Dash" },
                llm_activity_details: { steps: 2 },
                summary: { insight: "Final" },
              },
            ],
          },
        },
      },
    });
    const report = state.reports[0];
    const botMessage = report.chats[0].messageList.find((message) => !message.isUser);
    expect(botMessage.isThinkPlan).toBe(true);
    expect(botMessage.text).toBe("Final");
    expect(botMessage.finalAnswer).toBe("Final");
    expect(botMessage.askedQuestions).toEqual(["Q1", "Q2"]);
    expect(botMessage.questionHistory).toHaveLength(1);
    expect(botMessage.questionHistory[0].fullChatResponse.hreportId).toBe("hr-1");
    expect(botMessage.openingInsight).toBe("Opening");
    expect(botMessage.citedQuestionIndexes).toEqual([1]);
    expect(botMessage.plan).toEqual({ strategy_id: "s1" });
    expect(botMessage.dashboardModel).toEqual({ title: "Dash" });
    expect(botMessage.llmActivityDetails).toEqual({ steps: 2 });
    // No snake_case duplicates on the message itself.
    [
      "is_think_plan",
      "asked_questions",
      "question_history",
      "cited_question_indexes",
      "opening_insight",
      "final_answer",
      "dashboard_model",
      "llm_activity_details",
      "activity_trail",
      "viz",
      "summary",
      "dashboard",
    ].forEach((key) => expect(botMessage).not.toHaveProperty(key));
    // Raw payload preserved for tabs/bridge rehydration.
    expect(botMessage.fullChatResponse.is_think_plan).toBe(true);
    expect(botMessage.fullChatResponse.question_history).toHaveLength(1);
  });

  it("it should rebuild step fullChatResponses from the slim persisted shape", () => {
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
            inputs: [{ chat_sequence_id: 1, input: "Think question" }],
            chat_responses: [
              {
                chat_sequence_id: 1,
                is_think_plan: true,
                asked_questions: ["Q1"],
                question_history: [
                  {
                    index: 1,
                    question: "Q1",
                    title: "T1",
                    answer: "ans",
                    analysis: "analysis",
                    chat_seq_id: "1_1",
                    data: [{ v: 1 }],
                    hreportId: "hr-9",
                    chat_response: {
                      viz: { vf_title: "T1", vf_template: btoa("function Draw(){}") },
                      sql: { raw_sql: "SELECT 1" },
                      summary: { insight: "ans" },
                      report_model: { viz_model: { id: "m1" } },
                      token_usage: {},
                    },
                  },
                ],
                final_answer: "Final",
                summary: { insight: "Final" },
              },
            ],
          },
        },
      },
    });
    const botMessage = state.reports[0].chats[0].messageList.find((message) => !message.isUser);
    const step = botMessage.questionHistory[0];
    expect(step.hreportId).toBe("hr-9");
    expect(step.report_model).toEqual({ viz_model: { id: "m1" } });
    expect(step.chat_response).not.toHaveProperty("hreportId");
    expect(step.fullChatResponse.hreportId).toBe("hr-9");
    expect(step.fullChatResponse.report_model).toEqual({ viz_model: { id: "m1" } });
    expect(step.vizLoading).toBe(false);
    expect(step.vizError).toBe("");
    expect(step.data).toEqual([{ v: 1 }]);
    expect(botMessage.fullChatResponse.question_history[0].fullChatResponse.hreportId).toBe("hr-9");
  });

  it("it should assign a fresh hreportId to fast responses missing one", () => {
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
              { chat_sequence_id: 1, input: "Chart question" },
              { chat_sequence_id: 2, input: "Text question" },
            ],
            chat_responses: [
              {
                chat_sequence_id: 1,
                viz: { vf_title: "Chart", vf_template: btoa("function Draw(){}") },
                sql: { raw_sql: "SELECT 1" },
                summary: { insight: "Insight 1" },
              },
              {
                chat_sequence_id: 2,
                summary: { insight: "Just text" },
              },
            ],
          },
        },
      },
    });
    const botMessages = state.reports[0].chats[0].messageList.filter((message) => !message.isUser);
    const chartMessage = botMessages.find((message) => message.chatSequenceId === 1);
    const textMessage = botMessages.find((message) => message.chatSequenceId === 2);
    expect(chartMessage.fullChatResponse.hreportId).toBeDefined();
    expect(textMessage.fullChatResponse).not.toHaveProperty("hreportId");
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

describe("loadInstantBiHreports", () => {
  const makeDispatch = () => {
    const fn = jest.fn((action) => {
      if (typeof action === "function") {
        return action(fn, () => ({
          instantBI: {
            reports: [
              {
                active: true,
                metadataForHreport: { metadata: {}, funcs: [], dateFunctions: [] },
              },
            ],
          },
        }));
      }
      return action;
    });
    return fn;
  };

  beforeEach(() => {
    createHReportBridge.mockClear();
  });

  it("should rehydrate fast responses and think-plan steps with saved hreportIds", async () => {
    const dispatch = makeDispatch();
    await loadInstantBiHreports({
      dispatch,
      reportState: {
        chat_responses: [
          {
            chat_sequence_id: 1,
            is_think_plan: true,
            question_history: [
              {
                index: 1,
                question: "Q1",
                report_model: { viz_model: { id: "m1" } },
                fullChatResponse: {
                  hreportId: "hr-step-1",
                  report_model: { viz_model: { id: "m1" } },
                },
              },
              {
                index: 2,
                question: "Q2",
                report_model: {},
                fullChatResponse: {},
              },
            ],
            final_answer: "Final",
          },
          {
            chat_sequence_id: 2,
            hreportId: "hr-fast",
            report_model: { viz_model: { id: "m2" } },
          },
        ],
        hreportInteractions: {
          "hr-step-1": { selectedType: "Antcharts" },
        },
      },
    });
    const calls = createHReportBridge.mock.calls.map(([props]) => props);
    expect(calls.map((props) => props.reportId)).toEqual(["hr-step-1", "hr-fast"]);
    expect(calls[0].reportModel).toEqual({ viz_model: { id: "m1" } });
    expect(calls[0].interactions).toEqual({ selectedType: "Antcharts" });
    expect(calls[1].reportModel).toEqual({ viz_model: { id: "m2" } });
    expect(calls[1].interactions).toEqual({});
  });

  it("should rehydrate slim steps with hoisted hreportIds", async () => {
    const dispatch = makeDispatch();
    await loadInstantBiHreports({
      dispatch,
      reportState: {
        chat_responses: [
          {
            chat_sequence_id: 1,
            is_think_plan: true,
            question_history: [
              {
                index: 1,
                question: "Q1",
                hreportId: "hr-slim-1",
                chat_response: {
                  viz: { vf_title: "T1" },
                  report_model: { viz_model: { id: "m1" } },
                },
              },
            ],
            final_answer: "Final",
          },
        ],
        hreportInteractions: {},
      },
    });
    const calls = createHReportBridge.mock.calls.map(([props]) => props);
    expect(calls.map((props) => props.reportId)).toEqual(["hr-slim-1"]);
    expect(calls[0].reportModel).toEqual({ viz_model: { id: "m1" } });
  });

  it("should skip error responses and steps without a report model", async () => {
    const dispatch = makeDispatch();
    await loadInstantBiHreports({
      dispatch,
      reportState: {
        chat_responses: [
          { chat_sequence_id: 1, hreportId: "hr-error", error: "boom" },
          {
            chat_sequence_id: 2,
            is_think_plan: true,
            question_history: [
              {
                index: 1,
                question: "Q1",
                fullChatResponse: { hreportId: "hr-no-model" },
              },
            ],
          },
        ],
        hreportInteractions: {},
      },
    });
    expect(createHReportBridge).not.toHaveBeenCalled();
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

  it("should omit sql and chat_response_item from data-insight payloads", () => {
    expect(
      buildInstantBIInteractiveChatFormData({
        input: "Explain this chart",
        chatId: "chat-1",
        chatSequenceId: "1-1",
      })
    ).toEqual({
      input: "Explain this chart",
      chatid: "chat-1",
      chat_sequence_id: "1-1",
    });
  });

  it("should send chat id and parent sequence for think-plan explain", () => {
    expect(
      buildInstantBIInteractiveChatFormData({
        input: "Which employee should I pick?",
        chatId: "chat-1",
        chatSequenceId: "1",
      })
    ).toEqual({
      input: "Which employee should I pick?",
      chatid: "chat-1",
      chat_sequence_id: "1",
    });
  });
});
