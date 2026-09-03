import {
  InfoCircleOutlined,
  ReloadOutlined
} from "@ant-design/icons"
import { Modal, Popover, Row, Space, Typography } from 'antd'
import React, { useEffect, useRef, useState } from 'react'
import Markdown from 'react-markdown'
import remarkGfm from 'remark-gfm'
import LoadingBar from '../../../common/components/hi-loading-bar'
import HIIcon from '../../../common/icons/hi-icons'
import notify from '../../../hi-notifications/notify'
import InstantBITooltip from '../../instant-bi-tooltip-title'
import { cleanSQL, getInstantBIAgentSubject } from '../../utils/common-utils'
import { convertInstantBIChart, loadInstantBIDataInsight, shouldUseLoadChatPayloadForInsight } from '../../utils/instant-bi-requests'
import IBSpace from '../ib-space/ib-space'
import InstantChartView from "./chart-view"
import ChatScreenRecommendationSkeleton from './chat-screen-skeleton'
import "./chat-screen.scss"
import ChatTabs from "./chat-tabs"

const { Text } = Typography

const LOADED_CHAT_SOURCES = ["play-button", "auto-load", "scroll-load"];

export const DataInsightTokenUsage = ({ tokens = {} }) => {
  const [open, setOpen] = useState(false);
  const entries = Object.entries(tokens);

  useEffect(() => {
    if (!open) return;
    const scrollEl = document.getElementById('scrollableDiv');
    if (!scrollEl) return;
    const close = () => setOpen(false);
    scrollEl.addEventListener('scroll', close, { passive: true });
    return () => scrollEl.removeEventListener('scroll', close);
  }, [open]);

  if (!entries.length) return null;

  const content = (
    <div
      className="message-container__data-insight-token-popover"
      onClick={(e) => e.stopPropagation()}
    >
      {entries.map(([key, value]) => (
        <div key={key} className="message-container__data-insight-token-row">
          <span className="message-container__data-insight-token-label">
            {key.replace(/_/g, " ")}
          </span>
          <span className="message-container__data-insight-token-value">
            {value ?? "—"}
          </span>
        </div>
      ))}
    </div>
  );

  return (
    <Popover
      content={content}
      trigger="click"
      placement="topRight"
      open={open}
      onOpenChange={setOpen}
      overlayClassName="message-container__data-insight-token-popover-overlay"
    >
      <InfoCircleOutlined className="message-container__data-insight-info-icon" />
    </Popover>
  );
};

const MessageLayout = ({ chatItem = {}, index, ...rest }) => {
  const {
    vf = "",
    data = [],
    id = "",
    metadata = [],
    sql = "",
    isFullWidth,
    isOpenMode,
    isEditMode,
    fullChatResponse = {},
    handleScroll = () => { },
    activeReport = {},
    dispatch,
    userInput = "",
    chatSequenceId,
    scrollableRootRef,
    onRequestChatLoad,
    onRetryLoad = () => { },
    loadingChatSequenceId,
    onAbortChatLoad = () => { },
    skippedSequenceIds = [],
    abortedSequenceIds = [],
    dataInsight: messageDataInsight = "",
    dataInsightTokenUsage = {},
    hreportLoading
  } = rest || {}
  const [activeTab, setActiveTab] = useState("preview"); // preview | data | sql
  const [isMaximized, setIsMaximized] = useState(false);
  const [hasPreviewError, setHasPreviewError] = useState(false);
  const [isLoadingDataInsight, setIsLoadingDataInsight] = useState(false);
  const [isConvertingChart, setIsConvertingChart] = useState(false);
  const [vfEditorLaunch, setVfEditorLaunch] = useState(null); // { code } | null
  const messageRef = useRef(null);
  const dataInsightApiRef = useRef(null);
  const dataInsightAbortedRef = useRef(false);
  const Notify = notify(dispatch);
  const {
    reportInfo = {},
    metadata: reportMetadata = {},
    id: reportId,
    activeChatID,
    loadedChatResponses = {},
    loadedChatResponseSources = {},
  } = activeReport || {};
  const agentSubject = getInstantBIAgentSubject(activeReport) || {};
  const agentFile = agentSubject.file;
  const agentDir = agentSubject.dir;
  const dynamicFileName = reportInfo?.uuid || (reportInfo?.reportName && `${reportInfo.reportName}.instant`);
  const loadedChatResponse = loadedChatResponses?.[chatSequenceId] || null;
  const loadedChatResponseSource = loadedChatResponseSources?.[chatSequenceId];
  const isLoadedChatResponse = LOADED_CHAT_SOURCES.includes(loadedChatResponseSource);
  const effectiveLoadedChatResponse = isLoadedChatResponse ? loadedChatResponse : null;
  const {
    viz: loadedViz = {},
    sql: loadedSqlDetails = {},
    summary: loadedSummary = {},
    data: loadedData = [],
    metadata: loadedMetadata = [],
  } = effectiveLoadedChatResponse || {};
  const resolvedVf = loadedViz?.vf_template ? atob(loadedViz.vf_template) : vf;
  const resolvedSql = loadedSqlDetails?.raw_sql || sql?.raw_sql || sql || "";
  const resolvedText = loadedSummary?.insight || chatItem?.text || "";
  const resolvedData = effectiveLoadedChatResponse
    ? (Array.isArray(loadedData) ? loadedData : [])
    : data;
  const resolvedMetadata = effectiveLoadedChatResponse
    ? (Array.isArray(loadedMetadata) ? loadedMetadata : [])
    : metadata;
  const resolvedFullChatResponse = effectiveLoadedChatResponse || fullChatResponse;
  const sqlDetails = resolvedFullChatResponse?.sql || {}
  const vizDetails = resolvedFullChatResponse?.viz || {}
  const chartSettings = vizDetails?.settings || {}
  const similarChart = vizDetails?.similar_chart || []
  const tokenUsage = resolvedFullChatResponse?.token_usage || {}
  const dataInsightContent =
    resolvedFullChatResponse?.data_insight?.insight || messageDataInsight || "";
  const dataInsightTokens =
    resolvedFullChatResponse?.data_insight?.token_usage || dataInsightTokenUsage || {};
  const hasDataInsightTokens = Object.keys(dataInsightTokens).length > 0;
  const hasInlineChatResponse =
    fullChatResponse && Object.keys(fullChatResponse).length > 0;
  const isFailedSequence = skippedSequenceIds.includes(chatSequenceId);
  const isAbortedSequence = abortedSequenceIds.includes(chatSequenceId);
  const isPendingScrollLoad = !chatItem.isUser && (isOpenMode || isEditMode) && chatItem?.needsLoadChat === true && !effectiveLoadedChatResponse && !hasInlineChatResponse && !isFailedSequence && !isAbortedSequence;
  const hasMessage = Boolean(resolvedText?.trim());
  const isLoadingChat = loadingChatSequenceId === chatSequenceId;
  const hasPreviewContent =
    Boolean(resolvedVf?.trim()) ||
    (Array.isArray(resolvedData) && resolvedData.length > 0);
  const canEditPreferences = !isOpenMode && !chatItem?.error && !isFailedSequence && !isPendingScrollLoad && !isLoadingChat;
  const showMaximizeButton = activeTab === "preview" && !chatItem?.error && !isFailedSequence && !hasPreviewError && !isLoadingChat;
  const showDataInsightButton = activeTab === "preview" && !chatItem?.error && !hasPreviewError && !isFailedSequence && !isAbortedSequence && !isPendingScrollLoad && !isLoadingChat && !isLoadingDataInsight;

  useEffect(() => {
    setHasPreviewError(false);
  }, [resolvedVf, id]);

  useEffect(() => {
    if (hasPreviewError) {
      setIsMaximized(false);
    }
  }, [hasPreviewError]);

  // useEffect(() => {
  //   if (!isPendingScrollLoad || !onRequestChatLoad) return;

  //   const scrollRoot = scrollableRootRef?.current;
  //   const messageNode = messageRef.current;
  //   if (!messageNode) return;
  //   if (!isOpenMode && !scrollRoot) return;

  //   const observer = new IntersectionObserver(
  //     (entries) => {
  //       if (entries.some((entry) => entry.isIntersecting)) {
  //         onRequestChatLoad(chatSequenceId);
  //       }
  //     },
  //     { root: isOpenMode ? null : scrollRoot, threshold: 0.2 }
  //   );

  //   observer.observe(messageNode);
  //   return () => observer.disconnect();
  // }, [
  //   isPendingScrollLoad,
  //   isOpenMode,
  //   chatSequenceId,
  //   onRequestChatLoad,
  //   scrollableRootRef,
  // ]);

  useEffect(() => {
    return () => {
      if (dataInsightApiRef.current) {
        dataInsightAbortedRef.current = true;
        dataInsightApiRef.current.abort();
        dataInsightApiRef.current = null;
      }
    };
  }, []);

  const onChange = (e) => {
    setActiveTab(e.target.value);
    handleScroll()
  };

  const handleAbortDataInsight = () => {
    dataInsightAbortedRef.current = true;
    dataInsightApiRef.current?.abort();
    dataInsightApiRef.current = null;
    setIsLoadingDataInsight(false);
  };

  const handleSelectSimilarChart = (chartType) => {
    if (
      !chartType ||
      !dispatch ||
      !reportId ||
      !chatSequenceId ||
      !activeChatID ||
      isConvertingChart
    ) {
      return;
    }
    const currentName = vizDetails?.chart_name || "";
    if (
      String(currentName).toLowerCase().replace(/\s+/g, "_") ===
      String(chartType).toLowerCase().replace(/\s+/g, "_")
    ) {
      return;
    }

    const vfTemplate =
      vizDetails?.vf_template ||
      loadedViz?.vf_template ||
      (resolvedVf ? btoa(resolvedVf) : "");

    if (!vfTemplate) {
      Notify.error({
        type: "Frontend",
        message: "Required convert chart data is missing.",
      });
      return;
    }

    setIsConvertingChart(true);
    convertInstantBIChart({
      dispatch,
      reportId,
      chatSequenceId,
      chatId: activeChatID,
      vfTemplate,
      selectedChart: chartType,
      Notify,
      onComplete: ({ openVfEditor, vfCode } = {}) => {
        setIsConvertingChart(false);
        if (openVfEditor) setVfEditorLaunch({ code: vfCode || "" });
      },
    });
  };

  const handleDataInsight = () => {
    if (isLoadingDataInsight) return;
    dataInsightAbortedRef.current = false;
    setIsLoadingDataInsight(true);
    dataInsightApiRef.current = loadInstantBIDataInsight({
      dispatch,
      reportId,
      chatSequenceId,
      userInput,
      location: reportInfo?.location,
      fileName: dynamicFileName,
      chatId: activeChatID,
      agent: agentSubject,
      useLoadChatPayload: shouldUseLoadChatPayloadForInsight({
        isOpenMode,
        isEditMode,
        needsLoadChat: chatItem?.needsLoadChat,
        persistedInFile: chatItem?.persistedInFile,
      }),
      existingChatResponse: effectiveLoadedChatResponse || {
        ...fullChatResponse,
        data,
        metadata,
      } || {},
      Notify,
      abortedRef: dataInsightAbortedRef,
      onComplete: () => {
        setIsLoadingDataInsight(false);
        dataInsightApiRef.current = null;
      },
    });
  };

  const handleCopySQL = async () => {
    try {
      await navigator.clipboard.writeText(cleanSQL(resolvedSql));
      Notify.success({ type: "Frontend", message: "SQL copied !" });
    } catch (err) { }
  };

  const isLoadErrorState = isAbortedSequence || isFailedSequence;

  const renderLoadError = (message) => (
    <Space align="center" className="message-container__load-error" size={4}>
      <Text type="secondary">{message}</Text>
      <InstantBITooltip title="Retry">
        <ReloadOutlined
          className="copy-chat-response-icon"
          data-testid="retry-chat-load"
          onClick={() => onRetryLoad(chatSequenceId)}
        />
      </InstantBITooltip>
    </Space>
  );

  const messageBubbleClassName = [
    "message-container__chat-message",
    chatItem.isUser
      ? "message-container__chat-message-user"
      : "message-container__chat-message-bot",
    isFullWidth ? "message-container__chat-message--constrained" : "",
    chatItem?.error ? "message-container__chat-message--error" : "",
    isLoadErrorState ? "message-container__chat-message--load-error" : "",
  ]
    .filter(Boolean)
    .join(" ");

  const tabsProps = {
    hasMessage,
    showMaximizeButton,
    isMaximized,
    setIsMaximized,
    showDataInsightButton,
    isOpenMode,
    similarChart,
    vizDetails,
    canEditPreferences,
    isConvertingChart,
    vfEditorLaunch,
    resolvedVf,
    handleSelectSimilarChart,
    reportId,
    chatSequenceId,
    resolvedData,
    id,
    chartSettings,
    setHasPreviewError,
    resolvedFullChatResponse,
    fullChatResponse: { ...fullChatResponse, hreportLoading },
    isLoadingDataInsight,
    dataInsightContent,
    hasDataInsightTokens,
    dataInsightTokens,
    resolvedSql,
    handleAbortDataInsight,
    handleCopySQL,
    sqlDetails,
    vizDetails,
    tokenUsage,
    dispatch,
    handleDataInsight,
    activeReport
  }

  return (
    <div className="message-container" key={index} ref={messageRef}>
      <IBSpace space="8" className="message-container__row">
        {!chatItem.isUser && (
          <div data-testid="bot-message" className="message-container__avatar">
            <HIIcon className="ib-chat-icon" name="hi-instant-bi-svg" />
          </div>
        )}
        <IBSpace
          stack="vertical"
          alignItem={chatItem.isUser ? "end" : "start"}
          className="message-container__body"
          data-testid="message-id"
        >
          <Space
            className={messageBubbleClassName}
          >
            {chatItem.isUser ? (
              <Text>{chatItem.text}</Text>
            ) : isAbortedSequence ? (
              renderLoadError("Response aborted")
            ) : isFailedSequence ? (
              renderLoadError("Unable to load response")
            ) : isPendingScrollLoad ? (
              isLoadingChat ? (
                <div className="ib-load-chat-loading-bar" data-testid="ib-load-chat-loading-bar">
                  <LoadingBar handleClick={onAbortChatLoad} />
                  <ChatScreenRecommendationSkeleton />
                </div>
              ) : (
                <Text type="secondary">Just a moment…</Text>
              )
            ) : (
              <div className="message-container__bot-content" >
                <div className="message-container__bot-markdown-renderer">
                  <Markdown remarkPlugins={[remarkGfm]}>
                    {resolvedText}
                  </Markdown>
                </div>
                <div
                  className={`chart-container${hasMessage ? " chart-container--with-tabs" : ""
                    }${hasPreviewError && activeTab === "preview"
                      ? " chart-container--preview-error"
                      : ""
                    }`}
                >
                  <Row justify={"end"} className="instant-chart-tabs-row">
                    <ChatTabs {...tabsProps} />
                  </Row>
                  <Modal
                    title="Preview"
                    open={isMaximized && showMaximizeButton}
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
                      id={id}
                      chartName={vizDetails?.chart_name}
                      chartSettings={chartSettings}
                      plotConfig={vizDetails?.plot_config}
                      className="chart-wrapper--modal"
                      fullChatResponse={fullChatResponse}
                    />
                  </Modal>
                </div>
              </div>
            )}
          </Space>
          <Space className="message-container__date-header">
            {chatItem.time}
          </Space>
        </IBSpace>
      </IBSpace>
    </div>
  );
}

// Don't re-render completed mesgs
export default React.memo(MessageLayout, (prev, next) => {
  const seq = prev.chatSequenceId;
  return (
    prev.chatItem === next.chatItem &&
    prev.isFullWidth === next.isFullWidth &&
    prev.isOpenMode === next.isOpenMode &&
    prev.isEditMode === next.isEditMode &&
    (prev.loadingChatSequenceId === seq) === (next.loadingChatSequenceId === seq) &&
    prev.skippedSequenceIds?.includes(seq) === next.skippedSequenceIds?.includes(seq) &&
    prev.abortedSequenceIds?.includes(seq) === next.abortedSequenceIds?.includes(seq) &&
    prev.activeReport?.loadedChatResponses?.[seq] ===
    next.activeReport?.loadedChatResponses?.[seq] &&
    prev.activeReport?.loadedChatResponseSources?.[seq] ===
    next.activeReport?.loadedChatResponseSources?.[seq]
  );
});