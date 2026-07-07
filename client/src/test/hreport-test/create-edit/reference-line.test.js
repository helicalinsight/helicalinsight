import { configureStore } from '@reduxjs/toolkit';
import { render, screen } from '@testing-library/react';
import "core-js/stable";
import { DndProvider } from 'react-dnd';
import { HTML5Backend } from 'react-dnd-html5-backend';
import { Provider } from 'react-redux';
import "regenerator-runtime/runtime";
import { hiMockAxios } from '../../../app/mock-axios';
import CustomReferenceLine from '../../../components/hi-reports/hi-editing-area/components/values/reference-line/custom-reference-line';
import { calculateReferenceLinePosition, getChartMaxScaleValue } from "../../../components/hi-reports/hi-viz-area/utils/utillities";
import reducers from '../../../redux';
import '../../utils/mockJsdom';

const chartScalMap = new Map([
    [
        "view12-booking_platform",
        {
            "key": "view12-booking_platform",
            "scale": {
                "type": "cat",
                "isCategory": true,
                "isLinear": false,
                "isContinuous": false,
                "isIdentity": false,
                "values": [
                    "Agent",
                    "Makemytrip",
                    "Website"
                ],
                "range": [
                    0.16666666666666666,
                    0.8333333333333334
                ],
                "ticks": [
                    "Agent",
                    "Makemytrip",
                    "Website"
                ],
                "__cfg__": {
                    "field": "booking_platform",
                    "values": [
                        "Agent",
                        "Makemytrip",
                        "Website"
                    ],
                    "type": "cat",
                    "nice": true
                },
                "tickMethod": "cat",
                "field": "booking_platform",
                "nice": true,
                "min": 0,
                "max": 2,
                "translateIndexMap": {}
            },
            "scaleDef": {
                "type": "cat",
                "nice": true
            }
        }
    ],
    [
        "view12-sum_travel_cost",
        {
            "key": "view12-sum_travel_cost",
            "scale": {
                "type": "linear",
                "isCategory": false,
                "isLinear": true,
                "isContinuous": true,
                "isIdentity": false,
                "values": [
                    3641245,
                    6719588,
                    8173137
                ],
                "range": [
                    0,
                    1
                ],
                "ticks": [
                    0,
                    2500000,
                    5000000,
                    7500000,
                    10000000
                ],
                "__cfg__": {
                    "field": "sum_travel_cost",
                    "values": [
                        3641245,
                        6719588,
                        8173137
                    ],
                    "min": 0,
                    "nice": true
                },
                "tickMethod": "wilkinson-extended",
                "nice": true,
                "field": "sum_travel_cost",
                "min": 0,
                "max": 10000000
            },
            "scaleDef": {
                "min": 0,
                "nice": true
            }
        }
    ]
])

const crypto = require('crypto');
const flushPromises = () => new Promise(setImmediate);

const App = ({ store, ...rest }) => {
    return (
        <DndProvider backend={HTML5Backend}>
            <Provider store={store}>
                <CustomReferenceLine width={500} top={100} {...rest} />
            </Provider>
        </DndProvider>
    );
};

describe("Hreport Reference Line", () => {
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

    test("render reference line", async () => {
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
        expect(screen.queryByTestId(/hr-reference-line/i)).toBeTruthy();
    });

    // test("render reference line with label", async () => {
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
    //     await flushPromises(render(<App store={store} label="Test Label" />))
    //     expect(screen.queryByTestId(/hr-reference-label/i)).toBeTruthy();
    // });

    test("test reference line position", async () => {
        let position = calculateReferenceLinePosition(9000000, 0, 571, "7000000", true)
        expect(position).toEqual(126.8888888888889)
    });

    test("max scale value function", async () => {
        const chartData = {
            chart: {
                scalePool: {
                    "scales": chartScalMap,
                }
            }
        }
        const [minScaleValue, maxScaleValue] = getChartMaxScaleValue(chartData, "sum_travel_cost")
        expect(maxScaleValue).toEqual(10000000)
    })
});




