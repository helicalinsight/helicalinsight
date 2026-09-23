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
import { getReportById } from "../../../bridges/hreport/utils"
import LoadingBar from '../../../common/components/hi-loading-bar'
import ChartIcon from "../../../common/icons/chart-icons"
import "../../components/ib-chart-preferences.scss"
import InstantBITooltip from '../../instant-bi-tooltip-title'
import { getHReportSelectedChartType } from "../../utils/common-utils"
import InstantBIResponseMetadata from '../instant-bi-response-metadata'
import AISparklesIcon from './ai-sparkles-icon'
import ChatTypingText from "./chat-typing-text"
import InstantChartView from "./chart-view"
import WorkBehindIcon from "./work-behind-icon"
import { JsonEditorPanel } from "../../../common/json-editor"
import { cloneDeep } from "lodash"

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
            <InstantBITooltip title="Change Visualization">
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
                    className="chart-preview-section__go-to-hreport-filter-button"
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

const PreviewTab = (props = {}) => {
    const {
        showDataInsightButton,
        handleDataInsight,
        isOpenMode,
        vizDetails,
        isConvertingChart,
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
    } = props || {};

    const editingAreaRef = useRef(null);
    const filtersAreaRef = useRef(null);
    const [renderCount, setRenderCount] = useState(0);
    const { hreportId, } = fullChatResponse || {};
    const { filters = [], selectedType } = activeReport?.hreportInteractions?.[hreportId] || {};
    const hasFilters = filters?.length > 0;
    const reports = useSelector((state) => state.hreport.present.reports);

    const handleGoToHreport = () => {
        if (hreportId) {
            const currentReport = getReportById(dispatch, hreportId);
            if (currentReport) {
                const clonedReport = cloneDeep(currentReport);
                clonedReport.reportInfo = { ...clonedReport.reportInfo, reportName: "Untitled 1" };
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

    if (!hreportId) {
        return (
            <div className="chart-preview-section chart-preview-section--empty">
                {isPreviewLoading ? (
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
                {hreportId && !isOpenMode && hasFilters && (
                    <RenderFilters filtersArea={filtersAreaRef.current} changeActiveReport={changeActiveReport} />
                )}
                {hreportId && !isOpenMode && (
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
                    fullChatResponse={fullChatResponse}
                    renderEditingArea={(editingArea) => {
                        editingAreaRef.current = editingArea
                        setRenderCount(renderCount + 1)
                    }}
                    renderFilters={(filtersArea) => {
                        filtersAreaRef.current = filtersArea
                        setRenderCount(renderCount + 1)
                    }}
                    isOpenMode={isOpenMode}
                    vizType={getHReportSelectedChartType(hreportId, reports, selectedType)}
                />
                {isConvertingChart && (
                    <div
                        className="chart-preview-section__converting"
                        data-testid="ib-convert-chart-loading"
                    >
                        <LoadingBar />
                        <Text type="secondary">Converting chart…</Text>
                    </div>
                )}
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
            )}
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
                    className="chart-preview-section__sql-button"
                    icon={<ConsoleSqlOutlined />}
                    onClick={() => setActiveTab("sql")}
                />
            </InstantBITooltip>
            <InstantBITooltip title="Report Model">
                <Button
                    size="small"
                    type="text"
                    className="chart-preview-section__spec-button"
                    icon={<FileTextOutlined />}
                    onClick={() => setActiveTab("spec")}
                />
            </InstantBITooltip>
            {showDashboardTab ? (
                <InstantBITooltip title="Dashboard Model">
                    <Button
                        size="small"
                        type="text"
                        className="chart-preview-section__spec-button"
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
                        className="chart-preview-section__spec-button"
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
        setIsMaximized,
        defaultTab,
        semanticLabel = "Semantic",
        showWorkingTab = false,
        onTabChange,
        onPreviewActivate,
        previewFirst = true,
    } = props || {};
    const initialTab = defaultTab || (previewFirst ? "preview" : "semantic");
    const [activeTab, setActiveTab] = useState(initialTab);

    useEffect(() => {
        if (defaultTab) {
            setActiveTab(defaultTab);
        }
    }, [defaultTab]);

    const handleTabChange = (active) => {
        if (active === "maximize") {
            setIsMaximized?.(true);
            return;
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
    const previewPane = (
        <Tabs.TabPane tab={getTitle(<EyeOutlined />, "Preview")} key="preview">
            <PreviewTab {...props} setActiveTab={setActiveTab} />
        </Tabs.TabPane>
    );
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
                (hasMessage && showMaximizeButton) &&
                <Tabs.TabPane tab={getTitle(<FullscreenOutlined />, "Maximize")} key="maximize">
                    {null}
                </Tabs.TabPane>
            }
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

export { JsonTab };
export default ChatTabs
