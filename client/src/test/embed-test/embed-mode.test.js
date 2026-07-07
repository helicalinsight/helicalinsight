import "core-js/stable";
import "regenerator-runtime/runtime";
import '../utils/mockJsdom'
import { configureStore } from '@reduxjs/toolkit';
import { render, screen, fireEvent,act,waitFor } from '@testing-library/react';
import { Provider } from 'react-redux'; 
import reducers from '../../redux';
import { DndProvider } from 'react-dnd'; 
import { HTML5Backend } from 'react-dnd-html5-backend';
import { hiMockAxios } from '../../app/mock-axios';
import { HIRouter } from '../../pages';
const crypto = require('crypto');
const flushPromises = () => new Promise(setImmediate);
const App = ({store,auth,report}) => {
	return (
		<DndProvider backend={HTML5Backend}>
			<Provider store={store}>
                <HIRouter auth={auth} report={report} />
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
    test("jest example",async () => {
        expect(1+1).toBeTruthy();
    });
    test("jwt report",async () => {
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
        await flushPromises( render(<App 
            store={store}
            auth={{ type: 'jwt', authToken:"Bearer 1234" }} 
            report={{dir: 'naresh', file: "parent.hr"}} 
            />))
        expect(screen.queryByTestId(/table-value-Agent-0/i)).toBeTruthy();
    });
    test("jwt report with filters",async () => {
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
        await flushPromises( render(<App 
            store={store}
            auth={{ type: 'jwt', authToken:"Bearer 1234" }} 
            report={{dir: 'naresh', file: "child.hr",booking_platform :"Agent"}} 
            />))
        expect(screen.queryByTestId(/table-value-Agent-0/i)).toBeTruthy();
    });

    test("token report",async () => {
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
        await flushPromises( render(<App 
            store={store}
            auth={{ type: 'token', authToken:"Bearer 1234" }} 
            report={{dir: 'naresh', file: "parent.hr"}} 
            />))
        expect(screen.queryByTestId(/table-value-Agent-0/i)).toBeTruthy();
    });
    test("token report with filters",async () => {
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
        await flushPromises( render(<App 
            store={store}
            auth={{ type: 'token', authToken:"Bearer 1234" }} 
            report={{dir: 'naresh', file: "child.hr",booking_platform :"Agent"}} 
            />))
        expect(screen.queryByTestId(/table-value-Agent-0/i)).toBeTruthy();
    });
}); 