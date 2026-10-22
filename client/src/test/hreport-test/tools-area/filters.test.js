import { v4 as uuidv4 } from "uuid";
import { configureStore } from '@reduxjs/toolkit';
import { render, screen, fireEvent, act, waitFor } from '@testing-library/react';
import { Provider } from 'react-redux';
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";

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
const reducers = require('../../../redux').default;
const Filters = require("../../../components/hi-reports/hi-editing-area/components/filters/filters").default;
const { hiMockAxios } = require('../../../app/mock-axios');
const actionTypes = require("../../../redux/actions/actionTypes").default;
const { openMetadata } = require("../../../components/hi-reports/utils/base");
const { addFieldToCanvas, createFilter } = require("../../../redux/actions/hreport.actions");

const flushPromises = () => new Promise(setImmediate);

const App = (props) => {
    let { store, ...rest } = props
    return (
        <DndProvider backend={HTML5Backend}>
            <Provider store={store}>
                <Filters {...rest} />
            </Provider>
        </DndProvider>
    );
};


describe("Rendering filters pane", () => {
    const store = configureStore({
        reducer: reducers,
        middleware: (getDefaultMiddleware) =>
            getDefaultMiddleware({
                thunk: {
                    extraArgument: hiMockAxios
                },
                immutableCheck: false,
                serializableCheck: false,
            }),
    });
    const dispatch = store.dispatch
    const getState = store.getState
    test("rendering filters", async () => {
        let reportId = uuidv4()
        dispatch({ type: actionTypes.LOAD_INTIAL_REPORT, payload: { reportId } })
        await flushPromises(openMetadata({ location: "naresh", metadataFileName: "Metadata_1.metadata" }, dispatch))
        let { metadata } = getState().hreport.present.reports[0]
        let table = metadata.tables["meeting_details"]
        let column = table.columns["meeting_date"]
        dispatch(createFilter({ column, table, from: "metadata" }))
        await flushPromises(render(<App store={store} reportId={reportId} />))
        column = table.columns["client_name"]
        dispatch(createFilter({ column, table, from: "metadata" }))
        expect(screen.queryByTestId(/hr-filter-label-meeting_date/i)).toBeTruthy()
        expect(screen.queryByTestId(/hr-filter-label-client_name/i)).toBeTruthy()
        fireEvent.doubleClick(screen.queryByTestId(/hr-filter-label-meeting_date/i))
        let renameInput = screen.queryByTestId(/filter-rename-input-meeting_date/i)
        expect(renameInput).toBeTruthy()
        fireEvent.change(renameInput, { target: { value: "meeting_date_changed" } });
        fireEvent.keyDown(renameInput, { keyCode: 13 });
        expect(screen.queryByTestId(/filter-rename-input-meeting_date/i)).toBeFalsy()
        expect(screen.queryByTestId(/hr-filter-label-meeting_date_changed/i)).toBeTruthy()
        expect(screen.queryByTestId(/hr-filter-label-client_name/i)).toBeTruthy()
        fireEvent.doubleClick(screen.queryByTestId(/hr-filter-label-client_name/i))
        renameInput = screen.queryByTestId(/filter-rename-input-client_name/i)
        expect(renameInput).toBeTruthy()
        fireEvent.change(renameInput, { target: { value: "client_name_changed" } });
        fireEvent.keyDown(renameInput, { keyCode: 13 });
        expect(screen.queryByTestId(/hr-filter-label-meeting_date_changed/i)).toBeTruthy()
        expect(screen.queryByTestId(/hr-filter-label-client_name_changed/i)).toBeTruthy()


    });
    test("rendering filter advance mode", async () => {
        let reportId = "test_1234"
        dispatch({ type: actionTypes.LOAD_INTIAL_REPORT, payload: { reportId } })
        await flushPromises(openMetadata({ location: "naresh", metadataFileName: "Metadata_1.metadata" }, dispatch))
        let { metadata } = getState().hreport.present.reports[0]
        let table = metadata.tables["meeting_details"]
        let column = table.columns["meeting_date"]
        dispatch(createFilter({ column, table, from: "metadata" }))
        await flushPromises(render(<App store={store} reportId={reportId} />))
        expect(screen.queryByTestId(/hr-filter-label-meeting_date/i)).toBeTruthy()
        expect(screen.queryByTestId(/filters-advance-btn-test_1234-meeting_date/i)).toBeTruthy()
        fireEvent.click(screen.queryByTestId(/filters-advance-btn-test_1234/i))
        expect(screen.queryByTestId(/filter-advanced-mode/i)).toBeTruthy()
        const getTestFilter = () => {
            const report = getState().hreport.present.reports.find((item) => item.active);
            return report.filters[0];
        };
        const chipLabels = screen.getAllByTestId('hr-custom-column-chip-label');
        expect(chipLabels).toHaveLength(2);
        expect(chipLabels[0].textContent).toBe('meeting_date');
        fireEvent.click(chipLabels[0]);
        expect(await screen.findByText('Group by')).toBeTruthy();
        expect(screen.getByText('Aggregate')).toBeTruthy();
        expect(screen.getByText('Functions')).toBeTruthy();
        expect(screen.getByText('Order By')).toBeTruthy();
        const initialDefaultFunction = getTestFilter().mapping.displayColumn.defaultFunction;
        fireEvent.click(screen.getByText('Group by'));
        await waitFor(() => {
            expect(getTestFilter().mapping.displayColumn.defaultFunction).not.toBe(initialDefaultFunction);
        });
        expect(['db.generic.groupBy.group', 'none'])
            .toContain(getTestFilter().mapping.displayColumn.defaultFunction);
        expect(getTestFilter().mapping.orderBy.display).toBe('asc');
        fireEvent.click(screen.getAllByLabelText('sort-ascending')[0]);
        await waitFor(() => {
            expect(getTestFilter().mapping.orderBy.display).toBe('desc');
        });
    });
    test("date filter with dateadd db function", async () => {
        let reportId = uuidv4()
        dispatch({ type: actionTypes.LOAD_INTIAL_REPORT, payload: { reportId } })
        // await waitFor(()=>openMetadata({location:"naresh",metadataFileName:"Metadata_1.metadata"},dispatch)) 
        await flushPromises(openMetadata({ location: "naresh", metadataFileName: "Metadata_1.metadata" }, dispatch))
        await flushPromises(render(<App store={store} reportId={reportId} />))
        let { metadata } = getState().hreport.present.reports[0]
        let table = metadata.tables["dimdate"]
        let column = table.columns["fiscal_year"]
        dispatch(addFieldToCanvas({ column, table, addedAs: "column" }))

    });
});