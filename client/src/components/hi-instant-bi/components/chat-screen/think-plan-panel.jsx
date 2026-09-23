import React, { useEffect, useMemo, useRef, useState } from "react";
import { Typography } from "antd";
import { AppstoreOutlined, ClusterOutlined, HistoryOutlined } from "@ant-design/icons";
import Markdown from "react-markdown";
import remarkGfm from "remark-gfm";
import InstantBITooltip from "../../instant-bi-tooltip-title";
import { JsonTab } from "./chat-tabs";
import WorkBehindIcon from "./work-behind-icon";
import ThinkCitationMarkdown from "./think-citation-markdown";
import { firstNonEmptyJson } from "./think-plan-helpers";
import ThinkStepCard from "./think-step-card";

const { Text } = Typography;

const ThinkPlanPanel = ({
  questionHistory = [],
  askedQuestions = [],
  openingInsight = "",
  finalAnswer = "",
  citedQuestionIndexes: _citedQuestionIndexes = [],
  isStreaming = false,
  activityTrail = [],
  userInput = "",
  planMessageId = "",
  planChatSequenceId = "",
  isOpenMode = false,
  onShowQuestion,
  dispatch,
  activeReport,
  llmActivityDetails = null,
  plan = null,
  dashboardModel = null,
}) => {
  const items = useMemo(() => {
    if ((questionHistory || []).length) return questionHistory;
    return (askedQuestions || []).map((question, index) => ({
      index: index + 1,
      question,
      title: "",
      answer: "",
      analysis: "",
    }));
  }, [questionHistory, askedQuestions]);

  const resolvedDashboardModel = firstNonEmptyJson(
    dashboardModel,
    plan?.dashboard,
    plan?.dashboard_model,
    plan,
  );
  const resolvedActivityDetails = firstNonEmptyJson(
    llmActivityDetails,
    {
      ...(firstNonEmptyJson(plan) ? { plan } : {}),
      ...(Array.isArray(activityTrail) && activityTrail.length
        ? { activity_trail: activityTrail }
        : {}),
      ...(items.length ? { question_history: items } : {}),
    },
  );
  const [planView, setPlanView] = useState(null);
  const [citationFocus, setCitationFocus] = useState({ index: null, nonce: 0 });
  const highlightClearRef = useRef(null);
  const togglePlanView = (view) => {
    setPlanView((current) => (current === view ? null : view));
  };

  const handleCitationClick = (stepIndex) => {
    const target = Number(stepIndex);
    if (!Number.isFinite(target) || target < 1) return;
    setCitationFocus((prev) => ({ index: target, nonce: prev.nonce + 1 }));
    if (highlightClearRef.current) {
      clearTimeout(highlightClearRef.current);
    }
    highlightClearRef.current = setTimeout(() => {
      setCitationFocus((prev) => (
        prev.index === target ? { index: null, nonce: prev.nonce } : prev
      ));
    }, 1600);
    // Wait for expand paint before scrolling so the step is visible.
    window.setTimeout(() => {
      const el = document.querySelector(`[data-testid="ib-think-step-${target}"]`);
      el?.scrollIntoView({ behavior: "smooth", block: "center" });
    }, 60);
  };

  useEffect(() => () => {
    if (highlightClearRef.current) clearTimeout(highlightClearRef.current);
  }, []);

  if (isStreaming) {
    return (
      <div className="ib-think-plan ib-think-plan--streaming">
        <div className="ib-stream-with-work-behind" data-testid="ib-think-streaming">
          <WorkBehindIcon
            active
            lines={activityTrail}
            question={userInput}
            fallback="Planning supporting questions…"
            testId="ib-think-plan-work-behind"
          />
        </div>
      </div>
    );
  }

  return (
    <div className="ib-think-plan" data-testid="ib-think-plan">
      {items.length ? (
        <div className="ib-think-plan__toolbar">
          <div className={`ib-think-plan__toolbar-actions${planView ? " is-open" : ""}`}>
            <InstantBITooltip title="Workings">
              <button
                type="button"
                className={`ib-think-plan__icon-btn${planView === "workings" ? " is-active" : ""}`}
                onClick={() => togglePlanView("workings")}
                data-testid="ib-think-plan-workings"
                aria-pressed={planView === "workings"}
              >
                <HistoryOutlined />
              </button>
            </InstantBITooltip>
            <InstantBITooltip title="Dashboard Model">
              <button
                type="button"
                className={`ib-think-plan__icon-btn${planView === "dashboard" ? " is-active" : ""}`}
                onClick={() => togglePlanView("dashboard")}
                data-testid="ib-think-plan-dashboard-model"
                aria-pressed={planView === "dashboard"}
              >
                <AppstoreOutlined />
              </button>
            </InstantBITooltip>
            <InstantBITooltip title="Activity Details">
              <button
                type="button"
                className={`ib-think-plan__icon-btn${planView === "activity" ? " is-active" : ""}`}
                onClick={() => togglePlanView("activity")}
                data-testid="ib-think-plan-activity-details"
                aria-pressed={planView === "activity"}
              >
                <ClusterOutlined />
              </button>
            </InstantBITooltip>
          </div>
        </div>
      ) : null}

      {planView === "workings" ? (
        <div className="ib-think-plan__inspector" data-testid="ib-think-plan-inspector-workings">
          {Array.isArray(activityTrail) && activityTrail.length ? (
            <WorkBehindIcon
              lines={activityTrail}
              question={userInput}
              fallback="No workings recorded for this investigation."
              testId="ib-think-plan-workings-panel"
            />
          ) : (
            <Text type="secondary">No workings recorded for this investigation.</Text>
          )}
        </div>
      ) : null}
      {planView === "dashboard" ? (
        <div className="ib-think-plan__inspector" data-testid="ib-think-plan-inspector-dashboard">
          <JsonTab value={resolvedDashboardModel} emptyLabel="No dashboard model available." />
        </div>
      ) : null}
      {planView === "activity" ? (
        <div className="ib-think-plan__inspector" data-testid="ib-think-plan-inspector-activity">
          <JsonTab value={resolvedActivityDetails} emptyLabel="No LLM activity details available." />
        </div>
      ) : null}

      {openingInsight ? (
        <div
          className="message-container__bot-markdown-renderer ib-think-plan__intro"
          data-testid="ib-think-plan-intro"
        >
          <Markdown remarkPlugins={[remarkGfm]}>{openingInsight}</Markdown>
        </div>
      ) : null}

      <div className="ib-think-plan__details">
        <div className="ib-think-plan__steps">
          {items.map((item, idx) => {
            const stepIndex = item.index || idx + 1;
            return (
              <ThinkStepCard
                key={`${stepIndex}-${item.question || ""}`}
                item={item}
                planMessageId={planMessageId}
                planChatSequenceId={planChatSequenceId}
                isStreaming={false}
                isOpenMode={isOpenMode}
                onShowQuestion={onShowQuestion}
                dispatch={dispatch}
                activeReport={activeReport}
                llmActivityDetails={resolvedActivityDetails}
                plan={plan}
                expandSignal={
                  citationFocus.index === stepIndex ? citationFocus.nonce : 0
                }
                citationHighlight={citationFocus.index === stepIndex}
              />
            );
          })}
          {!items.length ? (
            <Text type="secondary">Preparing supporting questions…</Text>
          ) : null}
        </div>
      </div>

      {finalAnswer ? (
        <div className="message-container__bot-content ib-think-final">
          <div className="message-container__bot-markdown-renderer">
            <ThinkCitationMarkdown onCitationClick={handleCitationClick}>
              {finalAnswer}
            </ThinkCitationMarkdown>
          </div>
        </div>
      ) : null}
    </div>
  );
};

export default ThinkPlanPanel;
