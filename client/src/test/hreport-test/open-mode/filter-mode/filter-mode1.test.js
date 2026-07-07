import { configureStore } from '@reduxjs/toolkit';
import { render, screen, waitFor } from '@testing-library/react';
import { DndProvider } from 'react-dnd';
import { HTML5Backend } from 'react-dnd-html5-backend';
import { Provider } from 'react-redux';
import "regenerator-runtime/runtime";
import { hiMockAxios } from '../../../../app/mock-axios';
import { HelicalReports } from "../../../../pages/helical-reports-page";
import reducers from '../../../../redux';
import '../../../utils/mockJsdom';

const crypto = require('crypto');

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

    test("open child report using drill through field",async () => {
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
            "file": {
                "path": "naresh/with_filter.hr",
                "name": "with_filter.hr",
                "title": "with_filter"
            },
            "extension": "hr",
            "parameters": {},
            "mode": "filter",
            "cacheRefresh": false,
            "dashboardFilter": {
                "columnName": "booking_platform",
                "uid": "a3b10723-eb5b-4c1c-a993-fce64ba30282"
            },
            "allFilters": {
                "booking_platform": [
                    "Website"
                ]
            }
        }
        await waitFor(()=> render(<App store={store} {...props} />))
        expect(screen.queryByTestId(/test_123-filters-pane/i)).toBeTruthy();
        expect(screen.queryByTestId(/booking_platform-filter-toggle/i)).toBeTruthy();
        await waitFor(()=> screen.queryByTestId(/booking_platform-filter-toggle/i))
    });
}); 