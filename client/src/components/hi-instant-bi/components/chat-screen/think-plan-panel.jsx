import React, { useEffect, useMemo, useRef, useState } from "react";
import { useSelector } from "react-redux";
import { Tabs, Typography } from "antd";
import { AppstoreOutlined, ClusterOutlined, DashboardOutlined, HistoryOutlined } from "@ant-design/icons";
import Markdown from "react-markdown";
import remarkGfm from "remark-gfm";
import InstantBITooltip from "../../instant-bi-tooltip-title";
import { hasDashboardTileModel } from "../../../bridges/dashboard/dashboard-bridge";
import { JsonTab } from "./chat-tabs";
import WorkBehindIcon from "./work-behind-icon";
import IBDashboardView from "./ib-dashboard-view/ib-dashboard-view";
import ThinkCitationMarkdown from "./think-citation-markdown";
import { firstNonEmptyJson } from "./think-plan-helpers";
import ThinkStepCard from "./think-step-card";
import AISparklesIcon from "./ai-sparkles-icon";
import ChatTypingText from "./chat-typing-text";
import LoadingBar from "../../../common/components/hi-loading-bar";
import notify from "../../../hi-notifications/notify";
import { getInstantBIAgentSubject } from "../../utils/common-utils";
import { loadInstantBIDataInsight } from "../../utils/instant-bi-requests";
import { useDashboardView } from "./dashboard-view-coordinator";

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
  isEditMode = false,
  onShowQuestion,
  dispatch,
  activeReport,
  llmActivityDetails = null,
  plan = null,
  dashboardModel = null,
  dataInsight = "",
}) => {
  const streamResponse = useSelector((state) => Boolean(state.app.applicationSettingsData?.streamResponse));
  const items = useMemo(() => {
    if ((questionHistory || []).length) return questionHistory;
    return (askedQuestions || []).map((question, index) => ({
      index: index + 1,
      question,
      title: "",
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
  const {
    activeDashboardViewId,
    activateDashboardView,
    deactivateDashboardView,
  } = useDashboardView();

  const canViewDashboard = Boolean(resolvedDashboardModel)
    && (questionHistory || []).some(hasDashboardTileModel);

  const isDashboardViewActive = planView === "dashboardView"
    && canViewDashboard
    && activeDashboardViewId === planMessageId;
  const highlightClearRef = useRef(null);
  const [isLoadingDataInsight, setIsLoadingDataInsight] = useState(false);
  const [dataInsightTrail, setDataInsightTrail] = useState([]);
  const [dataInsightContent, setDataInsightContent] = useState(dataInsight || "");
  const dataInsightApiRef = useRef(null);
  const dataInsightAbortedRef = useRef(false);
  const Notify = notify(dispatch);
  useEffect(() => {
    if (planView !== "dashboardView") return;
    if (activeDashboardViewId !== planMessageId) {
      setPlanView(null);
    }
  }, [activeDashboardViewId, planMessageId, planView]);

  const handleTabChange = (active) => {
    if (active === "dashboardView") {
      if (planView === "dashboardView") {
        deactivateDashboardView(planMessageId);
        setPlanView(null);
      } else {
        activateDashboardView(planMessageId);
        setPlanView("dashboardView");
      }
      return;
    }
    if (planView === "dashboardView") {
      deactivateDashboardView(planMessageId);
    }
    setPlanView((current) => (current === active ? null : active));
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

  useEffect(() => {
    if (dataInsight) setDataInsightContent(dataInsight);
  }, [dataInsight]);

  useEffect(() => () => {
    if (highlightClearRef.current) clearTimeout(highlightClearRef.current);
    if (dataInsightApiRef.current) {
      dataInsightAbortedRef.current = true;
      dataInsightApiRef.current.abort();
      dataInsightApiRef.current = null;
    }
  }, []);

  const handleAbortDataInsight = () => {
    dataInsightAbortedRef.current = true;
    dataInsightApiRef.current?.abort();
    dataInsightApiRef.current = null;
    setIsLoadingDataInsight(false);
  };

  const handleDataInsight = () => {
    if (!isOpenMode || isLoadingDataInsight || !dispatch) return;
    const reportId = activeReport?.id;
    const activeChatID = activeReport?.activeChatID;
    const reportInfo = activeReport?.reportInfo || {};
    if (!reportId || !activeChatID || !planChatSequenceId) {
      Notify?.error?.({
        type: "Frontend",
        message: "Unable to explain this investigation: chat sequence is missing.",
      });
      return;
    }
    dataInsightAbortedRef.current = false;
    setIsLoadingDataInsight(true);
    setDataInsightTrail([]);
    const agentSubject = getInstantBIAgentSubject(activeReport) || {};
    dataInsightApiRef.current = loadInstantBIDataInsight({
      dispatch,
      reportId,
      chatSequenceId: planChatSequenceId,
      userInput,
      location: reportInfo?.location,
      fileName: reportInfo?.uuid || (reportInfo?.reportName && `${reportInfo.reportName}.instant`),
      chatId: activeChatID,
      agent: agentSubject,
      useLoadChatPayload: Boolean(isOpenMode),
      existingChatResponse: {},
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

  const renderThinkStepCard = ({ item, stepIndex, onShowQuestion = () => { }, renderChartOnly = false, dashboardMode }) => {
    return (
      <ThinkStepCard
        key={`${stepIndex}-${item.question || ""}`}
        item={item}
        planMessageId={planMessageId}
        planChatSequenceId={planChatSequenceId}
        isStreaming={false}
        isOpenMode={isOpenMode}
        isEditMode={isEditMode}
        onShowQuestion={onShowQuestion}
        dispatch={dispatch}
        activeReport={activeReport}
        llmActivityDetails={resolvedActivityDetails}
        plan={plan}
        expandSignal={
          citationFocus.index === stepIndex ? citationFocus.nonce : 0
        }
        citationHighlight={citationFocus.index === stepIndex}
        renderChartOnly={renderChartOnly}
        dashboardMode={dashboardMode}
      />
    )
  }

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
      {isOpenMode ? (
        <InstantBITooltip title="Explain">
          <button
            type="button"
            className="ib-think-plan__data-insight-fab"
            data-testid="ib-think-plan-data-insight-btn"
            onClick={handleDataInsight}
            disabled={isLoadingDataInsight}
          >
            <AISparklesIcon />
          </button>
        </InstantBITooltip>
      ) : null}
      {items.length ? (
        <>
          <div className="ib-think-plan__toolbar">
            <div className={`ib-think-plan__toolbar-actions${planView ? " is-open" : ""}`}>
              {streamResponse && !isOpenMode ? (
                <InstantBITooltip title="Workings">
                  <button
                    type="button"
                    className={`ib-think-plan__icon-btn${planView === "workings" ? " is-active" : ""}`}
                    onClick={() => handleTabChange("workings")}
                    data-testid="ib-think-plan-workings"
                    aria-pressed={planView === "workings"}
                  >
                    <HistoryOutlined />
                  </button>
                </InstantBITooltip>
              ) : null}
              <InstantBITooltip title="Dashboard Model">
                <button
                  type="button"
                  className={`ib-think-plan__icon-btn${planView === "dashboard" ? " is-active" : ""}`}
                  onClick={() => handleTabChange("dashboard")}
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
                  onClick={() => handleTabChange("activity")}
                  data-testid="ib-think-plan-activity-details"
                  aria-pressed={planView === "activity"}
                >
                  <ClusterOutlined />
                </button>
              </InstantBITooltip>
              {canViewDashboard ? (
                <InstantBITooltip title="View Dashboard">
                  <button
                    type="button"
                    className={`ib-think-plan__icon-btn${planView === "dashboardView" ? " is-active" : ""}`}
                    onClick={() => handleTabChange("dashboardView")}
                    data-testid="ib-think-plan-view-dashboard"
                    aria-pressed={planView === "dashboardView"}
                  >
                    <DashboardOutlined />
                  </button>
                </InstantBITooltip>
              ) : null}
            </div>
          </div>
          <div className="ib-think-plan__tabs">
            <Tabs
              activeKey={planView}
              onChange={handleTabChange}
              renderTabBar={() => <></>}
              animated={false}
            >
              {streamResponse && !isOpenMode ? (
                <Tabs.TabPane key="workings" tab="">
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
                </Tabs.TabPane>
              ) : null}
              <Tabs.TabPane key="dashboard" tab="">
                <div className="ib-think-plan__inspector" data-testid="ib-think-plan-inspector-dashboard">
                  <JsonTab value={resolvedDashboardModel} emptyLabel="No dashboard model available." />
                </div>
              </Tabs.TabPane>
              <Tabs.TabPane key="activity" tab="">
                <div className="ib-think-plan__inspector" data-testid="ib-think-plan-inspector-activity">
                  <JsonTab value={resolvedActivityDetails} emptyLabel="No LLM activity details available." />
                </div>
              </Tabs.TabPane>
              {canViewDashboard ? (
                <Tabs.TabPane key="dashboardView" tab="">
                  <div className="ib-think-plan__inspector" data-testid="ib-think-plan-inspector-dashboard-view">
                    <IBDashboardView
                      dashboardModel={resolvedDashboardModel}
                      steps={questionHistory}
                      dispatch={dispatch}
                      activeReport={activeReport}
                      planMessageId={planMessageId}
                      isOpenMode={isOpenMode}
                      active={isDashboardViewActive}
                      questionsHistory={items}
                    />
                  </div>
                </Tabs.TabPane>
              ) : null}
            </Tabs>
          </div>
        </>
      ) : null}

      {!isDashboardViewActive ? (
        <div>
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
                return renderThinkStepCard({ item, stepIndex, onShowQuestion });
              })}
              {!items.length ? (
                <Text type="secondary">Preparing supporting questions…</Text>
              ) : null}
            </div>

            {finalAnswer ? (
              <div className="message-container__bot-content ib-think-final">
                <div className="ib-think-final__answer">
                  <div className="message-container__bot-markdown-renderer">
                    <ThinkCitationMarkdown onCitationClick={handleCitationClick}>
                      {finalAnswer}
                    </ThinkCitationMarkdown>
                  </div>
                </div>
              </div>
            ) : null}

            {(isLoadingDataInsight || dataInsightContent) ? (
              <div
                className="message-container__data-insight"
                data-testid="ib-think-final-data-insight"
              >
                {isLoadingDataInsight ? (
                  <div
                    className="ib-data-insight-loading-bar"
                    data-testid="ib-think-final-data-insight-loading"
                  >
                    <LoadingBar handleClick={handleAbortDataInsight} />
                    <ChatTypingText
                      lines={dataInsightTrail}
                      question={userInput}
                      fallback="Preparing your explanation…"
                      active={isLoadingDataInsight}
                    />
                  </div>
                ) : (
                  <div className="message-container__data-insight-body">
                    <ThinkCitationMarkdown onCitationClick={handleCitationClick}>
                      {dataInsightContent}
                    </ThinkCitationMarkdown>
                  </div>
                )}
              </div>
            ) : null}

          </div>
        </div>
      ) : null}
    </div>
  );
};

export default ThinkPlanPanel;
