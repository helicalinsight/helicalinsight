import {
    ConsoleSqlOutlined,
    CopyOutlined,
    DatabaseOutlined,
    ExportOutlined,
    EyeOutlined,
    FileTextOutlined,
    FilterOutlined,
    FullscreenOutlined,
    HistoryOutlined,
    AppstoreOutlined,
    ClusterOutlined,
    InfoCircleOutlined
} from "@ant-design/icons"
import { Button, Drawer, Popover, Tabs, Typography } from 'antd'
import React, { useEffect, useRef, useState } from 'react'
import Markdown from 'react-markdown'
import { useSelector } from "react-redux"
import remarkGfm from 'remark-gfm'
import { changeReport } from "../../../../redux/actions/hreport.actions"
import { exploredSemanticPayload, getReportById } from "../../../bridges/hreport/utils"
import LoadingBar from '../../../common/components/hi-loading-bar'
import ChartIcon from "../../../common/icons/chart-icons"
import "../../components/ib-chart-preferences.scss"
import InstantBITooltip from '../../instant-bi-tooltip-title'
import { getHReportSelectedChartType, getInstantBIAgentSubject } from "../../utils/common-utils"
import { getReportModelInteractions } from "../../utils/report-model-interactions"
import InstantBIResponseMetadata from '../instant-bi-response-metadata'
import AISparklesIcon from './ai-sparkles-icon'
import ChatTypingText from "./chat-typing-text"
import InstantChartView from "./chart-view"
import WorkBehindIcon from "./work-behind-icon"
import { JsonEditorPanel } from "../../../common/json-editor"
import { cloneDeep } from "lodash"
import { useSlashMode, useSlashExploreOnActiveTab } from "../../slash/slash-mode";
import { applyReportModelToActiveHreport, buildChatChartWithoutActivating, buildSemanticDetails } from "../../utils/instant-bi-requests";

const { Text } = Typography

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

const RenderEditingArea = (props) => {
    const [open, setOpen] = useState(false);
    const { editingArea, onClickGoToHreport = () => { }, changeActiveReport = () => { } } = props || {};

    return (
        <>
            <InstantBITooltip title="Explore this chart.">
                <Button
                    size="small"
                    type="text"
                    className="chart-preview-section__go-to-hreport-button"
                    icon={<ExportOutlined />}
                    onClick={onClickGoToHreport}
                />
            </InstantBITooltip>
            {/* Slash hides Change Visualization. Explore stays. */}
            {!props.hideChartActions && <InstantBITooltip title="Change Visualization">
                <Button
                    size="small"
                    type="text"
                    className="chart-preview-section__trigger-button"
                    icon={<ChartIcon name="convert-chart" />}
                    onClick={() => {
                        setOpen(true)
                        changeActiveReport()
                    }}
                />
            </InstantBITooltip>}
            <Drawer
                title={null}
                placement="right"
                width={"25%"}
                visible={open}
                onClose={() => setOpen(false)}
                maskClosable={false}
                keyboard={false}
                destroyOnClose={false}
                className={"ib-chart-preferences-drawer"}
                footer={null}
            >
                {editingArea}
            </Drawer>
        </>
    )
}

const RenderFilters = (props) => {
    const [open, setOpen] = useState(false);
    const { filtersArea = null, changeActiveReport = () => { } } = props || {}
    return (
        <>
            <InstantBITooltip title="Open Filters">
                <Button
                    size="small"
                    type="text"
                    className={props.isOpenMode ? "chart-preview-section__hreport-filter-button-open" : "chart-preview-section__go-to-hreport-filter-button"}
                    icon={<FilterOutlined />}
                    onClick={() => {
                        setOpen(true)
                        changeActiveReport()
                    }}
                />
            </InstantBITooltip>
            <Drawer
                title={null}
                placement="right"
                width={"25%"}
                visible={open}
                onClose={() => setOpen(false)}
                maskClosable={false}
                keyboard={false}
                destroyOnClose={false}
                className={"ib-chart-preferences-drawer"}
                footer={null}
            >
                {filtersArea}
            </Drawer>
        </>
    )
}

// Refactored out of PreviewTab so Slash can show the explanation without rendering the chart.
export const DataInsightBody = ({
    isLoadingDataInsight,
    dataInsightTrail,
    dataInsightContent,
    handleAbortDataInsight,
    hasDataInsightTokens,
    dataInsightTokens,
}) => {
    if (!isLoadingDataInsight && !dataInsightContent) return null;
    return (
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
                    <ChatTypingText
                        lines={dataInsightTrail}
                        fallback="Preparing your explanation…"
                        active={isLoadingDataInsight}
                    />
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
    );
};

const PreviewTab = (props = {}) => {
    const {
        showDataInsightButton,
        handleDataInsight,
        isOpenMode,
        vizDetails,
        resolvedData,
        resolvedVf,
        id,
        chartSettings,
        setHasPreviewError,
        resolvedFullChatResponse,
        fullChatResponse,
        isLoadingDataInsight,
        dataInsightTrail,
        dataInsightContent,
        handleAbortDataInsight,
        hasDataInsightTokens,
        dataInsightTokens,
        dispatch,
        activeReport,
        previewPlaceholder = null,
        isPreviewLoading = false,
        previewActivityTrail = [],
        extraProps = {},
        openModeFilters = false
    } = props || {};
    // Dashboard tiles have no Slash provider, so this stays false there.
    const slashMode = useSlashMode();
    const exploreOnActiveTab = useSlashExploreOnActiveTab();

    const editingAreaRef = useRef(null);
    const filtersAreaRef = useRef(null);
    const [renderCount, setRenderCount] = useState(0);
    const { hreportId, } = fullChatResponse || {};
    const reports = useSelector((state) => state.hreport.present.reports);
    const reportModel = fullChatResponse?.report_model
        || resolvedFullChatResponse?.report_model
        || {};
    let { filters = [], selectedType } = getReportModelInteractions(
        reportModel,
        activeReport?.hreportInteractions?.[hreportId]
    );
    const liveFilters = reports.find((report) => report.id === hreportId)?.filters;
    if (Array.isArray(liveFilters) && liveFilters.length) {
        filters = liveFilters
    }
    if (props.filters) {
        filters = props.filters
    }
    const hasFilters = filters?.length > 0;
    const hasReportModel = Object.keys(reportModel || {}).length > 0;
    const [hostedChartId, setHostedChartId] = useState("");
    const chartBuildStarted = useRef(false);
    const reportModelRef = useRef(reportModel);
    reportModelRef.current = reportModel;
    // A message saved before the chat chart existed still needs a preview. Explore stays the icon already in this pane.
    const chartReportId = hreportId || hostedChartId;

    // Slash does not draw a chart in the message, so this preview report is not created.
    useEffect(() => {
        if (slashMode || hreportId || !exploreOnActiveTab || !hasReportModel || chartBuildStarted.current) return;
        chartBuildStarted.current = true;
        buildChatChartWithoutActivating({ dispatch, reportModel: reportModelRef.current }).then((nextId) => {
            if (nextId) setHostedChartId(nextId);
        });
    }, [dispatch, exploreOnActiveTab, hasReportModel, hreportId, slashMode]);

    const handleGoToHreport = () => {
        // In a Helical Report, Explore draws the chart on the tab that is open now.
        if (exploreOnActiveTab) {
            const model = fullChatResponse?.report_model || resolvedFullChatResponse?.report_model || reportModel;
            applyReportModelToActiveHreport({
                dispatch,
                reportModel: model,
                semanticDetails: buildSemanticDetails({
                    vizDetails,
                    reportModel: model,
                    fullChatResponse,
                    resolvedFullChatResponse,
                    chatId: props.chatId || props.activeReport?.activeChatID,
                    chatSequenceId: props.chatSequenceId,
                    userInput: props.userInput,
                    agent: getInstantBIAgentSubject(props.activeReport),
                }),
            });
            return;
        }
        if (hreportId) {
            const currentReport = getReportById(dispatch, hreportId);
            if (currentReport) {
                const clonedReport = cloneDeep(currentReport);
                // The opened report is a real tab, not the hidden chat preview.
                clonedReport.isInstantReport = false;
                clonedReport.reportInfo = { ...clonedReport.reportInfo, reportName: clonedReport.reportInfo?.reportName || "Untitled 1" };
                // A new window never runs the hreport bridge, so the clone carries the same semantic payload.
                const openedModel = fullChatResponse?.report_model || resolvedFullChatResponse?.report_model || reportModel;
                const openedSemantic = buildSemanticDetails({
                    vizDetails,
                    reportModel: openedModel,
                    fullChatResponse,
                    resolvedFullChatResponse,
                    chatId: props.chatId || props.activeReport?.activeChatID,
                    chatSequenceId: props.chatSequenceId,
                    userInput: props.userInput,
                    agent: getInstantBIAgentSubject(props.activeReport),
                });
                const storedSemantic = exploredSemanticPayload(openedSemantic);
                if (Object.keys(storedSemantic).length) {
                    clonedReport.instantSemantic = storedSemantic;
                }
                // Instant BI Explore writes the title onto the report it opens. The chat chart is not changed.
                // Show is turned on. Text is the chat title, or the model title when the chat has none.
                // The panel reads properties, and the drawn chart reads reportData.properties.
                const modelTitle = openedModel?.viz_model?.properties?.title;
                const exploredTitle = String(
                    vizDetails?.vf_title
                    || (typeof modelTitle === "string" ? modelTitle : "")
                    || ""
                ).trim();
                if (exploredTitle) {
                    const nextTitle = {
                        ...(clonedReport.properties?.title || {}),
                        show: true,
                        value: exploredTitle,
                    };
                    clonedReport.properties = {
                        ...(clonedReport.properties || {}),
                        title: nextTitle,
                    };
                    if (clonedReport.reportData) {
                        clonedReport.reportData = {
                            ...clonedReport.reportData,
                            properties: {
                                ...(clonedReport.reportData.properties || {}),
                                title: nextTitle,
                            },
                        };
                    }
                }
                try {
                    localStorage.setItem('hreport_active_report', JSON.stringify({ activeHreport: clonedReport, fromInstantBI: true }));
                    const newUrl = window.baseURL + `#/helical-report`;
                    window.open(newUrl);
                } catch (error) {
                    if (error.name === 'QuotaExceededError') {
                        console.error('Storage limit reached! Consider clearing localStorage.');
                    } else {
                        console.error('Storage failed:', error);
                    }
                }
            }
        }
    }

    const changeActiveReport = () => {
        dispatch(changeReport({ id: hreportId }));
    }

    // Refactored: Slash returns only the explanation. The chart stays out of this message.
    if (slashMode) {
        return (
            <DataInsightBody
                isLoadingDataInsight={isLoadingDataInsight}
                dataInsightTrail={dataInsightTrail}
                dataInsightContent={dataInsightContent}
                handleAbortDataInsight={handleAbortDataInsight}
                hasDataInsightTokens={hasDataInsightTokens}
                dataInsightTokens={dataInsightTokens}
            />
        );
    }

    // Same preview pane while the chart report is created. The existing Explore icon appears with the chart.
    if (!chartReportId) {
        const chartIsBuilding = exploreOnActiveTab && hasReportModel;
        return (
            <div className="chart-preview-section chart-preview-section--empty">
                {(isPreviewLoading || chartIsBuilding) ? (
                    <WorkBehindIcon
                        active
                        lines={previewActivityTrail}
                        fallback="Preparing visualization…"
                        title="Preview"
                        testId="ib-think-preview-loading"
                    />
                ) : (
                    previewPlaceholder || (
                        <Text type="secondary">Select Preview to load the visualization.</Text>
                    )
                )}
            </div>
        );
    }

    // Instant BI chart preview. Slash returns above with only the explanation.
    const chartResponse = chartReportId === hreportId
        ? fullChatResponse
        : { ...fullChatResponse, hreportId: chartReportId };
    return (
        <React.Fragment>
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
                {chartReportId && hasFilters && (
                    <RenderFilters filtersArea={filtersAreaRef.current} changeActiveReport={changeActiveReport} isOpenMode={isOpenMode} />
                )}
                {chartReportId && !isOpenMode && (
                    <RenderEditingArea editingArea={editingAreaRef.current} onClickGoToHreport={handleGoToHreport} changeActiveReport={changeActiveReport} />
                )}
                <InstantChartView
                    compact
                    data={resolvedData}
                    vf={resolvedVf}
                    id={id}
                    chartName={vizDetails?.chart_name}
                    chartSettings={chartSettings}
                    plotConfig={vizDetails?.plot_config}
                    className="chart-wrapper--message"
                    onPreviewError={setHasPreviewError}
                    backendError={resolvedFullChatResponse?.error}
                    fullChatResponse={chartResponse}
                    renderEditingArea={(editingArea) => {
                        editingAreaRef.current = editingArea
                        setRenderCount(renderCount + 1)
                    }}
                    renderFilters={(filtersArea) => {
                        filtersAreaRef.current = filtersArea
                        setRenderCount(renderCount + 1)
                    }}
                    isOpenMode={isOpenMode}
                    vizType={getHReportSelectedChartType(chartReportId, reports, selectedType)}
                    dashboardMode={props.dashboardMode}
                    extraProps={extraProps}
                    openModeFilters={openModeFilters}
                />
            </div>
            <DataInsightBody
                isLoadingDataInsight={isLoadingDataInsight}
                dataInsightTrail={dataInsightTrail}
                dataInsightContent={dataInsightContent}
                handleAbortDataInsight={handleAbortDataInsight}
                hasDataInsightTokens={hasDataInsightTokens}
                dataInsightTokens={dataInsightTokens}
            />
        </React.Fragment>
    )
}

const SemanticTab = (props = {}) => {
    const {
        sqlDetails,
        vizDetails,
        tokenUsage,
    } = props || {}
    return (
        <div className="json-data-viewer">
            <InstantBIResponseMetadata
                sqlDetails={sqlDetails}
                vizDetails={vizDetails}
                tokenUsage={tokenUsage}
            />
        </div>
    )
}

const JsonTab = ({ value, emptyLabel = "No data available." }) => {
    const hasValue = value != null && !(typeof value === "object" && !Object.keys(value || {}).length);
    if (!hasValue) {
        return <Text type="secondary">{emptyLabel}</Text>;
    }
    const text = typeof value === "string" ? value : JSON.stringify(value, null, 2);
    return (
        <div style={{ height: 500 }} className="ib-json-tab">
            <JsonEditorPanel value={text} active={false} />
        </div>
    );
};

const SQLTab = (props = {}) => {
    const {
        resolvedSql,
        handleCopySQL,
        fullChatResponse = {},
        showDashboardTab = false,
        showActivityDetailsTab = false,
        dashboardModel = null,
        llmActivityDetails = null,
    } = props || {}
    const [activeTab, setActiveTab] = useState("sql");
    const jsonViews = {
        spec: {
            value: { report_model: fullChatResponse?.report_model || {} },
            emptyLabel: "No report model available.",
        },
        dashboard: {
            value: dashboardModel,
            emptyLabel: "No dashboard model available.",
        },
        activity: {
            value: llmActivityDetails,
            emptyLabel: "No LLM activity details available.",
        },
    };

    function displaySQL() {
        return (
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
        )
    }

    return (
        <div>
            <InstantBITooltip title="SQL">
                <Button
                    size="small"
                    type="text"
                    className={`chart-preview-section__sql-button ${activeTab === "sql" ? " is-active" : ""}`}
                    icon={<ConsoleSqlOutlined />}
                    onClick={() => setActiveTab("sql")}
                />
            </InstantBITooltip>
            <InstantBITooltip title="Report Model">
                <Button
                    size="small"
                    type="text"
                    className={`chart-preview-section__spec-button ${activeTab === "spec" ? " is-active" : ""}`}
                    icon={<FileTextOutlined />}
                    onClick={() => setActiveTab("spec")}
                />
            </InstantBITooltip>
            {showDashboardTab ? (
                <InstantBITooltip title="Dashboard Model">
                    <Button
                        size="small"
                        type="text"
                        className={`chart-preview-section__spec-button ${activeTab === "dashboard" ? " is-active" : ""}`}
                        icon={<AppstoreOutlined />}
                        onClick={() => setActiveTab("dashboard")}
                    />
                </InstantBITooltip>
            ) : null}
            {showActivityDetailsTab ? (
                <InstantBITooltip title="Activity Details">
                    <Button
                        size="small"
                        type="text"
                        className={`chart-preview-section__spec-button ${activeTab === "activity" ? " is-active" : ""}`}
                        icon={<ClusterOutlined />}
                        onClick={() => setActiveTab("activity")}
                    />
                </InstantBITooltip>
            ) : null}
            {activeTab === "sql" && displaySQL()}
            {jsonViews[activeTab] ? <JsonTab {...jsonViews[activeTab]} /> : null}
        </div>
    )
}

const WorkingTab = (props = {}) => {
    const {
        workingLines = [],
        workingActive = false,
        workingQuestion = "",
        workingFallback = "Working on this…",
    } = props || {};
    const lines = Array.isArray(workingLines) ? workingLines.filter(Boolean) : [];
    if (!lines.length && !workingActive) {
        return <Text type="secondary">No workings recorded for this step.</Text>;
    }
    return (
        <div className="ib-working-tab" data-testid="ib-working-tab">
            <div className="ib-working-tab__heading">Working</div>
            <div className="ib-working-tab__body">
                {lines.length ? (
                    lines.map((line, index) => {
                        const text = String(line || "").trim();
                        if (!text) return null;
                        const isLast = index === lines.length - 1;
                        if (workingActive && isLast) {
                            return (
                                <p key={`${index}-${text.slice(0, 24)}`} className="ib-working-tab__p">
                                    <ChatTypingText
                                        lines={[text]}
                                        question={workingQuestion}
                                        fallback={workingFallback}
                                        active
                                    />
                                </p>
                            );
                        }
                        return (
                            <p key={`${index}-${text.slice(0, 24)}`} className="ib-working-tab__p">
                                {text}
                            </p>
                        );
                    })
                ) : (
                    <p className="ib-working-tab__p ib-working-tab__p--muted">{workingFallback}</p>
                )}
            </div>
        </div>
    );
};

const getTitle = (icon, title = "") => {
    return (<InstantBITooltip title={title}>{icon}</InstantBITooltip>)
}

const ChatTabs = (props = {}) => {
    const {
        hasMessage,
        showMaximizeButton,
        showDataInsightButton,
        setIsMaximized,
        defaultTab,
        semanticLabel = "Semantic",
        showWorkingTab = false,
        onTabChange,
        onPreviewActivate,
        previewFirst = true,
        dispatch,
        fullChatResponse,
        resolvedFullChatResponse,
        handleDataInsight,
        dataInsightContent,
        isLoadingDataInsight,
    } = props || {};
    // Slash replaces Preview and Maximize with Explore and Explain, and does not show the chart here.
    const slashMode = useSlashMode();
    const exploreOnActiveTab = useSlashExploreOnActiveTab();
    // No Preview pane in Slash, so this key leaves the chart area empty until another icon is chosen.
    const initialTab = slashMode ? "preview" : (defaultTab || (previewFirst ? "preview" : "semantic"));
    const [activeTab, setActiveTab] = useState(initialTab);

    useEffect(() => {
        if (slashMode) return;
        if (defaultTab) {
            setActiveTab(defaultTab);
        }
    }, [defaultTab, slashMode]);

    // Hosted Slash draws a Fast answer on the open report tab. The selected icon stays put.
    const openExplore = () => {
        const model = fullChatResponse?.report_model || resolvedFullChatResponse?.report_model;
        if (exploreOnActiveTab) {
            applyReportModelToActiveHreport({
                dispatch,
                reportModel: model,
                semanticDetails: buildSemanticDetails({
                    vizDetails: props.vizDetails,
                    reportModel: model,
                    fullChatResponse,
                    resolvedFullChatResponse,
                    chatId: props.chatId || props.activeReport?.activeChatID,
                    chatSequenceId: props.chatSequenceId,
                    userInput: props.userInput,
                    agent: getInstantBIAgentSubject(props.activeReport),
                }),
            });
        }
    };

    const handleTabChange = (active) => {
        if (active === "maximize") {
            setIsMaximized?.(true);
            return;
        }
        // Explore draws on the open report and leaves the selected icon tab unchanged.
        if (active === "explore") {
            openExplore();
            return;
        }
        if (active === "explain") {
            if (!dataInsightContent && !isLoadingDataInsight) {
                handleDataInsight?.();
            }
            // Slash already shows the explanation under the question. The icon only loads it.
            if (slashMode) return;
        }
        setActiveTab(active);
        onTabChange?.(active);
        if (active === "preview") {
            onPreviewActivate?.();
        }
    };

    const semanticPane = (
        <Tabs.TabPane tab={getTitle(<DatabaseOutlined />, semanticLabel)} key="semantic">
            <SemanticTab {...props} />
        </Tabs.TabPane>
    );
    // Refactored the Slash icon row: Preview and Maximize are removed, Explore and Explain take their place.
    const previewPane = !slashMode ? (
        <Tabs.TabPane tab={getTitle(<EyeOutlined />, "Preview")} key="preview">
            <PreviewTab {...props} setActiveTab={setActiveTab} />
        </Tabs.TabPane>
    ) : null;
    const explorePane = slashMode ? (
        <Tabs.TabPane tab={getTitle(<ExportOutlined />, "Explore this chart.")} key="explore">
            {null}
        </Tabs.TabPane>
    ) : null;
    const explainPane = slashMode && showDataInsightButton ? (
        <Tabs.TabPane tab={getTitle(<AISparklesIcon />, "Explain this chart")} key="explain">
            {null}
        </Tabs.TabPane>
    ) : null;
    const sqlPane = (
        <Tabs.TabPane tab={getTitle(<ConsoleSqlOutlined />, "SQL")} key="sql">
            <SQLTab {...props} />
        </Tabs.TabPane>
    );
    const workingPane = showWorkingTab ? (
        <Tabs.TabPane tab={getTitle(<HistoryOutlined />, "Working")} key="working">
            <WorkingTab {...props} />
        </Tabs.TabPane>
    ) : null;

    return (
        <Tabs
            className="ib-icon-tabs"
            activeKey={activeTab}
            onChange={handleTabChange}
        >
            {
                (!slashMode && hasMessage && showMaximizeButton) &&
                <Tabs.TabPane tab={getTitle(<FullscreenOutlined />, "Maximize")} key="maximize">
                    {null}
                </Tabs.TabPane>
            }
            {explorePane}
            {explainPane}
            {previewFirst ? (
                <>
                    {previewPane}
                    {semanticPane}
                    {sqlPane}
                    {workingPane}
                </>
            ) : (
                <>
                    {semanticPane}
                    {previewPane}
                    {sqlPane}
                    {workingPane}
                </>
            )}
        </Tabs>
    )
}

export { JsonTab, SemanticTab, PreviewTab, SQLTab, WorkingTab };
export default ChatTabs
