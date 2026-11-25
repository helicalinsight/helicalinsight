import { FilterOutlined } from "@ant-design/icons";
import { useEffect, useRef } from "react";
import Markdown from "react-markdown";
import remarkGfm from "remark-gfm";
import { useDispatch } from "react-redux";
import ChartIcon from "../../common/icons/chart-icons";
import { changeEditingPane } from "../../../redux/actions/hreport.actions";
import AISparklesIcon from "../../hi-instant-bi/components/chat-screen/ai-sparkles-icon";
import InstantBITooltip from "../../hi-instant-bi/instant-bi-tooltip-title";

// The insight sits under the chart. The sparkle button asks for a new explanation.
const ExploredChartChrome = ({
    insight = "",
    explaining = false,
    canExplain = false,
    onExplain,
    onInsightHeight,
    activeTool = "",
    children,
}) => {
    const dispatch = useDispatch();
    const insightRef = useRef(null);
    const openPane = (id) => dispatch(changeEditingPane({ id }));
    const showInsight = Boolean(insight) || explaining;

    useEffect(() => {
        const node = insightRef.current;
        if (!node) {
            onInsightHeight?.(0);
            return undefined;
        }
        // The chart is given a pixel height. Reporting this row lets that height leave room under the plot.
        const reportHeight = () => onInsightHeight?.(node.offsetHeight || 0);
        reportHeight();
        if (typeof ResizeObserver === "undefined") return undefined;
        const observer = new ResizeObserver(reportHeight);
        observer.observe(node);
        return () => observer.disconnect();
    }, [insight, explaining, onInsightHeight]);

    return (
        <>
            <div className="hr-explored-chart">
                {children}
                <div className="hr-explored-icons hr-explored-icons--bottom">
                    {canExplain ? (
                        <InstantBITooltip title="Explain this chart">
                            <button
                                type="button"
                                className={`hr-explored-icons__fab hr-explored-icons__fab--explain${explaining ? " is-active" : ""}`}
                                onClick={onExplain}
                                disabled={explaining}
                            >
                                <AISparklesIcon />
                            </button>
                        </InstantBITooltip>
                    ) : null}
                    <InstantBITooltip title="Change Visualization">
                        <button
                            type="button"
                            className={`hr-explored-icons__fab hr-explored-icons__fab--viz${activeTool === "1" ? " is-active" : ""}`}
                            onClick={() => openPane("1")}
                        >
                            <ChartIcon name="convert-chart" />
                        </button>
                    </InstantBITooltip>
                    <InstantBITooltip title="Open Filters">
                        <button
                            type="button"
                            className={`hr-explored-icons__fab hr-explored-icons__fab--filter${activeTool === "2" ? " is-active" : ""}`}
                            onClick={() => openPane("2")}
                        >
                            <FilterOutlined />
                        </button>
                    </InstantBITooltip>
                </div>
            </div>
            {showInsight ? (
                <div className="hr-explored-insight" ref={insightRef}>
                    {explaining ? <p>Preparing your explanation…</p> : (
                        <Markdown remarkPlugins={[remarkGfm]}>{insight}</Markdown>
                    )}
                </div>
            ) : null}
        </>
    );
};

export default ExploredChartChrome;
