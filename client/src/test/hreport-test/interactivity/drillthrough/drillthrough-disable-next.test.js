import { configureStore } from '@reduxjs/toolkit';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
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

const App = ({ store }) => {
    return (
        <DndProvider backend={HTML5Backend}>
            <Provider store={store}>
                <div id="report-drilldown-menu" ></div>
                <HelicalReports />
            </Provider>
        </DndProvider>
    );
};

describe("Test drillthrough report next button disable", () => {
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

    test("next button disable on intial drillthrough state", async () => {
        await flushPromises(dispatch(appActions.setEditModeInfo({
            dir: "naresh/parent.hr", file: "parent.hr", extension: "hr"
        })))
        await flushPromises(render(<App store={store} />))
        expect(screen.queryByTestId(/tool-6/i)).toBeTruthy()
        let isDrillThrough = getState().hreport.present.reports[0].isDrillThrough;
        expect(isDrillThrough).toBeFalsy();
        await flushPromises(loadChildReport({
            path: "naresh/child.hr", file: "child.hr", extension: "hr", name: "child.hr"
        }, dispatch))
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
        expect(screen.queryByTestId(/drill-through-next-btn/i)).toBeTruthy()
    })

    test("empty data when click on prev drillthrough step and drillthrough menu render ", async () => {
        await flushPromises(dispatch(appActions.setEditModeInfo({
            dir: "naresh/parent.hr", file: "parent.hr", extension: "hr"
        })))
        await flushPromises(render(<App store={store} />))
        expect(screen.queryByTestId(/table-value-Agent-0/i)).toBeTruthy();
        await flushPromises(loadChildReport({
            path: "naresh/child.hr", file: "child.hr", extension: "hr", name: "child.hr"
        }, dispatch))
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
        fireEvent.click(screen.queryByTestId(/table-value-Agent-0/i))
        expect(screen.queryByTestId(/drillthrough-menu/i)).toBeFalsy()
        expect(screen.queryByTestId(/drillthrough-testing-btn/i)).toBeTruthy()
        await flushPromises(fireEvent.click(screen.queryByTestId(/drillthrough-testing-btn/i)))
        expect(screen.queryByTestId(/table-value-Makemytrip-0/i)).toBeFalsy();
        expect(screen.queryByTestId(/drill-through-prev-btn/i)).toBeTruthy();
        fireEvent.click(screen.queryByTestId(/drill-through-prev-btn/i))
        let data = getState().hreport.present.reports[1].reportData.data;
        expect(data).toEqual([])
    });

    test("disable drillthrough menu when drilldown is enabled", async () => {
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
        await flushPromises(dispatch(appActions.setEditModeInfo({
            dir: "naresh/parent.hr", file: "parent.hr", extension: "hr"
        })))
        await flushPromises(render(<App store={store} />))
        expect(screen.queryByTestId(/table-value-Agent-0/i)).toBeTruthy();
        expect(screen.queryByTestId(/tool-6/i)).toBeTruthy()
        fireEvent.click(screen.queryByTestId(/tool-6/i))
        expect(screen.queryByText(/Interactivity/i)).toBeTruthy()
        expect(screen.queryByText(/Drill Through/i)).toBeTruthy();
        expect(screen.queryByTestId(/hr-report-drill-down-btn/i)).toBeTruthy()
        expect(screen.queryByTestId(/hr-report-drill-through-btn/i)).toBeTruthy()
        expect(screen.queryByTestId(/hr-report-interactivity-btn/i)).toBeTruthy()
        fireEvent.click(screen.queryByTestId(/hr-report-interactivity-btn/i))
        expect(screen.queryByText(/Drill Through/i)).toBeFalsy();
        expect(screen.queryByText(/Drill Down/i)).toBeFalsy();
        fireEvent.click(screen.queryByTestId(/hr-report-interactivity-btn/i))
        fireEvent.click(screen.queryByTestId(/hr-report-drill-through-btn/i))
        expect(screen.queryByTestId(/drillthrough-menu/i)).toBeFalsy()
    })
}); 