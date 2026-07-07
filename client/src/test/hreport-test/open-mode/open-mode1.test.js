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
    
    test("jest example",async () => {
        expect(1+1).toBeTruthy();
    });
    test("open report in instant bi",async () => {
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
        const props = {
            mode: "instant-bi",
            metadata:{
                "metadataFileName": `Metadata_1.metadata`,
                "location": "TestFold"
            },
            columns: [],
            rows: [{ table: "travel_details", column: "booking_platform" }],
            filters:[] ,
            marks: [],
            visualisationType: "Table"
        }
       await flushPromises( render(<App store={store} {...props} />))
        expect(screen.queryByTestId(/table-value-Agent-0/i)).toBeTruthy();
        
    });
    test("open report in instant bi",async () => {
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
        const props = {
            mode: "instant-bi",
            metadata:{
                "metadataFileName": `Metadata_1.metadata`,
                "location": "TestFold"
            },
            columns: [{ table: "travel_details", column: "booking_platform" }],
            rows: [],
            filters:[] ,
            marks: [],
            visualisationType: "Table"
        }
       await flushPromises( render(<App store={store} {...props} />))
        expect(screen.queryByTestId(/table-value-Agent-0/i)).toBeTruthy();
        
    });
    test("open report in instant bi",async () => {
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
        const props = {
            mode: "instant-bi",
            metadata:{
                "metadataFileName": `Metadata_1.metadata`,
                "location": "TestFold"
            },
            columns: [{ table: "travel_details", column: "booking_platform" }],
            rows: [{ table: "travel_details", column: "mode_of_payment" }],
            filters:[{table:"travel_details",column:"booking_platform",values:"Agent"}] ,
            marks: [],
            visualisationType: "Table"
        }
       await flushPromises( render(<App store={store} {...props} />))
        expect(screen.queryByTestId(/table-value-Agent-0/i)).toBeTruthy();
        
    });
    test("open report in instant bi",async () => {
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
        const props = {
            mode: "instant-bi",
            metadata:{
                "metadataFileName": `Metadata_1.metadata`,
                "location": "TestFold"
            },
            columns: [{ table: "travel_details", column: "booking_platform" }],
            rows: [],
            filters:[] ,
            marks: [{ table: "travel_details", column: "booking_platform",markType:"color" }],
            visualisationType: "Table"
        }
       await flushPromises( render(<App store={store} {...props} />))
        expect(screen.queryByTestId(/table-value-Agent-0/i)).toBeTruthy();
        
    });
}); 