import '../../utils/mockJsdom'
import { configureStore } from '@reduxjs/toolkit';
import { render, screen, fireEvent, act, waitFor } from '@testing-library/react';
import { Provider } from 'react-redux';
import reducers from '../../../redux';
import { HelicalReports } from "../../../pages/helical-reports-page"
import { DndProvider } from 'react-dnd';
import { HTML5Backend } from 'react-dnd-html5-backend';
import { hiMockAxios } from '../../../app/mock-axios';
import { appActions } from '../../../redux/actions';
import { generateReport } from '../../../components/hi-reports/utils/base';
import { addFieldToCanvas, changeEditingPane, changeEditorContent, updateSubVizType } from '../../../redux/actions/hreport.actions';
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
        
    test("to test subviz type avialable for viz type", async () => {
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
        let activeReport = getState().hreport.present.reports[0]
        dispatch(changeEditingPane({ id: "3" }))
        generateReport(
            { ...activeReport, selectedType: "GridChart" },
            dispatch,
        )
        dispatch(updateSubVizType({ value: "_all_", name: "tick" }))
        activeReport = getState().hreport.present.reports[0]
        generateReport(
            { ...activeReport, selectedType: "AntChart" },
            dispatch,
        )
        dispatch(updateSubVizType({ value: "_all_", name: "bar" }))
        let subVizType = getState().hreport.present.reports[0].marksList[0].subVizType;
        expect(subVizType).toBe("bar")
    });
}); 