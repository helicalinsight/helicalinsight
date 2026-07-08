import { Row, Space, Typography, Modal, Popover } from 'antd'
import { useEffect, useRef, useState } from 'react'
import Markdown from 'react-markdown'
import remarkGfm from 'remark-gfm'
import LoadingBar from '../../../common/components/hi-loading-bar'
import HIIcon from '../../../common/icons/hi-icons'
import ChatScreenRecommendationSkeleton from './chat-screen-skeleton'
import IBSpace from '../ib-space/ib-space'
import "./chat-screen.scss"
import ChatBotMessageFlow from './chatbot-message-flow'
import AISparklesIcon from './ai-sparkles-icon'
import {
  EyeOutlined,
  TableOutlined,
  DatabaseOutlined,
  ConsoleSqlOutlined,
  CopyOutlined,
  FullscreenOutlined,
  FullscreenExitOutlined,
  ReloadOutlined,
  InfoCircleOutlined,
} from "@ant-design/icons";
import InstantBITooltip from '../../instant-bi-tooltip-title';
import notify from '../../../hi-notifications/notify'
import { ChartView, cleanSQL, getInstantBIAgentSubject, tabItems } from '../../utils/common-utils'
import CommonMarkdownTable from '../../utils/common-markdown-table'
import InstantBIResponseMetadata from '../instant-bi-response-metadata'
import { loadInstantBIDataInsight, shouldUseLoadChatPayloadForInsight } from '../../utils/instant-bi-requests'

const { Text } = Typography

const LOADED_CHAT_SOURCES = ["play-button", "auto-load", "scroll-load"];

const DataInsightTokenUsage = ({ tokens = {} }) => {
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
      onRetryLoad = () => {},
      loadingChatSequenceId,
      onAbortChatLoad = () => { },
      skippedSequenceIds = [],
      abortedSequenceIds = [],
      dataInsight: messageDataInsight = "",
      dataInsightTokenUsage = {},
    } = rest || {}
    const [activeTab, setActiveTab] = useState("preview"); // preview | data | sql
    const [isMaximized, setIsMaximized] = useState(false);
    const [hasPreviewError, setHasPreviewError] = useState(false);
    const [isLoadingDataInsight, setIsLoadingDataInsight] = useState(false);
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
    const resolvedSql = loadedSqlDetails?.raw_sql || sql;
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
    const isPendingScrollLoad =
      !chatItem.isUser &&
      (isOpenMode || isEditMode) &&
      chatItem?.needsLoadChat === true &&
      !effectiveLoadedChatResponse &&
      !hasInlineChatResponse &&
      !isFailedSequence &&
      !isAbortedSequence;
    const hasMessage = Boolean(resolvedText?.trim());
    const isLoadingChat = loadingChatSequenceId === chatSequenceId;
    const hasPreviewContent =
      Boolean(resolvedVf?.trim()) ||
      (Array.isArray(resolvedData) && resolvedData.length > 0);
    const hasValidVf = Boolean(resolvedVf?.trim());
    const showMaximizeButton =
      activeTab === "preview" &&
      hasValidVf &&
      !chatItem?.error &&
      !isFailedSequence &&
      !hasPreviewError &&
      !isLoadingChat;
    const showDataInsightButton =
      activeTab === "preview" &&
      hasValidVf &&
      !chatItem?.error &&
      !hasPreviewError &&
      !isFailedSequence &&
      !isAbortedSequence &&
      !isPendingScrollLoad &&
      !isLoadingChat &&
      !isLoadingDataInsight;

    useEffect(() => {
      setHasPreviewError(false);
    }, [resolvedVf, id]);

    useEffect(() => {
      if (hasPreviewError) {
        setIsMaximized(false);
      }
    }, [hasPreviewError]);

    useEffect(() => {
      if (!isPendingScrollLoad || !onRequestChatLoad) return;

      const scrollRoot = scrollableRootRef?.current;
      const messageNode = messageRef.current;
      if (!messageNode) return;
      if (!isOpenMode && !scrollRoot) return;

      const observer = new IntersectionObserver(
        (entries) => {
          if (entries.some((entry) => entry.isIntersecting)) {
            onRequestChatLoad(chatSequenceId);
          }
        },
        { root: isOpenMode ? null : scrollRoot, threshold: 0.2 }
      );

      observer.observe(messageNode);
      return () => observer.disconnect();
    }, [
      isPendingScrollLoad,
      isOpenMode,
      chatSequenceId,
      onRequestChatLoad,
      scrollableRootRef,
    ]);

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
     } catch (err) {}
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
                <div className="message-container__bot-content">
                  <Markdown remarkPlugins={[remarkGfm]}>
                    {resolvedText}
                  </Markdown>
                  <div
                    className={`chart-container${
                      hasMessage ? " chart-container--with-tabs" : ""
                    }${
                      hasPreviewError && activeTab === "preview"
                        ? " chart-container--preview-error"
                        : ""
                    }`}
                  >
                    <Row justify={"end"}>
                     {hasMessage && (
                      <div className="icon-tabs-container">
                        {showMaximizeButton && (
                          <InstantBITooltip
                            title={isMaximized ? "Minimize" : "Maximize"}
                          >
                            <button
                              type="button"
                              className="icon-tab-btn"
                              onClick={() => setIsMaximized(!isMaximized)}
                            >
                              {isMaximized ? (
                                <FullscreenExitOutlined />
                              ) : (
                                <FullscreenOutlined />
                              )}
                            </button>
                          </InstantBITooltip>
                        )}
                        {tabItems.map((item) => (
                          <InstantBITooltip key={item.key} title={item.title}>
                            <button
                              type="button"
                              className={`icon-tab-btn ${
                                activeTab === item.key ? "active" : ""
                              }`}
                              onClick={() => {
                                setActiveTab(item.key);
                                handleScroll();
                              }}
                            >
                              {item.icon}
                            </button>
                          </InstantBITooltip>
                        ))}
                      </div>
                     )}
                    </Row>
                    {activeTab === "preview" ? (
                      <>
                        <div className="chart-preview-section">
                          {showDataInsightButton && (
                            <InstantBITooltip title="Explain this chart">
                              <button
                                type="button"
                                className="chart-preview-section__data-insight-fab"
                                data-testid="data-insight-play-btn"
                                onClick={handleDataInsight}
                              >
                                <AISparklesIcon />
                              </button>
                            </InstantBITooltip>
                          )}
                          <ChartView
                            compact
                            data={resolvedData}
                            vf={resolvedVf}
                            id={`${id}-${chatItem.time || ""}`}
                            chartName={vizDetails?.chart_name}
                            className="chart-wrapper--message"
                            onPreviewError={setHasPreviewError}
                            backendError={resolvedFullChatResponse?.error}
                          />
                        </div>
                        {(isLoadingDataInsight || dataInsightContent) && (
                          <div
                            className="message-container__data-insight"
                            data-testid="data-insight-section"
                          >
                            {isLoadingDataInsight ? (
                              <div
                                className="ib-data-insight-loading-bar"
                                data-testid="ib-data-insight-loading-bar"
                              >
                                <LoadingBar handleClick={handleAbortDataInsight} />
                                <Text type="secondary">Preparing your explanation…</Text>
                              </div>
                            ) : (
                              <div className="message-container__data-insight-body">
                                <Markdown remarkPlugins={[remarkGfm]}>
                                  {dataInsightContent}
                                </Markdown>
                                {hasDataInsightTokens && (
                                  <DataInsightTokenUsage tokens={dataInsightTokens} />
                                )}
                              </div>
                            )}
                          </div>
                        )}
                      </>
                    ) : activeTab === "sql" ? (
                      <div className="sql-view-container">
                        {resolvedSql && (
                          <div className="sql-copy-btn">
                            <InstantBITooltip title="Copy SQL">
                              <CopyOutlined onClick={handleCopySQL} />
                            </InstantBITooltip>
                          </div>
                        )}
                        <Markdown remarkPlugins={[remarkGfm]}>
                          {resolvedSql}
                        </Markdown>
                      </div>
                    ) : (
                      <div className="json-data-viewer">
                        {activeTab === "metadata" ? (
                          <InstantBIResponseMetadata
                            sqlDetails={sqlDetails}
                            vizDetails={vizDetails}
                            tokenUsage={tokenUsage}
                          />
                        ) : (
                          <CommonMarkdownTable data={resolvedData || []} />
                        )}
                      </div>
                    )}

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
                      <ChartView
                        data={resolvedData}
                        vf={resolvedVf}
                        id={id}
                        chartName={vizDetails?.chart_name}
                        className="chart-wrapper--modal"
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

export default MessageLayout