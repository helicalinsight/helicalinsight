import "core-js/stable";
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
    beforeAll(() => {
        delete window.matchMedia
        window.matchMedia = (query) => ({
            matches: false,
            media: query,
            onchange: null,
            addListener: jest.fn(),
            removeListener: jest.fn(),
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