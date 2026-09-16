import { Typography } from 'antd';
import { useEffect } from 'react';
import { useSelector } from 'react-redux';
import { HelicalReports } from '../../../../pages';
import { getHReportVizInfo, getVizHeight, getVizModifierClass, getVizWidth, parseBackendErrorMessage } from '../../utils/common-utils';
import { IbResponseError } from '../ib-custom-chart';

const { Text } = Typography

const InstantChartView = (props = {}) => {
    const {
        id,
        className = "",
        compact = false,
        onPreviewError = () => { },
        backendError,
        fullChatResponse = {},
        isOpenMode,
        vizType
    } = props || {}
    const { hreportId, error, hreportLoading } = fullChatResponse || {}
    const liveHreport = useSelector((state) =>
        (state.hreport?.present?.reports || []).find((report) => report.id === hreportId)
    );
    const { selectedType: liveSelectedType, subVizType: liveSubVizType } = getHReportVizInfo(
        hreportId,
        liveHreport ? [liveHreport] : [],
        vizType,
    );
    const effectiveVizType = liveSelectedType || vizType;
    const vizOptions = { subVizType: liveSubVizType || "", report: liveHreport || null };
    const vizHeight = getVizHeight(effectiveVizType, fullChatResponse, vizOptions);

    const hasVfError = String(error || "").trim();

    useEffect(() => {
        if (hasVfError) onPreviewError?.(true);
    }, [id, onPreviewError, fullChatResponse]);

    if (hasVfError) {
        return (
            <IbResponseError
                className={className}
                details={parseBackendErrorMessage(backendError)}
            />
        );
    }

    if (!hreportId) return null;

    const modeForHreport = isOpenMode ? "instant-bi-open" : "instant-bi-create"

    return (
        <div
            className={`chart-wrapper${compact ? " chart-wrapper--compact" : ""}`.trim()}
            style={{ width: "100%", height: `${vizHeight}rem` }}
        >
            {hreportLoading && <Text type="secondary">Just a moment…</Text>}
            {!hreportLoading && <div className="chart-wrapper__content" style={{ width: "100%", height: "100%" }}>
                <HelicalReports
                    mode={modeForHreport}
                    reportId={hreportId}
                    renderEditingArea={props.renderEditingArea}
                    renderFilters={props.renderFilters}
                />
            </div>}
        </div>
    );
}

export default InstantChartView