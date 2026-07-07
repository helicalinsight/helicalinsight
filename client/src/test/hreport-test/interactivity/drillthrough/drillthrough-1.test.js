// import "regenerator-runtime/runtime";
import { configureStore } from '@reduxjs/toolkit';
import { fireEvent, render, screen } from '@testing-library/react';
import "core-js/stable";
import { DndProvider } from 'react-dnd';
import { HTML5Backend } from 'react-dnd-html5-backend';
import { Provider } from 'react-redux';
import { hiMockAxios } from '../../../../app/mock-axios';
import { dropIntoParams } from '../../../../components/hi-reports/hi-editing-area/utils/marks-utils';
import { loadChildReport } from '../../../../components/hi-reports/utils/base';
import { HelicalReports } from "../../../../pages/helical-reports-page";
import reducers from '../../../../redux';
import { appActions } from '../../../../redux/actions';
import '../../../utils/mockJsdom';
const crypto = require('crypto');
const flushPromises = () => new Promise(setImmediate);
const App = ({store}) => {
	return (
		<DndProvider backend={HTML5Backend}>
			<Provider store={store}>
                <div id="report-drilldown-menu" ></div>
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
        await flushPromises( dispatch(appActions.setEditModeInfo({ 
            dir:"naresh/parent.hr", file:"parent.hr", extension:"hr" 
        })))
        await flushPromises( render(<App store={store}/>))
        expect(screen.queryByTestId(/table-value-Agent-0/i)).toBeTruthy();
        expect(screen.queryByTestId(/tool-6/i)).toBeTruthy()
        fireEvent.click(screen.queryByTestId(/tool-6/i))   
        expect(screen.queryByText(/Interactivity/i)).toBeTruthy()
        expect(screen.queryByText(/Drill Through/i)).toBeTruthy();
        const drillthrough_reports_btn = screen.queryByTestId(/drillthrough-reports-btn/i)
        expect(drillthrough_reports_btn).toBeTruthy();
        expect(drillthrough_reports_btn.querySelector('span')).toBeTruthy();
        await flushPromises( loadChildReport({ 
            path:"naresh/child.hr", file:"child.hr", extension:"hr",name:"child.hr" 
        },dispatch))
        let filterId = getState().hreport.present.reports[0].drillThroughList[1].parameters[0].uid
        let drillThroughId = getState().hreport.present.reports[0].drillThroughList[1].drillThroughId
        dropIntoParams({
            "type": "metadata_field",
            filterId,
            drillThroughId,
            "field": {
                "columnName": "booking_platform",
                "column": {
                    "alias": "booking_platform",
                    "fullyQualifiedColumn": "travel_details.booking_platform",
                    "columnId": "95859ecc-a29c-43a0-b5da-4c3e63299cbb",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    }
                },
                "table": {
                    "id": "8a28627d07d04ef096d9935f12e0c7e9",
                    "alias": "travel_details",
                    "name": "travel_details",
                    "key": "ceb64ffe-b346-4512-ad5a-acb6e0b4dd37"
                },
                "type": {
                    "java.lang.String": "text"
                },
                "defaultFunction": "db.generic.groupBy.group",
                "key": "95859ecc-a29c-43a0-b5da-4c3e63299cbb",
                "alias": "booking_platform"
            }
        }, dispatch)
        expect(screen.queryByTestId(/table-value-Agent-0/i)).toBeTruthy();
        expect(screen.queryByTestId(/table-value-Makemytrip-1/i)).toBeTruthy();
        expect(screen.queryByTestId(/table-value-Website-2/i)).toBeTruthy();
        fireEvent.click(screen.queryByTestId(/table-value-Agent-0/i))   
        expect(screen.queryByText(/Actions/i)).toBeTruthy();
        expect(screen.queryByTestId(/drillthrough-testing-btn/i)).toBeTruthy();
        await flushPromises(fireEvent.click(screen.queryByTestId(/drillthrough-testing-btn/i)) )
        expect(screen.queryByTestId(/table-value-Agent-0/i)).toBeTruthy();
        expect(screen.queryByTestId(/table-value-Cash-0/i)).toBeTruthy();
        expect(screen.queryByTestId(/table-value-Agent-3/i)).toBeTruthy();
        expect(screen.queryByTestId(/table-value-Net Banking-3/i)).toBeTruthy();
        expect(screen.queryByTestId(/table-value-Makemytrip-0/i)).toBeFalsy();
        expect(screen.queryByTestId(/drill-through-prev-btn/i)).toBeTruthy();
        await flushPromises( fireEvent.click(screen.queryByTestId(/drill-through-prev-btn/i)) )
        // expect(screen.queryByTestId(/table-value-Agent-0/i)).toBeTruthy();
        // expect(screen.queryByTestId(/table-value-Makemytrip-1/i)).toBeTruthy();
        // expect(screen.queryByTestId(/table-value-Website-2/i)).toBeTruthy();
        // fireEvent.click(screen.queryByTestId(/table-value-Website-2/i))   
    });
}); 