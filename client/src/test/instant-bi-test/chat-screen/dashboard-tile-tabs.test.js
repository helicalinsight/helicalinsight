import '../../utils/mockJsdom';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { Provider } from 'react-redux';

jest.setTimeout(30000);

const __originalFetch = global.fetch;
global.fetch = jest.fn((url, ...rest) => {
    const urlStr = String(url);
    if (urlStr.includes('.wasm') || urlStr.includes('muze')) {
        return new Promise(() => {});
    }
    return __originalFetch ? __originalFetch(url, ...rest) : Promise.reject(new Error('fetch not available'));
});
afterAll(() => {
    global.fetch = __originalFetch;
});

jest.mock('react-markdown', () => ({
    __esModule: true,
    default: ({ children }) =>
        require('react').createElement('div', { 'data-testid': 'markdown' }, children),
}));

jest.mock('../../../components/hi-instant-bi/components/chat-screen/chart-view', () => ({
    __esModule: true,
    default: (props) => {
        const React = require('react');
        return React.createElement('div', {
            'data-testid': 'instant-chart-view',
            'data-hreport-id': (props.fullChatResponse || {}).hreportId || '',
        });
    },
}));

jest.mock('../../../components/common/components/hi-loading-bar', () => ({
    __esModule: true,
    default: ({ handleClick }) =>
        require('react').createElement('div', {
            role: 'button',
            'data-testid': 'loading-bar',
            onClick: handleClick,
        }),
}));

jest.mock('../../../components/hi-instant-bi/components/chat-screen/chat-typing-text', () => ({
    __esModule: true,
    default: ({ lines = [], fallback = '' }) => {
        const React = require('react');
        const text = (Array.isArray(lines) && lines.length ? lines : [fallback])
            .filter(Boolean)
            .join('\n');
        return React.createElement('div', { 'data-testid': 'ib-typing-narrative' }, text);
    },
}));

jest.mock('../../../components/hi-instant-bi/utils/instant-bi-requests', () => ({
    __esModule: true,
    loadInstantBIDataInsight: jest.fn(),
}));

const DashboardTileTabs = require('../../../components/hi-instant-bi/components/chat-screen/dashboard-tile-tabs').default;
const { loadInstantBIDataInsight } = require('../../../components/hi-instant-bi/utils/instant-bi-requests');

const liveHreport = {
    id: 'hr-1',
    selectedType: 'Antcharts',
    marksList: [{ value: '_all_', subVizType: 'bar' }],
    fields: [],
    reportData: { data: [{ a: 1 }], metadata: [] },
};

const ibReport = {
    id: 'ib-1',
    active: true,
    activeChatID: 'chat-1',
    reportInfo: { location: 'reports', uuid: 'rep.instant', reportName: 'Rep' },
    subject: { model: { file: 'sales.metadata', dir: 'agents' } },
};

const tileReportInfo = {
    file: {
        title: 'Travel cost',
        instant_item: {
            question: 'Why is travel cost high?',
            chat_seq_id: '3-1',
            chat_response: {
                viz: { vf_title: 'Cost', chart_name: 'bar' },
                sql: { raw_sql: 'SELECT a, SUM(b) FROM t GROUP BY a' },
                summary: { insight: 'Costs rose' },
                report_model: {
                    data_model: { sql: 'SELECT a, SUM(b) FROM t GROUP BY a' },
                    viz_model: { chart: { viz: 'Bar', mark: 'Chart' } },
                },
                token_usage: {},
            },
        },
    },
};

const createMockStore = ({ ibReports = [ibReport], hreports = [liveHreport] } = {}) => {
    const state = {
        hreport: { present: { reports: hreports } },
        dashboard: { present: { dashboardVariables: {} } },
        instantBI: { reports: ibReports },
    };
    const dispatch = jest.fn((action) => {
        if (typeof action === 'function') {
            return action(dispatch, () => state, () => ({}));
        }
        return action;
    });
    return {
        getState: () => state,
        dispatch,
        subscribe: () => () => {},
    };
};

const renderTile = (store, props = {}) =>
    render(
        <Provider store={store}>
            <DashboardTileTabs
                hreportId="hr-1"
                gridId="item-1"
                reportInfo={tileReportInfo}
                {...props}
            />
        </Provider>
    );

describe('DashboardTileTabs', () => {
    beforeEach(() => {
        loadInstantBIDataInsight.mockClear();
        loadInstantBIDataInsight.mockReturnValue({ abort: jest.fn() });
    });

    test('renders tabs with the live chart and an insight trigger', () => {
        const store = createMockStore();
        renderTile(store);
        expect(screen.getByTestId('instant-chart-view')).toBeTruthy();
        expect(screen.getByTestId('instant-chart-view').getAttribute('data-hreport-id')).toBe('hr-1');
        expect(screen.getByTestId('data-insight-play-btn')).toBeTruthy();
    });

    test('returns null without hreportId', () => {
        const store = createMockStore();
        const { container } = renderTile(store, { hreportId: null, reportInfo: {} });
        expect(container.firstChild).toBeNull();
        expect(loadInstantBIDataInsight).not.toHaveBeenCalled();
    });

    test('clicking the trigger calls data insight with tile + chat context', () => {
        const store = createMockStore();
        renderTile(store);
        fireEvent.click(screen.getByTestId('data-insight-play-btn'));
        expect(loadInstantBIDataInsight).toHaveBeenCalledTimes(1);
        const args = loadInstantBIDataInsight.mock.calls[0][0];
        expect(args).toMatchObject({
            reportId: 'ib-1',
            chatSequenceId: '3-1',
            userInput: 'Why is travel cost high?',
            chatId: 'chat-1',
            useLoadChatPayload: false,
            skipDispatch: true,
        });
        expect(args.agent).toEqual({ file: 'sales.metadata', dir: 'agents' });
        expect(args.existingChatResponse.sql).toEqual({
            raw_sql: 'SELECT a, SUM(b) FROM t GROUP BY a',
        });
        expect(args.existingChatResponse.data).toEqual([{ a: 1 }]);
        expect(args.existingChatResponse.hreportId).toBe('hr-1');
        expect(screen.getByTestId('ib-data-insight-loading-bar')).toBeTruthy();
    });

    test('insight result renders content and clears loading', async () => {
        const store = createMockStore();
        renderTile(store);
        fireEvent.click(screen.getByTestId('data-insight-play-btn'));
        const { onComplete } = loadInstantBIDataInsight.mock.calls[0][0];
        onComplete({ success: true, insight: 'Costs rose sharply.' });
        await waitFor(() => expect(screen.getByTestId('data-insight-section')).toBeTruthy());
        expect(screen.getByText('Costs rose sharply.')).toBeTruthy();
        expect(screen.queryByTestId('ib-data-insight-loading-bar')).toBeNull();
    });

    test('abort button stops loading and aborts the request', () => {
        const abort = jest.fn();
        loadInstantBIDataInsight.mockReturnValue({ abort });
        const store = createMockStore();
        renderTile(store);
        fireEvent.click(screen.getByTestId('data-insight-play-btn'));
        expect(screen.getByTestId('ib-data-insight-loading-bar')).toBeTruthy();
        fireEvent.click(screen.getByTestId('loading-bar'));
        expect(abort).toHaveBeenCalledTimes(1);
        expect(screen.queryByTestId('ib-data-insight-loading-bar')).toBeNull();
    });

    test('missing IB chat context blocks the call with a notification', () => {
        const store = createMockStore({ ibReports: [] });
        renderTile(store);
        fireEvent.click(screen.getByTestId('data-insight-play-btn'));
        expect(loadInstantBIDataInsight).not.toHaveBeenCalled();
        const notified = store.dispatch.mock.calls
            .map(([action]) => action)
            .filter((action) => action && typeof action === 'object');
        expect(notified.length).toBeGreaterThan(0);
    });

    test('unmount aborts an in-flight insight request', () => {
        const abort = jest.fn();
        loadInstantBIDataInsight.mockReturnValue({ abort });
        const store = createMockStore();
        const { unmount } = renderTile(store);
        fireEvent.click(screen.getByTestId('data-insight-play-btn'));
        unmount();
        expect(abort).toHaveBeenCalledTimes(1);
    });
});
