import "core-js/stable";
import "regenerator-runtime/runtime";
import '../../utils/mockJsdom'
import { configureStore } from '@reduxjs/toolkit';
import { render, screen, fireEvent,act,waitFor } from '@testing-library/react';
import { Provider } from 'react-redux'; 
import reducers from '../../../redux';
import {HelicalReports} from "../../../pages/helical-reports-page"
import { DndProvider } from 'react-dnd'; 
import { HTML5Backend } from 'react-dnd-html5-backend';
import { hiMockAxios } from '../../../app/mock-axios';
const crypto = require('crypto');
const flushPromises = () => new Promise(setImmediate);
const App = ({store,...props}) => {
	return (
		<DndProvider backend={HTML5Backend}>
			<Provider store={store}>
                <div id="report-drilldown-menu" ></div>
                <HelicalReports {...props} />
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

      
    test("edit report using url",async () => {
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
         await flushPromises( render(<App store={store} urlObj={{dir:"naresh",file:"parent.hr"}}/>))
        expect(screen.queryByTestId(/table-value-Agent-0/i)).toBeTruthy();
    });
    test("load metadata using url",async () => {
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
         await flushPromises( render(<App store={store} urlObj={{dir:"naresh",file:"Metadata_1.metadata"}}/>))
        expect(screen.queryByTestId(/tablename-employee_details/i)).toBeTruthy();
    });
}); 