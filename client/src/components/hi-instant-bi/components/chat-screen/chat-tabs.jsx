import {
    ConsoleSqlOutlined,
    CopyOutlined,
    DatabaseOutlined,
    ExportOutlined,
    EyeOutlined,
    FilterOutlined,
    FullscreenOutlined,
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
import CommonMarkdownTable from '../../utils/common-markdown-table'
import { getHReportSelectedChartType } from "../../utils/common-utils"
import InstantBIResponseMetadata from '../instant-bi-response-metadata'
import AISparklesIcon from './ai-sparkles-icon'
import InstantChartView from "./chart-view"

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
        dataInsightContent,
        handleAbortDataInsight,
        hasDataInsightTokens,
        dataInsightTokens,
        dispatch,
        activeReport
    } = props || {};

    const editingAreaRef = useRef(null);
    const filtersAreaRef = useRef(null);
    const [renderCount, setRenderCount] = useState(0);
    const { hreportId,  } = fullChatResponse || {};
    const { filters = [], selectedType } = activeReport?.hreportInteractions?.[hreportId] || {};
    const hasFilters = filters?.length > 0;
    const reports = useSelector((state) => state.hreport.present.reports);

    const handleGoToHreport = () => {
        if (hreportId) {
            const currentReport = getReportById(dispatch, hreportId);
            if (currentReport) {
                try {
                    localStorage.setItem('hreport_active_report', JSON.stringify({ activeHreport: currentReport, fromInstantBI: true }));
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
        </React.Fragment>
    )
}

const DataTab = (props = {}) => {
    const { resolvedData } = props || {}
    return (
        <div className="message-container__bot-data-renderer">
            <CommonMarkdownTable data={resolvedData || []} />
        </div>
    )
}

const SemanticTab = (props = {}) => {
    const {
        activeTab,
        sqlDetails,
        vizDetails,
        tokenUsage,
        resolvedData
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

const SQLTab = (props = {}) => {
    const {
        resolvedSql,
        handleCopySQL
    } = props || {}
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

const getTitle = (icon, title = "") => {
    return (<InstantBITooltip title={title}>{icon}</InstantBITooltip>)
}

const ChatTabs = (props = {}) => {
    const [activeTab, setActiveTab] = useState("preview");
    const { hasMessage, showMaximizeButton, isMaximized, setIsMaximized } = props || {}
    return (
        <Tabs
            activeKey={activeTab}
            onChange={(active) => {
                if (active === "maximize") {
                    setIsMaximized(true);
                    return;
                }
                setActiveTab(active);
            }}
        >
            {
                (hasMessage && showMaximizeButton) &&
                <Tabs.TabPane tab={getTitle(<FullscreenOutlined />, "Maximize")} key="maximize">
                    {null}
                </Tabs.TabPane>
            }
            <Tabs.TabPane tab={getTitle(<EyeOutlined />, "Preview")} key="preview">
                <PreviewTab {...props} setActiveTab={setActiveTab} />
            </Tabs.TabPane>

            <Tabs.TabPane tab={getTitle(<DatabaseOutlined />, "Semantic")} key="semantic">
                <SemanticTab {...props} />
            </Tabs.TabPane>

            <Tabs.TabPane tab={getTitle(<ConsoleSqlOutlined />, "SQL")} key="sql">
                <SQLTab {...props} />
            </Tabs.TabPane>
        </Tabs>
    )
}

export default ChatTabs