import "core-js/stable";
import { configureStore } from '@reduxjs/toolkit';
import { render, screen, waitFor } from '@testing-library/react';
import { DndProvider } from 'react-dnd';
import { HTML5Backend } from 'react-dnd-html5-backend';
import { Provider } from 'react-redux';
import "regenerator-runtime/runtime";
import { hiMockAxios } from '../../../app/mock-axios';
import { dropIntoParams } from '../../../components/hi-reports/hi-editing-area/utils/marks-utils';
import { loadChildReport } from "../../../components/hi-reports/utils/base";
import { HelicalReports } from "../../../pages/helical-reports-page";
import reducers from '../../../redux';
import { appActions } from "../../../redux/actions";
import '../../utils/mockJsdom';
const crypto = require('crypto');
const flushPromises = () => new Promise(setImmediate);
const App = ({ store, ...props }) => {
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
      
    test("open parent report then child report", async () => {
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
        await flushPromises( dispatch(appActions.setEditModeInfo({ 
            dir:"naresh/with_filter.hr", file:"with_filter.hr", extension:"hr" 
        })))
        await flushPromises( render(<App store={store} />))
        expect(screen.queryByTestId(/table-value-Bus-0/i)).toBeTruthy();
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
        expect(screen.queryByTestId(/table-value-Bus-0/i)).toBeTruthy();
    });
}); 