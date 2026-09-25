import React, { useEffect, useRef, useState } from "react";
import { Modal, Row, Typography } from "antd";
import { CaretRightOutlined } from "@ant-design/icons";
import Markdown from "react-markdown";
import remarkGfm from "remark-gfm";
import InstantBITooltip from "../../instant-bi-tooltip-title";
import { cleanSQL, getInstantBIAgentSubject } from "../../utils/common-utils";
import { loadInstantBIDataInsight } from "../../utils/instant-bi-requests";
import ChatTabs from "./chat-tabs";
import InstantChartView from "./chart-view";
import { getSharedChatTabsShell } from "./chat-tabs-shared";
import notify from "../../../hi-notifications/notify";
import { IbResponseError } from "../ib-custom-chart";
import { buildSqlDetailsFallback, cleanText, collapsedQuestionLead } from "./think-plan-helpers";

const { Text } = Typography;

const GENERIC_STEP_ERRORS = new Set([
  "no report model available for this step.",
  "report model is not available for this step yet.",
  "unable to load visualization.",
]);

const errorText = (value) => {
  if (value == null || value === false) return "";
  if (typeof value === "string") return value.trim();
  if (typeof value === "object") {
    return String(value.message || value.error || "").trim();
  }
  return String(value).trim();
};

const stepErrorDetails = (item = {}) => {
  const chat = item.fullChatResponse || item.chat_response || {};
  const sql = chat.sql && typeof chat.sql === "object" ? chat.sql : {};
  const seen = new Set();
  const lines = [];
  [
    item.sql_error,
    item.sqlError,
    chat.sql_error,
    sql.error,
    chat.error,
    item.error,
    item.execution_result,
    item.vizError,
    item.analysis,
  ].forEach((value) => {
    const text = errorText(value);
    const key = text.toLowerCase();
    if (!text || GENERIC_STEP_ERRORS.has(key) || seen.has(key)) return;
    seen.add(key);
    lines.push(text);
  });
  return lines.join("\n\n");
};

const ThinkStepCard = ({
  item,
  planMessageId,
  planChatSequenceId = "",
  isStreaming = false,
  isOpenMode = false,
  onShowQuestion,
  dispatch,
  activeReport,
  llmActivityDetails = null,
  plan = null,
  expandSignal = 0,
  citationHighlight = false,
}) => {
  const index = item.index || 0;
  const question = item.question || "";
  const analysis = cleanText(item.analysis);
  const findings = cleanText(item.answer) || cleanText(item.purpose);
  const fullChatResponse = item.fullChatResponse
    || item.chat_response
    || item.chatResponse
    || {};
  const vizDetails = item.vizDetails || fullChatResponse?.viz || {};
  const sqlDetails = buildSqlDetailsFallback(item, llmActivityDetails, plan);
  const resolvedSql = sqlDetails?.raw_sql || "";
  const resolvedData = Array.isArray(item.data) ? item.data : [];
  const resolvedVf = item.vf
    || (vizDetails?.vf_template ? (() => {
      try {
        return atob(vizDetails.vf_template);
      } catch (_err) {
        return "";
      }
    })() : "");
  const hasViz = Boolean(fullChatResponse?.hreportId);
  const isLoadingViz = Boolean(item.vizLoading);
  const hasReportModel = Boolean(
    Object.keys(item.report_model || item.reportModel || fullChatResponse?.report_model || {}).length
  );
  const tokenUsage = fullChatResponse?.token_usage || {};
  const chartSettings = vizDetails?.settings || {};
  const hasMessage = Boolean(analysis || findings || question);
  const stepHeading = item.title || question || `Question ${index}`;
  const stepChatSeqId = item.chat_seq_id
    || item.chatSeqId
    || (planChatSequenceId ? `${planChatSequenceId}_${index}` : "");
  const [isMaximized, setIsMaximized] = useState(false);
  const [expanded, setExpanded] = useState(Boolean(isStreaming));
  const collapsedQuestion = !expanded && question && question !== stepHeading ? question : "";
  const [isLoadingDataInsight, setIsLoadingDataInsight] = useState(false);
  const [dataInsightTrail, setDataInsightTrail] = useState([]);
  const [dataInsightContent, setDataInsightContent] = useState(
    fullChatResponse?.data_insight?.insight || ""
  );
  const dataInsightApiRef = useRef(null);
  const dataInsightAbortedRef = useRef(false);
  const previewHydratedRef = useRef(false);
  const Notify = notify(dispatch);

  useEffect(() => {
    if (!isStreaming) {
      setExpanded(false);
    }
  }, [isStreaming]);

  useEffect(() => {
    if (expandSignal) {
      setExpanded(true);
    }
  }, [expandSignal]);

  useEffect(() => {
    // Auto-load Preview when the step has a report model ready.
    if (
      previewHydratedRef.current
      || isStreaming
      || isLoadingViz
      || hasViz
      || !hasReportModel
      || !question
      || !onShowQuestion
    ) {
      return;
    }
    previewHydratedRef.current = true;
    onShowQuestion(question, index, item);
  }, [
    hasReportModel,
    hasViz,
    index,
    isLoadingViz,
    isStreaming,
    item,
    onShowQuestion,
    question,
  ]);

  const handleCopySQL = async () => {
    try {
      await navigator.clipboard.writeText(cleanSQL(resolvedSql));
    } catch (_err) { /* ignore */ }
  };

  const handleAbortDataInsight = () => {
    dataInsightAbortedRef.current = true;
    dataInsightApiRef.current?.abort();
    dataInsightApiRef.current = null;
    setIsLoadingDataInsight(false);
  };

  const handleDataInsight = () => {
    if (!hasViz || isLoadingDataInsight || !dispatch) return;
    const reportId = activeReport?.id;
    const activeChatID = activeReport?.activeChatID;
    if (!reportId || !activeChatID || !stepChatSeqId) {
      Notify?.error?.({
        type: "Frontend",
        message: "Unable to explain this chart: chat sequence is missing.",
      });
      return;
    }
    if (!resolvedSql && !fullChatResponse?.sql?.raw_sql) {
      Notify?.error?.({
        type: "Frontend",
        message: "Unable to explain this chart: SQL is not available yet.",
      });
      return;
    }
    dataInsightAbortedRef.current = false;
    setIsLoadingDataInsight(true);
    setDataInsightTrail([]);
    const agentSubject = getInstantBIAgentSubject(activeReport) || {};
    const reportInfo = activeReport?.reportInfo || {};
    dataInsightApiRef.current = loadInstantBIDataInsight({
      dispatch,
      reportId,
      chatSequenceId: stepChatSeqId,
      userInput: question,
      location: reportInfo?.location,
      fileName: reportInfo?.uuid || (reportInfo?.reportName && `${reportInfo.reportName}.instant`),
      chatId: activeChatID,
      agent: agentSubject,
      useLoadChatPayload: Boolean(isOpenMode),
      sql: resolvedSql || fullChatResponse?.sql?.raw_sql || "",
      existingChatResponse: {
        ...fullChatResponse,
        sql: fullChatResponse?.sql || sqlDetails,
        data: resolvedData,
      },
      Notify,
      abortedRef: dataInsightAbortedRef,
      onProgress: (progress) => {
        if (progress?.message) {
          setDataInsightTrail((prev) => {
            if (prev[prev.length - 1] === progress.message) return prev;
            return [...prev, progress.message];
          });
        }
      },
      onComplete: (result) => {
        setIsLoadingDataInsight(false);
        dataInsightApiRef.current = null;
        const insight = result?.insight
          || result?.response?.insight
          || result?.data_insight?.insight
          || "";
        if (insight) setDataInsightContent(insight);
      },
    });
  };

  const tabsProps = {
    ...getSharedChatTabsShell({
      isOpenMode,
      hasMessage,
      showMaximizeButton: hasViz,
      isMaximized,
      setIsMaximized,
      showDataInsightButton: hasViz && !isLoadingDataInsight,
      workingLines: item.vizActivityTrail || [],
      workingActive: isLoadingViz,
      workingQuestion: question,
      workingFallback: "No workings recorded for this response.",
    }),
    showWorkingTab: false,
    vizDetails,
    isConvertingChart: false,
    resolvedData,
    resolvedVf,
    id: `${planMessageId || "think"}_${index}`,
    chartSettings,
    setHasPreviewError: () => {},
    resolvedFullChatResponse: fullChatResponse,
    fullChatResponse,
    isLoadingDataInsight,
    dataInsightTrail,
    dataInsightContent,
    handleAbortDataInsight,
    hasDataInsightTokens: false,
    dataInsightTokens: {},
    resolvedSql: resolvedSql
      ? `\`\`\`sql\n${cleanSQL(resolvedSql)}\n\`\`\``
      : "",
    handleCopySQL,
    sqlDetails,
    tokenUsage,
    dispatch,
    handleDataInsight,
    activeReport,
    isPreviewLoading: isLoadingViz,
    previewActivityTrail: item.vizActivityTrail || (
      isLoadingViz ? ["Preparing visualization from report model…"] : []
    ),
    previewPlaceholder: !isStreaming && (item.vizError || !hasReportModel) ? (
      <IbResponseError details={stepErrorDetails(item)} />
    ) : hasReportModel ? (
      <Text type="secondary">Open Preview to load this chart.</Text>
    ) : null,
    onPreviewActivate: () => {
      if (!question || isStreaming || isLoadingViz || hasViz || !hasReportModel) return;
      onShowQuestion?.(question, index, item);
    },
  };

  return (
    <div
      className={`message-container__bot-content ib-think-step${expanded ? " is-expanded" : " is-collapsed"}${citationHighlight ? " is-citation-focus" : ""}`}
      data-testid={`ib-think-step-${index}`}
    >
      <div className="ib-think-step__header">
        <span className="ib-think-step__rail-node" aria-hidden="true" />
        <button
          type="button"
          className="ib-think-step__toggle"
          onClick={() => setExpanded((value) => !value)}
          aria-expanded={expanded}
          aria-label={expanded ? "Collapse step" : "Expand step"}
          data-testid={`ib-think-step-toggle-${index}`}
        >
          {collapsedQuestion ? (
            <InstantBITooltip
              title={`${collapsedQuestionLead(collapsedQuestion)} ${collapsedQuestion}`}
              placement="topLeft"
              mouseEnterDelay={0.15}
              overlayClassName="ib-think-step-question-tooltip"
              overlayStyle={{ maxWidth: 480 }}
            >
              <span className="ib-think-step__toggle-label">{stepHeading}</span>
            </InstantBITooltip>
          ) : (
            <span className="ib-think-step__toggle-label">{stepHeading}</span>
          )}
        </button>
        <InstantBITooltip
          title={expanded ? "Hide details" : "See details"}
          placement="topRight"
          mouseEnterDelay={0}
          mouseLeaveDelay={0}
          align={{ offset: [4, -2] }}
        >
          <button
            type="button"
            className="ib-think-step__handle"
            onClick={() => setExpanded((value) => !value)}
            aria-label={expanded ? "Hide details" : "See details"}
            data-testid={`ib-think-step-caret-${index}`}
          >
            <CaretRightOutlined
              className={`ib-think-step__caret${expanded ? " is-expanded" : ""}`}
            />
          </button>
        </InstantBITooltip>
      </div>
      {expanded ? (
        <div className="ib-think-step__body">
          <div className="message-container__bot-markdown-renderer ib-think-step__markdown">
            {question && stepHeading !== question ? (
              <div className="ib-think-step__question">{question}</div>
            ) : null}
            {analysis ? (
              <div className="ib-think-step__section">
                <Markdown remarkPlugins={[remarkGfm]}>{analysis}</Markdown>
              </div>
            ) : null}
            {findings && findings !== analysis ? (
              <div className="ib-think-step__section">
                <Markdown remarkPlugins={[remarkGfm]}>{findings}</Markdown>
              </div>
            ) : null}
          </div>
          <div className="chart-container chart-container--with-tabs ib-think-step__tabs">
            <Row justify="end" className="instant-chart-tabs-row">
              <ChatTabs {...tabsProps} />
            </Row>
            <Modal
              title="Preview"
              visible={isMaximized && hasViz}
              onCancel={() => setIsMaximized(false)}
              width="95%"
              style={{ top: 20 }}
              footer={null}
              wrapClassName="ib-chart-preview-modal"
              destroyOnClose
            >
              <InstantChartView
                data={resolvedData}
                vf={resolvedVf}
                id={`${planMessageId || "think"}_${index}`}
                chartName={vizDetails?.chart_name}
                chartSettings={chartSettings}
                plotConfig={vizDetails?.plot_config}
                className="chart-wrapper--modal"
                fullChatResponse={fullChatResponse}
              />
            </Modal>
          </div>
        </div>
      ) : null}
    </div>
  );
};

export default ThinkStepCard;
