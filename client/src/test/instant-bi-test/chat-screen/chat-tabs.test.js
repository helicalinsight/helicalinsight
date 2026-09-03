import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { Router, useHistory } from 'react-router-dom';
import { createMemoryHistory } from 'history';
import ChatTabs from '../../../components/hi-instant-bi/components/chat-screen/chat-tabs';
import InstantChartView from '../../../components/hi-instant-bi/components/chat-screen/chart-view';
import { changeReport } from '../../../redux/actions/hreport.actions';

jest.mock('react-markdown', () => ({
    __esModule: true,
    default: ({ children }) =>
        require('react').createElement('div', { 'data-testid': 'markdown' }, children),
}));

jest.mock('../../../components/hi-instant-bi/components/chat-screen/chart-view', () => {
    const React = require('react');
    const InstantChartViewMock = (props) => {
        const { renderEditingArea, renderFilters, ...rest } = props;
        InstantChartViewMock.lastProps = rest;
        const callbacksRef = React.useRef(null);
        callbacksRef.current = { renderEditingArea, renderFilters };
        React.useEffect(() => {
            const { renderEditingArea: onEditArea, renderFilters: onFilters } = callbacksRef.current;
            if (onFilters) {
                onFilters(
                    React.createElement('div', { 'data-testid': 'filters-area-slot' }, 'FILTERS AREA')
                );
            }
            if (onEditArea) {
                onEditArea(
                    React.createElement('div', { 'data-testid': 'editing-area-slot' }, 'EDITING AREA')
                );
            }
        }, []);
        return React.createElement('div', { 'data-testid': 'instant-chart-view' });
    };
    return { __esModule: true, default: InstantChartViewMock };
});

jest.mock('../../../components/common/components/hi-loading-bar', () => ({
    __esModule: true,
    default: ({ handleClick }) =>
        require('react').createElement('div', {
            role: 'button',
            'data-testid': 'loading-bar',
            onClick: handleClick,
        }),
}));

jest.mock('../../../components/hi-instant-bi/components/instant-bi-response-metadata', () => ({
    __esModule: true,
    default: ({ sqlDetails, vizDetails, tokenUsage }) =>
        require('react').createElement('div', {
            'data-testid': 'response-metadata',
            'data-has-sql': String(Boolean(sqlDetails)),
            'data-viz-name': (vizDetails && vizDetails.chart_name) || '',
            'data-tokens': JSON.stringify(tokenUsage === undefined ? null : tokenUsage),
        }),
}));

jest.mock('../../../redux/actions/hreport.actions', () => ({
    changeReport: jest.fn((payload) => ({ type: 'CHANGE_REPORT', payload })),
}));

jest.mock('../../../app/constants', () => ({
    routesUrl: { helicalReportUrl: '/helical-report' },
}));

function baseProps(overrides = {}) {
    return {
        hasMessage: true,
        showMaximizeButton: false,
        isMaximized: false,
        setIsMaximized: jest.fn(),
        showDataInsightButton: false,
        handleDataInsight: jest.fn(),
        isOpenMode: false,
        vizDetails: { chart_name: 'Revenue Chart', plot_config: { legend: true } },
        isConvertingChart: false,
        resolvedData: [],
        resolvedVf: null,
        id: 'chat-1',
        chartSettings: { theme: 'dark' },
        setHasPreviewError: jest.fn(),
        resolvedFullChatResponse: {},
        fullChatResponse: { hreportId: 'hr-1' },
        isLoadingDataInsight: false,
        dataInsightContent: '',
        handleAbortDataInsight: jest.fn(),
        hasDataInsightTokens: false,
        dataInsightTokens: {},
        sqlDetails: { dialect: 'postgresql' },
        tokenUsage: { prompt_tokens: 10 },
        resolvedSql: '',
        handleCopySQL: jest.fn(),
        dispatch: jest.fn(),
        ...overrides,
    };
}

function renderChatTabs(props, initialPath = '/') {
    const historyRef = { current: null };
    const HistoryProbe = () => {
        historyRef.current = useHistory();
        return null;
    };
    const utils = render(
        <Router history={createMemoryHistory({ initialEntries: [initialPath] })}>
            <HistoryProbe />
            <ChatTabs {...props} />
        </Router>
    );
    return { ...utils, history: historyRef.current };
}

function openTab(container, key) {
    fireEvent.click(container.querySelector(`[data-node-key="${key}"] .ant-tabs-tab-btn`));
}

describe('ChatTabs - tabs shell', () => {
    test('renders preview, data, semantic and sql tab headers', () => {
        const { container } = renderChatTabs(baseProps());
        ['preview', 'data', 'semantic', 'sql'].forEach((key) => {
            expect(container.querySelector(`[data-node-key="${key}"]`)).toBeTruthy();
        });
    });

    test('shows the maximize tab only when message exists and button enabled', () => {
        const { container, rerender } = renderChatTabs(baseProps());
        expect(container.querySelector('[data-node-key="maximize"]')).toBeNull();

        rerender(
            <Router history={createMemoryHistory()}>
                <ChatTabs {...baseProps({ showMaximizeButton: true })} />
            </Router>
        );
        expect(container.querySelector('[data-node-key="maximize"]')).toBeTruthy();
    });

    test('hides the maximize tab when there is no message even if enabled', () => {
        const { container } = renderChatTabs(
            baseProps({ hasMessage: false, showMaximizeButton: true })
        );
        expect(container.querySelector('[data-node-key="maximize"]')).toBeNull();
    });

    test('clicking maximize reports maximized state without leaving preview tab', () => {
        const props = baseProps({ showMaximizeButton: true });
        const { container } = renderChatTabs(props);

        fireEvent.click(container.querySelector('[data-node-key="maximize"]'));

        expect(props.setIsMaximized).toHaveBeenCalledWith(true);
        const previewTab = container.querySelector('[data-node-key="preview"] .ant-tabs-tab-btn');
        expect(previewTab.getAttribute('aria-selected')).toBe('true');
    });

    test('activates the clicked tab', () => {
        const { container } = renderChatTabs(baseProps());

        fireEvent.click(container.querySelector('[data-node-key="data"]'));

        expect(
            container
                .querySelector('[data-node-key="data"] .ant-tabs-tab-btn')
                .getAttribute('aria-selected')
        ).toBe('true');
        expect(
            container
                .querySelector('[data-node-key="preview"] .ant-tabs-tab-btn')
                .getAttribute('aria-selected')
        ).toBe('false');
    });
});

describe('ChatTabs - Data tab', () => {
    test('renders markdown table source for resolved rows', () => {
        const props = baseProps({
            resolvedData: [
                { region: 'EMEA', revenue: 100 },
                { region: 'APAC', revenue: 200 },
            ],
        });
        const { container } = renderChatTabs(props);
        openTab(container, 'data');

        const markdown = screen.getByTestId('markdown');
        expect(markdown.textContent).toContain('| region | revenue |');
        expect(markdown.textContent).toContain('| EMEA | 100 |');
        expect(markdown.textContent).toContain('| APAC | 200 |');
    });

    test('shows the empty state when no data is resolved', () => {
        const { container } = renderChatTabs(baseProps({ resolvedData: [] }));
        openTab(container, 'data');

        expect(screen.getByText('No data available')).toBeTruthy();
    });
});

describe('ChatTabs - SQL tab', () => {
    test('renders the resolved sql with a copy action that delegates', () => {
        const handleCopySQL = jest.fn();
        const props = baseProps({ resolvedSql: 'SELECT * FROM sales', handleCopySQL });
        const { container } = renderChatTabs(props);
        openTab(container, 'sql');

        const markdown = screen.getByTestId('markdown');
        expect(markdown.textContent).toBe('SELECT * FROM sales');

        fireEvent.click(container.querySelector('.sql-copy-btn [role="img"]'));
        expect(handleCopySQL).toHaveBeenCalledTimes(1);
    });

    test('hides the copy action when there is no sql', () => {
        const { container } = renderChatTabs(baseProps({ resolvedSql: '' }));
        openTab(container, 'sql');

        expect(container.querySelector('.sql-copy-btn')).toBeNull();
    });
});

describe('ChatTabs - Semantic tab', () => {
    test('forwards sql details, viz details and token usage to the metadata panel', () => {
        const { container } = renderChatTabs(baseProps());
        openTab(container, 'semantic');

        const metadata = screen.getByTestId('response-metadata');
        expect(metadata.getAttribute('data-has-sql')).toBe('true');
        expect(metadata.getAttribute('data-viz-name')).toBe('Revenue Chart');
        expect(JSON.parse(metadata.getAttribute('data-tokens'))).toEqual({ prompt_tokens: 10 });
    });
});

describe('ChatTabs - Preview tab', () => {
    test('passes chart inputs through to the chart view', () => {
        const props = baseProps({
            resolvedData: [{ a: 1 }],
            resolvedVf: '<vf/>',
            resolvedFullChatResponse: { error: 'boom' },
            fullChatResponse: { hreportId: 'hr-1', hreportLoading: false },
        });
        renderChatTabs(props);

        expect(InstantChartView.lastProps).toMatchObject({
            compact: true,
            data: [{ a: 1 }],
            vf: '<vf/>',
            id: 'chat-1',
            chartName: 'Revenue Chart',
            plotConfig: { legend: true },
            chartSettings: { theme: 'dark' },
            backendError: 'boom',
            isOpenMode: false,
            className: 'chart-wrapper--message',
        });
        expect(InstantChartView.lastProps.fullChatResponse).toEqual(props.fullChatResponse);
        expect(InstantChartView.lastProps.onPreviewError).toBe(props.setHasPreviewError);
    });

    test('propagates preview errors through the callback handed to the chart view', () => {
        const setHasPreviewError = jest.fn();
        renderChatTabs(baseProps({ setHasPreviewError }));

        InstantChartView.lastProps.onPreviewError(true);

        expect(setHasPreviewError).toHaveBeenCalledWith(true);
    });

    test('shows the data insight trigger only when enabled and fires it on click', () => {
        const handleDataInsight = jest.fn();
        const props = baseProps({ showDataInsightButton: true, handleDataInsight });
        const { container, rerender } = renderChatTabs(props);

        const fab = screen.getByTestId('data-insight-play-btn');
        fireEvent.click(fab);
        expect(handleDataInsight).toHaveBeenCalledTimes(1);

        rerender(
            <Router history={createMemoryHistory()}>
                <ChatTabs {...baseProps({ showDataInsightButton: false })} />
            </Router>
        );
        expect(screen.queryByTestId('data-insight-play-btn')).toBeNull();
        expect(container).toBeTruthy();
    });

    test('shows the converting overlay while a conversion is running', () => {
        renderChatTabs(baseProps({ isConvertingChart: true }));

        expect(screen.getByTestId('ib-convert-chart-loading')).toBeTruthy();
        expect(screen.getByText('Converting chart…')).toBeTruthy();
    });

    test('hides filters and editing controls in open mode', () => {
        const { container } = renderChatTabs(baseProps({ isOpenMode: true }));

        expect(
            container.querySelector('.chart-preview-section__go-to-hreport-filter-button')
        ).toBeNull();
        expect(container.querySelector('.chart-preview-section__trigger-button')).toBeNull();
        expect(container.querySelector('.chart-preview-section__go-to-hreport-button')).toBeNull();
        expect(InstantChartView.lastProps.isOpenMode).toBe(true);
    });

    test('navigates to the report editor preserving context', () => {
        const { container, history } = renderChatTabs(baseProps());
        const pushSpy = jest.spyOn(history, 'push');

        fireEvent.click(
            container.querySelector('.chart-preview-section__go-to-hreport-button')
        );

        expect(pushSpy).toHaveBeenCalledWith({
            pathname: '/helical-report',
            state: {
                reportId: 'hr-1',
                fromInstantBI: true,
            },
        });
    });

    test('stays put when the response has no linked report id', () => {
        const { container, history } = renderChatTabs(
            baseProps({ fullChatResponse: {} })
        );
        const pushSpy = jest.spyOn(history, 'push');

        fireEvent.click(
            container.querySelector('.chart-preview-section__go-to-hreport-button')
        );

        expect(pushSpy).not.toHaveBeenCalled();
    });

    test('opens the filters drawer after activating the current report', async () => {
        const props = baseProps();
        const { container } = renderChatTabs(props);

        fireEvent.click(
            container.querySelector('.chart-preview-section__go-to-hreport-filter-button')
        );

        await waitFor(() => expect(screen.getByTestId('filters-area-slot')).toBeTruthy());
        expect(changeReport).toHaveBeenCalledWith({ id: 'hr-1' });
        expect(props.dispatch).toHaveBeenCalledWith({ type: 'CHANGE_REPORT', payload: { id: 'hr-1' } });
    });

    test('opens the editing drawer with the area supplied by the chart view', async () => {
        const { container } = renderChatTabs(baseProps());

        fireEvent.click(container.querySelector('.chart-preview-section__trigger-button'));

        await waitFor(() => expect(screen.getByTestId('editing-area-slot')).toBeTruthy());
    });
});

describe('ChatTabs - data insight section', () => {
    test('shows an abortable loading bar while the explanation is prepared', () => {
        const handleAbortDataInsight = jest.fn();
        renderChatTabs(baseProps({ isLoadingDataInsight: true, handleAbortDataInsight }));

        expect(screen.getByTestId('data-insight-section')).toBeTruthy();
        expect(screen.getByTestId('ib-data-insight-loading-bar')).toBeTruthy();
        expect(screen.getByText('Preparing your explanation…')).toBeTruthy();

        fireEvent.click(screen.getByTestId('loading-bar'));
        expect(handleAbortDataInsight).toHaveBeenCalledTimes(1);
    });

    test('renders the explanation content once ready', () => {
        renderChatTabs(baseProps({ dataInsightContent: 'Revenue grew **20%**' }));

        expect(screen.getByTestId('data-insight-section')).toBeTruthy();
        expect(screen.getByTestId('markdown').textContent).toContain('Revenue grew **20%**');
        expect(screen.queryByTestId('loading-bar')).toBeNull();
    });

    test('omits the whole section when idle without content', () => {
        renderChatTabs(baseProps());
        expect(screen.queryByTestId('data-insight-section')).toBeNull();
    });

    test('lists token usage entries and falls back for missing values', async () => {
        const { container } = renderChatTabs(
            baseProps({
                dataInsightContent: 'done',
                hasDataInsightTokens: true,
                dataInsightTokens: { input_tokens: 5, output_tokens: null },
            })
        );

        expect(container.querySelector('.message-container__data-insight-info-icon')).toBeTruthy();

        fireEvent.click(
            container.querySelector('.message-container__data-insight-info-icon')
        );

        await waitFor(() => {
            expect(screen.getByText('input tokens')).toBeTruthy();
        });
        expect(screen.getByText('input tokens').nextElementSibling.textContent).toBe('5');
        expect(screen.getByText('output tokens').nextElementSibling.textContent).toBe('—');
    });

    test('does not offer token usage without tokens flag', () => {
        const { container } = renderChatTabs(baseProps({ dataInsightContent: 'done' }));
        expect(container.querySelector('.message-container__data-insight-info-icon')).toBeNull();
    });
});
