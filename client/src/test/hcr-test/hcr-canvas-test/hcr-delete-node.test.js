import "regenerator-runtime/runtime";
import { configureStore } from "@reduxjs/toolkit";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from 'react-dnd-html5-backend';
import { Provider } from "react-redux";
import { hiMockAxios } from "../../../app/mock-axios";
import { CannedReportsPage } from "../../../pages";
import reducers from '../../../redux';
import { render, screen } from "@testing-library/react";

const crypto = require('crypto');
const flushPromises = () => new Promise(setImmediate);

const App = ({ store }) => {
    return (
        <DndProvider backend={HTML5Backend}>
            <Provider store={store}>
                <CannedReportsPage />
            </Provider>
        </DndProvider>
    );
};

describe("hcr Canvas", () => {
    test("test hcr delete node", async () => {
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
        const dispatch = store.dispatch;
        const getState = store.getState

        await flushPromises(render(<App store={store} />))
        expect(screen.queryByTestId(/canned-report-content-container/i)).toBeTruthy();
        expect(screen.queryByTestId(/hcr-canvas-tab-wrapper/i)).toBeTruthy();
        // to be added ...
    });
}); 