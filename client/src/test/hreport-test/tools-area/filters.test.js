import "core-js/stable";
import { v4 as uuidv4 } from "uuid";
import { configureStore } from '@reduxjs/toolkit';
import { render, screen, fireEvent, act, waitFor } from '@testing-library/react';
import { Provider } from 'react-redux';
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import reducers from '../../../redux';
import Filters from "../../../components/hi-reports/hi-editing-area/components/filters/filters"
import { hiMockAxios } from '../../../app/mock-axios';
import actionTypes from "../../../redux/actions/actionTypes";
import { openMetadata } from "../../../components/hi-reports/utils/base";
import { addFieldToCanvas, createFilter } from "../../../redux/actions/hreport.actions";

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
    beforeAll(() => {
        delete window.matchMedia
        window.matchMedia = (query) => ({
            matches: false,
            media: query,
            onchange: null,
            addListener: jest.fn(), // deprecated
            removeListener: jest.fn(), // deprecated
            addEventListener: jest.fn(),
            removeEventListener: jest.fn(),
            dispatchEvent: jest.fn(),
        })
        window.HTMLElement.prototype.scrollBy = jest.fn();
    });
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


    afterAll(() => {
        global.gc && global.gc()
    })
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
        expect(screen.queryByTestId(/advance-filter-display-dbfunc/i)).toBeTruthy()
        fireEvent.click(screen.queryByTestId(/advance-filter-display-dbfunc/i))
        expect(screen.queryByTestId(/advance-mode-list-meeting_date/i)).toBeTruthy()
        expect(screen.queryByTestId(/advance-mode-list-meeting_date/i).children.length).toEqual(79)
        const searchElem = screen.queryByTestId(/advance-mode-list-input-meeting_date/i)
        expect(searchElem).toBeTruthy()
        fireEvent.change(searchElem, { target: { value: "con" } })
        expect(screen.queryByTestId(/advance-mode-list-meeting_date/i).children.length).toEqual(3)
        fireEvent.change(searchElem, { target: { value: "sin" } })
        expect(screen.queryByTestId(/advance-mode-list-meeting_date/i).children.length).toEqual(2)
        fireEvent.change(searchElem, { target: { value: "" } })
        expect(screen.queryByTestId(/advance-mode-list-meeting_date/i).children.length).toEqual(79)
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