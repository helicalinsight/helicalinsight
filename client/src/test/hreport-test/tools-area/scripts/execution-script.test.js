import { configureStore } from '@reduxjs/toolkit';
import { cleanup, render, screen, waitFor } from '@testing-library/react';
import { DndProvider } from 'react-dnd';
import { HTML5Backend } from 'react-dnd-html5-backend';
import { Provider } from 'react-redux';
import "regenerator-runtime/runtime";
import { hiMockAxios } from '../../../../app/mock-axios';
import { generateReport } from '../../../../components/hi-reports/utils/base';
import { HelicalReports } from "../../../../pages/helical-reports-page";
import reducers from '../../../../redux';
import { appActions } from '../../../../redux/actions';
import { applyReportScripts, changeEditorContent } from '../../../../redux/actions/hreport.actions';
import '../../../utils/mockJsdom';
const crypto = require('crypto');

const App = ({ store }) => {
    return (
        <DndProvider backend={HTML5Backend}>
            <Provider store={store}>
                <HelicalReports />
            </Provider>
        </DndProvider>
    );
};

describe("Hreport visualisation", () => {
    beforeEach(() => {
        jest.restoreAllMocks()
    })
    afterEach(cleanup)

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
    
    test("execution script test with user details", async () => {
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
        await waitFor(() => dispatch(appActions.setEditModeInfo({
            dir: "naresh/parent.hr", file: "parent.hr", extension: "hr"
        })))
        await waitFor(() => render(<App store={store} />))
        expect(screen.queryByTestId(/table-value-Agent-0/i)).toBeTruthy();
        // let script1 = `add_row({table:"travel_details",column:"travel_medium"})`
        // dispatch(changeEditorContent({ id: "pre-execution", value: script1 }))
        // dispatch(applyReportScripts())
        // let activeReport = getState().hreport.present.reports[0]
        // let userDetails = {
        //     "name": "hiadmin",
        //     "email": "admin@helicalinsight.com",
        //     "actualUserName": "hiadmin",
        //     "roles": [
        //         "ROLE_ADMIN",
        //         "ROLE_USER"
        //     ],
        //     "profile": []
        // }
        // await waitFor(() => generateReport({ ...activeReport, user: {...userDetails} }, dispatch))
        // activeReport = getState().hreport.present.reports[0]
        // expect(screen.queryByTestId(/table-value-Agent-0/i)).toBeTruthy();
        // expect(screen.queryByTestId(/table-value-Bus-0/i)).toBeTruthy();
        // let script2 = `add_row({table:"travel_details",column:"travel_medium"})`
        // dispatch(changeEditorContent({id:"pre-execution",value:script2}))  
        // dispatch(applyReportScripts())  
        // activeReport = getState().hreport.present.reports[0]
        // await waitFor(()=> generateReport(activeReport,dispatch))         
        // expect(screen.queryByTestId(/table-value-Agent-0/i)).toBeTruthy();
        // expect(screen.queryByTestId(/table-value-Bus-0/i)).toBeTruthy();
        // let script3 = `
        //     add_row({table:"travel_details",column:"travel_medium"})
        //     remove_column({table:"travel_details",column:"booking_platform"})
        //     add_filter({table:"travel_details",column:"booking_platform",values:"Website"});
        //     properties.title.show = true
        //     properties.title.value = "test"

        // `
        // dispatch(changeEditorContent({id:"pre-execution",value:script3}))  
        // dispatch(applyReportScripts())
        // activeReport = getState().hreport.present.reports[0]
        // await waitFor(()=> generateReport(activeReport,dispatch))         
        // expect(screen.queryByTestId(/table-value-Bus-0/i)).toBeTruthy();
    });
}); 