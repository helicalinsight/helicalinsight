import { configureStore } from '@reduxjs/toolkit';
import { render, screen, waitFor } from '@testing-library/react';
import { DndProvider } from 'react-dnd';
import { HTML5Backend } from 'react-dnd-html5-backend';
import { Provider } from 'react-redux';
import "regenerator-runtime/runtime";
import { hiMockAxios } from '../../../../app/mock-axios';
import { HelicalReports } from "../../../../pages/helical-reports-page";
import reducers from '../../../../redux';
import { appActions } from '../../../../redux/actions';
import { changeEditingPane, changeEditorContent } from '../../../../redux/actions/hreport.actions';
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

describe("Hreport CSS Editor", () => {
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
        

    test("to test hreport css editor", async () => {
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
        dispatch(changeEditingPane({ id: "3" }))
        const style = "#hi-report-5bewf:{background:red;}"
        dispatch(changeEditorContent({ id: null, value: style }))
        let activeReport = getState().hreport.present.reports[0]
        expect(activeReport.styles).toEqual(style)
    });
}); 