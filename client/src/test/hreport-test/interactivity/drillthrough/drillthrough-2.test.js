import { configureStore } from '@reduxjs/toolkit';
import { fireEvent, render, screen } from '@testing-library/react';
import "core-js/stable";
import { DndProvider } from 'react-dnd';
import { HTML5Backend } from 'react-dnd-html5-backend';
import { Provider } from 'react-redux';
import "regenerator-runtime/runtime";
import { hiMockAxios } from '../../../../app/mock-axios';
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
    
    test("open child report using static value",async () => {
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
            dir:"naresh/parent.hr", file:"parent.hr", extension:"hr" ,name:"parent.hr"
        })))
        await flushPromises( render(<App store={store}/>))
        expect(screen.queryByTestId(/table-value-Agent-0/i)).toBeTruthy();
        expect(screen.queryByTestId(/tool-6/i)).toBeTruthy()
        fireEvent.click(screen.queryByTestId(/tool-6/i))   
        expect(screen.queryByText(/Interactivity/i)).toBeTruthy()
        expect(screen.queryByText(/Drill Through/i)).toBeTruthy();
        const drillthrough_reports_btn = screen.queryByTestId(/drillthrough-reports-btn/i)
        const drillthrough_reports_refresh_btn = screen.queryByTestId(/drillthrough-reports-refresh-btn/i)
        expect(drillthrough_reports_btn).toBeTruthy();
        expect(drillthrough_reports_refresh_btn).toBeTruthy();
        expect(drillthrough_reports_btn.querySelector('span')).toBeTruthy();
        await flushPromises( loadChildReport({ 
            path:"naresh/child.hr", file:"child.hr", extension:"hr",name:"child.hr" 
        },dispatch))
        expect(screen.queryByTestId(/table-value-Agent-0/i)).toBeTruthy();
        expect(screen.queryByTestId(/table-value-Makemytrip-1/i)).toBeTruthy();
        expect(screen.queryByTestId(/table-value-Website-2/i)).toBeTruthy();
        expect(screen.queryByTestId(/report1-filter1-static-check/i)).toBeTruthy();
        fireEvent.click(screen.queryByTestId(/report1-filter1-static-check/i))        
        expect(screen.queryByTestId(/report1-filter1-static-input/i)).toBeTruthy();
        fireEvent.change(screen.queryByTestId(/report1-filter1-static-input/i), {target: { value: "Website" }})
        // fireEvent.click(screen.queryByTestId(/table-value-Agent-0/i))   
        expect(screen.queryByTestId(/drillthrough-btn/i)).toBeFalsy();
        // await waitFor(()=> fireEvent.click(screen.queryByTestId(/drillthrough-btn/i)) )
        expect(screen.queryByTestId(/table-value-Website-0/i)).toBeFalsy();
        expect(screen.queryByTestId(/table-value-Cash-0/i)).toBeFalsy();
        expect(screen.queryByTestId(/table-value-Website-3/i)).toBeFalsy();
        expect(screen.queryByTestId(/table-value-Net Banking-3/i)).toBeFalsy();
        expect(screen.queryByTestId(/table-value-Makemytrip-0/i)).toBeFalsy(); 
    });
}); 