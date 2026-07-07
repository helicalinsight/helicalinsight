import "regenerator-runtime/runtime";
import '../../../utils/mockJsdom'
import { configureStore } from '@reduxjs/toolkit';
import { render, screen, fireEvent,act,waitFor } from '@testing-library/react';
import { Provider } from 'react-redux'; 
import reducers from '../../../../redux';
import {HelicalReports} from "../../../../pages/helical-reports-page"
import { DndProvider } from 'react-dnd'; 
import { HTML5Backend } from 'react-dnd-html5-backend';
import { hiMockAxios } from '../../../../app/mock-axios';
import { appActions } from '../../../../redux/actions';
import { generateReport, loadChildReport } from '../../../../components/hi-reports/utils/base';
import { dropIntoParams } from '../../../../components/hi-reports/hi-editing-area/utils/marks-utils';
import { addFieldToCanvas } from '../../../../redux/actions/hreport.actions';
const crypto = require('crypto');

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
    
    test("jest example",async () => {
        expect(1+1).toBeTruthy();
    });
    // test("open child report using drill through field",async () => {
    //     const store = configureStore({
    //         reducer: reducers,
    //         middleware: (getDefaultMiddleware) =>
    //             getDefaultMiddleware({
    //                 thunk: {
    //                     extraArgument: hiMockAxios
    //                 },
    //                 immutableCheck: false,
    //                 serializableCheck: false,
    //             }),
    //     });
    //     const dispatch = store.dispatch
    //     const getState = store.getState
    //     await waitFor(()=> dispatch(appActions.setEditModeInfo({ 
    //         dir:"naresh/parent.hr", file:"parent.hr", extension:"hr" 
    //     })))
    //     await waitFor(()=> render(<App store={store}/>))
    //     expect(screen.queryByTestId(/table-value-Agent-0/i)).toBeTruthy();
    //     dispatch(addFieldToCanvas({column:"travel_date",table:"travel_details",addedAs:"column"}))
    //     let report  = getState().hreport.present.reports[0]
    //     await waitFor(()=> generateReport({...report},dispatch));
    //     expect(screen.queryByTestId(/table-value-Agent-0/i)).toBeTruthy();
    //     expect(screen.queryByTestId(/table-value-2015-03-09 11:38:00.0-0/i)).toBeTruthy();
    //     expect(screen.queryByTestId(/tool-6/i)).toBeTruthy()
    //     fireEvent.click(screen.queryByTestId(/tool-6/i))   
    //     expect(screen.queryByText(/Interactivity/i)).toBeTruthy()
    //     expect(screen.queryByText(/Drill Down/i)).toBeTruthy();
    //     fireEvent.click(screen.queryByTestId(/table-value-Agent-0/i))   
    //     expect(screen.queryByText(/Actions/i)).toBeTruthy();
    //     expect(screen.queryByTestId(/drilldown-2015-03-09 11:38:00.0/i)).toBeTruthy();
    //     await waitFor(()=> fireEvent.click(screen.queryByTestId(/drilldown-2015-03-09 11:38:00.0/i)) )
    //     expect(screen.queryByTestId(/table-value-2015-03-09 11:38:00.0-0/i)).toBeTruthy();
    // });
}); 