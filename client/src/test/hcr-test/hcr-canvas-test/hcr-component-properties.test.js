import { configureStore } from "@reduxjs/toolkit";
import { render, screen } from "@testing-library/react";
import "core-js/stable";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from 'react-dnd-html5-backend';
import { Provider } from "react-redux";
import "regenerator-runtime/runtime";
import { hiMockAxios } from "../../../app/mock-axios";
import ElementProperties from "../../../components/hi-canned-reports/hcrCanvas/elementProperties";
import reducers from '../../../redux';

const crypto = require('crypto');
const flushPromises = () => new Promise(setImmediate);

const App = ({ store }) => {
    return (
        <DndProvider backend={HTML5Backend}>
            <Provider store={store}>
                <ElementProperties />
            </Provider>
        </DndProvider>
    );
};

describe("hcr Canvas elements properties component", () => {
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

    test("render properties component", async () => {
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
        await flushPromises(render(<App store={store} />))
        expect(screen.queryByTestId(/hcr-components-properties-container/i)).toBeTruthy();
        expect(screen.queryByTestId(/hcr-components-properties-key-container/i)).toBeTruthy();
        expect(screen.queryByTestId(/hcr-components-properties-value-container/i)).toBeTruthy();
    });
}); 