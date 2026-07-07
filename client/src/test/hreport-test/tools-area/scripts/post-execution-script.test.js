import "core-js/stable";
import { configureStore } from '@reduxjs/toolkit';
import { render, screen, waitFor } from '@testing-library/react';
import { DndProvider } from 'react-dnd';
import { HTML5Backend } from 'react-dnd-html5-backend';
import { Provider } from 'react-redux';
import { hiMockAxios } from '../../../../app/mock-axios';
import { generateReport } from '../../../../components/hi-reports/utils/base';
import { HelicalReports } from "../../../../pages/helical-reports-page";
import reducers from '../../../../redux';
import { appActions } from '../../../../redux/actions';
import { applyReportScripts, changeEditorContent } from '../../../../redux/actions/hreport.actions';
// import "regenerator-runtime/runtime";
// import '../../../utils/mockJsdom';
const crypto = require('crypto');
const flushPromises = () => new Promise(setImmediate);
const App = ({ store }) => {
    return (
        <DndProvider backend={HTML5Backend}>
            <Provider store={store}>
                <div id="report-drilldown-menu" ></div>
                <HelicalReports />
            </Provider>
        </DndProvider>
    );
};

describe("Post Execution Script", () => {
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
        window.createObjectURL = jest.fn();
        window.HTMLElement.prototype.scrollBy = jest.fn();
        window.crypto = {};
        window.crypto.getRandomValues = arr => crypto.randomBytes(arr.length)
    });
    
  afterAll(() => {
    global.gc && global.gc()
  })
    
    test("post execution test ", async () => {
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
       await flushPromises( dispatch(appActions.setEditModeInfo({
            dir: "naresh/parent.hr", file: "parent.hr", extension: "hr"
        })))
       await flushPromises( render(<App store={store} />))
        expect(screen.queryByTestId(/table-value-Agent-0/i)).toBeTruthy();
        let script1 = `add_row({table:"travel_details",column:"travel_medium"})`
        dispatch(changeEditorContent({ id: "post-execution", value: script1 }))
        dispatch(applyReportScripts())
        let activeReport = getState().hreport.present.reports[0]
       await flushPromises( generateReport(activeReport, dispatch))
        expect(screen.queryByTestId(/table-value-Agent-0/i)).toBeTruthy();
    });
}); 