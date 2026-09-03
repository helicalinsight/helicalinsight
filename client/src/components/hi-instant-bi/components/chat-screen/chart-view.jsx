import { Typography } from 'antd';
import { useEffect } from 'react';
import { HelicalReports } from '../../../../pages';
import { getVizHeight, parseBackendErrorMessage } from '../../utils/common-utils';
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
            className={`chart-wrapper${compact ? " chart-wrapper--compact" : ""}${className}`.trim()}
            style={{ width: "100%", height: `${getVizHeight(vizType, fullChatResponse)}rem` }}
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